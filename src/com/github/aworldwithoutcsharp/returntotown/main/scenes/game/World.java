package com.github.aworldwithoutcsharp.returntotown.main.scenes.game;

import com.github.aworldwithoutcsharp.returntotown.main.util.data.DataManager;

public class World {
    public static final int WIDTH = DataManager.getData().game.width, HEIGHT = DataManager.getData().game.height;
    private static Tile[] tiles = new Tile[WIDTH * HEIGHT];

    static {
        initTiles();
    }
    private static void initTiles() {
        // init default tiles with no functionality (TODO: change)
        for (int i=0; i<WIDTH*HEIGHT; i++) {
            tiles[i] = new Tile(i);
        }
    }

    public static Tile[] getTiles() {
        return tiles;
    }
}
