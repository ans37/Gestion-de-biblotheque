package model;

import java.time.LocalDateTime;

public class Consultation {
    private Long id;
    private Long idUtilisateur;
    private Long idLivre;
    private LocalDateTime dateConsultation;
    private Integer dureeLecture; // en minutes

    // Constructeurs
    public Consultation() {
        this.dateConsultation = LocalDateTime.now();
    }

    public Consultation(Long idUtilisateur, Long idLivre) {
        this();
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
    }

    public Consultation(Long id, Long idUtilisateur, Long idLivre,
                        LocalDateTime dateConsultation, Integer dureeLecture) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
        this.dateConsultation = dateConsultation;
        this.dureeLecture = dureeLecture;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }

    public LocalDateTime getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDateTime dateConsultation) { this.dateConsultation = dateConsultation; }

    public Integer getDureeLecture() { return dureeLecture; }
    public void setDureeLecture(Integer dureeLecture) { this.dureeLecture = dureeLecture; }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", idLivre=" + idLivre +
                ", date=" + dateConsultation +
                '}';
    }
}