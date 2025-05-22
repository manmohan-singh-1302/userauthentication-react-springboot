import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Register(){
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        username: "",
        email: "",  
        password: "",  
    })
const [message, setMessage] = useState("");
const navigate = useNavigate();
const handleChange = (e) => {
    setForm({...form, [e.target.name]:e.target.value})
}

async function handleSubmit(e){
    e.preventDefault();
    try{
        const response = await fetch("http://localhost:5454/api/users",{
            method: "POST",
            headers:{
                'Content-Type': 'application/json'
            },
            body:JSON.stringify(form)
        })
        if(response.ok){
            setMessage("User registered successfully");
            navigate("/login");
        }
        else{
            const data = await response.json();
            setMessage(data.message || "Failed to register user");
        }
    }catch (err){
        setMessage("Error connecting to server");   
    }
}



    return(
        <div>
        <h2>Register Page</h2>
        <form onSubmit={handleSubmit}>
            <div>
                <label htmlFor="firstName">FirstName:</label>
                <input id = "firstName" name="firstName" value={form.firstName} onChange={handleChange} required autoFocus/>
            </div>
            <div>
                <label htmlFor="lastName">lastName:</label>
                <input id = "lastName" type="text" name="lastName" value={form.lastName} onChange={handleChange} required/>
            </div>
            <div>
                <label htmlFor="username"> Username:</label>
                <input id = "username" name="username" value={form.username} onChange={handleChange} required/>
            </div>
            <div>
                <label htmlFor="email">Email:</label>
                <input id = "email" name="email" value={form.email} onChange={handleChange} required/>   
            </div>
            <div>
                <label htmlFor="password">Password:</label>
                <input id = "password" name="password" type = "password" value={form.password} onChange={handleChange} required/>
            </div>
            <div>
                <button type = "submit">Register</button>
            </div>
        </form>
        {message && <p>{message}</p>}
        </div>
    )
}

export default Register;