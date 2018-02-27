package com.github.aworldwithoutcsharp.returntotown.main.scenes;

import com.github.aworldwithoutcsharp.returntotown.main.Core;

public class Tutorial {

    private static String createOutput() {
        String output = "";
        return output;
    }

    public static void run() {
        System.out.println(
                "Welcome to the tutorial!" + " " +
                        "This scene is designed to familiarize yourself with the text interface of this game...");
        Core.UserCommand input = null;

        while (input != null ? input.getDefinition() != Core.CommandDefinition.EXIT : true) {
            String output = createOutput();
            System.out.println(output);
            input = Core.input();

            input.getDefinition().perform();    // will only do anything for the enum instances that have explicitely
                                                // defined #perform(); see Core.CommandDefinition
        }
    }
}
