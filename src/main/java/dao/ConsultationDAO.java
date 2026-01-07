package dao;

import model.Consultation;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationDAO implements DAO<Consultation> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Consultation create(Consultation consultation) {
        String sql = "INSERT INTO Consultation (id_utilisateur, id_livre, date_consultation, duree_lecture) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, consultation.getIdUtilisateur());
            stmt.setLong(2, consultation.getIdLivre());
            stmt.setTimestamp(3, Timestamp.valueOf(consultation.getDateConsultation()));
            stmt.setInt(4, consultation.getDureeLecture() != null ? consultation.getDureeLecture() : 0);

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        consultation.setId(rs.getLong(1));
                        return consultation;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM Consultation WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConsultation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Consultation> findAll() {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT * FROM Consultation ORDER BY date_consultation DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                consultations.add(mapResultSetToConsultation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        return consultations;
    }

    @Override
    public Consultation update(Consultation consultation) {
        String sql = "UPDATE Consultation SET duree_lecture = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, consultation.getDureeLecture() != null ? consultation.getDureeLecture() : 0);
            stmt.setLong(2, consultation.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return consultation;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Consultation WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Consultation> findByUtilisateur(Long idUtilisateur) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT * FROM Consultation WHERE id_utilisateur = ? ORDER BY date_consultation DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                consultations.add(mapResultSetToConsultation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        return consultations;
    }

    public List<Consultation> findByLivre(Long idLivre) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT * FROM Consultation WHERE id_livre = ? ORDER BY date_consultation DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                consultations.add(mapResultSetToConsultation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        return consultations;
    }

    public Consultation findLastConsultation(Long idUtilisateur, Long idLivre) {
        String sql = "SELECT * FROM Consultation WHERE id_utilisateur = ? AND id_livre = ? " +
                "ORDER BY date_consultation DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            stmt.setLong(2, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToConsultation(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la consultation: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int getNombreConsultationsUtilisateur(Long idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM Consultation WHERE id_utilisateur = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getNombreConsultationsLivre(Long idLivre) {
        String sql = "SELECT COUNT(*) FROM Consultation WHERE id_livre = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des consultations: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private Consultation mapResultSetToConsultation(ResultSet rs) throws SQLException {
        Consultation consultation = new Consultation();
        consultation.setId(rs.getLong("id"));
        consultation.setIdUtilisateur(rs.getLong("id_utilisateur"));
        consultation.setIdLivre(rs.getLong("id_livre"));

        Timestamp ts = rs.getTimestamp("date_consultation");
        if (ts != null) {
            consultation.setDateConsultation(ts.toLocalDateTime());
        }

        consultation.setDureeLecture(rs.getInt("duree_lecture"));
        return consultation;
    }
}