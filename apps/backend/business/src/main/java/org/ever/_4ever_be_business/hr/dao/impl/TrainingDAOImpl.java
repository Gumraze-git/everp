package org.ever._4ever_be_business.hr.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.dao.TrainingDAO;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeTrainingRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TrainingDAOImpl implements TrainingDAO {

    private final TrainingRepository trainingRepository;
    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<Training> findTrainingById(String trainingId) {
        return trainingRepository.findById(trainingId);
    }

    @Override
    public List<EmployeeTraining> findEmployeeTrainingsByTrainingId(String trainingId) {
        return employeeTrainingRepository.findByTrainingIdWithAllRelations(trainingId);
    }

    @Override
    public Page<Training> searchTrainingPrograms(TrainingSearchConditionVo condition, Pageable pageable) {
        return trainingRepository.searchTrainingPrograms(condition, pageable);
    }

    @Override
    public Optional<Employee> findEmployeeByIdWithDetails(String employeeId) {
        return employeeRepository.findById(employeeId);
    }

    @Override
    public List<EmployeeTraining> findEmployeeTrainingsByEmployeeId(String employeeId) {
        return employeeTrainingRepository.findByEmployeeIdWithTraining(employeeId);
    }

    @Override
    public Page<Employee> searchEmployeesWithTrainingInfo(EmployeeTrainingSearchConditionVo condition, Pageable pageable) {
        return employeeRepository.searchEmployeesWithTrainingInfo(condition, pageable);
    }
}
