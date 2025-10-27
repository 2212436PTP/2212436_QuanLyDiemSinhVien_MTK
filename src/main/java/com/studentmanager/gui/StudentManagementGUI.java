package com.studentmanager.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.studentmanager.controller.StudentController;
import com.studentmanager.dao.DatabaseConnection;
import com.studentmanager.model.Student;
import com.studentmanager.model.Grade;
import com.studentmanager.strategy.*;
import com.studentmanager.util.ReportGenerator;

/**
 * Main GUI application for Student Management System
 */
public class StudentManagementGUI extends JFrame {
    private StudentController controller;
    private JTabbedPane tabbedPane;
    
    // Student Management Tab
    private JTable studentsTable;
    private DefaultTableModel studentsTableModel;
    private JTextField txtStudentId, txtFullName, txtEmail, txtPhone;
    private JComboBox<String> cmbMajor;
    
    // Grade Management Tab
    private JTable gradesTable;
    private DefaultTableModel gradesTableModel;
    private JTextField txtScore, txtSemester, txtYear;
    private JComboBox<String> cmbCoefficient;
    private JComboBox<String> cmbSubject;
    private JComboBox<String> cmbStudentSelect;
    
    // Grade Calculation Tab (Strategy Pattern)
    private JComboBox<String> cmbStrategyStudent;
    private JComboBox<String> cmbStrategy;
    private JTextArea txtStrategyResult;
    private JTable strategyTable;
    private DefaultTableModel strategyTableModel;
    
    // Reports Tab
    private JComboBox<String> cmbReportStudent;
    private JTextArea txtReportArea;
    
    public StudentManagementGUI() {
        setTitle("Hệ Thống Quản Lý Điểm Sinh Viên");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize controller and database
        DatabaseConnection.initializeDatabase();
        controller = new StudentController();
        
        // Setup GUI components
        initializeComponents();
        setupTabbedPane();
        
        // Load initial data
        refreshAllData();
        
        // Set icon if available
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        setVisible(true);
    }
    
    private void initializeComponents() {
        // Students Table
        String[] studentColumns = {"ID", "Mã SV", "Họ Tên", "Email", "SĐT", "Ngành", "Số Môn"};
        studentsTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentsTable = new JTable(studentsTableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudentData();
            }
        });
        
        // Grades Table
        String[] gradeColumns = {"ID", "Mã SV", "Môn Học", "Điểm", "Hệ Số", "Học Kỳ", "Năm"};
        gradesTableModel = new DefaultTableModel(gradeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gradesTable = new JTable(gradesTableModel);
        gradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        gradesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedGradeData();
            }
        });
        
        // Strategy Results Table
        String[] strategyColumns = {"Strategy", "Kết Quả"};
        strategyTableModel = new DefaultTableModel(strategyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        strategyTable = new JTable(strategyTableModel);
        
        // Text fields
        txtStudentId = new JTextField(15);
        txtFullName = new JTextField(20);
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(15);
        
        txtScore = new JTextField(10);
        txtSemester = new JTextField(10);
        txtYear = new JTextField(10);
        
        // Initialize combo boxes with predefined options
        String[] majors = {
            "Công Nghệ Thông Tin", "Kỹ Thuật Phần Mềm", "Hệ Thống Thông Tin", 
            "An Toàn Thông Tin", "Khoa Học Máy Tính", "Trí Tuệ Nhân Tạo",
            "Kỹ Thuật Máy Tính", "Mạng Máy Tính và Truyền Thông"
        };
        cmbMajor = new JComboBox<>(majors);
        cmbMajor.setEditable(true); // Cho phép nhập thêm nếu cần
        
        String[] subjects = {
            "Lập Trình Java", "Cơ Sở Dữ Liệu", "Cấu Trúc Dữ Liệu", "Thuật Toán",
            "Lập Trình Web", "Mạng Máy Tính", "Hệ Điều Hành", "Công Nghệ Phần Mềm",
            "Toán Rời Rạc", "Xác Suất Thống Kê", "Tiếng Anh", "Vật Lý Đại Cương",
            "Kiến Trúc Máy Tính", "An Toàn Thông Tin", "Trí Tuệ Nhân Tạo",
            "Machine Learning", "Lập Trình Mobile", "Phân Tích Thiết Kế Hệ Thống"
        };
        cmbSubject = new JComboBox<>(subjects);
        cmbSubject.setEditable(true); // Cho phép nhập thêm môn học mới
        
        String[] coefficients = {"1.0", "2.0", "3.0", "4.0"};
        cmbCoefficient = new JComboBox<>(coefficients);
        cmbCoefficient.setEditable(true); // Cho phép nhập giá trị khác nếu cần
        
        // Combo boxes
        cmbStudentSelect = new JComboBox<>();
        cmbStrategyStudent = new JComboBox<>();
        cmbStrategy = new JComboBox<>();
        cmbReportStudent = new JComboBox<>();
        
        // Text areas
        txtStrategyResult = new JTextArea(5, 40);
        txtStrategyResult.setEditable(false);
        txtStrategyResult.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        txtReportArea = new JTextArea(20, 50);
        txtReportArea.setEditable(false);
        txtReportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        // Populate strategy combo box
        GradeCalculationStrategy[] strategies = controller.getAvailableStrategies();
        for (GradeCalculationStrategy strategy : strategies) {
            cmbStrategy.addItem(strategy.getStrategyName());
        }
    }
    
    private void setupTabbedPane() {
        tabbedPane = new JTabbedPane();
        
        // Students Management Tab
        tabbedPane.addTab("Quản Lý Sinh Viên", createStudentManagementPanel());
        
        // Grades Management Tab
        tabbedPane.addTab("Quản Lý Điểm", createGradeManagementPanel());
        
        // Strategy Pattern Tab
        tabbedPane.addTab("Tính Điểm (Strategy)", createStrategyPanel());
        
        // Reports Tab
        tabbedPane.addTab("Báo Cáo", createReportsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Status bar
        JLabel statusBar = new JLabel("Sẵn sàng | Database: SQLite | Strategy Pattern: Active");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createStudentManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Sinh Viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Mã SV:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtStudentId, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Họ Tên:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(txtFullName, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtEmail, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(txtPhone, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Ngành:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(cmbMajor, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập Nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm Mới");
        JButton btnSearch = new JButton("Tìm Kiếm");
        
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearStudentFields());
        btnSearch.addActionListener(e -> searchStudents());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnSearch);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        inputPanel.add(buttonPanel, gbc);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGradeManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Điểm"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Student selection
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Sinh Viên:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(cmbStudentSelect, gbc);
        
        // Grade details
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Môn Học:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(cmbSubject, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Điểm:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(txtScore, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Hệ Số:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(cmbCoefficient, gbc);
        gbc.gridx = 2;
        inputPanel.add(new JLabel("Học Kỳ:"), gbc);
        gbc.gridx = 3;
        inputPanel.add(txtSemester, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Năm:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(txtYear, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAddGrade = new JButton("Thêm Điểm");
        JButton btnUpdateGrade = new JButton("Cập Nhật");
        JButton btnDeleteGrade = new JButton("Xóa");
        JButton btnClearGrade = new JButton("Làm Mới");
        
        btnAddGrade.addActionListener(e -> addGrade());
        btnUpdateGrade.addActionListener(e -> updateGrade());
        btnDeleteGrade.addActionListener(e -> deleteGrade());
        btnClearGrade.addActionListener(e -> clearGradeFields());
        
        buttonPanel.add(btnAddGrade);
        buttonPanel.add(btnUpdateGrade);
        buttonPanel.add(btnDeleteGrade);
        buttonPanel.add(btnClearGrade);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 4;
        inputPanel.add(buttonPanel, gbc);
        
        // Table
        JScrollPane scrollPane = new JScrollPane(gradesTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStrategyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Top panel for student selection and strategy
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Tính Điểm Theo Strategy Pattern"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Sinh Viên:"), gbc);
        gbc.gridx = 1;
        topPanel.add(cmbStrategyStudent, gbc);
        
        gbc.gridx = 2;
        topPanel.add(new JLabel("Strategy:"), gbc);
        gbc.gridx = 3;
        topPanel.add(cmbStrategy, gbc);
        
        JButton btnCalculate = new JButton("Tính Điểm");
        JButton btnCalculateAll = new JButton("Tính Tất Cả");
        btnCalculate.addActionListener(e -> calculateGradeWithStrategy());
        btnCalculateAll.addActionListener(e -> calculateAllStrategies());
        
        gbc.gridx = 4;
        topPanel.add(btnCalculate, gbc);
        gbc.gridx = 5;
        topPanel.add(btnCalculateAll, gbc);
        
        // Results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Kết Quả"));
        
        // Strategy comparison table
        JScrollPane strategyScrollPane = new JScrollPane(strategyTable);
        strategyScrollPane.setPreferredSize(new Dimension(0, 200));
        
        // Detailed result text area
        JScrollPane txtScrollPane = new JScrollPane(txtStrategyResult);
        txtScrollPane.setPreferredSize(new Dimension(0, 150));
        
        resultsPanel.add(strategyScrollPane, BorderLayout.CENTER);
        resultsPanel.add(txtScrollPane, BorderLayout.SOUTH);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(resultsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBorder(BorderFactory.createTitledBorder("Tạo Báo Cáo"));
        
        controlPanel.add(new JLabel("Sinh Viên:"));
        controlPanel.add(cmbReportStudent);
        
        JButton btnTranscript = new JButton("Bảng Điểm");
        JButton btnSummary = new JButton("Tổng Quan");
        JButton btnStatistics = new JButton("Thống Kê");
        
        btnTranscript.addActionListener(e -> generateTranscript());
        btnSummary.addActionListener(e -> generateSummary());
        btnStatistics.addActionListener(e -> generateStatistics());
        
        controlPanel.add(btnTranscript);
        controlPanel.add(btnSummary);
        controlPanel.add(btnStatistics);
        
        // Report display area
        JScrollPane scrollPane = new JScrollPane(txtReportArea);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Student Management Methods
    private void addStudent() {
        try {
            String studentId = txtStudentId.getText().trim();
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String major = (String) cmbMajor.getSelectedItem();
            
            if (studentId.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã SV và Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Student student = new Student(studentId, fullName, 
                                        email.isEmpty() ? null : email,
                                        phone.isEmpty() ? null : phone,
                                        (major != null && !major.trim().isEmpty()) ? major : null);
            
            if (controller.addStudent(student)) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thành công!");
                clearStudentFields();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên thất bại! (Có thể mã SV đã tồn tại)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần cập nhật!");
            return;
        }
        
        try {
            int id = (Integer) studentsTableModel.getValueAt(selectedRow, 0);
            String studentId = txtStudentId.getText().trim();
            String fullName = txtFullName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String major = (String) cmbMajor.getSelectedItem();
            
            if (studentId.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã SV và Họ tên không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Student student = new Student(id, studentId, fullName,
                                        email.isEmpty() ? null : email,
                                        phone.isEmpty() ? null : phone,
                                        major.isEmpty() ? null : major);
            
            if (controller.updateStudent(student)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                clearStudentFields();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!");
            return;
        }
        
        String studentId = (String) studentsTableModel.getValueAt(selectedRow, 1);
        String fullName = (String) studentsTableModel.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sinh viên:\n" + fullName + " (" + studentId + ")?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearStudentFields();
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearStudentFields() {
        txtStudentId.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        cmbMajor.setSelectedIndex(0);
    }
    
    private void searchStudents() {
        String searchTerm = JOptionPane.showInputDialog(this, "Nhập tên sinh viên cần tìm:");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<Student> results = controller.searchStudentsByName(searchTerm.trim());
            
            // Clear and populate table with search results
            studentsTableModel.setRowCount(0);
            for (Student student : results) {
                studentsTableModel.addRow(new Object[] {
                    student.getId(),
                    student.getStudentId(),
                    student.getFullName(),
                    student.getEmail(),
                    student.getPhoneNumber(),
                    student.getMajor(),
                    student.getGrades().size()
                });
            }
            
            JOptionPane.showMessageDialog(this, "Tìm thấy " + results.size() + " kết quả.");
        }
    }
    
    private void loadSelectedStudentData() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtStudentId.setText((String) studentsTableModel.getValueAt(selectedRow, 1));
            txtFullName.setText((String) studentsTableModel.getValueAt(selectedRow, 2));
            
            String email = (String) studentsTableModel.getValueAt(selectedRow, 3);
            txtEmail.setText(email != null ? email : "");
            
            String phone = (String) studentsTableModel.getValueAt(selectedRow, 4);
            txtPhone.setText(phone != null ? phone : "");
            
            String major = (String) studentsTableModel.getValueAt(selectedRow, 5);
            cmbMajor.setSelectedItem(major != null ? major : "");
        }
    }
    
    // Grade Management Methods
    private void addGrade() {
        try {
            String studentInfo = (String) cmbStudentSelect.getSelectedItem();
            if (studentInfo == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!");
                return;
            }
            
            String studentId = studentInfo.split(" - ")[0];
            String subject = (String) cmbSubject.getSelectedItem();
            String scoreText = txtScore.getText().trim();
            String coefficientText = (String) cmbCoefficient.getSelectedItem();
            String semester = txtSemester.getText().trim();
            String yearText = txtYear.getText().trim();
            
            if (subject.isEmpty() || scoreText.isEmpty() || coefficientText.isEmpty() || 
                semester.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double score = Double.parseDouble(scoreText);
            double coefficient = Double.parseDouble(coefficientText);
            int year = Integer.parseInt(yearText);
            
            if (score < 0 || score > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải từ 0 đến 10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (controller.addGrade(studentId, subject, score, coefficient, semester, year)) {
                JOptionPane.showMessageDialog(this, "Thêm điểm thành công!");
                clearGradeFields();
                refreshGradesData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm điểm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng điểm để cập nhật!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get grade ID from selected row
            int gradeId = (Integer) gradesTableModel.getValueAt(selectedRow, 0);
            
            // Get input data
            String studentInfo = (String) cmbStudentSelect.getSelectedItem();
            if (studentInfo == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String studentId = studentInfo.split(" - ")[0];
            String subject = (String) cmbSubject.getSelectedItem();
            String scoreText = txtScore.getText().trim();
            String coefficientText = (String) cmbCoefficient.getSelectedItem();
            String semester = txtSemester.getText().trim();
            String yearText = txtYear.getText().trim();
            
            if (subject.isEmpty() || scoreText.isEmpty() || coefficientText.isEmpty() || 
                semester.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double score = Double.parseDouble(scoreText);
            double coefficient = Double.parseDouble(coefficientText);
            int year = Integer.parseInt(yearText);
            
            if (score < 0 || score > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải nằm trong khoảng 0-10!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Find student by studentId to get student.id
            Student student = controller.getStudentByStudentId(studentId);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy sinh viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create Grade object with updated data
            Grade grade = new Grade();
            grade.setId(gradeId);
            grade.setStudentId(student.getId()); // Use student.id (int) not studentId (String)
            grade.setSubject(subject);
            grade.setScore(score);
            grade.setCoefficient(coefficient);
            grade.setSemester(semester);
            grade.setYear(year);
            
            // Update in database
            if (controller.updateGrade(grade)) {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm thành công!");
                clearGradeFields();
                refreshGradesData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật điểm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng điểm để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Get grade information for confirmation
            String studentId = (String) gradesTableModel.getValueAt(selectedRow, 1);
            String subject = (String) gradesTableModel.getValueAt(selectedRow, 2);
            String score = gradesTableModel.getValueAt(selectedRow, 3).toString();
            String semester = (String) gradesTableModel.getValueAt(selectedRow, 5);
            String year = gradesTableModel.getValueAt(selectedRow, 6).toString();
            
            // Confirm deletion
            String message = String.format("Bạn có chắc chắn muốn xóa điểm này?\n\n" +
                                         "Sinh viên: %s\n" +
                                         "Môn học: %s\n" +
                                         "Điểm: %s\n" +
                                         "Học kỳ: %s\n" +
                                         "Năm: %s", 
                                         studentId, subject, score, semester, year);
            
            int confirm = JOptionPane.showConfirmDialog(this, message, "Xác nhận xóa", 
                                                       JOptionPane.YES_NO_OPTION, 
                                                       JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Get grade ID
                int gradeId = (Integer) gradesTableModel.getValueAt(selectedRow, 0);
                
                // Delete from database
                if (controller.deleteGrade(gradeId)) {
                    JOptionPane.showMessageDialog(this, "Xóa điểm thành công!");
                    clearGradeFields();
                    refreshGradesData();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa điểm thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearGradeFields() {
        cmbSubject.setSelectedIndex(0);
        txtScore.setText("");
        cmbCoefficient.setSelectedItem("1.0");
        txtSemester.setText("");
        txtYear.setText("");
    }
    
    private void loadSelectedGradeData() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Get data from selected row
            String studentId = (String) gradesTableModel.getValueAt(selectedRow, 1);
            String subject = (String) gradesTableModel.getValueAt(selectedRow, 2);
            String score = gradesTableModel.getValueAt(selectedRow, 3).toString();
            String coefficient = gradesTableModel.getValueAt(selectedRow, 4).toString();
            String semester = (String) gradesTableModel.getValueAt(selectedRow, 5);
            String year = gradesTableModel.getValueAt(selectedRow, 6).toString();
            
            // Load student into combo box
            List<Student> students = controller.getAllStudents();
            for (Student student : students) {
                if (student.getStudentId().equals(studentId)) {
                    String studentInfo = student.getStudentId() + " - " + student.getFullName();
                    cmbStudentSelect.setSelectedItem(studentInfo);
                    break;
                }
            }
            
            // Load other fields
            cmbSubject.setSelectedItem(subject);
            txtScore.setText(score);
            cmbCoefficient.setSelectedItem(coefficient);
            txtSemester.setText(semester);
            txtYear.setText(year);
        }
    }
    
    // Strategy Pattern Methods
    private void calculateGradeWithStrategy() {
        String studentInfo = (String) cmbStrategyStudent.getSelectedItem();
        String strategyName = (String) cmbStrategy.getSelectedItem();
        
        if (studentInfo == null || strategyName == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên và strategy!");
            return;
        }
        
        String studentId = studentInfo.split(" - ")[0];
        
        // Get the selected strategy
        GradeCalculationStrategy[] strategies = controller.getAvailableStrategies();
        GradeCalculationStrategy selectedStrategy = null;
        
        for (GradeCalculationStrategy strategy : strategies) {
            if (strategy.getStrategyName().equals(strategyName)) {
                selectedStrategy = strategy;
                break;
            }
        }
        
        if (selectedStrategy != null) {
            String result = controller.calculateGradeWithStrategy(studentId, selectedStrategy);
            txtStrategyResult.setText("Strategy: " + strategyName + "\nKết quả: " + result);
        }
    }
    
    private void calculateAllStrategies() {
        String studentInfo = (String) cmbStrategyStudent.getSelectedItem();
        if (studentInfo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!");
            return;
        }
        
        String studentId = studentInfo.split(" - ")[0];
        
        // Clear strategy table
        strategyTableModel.setRowCount(0);
        
        // Calculate with all strategies
        GradeCalculationStrategy[] strategies = controller.getAvailableStrategies();
        StringBuilder resultText = new StringBuilder();
        
        for (GradeCalculationStrategy strategy : strategies) {
            String result = controller.calculateGradeWithStrategy(studentId, strategy);
            strategyTableModel.addRow(new Object[]{strategy.getStrategyName(), result});
            resultText.append(strategy.getStrategyName()).append(": ").append(result).append("\n");
        }
        
        txtStrategyResult.setText(resultText.toString());
    }
    
    // Report Methods
    private void generateTranscript() {
        String studentInfo = (String) cmbReportStudent.getSelectedItem();
        if (studentInfo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên!");
            return;
        }
        
        String studentId = studentInfo.split(" - ")[0];
        Student student = controller.getStudentByStudentId(studentId);
        List<Grade> grades = controller.getGradesByStudentId(studentId);
        
        if (student != null) {
            // Display in text area
            StringBuilder report = new StringBuilder();
            report.append("BẢNG ĐIỂM SINH VIÊN\n");
            report.append("================\n\n");
            report.append("Mã SV: ").append(student.getStudentId()).append("\n");
            report.append("Họ tên: ").append(student.getFullName()).append("\n");
            report.append("Email: ").append(student.getEmail() != null ? student.getEmail() : "N/A").append("\n");
            report.append("Ngành: ").append(student.getMajor() != null ? student.getMajor() : "N/A").append("\n\n");
            
            if (grades != null && !grades.isEmpty()) {
                report.append("CHI TIẾT ĐIỂM:\n");
                report.append(String.format("%-20s %-8s %-8s %-10s %-8s%n", "Môn học", "Điểm", "Hệ số", "Học kỳ", "Năm"));
                report.append("-".repeat(60)).append("\n");
                
                for (Grade grade : grades) {
                    report.append(String.format("%-20s %-8.2f %-8.1f %-10s %-8d%n",
                        grade.getSubject(), grade.getScore(), grade.getCoefficient(),
                        grade.getSemester(), grade.getYear()));
                }
                
                // Add strategy calculations
                report.append("\nKẾT QUẢ TÍNH ĐIỂM:\n");
                report.append("-".repeat(30)).append("\n");
                GradeCalculationStrategy[] strategies = controller.getAvailableStrategies();
                for (GradeCalculationStrategy strategy : strategies) {
                    String result = controller.calculateGradeWithStrategy(studentId, strategy);
                    report.append(strategy.getStrategyName()).append(": ").append(result).append("\n");
                }
            }
            
            txtReportArea.setText(report.toString());
        }
    }
    
    private void generateSummary() {
        List<Student> students = controller.getAllStudents();
        
        StringBuilder report = new StringBuilder();
        report.append("BÁO CÁO TỔNG QUAN\n");
        report.append("================\n\n");
        report.append("Tổng số sinh viên: ").append(students.size()).append("\n\n");
        
        if (!students.isEmpty()) {
            report.append(String.format("%-12s %-20s %-10s %-15s%n", "Mã SV", "Họ tên", "Số môn", "Điểm TB"));
            report.append("-".repeat(60)).append("\n");
            
            for (Student student : students) {
                String avgGrade = controller.calculateAverageGrade(student.getStudentId());
                report.append(String.format("%-12s %-20s %-10d %-15s%n",
                    student.getStudentId(), student.getFullName(),
                    student.getGrades().size(), avgGrade));
            }
        }
        
        txtReportArea.setText(report.toString());
    }
    
    private void generateStatistics() {
        List<Student> students = controller.getAllStudents();
        
        int totalStudents = students.size();
        int studentsWithGrades = 0;
        int excellent = 0, good = 0, fair = 0, average = 0, poor = 0;
        
        for (Student student : students) {
            if (!student.getGrades().isEmpty()) {
                studentsWithGrades++;
                String avgStr = controller.calculateAverageGrade(student.getStudentId());
                try {
                    double avg = Double.parseDouble(avgStr);
                    if (avg >= 8.5) excellent++;
                    else if (avg >= 7.0) good++;
                    else if (avg >= 5.5) fair++;
                    else if (avg >= 4.0) average++;
                    else poor++;
                } catch (NumberFormatException e) {
                    // Skip invalid grades
                }
            }
        }
        
        StringBuilder report = new StringBuilder();
        report.append("THỐNG KÊ ĐIỂM\n");
        report.append("=============\n\n");
        report.append("Tổng số sinh viên: ").append(totalStudents).append("\n");
        report.append("Sinh viên có điểm: ").append(studentsWithGrades).append("\n\n");
        
        report.append("THỐNG KÊ THEO XẾP LOẠI:\n");
        report.append("- Xuất sắc (≥8.5): ").append(excellent).append(" sinh viên\n");
        report.append("- Giỏi (≥7.0): ").append(good).append(" sinh viên\n");
        report.append("- Khá (≥5.5): ").append(fair).append(" sinh viên\n");
        report.append("- Trung bình (≥4.0): ").append(average).append(" sinh viên\n");
        report.append("- Yếu (<4.0): ").append(poor).append(" sinh viên\n");
        
        if (studentsWithGrades > 0) {
            report.append("\nTỶ LỆ PHẦN TRĂM:\n");
            report.append(String.format("- Xuất sắc: %.1f%%\n", (double)excellent/studentsWithGrades*100));
            report.append(String.format("- Giỏi: %.1f%%\n", (double)good/studentsWithGrades*100));
            report.append(String.format("- Khá: %.1f%%\n", (double)fair/studentsWithGrades*100));
            report.append(String.format("- Trung bình: %.1f%%\n", (double)average/studentsWithGrades*100));
            report.append(String.format("- Yếu: %.1f%%\n", (double)poor/studentsWithGrades*100));
        }
        
        txtReportArea.setText(report.toString());
    }
    
    // Data refresh methods
    private void refreshAllData() {
        refreshStudentsData();
        refreshGradesData();
        refreshComboBoxes();
    }
    
    private void refreshStudentsData() {
        studentsTableModel.setRowCount(0);
        List<Student> students = controller.getAllStudents();
        
        for (Student student : students) {
            studentsTableModel.addRow(new Object[] {
                student.getId(),
                student.getStudentId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhoneNumber(),
                student.getMajor(),
                student.getGrades().size()
            });
        }
    }
    
    private void refreshGradesData() {
        gradesTableModel.setRowCount(0);
        List<Student> students = controller.getAllStudents();
        
        for (Student student : students) {
            List<Grade> grades = controller.getGradesByStudentId(student.getStudentId());
            for (Grade grade : grades) {
                gradesTableModel.addRow(new Object[] {
                    grade.getId(),
                    student.getStudentId(),
                    grade.getSubject(),
                    grade.getScore(),
                    grade.getCoefficient(),
                    grade.getSemester(),
                    grade.getYear()
                });
            }
        }
    }
    
    private void refreshComboBoxes() {
        // Clear combo boxes
        cmbStudentSelect.removeAllItems();
        cmbStrategyStudent.removeAllItems();
        cmbReportStudent.removeAllItems();
        
        // Populate with current students
        List<Student> students = controller.getAllStudents();
        for (Student student : students) {
            String item = student.getStudentId() + " - " + student.getFullName();
            cmbStudentSelect.addItem(item);
            cmbStrategyStudent.addItem(item);
            cmbReportStudent.addItem(item);
        }
    }
    
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new StudentManagementGUI();
            }
        });
    }
}