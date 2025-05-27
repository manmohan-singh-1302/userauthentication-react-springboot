import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../services/authService";
import "../styles/Register.css"
function Register(){
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",
        password: "",
        confirmPassword: ""
    });
const [errors, setErrors] = useState({});
const [loading, setLoading] = useState(false);
const navigate = useNavigate();

// custom form validation function
const validateForm = () =>{
    const newErrors = {};

    // username validation.
    if(form.username.length < 3){
        newErrors.username = "Username must be at least 3 characteres long";
    }

    // email validation.
    const emailRegex = /^[^\s@]+@[^\s@]\.[^/s@]$/;
    if(emailRegex.test(form.email)){
        newErrors.email = "Please enter a valid email address";
    }

    // password validation.
    if(form.password.length < 8){
        newErrors.password = "Password must be at least 8 characters long";
    }
    if(!/[A-Z]/.test(form.password)){
        newErrors.password = "Password must contain at least one uppercase letter";
    }
    if(!/[a-z]/.test(form.password)){
        newErrors.password = "Password must contain at least one lowercase letter";
    }
    if(!/[0-9]/.test(form.password)){
        newErrors.password = "Password must contain at least one number";
    }
    if(!/[^A-Za-z0-9]/.test(form.password)){
        newErrors.password = "Password must contain at least one special character";
    }

    // confirm password validation
    if(form.password !== form.confirmPassword){
        newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
}
const handleChange = (e) => {
    const {name, value} = e.target;
    setForm(prev=>({
        ...prev, [name]:value
    }));

    // if there was any error for any field remove's it.
    if(errors[name]){
        setErrors(prev => ({
            ...prev,
            [name]:""
        }));
    }
};

const handleSubmit = async (e) => {
    e.preventDefault();

    if(!validateForm()){
        return;
    }

    setLoading(true);
    try{
        
        // remove confirmpassword before sending to API.
        const {confirmPassword, ...registrationData} = form;
        await register(registrationData);
        navigate("/login",{
            state:{message:"Registration successful! Please login."}
    })
    }catch (error){
        setErrors({
            submit: error.response?.data?.message || "Failed to register. Please try again"
        });   
    }finally{
        setLoading(false);
    }
};



    return(
        <div className="register-container">
        <h2>Register Page</h2>
        <form onSubmit={handleSubmit}>
            <div className="form-group">
                <label htmlFor="firstName">First Name:</label>
                <input id = "firstName" name="firstName" value={form.firstName} onChange={handleChange} required autoFocus/>
            </div>
            <div className="form-group">
                <label htmlFor="lastName">Last Name:</label>
                <input id = "lastName" type="text" name="lastName" value={form.lastName} onChange={handleChange} required/>
            </div>
            <div className="form-group">
                <label htmlFor="username"> Username:</label>
                <input id = "username" name="username" value={form.username} onChange={handleChange} required/>
                {errors.username && (<div className="error-message">{errors.username}</div>)}
            </div>
            <div className="form-group">
                <label htmlFor="email">Email:</label>
                <input id = "email" name="email" value={form.email} onChange={handleChange} required/> 
                {errors.email && (<div className = "error-message">{errors.email}</div>)}  
            </div>
            <div className="form-group">
                <label htmlFor="password">Password:</label>
                <input id = "password" name="password" type = "password" value={form.password} onChange={handleChange} required/>
                {errors.password && (<div className="error-message">{errors.password}</div>)}
            </div>
            <div className="form-group">
                <label htmlFor="confirmPassword">Confirm Password:</label>
                <input type="password" name="confirmPassword" id="confirmPassword" value = {form.confirmPassword} onChange={handleChange} required/>
                {errors.confirmPassword && (<div className="error-message">{errors.confirmPassword}</div>)}
            </div>

            {errors.submit && (<div className="error-message submit-error">{errors.submit}</div>)}
            <div>
                <button type = "submit" disabled = {loading} className="submit-button">{loading? 'Registering...': 'Register'}</button>
            </div>
        </form>
        </div>
    )
}

export default Register;