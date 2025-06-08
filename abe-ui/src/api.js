import axios from 'axios';
const BASE = 'http://localhost:8080/api';

export const setupSystem = () => axios.post(`${BASE}/setup`);
export const defineAttrs = (attrs) => axios.post(`${BASE}/attrs`, attrs);
export const encrypt = (data) => axios.post(`${BASE}/encrypt`, data);
export const decrypt = (data) => axios.post(`${BASE}/decrypt`, data);