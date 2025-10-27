package com.studentmanager.dao;

import com.studentmanager.model.Grade;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Grade operations
 */
public class GradeDAO {
    
    /**
     * Add a new grade to the database
     * @param grade Grade to add
     * @return true if successful, false otherwise
     */
    public boolean addGrade(Grade grade) {
        String sql = "INSERT INTO grades (student_id, subject, score, coefficient, semester, year) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, grade.getStudentId());
            pstmt.setString(2, grade.getSubject());
            pstmt.setDouble(3, grade.getScore());
            pstmt.setDouble(4, grade.getCoefficient());
            pstmt.setString(5, grade.getSemester());
            pstmt.setInt(6, grade.getYear());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // SQLite doesn't support getGeneratedKeys properly, so we'll query for the last inserted ID
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        grade.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding grade: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all grades for a specific student
     * @param studentId Student ID
     * @return List of grades for the student
     */
    public List<Grade> getGradesByStudentId(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? ORDER BY year DESC, semester, subject";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(createGradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting grades: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }
    
    /**
     * Get grades by student ID and semester
     * @param studentId Student ID
     * @param semester Semester
     * @param year Year
     * @return List of grades for the student in specified semester
     */
    public List<Grade> getGradesByStudentAndSemester(int studentId, String semester, int year) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades WHERE student_id = ? AND semester = ? AND year = ? ORDER BY subject";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            pstmt.setString(2, semester);
            pstmt.setInt(3, year);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                grades.add(createGradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting grades by semester: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }
    
    /**
     * Get grade by ID
     * @param id Grade ID
     * @return Grade object or null if not found
     */
    public Grade getGradeById(int id) {
        String sql = "SELECT * FROM grades WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createGradeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting grade: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Update grade information
     * @param grade Grade with updated information
     * @return true if successful, false otherwise
     */
    public boolean updateGrade(Grade grade) {
        String sql = "UPDATE grades SET subject = ?, score = ?, coefficient = ?, semester = ?, year = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, grade.getSubject());
            pstmt.setDouble(2, grade.getScore());
            pstmt.setDouble(3, grade.getCoefficient());
            pstmt.setString(4, grade.getSemester());
            pstmt.setInt(5, grade.getYear());
            pstmt.setInt(6, grade.getId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating grade: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete grade by ID
     * @param id Grade ID to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteGrade(int id) {
        String sql = "DELETE FROM grades WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting grade: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get all grades for all students
     * @return List of all grades
     */
    public List<Grade> getAllGrades() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT * FROM grades ORDER BY student_id, year DESC, semester, subject";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                grades.add(createGradeFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all grades: " + e.getMessage());
            e.printStackTrace();
        }
        return grades;
    }
    
    /**
     * Get distinct semesters for a student
     * @param studentId Student ID
     * @return List of semesters
     */
    public List<String> getDistinctSemestersForStudent(int studentId) {
        List<String> semesters = new ArrayList<>();
        String sql = "SELECT DISTINCT semester, year FROM grades WHERE student_id = ? ORDER BY year DESC, semester";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                semesters.add(rs.getString("semester") + " " + rs.getInt("year"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting semesters: " + e.getMessage());
            e.printStackTrace();
        }
        return semesters;
    }
    
    /**
     * Create Grade object from ResultSet
     * @param rs ResultSet from database query
     * @return Grade object
     * @throws SQLException
     */
    private Grade createGradeFromResultSet(ResultSet rs) throws SQLException {
        return new Grade(
            rs.getInt("id"),
            rs.getInt("student_id"),
            rs.getString("subject"),
            rs.getDouble("score"),
            rs.getDouble("coefficient"),
            rs.getString("semester"),
            rs.getInt("year")
        );
    }
}