import java.rmi.*;
public interface ClinicaServerIntf extends Remote {
    double add(double d1, double d2) throws RemoteException;
}