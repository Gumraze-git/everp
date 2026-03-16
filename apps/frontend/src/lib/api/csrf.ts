import axios from 'axios';
import Cookies from 'js-cookie';
import { API_BASE_URL } from '@/app/types/api';

export const API_XSRF_COOKIE_NAME = 'GW-XSRF-TOKEN';
export const API_XSRF_HEADER_NAME = 'X-GW-XSRF-TOKEN';

let apiCsrfBootstrapPromise: Promise<void> | null = null;

function getApiGatewayBaseUrl() {
  return API_BASE_URL.replace(/\/$/, '');
}

export async function ensureApiCsrfToken(force = false) {
  if (typeof window === 'undefined') {
    return;
  }

  if (!force && Cookies.get(API_XSRF_COOKIE_NAME)) {
    return;
  }

  apiCsrfBootstrapPromise ??= axios
    .get(`${getApiGatewayBaseUrl()}/csrf`, {
      withCredentials: true,
    })
    .then(() => undefined)
    .finally(() => {
      apiCsrfBootstrapPromise = null;
    });

  await apiCsrfBootstrapPromise;

  if (!Cookies.get(API_XSRF_COOKIE_NAME)) {
    throw new Error('missing_gw_xsrf_token');
  }
}

export function getApiCsrfHeaderValue() {
  return Cookies.get(API_XSRF_COOKIE_NAME);
}
