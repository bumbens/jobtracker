/** Loading spinner, reused on every page while data is fetching. */
function Spinner() {
  return (
    <div className="flex items-center justify-center p-8">
      <div className="w-8 h-8 border-4 border-gray-200 border-t-blue-500 rounded-full animate-spin"></div>
    </div>
  );
}

export default Spinner;
