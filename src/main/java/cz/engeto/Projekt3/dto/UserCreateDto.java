package cz.engeto.Projekt3.dto;

import jakarta.validation.constraints.NotBlank;

public class UserCreateDto {

    @NotBlank(message = "Jméno musí být vyplněno")
    private String name;

    @NotBlank(message = "Příjmení musí být vyplněno")
    private String surname;

    @NotBlank(message = "Person ID musí být vyplněno")
    private String personId;


    public UserCreateDto() {
    }

    public UserCreateDto(String name, String surname, String personId) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }
}
