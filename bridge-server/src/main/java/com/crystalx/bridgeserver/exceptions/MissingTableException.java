package com.crystalx.bridgeserver.exceptions;

public class MissingTableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	/**
     * Create the exception with the default message.
     */
    public MissingTableException() {
        super("Invalid table ID.");
    }

    /**
     * Create the exception and add the entered string to the message.
     *
     * @param card what the user attempted to convert to a card
     */
    public MissingTableException(String id) {
        super("Invalid table: " + id);
    }
}
