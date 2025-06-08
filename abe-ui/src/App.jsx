// src/App.jsx
import React, { useState } from "react";
import AdminPanel from "./components/AdminPanel";
import EncryptForm from "./components/EncryptForm";

function App() {
  const [view, setView] = useState("admin");

  return (
    <div className="min-h-screen p-6 bg-gray-50 font-sans">
      <h1 className="text-3xl font-bold mb-6 text-center"> Projet ABE Interface</h1>

      <div className="flex justify-center mb-6">
        <button
          onClick={() => setView("admin")}
          className={`px-4 py-2 rounded-l ${view === "admin" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
        >
          Admin
        </button>
        <button
          onClick={() => setView("user")}
          className={`px-4 py-2 rounded-r ${view === "user" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
        >
          Utilisateur
        </button>
      </div>

      <div className="max-w-3xl mx-auto bg-white p-6 rounded shadow">
        {view === "admin" ? <AdminPanel /> : <EncryptForm />}
      </div>
    </div>
  );
}

export default App;
