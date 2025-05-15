import { Component, OnChanges, OnInit } from '@angular/core';
import { EventWebService } from '/home/nigel/Desktop/bridge-client/src/app/services/event.web.service';
//import { EventWebService } from './src/app/services/event.web.service';
import { CardModel, TableModel, PlayerModel, Rank, Seat, Suit, myIdKey } from '/home/nigel/Desktop/bridge-client/src/app/datamodules';  

@Component({
  selector: 'bridge-table',
  templateUrl: './bridge-table.component.html',
  styleUrls: ['./bridge-table.component.css']
})

export class BridgeTableComponent implements OnInit, OnChanges {
  webservice:EventWebService;
  seat = Seat;
  rank = Rank;
  suit = Suit;
  deck:any[];
  dummyHand:any[];
  myHand:any[];
  gameStage: number;
  activeTables: any[];
  bidhistory: any[];
  myName:string;
  myId:string;
  myTableId:string;
  mytable: PlayerModel[] = [];
  mySeat:number;
  message: string = "";
  contract:any;
  bidAllowed: boolean = false;
  playAllowed: boolean = false;
  playedCards: string[] = ["null","null","null","null"];

  constructor(webservice: EventWebService) {
    this.webservice = webservice;
  }

  uuidv4(): any {
    return "10000000-1000-4000-8000-100000000000".replace(/[018]/g, c =>
        (parseInt(c) ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> parseInt(c) / 4).toString(16)
    );
  }

  setMessage(msg: string): void{
    this.message = msg;
    setTimeout(()=>{this.message = ""}, 3000);
  }

  mqttPlayerMsg(event): void{
    console.log("mqttPlayerMsg(): "+JSON.stringify(event));
    switch(event.msgType){
      case "NewHand": this.myHand = event.action; break;
      case "RequestPlay": this.playAllowed = true; break;
      case "RequestBid": this.bidAllowed = true; break;
      default: console.log("WARNING: \""+event.msgType +"\" unrecognised MQTT message type");
    }
  }

  mqttTableMsg(event): void{
    console.log("mqttTableMsg(): "+JSON.stringify(event));
    switch(event.msgType){
      case "NewCardLaid": this.newCardLaid(event.action.card, event.action.seat); break;
      case "NewPlayer": this.mytable.push(event.action); this.getActiveTables(); break;
      case "NewBidMade": this.bidhistory = event.action; break;
      case "NewGameStage": this.nextGameStage(); break;
      case "NewDummy": this.dummyHand = this.adapt4dummy(event.action); break;
      case "NewContract": this.contract = event.action; console.log(this.contract); break;
      default: console.log("WARNING: \""+event.msgType +"\" unrecognised MQTT message type");
    }
  }

  newCardLaid(card:string, seat:number){
    this.playedCards[seat] = card;
  }

  getCard(seat: number){
    return this.playedCards[seat];
  }

  processChildEvent(event): void{
    console.log("processChildEvent()"+event);
    switch(event.command){
      case "message": this.setMessage(event.action); break;
      case "createNewTable":this.myName=event.action;this.createNewTable();break;
    }
  }

  processBidEvent(event): void{
    if(this.bidAllowed == false) {
      console.log("WARNING: Not authorised to bid");
      return;
    }
    this.bidAllowed = false;
    console.log("processBidEvent()"+event);
    this.webservice.makeBid(event, this.myTableId, this.myId).subscribe((resp) => {
      console.log("processBidEvent response: "+JSON.stringify(resp.body));
    });
  }

  processPlayEvent(event): void{
    if(this.playAllowed == false) {
      this.setMessage("Not your turn to play yet.")
      return;
    }
    this.playAllowed = false;
    console.log("processPlayEvent()"+event);
    this.webservice.playCard(event, this.myTableId, this.myId).subscribe((resp) => {
      console.log("processPlayEvent response: "+JSON.stringify(resp.body));
    });
  }

  getActiveTables(): void{
    this.webservice.getActiveTables().subscribe((resp) => {
      console.log("getActiveTables response: "+JSON.stringify(resp.body));
      this.activeTables = resp.body;
    });
  }

  joinTable(data:TableModel): void{
    this.myTableId = data.tableId;
    this.webservice.joinTable(data).subscribe((resp) => {
      console.log("joinTable response: "+JSON.stringify(resp.body));
      //this.myTableId = resp.body.tableId;
      this.mySeat = resp.body.players[0].seat;
      this.myName = resp.body.players[0].name;
      this.myId = resp.body.players[0].userId;
      this.getActiveTables();
    });
  }

  nextGameStage(){
    if(this.gameStage==3) this.gameStage=2
    else this.gameStage++;
  }

  adapt4dummy(hand: string[]){
    let clubs=[];
    let diamonds=[];
    let hearts=[];
    let spades=[];
    let longesthand=0;
    for(var x=0;x<hand.length;x++){
      switch(hand[x].charAt(1)){
        case 'c':
          clubs.push(hand[x]);
          if(clubs.length > longesthand)
            longesthand = clubs.length;
          break;
        case 'd':
          diamonds.push(hand[x]);
          if(diamonds.length > longesthand)
            longesthand = diamonds.length;
          break;
        case 'h':
          hearts.push(hand[x]);
          if(hearts.length > longesthand)
            longesthand = hearts.length;
          break;
        case 's':
          spades.push(hand[x]);
          if(spades.length > longesthand)
            longesthand = spades.length;
          break;
      }
    }
    let retval = [];
    clubs.reverse();
    diamonds.reverse();
    hearts.reverse();
    spades.reverse();
    clubs = this.pad(longesthand, clubs);
    diamonds = this.pad(longesthand, diamonds);
    hearts = this.pad(longesthand, hearts);
    spades = this.pad(longesthand, spades);
    for (var x=0;x<longesthand;x++){
      retval.push(clubs[x]);
      retval.push(diamonds[x]);
      retval.push(hearts[x]);
      retval.push(spades[x]);
    }
    return retval;
  }

  pad(longest, suit){
    if(suit.length < longest){
      for(var x=longest-suit.length;x>0;x--){
        suit.push("null");
      }
    }
    return suit;
  }

  createNewTable() {
    console.log("createNewTable called");
    this.mySeat = 0;
    this.webservice.createTable({name:this.myName, userId:this.myId, seat:this.mySeat}).subscribe((resp) => {
      console.log("createNewTable response: "+JSON.stringify(resp.body));
      this.getActiveTables();
      this.myTableId = resp.body.tableId;
    });
  }

  ngOnChanges(): void {
    
  }

  ngOnInit(): void {
    this.gameStage = 1;
    if(localStorage.getItem(myIdKey)){
      this.myId = localStorage.getItem(myIdKey);
    } else {
      this.myId = this.uuidv4();
      localStorage.setItem(myIdKey, this.myId) ;
    }
  }
}
