# HƯỚNG DẪN SỬ DỤNG - STUDENT MANAGEMENT SYSTEM

## Cách chạy ứng dụng:

### Cách 1: Sử dụng file .bat (Đơn giản nhất)
1. Double-click vào file `RunStudentManager.bat`
2. Ứng dụng sẽ tự động khởi chạy

### Cách 2: Chạy từ Command Line
```bash
cd dist
java -jar StudentManagement.jar
```

## Tính năng chính

### 1. Quản lý sinh viên
- Thêm sinh viên mới
- Xem danh sách sinh viên
- Tìm kiếm sinh viên theo tên
- Cập nhật thông tin sinh viên
- Xóa sinh viên

### 2. Quản lý điểm
- Thêm điểm cho sinh viên
- Xem điểm của sinh viên
- Cập nhật điểm
- Xóa điểm

### 3. Tính toán điểm (Strategy Pattern)
- **Điểm trung bình**: Tính điểm trung bình có trọng số
- **Điểm chữ**: Chuyển đổi điểm số thành điểm chữ (A, B, C, D, F)
- **Xếp loại**: Phân loại kết quả học tập (Xuất sắc, Giỏi, Khá, Trung bình, Yếu)

## Kiến trúc hệ thống

### Design Patterns
- **Strategy Pattern**: Áp dụng trong việc tính toán điểm theo nhiều phương thức khác nhau
- **MVC Pattern**: Tách biệt Model, View, Controller
- **DAO Pattern**: Quản lý truy cập dữ liệu

### Cấu trúc thư mục
```
src/main/java/com/studentmanager/
├── model/              # Các lớp mô hình dữ liệu
│   ├── Student.java    # Lớp sinh viên
│   └── Grade.java      # Lớp điểm số
├── strategy/           # Strategy Pattern cho tính điểm
│   ├── GradeCalculationStrategy.java
│   ├── AverageGradeStrategy.java
│   ├── LetterGradeStrategy.java
│   ├── ClassificationStrategy.java
│   └── GradeCalculator.java
├── dao/                # Data Access Objects
│   ├── DatabaseConnection.java
│   ├── StudentDAO.java
│   └── GradeDAO.java
├── controller/         # Business Logic
│   └── StudentController.java
├── view/               # User Interface
│   └── ConsoleView.java
└── StudentManagementSystem.java  # Main class
```

## Yêu cầu hệ thống
- Java 8 trở lên
- SQLite JDBC Driver (sqlite-jdbc-x.x.x.jar)

## Cài đặt và chạy

### 1. Tải SQLite JDBC Driver
Tải file `sqlite-jdbc-3.43.0.0.jar` (hoặc phiên bản mới nhất) từ:
https://github.com/xerial/sqlite-jdbc/releases

### 2. Thêm SQLite JDBC vào classpath
Đặt file jar vào thư mục `lib/` trong dự án.

### 3. Compile và chạy
```bash
# Compile
javac -cp "lib/*" -d bin src/main/java/com/studentmanager/*.java src/main/java/com/studentmanager/*/*.java

# Chạy
java -cp "bin;lib/*" com.studentmanager.StudentManagementSystem
```

### 4. Chạy trong VS Code
1. Mở terminal trong VS Code
2. Chạy các lệnh compile và run ở trên
3. Hoặc sử dụng Run configurations trong VS Code

## Cách sử dụng

### 1. Thêm sinh viên
- Chọn menu "1. Thêm sinh viên mới"
- Nhập thông tin sinh viên: mã SV, họ tên, email, SĐT, ngành

### 2. Thêm điểm
- Chọn menu "6. Thêm điểm"
- Nhập mã sinh viên, tên môn học, điểm (0-10), hệ số, học kỳ, năm

### 3. Tính toán điểm (Strategy Pattern)
- Chọn menu "8. Tính toán điểm (Strategy Pattern)"
- Nhập mã sinh viên
- Chọn phương thức tính điểm:
  - **Điểm trung bình**: Tính điểm TB có trọng số
  - **Điểm chữ**: A (≥8.5), B (≥7.0), C (≥5.5), D (≥4.0), F (<4.0)
  - **Xếp loại**: Xuất sắc (≥8.5), Giỏi (≥7.0), Khá (≥5.5), TB (≥4.0), Yếu (<4.0)

## Ví dụ sử dụng Strategy Pattern

```java
// Tạo các strategy khác nhau
GradeCalculationStrategy avgStrategy = new AverageGradeStrategy();
GradeCalculationStrategy letterStrategy = new LetterGradeStrategy();
GradeCalculationStrategy classificationStrategy = new ClassificationStrategy();

// Sử dụng context để tính toán
GradeCalculator calculator = new GradeCalculator();

// Tính điểm trung bình
calculator.setStrategy(avgStrategy);
String average = calculator.calculateGrade(studentGrades);

// Tính điểm chữ
calculator.setStrategy(letterStrategy);
String letterGrade = calculator.calculateGrade(studentGrades);

// Tính xếp loại
calculator.setStrategy(classificationStrategy);
String classification = calculator.calculateGrade(studentGrades);
```

## Database Schema

### Bảng students
- id (INTEGER PRIMARY KEY AUTOINCREMENT)
- student_id (VARCHAR(20) UNIQUE NOT NULL)
- full_name (VARCHAR(100) NOT NULL)
- email (VARCHAR(100))
- phone_number (VARCHAR(15))
- major (VARCHAR(100))

### Bảng grades
- id (INTEGER PRIMARY KEY AUTOINCREMENT)
- student_id (INTEGER NOT NULL, FOREIGN KEY)
- subject (VARCHAR(100) NOT NULL)
- score (REAL NOT NULL, CHECK 0-10)
- coefficient (REAL NOT NULL DEFAULT 1.0)
- semester (VARCHAR(20) NOT NULL)
- year (INTEGER NOT NULL)

## Tính năng nâng cao có thể mở rộng
1. Giao diện GUI với JavaFX/Swing
2. Báo cáo thống kê điểm
3. Import/Export dữ liệu Excel
4. Xác thực người dùng
5. Backup và restore database
6. Gửi email thông báo điểm
7. Lịch sử thay đổi điểm

## Tác giả
[Tên tác giả]
[Email liên hệ]

## Giấy phép
[Thông tin giấy phép]