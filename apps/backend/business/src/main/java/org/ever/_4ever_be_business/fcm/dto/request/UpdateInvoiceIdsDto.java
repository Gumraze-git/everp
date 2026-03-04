package org.ever._4ever_be_business.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInvoiceIdsDto {

    @JsonProperty("invoiceIds")
    private List<String> invoiceIds;
}
