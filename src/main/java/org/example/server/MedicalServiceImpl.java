package org.example.server;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.*;
import org.example.shared.*;
import org.example.server.MedicalService;

public class MedicalServiceImpl extends UnicastRemoteObject implements MedicalService {
    protected MedicalServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Doctor addDoctor(Doctor doc) throws RemoteException {
        String sql = "INSERT INTO doctors(name,specialty,cedula,email) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, doc.getName());
            ps.setString(2, doc.getSpecialty());
            ps.setString(3, doc.getCedula());
            ps.setString(4, doc.getEmail());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) doc.setId(rs.getInt(1));
            return doc;
        } catch (Exception e) {
            throw new RemoteException("Error al agregar médico", e);
        }
    }

    @Override
    public List<Doctor> getAllDoctors() throws RemoteException {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT * FROM doctors";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Doctor d = new Doctor();
                d.setId(rs.getInt("id"));
                d.setName(rs.getString("name"));
                d.setSpecialty(rs.getString("specialty"));
                d.setCedula(rs.getString("cedula"));
                d.setEmail(rs.getString("email"));
                list.add(d);
            }
            return list;
        } catch (Exception e) {
            throw new RemoteException("Error al listar médicos", e);
        }
    }

    @Override
    public boolean updateDoctor(Doctor doc) throws RemoteException {
        String sql = "UPDATE doctors SET name=?, specialty=?, cedula=?, email=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, doc.getName());
            ps.setString(2, doc.getSpecialty());
            ps.setString(3, doc.getCedula());
            ps.setString(4, doc.getEmail());
            ps.setInt(5, doc.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al actualizar médico", e);
        }
    }

    @Override
    public boolean deleteDoctor(int id) throws RemoteException {
        String sql = "DELETE FROM doctors WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al eliminar médico", e);
        }
    }



    @Override
    public Patient addPatient(Patient p) throws RemoteException {
        String sql = "INSERT INTO patients(name, curp, phone, email) VALUES(?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCurp());
            ps.setString(3, p.getPhone());
            ps.setString(4, p.getEmail());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                p.setId(rs.getInt(1));
            }
            return p;
        } catch (Exception e) {
            throw new RemoteException("Error al agregar paciente", e);
        }
    }

    @Override
    public List<Patient> getAllPatients() throws RemoteException {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM patients";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient p = new Patient();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCurp(rs.getString("curp"));
                p.setPhone(rs.getString("phone"));
                p.setEmail(rs.getString("email"));
                list.add(p);
            }
            return list;
        } catch (Exception e) {
            throw new RemoteException("Error al listar pacientes", e);
        }
    }

    @Override
    public boolean updatePatient(Patient p) throws RemoteException {
        String sql = "UPDATE patients SET name=?, curp=?, phone=?, email=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCurp());
            ps.setString(3, p.getPhone());
            ps.setString(4, p.getEmail());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al actualizar paciente", e);
        }
    }

    @Override
    public boolean deletePatient(int id) throws RemoteException {
        String sql = "DELETE FROM patients WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al eliminar paciente", e);
        }
    }

    // ——— CITA ———

    @Override
    public Appointment addAppointment(Appointment ap) throws RemoteException {
        String sql = "INSERT INTO appointments(date, time, reason, doctor_id, patient_id) VALUES(?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, new java.sql.Date(ap.getDate().getTime()));
            ps.setString(2, ap.getTime());
            ps.setString(3, ap.getReason());
            ps.setInt(4, ap.getDoctorId());
            ps.setInt(5, ap.getPatientId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                ap.setId(rs.getInt(1));
            }
            return ap;
        } catch (Exception e) {
            throw new RemoteException("Error al agregar cita", e);
        }
    }

    @Override
    public List<Appointment> getAllAppointments() throws RemoteException {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Appointment ap = new Appointment();
                ap.setId(rs.getInt("id"));
                ap.setDate(rs.getDate("date"));
                ap.setTime(rs.getString("time"));
                ap.setReason(rs.getString("reason"));
                ap.setDoctorId(rs.getInt("doctor_id"));
                ap.setPatientId(rs.getInt("patient_id"));
                list.add(ap);
            }
            return list;
        } catch (Exception e) {
            throw new RemoteException("Error al listar citas", e);
        }
    }

    @Override
    public boolean updateAppointment(Appointment ap) throws RemoteException {
        String sql = "UPDATE appointments SET date=?, time=?, reason=?, doctor_id=?, patient_id=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, new java.sql.Date(ap.getDate().getTime()));
            ps.setString(2, ap.getTime());
            ps.setString(3, ap.getReason());
            ps.setInt(4, ap.getDoctorId());
            ps.setInt(5, ap.getPatientId());
            ps.setInt(6, ap.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al actualizar cita", e);
        }
    }

    @Override
    public boolean deleteAppointment(int id) throws RemoteException {
        String sql = "DELETE FROM appointments WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RemoteException("Error al eliminar cita", e);
        }
    }

// Implementa PACIENTE y CITA de forma análoga...

}
