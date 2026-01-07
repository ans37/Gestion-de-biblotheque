package service;

import dao.MessageDAO;
import model.Message;

import java.util.List;

public class ChatService {

    private final MessageDAO messageDAO;

    public ChatService() {
        this.messageDAO = new MessageDAO();
    }

    /**
     * Envoyer un nouveau message
     */
    public Message envoyerMessage(Long idUtilisateur, String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide");
        }

        if (idUtilisateur == null) {
            throw new IllegalArgumentException("L'utilisateur doit être connecté pour envoyer un message");
        }

        Message message = new Message(idUtilisateur, contenu.trim());
        return messageDAO.create(message);
    }

    /**
     * Récupérer tous les messages
     */
    public List<Message> recupererTousLesMessages() {
        return messageDAO.findAll();
    }

    /**
     * Récupérer les N derniers messages
     */
    public List<Message> recupererMessagesRecents(int limit) {
        if (limit <= 0) {
            limit = 50; // Par défaut
        }
        return messageDAO.findRecentMessages(limit);
    }

    /**
     * Supprimer un message (admin uniquement)
     */
    public boolean supprimerMessage(Long idMessage) {
        if (idMessage == null) {
            return false;
        }
        return messageDAO.delete(idMessage);
    }

    /**
     * Demander une réponse à DeepSeek AI
     */
    public String askDeepSeek(String question, model.Role role) {
        try {
            // Simuler un délai de traitement
            Thread.sleep(800);

            // Normaliser la question (enlever les accents)
            String q = normalize(question.toLowerCase().trim());
            boolean isAdmin = (role == model.Role.ADMINISTRATEUR);

            // 1. Vérification du contexte bibliothèque
            if (!isBibliothequeContext(q)) {
                return "Je suis l'assistant de votre bibliothèque. Pourriez-vous reformuler votre question pour qu'elle concerne la gestion des livres, un résumé d'œuvre ou l'utilisation de l'application ? Merci !";
            }

            // 2. Fonctionnalités globales (ex: résumés)
            if (q.contains("resumer") || q.contains("resume")) {
                if (q.contains("misérables") || q.contains("miserables") || q.contains("hugo")) {
                    return getMiserablesSummary();
                }
                return "Je peux vous proposer un résumé détaillé des 'Misérables' de Victor Hugo. Voulez-vous le lire ?";
            }

            // 3. Réponses selon le rôle
            if (isAdmin) {
                return handleAdminResponse(q);
            } else {
                return handleUserResponse(q);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Désolé, j'ai rencontré une petite erreur technique. Pouvez-vous répéter ?";
        }
    }

    private boolean isBibliothequeContext(String q) {
        return q.contains("livre") || q.contains("catégorie") || q.contains("recherche") ||
                q.contains("admin") || q.contains("gestion") || q.contains("bibliothèque") ||
                q.contains("bonjour") || q.contains("salut") || q.contains("aide") ||
                q.contains("controller") || q.contains("service") || q.contains("dao") ||
                q.contains("data") || q.contains("base de données") || q.contains("sql") ||
                q.contains("resume") || q.contains("resumer");
    }

    private String handleUserResponse(String q) {
        if (q.contains("livre")) {
            return "Pour consulter les livres, utilisez la liste principale sur l'écran d'accueil. Vous pouvez cliquer sur un livre pour voir ses détails et le télécharger.";
        }
        if (q.contains("catégorie")) {
            return "Vous trouverez les catégories dans le menu latéral gauche. Elles permettent de filtrer les livres par genre (Roman, Science-Fiction, Informatique, etc.).";
        }
        if (q.contains("recherche") || q.contains("🔍")) {
            return "Utilisez la barre de recherche 🔍 située en haut de l'écran pour trouver rapidement un livre par son titre ou son auteur.";
        }
        if (q.contains("admin") || q.contains("droit")) {
            return "Les fonctionnalités d'administration (ajout/modification de livres) sont réservées au personnel autorisé via l'onglet de gestion.";
        }

        return "Bonjour ! Je suis l'assistant de la bibliothèque. Je peux vous guider dans l'application ou vous aider à trouver des ouvrages. Que puis-je faire pour vous ?";
    }

    private String handleAdminResponse(String q) {
        if (q.contains("gestion") || q.contains("ajouter") || q.contains("modifier")) {
            return "En tant qu'administrateur, vous pouvez gérer le catalogue via le menu 'Gestion'. N'oubliez pas de valider les données avant d'enregistrer.";
        }
        if (q.contains("controller") || q.contains("service") || q.contains("dao") || q.contains("architecture")) {
            return "L'architecture est basée sur le pattern MVC : \n1. Controllers : Gèrent l'interface utilisateur.\n2. Services : Centralisent la logique métier.\n3. DAOs : Assurent la persistance des données dans MySQL.";
        }
        if (q.contains("base de donnees") || q.contains("sql") || q.contains("table")) {
            return "Les données sont stockées dans MySQL. Nous utilisons JDBC pour les requêtes. Pour le chat, assurez-vous que la table 'message' est bien synchronisée.";
        }
        return "Mode Administrateur actif. Je suis à votre disposition pour toute assistance technique ou aide à la gestion du système. Quelle est votre requête ?";
    }

    private String getMiserablesSummary() {
        return "Voici un résumé des Misérables de Victor Hugo :\n\n" +
                "Contexte : Publié en 1862, ce roman est une fresque historique et sociale explorant la rédemption et la lutte contre l'injustice.\n\n"
                +
                "Intrigue :\n" +
                "- Fantine : Jean Valjean se réinvente après le bagne et promet de protéger Cosette.\n" +
                "- Cosette : Valjean sauve la fillette des Thénardier et s'enfuit à Paris.\n" +
                "- Marius : L'idylle amoureuse sur fond d'insurrection républicaine de 1832.\n" +
                "- Jean Valjean : Le sacrifice final pour le bonheur de Cosette et la rédemption devant la loi incarnée par Javert.\n\n"
                +
                "Thèmes : Misère, Rédemption, Loi vs Conscience.\n\n" +
                "Cette réponse est générée par l'AI, à titre indicatif seulement.";
    }

    private String normalize(String str) {
        if (str == null)
            return "";
        return java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
