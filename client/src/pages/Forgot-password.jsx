import { useNavigate } from "react-router-dom";

function ForgotPassword(){
    const [form, setForm] = useState({
        registeredEmail:""
    })
    const [message, setMessage] = useState("");
    const navigate  = useNavigate();
    const handleChange = (e) =>{
        setForm({...form, [e.target.name]:e.target.value})
    }
    const handleSubmit = async (e) =>{
        e.preventDefault();
        try{
            
        }
    }
    return(
        <div>
            <h2>Reset Password</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="registeredEmail">Enter Registerd Email:</label>
                    <input id="registeredEmail" name="registeredEmail" value={form.registeredEmail} autoFocus onChange={handleChange} required/>
                </div>
                <div>
                    <button type = "submit">Submit</button>
                </div>
                {message && <p>{message}</p>}
            </form>
        </div>
    )
}