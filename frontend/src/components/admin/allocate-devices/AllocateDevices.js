import { useEffect, useState } from "react";
import {Spin, Space,Select, Table, Button, Dropdown, Menu} from 'antd';
import { getAllDevices, getAllUsers,deleteDevice,allocateDevice } from "../../../utils/Utils";
import { DeleteOutlined, ToolOutlined,DownOutlined } from '@ant-design/icons';
import { Option } from "antd/es/mentions";
import './AllocateDevices.css';
import { Experimental_CssVarsProvider } from "@mui/material";
const AllocateDevices = () =>{
    const [loadedData, setLoadedData] = useState(false);
    const [devices, setDevices] = useState([]);
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    useEffect(()=>{
        async function fetchAllDevices(){
            try{
                const res = await getAllDevices();
                setDevices(res);
                setLoadedData(true);

            }catch(error){
                console.error(error);
            }
        }

        async function fetchAllUsers() {
            try{
                const res = await getAllUsers();
                setUsers(res.userIds);
                setLoadedData(true);

                console.log(res.userIds);

            }catch(error){
                console.error(error);
            }
        }
        fetchAllDevices();
        fetchAllUsers();
    }, []);

    const handleDelete = async (id) => {
        try{
            const res = await deleteDevice(id);
            if(res === 200){
                setDevices(devices.filter(device => device.id !== id));
            }
            else{
                alert("Failed to delete device");
            }
        }
        catch(error){
            console.error("Failed to delete device:", error);
        }
    };

    const handleUserChange =async (id, value) =>{
        if(id == null || value == null){
            return;
        }
        try{
            const res = await allocateDevice(id, value);
            if(res === 200){
                alert("Device allocated successfully");
            }
            else{
                alert("Failed to allocate device");
            }
        }catch(error){
            console.error("Failed to allocate device:", error);
        }
    };

    const columns = [
        {
            title: 'ID',
            dataIndex: 'id',
            key: 'id',
        },
        {
            title: 'Description',
            dataIndex: 'description',
            key: 'description',
        },
        {
            title: 'Address',
            dataIndex: 'address',
            key: 'address',
        },
        {
            title: 'Consumption',
            dataIndex: 'max_consumption',
            key: 'consumption'
        },
        {
            title: 'Allocated User ID',
            key: 'userId',
            className:'allocated-user',
            width: 500,
            render: (text, record) => (
                <Select
                    defaultValue={record.user ? record.user.id : undefined}
                    onChange={(value) => {setSelectedUser(value);}}
                    style={{ width: 300,
                        }}
                >
                   {users.map(userId => (
                    <Option key={userId} value={userId}>
                        {userId} 
                    </Option>
                ))}
                </Select>
            ),
        },
        {
            title: 'Action',
            key: 'action',
            className:'action',
            render: (text, record) => (
                    <Space size="middle">
                        <Button
                            type="primary"
                            icon={<DeleteOutlined />}
                            onClick={() => handleDelete(record.id)}
                        >
                            Delete
                        </Button>

                        <Button type = "primary" icon={<ToolOutlined/>} onClick={()=>handleUserChange(record.id, selectedUser)}>
                            Update
                        </Button>
                        </Space>
                
            ),
            
        },
    ];


    return (
        <div className="table-container">
            {loadedData ? <Table className = "device-table" columns = {columns} dataSource={devices} pagination={false}/> : <Spin size = "large"/>}
        </div>
    )
};

export default AllocateDevices;