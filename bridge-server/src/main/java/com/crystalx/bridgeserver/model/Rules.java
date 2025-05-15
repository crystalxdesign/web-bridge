package com.crystalx.bridgeserver.model;

public final class Rules {
    public static final int CLUBS    = 0;
    public static final int DIAMONDS = 1;
    public static final int HEARTS   = 2;
    public static final int SPADES   = 3;
    public static final int NOTRUMP  = 4;

    public static final int TEN   = 10;
    public static final int JACK  = 11;
    public static final int QUEEN = 12;
    public static final int KING  = 13;
    public static final int ACE   = 14;

    public static final int NORTH = 0;
    public static final int EAST  = 1;
    public static final int SOUTH = 2;
    public static final int WEST  = 3;

    public static final int UNDOUBLED = 0;
    public static final int DOUBLE    = 1;
    public static final int REDOUBLE  = 2;

    public static final int BIDDING  = 0;
    public static final int CARDPLAY = 1;
 
    private Rules() {} // A Rules object should never be instantiated

    /**
     * Check if a card has the same suit as the lead.
     *
     * @param check the {@code Card} to test
     * @param lead the {@code Card} lead, or {@code null} if this is the first
     *             card of the trick
     * @return {@code true} if the suits of the two cards are the same,
     *         {@code false} otherwise
     */
    public static boolean followsSuit(Card check, Card lead) {
        int leadSuit = lead == null ? check.suit() : lead.suit(); // Any suit can be played if this is the card lead

        return check.suit() == leadSuit;
    }

    /**
     * Check if it is legal to play a card.
     *
     * The card is legal if the following conditions are met:
     * - The card is in the player's hand.
     * - The suit of the card is the same as the suit of the lead, unless either
     *   of the following are true:
     *  - The card being played is the lead
     *  - The player can't follow suit
     *
     * @param check the {@code card} to test
     * @param p the person playing the card, before it's removed from their hand
     * @param lead the {@code Card} lead, or {@code null} if this is the first
     *             card of the trick
     * @return {@code true} if this is a legal play, {@code false} otherwise
     */
    public static boolean playable(Card check, Player p, Card lead) {
        int leadSuit = lead == null ? check.suit() : lead.suit(); // Any suit can be played if this is the card lead

        boolean legalSuit = Rules.followsSuit(check, lead) || !p.hasSuit(leadSuit);
        boolean legalCard = p.find(check) >= 0;

        return legalSuit && legalCard;
    }

    /**
     * Get the name of a player from their number.
     *
     * @param player the number of the player
     * @return the name of the player as a String
     */
    public static String playerName(int player) {
        String name = "";
        if (player == Rules.NORTH)      { name = "North"; }
        else if (player == Rules.EAST)  { name = "East"; }
        else if (player == Rules.SOUTH) { name = "South"; }
        else if (player == Rules.WEST)  { name = "West"; }

        return name;
    }

    /**
     * Find the declarer of an auction.
     *
     * @param calls the auction history
     * @param side the side to make the last bid
     * @param strain the strain of the last bid
     * @param dealer the person who bid first
     * @return the declarer
     */
    public static int declarer(java.util.List<Call> calls, int side, int strain, int dealer) {
        int declarer = -1;

        for (int i = calls.size() - 1; i >= 0; i--) {
            if (calls.get(i) instanceof Bid && ((Bid) calls.get(i)).strain() == strain && (i + dealer) % 2 == side) {
                declarer = (i + dealer) % 4;
            }
        }

        return declarer;
    }
}
