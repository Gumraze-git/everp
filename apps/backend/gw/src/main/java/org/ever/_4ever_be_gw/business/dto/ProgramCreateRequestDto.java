package org.ever._4ever_be_gw.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProgramCreateRequestDto {

    @NotBlank(message = "프로그램 이름은 필수입니다")
    @Size(max = 100, message = "프로그램 이름은 최대 100자까지 입력 가능합니다")
    private String programName;

    @Pattern(regexp = "^(BASIC_TRAINING|TECHNICAL_TRAINING|SOFT_SKILL_TRAINING)$", 
             message = "카테고리는 BASIC_TRAINING, TECHNICAL_TRAINING, SOFT_SKILL_TRAINING 중 하나여야 합니다")
    private String category;

    @NotNull(message = "교육 시간은 필수입니다")
    private Integer trainingHour;

    @NotNull(message = "온라인 여부는 필수입니다")
    private Boolean isOnline;

    @NotNull(message = "시작일은 필수입니다")
    private LocalDate startDate;

    @NotNull(message = "정원은 필수입니다")
    private Integer capacity;

    private List<Long> requiredDepartments;

    private List<Long> requiredPositions;

    @Size(max = 500, message = "교육 설명은 최대 500자까지 입력 가능합니다")
    private String description;

}
