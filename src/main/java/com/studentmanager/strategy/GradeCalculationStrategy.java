package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Strategy interface for calculating grades
 */
public interface GradeCalculationStrategy {
    /**
     * Calculate result based on the list of grades
     * @param grades List of grades
     * @return Calculated result as String
     */
    String calculateGrade(List<Grade> grades);
    
    /**
     * Get the name of this calculation strategy
     * @return Strategy name
     */
    String getStrategyName();
}