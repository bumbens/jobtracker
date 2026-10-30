import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import apiClient from "../api/client";
import { APPLICATION_FIELDS } from "../utils/applicationFields";

/** Form for creating a new job application (POST /applications). */
function AddApplication() {
    const [formData, setFormData] = useState({});
    const [positionSuggestions, setPositionSuggestions] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        apiClient.get('/applications')
            .then((response) => {
                const uniquePosition = [...new Set(response.data.map((app) => app.position))];
                setPositionSuggestions(uniquePosition);
            })
            .catch(() => {

            });
    }, []);

    const handleChange = (e) => {
        setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    };
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await apiClient.post('/applications', {
                ...formData,
                jobUrl: formData.jobUrl || null,
                candidateProfileUrl: formData.candidateProfileUrl || null,
                source: formData.source || null,
                salaryRange: formData.salaryRange || null,
                notes: formData.notes || null,
            });
            navigate('/applications');
        } catch (err) {
            setError('Failed to save the application - check fields');
        }
    };

    return (
        <div className="min-h-screen p-8 flex flex-col items-center bg-gray-50">
            <div className="w-full max-w-md mb-4">
                <Link
                    to="/applications"
                    className="text-blue-500 hover:underline block mb-4"
                    onClick={(e) => {
                        if (Object.values(formData).some(Boolean)) {
                            const shouldLeave = window.confirm("Are you sure? The changes will not be saved.")
                            if (!shouldLeave) {
                                e.preventDefault();
                            }
                        }
                    }}
                >
                    &larr; Back to applications
                </Link>
            </div>
            <form onSubmit={handleSubmit} className="bg-white p-10 rounded-xl shadow-lg w-full max-w-md space-y-4 animate-fade-in-up">
                <h1 className="text-2xl font-bold mb-2 text-gray-800">Add application</h1>
                {error && <p className="text-red-500">{error}</p>}

                {APPLICATION_FIELDS.map((field) => (
                    <div key={field.name}>
                        <label className="block text-sm text-gray-600 mb-1">{field.label}</label>
                        <input
                            type={field.type}
                            name={field.name}
                            value={formData[field.name] || ''}
                            onChange={handleChange}
                            className="w-full border rounded-lg px-3 py-2 transition focus:ring-2 focus:ring-blue-500"
                            required={field.required}
                            list={field.datalist ? 'position-suggestions' : undefined}
                        />
                        {field.datalist && (
                            <datalist id="position-suggestions">
                                {positionSuggestions.map((suggestion) => (
                                    <option key={suggestion} value={suggestion} />
                                ))}
                            </datalist>
                        )}

                    </div>
                ))}
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Notes (optional)</label>
                    <textarea
                        name="notes"
                        value={formData.notes || ''}
                        onChange={handleChange}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-blue-500 text-white p-2 rounded-lg shadow-sm transition-colors duration-200 hover:bg-blue-600"
                >
                    Save
                </button>
            </form>
        </div>
    );

}

export default AddApplication;