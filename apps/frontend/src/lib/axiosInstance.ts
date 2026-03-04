import axios from 'axios';
import Cookies from 'js-cookie';

async function resolveAccessToken(): Promise<string | null> {
  if (typeof window !== 'undefined') {
    return Cookies.get('access_token') ?? null;
  }

  const { cookies } = await import('next/headers');
  const cookieStore = await cookies();
  return cookieStore.get('access_token')?.value ?? null;
}

axios.interceptors.request.use(
  async (config) => {
    const token = await resolveAccessToken();

    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

export default axios;
