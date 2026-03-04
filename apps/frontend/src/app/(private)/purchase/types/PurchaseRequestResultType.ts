// 주문 자재 목록
interface PurchaseItem {
  name: string; // 품목명
  quantity: string; // 수량
  unit: string; // 단위
  price: string; // 단가
}
export interface PurchaseRequestResult {
  id: string; // 요청번호
  requester: string; // 요청자
  department: string; // 부서
  requestDate: string; // 요청일
  dueDate: string; // 희망 납기일
  totalAmount: string; // 총 금액
  status: 'approved' | 'pending' | 'waiting' | 'rejected'; // 상태
  items: PurchaseItem[]; // 주문 자재 목록
}
