import java.rmi.*;
import java.util.List;
import java.util.Scanner;
import java.io.*;
public class ClinicaClient {
    public static String pathInformacaoLogin = "/home/ubuntu/Documents/clinica/dadoslogin";

    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("Usage: java ClinicaClient <IP_SERVER>");
            return;
        }
        try {
            //String clinicaServerURL = "rmi://" + args[0] + "/ClinicaServer";
            String clinicaServerURL = "rmi://" + "192.168.56.101" + "/ClinicaServer";
            ClinicaServerIntf clinicaServerIntf = (ClinicaServerIntf)Naming.lookup(clinicaServerURL);

            Scanner scanner = new Scanner(System.in);
            String input;
            String user;
            String password;
            String passwordConfirm;

            while(true){ //APLICAÇÃO A CORRER

                menuInicial();
                input = scanner.nextLine();
                if (input.equals("Sair")) {
                    System.out.println("Até à próxima! :)");
                    break;
                }
                //LOGIN
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
                        //PROGRAMA APÓS LOGIN
                        System.out.println("Login efetuado com sucesso!\n");
                        menuBemVindo();
                        input = scanner.nextLine();
                        switch (input) {
                            case "reservar":




                                break;
                            case "cancelar":




                                break;
                            case "listar":
                                input = scanner.nextLine();
                                    List<String> listaConsultas = clinicaServerIntf.listarConsultas();
                                    if (!listaConsultas.isEmpty()) {
                                        for (String consulta : listaConsultas) {
                                            System.out.println(consulta);
                                        }
                                    } else {
                                        System.out.println("Não há consultas reservadas");
                                    }
                                break;
                            default:
                                System.out.println("Entrada inválida");
                                break;
                        }

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
        }catch(Exception e) {
            System.out.println("Exception: " + e);
            System.out.println("Usage: IP");
        }
    }



//FUNÇÕES
    public static void menuInicial(){
        System.out.println();
        System.out.println("|-------------------------------------------------|");
        System.out.println("| Bem vindo à aplicação de marcação de consultas! |");
        System.out.println("|-------------------------------------------------|");
        System.out.println("| Digite 'login' para iniciar sessão              |");
        System.out.println("| Não possui conta? Digite 'registar'             |");
        System.out.println("|-------------------------------------------------|");
        System.out.println();
    }
    public static void menuBemVindo(){
        System.out.println("|----------------------------------------------------------|");
        System.out.println("|                         Bem vindo !                      |");
        System.out.println("|----------------------------------------------------------|");
        System.out.println("| Digite 'reservar' para iniciar a reserva de uma consulta |");
        System.out.println("| Digite 'cancelar' para cancelar uma consulta             |");
        System.out.println("| Digite 'listar' para listar as consultas reservadas      |");
        System.out.println("|----------------------------------------------------------|");


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




