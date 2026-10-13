import { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";
import apiClient from "../api/client";
import Spinner from "../components/Spinner";

/** Application detail page: fetches the application plus its contacts and events in parallel. */
function ApplicationDetail() {
    const { id } = useParams();
    const [application, setApplication] = useState(null);
    const [contacts, setContacts] = useState([]);
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

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
                setError('Failed to fetch application details')
            })
            .finally(() => {
                setLoading(false)
            });
    }, [id])

    if (loading) return <Spinner />;
    if (error) return <p className="p-8 text-red-500">{error}</p>;

    return (
        <div className="min-h-screen p-8 max-w-2xl mx-auto">
            <Link to="/applications" className="text-blue-500 hover:underline">&larr; Back to applications</Link>

            <h1 className="text-2xl font-bold mt-4 mb-1">{application.company} — {application.position}</h1>
            <p className="text-gray-600 mb-6">Status: {application.status}</p>

            <section className="mb-8">
                <h2 className="text-xl font-semibold mb-3">Contacts</h2>
                {contacts.length === 0 ? (
                    <p className="text-gray-500">No contacts yet.</p>
                ) : (
                    <ul className="space-y-2">
                        {contacts.map((contact) => (
                            <li key={contact.id} className="bg-white p-3 rounded shadow-sm">
                                <p className="font-medium">{contact.name}</p>
                                <p className="text-sm text-gray-500">{contact.role} — {contact.email}</p>
                            </li>
                        ))}
                    </ul>
                )}
            </section>

            <section>
                <h2 className="text-xl font-semibold mb-3">History</h2>
                {events.length === 0 ? (
                    <p className="text-gray-500">No events yet.</p>
                ) : (
                    <ul className="space-y-2">
                        {events.map((event) => (
                            <li key={event.id} className="bg-white p-3 rounded shadow-sm">
                                <p className="font-medium">{event.applicationStatus} — {event.date}</p>
                                {event.notes && <p className="text-sm text-gray-500">{event.notes}</p>}
                            </li>
                        ))}
                    </ul>
                )}
            </section>
        </div>
    );
}

export default ApplicationDetail;

