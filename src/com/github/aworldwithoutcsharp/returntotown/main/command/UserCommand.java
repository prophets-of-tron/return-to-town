package com.github.aworldwithoutcsharp.returntotown.main.command;

public class UserCommand {
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