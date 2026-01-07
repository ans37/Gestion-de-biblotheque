package util;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;

public class Validation {

    // Patterns de validation
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern ISBN_PATTERN =
            Pattern.compile("^(97[89])?[-\\s]?\\d{1,5}[-\\s]?\\d{1,7}[-\\s]?\\d{1,7}[-\\s]?\\d{1}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+212|0)[5-7]\\d{8}$");

    /**
     * Valider une adresse email
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Valider un numéro ISBN
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = ISBN_PATTERN.matcher(isbn.trim());
        return matcher.matches();
    }

    /**
     * Valider un numéro de téléphone marocain
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = PHONE_PATTERN.matcher(phone.trim());
        return matcher.matches();
    }

    /**
     * Valider qu'une chaîne n'est pas vide
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Valider qu'une chaîne a une longueur minimum
     */
    public static boolean hasMinLength(String str, int minLength) {
        return str != null && str.trim().length() >= minLength;
    }

    /**
     * Valider qu'une chaîne a une longueur maximum
     */
    public static boolean hasMaxLength(String str, int maxLength) {
        return str != null && str.trim().length() <= maxLength;
    }

    /**
     * Valider un nombre positif
     */
    public static boolean isPositiveNumber(String str) {
        try {
            double value = Double.parseDouble(str.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valider un nombre entier
     */


    /**
     * Valider un nombre décimal
     */
    public static boolean isDecimal(String str) {
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valider une année (entre 1000 et année actuelle + 10)
     */
    public static boolean isValidYear(String yearStr) {
        try {
            int year = Integer.parseInt(yearStr.trim());
            int currentYear = java.time.Year.now().getValue();
            return year >= 1000 && year <= currentYear + 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Valider un prix (positif et max 2 décimales)
     */
    public static boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr.trim());
            if (price < 0) return false;

            // Vérifier max 2 décimales
            String[] parts = priceStr.trim().split("\\.");
            if (parts.length > 1 && parts[1].length() > 2) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Nettoyer une chaîne de caractères
     */
    public static String sanitize(String str) {
        if (str == null) return "";
        return str.trim().replaceAll("\\s+", " ");
    }

    /**
     * Formater un ISBN (ajouter les tirets)
     */
    public static String formatISBN(String isbn) {
        if (isbn == null) return "";

        // Enlever tous les caractères non numériques
        String cleaned = isbn.replaceAll("[^0-9]", "");

        // Formater selon la longueur
        if (cleaned.length() == 10) {
            // ISBN-10: X-XXX-XXXXX-X
            return cleaned.substring(0, 1) + "-" +
                    cleaned.substring(1, 4) + "-" +
                    cleaned.substring(4, 9) + "-" +
                    cleaned.substring(9);
        } else if (cleaned.length() == 13) {
            // ISBN-13: XXX-X-XXX-XXXXX-X
            return cleaned.substring(0, 3) + "-" +
                    cleaned.substring(3, 4) + "-" +
                    cleaned.substring(4, 7) + "-" +
                    cleaned.substring(7, 12) + "-" +
                    cleaned.substring(12);
        }

        return isbn;
    }

    /**
     * Vérifie si une chaîne est numérique (Java 8+ style)
     * @param str la chaîne à vérifier
     * @return true si la chaîne est numérique
     */
    public static boolean isNumeric(String str) {
        return Optional.ofNullable(str)
                .map(s -> s.trim().replace(',', '.'))
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        Double.parseDouble(s);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .orElse(false);
    }

    /**
     * Vérifie si une chaîne est un entier
     * @param str la chaîne à vérifier
     * @return true si la chaîne est un entier
     */
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }

        String trimmed = str.trim();

        // Si la chaîne est vide, on considère que c'est valide (optionnel)
        if (trimmed.isEmpty()) {
            return true;
        }

        try {
            Integer.parseInt(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


