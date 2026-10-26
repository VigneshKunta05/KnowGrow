
package com.knowgrow.knowgrow.skill;

import com.knowgrow.knowgrow.employee.Employee;
import com.knowgrow.knowgrow.employee.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/skills")
@CrossOrigin
public class SkillController {

    private final EmployeeSkillRepository skillRepository;
    private final EmployeeRepository employeeRepository;

    public SkillController(
            EmployeeSkillRepository skillRepository,
            EmployeeRepository employeeRepository) {
        this.skillRepository = skillRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping
    public ResponseEntity<?> createSkill(
            @RequestBody SkillRequest request) {

        if (request.employeeId() == null
                || request.skillName() == null
                || request.skillName().isBlank()
                || !validLevel(request.currentLevel())
                || !validLevel(request.requiredLevel())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message",
                    "Provide employeeId, skillName and levels between 1 and 5."
            ));
        }

        Employee employee = employeeRepository
                .findById(request.employeeId())
                .orElse(null);

        if (employee == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Employee not found."
            ));
        }

        String skillName = request.skillName().trim();

        boolean duplicate = skillRepository
                .findAll().stream()
                .anyMatch(s -> s.getEmployee().getId().equals(employee.getId())
                        && s.getSkillName().equalsIgnoreCase(skillName));

        if (duplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "This employee already has an assessment for that skill."
            ));
        }

        EmployeeSkill skill = new EmployeeSkill();
        skill.setEmployee(employee);
        skill.setSkillName(skillName);
        skill.setCurrentLevel(request.currentLevel());
        skill.setRequiredLevel(request.requiredLevel());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(skillRepository.save(skill)));
    }

    @GetMapping
    public List<SkillGapResponse> getAllSkills() {
        return skillRepository.findAllByOrderBySkillNameAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/gaps")
    public List<SkillGapResponse> getSkillGaps() {
        return skillRepository.findAllByOrderBySkillNameAsc()
                .stream()
                .map(this::toResponse)
                .filter(skill -> skill.gap() > 0)
                .toList();
    }

    private boolean validLevel(Integer level) {
        return level != null && level >= 1 && level <= 5;
    }

    private SkillGapResponse toResponse(EmployeeSkill skill) {
        int gap = Math.max(
                0, skill.getRequiredLevel() - skill.getCurrentLevel()
        );

        return new SkillGapResponse(
                skill.getId(),
                skill.getEmployee().getId(),
                skill.getEmployee().getName(),
                skill.getSkillName(),
                skill.getCurrentLevel(),
                skill.getRequiredLevel(),
                gap
        );
    }

    public record SkillRequest(
            Long employeeId,
            String skillName,
            Integer currentLevel,
            Integer requiredLevel
    ) {}
}
