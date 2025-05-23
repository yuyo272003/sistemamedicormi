package org.example.client;

import org.example.server.MedicalService;
import javax.swing.*;
import java.rmi.Naming;

public class Client {
    public static void main(String[] args) {
        try {
            MedicalService service = (MedicalService) Naming.lookup("//localhost:2000/MedicalService");
            SwingUtilities.invokeLater(() -> new MainFrame(service));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "No se pudo conectar al servidor RMI");
        }
    }
}
