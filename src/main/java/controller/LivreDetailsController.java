package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import dao.*;
import util.AlertHelper;
import util.StringHelper;
import util.PDFManager;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.Desktop;

import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Contrôleur pour la page de détails d'un livre
 */
public class LivreDetailsController {

    @FXML
    private Label labelTitre;
    @FXML
    private Label labelAuteur;
    @FXML
    private Label labelCategorie;
    @FXML
    private Label labelTypeLivre;
    @FXML
    private Label labelPrix;
    @FXML
    private Label labelIsbn;
    @FXML
    private Label labelAnnee;
    @FXML
    private Label labelLangue;
    @FXML
    private Label labelPages;
    @FXML
    private Label labelDisponibilite;
    @FXML
    private TextArea txtDescription;
    @FXML
    private Button btnTelecharger;
    @FXML
    private Label labelMessagePaiement;

    private Livre livre;
    private Utilisateur utilisateur;
    private LivreDAO livreDAO;
    private ConsultationDAO consultationDAO;
    private PaiementDAO paiementDAO;
    private TelechargementDAO telechargementDAO;

    public LivreDetailsController() {
        livreDAO = new LivreDAO();
        consultationDAO = new ConsultationDAO();
        paiementDAO = new PaiementDAO();
        telechargementDAO = new TelechargementDAO();
    }

    /**
     * Définir le livre à afficher
     */
    public void setLivre(Livre livre) {
        this.livre = livre;
        afficherDetailsLivre();
    }

    /**
     * Définir l'utilisateur connecté
     */
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    /**
     * Afficher les détails du livre
     */
    private void afficherDetailsLivre() {
        if (livre == null)
            return;

        // Informations principales
        labelTitre.setText(livre.getTitre());
        labelAuteur.setText(livre.getAuteur() != null ? livre.getAuteur().getNomComplet() : "Inconnu");
        labelCategorie.setText(livre.getCategorie() != null ? livre.getCategorie().getNom() : "N/A");

        // Type et prix
        if (livre.isGratuit()) {
            labelTypeLivre.setText("GRATUIT");
            labelTypeLivre.getStyleClass().clear();
            labelTypeLivre.getStyleClass().add("badge-gratuit");
            labelPrix.setVisible(false);
            labelMessagePaiement.setVisible(false);
        } else {
            labelTypeLivre.setText("PAYANT");
            labelTypeLivre.getStyleClass().clear();
            labelTypeLivre.getStyleClass().add("badge-payant");
            labelPrix.setText(StringHelper.formatPrice(livre.getPrix()));
            labelPrix.setVisible(true);

            // Vérifier si l'utilisateur a déjà payé
            if (utilisateur != null && paiementDAO.hasUserPaidForBook(utilisateur.getId(), livre.getId())) {
                labelMessagePaiement.setText("✓ Vous avez déjà acheté ce livre");
                labelMessagePaiement.setStyle("-fx-text-fill: #27ae60;");
                labelMessagePaiement.setVisible(true);
            } else {
                labelMessagePaiement.setVisible(true);
            }
        }

        // Autres informations
        labelIsbn.setText(livre.getIsbn() != null ? livre.getIsbn() : "N/A");
        labelAnnee.setText(livre.getAnneePublication() != null ? livre.getAnneePublication().toString() : "N/A");
        labelLangue.setText(livre.getLangue() != null ? livre.getLangue() : "N/A");
        String taille = PDFManager.getTaillePDFMo(livre.getCheminPdf());
        labelPages
                .setText((livre.getNombrePages() != null ? livre.getNombrePages() + " pages" : "N/A") + " - " + taille);

        // Disponibilité
        if (livre.getDisponible()) {
            labelDisponibilite.setText("✓ Disponible");
            labelDisponibilite.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        } else {
            labelDisponibilite.setText("✗ Non disponible");
            labelDisponibilite.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }

        // Description
        txtDescription
                .setText(livre.getDescription() != null ? livre.getDescription() : "Aucune description disponible");
    }

    /**
     * Gérer la lecture en ligne
     */
    @FXML
    private void handleLire() {
        if (livre == null)
            return;

        // Vérifier que le fichier existe
        if (!PDFManager.pdfExiste(livre.getCheminPdf())) {
            AlertHelper.showError("Fichier introuvable",
                    "Le fichier PDF n'existe pas",
                    "Chemin: " + livre.getCheminPdf() + "\n\n" +
                            "Veuillez contacter l'administrateur.");
            return;
        }

        // Enregistrer la consultation si un utilisateur est connecté
        if (utilisateur != null) {
            Consultation consultation = new Consultation(utilisateur.getId(), livre.getId());
            consultationDAO.create(consultation);
        }

        // Ouvrir le PDF avec le lecteur système
        boolean succes = PDFManager.lireLivre(livre);

        if (succes) {
            AlertHelper.showInfo("Lecture en ligne",
                    "Le livre s'ouvre dans votre lecteur PDF",
                    "Le fichier PDF va s'ouvrir avec votre application par défaut.\n\n" +
                            "💡 Astuce : La lecture en ligne est toujours gratuite !");
        } else {
            AlertHelper.showError("Erreur",
                    "Impossible d'ouvrir le PDF",
                    "Assurez-vous qu'un lecteur PDF est installé sur votre système.\n\n" +
                            "Recommandations :\n" +
                            "• Adobe Acrobat Reader\n" +
                            "• Foxit Reader\n" +
                            "• Navigateur web par défaut");
        }
    }

    /**
     * Gérer le téléchargement
     */

    @FXML
    private void handleTelecharger() {
        System.out.println("\n========================================");
        System.out.println("🔔 BOUTON TÉLÉCHARGER CLIQUÉ !");
        System.out.println("========================================");

        // Test 1 : Le livre existe ?
        if (livre == null) {
            System.out.println("❌ ERREUR : livre est NULL");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur : Le livre est null");
            alert.showAndWait();
            return;
        }

        // --- DÉBUT MODIFICATION PAIEMENT ---
        // Vérifier si le livre est payant
        if (!livre.isGratuit()) {
            // 1. Vérifier si l'utilisateur est connecté
            if (utilisateur == null) {
                AlertHelper.showWarning("Connexion requise", null,
                        "Ce livre est payant. Veuillez vous connecter pour l'acheter.");
                return;
            }

            // 2. Vérifier si l'utilisateur a déjà payé
            boolean aPaye = paiementDAO.hasUserPaidForBook(utilisateur.getId(), livre.getId());

            if (!aPaye) {
                AlertHelper.showInfo("Paiement requis",
                        "Ce livre est payant (" + StringHelper.formatPrice(livre.getPrix()) + ").",
                        "Vous allez être redirigé vers la page de paiement.");

                ouvrirPagePaiement();
                return; // Bloquer le téléchargement
            }
        }
        // --- FIN MODIFICATION PAIEMENT ---

        System.out.println("✓ Livre : " + livre.getTitre());
        System.out.println("✓ Chemin PDF : " + livre.getCheminPdf());

        // Test 2 : Le fichier existe ?
        if (livre.getCheminPdf() == null || livre.getCheminPdf().trim().isEmpty()) {
            System.out.println("❌ ERREUR : Chemin PDF vide");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Le chemin du PDF est vide");
            alert.showAndWait();
            return;
        }

        File fichierSource = new File(livre.getCheminPdf());
        System.out.println("✓ Chemin absolu : " + fichierSource.getAbsolutePath());
        System.out.println("✓ Fichier existe ? " + fichierSource.exists());

        if (!fichierSource.exists()) {
            System.out.println("❌ ERREUR : Le fichier n'existe pas");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fichier introuvable");
            alert.setContentText("Le fichier PDF n'existe pas à cet emplacement:\n\n" +
                    fichierSource.getAbsolutePath() +
                    "\n\nVérifiez le chemin dans la base de données.");
            alert.showAndWait();
            return;
        }

        System.out.println("✓✓✓ Tous les tests passés, début du téléchargement...");

        // Test 3 : Dialogue de sauvegarde
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le livre");
            fileChooser.setInitialFileName(livre.getTitre() + ".pdf");

            // Dossier par défaut
            File dossierDownloads = new File(System.getProperty("user.home") + "/Downloads");
            if (dossierDownloads.exists()) {
                fileChooser.setInitialDirectory(dossierDownloads);
            }

            // Filtre PDF
            FileChooser.ExtensionFilter pdfFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
            fileChooser.getExtensionFilters().add(pdfFilter);

            System.out.println("✓ FileChooser configuré, ouverture du dialogue...");

            // Obtenir le stage
            Stage stage = (Stage) btnTelecharger.getScene().getWindow();
            System.out.println("✓ Stage obtenu : " + (stage != null ? "OK" : "NULL"));

            // Afficher le dialogue
            File fichierDestination = fileChooser.showSaveDialog(stage);

            if (fichierDestination == null) {
                System.out.println("ℹ️ Téléchargement annulé par l'utilisateur");
                return;
            }

            System.out.println("✓ Destination choisie : " + fichierDestination.getAbsolutePath());

            // Copier le fichier
            Files.copy(
                    fichierSource.toPath(),
                    fichierDestination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            System.out.println("✓✓✓ TÉLÉCHARGEMENT RÉUSSI ! ✓✓✓");

            // Message de confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Téléchargement réussi");
            alert.setHeaderText("✓ Le livre a été téléchargé avec succès !");
            alert.setContentText("Livre : " + livre.getTitre() + "\n" +
                    "Emplacement : " + fichierDestination.getAbsolutePath());

            ButtonType btnOuvrir = new ButtonType("Ouvrir le dossier");
            ButtonType btnFermer = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(btnOuvrir, btnFermer);

            alert.showAndWait().ifPresent(response -> {
                if (response == btnOuvrir) {
                    try {
                        Desktop.getDesktop().open(fichierDestination.getParentFile());
                    } catch (Exception e) {
                        System.err.println("Erreur ouverture dossier: " + e.getMessage());
                    }
                }
            });

            // Enregistrer dans la BD (optionnel)
            if (utilisateur != null && telechargementDAO != null) {
                try {
                    Telechargement tel = new Telechargement(utilisateur.getId(), livre.getId(), true);
                    telechargementDAO.create(tel);
                    System.out.println("✓ Téléchargement enregistré en BD");
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur BD (non bloquant): " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("❌❌❌ ERREUR LORS DU TÉLÉCHARGEMENT ❌❌❌");
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du téléchargement:\n\n" + e.getMessage());
            alert.showAndWait();
        }

        System.out.println("========================================\n");
    }

    /**
     * Ouvrir la page de paiement
     */
    private void ouvrirPagePaiement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PaiementView.fxml"));
            Parent root = loader.load();

            PaiementController controller = loader.getController();
            controller.setLivre(livre);
            controller.setUtilisateur(utilisateur);
            controller.setLivreDetailsController(this);

            Stage stage = new Stage();
            stage.setTitle("Paiement - " + livre.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            AlertHelper.showError("Erreur", "Impossible d'ouvrir la page de paiement", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retour à la page précédente
     */
    @FXML
    private void handleRetour() {
        Stage stage = (Stage) labelTitre.getScene().getWindow();
        stage.close();
    }

    /**
     * Rafraîchir l'affichage (appelé après un paiement réussi)
     */
    public void rafraichir() {
        afficherDetailsLivre();
    }
}