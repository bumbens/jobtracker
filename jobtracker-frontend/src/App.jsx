import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import Applications from "./pages/Applications";
import AddApplication from "./pages/AddApplication";
import ProtectedRoute from "./components/ProtectedRoute";
import ApplicationDetail from "./pages/ApplicationDetail";

// Top-level route table: public auth pages, plus protected pages behind ProtectedRoute.
function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
              <Dashboard />
          </ProtectedRoute>} />
      <Route
        path="/applications"
        element={
          <ProtectedRoute>
              <Applications />
          </ProtectedRoute>} />
      <Route
        path="/add-application"
        element={
          <ProtectedRoute>
              <AddApplication />
          </ProtectedRoute>} />
      <Route
        path="/applications/:id"
        element={
          <ProtectedRoute>
              <ApplicationDetail />
          </ProtectedRoute>} />
    </Routes>
  )
}

export default App
