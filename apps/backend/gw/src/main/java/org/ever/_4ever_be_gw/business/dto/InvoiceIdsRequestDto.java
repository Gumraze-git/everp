package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class InvoiceIdsRequestDto {

    @NotEmpty(message = "invoiceIds는 비어있을 수 없습니다")
    private List<String> invoiceIds;
}
