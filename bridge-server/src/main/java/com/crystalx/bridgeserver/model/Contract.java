package com.crystalx.bridgeserver.model;

public class Contract {
    /**
     * The member of the partnership who introduced the strain of the contract.
     */
    private final int DECLARER;

    /**
     * A doubled or redoubled contract will result in higher scores.
     */
    private final int DOUBLED;

    /**
     * The players need to take 6 more than the rank of the contract. If the
     * rank is 0, everybody passed and both sides score 0.
     */
    private final int RANK;

    /**
     * The trump suit of the contract.
     */
    private final int STRAIN;

    /**
     * Create the default contract (1NT by North).
     */
    public Contract() {
        this(1, Rules.NOTRUMP, 0, Rules.NORTH);
    }

    /**
     * Create a contract from four ints.
     *
     * @param rank 0-7, inclusive
     * @param strain {@code Rules.CLUBS}, {@code Rules.DIAMONDS},
     *               {@code Rules.HEARTS}, {@code Rules.SPADES}, or
     *               {@code Rules.NOTRUMP}
     * @param doubled 0 if undoubled, 1 if doubled, or 2 if redoubled
     * @param declarer {@code Rules.NORTH}, {@code Rules.EAST},
     *                 {@code Rules.SOUTH}, or {@code Rules.WEST}
     */
    public Contract(int rank, int strain, int doubled, int declarer) {
        this.DECLARER = declarer;
        this.DOUBLED = doubled;
        this.RANK = rank;
        this.STRAIN = strain;
    }

    /**
     * Create a contract from the highest bid.
     *
     * @param b the highest bid in the auction
     * @param doubled 0 if undoubled, 1 if doubled, or 2 if redoubled
     * @param declarer {@code Rules.NORTH}, {@code Rules.EAST},
     *                 {@code Rules.SOUTH}, or {@code Rules.WEST}
     */
    public Contract(Bid b, int doubled, int declarer) {
        this(b.rank(), b.strain(), doubled, declarer);
    }

    /**
     * Get the rank of the contract.
     *
     * @return 0-7, inclusive
     */
    public int rank() { return this.RANK; }

    /**
     * Get the strain of the contract.
     *
     * @return {@code Rules.CLUBS}, {@code Rules.DIAMONDS},
     *         {@code Rules.HEARTS}, {@code Rules.SPADES}, or
     *         {@code Rules.NOTRUMP}
     */
    public int strain() { return this.STRAIN; }

    /**
     * Get whether the contract was doubled.
     *
     * @return 0 if undoubled, 1 if doubled, or 2 if redoubled
     */
    public int doubled() { return this.DOUBLED; }

    /**
     * Get the player who introduced the strain of the contract.
     *
     * @return {@code Rules.NORTH}, {@code Rules.EAST},
     *         {@code Rules.SOUTH}, or {@code Rules.WEST}
     */
    public int declarer() { return this.DECLARER; }

    /**
     * Find the score for the contract.
     *
     * The score is the sum of contract, overtrick, and bonus points minus
     * undertrick points.
     *
     * Contract points:
     * - NT: 40 points for the first trick, 30 points/trick after
     * - Major suits: 30 points/trick
     * - Minor suits: 20 points/trick
     * - X and XX double and quadruple the score
     *
     * Overtrick points:
     * - NT/H/S: 30 points/overtrick
     * - C/D: 20 points/overtrick
     * - X: 100 points/overtrick
     * - XX: 200 points/overtrick
     *
     * Undertrick points:
     * +-----------------------+-----------+---------+-----------+
     * | Number of undertricks | Undoubled | Doubled | Redoubled |
     * |-----------------------+-----------+---------+-----------|
     * |          1st          |           |   100   |    200    |
     * |-----------------------|           |---------+-----------|
     * |      2nd and 3rd      |    50     |   200   |    400    |
     * |-----------------------|           |---------+-----------|
     * |          4th+         |           |   300   |    600    |
     * |-----------------------+-----------+---------+-----------|
     *
     * @param results the winners of each trick
     * @return declarer's score (defender's score is the negative of the score)
     */
    public int score(int[] results) {
        int contractPoints = 0, overPoints = 0, underPoints = 0, bonusPoints = 0;
        int need = 6 + this.RANK;
        int made = 0;
        for (int r : results) {
            if (r == this.DECLARER % 2) { made++; }
        }

        if (this.RANK == 0) { return 0; } // Nobody scores if everybody passed

        if (made >= need) {
            if (this.STRAIN == Rules.NOTRUMP) {
                contractPoints = 10 + 30 * this.RANK;
            }
            else if (this.STRAIN == Rules.HEARTS || this.STRAIN == Rules.SPADES) {
                contractPoints = 30 * this.RANK;
            }
            else if (this.STRAIN == Rules.CLUBS || this.STRAIN == Rules.DIAMONDS) {
                contractPoints = 20 * this.RANK;
            }

            if (this.DOUBLED == Rules.DOUBLE)        { contractPoints *= 2; }
            else if (this.DOUBLED == Rules.REDOUBLE) { contractPoints *= 4; }
        }

        if (made > need) {
            int overtricks = made - need;
            if (this.DOUBLED == 1) {
                overPoints = 100 * overtricks;
            }
            else if (this.DOUBLED == 2) {
                overPoints = 200 * overtricks;
            }
            else if (this.STRAIN == Rules.HEARTS || this.STRAIN == Rules.SPADES || this.STRAIN == Rules.NOTRUMP) {
                overPoints = 30 * overtricks;
            }
            else {
                overPoints = 20 * overtricks;
            }
        }

        if (made < need) {
            int undertricks = need - made;
            if (this.DOUBLED == 0) {
                underPoints = 50 * undertricks;
            }
            else {
                for (int i = 0; i < undertricks; i++) {
                    if (i == 0) {
                        underPoints += 100 * this.DOUBLED * 2;
                    }
                    else if (i == 1 || i == 2) {
                        underPoints += 200 * this.DOUBLED * 2;
                    }
                    else {
                        underPoints += 300 * this.DOUBLED * 2;
                    }
                }
            }
        }

        /*
         * Bonus points:
         * - Slam bonus:
         *  - For a made rank-6 contract, 500 points are awarded.
         *  - For a made rank-7 contract, 1000 points are awarded.
         * - Game bonus: for a made contract worth 100 or more points, 300
         *   points are awarded.
         * - Doubled/redoubled bonus:
         *  - If a made contract was doubled, 50 points are awarded.
         *  - If a made contract was redoubled, 100 points are awarded.
         */
        if (made >= need) {
            if (need == 6) {
                bonusPoints += 500;
            }
            else if (need == 7) {
                bonusPoints += 1000;
            }

            if (contractPoints >= 100) {
                bonusPoints += 300;
            }

            if (made - need >= 0) {
                if (this.DOUBLED == 1) {
                    bonusPoints += 50;
                }
                else if (this.DOUBLED == 2) {
                    bonusPoints += 100;
                }
            }
        }

        return contractPoints + overPoints - underPoints + bonusPoints;
    }

    public String toString() {
    	StringBuffer sb = new StringBuffer();
        sb.append(this.RANK);
        // Strain
        if (this.STRAIN == Rules.CLUBS)         { sb.append("C"); }
        else if (this.STRAIN == Rules.DIAMONDS) { sb.append("D"); }
        else if (this.STRAIN == Rules.HEARTS)   { sb.append("H"); }
        else if (this.STRAIN == Rules.SPADES)   { sb.append("S"); }
        else if (this.STRAIN == Rules.NOTRUMP)  { sb.append("NT"); }

        // Doubles
        if (this.DOUBLED == Rules.DOUBLE)        { sb.append(" DOUBLED"); }
        else if (this.DOUBLED == Rules.REDOUBLE) { sb.append(" REDOUBLED"); }

        sb.append(", DECLARER: ");

        // Declarer
        if (this.DECLARER == Rules.NORTH)      { sb.append("North"); }
        else if (this.DECLARER == Rules.EAST)  { sb.append("East"); }
        else if (this.DECLARER == Rules.SOUTH) { sb.append("South"); }
        else if (this.DECLARER == Rules.WEST)  { sb.append("West"); }

        return sb.toString();
    }
}
