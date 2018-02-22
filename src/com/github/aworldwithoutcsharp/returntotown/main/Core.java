package com.github.aworldwithoutcsharp.returntotown.main;

import com.github.aworldwithoutcsharp.returntotown.main.scenes.Tutorial;

import java.util.Scanner;

/*
 * TODO: {
 *     Mechanics:
 *       - input:
 *         - command-based
 *       - output:
 *         - details / paragraph
 *         - possibly formatted keywords??
 *     I. Tutorial [Gala]
 *       A. Tasks
 *         1) make an order
 *         2) drink
 *         3) observation
 *       B. Focus on...
 *         1) commands
 *         2) observation / using text output
 *     II. Game
 *       A. Basic Plot
 *         1) starts out in forest
 *         2) searches for clues
 *         3) Sara leaves notes trying to convince player to abandon the search/hunt for Ralph's notes
 *         4) left with decision: leave or stay in company
 *         5) message / question of morality
 *       B. Location-based Events
 *         1+) notes
 *           a. text
 *           b. receipts for former drugs
 *           c. picture of father in hospital | cat scan
 *         2)
 *       C. Characters
 *         1) protagonist
 *           a. CEO of a pharmaceutical company
 *           b. focus is on the profit
 *         2) "Ralph"
 *           a. lost father to disease, couldn't afford medicine
 *         3) "Sara"
 *           a. protag's secretary
 *           b. guards (who almost caught Ralph while drugging protag) tell Sara where-abouts of Ralph
 *           c. tells protag that his business is helping economy
 *         4) [protag's daughter]
 * }
 */

public class Core {
    public enum CommandType {
        EXIT(new ArgumentDefinition[]{}, "exit");

        private ArgumentDefinition[] arguments;
        private String usage;
        CommandType(ArgumentDefinition[] arguments, String usage) {
            this.arguments = arguments;
            this.usage = usage;
        }
        public ArgumentDefinition[] getArguments() {
            return arguments;
        }
        public String getUsage() {
            return usage;
        }
    }
    public static class Command {
        private CommandType type;
        private String[] arguments;
        public Command(CommandType type, String[] arguments) {
            this.type = type;
            this.arguments = arguments;
        }
        public CommandType getType() {
            return type;
        }
        public String[] getArguments() {
            return arguments;
        }
    }
    private enum ArgumentType {}
    private static class ArgumentDefinition {
        private String name;
        private ArgumentType type;
        public ArgumentDefinition(String name, ArgumentType type) {
            this.name = name;
            this.type = type;
        }
        public String getName() {
            return name;
        }
        public ArgumentType getType() {
            return type;
        }
    }

    private static final String PROMPT = "> ";

    private static Scanner in = new Scanner(System.in);
    public static Command input() {
        return parseInput(rawInput());
    }
    private static String rawInput() {
        System.out.print(PROMPT);
        return in.nextLine();
    }
    private static Command parseInput(String s) {
        if (s.length() == 0) return null;
        String[] words = s.split(" ");
        CommandType type = CommandType.valueOf(words[0].toUpperCase());
        for (ArgumentDefinition argument : type.getArguments()) {
            // TODO: compare argument.getType() to ArgumentType.fromInput(String)
            // TODO: create ArgumentType.fromInput(String)
        }
        String[] args = new String[words.length - 1];
        if (words.length > 1) {
            for (int i=1; i<words.length; i++) args[i-1] = words[i];
        }
        return new Command(type, args);
    }
    private void run() {
        Tutorial.run();
    }

    public static void main(String[] args) {
       new Core().run();
    }
}
