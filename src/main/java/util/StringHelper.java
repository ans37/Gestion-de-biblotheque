package util;

public class StringHelper {

    /**
     * Tronquer une chaîne à une longueur donnée
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Capitaliser la première lettre
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    /**
     * Capitaliser chaque mot
     */
    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) return "";

        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(capitalize(word)).append(" ");
            }
        }

        return result.toString().trim();
    }

    /**
     * Vérifier si une chaîne contient uniquement des lettres
     */
    public static boolean isAlphabetic(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("[a-zA-ZÀ-ÿ\\s]+");
    }

    /**
     * Vérifier si une chaîne contient uniquement des chiffres
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        return str.matches("\\d+");
    }

    /**
     * Formater un prix en DH
     */
    public static String formatPrice(double price) {
        return String.format("%.2f DH", price);
    }

    /**
     * Formater une date pour l'affichage
     */
    public static String formatDate(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Formater une date courte
     */
    public static String formatDateShort(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "";
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
}