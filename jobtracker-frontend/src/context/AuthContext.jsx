import { createContext, useContext, useState } from "react";
import apiClient from "../api/client";

const AuthContext = createContext(null);

/**
 * Provides the current user and login/register/logout actions to the whole
 * app via React Context. Persists the token and user info to localStorage
 * so a page refresh doesn't silently log the user out.
 */
export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        const storedUser = localStorage.getItem('user');
        return storedUser ? JSON.parse(storedUser) : null;
    });

    const login = async (email, password) => {
        const response = await apiClient.post('/auth/login', { email, password });
        const { token, userId, email: userEmail, displayName } = response.data;

        localStorage.setItem('token', token);
        const userData = { userId, email: userEmail, displayName };
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);
    }

    const register = async (email, password, displayName) => {
        const response = await apiClient.post('/auth/register', { email, password, displayName });
        const { token, userId, email: userEmail, displayName: userDisplayName } = response.data;

        localStorage.setItem('token', token);
        const userData = { userId, email: userEmail, displayName: userDisplayName };
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);
    }

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
    }

    const value = {
        user,
        login,
        register,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

    export function useAuth() {
        return useContext(AuthContext);
    }
