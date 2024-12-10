import java.util.List;
import java.util.Scanner;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import default_package.ProjFrontend;

public class ConsumerSoap {

    public static void main(String args[]) {
        try {

            JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
            factory.setAddress("http://localhost:8080/ProjFrontendSoap/services/ProjFrontendPort");
            factory.setServiceClass(ProjFrontend.class);

            Object client = factory.create();

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
                    boolean loginSucesso = ((ProjFrontend)client).login(user,password);
                    if(!loginSucesso){
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
                    boolean registoSucesso = ((ProjFrontend)client).registarUtilizador(user,password, passwordConfirm);
                    if(registoSucesso){
                        System.out.println("Registo feito com sucesso!\n");
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
                        System.out.println("Digite a data (DD-MM-YYYY):"); // Mudança no formato esperado
                        String dataStr = scanner.nextLine();
                        System.out.println("Digite a hora entre as 8h e 20h (HH:MM):");
                        String hora = scanner.nextLine();

                        boolean reservado = ((ProjFrontend)client).reservarConsultaServer(especialidade, clinica, user, dataStr, hora);

                        if (reservado) {
                            System.out.println("Consulta reservada com sucesso!");
                        } else {
                            System.out.println("Nenhum médico disponível para essa especialidade e horário.");
                        }
                        break;

                    case "cancelar":
                        System.out.println("Insira o ID da consulta que deseja cancelar.");
                        input = scanner.nextLine();
                        int idConsulta =Integer.parseInt(input);
                        boolean cancelado = ((ProjFrontend)client).cancelarConsultaServer(idConsulta, user);
                        if(cancelado){
                            System.out.println("Consulta cancelada com sucesso!");
                        } else {
                            System.out.println("Erro ao cancelar consulta.");
                        }
                        break;
                    case "listar":
                        List<String> listaConsultas = ((ProjFrontend)client).listarConsultasServer(user);
                        if (!listaConsultas.isEmpty()) {
                            for (String consulta : listaConsultas) {
                                System.out.println(consulta);
                            }
                        } else {
                            System.out.println("Não há consultas reservadas");
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
        System.out.println("|------------------------------------------------------|");
        System.out.println("| Bem vindo à aplicação SOAP de marcação de consultas! |");
        System.out.println("|------------------------------------------------------|");
        System.out.println("| Digite 'login' para iniciar sessão                   |");
        System.out.println("| Não possui conta? Digite 'registar'                  |");
        System.out.println("|------------------------------------------------------|");
        System.out.println();
    }
    public static void menuPaginaInicial(){
        System.out.println("|-------------------------------------------------------------|");
        System.out.println("|                    Página Inicial SOAP                      |");
        System.out.println("|-------------------------------------------------------------|");
        System.out.println("| Digite 'reservar' para iniciar a reserva de uma consulta    |");
        System.out.println("| Digite 'cancelar' para cancelar uma consulta                |");
        System.out.println("| Digite 'listar' para listar as consultas reservadas         |");
        System.out.println("|-------------------------------------------------------------|");
    }

}




