import React, { useEffect } from 'react';
import { useState } from 'react';
import { getAllUsers } from '../../../utils/Utils';
import {Spin, Space,Select,Table, Button, Dropdown, Menu} from 'antd';
import { Option } from "antd/es/mentions";
import { DeleteOutlined, ToolOutlined,DownOutlined } from '@ant-design/icons';
import { deleteUser } from '../../../utils/Utils';
const UpdateUser = ()=>{
    const [id, setId] = useState('');
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState(null);
    useEffect(()=>{
        async function fetchUsers(){
            try{
                const res = await getAllUsers();
                setUsers(res.userIds);
            }catch(error){
                console.error(error);
            }
        }
        fetchUsers();
    },[]);

    const handleDeleteUser = async(id)=>{
        try{
            const res = await deleteUser(id);
            if(res === 204){
                setUsers(users.filter(user => user.id !== id));
            }
        }catch(error){
            console.error("Failed to delete user:", error);
        }
    }

    return (
        <div className='form-container'>
            <h3>Delete existing user</h3>
            <Space direction="vertical" size="middle" align='center'>
            <Select
                    defaultValue={undefined}
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
                <br/>
                <Button
                            type="primary"
                            icon={<DeleteOutlined />}
                            onClick={()=>{handleDeleteUser(selectedUser)}}
                        >
                            Delete
                        </Button>
            </Space>
            
        </div>
    );
};

export default UpdateUser;