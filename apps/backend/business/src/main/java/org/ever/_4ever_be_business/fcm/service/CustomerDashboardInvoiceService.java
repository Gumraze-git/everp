package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;

import java.util.List;

public interface CustomerDashboardInvoiceService {
    List<SupplierPurchaseInvoiceListItemDto> getCustomerInvoices(String userId, int size);
}
