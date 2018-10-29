package com.github.aworldwithoutcsharp.returntotown.main.command;

import com.github.aworldwithoutcsharp.returntotown.main.Core;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;

import java.util.ArrayList;
import java.util.NoSuchElementException;

public enum CommandDefinition {
    // Global commands
    EXIT("exit", "stop playing", new ArgumentDefinition[]{}) {
        @Override
        public void perform(String[] args) {
            Core.exit();
        }
    },
    HELP("help", "prints out command(s)", new ArgumentDefinition[]{}) {
        @Override
        public void perform(String[] args) {
            // iterate over all CommandDefinition instances
            for (CommandDefinition def : values()) {
                Console.println(def.getUsage());
                Console.println(" - "+def.getDescription());
            }
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex, String beginning) {
            // only one argument
            if (argIndex != 0) throw new IllegalArgumentException("Argument index: " + argIndex + " does not exist");

            ArrayList<String> suggestions = new ArrayList<>();
            // iterate over possible commands, because this is `help`
            // TODO: make it depend on the scene / "conditional" commands
            for (CommandDefinition command : values()) {
                String lowerCase = command.getName().toLowerCase();
                if(lowerCase.startsWith(beginning.toLowerCase())) suggestions.add(lowerCase);
            }
            return suggestions;
        }
    },

    // TileData- (location)-specific commands
    OBSERVE("obs", "observe a tile in the game", new ArgumentDefinition[]{}) {
        @Override
        public void perform(String[] args) {
            // Do nothing, the TileData is responsible for this event, and if it does not handle it, it is not
            // applicable, thus nothing happens. So, do nothing either way.
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex, String beginning) {
            return null;    // not applicable to TileData, silently ignore
        }
    },
    INVESTIGATE("inv", "TODO", new ArgumentDefinition[]{}) {
        @Override
        public void perform(String[] args) {
            // Do nothing, the TileData is responsible for this event, and if it does not handle it, it is not
            // applicable, thus nothing happens. So, do nothing either way.
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex, String beginning) {
            return null;    // not applicable to TileData, silently ignore
        }
    },
    INTERACT("int", "interact with an entity in the game", new ArgumentDefinition[]{
            new ArgumentDefinition("target", ArgumentType.ENTITY)
    }) {
        @Override
        public void perform(String[] args) {
            // Do nothing, the TileData is responsible for this event, and if it does not handle it, it is not
            // applicable, thus nothing happens. So, do nothing either way.
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex, String beginning) {
            return null;    // not applicable to TileData, silently ignore
        }
    };

    private String name;
    private String description;
    private ArgumentDefinition[] arguments;
    CommandDefinition(String name, String description, ArgumentDefinition[] arguments) {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
    }
    public String getName() {
        return name;
    }
    public String toString() { return name; }
    public String getDescription() {
        return description;
    }
    public String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(' ');
        for (ArgumentDefinition argumentDef : arguments) {
            sb.append('<');
            sb.append(argumentDef.getName());
            sb.append('>');
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

    /**
     *
     * @param beginning what the user has already typed in in the word
     * TODO: add <code>scene</code> paramater that affects suggestions based on the situation
     * @return a list of auto-completed suggestions
     */
    public ArrayList<String> tabComplete(int argIndex, String beginning) {
        return null;
    }

    public static CommandDefinition forName(String name) {
        for (CommandDefinition def : CommandDefinition.values()) {
            if (def.getName().equals(name)) return def;
        }
        throw new NoSuchElementException("No such command: '" + name + "'");
    }
}