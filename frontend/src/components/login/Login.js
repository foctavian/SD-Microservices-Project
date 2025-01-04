import { useState } from "react";
import "./Login.css";
import axios from "axios";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const sendLoginRequest = async () => {
    try {
      const data = {
        username,
        password,
      };
      const response = await axios.post(
        "http://user.service.localhost/auth/login",
        data,
        {
          headers: {
            "Content-Type": "application/json", // Make sure to send JSON
          },
        },
      );

      if (response.status === 200) {
        const { id,_, role, token, expirationTime } = await response.data;
        Cookies.set("token", token, { expires: expirationTime, path: "/" });
        console.log("Login successful");

        //send props to home component

        navigate("/", { state: { id,username , role} });
      } else {
        console.log("Login failed");
      }
    } catch (err) {
      console.error("Error while logging in", err);
    }
  };

  return (
    <div className="form-container">
      <h2>Log In</h2>

      <div className="input-container">
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
      </div>

      <div className="input-container">
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>

      <br />
      <button className="login-button" onClick={sendLoginRequest}>
        Log In
      </button>
    </div>
  );
};

export default Login;
