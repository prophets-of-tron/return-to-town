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
                        "This scene is designed to familiarize yourself with the text interface of this game... Where you will begin is in a large ballroom attending a gala. There is a POSTER on the wall next to you. You are surrounded by a mass amount of PATRONS and investors. you are holding a glass of CHAMPAGNE and wearing a nice tuxedo. You are the clearly upper-class CEO of the major pharmaceutical of the community. Try INTERACTING with the POSTER.");
        String input = "";
        while (! input.equals("exit")) {
            String output = createOutput();
            System.out.println(output);
            input = Core.input();
        }
        while(! input.equals("interact(POSTER)"))
    }
}
