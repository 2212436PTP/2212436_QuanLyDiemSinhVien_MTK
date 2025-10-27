package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Strategy to convert numeric grade to letter grade
 */
public class LetterGradeStrategy implements GradeCalculationStrategy {
    
    @Override
    public String calculateGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return "F";
        }
        
        // First calculate the average
        AverageGradeStrategy avgStrategy = new AverageGradeStrategy();
        String avgString = avgStrategy.calculateGrade(grades);
        double average = Double.parseDouble(avgString);
        
        // Convert to letter grade
        return convertToLetterGrade(average);
    }
    
    private String convertToLetterGrade(double score) {
        if (score >= 8.5) {
            return "A";
        } else if (score >= 7.0) {
            return "B";
        } else if (score >= 5.5) {
            return "C";
        } else if (score >= 4.0) {
            return "D";
        } else {
            return "F";
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Điểm Chữ";
    }
}