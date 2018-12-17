package com.github.aworldwithoutcsharp.returntotown.main.command;

import com.github.aworldwithoutcsharp.returntotown.main.Core;
import com.github.aworldwithoutcsharp.returntotown.main.entities.Player;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;
import com.github.aworldwithoutcsharp.returntotown.main.scenes.game.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

public enum CommandDefinition {
    // Global commands
    EXIT("exit", "stop playing", new ArgumentDefinition[]{}) {
        @Override
        public void perform(String[] args) {
            Core.exit();
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex) {
            return null;    // not applicable to TileData, silently ignore
        }
    },
    HELP("help", "prints out command help", new ArgumentDefinition[]{
            new ArgumentDefinition("command", ArgumentType.COMMAND_NAME, false)
    }) {
        @Override
        public void perform(String[] args) {
            if (args.length == 0) {
                // iterate over all CommandDefinition instances
                for (CommandDefinition def : values()) {
                    Console.println(def.getUsage());
                    Console.println(" - " + def.getDescription());
                }
            } else {
                CommandDefinition def = CommandDefinition.forName(args[0]);
                Console.println(def.getUsage());
                Console.println(" - " + def.getDescription());
            }
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex) {
            ArrayList<String> suggestions = new ArrayList<>();
            // iterate over possible commands, because this is `help`
            // TODO: make it depend on the scene / "conditional" commands
            for (CommandDefinition command : values()) {
                suggestions.add(command.getName());
            }
            System.out.println(suggestions);
            return suggestions;
        }
    },
    MOVE("mov", "move to an adjacent tile in the game", new ArgumentDefinition[]{
            new ArgumentDefinition("direction", ArgumentType.DIRECTION, true)
    }) {
        @Override
        public void perform(String[] args) {
            Player player = Core.getPlayer();

            String dir = args[0];
            if (dir.equalsIgnoreCase("west")) {
                if (player.x == 0) throw new IllegalStateException("Cannot break the fourth wall.");
                else player.x--;
            }
            if (dir.equalsIgnoreCase("east")) {
                if (player.x == World.WIDTH - 1) throw new IllegalStateException("Cannot break the fourth wall.");
                else player.x++;
            }
            if (dir.equalsIgnoreCase("south")) {
                if (player.y == 0) throw new IllegalStateException("Cannot break the fourth wall.");
                else player.y--;
            }
            if (dir.equalsIgnoreCase("north")) {
                if (player.y == World.HEIGHT - 1) throw new IllegalStateException("Cannot break the fourth wall.");
                else player.y++;
            }
            try {
                throw new Exception("shouldn't be happening; just a safe check");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex) {
            return new ArrayList<>(Arrays.asList(new String[]{"east", "north", "west", "south"}));
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
        public ArrayList<String> tabComplete(int argIndex) {
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
        public ArrayList<String> tabComplete(int argIndex) {
            return null;    // not applicable to TileData, silently ignore
        }
    },
    INTERACT("int", "interact with an entity in the game", new ArgumentDefinition[]{
            new ArgumentDefinition("target", ArgumentType.ENTITY, true)
    }) {
        @Override
        public void perform(String[] args) {
            // Do nothing, the TileData is responsible for this event, and if it does not handle it, it is not
            // applicable, thus nothing happens. So, do nothing either way.
        }

        @Override
        public ArrayList<String> tabComplete(int argIndex) {
            return null;    // not applicable to TileData, silently ignore
        }
    };

    private String name;
    private String description;
    private ArgumentDefinition[] arguments;

    CommandDefinition(String name, String description, ArgumentDefinition[] arguments) {
        this.name = name;
        this.description = description;

        boolean reachedRequiredArg = false;
        for (ArgumentDefinition argumentDef : arguments) {
            if (argumentDef.isRequired()) reachedRequiredArg = true;
            else if (reachedRequiredArg)
                throw new IllegalArgumentException("Optional arguments must be after all required arguments!");
        }
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
    public int getRequiredArgumentCount() {
        int count = 0;
        for (ArgumentDefinition argumentDef : arguments) {
            if (argumentDef.isRequired()) count++;
        }
        return count;
    }

    /**
     *
     * @param args the list of argument values the USER provides
     */
    public void perform(String[] args) {}

    /**
     *
     * @return a list of auto-completed suggestions
     */
    public ArrayList<String> tabComplete(int argIndex) {
        return null;
    }

    public static CommandDefinition forName(String name) {
        for (CommandDefinition def : CommandDefinition.values()) {
            if (def.getName().equals(name)) return def;
        }
        throw new NoSuchElementException("No such command: '" + name + "'");
    }
}