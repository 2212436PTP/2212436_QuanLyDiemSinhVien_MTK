package com.studentmanager.util;

import com.studentmanager.model.Student;
import com.studentmanager.model.Grade;
import java.io.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating reports
 */
public class ReportGenerator {
    
    /**
     * Generate transcript report for a student
     * @param student Student object
     * @param grades List of grades
     * @param outputPath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean generateTranscript(Student student, List<Grade> grades, String outputPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath, true))) {
            writer.println("=".repeat(80));
            writer.println("                    BẢNG ĐIỂM SINH VIÊN");
            writer.println("=".repeat(80));
            writer.println();
            writer.println("Thời gian tạo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println();
            writer.println("THÔNG TIN SINH VIÊN:");
            writer.println("- Mã sinh viên: " + student.getStudentId());
            writer.println("- Họ tên: " + student.getFullName());
            writer.println("- Email: " + (student.getEmail() != null ? student.getEmail() : "N/A"));
            writer.println("- Số điện thoại: " + (student.getPhoneNumber() != null ? student.getPhoneNumber() : "N/A"));
            writer.println("- Ngành học: " + (student.getMajor() != null ? student.getMajor() : "N/A"));
            writer.println();
            
            if (grades != null && !grades.isEmpty()) {
                writer.println("CHI TIẾT ĐIỂM:");
                writer.println("-".repeat(80));
                writer.printf("%-25s %-8s %-8s %-12s %-8s%n", "Môn học", "Điểm", "Hệ số", "Học kỳ", "Năm");
                writer.println("-".repeat(80));
                
                for (Grade grade : grades) {
                    writer.printf("%-25s %-8.2f %-8.1f %-12s %-8d%n",
                        grade.getSubject(),
                        grade.getScore(),
                        grade.getCoefficient(),
                        grade.getSemester(),
                        grade.getYear());
                }
                
                writer.println("-".repeat(80));
                writer.println("Tổng số môn: " + grades.size());
            } else {
                writer.println("Chưa có điểm nào được ghi nhận.");
            }
            
            writer.println();
            writer.println("=".repeat(80));
            writer.println();
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating transcript: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate summary report for all students
     * @param students List of students
     * @param outputPath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean generateSummaryReport(List<Student> students, String outputPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("=".repeat(100));
            writer.println("                         BÁO CÁO TỔNG QUAN SINH VIÊN");
            writer.println("=".repeat(100));
            writer.println();
            writer.println("Thời gian tạo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println("Tổng số sinh viên: " + students.size());
            writer.println();
            
            if (!students.isEmpty()) {
                writer.printf("%-8s %-15s %-25s %-25s %-20s %-10s%n", 
                             "STT", "Mã SV", "Họ tên", "Email", "Ngành", "Số môn");
                writer.println("-".repeat(100));
                
                for (int i = 0; i < students.size(); i++) {
                    Student student = students.get(i);
                    writer.printf("%-8d %-15s %-25s %-25s %-20s %-10d%n",
                        i + 1,
                        student.getStudentId(),
                        student.getFullName(),
                        student.getEmail() != null ? student.getEmail() : "N/A",
                        student.getMajor() != null ? student.getMajor() : "N/A",
                        student.getGrades().size());
                }
                
                writer.println("-".repeat(100));
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating summary report: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate grade statistics report
     * @param students List of students with grades
     * @param outputPath Output file path
     * @return true if successful, false otherwise
     */
    public static boolean generateStatisticsReport(List<Student> students, String outputPath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("=".repeat(80));
            writer.println("                    BÁO CÁO THỐNG KÊ ĐIỂM");
            writer.println("=".repeat(80));
            writer.println();
            writer.println("Thời gian tạo: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            writer.println();
            
            int totalStudents = students.size();
            int studentsWithGrades = 0;
            int totalGrades = 0;
            double totalScore = 0.0;
            
            int excellent = 0, good = 0, fair = 0, average = 0, poor = 0;
            
            for (Student student : students) {
                if (!student.getGrades().isEmpty()) {
                    studentsWithGrades++;
                    totalGrades += student.getGrades().size();
                    
                    // Calculate student's average
                    double sum = 0, weightSum = 0;
                    for (Grade grade : student.getGrades()) {
                        sum += grade.getScore() * grade.getCoefficient();
                        weightSum += grade.getCoefficient();
                        totalScore += grade.getScore();
                    }
                    
                    if (weightSum > 0) {
                        double avg = sum / weightSum;
                        if (avg >= 8.5) excellent++;
                        else if (avg >= 7.0) good++;
                        else if (avg >= 5.5) fair++;
                        else if (avg >= 4.0) average++;
                        else poor++;
                    }
                }
            }
            
            writer.println("THỐNG KÊ TỔNG QUAN:");
            writer.println("- Tổng số sinh viên: " + totalStudents);
            writer.println("- Sinh viên có điểm: " + studentsWithGrades);
            writer.println("- Tổng số bài kiểm tra: " + totalGrades);
            if (totalGrades > 0) {
                writer.println("- Điểm trung bình chung: " + String.format("%.2f", totalScore / totalGrades));
            }
            writer.println();
            
            writer.println("THỐNG KÊ THEO XẾP LOẠI:");
            writer.println("- Xuất sắc (≥ 8.5): " + excellent + " sinh viên");
            writer.println("- Giỏi (≥ 7.0): " + good + " sinh viên");
            writer.println("- Khá (≥ 5.5): " + fair + " sinh viên");
            writer.println("- Trung bình (≥ 4.0): " + average + " sinh viên");
            writer.println("- Yếu (< 4.0): " + poor + " sinh viên");
            
            if (studentsWithGrades > 0) {
                writer.println();
                writer.println("TỶ LỆ PHẦN TRĂM:");
                writer.println("- Xuất sắc: " + String.format("%.1f%%", (double)excellent/studentsWithGrades*100));
                writer.println("- Giỏi: " + String.format("%.1f%%", (double)good/studentsWithGrades*100));
                writer.println("- Khá: " + String.format("%.1f%%", (double)fair/studentsWithGrades*100));
                writer.println("- Trung bình: " + String.format("%.1f%%", (double)average/studentsWithGrades*100));
                writer.println("- Yếu: " + String.format("%.1f%%", (double)poor/studentsWithGrades*100));
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Error generating statistics report: " + e.getMessage());
            return false;
        }
    }
}