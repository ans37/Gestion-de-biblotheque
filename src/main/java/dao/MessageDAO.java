package dao;

import model.Message;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO implements DAO<Message> {

    public MessageDAO() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS message (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "id_utilisateur BIGINT NOT NULL, " +
                "contenu TEXT NOT NULL, " +
                "date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id) ON DELETE CASCADE" +
                ")";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement()) {
            System.out.println("DEBUG: Vérification/Création de la table 'message'...");
            stmt.execute(sql);
            System.out.println("DEBUG: Table 'message' opérationnelle.");
        } catch (SQLException e) {
            System.err.println("CRITICAL: Impossible de créer la table 'message' : " + e.getMessage());
        }
    }

    @Override
    public Message create(Message message) {
        String sql = "INSERT INTO message (id_utilisateur, contenu, date_envoi) VALUES (?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, message.getIdUtilisateur());
            stmt.setString(2, message.getContenu());
            stmt.setTimestamp(3, Timestamp.valueOf(now));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setId(generatedKeys.getLong(1));
                    }
                }
                message.setDateEnvoi(now);
            }

            return message;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du message : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Message findById(Long id) {
        String sql = "SELECT m.*, u.nom_utilisateur FROM message m " +
                "JOIN Utilisateur u ON m.id_utilisateur = u.id " +
                "WHERE m.id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractMessageFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du message : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom_utilisateur FROM message m " +
                "JOIN Utilisateur u ON m.id_utilisateur = u.id " +
                "ORDER BY m.date_envoi ASC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages : " + e.getMessage());
            e.printStackTrace();
        }

        return messages;
    }

    /**
     * Récupérer les N derniers messages
     */
    public List<Message> findRecentMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.*, u.nom_utilisateur FROM message m " +
                "JOIN Utilisateur u ON m.id_utilisateur = u.id " +
                "ORDER BY m.date_envoi DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }

            // Inverser l'ordre pour avoir les plus anciens en premier
            java.util.Collections.reverse(messages);

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des messages récents : " + e.getMessage());
            e.printStackTrace();
        }

        return messages;
    }

    @Override
    public Message update(Message message) {
        String sql = "UPDATE message SET contenu = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, message.getContenu());
            stmt.setLong(2, message.getId());

            stmt.executeUpdate();
            return message;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du message : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM message WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du message : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extraire un message depuis un ResultSet
     */
    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getLong("id"));
        message.setIdUtilisateur(rs.getLong("id_utilisateur"));
        message.setContenu(rs.getString("contenu"));

        Timestamp timestamp = rs.getTimestamp("date_envoi");
        if (timestamp != null) {
            message.setDateEnvoi(timestamp.toLocalDateTime());
        }

        message.setNomUtilisateur(rs.getString("nom_utilisateur"));

        return message;
    }
}
