package dao;

import model.Categorie;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieDAO implements DAO<Categorie> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Categorie create(Categorie categorie) {
        String sql = "INSERT INTO Categorie (nom, description) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        categorie.setId(rs.getLong(1));
                        return categorie;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Categorie findById(Long id) {
        String sql = "SELECT * FROM Categorie WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategorie(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Categorie> findAll() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categorie ORDER BY nom";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategorie(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des catégories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Categorie update(Categorie categorie) {
        String sql = "UPDATE Categorie SET nom = ?, description = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setLong(3, categorie.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return categorie;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Categorie WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Categorie findByNom(String nom) {
        String sql = "SELECT * FROM Categorie WHERE nom = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategorie(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de la catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private Categorie mapResultSetToCategorie(ResultSet rs) throws SQLException {
        Categorie categorie = new Categorie();
        categorie.setId(rs.getLong("id"));
        categorie.setNom(rs.getString("nom"));
        categorie.setDescription(rs.getString("description"));
        return categorie;
    }
}