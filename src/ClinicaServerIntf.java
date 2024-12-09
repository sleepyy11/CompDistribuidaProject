import java.rmi.*;
import java.util.Date;
import java.util.List;

public interface ClinicaServerIntf extends Remote {
    boolean reservarConsulta(String especialidade, String clinica, String paciente, String data, String hora) throws RemoteException;
    boolean cancelarConsulta(int idConsulta, String user) throws RemoteException;
    List<String> listarConsultas(String user) throws RemoteException;

}