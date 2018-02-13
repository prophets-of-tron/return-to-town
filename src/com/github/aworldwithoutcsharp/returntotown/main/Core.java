package com.github.aworldwithoutcsharp.returntotown.main;

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
    private static final String PROMPT_SEPARATOR = "";

    private static Scanner in = new Scanner(System.in);
    public static String input() {
        return in.nextLine();
    }
    public static String input(String prompt) {
        System.out.print(prompt + PROMPT_SEPARATOR);
        return in.nextLine();
    }

    public static void main(String[] args) {

    }
}
