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
        String sqlSelectMedicoDisponivel =
                "SELECT m.idMedico " +
                        "FROM Medico m " +
                        "LEFT JOIN Consulta c ON m.idMedico = c.idMedico AND c.data = ? AND c.hora = ? " +
                        "WHERE m.especialidade = ? AND m.clinica = ? " +
                        "AND c.idConsulta IS NULL " +
                        "LIMIT 1";

        String sqlInsertConsulta =
                "INSERT INTO Consulta (idMedico, paciente, data, hora) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelectMedicoDisponivel);
             PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsertConsulta)) {

            // Define os parâmetros para buscar o médico disponível
            pstmtSelect.setDate(1, new java.sql.Date(data.getTime()));
            pstmtSelect.setString(2, hora);
            pstmtSelect.setString(3, especialidade);
            pstmtSelect.setString(4, clinica);

            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    int idMedico = rs.getInt("idMedico");

                    // Define os parâmetros para inserir a consulta
                    pstmtInsert.setInt(1, idMedico);
                    pstmtInsert.setString(2, paciente);
                    pstmtInsert.setDate(3, new java.sql.Date(data.getTime()));
                    pstmtInsert.setString(4, hora);

                    // Executa a inserção
                    pstmtInsert.executeUpdate();
                    return true; // Consulta reservada com sucesso
                } else {
                    return false; // Nenhum médico disponível
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Erro ao reservar consulta para o paciente: " + paciente, e);
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