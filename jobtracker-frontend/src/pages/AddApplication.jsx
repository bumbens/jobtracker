import { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/client";

/** Form for creating a new job application (POST /applications). */
function AddApplication() {
    const [company, setCompany] = useState('');
    const [position, setPosition] = useState('');
    const [jobUrl, setJobUrl] = useState('');
    const [source, setSource] = useState('');
    const [applicationDate, setApplicationDate] = useState('')
    const [salaryRange, setSalaryRange] = useState('');
    const [notes, setNotes] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        try {
            await apiClient.post('/applications', {
                company,
                position,
                jobUrl: jobUrl || null,
                source: source || null,
                applicationDate,
                salaryRange: salaryRange || null,
                notes: notes || null,
            });
            navigate('/applications');
        } catch(err){
            setError('Failed to save the application - check fields');
        }
    };

    return (
        <div className="min-h-screen p-8 flex justify-center">
            <form onSubmit={handleSubmit} className="bg-white p-8 rounded shadow-md w-full max-w-md space-y-4">
                <h1 className="text-2xl font-bold mb-2">Add application</h1>
                {error && <p className="text-red-500">{error}</p>}

                <div>
                    <label className="block text-sm text-gray-600 mb-1">Company</label>
                    <input
                        type="text"
                        value={company}
                        onChange={(e) => setCompany(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        required
                        />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Position</label>
                    <input
                        type="text"
                        value={position}
                        onChange={(e) => setPosition(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        required
                        />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Date of application</label>
                    <input
                        type="date"
                        value={applicationDate}
                        onChange={(e) => setApplicationDate(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        required
                        />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Source (optional)</label>
                    <input
                        type="text"
                        value={source}
                        onChange={(e) => setSource(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Salary range (optional)</label>
                    <input
                        type="text"
                        value={salaryRange}
                        onChange={(e) => setSalaryRange(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        />
                </div>
                <div>
                    <label className="block text-sm text-gray-600 mb-1">Notes (optional)</label>
                    <input
                        type="text"
                        value={notes}
                        onChange={(e) => setNotes(e.target.value)}
                        className="w-full border rounded px-3 py-2"
                        />
                </div>
                <button type="submit" className="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
                    Save
                </button>
            </form>
        </div>
    );
    
}

export default AddApplication;