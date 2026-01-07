package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import dao.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private TextField txtRecherche;
    @FXML
    private GridPane conteneurLivres; // Changed from VBox to GridPane
    @FXML
    private Label labelStatut;
    @FXML
    private Label labelNombreLivres;
    @FXML
    private Label labelUtilisateur;
    @FXML
    private MenuItem menuDeconnexion;
    @FXML
    private Button btnAdmin;

    private LivreDAO livreDAO;
    private Utilisateur utilisateurConnecte;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        livreDAO = new LivreDAO();

        // Configurer la grille pour 2 colonnes égales
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        conteneurLivres.getColumnConstraints().addAll(col1, col2);

        chargerTousLesLivres();

        // Ajouter un écouteur pour la recherche en temps réel
        txtRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                chargerTousLesLivres();
            }
        });
    }

    /**
     * Charger tous les livres disponibles
     */
    private void chargerTousLesLivres() {
        try {
            List<Livre> livres = livreDAO.findAll();
            afficherLivres(livres);
            labelStatut.setText("Prêt");
            labelNombreLivres.setText(livres.size() + " livres disponibles");
        } catch (Exception e) {
            afficherErreur("Erreur lors du chargement des livres", e.getMessage());
        }
    }

    /**
     * Afficher une liste de livres
     */
    private void afficherLivres(List<Livre> livres) {
        conteneurLivres.getChildren().clear();

        if (livres.isEmpty()) {
            Label lblVide = new Label("Aucun livre trouvé");
            lblVide.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 50;");
            VBox box = new VBox(lblVide);
            box.setAlignment(Pos.CENTER);
            // Add to first cell if empty
            conteneurLivres.add(box, 0, 0, 2, 1);
            return;
        }

        int column = 0;
        int row = 0;

        for (Livre livre : livres) {
            HBox card = creerCarteLivre(livre);

            // Adjust card width to ensure it fills the grid cell
            card.setMaxWidth(Double.MAX_VALUE);
            GridPane.setHgrow(card, Priority.ALWAYS);

            conteneurLivres.add(card, column, row);

            column++;
            if (column >= 2) { // 2 items per row
                column = 0;
                row++;
            }
        }
    }

    /**
     * Créer une carte visuelle pour un livre
     */
    private HBox creerCarteLivre(Livre livre) {
        HBox card = new HBox(20);
        card.getStyleClass().add("livre-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));

        // Icône du livre
        Label iconeLivre = new Label("📖");
        iconeLivre.setStyle("-fx-font-size: 40px;");

        // Informations du livre
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Titre
        Label titre = new Label(livre.getTitre());
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Auteur
        Label auteur = new Label("Par " + (livre.getAuteur() != null ? livre.getAuteur().getNomComplet() : "Inconnu"));
        auteur.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Catégorie et année
        HBox detailsBox = new HBox(15);
        Label categorie = new Label(livre.getCategorie() != null ? livre.getCategorie().getNom() : "N/A");
        categorie.setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 11px;");

        if (livre.getAnneePublication() != null) {
            Label annee = new Label(livre.getAnneePublication().toString());
            annee.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
            detailsBox.getChildren().addAll(categorie, annee);
        } else {
            detailsBox.getChildren().add(categorie);
        }

        // Badge gratuit/payant
        Label badgeType = new Label(livre.isGratuit() ? "GRATUIT" : "PAYANT - " + livre.getPrix() + " DH");
        badgeType.getStyleClass().add(livre.isGratuit() ? "badge-gratuit" : "badge-payant");

        infoBox.getChildren().addAll(titre, auteur, detailsBox, badgeType);

        // Boutons d'action
        VBox actionsBox = new VBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);

        Button btnVoir = new Button("👁️ Voir détails");
        btnVoir.getStyleClass().add("btn-primary");
        btnVoir.setOnAction(e -> ouvrirDetailsLivre(livre));

        Button btnLire = new Button("📖 Lire");
        btnLire.getStyleClass().add("btn-success");
        btnLire.setOnAction(e -> lireLivre(livre));

        actionsBox.getChildren().addAll(btnVoir, btnLire);

        card.getChildren().addAll(iconeLivre, infoBox, actionsBox);

        // Ajouter un effet au survol
        card.setOnMouseEntered(e -> card.setStyle("-fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-cursor: default;"));

        return card;
    }

    /**
     * Ouvrir la page de détails d'un livre
     */
    private void ouvrirDetailsLivre(Livre livre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LivreDetailsView.fxml"));
            Parent root = loader.load();

            LivreDetailsController controller = loader.getController();
            controller.setLivre(livre);
            controller.setUtilisateur(utilisateurConnecte);

            Stage stage = new Stage();
            stage.setTitle("Détails du livre - " + livre.getTitre());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir les détails du livre");
            e.printStackTrace();
        }
    }

    /**
     * Lire un livre en ligne
     */
    private void lireLivre(Livre livre) {
        // Enregistrer la consultation
        if (utilisateurConnecte != null) {
            ConsultationDAO consultationDAO = new ConsultationDAO();
            Consultation consultation = new Consultation(utilisateurConnecte.getId(), livre.getId());
            consultationDAO.create(consultation);
        }

        // Ouvrir le lecteur PDF (à implémenter)
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lecteur en ligne");
        alert.setHeaderText("Lecture de : " + livre.getTitre());
        alert.setContentText("Le lecteur PDF s'ouvrira ici.\n\nChemin du fichier : " + livre.getCheminPdf());
        alert.showAndWait();
    }

    @FXML
    private void handleRechercheRapide() {
        String critere = txtRecherche.getText().trim();

        if (critere.isEmpty()) {
            chargerTousLesLivres();
            return;
        }

        try {
            List<Livre> livres = livreDAO.search(critere);
            afficherLivres(livres);
            labelStatut.setText("Recherche : \"" + critere + "\"");
            labelNombreLivres.setText(livres.size() + " résultat(s) trouvé(s)");
        } catch (Exception e) {
            afficherErreur("Erreur de recherche", e.getMessage());
        }
    }

    @FXML
    private void handleAccueil() {
        chargerTousLesLivres();
    }

    @FXML
    private void handleRecherche() {
        txtRecherche.requestFocus();
    }

    @FXML
    private void handleTousLivres() {
        chargerTousLesLivres();
    }

    @FXML
    private void handleCategorieRoman() {
        filtrerParCategorie("Roman");
    }

    @FXML
    private void handleCategorieSciFi() {
        filtrerParCategorie("Science-Fiction");
    }

    @FXML
    private void handleCategorieInfo() {
        filtrerParCategorie("Informatique");
    }

    @FXML
    private void handleCategorieHistoire() {
        filtrerParCategorie("Histoire");
    }

    @FXML
    private void handleCategorieSciences() {
        filtrerParCategorie("Sciences");
    }

    @FXML
    private void handleCategorieDroit() {
        filtrerParCategorie("Droit");
    }

    private void filtrerParCategorie(String nomCategorie) {
        try {
            // 1. D'abord trouver l'ID de la catégorie par son nom
            CategorieDAO categorieDAO = new CategorieDAO();
            Categorie categorie = categorieDAO.findByNom(nomCategorie);

            if (categorie != null) {
                // 2. Utiliser l'ID pour trouver les livres
                List<Livre> livres = livreDAO.findByCategorie(categorie.getId());
                afficherLivres(livres);
                labelStatut.setText("Catégorie : " + nomCategorie);
                labelNombreLivres.setText(livres.size() + " livre(s) dans cette catégorie");
            } else {
                afficherErreur("Erreur", "Catégorie non trouvée : " + nomCategorie);
            }
        } catch (Exception e) {
            afficherErreur("Erreur", "Impossible de filtrer par catégorie");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Connexion Administrateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);

            LoginController controller = loader.getController();
            controller.setMainController(this);

            stage.showAndWait();

        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir la page de connexion");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        utilisateurConnecte = null;
        btnAdmin.setVisible(false);
        menuDeconnexion.setDisable(true);
        labelUtilisateur.setVisible(false);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez été déconnecté avec succès.");
        alert.showAndWait();
    }

    @FXML
    private void handleGestionAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AdminView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion de la Bibliothèque");
            stage.setScene(new Scene(root, 1200, 700));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le panneau d'administration");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAPropos() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Bibliothèque Numérique v1.0");
        alert.setContentText("Application de gestion de bibliothèque\n" +
                "Développée avec JavaFX et MySQL\n\n" +
                "© 2024 - Tous droits réservés");
        alert.showAndWait();
    }

    @FXML
    private void handleQuitter() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText("Voulez-vous vraiment quitter l'application ?");
        alert.setContentText("Toutes les fenêtres seront fermées.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    @FXML
    private void openChatWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/chat.fxml"));
            Parent chatView = loader.load();

            // Récupérer le contrôleur et définir l'utilisateur connecté
            ChatController chatController = loader.getController();

            // Si un utilisateur est connecté, le passer au chat
            if (utilisateurConnecte != null) {
                chatController.setUtilisateurConnecte(utilisateurConnecte);
            } else {
                // Créer un utilisateur invité par défaut
                Utilisateur invite = new Utilisateur();
                invite.setId(2L); // ID de l'utilisateur "user" par défaut
                invite.setNomUtilisateur("Invité");
                invite.setNom("Invité");
                invite.setPrenom("Utilisateur");
                chatController.setUtilisateurConnecte(invite);
            }

            // Ouvrir dans une nouvelle fenêtre
            Stage chatStage = new Stage();
            chatStage.setTitle("💬 Chat - Bibliothèque");
            chatStage.setScene(new Scene(chatView, 800, 600));
            chatStage.show();

            labelStatut.setText("Chat ouvert");

        } catch (IOException e) {
            afficherErreur("Erreur", "Impossible d'ouvrir le chat: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            afficherErreur("Erreur", "Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Définir l'utilisateur connecté
     */
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;

        if (utilisateur != null && utilisateur.getRole() == Role.ADMINISTRATEUR) {
            btnAdmin.setVisible(true);
            menuDeconnexion.setDisable(false);
            labelUtilisateur.setText("👤 Connecté en tant que : " + utilisateur.getNomUtilisateur() + " (Admin)");
            labelUtilisateur.setVisible(true);
        }
    }

    /**
     * Afficher une boîte de dialogue d'erreur
     */
    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}