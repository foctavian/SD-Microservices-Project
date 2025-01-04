import useLogout from "../../utils/Utils";
import ViewDevices from "../admin/view-devices/ViewDevices";
import MeasurementGraph from '../measurement-graph/MeasurementGraph';
import Chat from "../chat/Chat";
import '../admin//Admin.css'
import { useState } from 'react';

const User = (props)=> {
    const id = props.id;
    const username = props.username;
    const role = props.role;
    const [selectedComponent, setSelectedComponent] = useState(null);
    const [visibleGraph, setVisibleGraph] = useState(false);
    const [visibleChat, setVisibleChat] = useState(false);

    const logout = useLogout();

    function renderComponent(){
        switch(selectedComponent){
            case 'viewDevices':
                return <ViewDevices id = {id}/>
            case 'measurementGraph':
                return <MeasurementGraph userId = {id}/>
            case 'chat':
                return <Chat id = {id} username = {username} role = {role}/>
            default:
                return <ViewDevices id = {id}/>
        }
    }


    return(
        <div className="main-container">
             <div className="header-container">
                <h2 className="admin-title">User Dashboard - {username}</h2>
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
            <div className='data-container'>
                {renderComponent()}
           </div>
        </div>
    );
};

export default User;