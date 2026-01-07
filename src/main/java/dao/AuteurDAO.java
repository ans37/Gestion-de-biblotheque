package dao;

import model.Auteur;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuteurDAO implements DAO<Auteur> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Auteur create(Auteur auteur) {
        String sql = "INSERT INTO Auteur (nom, prenom, biographie) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, auteur.getNom());
            stmt.setString(2, auteur.getPrenom());
            stmt.setString(3, auteur.getBiographie());

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        auteur.setId(rs.getLong(1));
                        return auteur;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'auteur: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Auteur findById(Long id) {
        String sql = "SELECT * FROM Auteur WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAuteur(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'auteur: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Auteur> findAll() {
        List<Auteur> auteurs = new ArrayList<>();
        String sql = "SELECT * FROM Auteur ORDER BY nom, prenom";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                auteurs.add(mapResultSetToAuteur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des auteurs: " + e.getMessage());
            e.printStackTrace();
        }
        return auteurs;
    }

    @Override
    public Auteur update(Auteur auteur) {
        String sql = "UPDATE Auteur SET nom = ?, prenom = ?, biographie = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, auteur.getNom());
            stmt.setString(2, auteur.getPrenom());
            stmt.setString(3, auteur.getBiographie());
            stmt.setLong(4, auteur.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return auteur;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'auteur: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Auteur WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'auteur: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Auteur> searchByName(String keyword) {
        List<Auteur> auteurs = new ArrayList<>();
        String sql = "SELECT * FROM Auteur WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                auteurs.add(mapResultSetToAuteur(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'auteurs: " + e.getMessage());
            e.printStackTrace();
        }
        return auteurs;
    }

    private Auteur mapResultSetToAuteur(ResultSet rs) throws SQLException {
        Auteur auteur = new Auteur();
        auteur.setId(rs.getLong("id"));
        auteur.setNom(rs.getString("nom"));
        auteur.setPrenom(rs.getString("prenom"));
        auteur.setBiographie(rs.getString("biographie"));
        return auteur;
    }
}