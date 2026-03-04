package org.ever._4ever_be_gw.business.dto.quotation;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDetailDto {
    private String quotationId;                 // 견적서 Id -> uuid v7
    private String quotationNumber;             // 견적서 번호, ex) QO-______ -> id의 앞 6자리
    private LocalDate quotationDate;            // 견적 날짜, YYYY-MM-DD
    private LocalDate dueDate;                  // 요청 납기 일자
    private String statusCode;                  // 상태 코드
    private String customerName;                // 고객사 이름
    private String ceoName;                     // 고객사의 대표 이름
    private List<QuotationItemDto> items;       // 요청 item 목록
    private long totalAmount;                   // 총액
}
