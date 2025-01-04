import './App.css';
import Login from './components/login/Login';
import Home from './components/home/Home';
import useLogout from './utils/Utils';
import { BrowserRouter, Route, Routes } from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path = '/login' element = {<Login/>}/>
        <Route path = '/' element = {<Home/>} />
        <Route path = '/logout' element={useLogout}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
