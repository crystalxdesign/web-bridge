package com.crystalx.bridgeserver.model;

import lombok.Data;

@Data
public class MqttMessage {
	String topic;
	String msg;
}
