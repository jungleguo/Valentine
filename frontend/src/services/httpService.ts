import axios from 'axios'

export default function httpService() {
    const http = axios.create({ baseURL: 'localhost:8080' });

    http.interceptors.request.use((config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });

    // 响应拦截器（统一错误处理）
    http.interceptors.response.use(
        (response) => response.data,
        (error) => {
            return Promise.reject(error);
        }
    );

    const get = (uri: string) => {
        return http.get(uri);
    }

    const post = (uri:string, data: any) => {
        return http.post(uri, data);
    }

    return {
        get,
        post
    }
}