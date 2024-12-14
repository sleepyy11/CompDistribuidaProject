import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Consumes("application/json")
@Produces("application/json")
public class ClinicaREST {
    //static String clinicaServerURL = "rmi://" + "192.168.56.102" + "/ClinicaServer"; //Portatil Miguel
    static String clinicaServerURL = "rmi://" + "192.168.56.101" + "/ClinicaServer"; //PC Ana e Miguel

    static String pathInformacaoLogin = "C:\\Users\\migue\\eclipse-workspace\\ProjFrontendSoap\\src\\dadoslogin.txt";

    @POST
    @Path("/listarConsultas")
    public String listarConsultasServer(String user) throws RemoteException, MalformedURLException, NotBoundException {

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);
        String resposta = "";

        List<String> listaConsultas = clinicaServerIntf.listarConsultas(user);
        for (String consulta: listaConsultas) {
            resposta += consulta;
        }
        return resposta;

    }

    @POST
    @Path("/cancelarConsulta")
    public String cancelarConsultaServer(String data) throws RemoteException, MalformedURLException, NotBoundException {
        String[] dataSplit = data.split(",");

        int idConsulta = Integer.parseInt(dataSplit[0]);
        String user = dataSplit[1];

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

        boolean bool = clinicaServerIntf.cancelarConsulta(idConsulta, user);

        if(bool == true) {
            return "true";
        } else {
            return "false";
        }

    }

    @POST
    @Path("/reservarConsulta")
    public String reservarConsultaServer(String data) throws RemoteException, MalformedURLException, NotBoundException {
        String[] dataSplit = data.split(",");
        String especialidade = dataSplit[0];
        String clinica = dataSplit[1];
        String user = dataSplit[2];
        String date = dataSplit[3];
        String hora = dataSplit[4];

        ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

        boolean bool = clinicaServerIntf.reservarConsulta(especialidade, clinica, user, date, hora);
        if(bool == true) {
            return "true";
        } else {
            return "false";
        }

    }

    @POST
    @Path("/login")
    public String login(String data)throws IOException{
        String user = data.split(",")[0];
        String password = data.split(",")[1];

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
                    return "true";
                }
            }
        }
        bufferedReader.close();
        return "false";
    }

    @POST
    @Path("/registarUtilizador")
    public String registarUtilizador(String data) throws IOException {

        String user = data.split(",")[0];
        String password = data.split(",")[1];
        String passwordRepetida = data.split(",")[2];

        if(!password.equals(passwordRepetida)) {
            return "false";
        }
        //valida se possui @
        String[] userPartes = user.split("@");
        if(userPartes.length != 2) {
            return "false";
        }
        // valida se depois do @, é algo do genero de: @example.com
        String userDomain = userPartes[1];
        String[] userDomain_Topleveldomain = userDomain.split("\\."); //necessita \\ para contar o "." por ser um caracter especial em exp. regular
        if(userDomain_Topleveldomain.length != 2) {
            return "false";
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
                return "false";
            }
        }

        bufferedWriter.write(user + ";" + password + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
        bufferedReader.close();
        return "true";
    }



}

