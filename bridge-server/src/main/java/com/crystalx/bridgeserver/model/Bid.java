package com.crystalx.bridgeserver.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crystalx.bridgeserver.exceptions.MalformedCallException;

public class Bid extends Call implements Comparable<Bid> {
    private int rank;
    private int strain;

    /**
     * Create the default bid (1C).
     */
    public Bid() {
        this.rank = 1;
        this.strain = Rules.CLUBS;
    }

    /**
     * Create bid from two ints.
     *
     * @param rank 1-7 inclusive
     * @param strain C, D, H, S, or NT
     */
    public Bid(int rank, int strain) {
        this.rank = rank;
        this.strain = strain;
    }

    /**
     * Creates a bid from a string. The string is accepted if if is of the
     * form {@code rank+strain}, parsed by the following rules:
     * - {@code rank} is a number from 1-7 (inclusive)
     * - {@code strain} is one of C (clubs), D (diamonds), H (hearts), S
     *   (spades), and NT or N (notrump).
     * - The string is parsed case-insensitively.
     *
     * @param bidStr a string representing the bid to be created
     * @throws MalformedCallException if the passed string doesn't represent a
     *                                bid
     */
    public Bid(String bidStr) {
        bidStr = bidStr.toLowerCase(); // The conversion is case-insensitive

        // Split the string into two parts
        Matcher bidMatch = Pattern.compile("^(?<rank>[1-7])(?<strain>[CcDdHhSs]|[Nn][Tt]?)$").matcher(bidStr);
        if (!bidMatch.matches()) { // Validate the input
            throw new MalformedCallException(bidStr);
        }

        String rankStr = bidMatch.group("rank");
        String strainStr = bidMatch.group("strain");

        // Convert the strings entered to chars
        char r = rankStr.charAt(0);
        char s = strainStr.charAt(0);

        int rank = (int)r - 48; // Convert char digit to integer to get the rank

        int strain;
        if (s == 'c')      { strain = Rules.CLUBS; }
        else if (s == 'd') { strain = Rules.DIAMONDS; }
        else if (s == 'h') { strain = Rules.HEARTS; }
        else if (s == 's') { strain = Rules.SPADES; }
        else               { strain = Rules.NOTRUMP; }

        this.rank = rank;
        this.strain = strain;
    }

    /**
     * Accessor for the rank of the bid.
     *
     * @return the rank of the bid
     */
    public int rank() { return this.rank; }

    /**
     * Accessor for the strain of the bid.
     *
     * @return one of {@code Rules.CLUBS}, {@code Rules.DAIMONDS},
     *         {@code Rules.HEARTS}, {@code Rules.SPADES}, and
     *         {@code Rules.NOTRUMP}
     */
    public int strain() { return this.strain; }

    /**
     * Find whether one bid is smaller, equal to, or greater than another.
     * A bid is smaller than another one if one of the following is satisfied:
     * - The strain of the former is less than the strain of the latter
     * - The strains are equal and the strain of the former is less than that of
     *   the latter
     * Two bids are equal if their strains and ranks are equal. Otherwise, the
     * first bid is larger than the second.
     *
     * @param b the bid to compare to this one
     * @return {@literal <} 0 if the first bid is less than the second, 0 if
     *         they are equal, {@literal >} 0 otherwise.
     * @see java.lang.Comparable#compareTo
     */
	public int compareTo(Bid b) {
      int result = this.rank - b.rank();
      if (result == 0) { result = this.strain - b.strain(); }

      return result;
	}
	
    /**
     * Convert the {@code Bid} to a string.
     * If {@code b} is a {@code Bid} and {@code Bid b1 = new Bid(b.toString())},
     * the two objects should be equivalent.
     *
     * @return a string
     */
    public String toString() {
        String rankStr = new Integer(this.rank).toString();

        String strainStr;
        if (this.strain == Rules.CLUBS)         { strainStr = "C"; }
        else if (this.strain == Rules.DIAMONDS) { strainStr = "D"; }
        else if (this.strain == Rules.HEARTS)   { strainStr = "H"; }
        else if (this.strain == Rules.SPADES)   { strainStr = "S"; }
        else                                    { strainStr = "NT"; }

        return rankStr + strainStr;
    }


}
