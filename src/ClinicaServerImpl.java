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

        public static final String DB_URL = "jdbc:mysql://192.168.56.101:3306/mydb";
        public static final String DB_USER = "root";
        public static final String DB_PASSWORD = "user";

    public ClinicaServerImpl() throws RemoteException {
    }

    @Override
    public boolean reservarConsulta(String especialidade, String clinica, Date Data, String hora) throws RemoteException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        return false;
    }

    @Override
    public boolean cancelarConsulta(int idConsulta) throws RemoteException {
        return false;
    }
    public List<String> listarConsultas() throws RemoteException {
        List<String> consultas = new ArrayList<>();

        // Definir a consulta SQL para listar todas as consultas
        String sql = "SELECT * FROM Consulta";

        // Estabelecer a conexão com a base de dados
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterar sobre os resultados e adicionar cada consulta à lista
            while (rs.next()) {
                // Montar a string com as informações da consulta
                String consulta = "ID: " + rs.getInt("idConsulta") + ", " +
                        "Data: " + rs.getDate("data") + ", " +
                        "Hora: " + rs.getString("hora") + ", " +
                        "Paciente: " + rs.getString("paciente") +
                        " (Medico ID: " + rs.getInt("idMedico") + ")";
                consultas.add(consulta);  // Adiciona a consulta na lista
            }

        } catch (SQLException e) {
            throw new RemoteException("Erro ao listar consultas", e);
        }
        return consultas;  // Retorna a lista com todas as consultas
    }
}