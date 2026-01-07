package model;

import java.time.LocalDateTime;

public class Telechargement {
    private Long id;
    private Long idUtilisateur;
    private Long idLivre;
    private LocalDateTime dateTelechargement;
    private Boolean autorise;
    private Long idPaiement;

    // Constructeurs
    public Telechargement() {
        this.dateTelechargement = LocalDateTime.now();
        this.autorise = false;
    }

    public Telechargement(Long idUtilisateur, Long idLivre, Boolean autorise) {
        this();
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
        this.autorise = autorise;
    }

    public Telechargement(Long id, Long idUtilisateur, Long idLivre,
                          LocalDateTime dateTelechargement, Boolean autorise, Long idPaiement) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
        this.dateTelechargement = dateTelechargement;
        this.autorise = autorise;
        this.idPaiement = idPaiement;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }

    public LocalDateTime getDateTelechargement() { return dateTelechargement; }
    public void setDateTelechargement(LocalDateTime dateTelechargement) {
        this.dateTelechargement = dateTelechargement;
    }

    public Boolean getAutorise() { return autorise; }
    public void setAutorise(Boolean autorise) { this.autorise = autorise; }

    public Long getIdPaiement() { return idPaiement; }
    public void setIdPaiement(Long idPaiement) { this.idPaiement = idPaiement; }

    public boolean isAutorise() {
        return autorise != null && autorise;
    }

    @Override
    public String toString() {
        return "Telechargement{" +
                "id=" + id +
                ", idLivre=" + idLivre +
                ", autorise=" + autorise +
                ", date=" + dateTelechargement +
                '}';
    }
}