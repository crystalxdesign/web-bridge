import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { IMqttServiceOptions, MqttModule } from "ngx-mqtt";
import { environment as env } from '../environments/environment.development';

import { EventMqttService} from './services/event.mqtt.service';
import { EventWebService} from './services/event.web.service';
import { MqttListenerComponent } from './mqtt-listener/mqtt-listener.component';
import { BridgeTableComponent } from './bridge-table/bridge-table.component';
import { PlayingCardComponent } from './playing-card/playing-card.component';
import { HttpClientModule } from '@angular/common/http';
import { ChooseSeatComponent } from './choose-table/choose-table.component';
import { BiddingComponent } from './bidding/bidding.component';
import { FormsModule } from "@angular/forms";

const MQTT_SERVICE_OPTIONS: IMqttServiceOptions = {
    hostname: env.mqtt.server,
    port: env.mqtt.port,
    protocol: (env.mqtt.protocol === "wss") ? "wss" : "ws",
    path: '',
};

@NgModule({
  declarations: [
    AppComponent,
    MqttListenerComponent,
    BridgeTableComponent,
    PlayingCardComponent,
    ChooseSeatComponent,
    BiddingComponent
  ],
  imports: [
    BrowserModule,
    MqttModule.forRoot(MQTT_SERVICE_OPTIONS),
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [EventMqttService, EventWebService],
  bootstrap: [AppComponent]
})
export class AppModule { }

