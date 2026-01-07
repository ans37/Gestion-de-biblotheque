package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import dao.*;
import util.AlertHelper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le panneau d'administration
 */
public class AdminController implements Initializable {

    // Onglet Livres
    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, Long> colId;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colAnnee;
    @FXML private TableColumn<Livre, String> colType;
    @FXML private TableColumn<Livre, Double> colPrix;
    @FXML private TableColumn<Livre, Boolean> colDisponible;

    @FXML private TextField txtRechercheAdmin;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Label labelTotalLivres;
    @FXML private Label labelLivresGratuits;
    @FXML private Label labelLivresPayants;

    // Onglet Auteurs
    @FXML private TableView<Auteur> tableAuteurs;
    @FXML private TableColumn<Auteur, Long> colAuteurId;
    @FXML private TableColumn<Auteur, String> colAuteurNom;
    @FXML private TableColumn<Auteur, String> colAuteurPrenom;
    @FXML private TableColumn<Auteur, String> colAuteurBio;
    @FXML private Button btnModifierAuteur;
    @FXML private Button btnSupprimerAuteur;

    // Onglet Catégories
    @FXML private TableView<Categorie> tableCategories;
    @FXML private TableColumn<Categorie, Long> colCategorieId;
    @FXML private TableColumn<Categorie, String> colCategorieNom;
    @FXML private TableColumn<Categorie, String> colCategorieDesc;
    @FXML private Button btnModifierCategorie;
    @FXML private Button btnSupprimerCategorie;

    // Statistiques
    @FXML private Label labelStatTotalLivres;
    @FXML private Label labelStatLivresGratuits;
    @FXML private Label labelStatLivresPayants;
    @FXML private Label labelStatTotalAuteurs;
    @FXML private Label labelStatTotalCategories;
    @FXML private Label labelStatTotalTelechargements;

    // DAOs
    private LivreDAO livreDAO;
    private AuteurDAO auteurDAO;
    private CategorieDAO categorieDAO;
    private TelechargementDAO telechargementDAO;

    private ObservableList<Livre> livresObservable;
    private ObservableList<Auteur> auteursObservable;
    private ObservableList<Categorie> categoriesObservable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les DAOs
        livreDAO = new LivreDAO();
        auteurDAO = new AuteurDAO();
        categorieDAO = new CategorieDAO();
        telechargementDAO = new TelechargementDAO();

        // Configurer les tables
        configurerTableLivres();
        configurerTableAuteurs();
        configurerTableCategories();

        // Charger les données
        chargerLivres();
        chargerAuteurs();
        chargerCategories();
        chargerStatistiques();

        // Configurer les listeners
        configurerListeners();
    }

    /**
     * Configurer la table des livres
     */
    private void configurerTableLivres() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));

        // Colonne auteur personnalisée
        colAuteur.setCellValueFactory(cellData -> {
            Auteur auteur = cellData.getValue().getAuteur();
            return new javafx.beans.property.SimpleStringProperty(
                    auteur != null ? auteur.getNomComplet() : "N/A"
            );
        });

        // Colonne catégorie personnalisée
        colCategorie.setCellValueFactory(cellData -> {
            Categorie categorie = cellData.getValue().getCategorie();
            return new javafx.beans.property.SimpleStringProperty(
                    categorie != null ? categorie.getNom() : "N/A"
            );
        });

        colAnnee.setCellValueFactory(new PropertyValueFactory<>("anneePublication"));

        // Colonne type personnalisée
        colType.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getTypeLivre().name()
            );
        });

        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colDisponible.setCellValueFactory(new PropertyValueFactory<>("disponible"));
    }

    /**
     * Configurer la table des auteurs
     */
    private void configurerTableAuteurs() {
        colAuteurId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAuteurNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colAuteurPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colAuteurBio.setCellValueFactory(new PropertyValueFactory<>("biographie"));
    }

    /**
     * Configurer la table des catégories
     */
    private void configurerTableCategories() {
        colCategorieId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCategorieNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colCategorieDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    /**
     * Configurer les listeners pour les sélections
     */
    private void configurerListeners() {
        // Listener pour la sélection dans la table des livres
        tableLivres.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnModifier.setDisable(!selected);
            btnSupprimer.setDisable(!selected);
        });

        // Listener pour la sélection dans la table des auteurs
        tableAuteurs.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnModifierAuteur.setDisable(!selected);
            btnSupprimerAuteur.setDisable(!selected);
        });

        // Listener pour la sélection dans la table des catégories
        tableCategories.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnModifierCategorie.setDisable(!selected);
            btnSupprimerCategorie.setDisable(!selected);
        });
    }

    /**
     * Charger tous les livres
     */
    private void chargerLivres() {
        try {
            List<Livre> livres = livreDAO.findAll();
            livresObservable = FXCollections.observableArrayList(livres);
            tableLivres.setItems(livresObservable);

            // Mettre à jour les statistiques
            int gratuits = (int) livres.stream().filter(Livre::isGratuit).count();
            int payants = livres.size() - gratuits;

            labelTotalLivres.setText("Total : " + livres.size() + " livres");
            labelLivresGratuits.setText("Gratuits : " + gratuits);
            labelLivresPayants.setText("Payants : " + payants);

        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors du chargement des livres", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charger tous les auteurs
     */
    private void chargerAuteurs() {
        try {
            List<Auteur> auteurs = auteurDAO.findAll();
            auteursObservable = FXCollections.observableArrayList(auteurs);
            tableAuteurs.setItems(auteursObservable);
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors du chargement des auteurs", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charger toutes les catégories
     */
    private void chargerCategories() {
        try {
            List<Categorie> categories = categorieDAO.findAll();
            categoriesObservable = FXCollections.observableArrayList(categories);
            tableCategories.setItems(categoriesObservable);
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors du chargement des catégories", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charger les statistiques
     */
    private void chargerStatistiques() {
        try {
            List<Livre> livres = livreDAO.findAll();
            int totalLivres = livres.size();
            int gratuits = (int) livres.stream().filter(Livre::isGratuit).count();
            int payants = totalLivres - gratuits;

            int totalAuteurs = auteurDAO.findAll().size();
            int totalCategories = categorieDAO.findAll().size();
            int totalTelechargements = telechargementDAO.countTotal();

            labelStatTotalLivres.setText(String.valueOf(totalLivres));
            labelStatLivresGratuits.setText(String.valueOf(gratuits));
            labelStatLivresPayants.setText(String.valueOf(payants));
            labelStatTotalAuteurs.setText(String.valueOf(totalAuteurs));
            labelStatTotalCategories.setText(String.valueOf(totalCategories));
            labelStatTotalTelechargements.setText(String.valueOf(totalTelechargements));

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== GESTION DES LIVRES ====================

    @FXML
    private void handleAjouterLivre() {
        ouvrirFormulairelivre(null);
    }

    @FXML
    private void handleModifierLivre() {
        Livre livreSelectionne = tableLivres.getSelectionModel().getSelectedItem();
        if (livreSelectionne != null) {
            ouvrirFormulairelivre(livreSelectionne);
        }
    }

    @FXML
    private void handleSupprimerLivre() {
        Livre livreSelectionne = tableLivres.getSelectionModel().getSelectedItem();

        if (livreSelectionne == null) {
            return;
        }

        boolean confirme = AlertHelper.showConfirmation(
                "Confirmer la suppression",
                "Supprimer le livre \"" + livreSelectionne.getTitre() + "\" ?",
                "Cette action est irréversible."
        );

        if (confirme) {
            try {
                boolean success = livreDAO.delete(livreSelectionne.getId());
                if (success) {
                    livresObservable.remove(livreSelectionne);
                    AlertHelper.showSuccess("Suppression du livre");
                    chargerStatistiques();
                } else {
                    AlertHelper.showError("Erreur", "Échec de la suppression",
                            "Le livre n'a pas pu être supprimé");
                }
            } catch (Exception e) {
                AlertHelper.showError("Erreur", "Erreur lors de la suppression", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Ouvrir le formulaire de livre
     */
    private void ouvrirFormulairelivre(Livre livre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LivreFormView.fxml"));
            Parent root = loader.load();

            LivreFormController controller = loader.getController();
            controller.setAdminController(this);

            if (livre != null) {
                controller.setLivre(livre);
            }

            Stage stage = new Stage();
            stage.setTitle(livre == null ? "Ajouter un livre" : "Modifier un livre");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            AlertHelper.showError("Erreur", "Impossible d'ouvrir le formulaire", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRechercherAdmin() {
        String keyword = txtRechercheAdmin.getText().trim();

        if (keyword.isEmpty()) {
            chargerLivres();
            return;
        }

        try {
            List<Livre> livres = livreDAO.search(keyword);
            livresObservable = FXCollections.observableArrayList(livres);
            tableLivres.setItems(livresObservable);
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors de la recherche", e.getMessage());
        }
    }

    @FXML
    private void handleActualiser() {
        chargerLivres();
        chargerAuteurs();
        chargerCategories();
        chargerStatistiques();
        AlertHelper.showInfo("Actualisation", "Données actualisées",
                "Toutes les données ont été rechargées");
    }

    // ==================== GESTION DES AUTEURS ====================

    @FXML
    private void handleAjouterAuteur() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter un auteur");
        dialog.setHeaderText("Nouvel auteur");
        dialog.setContentText("Nom:");

        Optional<String> resultNom = dialog.showAndWait();
        if (!resultNom.isPresent()) return;

        dialog = new TextInputDialog();
        dialog.setContentText("Prénom:");
        Optional<String> resultPrenom = dialog.showAndWait();
        if (!resultPrenom.isPresent()) return;

        Auteur auteur = new Auteur(resultNom.get(), resultPrenom.get());

        try {
            Auteur created = auteurDAO.create(auteur);
            if (created != null) {
                auteursObservable.add(created);
                AlertHelper.showSuccess("Ajout de l'auteur");
                chargerStatistiques();
            }
        } catch (Exception e) {
            AlertHelper.showError("Erreur", "Erreur lors de l'ajout", e.getMessage());
        }
    }

    @FXML
    private void handleModifierAuteur() {
        Auteur auteurSelectionne = tableAuteurs.getSelectionModel().getSelectedItem();
        if (auteurSelectionne == null) return;

        // À implémenter : ouvrir un formulaire de modification
        AlertHelper.showInfo("Info", "Fonctionnalité à venir",
                "La modification d'auteur sera disponible prochainement");
    }

    @FXML
    private void handleSupprimerAuteur() {
        Auteur auteurSelectionne = tableAuteurs.getSelectionModel().getSelectedItem();
        if (auteurSelectionne == null) return;

        boolean confirme = AlertHelper.showConfirmation(
                "Confirmer la suppression",
                "Supprimer l'auteur " + auteurSelectionne.getNomComplet() + " ?",
                "Tous les livres de cet auteur seront également supprimés."
        );

        if (confirme) {
            try {
                boolean success = auteurDAO.delete(auteurSelectionne.getId());
                if (success) {
                    auteursObservable.remove(auteurSelectionne);
                    chargerLivres(); // Recharger les livres
                    AlertHelper.showSuccess("Suppression de l'auteur");
                    chargerStatistiques();
                }
            } catch (Exception e) {
                AlertHelper.showError("Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    // ==================== GESTION DES CATÉGORIES ====================

    @FXML
    private void handleAjouterCategorie() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter une catégorie");
        dialog.setHeaderText("Nouvelle catégorie");
        dialog.setContentText("Nom:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            Categorie categorie = new Categorie(result.get());

            try {
                Categorie created = categorieDAO.create(categorie);
                if (created != null) {
                    categoriesObservable.add(created);
                    AlertHelper.showSuccess("Ajout de la catégorie");
                    chargerStatistiques();
                }
            } catch (Exception e) {
                AlertHelper.showError("Erreur", "Erreur lors de l'ajout", e.getMessage());
            }
        }
    }

    @FXML
    private void handleModifierCategorie() {
        AlertHelper.showInfo("Info", "Fonctionnalité à venir",
                "La modification de catégorie sera disponible prochainement");
    }

    @FXML
    private void handleSupprimerCategorie() {
        Categorie categorieSelectionnee = tableCategories.getSelectionModel().getSelectedItem();
        if (categorieSelectionnee == null) return;

        boolean confirme = AlertHelper.showConfirmation(
                "Confirmer la suppression",
                "Supprimer la catégorie \"" + categorieSelectionnee.getNom() + "\" ?",
                "Tous les livres de cette catégorie seront également supprimés."
        );

        if (confirme) {
            try {
                boolean success = categorieDAO.delete(categorieSelectionnee.getId());
                if (success) {
                    categoriesObservable.remove(categorieSelectionnee);
                    chargerLivres();
                    AlertHelper.showSuccess("Suppression de la catégorie");
                    chargerStatistiques();
                }
            } catch (Exception e) {
                AlertHelper.showError("Erreur", "Erreur lors de la suppression", e.getMessage());
            }
        }
    }

    @FXML
    private void handleRetourAccueil() {
        Stage stage = (Stage) tableLivres.getScene().getWindow();
        stage.close();
    }

    /**
     * Rafraîchir la liste des livres (appelé depuis LivreFormController)
     */
    public void refreshLivres() {
        chargerLivres();
        chargerStatistiques();
    }
}