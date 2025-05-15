package com.crystalx.bridgeserver.model;

import java.util.regex.Pattern;

import com.crystalx.bridgeserver.exceptions.MalformedCardException;

import java.util.regex.Matcher;

public class Card implements Comparable<Card> {
    /**
     * The rank of the card.
     *
     * Must be in 2 - 12, inclusive.
     */
    private int rank;
    /**
     * The suit of the card.
     *
     * Must be one of: 0 (clubs), 1 (diamonds), 2 (hearts), or 3 (spades).
     */
    private int suit;

    /**
     * Creates the default card (ace of spades).
     */
    public Card() { this(12, 3); }

    /**
     * Creates a card from a string. The string is accepted if if is of the
     * form {@code rank+suit}, parsed by the following rules:
     * - {@code rank} is a number from 2-10 (inclusive) of one of T (ten), J
     *   (jack), Q (queen), K (king), and A (ace).
     * - {@code suit} is one of C (clubs), D (diamonds), H (hearts), and S
     *   (spades).
     * - The string is parsed case-insensitively.
     *
     * @param cardStr a string representing the card to be created
     * @throws MalformedCardException if the passed string doesn't represent a
     *                                card
     */
    public Card(String cardStr) {
        cardStr = cardStr.toLowerCase(); // The conversion is case-insensitive

        // Split the string into two parts
        Matcher cardMatch = Pattern.compile("^(?<rank>[2-9]|10|[TtJjQqKkAa])(?<suit>[CcDdHhSs])$").matcher(cardStr);
        if (!cardMatch.matches()) { // Validate the input
            throw new MalformedCardException(cardStr);
        }

        String rankStr = cardMatch.group("rank");
        String suitStr = cardMatch.group("suit");

        // Convert the strings entered to chars
        char r; // Two options for r: 2-9/t or 10, the latter case is equivalent to t
        if (rankStr.length() == 1) { r = rankStr.charAt(0); }
        else { r = 't'; }

        char s = suitStr.charAt(0);

        if (r >= '2' && r <= '9') { rank = (int)r - 48; } // Convert char digit to integer to get the rank
        else if (r == 't')        { rank = Rules.TEN; }
        else if (r == 'j')        { rank = Rules.JACK; }
        else if (r == 'q')        { rank = Rules.QUEEN; }
        else if (r == 'k')        { rank = Rules.KING; }
        else if (r == 'a')        { rank = Rules.ACE; }

        if (s == 'c')      { suit = Rules.CLUBS; }
        else if (s == 'd') { suit = Rules.DIAMONDS; }
        else if (s == 'h') { suit = Rules.HEARTS; }
        else if (s == 's') { suit = Rules.SPADES; }

        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Creates a card from the rank and suit.
     *
     * @param rank the rank of the card, an integer between 2 and 14, inclusive
     * @param suit the suit of the card, an integer between 0 and 3, inclusive
     */
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Accessor for the rank of the card.
     *
     * @return the rank of the card
     */
    public int rank() { return this.rank; }

    public String rankStr() {
        String out;
        if (this.rank >= 2 && this.rank <= 9)  { out = new Integer(this.rank).toString(); }
        else if (this.rank == Rules.TEN)       { out = "t"; }
        else if (this.rank == Rules.JACK)      { out = "j"; }
        else if (this.rank == Rules.QUEEN)     { out = "q"; }
        else if (this.rank == Rules.KING)      { out = "k"; }
        else                                   { out = "a"; }

        return out;
    }

    /**
     * Accessor for the suit of the card.
     *
     * @return one of {@code Rules.CLUBS}, {@code Rules.DAIMONDS},
     *         {@code Rules.HEARTS}, and {@code Rules.SPADES}
     */
    public int suit() { return this.suit; }

    public String suitStr() {
        String out;
        if (this.suit == Rules.CLUBS)         { out = "c"; }
        else if (this.suit == Rules.DIAMONDS) { out = "d"; }
        else if (this.suit == Rules.HEARTS)   { out = "h"; }
        else                                  { out = "s"; }

        return out;
    }

    /**
     * Convert the {@code Card} to a string.
     * If {@code c} is a {@code Card} and
     * {@code Card c1 = new Card(c.toString())}, the two objects should be
     * equivalent.
     *
     * @return a string
     */
    public String toString() {
        String r = this.rankStr();
        String s = this.suitStr();

        return r+s;
    }

    public boolean equals(Card c) {
        return c.rank() == this.rank && c.suit() == this.suit;
    }

    /**
     * Find whether one card is smaller, equal to, or greater than another.
     * A card is smaller than another one if one of the following is satisfied:
     * - The suit of the former is less than the suit of the latter
     * - The suits are equal and the suit of the former is less than that of
     *   the latter
     * Two cards are equal if their suits and ranks are equal. Otherwise, the
     * first card is larger than the second.
     *
     * @param c the card to compare to this one
     * @return {@literal <} 0 if the first card is less than the second, 0 if
     *         they are equal, {@literal >} 0 otherwise.
     * @see java.lang.Comparable#compareTo
     */
    public int compareTo(Card c) {
        int result = this.suit() - c.suit();
        if (result == 0) { result = this.rank() - c.rank(); }

        return result;
    }
}
