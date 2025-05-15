package com.crystalx.bridgeserver;

import lombok.Data;

@Data
public class MqttMessage {
	String message;
	String topic;
}
