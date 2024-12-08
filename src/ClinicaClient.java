import java.rmi.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.io.*;
public class ClinicaClient {
    //public static String pathInformacaoLogin = "/home/ubuntu/Documents/clinica/dadoslogin";
    public static String pathInformacaoLogin = "/home/ubuntu/Documents/clinica/dadoslogin";

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("Usage: java ClinicaClient <IP_SERVER>");
            return;
        }
        try {
            String clinicaServerURL = "rmi://" + args[0] + "/ClinicaServer";
            ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

            Scanner scanner = new Scanner(System.in);
            String input;
            String user;
            String password;
            String passwordConfirm;
            while(true){ //APLICAÇÃO A CORRER
                menuAuth();
                input = scanner.nextLine();
                //LOGIN (CASO EFETUADO COM SUCESSO, SAI DO CICLO)
                if (input.equals("login")) { //LOGIN
                    System.out.println("Digite o seu user:");
                    input = scanner.nextLine();
                    user = input;
                    System.out.println("Digite a sua password:");
                    input = scanner.nextLine();
                    password = input;
                    if(!login(user, password)){
                        System.out.println("Login inválido\n");
                    } else {
                        System.out.println("Login efetuado com sucesso!\n");
                        //SAI DO MENU DE AUTENTICAO
                        break;
                    }
                }
                //REGISTAR UTILIZADOR
                if (input.equals("registar")) {
                    System.out.println("Digite o seu user:");
                    input = scanner.nextLine();
                    user = input;
                    System.out.println("Digite a sua password:");
                    input = scanner.nextLine();
                    password = input;
                    System.out.println("Digite a sua password novamente:");
                    input = scanner.nextLine();
                    passwordConfirm = input;
                    if(password.equals(passwordConfirm)){
                        if(registarUtilizador(user, password)){
                            System.out.println("Registo feito com sucesso!\n");
                        } else {
                            System.out.println("Erro ao registar\n");
                        }
                    } else {
                        System.out.println("Erro ao registar\n");
                    }

                }
            }
            while(true){
                menuPaginaInicial();
                input = scanner.nextLine();
                switch (input) {
                    case "reservar":

                        System.out.println("Digite a especialidade:");
                        String especialidade = scanner.nextLine();
                        System.out.println("Digite a clínica:");
                        String clinica = scanner.nextLine();
                        System.out.println("Digite a data (YYYY-MM-DD):");
                        String dataStr = scanner.nextLine();
                        System.out.println("Digite a hora (HH:MM):");
                        String hora = scanner.nextLine();

                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date data = dateFormat.parse(dataStr);

                            boolean reservado = clinicaServerIntf.reservarConsulta(especialidade, clinica, user, data, hora);

                            if (reservado) {
                                System.out.println("Consulta reservada com sucesso!");
                            } else {
                                System.out.println("Nenhum médico disponível para essa especialidade e horário.");
                            }
                        } catch (ParseException e) {
                            System.out.println("Formato de data inválido.");
                        }

                        break;
                    case "cancelar":
                        System.out.println("Insira o ID da consulta que deseja cancelar.");
                        input = scanner.nextLine();
                        int idConsulta =Integer.parseInt(input);
                        boolean cancelado = clinicaServerIntf.cancelarConsulta(idConsulta, user);
                        if(cancelado){
                            System.out.println("Consulta cancelada com sucesso!");
                        } else {
                            System.out.println("Erro ao cancelar consulta.");
                        }
                        break;
                    case "listar":
                        try {
                            List<String> listaConsultas = clinicaServerIntf.listarConsultas(user);
                            if (!listaConsultas.isEmpty()) {
                                for (String consulta : listaConsultas) {
                                    System.out.println(consulta);
                                }
                            } else {
                                System.out.println("Não há consultas reservadas");
                            }
                        } catch (RemoteException e) {
                            System.out.println("Erro ao listar consultas: " + e.getMessage());
                        }
                        break;
                    case "sair":
                        return;
                    default:
                        System.out.println("Entrada inválida");
                        break;
                }

            }
        }catch(Exception e) {
            System.out.println("Exception: " + e);
            System.out.println("Usage: IP");
        }
    }



    //FUNÇÕES
    public static void menuAuth(){
        System.out.println();
        System.out.println("|-------------------------------------------------|");
        System.out.println("| Bem vindo à aplicação de marcação de consultas! |");
        System.out.println("|-------------------------------------------------|");
        System.out.println("| Digite 'login' para iniciar sessão              |");
        System.out.println("| Não possui conta? Digite 'registar'             |");
        System.out.println("|-------------------------------------------------|");
        System.out.println();
    }
    public static void menuPaginaInicial(){
        System.out.println("|-------------------------------------------------------------|");
        System.out.println("|                         Página Inicial                      |");
        System.out.println("|-------------------------------------------------------------|");
        System.out.println("| Digite 'reservar' para iniciar a reserva de uma consulta    |");
        System.out.println("| Digite 'cancelar' para cancelar uma consulta                |");
        System.out.println("| Digite 'listar' para listar as consultas reservadas         |");
        System.out.println("|-------------------------------------------------------------|");
    }
    public static boolean registarUtilizador(String user, String password) throws IOException{
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
                return false;
            }
        }

        bufferedWriter.write(user + ";" + password + "\n");
        bufferedWriter.flush();
        bufferedWriter.close();
        return true;
    }
    public static boolean login(String user, String password)throws IOException{
        String linha;
        String userTXT;
        String passwordTXT;
        File file = new File(pathInformacaoLogin);

        if (!file.exists()) {
            throw new IOException("Ficheiro não existente");
        }

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((linha = bufferedReader.readLine()) != null){
            String[] partes = linha.split(";");
            userTXT = partes[0];
            passwordTXT = partes[1];
            if(userTXT.equals(user)){
                if(passwordTXT.equals(password)){
                    return true;
                }
            }
        }
        return false;
    }

}




