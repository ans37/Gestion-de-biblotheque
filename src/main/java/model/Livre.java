package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Livre {
    private Long id;
    private String isbn;
    private String titre;
    private Integer anneePublication;
    private String description;
    private String langue;
    private Integer nombrePages;
    private String cheminPdf;
    private Boolean disponible;
    private TypeLivre typeLivre;
    private Double prix;
    private LocalDateTime dateAjout;
    private Long idAuteur;
    private Long idCategorie;

    // Objets associés (pour faciliter l'affichage)
    private Auteur auteur;
    private Categorie categorie;

    // Constructeurs
    public Livre() {
        this.disponible = true;
        this.typeLivre = TypeLivre.GRATUIT;
        this.prix = 0.0;
    }

    public Livre(String titre, String cheminPdf, Long idAuteur, Long idCategorie) {
        this();
        this.titre = titre;
        this.cheminPdf = cheminPdf;
        this.idAuteur = idAuteur;
        this.idCategorie = idCategorie;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Integer getAnneePublication() { return anneePublication; }
    public void setAnneePublication(Integer anneePublication) { this.anneePublication = anneePublication; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }

    public Integer getNombrePages() { return nombrePages; }
    public void setNombrePages(Integer nombrePages) { this.nombrePages = nombrePages; }

    public String getCheminPdf() { return cheminPdf; }
    public void setCheminPdf(String cheminPdf) { this.cheminPdf = cheminPdf; }

    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }

    public TypeLivre getTypeLivre() { return typeLivre; }
    public void setTypeLivre(TypeLivre typeLivre) { this.typeLivre = typeLivre; }

    public Double getPrix() { return prix; }
    public void setPrix(Double prix) { this.prix = prix; }

    public LocalDateTime getDateAjout() { return dateAjout; }
    public void setDateAjout(LocalDateTime dateAjout) { this.dateAjout = dateAjout; }

    public Long getIdAuteur() { return idAuteur; }
    public void setIdAuteur(Long idAuteur) { this.idAuteur = idAuteur; }

    public Long getIdCategorie() { return idCategorie; }
    public void setIdCategorie(Long idCategorie) { this.idCategorie = idCategorie; }

    public Auteur getAuteur() { return auteur; }
    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
        if (auteur != null) {
            this.idAuteur = auteur.getId();
        }
    }

    public Categorie getCategorie() { return categorie; }
    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
        if (categorie != null) {
            this.idCategorie = categorie.getId();
        }
    }

    public boolean isGratuit() {
        return typeLivre == TypeLivre.GRATUIT;
    }

    public boolean isPayant() {
        return typeLivre == TypeLivre.PAYANT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livre livre = (Livre) o;
        return Objects.equals(id, livre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Livre{" +
                "titre='" + titre + '\'' +
                ", auteur=" + (auteur != null ? auteur.getNomComplet() : "N/A") +
                ", type=" + typeLivre +
                ", prix=" + prix +
                '}';
    }
}