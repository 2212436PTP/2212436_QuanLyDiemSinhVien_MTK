package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Strategy to calculate average numeric grade
 */
public class AverageGradeStrategy implements GradeCalculationStrategy {
    
    @Override
    public String calculateGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return "0.0";
        }
        
        double totalWeightedScore = 0.0;
        double totalCoefficient = 0.0;
        
        for (Grade grade : grades) {
            totalWeightedScore += grade.getWeightedScore();
            totalCoefficient += grade.getCoefficient();
        }
        
        if (totalCoefficient == 0) {
            return "0.0";
        }
        
        double average = totalWeightedScore / totalCoefficient;
        return String.format("%.2f", average);
    }
    
    @Override
    public String getStrategyName() {
        return "Điểm Trung Bình";
    }
}