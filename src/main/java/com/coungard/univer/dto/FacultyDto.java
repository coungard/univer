package com.coungard.univer.dto;

import java.util.UUID;

public record FacultyDto(
        UUID id,
        String name,
        String description,
        UUID universityId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private UUID universityId;

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

        public Builder universityId(UUID universityId) {
            this.universityId = universityId;
            return this;
        }

        public FacultyDto build() {
            return new FacultyDto(id, name, description, universityId);
        }
    }
}
