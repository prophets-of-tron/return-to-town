package com.github.aworldwithoutcsharp.returntotown.main.io;

import com.github.aworldwithoutcsharp.returntotown.main.command.Command;
import com.github.aworldwithoutcsharp.returntotown.main.command.CommandDefinition;
import com.github.aworldwithoutcsharp.returntotown.main.command.InvalidCommandException;
import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;

import java.io.IOException;
import java.util.List;

public class Console {
    public static Terminal terminal;
    public static TextGraphics textGraphics;
    static {
        TerminalFactory terminalFactory = new DefaultTerminalFactory();
        try {
            terminal = terminalFactory.createTerminal();
            terminal.enterPrivateMode();
            textGraphics = terminal.newTextGraphics();
        } catch (IOException e) {
            e.printStackTrace();
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

    private static StringBuilder i;
    private static int p, positionSinceStartedTabbing;
    private static int currentTabIndex;
    /** also for tabbing */
    private static KeyStroke keyStroke;

    private static String rawInput() {
        try {
            print(PROMPT);

            i = new StringBuilder();
            p = 0;  // insertion position in `i`
            currentTabIndex = -1;   // no current tab selection
            keyStroke = null;

            loop:
            do {
                boolean breakLoop = false;
                keyStroke = terminal.readInput();
                switch (keyStroke.getKeyType()) {
                    case Escape: {
                        clear(0, i.length());
                        break;
                    }
                    case Enter: {
                        terminal.putCharacter('\n');
                        breakLoop = true;
                    }
                    case Backspace: {
                        if (p > 0) backspace();
                        else {
                            // maybe play a mellow beep like PowerShell
                        }
                        break;
                    }
                    case Delete: {
                        if (p < i.length()) {
                            // TODO: check if this works
                            int numbCharsForward = (p < i.length()-1) ? 1 : 0;
                            p += numbCharsForward + 1;
                            backspace();
                            p -= numbCharsForward;
                        }
                        break;
                    }
                    case ArrowLeft: {
                        if (p > 0) p--;
                        break;
                    }
                    case ArrowRight: {
                        if (p < i.length()) p++;
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
                if (breakLoop) break loop;

                TerminalPosition startPosition = terminal.getCursorPosition();
                textGraphics.putString(startPosition.withColumn(PROMPT.length()), i.toString());
                terminal.setCursorPosition(startPosition.withColumn(PROMPT.length() + p));
                terminal.flush();

            } while (true);
            return i.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("something went wrong!");
    }
    private static void tab() throws IOException {
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
        String str = i.substring(0, positionSinceStartedTabbing).toString();
        if (str.length() == 0) return CommandDefinition.HELP.tabComplete(0, "");

        String[] words = str.split(" ");
        if (str.length() == 0) return CommandDefinition.HELP.tabComplete(0, "");

        String commandNameStart = words[0];
        boolean newWord = isNewWord();
        int wordIndex = getRawCursorWordIndex(positionSinceStartedTabbing, words);
        if (wordIndex == 0) return CommandDefinition.HELP.tabComplete(0, commandNameStart);

        String beginning = wordIndex < words.length ? words[wordIndex] : "";
        int argIndex = (newWord ? words.length : words.length-1) - 1; // - 1 to exclude the command name

        List<String> commandSuggestions = CommandDefinition.HELP.tabComplete(0, commandNameStart);
        if (!commandSuggestions.contains(commandNameStart) && wordIndex == 0) return commandSuggestions;
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
        return p > 0 && i.charAt(p-1) == ' ';
    }
    private static void clear(int start, int end) throws IOException {
        // clear portion on terminal
        for (p=end; p>start; /*backspace() decrements `p`*/) backspace();
    }

    /**
     * Assumes <code>p &gt; 0</code>
     * @throws IOException
     */
    private static void backspace() throws IOException {
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
    private static void write(String s) {
        try {
            textGraphics.putString(terminal.getCursorPosition(), s);
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void writeln(String s) {
        try {
            textGraphics.putString(terminal.getCursorPosition(), s);
            terminal.putCharacter('\n');
            terminal.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
