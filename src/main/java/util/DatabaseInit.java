package util;

import dao.CategorieDAO;
import model.Categorie;

import java.util.Arrays;
import java.util.List;

public class DatabaseInit {

    public static void initializeDefaultData() {
        System.out.println("Vérification des données par défaut...");
        initializeCategories();
    }

    private static void initializeCategories() {
        CategorieDAO categorieDAO = new CategorieDAO();

        List<String[]> defaultCategories = Arrays.asList(
                new String[] { "Roman", "Fictions littéraires" },
                new String[] { "Science-Fiction", "Romans futuristes et technologiques" },
                new String[] { "Informatique", "Livres sur le développement, réseaux, IA, etc." },
                new String[] { "Histoire", "Ouvrages historiques" },
                new String[] { "Sciences", "Physique, Chimie, Biologie, Mathématiques" });

        for (String[] catData : defaultCategories) {
            String nom = catData[0];
            String description = catData[1];

            if (categorieDAO.findByNom(nom) == null) {
                System.out.println("Création de la catégorie manquante : " + nom);
                Categorie categorie = new Categorie();
                categorie.setNom(nom);
                categorie.setDescription(description);
                categorieDAO.create(categorie);
            }
        }
    }
}
