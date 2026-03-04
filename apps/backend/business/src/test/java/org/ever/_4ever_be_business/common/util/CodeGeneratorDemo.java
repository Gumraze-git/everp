package org.ever._4ever_be_business.common.util;

/**
 * CodeGenerator 데모 클래스
 * 실제 생성되는 코드 예시를 출력합니다.
 */
public class CodeGeneratorDemo {
    public static void main(String[] args) {
        System.out.println("=== CodeGenerator 생성 예시 ===\n");

        System.out.println("1. 고객사 코드 (Customer Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateCustomerCode());
        }

        System.out.println("\n2. 주문 코드 (Order Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateOrderCode());
        }

        System.out.println("\n3. 제품 코드 (Product Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateProductCode());
        }

        System.out.println("\n4. 견적 코드 (Quotation Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateQuotationCode());
        }

        System.out.println("\n5. 재고 코드 (Inventory Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateInventoryCode());
        }

        System.out.println("\n6. 공급업체 코드 (Vendor Code):");
        for (int i = 0; i < 5; i++) {
            System.out.println("   " + CodeGenerator.generateVendorCode());
        }

        System.out.println("\n7. 창고 코드 (Warehouse Code):");
        for (int i = 0; i < 3; i++) {
            System.out.println("   " + CodeGenerator.generateWarehouseCode());
        }

        System.out.println("\n8. 배송 코드 (Shipment Code):");
        for (int i = 0; i < 3; i++) {
            System.out.println("   " + CodeGenerator.generateShipmentCode());
        }

        System.out.println("\n9. 결제 코드 (Payment Code):");
        for (int i = 0; i < 3; i++) {
            System.out.println("   " + CodeGenerator.generatePaymentCode());
        }

        System.out.println("\n10. 반품 코드 (Return Code):");
        for (int i = 0; i < 3; i++) {
            System.out.println("   " + CodeGenerator.generateReturnCode());
        }

        System.out.println("\n11. 커스텀 프리픽스:");
        System.out.println("   " + CodeGenerator.generateCode("CUSTOM"));
        System.out.println("   " + CodeGenerator.generateCode("TEST"));
        System.out.println("   " + CodeGenerator.generateCode("DEMO"));

        System.out.println("\n=== 특징 ===");
        System.out.println("- UUID v7 기반으로 시간 순서 보장");
        System.out.println("- 마지막 6자리 16진수 사용 (0-9, A-F)");
        System.out.println("- 중복 가능성 극히 낮음 (16^6 = 16,777,216 조합)");
    }
}
