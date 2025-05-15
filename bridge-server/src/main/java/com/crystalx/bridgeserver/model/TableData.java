package com.crystalx.bridgeserver.model;
import java.util.List;

import lombok.Data;

@Data
public class TableData {
	String tableId;
	List<PlayerData> players;
}
