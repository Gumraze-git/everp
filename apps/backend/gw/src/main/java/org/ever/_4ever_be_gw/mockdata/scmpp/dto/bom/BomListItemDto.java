package org.ever._4ever_be_gw.mockdata.scmpp.dto.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomListItemDto {
    private String bomId;
    private String bomNumber;
    private String itemId;
    private String itemCode;
    private String itemName;
    private String version;
    private String status;
    private LocalDateTime lastModifiedAt;
}
