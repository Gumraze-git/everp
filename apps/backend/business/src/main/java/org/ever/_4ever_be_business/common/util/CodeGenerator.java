package org.ever._4ever_be_business.common.util;

/**
 * 엔티티 코드 생성 유틸리티 클래스
 * UUID v7의 마지막 6자리를 사용하여 고유한 코드를 생성합니다.
 *
 * 특징:
 * - UUID v7 기반으로 시간 순서 보장
 * - 하이픈(-) 제거 후 마지막 6자리 사용
 * - 대문자로 변환하여 일관성 유지
 */
public class CodeGenerator {

    /**
     * UUID v7 기반 코드 생성 (프리픽스 + 6자리)
     *
     * 예시:
     * - generateCode("CUS") -> "CUS-A1B2C3"
     * - generateCode("ORD") -> "ORD-D4E5F6"
     * - generateCode("PRD") -> "PRD-1A2B3C"
     *
     * @param prefix 코드 프리픽스 (예: "CUS", "ORD", "PRD")
     * @return 생성된 코드 (형식: PREFIX-XXXXXX)
     */
    public static String generateCode(String prefix) {
        // UUID v7 생성
        String uuid = UuidV7Generator.generate();

        // 하이픈 제거 (예: 000019a1-a3b9-a7b7-add7-b371db8283f0 -> 000019a1a3b9a7b7add7b371db8283f0)
        String uuidWithoutHyphens = uuid.replace("-", "");

        // 마지막 6자리 추출 (예: 8283f0)
        String last6Chars = uuidWithoutHyphens.substring(uuidWithoutHyphens.length() - 6);

        // 대문자로 변환 (예: 8283f0 -> 8283F0)
        String code = last6Chars.toUpperCase();

        // 프리픽스와 결합 (예: CUS-8283F0)
        return prefix + "-" + code;
    }

    /**
     * 고객사 코드 생성
     *
     * @return CUS-XXXXXX 형식의 코드
     */
    public static String generateCustomerCode() {
        return generateCode("CUS");
    }

    /**
     * 주문 코드 생성
     *
     * @return ORD-XXXXXX 형식의 코드
     */
    public static String generateOrderCode() {
        return generateCode("ORD");
    }

    /**
     * 제품 코드 생성
     *
     * @return PRD-XXXXXX 형식의 코드
     */
    public static String generateProductCode() {
        return generateCode("PRD");
    }

    /**
     * 견적 코드 생성
     *
     * @return QUO-XXXXXX 형식의 코드
     */
    public static String generateQuotationCode() {
        return generateCode("QUO");
    }

    /**
     * 재고 코드 생성
     *
     * @return INV-XXXXXX 형식의 코드
     */
    public static String generateInventoryCode() {
        return generateCode("INV");
    }

    /**
     * 공급업체 코드 생성
     *
     * @return VEN-XXXXXX 형식의 코드
     */
    public static String generateVendorCode() {
        return generateCode("VEN");
    }

    /**
     * 창고 코드 생성
     *
     * @return WH-XXXXXX 형식의 코드
     */
    public static String generateWarehouseCode() {
        return generateCode("WH");
    }

    /**
     * 배송 코드 생성
     *
     * @return SHP-XXXXXX 형식의 코드
     */
    public static String generateShipmentCode() {
        return generateCode("SHP");
    }

    /**
     * 결제 코드 생성
     *
     * @return PAY-XXXXXX 형식의 코드
     */
    public static String generatePaymentCode() {
        return generateCode("PAY");
    }

    /**
     * 반품 코드 생성
     *
     * @return RTN-XXXXXX 형식의 코드
     */
    public static String generateReturnCode() {
        return generateCode("RTN");
    }
}
