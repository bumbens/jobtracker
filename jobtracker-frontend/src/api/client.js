import axios from 'axios';

// Axios instance for the backend API. A request interceptor attaches the
// JWT from localStorage to every outgoing request, so callers never have
// to set the Authorization header themselves.
const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api',
})

apiClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
});

export default apiClient;