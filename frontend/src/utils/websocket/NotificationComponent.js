// NotificationComponent.js
import React, { useState, useEffect } from "react";
import WebSocketComponent from "./WebSocketComponent";
import { notification } from "antd";
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";


const NotificationComponent = (props) => {
    const username = props.username;
    const id = props.id;

    useEffect(() => {
        if (username) {

            const wsurl = new SockJS('http://monitoring.service.localhost/ws');
            const client = Stomp.over(wsurl);
           
            wsurl.onopen = ()=>{
                console.log('Connected to WebSocket');
            }

            client.connect({}, ()=>{
                console.log('stomp connected');

                client.subscribe('topic/notification', (message) => {
                    console.log("RECEIVED");
                    const notificationMessage = JSON.parse(message.body);
                    console.log(notificationMessage);
                    notification.open({
                        message: notificationMessage.title,
                        description: notificationMessage.message,
                    });
                });
            });
        }

    }, []); // Reconnect when username changes

    return null; // No visible UI needed for this component
};

export default NotificationComponent;
