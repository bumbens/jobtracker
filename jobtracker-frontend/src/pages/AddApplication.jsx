import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import apiClient from "../api/client";

/** Form for creating a new job application (POST /applications). */
function AddApplication() {
    const [company, setCompany] = useState('');
    const [position, setPosition] = useState('');
    const [jobUrl, setJobUrl] = useState('');
    const [candidateProfileUrl, setCandidateProfileUrl] = useState('');
    const [source, setSource] = useState(''); 
    const [applicationDate, setApplicationDate] = useState('')
    const [salaryRange, setSalaryRange] = useState('');
    const [positionSuggestions, setPositionSuggestions] = useState([]);
    const [notes, setNotes] = useState('');
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await apiClient.post('/applications', {
                company,
                position,
                jobUrl: jobUrl || null,
                candidateProfileUrl: candidateProfileUrl || null,
                source: source || null,
                applicationDate,
                salaryRange: salaryRange || null,
                notes: notes || null,
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
                        if (company || position || jobUrl || candidateProfileUrl || source || applicationDate || salaryRange || notes) {
                            const shouldLeave = window.confirm("Are you sure? The changes will not be saved.");
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

                <div>
                    <label className="block text-sm text-gray-600 mb-1">Company</label>
                    <input
                        type="text"
                        value={company}
                        onChange={(e) => setCompany(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    />    
                    
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Position</label>
                    <input
                        type="text"
                        value={position}
                        onChange={(e) => setPosition(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                        list="position-suggestions"
                        required
                    />
                    <datalist id="position-suggestions">
                        {positionSuggestions.map((suggestion) => (
                            <option key={suggestion} value={suggestion} />
                        ))}
                    </datalist>
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Date of application</label>
                    <input
                        type="date"
                        value={applicationDate}
                        onChange={(e) => setApplicationDate(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Source (optional)</label>
                    <input
                        type="text"
                        value={source}
                        onChange={(e) => setSource(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Link to job posting (optional)</label>
                    <input
                        type="url"
                        value={jobUrl}
                        onChange={(e) => setJobUrl(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Link to candidate proile (optional)</label>
                    <input
                        type="url"
                        value={candidateProfileUrl}
                        onChange={(e) => setCandidateProfileUrl(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Salary range (optional)</label>
                    <input
                        type="text"
                        value={salaryRange}
                        onChange={(e) => setSalaryRange(e.target.value)}
                        className="w-full border rounded-lg px-3 py-2 transition focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Notes (optional)</label>
                    <textarea
                        value={notes}
                        onChange={(e) => setNotes(e.target.value)}
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