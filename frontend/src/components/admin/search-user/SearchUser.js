import React from 'react';
import { useState } from 'react';
import { searchUser } from '../../../utils/Utils';

const SearchUser = () => {
    const [username, setUsername] = useState('');
    const [role, setRole] = useState('');

    function handleSearch(){
        const data = searchUser(username);
        if(data != null){
            setRole(data.role);
            setUsername(data.username); //may be obsolete
        }
        else{
            alert('User not found');
        }
    }

    return (
        <div className="form-container">
            <h3>Search for a user</h3>
            <div className="input-container">
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
            </div>
            <br />
            <button onClick={()=>{handleSearch}}>Search</button>
        </div>

    );
};

export default SearchUser;