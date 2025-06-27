@echo off
echo ========================================
echo BCrypt Password Generator
echo ========================================
echo.

REM Check if Java is available
java -version >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java first
    pause
    exit /b 1
)

echo Creating password generator...
echo.

REM Create temporary Java file
echo import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; > temp_password_generator.java
echo public class temp_password_generator { >> temp_password_generator.java
echo     public static void main(String[] args) { >> temp_password_generator.java
echo         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); >> temp_password_generator.java
echo         String password = "123456"; >> temp_password_generator.java
echo         String encodedPassword = encoder.encode(password); >> temp_password_generator.java
echo         System.out.println("Password: " + password); >> temp_password_generator.java
echo         System.out.println("Encoded: " + encodedPassword); >> temp_password_generator.java
echo         System.out.println("Verify: " + encoder.matches(password, encodedPassword)); >> temp_password_generator.java
echo     } >> temp_password_generator.java
echo } >> temp_password_generator.java

echo Generating BCrypt hash for password '123456'...
echo.

REM Try to compile and run (this might fail without Spring Security in classpath)
javac temp_password_generator.java >nul 2>nul
if %errorlevel% equ 0 (
    java temp_password_generator
) else (
    echo Cannot compile Java file (missing Spring Security dependency)
    echo.
    echo Using pre-generated BCrypt hash:
    echo Password: 123456
    echo Encoded: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa
    echo.
    echo You can use this hash directly in your database.
)

REM Clean up
del temp_password_generator.java >nul 2>nul
del temp_password_generator.class >nul 2>nul

echo.
echo ========================================
echo SQL Insert Statement:
echo ========================================
echo.
echo INSERT INTO auth_user (username, password, email, full_name, role, enabled, created_at, updated_at) VALUES
echo ('your_admin_username', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'your_email@example.com', 'Your Name', 'ADMIN', 1, NOW(), NOW());
echo.
echo ========================================
echo Note: Password is '123456'
echo ========================================
echo.
pause 