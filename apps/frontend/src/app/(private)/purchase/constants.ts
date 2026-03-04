import { Tab } from '@/app/types/NavigationType';
import { TableHeader } from '@/app/(private)/purchase/types/TableHeader';
import PurchaseRequestListTab from '@/app/(private)/purchase/components/tabs/PurchaseRequestListTab';
import PurchaseOrderListTab from '@/app/(private)/purchase/components/tabs/PurchaseOrderListTab';
import SupplierListTab from '@/app/(private)/purchase/components/tabs/SupplierListTab';
import { KeyValueItem } from '@/app/types/CommonType';
import { Period } from '@/app/types/StatType';

export type SupplierCategory = 'ALL' | 'MATERIAL' | 'PARTS' | 'ETC';
export type SupplierStatus = 'ALL' | 'ACTIVE' | 'INACTIVE';
export type PurchaseOrderStatus =
  | 'ALL'
  | 'APPROVAL'
  | 'PENDING'
  | 'REJECTED'
  | 'DELIVERING'
  | 'DELIVERED';
export type PurchaseReqStatus = 'ALL' | 'APPROVED' | 'PENDING' | 'REJECTED';

// 구매 기간 필터링
export const STAT_PERIODS: KeyValueItem<Period>[] = [
  { key: 'week', value: '이번 주' },
  { key: 'month', value: '이번 달' },
  { key: 'quarter', value: '이번 분기' },
  { key: 'year', value: '연도별' },
];

// 구매 상태 필터링
export const PURCHASE_REQ_STATUS: KeyValueItem<PurchaseReqStatus>[] = [
  { key: 'ALL', value: '전체' },
  { key: 'APPROVED', value: '승인' },
  { key: 'PENDING', value: '대기' },
  { key: 'REJECTED', value: '반려' },
];

// 발주 상태 필터링
// export const PURCHASE_ORDER_STATUS: KeyValueItem<PurchaseOrderStatus>[] = [
//   { key: 'ALL', value: '전체' },
//   { key: 'APPROVAL', value: '승인' },
//   { key: 'PENDING', value: '대기' },
//   { key: 'REJECTED', value: '반려' },
//   { key: 'DELIVERING', value: '배송중' },
//   { key: 'DELIVERED', value: '배송완료' },
// ];

// 공급 카테고리 필터링
export const SUPPLIER_CATEGORY_ITEMS: KeyValueItem<SupplierCategory>[] = [
  { key: 'ALL', value: '전체' },
  { key: 'MATERIAL', value: '원자재' },
  { key: 'PARTS', value: '부품' },
  { key: 'ETC', value: '기타' },
];

// 공급업체 상태 필터링
export const SUPPLIER_STATUS_ITEMS: KeyValueItem<SupplierStatus>[] = [
  { key: 'ALL', value: '전체' },
  { key: 'ACTIVE', value: '활성' },
  { key: 'INACTIVE', value: '비활성' },
];

// 구매 관리 탭 전환
export const PURCHASE_TABS: Tab[] = [
  {
    id: 'requests',
    name: '구매 요청',
    icon: 'ri-file-add-line',
    component: PurchaseRequestListTab,
  },
  {
    id: 'orders',
    name: '발주서',
    icon: 'ri-shopping-bag-3-line',
    component: PurchaseOrderListTab,
  },
  {
    id: 'suppliers',
    name: '공급업체 관리',
    icon: 'ri-building-line',
    component: SupplierListTab,
  },
];

export const SUPPLIER_PURCHASE_TABS: Tab[] = [
  {
    id: 'orders',
    name: '발주서',
    icon: 'ri-shopping-bag-3-line',
    component: PurchaseOrderListTab,
  },
];

// 공급사
export const SUPPLIERS = [
  '대한철강',
  '알루텍',
  '스테인리스코리아',
  '용접재료상사',
  '패스너코리아',
  '케미칼솔루션',
  '구리산업',
] as const;

// 구매 요청 목록 테이블 헤더
export const PURCHASE_LIST_TABLE_HEADERS = [
  '요청번호',
  '요청자',
  '부서',
  '요청일',
  '총 금액',
  '상태',
  '작업',
] as const;

// 구매 요청 모달 입력 테이블 헤더
export const PURCHASE_REQUEST_TABLE_HEADERS = [
  '품목명',
  '수량',
  '단위',
  '예상 단가',
  '예상 총액',
  '희망 공급업체',
  '희망 납기일',
  '사용 목적',
  '비고',
  '',
] as const;

// 구매 상세 정보 모달 테이블 헤더
export const PURCHASE_ITEM_TABLE_HEADERS = [
  '품목명',
  '수량',
  '단위',
  '단가',
  '금액',
  '납기일',
] as const;

// 발주서 상세 정보 주문 품목 테이블 헤더
export const ORDER_ITEM_TABLE_HEADERS: readonly TableHeader[] = [
  { label: '품목명' },
  { label: '규격/사양' },
  { label: '수량' },
  { label: '단위' },
  { label: '단가' },
  { label: '금액' },
  { label: '비고' },
  { label: '작업' },
] as const;
