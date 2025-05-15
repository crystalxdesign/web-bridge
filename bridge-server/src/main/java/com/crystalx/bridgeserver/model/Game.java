package com.crystalx.bridgeserver.model;

import java.util.List;
import java.util.ArrayList;

public class Game {
    private Deck deck;

    /**
     * The four people playing the game.
     */
    //private Player[] players;

    /**
     * The winners of previous tricks. If if the trick hasn't been played
     * yet, the number should be -1.
     */
    private int[] results;

    /**
     * The contract of the bridge game, as determined by the auction.
     */
    private Contract contract;
    
    private int gameState;
    private boolean gamelead = true;
    private Card[] cardsInTrick = new Card[4];
    private int nCardsInTrick = 0;
    private int trickCount = 0;
    private int leader = -1;
    
    private List<Integer> scores = new ArrayList<Integer>();
    private List<Call> calls = new ArrayList<Call>();
    private Bid lastBid = null;
    private int dealer = Rules.NORTH;
    private int dblLevel = Rules.UNDOUBLED;
    private int side = 0;

    /**
     * Create a bridge game. The game has a standard 52-card deck dealt to 4
     * players so that each has a hand of 13 cards.
     */
    public Game() {
    	this.gameState = Rules.BIDDING;
        this.deck = new Deck();
        this.deck.shuffle();
        this.results = new int[13]; // 13 tricks
        for (int i = 0; i < 13; i++) {
            this.results[i] = -1; // Fill the array with -1
        }
    }

    public Card[] getNewDeal() {
    	return this.deck.deal(13);
    }
    
    /**
     * Play a trick, retrieving a card from each player.
     *
     * @param leader the first player to go, one of Rules.NORTH, Rules.EAST,
     *              Rules.SOUTH, and Rules.WEST
     * @return the player that won
     */
    public int playCard(String card, int player) {
    	int retval = -1;
    	this.gamelead = false;
    	this.cardsInTrick[nCardsInTrick++] = new Card(card);
    	if(nCardsInTrick == 4){
    		this.leader = this.winner();
    		nCardsInTrick = 0;
    		retval = this.leader ;
    	}
    	return retval;
    }
    
    /**
     * Determine the winner.
     * The highest card wins. To compare to cards:
     * - If both cards follow suit with the leader, the higher rank wins.
     * - Otherwise the higher trump wins.
     *
     * Because the initial highest card is the lead, the highest card will
     * always follow suit or be trump.
     *
     */
    public int winner() {
        int winner = 0;

        for (int i = 0; i < 4; i++) {
            if (cardsInTrick[i].suit() == cardsInTrick[winner].suit()) {
                if (cardsInTrick[i].rank() > cardsInTrick[winner].rank()) { winner = i; }
            }
            else if (cardsInTrick[i].suit() == this.contract.strain()) { winner = i; }
        }
        setResult(winner);
        return winner;
    }

    /**
     * Save the result of a trick.
     *
     * @param trickNum the zero-indexed number of the trick (i. e. the first
     *                 trick is trick 0)
     * @param r the winner of the trick
     */
    public void setResult(int r) { this.results[trickCount++] = r % 2; }

    /**
     * Get the results of the game.
     *
     * @return the winners of each trick
     */
    public int[] getResults() { return this.results; }

    /**
     * Determine the contract.
     *
     * @param dealer the first person to bid
     */
    public void makeCall(int seat, String call) {
        Call entered = readCall(call);
        if (entered instanceof Bid && (lastBid == null || ((Bid) entered).compareTo(lastBid) > 0)) { // Bids
            calls.add(entered);
            lastBid = (Bid) entered;
            side = seat % 2;
        }
        else if (entered instanceof Double && lastBid != null &&
                 (((Double) entered).level() == Rules.DOUBLE && dblLevel == Rules.UNDOUBLED ||
                 ((Double) entered).level() == Rules.REDOUBLE && dblLevel == Rules.DOUBLE)) { // Doubles
            dblLevel = ((Double) entered).level();
            calls.add(entered);
        }
        else if (entered instanceof Pass) { // Pass
            calls.add(entered);
        }
        
        if (lastBid != null && auctionFinished()) {
            int declarer = Rules.declarer(calls, side, lastBid.strain(), seat);
            this.contract = new Contract(lastBid, dblLevel, declarer);
        }
        else {
            this.contract = null;
        }
    }

    /**
     * Read a valid call, displaying help when asked.
     *
     * @param prompt a string to display when asking for input
     * @return a valid (but not necessarily legal) Call object
     */
    public Call readCall(String call) {
        Call out;
        if (Call.isBid(call)) {
            out = new Bid(call);
        }
        else if (Call.isDouble(call)) {
            out = new Double(call);
        }
        else if (Call.isPass(call)) {
            out = new Pass();
        } else {
            out = null;
        }

        return out;
    }

    /**
     * Determine if the auction if over.
     * The auction is finished if the last three bids are passes.
     *
     * @param calls the history of the auction
     * @return whether or not the auction is done
     */
    public boolean auctionFinished() {
    	if(calls.size()<3) return false;
    	else return (
    		calls.get(calls.size()-1) instanceof Pass &&
    		calls.get(calls.size()-2) instanceof Pass &&
    		calls.get(calls.size()-3) instanceof Pass
        );
    }

    /**
     * Get the contract for this hand.
     *
     * @return the contract
     */
    public Contract getContract() { return this.contract; }
    
    private void setScore() {
    	if (this.getContract() != null) {
            if (this.getContract().declarer() % 2 == 0) {
                scores.add(this.getContract().score(this.getResults()));
            }
            else {
                scores.add(-this.getContract().score(this.getResults()));
            }
        }
        else {
            scores.add(0);
        }
    }
    
    public List<Integer> getScore() {
        return scores;
    }
    
    public boolean isGameLead() {
        return this.gamelead;
    }
    
    public int getDummy() {
        return (this.contract.declarer() + 2) % 4;
    }
    
    public int getOpener() {
        return (this.contract.declarer() + 1) % 4;
    }
    
    public int getCurrentLeader() {
        return this.leader;
    }
    
    public int getDeclarer() {
        return this.contract.declarer();
    }
    
    public void setDealer(int fb){
    	this.dealer = fb;
    }
    
    public int getDealer(){
    	return this.dealer;
    }
    
    public List<Call> getCallHistory() {
        return this.calls;
    }
    
    public boolean isGameOver() {
        return this.trickCount == 14;
    }
    
    public void reset() {
    	this.gameState = Rules.BIDDING;
        this.deck = new Deck();
        this.deck.shuffle();
        this.calls.clear();
        this.lastBid = null;
        this.gamelead = true;
        this.dblLevel = Rules.UNDOUBLED;
        this.results = new int[13]; // 13 tricks
        for (int i = 0; i < 13; i++) {
            this.results[i] = -1; // Fill the array with -1
        }
    }
}
