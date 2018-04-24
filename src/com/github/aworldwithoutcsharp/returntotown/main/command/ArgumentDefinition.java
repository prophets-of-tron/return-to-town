package com.github.aworldwithoutcsharp.returntotown.main.command;

public class ArgumentDefinition {
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