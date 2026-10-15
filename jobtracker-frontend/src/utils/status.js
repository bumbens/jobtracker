// Single source of truth for application statuses: the fixed display order,
// their Tailwind color classes, and their human-readable labels. Shared by
// Applications.jsx and StatusBarChart.jsx so both stay in sync.
export const STATUSES = ['APPLIED', 'SCREENING', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN'];

export function getStatusColor(status) {
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

export function getStatusLabel(status) {
    switch (status) {
        case "APPLIED": return "Applied";
        case "SCREENING": return "Screening";
        case "INTERVIEW": return "Interview";
        case "OFFER": return "Offer";
        case "REJECTED": return "Rejected";
        case "WITHDRAWN": return "Withdrawn";
        default: return status;
    }
}
