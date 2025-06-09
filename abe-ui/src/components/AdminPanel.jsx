 import React, { useState, useEffect } from 'react';
import axios from 'axios'; 
 export default function AdminPanel() {
   const [attrs, setAttrs] = useState([]);
   const [input, setInput] = useState('');
   const [log, setLog] = useState('');

    const buttonBase = "transition duration-300 delay-100 ease-in-out transform hover:scale-105 hover:shadow-md";

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
     <div className="flex flex-col gap-1 p-6 bg-white rounded shadow">
      <div>
       <h2 className="text-xl font-bold mb-2">Panneau d'administration</h2>
       <button onClick={handleSetup} className={`bg-blue-500 text-white px-4 py-1 rounded mr-2 ${buttonBase}`}>
         Initialiser le système
       </button>

      </div>
       <div className="my-3">
         <input
           value={input}
           onChange={(e) => setInput(e.target.value)}
           placeholder="ex: student,prof,iot"
           className="border p-2 w-full"
         />
       
       </div>
       <div>

         <button onClick={handleDefineAttrs} className={` bg-green-600 text-white px-4 py-1 rounded ${buttonBase}`}>
           Définir les attributs
         </button>
       </div>
       <p className="mt-2 text-sm text-gray-600">Actuellement : {attrs.join(', ')}</p>
       <p className="mt-2 text-blue-700 font-semibold">{log}</p>
     </div>
   );
 }
