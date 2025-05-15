import { Injectable } from '@angular/core';
import { IMqttMessage, MqttService } from "ngx-mqtt";
import { Observable } from "rxjs";

@Injectable()
export class EventMqttService {

  private endpointPlayer: string;
  private endpointTable: string;

  constructor(
    private _mqttService: MqttService,
  ) {
    this.endpointPlayer = 'player';
    this.endpointTable = 'table';
  }

  player(userId: string): Observable<IMqttMessage> {
    let topicName = `${this.endpointPlayer}/${userId}/#`;
    console.log("Subscribing to "+topicName);
    return this._mqttService.observe(topicName);
  }

  table(tableId: string): Observable<IMqttMessage> {
    let topicName = `${this.endpointTable}/${tableId}/#`;
    console.log("Subscribing to "+topicName);
    return this._mqttService.observe(topicName);
  }
}