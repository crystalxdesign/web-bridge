package com.crystalx.bridgeserver.model;

public class Deck {
    /**
     * Internal storage for the deck.
     */
    private Card[] deck;

    /**
     * Create a new deck.
     */
    public Deck() {
        this.deck = new Card[52];

        int pos = 0;
        for (int r = 2; r <= Rules.ACE; r++) {
            for (int s = Rules.CLUBS; s <= Rules.SPADES; s++) {
                this.deck[pos] = new Card(r, s);
                pos++;
            }
        }
    }

    /**
     * Randomly shuffle the deck in-place.
     *
     * The shuffle algorithm is the Fisher-Yates shuffle (modified by
     * Durstenfeld).
     * @see <a href="https://en.wikipedia.org/wiki/Fisher-Yates_shuffle#The_modern_algorithm">
     *   Fisher-Yates shuffle on Wikipedia
     * </a>
     */
    public void shuffle() {
        int j; // A random number for performing the shuffle
        java.util.Random r = new java.util.Random();
        Card temp; // A temporary variable for swapping values

        for (int i = this.deck.length - 1; i > 0; i--) {
            j = r.nextInt(i + 1); // Generate a random integer in the range [0, i]

            // Swap this.deck[i] and this.deck[j]
            temp = this.deck[i];
            this.deck[i] = this.deck[j];
            this.deck[j] = temp;
        }
    }

    /**
     * Deal a specified number of cards and remove them from the deck.
     *
     * @param number the number of cards to deal
     * @return an array of cards of length {@code number}
     */
    public Card[] deal(int number) {
        if (number < 1 || number > this.deck.length) {
            throw new IllegalArgumentException("Number of cards must between 1 and " + deck.length + ".");
        }

        // Get the first n cards from the deck
        Card[] cards = new Card[number];
        for (int i = 0; i < number; i++) { cards[i] = this.deck[i]; }

        // Remove the dealt cards from the deck
        Card[] newDeck = new Card[this.deck.length - number];
        for (int i = number; i < this.deck.length; i++) { newDeck[i - number] = this.deck[i]; }
        this.deck = newDeck;

        return cards;
    }
}
