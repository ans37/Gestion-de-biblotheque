package dao;

import model.Telechargement;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelechargementDAO implements DAO<Telechargement> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Telechargement create(Telechargement telechargement) {
        String sql = "INSERT INTO Telechargement (id_utilisateur, id_livre, date_telechargement, autorise, id_paiement) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, telechargement.getIdUtilisateur());
            stmt.setLong(2, telechargement.getIdLivre());
            stmt.setTimestamp(3, Timestamp.valueOf(telechargement.getDateTelechargement()));
            stmt.setBoolean(4, telechargement.getAutorise());
            if (telechargement.getIdPaiement() != null) {
                stmt.setLong(5, telechargement.getIdPaiement());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        telechargement.setId(rs.getLong(1));
                        return telechargement;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du téléchargement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Telechargement findById(Long id) {
        String sql = "SELECT * FROM Telechargement WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTelechargement(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du téléchargement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Telechargement> findAll() {
        List<Telechargement> telechargements = new ArrayList<>();
        String sql = "SELECT * FROM Telechargement ORDER BY date_telechargement DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                telechargements.add(mapResultSetToTelechargement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des téléchargements: " + e.getMessage());
            e.printStackTrace();
        }
        return telechargements;
    }

    @Override
    public Telechargement update(Telechargement telechargement) {
        String sql = "UPDATE Telechargement SET autorise = ?, id_paiement = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, telechargement.getAutorise());
            if (telechargement.getIdPaiement() != null) {
                stmt.setLong(2, telechargement.getIdPaiement());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            stmt.setLong(3, telechargement.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return telechargement;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du téléchargement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Telechargement WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du téléchargement: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean canUserDownload(Long idUtilisateur, Long idLivre) {
        // Vérifier d'abord s'il existe déjà un téléchargement autorisé
        String sql = "SELECT COUNT(*) FROM Telechargement WHERE id_utilisateur = ? AND id_livre = ? AND autorise = true";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            stmt.setLong(2, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Déjà un téléchargement autorisé
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification: " + e.getMessage());
            e.printStackTrace();
        }

        // Sinon vérifier via PaiementDAO si l'utilisateur a payé
        PaiementDAO paiementDAO = new PaiementDAO();
        return paiementDAO.hasUserPaidForBook(idUtilisateur, idLivre);
    }

    public Telechargement enregistrerTelechargement(Long idUtilisateur, Long idLivre, Long idPaiement) {
        Telechargement telechargement = new Telechargement();
        telechargement.setIdUtilisateur(idUtilisateur);
        telechargement.setIdLivre(idLivre);
        telechargement.setAutorise(true);
        telechargement.setIdPaiement(idPaiement);

        return create(telechargement);
    }

    public List<Telechargement> findByUtilisateur(Long idUtilisateur) {
        List<Telechargement> telechargements = new ArrayList<>();
        String sql = "SELECT * FROM Telechargement WHERE id_utilisateur = ? ORDER BY date_telechargement DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                telechargements.add(mapResultSetToTelechargement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des téléchargements: " + e.getMessage());
            e.printStackTrace();
        }
        return telechargements;
    }

    public List<Telechargement> findByLivre(Long idLivre) {
        List<Telechargement> telechargements = new ArrayList<>();
        String sql = "SELECT * FROM Telechargement WHERE id_livre = ? ORDER BY date_telechargement DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                telechargements.add(mapResultSetToTelechargement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des téléchargements: " + e.getMessage());
            e.printStackTrace();
        }
        return telechargements;
    }

    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM Telechargement WHERE autorise = true";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int countByLivre(Long idLivre) {
        String sql = "SELECT COUNT(*) FROM Telechargement WHERE id_livre = ? AND autorise = true";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int countByUtilisateur(Long idUtilisateur) {
        String sql = "SELECT COUNT(*) FROM Telechargement WHERE id_utilisateur = ? AND autorise = true";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public List<Telechargement> findTelechargementsAutorises() {
        List<Telechargement> telechargements = new ArrayList<>();
        String sql = "SELECT * FROM Telechargement WHERE autorise = true ORDER BY date_telechargement DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                telechargements.add(mapResultSetToTelechargement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des téléchargements: " + e.getMessage());
            e.printStackTrace();
        }
        return telechargements;
    }

    private Telechargement mapResultSetToTelechargement(ResultSet rs) throws SQLException {
        Telechargement telechargement = new Telechargement();
        telechargement.setId(rs.getLong("id"));
        telechargement.setIdUtilisateur(rs.getLong("id_utilisateur"));
        telechargement.setIdLivre(rs.getLong("id_livre"));

        Timestamp ts = rs.getTimestamp("date_telechargement");
        if (ts != null) {
            telechargement.setDateTelechargement(ts.toLocalDateTime());
        }

        telechargement.setAutorise(rs.getBoolean("autorise"));

        Long idPaiement = rs.getLong("id_paiement");
        if (!rs.wasNull()) {
            telechargement.setIdPaiement(idPaiement);
        }

        return telechargement;
    }
}