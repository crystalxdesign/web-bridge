package com.crystalx.bridgeserver.model;

import com.crystalx.bridgeserver.exceptions.MalformedCallException;

public class Double extends Call {
    private int level;

    public Double() {
        this.level = Rules.DOUBLE;
    }

    public Double(int level) {
        this.level = level;
    }

    public Double(String doubleStr) {
    	doubleStr = doubleStr.toLowerCase();

        // Validate the input
        if (!Call.isDouble(doubleStr)) {
            throw new MalformedCallException();
        }

        this.level = doubleStr.equals("x") ? Rules.DOUBLE : Rules.REDOUBLE;
    }

    public int level() {
        return this.level;
    }

    public String toString() {
        if (this.level == Rules.DOUBLE) {
            return "X";
        }
        else if (this.level == Rules.REDOUBLE) {
            return "XX";
        }
        else {
            return "";
        }
    }
}
