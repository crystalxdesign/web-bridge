package com.crystalx.bridgeserver.model;

import java.util.List;
import lombok.Data;

@Data
public class BridgeTable {
	String tableId;
	List<Player> players;
	Game game;
	public boolean isFull() {
		return (!players.get(0).getName().isEmpty() &&
				!players.get(1).getName().isEmpty() &&
				!players.get(2).getName().isEmpty() &&
				!players.get(3).getName().isEmpty());
	}
	public int getSeat(String id) {
		int retval = -1;
		for(int x=0;x<4;x++) {
			if(players.get(x).getUserId().equalsIgnoreCase(id))  {
				retval = x;
			}
		}
		return retval;
	}
}
