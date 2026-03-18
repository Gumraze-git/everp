package org.ever._4ever_be_scm.scm.pp.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.pp.entity.Operation;
import org.ever._4ever_be_scm.scm.pp.repository.OperationRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OperationInitializer {
    private final OperationRepository operationRepository;

    private static final List<Operation> OPERATIONS = List.of(
        Operation.builder().id("1").opCode("OP-001").opName("절단").description("판재 및 수지 자재 절단").requiredTime(BigDecimal.valueOf(120)).build(),
        Operation.builder().id("2").opCode("OP-002").opName("사출").description("플라스틱 사출 성형").requiredTime(BigDecimal.valueOf(240)).build(),
        Operation.builder().id("3").opCode("OP-003").opName("가공").description("브래킷 및 금속 부품 가공").requiredTime(BigDecimal.valueOf(170)).build(),
        Operation.builder().id("4").opCode("OP-004").opName("조립").description("서브 어셈블리 및 완제품 조립").requiredTime(BigDecimal.valueOf(180)).build(),
        Operation.builder().id("5").opCode("OP-005").opName("용접").description("금속 체결 및 용접 공정").requiredTime(BigDecimal.valueOf(200)).build(),
        Operation.builder().id("6").opCode("OP-006").opName("도장").description("외장 부품 표면 도장").requiredTime(BigDecimal.valueOf(360)).build(),
        Operation.builder().id("7").opCode("OP-007").opName("검사").description("치수 및 외관 품질 검사").requiredTime(BigDecimal.valueOf(90)).build(),
        Operation.builder().id("8").opCode("OP-008").opName("포장").description("출하 전 포장 공정").requiredTime(BigDecimal.valueOf(120)).build()
    );

    @PostConstruct
    public void initOperations() {
        OPERATIONS.stream()
            .filter(operation -> operationRepository.findById(operation.getId()).isEmpty())
            .forEach(operationRepository::save);
    }
}
