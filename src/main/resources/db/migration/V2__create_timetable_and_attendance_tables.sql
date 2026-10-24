
-- =====================================================
-- V2: TIMETABLE AND ATTENDANCE SCHEMA CHANGES
-- =====================================================

-- 1. PERIOD SLOTS
CREATE TABLE period_slots (
    period_slot_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    department_id BIGINT NULL,
    period_number TINYINT NOT NULL,
    slot_name VARCHAR(50) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_break BOOLEAN NOT NULL DEFAULT FALSE,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_slot_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
        ON DELETE SET NULL,

    CONSTRAINT chk_period_slot_time
        CHECK (start_time < end_time),

    CONSTRAINT uk_dept_period_number
        UNIQUE (department_id, period_number)
);


-- 2. ACADEMIC HOLIDAYS
CREATE TABLE academic_holidays (
    holiday_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    academic_year VARCHAR(20) NOT NULL,
    holiday_name VARCHAR(120) NOT NULL,
    holiday_type ENUM(
        'NATIONAL',
        'FESTIVAL',
        'INSTITUTIONAL',
        'EXAM_PREP_LEAVE',
        'EMERGENCY_CLOSURE'
    ) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    department_id BIGINT NULL,
    program_id BIGINT NULL,
    description VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_holiday_department
        FOREIGN KEY (department_id)
        REFERENCES departments(department_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_holiday_program
        FOREIGN KEY (program_id)
        REFERENCES programs(program_id)
        ON DELETE CASCADE,

    CONSTRAINT chk_holiday_dates
        CHECK (start_date <= end_date)
);


-- 3. TIMETABLE ENTRIES
-- period_slots must be created before this table.
CREATE TABLE timetable_entries (
    timetable_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    subject_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    period_slot_id BIGINT NOT NULL,
    day_of_week ENUM(
        'MONDAY',
        'TUESDAY',
        'WEDNESDAY',
        'THURSDAY',
        'FRIDAY',
        'SATURDAY',
        'SUNDAY'
    ) NOT NULL,
    room_number VARCHAR(30) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_timetable_class
        FOREIGN KEY (class_id)
        REFERENCES classes(class_id),

    CONSTRAINT fk_timetable_subject
        FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id),

    CONSTRAINT fk_timetable_staff
        FOREIGN KEY (staff_id)
        REFERENCES staff(staff_id),

    CONSTRAINT fk_timetable_period_slot
        FOREIGN KEY (period_slot_id)
        REFERENCES period_slots(period_slot_id),

    CONSTRAINT uk_class_day_period
        UNIQUE (class_id, day_of_week, period_slot_id),

    CONSTRAINT uk_staff_day_period
        UNIQUE (staff_id, day_of_week, period_slot_id)
);


-- 4. ALTER THE EXISTING ATTENDANCE SESSIONS TABLE
-- Do not CREATE attendance_sessions again.

ALTER TABLE attendance_sessions
    ADD COLUMN period_slot_id BIGINT NULL,
    ADD COLUMN actual_start_time TIME NULL,
    ADD COLUMN actual_end_time TIME NULL;


-- Replace the old uniqueness rule with the new one.
-- The old schema has uk_class_subject_session.
ALTER TABLE attendance_sessions
    DROP INDEX uk_class_subject_session;


ALTER TABLE attendance_sessions
    ADD CONSTRAINT fk_session_period_slot
        FOREIGN KEY (period_slot_id)
        REFERENCES period_slots(period_slot_id);


ALTER TABLE attendance_sessions
    ADD CONSTRAINT uk_class_date_period
        UNIQUE (class_id, session_date, period_slot_id);


-- 5. UPDATE THE EXISTING ATTENDANCE RECORDS FOREIGN KEY
-- Keep the existing attendance_records table and its data.
-- Change session deletion to cascade to attendance records.

ALTER TABLE attendance_records
    DROP FOREIGN KEY fk_attendance_session;


ALTER TABLE attendance_records
    ADD CONSTRAINT fk_record_session
        FOREIGN KEY (session_id)
        REFERENCES attendance_sessions(session_id)
        ON DELETE CASCADE;
