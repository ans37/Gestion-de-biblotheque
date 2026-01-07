package util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Livre;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Gestionnaire pour les opérations liées aux fichiers PDF
 */
public class PDFManager {

    /**
     * Ouvrir un fichier PDF avec l'application par défaut du système
     * 
     * @param cheminPdf Le chemin du fichier PDF à ouvrir
     * @return true si l'ouverture a réussi, false sinon
     */
    public static boolean ouvrirPDF(String cheminPdf) {
        if (cheminPdf == null || cheminPdf.isEmpty()) {
            System.err.println("Chemin du fichier PDF invalide");
            return false;
        }

        File fichierPdf = new File(cheminPdf);

        // Vérifier si le fichier existe
        if (!fichierPdf.exists()) {
            System.err.println("Le fichier PDF n'existe pas : " + cheminPdf);
            return false;
        }

        // Vérifier si Desktop est supporté sur ce système
        if (!Desktop.isDesktopSupported()) {
            System.err.println("Desktop n'est pas supporté sur ce système");
            return false;
        }

        Desktop desktop = Desktop.getDesktop();

        // Vérifier si l'action OPEN est supportée
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            System.err.println("L'action OPEN n'est pas supportée sur ce système");
            return false;
        }

        try {
            // Ouvrir le fichier PDF avec l'application par défaut
            desktop.open(fichierPdf);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du fichier PDF : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ouvrir un fichier PDF à partir d'un objet Livre
     * 
     * @param livre L'objet Livre contenant le chemin du PDF
     * @return true si l'ouverture a réussi, false sinon
     */
    public static boolean lireLivre(Livre livre) {
        if (livre == null) {
            System.err.println("L'objet Livre est null");
            return false;
        }

        String cheminPdf = livre.getCheminPdf();
        if (cheminPdf == null || cheminPdf.isEmpty()) {
            System.err.println("Le chemin du PDF n'est pas défini pour le livre : " + livre.getTitre());
            return false;
        }

        return ouvrirPDF(cheminPdf);
    }

    /**
     * Vérifier si un fichier PDF existe
     * 
     * @param cheminPdf Le chemin du fichier PDF
     * @return true si le fichier existe, false sinon
     */
    public static boolean pdfExiste(String cheminPdf) {
        if (cheminPdf == null || cheminPdf.isEmpty()) {
            return false;
        }
        File fichier = new File(cheminPdf);
        return fichier.exists() && fichier.isFile();
    }

    /**
     * Obtenir la taille d'un fichier PDF en octets
     * 
     * @param cheminPdf Le chemin du fichier PDF
     * @return La taille du fichier en octets, ou -1 si le fichier n'existe pas
     */
    public static long getTaillePDF(String cheminPdf) {
        if (!pdfExiste(cheminPdf)) {
            return -1;
        }
        File fichier = new File(cheminPdf);
        return fichier.length();
    }

    /**
     * Obtenir la taille d'un fichier PDF en Mo
     * 
     * @param cheminPdf Le chemin du fichier PDF
     * @return La taille du fichier en Mo (chaîne formatée)
     */
    public static String getTaillePDFMo(String cheminPdf) {
        long taille = getTaillePDF(cheminPdf);
        if (taille == -1) {
            return "N/A";
        }

        double tailleMo = taille / (1024.0 * 1024.0);
        return String.format("%.1f Mo", tailleMo);
    }

    /**
     * Formater la taille d'un fichier en format lisible (KB, MB, GB)
     * 
     * @param tailleEnOctets La taille en octets
     * @return La taille formatée
     */
    public static String formaterTaille(long tailleEnOctets) {
        if (tailleEnOctets < 0) {
            return "N/A";
        }

        if (tailleEnOctets < 1024) {
            return tailleEnOctets + " B";
        } else if (tailleEnOctets < 1024 * 1024) {
            return String.format("%.2f KB", tailleEnOctets / 1024.0);
        } else if (tailleEnOctets < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", tailleEnOctets / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", tailleEnOctets / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Télécharger un livre avec une boîte de dialogue pour choisir l'emplacement
     * 
     * @param livre L'objet Livre à télécharger
     * @param stage Le stage parent pour la boîte de dialogue
     * @return Le chemin absolu du fichier téléchargé, ou null si échec/annulation
     */
    public static String telechargerLivreAvecDialogue(Livre livre, Stage stage) {
        if (livre == null) {
            System.err.println("L'objet Livre est null");
            return null;
        }

        String cheminSource = livre.getCheminPdf();
        if (cheminSource == null || cheminSource.isEmpty()) {
            System.err.println("Le chemin du PDF n'est pas défini pour le livre : " + livre.getTitre());
            return null;
        }

        File fichierSource = new File(cheminSource);
        if (!fichierSource.exists()) {
            System.err.println("Le fichier PDF n'existe pas : " + cheminSource);
            return null;
        }

        // Créer un FileChooser pour sélectionner l'emplacement de sauvegarde
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le livre");
        fileChooser.setInitialFileName(livre.getTitre() + ".pdf");

        // Ajouter un filtre pour les fichiers PDF
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        // Afficher la boîte de dialogue
        File fichierDestination = fileChooser.showSaveDialog(stage);

        if (fichierDestination == null) {
            // L'utilisateur a annulé
            return null;
        }

        try {
            // Copier le fichier vers l'emplacement choisi
            Files.copy(fichierSource.toPath(), fichierDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Livre téléchargé avec succès : " + fichierDestination.getAbsolutePath());
            return fichierDestination.getAbsolutePath();
        } catch (IOException e) {
            System.err.println("Erreur lors de la copie du fichier : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ouvrir le dossier contenant le fichier téléchargé
     * 
     * @param cheminFichier Le chemin complet du fichier
     */
    public static void ouvrirDossierTelechargements(String cheminFichier) {
        if (cheminFichier == null || cheminFichier.isEmpty()) {
            System.err.println("Chemin de fichier invalide");
            return;
        }

        try {
            File fichier = new File(cheminFichier);
            File dossier = fichier.getParentFile();

            if (dossier != null && dossier.exists()) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(dossier);
                } else {
                    System.err.println("L'ouverture de dossier n'est pas supportée sur ce système");
                }
            } else {
                System.err
                        .println("Le dossier n'existe pas : " + (dossier != null ? dossier.getAbsolutePath() : "null"));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du dossier : " + e.getMessage());
            e.printStackTrace();
        }
    }
}