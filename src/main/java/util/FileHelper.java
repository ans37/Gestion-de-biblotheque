package util;

public class FileHelper {

    /**
     * Vérifier si un fichier existe
     */
    public static boolean fileExists(String path) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        java.io.File file = new java.io.File(path);
        return file.exists() && file.isFile();
    }

    /**
     * Vérifier si un fichier est un PDF
     */
    public static boolean isPDF(String path) {
        if (!fileExists(path)) {
            return false;
        }
        return path.toLowerCase().endsWith(".pdf");
    }

    /**
     * Obtenir la taille d'un fichier en Mo
     */
    public static double getFileSizeMB(String path) {
        if (!fileExists(path)) {
            return 0;
        }
        java.io.File file = new java.io.File(path);
        return file.length() / (1024.0 * 1024.0);
    }

    /**
     * Obtenir le nom du fichier sans l'extension
     */
    public static String getFileNameWithoutExtension(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }
        java.io.File file = new java.io.File(path);
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        return (lastDot == -1) ? name : name.substring(0, lastDot);
    }

    /**
     * Obtenir l'extension du fichier
     */
    public static String getFileExtension(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }
        int lastDot = path.lastIndexOf('.');
        return (lastDot == -1) ? "" : path.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Créer un répertoire s'il n'existe pas
     */
    public static boolean createDirectoryIfNotExists(String path) {
        java.io.File directory = new java.io.File(path);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
}