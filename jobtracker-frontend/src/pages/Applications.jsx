import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/client";
import Spinner from "../components/Spinner";

/** Lists the authenticated user's job applications (GET /applications) and lets them delete one. */
function Applications() {
    const [applications, setApplications] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
        if(!window.confirm("Are you sure?")) {
            return;
        }
        try {
            await apiClient.delete(`/applications/${id}`);
            setApplications((prev) => prev.filter((app) => app.id !== id));
        } catch (err) {
            alert('Failed to delete the application');
        }
    };

    if (loading) return <Spinner />;
    if (error) return <p className="p-8">Error: {error}</p>;

    return (
        <div className="min-h-screen p-8">
            <h1 className="text-2xl font-bold mb-6">My Applications</h1>
            {applications.length === 0 ? (
                <p className="text-gray-600">No applications found. <Link to="/add-application" className="text-blue-500 hover:underline">Add a new application</Link>.</p>
            ) : (
                <ul className="space-y-3">
                    {applications.map((app) => (
                        <li key={app.id} className="bg-white p-4 rounded shadow-sm hover:shadow-md transition flex justify-between items-center">
                            <Link to={`/applications/${app.id}`} className="text-blue-500 hover:underline">
                                {app.company} - {app.position}
                            </Link>
                            <p className="text-gray-600 text-sm">Status: {app.status}</p>
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