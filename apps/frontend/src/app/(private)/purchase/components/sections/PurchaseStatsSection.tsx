// 'use client';

// import { useQuery } from '@tanstack/react-query';

// import { fetchPurchaseStats } from '@/app/(private)/purchase/api/purchase.api';
// import { mapPurchaseStatsToCards } from '@/app/(private)/purchase/services/purchase.service';
// import StatSection from '@/app/components/common/StatSection';

// export default function PurchaseStatsSection() {
//   const { data, isLoading, isError } = useQuery({
//     queryKey: ['purchase-stats'],
//     queryFn: fetchPurchaseStats,
//   });

//   if (isLoading) return <p>불러오는 중...</p>;
//   if (isError || !data) return <p>데이터를 불러오지 못했습니다.</p>;

//   const statsData = mapPurchaseStatsToCards(data);

//   return (
//     <div className="space-y-4">
//       <StatSection
//         title="구매 및 조달 관리"
//         subTitle="구매 요청부터 발주까지 전체 프로세스 관리"
//         statsData={statsData}
//       />
//     </div>
//   );
// }
