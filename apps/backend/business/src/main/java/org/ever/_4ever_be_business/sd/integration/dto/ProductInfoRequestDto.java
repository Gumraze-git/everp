package org.ever._4ever_be_business.sd.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoRequestDto {
    private List<String> productIds;
}
