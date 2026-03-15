package cz.engeto.Projekt3.service;

import cz.engeto.Projekt3.dto.UserCreateDto;
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

    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    private UserCreateDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserCreateDto("Jana", "Pokusná", "jXa4g3H7oPq2");
    }

    // --- 1. SKUPINA: POST, INSERT ---

    @Test
    @DisplayName("1. Uložení: Musí se vygenerovat ID")
    void testSave_GeneratesId() {
        User saved = userService.saveUser(userDto);
        assertThat(saved.getId()).isPositive();
    }

    @Test
    @DisplayName("2. Uložení: Musí se vygenerovat UUID")
    void testSave_GeneratesUuid() {
        User saved = userService.saveUser(userDto);
        assertThat(saved.getUuid()).isNotNull();
    }

    // --- 2. SKUPINA: CHYBY (VALIDACE) ---

    @Test
    @DisplayName("3. Chyba: PersonId není v povoleném souboru")
    void testSave_InvalidPersonId() {
        userDto.setPersonId("NEZNAMY_KOD");
        assertThrows(RuntimeException.class, () -> userService.saveUser(userDto));
    }

    @Test
    @DisplayName("4. Chyba: Nelze uložit duplicitní PersonId")
    void testSave_DuplicatePersonId() {
        userService.saveUser(userDto); // První uložení
        assertThrows(RuntimeException.class, () -> userService.saveUser(userDto)); // Druhé musí selhat
    }

    // --- 3. SKUPINA: PUT, UPDATE ---
    @Test
    @DisplayName("5. Update: Úspěšná změna jména")
    void testUpdate_ChangesName() {
        User saved = userService.saveUser(userDto);
        UserShortDto updateDto = new UserShortDto(saved.getId(), "NoveJmeno", "Pokusná");

        User updated = userService.updateUser(updateDto);
        assertThat(updated.getName()).isEqualTo("NoveJmeno");
    }

    @Test
    @DisplayName("6. Update: Chyba při neexistujícím ID")
    void testUpdate_NonExistentId() {
        UserShortDto updateDto = new UserShortDto(999, "Jan", "Novak");
        assertThrows(RuntimeException.class, () -> userService.updateUser(updateDto));
    }

    // --- 4. SKUPINA: DELETE, MAZÁNÍ ---

    @Test
    @DisplayName("7. Smazání: Uživatel po smazání neexistuje v DB")
    void testDelete_RemovesFromDb() {
        User saved = userService.saveUser(userDto);
        userService.deleteUser(saved.getId());
        assertThat(userRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    @DisplayName("8. Smazání: Chyba při mazání neexistujícího ID")
    void testDelete_NonExistentId() {
        assertThrows(RuntimeException.class, () -> userService.deleteUser(888));
    }

     // --- 5. SKUPINA: GET, SELECT ---
    @Test
    @DisplayName("9. Select: Chyba při hledání neexistujícího ID")
    void testGet_NonExistentId() {
        assertThrows(RuntimeException.class, () -> userService.getUserById(777));
    }

    @Test
    @DisplayName("10. Select: Seznam ShortDto není prázdný po uložení")
    void testGetAll_ReturnsData() {
        userService.saveUser(userDto);
        assertThat(userService.getAllUsersBasic()).isNotEmpty();
    }

}
