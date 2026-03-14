package cz.engeto.Projekt3.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Oseterni pro chyby @NotBlank, hlida vstupni data z postmanu
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidation(MethodArgumentNotValidException ex) {
        // Vezmeme první chybu, která nastala
        String zprava = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        // Vrátíme ji jako text s kódem 400 (Bad Request)
        return new ResponseEntity<>(zprava, HttpStatus.BAD_REQUEST);
    }


    // Kontrola logickych chyb, throw new RuntimeException napr. nenalezeni uzivatele
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Kontrola duplicity v databázi (reguje na @Column(unique = true) t.j. např. stejné personId)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleConflict(DataIntegrityViolationException ex) {
        return new ResponseEntity<>("Data již v databázi existují (duplicitní klíč).", HttpStatus.CONFLICT);
    }

}
