package org.ever._4ever_be_business.hr.enums;

public enum VacationStatus {
    APPLYING,   // 직원이 막 신청서를 작성/제출 중인 상태(임시저장·초안 포함; 제출 직후에도 사용 가능)
    PENDING,    // 결재(승인) 대기: 결재선/관리자 검토 단계
    APPROVED,   // 승인 완료: 휴가 사용 확정
    REJECTED,   // 반려됨: 결재 불가/사유 포함
    CANCELLED   // 취소됨: 신청자가 취소하거나 승인 후 철회
}
