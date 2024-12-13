import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// CXF REST Client Invoking POST Method

public class ConsumerREST {
    public static void main(String args[]) {
        try {

            Scanner scannerInterfaceG = new Scanner(System.in);
            String input;
            String user;
            String password;
            String passwordConfirm;
            String serverBooleanResult = "";
            String consultasReservadasStr = "";
            while(true){ //APLICAÇÃO A CORRER
                menuAuth();
                input = scannerInterfaceG.nextLine();
                //LOGIN (CASO EFETUADO COM SUCESSO, SAI DO CICLO)
                if (input.equals("login")) { //LOGIN
                    System.out.println("Digite o seu user:");
                    input = scannerInterfaceG.nextLine();
                    user = input;
                    System.out.println("Digite a sua password:");
                    input = scannerInterfaceG.nextLine();
                    password = input;

                    try {
                        URL url = new URL("http://localhost:8080/ProjFrontendRest/rest/login");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");

                        String inputServer = user + "," + password;

                        OutputStream os = conn.getOutputStream();
                        os.write(inputServer.getBytes());
                        os.flush();

                        Scanner scanner;
                        String response;
                        if (conn.getResponseCode() != 200) {
                            scanner = new Scanner(conn.getErrorStream());
                            response = "Error From Server \n\n";
                        } else {
                            scanner = new Scanner(conn.getInputStream());
                            response = "Response From Server \n\n";
                        }
                        scanner.useDelimiter("\\Z");
                        serverBooleanResult = scanner.next();

                        scanner.close();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String loginSucesso = serverBooleanResult;
                    if(!loginSucesso.equals("true")){
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
                    input = scannerInterfaceG.nextLine();
                    user = input;
                    System.out.println("Digite a sua password:");
                    input = scannerInterfaceG.nextLine();
                    password = input;
                    System.out.println("Digite a sua password novamente:");
                    input = scannerInterfaceG.nextLine();
                    passwordConfirm = input;

                    try {
                        URL url = new URL("http://localhost:8080/ProjFrontendRest/rest/registarUtilizador");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");

                        String inputServer = user + "," + password + "," + passwordConfirm;

                        OutputStream os = conn.getOutputStream();
                        os.write(inputServer.getBytes());
                        os.flush();

                        Scanner scanner;
                        String response;
                        if (conn.getResponseCode() != 200) {
                            scanner = new Scanner(conn.getErrorStream());
                            response = "Error From Server \n\n";
                        } else {
                            scanner = new Scanner(conn.getInputStream());
                            response = "Response From Server \n\n";
                        }
                        scanner.useDelimiter("\\Z");
                        serverBooleanResult = scanner.next();

                        scanner.close();
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String registoSucesso = serverBooleanResult;
                    if(registoSucesso.equals("true")){
                        System.out.println("Registo feito com sucesso!\n");
                    } else {
                        System.out.println("Erro ao registar\n");
                    }

                }
            }
            while(true){
                menuPaginaInicial();
                input = scannerInterfaceG.nextLine();
                switch (input) {
                    case "reservar":
                        System.out.println("Digite a especialidade:");
                        String especialidade = scannerInterfaceG.nextLine();
                        System.out.println("Digite a clínica:");
                        String clinica = scannerInterfaceG.nextLine();
                        System.out.println("Digite a data (DD-MM-YYYY):"); // Mudança no formato esperado
                        String dataStr = scannerInterfaceG.nextLine();
                        System.out.println("Digite a hora entre as 8h e 20h (HH:MM):");
                        String hora = scannerInterfaceG.nextLine();



                        try {
                            URL url = new URL("http://localhost:8080/ProjFrontendRest/rest/reservarConsulta");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");

                            String inputServer = especialidade + "," + clinica + "," + user + "," + dataStr + "," + hora;

                            OutputStream os = conn.getOutputStream();
                            os.write(inputServer.getBytes());
                            os.flush();

                            Scanner scanner;
                            String response;
                            if (conn.getResponseCode() != 200) {
                                scanner = new Scanner(conn.getErrorStream());
                                response = "Error From Server \n\n";
                            } else {
                                scanner = new Scanner(conn.getInputStream());
                                response = "Response From Server \n\n";
                            }
                            scanner.useDelimiter("\\Z");
                            serverBooleanResult = scanner.next();

                            scanner.close();
                            conn.disconnect();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String reservado = serverBooleanResult;

                        if (reservado.equals("true")) {
                            System.out.println("Consulta reservada com sucesso!");
                        } else {
                            System.out.println("Nenhum médico disponível para essa especialidade e horário.");
                        }
                        break;

                    case "cancelar":
                        System.out.println("Insira o ID da consulta que deseja cancelar.");
                        input = scannerInterfaceG.nextLine();
                        int idConsulta =Integer.parseInt(input);

                        try {
                            URL url = new URL("http://localhost:8080/ProjFrontendRest/rest/cancelarConsulta");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");

                            String inputServer = idConsulta + "," + user;

                            OutputStream os = conn.getOutputStream();
                            os.write(inputServer.getBytes());
                            os.flush();

                            Scanner scanner;
                            String response;
                            if (conn.getResponseCode() != 200) {
                                scanner = new Scanner(conn.getErrorStream());
                                response = "Error From Server \n\n";
                            } else {
                                scanner = new Scanner(conn.getInputStream());
                                response = "Response From Server \n\n";
                            }
                            scanner.useDelimiter("\\Z");
                            serverBooleanResult = scanner.next();

                            scanner.close();
                            conn.disconnect();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String cancelado = serverBooleanResult;
                        if(cancelado.equals("true")){
                            System.out.println("Consulta cancelada com sucesso!");
                        } else {
                            System.out.println("Erro ao cancelar consulta.");
                        }
                        break;
                    case "listar":
                        try {
                            URL url = new URL("http://localhost:8080/ProjFrontendRest/rest/listarConsultas");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");

                            String inputServer = user;

                            OutputStream os = conn.getOutputStream();
                            os.write(inputServer.getBytes());
                            os.flush();

                            Scanner scanner;
                            String response;
                            if (conn.getResponseCode() != 200) {
                                scanner = new Scanner(conn.getErrorStream());
                                response = "Error From Server \n\n";
                            } else {
                                scanner = new Scanner(conn.getInputStream());
                                response = "Response From Server \n\n";
                            }
                            scanner.useDelimiter("\\Z");
                            consultasReservadasStr = scanner.next();

                            scanner.close();
                            conn.disconnect();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!consultasReservadasStr.equals("")) {
                            System.out.println(consultasReservadasStr);
                        } else {
                            System.out.println("Não há consultas reservadas");
                        }
                        break;
                    case "sair":
                        System.out.println("Até à próxima!");
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
        System.out.println("| Digite 'sair' para terminar a aplicação                     |");
        System.out.println("|-------------------------------------------------------------|");
    }



}


