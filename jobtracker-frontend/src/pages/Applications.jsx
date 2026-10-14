import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Spinner from "../components/Spinner";

/** Lists the authenticated user's job applications (GET /applications) and lets them delete one. */

function getStatusColor(status) {
    switch (status) {
        case "APPLIED": return "bg-gray-100 text-gray-700";
        case "SCREENING": return "bg-yellow-100 text-yellow-700";
        case "INTERVIEW": return "bg-blue-100 text-blue-700";
        case "OFFER": return "bg-green-100 text-green-700";
        case "REJECTED": return "bg-red-100 text-red-700";
        case "WITHDRAWN": return "bg-gray-200 text-gray-500";
        default: return "bg-gray-100 text-gray-700";
    }
}

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

    if (loading) return <Spinner />;
    if (error) return <p className="p-8">Error: {error}</p>;

    return (
        <div className="min-h-screen p-8">
            <Link to="/dashboard" className="text-blue-500 hover:underline block mb-4">&larr; Back to dashboard</Link>

            <h1 className="text-2xl font-bold mb-6">My Applications</h1>
            <div className="flex flex-wrap justify-between items-center gap-3 mb-6">
                <Link to="/add-application" className="inline-block bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 mb-1">
                    Add New Application</Link>
                <select 
                    value={statusFilter} 
                    onChange={(e) => setStatusFilter(e.target.value)} 
                    className="border border-gray-300 rounded py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
                >
                    <h1 className="text-lg font-semibold mb-2">Filter by:</h1>
                    <option value="" >All statuses</option>
                    <option value="APPLIED">Applied</option>
                    <option value="SCREENING">Screening</option>
                    <option value="INTERVIEW">Interview</option>
                    <option value="OFFER">Offer</option>
                    <option value="REJECTED">Rejected</option>
                    <option value="WITHDRAWN">Withdrawn</option>
                </select>

            </div>


            {applications.length === 0 ? (
                <p className="text-gray-600">No applications found. <Link to="/add-application" className="text-blue-500 hover:underline">Add a new application</Link>.</p>
            ) : filteredApplications.length === 0 ? (
                <p className="text-gray-600">No applications match the selected filter.</p>
            ) : ( 
                <ul className="space-y-3">
                    {filteredApplications.map((app) => (
                        <li key={app.id} className="bg-white p-4 rounded shadow-sm hover:shadow-md transition flex flex-col items-start gap-3 sm:flex-row sm:justify-between sm:items-center">
                            <div>
                                <div className="flex flex-wrap items-center gap-2">
                                    <Link to={`/applications/${app.id}`} className="text-blue-500 hover:underline sm:w-56 truncate">
                                        {app.company} - {app.position}
                                    </Link>

                                    <select
                                        value={app.status}
                                        onChange={(e) => handleStatusChange(app.id, e.target.value)}
                                        className={`border rounded px-2 py-1 text-sm sm:ml-10 ${getStatusColor(app.status)}`}
                                    >
                                        <option value="APPLIED">Applied</option>
                                        <option value="SCREENING">Screening</option>
                                        <option value="INTERVIEW">Interview</option>
                                        <option value="OFFER">Offer</option>
                                        <option value="REJECTED">Rejected</option>
                                        <option value="WITHDRAWN">Withdrawn</option>
                                    </select>
                                </div>
                                <p className={"text-xs text-gray-400 mt-1"}>
                                    Applied: {app.applicationDate} · Last updated: {new Date(app.updatedAt).toLocaleDateString()}
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
    );
}

export default Applications;