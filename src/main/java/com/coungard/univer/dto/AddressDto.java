package com.coungard.univer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@Schema(description = "Ввод адреса: либо ID существующего, либо данные для создания")
public record AddressDto(

        @Schema(description = "ID существующего адреса (если используется)", example = "a8f2e7b1-...")
        UUID id,

        @NotBlank @Schema(description = "Полный адрес", example = "367026, Российская Федерация, Республика Дагестан, г. Махачкала, проспект Имама Шамиля, 70")
        String address,

        @NotBlank @Schema(description = "Страна", example = "Россия")
        String country,

        @NotBlank @Schema(description = "Регион", example = "Дагестан")
        String region,
        @NotBlank @Schema(description = "Город", example = "Каспийск")
        String city,
        @NotBlank @Schema(description = "Улица", example = "Ленина, 10")
        String street,
        @Schema(description = "Почтовый индекс", example = "367026")
        String postalCode,
        @Schema(description = "Телефон/Факс", example = "+7 (8722) 62-37-61")
        String phone,
        @Schema(description = "Электронная почта", example = "dstu@dstu.ru")
        String email,
        @Schema(description = "Сайт", example = "http://www.dstu.ru")
        String website
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String address;
        private String country;
        private String region;
        private String city;
        private String street;
        private String postalCode;
        private String phone;
        private String email;
        private String website;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public AddressDto build() {
            return new AddressDto(
                    id,
                    address,
                    country,
                    region,
                    city,
                    street,
                    postalCode,
                    phone,
                    email,
                    website
            );
        }
    }
}