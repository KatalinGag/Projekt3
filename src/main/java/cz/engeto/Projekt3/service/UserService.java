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
    private final String FILE_PATH = "dataPersonId.txt"; // Soubor musí být v kořenu projektu

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Založení nového uživatele s automatickým přidělením PersonID ze souboru
    public User saveUser(User user) {
        // A) Načteme seznam všech ID ze souboru
        List<String> allIdsFromFile = loadPersonIdsFromFile();

        // B) Najdeme první ID, které ještě nikdo v databázi nemá
        String availableId = null;
        for (String idCandidate : allIdsFromFile) {
            if (!userRepository.existsByPersonId(idCandidate)) {
                availableId = idCandidate;
                break; // Našli jsme volné ID, končíme cyklus
            }
        }

        // C) Pokud jsme žádné volné ID nenašli, vyhodíme chybu
        if (availableId == null) {
            throw new RuntimeException("Chyba: Všechna PersonID ze souboru jsou již obsazena!");
        }

        // D) Přiřadíme nalezené ID uživateli a uložíme ho
        user.setPersonId(availableId);
        return userRepository.save(user);
    }

    // Metoda pro čtení souboru. Protože mám soubor v resources, použiju ClassPathResource.
    private List<String> loadPersonIdsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource(FILE_PATH);
            // Pro soubory v resources je jistější číst přes InputStream
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.toList());
            }
        } catch (IOException e) {
            throw new RuntimeException("Chyba: Nepodařilo se načíst soubor " + FILE_PATH + " z resources.");
        }
    }


    // 2. Informace o všech (Detailní)
    public List<User> getAllUsersDetailed() {
        return userRepository.findAll();
    }

    // 3. Informace o všech (Základní DTO)
    public List<UserShortDto> getAllUsersBasic() {
        List<User> allUsers = userRepository.findAll();
        List<UserShortDto> result = new ArrayList<>();
        for (User u : allUsers) {
            result.add(new UserShortDto(u.getId(), u.getName(), u.getSurname()));
        }
        return result;
    }

    // 4. Informace o jednom (podle ID)
    public User getUserById(int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new RuntimeException("Uživatel s ID " + id + " neexistuje.");
        }
    }

    // 5. Upravit uživatele (PUT)
    public User updateUser(User updatedData) {
        // Kontrola existence před uložením
        if (userRepository.existsById(updatedData.getId())) {
            // POZOR: Při updatu obvykle personId a uuid neměníme, zůstávají původní
            User existingUser = userRepository.findById(updatedData.getId()).get();
            existingUser.setName(updatedData.getName());
            existingUser.setSurname(updatedData.getSurname());
            // personId a uuid ponecháme z původního záznamu
            return userRepository.save(existingUser);
        } else {
            throw new RuntimeException("Nelze upravit: Uživatel s ID " + updatedData.getId() + " neexistuje.");
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








