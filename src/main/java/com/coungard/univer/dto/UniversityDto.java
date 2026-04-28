package com.coungard.univer.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UniversityDto(
        UUID id,
        String name,
        String description,
        LocalDateTime createdAt,
        AddressDto address,
        List<FacultyDto> faculties
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private LocalDateTime createdAt;
        private AddressDto address;
        private List<FacultyDto> faculties;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder address(AddressDto address) {
            this.address = address;
            return this;
        }

        public Builder faculties(List<FacultyDto> faculties){
            this.faculties=faculties;
            return this;
        }

        /**
         * Создаёт и возвращает неизменяемый экземпляр UniversityDto.
         */
        public UniversityDto build() {
            return new UniversityDto(
                    id,
                    name,
                    description,
                    createdAt,
                    address,
                    faculties
            );
        }
    }
}