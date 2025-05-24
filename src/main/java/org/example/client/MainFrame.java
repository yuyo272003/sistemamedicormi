package org.example.client;

import org.example.server.MedicalService;
import org.example.shared.Appointment;
import org.example.shared.Doctor;
import org.example.shared.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private final MedicalService service;
    private List<Doctor> doctors = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    private final Map<Integer, String> doctorNames  = new HashMap<>();
    private final Map<Integer, String> patientNames = new HashMap<>();

    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font CELL_FONT   = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Color ACCENT      = new Color(52, 152, 219);
    private static final Color BUTTON_BG   = new Color(41, 128, 185);
    private static final Color BG          = new Color(245, 245, 245);

    public MainFrame(MedicalService service) {
        super("Sistema Médico");
        this.service = service;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}
        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel header = new JPanel();
        header.setBackground(ACCENT);
        header.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("Sistema de Gestión Médica");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.add(title);
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(CELL_FONT);
        tabs.setBackground(BG);
        tabs.addTab("Médicos",    createDoctorPanel());
        tabs.addTab("Pacientes",  createPatientPanel());
        tabs.addTab("Citas",      createAppointmentPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createDoctorPanel() {
        String[] cols = {"Nombre", "Especialidad", "Email"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        loadDoctors();
        reloadDoctorsTable(model);

        JButton add  = mkButton("Agregar",  e -> { showAddDoctorDialog();    reloadDoctorsTable(model); });
        JButton edit = mkButton("Editar",   e -> {
            int r = table.getSelectedRow();
            if (r>=0) { showUpdateDoctorDialog(doctors.get(r)); reloadDoctorsTable(model); }
        });
        JButton del  = mkButton("Eliminar", e -> {
            int r = table.getSelectedRow();
            if (r>=0) { deleteDoctor(doctors.get(r).getId()); reloadDoctorsTable(model); }
        });

        return panelWith(table, add, edit, del);
    }

    private JPanel createPatientPanel() {
        String[] cols = {"Nombre", "CURP", "Teléfono", "Email"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        loadPatients();
        reloadPatientsTable(model);

        JButton add  = mkButton("Agregar",  e -> { showAddPatientDialog();   reloadPatientsTable(model); });
        JButton edit = mkButton("Editar",   e -> {
            int r = table.getSelectedRow();
            if (r>=0) { showUpdatePatientDialog(patients.get(r)); reloadPatientsTable(model); }
        });
        JButton del  = mkButton("Eliminar", e -> {
            int r = table.getSelectedRow();
            if (r>=0) { deletePatient(patients.get(r).getId()); reloadPatientsTable(model); }
        });

        return panelWith(table, add, edit, del);
    }

    private JPanel createAppointmentPanel() {
        String[] cols = {"Fecha", "Hora", "Motivo", "Doctor", "Paciente"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        styleTable(table);

        // cargar nombres de doctors y patients para mostrar en tabla
        loadDoctors();
        loadPatients();
        loadAppointments();
        reloadAppointmentsTable(model);

        JButton add    = mkButton("Agregar",    e -> { showAddAppointmentDialog(); reloadAppointmentsTable(model); });
        JButton edit   = mkButton("Editar",     e -> {
            int r = table.getSelectedRow();
            if (r>=0) { showUpdateAppointmentDialog(appointments.get(r)); reloadAppointmentsTable(model); }
        });
        JButton del    = mkButton("Eliminar",   e -> {
            int r = table.getSelectedRow();
            if (r>=0) { deleteAppointment(appointments.get(r).getId()); reloadAppointmentsTable(model); }
        });

        return panelWith(table, add, edit, del);
    }

    private JPanel panelWith(JTable table, JButton add, JButton edit, JButton del) {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        bar.setBackground(BG);
        bar.add(add); bar.add(edit); bar.add(del);
        p.add(bar, BorderLayout.SOUTH);
        return p;
    }

    private void styleTable(JTable t) {
        t.setFont(CELL_FONT);
        t.setRowHeight(28);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));
        JTableHeader h = t.getTableHeader();
        h.setFont(HEADER_FONT);
        h.setBackground(ACCENT);
        h.setForeground(Color.WHITE);
    }

    private JButton mkButton(String text, ActionListener l) {
        JButton b = new JButton(text);
        b.setFont(CELL_FONT);
        b.setBackground(BUTTON_BG);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.addActionListener(l);
        return b;
    }

    private void loadDoctors() {
        try { doctors = service.getAllDoctors(); }
        catch (Exception ex) { showError(ex); }
    }
    private void reloadDoctorsTable(DefaultTableModel m) {
        m.setRowCount(0);
        doctors.forEach(d ->
                m.addRow(new Object[]{d.getName(), d.getSpecialty(), d.getEmail()})
        );
    }

    private void loadPatients() {
        try { patients = service.getAllPatients(); }
        catch (Exception ex) { showError(ex); }
    }
    private void reloadPatientsTable(DefaultTableModel m) {
        m.setRowCount(0);
        patients.forEach(p ->
                m.addRow(new Object[]{p.getName(), p.getCurp(), p.getPhone(), p.getEmail()})
        );
    }

    private void loadAppointments() {
        try {
            appointments = service.getAllAppointments();
            doctorNames.clear(); patientNames.clear();
            for (Doctor d : doctors)   doctorNames.put(d.getId(), d.getName());
            for (Patient p : patients) patientNames.put(p.getId(), p.getName());
        } catch (Exception ex) { showError(ex); }
    }
    private void reloadAppointmentsTable(DefaultTableModel m) {
        m.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        for (Appointment a : appointments) {
            String date = fmt.format(a.getDate());
            String dn   = doctorNames.getOrDefault(a.getDoctorId(), "");
            String pn   = patientNames.getOrDefault(a.getPatientId(), "");
            m.addRow(new Object[]{date, a.getTime(), a.getReason(), dn, pn});
        }
    }

    // —— MÉTODOS DE DIÁLOGO CON VALIDACIONES ——

    private void showAddDoctorDialog() {
        JTextField name = new JTextField(), spec = new JTextField(),
                ced  = new JTextField(), mail = new JTextField();
        JPanel panel = new JPanel(new GridLayout(4,2));
        panel.add(new JLabel("Nombre:"));      panel.add(name);
        panel.add(new JLabel("Especialidad:"));panel.add(spec);
        panel.add(new JLabel("Cédula:"));      panel.add(ced);
        panel.add(new JLabel("Email:"));       panel.add(mail);

        int op = JOptionPane.showConfirmDialog(this, panel, "Nuevo Médico",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() ||
                    spec.getText().trim().isEmpty() ||
                    ced.getText().trim().isEmpty() ||
                    mail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Doctor d = new Doctor();
                d.setName(name.getText().trim());
                d.setSpecialty(spec.getText().trim());
                d.setCedula(ced.getText().trim());
                d.setEmail(mail.getText().trim());
                service.addDoctor(d);
                loadDoctors();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void showUpdateDoctorDialog(Doctor d) {
        JTextField name = new JTextField(d.getName());
        JTextField spec = new JTextField(d.getSpecialty());
        JTextField ced  = new JTextField(d.getCedula());
        JTextField mail = new JTextField(d.getEmail());
        JPanel panel = new JPanel(new GridLayout(4,2));
        panel.add(new JLabel("Nombre:"));      panel.add(name);
        panel.add(new JLabel("Especialidad:"));panel.add(spec);
        panel.add(new JLabel("Cédula:"));      panel.add(ced);
        panel.add(new JLabel("Email:"));       panel.add(mail);

        int op = JOptionPane.showConfirmDialog(this, panel, "Editar Médico",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() ||
                    spec.getText().trim().isEmpty() ||
                    ced.getText().trim().isEmpty() ||
                    mail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                d.setName(name.getText().trim());
                d.setSpecialty(spec.getText().trim());
                d.setCedula(ced.getText().trim());
                d.setEmail(mail.getText().trim());
                service.updateDoctor(d);
                loadDoctors();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deleteDoctor(int id) {
        try { service.deleteDoctor(id); loadDoctors(); }
        catch (Exception ex) { showError(ex); }
    }

    private void showAddPatientDialog() {
        JTextField name  = new JTextField(), curp = new JTextField(),
                phone = new JTextField(), mail = new JTextField();
        JPanel panel = new JPanel(new GridLayout(4,2));
        panel.add(new JLabel("Nombre:"));   panel.add(name);
        panel.add(new JLabel("CURP:"));     panel.add(curp);
        panel.add(new JLabel("Teléfono:")); panel.add(phone);
        panel.add(new JLabel("Email:"));    panel.add(mail);

        int op = JOptionPane.showConfirmDialog(this, panel, "Nuevo Paciente",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() ||
                    curp.getText().trim().isEmpty() ||
                    phone.getText().trim().isEmpty() ||
                    mail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Patient p = new Patient();
                p.setName(name.getText().trim());
                p.setCurp(curp.getText().trim());
                p.setPhone(phone.getText().trim());
                p.setEmail(mail.getText().trim());
                service.addPatient(p);
                loadPatients();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void showUpdatePatientDialog(Patient p) {
        JTextField name  = new JTextField(p.getName());
        JTextField curp  = new JTextField(p.getCurp());
        JTextField phone = new JTextField(p.getPhone());
        JTextField mail  = new JTextField(p.getEmail());
        JPanel panel = new JPanel(new GridLayout(4,2));
        panel.add(new JLabel("Nombre:"));   panel.add(name);
        panel.add(new JLabel("CURP:"));     panel.add(curp);
        panel.add(new JLabel("Teléfono:")); panel.add(phone);
        panel.add(new JLabel("Email:"));    panel.add(mail);

        int op = JOptionPane.showConfirmDialog(this, panel, "Editar Paciente",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() ||
                    curp.getText().trim().isEmpty() ||
                    phone.getText().trim().isEmpty() ||
                    mail.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                p.setName(name.getText().trim());
                p.setCurp(curp.getText().trim());
                p.setPhone(phone.getText().trim());
                p.setEmail(mail.getText().trim());
                service.updatePatient(p);
                loadPatients();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deletePatient(int id) {
        try { service.deletePatient(id); loadPatients(); }
        catch (Exception ex) { showError(ex); }
    }

    private void showAddAppointmentDialog() {
        JTextField date   = new JTextField(),
                time   = new JTextField(),
                reason = new JTextField(),
                docId  = new JTextField(),
                patId  = new JTextField();
        JPanel panel = new JPanel(new GridLayout(5,2));
        panel.add(new JLabel("Fecha (YYYY-MM-DD):")); panel.add(date);
        panel.add(new JLabel("Hora (HH:MM:SS):"));    panel.add(time);
        panel.add(new JLabel("Motivo:"));             panel.add(reason);
        panel.add(new JLabel("ID Doctor:"));          panel.add(docId);
        panel.add(new JLabel("ID Paciente:"));        panel.add(patId);

        int op = JOptionPane.showConfirmDialog(this, panel, "Nueva Cita",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (date.getText().trim().isEmpty() ||
                    time.getText().trim().isEmpty() ||
                    reason.getText().trim().isEmpty() ||
                    docId.getText().trim().isEmpty() ||
                    patId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Appointment a = new Appointment();
                a.setDate(java.sql.Date.valueOf(date.getText().trim()));
                a.setTime(time.getText().trim());
                a.setReason(reason.getText().trim());
                a.setDoctorId(Integer.parseInt(docId.getText().trim()));
                a.setPatientId(Integer.parseInt(patId.getText().trim()));
                service.addAppointment(a);
                loadAppointments();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void showUpdateAppointmentDialog(Appointment a) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        JTextField date   = new JTextField(fmt.format(a.getDate()));
        JTextField time   = new JTextField(a.getTime());
        JTextField reason = new JTextField(a.getReason());
        JTextField docId  = new JTextField(String.valueOf(a.getDoctorId()));
        JTextField patId  = new JTextField(String.valueOf(a.getPatientId()));
        JPanel panel = new JPanel(new GridLayout(5,2));
        panel.add(new JLabel("Fecha (YYYY-MM-DD):")); panel.add(date);
        panel.add(new JLabel("Hora (HH:MM:SS):"));    panel.add(time);
        panel.add(new JLabel("Motivo:"));             panel.add(reason);
        panel.add(new JLabel("ID Doctor:"));          panel.add(docId);
        panel.add(new JLabel("ID Paciente:"));        panel.add(patId);

        int op = JOptionPane.showConfirmDialog(this, panel, "Editar Cita",
                JOptionPane.OK_CANCEL_OPTION);
        if (op == JOptionPane.OK_OPTION) {
            if (date.getText().trim().isEmpty() ||
                    time.getText().trim().isEmpty() ||
                    reason.getText().trim().isEmpty() ||
                    docId.getText().trim().isEmpty() ||
                    patId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Todos los campos son obligatorios.",
                        "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                a.setDate(java.sql.Date.valueOf(date.getText().trim()));
                a.setTime(time.getText().trim());
                a.setReason(reason.getText().trim());
                a.setDoctorId(Integer.parseInt(docId.getText().trim()));
                a.setPatientId(Integer.parseInt(patId.getText().trim()));
                service.updateAppointment(a);
                loadAppointments();
            } catch (Exception ex) { showError(ex); }
        }
    }

    private void deleteAppointment(int id) {
        try { service.deleteAppointment(id); loadAppointments(); }
        catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
