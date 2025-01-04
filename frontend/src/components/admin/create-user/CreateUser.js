import Password from "antd/es/input/Password";
import { useState } from "react";
import './CreateUser.css'
import { createNewUser } from "../../../utils/Utils";
import { Select } from "antd";
import { Button, message, Space } from 'antd';

const CreateUser = () =>{

    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("");

    const [messageApi, contextHolder] = message.useMessage();
    
    async function handleCreateUser(){
        const data = {
            'username':username,
            'password':password,
            'role':role
        };
        try{
        const response = await createNewUser(data);
        if(response === 200){
            console.log("da")
        }
        else{
           console.log("nu")
        }
    }catch(err){
        
    }

    }

    return (
        <div className="form-container">
            <h3>Create a new user</h3>

            <div className="input-container">
                <input 
                    type="text"
                    autoComplete="off"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    >
                </input>
            </div>

            <div className="input-container">
                <input 
                    type="password"
                    autoComplete="off"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    >
                </input>
            </div>

            <Select popupMatchSelectWidth={100}
                    options={[{ value: 'ADMIN', label: <p>Admin</p> },
                            { value: 'USER', label: <p>User</p> }]} onChange={(e)=>{
                                setRole(e)
                            }}/>
            
            <br/>
            <br/>
            <button onClick={()=>{
                handleCreateUser();
            }}>Create user</button>
        </div>
    );
};

export default CreateUser;