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
        testUser.setPersonId("ID_ZE_SOUB_12");
    }

    // --- TESTY PRO INSERT (POST) ---

    @Test
    @DisplayName("1. Uložení: Uživatel musí dostat vygenerované ID")
    void SaveUserTest_Id() {
        User newUser = userService.saveUser(testUser);
        assertThat(newUser.getId()).isPositive();
    }

    @Test
    @DisplayName("2. Uložení: Uživatel musí mít načtené PersonId")
    void saveUserTest_PersonId() {
        User newUser = userService.saveUser(testUser);
        assertThat(newUser.getPersonId()).as("PersonId ze souboru").isNotNull();
    }

    @Test
    @DisplayName("3. Uložení: Uživatel musí mít načtené UUID")
    void saveUserTest_UUID() {
        User newUser = userService.saveUser(testUser);
        assertThat(newUser.getUuid()).as("Generované UUID").isNotNull();
    }

    @Test
    @DisplayName("4. Uložení: Jméno se musí správně uložit do DB")
    void saveUserTest_Name() {
        User newUser = userService.saveUser(testUser);
        assertThat(newUser.getName()).isEqualTo("Jana");
    }

    @Test
    @DisplayName("5. Uložení: Příjmení se musí správně uložit do DB")
    void saveUserTest_Surname() {
        User newUser = userService.saveUser(testUser);
        assertThat(newUser.getSurname()).isEqualTo("Pokusná");
    }

    @Test
    @DisplayName("6. Validace: Vyhodí chybu, pokud personnID není v souboru")
    void saveUserTest_PersonID() {
        // Připravíme uživatele s personID, které v není souboru
        boolean isError = false;
        testUser.setPersonId("PERSON_ID111");

        // Zkusím ulozit to chybne PERSON_ID111 a chyba bude, jestli to projde
        try {
            userService.saveUser(testUser); // zkusim ulozit spatne id

        } catch (RuntimeException ex) {
            isError = true;
            // Tady zachytíme regulernu tu konkretni chybu
            assertThat(ex.getMessage()).contains("není v seznamu povolených");
        }
        assertThat(isError).as("Test selhal, protože se nevyhodila žádná chyba!").isTrue();
    }


    // --- TESTY PRO SELECT (GET) ---

    @Test
    @DisplayName("7. Select: Vyhodí chybu, pokud ID neexistuje")
    void getUserTest_ID() {
        assertThrows(RuntimeException.class, () -> userService.getUserById(9999));
    }

    @Test
    @DisplayName("8. Select: Musí vrátit seznam DTO, pokud v DB existuje uživatel")
    void getUserTest_AllDTO() {
        userService.saveUser(testUser); // Uložíme pro účely tohoto testu

        List<UserShortDto> list = userService.getAllUsersBasic();

        assertThat(list).isNotEmpty();
        assertThat(list.get(0)).isInstanceOf(UserShortDto.class);
    }

    // --- TESTY PRO UPDATE (PUT) ---

    @Test
    @DisplayName("9. Update: Změní jméno uživatele")
    void updateUserTest_Name() {
        User newUser = userService.saveUser(testUser);

        User updateUser = new User();
        updateUser.setId(newUser.getId());
        updateUser.setName("Zmenene_Jmeno");

        User user = userService.updateUser(updateUser);
        assertThat(user.getName()).isEqualTo("Zmenene_Jmeno");
    }

    @Test
    @DisplayName("10. Update: Změní přijmení uživatele")
    void updateUserTest_Surname() {
        User newUser = userService.saveUser(testUser);

        User updateUser = new User();
        updateUser.setId(newUser.getId());
        updateUser.setSurname("Zmenene_Prijmeni");

        User user = userService.updateUser(updateUser);
        assertThat(user.getSurname()).isEqualTo("Zmenene_Prijmeni");
    }


    @Test
    @DisplayName("11. Update: Změna PersonId na posleni platne personID ze souboru")
    void updateUserTest_PersonId_Valid() {
        User newUser = userService.saveUser(testUser);

        User updateUser = new User();
        updateUser.setId(newUser.getId());
        updateUser.setName(newUser.getName());
        updateUser.setSurname(newUser.getSurname());

        // Změníme personID na posledni personID ze souboru
        updateUser.setPersonId("mY6sT1jA3cLz");

        User user = userService.updateUser(updateUser);
        assertThat(user.getPersonId()).isEqualTo("mY6sT1jA3cLz");
    }


    @Test
    @DisplayName("12. Update: Nesmí dovolit změnu UUID")
    void updateUserTest_UUID() {
        User saved = userService.saveUser(testUser);
        String originalUuid = saved.getUuid();

        User updateUser = new User();
        updateUser.setId(saved.getId());
        updateUser.setUuid("testovaci_UUID");

        User user = userService.updateUser(updateUser);

        assertThat(user.getUuid()).isEqualTo(originalUuid);
    }


    // --- TESTY PRO DELETE ---

    @Test
    @DisplayName("13. Delete: Po smazání uživatel nesmí existovat v DB")
    void deleteUserTest() {
        User saved = userService.saveUser(testUser);
        int id = saved.getId();

        userService.deleteUser(id);

        assertThat(userRepository.existsById(id)).isFalse();
    }

}
