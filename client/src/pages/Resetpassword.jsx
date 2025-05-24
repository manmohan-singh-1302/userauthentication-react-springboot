import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { resetPassword } from "../services/authService";
import "../styles/ResetPassword.css";



function ResetPassword(){

    const [searchParams] = useSearchParams();
    const email = searchParams.get("email");

    const[form, setForm] = useState({
        newPassword:"",
        confirmPassword:""
    });

    const[errors, setErrors] = useState("");
    const[loading, setLoading] = useState(false);
    const[successMessage, setSuccessMessage] = useState("");

    const navigate = useNavigate();

    const validateForm = () =>{
        if(!form.newPassword || !form.confirmPassword){
            setErrors("Please enter both password and confirm password");
            return false;
        }
        if(form.newPassword.length<8){
            setErrors("Password must be atleast 8 characters long");
            return false;
        }
        if(form.newPassword !== form.confirmPassword){
            setErrors("Passwords do not match");
            return false;
        }

        const passwordRegex = /^(=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@!$%*?&])(A-Za-z\d@$%!*?&){8,}$/;
        if(!passwordRegex.test(form.newPassword)){
            setErrors("Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return false;
        }
        return true;
    }

    const handleChange = (e) => {
       const {name, value} = e.target;
       setForm(prev=>({
        ...prev, 
        [name]:value
       }));
       setErrors("");
    }
    
    const handleSubmit = async(e) =>{
        e.preventDefault();
        setLoading(true);
        setErrors("");
        setSuccessMessage("");

        if(!validateForm()){
            setLoading(false);
            return;
        }
        try{
           await resetPassword(email, form.newPassword);
           setSuccessMessage("Password has been reset successfully");
            setTimeout(() => {
                navigate('/login');
            }, 3000);
        } catch(error){
            setErrors(error.response?.data?.message || 'Failed to reset password');
        } finally{
            setLoading(false);
        }
    }
    return(
        <div className="reset-password-container">
            <h2>Reset Passwword</h2>
            <p className="description">Please enter your new password</p>
            {successMessage && (
                <div className="success-message">{successMessage}</div>
            )}
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="newPassword">New Password:</label>
                    <input type="password" id = "newPassword" name="newPassword" 
                    value = {form.newPassword} onChange={handleChange}
                    autoFocus required/>
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password:</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" 
                    value={form.confirmPassword} onChange={handleChange} 
                    required/>
                </div>

            {errors && <div className="error-message">{errors}</div>}
                <div className="form-group">
                    <button type="submit" disabled = {loading} className="submit-button">
                        {loading?"Resetting...":"Reset Password"}
                    </button>
                </div>
                
                <div className="form-footer">
                    <p>Remember your password? <a href="/login">Login here</a></p>
                </div>
            </form>
        </div>
    )
}

export default ResetPassword;