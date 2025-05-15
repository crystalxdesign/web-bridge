package com.crystalx.bridgeserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.crystalx.bridgeserver.model.Card;
import com.crystalx.bridgeserver.model.Game;
import com.crystalx.bridgeserver.model.Player;
import com.crystalx.bridgeserver.model.PlayerData;
import com.crystalx.bridgeserver.model.TableData;
import com.crystalx.bridgeserver.exceptions.MissingTableException;
import com.crystalx.bridgeserver.exceptions.MissingUserIdException;
import com.crystalx.bridgeserver.model.Bid;
import com.crystalx.bridgeserver.model.Double;
import com.crystalx.bridgeserver.model.Pass;
import com.crystalx.bridgeserver.model.BridgeTable;
import com.crystalx.bridgeserver.model.Call;

@Repository
public class BridgeServerDAO {
    //@Autowired
    //private JdbcTemplate jdbcTemplate;
    
	private static final Logger log = LoggerFactory.getLogger(BridgeServerDAO.class);
	private MqttHandler mqtt;
	
	private static String PlayerTopic = "player/%s";
	private static String TableTopic = "table/%s";
	
	private static String NewHand = PlayerTopic + "/hand";
	private static String NewHandMsg = "{\"msgType\":\"NewHand\",\"action\": %s}";
	private static String RequestPlay = PlayerTopic + "/invite/play";
	private static String RequestPlayMsg = "{\"msgType\":\"RequestPlay\",\"action\": \"play\"}";
	private static String RequestBid = PlayerTopic + "/invite/bid";
	private static String RequestBidMsg = "{\"msgType\":\"RequestBid\",\"action\": \"bid\"}";
	
	private static String TrickWinner = TableTopic + "/trickWinner";
	private static String TrickWinnerMsg = "{\"msgType\":\"TrickWinner\",\"action\": \"%s\"}";
	private static String NewScore = TableTopic + "/score";
	private static String NewScoreMsg = "{\"msgType\":\"NewScore\",\"action\": %s}";
	private static String NewContract = TableTopic + "/contract";
	private static String NewContractMsg = "{\"msgType\":\"NewContract\",\"action\": \"%s\"}";
	private static String NewCardLaid = TableTopic + "/seat/%s/card/%s";
	private static String NewCardLaidMsg = "{\"msgType\":\"NewCardLaid\",\"action\": {\"card\":\"%s\", \"seat\":\"%s\"}}";
	private static String NewPlayer = TableTopic + "/seat/%s/newplayer";
	private static String NewPlayerMsg = "{\"msgType\":\"NewPlayer\",\"action\": {\"name\": \"%s\", \"userId\": \"%s\", \"seat\": %s}}";
	private static String NewBidMade = TableTopic + "/seat/%s/bid";
	private static String NewBidMadeMsg = "{\"msgType\":\"NewBidMade\",\"action\": %s}";
	private static String NewGameStage = TableTopic + "/stage";
	private static String NewGameStageMsg = "{\"msgType\":\"NewGameStage\",\"action\": %s}";
	private static String NewDummy = TableTopic + "/dummy";
	private static String NewDummyMsg = "{\"msgType\":\"NewDummy\",\"action\": %s}";

	//private HashMap<String, Game> games;
	private HashMap<String, BridgeTable> bridgeTables;

	BridgeServerDAO() throws MqttException{
		//games = new HashMap<String, Game>();
		bridgeTables = new HashMap<String, BridgeTable>();
		mqtt = new MqttHandler();
		mqtt.connect("tcp://localhost:1883", "BridgeServer");
		loadTestData();
	}
	
	private Game newGame(String tableID) {
		Game game;
		List<Player> players;
		if(bridgeTables.containsKey(tableID)) {
			BridgeTable bt = bridgeTables.get(tableID);
			game = bt.getGame();
			players = bt.getPlayers();
			// will need to save score before moving to next game
			game.reset();
			for(int x=0;x<4;x++) {
				Player player = bt.getPlayers().get(x);
				player.setHand(game.getNewDeal());
				String topic = String.format(NewHand,player.getUserId());
				String msg = String.format(NewHandMsg,CardList2String(Arrays.asList(player.getHand())));
				mqtt.publish(topic, msg);
				log.info("newGame(): "+topic);
			}
		} else throw new MissingTableException();
        return game;
    }
	
	private MqttMessage getScores(BridgeTable bt) {
		MqttMessage retval = new MqttMessage();
		Game game = bt.getGame();
		retval.setTopic(String.format(NewScore,bt.getTableId()));
		retval.setMessage(String.format(NewScoreMsg,game.getScore()));
		log.info("getScores(): "+retval.getTopic());
		return retval;
    }

    public TableData createNewTable(PlayerData requestTable) {
    	
    	TableData retval = new TableData();
    	BridgeTable bt = new BridgeTable();
    	List<Player> players = new ArrayList<Player>();
    	Player p = new Player();
    	p.setName(requestTable.getName());
    	p.setUserId(requestTable.getUserId());
    	players.add(p);
    	for(int x=0;x<3;x++) {
    		p = new Player();
    		players.add(p);
    	}
    	bt.setPlayers(players);
    	String tableId = UUID.randomUUID().toString();
    	bt.setTableId(tableId);
    	bridgeTables.put(tableId, bt);

    	retval.setTableId(tableId);
    	retval.setPlayers(new ArrayList<PlayerData>());
    	PlayerData pd = new PlayerData();
    	pd.setName(requestTable.getName());
    	pd.setUserId(requestTable.getUserId());
    	pd.setSeat(0);
    	retval.getPlayers().add(pd);
    	return retval;
    }
    
    private String CardList2String(List<Card> cards) {
    	Collections.reverse(cards);
    	StringBuffer retval = new StringBuffer();
    	retval.append("[");
    	for(Card card:cards) {
    		retval.append("\"");
    		retval.append(card.rankStr());
    		retval.append(card.suitStr());
    		retval.append("\",");
    	}
    	if(retval.length()>1) {
    		retval.setCharAt(retval.length()-1, ']');
    	} else {
    		retval.append("]");
    	}
    	return retval.toString();
    }
    
    private String CallList2String(List<Call> calls, int padding) {
    	StringBuffer retval = new StringBuffer();
    	retval.append("[");
    	for(int x=0; x<padding; x++) {
    		retval.append("\"-\",");
    	}
    	for(Call call:calls) {
    		retval.append("\"");
    		if(call instanceof Bid) {
    			retval.append(((Bid)call).toString());
    		} else if(call instanceof Pass){
    			retval.append(((Pass)call).toString());
    		} else if(call instanceof Double){
    			retval.append(((Double)call).toString());
    		}
     		retval.append("\",");
    	}
    	if(retval.length()>1) {
    		retval.setCharAt(retval.length()-1, ']');
    	} else {
    		retval.append("]");
    	}
    	return retval.toString();
    }
    
	public void playCard(String tableID, String userID, String card){
		if(bridgeTables.containsKey(tableID)) {
			List<MqttMessage> queue = new ArrayList<MqttMessage>();
			MqttMessage msg;
			BridgeTable bt = bridgeTables.get(tableID);
			Game game = bt.getGame();
			int seat = bt.getSeat(userID);
			if(game.isGameLead()) {
				msg = new MqttMessage();
				msg.setTopic(String.format(NewDummy,bt.getTableId()));
				msg.setMessage(String.format(NewDummyMsg,CardList2String(Arrays.asList(bt.getPlayers().get(game.getDummy()).getHand()))));
				queue.add(msg);
				log.info("playCard(): "+msg.getTopic());
			}
			msg = new MqttMessage();
			msg.setTopic(String.format(NewCardLaid,bt.getTableId(),seat,card));
			msg.setMessage(String.format(NewCardLaidMsg,card,seat));
			queue.add(msg);
			log.info("playCard(): "+msg.getTopic());
			if(game.playCard(card, seat) < 0) {
				// trick not complete yet
				int nextseat = (seat + 1)%4;
				msg = new MqttMessage();
				msg.setTopic(String.format(RequestPlay,bt.getPlayers().get(nextseat).getUserId()));
				msg.setMessage(RequestPlayMsg);
				queue.add(msg);
				log.info("playCard(): "+msg.getTopic());
			} else {
				msg = new MqttMessage();
				msg.setTopic(String.format(TrickWinner,bt.getTableId()));
				msg.setMessage(String.format(TrickWinnerMsg,game.getCurrentLeader()));
				queue.add(msg);
				log.info("playCard(): "+msg.getTopic());
				if(game.isGameOver()) {
					//game complete
					queue.add(getScores(bt));
				} else {
					// get winner of last trick to lead the next
					msg = new MqttMessage();
					msg.setTopic(String.format(RequestPlay,bt.getPlayers().get(game.getCurrentLeader()).getUserId()));
					msg.setMessage(RequestPlayMsg);
					queue.add(msg);
					log.info("playCard(): "+msg.getTopic());
				}
			}
			PublishQueue pq = new PublishQueue(queue, 500L);
			pq.start();

		} else throw new MissingTableException(tableID);
	}
	
	public List<Call> makeBid(String tableID, String userID, String call){
		List<Call> retval;
		List<MqttMessage> queue = new ArrayList<MqttMessage>();
		if(bridgeTables.containsKey(tableID)) {
			BridgeTable bt = bridgeTables.get(tableID);
			Game game = bt.getGame();
			int seat = bt.getSeat(userID);
			game.makeCall(seat, call);
			retval = game.getCallHistory();
			MqttMessage msg = new MqttMessage();
			msg.setTopic(String.format(NewBidMade,bt.getTableId(),seat));
			msg.setMessage(String.format(NewBidMadeMsg,CallList2String(retval,game.getDealer())));
			queue.add(msg);
			log.info("makeBid(): "+msg.getTopic());
			if(game.auctionFinished()) {
				if(game.getContract() != null) {
					msg = new MqttMessage();
					msg.setTopic(String.format(NewContract,bt.getTableId()));
					msg.setMessage(String.format(NewContractMsg,game.getContract().toString()));
					queue.add(msg);
					log.info("makeBid(): "+msg.getTopic());
					msg = new MqttMessage();
					msg.setTopic(String.format(NewGameStage,bt.getTableId()));
					msg.setMessage(String.format(NewGameStageMsg,"3"));
					queue.add(msg);
					log.info("makeBid(): "+msg.getTopic());
					//request opener to lead
					msg = new MqttMessage();
					msg.setTopic(String.format(RequestPlay,bt.getPlayers().get(game.getOpener()).getUserId()));
					msg.setMessage(RequestPlayMsg);
					queue.add(msg);
					log.info("makeBid(): "+msg.getTopic());
				} else {
					//No contact was made so start a new game
					// previous opener will be new dealer
					//game.getOpener()
					msg = new MqttMessage();
					msg.setTopic(String.format(RequestBid,game.getDealer()));
					msg.setMessage(RequestBidMsg);
					queue.add(msg);
					log.info("makeBid(): "+msg.getTopic());
					game.reset();
				}
			} else {
				int currentBidder = bt.getSeat(userID);
				int nextBidder = (currentBidder+1)%4;
				String nextUserId = bt.getPlayers().get(nextBidder).getUserId();
				msg = new MqttMessage();
				msg.setTopic(String.format(RequestBid,nextUserId));
				msg.setMessage(RequestBidMsg);
				queue.add(msg);
				log.info("makeBid(): "+msg.getTopic());
			}
			PublishQueue pq = new PublishQueue(queue, 500L);
			pq.start();

		} else throw new MissingTableException(tableID);
		
		return retval;
	}
	
	public TableData joinTable(TableData table){
		List<MqttMessage> queue = new ArrayList<MqttMessage>();
		if(bridgeTables.containsKey(table.getTableId())) {
			BridgeTable bt = bridgeTables.get(table.getTableId());
			int seat = table.getPlayers().get(0).getSeat();
			Player p = bt.getPlayers().get(seat);
			p.setName(table.getPlayers().get(0).getName());
			p.setUserId(table.getPlayers().get(0).getUserId());
			
			MqttMessage msg = new MqttMessage();
			msg.setTopic(String.format(NewPlayer,table.getTableId(),seat));
			msg.setMessage(String.format(NewPlayerMsg,p.getName(), p.getUserId(), seat));
			queue.add(msg);
			log.info("joinTable(): "+msg.getTopic());
			if(bt.isFull()) {
				bt.setGame(new Game());
		    	MqttMessage mqttmsg;
				for(int x=0;x<4;x++) {
					Player player = bt.getPlayers().get(x);
					player.setHand(bt.getGame().getNewDeal());
					mqttmsg = new MqttMessage();
					mqttmsg.setTopic(String.format(NewHand,player.getUserId()));
					mqttmsg.setMessage(String.format(NewHandMsg,CardList2String(Arrays.asList(player.getHand()))));
					queue.add(mqttmsg);
					log.info("joinTable(): "+msg.getTopic());
				}
		    	//select starting bidder randomly
		    	int firstBidder = (int)(Math.random() * 4);
		    	bt.getGame().setDealer(firstBidder);
		    	String nextUserId = bt.getPlayers().get(firstBidder).getUserId();
				mqttmsg = new MqttMessage();
				mqttmsg.setTopic(String.format(NewGameStage,bt.getTableId()));
				mqttmsg.setMessage(String.format(NewGameStageMsg,"2"));
				queue.add(mqttmsg);
				log.info("joinTable(): "+msg.getTopic());
				msg = new MqttMessage();
				msg.setTopic(String.format(NewBidMade,bt.getTableId(),seat));
				StringBuffer sb = new StringBuffer();
				sb.append("[");
				for(int x=0; x<firstBidder; x++) sb.append("\"-\",");
		    	if(sb.length()>1) {
		    		sb.setCharAt(sb.length()-1, ']');
		    	} else {
		    		sb.append("]");
		    	}
				msg.setMessage(String.format(NewBidMadeMsg,sb.toString()));
				queue.add(msg);
				log.info("joinTable(): "+msg.getTopic());
				msg = new MqttMessage();
				msg.setTopic(String.format(RequestBid,nextUserId));
				msg.setMessage(RequestBidMsg);
				queue.add(msg);
				log.info("joinTable(): "+msg.getTopic());
			}
			PublishQueue pq = new PublishQueue(queue, 500L);
			pq.start();
		} else throw new MissingTableException();
		
		return table;
	}
	
	public List<TableData> getAvailableTables(){
		List<TableData> retval = new ArrayList<TableData>();
		bridgeTables.forEach((id, table)->{
			TableData td = new TableData();
			td.setPlayers(new ArrayList<PlayerData>());
			td.setTableId(table.getTableId());
			for(int x=0;x<4;x++) {
				if(table.getPlayers().get(x).getUserId() != null) {
					Player p = table.getPlayers().get(x);
					PlayerData pd = new PlayerData();
					pd.setName(p.getName());
					pd.setUserId(p.getUserId());
					pd.setSeat(x);
					td.getPlayers().add(pd);
				}
			}
			retval.add(td);
		});
    	return retval;
	}
	
	private void loadTestData(){
		BridgeTable bt = new BridgeTable();
		bt.setTableId("59c73dfc-d715-4566-a092-047322ea0c6d");
		bt.setPlayers(new ArrayList<Player>());
		for(int x=0;x<4;x++) bt.getPlayers().add(new Player());
		bt.getPlayers().get(0).setName("Rupert");
		bt.getPlayers().get(0).setUserId("d82e150f-1529-4182-a9b0-befb0c55784a");
		bt.getPlayers().get(1).setName("Michael");
		bt.getPlayers().get(1).setUserId("26bbbb06-fb3a-45a6-bd84-1343fc72c77a");
		bridgeTables.put(bt.getTableId(), bt);
		
		bt = new BridgeTable();
		bt.setTableId("77357cf9-df41-4e24-b765-ed8b69c3a906");
		bt.setPlayers(new ArrayList<Player>());
		for(int x=0;x<4;x++) bt.getPlayers().add(new Player());
		bt.getPlayers().get(0).setName("Roger");
		bt.getPlayers().get(0).setUserId("feb87509-f819-4ca9-ab2e-d5fa8706ea6f");
		bt.getPlayers().get(2).setName("Eustace");
		bt.getPlayers().get(2).setUserId("b4bf1bb8-b48c-48e2-bbaa-5d2147c140ff");
		bridgeTables.put(bt.getTableId(), bt);

		bt = new BridgeTable();
		bt.setTableId("de6138d5-414e-480f-b6ab-72d21545d598");
		bt.setPlayers(new ArrayList<Player>());
		for(int x=0;x<4;x++) bt.getPlayers().add(new Player());
		bt.getPlayers().get(0).setName("Graham");
		bt.getPlayers().get(0).setUserId("2d02fbf8-c7c2-4d3d-a358-69f975c8e06a");
		bt.getPlayers().get(2).setName("Benjamin");
		bt.getPlayers().get(2).setUserId("c54169ca-9de9-4441-b01b-9ff58644b475");
		bt.getPlayers().get(3).setName("Charles");
		bt.getPlayers().get(3).setUserId("9b953196-105f-4404-8a8c-5ba01dd4d7f7");
		bridgeTables.put(bt.getTableId(), bt);
	}
	
	public void resetTestData() {
		bridgeTables.clear();
		loadTestData();
	}
	
	private class PublishQueue extends Thread {
		private List<MqttMessage> queue;
		private long seconds;
		private TimeUnit time = TimeUnit.MILLISECONDS;
		
		private PublishQueue(List<MqttMessage> queue, long seconds) {
			this.queue = queue;
			this.seconds = seconds;
		}
		
	    public void run()
	    {
	    	try {
	    		for(MqttMessage msg:queue) {
	    			time.sleep(seconds);
					mqtt.publish(msg.getTopic(), msg.getMessage());
	    		}
	    	} catch (InterruptedException e) {}
	    }
	}
}
