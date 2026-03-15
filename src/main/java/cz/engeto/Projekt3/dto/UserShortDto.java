package cz.engeto.Projekt3.dto;

import jakarta.validation.constraints.NotBlank;

public class UserShortDto {
        private int id;

        @NotBlank(message = "Jméno musí být vyplněno")
        private String name;

        @NotBlank(message = "Příjmení musí být vyplněno")
        private String surname;

        public UserShortDto() {
        }

        public UserShortDto(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }
