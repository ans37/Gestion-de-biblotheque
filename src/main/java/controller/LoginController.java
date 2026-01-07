package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Utilisateur;
import model.Role;
import dao.UtilisateurDAO;
import util.AlertHelper;
import util.SessionManager;
import util.Validation;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la page de connexion administrateur
 */
public class LoginController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label labelErreur;

    private UtilisateurDAO utilisateurDAO;
    private MainController mainController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurDAO = new UtilisateurDAO();

        // Ajouter un listener pour masquer l'erreur lors de la saisie
        txtUsername.textProperty().addListener((obs, oldVal, newVal) -> {
            if (labelErreur.isVisible()) {
                labelErreur.setVisible(false);
            }
        });

        txtPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (labelErreur.isVisible()) {
                labelErreur.setVisible(false);
            }
        });
    }

    /**
     * Gérer la connexion
     */
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validation des champs
        if (!validateInputs(username, password)) {
            return;
        }

        try {
            // Authentifier l'utilisateur
            Utilisateur user = utilisateurDAO.authenticate(username, password);

            if (user == null) {
                afficherErreur("Nom d'utilisateur ou mot de passe incorrect");
                return;
            }

            // Vérifier que c'est un administrateur
            if (user.getRole() != Role.ADMINISTRATEUR) {
                afficherErreur("Accès refusé. Vous devez être administrateur pour vous connecter.");
                return;
            }

            // Enregistrer la session
            SessionManager.getInstance().setCurrentUser(user);

            // Informer le contrôleur principal
            if (mainController != null) {
                mainController.setUtilisateurConnecte(user);
            }

            // Afficher un message de succès
            AlertHelper.showSuccess("Connexion");

            // Fermer la fenêtre de connexion
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
            afficherErreur("Erreur lors de la connexion. Veuillez réessayer.");
        }
    }

    /**
     * Valider les entrées utilisateur
     */
    private boolean validateInputs(String username, String password) {
        // Vérifier que les champs ne sont pas vides
        if (!Validation.isNotEmpty(username)) {
            afficherErreur("Le nom d'utilisateur est requis");
            txtUsername.requestFocus();
            return false;
        }

        if (!Validation.isNotEmpty(password)) {
            afficherErreur("Le mot de passe est requis");
            txtPassword.requestFocus();
            return false;
        }

        // Vérifier la longueur minimum
        if (!Validation.hasMinLength(username, 3)) {
            afficherErreur("Le nom d'utilisateur doit contenir au moins 3 caractères");
            txtUsername.requestFocus();
            return false;
        }

        if (!Validation.hasMinLength(password, 4)) {
            afficherErreur("Le mot de passe doit contenir au moins 4 caractères");
            txtPassword.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Gérer l'annulation
     */
    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }

    /**
     * Afficher un message d'erreur
     */
    private void afficherErreur(String message) {
        labelErreur.setText("⚠️ " + message);
        labelErreur.setVisible(true);
    }

    /**
     * Définir le contrôleur principal
     */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
}