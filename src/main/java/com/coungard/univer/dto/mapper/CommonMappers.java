package com.coungard.univer.dto.mapper;

import com.coungard.univer.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel="spring")
public interface CommonMappers {

    @Named("mapToDepartment")
    default Department mapToDepartment(UUID id) {
        if (id == null) return null;
        Department dept = new Department();
        dept.setId(id);
        return dept;
    }

    @Named("mapToUniversity")
    default University mapToUniversity(UUID id) {
        if (id == null) return null;
        University university = new University();
        university.setId(id);
        return university;
    }

    @Named("mapToStudent")
    default Student mapToStudent(UUID id) {
        if (id == null) return null;
        Student student = new Student();
        student.setId(id);
        return student;
    }

    @Named("mapToCourse")
    default Course mapToCourse(UUID id) {
        if (id == null) return null;
        Course course = new Course();
        course.setId(id);
        return course;
    }

    @Named("mapToLecture")
    default Lecture mapToLecture(UUID id) {
        if (id == null) return null;
        Lecture lecture = new Lecture();
        lecture.setId(id);
        return lecture;
    }
}
