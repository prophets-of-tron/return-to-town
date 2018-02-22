package com.github.aworldwithoutcsharp.returntotown.main.entities;

import com.github.aworldwithoutcsharp.returntotown.main.Core;

public class Player extends Entity {
    public int score;

    public Player(boolean tutorial) {
        location = tutorial ? /*replace this when decided*/null : Core.Location.FOREST;
    }
}
