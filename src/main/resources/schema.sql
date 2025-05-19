CREATE DATABASE medicaldb;
USE medicaldb;

CREATE TABLE doctors (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(100),
                         specialty VARCHAR(100),
                         cedula VARCHAR(50) UNIQUE,
                         email VARCHAR(100)
);

CREATE TABLE patients (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(100),
                          curp VARCHAR(18) UNIQUE,
                          phone VARCHAR(15),
                          email VARCHAR(100)
);

CREATE TABLE appointments (
                              id INT AUTO_INCREMENT PRIMARY KEY,
                              date DATE,
                              time TIME,
                              reason VARCHAR(255),
                              doctor_id INT,
                              patient_id INT,
                              FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                              FOREIGN KEY (patient_id) REFERENCES patients(id)
);
