package com.github.aworldwithoutcsharp.returntotown.main.scenes.game;

import com.github.aworldwithoutcsharp.returntotown.main.command.UserCommand;
import com.github.aworldwithoutcsharp.returntotown.main.io.Console;
import com.github.aworldwithoutcsharp.returntotown.main.util.data.DataManager;
import com.github.aworldwithoutcsharp.returntotown.main.util.data.ProjectData;

public class Tile {
    private int position;
    public Tile(int position) {
        this.position = position;
    }

    public String getOutput() {
        return DataManager.getData().game.tiles[this.position].intro;
    }

    void onCommand(UserCommand command) {
        ProjectData.GameData.TileData tileData = DataManager.getData().game.tiles[this.position];
        String output;
        switch (command.getDefinition()) {
            case OBSERVE: {
                output = tileData.observe;
                break;
            }
            case INTERACT: {
                output = tileData.interact;
                break;
            }
            default: throw new IllegalArgumentException("Invalid command definition: "
                    + command.getDefinition().getName());
        }
        if (output.length() == 0 || output == null) return; // silently ignore if command isn't applicable
        Console.writeln(output);
    }
}
