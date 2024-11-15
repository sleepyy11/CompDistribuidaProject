import java.rmi.*;
import java.rmi.server.*;
public class ClinicaServerImpl extends UnicastRemoteObject
        implements ClinicaServerIntf {

    public ClinicaServerImpl() throws RemoteException {
    }
    public double add(double d1, double d2) throws RemoteException {
        return d1 + d2;
    }
}