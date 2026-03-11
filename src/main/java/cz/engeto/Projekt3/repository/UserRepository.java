package cz.engeto.Projekt3.repository;

import cz.engeto.Projekt3.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Tato metoda zkontroluje, zda již v databázi existuje uživatel
     * s konkrétním personId. Spring Boot podle názvu metody
     * automaticky vygeneruje SQL dotaz:
     * SELECT COUNT(*) FROM user WHERE person_id = ?
     */
    boolean existsByPersonId(String personId);
    }
