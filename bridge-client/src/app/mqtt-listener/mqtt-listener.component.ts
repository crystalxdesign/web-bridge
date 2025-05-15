import { Component, OnChanges, Input, Output, SimpleChanges, EventEmitter } from '@angular/core';
import { Subscription } from 'rxjs';
import { EventMqttService } from '../services/event.mqtt.service';
import { IMqttMessage } from "ngx-mqtt";

@Component({
    selector: 'mqtt-listener',
    template: '',
    styleUrls: ['./mqtt-listener.component.css'],
})
export class MqttListenerComponent implements OnChanges {
    @Input() tableId: string | undefined;
    @Input() userId: string | undefined;
    @Output() mqttTableMsg = new EventEmitter<any>();
    @Output() mqttPlayerMsg = new EventEmitter<any>();
    //events: any[];
    subscription: Subscription;

    constructor(
        private readonly eventMqtt: EventMqttService,
    ) {
    }
  
    ngOnChanges(changes: SimpleChanges): void {
        //only subscribe to user messages when first initiated
        if (typeof changes['userId'] !== 'undefined') {
            this.subscribeToPlayer();
            //this.subscribeToOtherPlayers();
        }
        if (typeof changes['tableId'] !== 'undefined') {
            if(changes['tableId'].firstChange != true) this.subscribeToTable();
        }
    }
  
    ngOnDestroy(): void {
        this.unSubscribeToMqtt();
    }

    private unSubscribeToMqtt(): void {
        if (this.subscription) {
            this.subscription.unsubscribe();
            console.log("UNSUBSCRIBED FROM MQTT MESSAGES");
        }
    }

    private subscribeToPlayer(): void {
        this.subscription = this.eventMqtt.player(this.userId)
        .subscribe((data: IMqttMessage) => {
            var msg = new TextDecoder().decode(data.payload);
            let item = JSON.parse(msg);
            this.mqttPlayerMsg.emit(item);
        });
    }

    private subscribeToTable(): void {
        this.subscription = this.eventMqtt.table(this.tableId)
        .subscribe((data: IMqttMessage) => {
            var msg = new TextDecoder().decode(data.payload);
            let item = JSON.parse(msg);
            this.mqttTableMsg.emit(item);
        });
    }

    private subscribeToOtherPlayers(): void {
        let ids = ["2d02fbf8-c7c2-4d3d-a358-69f975c8e06a","c54169ca-9de9-4441-b01b-9ff58644b475","9b953196-105f-4404-8a8c-5ba01dd4d7f7"];
        for(var x=0; x<ids.length; x++){
            this.subscription = this.eventMqtt.player(ids[x])
            .subscribe((data: IMqttMessage) => {
                var msg = new TextDecoder().decode(data.payload);
                let item = JSON.parse(msg);
                this.mqttPlayerMsg.emit(item);
            });
        }
    }
}