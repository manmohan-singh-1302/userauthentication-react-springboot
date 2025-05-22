
import { BrowserRouter, Route, Routes} from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import './App.css'
import Home from './pages/Home'

function App() {


  return (
    <BrowserRouter>
    <Routes>
      <Route path="/login" element = {<Login/>}/>
      <Route path="/register" element = {<Register/>}/>
      <Route path = "/home" element = {<Home/>}/>
      <Route path = "/forgot-password" element = {<div>Forgot Password</div>}/>
    </Routes>
    </BrowserRouter>
    
  )
}

export default App
