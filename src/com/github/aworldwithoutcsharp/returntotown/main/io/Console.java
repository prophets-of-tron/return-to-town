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
import java.util.*;

/*
 * FIXME: add space after command name and arguments with tab completion
 * TODO: indent output on multiline with `*`
 * TODO? make not static
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
                        // (try not doing this and see why)
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
                terminal.clearScreen();
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
            int lastILength = i.length();   // before modifications from keystroke
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
                    int end = p;    // specifically + 0
                    if (keyStroke.isCtrlDown()) {
                        while (!isNewWord(false, end)) end++;
                    }
                    while (!ranOnce || (keyStroke.isCtrlDown() && p <= end)) {  // <= or < ?
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
            terminal.setCursorPosition(startPosition.withColumn(PROMPT.length()));
            // clear last span of text, in case of deletion (so extraneous characters aren't preserved)
            print(new String(new char[lastILength]).replace("\0", " "), false);
            terminal.setCursorPosition(startPosition.withColumn(PROMPT.length()));  // reset after clearing
            print(i.toString(), false);
            terminal.setCursorPosition(startPosition.withColumn(PROMPT.length() + p));  // reset after printing
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

        int f = floorToWord(p);
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
        try {
            CommandDefinition commandDefinition = CommandDefinition.forName(commandNameStart);
            return commandDefinition.tabComplete(argIndex, beginning);
        } catch (NoSuchElementException e) {
            return new ArrayList<>();   // the first word isn't a command (user entered it wrong)
        }

    }
    private static int floorToWord(int c) {
        if (isNewWord()) return p; // if cursor (`p`) is at a new word, that's the beginning of the word
        // multiple spaces are treated as one space
        String s = i.toString();
        String[] words = s.replaceAll(" +", " ").split(" ", -1);
        int wordIndex = getRawCursorWordIndex(c, words);
        int charIndex = s.indexOf(words[wordIndex]);   // use indexOf to account for multiple spaces (see above)
        return charIndex;   // this method doesn't preserve spaces before now
    }
    /** "raw" because it doesn't account for trailing spaces */
    private static int getRawCursorWordIndex(int c, String[] words) {
        boolean newWord = isNewWord();
        int accumulatedLength = 0;
        for (int w=0; w<words.length; w++) {
            accumulatedLength += words[w].length();
            if (c < accumulatedLength+" ".length()+1) return w+(newWord?1:0);
        }
        throw new RuntimeException("yeah... something went wrong");
    }

    /**
     * Calculates whether or not the cursor <code>p</code> is on a new word (after a space)
     * @param right whether to search from the right (test if there is a space <em>at</em> the cursor, instead of
     *              before) or not
     * @param pos the cursor position
     * @return <code>true</code> if there is a space before the cursor <em>and</em> the cursor isn't at the beginning
     * and <code>false</code> otherwise
     */
    private static boolean isNewWord(boolean right, int pos) {
        int dir = right ? 0 : -1;
        boolean exceeds = right ? (pos >= i.length()) : (pos - 1 < 0);
        return !exceeds && i.charAt(pos+dir) == ' ';   // TODO: figure out this and working with "     word" as one word
    }
    private static boolean isNewWord(boolean right) {
        return isNewWord(right, p);
    }
    private static boolean isNewWord() {
        return isNewWord(false);
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

    public static void print(String s, boolean flush) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        write(s, flush);
    }
    public static void print(String s) {
        print(s, true);
    }
    public static void println(String s, boolean flush) {
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        writeln(s, flush);
    }
    public static void println(String s) {
        println(s, true);
    }
    public static void error(String s, boolean flush) {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        write(s, flush);
    }
    public static void error(String s) {
        error(s, true);
    }
    public static void errorln(String s, boolean flush) {
        textGraphics.setForegroundColor(TextColor.ANSI.RED);
        writeln(s, flush);
    }
    public static void errorln(String s) {
        errorln(s, true);
    }
    public static void write(String s, boolean flush) {
        put(s);
        if (flush) terminal.flush();
    }
    public static void write(String s) {
        write(s, true);
    }
    public static void writeln(String s, boolean flush) {
        put(s);
        terminal.putCharacter('\n');
        if (flush) terminal.flush();
    }
    public static void writeln(String s) {
        writeln(s, true);
    }
    /** Writes the string to screen, breaking at words */
    private static void put(String s) {
        int w = terminal.getTerminalSize().getColumns(), h = terminal.getTerminalSize().getRows();
        int x = terminal.getCursorPosition().getColumn(), y = terminal.getCursorPosition().getRow();

        // simple yet powerful; limit is to include trialing empty strings
        List<String> words = new ArrayList<>(Arrays.asList(s.split(" ", -1)));
        for (int i=0; i<words.size(); i++) {
            String word = words.get(i);
            while (word.length() > w) {
                String line = word.substring(0, w);
                // split into two list items
                words.set(i, line);
                words.add(i, word.substring(w));
            }
        }

        for (int i=0; i<words.size(); i++) {
            String output = words.get(i) + (i < words.size()-1 ? " " : "");
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
