package cz.engeto.Projekt3.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity
@Table(name = "users") // V databázi hledej tabulku users, tabulku user nemuzu, user je klicove slovo
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Jméno musí být vyplněno") // Pro Spring (validace vstupu)
    @Column(nullable = false) // Pro databazi
    private String name;

    @NotBlank(message = "Příjmení musí být vyplněno") // Pro Spring (validace vstupu)
    @Column(nullable = false) // Pro databazi
    private String surname;

    @NotBlank(message = "Person ID musí být vyplněno")
    @Size(min = 12, max = 12, message = "Person ID musí mít přesně 12 znaků")
    @Column(nullable = false, unique = true)
    private String personId;

    @Column(nullable = false, unique = true)
    private String uuid;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @PrePersist
    public void generateUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }
}
