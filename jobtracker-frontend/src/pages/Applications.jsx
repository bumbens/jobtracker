import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getStatusColor, getStatusLabel, STATUSES } from "../utils/status";
import apiClient from "../api/client";
import Spinner from "../components/Spinner";

/** Lists the authenticated user's job applications (GET /applications) and lets them delete one. */

function Applications() {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [statusFilter, setStatusFilter] = useState("");

    useEffect(() => {
        apiClient.get("/applications")
            .then((response) => {
                setApplications(response.data);
            })
            .catch(() => {
                setError("Failed to fetch applications.");
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure?")) {
            return;
        }
        try {
            await apiClient.delete(`/applications/${id}`);
            setApplications((prev) => prev.filter((app) => app.id !== id));
        } catch (err) {
            alert('Failed to delete the application');
        }
    };

    const handleStatusChange = async (id, newStatus) => {
        try {
            await apiClient.patch(`/applications/${id}/status`, { status: newStatus });
            setApplications((prev) => prev.map((app) => app.id === id ? { ...app, status: newStatus } : app));
        } catch (err) {
            alert('Failed to update the application status');
        }
    };

    const filteredApplications = statusFilter
        ? applications.filter((app) => app.status === statusFilter)
        : applications;


    const STATUS_ORDER = ['OFFER', 'INTERVIEW', 'SCREENING', 'APPLIED', 'WITHDRAWN', 'REJECTED']

    const sortedApplications = [...filteredApplications].sort(
        (a, b) => STATUS_ORDER.indexOf(a.status) - STATUS_ORDER.indexOf(b.status)
    );

    if (loading) return <Spinner />;
    if (error) return <p className="p-8">Error: {error}</p>;

    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-4xl mx-auto">
                <Link to="/dashboard" className="text-blue-500 hover:underline block mb-4">&larr; Back to dashboard</Link>

                <div className="flex flex-wrap justify-between items-center gap-3 mb-6">
                    <h1 className="text-2xl font-bold">My Applications</h1>
                    <div className="flex flex-wrap items-center gap-3">
                        <select
                            value={statusFilter}
                            onChange={(e) => setStatusFilter(e.target.value)}
                            className="border border-gray-300 rounded py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="">All statuses</option>
                            {STATUSES.map((status) => (
                                <option key={status} value={status}>{getStatusLabel(status)}</option>
                            ))}
                        </select>
                        <Link to="/add-application" className="inline-block bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
                            Add New Application
                        </Link>
                    </div>
                </div>

                {applications.length === 0 ? (
                    <p className="text-gray-600">No applications found. <Link to="/add-application" className="text-blue-500 hover:underline">Add a new application</Link>.</p>
                ) : filteredApplications.length === 0 ? (
                    <p className="text-gray-600">No applications match the selected filter.</p>
                ) : (
                    <ul className="space-y-3">
                        {sortedApplications.map((app) => (
                            
                            <li key={app.id} className="bg-white p-4 rounded shadow-sm hover:shadow-md transition flex flex-col items-start gap-3 sm:flex-row sm:justify-between sm:items-center">
                                <div>
                                    <div className="flex flex-col sm:flex-row sm:flex-wrap items-start sm:items-center gap-2">
                                        <Link to={`/applications/${app.id}`} className="text-blue-500 hover:underline sm:w-56 truncate">
                                            {app.company} - {app.position}
                                        </Link>

                                        <select
                                            value={app.status}
                                            onChange={(e) => handleStatusChange(app.id, e.target.value)}
                                            className={`border rounded px-2 py-1 text-sm sm:ml-10 ${getStatusColor(app.status)}`}
                                        >
                                            {STATUSES.map((status) => (
                                                <option key={status} value={status} className="bg-gray-50 text-gray-600">{getStatusLabel(status)}</option>
                                            ))}
                                        </select>
                                    </div>
                                    <p className="text-xs text-gray-400 mt-1">
                                        Applied: {new Date(app.applicationDate).toLocaleDateString()} · Last updated: {new Date(app.updatedAt).toLocaleDateString()}
                                    </p>
                                </div>
                                <button
                                    onClick={() => handleDelete(app.id)}
                                    className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
                                >
                                    Delete
                                </button>
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export default Applications;
