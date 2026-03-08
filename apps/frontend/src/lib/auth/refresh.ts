import axios from 'axios';
import { startAuthorization } from './startAuthorization';
import { persistAccessToken, readStoredToken } from './tokenStorage';

function makeBasicAuthHeader(clientId: string, clientSecret: string): string {
  const plain = `${clientId}:${clientSecret}`;
  const utf8 = new TextEncoder().encode(plain);
  let binary = '';
  for (let i = 0; i < utf8.length; i++) binary += String.fromCharCode(utf8[i]);
  const encoded = btoa(binary);
  return `Basic ${encoded}`;
}

export async function trySilentRefresh() {
  const AUTH_URL = process.env.NEXT_PUBLIC_AUTH_BASE_URL?.trim() || 'http://localhost:18081';
  const CLIENT_ID = process.env.NEXT_PUBLIC_OAUTH_CLIENT_ID?.trim() || 'everp-spa';

  const { token } = readStoredToken();
  try {
    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: CLIENT_ID,
    });

    const headers: Record<string, string> = {
      'Content-Type': 'application/x-www-form-urlencoded',
    };

    if (token) {
      headers.access_token = token;
    }

    if (CLIENT_ID === 'everp') {
      headers.Authorization = makeBasicAuthHeader('everp', 'super-secret');
    }

    const res = await axios.post(`${AUTH_URL}/oauth2/token`, body.toString(), {
      headers: {
        ...headers,
      },
      withCredentials: true,
    });

    const { access_token, expires_in } = res.data;

    persistAccessToken(access_token, expires_in);
  } catch (error) {
    startAuthorization('/');
    if (axios.isAxiosError(error)) {
      console.error('Silent refresh failed:', error.response?.data || error.message);
    } else {
      console.error('Silent refresh failed:', error);
    }
    throw new Error('refresh_failed');
  }
}
