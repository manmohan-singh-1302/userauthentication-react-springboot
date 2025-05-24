import { useState, useEffect } from "react";
import "../styles/ValidateOtp.css";
import { useLocation, useNavigate } from "react-router-dom";
import { validateOtp } from "../services/authService";

function ValidateOtp () {
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);
    const [errors, setErrors ] = useState("");
    const [email, setEmail] = "";
    const [timeLeft, setTimeLeft] = useState(300);

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if(location.state?.email){
            setEmail(location.state.email);
        }
        else{
            navigate("/forgot-password");
        }
   

    const timer = setInterval(()=>{
        setTimeLeft((prevTime)=>{
            if(prevTime<=0){
            clearInterval(timer);
            return 0;
            }
            return prevTime-1;
        });
    }, 1000);
    return () => clearInterval(timer);
}, [location.state, navigate]);

const handleChange = (e) => {
    const value = e.target.value;

    if(/^\d*$/.test(value) && value.length <=6){
        setOtp(value);
        setErrors("");
    }
}

const formatTime = (seconds) => {
    const minutes = Math.floor(seconds/60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    if(!otp){
        setErrors("Please enter the OTP")
        setLoading(false);
        return;
    }

    if(otp.length!==6){
        setErrors("OTP must be 6 digits");
        setLoading(false);
        return;
    }

    try{
        await validateOtp(otp);
        navigate("/reset-password", {state:{email}});
    } catch (error) {
        setErrors(error.response?.data?.message || "Invalid OTP");
    } finally {
        setLoading(false);
    }
};
    return (
        <div>
            <h2>Validate OTP</h2>
            <p className="description">Please enter the 6-digit code sent to your email</p>
            {timeLeft > 0 ?(
                <p className="timer">Time remaining: {formatTime(timeLeft)}</p>
            ):(
                <p className="timer expired">OTP has expired</p>
            )}

            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="otp">Enter OTP:</label>
                    <input type="text" id="otp" name = "otp" value={otp} onChange={handleChange} required autoFocus placeholder="Enter 6-digit code" maxLength={6}/>
                </div>
                {errors && <div className="error-message">{errors}</div>}
                <div>
                    <button type="submit" className="submit-button" disabled = {loading || timeLeft === 0}>{loading?"Verifying...":"Verify OTP"}</button>
                </div>
                <div className="form-footer">
                    <p>Did not receive the code? {" "} <a href="/forgot-password">Request for new code</a></p>
                </div>
            </form>
        </div>
    )
}

export default ValidateOtp;