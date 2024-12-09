import java.rmi.*;
import java.rmi.server.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClinicaServerImpl extends UnicastRemoteObject
        implements ClinicaServerIntf {

        public static final String DB_URL = "jdbc:mysql://localhost:3306/mydb";
        public static final String DB_USER = "user";
        public static final String DB_PASSWORD = "password";

    public ClinicaServerImpl() throws RemoteException {
    }




    public boolean reservarConsulta(String especialidade, String clinica, String paciente, String data, String hora) throws RemoteException {
        // Converter a String data ("DD-MM-YYYY") para java.sql.Date
        Date sqlDate;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false); //garante que a data segue o formato esperado
            java.util.Date parsedDate = dateFormat.parse(data);
            sqlDate = new Date(parsedDate.getTime());
        } catch (ParseException e) {
            throw new RemoteException("Formato de data inválido: " + data, e);
        }

        String sqlBuscarClinicaId = "SELECT idClinica FROM Clinica WHERE nome = ?";
        String sqlBuscarMedicos = "SELECT m.idMedico " +
                "FROM Medico m " +
                "LEFT JOIN Consulta c ON m.idMedico = c.idMedico AND c.data = ? AND c.hora = ? " +
                "WHERE m.especialidade = ? AND m.idClinica = ? " +
                "AND c.idConsulta IS NULL";

        String sqlInserirConsulta = "INSERT INTO Consulta (idMedico, data, hora, paciente) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            //obter o id  da clinica
            int idClinica;
            try (PreparedStatement pstmtClinica = connection.prepareStatement(sqlBuscarClinicaId)) {
                pstmtClinica.setString(1, clinica);
                try (ResultSet rsClinica = pstmtClinica.executeQuery()) {
                    if (rsClinica.next()) {
                        idClinica = rsClinica.getInt("idClinica");
                    } else {
                        throw new RemoteException("Clínica não encontrada: " + clinica);
                    }
                }
            }

            //procurar medico disponivel para a data, horario e especialidade
            try (PreparedStatement pstmtBuscarMedicos = connection.prepareStatement(sqlBuscarMedicos)) {
                pstmtBuscarMedicos.setDate(1, sqlDate);
                pstmtBuscarMedicos.setString(2, hora);
                pstmtBuscarMedicos.setString(3, especialidade);
                pstmtBuscarMedicos.setInt(4, idClinica);

                try (ResultSet rsMedicos = pstmtBuscarMedicos.executeQuery()) {
                    while (rsMedicos.next()) {
                        int idMedico = rsMedicos.getInt("idMedico");

                        //insere consulta para o medico disponivel encontrado
                        try (PreparedStatement pstmtInserirConsulta = connection.prepareStatement(sqlInserirConsulta)) {
                            pstmtInserirConsulta.setInt(1, idMedico);
                            pstmtInserirConsulta.setDate(2, sqlDate);
                            pstmtInserirConsulta.setString(3, hora);
                            pstmtInserirConsulta.setString(4, paciente);

                            pstmtInserirConsulta.executeUpdate();
                            return true; // Consulta reservada com sucesso
                        }
                    }
                }
            }

            //se nenhum medico disponivel for encontrado para aquele horario e especialidade
            return false;

        } catch (SQLException e) {
            throw new RemoteException("Erro ao reservar consulta", e);
        }
    }




    @Override
    public boolean cancelarConsulta(int idConsulta, String user) throws RemoteException {
        String sql = "DELETE FROM Consulta WHERE idConsulta = ? AND paciente = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // definir parametros da query
            pstmt.setInt(1, idConsulta);
            pstmt.setString(2, user);

            // executar query
            int linhasAlteradas = pstmt.executeUpdate();

            // verificar se alguma linha foi afetada (para validar se a consulta foi removida)
            return linhasAlteradas > 0;

        } catch (SQLException e) {
            throw new RemoteException("Erro ao cancelar consulta com ID: " + idConsulta, e);
        }
    }
    public List<String> listarConsultas(String user) throws RemoteException {
        List<String> consultas = new ArrayList<>();

        // query para listar as consultas do user autenticado
        String sql = "SELECT * FROM Consulta WHERE paciente = ?";

        // estabelecer conexao com a BD
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // substituir o ? pelo parametro na query
            pstmt.setString(1, user);

            // correr a query
            try (ResultSet rs = pstmt.executeQuery()) {
                // adicionar cada linha da tabela que corresponde ao resultado da query à lista
                while (rs.next()) {
                    String consulta = "- ID da consulta: " + rs.getInt("idConsulta") + "\n" +
                            "- ID do Médico: " + rs.getInt("idMedico") + "\n" +
                            "- Data: " + rs.getDate("data") + "\n" +
                            "- Hora: " + rs.getString("hora") + "\n" +
                            "- Paciente: " + rs.getString("paciente") + "\n\n";
                    consultas.add(consulta); // adiciona a consulta na lista
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Erro ao listar consultas para o user: " + user);
        }

        return consultas; // retorna a lista com as consultas filtradas
    }

}