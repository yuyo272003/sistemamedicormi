-- 1) Elimina tablas antiguas (en orden: primero la que depende)
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;

-- 2) Crea tabla MÉDICOS
CREATE TABLE doctors (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100)    NOT NULL,
                         specialty VARCHAR(100),
                         cedula VARCHAR(50)   UNIQUE,
                         email VARCHAR(100)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;

-- 3) Crea tabla PACIENTES
CREATE TABLE patients (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100)    NOT NULL,
                          curp VARCHAR(18)     UNIQUE,
                          phone VARCHAR(15),
                          email VARCHAR(100)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;

-- 4) Crea tabla CITAS con claves foráneas en cascada
CREATE TABLE appointments (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              date DATE            NOT NULL,
                              `time` TIME          NOT NULL,
                              reason VARCHAR(255),
                              doctor_id INT        NOT NULL,
                              patient_id INT       NOT NULL,
                              CONSTRAINT fk_appointments_doctor
                                  FOREIGN KEY (doctor_id)
                                      REFERENCES doctors(id)
                                      ON DELETE CASCADE,
                              CONSTRAINT fk_appointments_patient
                                  FOREIGN KEY (patient_id)
                                      REFERENCES patients(id)
                                      ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4;
