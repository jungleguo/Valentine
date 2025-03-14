import axios from 'axios';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 5000
});

export default {
  getPortalConfig: () => apiClient.get('/portal').then(res => res.data)
};