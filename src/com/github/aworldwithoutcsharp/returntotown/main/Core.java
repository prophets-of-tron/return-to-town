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
    public enum CommandDefinition {
        EXIT("exit", new ArgumentDefinition[]{}),
        HELP("help", new ArgumentDefinition[]{
                new ArgumentDefinition("command", ArgumentType.COMMAND_NAME, true)
        }) {    
            @Override
            public void perform(String[] args) {
                if (args.length == 0) {
                    // iterate over all CommandDefinition instances
                    for (CommandDefinition def : values()) {
                        System.out.println(def.getUsage());
                    }
                } else {
                    System.out.println(CommandDefinition.valueOf(args[0].toUpperCase()).getUsage());
                }
            }
        },
        INTERACT("int", new ArgumentDefinition[]{
                new ArgumentDefinition("target", ArgumentType.OBJECT, false)
        }) {
            @Override
            public void perform(String[] args) {
                // TODO: make it VARY
                System.out.println("The poster reads, 'Nice Job! You not only can follow simple instructions, but you can also read! Now remember, you are at a party, so stop reading signs and ionteract with the PATRONS you invited!'");
            }
        };

        private String name;
        private ArgumentDefinition[] arguments;
        CommandDefinition(String name, ArgumentDefinition[] arguments) {
            this.name = name;
            this.arguments = arguments;
        }
        public String getUsage() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(' ');
            for (ArgumentDefinition argumentDef : arguments) {
                boolean opt = argumentDef.isOptional();
                if (opt) sb.append('[');
                sb.append('<');
                sb.append(argumentDef.getName());
                sb.append('>');
                if (opt) sb.append(']');
                sb.append('\n');
            }
            sb.deleteCharAt(sb.length()-1); // delete last newline
            return sb.toString();
        }
        public ArgumentDefinition[] getArguments() {
            return arguments;
        }

        /**
         *
         * @param args the list of argument values the USER provides
         */
        public void perform(String[] args) {}
    }
    public static class UserCommand {
        private CommandDefinition def;
        private String[] arguments;
        public UserCommand(CommandDefinition definition, String[] arguments) {
            this.def = definition;
            this.arguments = arguments;
        }
        public CommandDefinition getDefinition() {
            return def;
        }
        public String[] getArguments() {
            return arguments;
        }
    }
    private enum ArgumentType {
        OBJECT, COMMAND_NAME
    }
    private static class ArgumentDefinition {
        private String name;
        private ArgumentType type;
        private boolean optional;
        public ArgumentDefinition(String name, ArgumentType type, boolean optional) {
            this.name = name;
            this.type = type;
            this.optional = optional;
        }
        public String getName() {
            return name;
        }
        public ArgumentType getType() {
            return type;
        }
        public boolean isOptional() {
            return optional;
        }
    }

    private static final String PROMPT = "> ";

    private static Scanner in = new Scanner(System.in);
    public static UserCommand input() {
        return parseInput(rawInput());
    }
    private static String rawInput() {
        System.out.print(PROMPT);
        return in.nextLine();
    }
    private static UserCommand parseInput(String s) {
        if (s.length() == 0) return null;
        String[] words = s.split(" ");
        CommandDefinition type = CommandDefinition.valueOf(words[0].toUpperCase());
        for (ArgumentDefinition argument : type.getArguments()) {
            // TODO: compare argument.getDefinition() to ArgumentType.fromInput(String)
            // TODO: create static ArgumentType[] ArgumentType.fromInput(String)
        }
        String[] args = new String[words.length - 1];
        if (words.length > 1) {
            for (int i=1; i<words.length; i++) args[i-1] = words[i];
        }
        return new UserCommand(type, args);
    }
    private static void run() {
        Tutorial.run();
    }

    public static void main(String[] args) {
       Core.run();
    }
}
