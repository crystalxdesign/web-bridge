import { Component, Input, Output, OnChanges, OnInit, EventEmitter} from '@angular/core';

@Component({
  selector: 'playing-card',
  templateUrl: "./playing-card.component.html",
  styleUrls: ['./playing-card.component.css']
})

export class PlayingCardComponent implements OnInit, OnChanges {
  @Input() cardType: string | undefined;
  @Input() view: string | undefined;
  @Input() playAllowed: boolean | undefined;
  @Output() notifyPlay = new EventEmitter<any>();

  trigger(): void {
    if(this.playAllowed && this.cardType != "null") {
      console.log("Card clicked: "+this.cardType);
      this.notifyPlay.emit(this.cardType);
      this.cardType = "null";
      this.view = ".played"
    }
  }

  ngOnChanges(): void {
    
  }

  ngOnInit(): void {

  }
}


