package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Strategy to calculate GPA on 4.0 scale
 */
public class GPACalculationStrategy implements GradeCalculationStrategy {
    
    @Override
    public String calculateGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return "0.00";
        }
        
        double totalGradePoints = 0.0;
        double totalCreditHours = 0.0;
        
        for (Grade grade : grades) {
            double gradePoint = convertToGradePoint(grade.getScore());
            totalGradePoints += gradePoint * grade.getCoefficient();
            totalCreditHours += grade.getCoefficient();
        }
        
        if (totalCreditHours == 0) {
            return "0.00";
        }
        
        double gpa = totalGradePoints / totalCreditHours;
        return String.format("%.2f", gpa);
    }
    
    /**
     * Convert Vietnamese 10-point scale to 4.0 GPA scale
     */
    private double convertToGradePoint(double score) {
        if (score >= 8.5) {
            return 4.0; // A
        } else if (score >= 7.0) {
            return 3.0; // B
        } else if (score >= 5.5) {
            return 2.0; // C
        } else if (score >= 4.0) {
            return 1.0; // D
        } else {
            return 0.0; // F
        }
    }
    
    @Override
    public String getStrategyName() {
        return "GPA (4.0 Scale)";
    }
}