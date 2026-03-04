import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';
import { MM, SD, IM, FCM, HRM, PP } from '@/lib/role';
const ACCESS_RULES = [
  {
    path: '/dashboard',
    roles: [
      'ALL_ADMIN',
      ...MM,
      ...HRM,
      ...FCM,
      ...IM,
      ...PP,
      ...SD,
      'SUPPLIER_ADMIN',
      'CUSTOMER_ADMIN',
    ],
  },
  { path: '/purchase', roles: ['ALL_ADMIN', ...MM, ...SD, ...IM, ...PP, 'SUPPLIER_ADMIN'] },
  { path: '/sales', roles: ['ALL_ADMIN', ...MM, ...SD, ...IM, ...PP, 'CUSTOMER_ADMIN'] },
  { path: '/inventory', roles: ['ALL_ADMIN', ...MM, ...SD, ...IM, ...PP] },
  { path: '/finance', roles: ['ALL_ADMIN', ...FCM, 'SUPPLIER_ADMIN', 'CUSTOMER_ADMIN'] },
  { path: '/hrm', roles: ['ALL_ADMIN', ...HRM] },
  { path: '/production', roles: ['ALL_ADMIN', ...MM, ...SD, ...IM, ...PP] },
];
export function middleware(req: NextRequest) {
  const { pathname } = req.nextUrl;
  const roleCookie = req.cookies.get('role');
  const rawRole = roleCookie?.value ?? 'GUEST';
  const role = rawRole.trim().toUpperCase();

  const rule = ACCESS_RULES.find((r) => pathname.startsWith(r.path));
  if (rule && roleCookie && !rule.roles.includes(role)) {
    return NextResponse.redirect(new URL('/unauthorized', req.url));
  }

  return NextResponse.next();
}
export const config = {
  matcher: [
    '/dashboard/:path*',
    '/purchase/:path*',
    '/sales/:path*',
    '/inventory/:path*',
    '/finance/:path*',
    '/hrm/:path*',
    '/production/:path*',
  ],
};
