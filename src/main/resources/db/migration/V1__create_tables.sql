
-- =====================================================
-- 1. DEPARTMENTS
-- =====================================================

CREATE TABLE departments (
    department_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_code VARCHAR(20) NOT NULL UNIQUE,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    hod_name VARCHAR(100),
    status ENUM('ACTIVE', 'INACTIVE')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);


-- =====================================================
-- 2. DEGREES
-- =====================================================

CREATE TABLE degrees (
    degree_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    degree_code VARCHAR(20) NOT NULL UNIQUE,
    degree_name VARCHAR(50) NOT NULL UNIQUE,
    level ENUM(
        'DIPLOMA',
        'UNDERGRADUATE',
        'POSTGRADUATE',
        'DOCTORATE'
    ) NOT NULL,
    duration_years INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_degree_duration
        CHECK (duration_years > 0)
);


-- =====================================================
-- 3. PROGRAMS
-- =====================================================

CREATE TABLE programs (
    program_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    degree_id BIGINT NOT NULL,
    program_code VARCHAR(30) NOT NULL,
    program_name VARCHAR(150) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'DISCONTINUED')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_program_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id),

    CONSTRAINT fk_program_degree
        FOREIGN KEY (degree_id)
        REFERENCES degrees(degree_id),

    CONSTRAINT uk_degree_program_code
        UNIQUE (degree_id, program_code)
);


-- =====================================================
-- 4. SUBJECTS
-- =====================================================

CREATE TABLE subjects (
    subject_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    subject_code VARCHAR(30) NOT NULL UNIQUE,
    subject_name VARCHAR(120) NOT NULL,
    credits DECIMAL(4,2) NOT NULL DEFAULT 0,
    subject_type ENUM('THEORY', 'LAB', 'PROJECT')
        NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_subject_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id),

    CONSTRAINT chk_subject_credits
        CHECK (credits >= 0)
);


-- =====================================================
-- 5. PROGRAM CURRICULUM
-- =====================================================

CREATE TABLE program_curriculum (
    curriculum_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    semester TINYINT NOT NULL,
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_curriculum_program
        FOREIGN KEY (program_id)
        REFERENCES programs(program_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_curriculum_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id)
        ON DELETE CASCADE,

    CONSTRAINT uk_program_subject_semester
        UNIQUE (program_id, subject_id, semester),

    CONSTRAINT chk_curriculum_semester
        CHECK (semester BETWEEN 1 AND 12)
);


-- =====================================================
-- 6. STUDENTS
-- =====================================================

CREATE TABLE students (
    student_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,
    admission_number VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60),
    email VARCHAR(150),
    phone VARCHAR(20),
    date_of_birth DATE,
    admission_year SMALLINT NOT NULL,
    status ENUM(
        'ACTIVE',
        'INACTIVE',
        'GRADUATED',
        'SUSPENDED',
        'WITHDRAWN'
    ) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_student_program
        FOREIGN KEY (program_id)
        REFERENCES programs(program_id)
);


-- =====================================================
-- 7. CLASSES
-- =====================================================

CREATE TABLE classes (
    class_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    program_id BIGINT NOT NULL,
    class_name VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    semester TINYINT NOT NULL,
    section VARCHAR(10),
    status ENUM('ACTIVE', 'INACTIVE')
        NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_class_program
        FOREIGN KEY (program_id)
        REFERENCES programs(program_id),

    CONSTRAINT chk_class_semester
        CHECK (semester BETWEEN 1 AND 12),

    CONSTRAINT uk_program_year_semester_section
        UNIQUE (program_id, academic_year, semester, section)
);


-- =====================================================
-- 8. STUDENT CLASS ENROLLMENTS
-- =====================================================

CREATE TABLE student_class_enrollments (
    enrollment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL DEFAULT (CURRENT_DATE),
    status ENUM(
        'ACTIVE',
        'COMPLETED',
        'TRANSFERRED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id)
        REFERENCES students(student_id),

    CONSTRAINT fk_enrollment_class
        FOREIGN KEY (class_id)
        REFERENCES classes(class_id),

    CONSTRAINT uk_student_class
        UNIQUE (student_id, class_id)
);


-- =====================================================
-- 9. STAFF
-- Covers teaching and non-teaching employees
-- =====================================================

CREATE TABLE staff (
    staff_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    employee_code VARCHAR(30) NOT NULL UNIQUE,
    first_name VARCHAR(60) NOT NULL,
    last_name VARCHAR(60),
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20),

    staff_type ENUM('TEACHING', 'NON_TEACHING')
        NOT NULL,

    designation VARCHAR(80) NOT NULL,

    qualification VARCHAR(100),
    specialization VARCHAR(120),

    status ENUM(
        'ACTIVE',
        'INACTIVE',
        'ON_LEAVE',
        'RESIGNED'
    ) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_staff_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
);


-- =====================================================
-- 10. TEACHING ASSIGNMENTS
-- Only TEACHING staff should be assigned to classes
-- =====================================================

CREATE TABLE teaching_assignments (
    assignment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,

    status ENUM('ACTIVE', 'INACTIVE', 'COMPLETED')
        NOT NULL DEFAULT 'ACTIVE',

    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_assignment_class
        FOREIGN KEY (class_id)
        REFERENCES classes(class_id),

    CONSTRAINT fk_assignment_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id),

    CONSTRAINT fk_assignment_staff
        FOREIGN KEY (staff_id)
        REFERENCES staff(staff_id),

    CONSTRAINT uk_class_subject_staff
        UNIQUE (class_id, subject_id, staff_id)
);


-- =====================================================
-- 11. ATTENDANCE SESSIONS
-- =====================================================

CREATE TABLE attendance_sessions (
    session_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,

    session_date DATE NOT NULL,
    session_number TINYINT NOT NULL DEFAULT 1,

    status ENUM('OPEN', 'LOCKED', 'CANCELLED')
        NOT NULL DEFAULT 'OPEN',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_session_class
        FOREIGN KEY (class_id)
        REFERENCES classes(class_id),

    CONSTRAINT fk_session_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id),

    CONSTRAINT fk_session_staff
        FOREIGN KEY (staff_id)
        REFERENCES staff(staff_id),

    CONSTRAINT uk_class_subject_session
        UNIQUE (class_id, subject_id, session_date, session_number),

    CONSTRAINT chk_session_number
        CHECK (session_number > 0)
);


-- =====================================================
-- 12. ATTENDANCE RECORDS
-- =====================================================

CREATE TABLE attendance_records (
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,

    attendance_status ENUM('PRESENT', 'ABSENT')
        NOT NULL DEFAULT 'PRESENT',

    marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attendance_session
        FOREIGN KEY (session_id)
        REFERENCES attendance_sessions(session_id),

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id)
        REFERENCES students(student_id),

    CONSTRAINT uk_session_student
        UNIQUE (session_id, student_id)
);


-- =====================================================
-- 13. EXAMS
-- =====================================================

CREATE TABLE exams (
    exam_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,

    exam_type ENUM('MID_1', 'MID_2', 'SEMESTER_END')
        NOT NULL,

    exam_name VARCHAR(120) NOT NULL,
    exam_date DATE,
    max_marks DECIMAL(6,2) NOT NULL,
    passing_marks DECIMAL(6,2) NOT NULL DEFAULT 0,

    status ENUM(
        'PLANNED',
        'SCHEDULED',
        'ONGOING',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'PLANNED',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_exam_class
        FOREIGN KEY (class_id)
        REFERENCES classes(class_id),

    CONSTRAINT fk_exam_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id),

    CONSTRAINT uk_class_subject_exam_type
        UNIQUE (class_id, subject_id, exam_type),

    CONSTRAINT chk_exam_marks
        CHECK (
            max_marks > 0
            AND passing_marks >= 0
            AND passing_marks <= max_marks
        )
);


-- =====================================================
-- 14. MARKS
-- =====================================================

CREATE TABLE marks (
    mark_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,

    marks_obtained DECIMAL(6,2),
    remarks VARCHAR(255),

    status ENUM('DRAFT', 'PUBLISHED', 'ABSENT', 'WITHHELD')
        NOT NULL DEFAULT 'DRAFT',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_marks_exam
        FOREIGN KEY (exam_id)
        REFERENCES exams(exam_id),

    CONSTRAINT fk_marks_student
        FOREIGN KEY (student_id)
        REFERENCES students(student_id),

    CONSTRAINT uk_exam_student
        UNIQUE (exam_id, student_id),

    CONSTRAINT chk_marks_obtained
        CHECK (marks_obtained IS NULL OR marks_obtained >= 0)
);
