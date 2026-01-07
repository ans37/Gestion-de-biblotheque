package util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHelper {

    /**
     * Afficher une alerte d'information
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'erreur
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte d'avertissement
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Afficher une alerte de confirmation
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        return alert.showAndWait()
                .filter(response -> response == javafx.scene.control.ButtonType.OK)
                .isPresent();
    }

    /**
     * Afficher une erreur de validation
     */
    public static void showValidationError(String fieldName, String errorMessage) {
        showError("Erreur de validation",
                "Le champ '" + fieldName + "' est invalide",
                errorMessage);
    }

    /**
     * Afficher un message de succès
     */
    public static void showSuccess(String operation) {
        showInfo("Succès",
                operation + " effectuée avec succès",
                "L'opération s'est terminée sans erreur.");
    }
}