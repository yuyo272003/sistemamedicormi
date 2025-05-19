package org.example.client;

import org.example.server.MedicalService;
import org.example.shared.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final MedicalService service;

    private final DefaultListModel<Doctor> doctorModel = new DefaultListModel<>();
    private final DefaultListModel<Patient> patientModel = new DefaultListModel<>();
    private final DefaultListModel<Appointment> appointmentModel = new DefaultListModel<>();
    private final Map<Integer,String> doctorNames  = new HashMap<>();
    private final Map<Integer,String> patientNames = new HashMap<>();

    public MainFrame(MedicalService service) {
        super("Gestión Médico · Paciente · Citas");
        this.service = service;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Médicos", createDoctorPanel());
        tabs.addTab("Pacientes", createPatientPanel());
        tabs.addTab("Citas", createAppointmentPanel());
        add(tabs, BorderLayout.CENTER);
    }

    // ——— Panel Médicos ———
    private JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JList<Doctor> list = new JList<>(doctorModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        add.addActionListener(e -> showAddDoctorDialog());
        del.addActionListener(e -> {
            Doctor d = list.getSelectedValue();
            if (d != null) { deleteDoctor(d.getId()); loadDoctors(); }
        });

        JPanel btns = new JPanel();
        btns.add(add);
        btns.add(del);
        panel.add(btns, BorderLayout.SOUTH);

        loadDoctors();
        return panel;
    }

    private void loadDoctors() {
        try {
            doctorModel.clear();
            doctorNames.clear();
            List<Doctor> docs = service.getAllDoctors();
            for (Doctor d : docs) {
                doctorModel.addElement(d);
                doctorNames.put(d.getId(), d.getName());
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void showAddDoctorDialog() {
        JTextField name = new JTextField(), spec = new JTextField(),
                ced = new JTextField(), mail = new JTextField();
        JPanel p = new JPanel(new GridLayout(4,2));
        p.add(new JLabel("Nombre:"));     p.add(name);
        p.add(new JLabel("Especialidad:"));p.add(spec);
        p.add(new JLabel("Cédula:"));      p.add(ced);
        p.add(new JLabel("Email:"));       p.add(mail);

        if (JOptionPane.showConfirmDialog(this,p,"Nuevo Médico",
                JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                Doctor d = new Doctor();
                d.setName(name.getText());
                d.setSpecialty(spec.getText());
                d.setCedula(ced.getText());
                d.setEmail(mail.getText());
                service.addDoctor(d);
                loadDoctors();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deleteDoctor(int id) {
        try { service.deleteDoctor(id); }
        catch (Exception ex) { showError(ex); }
    }


    // ——— Panel Pacientes ———
    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JList<Patient> list = new JList<>(patientModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        add.addActionListener(e -> showAddPatientDialog());
        del.addActionListener(e -> {
            Patient p = list.getSelectedValue();
            if (p != null) { deletePatient(p.getId()); loadPatients(); }
        });

        JPanel btns = new JPanel();
        btns.add(add);
        btns.add(del);
        panel.add(btns, BorderLayout.SOUTH);

        loadPatients();
        return panel;
    }

    private void loadPatients() {
        try {
            patientModel.clear();
            patientNames.clear();
            List<Patient> pts = service.getAllPatients();
            for (Patient p : pts) {
                patientModel.addElement(p);
                patientNames.put(p.getId(), p.getName());
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void showAddPatientDialog() {
        JTextField name = new JTextField(), curp = new JTextField(),
                phone = new JTextField(), mail = new JTextField();
        JPanel p = new JPanel(new GridLayout(4,2));
        p.add(new JLabel("Nombre:")); p.add(name);
        p.add(new JLabel("CURP:"));   p.add(curp);
        p.add(new JLabel("Teléfono:"));p.add(phone);
        p.add(new JLabel("Email:"));   p.add(mail);

        if (JOptionPane.showConfirmDialog(this,p,"Nuevo Paciente",
                JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                Patient pt = new Patient();
                pt.setName(name.getText());
                pt.setCurp(curp.getText());
                pt.setPhone(phone.getText());
                pt.setEmail(mail.getText());
                service.addPatient(pt);
                loadPatients();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deletePatient(int id) {
        try { service.deletePatient(id); }
        catch (Exception ex) { showError(ex); }
    }


    // ——— Panel Citas ———
    private JPanel createAppointmentPanel() {
        // ① Asegúrate de tener los nombres cargados:
        loadDoctors();
        loadPatients();

        // ② Crea el panel y el JList:
        JPanel panel = new JPanel(new BorderLayout());
        JList<Appointment> list = new JList<>(appointmentModel);

        // ③ Configura el renderer para mostrar nombres en vez de IDs:
        list.setCellRenderer(new DefaultListCellRenderer() {
            private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                Appointment ap = (Appointment) value;
                String dName = doctorNames.getOrDefault(ap.getDoctorId(), "Dr#"+ap.getDoctorId());
                String pName = patientNames.getOrDefault(ap.getPatientId(), "Pt#"+ap.getPatientId());
                String date  = fmt.format(ap.getDate());
                String text  = String.format("%s %s — %s (Dr: %s, Pt: %s)",
                        date, ap.getTime(), ap.getReason(),
                        dName, pName);
                // delega colores, selección, fuente, etc. a la implementación base:
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });

        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton add = new JButton("Agregar");
        JButton del = new JButton("Eliminar");
        // … tus listeners de add y del …

        JPanel btns = new JPanel();
        btns.add(add);
        btns.add(del);
        panel.add(btns, BorderLayout.SOUTH);

        loadAppointments();
        return panel;
    }



    private void loadAppointments() {
        try {
            appointmentModel.clear();
            List<Appointment> apps = service.getAllAppointments();
            apps.forEach(appointmentModel::addElement);
        } catch (Exception ex) { showError(ex); }
    }

    private void showAddAppointmentDialog() {
        // Podrías poblar dropdowns con doctors y patients para elegir IDs...
        JTextField date = new JTextField(), time = new JTextField(),
                reason = new JTextField(), docId = new JTextField(),
                patId = new JTextField();
        JPanel p = new JPanel(new GridLayout(5,2));
        p.add(new JLabel("Fecha (YYYY-MM-DD):")); p.add(date);
        p.add(new JLabel("Hora (HH:MM:SS):"));    p.add(time);
        p.add(new JLabel("Motivo:"));             p.add(reason);
        p.add(new JLabel("Doctor ID:"));          p.add(docId);
        p.add(new JLabel("Paciente ID:"));        p.add(patId);

        if (JOptionPane.showConfirmDialog(this,p,"Nueva Cita",
                JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION) {
            try {
                Appointment ap = new Appointment();
                ap.setDate(java.sql.Date.valueOf(date.getText()));
                ap.setTime(time.getText());
                ap.setReason(reason.getText());
                ap.setDoctorId(Integer.parseInt(docId.getText()));
                ap.setPatientId(Integer.parseInt(patId.getText()));
                service.addAppointment(ap);
                loadAppointments();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deleteAppointment(int id) {
        try { service.deleteAppointment(id); }
        catch (Exception ex) { showError(ex); }
    }

    // ——— Utilería ———
    private void showError(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}