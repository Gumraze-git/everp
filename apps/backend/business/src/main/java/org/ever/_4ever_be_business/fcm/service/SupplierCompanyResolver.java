package org.ever._4ever_be_business.fcm.service;

public interface SupplierCompanyResolver {
    SupplierCompanyInfo resolve(String supplierUserId);

    record SupplierCompanyInfo(String supplierCompanyId, String supplierCompanyName) {}
}
