
package com.knowgrow.knowgrow.skill;

import com.knowgrow.knowgrow.employee.Employee;
import jakarta.persistence.*;

@Entity
@Table(
        name = "employee_skills",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employee_id", "skill_name"}
        )
)
public class EmployeeSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    @Column(nullable = false)
    private Integer currentLevel;

    @Column(nullable = false)
    private Integer requiredLevel;

    public EmployeeSkill() {}

    public Long getId() { return id; }
    public Employee getEmployee() { return employee; }
    public String getSkillName() { return skillName; }
    public Integer getCurrentLevel() { return currentLevel; }
    public Integer getRequiredLevel() { return requiredLevel; }

    public void setId(Long id) { this.id = id; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }
    public void setRequiredLevel(Integer requiredLevel) { this.requiredLevel = requiredLevel; }
}
