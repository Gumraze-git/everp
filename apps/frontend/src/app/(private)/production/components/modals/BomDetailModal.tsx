import { BomDetailResponse } from '@/app/(private)/production/types/BomDetailApiType';
import { fetchBomDetail } from '../../api/production.api';
import { useQuery } from '@tanstack/react-query';
import { ModalProps } from '@/app/components/common/modal/types';
import BomTreeContainer from '../../BomTreeContainer';
import { formatDateTime } from '@/app/utils/date';

interface BomDetailModalProps extends ModalProps {
  bomId: string;
}

export default function BomDetailModal({ bomId }: BomDetailModalProps) {
  const {
    data: bomDetail,
    isLoading,
    isError,
  } = useQuery<BomDetailResponse>({
    queryKey: ['bomDetail', bomId],
    queryFn: () => fetchBomDetail(bomId),
    enabled: !!bomId,
  });

  // const renderLevelStructure = (levelStructure: BomDetailResponse['levelStructure']) => {
  //   return (
  //     <div className="space-y-4">
  //       {Object.entries(levelStructure).map(([level, items]) => (
  //         <div
  //           key={level}
  //           className="ml-4"
  //           style={{ marginLeft: `${parseInt(level.replace('Level ', '')) * 20}px` }}
  //         >
  //           <div className="text-sm font-medium text-gray-600 mb-2">{level}</div>
  //           {items.map((item) => (
  //             <div
  //               key={item.code}
  //               className="flex items-center space-x-2 p-2 bg-gray-50 rounded mb-1"
  //             >
  //               <i className="ri-arrow-right-s-line text-gray-400"></i>
  //               <span className="font-medium">{item.code}</span>
  //               <span>{item.name}</span>
  //               <span className="text-sm text-gray-500">{item.quantity}</span>
  //             </div>
  //           ))}
  //         </div>
  //       ))}
  //     </div>
  //   );
  // };

  const getStatusBadge = (status: string) => {
    const statusConfig: Record<string, { label: string; class: string }> = {
      ACTIVE: { label: '활성', class: 'bg-green-100 text-green-800' },
      INACTIVE: { label: '비활성', class: 'bg-gray-100 text-gray-800' },
      DRAFT: { label: '초안', class: 'bg-yellow-100 text-yellow-800' },
    };
    const config = statusConfig[status] || { label: status, class: 'bg-gray-100 text-gray-800' };
    return (
      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${config.class}`}>
        {config.label}
      </span>
    );
  };

  return (
    <>
      {isLoading ? (
        <div className="p-6 text-center py-12">
          <i className="ri-loader-4-line animate-spin text-3xl text-gray-400"></i>
          <p className="mt-3 text-gray-500">로딩 중...</p>
        </div>
      ) : isError || !bomDetail ? (
        <div className="p-6 text-center py-12">
          <i className="ri-error-warning-line text-3xl text-red-400"></i>
          <p className="mt-3 text-red-500">데이터를 불러오는데 실패했습니다.</p>
        </div>
      ) : (
        <div className="p-6 space-y-6">
          {/* 제품 기본 정보 */}
          <div>
            <h4 className="text-md font-semibold text-gray-900 mb-4">제품 기본 정보</h4>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="text-sm font-medium text-gray-500">제품명</label>
                <p className="text-sm text-gray-900">{bomDetail.productName}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">제품 코드</label>
                <p className="text-sm text-gray-900">{bomDetail.productNumber}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">BOM 번호</label>
                <p className="text-sm text-gray-900">{bomDetail.bomNumber}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">버전</label>
                <p className="text-sm text-gray-900">{bomDetail.version}</p>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">상태</label>
                <div className="mt-1">{getStatusBadge(bomDetail.statusCode)}</div>
              </div>
              <div>
                <label className="text-sm font-medium text-gray-500">최종 수정일</label>
                <p className="text-sm text-gray-900">{formatDateTime(bomDetail.lastModifiedAt)}</p>
              </div>
            </div>
          </div>

          {/* 구성품 리스트 */}
          <div>
            <h4 className="text-md font-semibold text-gray-900 mb-4">구성품 리스트</h4>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50 text-center">
                  <tr>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">
                      품목 코드
                    </th>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">
                      품목명
                    </th>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">수량</th>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">단위</th>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">레벨</th>
                    <th className="px-4 py-2 text-xs font-medium text-gray-500 uppercase">
                      공급사
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {bomDetail.components.map((comp) => (
                    <tr key={comp.itemId} className="text-center">
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.code}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.name}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.quantity}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.unit}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.level}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{comp.supplierName}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* 레벨 구조 */}
          <div>
            <h4 className="text-md font-semibold text-gray-900 mb-4">BOM 트리 구조</h4>
            <BomTreeContainer bomData={bomDetail.levelStructure} />
          </div>

          {/* 공정 라우팅 정보 */}
          <div>
            <h4 className="text-md font-semibold text-gray-900 mb-4">공정 라우팅 정보</h4>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">
                      순서
                    </th>
                    <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">
                      품목명
                    </th>
                    <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">
                      공정명
                    </th>
                    <th className="px-4 py-2 text-center text-xs font-medium text-gray-500 uppercase">
                      가동시간(분)
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {bomDetail.routing.map((routing) => (
                    <tr key={routing.sequence} className="text-center">
                      <td className="px-4 py-2 text-sm text-gray-900">{routing.sequence}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{routing.itemName}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{routing.operationName}</td>
                      <td className="px-4 py-2 text-sm text-gray-900">{routing.runTime}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
