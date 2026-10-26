
package com.knowgrow.knowgrow.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeSkillRepository
        extends JpaRepository<EmployeeSkill, Long> {

    List<EmployeeSkill> findAllByOrderBySkillNameAsc();
}
