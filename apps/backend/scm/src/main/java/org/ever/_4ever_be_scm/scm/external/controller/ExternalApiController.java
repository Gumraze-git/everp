package org.ever._4ever_be_scm.scm.external.controller;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.external.dto.*;
import org.ever._4ever_be_scm.scm.external.service.ExternalApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scm-pp")
@RequiredArgsConstructor
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    /**
     * 1. ProductOrderId로 ProductOrderItem들 가져오기
     */
    @PostMapping("/product-order-items/search")
    public ResponseEntity<ProductOrderItemResponseDto> getProductOrderItems(
            @RequestBody ProductOrderRequestDto request) {
        ProductOrderItemResponseDto response = externalApiService.getProductOrderItems(request.getProductOrderId());
        return ResponseEntity.ok(response);
    }

    /**
     * 2. ProductOrderId들로 ProductOrder 정보 제공
     */
    @PostMapping("/product-orders/search")
    public ResponseEntity<List<ProductOrderInfoDto>> getProductOrderInfos(
            @RequestBody ProductOrderIdsRequestDto request) {
        List<ProductOrderInfoDto> response = externalApiService.getProductOrderInfos(request.getProductOrderIds());
        return ResponseEntity.ok(response);
    }

    /**
     * 3. ProductId들로 Product 정보들 제공
     */
    @PostMapping("/products/search")
    public ResponseEntity<ProductMultipleResponseDto> getProductsMultiple(
            @RequestBody ProductMultipleRequestDto request) {
        ProductMultipleResponseDto response = externalApiService.getProductsMultiple(request.getProductIds());
        return ResponseEntity.ok(response);
    }

    /**
     * 4. ItemId를 통해 부족재고 파악
     */
    @PostMapping("/inventory-stock-checks")
    public ResponseEntity<StockCheckResponseDto> checkStock(
            @RequestBody StockCheckRequestDto request) {
        StockCheckResponseDto response = externalApiService.checkStock(request.getItems());
        return ResponseEntity.ok(response);
    }

    /**
     * 5. SupplierCompanyId로 SupplierCompany 정보 가져오기(단건)
     */
    @GetMapping("/supplier-companies/{supplierCompanyId}")
    public ResponseEntity<SupplierCompanySingleResponseDto> getSupplierCompanySingle(
            @PathVariable String supplierCompanyId) {
        SupplierCompanySingleResponseDto response =
                externalApiService.getSupplierCompanySingle(supplierCompanyId);
        return ResponseEntity.ok(response);
    }

    /**
     * 6. SupplierCompanyId들로 SupplierCompany 정보 가져오기(다수)
     */
    @PostMapping("/supplier-companies/search")
    public ResponseEntity<SupplierCompanyMultipleResponseDto> getSupplierCompaniesMultiple(
            @RequestBody SupplierCompanyMultipleRequestDto request) {
        SupplierCompanyMultipleResponseDto response = externalApiService.getSupplierCompaniesMultiple(request.getSupplierCompanyIds());
        return ResponseEntity.ok(response);
    }

    /**
     * 7. 카테고리가 ITEM인 Product 목록 반환
     */
    @GetMapping("/product-options")
    public ResponseEntity<ProductMultipleResponseDto> getItemCategoryProducts() {
        ProductMultipleResponseDto response = externalApiService.getItemCategoryProducts();
        return ResponseEntity.ok(response);
    }

    /**
     * 8. supplierUserId로 supplierCompanyId return
     */

    @PostMapping("/supplier-companies/ids/search")
    public ResponseEntity<SupplierCompanyIdDto> getSupplierCompanyId(
            @RequestBody SupplierUserIdDto request) {
        SupplierCompanyIdDto response = externalApiService.getSupplierCompanyId(request);
        return ResponseEntity.ok(response);
    }

}
