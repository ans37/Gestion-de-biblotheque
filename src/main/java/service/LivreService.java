package service;

import dao.LivreDAO;
import model.Livre;

import java.util.List;

/**
 * Service gérant la logique métier pour les livres.
 * Cette classe fait l'intermédiaire entre les contrôleurs (Vue) et la DAO (Base
 * de données).
 * Elle ne contient AUCUN code JavaFX.
 */
public class LivreService {

    private final LivreDAO livreDAO;

    public LivreService() {
        this.livreDAO = new LivreDAO();
    }

    /**
     * Récupère la liste de tous les livres.
     * 
     * @return Liste de livres
     */
    public List<Livre> recupererTousLesLivres() {
        return livreDAO.findAll();
    }

    /**
     * Récupère les livres d'une catégorie spécifique.
     * 
     * @param idCategorie ID de la catégorie
     * @return Liste de livres de cette catégorie
     * @throws IllegalArgumentException si l'ID de la catégorie est null
     */
    public List<Livre> recupererLivresParCategorie(Long idCategorie) {
        if (idCategorie == null) {
            throw new IllegalArgumentException("L'identifiant de la catégorie ne peut pas être null.");
        }
        return livreDAO.findByCategorie(idCategorie);
    }

    /**
     * Recherche des livres par mot-clé (titre ou auteur).
     * 
     * @param motCle Mot-clé de recherche
     * @return Liste de livres correspondants
     */
    public List<Livre> rechercherLivres(String motCle) {
        if (motCle == null || motCle.trim().isEmpty()) {
            return recupererTousLesLivres();
        }
        return livreDAO.search(motCle.trim());
    }

    /**
     * Ajoute un nouveau livre après validation.
     * 
     * @param livre Le livre à ajouter
     * @return Le livre ajouté avec son ID généré
     * @throws IllegalArgumentException Si les données du livre sont invalides
     */
    public Livre ajouterLivre(Livre livre) {
        validerDonneesLivre(livre);
        return livreDAO.create(livre);
    }

    /**
     * Met à jour un livre existant après validation.
     * 
     * @param livre Le livre à mettre à jour
     * @return Le livre mis à jour
     * @throws IllegalArgumentException Si les données du livre sont invalides
     */
    public Livre modifierLivre(Livre livre) {
        validerDonneesLivre(livre); // Vérifie aussi les données pour la modif
        if (livre.getId() == null) {
            throw new IllegalArgumentException("Impossible de modifier un livre sans identifiant.");
        }
        return livreDAO.update(livre);
    }

    /**
     * Supprime un livre par son ID.
     * 
     * @param id ID du livre à supprimer
     * @return true si suppression réussie
     */
    public boolean supprimerLivre(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null pour la suppression.");
        }
        return livreDAO.delete(id);
    }

    /**
     * Vérifie la validité des données d'un livre avant traitement.
     * 
     * @param livre Le livre à valider
     * @throws IllegalArgumentException Si une règle métier n'est pas respectée
     */
    private void validerDonneesLivre(Livre livre) {
        if (livre == null) {
            throw new IllegalArgumentException("Le livre ne peut pas être null.");
        }

        if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du livre est obligatoire.");
        }

        if (livre.getAuteur() == null && livre.getIdAuteur() == 0) { // Adapter selon votre logique Auteur
            // Note: Si getIdAuteur est Long, vérifier null. Si long primitif, vérifier 0 ou
            // valeur par défaut.
            // Dans Livre.java : private Long idAuteur; -> check null
            if (livre.getIdAuteur() == null) {
                throw new IllegalArgumentException("L'auteur est obligatoire.");
            }
        }

        if (livre.getPrix() != null && livre.getPrix() < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif.");
        }

        if (livre.getNombrePages() != null && livre.getNombrePages() <= 0) {
            throw new IllegalArgumentException("Le nombre de pages doit être positif.");
        }

        // Autres validations métier (ISBN, année cohérente, etc.)
    }
}
