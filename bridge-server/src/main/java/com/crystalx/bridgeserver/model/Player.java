package com.crystalx.bridgeserver.model;

public class Player {
    private String name = "";
    private String userId = "";
    private Card[] hand;
    /**
     * Create a player and sort their hand.
     *
     * @param hand an array of {@code Card}s of length 13
     */
    
    public Player() {
    }
    
    public Player(Card[] hand) {
    	setHand(hand);
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getName() {
    	return this.name;
    }

    public void setUserId(String userId) {
    	this.userId = userId;
    }

    public String getUserId() {
    	return this.userId;
    }

    public void setHand(Card[] hand) {
        this.hand = hand;
        java.util.Arrays.sort(hand);
    }
    /**
     * Get a specific card.
     *
     * @param pos the position to retrieve
     * @return the card at {@code pos}
     */
    public Card cardAt(int pos) { return this.hand[pos]; }

    /**
     * Get the hand.
     *
     * @return the player's hand
     */
    public Card[] getHand() { return this.hand; }

    /**
     * Find a {@code Card} in the hand.
     *
     * @param c the card to find
     * @return the position of the card, or {@literal <} 0 if not found
     */
    public int find(Card c) {
        for (int i = 0; i < this.hand.length; i++) {
            if (this.hand[i].equals(c)) { return i; }
        }

        return -1;
    }

    /**
     * Play a card, removing it from the hand.
     *
     * @param pos the position of the card to play
     * @return the card at {@code pos}
     */
    public Card playCard(int pos) {
        Card played = this.cardAt(pos); // Save the card played to be returned later

        // Remove the card
        Card[] newHand = new Card[this.hand.length - 1];
        for (int i = 0; i < pos; i++) { newHand[i] = this.hand[i]; } // Copy everything up to pos
        for (int i = pos; i < newHand.length; i++) { newHand[i] = this.hand[i+1]; } // Skip the card at pos
        this.hand = newHand;

        return played;
    }

    /**
     * Check if the player has a card of the given suit.
     *
     * @param suit the suit to search for
     * @return {@code true} if the suit was found, {@code false} otherwise
     */
    public boolean hasSuit(int suit) {
        boolean out = false;

        for (Card c : this.hand) {
            out = out || c.suit() == suit;
        }

        return out;
    }

    public Card[] suit(int s) {
        // Count the suit
        int numSuit = 0;
        for (int i = 0; i < this.hand.length; i++) {
            if (this.hand[i].suit() == s) { numSuit++; }
        }

        int pos = 0;
        Card[] suit = new Card[numSuit];
        for (Card c : this.hand) {
            if (c.suit() == s) {
                suit[pos] = c;
                pos++;
            }
        }

        return suit;
    }
}
