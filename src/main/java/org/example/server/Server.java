package org.example.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            // Levanta un registry local en el puerto 2000
            Registry registry = LocateRegistry.createRegistry(2000);

            // Crea e “exporta” el servicio
            MedicalService service = new MedicalServiceImpl();
            registry.rebind("MedicalService", service);

            System.out.println(">> Servidor RMI listo en puerto 2000");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
