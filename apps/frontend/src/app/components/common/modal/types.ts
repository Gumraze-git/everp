/**
 * 모든 모달이 기본적으로 가져야 할 속성 정의
 */
export interface ModalProps {
  id?: string; // 모달의 고유 식별자 (자동 생성됨)
  title?: string;
  width?: string;
  height?: string;
  onClose: () => void; // 닫기 이벤트 함수 (필수)
}

/**
 * 실제로 관리될 모달 스택 데이터 구조 단순화
 * 모든 모달 아이템은 동일한 기본 ModalProps를 갖는 것으로 간주합니다.
 */
export interface ModalItem {
  id: string;
  // Component 타입은 ModalProps만 받는 컴포넌트로 고정
  Component: React.ComponentType<ModalProps>;
  // props 타입도 ModalProps로 고정
  props: ModalProps;
}
