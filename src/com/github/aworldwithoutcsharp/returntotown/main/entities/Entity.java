package com.github.aworldwithoutcsharp.returntotown.main.entities;

public class Entity {
    public int x, y;
    private int health = 100;

    public void damage(int amount) {
        health -= amount;
        if (health <= 0) die();
    }
    public void die() {
        // TODO: die
    }
    public void heal(int amount) {
        health += amount;
        if (health > 100) health = 100;
    }
}
