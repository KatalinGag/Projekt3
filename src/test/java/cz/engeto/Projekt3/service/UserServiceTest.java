package cz.engeto.Projekt3.service;

import cz.engeto.Projekt3.dto.UserShortDto;
import cz.engeto.Projekt3.model.User;
import cz.engeto.Projekt3.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // Spustí aplikaci pro testování
@Transactional   // Po každém testu odroluje změny


public class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // --- TESTY PRO ZALOŽENÍ (POST) ---

    @Test
    @DisplayName("1. Úspěšné uložení: Uživatel musí dostat ID z DB a PersonId ze souboru")
    void testSaveUser_Success() {
        // PŘÍPRAVA
        User user = new User();
        user.setName("Jan");
        user.setSurname("Novák");

        // AKCE
        User saved = userService.saveUser(user);

        // OVĚŘENÍ
        assertThat(saved.getId()).isPositive();            // Má vygenerované ID?
        assertThat(saved.getPersonId()).isNotNull();       // Načetlo se ID ze souboru?
        assertThat(saved.getUuid()).isNotNull();           // Vygenerovalo se UUID?
        assertThat(saved.getName()).isEqualTo("Jan");      // Sedí jméno?
    }

    // --- TESTY PRO ZÍSKÁNÍ DAT (GET) ---

    @Test
    @DisplayName("2. Detail uživatele: Vyhodí chybu, pokud ID neexistuje")
    void testGetUserById_NotFound() {
        // AKCE & OVĚŘENÍ
        assertThrows(RuntimeException.class, () -> {
                userService.getUserById(9999); // ID 9999 v DB určitě není
        });
    }

    @Test
    @DisplayName("3. Seznam DTO: Musí vrátit správný typ dat (UserShortDto)")
    void testGetAllUsersBasic() {
        // PŘÍPRAVA: Uložíme jednoho uživatele
        userService.saveUser(new User());

        // AKCE
        List<UserShortDto> list = userService.getAllUsersBasic();

        // OVĚŘENÍ
        assertThat(list).isNotEmpty();
        assertThat(list.get(0)).isInstanceOf(UserShortDto.class);
    }

    // --- TESTY PRO ÚPRAVU (PUT) ---

    @Test
    @DisplayName("4. Update: Musí změnit jméno, ale zachovat původní PersonId a UUID")
    void testUpdateUser_Security() {
        // PŘÍPRAVA: Uložíme uživatele a zapamatujeme si jeho ID ze souboru
        User original = userService.saveUser(new User());
        String originalPersonId = original.getPersonId();
        String originalUuid = original.getUuid();

        // AKCE: Zkusíme změnit jméno a podvrhnout falešné PersonId
        User updateData = new User();
        updateData.setId(original.getId());
        updateData.setName("Změněné Jméno");
        updateData.setPersonId("FALEŠNÉ-ID-999");

        User updated = userService.updateUser(updateData);

        // OVĚŘENÍ
        assertThat(updated.getName()).isEqualTo("Změněné Jméno");
        assertThat(updated.getPersonId()).isEqualTo(originalPersonId); // Zůstalo původní!
        assertThat(updated.getUuid()).isEqualTo(originalUuid);         // Zůstalo původní!
    }

    // --- TESTY PRO MAZÁNÍ (DELETE) ---

    @Test
    @DisplayName("5. Delete: Uživatel musí po smazání zmizet z databáze")
    void testDeleteUser() {
        // PŘÍPRAVA
        User saved = userService.saveUser(new User());
        int id = saved.getId();

        // AKCE
        userService.deleteUser(id);

        // OVĚŘENÍ
        assertThat(userRepository.existsById(id)).isFalse();
    }
}

