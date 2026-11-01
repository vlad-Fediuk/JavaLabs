CREATE TABLE IF NOT EXISTS students (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL CHECK (LENGTH(TRIM(name)) > 0),
    student_group VARCHAR(50) NOT NULL CHECK (LENGTH(TRIM(student_group)) > 0)
);

CREATE TABLE IF NOT EXISTS subjects (
    id BIGINT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE CHECK (LENGTH(TRIM(name)) > 0),
    credits INT NOT NULL CHECK (credits > 0)
);

CREATE TABLE IF NOT EXISTS attendances (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_name VARCHAR(100) NOT NULL CHECK (LENGTH(TRIM(subject_name)) > 0),
    was_present BOOLEAN NOT NULL,
    attendance_date VARCHAR(20) NOT NULL CHECK (LENGTH(TRIM(attendance_date)) > 0),
    CONSTRAINT fk_attendances_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS grades (
    id BIGINT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    subject_name VARCHAR(100) NOT NULL CHECK (LENGTH(TRIM(subject_name)) > 0),
    work_type VARCHAR(50) NOT NULL CHECK (LENGTH(TRIM(work_type)) > 0),
    score NUMERIC(5, 2) NOT NULL CHECK (score >= 0.0 AND score <= 100.0),
    work_date VARCHAR(20) NOT NULL CHECK (LENGTH(TRIM(work_date)) > 0),
    was_present BOOLEAN NOT NULL DEFAULT TRUE,
    extra_info NUMERIC(5, 2) NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_grades_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

ALTER TABLE grades ADD COLUMN IF NOT EXISTS was_present BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE grades ADD COLUMN IF NOT EXISTS extra_info NUMERIC(5, 2) NOT NULL DEFAULT 0.0;
