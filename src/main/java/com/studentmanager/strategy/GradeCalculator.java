package com.studentmanager.strategy;

import com.studentmanager.model.Grade;
import java.util.List;

/**
 * Context class that uses different grade calculation strategies
 */
public class GradeCalculator {
    private GradeCalculationStrategy strategy;
    
    public GradeCalculator() {
        // Default strategy
        this.strategy = new AverageGradeStrategy();
    }
    
    public GradeCalculator(GradeCalculationStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(GradeCalculationStrategy strategy) {
        this.strategy = strategy;
    }
    
    public String calculateGrade(List<Grade> grades) {
        return strategy.calculateGrade(grades);
    }
    
    public String getStrategyName() {
        return strategy.getStrategyName();
    }
}