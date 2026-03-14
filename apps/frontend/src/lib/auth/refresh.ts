import axios from 'axios';
import { persistAccessToken, readStoredToken } from './tokenStorage';
import { getAuthBaseUrl, getOauthClientId } from './config';

export async function trySilentRefresh() {
  const authUrl = getAuthBaseUrl();
  const clientId = getOauthClientId();

  const { token } = readStoredToken();
  try {
    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: clientId,
    });

    const headers: Record<string, string> = {
      'Content-Type': 'application/x-www-form-urlencoded',
    };

    if (token) {
      headers.access_token = token;
    }

    const res = await axios.post(`${authUrl}/oauth2/token`, body.toString(), {
      headers: {
        ...headers,
      },
      withCredentials: true,
    });

    const { access_token, expires_in } = res.data;

    persistAccessToken(access_token, expires_in);
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error('Silent refresh failed:', error.response?.data || error.message);
    } else {
      console.error('Silent refresh failed:', error);
    }
    throw new Error('refresh_failed');
  }
}
