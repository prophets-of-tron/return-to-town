package com.github.aworldwithoutcsharp.returntotown.main.command;

public class ArgumentDefinition {
    private String name;
    private ArgumentType type;
    private boolean required;
    public ArgumentDefinition(String name, ArgumentType type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }
    public String getName() {
        return name;
    }
    public ArgumentType getType() {
        return type;
    }
    public boolean isRequired() { return required; }
}