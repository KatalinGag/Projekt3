package cz.engeto.Projekt3.service;


import cz.engeto.Projekt3.dto.UserShortDto;
import cz.engeto.Projekt3.model.User;
import cz.engeto.Projekt3.repository.UserRepository;
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

    // 1. Založení nového uživatele s automatickým přidělením PersonID ze souboru
    public User saveUser(User user) {
        // Načtu seznam všech ID ze souboru
        List<String> allIdsFromFile = loadPersonIdsFromFile();

        // Najdu první ID, které ještě nikdo v databázi nemá
        String personId = null;
        for (String freeId : allIdsFromFile) {
            if (!userRepository.existsByPersonId(freeId)) {
                personId = freeId;
                break; // Našli jsme volné ID, končíme cyklus
            }
        }

        // Pokud se žádné volné ID nenaslo, vytvorim chybu
        if (personId == null) {
            throw new RuntimeException("Chyba: Všechna PersonID ze souboru jsou již obsazena!");
        }

        // Přiřadím nalezené ID uživateli a uložím ho
        user.setPersonId(personId);
        return userRepository.save(user);
    }

    // Metoda pro čtení souboru. Protože mám soubor v resources, použiju ClassPathResource.
    private List<String> loadPersonIdsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource(FILE_PATH);
            // Protoze je soubor v resources, čtu ho přes InputStream
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Chyba: Nepodařilo se načíst soubor " + FILE_PATH + " z resources.");
        }
    }


    // 2. Informace o všech uzivatelich,  detailní rozšířené
    public List<User> getAllUsersDetailed() {
        return userRepository.findAll();
    }

    // 3. Informace o všech uzivatelich, pouziju zkraceni DTO
    public List<UserShortDto> getAllUsersBasic() {
        List<User> users = userRepository.findAll();
        List<UserShortDto> shortUsers = new ArrayList<>();
        for (User u : users) {
            shortUsers.add(new UserShortDto(u.getId(), u.getName(), u.getSurname()));
        }
        return shortUsers;
    }

    // 4. Informace o jednom uzivateli podle ID
    public User getUserById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("Uživatel s ID " + id + " neexistuje.");
        }
    }

    // 5. Update, zmena uživatele (PUT)
    public User updateUser(User user) {
        // Kontrola existence před uložením
        if (userRepository.existsById(user.getId())) {
            // POZOR: Při updatu obvykle personId a uuid neměníme, zůstávají původní
            User existingUser = userRepository.findById(user.getId()).get();
            existingUser.setName(user.getName());
            existingUser.setSurname(user.getSurname());
            // personId a uuid se nemeni
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("Nelze upravit: Uživatel s ID " + user.getId() + " neexistuje.");
        }
    }

    // 6. Smazat uživatele
    public void deleteUser(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("Nelze smazat: Uživatel s ID " + id + " neexistuje.");
        }
    }
}








