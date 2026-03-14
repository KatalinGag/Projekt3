package cz.engeto.Projekt3.service;

import cz.engeto.Projekt3.dto.UserShortDto;
import cz.engeto.Projekt3.model.User;
import cz.engeto.Projekt3.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // Spustí aplikaci pro testování
@Transactional   // Po každém testu odroluje změny DB, rollback

public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void initTest() {
        // Před každým testem připravíme čerstvá data
        testUser = new User();
        testUser.setName("Jana");
        testUser.setSurname("Pokusná");
    }

    // --- TESTY PRO INSERT (POST) ---

    @Test
    @DisplayName("1. Uložení: Uživatel musí dostat vygenerované ID")
    void SaveUserTest_Id() {
        User saved = userService.saveUser(testUser);
        assertThat(saved.getId()).isPositive();
    }

    @Test
    @DisplayName("2. Uložení: Uživatel musí mít načtené PersonId")
    void saveUserTest_PersonId() {
        User saved = userService.saveUser(testUser);
        assertThat(saved.getPersonId()).as("PersonId ze souboru").isNotNull();
    }

    @Test
    @DisplayName("3. Uložení: Uživatel musí mít načtené UUID")
    void saveUserTest_UUID() {
        User saved = userService.saveUser(testUser);
        assertThat(saved.getUuid()).as("Generované UUID").isNotNull();
    }

    @Test
    @DisplayName("4. Uložení: Jméno se musí správně uložit do DB")
    void saveUserTest_Name() {
        User saved = userService.saveUser(testUser);
        assertThat(saved.getName()).isEqualTo("Jana");
    }

    @Test
    @DisplayName("5. Uložení: Příjmení se musí správně uložit do DB")
    void saveUserTest_Surname() {
        User saved = userService.saveUser(testUser);
        assertThat(saved.getSurname()).isEqualTo("Pokusná");
    }


    // --- TESTY PRO SELECT (GET) ---

    @Test
    @DisplayName("6. Select: Vyhodí chybu, pokud ID neexistuje")
    void getUserTest_ID() {
        assertThrows(RuntimeException.class, () -> userService.getUserById(9999));
    }

    @Test
    @DisplayName("7. Select: Musí vrátit seznam DTO, pokud v DB existuje uživatel")
    void getUserTest_AllDTO() {
        userService.saveUser(testUser); // Uložíme pro účely tohoto testu

        List<UserShortDto> list = userService.getAllUsersBasic();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0)).isInstanceOf(UserShortDto.class);
    }

    // --- TESTY PRO UPDATE (PUT) ---

    @Test
    @DisplayName("8. Update: Změní jméno uživatele")
    void updateUserTest_Name() {
        User saved = userService.saveUser(testUser);

        User updateUser = new User();
        updateUser.setId(saved.getId());
        updateUser.setName("Zmenene_Jmeno");

        User updated = userService.updateUser(updateUser);
        assertThat(updated.getName()).isEqualTo("Zmenene_Jmeno");
    }


    @Test
    @DisplayName("9. Update: Změní přijmení uživatele")
    void updateUserTest_Surname() {
        User saved = userService.saveUser(testUser);

        User updateUser = new User();
        updateUser.setId(saved.getId());
        updateUser.setSurname("Zmenene_Prijmeni");

        User updated = userService.updateUser(updateUser);
        assertThat(updated.getSurname()).isEqualTo("Zmenene_Prijmeni");
    }


    @Test
    @DisplayName("10. Update: Nesmí dovolit změnu PersonId")
    void updateUserTest_PersonId() {
        User saved = userService.saveUser(testUser);
        String originalPersonId = saved.getPersonId();

        User updateUser = new User();
        updateUser.setId(saved.getId());
        updateUser.setPersonId("testovaci_ID");

        User updated = userService.updateUser(updateUser);

        assertThat(updated.getPersonId()).isEqualTo(originalPersonId);
    }


    @Test
    @DisplayName("11. Update: Nesmí dovolit změnu UUID")
    void updateUserTest_UUID() {
        User saved = userService.saveUser(testUser);
        String originalUuid = saved.getUuid();

        User updateUser = new User();
        updateUser.setId(saved.getId());
        updateUser.setUuid("testovaci_UUID");

        User updated = userService.updateUser(updateUser);

        assertThat(updated.getUuid()).isEqualTo(originalUuid);
    }


    // --- TESTY PRO DELETE ---

    @Test
    @DisplayName("12. Delete: Po smazání uživatel nesmí existovat v DB")
    void deleteUserTest() {
        User saved = userService.saveUser(testUser);
        int id = saved.getId();

        userService.deleteUser(id);

        assertThat(userRepository.existsById(id)).isFalse();
    }
}
