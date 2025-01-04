import React from 'react';
import { useState } from 'react';
import { createNewDevice, validateConsumption } from '../../../utils/Utils';
import { Button, message, Space } from 'antd';

const CreateDevice = () => {
    
    const [description, setDescription] = useState("");
    const [address, setAddress] = useState("");
    const [maxcon, setMaxcon] = useState("");

    const [messageApi, contextHolder] = message.useMessage();


    async function handleCreateDevice(){
        if(validateConsumption(maxcon) === false){
            messageApi.open({
                type: 'error',
                content: 'Max consumption must be greater than 0',
              });
            return;
        }
        const data = {
            'description':description,
            'address':address,
            'max_consumption': parseFloat(maxcon)
        };
        const response = await createNewDevice(data);
        if(response === 201){
            //TODO manage notifications accordingly 
            console.log("Device created successfully");
        }
        else{
            console.log("Device could not be created");
        }
    };

    return (
        <div className='form-container'>
            <div className='input-container'>
            <input 
                    type="text"
                    autoComplete="off"
                    placeholder="Description"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    >
                </input>
                </div>
                <div className='input-container'>

                <input 
                    type="text"
                    autoComplete="off"
                    placeholder="Address"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    >
                </input>
                </div>
                <div className='input-container'>

                <input 
                    type="number"
                    autoComplete="off"
                    placeholder="Max Consumption"
                    value={maxcon}
                    onChange={(e) => setMaxcon(e.target.value)}
                    >
                </input>
            </div>
                <br/>
                <button onClick={()=>{
                    handleCreateDevice();
                }}>Create Device</button>
            </div>
    );
};

export default CreateDevice;