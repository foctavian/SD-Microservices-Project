import React, { useEffect } from 'react';
import './Admin.css';
import useLogout from '../../utils/Utils';
import { useState } from 'react';
import CreateUser from './create-user/CreateUser';
import UpdateUser from './update-user/UpdateUser';
import CreateDevice from './create-device/CreateDevice';
import ViewDevices from './view-devices/ViewDevices';
import AllocateDevices from './allocate-devices/AllocateDevices';
import MeasurementGraph from '../measurement-graph/MeasurementGraph'
import Chat from '../chat/Chat';
const Admin = (props) =>{

    const username = props.username;
    const id = props.id;
    const role = props.role;
    const logout = useLogout();
    const [selectedComponent, setSelectedComponent] = useState(null);
    const [visibleGraph, setVisibleGraph] = useState(false);
    const [visibleChat, setVisibleChat] = useState(false);
    const [chats, setChats] = useState([]);
    function renderComponent(){
        switch(selectedComponent){
            case 'newUser':
                return <CreateUser />;
            case 'updateUser':
                return <UpdateUser/>
            case 'newDevice':
                return <CreateDevice/>
            case 'viewDevices':
                return <ViewDevices id = {id}/>
            case 'allocateDevice':
                return <AllocateDevices/>
            case 'chat':
                return <Chat id = {id} username = {username} role = {role} setChats={setChats}/>
            case 'measurementGraph':
                return <MeasurementGraph userId = {id}/>
            default:
                return <ViewDevices id = {id}/>
        }
    }
   

    return(
        <div className="main-container">

            <div className="header-container">
                <h2 className="admin-title">Admin Dashboard - {username}</h2>
                <div>

                    { visibleGraph ? 
                        <button className='graph-button' onClick={()=>{
                            setSelectedComponent('viewDevices');
                            setVisibleGraph(!visibleGraph);
                        }}>Go back</button>
                        :
                        <button className='graph-button' onClick={()=>{
                            setSelectedComponent('measurementGraph');
                            setVisibleGraph(!visibleGraph);
                        }
                    }>Show consumption graphs</button>
                    }
                    
                        {
                            visibleChat ? 
                            <button className='chat-button' onClick={()=>{
                                setSelectedComponent('viewDevices');
                                setVisibleChat(!visibleChat);
                                
                            }}>Back</button>
                            :
                            <button className='chat-button' onClick={()=>{
                                setSelectedComponent('chat');
                                setVisibleChat(!visibleChat);

                            }}>Chats</button>
                        }
                    
                <button className="logout-button" onClick={logout}>Logout</button>
                </div>
            </div>
            <br/>
            <div className='sidepanel'>
                <div className="bubble">+</div> 
                <div className="admin-container">
                    <h3>User Settings</h3>

                    <button 
                    className="admin-button"
                    onClick={()=>{setSelectedComponent('newUser')}}
                    >Create a new user</button>
                    <button 
                    className="admin-button"
                    onClick={()=>{setSelectedComponent('updateUser')}}
                    >Update an existing user</button>

                    <h3>Device Settings</h3>

                    <button className="admin-button" onClick={()=>setSelectedComponent('newDevice')}>Create a new device</button>
                    <button className='admin-button' onClick={()=>setSelectedComponent('allocateDevice')}>Allocate a device</button>
                    <h3>Dashboard</h3>
                    <button className='admin-button' onClick={()=>setSelectedComponent('viewDevices')}>View Devices</button>
                </div>
           </div>
           <div className='data-container'>
                {renderComponent()}
           </div>
           
        </div>
    );
};
export default Admin;