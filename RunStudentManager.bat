@echo off
echo ========================================
echo    CHUONG TRINH QUAN LY DIEM SINH VIEN
echo         Student Grade Management System
echo ========================================
echo.
echo Dang khoi dong ung dung...
echo Starting application...
echo.

cd /d "%~dp0"
java -cp "dist\StudentManagement.jar;dist\sqlite-jdbc-3.43.0.0.jar" com.studentmanager.gui.StudentManagementGUI

echo.
echo Ung dung da ket thuc.
echo Application terminated.
pause