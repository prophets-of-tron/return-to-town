package com.github.aworldwithoutcsharp.returntotown.main.scenes.tutorial;

import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;

public class Tutorial {
    public static Tutorial instance;
    // no player; player isn't technically an "entity" here, just a virtual agent

    public Tutorial() {
        Tutorial.instance = this;
    }

    public void run() {
        Console.println(
                "Welcome to the tutorial!" + " " +
                 "This scene is designed to familiarize yourself with the text interface of this game... You will begin in a ballroom at a gala, with PATRONS of your pharmaceutical company all around. There is also a POSTER nearby, and there is a waiter coming your way with a tray of CHAMPAGNE. Try Interacting with the POSTER.");
        UserCommand input;
        while (true) {
            input = Console.input();

            // this could result in Core.exit(), so that's how we escape the loop
            if (input != null)
                input.getDefinition().perform(input.getArguments());    // will only do anything meaningful for the enum
                                                                        // instances that have explicitely defined
                                                                        // #perform(); see command.CommandDefinition
        }
    }
}
