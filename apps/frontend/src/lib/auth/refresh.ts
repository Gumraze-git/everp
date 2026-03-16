import axios from 'axios';
import { getAuthBaseUrl, getOauthClientId } from './config';
import { buildAuthXsrfHeaders } from './csrf';

export async function trySilentRefresh() {
  const authUrl = getAuthBaseUrl();
  const clientId = getOauthClientId();
  try {
    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: clientId,
    });

    const headers = await buildAuthXsrfHeaders();

    await axios.post(`${authUrl}/oauth2/token`, body.toString(), {
      headers,
      withCredentials: true,
    });
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error('Silent refresh failed:', error.response?.data || error.message);
    } else {
      console.error('Silent refresh failed:', error);
    }
    throw new Error('refresh_failed');
  }
}
