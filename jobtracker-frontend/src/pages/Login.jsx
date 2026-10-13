import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/** Login form. Calls AuthContext.login and redirects to /dashboard on success. */
function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await login(email, password);
            navigate("/dashboard");
        } catch (err) {
            setError("Invalid email or password");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center">
            <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow-md w-full max-w-sm">
                <h2 className="text-2xl font-bold mb-6 text-center">Login</h2>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full border rounded px-3 py-2 mb-4"
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    className="w-full px-3 py-2 mb-4 border rounded"
                    required
                />
                <button type="submit" className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
                    Login
                </button>
                <p className="text-center text-sm text-gray-600 mt-4">
                    Don't have an account? <Link to="/register" className="text-blue-500 hover:underline">Register</Link>
                </p>
            </form>
        </div>
    );
}

export default Login;