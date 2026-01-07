package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import model.*;
import dao.*;
import util.AlertHelper;
import util.FileHelper;
import util.Validation;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le formulaire d'ajout/modification de livre
 */
public class LivreFormController implements Initializable {

    @FXML
    private Label labelTitreForm;
    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitre;
    @FXML
    private ComboBox<Auteur> comboAuteur;
    @FXML
    private ComboBox<Categorie> comboCategorie;
    @FXML
    private TextField txtAnnee;
    @FXML
    private ComboBox<String> comboLangue;
    @FXML
    private TextField txtNombrePages;
    @FXML
    private TextArea txtDescription;
    @FXML
    private TextField txtCheminPdf;
    @FXML
    private RadioButton radioGratuit;
    @FXML
    private RadioButton radioPayant;
    @FXML
    private ToggleGroup groupeType;
    @FXML
    private HBox boxPrix;
    @FXML
    private TextField txtPrix;
    @FXML
    private CheckBox checkDisponible;
    @FXML
    private Label labelErreur;

    private Livre livreAModifier;
    private AdminController adminController;

    private LivreDAO livreDAO;
    private AuteurDAO auteurDAO;
    private CategorieDAO categorieDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les DAOs
        livreDAO = new LivreDAO();
        auteurDAO = new AuteurDAO();
        categorieDAO = new CategorieDAO();

        // Charger les données
        chargerAuteurs();
        chargerCategories();
        chargerLangues();

        // Configurer les listeners
        configurerListeners();
    }

    /**
     * Charger les auteurs dans le ComboBox
     */
    private void chargerAuteurs() {
        try {
            List<Auteur> auteurs = auteurDAO.findAll();
            comboAuteur.setItems(FXCollections.observableArrayList(auteurs));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des auteurs: " + e.getMessage());
        }
    }

    /**
     * Charger les catégories dans le ComboBox
     */
    private void chargerCategories() {
        try {
            List<Categorie> categories = categorieDAO.findAll();
            comboCategorie.setItems(FXCollections.observableArrayList(categories));
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des catégories: " + e.getMessage());
        }
    }

    /**
     * Charger les langues
     */
    private void chargerLangues() {
        comboLangue.setItems(FXCollections.observableArrayList(
                "Français", "Anglais", "Arabe", "Espagnol", "Allemand", "Italien", "Autre"));
        comboLangue.getSelectionModel().select("Français");
    }

    /**
     * Configurer les listeners
     */
    private void configurerListeners() {
        // Listener pour afficher/masquer le champ prix
        radioGratuit.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boxPrix.setVisible(!newVal);
            if (newVal) {
                txtPrix.setText("0.00");
            }
        });

        radioPayant.selectedProperty().addListener((obs, oldVal, newVal) -> {
            boxPrix.setVisible(newVal);
        });

        // Masquer l'erreur lors de la saisie
        txtTitre.textProperty().addListener((obs, oldVal, newVal) -> masquerErreur());
        comboAuteur.valueProperty().addListener((obs, oldVal, newVal) -> masquerErreur());
        comboCategorie.valueProperty().addListener((obs, oldVal, newVal) -> masquerErreur());
    }

    /**
     * Définir le livre à modifier
     */
    public void setLivre(Livre livre) {
        this.livreAModifier = livre;
        labelTitreForm.setText("MODIFIER UN LIVRE");
        remplirFormulaire(livre);
    }

    /**
     * Remplir le formulaire avec les données du livre
     */
    private void remplirFormulaire(Livre livre) {
        txtIsbn.setText(livre.getIsbn());
        txtTitre.setText(livre.getTitre());

        // Sélectionner l'auteur
        if (livre.getAuteur() != null) {
            comboAuteur.getSelectionModel().select(livre.getAuteur());
        }

        // Sélectionner la catégorie
        if (livre.getCategorie() != null) {
            comboCategorie.getSelectionModel().select(livre.getCategorie());
        }

        if (livre.getAnneePublication() != null) {
            txtAnnee.setText(livre.getAnneePublication().toString());
        }

        if (livre.getLangue() != null) {
            comboLangue.getSelectionModel().select(livre.getLangue());
        }

        if (livre.getNombrePages() != null) {
            txtNombrePages.setText(livre.getNombrePages().toString());
        }

        txtDescription.setText(livre.getDescription());
        txtCheminPdf.setText(livre.getCheminPdf());

        // Type de livre
        if (livre.isGratuit()) {
            radioGratuit.setSelected(true);
        } else {
            radioPayant.setSelected(true);
            txtPrix.setText(livre.getPrix().toString());
        }

        checkDisponible.setSelected(livre.getDisponible());
    }

    /**
     * Parcourir pour sélectionner un fichier PDF
     */
    @FXML
    private void handleParcourirPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"));

        File file = fileChooser.showOpenDialog(txtCheminPdf.getScene().getWindow());
        if (file != null) {
            txtCheminPdf.setText(file.getAbsolutePath());
        }
    }

    /**
     * Enregistrer le livre
     */
    @FXML
    private void handleEnregistrer() {
        // Valider les données
        if (!validerFormulaire()) {
            return;
        }

        try {
            // Créer ou mettre à jour le livre
            Livre livre = livreAModifier != null ? livreAModifier : new Livre();

            livre.setIsbn(Validation.sanitize(txtIsbn.getText()));
            livre.setTitre(Validation.sanitize(txtTitre.getText()));
            livre.setIdAuteur(comboAuteur.getValue().getId());
            livre.setAuteur(comboAuteur.getValue());
            livre.setIdCategorie(comboCategorie.getValue().getId());
            livre.setCategorie(comboCategorie.getValue());

            if (!txtAnnee.getText().trim().isEmpty()) {
                livre.setAnneePublication(Integer.parseInt(txtAnnee.getText().trim()));
            }

            livre.setLangue(comboLangue.getValue());

            if (!txtNombrePages.getText().trim().isEmpty()) {
                livre.setNombrePages(Integer.parseInt(txtNombrePages.getText().trim()));
            }

            livre.setDescription(Validation.sanitize(txtDescription.getText()));
            livre.setCheminPdf(txtCheminPdf.getText().trim());

            // Type et prix
            if (radioGratuit.isSelected()) {
                livre.setTypeLivre(TypeLivre.GRATUIT);
                livre.setPrix(0.0);
            } else {
                livre.setTypeLivre(TypeLivre.PAYANT);
                livre.setPrix(Double.parseDouble(txtPrix.getText().trim()));
            }

            livre.setDisponible(checkDisponible.isSelected());

            // Enregistrer dans la base de données
            Livre result;
            if (livreAModifier != null) {
                result = livreDAO.update(livre);
            } else {
                result = livreDAO.create(livre);
            }

            if (result != null) {
                AlertHelper.showSuccess(livreAModifier != null ? "Modification du livre" : "Ajout du livre");

                // Rafraîchir la liste dans AdminController
                if (adminController != null) {
                    adminController.refreshLivres();
                }

                // Fermer la fenêtre
                Stage stage = (Stage) txtTitre.getScene().getWindow();
                stage.close();
            } else {
                afficherErreur("Erreur lors de l'enregistrement du livre");
            }

        } catch (Exception e) {
            afficherErreur("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valider le formulaire
     */
    private boolean validerFormulaire() {
        // ISBN (optionnel mais doit être valide s'il est renseigné)
        String isbn = txtIsbn.getText().trim();
        if (!isbn.isEmpty() && !Validation.isValidISBN(isbn)) {
            afficherErreur("ISBN invalide. Format attendu : 978-X-XXX-XXXXX-X");
            txtIsbn.requestFocus();
            return false;
        }

        // Titre obligatoire
        if (!Validation.isNotEmpty(txtTitre.getText())) {
            afficherErreur("Le titre est obligatoire");
            txtTitre.requestFocus();
            return false;
        }

        // Auteur obligatoire
        if (comboAuteur.getValue() == null) {
            afficherErreur("Veuillez sélectionner un auteur");
            comboAuteur.requestFocus();
            return false;
        }

        // Catégorie obligatoire
        if (comboCategorie.getValue() == null) {
            afficherErreur("Veuillez sélectionner une catégorie");
            comboCategorie.requestFocus();
            return false;
        }

        // Année (optionnelle mais doit être valide)
        String annee = txtAnnee.getText().trim();
        if (!annee.isEmpty() && !Validation.isValidYear(annee)) {
            afficherErreur("Année invalide");
            txtAnnee.requestFocus();
            return false;
        }

        // Nombre de pages (optionnel mais doit être un entier positif)
        String pages = txtNombrePages.getText().trim();
        if (!pages.isEmpty() && (!Validation.isInteger(pages) || !Validation.isPositiveNumber(pages))) {
            afficherErreur("Le nombre de pages doit être un entier positif");
            txtNombrePages.requestFocus();
            return false;
        }

        // Chemin PDF obligatoire
        if (!Validation.isNotEmpty(txtCheminPdf.getText())) {
            afficherErreur("Le chemin du fichier PDF est obligatoire");
            txtCheminPdf.requestFocus();
            return false;
        }

        // Vérifier que le fichier existe (optionnel en développement)
        if (!FileHelper.fileExists(txtCheminPdf.getText().trim())) {
            afficherErreur("Le fichier PDF n'existe pas");
            txtCheminPdf.requestFocus();
            return false;
        }

        // Prix si livre payant
        if (radioPayant.isSelected()) {
            String prix = txtPrix.getText().trim();
            if (!Validation.isValidPrice(prix)) {
                afficherErreur("Prix invalide. Format: 19.99");
                txtPrix.requestFocus();
                return false;
            }

            if (!Validation.isPositiveNumber(prix)) {
                afficherErreur("Le prix doit être supérieur à 0");
                txtPrix.requestFocus();
                return false;
            }
        }

        return true;
    }

    /**
     * Annuler et fermer
     */
    @FXML
    private void handleAnnuler() {
        boolean confirme = AlertHelper.showConfirmation(
                "Annuler",
                "Voulez-vous vraiment annuler ?",
                "Les modifications non enregistrées seront perdues");

        if (confirme) {
            Stage stage = (Stage) txtTitre.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Afficher un message d'erreur
     */
    private void afficherErreur(String message) {
        labelErreur.setText("⚠️ " + message);
        labelErreur.setVisible(true);
    }

    /**
     * Masquer le message d'erreur
     */
    private void masquerErreur() {
        if (labelErreur.isVisible()) {
            labelErreur.setVisible(false);
        }
    }

    /**
     * Définir le contrôleur admin
     */
    public void setAdminController(AdminController controller) {
        this.adminController = controller;
    }
}