'use client';

import Link from 'next/link';
import { useRouter } from 'next/navigation';

export default function UnauthorizedPage() {
  const router = useRouter();

  return (
    <div className="min-h-screen bg-gradient-to-br from-white via-slate-50 to-slate-100 flex items-center justify-center px-4 py-12 text-center">
      <div className="max-w-lg w-full rounded-3xl border border-slate-200 bg-white shadow-xl shadow-slate-200/60 p-10 space-y-8">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-red-100 text-red-500 text-3xl">
          <i className="ri-error-warning-line text-3xl text-red-500" aria-hidden="true"></i>
        </div>
        <div className="space-y-3">
          <h1 className="text-3xl font-semibold text-slate-900">접근 권한이 필요합니다</h1>
          <p className="text-slate-600 leading-relaxed">
            요청하신 페이지를 열 수 있는 권한이 없습니다. 권한이 있어야 하는 경우 관리자에게
            요청하거나, 올바른 역할로 로그인했는지 확인하세요.
          </p>
        </div>
        <div className="rounded-2xl border border-slate-200 bg-slate-50 p-6 text-left text-sm text-slate-600 space-y-3">
          <p className="font-medium text-slate-900">다음 항목을 확인해 보세요.</p>
          <ul className="space-y-2">
            <li className="flex items-start gap-3">
              <span className="mt-1 text-red-400">•</span>
              <span>현재 계정이 해당 메뉴에 접근할 수 있는지 확인합니다.</span>
            </li>
            <li className="flex items-start gap-3">
              <span className="mt-1 text-red-400">•</span>
              <span>권한 요청이 필요한 경우 관리자에게 연락하여 역할을 부여받습니다.</span>
            </li>
          </ul>
        </div>
        <div className="flex flex-col sm:flex-row justify-center gap-3 pt-2">
          <button
            type="button"
            onClick={() => router.back()}
            className="w-full sm:w-auto rounded-full border border-slate-200 bg-white px-6 py-3 text-sm font-medium text-slate-600 transition hover:bg-slate-50"
          >
            이전 페이지
          </button>
          <Link
            href="/"
            className="w-full sm:w-auto rounded-full bg-red-500 px-6 py-3 text-sm font-semibold text-white shadow-lg shadow-red-500/30 transition hover:bg-red-600"
          >
            메인으로 이동
          </Link>
        </div>
      </div>
    </div>
  );
}
