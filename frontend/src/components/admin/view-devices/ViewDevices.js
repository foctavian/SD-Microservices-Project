import { useEffect } from "react";
import { Spin } from "antd";
import { useState } from "react";
import { Space, Table, Tag , Button, Dropdown, Menu} from 'antd';
import { DeleteOutlined, DownOutlined } from '@ant-design/icons';
import { getDevicesForUser } from "../../../utils/Utils";
const ViewDevices = (props) =>{
    const [loadedData, setLoadedData] = useState(false);
    const [devices, setDevices] = useState([])
    const id = props.id;
    useEffect(()=>{
        async function fetchUserDevices(){
            try {
                const res = await getDevicesForUser(id);
                setDevices(res)
                setLoadedData(true);
            } catch (error) {
                console.error("Failed to fetch user devices:", error);
            }
        }
        fetchUserDevices();
        },[]);
       

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
            title: 'User ID',
            key: 'userId',
            width: 400,
            render: (text, record) => (
                record.user ? record.user.id : 'N/A' 
            ),
        }
    ];

    return(<div>{loadedData ? <Table 
        columns={columns} 
        dataSource={devices} 
        rowKey="id" 
        pagination={false}
    /> : <Spin size="large"/>}</div>); 
};

export default ViewDevices;