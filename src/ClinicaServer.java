import java.net.*;
import java.rmi.*;
public class ClinicaServer {
    public static void main(String args[]) {
        try {
            ClinicaServerImpl clinicaServerImpl = new ClinicaServerImpl();
            Naming.rebind("ClinicaServer", clinicaServerImpl);
        }
        catch(Exception e) {
            System.out.println("Exception: " + e);
        }
    }
}