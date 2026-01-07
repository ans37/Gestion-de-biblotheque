package dao;

import model.Paiement;
import model.StatutPaiement;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaiementDAO implements DAO<Paiement> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Paiement create(Paiement paiement) {
        String sql = "INSERT INTO Paiement (id_utilisateur, id_livre, montant, date_paiement, " +
                "statut, mode_paiement, reference_transaction) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Générer une référence si elle n'existe pas
            if (paiement.getReferenceTransaction() == null) {
                paiement.setReferenceTransaction("TRX-" + System.currentTimeMillis() + "-" +
                        UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }

            stmt.setLong(1, paiement.getIdUtilisateur());
            stmt.setLong(2, paiement.getIdLivre());
            stmt.setDouble(3, paiement.getMontant());
            stmt.setTimestamp(4, Timestamp.valueOf(paiement.getDatePaiement()));
            stmt.setString(5, paiement.getStatut().name());
            stmt.setString(6, paiement.getModePaiement());
            stmt.setString(7, paiement.getReferenceTransaction());

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        paiement.setId(rs.getLong(1));
                        return paiement;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Paiement findById(Long id) {
        String sql = "SELECT * FROM Paiement WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Paiement> findAll() {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM Paiement ORDER BY date_paiement DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des paiements: " + e.getMessage());
            e.printStackTrace();
        }
        return paiements;
    }

    @Override
    public Paiement update(Paiement paiement) {
        String sql = "UPDATE Paiement SET statut = ?, mode_paiement = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paiement.getStatut().name());
            stmt.setString(2, paiement.getModePaiement());
            stmt.setLong(3, paiement.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return paiement;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Paiement WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasUserPaidForBook(Long idUtilisateur, Long idLivre) {
        String sql = "SELECT COUNT(*) FROM Paiement WHERE id_utilisateur = ? AND id_livre = ? AND statut = 'PAYE'";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            stmt.setLong(2, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Paiement findValidPaiement(Long idUtilisateur, Long idLivre) {
        String sql = "SELECT * FROM Paiement WHERE id_utilisateur = ? AND id_livre = ? AND statut = 'PAYE' " +
                "ORDER BY date_paiement DESC LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            stmt.setLong(2, idLivre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Paiement> findByUtilisateur(Long idUtilisateur) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM Paiement WHERE id_utilisateur = ? ORDER BY date_paiement DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des paiements: " + e.getMessage());
            e.printStackTrace();
        }
        return paiements;
    }

    public List<Paiement> findByLivre(Long idLivre) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM Paiement WHERE id_livre = ? ORDER BY date_paiement DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idLivre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des paiements: " + e.getMessage());
            e.printStackTrace();
        }
        return paiements;
    }

    public Paiement findByReference(String reference) {
        String sql = "SELECT * FROM Paiement WHERE reference_transaction = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reference);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPaiement(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du paiement: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean marquerCommePaye(Long idPaiement) {
        String sql = "UPDATE Paiement SET statut = 'PAYE' WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idPaiement);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public double getTotalVentes() {
        String sql = "SELECT SUM(montant) FROM Paiement WHERE statut = 'PAYE'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du total: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public List<Paiement> findByStatut(StatutPaiement statut) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM Paiement WHERE statut = ? ORDER BY date_paiement DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                paiements.add(mapResultSetToPaiement(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des paiements: " + e.getMessage());
            e.printStackTrace();
        }
        return paiements;
    }

    private Paiement mapResultSetToPaiement(ResultSet rs) throws SQLException {
        Paiement paiement = new Paiement();
        paiement.setId(rs.getLong("id"));
        paiement.setIdUtilisateur(rs.getLong("id_utilisateur"));
        paiement.setIdLivre(rs.getLong("id_livre"));
        paiement.setMontant(rs.getDouble("montant"));

        Timestamp ts = rs.getTimestamp("date_paiement");
        if (ts != null) {
            paiement.setDatePaiement(ts.toLocalDateTime());
        }

        paiement.setStatut(StatutPaiement.valueOf(rs.getString("statut")));
        paiement.setModePaiement(rs.getString("mode_paiement"));
        paiement.setReferenceTransaction(rs.getString("reference_transaction"));

        return paiement;
    }
}