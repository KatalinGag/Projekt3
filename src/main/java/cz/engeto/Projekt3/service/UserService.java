package cz.engeto.Projekt3.service;


import cz.engeto.Projekt3.dto.UserCreateDto;
import cz.engeto.Projekt3.dto.UserShortDto;
import cz.engeto.Projekt3.model.User;
import cz.engeto.Projekt3.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final List<String> allowedPersonIds; // Seznam načtený v paměti
    private final String FILE_PATH = "dataPersonId.txt";

    // Konstruktor: Tady se všechno připraví při startu aplikace
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.allowedPersonIds = loadPersonIdsFromFile(); // Načteme soubor hned na začátku
    }

    // 1. Vytvoření nového uživatele
    @Transactional
    public User saveUser(UserCreateDto userDto) {
        // Kontrola povoleného ID, hledáme v seznamu v paměti
        if (!allowedPersonIds.contains(userDto.getPersonId())) {
            throw new RuntimeException("Chyba: ID " + userDto.getPersonId() + " není v seznamu povolených!");
        }

        // Kontrola duplicity v DB
        if (userRepository.existsByPersonId(userDto.getPersonId())) {
            throw new RuntimeException("Chyba: ID " + userDto.getPersonId() + " je již obsazené!");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPersonId(userDto.getPersonId());

        return userRepository.save(user);
    }

    // 2. Úprava dat (PUT)
    @Transactional
    public User updateUser(UserShortDto userDto) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("Uživatel s ID " + userDto.getId() + " neexistuje."));

        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());

        return userRepository.save(user);
    }

    // 3. Pomocná metoda pro načtení souboru (zůstává podobná, ale volá se jen jednou)
    private List<String> loadPersonIdsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource(FILE_PATH);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                return reader.lines()
                        .map(String::trim)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // Pokud se soubor nepodaří načíst při startu, aplikace by raději neměla ani běžet
            throw new IllegalStateException("Kritická chyba: Nepodařilo se načíst seznam povolených ID!");
        }
    }


    // 4. Informace o všech uzivatelich,  detailní rozšířené
    public List<User> getAllUsersDetailed() {
        return userRepository.findAll();
    }


    // 5. Informace o všech uzivatelich, pouziju zkraceni DTO
    public List<UserShortDto> getAllUsersBasic() {
        List<User> users = userRepository.findAll();
        List<UserShortDto> shortUsers = new ArrayList<>();
        for (User user : users) {
            shortUsers.add(new UserShortDto(user.getId(), user.getName(), user.getSurname()));
        }
        return shortUsers;
    }

    // 6. Informace o jednom uzivateli podle ID
    public User getUserById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("Uživatel s ID " + id + " neexistuje.");
        }
    }

    // 7. Smazat uživatele
    public void deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Nelze smazat: Uživatel s ID " + id + " neexistuje.");
        }
    }
}








