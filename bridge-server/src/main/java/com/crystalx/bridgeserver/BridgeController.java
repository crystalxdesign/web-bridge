package com.crystalx.bridgeserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.crystalx.bridgeserver.model.Call;
import com.crystalx.bridgeserver.model.Card;
import com.crystalx.bridgeserver.model.Game;
import com.crystalx.bridgeserver.model.PlayerData;
import com.crystalx.bridgeserver.model.TableData;

@RestController
@RequestMapping("/bridge/v1")
@CrossOrigin(origins = "http://localhost:4200")
public class BridgeController {

	private static final Logger log = LoggerFactory.getLogger(BridgeController.class);
	
    @Autowired
	BridgeServerDAO bridgeDAO;

	@Autowired
	MqttGateway mqttGateway;
	
	@PostMapping("/sendMqttMessage")
	public ResponseEntity<?> publish(@RequestBody MqttMessage mqttMessage){
		
		try {
			mqttGateway.sendToMqtt(mqttMessage.getMessage(), mqttMessage.getTopic());
			return ResponseEntity.ok("Success");
		} catch(Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.ok("fail");
		}
	}
	
    /**
     * Make a bid.
     */
 	@GetMapping("/makeBid/userid/{userid}/tableid/{tableid}/bid/{bid}")
	public ResponseEntity<?> makeBid(@PathVariable("tableid") String tableid, @PathVariable("userid") String userid, @PathVariable("bid") String bid) {
   		bridgeDAO.makeBid(tableid, userid, bid);
   		return ResponseEntity.ok("Success");
	}

    /**
     * Lay a card.
     */
 	@GetMapping("/playCard/userid/{userid}/tableid/{tableid}/card/{card}")
	public ResponseEntity<?> playCard(@PathVariable("tableid") String tableid, @PathVariable("userid") String userid, @PathVariable("card") String card) {
   		bridgeDAO.playCard(tableid, userid, card);
   		return ResponseEntity.ok("Success");
	}

    /**
     * Get a list of Pre-existing Tables.
     */
 	@GetMapping("/getAvailableTables")
	public @ResponseBody Iterable<TableData> getAvailableTables() {
   		return bridgeDAO.getAvailableTables();
	}
 	
    /**
     * Create a new BridgeTable
     */
 	@PostMapping("/createTable")
	public @ResponseBody TableData createTable(@RequestBody PlayerData body) {
    	return bridgeDAO.createNewTable(body);
	}
 	
    /**
     * Join an existing BridgeTable
     */
 	@PutMapping("/joinTable")
	public @ResponseBody TableData joinTable(@RequestBody TableData body) {
    	return bridgeDAO.joinTable(body);
	}
 	
    /**
     * Create new game with tableid parameter
     */
 	@PutMapping("/newgame/{tableid}/status")
	public @ResponseBody Game newGame(@PathVariable("tableid") String tableid, 
			@RequestBody Game oldGame) {
 		return new Game();
	}
 	
    /**
     * remove after testing.
     */
 	@GetMapping("/reset")
	public ResponseEntity<?> reset() {
 		bridgeDAO.resetTestData();
 		return ResponseEntity.ok("Success");
	}
}