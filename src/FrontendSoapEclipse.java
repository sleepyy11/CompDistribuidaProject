import java.util.List;

import javax.jws.WebService;

import java.io.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@WebService(targetNamespace = "http://default_package/", portName = "ProjFrontendPort", serviceName = "ProjFrontendService")
public class ProjFrontend {
    //static String clinicaServerURL = "rmi://" + "192.168.56.102" + "/ClinicaServer"; //Portatil Miguel
    static String clinicaServerURL = "rmi://" + "192.168.56.101" + "/ClinicaServer"; //PC Ana e Miguel

    static String pathInformacaoLogin = "C:\\Users\\migue\\eclipse-workspace\\MathUtilityProject\\src\\dadosUser.txt";


    public List<String> listarConsultasServer(String user) throws RemoteException, MalformedURLException, NotBoundException{

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

        return clinicaServerIntf.listarConsultas(user);

    }

    public boolean cancelarConsultaServer(int idConsulta, String user) throws RemoteException, MalformedURLException, NotBoundException{

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

        return clinicaServerIntf.cancelarConsulta(idConsulta, user);


    }

    public boolean reservarConsultaServer(String especialidade, String clinica, String paciente, String data, String hora)throws IOException, MalformedURLException, NotBoundException{

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

        return clinicaServerIntf.reservarConsulta(especialidade, clinica, paciente, data, hora);
    }

    public boolean login(String user, String password)throws IOException{
        String linha;
        String userTXT;
        String passwordTXT;
        File file = new File(pathInformacaoLogin);

        if (!file.exists()) {
            throw new IOException("Ficheiro não existe");
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((linha = bufferedReader.readLine()) != null){
            String[] partes = linha.split(";");
            userTXT = partes[0];
            passwordTXT = partes[1];
            if(userTXT.equals(user)){
                if(passwordTXT.equals(password)){
                    bufferedReader.close();
                    return true;
                }
            }
        }
        bufferedReader.close();
        return false;
    }

    public boolean registarUtilizador(String user, String password, String passwordRepetida) throws IOException {
        if(!password.equals(passwordRepetida)) {
            return false;
        }
        String linha;
        String userTXT;
        File file = new File(pathInformacaoLogin);
        if (!file.exists()) {
            throw new IOException("Ficheiro não existente");
        }
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((linha = bufferedReader.readLine()) != null) {
            String[] partes = linha.split(";");
            userTXT = partes[0];
            if (userTXT.equals(user)){
                bufferedWriter.close();
                bufferedReader.close();
                return false;
            }
        }

        bufferedWriter.write(user + ";" + password + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedReader.close();
        return true;
    }

}