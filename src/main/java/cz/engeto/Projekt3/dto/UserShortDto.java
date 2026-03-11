package cz.engeto.Projekt3.dto;

public class UserShortDto {
        private int id;
        private String name;
        private String surname;

        // Tento prázdný konstruktor je dobrým zvykem (pro knihovny jako Jackson)
        public UserShortDto() {
        }

        // Tento konstruktor používáte v UserService: new UserShortDTO(u.getId(), u.getName(), u.getSurname())
        public UserShortDto(int id, String name, String surname) {
            this.id = id;
            this.name = name;
            this.surname = surname;
        }

        // Gettery jsou nezbytné, aby Spring mohl vyrobit JSON pro Postmana
        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }

        // Settery (volitelné, ale doporučené)
        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
    }
