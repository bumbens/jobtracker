import { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import { getStatusColor, getStatusLabel } from "../utils/status";
import apiClient from "../api/client";
import Spinner from "../components/Spinner";

/** Application detail page: fetches the application plus its contacts and events in parallel. */
function ApplicationDetail() {
    const { id } = useParams();
    const [application, setApplication] = useState(null);
    const [contacts, setContacts] = useState([]);
    const [events, setEvents] = useState([]);

    const [contactName, setContactName] = useState('');
    const [contactRole, setContactRole] = useState('');
    const [contactLinkedin, setContactLinkedin] = useState('');
    const [contactEmail, setContactEmail] = useState('');

    const [getApplicationsError, setGetApplicationError] = useState(null);
    const [addContactError, setAddContactError] = useState(null);
    const [deleteContactError, setDeleteContactError] = useState(null);

    const [loading, setLoading] = useState(true);

    useEffect(() => {
        Promise.all([
            apiClient.get(`/applications/${id}`),
            apiClient.get(`/applications/${id}/contacts`),
            apiClient.get(`/applications/${id}/events`),
        ])
            .then(([appResponse, contactsResponse, eventsResponse]) => {
                setApplication(appResponse.data);
                setContacts(contactsResponse.data);
                setEvents(eventsResponse.data);
            })
            .catch(() => {
                setGetApplicationError('Failed to fetch application details')
            })
            .finally(() => {
                setLoading(false)
            });
    }, [id])

    const handleAddContact = async (e) => {
        e.preventDefault();
        setAddContactError(null);
        try {
            const response = await apiClient.post(`/applications/${id}/contacts`, {
                name: contactName,
                role: contactRole || null,
                linkedInUrl: contactLinkedin || null,
                email: contactEmail || null,
            });
            setContacts((prev) => [...prev, response.data]);
            setContactName('');
            setContactRole('');
            setContactLinkedin('')
            setContactEmail('');
        } catch (err) {
            setAddContactError('Failed to add contact');
        }
    }

    const handleDeleteContact = async (contactId) => {
        if (!window.confirm('Delete this contact?')) return;
        try {
            await apiClient.delete(`/applications/${id}/contacts/${contactId}`);
            setContacts((prev) => prev.filter((c) => c.id !== contactId));
        } catch (err) {
            setDeleteContactError('Failed to delete contact');
        }
    }

    if (loading) return <Spinner />;
    if (getApplicationsError) return <p className="p-8 text-red-500">{getApplicationsError}</p>;

    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-2xl mx-auto">
                <Link to="/applications" className="text-blue-500 hover:underline block mb-4">&larr; Back to applications</Link>

                <div className="bg-white rounded shadow-sm p-6 mb-6">
                    <h1 className="text-2xl font-bold mb-1">{application.company} — {application.position}</h1>
                    <p className="text-gray-600">Status: {application.status}</p>
                </div>

                <div className="bg-white rounded shadow-sm p-6 mb-6">
                    <h2 className="text-xl font-semibold mb-3">Contacts</h2>
                    {deleteContactError && <p className="text-red-500 text-sm mb-2">{deleteContactError}</p>}
                    {contacts.length === 0 ? (
                        <p className="text-gray-500 mb-3">No contacts yet.</p>
                    ) : (
                        <ul className="space-y-2 mb-4">
                            {contacts.map((contact) => (
                                <li key={contact.id} className="border-b last:border-0 pb-2 flex justify-between items-center">
                                    <div>
                                        <p className="font-medium">{contact.name}</p>
                                        <p className="text-sm text-gray-500">{contact.role} — {contact.email}</p>
                                    </div>
                                    <button
                                        onClick={() => handleDeleteContact(contact.id)}
                                        className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
                                    >
                                        Delete
                                    </button>
                                </li>
                            ))}
                        </ul>
                    )}

                    <form onSubmit={handleAddContact} className="space-y-2 border-t pt-4">
                        {addContactError && <p className="text-red-500 text-sm">{addContactError}</p>}
                        <input
                            type="text"
                            placeholder="Name"
                            value={contactName}
                            onChange={(e) => setContactName(e.target.value)}
                            className="w-full border rounded px-3 py-2"
                            required
                        />
                        <input
                            type="text"
                            placeholder="Role (optional)"
                            value={contactRole}
                            onChange={(e) => setContactRole(e.target.value)}
                            className="w-full border rounded px-3 py-2"
                        />
                        <input
                            type="email"
                            placeholder="Email (optional)"
                            value={contactEmail}
                            onChange={(e) => setContactEmail(e.target.value)}
                            className="w-full border rounded px-3 py-2"
                        />
                        <input
                            type="url"
                            placeholder="LinkedIn URL (optional)"
                            value={contactLinkedin}
                            onChange={(e) => setContactLinkedin(e.target.value)}
                            className="w-full border rounded px-3 py-2"
                        />
                        <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                            Add Contact
                        </button>
                    </form>
                </div>

                <div className="bg-white rounded shadow-sm p-6">
                    <h2 className="text-xl font-semibold mb-3">History</h2>
                    {events.length === 0 ? (
                        <p className="text-gray-500">No events yet.</p>
                    ) : (
                        <ul className="space-y-2">
                            {events.map((event) => (
                                <li key={event.id} className={`${getStatusColor(event.applicationStatus)} p-3 rounded`}>
                                    <p className="font-medium">{getStatusLabel(event.applicationStatus)} — {event.date}</p>
                                    {event.notes && <p className="text-sm text-gray-500">{event.notes}</p>}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ApplicationDetail;
