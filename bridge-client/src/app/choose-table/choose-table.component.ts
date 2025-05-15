import { Component, OnChanges, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { TableModel, Seat, myIdKey, myTableKey, mySeatKey} from '/home/nigel/Desktop/bridge-client/src/app/datamodules';

@Component({
  selector: 'choose-table',
  templateUrl: './choose-table.component.html',
  styleUrls: ['./choose-table.component.css']
})
export class ChooseSeatComponent implements OnInit, OnChanges {
  @Input() availableTables:TableModel[] | undefined;
  @Output() notifyParent = new EventEmitter<any>();
  @Output() getFreeTables = new EventEmitter<any>();
  @Output() notifyJoinTable = new EventEmitter<TableModel>();
  username: string = "";

  joinTable(table: number, seat: number): void {
    if(this.username == ""){
      this.notifyParent.emit({command:'message', action:'Please set the name field before joining a table.'})
      return;
    }
    localStorage.setItem(myTableKey, this.availableTables[table].tableId);
    localStorage.setItem(mySeatKey, Seat[seat]);
    let joinObj:TableModel = {
      tableId:this.availableTables[table].tableId,
      players: [{	name:this.username,
        userId: localStorage.getItem(myIdKey),
        seat: seat
      }]
    }
    this.notifyJoinTable.emit(joinObj);
  }

  getPlayerName(table: number, seat: number): string{
    if(typeof this.availableTables[table].players[seat] == undefined) return '';
    return this.availableTables[table].players[seat].name;
  }

  createNewTable(): void {
    if(this.username == ""){
      this.notifyParent.emit({command:'message', action:'Please set the name field before creating a new table.'})
      return;
    }
    this.notifyParent.emit({command:'createNewTable', action:this.username});
  }

  ngOnChanges(): void {
    
  }

  ngOnInit(): void {
    this.getFreeTables.emit();
  }
}