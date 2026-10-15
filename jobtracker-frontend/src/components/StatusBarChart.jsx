import { getStatusLabel } from "../utils/status";

/** Horizontal bar chart comparing application counts across statuses, scaled to the largest bucket. */
function StatusBarChart({countByStatus}) {
    const statuses = ['APPLIED', 'SCREENING', 'INTERVIEW', 'OFFER', 'REJECTED', 'WITHDRAWN'];
    const maxCount = Math.max(...statuses.map((status) => countByStatus[status] ?? 0), 1)

    return (
        <div className="space-y-2">
            {statuses.map((status) => {
                const count = countByStatus[status] ?? 0;
                const barWidth = (count / maxCount) * 100;
                return (
                    <div key={status} className="flex items-center gap-3">
                        <span className="w-24 text-sm text-gray-600 shrink-0">{getStatusLabel(status)}</span>
                        <div className="flex-1 bg-gray-100 rounded h-6">
                            <div
                                className="bg-blue-500 h-6 rounded"
                                style={{ width: `${barWidth}%` }}
                            ></div>
                            
                        </div>
                        <span className="w-8 text-sm text-gray-700 text-right shrink-0">{count}</span>
                    </div>
                );
            })}
        </div>
    );
}

export default StatusBarChart;