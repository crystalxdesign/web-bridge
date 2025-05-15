package com.crystalx.bridgeserver.model;

import java.util.regex.Pattern;

public abstract class Call {

    /**
     * Check if a string is a valid call.
     *
     * @param s the string to check
     * @return if the string is a valid (but not necessarily legal) call.
     */
    public static boolean isCall(String s) {
        return Pattern.matches("^[Pp]|[Xx][Xx]?|[1-7](?:[CcDdHhSs]|[Nn][Tt]?)$", s);
    }

    /**
     * Check if a string is a valid bid.
     *
     * @param s the string to check
     * @return if the string is a valid (but not necessarily legal) bid.
     */
    public static boolean isBid(String s) {
        return Pattern.matches("^[1-7](?:[CcDdHhSs]|[Nn][Tt]?)$", s);
    }

    /**
     * Check if a string is a valid double.
     *
     * @param s the string to check
     * @return if the string is a valid (but not necessarily legal) double.
     */
    public static boolean isDouble(String s) {
        return Pattern.matches("^[Xx][Xx]?$", s);
    }
    
    /**
     * Check if a string is a pass.
     *
     * @param s the string to check
     * @return if the string is a valid (but not necessarily legal) pass.
     */
    public static boolean isPass(String s) {
        return Pattern.matches("^[Pp]ass$", s);
    }
}
