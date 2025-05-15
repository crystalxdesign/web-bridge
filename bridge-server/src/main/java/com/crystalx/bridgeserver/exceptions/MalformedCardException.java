package com.crystalx.bridgeserver.exceptions;

public class MalformedCardException extends RuntimeException {
    /**
     * Create the exception with the default message.
     */
    public MalformedCardException() {
        super("Invalid card.");
    }

    /**
     * Create the exception and add the entered string to the message.
     *
     * @param card what the user attempted to convert to a card
     */
    public MalformedCardException(String card) {
        super("Invalid card: " + card);
    }
}
