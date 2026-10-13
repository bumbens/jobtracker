import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import apiClient from '../api/client';


// Turns the applications count into a readable sentence (none / singular / plural).
function getApplicationsMessage(count) {
    if (count === 0) return "You haven't applied for any position yet"
    if (count === 1) return "You have applied for 1 position so far"
    return `You have applied for ${count} positions so far`
}

/** Landing page after login: greeting, link to the applications list, and stats from /dashboard/stats. */
function Dashboard() {
    const { user, logout } = useAuth();
    const [stats, setStats] = useState(null);

    useEffect(() => {
        apiClient.get('/dashboard/stats')
            .then((response) => {
                setStats(response.data);
            })
            .catch(() => {

            })
    }, [])

    
    return (
        <div className="min-h-screen flex flex-col items-center justify-center">
            <h1 className="text-2xl font-semibold mb-4">Welcome, {user.displayName}!</h1>
            <div className="space-x-3">
                <Link to="/applications" className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
                    View Applications
                </Link>
            </div>
            <div className="mt-6 text-center">
                <h2 className="text-xl font-semibold mb-2">Stats</h2>
                {stats ? getApplicationsMessage(stats.totalApplications) : 'Fetching data...'}
            </div>
            <div className="mt-6">
                <h2 className="text-xl font-semibold mb-2">Recent Applications</h2>
                {/* Here you can fetch and display recent applications */}
            </div>
            <button onClick={logout} className="bg-red-500 text-white p-2 rounded hover:bg-red-600">
                Logout
            </button>
        </div>
    );
}

export default Dashboard;