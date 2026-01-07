package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import util.AlertHelper;
import util.FileHelper;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Contrôleur pour le lecteur PDF intégré
 * Utilise WebView pour afficher les PDFs
 */
public class PDFReaderController implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private WebView webView;
    @FXML
    private Label labelTitre;
    @FXML
    private Label labelPage;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private TextField txtPageNumber;
    @FXML
    private Slider sliderZoom;
    @FXML
    private ProgressIndicator progressIndicator;

    private WebEngine webEngine;
    private String cheminPDF;
    private int currentPage = 1;
    private int totalPages = 0;
    private double zoomLevel = 1.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = webView.getEngine();

        // Désactiver le menu contextuel
        webView.setContextMenuEnabled(false);

        // Configurer le slider de zoom
        sliderZoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            zoomLevel = newVal.doubleValue() / 100.0;
            updateZoom();
        });

        // Configurer le champ de numéro de page
        txtPageNumber.setOnAction(e -> goToPage());

        // Listener pour détecter le chargement
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                progressIndicator.setVisible(false);
            } else if (newState == javafx.concurrent.Worker.State.FAILED) {
                progressIndicator.setVisible(false);
                AlertHelper.showError("Erreur",
                        "Impossible de charger le PDF",
                        "Le fichier PDF n'a pas pu être chargé");
            }
        });
    }

    /**
     * Charger un fichier PDF
     */
    public void chargerPDF(String cheminPDF, String titre) {
        this.cheminPDF = cheminPDF;
        labelTitre.setText(titre);

        // Vérifier que le fichier existe
        if (!FileHelper.fileExists(cheminPDF)) {
            AlertHelper.showError("Fichier introuvable",
                    "Le fichier PDF n'existe pas",
                    "Chemin: " + cheminPDF);
            return;
        }

        // Vérifier que c'est un PDF
        if (!FileHelper.isPDF(cheminPDF)) {
            AlertHelper.showError("Format invalide",
                    "Ce n'est pas un fichier PDF",
                    "Extension attendue: .pdf");
            return;
        }

        progressIndicator.setVisible(true);

        // Charger le PDF
        loadPDF();
    }

    /**
     * Charger le PDF dans le WebView
     */
    private void loadPDF() {
        try {
            File file = new File(cheminPDF);
            String url = file.toURI().toURL().toString();

            // Créer une page HTML pour afficher le PDF
            String html = createHTMLForPDF(url);

            webEngine.loadContent(html);

            // Simuler le nombre de pages (dans une vraie app, utiliser Apache PDFBox)
            totalPages = estimatePDFPages();
            updatePageInfo();

        } catch (Exception e) {
            progressIndicator.setVisible(false);
            AlertHelper.showError("Erreur",
                    "Impossible de charger le PDF",
                    e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Créer le HTML pour afficher le PDF
     */
    private String createHTMLForPDF(String pdfUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <style>" +
                "        body {" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            overflow: hidden;" +
                "            background-color: #525659;" +
                "        }" +
                "        #pdfContainer {" +
                "            width: 100%;" +
                "            height: 100vh;" +
                "        }" +
                "        embed, object, iframe {" +
                "            width: 100%;" +
                "            height: 100%;" +
                "            border: none;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div id='pdfContainer'>" +
                "        <embed src='" + pdfUrl + "' type='application/pdf'>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Estimer le nombre de pages (simulé)
     * Dans une vraie app, utiliser Apache PDFBox pour obtenir le nombre réel
     */
    private int estimatePDFPages() {
        // Simulation basée sur la taille du fichier
        double sizeInMB = FileHelper.getFileSizeMB(cheminPDF);
        int estimatedPages = (int) (sizeInMB * 20); // ~20 pages par Mo (estimation)
        return Math.max(estimatedPages, 1);
    }

    /**
     * Mettre à jour l'affichage des informations de page
     */
    private void updatePageInfo() {
        labelPage.setText(String.format("Page %d / %d", currentPage, totalPages));
        txtPageNumber.setText(String.valueOf(currentPage));

        btnPrevious.setDisable(currentPage <= 1);
        btnNext.setDisable(currentPage >= totalPages);
    }

    /**
     * Mettre à jour le zoom
     */
    private void updateZoom() {
        // Dans une vraie app avec PDF.js ou PDFBox, implémenter le vrai zoom
        webEngine.executeScript("document.body.style.zoom = " + zoomLevel);
    }

    /**
     * Page précédente
     */
    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePageInfo();
            // Dans une vraie app, naviguer vers la page précédente
        }
    }

    /**
     * Page suivante
     */
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePageInfo();
            // Dans une vraie app, naviguer vers la page suivante
        }
    }

    /**
     * Aller à une page spécifique
     */
    private void goToPage() {
        try {
            int pageNumber = Integer.parseInt(txtPageNumber.getText().trim());

            if (pageNumber < 1 || pageNumber > totalPages) {
                AlertHelper.showWarning("Page invalide",
                        "Numéro de page invalide",
                        "Veuillez entrer un numéro entre 1 et " + totalPages);
                txtPageNumber.setText(String.valueOf(currentPage));
                return;
            }

            currentPage = pageNumber;
            updatePageInfo();
            // Dans une vraie app, naviguer vers la page

        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Entrée invalide",
                    "Veuillez entrer un numéro valide",
                    "");
            txtPageNumber.setText(String.valueOf(currentPage));
        }
    }

    /**
     * Zoom avant
     */
    @FXML
    private void handleZoomIn() {
        double newValue = sliderZoom.getValue() + 10;
        if (newValue <= sliderZoom.getMax()) {
            sliderZoom.setValue(newValue);
        }
    }

    /**
     * Zoom arrière
     */
    @FXML
    private void handleZoomOut() {
        double newValue = sliderZoom.getValue() - 10;
        if (newValue >= sliderZoom.getMin()) {
            sliderZoom.setValue(newValue);
        }
    }

    /**
     * Réinitialiser le zoom
     */
    @FXML
    private void handleResetZoom() {
        sliderZoom.setValue(100);
    }

    /**
     * Mode plein écran
     */
    @FXML
    private void handleFullScreen() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    /**
     * Imprimer le PDF
     */
    @FXML
    private void handlePrint() {
        // Dans une vraie app, implémenter l'impression
        AlertHelper.showInfo("Impression",
                "Fonctionnalité d'impression",
                "L'impression sera disponible dans une prochaine version.\n\n" +
                        "Pour l'instant, vous pouvez ouvrir le fichier PDF directement:\n" +
                        cheminPDF);
    }

    /**
     * Télécharger/Enregistrer le PDF
     */
    @FXML
    private void handleDownload() {
        AlertHelper.showInfo("Téléchargement",
                "Fichier PDF",
                "Le fichier est déjà disponible à l'emplacement:\n\n" +
                        cheminPDF);
    }

    /**
     * Fermer le lecteur
     */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) webView.getScene().getWindow();
        stage.close();
    }
}