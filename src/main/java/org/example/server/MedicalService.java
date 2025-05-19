package org.example.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import org.example.shared.Doctor;
import org.example.shared.Patient;
import org.example.shared.Appointment;

public interface MedicalService extends Remote {
    // MÉDICO
    Doctor addDoctor(Doctor doc) throws RemoteException;
    List<Doctor> getAllDoctors() throws RemoteException;
    boolean updateDoctor(Doctor doc) throws RemoteException;
    boolean deleteDoctor(int id) throws RemoteException;
    // PACIENTE
    Patient addPatient(Patient p) throws RemoteException;
    List<Patient> getAllPatients() throws RemoteException;
    boolean updatePatient(Patient p) throws RemoteException;
    boolean deletePatient(int id) throws RemoteException;
    // CITA
    Appointment addAppointment(Appointment ap) throws RemoteException;
    List<Appointment> getAllAppointments() throws RemoteException;
    boolean updateAppointment(Appointment ap) throws RemoteException;
    boolean deleteAppointment(int id) throws RemoteException;
}