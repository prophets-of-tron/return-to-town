package com.github.aworldwithoutcsharp.returntotown.main;

import com.github.aworldwithoutcsharp.returntotown.main.command.Command;
import com.github.aworldwithoutcsharp.returntotown.main.command.InvalidCommandException;
import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.github.aworldwithoutcsharp.returntotown.main.scenes.Tutorial;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;

import java.io.IOException;

/*
 * TODO: {
 *     Mechanics:
 *       - input:
 *         - command-based
 *       - output:
 *         - details / paragraph
 *         - possibly formatted keywords??
 *     I. Tutorial [Gala]
 *       A. Tasks
 *         1) make an order
 *         2) drink
 *         3) observation
 *       B. Focus on...
 *         1) commands
 *         2) observation / using text output
 *     II. Game
 *       A. Basic Plot
 *         1) starts out in forest
 *         2) searches for clues
 *         3) Sara leaves notes trying to convince player to abandon the search/hunt for Ralph's notes
 *         4) left with decision: leave or stay in company
 *         5) message / question of morality
 *       B. Location-based Events
 *         1+) notes
 *           a. text
 *           b. receipts for former drugs
 *           c. picture of father in hospital | cat scan
 *         2)
 *       C. Characters
 *         1) protagonist
 *           a. CEO of a pharmaceutical company
 *           b. focus is on the profit
 *         2) "Ralph"
 *           a. lost father to disease, couldn't afford medicine
 *         3) "Sara"
 *           a. protag's secretary
 *           b. guards (who almost caught Ralph while drugging protag) tell Sara where-abouts of Ralph
 *           c. tells protag that his business is helping economy
 *         4) [protag's daughter]
 * }
 */

public class Core {
    private static void run() {
        Tutorial.run();
    }

    public static void exit() {
        // TODO: save changes as needed
        System.exit(0);
    }

    public static void main(String[] args) {
        Core.run();
    }
}
