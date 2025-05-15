package com.crystalx.bridgeserver.exceptions;

public class MalformedCallException extends RuntimeException {
    /**
     * Create the exception with the default message.
     */
    public MalformedCallException() {
        super("Invalid call.");
    }

    /**
     * Create the exception and add the entered string to the message.
     *
     * @param call what the user attempted to convert to a call
     */
    public MalformedCallException(String call) {
        super("Invalid call: " + call);
    }
}
