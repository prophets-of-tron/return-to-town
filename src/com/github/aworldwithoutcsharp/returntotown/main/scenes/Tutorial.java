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
                        "This scene is designed to familiarize yourself with the text interface of this game... You will begin in a ballroom at a gala, with PATRONS of your pharmacuetical company all around. There is also a POSTER nearby, and there is a waiter coming your way with a tray of CHAMPAGNE. Try Interacting with the POSTER.");
        Core.Command input = null;

        while (input != null ? input.getType() != Core.CommandType.EXIT : true) {
            String output = createOutput();
            System.out.println(output);
            input = Core.input();
        }
    }
}
