package org.ever._4ever_be_business.fcm.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateARInvoiceDto {

    @JsonProperty("status")
    private String status;

    @JsonProperty("dueDate")
    private String dueDate;

    @JsonProperty("memo")
    private String memo;
}
