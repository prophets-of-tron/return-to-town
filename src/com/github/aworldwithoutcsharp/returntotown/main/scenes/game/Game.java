package com.github.aworldwithoutcsharp.returntotown.main.scenes.game;

import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.github.aworldwithoutcsharp.returntotown.main.entities.Player;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;

public class Game {
    public static Game instance;

    public Player player;

    public Game() {
        Game.instance = this;
    }

    public void run() {
        Console.println("TODO");
        UserCommand input;
        while (true) {
            input = Console.input();

            // this could result in Core.exit(), so that's how we escape the loop
            if (input != null)
                input.getDefinition().perform(input.getArguments());    // will only do anything meaningful for the enum
            // instances that have explicitly defined
            // #perform(); see command.CommandDefinition


        }
    }
}
