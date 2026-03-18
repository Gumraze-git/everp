package org.ever._4ever_be_business.fcm.integration.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.junit.jupiter.api.Test;

class MockSupplierCompanyServiceAdapterTest {

    @Test
    void getSupplierCompanyIdByUserIdReusesExistingVoucherCompanyIdsWhenAvailable() {
        PurchaseVoucherRepository repository = mock(PurchaseVoucherRepository.class);
        PurchaseVoucher firstVoucher = mock(PurchaseVoucher.class);
        PurchaseVoucher secondVoucher = mock(PurchaseVoucher.class);
        when(firstVoucher.getSupplierCompanyId()).thenReturn("SUPP-001");
        when(secondVoucher.getSupplierCompanyId()).thenReturn("SUPP-002");
        when(repository.findAll()).thenReturn(List.of(firstVoucher, secondVoucher));

        MockSupplierCompanyServiceAdapter adapter =
                new MockSupplierCompanyServiceAdapter(new MockDataProvider(), repository);

        String result = adapter.getSupplierCompanyIdByUserId("supplier-user-a");
        List<String> availableIds = List.of("SUPP-001", "SUPP-002");
        String expected = availableIds.get(Math.floorMod(Objects.hashCode("supplier-user-a"), availableIds.size()));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getSupplierCompanyIdByUserIdFallsBackToSyntheticIdWhenVoucherDataMissing() {
        PurchaseVoucherRepository repository = mock(PurchaseVoucherRepository.class);
        when(repository.findAll()).thenReturn(List.of());

        MockSupplierCompanyServiceAdapter adapter =
                new MockSupplierCompanyServiceAdapter(new MockDataProvider(), repository);

        String result = adapter.getSupplierCompanyIdByUserId("supplier-user-a");

        assertThat(result).startsWith("supplier-company-");
    }
}
