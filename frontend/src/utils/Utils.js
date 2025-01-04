import { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Cookies from "js-cookie";

const useLogout = () => {
  const navigate = useNavigate();

  const logout = useCallback(() => {
    Cookies.remove("token");
    navigate("/login");
  }, [navigate]);

  return logout;
};

export function createNewUser(data) {
  const response = axios.post(`http://user.service.localhost/api/user`, data, {
    headers: {
      Authorization: `Bearer ${Cookies.get("token")}`,
    },
  });
  return response.status;
}

export function createNewDevice(data) {
  const response = axios.post(`http://device.service.localhost/api/device`, data, {
    headers: {
      Authorization: `Bearer ${Cookies.get("token")}`,
    },
  });
  return response.status;
}

export function validateConsumption(c) {
  return c > 0;
}

export async function getDevicesForUser(id) {
  if (id == null) {
      return; 
  }
  try {
      const response = await axios.get(`http://device.service.localhost/api/device/get-devices/${id}`);
      return response.data; 
  } catch (error) {
      console.error("Error fetching devices:", error);
      throw error; 
}
}

export async function getAllDevices(){
  try{
    const response = await axios.get(`http://device.service.localhost/api/device`);
    return response.data;
  }catch(error){
    console.error("Error fetching devices:", error);
      throw error; 
  }
}

export async function getAllUsers(){
  try{
    const response = await axios.get(`http://user.service.localhost/api/user`,{ headers: {
      Authorization: `Bearer ${Cookies.get("token")}`
    },});
    return response.data;
  }catch(error){
    console.error("Error fetching users:", error);
      throw error; 
  }
}

export async function deleteDevice(id){
  if(id == null) return;

  try{
    const response = await axios.delete(`http://device.service.localhost/api/device/${id}`, { headers: {
      Authorization: `Bearer ${Cookies.get("token")}`
    },});
    return response.status;
  }
  catch(err){
    console.error("Error deleting device:", err);
    throw err;
  }
}

export async function allocateDevice(device, user){
  const data = {
    'userId':user,
    'deviceId':device
  }
  try{
    const response = await axios.post(`http://device.service.localhost/api/device/allocate-devices`, data, { headers: {
      Authorization: `Bearer ${Cookies.get("token")}`
    }
  });
  return response.status;
}
  catch(err){
    console.error("Error allocating device:", err);
    throw err;
  }
}

export async function verifyToken(){
  if(Cookies.get('token') === undefined){
    return false;
  }
  else{
    const token = Cookies.get('token');
    const verify = await axios.post('http://user.service.localhost/auth/verify', {'token':token}
    );

    if(verify.status === 200){
      return true;
    }
    else{
      return false;
    }
  }
}

export async function deleteUser(id){
  if(id == null) return;

  try{
    const res = await axios.delete(`http://user.service.localhost/api/user/${id}`, { headers: {
      Authorization: `Bearer ${Cookies.get("token")}`
    },});
    return res.status;
  }
  catch(err){
    console.error("Error deleting user:", err);
    throw err;
  }
}


export async function getConsumptionData(userId){
  if(userId == null) return;

  try{
    const res = await axios.get(`http://monitoring.service.localhost/api/monitoring/${userId}`,
      { headers: {
        Authorization: `Bearer ${Cookies.get("token")}`
      },});
      return res.data;
    }
    catch(err){
      console.error("Error fetching consumption data", err);
      throw err;
    }
}

export default useLogout;
