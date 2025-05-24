import { useState, useEffect} from "react";
import { useNavigate, useLocation} from "react-router-dom";
import {login} from "../services/authService";
import "../styles/Login.css";

function Login(){
    const [form, setForm] = useState({
        username: "",
        password: "",
    })

    const [errors, setErrors] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const location = useLocation();
    useEffect(() => {
        if(location.state?.message){
            setSuccessMessage(location.state.message);
            window.history.replaceState({}, document.title);
        }
    },[location]);


    const handleChange = (e) =>{
        const {name, value} = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));

        if(errors){
            setErrors("");
        }
    };

    const handleSubmit = async (e) =>{
        e.preventDefault();
        setLoading(true);
        try{
            const response = await login(form);
            setSuccessMessage("Login successful");
            navigate("/home");
        }catch (error){
            setErrors(error.response?.data || "Failed to login");
        } finally {
            setLoading(false);
        }
    };
    
    return (
        <div className = "login-container">
            <h2>Login Page</h2>
            {successMessage && (<div className = "success-message">{successMessage}</div>)}
            <form onSubmit={handleSubmit}>
                <div className = "form-group">
                    <label htmlFor="username">Username:</label>
                    <input id="username" name="username" value={form.username} onChange={handleChange} required autoFocus/>
                </div>
                <div className = "form-group">
                    <label htmlFor="password">Password:</label>
                    <input id="password" type="password" name="password" value={form.password} onChange={handleChange} required/>
                </div>
                <div className = "form-group">
                    <button type="submit" className = "submit-button" disabled = {loading}>{loading? "Logging in...":"Login"}</button>
                </div>
                {errors && <div className = "error-message">{errors}</div>}
                <div className = "form-fotter">
                    <a href="/forgot-password">Forgot password?</a>
                    <p>Don't have an account? <a href="/register">Register here</a></p>
                </div>
            </form>
        </div>
    );
}

export default Login;