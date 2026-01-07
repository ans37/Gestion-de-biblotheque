-- Création de la base de données
CREATE DATABASE IF NOT EXISTS bibliotheque_db;
USE bibliotheque_db;

-- Table Utilisateur
CREATE TABLE IF NOT EXISTS Utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom_utilisateur VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    role VARCHAR(20) NOT NULL,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table Auteur
CREATE TABLE IF NOT EXISTS Auteur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100),
    biographie TEXT,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Table Categorie
CREATE TABLE IF NOT EXISTS Categorie (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- Table Livre
CREATE TABLE IF NOT EXISTS Livre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(20),
    titre VARCHAR(255) NOT NULL,
    annee_publication INT,
    description TEXT,
    langue VARCHAR(50),
    nombre_pages INT,
    chemin_pdf VARCHAR(255),
    disponible BOOLEAN DEFAULT TRUE,
    type_livre VARCHAR(20) DEFAULT 'GRATUIT',
    prix DOUBLE DEFAULT 0.0,
    date_ajout DATETIME DEFAULT CURRENT_TIMESTAMP,
    id_auteur BIGINT,
    id_categorie BIGINT,
    FOREIGN KEY (id_auteur) REFERENCES Auteur(id),
    FOREIGN KEY (id_categorie) REFERENCES Categorie(id)
);

-- Table Paiement
CREATE TABLE IF NOT EXISTS Paiement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    id_livre BIGINT NOT NULL,
    montant DOUBLE NOT NULL,
    date_paiement DATETIME DEFAULT CURRENT_TIMESTAMP,
    statut VARCHAR(20) DEFAULT 'NON_PAYE',
    mode_paiement VARCHAR(50),
    reference_transaction VARCHAR(100),
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id),
    FOREIGN KEY (id_livre) REFERENCES Livre(id)
);

-- Table Telechargement
CREATE TABLE IF NOT EXISTS Telechargement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    id_livre BIGINT NOT NULL,
    date_telechargement DATETIME DEFAULT CURRENT_TIMESTAMP,
    autorise BOOLEAN DEFAULT FALSE,
    id_paiement BIGINT,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id),
    FOREIGN KEY (id_livre) REFERENCES Livre(id),
    FOREIGN KEY (id_paiement) REFERENCES Paiement(id)
);

-- Table Consultation
CREATE TABLE IF NOT EXISTS Consultation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur BIGINT NOT NULL,
    id_livre BIGINT NOT NULL,
    date_consultation DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id),
    FOREIGN KEY (id_livre) REFERENCES Livre(id)
);

-- Insertion de données initiales

-- Utilisateur Admin (mot de passe: admin123)
INSERT INTO Utilisateur (nom_utilisateur, mot_de_passe, nom, prenom, email, role) 
VALUES ('admin', 'admin123', 'Admin', 'Système', 'admin@bibliotheque.com', 'ADMINISTRATEUR');

-- Utilisateur Membre (mot de passe: user123)
INSERT INTO Utilisateur (nom_utilisateur, mot_de_passe, nom, prenom, email, role) 
VALUES ('user', 'user123', 'Utilisateur', 'Test', 'user@test.com', 'MEMBRE');

-- Catégories
INSERT INTO Categorie (nom, description) VALUES 
('Informatique', 'Livres sur le développement, réseaux, IA, etc.'),
('Roman', 'Fictions littéraires'),
('Science-Fiction', 'Romans futuristes et technologiques'),
('Histoire', 'Ouvrages historiques'),
('Sciences', 'Physique, Chimie, Biologie, Mathématiques'),
('Droit', 'Livre sur le droit');

-- Auteurs
INSERT INTO Auteur (nom, prenom, biographie) VALUES 
('Hugo', 'Victor', 'Écrivain dramaturge, poète, prosateur romantique français.'),
('Verne', 'Jules', 'Écrivain français dont l''œuvre est constituée de romans d''aventures.'),
('Martin', 'Robert C.', 'Auteur de Clean Code et Clean Architecture.'),
('Orwell', 'George', 'Écrivain et journaliste britannique.'),
('Rowling', 'J.K.', 'Autrice britannique de la saga Harry Potter.'),
('Tolkien', 'J.R.R.', 'Auteur du Seigneur des Anneaux.'),
('Camus', 'Albert', 'Écrivain et philosophe français.'),
('Kafka', 'Franz', 'Écrivain de langue allemande.'),
('Asimov', 'Isaac', 'Auteur de science-fiction et scientifique.'),
('King', 'Stephen', 'Auteur américain de romans d’horreur.'),
('Hemingway', 'Ernest', 'Romancier et journaliste américain.'),
('Austen', 'Jane', 'Romancière anglaise.'),
('Dickens', 'Charles', 'Écrivain anglais de l’époque victorienne.'),
('Dumas', 'Alexandre', 'Auteur français de romans historiques.'),
('Zola', 'Émile', 'Chef de file du naturalisme.'),
('Balzac', 'Honoré de', 'Auteur de La Comédie humaine.'),
('Flaubert', 'Gustave', 'Auteur de Madame Bovary.'),
('Coelho', 'Paulo', 'Romancier et poète brésilien.'),
('Brown', 'Dan', 'Auteur de thrillers.'),
('Eco', 'Umberto', 'Écrivain et philosophe italien.'),
('Shelley', 'Mary', 'Autrice de Frankenstein.'),
('Bradbury', 'Ray', 'Auteur de science-fiction.'),
('Huxley', 'Aldous', 'Auteur du Meilleur des mondes.'),
('Lovecraft', 'H.P.', 'Auteur de récits fantastiques.'),
('Michelet', 'Jules', 'Historien français, auteur de l’Histoire de la Révolution française.'),
('Tulard', 'Jean', 'Historien spécialiste de Napoléon Bonaparte.'),
('Le Goff', 'Jacques', 'Historien français spécialiste du Moyen Âge.'),
('Beevor', 'Antony', 'Historien britannique spécialiste de la Seconde Guerre mondiale.'),
('Gibbon', 'Edward', 'Historien anglais, auteur sur l’Empire romain.'),
('Keegan', 'John', 'Historien militaire britannique.'),
('Hodgson', 'Marshall', 'Historien de la civilisation islamique.'),
('Durant', 'Will', 'Historien et philosophe américain.'),
('Laroui', 'Abdallah', 'Historien marocain spécialiste de l’histoire du Maroc.'),
('Momigliano', 'Arnaldo', 'Historien de l’Antiquité.');

-- Livres
INSERT INTO Livre (titre, isbn, annee_publication, description, type_livre, prix, id_auteur, id_categorie, chemin_pdf) VALUES 
('Les Misérables', '978-2070409228', 1862, 'Un roman historique qui suit la vie de Jean Valjean.', 'GRATUIT', 0.0, 1, 2, 'C:/Livres/les miserables.pdf'),
('Vingt mille lieues sous les mers', '978-2070400508', 1870, 'Un roman d''aventures sous-marines.', 'GRATUIT', 0.0, 2, 3, 'C:/Livres/20000_lieues.pdf'),
('Clean Code', '978-0132350884', 2008, 'A Handbook of Agile Software Craftsmanship.', 'PAYANT', 35.0, 3, 1, 'C:/Livres/clean_code.pdf'),
('1984', '978-2070368228', 1949, 'Un roman dystopique célèbre.', 'PAYANT', 12.50, 4, 3, 'C:/Livres/1984.pdf'),
('Harry Potter à l’école des sorciers', '978-0747532699', 1997, 'Début de la saga Harry Potter.', 'PAYANT', 15.0, 5, 3, 'C:/Livres/hp1.pdf'),
('Le Seigneur des Anneaux', '978-0261102385', 1954, 'Épopée de fantasy.', 'PAYANT', 25.0, 6, 3, 'C:/Livres/lotr.pdf'),
('L’Étranger', '978-2070360024', 1942, 'Roman philosophique.', 'GRATUIT', 0.0, 7, 2, 'C:/Livres/etranger.pdf'),
('Le Procès', '978-2253006329', 1925, 'Roman absurde.', 'GRATUIT', 0.0, 8, 2, 'C:/Livres/proces.pdf'),
('Fondation', '978-0553293357', 1951, 'Saga de science-fiction.', 'PAYANT', 18.0, 9, 3, 'C:/Livres/fondation.pdf'),
('Shining', '978-0307743657', 1977, 'Roman d’horreur.', 'PAYANT', 14.0, 10, 3, 'C:/Livres/shining.pdf'),
('Le Vieil Homme et la Mer', '978-0684801223', 1952, 'Roman court.', 'GRATUIT', 0.0, 11, 2, 'C:/Livres/vieil_homme_mer.pdf'),
('Orgueil et Préjugés', '978-0141439518', 1813, 'Roman romantique.', 'GRATUIT', 0.0, 12, 2, 'C:/Livres/orgueil_prejuges.pdf'),
('Oliver Twist', '978-0141439747', 1838, 'Roman social.', 'GRATUIT', 0.0, 13, 2, 'C:/Livres/oliver_twist.pdf'),
('Les Trois Mousquetaires', '978-2070409181', 1844, 'Roman d’aventures.', 'GRATUIT', 0.0, 14, 3, 'C:/Livres/trois_mousquetaires.pdf'),
('Germinal', '978-2070404148', 1885, 'Roman social.', 'GRATUIT', 0.0, 15, 2, 'C:/Livres/germinal.pdf'),
('Le Père Goriot', '978-2070413119', 1835, 'Roman réaliste.', 'GRATUIT', 0.0, 16, 2, 'C:/Livres/pere_goriot.pdf'),
('Madame Bovary', '978-2070413110', 1857, 'Roman réaliste.', 'GRATUIT', 0.0, 17, 2, 'C:/Livres/madame_bovary.pdf'),
('L’Alchimiste', '978-0062315007', 1988, 'Roman initiatique.', 'PAYANT', 10.0, 18, 2, 'C:/Livres/alchimiste.pdf'),
('Da Vinci Code', '978-0307474278', 2003, 'Thriller ésotérique.', 'PAYANT', 20.0, 19, 3, 'C:/Livres/davinci_code.pdf'),
('Le Nom de la rose', '978-2253044765', 1980, 'Roman historique.', 'PAYANT', 17.0, 20, 2, 'C:/Livres/nom_rose.pdf'),
('Frankenstein', '978-0486282114', 1818, 'Roman gothique.', 'GRATUIT', 0.0, 21, 3, 'C:/Livres/frankenstein.pdf'),
('Fahrenheit 451', '978-1451673319', 1953, 'Roman dystopique.', 'PAYANT', 13.0, 22, 3, 'C:/Livres/fahrenheit451.pdf'),
('Le Meilleur des mondes', '978-0060850524', 1932, 'Roman dystopique.', 'PAYANT', 12.0, 23, 3, 'C:/Livres/meilleur_mondes.pdf'),
('L’Appel de Cthulhu', '978-2253004226', 1928, 'Nouvelle fantastique.', 'GRATUIT', 0.0, 24, 3, 'C:/Livres/cthulhu.pdf'),
('Histoire de la Révolution française', '978-2070409129', 1847, 'Analyse détaillée de la Révolution française.', 'GRATUIT', 0.0, 1, 4, 'C:/Livres/revolution_francaise.pdf'),
('Napoléon Bonaparte', '978-2070412150', 1969, 'Biographie complète de Napoléon Ier.', 'PAYANT', 18.0, 14, 4, 'C:/Livres/napoleon.pdf'),
('Histoire du Moyen Âge', '978-2130543210', 1998, 'Panorama historique du Moyen Âge européen.', 'PAYANT', 22.0, 15, 4, 'C:/Livres/moyen_age.pdf'),
('La Seconde Guerre mondiale', '978-2213667812', 2001, 'Étude globale du conflit mondial.', 'PAYANT', 25.0, 16, 4, 'C:/Livres/seconde_guerre.pdf'),
('Histoire de l’Empire romain', '978-2070409990', 1995, 'Chronique de l’Empire romain.', 'GRATUIT', 0.0, 17, 4, 'C:/Livres/empire_romain.pdf'),
('La Première Guerre mondiale', '978-2213667805', 2000, 'Analyse historique de la Grande Guerre.', 'PAYANT', 20.0, 18, 4, 'C:/Livres/premiere_guerre.pdf'),
('Histoire de l’Islam', '978-2130612345', 2005, 'Origines et expansion de la civilisation islamique.', 'PAYANT', 19.0, 19, 4, 'C:/Livres/histoire_islam.pdf'),
('Les Grandes Civilisations', '978-2070418886', 2010, 'Étude des civilisations anciennes.', 'PAYANT', 23.0, 20, 4, 'C:/Livres/civilisations.pdf'),
('Histoire du Maroc', '978-9954123456', 2012, 'Histoire générale du Maroc à travers les siècles.', 'GRATUIT', 0.0, 21, 4, 'C:/Livres/histoire_maroc.pdf'),
('Le Monde antique', '978-2130532109', 1990, 'Panorama historique de l’Antiquité.', 'GRATUIT', 0.0, 22, 4, 'C:/Livres/monde_antique.pdf'),
('Introduction au droit', '978-2247218842', 2019, 'Les bases fondamentales du droit et de ses branches.', 'GRATUIT', 0.0, 4, 4, 'C:/Livres/introduction_droit.pdf'),
('Droit constitutionnel', '978-2247219306', 2020, 'Étude des constitutions et des institutions politiques.', 'PAYANT', 25.0, 5, 4, 'C:/Livres/droit_constitutionnel.pdf'),
('Droit pénal général', '978-2247221453', 2018, 'Principes généraux du droit pénal.', 'PAYANT', 30.0, 6, 4, 'C:/Livres/droit_penal.pdf'),
('Droit civil – Les obligations', '978-2247215605', 2017, 'Les règles juridiques des contrats et responsabilités.', 'GRATUIT', 0.0, 7, 4, 'C:/Livres/droit_civil.pdf'),
('Introduction à la physique', '978-2100791766', 2021, 'Les concepts fondamentaux de la physique moderne.', 'GRATUIT', 0.0, 8, 5, 'C:/Livres/physique.pdf'),
('Chimie générale', '978-2100800987', 2020, 'Principes de base de la chimie.', 'PAYANT', 28.0, 9, 5, 'C:/Livres/chimie.pdf'),
('Biologie cellulaire', '978-2100788452', 2019, 'Étude de la structure et du fonctionnement des cellules.', 'PAYANT', 32.0, 10, 5, 'C:/Livres/biologie.pdf'),
('Mathématiques pour les sciences', '978-2100795412', 2022, 'Outils mathématiques pour les disciplines scientifiques.', 'GRATUIT', 0.0, 11, 5, 'C:/Livres/maths_sciences.pdf');


