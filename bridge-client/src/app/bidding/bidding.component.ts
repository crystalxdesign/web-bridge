import { Component, Output, Input, EventEmitter, SimpleChanges, OnChanges } from '@angular/core';

@Component({
  selector: 'bidding',
  templateUrl: './bidding.component.html',
  styleUrls: ['./bidding.component.css']
})
export class BiddingComponent implements OnChanges {
  @Output() notifyBid = new EventEmitter<any>();
  @Output() notifyParent = new EventEmitter<any>();
  @Input() bidhistory:any[];
  @Input() bidAllowed: boolean;
  bidlevel:number = -1;
  bidlevels:string[] = this.getBidLevels();
  isDoubled:boolean = false;
  isReDoubled:boolean = false;
  isPassed:boolean = false;
  message: string = "";

  ngOnChanges(changes: SimpleChanges): void {
    //only subscribe to user messages when first initiated
    if (changes['bidhistory']?.isFirstChange()) return;
    for(var x=0; x<this.bidlevels.length; x++){
      if(this.bidlevels[x] == this.bidhistory[this.bidhistory.length-1]) {
        this.bidlevel = x;
        break;
      }
    }
  }

  isAuthorised(){
    if(this.bidAllowed == false) {
      this.notifyParent.emit({command:'message', action:'Not your turn to bid yet.'});
    }
    return this.bidAllowed;
  }

  double(): void {
    if(this.isAuthorised() == false) return;
    this.isDoubled = true;
    this.notifyBid.emit('X');
  }

  redouble(): void {
    if(this.isAuthorised() == false) return;
    this.isReDoubled = true;
    this.notifyBid.emit('XX');
  }

  pass(): void {
    if(this.isAuthorised() == false) return;
    this.isPassed = true;
    this.notifyBid.emit('Pass');
  }

  setBidLevel(level: number): void {
    if(this.isAuthorised() == false) return;
    if(this.bidlevel < level) {
      this.bidlevel = level;
      this.notifyBid.emit(this.bidlevels[this.bidlevel]);
    }
  }

  getBidLevels(): string[] {
    let level = ["1","2","3","4","5","6","7"];
    let trump = ["C","D","H","S","NT"];
    let retval = [];
    for(var x=0; x<level.length;x++){
      for(var y=0; y<trump.length;y++){
        retval.push(level[x]+trump[y]);
      }
    }
    return retval;
  }
}
