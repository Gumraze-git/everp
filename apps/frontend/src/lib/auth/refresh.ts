import axios from 'axios';
import { getAuthBaseUrl, getOauthClientId } from './config';

export async function trySilentRefresh() {
  const authUrl = getAuthBaseUrl();
  const clientId = getOauthClientId();
  try {
    const body = new URLSearchParams({
      grant_type: 'refresh_token',
      client_id: clientId,
    });

    await axios.post(`${authUrl}/oauth2/token`, body.toString(), {
      headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      },
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
