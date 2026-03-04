'use client';

interface ErrorMessageProps {
  message?: string;
}

export default function ErrorMessage({ message }: ErrorMessageProps) {
  return (
    <div className="p-4 bg-red-50 text-red-500 border border-red-200 rounded-md my-4">
      <h3 className="font-semibold">❌ 오류가 발생했습니다</h3>
      <p className="mt-2 text-sm">{message ?? '알 수 없는 오류가 발생했습니다.'}</p>
    </div>
  );
}
