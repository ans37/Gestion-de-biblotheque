package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    // Paramètres de connexion - À MODIFIER selon votre configuration
    private static final String URL = "jdbc:mysql://localhost:3306/bibliotheque_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mettez votre mot de passe MySQL ici
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Constructeur privé pour le pattern Singleton
     */
    private DatabaseConnection() {
        try {
            // Charger le driver JDBC
            Class.forName(DRIVER);

            // Établir la connexion
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✓ Connexion à la base de données réussie !");

        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver JDBC non trouvé : " + e.getMessage());
            System.err.println("Assurez-vous que mysql-connector-java-x.x.x.jar est dans le classpath");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("✗ Erreur de connexion à la base de données : " + e.getMessage());
            System.err.println("Vérifiez que MySQL est démarré et que les paramètres sont corrects");
            e.printStackTrace();
        }
    }

    /**
     * Obtenir l'instance unique de DatabaseConnection (Singleton)
     * @return L'instance unique de DatabaseConnection
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Obtenir la connexion à la base de données
     * @return La connexion active
     * @throws SQLException Si la connexion est fermée ou invalide
     */
    public Connection getConnection() throws SQLException {
        // Vérifier si la connexion est toujours valide
        if (connection == null || connection.isClosed()) {
            try {
                this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Reconnexion à la base de données réussie !");
            } catch (SQLException e) {
                System.err.println("✗ Erreur lors de la reconnexion : " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    /**
     * Fermer la connexion à la base de données
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("✗ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }

    /**
     * Tester la connexion à la base de données
     * @return true si la connexion fonctionne, false sinon
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Afficher les informations de connexion (sans le mot de passe)
     */
    public void displayConnectionInfo() {
        System.out.println("===========================================");
        System.out.println("INFORMATIONS DE CONNEXION");
        System.out.println("===========================================");
        System.out.println("URL      : " + URL);
        System.out.println("User     : " + USER);
        System.out.println("Driver   : " + DRIVER);
        System.out.println("Status   : " + (testConnection() ? "✓ Connecté" : "✗ Déconnecté"));
        System.out.println("===========================================");
    }
}