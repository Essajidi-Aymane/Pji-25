import React, { useState, useEffect } from 'react';
import JsonUtil from '../utils/JsonUtil';
import axios from 'axios';
export default function EncryptForm() {
  const [attrs, setAttrs] = useState([]);
  const [msg, setMsg] = useState('');
  const [policy, setPolicy] = useState('');
  const [userAttrs, setUserAttrs] = useState('');
  const [result, setResult] = useState('');
  const [keys,setKeys] = useState(null);
  const [cipher, setCipher] = useState(''); 
  const [decrypted, setDecrypted] = useState(''); 
const [finaleMsg, setFinaleMsg] = useState('');
const buttonBase = "transition duration-300 delay-100 ease-in-out transform hover:scale-105 hover:shadow-md";
  useEffect(() => {
    fetch('/api/attrs')
      .then(r => r.json())
      .then(setAttrs);
  }, []);

const handleEncrypt = async () => {
  if (!keys?.UK || !keys?.EK) {
    alert("Clés EK/UK manquantes !");
    return;
  }

  let ukParsed;
  try {
    ukParsed = typeof keys.UK === 'string' ? JSON.parse(keys.UK) : keys.UK;
  } catch (e) {
    alert("Erreur: le champ UK n'est pas un JSON valide.");
    return;
  }

  try {
    const preReq = {
      message: msg,
      policy,
      ek: keys.EK,
      uk: ukParsed
    };

    const preRes = await fetch('/api/client/encrypt', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(preReq)
    });

    if (!preRes.ok) throw new Error("Erreur dans /client/encrypt");
    const preCipher = await preRes.text();

    const encRes = await fetch('/api/encrypt', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        preCt: preCipher,
        ukJson: ukParsed
      })
    });

    if (!encRes.ok) throw new Error("Erreur dans /encrypt");
    const fullCipher = await encRes.text();

    setResult(fullCipher);
    setCipher(fullCipher);
  } catch (err) {
    console.error("Erreur handleEncrypt:", err);
    alert("Erreur lors du chiffrement : " + err.message);
  }
};

const handleFinalDecrypt = async () => {
  const res = await axios.post('/api/client/decrypt', {
    transformedCt: JSON.stringify(decrypted),  
    dk: keys.DK
  });

  alert("Message final déchiffré : " + res.data);
};


const handleDecrypt = async () => {
  if (!keys?.TK || !keys?.D) {
    alert("Clés TK ou D manquantes !");
    return;
  }

  let tkParsed;
  try {
    tkParsed = typeof keys.TK === 'string' ? JSON.parse(keys.TK) : keys.TK;
  } catch (e) {
    alert("Erreur: le champ TK n'est pas un JSON valide.");
    return;
  }

  try {
    const decryptReq = {
      cipherTextJson: cipher,
      tkJson: tkParsed,
      attrs: userAttrs.split(',').map(a => a.trim()),
      D: keys.D
    };

    const res = await axios.post('/api/decrypt', decryptReq);
    setDecrypted(res.data); 
  } catch (err) {
    console.error("Erreur handleDecrypt:", err);
    alert("Erreur de transformation serveur : " + err.message);
  }
};



  const handleKeygen = async () => {
    const attrList = userAttrs
    .split(',')
    .map(a => a.trim())
    .filter(Boolean); 

  const res = await axios.post('/api/keygen', {
    userId: 'user-react',
    attrs: attrList
});
console.log("Clés reçues :", res.data);
  setKeys(res.data);
  };

 return (
    <div className="p-6 bg-white rounded shadow mt-4">
      <h2 className="text-xl font-bold mb-2">Formulaire de chiffrement ABE Cloud</h2>

      <div className='mb-6'>
      <input
        value={msg}
        onChange={e => setMsg(e.target.value)}
        placeholder="Message"
        className="border p-2 w-full mb-2"
      />
      </div>
      <div className='grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4'>
<div>
  <label className="block font-semibold mb-1">Politique</label>
        <input
        value={policy}
        onChange={e => setPolicy(e.target.value)}
        placeholder="Politique (ex: student AND iot)"
        className="border p-2 w-full mb-2"
      />
          <div className="mt-2 space-x-1">
            {userAttrs.split(',').filter(Boolean).map(attr => (
              <span key={attr} className="inline-block bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-xs">
                {attr.trim()}
              </span>
            ))}
            </div>

</div>
<div>
      <label className="block font-semibold mb-1"> Attributs Utilisateur</label>
      <input
        value={userAttrs}
        onChange={e => setUserAttrs(e.target.value)}
        placeholder="Attributs (ex: student,iot)"
        className="border p-2 w-full mb-4"
      />
</div>
      </div>



      <div className="flex flex-wrap justify-center gap-4 mb-6">
        <button
          onClick={handleKeygen}
          className={`bg-gray-700 text-white px-4 py-2 rounded ${buttonBase}`}
        >
          Générer mes clés
        </button>
        <button
          onClick={handleEncrypt}
          className= {`bg-purple-600 text-white px-4 py-2 rounded ${buttonBase}`}
          disabled={!keys}
        >
          Chiffrer
        </button>
        <button
          onClick={handleDecrypt}
          className={`bg-blue-600 text-white px-4 py-2 rounded ${buttonBase}`}
          disabled={!cipher}
        >
          Déchiffrer
        </button>
      </div>

{cipher && (
  <div className=" bg-gray-100  mt-4 text-sm break-words rounded p-4">
    <h3 className="font-semibold text-purple-700 mb-2"> Chiffrement</h3>
    <p><strong>Message chiffré :</strong> {JSON.parse(cipher).encMsg}</p>
    <p><strong>Politique :</strong> {JSON.parse(cipher).policy}</p>
  </div>
)}

 {decrypted && (
        <div className="bg-green-50 border-l-4 border-green-500 p-4 mt-4 rounded">
          <h3 className="font-semibold text-green-700 mb-1"> Transformation partielle réussie</h3>
          <pre className="text-xs">{JSON.stringify(decrypted, null, 2)}</pre>
          <button
            onClick={handleFinalDecrypt}
            className="bg-green-600 text-white mt-2 px-4 py-1 rounded"
          >
            Déchiffrement final
          </button>
        </div>
      )}

        {finaleMsg && (
        <div className="bg-green-100 border border-green-500 p-3 mt-4 rounded text-green-800">
           <strong>Message final :</strong> {finaleMsg}
        </div>
      )}


    </div>
  );
}