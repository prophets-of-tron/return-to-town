package com.github.aworldwithoutcsharp.returntotown.main;

import com.github.aworldwithoutcsharp.returntotown.main.entities.Player;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;
import com.github.aworldwithoutcsharp.returntotown.main.scenes.game.Game;
import com.github.aworldwithoutcsharp.returntotown.main.scenes.tutorial.Tutorial;

/*
 * TODO: {
 *     Mechanics:
 *       - input:
 *         - command-based
 *       - output:
 *         - details / paragraph
 *         - possibly formatted keywords??
 *     I. TutorialData [Gala]
 *       A. Tasks
 *         1) make an order
 *         2) drink
 *         3) observation
 *       B. Focus on...
 *         1) commands
 *         2) observation / using text output
 *     II. GameData
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
    public static boolean inTutorial;
    public static Player getPlayer() {
        // no player in tutorial; player isn't technically an "entity" there, just a virtual agent
        if (inTutorial) throw new IllegalStateException("This command does not apply to the tutorial");
        return Game.instance.player;
    }

    private static void init() {
        Console.init("Return to Town", new Runnable() {
            @Override
            public void run() {
                runGame();
            }
        });
    }
    private static void runGame() {
        inTutorial = true;
        new Tutorial().run();
        inTutorial = false;
        // new Game().run();
    }

    public static void exit() {
        // TODO: save changes as needed
        System.exit(0);
    }

    public static void main(String[] args) {
        Core.init();
    }
}
