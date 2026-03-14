package cz.engeto.Projekt3.service;


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
    private final String FILE_PATH = "dataPersonId.txt";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Vytvoreni noveho uzivatele - POST insert
    @Transactional // transakce
    public User saveUser(User user) {

        // Kontrola, jestli jsou všechna povinná pole vyplněná
        if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("Chyba: Jméno musí být vyplněno!");
        }
        if (user.getSurname() == null || user.getSurname().isBlank()) {
            throw new RuntimeException("Chyba: Příjmení musí být vyplněno!");
        }
        if (user.getPersonId() == null || user.getPersonId().isBlank()) {
            throw new RuntimeException("Chyba: Person ID musí být vyplněno!");
        }

        // Načtu si seznam povolených person ID ze souboru
        List<String> listPersonId = loadPersonIdsFromFile();


        // Zjistim, jestli bylo zadane povolene person ID
        if (listPersonId.contains(user.getPersonId())) {
            // Pokud uz je person ID pouzite v DB, hlasim chybu
            if (userRepository.existsByPersonId(user.getPersonId())) {
                throw new RuntimeException("Chyba: Toto ID už je obsazené!");
            } else {
                return userRepository.save(user);
            }

        } else {
            // person ID nenalezeno v souboru
            throw new RuntimeException("Chyba: ID " + user.getPersonId() + " není v seznamu povolených!");
        }
    }

    // 2. Oprava dat PUT update
    @Transactional
    public User updateUser(User user) {
        // Kontrola, jestli jsou v novém objektu vyplněná všechna pole
        if (user.getName() == null || user.getName().isBlank()) {
            throw new RuntimeException("Chyba: Jméno pro úpravu musí být vyplněno!");
        }
        if (user.getSurname() == null || user.getSurname().isBlank()) {
            throw new RuntimeException("Chyba: Příjmení pro úpravu musí být vyplněno!");
        }
        if (user.getPersonId() == null || user.getPersonId().isBlank()) {
            throw new RuntimeException("Chyba: Person ID pro úpravu musí být vyplněno!");
        }

        //Najdeme uživatele v optional podle ID
        Optional<User> optionalUser = userRepository.findById(user.getId());

        if (optionalUser.isPresent()) {
            // uzivatel nalezen
            User user1 = optionalUser.get();

            // Pokud uživatel mění person ID, musíme person ID zkontrolovat
            if (!user1.getPersonId().equals(user.getPersonId())) {
                List<String> listId = loadPersonIdsFromFile();

                // Je nové person ID v souboru?
                if (!listId.contains(user.getPersonId())) {
                    throw new RuntimeException("Chyba: Nové person ID není v souboru!");
                }

                // Je uz nove person ID pouzito?
                if (userRepository.existsByPersonId(user.getPersonId())) {
                    throw new RuntimeException("Chyba: Nové person ID už někdo používá!");
                }
            }

            user1.setName(user.getName());
            user1.setSurname(user.getSurname());
            user1.setPersonId(user.getPersonId());

            return userRepository.save(user1);

        } else {
            throw new RuntimeException("Uživatel s ID " + user.getId() + " neexistuje.");
        }
    }


    // 3. zpracovani souboru
    private List<String> loadPersonIdsFromFile() {
        try {
            // Otevřeme "cestu" k souboru v resources
            ClassPathResource resource = new ClassPathResource(FILE_PATH);

            // Připravíme si čtečku (BufferedReader)
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            List<String> lines = new ArrayList<>();
            String line;

            // Čteme soubor po řádku, dokud tam něco je
            while ((line = reader.readLine()) != null) {
                // Ořežeme mezery a přidáme do seznamu
                lines.add(line.trim());
            }

            reader.close(); // Zavřeme čtečku
            return lines;

        } catch (Exception e) {
            throw new RuntimeException("Chyba: Nepodařilo se přečíst soubor s person ID.");
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








