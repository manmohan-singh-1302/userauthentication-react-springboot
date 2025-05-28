
import { BrowserRouter, Route, Routes} from 'react-router-dom'
import Login from './pages/Login'
import Register from './pages/Register'
import './App.css'
import Home from './pages/Home'
import ForgotPassword from './pages/Forgot-password'
import ValidateOtp from './pages/ValidateOtp'
import ResetPassword from './pages/Reset-password'


function App() {


  return (
    <BrowserRouter>
    <Routes>
      <Route path="/login" element = {<Login/>}/>
      <Route path="/register" element = {<Register/>}/>
      <Route path = "/home" element = {<Home/>}/>
      <Route path = "/forgot-password" element = {<ForgotPassword/>}/>
      <Route path='/validate-otp' element = {<ValidateOtp/>}/>
      <Route path='/reset-password' element = {<ResetPassword/>}/>
    </Routes>
    </BrowserRouter>
  )
}

export default App;
