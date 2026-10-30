export const APPLICATION_FIELDS = [
    { name: 'company', type: 'text', label: 'Company', required: true },
    { name: 'position', type: 'text', label: 'Position', required: true },
    { name: 'applicationDate', type: 'date', required: true },
    { name: 'source', type: 'text', label: 'Source (optional)' },
    { name: 'jobUrl', type: 'url', label: 'Link to job posting (optional)' },
    { name: 'candidateProfileUrl', type: 'url', label: 'Link to candidate profile (optional)' },
    { name: 'salaryRange', type: 'text', label: 'Salary range (optional)' },
];