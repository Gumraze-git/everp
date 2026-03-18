package org.ever._4ever_be_business.config;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 애플리케이션 시작 시 필요한 초기 데이터를 생성하는 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class DataPreloader {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataPreloader.class);

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final TrainingRepository trainingRepository;

    @PostConstruct
    @Transactional
    public void preloadData() {
        log.info("========================================");
        log.info("초기 데이터 로딩 시작");
        log.info("========================================");

        loadDepartments();
        loadPositions();
        loadTrainings();

        log.info("========================================");
        log.info("초기 데이터 로딩 완료");
        log.info("========================================");
    }

    /**
     * 부서 데이터 생성
     */
    private void loadDepartments() {
        if (departmentRepository.count() > 0) {
            log.info("부서 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("부서 데이터 생성 중...");

        Department[] departments = {
                new Department("DEPT-001", "구매", "구매 및 조달 업무", LocalDateTime.now()),
                new Department("DEPT-002", "영업", "영업 및 고객 관리 업무", LocalDateTime.now()),
                new Department("DEPT-003", "재고", "재고 관리 및 물류 업무", LocalDateTime.now()),
                new Department("DEPT-004", "재무", "재무 및 회계 업무", LocalDateTime.now()),
                new Department("DEPT-005", "인적자원", "인사 및 조직 관리 업무", LocalDateTime.now()),
                new Department("DEPT-006", "생산", "생산 및 제조 업무", LocalDateTime.now())
        };

        for (Department dept : departments) {
            departmentRepository.save(dept);
            log.info("부서 생성: {} ({})", dept.getDepartmentName(), dept.getDepartmentCode());
        }

        log.info("총 {}개의 부서 생성 완료", departments.length);
    }

    /**
     * 직급 데이터 생성
     * 각 부서마다 모든 직급(사원~사장)을 생성
     */
    private void loadPositions() {
        if (positionRepository.count() > 0) {
            log.info("직급 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("직급 데이터 생성 중...");

        // 모든 부서 조회
        Department[] departments = {
                departmentRepository.findByDepartmentCode("DEPT-001")
                        .orElseThrow(() -> new RuntimeException("구매 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-002")
                        .orElseThrow(() -> new RuntimeException("영업 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-003")
                        .orElseThrow(() -> new RuntimeException("재고 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-004")
                        .orElseThrow(() -> new RuntimeException("재무 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-005")
                        .orElseThrow(() -> new RuntimeException("인적자원 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-006")
                        .orElseThrow(() -> new RuntimeException("생산 부서를 찾을 수 없습니다."))
        };

        // 직급 정보 (이름, 관리직 여부, 연봉)
        Object[][] positionInfos = {
                {"사원", false, new BigDecimal("3500")},
                {"주임", false, new BigDecimal("4000")},
                {"대리", false, new BigDecimal("4800")},
                {"과장", true, new BigDecimal("5800")},
                {"차장", true, new BigDecimal("7000")},
                {"부장", true, new BigDecimal("8500")},
                {"이사", true, new BigDecimal("10500")},
                {"상무", true, new BigDecimal("13000")},
                {"전무", true, new BigDecimal("16000")},
                {"사장", true, new BigDecimal("20000")}
        };

        int positionCount = 0;

        // 각 부서마다 모든 직급 생성
        for (int deptIdx = 0; deptIdx < departments.length; deptIdx++) {
            Department dept = departments[deptIdx];
            String deptCode = String.format("%03d", deptIdx + 1);

            for (int posIdx = 0; posIdx < positionInfos.length; posIdx++) {
                Object[] info = positionInfos[posIdx];
                String positionName = (String) info[0];
                boolean isManager = (boolean) info[1];
                BigDecimal salary = (BigDecimal) info[2];

                // 직급 코드: POS-{부서번호}{직급번호} (예: POS-001001, POS-001002, ...)
                String positionCode = String.format("POS-%s%02d", deptCode, posIdx + 1);

                Position position = new Position(
                        positionCode,
                        positionName,
                        dept,
                        isManager,
                        salary
                );

                positionRepository.save(position);
                positionCount++;

                log.info("직급 생성: {} ({}) - 부서: {}, 연봉: {}만원, 관리직: {}",
                        position.getPositionName(),
                        position.getPositionCode(),
                        dept.getDepartmentName(),
                        position.getSalary(),
                        position.getIsManager() ? "Y" : "N");
            }
        }

        log.info("총 {}개의 직급 생성 완료 ({}개 부서 × {}개 직급)",
                positionCount, departments.length, positionInfos.length);
    }

    /**
     * 교육 프로그램 데이터 생성
     */
    private void loadTrainings() {
        if (trainingRepository.count() > 0) {
            log.info("교육 프로그램 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("교육 프로그램 데이터 생성 중...");

        Training[] trainings = {
                new Training(
                        "생산 안전 기본 교육",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        16L,
                        "오프라인",
                        18L,
                        25,
                        "생산 라인 안전 및 보호구 착용 교육",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "MES 운영 실무",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        20L,
                        "오프라인",
                        14L,
                        25,
                        "생산 지시, 공정 실적, 이상 리포트 등록 교육",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "외장 사출 품질 관리",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.TECHNICAL_TRAINING,
                        24L,
                        "온라인",
                        12L,
                        20,
                        "사출 외관 불량, 수축, 변형 원인 분석",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "창고 바코드 운영",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        12L,
                        "오프라인",
                        10L,
                        18,
                        "입출고 스캔, 위치 적치, 검품 처리 실습",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "구매 윤리 및 공급사 대응",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        8L,
                        "온라인",
                        9L,
                        30,
                        "구매 협상, 공급사 평가, 윤리 규정 교육",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING
                ),
                new Training(
                        "8D 품질 대응",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.TECHNICAL_TRAINING,
                        18L,
                        "온라인",
                        11L,
                        22,
                        "클레임 접수부터 원인 분석, 재발 방지까지 8D 실습",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.COMPLETED
                )
        };

        for (Training training : trainings) {
            trainingRepository.save(training);
            log.info("교육 프로그램 생성: {} ({}) - {}시간, 신청: {}/{}, 상태: {}",
                    training.getTrainingName(),
                    training.getCategory(),
                    training.getDurationHours(),
                    training.getEnrolled(),
                    training.getCapacity(),
                    training.getTrainingStatus());
        }

        log.info("총 {}개의 교육 프로그램 생성 완료", trainings.length);
    }
}
