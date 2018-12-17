package com.github.aworldwithoutcsharp.returntotown.main.command;

import java.util.NoSuchElementException;

public class Command {
    public static boolean validateArgumentCount(CommandDefinition definition, int argCount) {
        return argCount >= definition.getRequiredArgumentCount() && argCount <= definition.getArguments().length;
    }

    public static UserCommand parseInput(String s) throws InvalidCommandException {
        if (s.length() == 0) return null;
        String[] words = s.replaceAll(" +", " ").split(" ");
        String commandName = words[0];
        CommandDefinition definition;
        try {
            definition = CommandDefinition.forName(commandName);
        } catch (NoSuchElementException e) {
            throw new InvalidCommandException("Invalid command: " + commandName);
        }

        String[] args = new String[words.length - 1];
        // be more specific than using validateArgumenetCount
        if (args.length < definition.getRequiredArgumentCount())
            throw new InvalidCommandException("Command '"+commandName+"' requires " + definition.getRequiredArgumentCount()
                    +" argument(s), not "+ args.length);
        if (args.length > definition.getArguments().length)
            throw new InvalidCommandException("Command '"+commandName+"' accepts " + definition.getArguments().length + " argument(s), not "+args.length);
        if (words.length > 1) {
            for (int i=1; i<words.length; i++) args[i-1] = words[i];
        }
        for (int i=0; i<definition.getArguments().length; i++) {
            ArgumentDefinition argumentDef = definition.getArguments()[i];
            if (argumentDef.isRequired() && i >= args.length) continue;
            if(!(i < args.length && argumentDef.getType().validateArgument(args[i])))
                throw new InvalidCommandException("Argument '" + argumentDef + "' should be of type '" + argumentDef.getType().toString().toLowerCase());
        }
        return new UserCommand(definition, args);
    }
}
