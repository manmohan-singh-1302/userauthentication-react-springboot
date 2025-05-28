import {useState } from "react";
import {useNavigate } from "react-router-dom";
import { forgotPassword } from "../services/authService";
import "../styles/ForgotPassword.css";
function ForgotPassword(){

    const [registeredEmail, setRegisteredEmail] = useState("");
    const [errors, seterrors] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [loading, setLoading] = useState(false);

    const navigate = useNavigate();

    const handleChange = (e) =>{
        setRegisteredEmail(e.target.value);
        seterrors("");
    };

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        if(!registeredEmail){
            seterrors("Please enter your registered email");
            setLoading(false);
            return;
        }

        if(!validateEmail(registeredEmail)){
            seterrors("Please enter a valid email address");
            setLoading(false);
            return;
        }

        try {
            await forgotPassword(registeredEmail);
            setSuccessMessage("OTP has been sent to your registered email");
            navigate("/validate-otp", {state: {email: registeredEmail}});
        } catch(error) {
            seterrors(error.response?.data?.message || 'Failed to send OTP');
        } finally {
            setLoading(false);
        }
    }

    return(
        <div className="forgot-passwrod-container">
            <h2>Reset Password</h2>
            <p className="description">Please enter your email address to receive a reset code</p>
            {
                successMessage && (
                    <div className="success-message">{successMessage}</div>
                )
            }
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="registeredEmail">Enter Registerd Email:</label>
                    <input id="registeredEmail" name="registeredEmail" value={registeredEmail} autoFocus onChange={handleChange} required/>
                </div>
                {errors && (<div className="error-message">{errors}</div>)}
                <div>
                    <button type = "submit" className="submit-loading" disabled = {loading}>{loading?"Sending...":"Send Reset Code"}</button>
                </div>
                <div className="form-footer">
                    <p>Remember your password? <a href="/login">Login here</a></p>
                </div>
            </form>
        </div>
    )
}

export default ForgotPassword;