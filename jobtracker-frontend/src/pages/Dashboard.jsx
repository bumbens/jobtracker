import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import { getStatusColor, getStatusLabel } from '../utils/status';
import Spinner from '../components/Spinner';
import apiClient from '../api/client';
import StatusBarChart from '../components/StatusBarChart';

function dayOrDays(numberOfDays) {
    return numberOfDays === 1 ? 'day' : 'days';
}

/** Landing page after login: greeting, link to the applications list, and stats from /dashboard/stats. */
function Dashboard() {
    const { user, logout } = useAuth();
    const [stats, setStats] = useState(null);
    const [applications, setApplications] = useState([]);
    const [statsError, setStatsError] = useState(null);
    const [applicationError, setApplicationError] = useState(null);
    const [statsLoading, setStatsLoading] = useState(true);
    const [applicationsLoading, setApplicationsLoading] = useState(true);
    const lastWeekDate = new Date();
    lastWeekDate.setUTCDate(lastWeekDate.getUTCDate() - 7);

    useEffect(() => {
        apiClient.get('/dashboard/stats')
            .then((response) => {
                setStats(response.data);
            })
            .catch(() => {
                setStatsError('Failed to fetch dashboard stats');
            })
            .finally(() => {
                setStatsLoading(false);
            });
        apiClient.get('/applications')
            .then((response) => {
                setApplications(response.data.content);
            })
            .catch(() => {
                setApplicationError('Failed to fetch applications');
            })
            .finally(() => {
                setApplicationsLoading(false);
            });
    }, [])

    const recentApplications = applications
        .filter((app) => new Date(app.createdAt) > lastWeekDate)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));




    if (statsLoading || applicationsLoading) return <Spinner />;

    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-4xl mx-auto">
                <div className="flex flex-wrap justify-between items-center gap-3 mb-8">
                    <h1 className="text-2xl font-semibold">Welcome, {user.displayName}!</h1>
                    <div className="space-x-3">
                        <Link to="/applications" className="bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
                            View Applications
                        </Link>
                        <button onClick={logout} className="bg-red-500 text-white p-2 rounded hover:bg-red-600">
                            Logout
                        </button>
                    </div>
                </div>
                <div className="bg-white rounded shadow-sm p-6 mb-6 animate-fade-in-up">
                    <h2 className="text-xl font-semibold mb-4">Stats</h2>
                    {stats ? (
                        <>
                            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
                                <div className="bg-gray-50 rounded p-4">
                                    <p className="text-sm text-gray-500">Total applications</p>
                                    <p className="text-2xl font-semibold mt-1">{stats.totalApplications}</p>
                                </div>
                                <div className="bg-gray-50 rounded p-4">
                                    <p className="text-sm text-gray-500">Response rate</p>
                                    <p className="text-2xl font-semibold mt-1">{stats.responseRate.toFixed(1)}%</p>
                                </div>
                                <div className="bg-gray-50 rounded p-4">
                                    <p className="text-sm text-gray-500">Avg. time to response</p>
                                    <p className="text-2xl font-semibold mt-1">
                                        {stats.averageResponse !== null
                                            ? `${stats.averageResponse.toFixed(0)} ${dayOrDays(Math.round(stats.averageResponse))}`
                                            : '—'}
                                    </p>
                                </div>
                            </div>
                            <StatusBarChart countByStatus={stats.countsByStatus} />
                        </>
                    ) : (
                        <p className="text-gray-400">Fetching data...</p>
                    )}

                </div>
                <div className="bg-white rounded shadow-sm p-6 animate-fade-in-up">

                    <h2 className="text-xl font-semibold mb-4">Recent Applications</h2>
                    <Link to="/add-application" className="inline-block bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 mb-4">
                        Add New Application
                    </Link>


                    {applicationError && <p className="text-red-500">{applicationError}</p>}
                    {statsError && <p className="text-red-500">{statsError}</p>}
                    {recentApplications.length === 0 ? (
                        <p className="text-gray-400">No applications in the last 7 days.</p>
                    ) : (
                        <ul className="space-y-3">
                            {recentApplications.map((app) => (
                                <li key={app.id} className="flex flex-wrap items-center justify-between gap-2 border-b last:border-0 pb-2">
                                    <div>
                                        <Link to={`/applications/${app.id}`} className="text-blue-500 hover:underline">
                                            {app.company} - {app.position}
                                        </Link>
                                        <p className="text-xs text-gray-400">
                                            Created: {new Date(app.createdAt).toLocaleDateString()} · Applied: {new Date(app.applicationDate).toLocaleDateString()}
                                        </p>
                                    </div>
                                    <span className={`${getStatusColor(app.status)} rounded shadow-sm min-w-30 text-center px-2 py-1 text-sm`}>
                                        {getStatusLabel(app.status)}
                                    </span>
                                </li>
                            ))}

                        </ul>
                    )}

                </div>
            </div>
        </div>
    );
}

export default Dashboard;