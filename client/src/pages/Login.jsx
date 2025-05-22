import { useState } from "react";
import { useNavigate } from "react-router-dom";
function Login(){
    const [form, setForm] = useState({
        username: "",
        password: "",
    })

    const [message, setMessage] = useState("");
    const navigate = useNavigate();
    const handleChange = (e) =>{
        setForm({...form, [e.target.name]:e.target.value})
    }


    const handleSubmit = async (e) =>{
        e.preventDefault();
        try{
            const response = await fetch("http://localhost:5454/api/auth/login",{
                method:"POST",
                headers:{
                    'Content-Type': 'application/json'
                },
                body:JSON.stringify(form)
            })
            if(response.ok){
                const data = await response.text();
                localStorage.setItem("token", data);
                setMessage("Login successsful");
               navigate("/home");
                // navigate("/home");
                // Navigate("/home");
            }
            else{
                const data = await response.json();
                setMessage(data.message || "Failed to login");
            }
        }catch (err){
                setMessage("Error connecting to server");
            }
    }
    return (
        <div>
            <h2>Login Page</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="username">Username:</label>
                    <input id="username" name="username" value={form.username} onChange={handleChange} required autoFocus/>
                </div>
                <div>
                    <label htmlFor="password">Password:</label>
                    <input id="password" type="password" name="password" value={form.password} onChange={handleChange} required/>
                </div>
                <div>
                    <button type="submit">Login</button>
                </div>
                {message && <p>{message}</p>}
                <div>
                    <a href="/forgot-password">Forgot password?</a>
                </div>
            </form>
        </div>
    )
}

export default Login;