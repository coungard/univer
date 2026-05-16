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
CREATE TABLE universities (
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
CREATE TABLE faculties (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    university_id UUID NOT NULL,
    FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE CASCADE,
    UNIQUE (name, university_id)
);

-- ===================================
-- Department (Кафедра)
-- ===================================
CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    faculty_id UUID NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id) ON DELETE CASCADE,
    UNIQUE (name, faculty_id)
);

-- ===================================
-- Person (Контактная информация)
-- ===================================
CREATE TABLE persons (
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
CREATE TABLE teachers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    position VARCHAR(120) NOT NULL,
    registered BOOLEAN default false ,
    person_id UUID,
    department_id UUID,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE ,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- ===================================
-- Student (Студент)
-- ===================================
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    person_id UUID,
    enrollment_date DATE NOT NULL,
    university_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (person_id) REFERENCES persons(id) ON DELETE CASCADE,
    FOREIGN KEY (university_id) REFERENCES universities(id) ON DELETE CASCADE
);

-- ===================================
-- Program (Образовательная программа)
-- ===================================
CREATE TABLE programs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    faculty_id UUID,
    code VARCHAR(36), -- Код профессии, специальности (08.02.01)
    name VARCHAR(72),  -- Наименовние учебного плана
    profession VARCHAR(120), -- Наименование профессии, специальности, направления подготовки
    direction VARCHAR(240), -- Образовательная программа, направленность, профиль
    education_level VARCHAR(180), --  Уровень образования (Высшее образование - бакалавриат)
    education_form VARCHAR(32),  -- Форма обучения (Очная/Заочная и т.д.)
    duration_of_study INTERVAL, -- Срок обучение
    qualification VARCHAR(120), -- Присваевываемая квалификационная категория
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP,
    FOREIGN KEY (faculty_id) REFERENCES faculties(id)
);

-- ===================================
-- Course (Курс)
-- ===================================
CREATE TABLE courses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    department_id UUID NOT NULL,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
);

-- ===================================
-- Lecture (Лекция)
-- ===================================
CREATE TABLE lectures (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(255) NOT NULL,
    content TEXT,
    scheduled_time TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 90,
    course_id UUID NOT NULL,
    teacher_id UUID,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (teacher_id) REFERENCES persons(id) ON DELETE SET NULL
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
    FOREIGN KEY (student_id) REFERENCES persons(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- ===================================
-- LectureAttendance (Посещение лекций)
-- ===================================
CREATE TABLE lecture_attendance (
    student_id UUID NOT NULL,
    lecture_id UUID NOT NULL,
    attended BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (student_id, lecture_id),
    FOREIGN KEY (student_id) REFERENCES persons(id) ON DELETE CASCADE,
    FOREIGN KEY (lecture_id) REFERENCES lectures(id) ON DELETE CASCADE
);

-- ===================================
-- Индексы для производительности
-- ===================================
CREATE INDEX idx_course_department ON courses(department_id);
CREATE INDEX idx_lecture_course ON lectures(course_id);
CREATE INDEX idx_enrollment_student ON enrollment(student_id);
CREATE INDEX idx_lecture_attendance_student ON lecture_attendance(student_id);