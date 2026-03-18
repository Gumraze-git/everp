package org.ever._4ever_be_scm.scm.pp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.ever._4ever_be_scm.scm.iv.entity.Product;
import org.ever._4ever_be_scm.scm.iv.entity.SupplierCompany;
import org.ever._4ever_be_scm.scm.iv.entity.SupplierUser;
import org.ever._4ever_be_scm.scm.iv.repository.ProductRepository;
import org.ever._4ever_be_scm.scm.iv.repository.ProductStockLogRepository;
import org.ever._4ever_be_scm.scm.iv.repository.SupplierCompanyRepository;
import org.ever._4ever_be_scm.scm.iv.repository.SupplierUserRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderApprovalRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductRequestRepository;
import org.ever._4ever_be_scm.scm.pp.integration.dto.BusinessQuotationListResponseDto;
import org.ever._4ever_be_scm.scm.pp.integration.port.BusinessQuotationServicePort;
import org.ever._4ever_be_scm.scm.pp.repository.MesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private ProductOrderRepository productOrderRepository;

    @Mock
    private ProductRequestRepository productRequestRepository;

    @Mock
    private SupplierUserRepository supplierUserRepository;

    @Mock
    private SupplierCompanyRepository supplierCompanyRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductOrderApprovalRepository productOrderApprovalRepository;

    @Mock
    private ProductStockLogRepository productStockLogRepository;

    @Mock
    private BusinessQuotationServicePort businessQuotationServicePort;

    @Mock
    private MesRepository mesRepository;

    private DashboardServiceImpl dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardServiceImpl(
                productOrderRepository,
                productRequestRepository,
                supplierUserRepository,
                supplierCompanyRepository,
                productRepository,
                productOrderApprovalRepository,
                productStockLogRepository,
                businessQuotationServicePort,
                mesRepository
        );
    }

    @Test
    void purchaseRequestFallbackUsesPrPrefix() {
        when(productRequestRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        var items = dashboardService.getPurchaseRequestsOverall(3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("PR-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).doesNotContain("목업").doesNotContain("MOCK");
        });
    }

    @Test
    void purchaseOrderFallbackUsesPoPrefix() {
        when(productOrderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        var items = dashboardService.getMmPurchaseOrders(3);

        assertThat(items).hasSize(3);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("PO-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("발주").doesNotContain("목업");
        });
    }

    @Test
    void supplierPurchaseOrderFallbackKeepsSupplierTone() {
        SupplierUser supplierUser = SupplierUser.builder()
                .id("supplier-user-id")
                .userId("supplier-user-1")
                .supplierUserName("공급사 담당자")
                .build();
        SupplierCompany supplierCompany = SupplierCompany.builder()
                .id("supplier-company-1")
                .supplierUser(supplierUser)
                .companyCode("SUP-001")
                .companyName("한빛 오토 외장")
                .build();
        Product product = mock(Product.class);

        when(supplierUserRepository.findByUserId("supplier-user-1")).thenReturn(Optional.of(supplierUser));
        when(supplierCompanyRepository.findBySupplierUser(supplierUser)).thenReturn(Optional.of(supplierCompany));
        when(productRepository.findFirstBySupplierCompany_Id("supplier-company-1")).thenReturn(product);
        when(product.getProductName()).thenReturn("전방 범퍼 커버");
        when(productOrderRepository.findBySupplierCompanyNameOrderByCreatedAtDesc("한빛 오토 외장")).thenReturn(List.of());

        var items = dashboardService.getSupplierPurchaseOrders("supplier-user-1", 2);

        assertThat(items).hasSize(2);
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("PO-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("전방 범퍼 커버").contains("발주");
            assertThat(item.getName()).isEqualTo("한빛 오토 외장");
        });
    }

    @Test
    void stockFallbackUsesInboundAndOutboundPrefixes() {
        when(productStockLogRepository.findByMovementTypeOrderByCreatedAtDesc("입고")).thenReturn(List.of());
        when(productStockLogRepository.findByMovementTypeOrderByCreatedAtDesc("출고")).thenReturn(List.of());

        var inboundItems = dashboardService.getInboundDeliveries(2);
        var outboundItems = dashboardService.getOutboundDeliveries(2);

        assertThat(inboundItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("IN-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("입고").doesNotContain("목업");
        });
        assertThat(outboundItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("OUT-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("출고").doesNotContain("목업");
        });
    }

    @Test
    void productionFallbackUsesQuotationAndMesPrefixes() {
        when(businessQuotationServicePort.getQuotationList(anyString(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(new BusinessQuotationListResponseDto(0, List.of(), null));
        when(mesRepository.findByStatusOrderByCreatedAtDesc("IN_PROGRESS")).thenReturn(List.of());

        var quotationItems = dashboardService.getQuotationsToProduction("planner-1", 2);
        var mesItems = dashboardService.getProductionInProgress("planner-1", 2);

        assertThat(quotationItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("QO-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("생산 전환 견적").doesNotContain("목업");
        });
        assertThat(mesItems).allSatisfy(item -> {
            assertThat(item.getItemNumber()).startsWith("MES-").doesNotContain("MOCK");
            assertThat(item.getItemTitle()).contains("생산").doesNotContain("목업");
        });
    }
}
