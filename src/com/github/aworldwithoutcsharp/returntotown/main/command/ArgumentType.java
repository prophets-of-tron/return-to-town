package com.github.aworldwithoutcsharp.returntotown.main.command;

import java.util.NoSuchElementException;

public enum ArgumentType {
    ENTITY {
        @Override
        public boolean validateArgument(String value) {
            return true;    // TODO: change when we have an Entity enum
        }
    }, DIRECTION {
        @Override
        public boolean validateArgument(String value) {
            for (String direction : new String[]{"east", "north", "west", "south"}) {
                if (!direction.equalsIgnoreCase(value)) return false;
            }
            return true;
        }
    }, COMMAND_NAME {
        /**
         *
         * @param value
         * @return <code>true</code> if <code>value</code> is a command name and <code>false</code> otherwise
         */
        @Override
        public boolean validateArgument(String value) {
            try {
                CommandDefinition.forName(value);
            } catch (NoSuchElementException e) {
                return false;
            }
            return true;
        }
    };

    /**
     * This really should be reimplemented in the enum instance initiation!
     * @param value
     * @return
     */
    public boolean validateArgument(String value) {
        return false;
    }

    /**
     * A convenience method for {@link ArgumentType#validateArgument(String)}
     * @param value what the user provided as an argument
     * @param type what <code>value</code> should be
     * @return
     */
    public static boolean validateArgument(String value, ArgumentType type) {
        return type.validateArgument(value);
    }
}