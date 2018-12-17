package com.github.aworldwithoutcsharp.returntotown.main.util.data;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Structure of YAML file, for Jackson YAML library
 *
 * @author clabe45
 */
public class ProjectData {
    @JsonProperty
    public GameData game;
    @JsonProperty
    public TutorialData tutorial;

    public static class GameData {
        @JsonProperty
        public int width, height;

        @JsonProperty
        public TileData[] tiles;

        public static class TileData {
            @JsonProperty
            public String intro, observe, interact;
        }
    }
    public static class TutorialData {

    }
}
