import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.util.Date;
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


    @Override
    public boolean reservarConsulta(String especialidade, String clinica, String paciente, Date data, String hora) throws RemoteException {
        String sqlBuscarClinicaId = "SELECT idClinica FROM Clinica WHERE nome = ?";
        String sqlBuscarMedicos = "SELECT m.idMedico " +
                "FROM Medico m " +
                "LEFT JOIN Consulta c ON m.idMedico = c.idMedico AND c.data = ? AND c.hora = ? " +
                "WHERE m.especialidade = ? AND m.idClinica = ? " +
                "AND c.idConsulta IS NULL";

        String sqlInserirConsulta = "INSERT INTO Consulta (idMedico, data, hora, paciente) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Passo 1: Obter o ID da clínica
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

            // Passo 2: Buscar todos os médicos disponíveis
            try (PreparedStatement pstmtBuscarMedicos = connection.prepareStatement(sqlBuscarMedicos)) {
                pstmtBuscarMedicos.setDate(1, new java.sql.Date(data.getTime()));
                pstmtBuscarMedicos.setString(2, hora);
                pstmtBuscarMedicos.setString(3, especialidade);
                pstmtBuscarMedicos.setInt(4, idClinica);

                try (ResultSet rsMedicos = pstmtBuscarMedicos.executeQuery()) {
                    while (rsMedicos.next()) {
                        int idMedico = rsMedicos.getInt("idMedico");

                        // Passo 3: Inserir consulta para o médico encontrado
                        try (PreparedStatement pstmtInserirConsulta = connection.prepareStatement(sqlInserirConsulta)) {
                            pstmtInserirConsulta.setInt(1, idMedico);
                            pstmtInserirConsulta.setDate(2, new java.sql.Date(data.getTime()));
                            pstmtInserirConsulta.setString(3, hora);
                            pstmtInserirConsulta.setString(4, paciente);

                            pstmtInserirConsulta.executeUpdate();
                            return true; // Consulta reservada com sucesso
                        }
                    }
                }
            }

            // Se nenhum médico disponível for encontrado
            throw new RemoteException("Nenhum médico disponível para a especialidade " + especialidade +
                    " na clínica " + clinica + " em " + data + " às " + hora);

        } catch (SQLException e) {
            throw new RemoteException("Erro ao reservar consulta", e);
        }
    }



    @Override
    public boolean cancelarConsulta(int idConsulta, String user) throws RemoteException {
        String sql = "DELETE FROM Consulta WHERE idConsulta = ? AND paciente = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Define os parâmetros na query
            pstmt.setInt(1, idConsulta);
            pstmt.setString(2, user);

            // Executa a query
            int rowsAffected = pstmt.executeUpdate();

            // Verifica se alguma linha foi afetada (consulta encontrada e removida)
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new RemoteException("Erro ao cancelar consulta com ID: " + idConsulta, e);
        }
    }
    public List<String> listarConsultas(String user) throws RemoteException {
        List<String> consultas = new ArrayList<>();

        // query SQL para listar as consultas do user passado por parametro
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

        return consultas; // Retorna a lista com as consultas filtradas
    }

}