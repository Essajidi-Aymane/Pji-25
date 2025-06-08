 import React, { useState, useEffect } from 'react';
import axios from 'axios'; 
 export default function AdminPanel() {
   const [attrs, setAttrs] = useState([]);
   const [input, setInput] = useState('');
   const [log, setLog] = useState('');

   const fetchAttrs = async () => {
     const res = await fetch('/api/attrs');
     const data = await res.json();
     setAttrs(data);
   };

   useEffect(() => {
     fetchAttrs();
   }, []);

   const handleSetup = async () => {
     const res = await fetch('/api/setup', { method: 'POST' });
     const msg = await res.text();
     setLog(msg);
   };

   const handleDefineAttrs = async () => {
     const body = input.split(',').map(a => a.trim());
     const res = await fetch('/api/attrs', {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify(body),
     });
     const msg = await res.text();
     setLog(msg);
     fetchAttrs();
   };

   return (
     <div className="p-6 bg-white rounded shadow">
       <h2 className="text-xl font-bold mb-2">Panneau d'administration</h2>
       <button onClick={handleSetup} className="bg-blue-500 text-white px-4 py-1 rounded mr-2">
         Initialiser le système
       </button>
       <div className="my-3">
         <input
           value={input}
           onChange={(e) => setInput(e.target.value)}
           placeholder="ex: student,prof,iot"
           className="border p-2 w-full"
         />
         <button onClick={handleDefineAttrs} className="mt-2 bg-green-600 text-white px-4 py-1 rounded">
           Définir les attributs
         </button>
       </div>
       <p className="text-sm text-gray-600">Actuellement : {attrs.join(', ')}</p>
       <p className="mt-2 text-blue-700 font-semibold">{log}</p>
     </div>
   );
 }
