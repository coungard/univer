-- Включаем поддержку UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ===================================
-- Address (Адрес университета)
-- ===================================
CREATE TABLE address (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    address TEXT NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    street TEXT NOT NULL,
    phone_fax VARCHAR(50),
    email VARCHAR(255) NOT NULL,
    website VARCHAR(255)
);

-- ===================================
-- University (Университет)
-- ===================================
CREATE TABLE university (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    address_id UUID NOT NULL,
    FOREIGN KEY (address_id) REFERENCES address(id) ON DELETE CASCADE
);

-- ===================================
-- Faculty (Факультет)
-- ===================================
CREATE TABLE faculty (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    university_id UUID NOT NULL,
    FOREIGN KEY (university_id) REFERENCES university(id) ON DELETE CASCADE,
    UNIQUE (name, university_id)
);

-- ===================================
-- Department (Кафедра)
-- ===================================
CREATE TABLE department (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    faculty_id UUID NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE,
    UNIQUE (name, faculty_id)
);


-- ===================================
-- Person (Контактная информация)
-- ===================================
CREATE TABLE person (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    fullname VARCHAR(100) NOT NULL,
    phone VARCHAR(24)
);

-- ===================================
-- Teacher (Преподаватель)
-- ===================================
CREATE TABLE teacher (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id UUID,
    department_id UUID,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE SET NULL,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
);

-- ===================================
-- Student (Студент)
-- ===================================
CREATE TABLE student (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id UUID,
    enrollment_date DATE NOT NULL,
    university_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES person(id) ON DELETE SET NULL,
    FOREIGN KEY (university_id) REFERENCES university(id) ON DELETE CASCADE
);

-- ===================================
-- Course (Курс)
-- ===================================
CREATE TABLE course (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
);

-- ===================================
-- Lecture (Лекция)
-- ===================================
CREATE TABLE lecture (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    scheduled_time TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 90,
    course_id UUID NOT NULL,
    teacher_id UUID,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES teacher(id) ON DELETE SET NULL
);

-- ===================================
-- Enrollment (Зачисление студента на курс)
-- ===================================
CREATE TABLE enrollment (
    student_id UUID NOT NULL,
    course_id UUID NOT NULL,
    enrolled_at TIMESTAMP DEFAULT NOW(),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'completed', 'dropped')),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE
);

-- ===================================
-- LectureAttendance (Посещение лекций)
-- ===================================
CREATE TABLE lecture_attendance (
    student_id UUID NOT NULL,
    lecture_id UUID NOT NULL,
    attended BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (student_id, lecture_id),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (lecture_id) REFERENCES lecture(id) ON DELETE CASCADE
);

-- ===================================
-- Индексы для производительности
-- ===================================
CREATE INDEX idx_course_department ON course(department_id);
CREATE INDEX idx_lecture_course ON lecture(course_id);
CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_lecture_attendance_student ON lecture_attendance(student_id);