-- Script pour ajouter la table Message à la base de données
USE bibliotheque_db;

-- Table Message pour le chat
CREATE TABLE IF NOT EXISTS Message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    contenu TEXT NOT NULL,
    date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Index pour améliorer les performances
CREATE INDEX idx_message_date ON Message(date_envoi);
CREATE INDEX idx_message_utilisateur ON Message(id_utilisateur);

-- Insérer quelques messages de test
INSERT INTO Message (id_utilisateur, contenu, date_envoi) VALUES 
(1, 'Bienvenue sur le chat de la bibliothèque !', NOW()),
(2, 'Merci ! Content d''être ici.', NOW());

SELECT 'Table Message créée avec succès !' as status;
