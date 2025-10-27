package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Strategy to classify grade into performance categories
 */
public class ClassificationStrategy implements GradeCalculationStrategy {
    
    @Override
    public String calculateGrade(List<Grade> grades) {
        if (grades == null || grades.isEmpty()) {
            return "Không xếp loại";
        }
        
        // First calculate the average
        AverageGradeStrategy avgStrategy = new AverageGradeStrategy();
        String avgString = avgStrategy.calculateGrade(grades);
        double average = Double.parseDouble(avgString);
        
        // Classify based on average
        return classifyGrade(average);
    }
    
    private String classifyGrade(double score) {
        if (score >= 8.5) {
            return "Xuất sắc";
        } else if (score >= 7.0) {
            return "Giỏi";
        } else if (score >= 5.5) {
            return "Khá";
        } else if (score >= 4.0) {
            return "Trung bình";
        } else {
            return "Yếu";
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Xếp Loại";
    }
}