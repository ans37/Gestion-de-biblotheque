package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Message;
import model.Utilisateur;
import service.ChatService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ChatController {

    @FXML
    private VBox chatMessagesContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private Button sendButton;

    @FXML
    private ScrollPane chatScrollPane;

    private ChatService chatService;
    private Utilisateur utilisateurConnecte;
    private Timer refreshTimer;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        chatService = new ChatService();

        // Charger les messages existants
        chargerMessages();

        // Auto-scroll vers le bas
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));

        // Activer l'envoi avec la touche Entrée
        messageInput.setOnAction(event -> handleEnvoyerMessage());

        // Démarrer le rafraîchissement automatique toutes les 3 secondes
        demarrerRafraichissementAuto();
    }

    /**
     * Définir l'utilisateur connecté
     */
    public void setUtilisateurConnecte(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    /**
     * Charger tous les messages
     */
    private void chargerMessages() {
        chatMessagesContainer.getChildren().clear();

        List<Message> messages = chatService.recupererMessagesRecents(100);

        for (Message message : messages) {
            ajouterMessageALaffichage(message);
        }

        // Scroll vers le bas après chargement
        Platform.runLater(() -> chatScrollPane.setVvalue(1.0));
    }

    /**
     * Ajouter un message à l'affichage
     */
    private void ajouterMessageALaffichage(Message message) {
        HBox messageBox = new HBox(10);
        messageBox.setPadding(new Insets(5, 10, 5, 10));

        boolean estMonMessage = utilisateurConnecte != null &&
                message.getIdUtilisateur().equals(utilisateurConnecte.getId());

        if (estMonMessage) {
            messageBox.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageBox.setAlignment(Pos.CENTER_LEFT);
        }

        VBox messageContent = new VBox(3);

        // Nom de l'utilisateur et heure
        String heure = (message.getDateEnvoi() != null) ? message.getDateEnvoi().format(TIME_FORMATTER) : "--:--";
        Label header = new Label(message.getNomUtilisateur() + " • " + heure);
        header.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");

        // Contenu du message
        Text messageText = new Text(message.getContenu());
        messageText.setWrappingWidth(400);
        messageText.setStyle("-fx-font-size: 13px;");

        Label messageLabel = new Label();
        messageLabel.setGraphic(messageText);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);
        messageLabel.setPadding(new Insets(8, 12, 8, 12));

        if (estMonMessage) {
            messageLabel.setStyle(
                    "-fx-background-color: #0084ff; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 18; " +
                            "-fx-font-size: 13px;");
            messageText.setStyle("-fx-fill: white; -fx-font-size: 13px;");
            messageContent.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageLabel.setStyle(
                    "-fx-background-color: #e4e6eb; " +
                            "-fx-text-fill: black; " +
                            "-fx-background-radius: 18; " +
                            "-fx-font-size: 13px;");
            messageText.setStyle("-fx-fill: black; -fx-font-size: 13px;");
            messageContent.setAlignment(Pos.CENTER_LEFT);
        }

        messageContent.getChildren().addAll(header, messageLabel);
        messageBox.getChildren().add(messageContent);

        chatMessagesContainer.getChildren().add(messageBox);
    }

    /**
     * Gérer l'envoi d'un message
     */
    @FXML
    private void handleEnvoyerMessage() {
        String contenu = messageInput.getText();

        if (contenu == null || contenu.trim().isEmpty()) {
            return;
        }

        if (utilisateurConnecte == null) {
            afficherErreur("Vous devez être connecté pour envoyer un message");
            return;
        }

        try {
            // Envoyer le message de l'utilisateur
            Message message = chatService.envoyerMessage(utilisateurConnecte.getId(), contenu);

            if (message != null) {
                message.setNomUtilisateur(utilisateurConnecte.getNomComplet());
                ajouterMessageALaffichage(message);
                messageInput.clear();

                // Scroll vers le bas
                Platform.runLater(() -> chatScrollPane.setVvalue(1.0));

                // Demander une réponse à l'IA pour chaque message
                String question = contenu.trim();

                // Créer une tâche asynchrone pour l'appel à DeepSeek
                javafx.concurrent.Task<String> task = new javafx.concurrent.Task<>() {
                    @Override
                    protected String call() {
                        return chatService.askDeepSeek(question, utilisateurConnecte.getRole());
                    }
                };

                task.setOnSucceeded(e -> {
                    String aiResponse = task.getValue();

                    // Créer un message IA (utiliser un ID système, par exemple 1)
                    Message aiMessage = chatService.envoyerMessage(1L, aiResponse);
                    if (aiMessage != null) {
                        aiMessage.setNomUtilisateur("DeepSeek AI");
                        Platform.runLater(() -> {
                            ajouterMessageALaffichage(aiMessage);
                            chatScrollPane.setVvalue(1.0);
                        });
                    } else {
                        System.err.println(
                                "Erreur : Impossible d'enregistrer la réponse de l'IA dans la base de données.");
                    }
                });

                task.setOnFailed(e -> {
                    Platform.runLater(() -> afficherErreur(
                            "Erreur lors de la communication avec l'IA : " + task.getException().getMessage()));
                });

                new Thread(task).start();
            } else {
                afficherErreur("Impossible d'envoyer le message. Vérifiez la connexion à la base de données.");
            }

        } catch (Exception e) {
            afficherErreur("Erreur lors de l'envoi du message : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Démarrer le rafraîchissement automatique
     */
    private void demarrerRafraichissementAuto() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    int ancienNombreMessages = chatMessagesContainer.getChildren().size();
                    chargerMessages();

                    // Si de nouveaux messages, scroll vers le bas
                    if (chatMessagesContainer.getChildren().size() > ancienNombreMessages) {
                        chatScrollPane.setVvalue(1.0);
                    }
                });
            }
        }, 3000, 3000); // Rafraîchir toutes les 3 secondes
    }

    /**
     * Arrêter le rafraîchissement automatique
     */
    public void arreterRafraichissement() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }

    /**
     * Afficher une erreur
     */
    private void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
