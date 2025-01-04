import React, { useState, useEffect, useRef } from 'react';
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { List, Typography, Tag, Card, Row, Col, Input, Button , Spin} from 'antd';
import './Chat.css';

const { Text } = Typography;

const Chat = ({ id, role, username, chats,setChats }) => {
  const [selectedConversation, setSelectedConversation] = useState('');
  const [currentChatMessages, setCurrentChatMessages] = useState([]);
  const [currentMessage, setCurrentMessage] = useState('');
  const [uniqueUsers, setUniqueUsers] = useState([]);
  const [messages, setMessages] = useState([]);
  const stompClientRef = useRef(null);
  const [typingStatus, setTypingStatus] = useState([]); 

  const handleChange = (e) => {
    setCurrentMessage(e.target.value);
    if (e.target.value !== '') {
      startTyping();
    } else {
      stopTyping();
    }
  };

  const handleKeyPress = (e) => {
    if (!e.shiftKey && e.key === "Enter" && currentMessage !== '') {
      sendMessage(currentMessage);
      setCurrentMessage('');
    }
    stopTyping();

  };

  const startTyping = () => {
    if (role==='ROLE_ADMIN') {
      stompClientRef.current.send("/app/typing", {}, JSON.stringify({ sender: id, typing: true, receiver: selectedConversation }));
    }
    else if (role === 'ROLE_USER'){
      stompClientRef.current.send("/app/typing", {}, JSON.stringify({ sender: id, typing: true, receiver: 'unassigned' }));
    }
  };

  const stopTyping = () => {
    if (role==='ROLE_ADMIN') {
      stompClientRef.current.send("/app/typing", {}, JSON.stringify({ sender: id, typing: false, receiver: selectedConversation }));
    }
    else if (role === 'ROLE_USER'){
      stompClientRef.current.send("/app/typing", {}, JSON.stringify({ sender: id, typing: false, receiver: 'unassigned' }));
    }
  };

  // Deduplicate messages
  const deduplicateMessages = (msgArray) => {
    return Array.from(
      new Map(msgArray.map((msg) => [`${msg.timestamp}-${msg.senderId}`, msg])).values()
    );
  };

  // Admin's UI
  let adminContent = (
    <div className='chat-container'>
      <div className='message-list'>
        <List
          dataSource={deduplicateMessages(messages)}
          renderItem={(msg, index) => (
            <div key={index} className={`message-item ${msg.senderId === id ? "send" : "received"}`}>
              <Text>{msg.message}</Text>
            </div>
          )}
        />
        {typingStatus.typing&&(
        <div className="typing-indicator">
          <Spin size="small" /> <Text type="secondary">Typing...</Text>
        </div>
      )}
      </div>
      <Input
        value={currentMessage}
        size="large"
        onChange={handleChange}
        onKeyDown={handleKeyPress}
        rows={2}
        placeholder="Type a message and press Enter..."
      />
    </div>
  );

  if (role === 'ROLE_ADMIN' && selectedConversation === '') {
    const filteredUsers = uniqueUsers.filter(user => user.admin === '' || user.admin === id);
    adminContent = (
      <div className='user-chat-selection-container'>
        <Card title="Active Users" style={{ marginBottom: '20px', width: '600px' }}>
          <List
            itemLayout="horizontal"
            dataSource={filteredUsers}
            renderItem={user => (
              <List.Item
                style={{ backgroundColor: user.open ? '#f6ffed' : '#fff', marginBottom: '10px', padding: '10px', borderRadius: '8px' }}
                onClick={() => setSelectedConversation(user.sender)}
              >
                <Row style={{ width: '100%' }}>
                  <Col span={18}>
                    <Text strong>{user.sender}</Text>
                    <br />
                    <Text type="secondary">{user.message}</Text>
                  </Col>
                  <Col span={6} style={{ textAlign: 'right' }}>
                    <Tag color={user.open ? 'green' : 'volcano'}>{user.open ? 'Assigned' : 'Unassigned'}</Tag>
                  </Col>
                  <Col span={6} style={{ textAlign: 'right' }}>
                    <Button>Close Chat</Button>
                  </Col>
                </Row>
              </List.Item>
            )}
          />
        </Card>
      </div>
    );
  }

  // Establish WebSocket connection
  useEffect(() => {
    const socket = new SockJS("http://chat.service.localhost/chatws");
    const client = Stomp.over(socket);

    client.connect({}, () => {
      stompClientRef.current = client;

      if (role === 'ROLE_ADMIN') {
        client.subscribe(`/topic/admins`, (msg) => {
          const message = JSON.parse(msg.body);
          setUniqueUsers(prevUsers => {
            if (!prevUsers.some(user => user.sender === message.senderId)) {
              return [...prevUsers, { sender: message.senderId, message: message.message, open: false, admin: '' }];
            }
            return prevUsers;
          });
        });
      } else {
        client.subscribe(`/topic/user/${id}`, (msg) => {
          const message = JSON.parse(msg.body);
          setMessages((prev) => deduplicateMessages([...prev, message]));
        });

        client.subscribe(`/topic/user/${id}/notifications`, (msg) => {
          const notification = JSON.parse(msg.body);
          alert(notification.message);
        });
      }

      client.subscribe(`/topic/typing`, (msg) => {
        const typingInfo = JSON.parse(msg.body);
        // Check if typing info is for this user or not
        if(typingInfo.receiver === id){
          console.log(typingInfo);
          setTypingStatus(typingInfo);
        }
        else if(typingInfo.sender === selectedConversation){
          setTypingStatus(typingInfo);
        }
    });
    });

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.disconnect();
      }
    };
  }, [id, role]);

  // Admin selects a conversation
  useEffect(() => {
    let subscription;
    const connectionMessage = {
      user: selectedConversation,
      admin: id,
    };

    if (role === 'ROLE_ADMIN' && selectedConversation !== '') {
      stompClientRef.current.send("/app/admin/connect", {}, JSON.stringify(connectionMessage));

      subscription = stompClientRef.current.subscribe(
        `/topic/user/${selectedConversation}`,
        (msg) => {
          const message = JSON.parse(msg.body);
          setMessages((prev) => deduplicateMessages([...prev, message]));
        }
      );

      // Assign admin to user in the list
      setUniqueUsers(prevUsers =>
        prevUsers.map(user => user.sender === selectedConversation ? { ...user, admin: id } : user)
      );
    }

    return () => {
      if (subscription) {
        stompClientRef.current.send("/app/admin/disconnect", {}, JSON.stringify(connectionMessage));
        subscription.unsubscribe();
      }
    };
  }, [selectedConversation, role, id]);

  // Send message function
  const sendMessage = (msg) => {
    const chatMessage = {
      message: msg,
      senderId: id,
      receiver: role === 'ROLE_ADMIN' ? selectedConversation : '',
      timestamp: new Date().toISOString(),
    };

    const destination = role === 'ROLE_ADMIN' ? "/app/admin/chat" : "/app/user/chat";
    stompClientRef.current.send(destination, {}, JSON.stringify(chatMessage));
    //setMessages((prev) => deduplicateMessages([...prev, chatMessage]));
  };

  return <>{adminContent}</>;
};

export default Chat;
