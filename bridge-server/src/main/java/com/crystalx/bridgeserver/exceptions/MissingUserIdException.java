package com.crystalx.bridgeserver.exceptions;

public class MissingUserIdException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	/**
     * Create the exception with the default message.
     */
    public MissingUserIdException() {
        super("Invalid user ID.");
    }

    /**
     * Create the exception and add the entered string to the message.
     *
     * @param card what the user attempted to convert to a card
     */
    public MissingUserIdException(String id) {
        super("Invalid userId: " + id);
    }
}
