'use client';

type PaginationProps = {
  currentPage: number;
  totalPages: number;
  totalElements?: number;
  onPageChange: (page: number) => void;
  maxVisible?: number;
};

const Pagination = ({
  currentPage,
  totalPages,
  totalElements,
  onPageChange,
  maxVisible = 5,
}: PaginationProps) => {
  const getPageRange = (
    currentPage: number,
    totalPages: number,
    maxVisible: number,
  ): (number | string)[] => {
    const pages: (number | string)[] = [];

    if (totalPages <= maxVisible) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      const start = Math.max(1, currentPage - 2);
      const end = Math.min(totalPages, start + maxVisible - 1);

      if (start > 1) {
        pages.push(1);
        if (start > 2) pages.push('...');
      }

      for (let i = start; i <= end; i++) pages.push(i);

      if (end < totalPages) {
        if (end < totalPages - 1) pages.push('...');
        pages.push(totalPages);
      }
    }

    return pages;
  };

  const pageRange = getPageRange(currentPage, totalPages, maxVisible);

  return (
    <div className="px-6 py-4 border-t border-gray-200">
      <div className="flex items-center justify-between">
        <div className="text-sm text-gray-700">
          총 <span className="font-medium">{totalElements ?? 0}</span>개의 항목
        </div>

        <div className="flex justify-center items-center space-x-2 mt-6">
          {/* 이전 버튼 */}
          <button
            disabled={currentPage === 1}
            onClick={() => onPageChange(currentPage - 1)}
            className={`px-3 py-1 border rounded-md text-sm transition-colors ${
              currentPage === 1
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed border-gray-200'
                : 'border-gray-300 text-gray-700 hover:bg-gray-50 cursor-pointer'
            }`}
          >
            이전
          </button>

          {/* 페이지 번호 */}
          {pageRange.map((p, index) =>
            p === '...' ? (
              <span key={index} className="px-2 text-gray-400">
                ...
              </span>
            ) : (
              <button
                key={index}
                onClick={() => onPageChange(p as number)}
                className={`px-3 py-1 rounded-md text-sm ${
                  currentPage === p
                    ? 'bg-blue-600 text-white'
                    : 'border border-gray-300 text-gray-700 hover:bg-gray-50 cursor-pointer'
                }`}
              >
                {p}
              </button>
            ),
          )}

          {/* 다음 버튼 */}
          <button
            disabled={currentPage === totalPages}
            onClick={() => onPageChange(currentPage + 1)}
            className={`px-3 py-1 border rounded-md text-sm transition-colors ${
              currentPage === totalPages
                ? 'bg-gray-100 text-gray-400 cursor-not-allowed border-gray-200'
                : 'border-gray-300 text-gray-700 hover:bg-gray-50 cursor-pointer'
            }`}
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
};

export default Pagination;
