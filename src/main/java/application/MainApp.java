package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import util.DatabaseConnection;

/**
 * Classe principale de l'application Bibliothèque Numérique
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Tester la connexion à la base de données
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            if (!dbConnection.testConnection()) {
                afficherErreurConnexion();
                // On continue quand même pour afficher l'interface (mode dégradé)
            } else {
                // Initialiser les données par défaut si nécessaire
                util.DatabaseInit.initializeDefaultData();
            }

            // Charger l'interface principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            // Configuration de la scène
            Scene scene = new Scene(root, 1280, 800);

            // Configuration du stage
            primaryStage.setTitle("📚 Bibliothèque Numérique - Gestion de Livres");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);

            // Ajouter une icône (optionnel)
            // primaryStage.getIcons().add(new
            // Image(getClass().getResourceAsStream("/images/icon.png")));

            // Afficher la fenêtre
            primaryStage.show();

            System.out.println("✓ Application démarrée avec succès !");

        } catch (Exception e) {
            System.err.println("✗ Erreur lors du démarrage de l'application");
            e.printStackTrace();
        }
    }

    /**
     * Afficher un message d'erreur si la connexion à la BD échoue
     */
    private void afficherErreurConnexion() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connexion");
        alert.setHeaderText("Impossible de se connecter à la base de données");
        alert.setContentText(
                "Veuillez vérifier :\n" +
                        "1. MySQL est bien démarré\n" +
                        "2. La base de données 'bibliotheque_db' existe\n" +
                        "3. Les identifiants dans DatabaseConnection.java sont corrects\n\n" +
                        "L'application va se fermer.");
        alert.showAndWait();
        // System.exit(1); // Ne pas fermer l'application pour laisser voir l'interface
    }

    @Override
    public void stop() {
        // Fermer la connexion à la base de données lors de la fermeture
        DatabaseConnection.getInstance().closeConnection();
        System.out.println("✓ Application fermée proprement");
    }

    /**
     * Point d'entrée de l'application
     */
    public static void main(String[] args) {
        launch(args);
    }
}