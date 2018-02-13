package com.github.aworldwithoutcsharp.returntotown.main;

import com.github.aworldwithoutcsharp.returntotown.main.scenes.Tutorial;

import java.util.Scanner;

/*
 * TODO: {
 *     Mechanics:
 *       - input:
 *         - command-based
 *       - output:
 *         - details / paragraph
 *         - possibly formatted keywords??
 *     I. Tutorial
 *     II. Game
 *       A. starts out in forest
 *       B. searches for clues
 *       C. discovers beginning
 * }
 */

public class Core {
    private static final String PROMPT = "> ";

    private static Scanner in = new Scanner(System.in);
    public static String input() {
        System.out.print(PROMPT);
        return in.nextLine();
    }
    private void run() {
        Tutorial.run();
    }

    public static void main(String[] args) {
        new Core().run();
    }
}
