import React from 'react';
import { useNavigate } from 'react-router-dom';
import Cookies from 'js-cookie';
import SockJS from 'sockjs-client';
import {Client} from '@stomp/stompjs';

const socketURL = 'ws://monitoring.service.localhost/ws';

const WebSocketComponent = (username , onMessageReceived) => {
    const client = new Client({
        webSocketFactory: () => new SockJS(socketURL),
        debug: (str) => console.log(str), // Optional: Log WebSocket activity
        reconnectDelay: 5000, // Try reconnecting after 5 seconds if disconnected
    });

    client.onConnect = () => {
        console.log("Connected to WebSocket");
        // Subscribe to user-specific topic
        client.subscribe("/user/${username}/topic/notification", (message) => {
            onMessageReceived(JSON.parse(message.body));
            console.log(message);
        });
    };

    client.onDisconnect = () => {
        console.log("Disconnected from WebSocket");
    };

    return client;
};

export default WebSocketComponent;