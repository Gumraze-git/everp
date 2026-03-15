import axios from 'axios';
import Cookies from 'js-cookie';
import { getAuthBaseUrl } from './config';

const XSRF_COOKIE_NAME = 'XSRF-TOKEN';
const XSRF_HEADER_NAME = 'X-XSRF-TOKEN';

export async function ensureAuthCsrfToken() {
  const authUrl = getAuthBaseUrl();

  await axios.get(`${authUrl}/csrf`, {
    withCredentials: true,
  });
}

export async function buildAuthXsrfHeaders() {
  await ensureAuthCsrfToken();

  const csrfToken = Cookies.get(XSRF_COOKIE_NAME);
  if (!csrfToken) {
    throw new Error('missing_xsrf_token');
  }

  return {
    'Content-Type': 'application/x-www-form-urlencoded',
    [XSRF_HEADER_NAME]: csrfToken,
  };
}
