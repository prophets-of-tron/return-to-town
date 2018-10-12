package com.github.aworldwithoutcsharp.returntotown.main.io;

import com.github.aworldwithoutcsharp.returntotown.main.command.Command;
import com.github.aworldwithoutcsharp.returntotown.main.command.CommandDefinition;
import com.github.aworldwithoutcsharp.returntotown.main.command.InvalidCommandException;
import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

import javax.swing.*;
import java.util.List;
import java.util.Stack;

/*
 * FIXME: fix overflow cut-off (again, because I lost all my code and now I'm starting from this point),
 * FIXME    maybe be rearranging text on the resize event
 * FIXME: add space after command name and arguments with tab completion
 */

public class Console {
    public static SwingTerminalFrame terminal;
    public static TextGraphics textGraphics;

    /**
     * Starts the text engine
     * @param title the title of the <code>JFrame</code>
     * @param callback will run after resizing (on linux, at least, this matters), or immediately if no resizing is done
     */
    public static void init(String title, Runnable callback) {
        terminal = new DefaultTerminalFactory()
                .createSwingTerminal();
        terminal.setVisible(true);
        terminal.setTitle(title);
        terminal.enterPrivateMode();
        textGraphics = terminal.newTextGraphics();

        while (true) {
            print("Launch in fullscreen? [Y/n] ");
            KeyStroke keyStroke = terminal.readInput();
            terminal.putCharacter('\n');
            terminal.flush();
            char yesNo = keyStroke.getCharacter();
            if (keyStroke.getKeyType() == KeyType.Enter) yesNo = 'y';  // default to 'y' (yes)
            if (yesNo == 'y' || yesNo == 'Y') {
                terminal.clearScreen();
                // run callback *when done resizing* (in case it resizes smoothly)
                terminal.addResizeListener(new TerminalResizeListener() {
                    @Override
                    public void onResized(Terminal terminal, TerminalSize terminalSize) {
                        // we're on the EDT thread (because Swing), so run in another thread
                        // (try not doing this and see wh)
                        new Thread(callback).start();
                        // terminal.removeResizeListener modifies the array while inside a for-each loop (it must be)
                        // in Lanterna's code
                        SwingUtilities.invokeLater(new Runnable() {
                            TerminalResizeListener resizeListener;  // I know, it's gross
                            Runnable setResizeListener(TerminalResizeListener resizeListener) {
                                this.resizeListener = resizeListener;
                                return this;
                            }
                            @Override
                            public void run() {
                                terminal.removeResizeListener(resizeListener);    // only run once
                            }
                        }.setResizeListener(this));
                    }
                });
                terminal.setExtendedState(JFrame.MAXIMIZED_BOTH);   // maximize, dummy
                break;
            } else if (yesNo == 'n' || yesNo == 'N') {
                callback.run(); // call immediately, as no resize is being done
                break;
            }
        }
    }

    /*
    INPUT
     */

    private static final String PROMPT = "> ";

    public static UserCommand input() {
        try {
            return Command.parseInput(rawInput());
        } catch (InvalidCommandException e) {
            errorln(e.getMessage() + "!");
            return null;
        }
    }

    /** history of commands entered */
    private static Stack<String> history = new Stack<>();

    private static StringBuilder i;
    private static int p, positionSinceStartedTabbing;
    private static int currentTabIndex, currentHistoryIndex;
    /** also for tabbing */
    private static KeyStroke keyStroke;

    private static String rawInput() {
        print(PROMPT);

        // reset static values
        i = new StringBuilder();
        p = 0;  // insertion position in `i`
        positionSinceStartedTabbing = 0;
        currentTabIndex = -1;   // no current tab selection
        currentHistoryIndex = -1;   // no current history selection
        keyStroke = null;

        do {
            boolean breakLoop = false;
            keyStroke = terminal.readInput();
            switch (keyStroke.getKeyType()) {
                case ArrowUp: {
                    if (currentHistoryIndex < history.size() - 1) {
                        currentHistoryIndex++;
                        String historyItem = history.get(currentHistoryIndex);
                        // replace `i`'s text with that of `historyItem`
                        clear(0, i.length());   // note that `clear` clears both the screen and the stringbuilder
                        i.insert(0, historyItem);
                        p = i.length();
                    }
                    break;
                }
                case ArrowDown: {
                    if (currentHistoryIndex > 0) {
                        currentHistoryIndex--;
                        String historyItem = history.get(currentHistoryIndex);
                        // replace `i`'s text with that of `historyItem`
                        clear(0, i.length());
                        i.insert(0, historyItem);
                        p = i.length();
                    } else if (currentHistoryIndex == 0) {
                        currentHistoryIndex = -1;
                        // clear `i`
                        clear(0, i.length());
                        p = 0;
                    }
                    break;
                }
                case Escape: {
                    clear(0, i.length());
                    break;
                }
                case Enter: {
                    terminal.putCharacter('\n');
                    breakLoop = true;
                    break;
                }
                case Backspace: {
                    // backspace whole word if keyStroke.isCtrlDown()
                    boolean ranOnce = false;
                    // always run once, no matter what
                    // then, if .isCtrlDown(), run more times
                    while (!ranOnce || (keyStroke.isCtrlDown() && !isNewWord())) {
                        if (p > 0) backspace();
                        else {
                            // maybe play a mellow beep like PowerShell
                            break;
                        }
                        ranOnce = true;
                    }
                    break;
                }
                case Delete: {
                    // delete whole word if keyStroke.isCtrlDown()
                    boolean ranOnce = false;
                    // always run once, no matter what
                    // then, if .isCtrlDown(), run more times
                    while (!ranOnce || (keyStroke.isCtrlDown() && !isNewWord())) {
                        if (p < i.length()) {
                            // TODO: check if this works
                            int numbCharsForward = (p < i.length() - 1) ? 1 : 0;
                            p += numbCharsForward + 1;
                            backspace();
                            p -= numbCharsForward;
                        } else break;
                        ranOnce = true;
                    }
                    break;
                }
                case ArrowLeft: {
                    boolean ranOnce = false;
                    // always run once, no matter what
                    // then, if .isCtrlDown(), run more times
                    while (!ranOnce || (keyStroke.isCtrlDown() && !isNewWord())) {
                        if (p > 0) p--;
                        else break;
                        ranOnce = true;
                    }
                    break;
                }
                case ArrowRight: {
                    boolean ranOnce = false;
                    // always run once, no matter what
                    // then, if .isCtrlDown(), run more times
                    while (!ranOnce || (keyStroke.isCtrlDown() && !isNewWord())) {
                        if (p < i.length()) p++;
                        else break;
                        ranOnce = true;
                    }
                    break;
                }
                case Tab: {
                    tab();
                    break;
                }
                default: {
                    char c = keyStroke.getCharacter();
                    i.insert(p, c);
                    p++;
                    break;
                }
            }
            if (keyStroke.getKeyType() != KeyType.Tab) {
                currentTabIndex = -1;    // stop "tabbing"
                positionSinceStartedTabbing = p;
            }
            if (breakLoop) break;

            TerminalPosition startPosition = terminal.getCursorPosition();
            textGraphics.putString(startPosition.withColumn(PROMPT.length()), i.toString());
            terminal.setCursorPosition(startPosition.withColumn(PROMPT.length() + p));
            terminal.flush();

        } while (true);
        String s = i.toString();
        history.push(s);
        return s;
    }
    private static void tab() {
        List<String> suggestions = getSuggestions();
        if (suggestions == null || suggestions.size() == 0) return;
        currentTabIndex = (currentTabIndex + 1) % suggestions.size();
        String currentSuggestion = suggestions.get(currentTabIndex);

        int f = floorToWord(p, i.toString().split(" "));
        clear(f, p);
        i.insert(p, currentSuggestion);
        // at the end of the main loop, `i` will be printed, so no need to do anything here

        p += currentSuggestion.length();
    }
    private static List<String> getSuggestions() {
        /*
        Use CommandDefinition.HELP for tab completing when there is no full command name
         */
        String str = i.substring(0, positionSinceStartedTabbing);
        if (str.length() == 0) return CommandDefinition.HELP.tabComplete(0, "");

        String[] words = str.split(" ");
        if (words.length == 0) return CommandDefinition.HELP.tabComplete(0, "");

        String commandNameStart = words[0];
        boolean newWord = isNewWord();
        int wordIndex = getRawCursorWordIndex(positionSinceStartedTabbing, words);

        if (wordIndex == 0) {
            List<String> commandSuggestions = CommandDefinition.HELP.tabComplete(0, commandNameStart);
            if (!commandSuggestions.contains(commandNameStart)) return commandSuggestions;
        }

        String beginning = wordIndex < words.length ? words[wordIndex] : "";
        int argIndex = (newWord ? words.length : words.length-1) - 1; // - 1 to exclude the command name

        CommandDefinition commandDefinition = CommandDefinition.forName(commandNameStart);

        return commandDefinition.tabComplete(argIndex, beginning);
    }
    private static int floorToWord(int c, String[] words) {
        int wordIndex = getRawCursorWordIndex(c, words);
        int accumulatedLength = 0;
        for (int w=0; w<wordIndex; w++)
            accumulatedLength += words[w].length() + (w<wordIndex-1?" ".length():0);
        return accumulatedLength;
    }
    /** "raw" because it doesn't account for trailing spaces */
    private static int getRawCursorWordIndex(int c, String[] words) {
        boolean newWord = isNewWord();
        int accumulatedLength = 0;
        for (int w=0; w<words.length; w++) {
            accumulatedLength += words[w].length();
            if (c < accumulatedLength+" ".length()+1) return w + (newWord?1:0);
        }
        throw new RuntimeException("yeah... something went wrong");
    }
    private static boolean isNewWord() {
        return p > 0 && i.charAt(p-1) == ' ';   // TODO: figure out this and working with "     word" as one word
    }
    private static void clear(int start, int end) {
        // clear portion on terminal
        for (p=end; p>start; /*backspace() decrements `p`*/) backspace();
    }

    /**
     * Assumes <code>p &gt; 0</code>
     */
    private static void backspace() {
        p--;
        // delete character in `i`
        i.deleteCharAt(p);

        // move back
        TerminalPosition startPosition = terminal.getCursorPosition();
        terminal.setCursorPosition(startPosition.withColumn(PROMPT.length() + p));
        // print whitespace
        terminal.putCharacter(' ');
    }

    /*
    OUTPUT
     */

    public static void print(String s) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        write(s);
    }
    public static void println(String s) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        writeln(s);
    }
    public static void error(String s) {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        write(s);
    }
    public static void errorln(String s) {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        writeln(s);
    }
    public static void write(String s) {
        put(s);
        terminal.flush();
    }
    public static void writeln(String s) {
        put(s);
        terminal.putCharacter('\n');
        terminal.flush();
    }
    /** Writes the string to screen, breaking at words */
    private static void put(String s) {
        int w = terminal.getTerminalSize().getColumns(), h = terminal.getTerminalSize().getRows();
        int x = terminal.getCursorPosition().getColumn(), y = terminal.getCursorPosition().getRow();

        String[] words = s.split(" ", -1);// simple yet powerful; limit is to include trialing empty strings
        for (int i=0; i<words.length; i++) {
            String output = words[i] + (i < words.length-1 ? " " : "");
            // move down a line, if necessary
            if (x + output.length() > w) {
                x = 0;
                y++;
            }
            textGraphics.putString(new TerminalPosition(x, y), output);

            x += output.length();
        }
    }
}
