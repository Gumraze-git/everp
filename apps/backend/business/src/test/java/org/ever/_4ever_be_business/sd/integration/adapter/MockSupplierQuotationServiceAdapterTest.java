package org.ever._4ever_be_business.sd.integration.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

class MockSupplierQuotationServiceAdapterTest {

    @Test
    void getQuotationIdsBySupplierCompanyIdReturnsRecentQuotationIdsWithinLimit() {
        QuotationRepository repository = mock(QuotationRepository.class);
        Quotation quotation1 = mock(Quotation.class);
        Quotation quotation2 = mock(Quotation.class);
        Quotation quotation3 = mock(Quotation.class);
        when(quotation1.getId()).thenReturn("QT-001");
        when(quotation2.getId()).thenReturn("QT-002");
        when(quotation3.getId()).thenReturn("QT-003");
        when(repository.findAllByOrderByCreatedAtDesc(any())).thenReturn(new PageImpl<>(List.of(quotation1, quotation2, quotation3)));

        MockSupplierQuotationServiceAdapter adapter = new MockSupplierQuotationServiceAdapter(repository);

        List<String> result = adapter.getQuotationIdsBySupplierCompanyId("SUPP-001", 2);
        List<String> allIds = List.of("QT-001", "QT-002", "QT-003");
        int offset = Math.floorMod(Objects.hashCode("SUPP-001"), allIds.size());
        List<String> expected = List.of(
                allIds.get(offset % allIds.size()),
                allIds.get((offset + 1) % allIds.size())
        );

        assertThat(result).containsExactlyElementsOf(expected);
    }
}
