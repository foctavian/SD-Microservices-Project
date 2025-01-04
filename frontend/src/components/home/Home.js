import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect , useRef} from "react";
import Cookies from "js-cookie";
import axios from "axios";
import { navigate } from "react-router-dom";
import Admin from "../admin/Admin";
import NotificationComponent from "../../utils/websocket/NotificationComponent";
import { Spin } from "antd";
import User from "../user/User";
import { verifyToken } from "../../utils/Utils";
import { notification } from "antd";
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";
const Home = () => {
  const [isSocketConnected, setIsSocketConnected] = useState(false);
  const [warnings, setWarnings] = useState([]);
  const stompClientRef = useRef(null); 
  const location = useLocation();
  const navigate = useNavigate();
  const username = location.state?.username;
  const role = location.state?.role;
  const id = location.state?.id;

  const [admin, setAdmin] = useState(false);

  useEffect(() => {

    async function verify(){
      try{
        const res = await verifyToken();
        if(!res){
          navigate('/login');
        } 
      }catch(error){
        console.error(error);
      }
    }
    function verifyRole(){
      if(role === 'ROLE_ADMIN'){
        setAdmin(true);
      }
    }      
    verifyRole();
    verify();
  }, []);



  useEffect(() => {
    const socket = new SockJS("http://monitoring.service.localhost/ws");
    const client = Stomp.over(socket);

    client.connect(
      {},
      () => {
        setIsSocketConnected(true);
        console.log("Connected to WebSocket");
        client.subscribe(`/topic/warnings/${id}/`, (message) => {
            alert(`Warning: ${message.body}`);
            warnings.push(extractDeviceIdFromWarning(message.body));
        });
      },
      (error) => {
        console.error("WebSocket connection failed", error);
        setIsSocketConnected(false);
      }
    );

    stompClientRef.current = client; 

    return () => {
      if (stompClientRef.current && stompClientRef.current.connected) {
        stompClientRef.current.disconnect(() => {
          console.log("Disconnected from WebSocket");
        });
      }
    };
  }, []);

  const extractDeviceIdFromWarning = (message) => {
    const match = message.match(/device ID: (\d+)/);
    if (match) {
      return parseInt(match[1], 10);
    }
    return null;
  };



  return (
    <div>
      {admin ? <Admin id = {id} username={username} role = {role}/> : <User  id = {id} username={username} role = {role} />}
    </div>
  );
};

export default Home;
