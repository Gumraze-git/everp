package org.ever._4ever_be_business.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CodeGeneratorTest {

    @Test
    @DisplayName("고객사 코드 생성 - 형식 검증 (CUS-XXXXXX)")
    void testGenerateCustomerCode_Format() {
        // given & when
        String customerCode = CodeGenerator.generateCustomerCode();

        // then
        assertNotNull(customerCode);
        assertTrue(customerCode.startsWith("CUS-"), "고객사 코드는 'CUS-'로 시작해야 합니다");
        assertEquals(10, customerCode.length(), "고객사 코드 길이는 10자여야 합니다 (CUS-XXXXXX)");

        // 하이픈 뒤 6자리 검증
        String suffix = customerCode.substring(4);
        assertEquals(6, suffix.length(), "코드 접미사는 6자리여야 합니다");
        assertTrue(suffix.matches("[0-9A-F]{6}"), "코드 접미사는 16진수 대문자여야 합니다");
    }

    @Test
    @DisplayName("고객사 코드 생성 - 유일성 검증 (100번 생성)")
    void testGenerateCustomerCode_Uniqueness() {
        // given
        Set<String> generatedCodes = new HashSet<>();
        int generateCount = 100;

        // when
        for (int i = 0; i < generateCount; i++) {
            String code = CodeGenerator.generateCustomerCode();
            generatedCodes.add(code);
        }

        // then
        assertEquals(generateCount, generatedCodes.size(),
                "생성된 모든 코드는 고유해야 합니다");
    }

    @Test
    @DisplayName("다양한 엔티티 코드 생성 검증")
    void testGenerateVariousEntityCodes() {
        // when
        String customerCode = CodeGenerator.generateCustomerCode();
        String orderCode = CodeGenerator.generateOrderCode();
        String productCode = CodeGenerator.generateProductCode();
        String quotationCode = CodeGenerator.generateQuotationCode();
        String inventoryCode = CodeGenerator.generateInventoryCode();
        String vendorCode = CodeGenerator.generateVendorCode();
        String warehouseCode = CodeGenerator.generateWarehouseCode();
        String shipmentCode = CodeGenerator.generateShipmentCode();
        String paymentCode = CodeGenerator.generatePaymentCode();
        String returnCode = CodeGenerator.generateReturnCode();

        // then
        assertTrue(customerCode.startsWith("CUS-"));
        assertTrue(orderCode.startsWith("ORD-"));
        assertTrue(productCode.startsWith("PRD-"));
        assertTrue(quotationCode.startsWith("QUO-"));
        assertTrue(inventoryCode.startsWith("INV-"));
        assertTrue(vendorCode.startsWith("VEN-"));
        assertTrue(warehouseCode.startsWith("WH-"));
        assertTrue(shipmentCode.startsWith("SHP-"));
        assertTrue(paymentCode.startsWith("PAY-"));
        assertTrue(returnCode.startsWith("RTN-"));

        // 모두 다른 코드여야 함
        Set<String> codes = Set.of(
                customerCode, orderCode, productCode, quotationCode, inventoryCode,
                vendorCode, warehouseCode, shipmentCode, paymentCode, returnCode
        );
        assertEquals(10, codes.size(), "모든 코드는 서로 달라야 합니다");
    }

    @Test
    @DisplayName("커스텀 프리픽스로 코드 생성")
    void testGenerateCode_CustomPrefix() {
        // given
        String prefix = "TEST";

        // when
        String code = CodeGenerator.generateCode(prefix);

        // then
        assertNotNull(code);
        assertTrue(code.startsWith("TEST-"));
        // TEST-XXXXXX = 4(TEST) + 1(-) + 6(XXXXXX) = 11자
        assertEquals(11, code.length(), "커스텀 코드 길이는 PREFIX-XXXXXX 형식이어야 합니다");

        // 하이픈 뒤 6자리 검증
        String suffix = code.substring(prefix.length() + 1);
        assertEquals(6, suffix.length());
        assertTrue(suffix.matches("[0-9A-F]{6}"));
    }

    @Test
    @DisplayName("UUID v7 기반 시간 순서 테스트 (생성 순서 보장)")
    void testGenerateCode_TimeOrdering() throws InterruptedException {
        // given & when
        String code1 = CodeGenerator.generateCustomerCode();
        Thread.sleep(2); // 2ms 대기로 타임스탬프 차이 보장
        String code2 = CodeGenerator.generateCustomerCode();
        Thread.sleep(2);
        String code3 = CodeGenerator.generateCustomerCode();

        // then
        assertNotEquals(code1, code2);
        assertNotEquals(code2, code3);
        assertNotEquals(code1, code3);

        System.out.println("Generated codes in order:");
        System.out.println("Code 1: " + code1);
        System.out.println("Code 2: " + code2);
        System.out.println("Code 3: " + code3);
    }
}
