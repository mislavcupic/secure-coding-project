-- Prvo dropamo ako već postoje (radi testiranja)
DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS seminar;
DROP TABLE IF EXISTS student;

-- Kreiramo tablice
CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) NOT NULL
);

CREATE TABLE seminar (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         topic VARCHAR(255) NOT NULL,
                         lecturer VARCHAR(255) NOT NULL
);

CREATE TABLE registration (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              student_id BIGINT,
                              seminar_id BIGINT,
                              registered_at TIMESTAMP,
                              CONSTRAINT fk_registration_student FOREIGN KEY (student_id) REFERENCES student(id),
                              CONSTRAINT fk_registration_seminar FOREIGN KEY (seminar_id) REFERENCES seminar(id)
);

-- Kreiranje tablice USERS
CREATE TABLE USERS (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY, -- Koristimo BIGINT za ID, AUTO_INCREMENT za automatsko generiranje
                       username VARCHAR(255) NOT NULL UNIQUE, -- Korisničko ime, mora biti jedinstveno
                       password VARCHAR(255) NOT NULL -- Hashirana lozinka
);

-- Kreiranje tablice ROLES
CREATE TABLE ROLES (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY, -- ID uloga
                       name VARCHAR(255) NOT NULL UNIQUE -- Naziv uloge (npr. 'USER', 'ADMIN'), mora biti jedinstven
);

-- Kreiranje spojne tablice ROLE_USER za many-to-many vezu između USERS i ROLES
CREATE TABLE ROLE_USER (
                           application_user_id BIGINT NOT NULL, -- ID korisnika
                           role_id BIGINT NOT NULL, -- ID uloge
                           PRIMARY KEY (application_user_id, role_id), -- Kompozitni primarni ključ

    -- Definiranje stranih ključeva
                           FOREIGN KEY (application_user_id) REFERENCES USERS(id) ON DELETE CASCADE,
                           FOREIGN KEY (role_id) REFERENCES ROLES(id) ON DELETE CASCADE
);