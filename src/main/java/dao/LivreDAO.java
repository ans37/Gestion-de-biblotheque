package dao;

import model.*;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO implements DAO<Livre> {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Livre create(Livre livre) {
        String sql = "INSERT INTO Livre (isbn, titre, annee_publication, description, langue, " +
                "nombre_pages, chemin_pdf, disponible, type_livre, prix, date_ajout, " +
                "id_auteur, id_categorie) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setObject(3, livre.getAnneePublication());
            stmt.setString(4, livre.getDescription());
            stmt.setString(5, livre.getLangue());
            stmt.setObject(6, livre.getNombrePages());
            stmt.setString(7, livre.getCheminPdf());
            stmt.setBoolean(8, livre.getDisponible() != null ? livre.getDisponible() : true);
            stmt.setString(9, livre.getTypeLivre().name());
            stmt.setDouble(10, livre.getPrix() != null ? livre.getPrix() : 0.0);
            stmt.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(12, livre.getIdAuteur());
            stmt.setLong(13, livre.getIdCategorie());

            int affected = stmt.executeUpdate();

            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        livre.setId(rs.getLong(1));
                        return livre;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du livre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Livre findById(Long id) {
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToLivre(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche du livre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Livre> findAll() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "ORDER BY l.titre";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des livres: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    @Override
    public Livre update(Livre livre) {
        String sql = "UPDATE Livre SET isbn = ?, titre = ?, annee_publication = ?, " +
                "description = ?, langue = ?, nombre_pages = ?, chemin_pdf = ?, " +
                "disponible = ?, type_livre = ?, prix = ?, " +
                "id_auteur = ?, id_categorie = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livre.getIsbn());
            stmt.setString(2, livre.getTitre());
            stmt.setObject(3, livre.getAnneePublication());
            stmt.setString(4, livre.getDescription());
            stmt.setString(5, livre.getLangue());
            stmt.setObject(6, livre.getNombrePages());
            stmt.setString(7, livre.getCheminPdf());
            stmt.setBoolean(8, livre.getDisponible() != null ? livre.getDisponible() : true);
            stmt.setString(9, livre.getTypeLivre().name());
            stmt.setDouble(10, livre.getPrix() != null ? livre.getPrix() : 0.0);
            stmt.setLong(11, livre.getIdAuteur());
            stmt.setLong(12, livre.getIdCategorie());
            stmt.setLong(13, livre.getId());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                return livre;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du livre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM Livre WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du livre: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Livre> search(String keyword) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.titre LIKE ? OR l.description LIKE ? OR a.nom LIKE ? OR a.prenom LIKE ? " +
                "ORDER BY l.titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            stmt.setString(4, pattern);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de livres: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    public List<Livre> findByCategorie(Long idCategorie) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.id_categorie = ? ORDER BY l.titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idCategorie);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par catégorie: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    public List<Livre> findByAuteur(Long idAuteur) {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.id_auteur = ? ORDER BY l.titre";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idAuteur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par auteur: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    public List<Livre> findGratuits() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.type_livre = 'GRATUIT' AND l.disponible = true ORDER BY l.titre";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livres gratuits: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    public List<Livre> findPayants() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT l.*, a.nom as auteur_nom, a.prenom as auteur_prenom, " +
                "c.nom as categorie_nom FROM Livre l " +
                "LEFT JOIN Auteur a ON l.id_auteur = a.id " +
                "LEFT JOIN Categorie c ON l.id_categorie = c.id " +
                "WHERE l.type_livre = 'PAYANT' AND l.disponible = true ORDER BY l.titre";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                livres.add(mapResultSetToLivre(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des livres payants: " + e.getMessage());
            e.printStackTrace();
        }
        return livres;
    }

    private Livre mapResultSetToLivre(ResultSet rs) throws SQLException {
        Livre livre = new Livre();
        livre.setId(rs.getLong("id"));
        livre.setIsbn(rs.getString("isbn"));
        livre.setTitre(rs.getString("titre"));
        livre.setAnneePublication(rs.getInt("annee_publication"));
        livre.setDescription(rs.getString("description"));
        livre.setLangue(rs.getString("langue"));
        livre.setNombrePages(rs.getInt("nombre_pages"));
        livre.setCheminPdf(rs.getString("chemin_pdf"));
        livre.setDisponible(rs.getBoolean("disponible"));
        livre.setTypeLivre(TypeLivre.valueOf(rs.getString("type_livre")));
        livre.setPrix(rs.getDouble("prix"));

        Timestamp ts = rs.getTimestamp("date_ajout");
        if (ts != null) {
            livre.setDateAjout(ts.toLocalDateTime());
        }

        livre.setIdAuteur(rs.getLong("id_auteur"));
        livre.setIdCategorie(rs.getLong("id_categorie"));

        // Créer les objets associés
        Auteur auteur = new Auteur();
        auteur.setId(rs.getLong("id_auteur"));
        auteur.setNom(rs.getString("auteur_nom"));
        auteur.setPrenom(rs.getString("auteur_prenom"));
        livre.setAuteur(auteur);

        Categorie categorie = new Categorie();
        categorie.setId(rs.getLong("id_categorie"));
        categorie.setNom(rs.getString("categorie_nom"));
        livre.setCategorie(categorie);

        return livre;
    }
}