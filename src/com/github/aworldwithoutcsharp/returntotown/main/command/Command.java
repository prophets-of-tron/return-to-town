package com.github.aworldwithoutcsharp.returntotown.main.command;

import java.util.NoSuchElementException;

public class Command {
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
        if (args.length != definition.getArguments().length)
            throw new InvalidCommandException("Command '"+commandName+"' requires " + definition.getArguments().length
                    +" argument(s), not "+ args.length);
        if (words.length > 1) {
            for (int i=1; i<words.length; i++) args[i-1] = words[i];
        }
        for (int i=0; i<definition.getArguments().length; i++) {
            ArgumentDefinition argumentDef = definition.getArguments()[i];
            if(! argumentDef.getType().validateArgument(args[i]))
                throw new InvalidCommandException("Argument '" + argumentDef + "' should be of type '" + argumentDef.getType().toString().toLowerCase());
        }
        return new UserCommand(definition, args);
    }
}
