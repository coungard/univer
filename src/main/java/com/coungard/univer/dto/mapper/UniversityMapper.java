package com.coungard.univer.dto.mapper;

import com.coungard.univer.dto.AddressDto;
import com.coungard.univer.dto.FacultyDto;
import com.coungard.univer.dto.UniversityDto;
import com.coungard.univer.entity.Address;
import com.coungard.univer.entity.Faculty;
import com.coungard.univer.entity.University;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class UniversityMapper {

    /**
     * Преобразует University в UniversityDto с использованием билдера
     */
    public UniversityDto toDto(University university) {
        if (university == null) return null;

        return UniversityDto.builder()
                .id(university.getId())
                .name(university.getName())
                .description(university.getDescription())
                .createdAt(university.getCreatedAt())
                .address(this.toAddressDto(university.getAddress()))
                .faculties(this.toFacultyDtos(university.getFaculties()))
                .build();
    }

    private List<FacultyDto> toFacultyDtos(List<Faculty> faculties) {
        if (faculties == null) return null;
        return faculties.stream()
                .filter(Objects::nonNull)
                .map(this::toFacultyDto)
                .collect(Collectors.toList());
    }

    private FacultyDto toFacultyDto(Faculty faculty) {
        if (faculty == null) return null;

        return FacultyDto.builder()
                .id(faculty.getId())
                .name(faculty.getName())
                .description(faculty.getDescription())
                .universityId(faculty.getId())
                .build();
    }

    /**
     * Преобразует UniversityDto в University
     */
    public University toEntity(UniversityDto dto) {
        if (dto == null) return null;

        University university = new University();
        university.setId(dto.id());
        university.setName(dto.name());
        university.setDescription(dto.description());
        university.setCreatedAt(dto.createdAt());

        if (dto.address() != null) {
            university.setAddress(toAddressEntity(dto.address()));
        }

        return university;
    }

    public List<UniversityDto> toDtoList(List<University> universities) {
        if (universities == null) return null;
        return universities.stream()
                .map(this::toDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<University> toEntityList(List<UniversityDto> dtos) {
        if (dtos == null) return null;
        return dtos.stream()
                .map(this::toEntity)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы

    public AddressDto toAddressDto(Address address) {
        if (address == null) return null;

        return AddressDto.builder()
                .id(address.getId())
                .address(address.getAddress())
                .country(address.getCountry())
                .region(address.getRegion())
                .city(address.getCity())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .website(address.getWebsite())
                .email(address.getEmail())
                .phone(address.getPhone())
                .build();
    }

    public Address toAddressEntity(AddressDto dto) {
        if (dto == null) return null;

        Address address = new Address();
        address.setId(dto.id());
        address.setAddress(dto.address());
        address.setCountry(dto.country());
        address.setRegion(dto.region());
        address.setCity(dto.city());
        address.setStreet(dto.street());
        address.setPostalCode(dto.postalCode());
        address.setWebsite(dto.website());
        address.setEmail(dto.email());
        address.setPhone(dto.phone());

        return address;
    }
}