package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;
import dao.*;
import util.AlertHelper;
import util.StringHelper;
import util.Validation;

import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la page de paiement
 */
public class PaiementController implements Initializable {

    // Récapitulatif commande
    @FXML
    private Label labelTitreLivre;
    @FXML
    private Label labelAuteurLivre;
    @FXML
    private Label labelPrixLivre;
    @FXML
    private Label labelMontantTotal;

    // Informations de paiement
    @FXML
    private ComboBox<String> comboModePaiement;
    @FXML
    private TextField txtNumeroCarte;
    @FXML
    private TextField txtNomCarte;
    @FXML
    private ComboBox<String> comboMois;
    @FXML
    private ComboBox<String> comboAnnee;
    @FXML
    private TextField txtCVV;

    // Adresse de facturation
    @FXML
    private TextField txtNomComplet;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtTelephone;
    @FXML
    private TextArea txtAdresse;
    @FXML
    private TextField txtVille;
    @FXML
    private TextField txtCodePostal;

    // Autres
    @FXML
    private CheckBox checkConditions;
    @FXML
    private Label labelErreur;

    private Livre livre;
    private Utilisateur utilisateur;
    private LivreDetailsController livreDetailsController;

    private PaiementDAO paiementDAO;
    private TelechargementDAO telechargementDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paiementDAO = new PaiementDAO();
        telechargementDAO = new TelechargementDAO();

        // Initialiser les ComboBox
        initialiserModesPaiement();
        initialiserDateExpiration();
    }

    /**
     * Initialiser les modes de paiement
     */
    private void initialiserModesPaiement() {
        List<String> modes = List.of(
                "Carte Bancaire",
                "Carte de Crédit",
                "PayPal",
                "Virement Bancaire");
        comboModePaiement.setItems(FXCollections.observableArrayList(modes));
        comboModePaiement.getSelectionModel().select(0);
    }

    /**
     * Initialiser les dates d'expiration
     */
    private void initialiserDateExpiration() {
        // Mois
        List<String> mois = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            mois.add(String.format("%02d", i));
        }
        comboMois.setItems(FXCollections.observableArrayList(mois));

        // Années (année actuelle + 10 ans)
        List<String> annees = new ArrayList<>();
        int anneeActuelle = Year.now().getValue();
        for (int i = 0; i <= 10; i++) {
            annees.add(String.valueOf(anneeActuelle + i));
        }
        comboAnnee.setItems(FXCollections.observableArrayList(annees));
    }

    /**
     * Définir le livre à acheter
     */
    public void setLivre(Livre livre) {
        this.livre = livre;
        afficherRecapitulatif();
    }

    /**
     * Définir l'utilisateur
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;

        // Pré-remplir certains champs si possible
        if (utilisateur != null) {
            if (utilisateur.getNom() != null && utilisateur.getPrenom() != null) {
                txtNomComplet.setText(utilisateur.getPrenom() + " " + utilisateur.getNom());
            }
            if (utilisateur.getEmail() != null) {
                txtEmail.setText(utilisateur.getEmail());
            }
        }
    }

    /**
     * Définir le contrôleur de détails du livre
     */
    public void setLivreDetailsController(LivreDetailsController controller) {
        this.livreDetailsController = controller;
    }

    /**
     * Afficher le récapitulatif de la commande
     */
    private void afficherRecapitulatif() {
        if (livre == null)
            return;

        labelTitreLivre.setText(livre.getTitre());
        labelAuteurLivre.setText(livre.getAuteur() != null ? livre.getAuteur().getNomComplet() : "Inconnu");
        labelPrixLivre.setText(StringHelper.formatPrice(livre.getPrix()));
        labelMontantTotal.setText(StringHelper.formatPrice(livre.getPrix()));
    }

    /**
     * Gérer le paiement
     */
    @FXML
    private void handlePayer() {
        // Valider le formulaire
        if (!validerFormulaire()) {
            return;
        }

        // Vérifier que l'utilisateur a accepté les conditions
        if (!checkConditions.isSelected()) {
            afficherErreur("Vous devez accepter les conditions générales");
            checkConditions.requestFocus();
            return;
        }

        try {
            // Créer le paiement
            Paiement paiement = new Paiement(
                    utilisateur.getId(),
                    livre.getId(),
                    livre.getPrix(),
                    comboModePaiement.getValue());

            // Simuler le traitement du paiement
            boolean paiementReussi = traiterPaiement(paiement);

            if (paiementReussi) {
                // Marquer le paiement comme payé
                paiement.marquerCommePaye();

                // Enregistrer le paiement dans la base de données
                Paiement paiementCree = paiementDAO.create(paiement);

                if (paiementCree != null) {
                    // Enregistrer le droit de téléchargement
                    telechargementDAO.enregistrerTelechargement(
                            utilisateur.getId(),
                            livre.getId(),
                            paiementCree.getId());

                    // Afficher la confirmation
                    afficherConfirmationPaiement(paiementCree);

                    // Rafraîchir la page de détails si elle existe
                    if (livreDetailsController != null) {
                        livreDetailsController.rafraichir();
                    }

                    // Fermer la fenêtre de paiement
                    Stage stage = (Stage) txtNumeroCarte.getScene().getWindow();
                    stage.close();
                } else {
                    afficherErreur("Erreur lors de l'enregistrement du paiement");
                }
            } else {
                afficherErreur("Le paiement a été refusé. Veuillez vérifier vos informations.");
            }

        } catch (Exception e) {
            afficherErreur("Erreur lors du traitement du paiement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Simuler le traitement du paiement
     */
    private boolean traiterPaiement(Paiement paiement) {
        // Simulation d'un délai de traitement
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Dans une vraie application, ici vous appelleriez une API de paiement
        // Pour la simulation, nous acceptons tous les paiements

        // Validation basique du numéro de carte (algorithme de Luhn simplifié)
        String numeroCarte = txtNumeroCarte.getText().replaceAll("\\s+", "");

        // Vérifier que ce n'est pas une carte de test invalide (commence par 0000)
        if (numeroCarte.startsWith("0000")) {
            return false;
        }

        // Sinon, accepter le paiement
        return true;
    }

    /**
     * Afficher la confirmation du paiement
     */
    private void afficherConfirmationPaiement(Paiement paiement) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Paiement réussi");
        alert.setHeaderText("✓ Votre paiement a été accepté");

        String message = String.format(
                "Merci pour votre achat !\n\n" +
                        "Livre : %s\n" +
                        "Montant : %s\n" +
                        "Mode de paiement : %s\n" +
                        "Référence : %s\n\n" +
                        "Vous pouvez maintenant télécharger le livre.",
                livre.getTitre(),
                StringHelper.formatPrice(paiement.getMontant()),
                paiement.getModePaiement(),
                paiement.getReferenceTransaction());

        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Valider le formulaire de paiement
     */
    private boolean validerFormulaire() {
        // Mode de paiement
        if (comboModePaiement.getValue() == null) {
            afficherErreur("Veuillez sélectionner un mode de paiement");
            return false;
        }

        // Numéro de carte
        String numeroCarte = txtNumeroCarte.getText().replaceAll("\\s+", "");
        if (!Validation.isNotEmpty(numeroCarte)) {
            afficherErreur("Le numéro de carte est requis");
            txtNumeroCarte.requestFocus();
            return false;
        }

        if (!Validation.isNumeric(numeroCarte) || numeroCarte.length() < 13 || numeroCarte.length() > 19) {
            afficherErreur("Numéro de carte invalide (13 à 19 chiffres)");
            txtNumeroCarte.requestFocus();
            return false;
        }

        // Nom sur la carte
        if (!Validation.isNotEmpty(txtNomCarte.getText())) {
            afficherErreur("Le nom sur la carte est requis");
            txtNomCarte.requestFocus();
            return false;
        }

        // Date d'expiration
        if (comboMois.getValue() == null || comboAnnee.getValue() == null) {
            afficherErreur("La date d'expiration est requise");
            return false;
        }

        // CVV
        String cvv = txtCVV.getText().trim();
        if (!Validation.isNumeric(cvv) || (cvv.length() != 3 && cvv.length() != 4)) {
            afficherErreur("CVV invalide (3 ou 4 chiffres)");
            txtCVV.requestFocus();
            return false;
        }

        // Nom complet
        if (!Validation.isNotEmpty(txtNomComplet.getText())) {
            afficherErreur("Le nom complet est requis");
            txtNomComplet.requestFocus();
            return false;
        }

        // Email
        if (!Validation.isValidEmail(txtEmail.getText())) {
            afficherErreur("Email invalide");
            txtEmail.requestFocus();
            return false;
        }

        // Téléphone (optionnel mais doit être valide s'il est renseigné)
        String telephone = txtTelephone.getText().trim();
        if (!telephone.isEmpty() && !Validation.isValidPhone(telephone)) {
            afficherErreur("Numéro de téléphone invalide. Format: +212 6XX XXX XXX ou 06XX XXX XXX");
            txtTelephone.requestFocus();
            return false;
        }

        // Adresse
        if (!Validation.isNotEmpty(txtAdresse.getText())) {
            afficherErreur("L'adresse est requise");
            txtAdresse.requestFocus();
            return false;
        }

        // Ville
        if (!Validation.isNotEmpty(txtVille.getText())) {
            afficherErreur("La ville est requise");
            txtVille.requestFocus();
            return false;
        }

        // Code postal
        String codePostal = txtCodePostal.getText().trim();
        if (!Validation.isNumeric(codePostal) || codePostal.length() != 5) {
            afficherErreur("Code postal invalide (5 chiffres)");
            txtCodePostal.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Retour
     */
    @FXML
    private void handleRetour() {
        boolean confirme = AlertHelper.showConfirmation(
                "Annuler le paiement",
                "Voulez-vous vraiment annuler le paiement ?",
                "Vous devrez recommencer le processus");

        if (confirme) {
            Stage stage = (Stage) txtNumeroCarte.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Afficher un message d'erreur
     */
    private void afficherErreur(String message) {
        labelErreur.setText("⚠️ " + message);
        labelErreur.setVisible(true);

        // Masquer automatiquement après 5 secondes
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> labelErreur.setVisible(false));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}