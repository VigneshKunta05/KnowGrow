
package com.knowgrow.knowgrow.skill;

public record SkillGapResponse(
        Long id,
        Long employeeId,
        String employeeName,
        String skillName,
        int currentLevel,
        int requiredLevel,
        int gap
) {}
