package model;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private Long idUtilisateur;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private String nomUtilisateur; // Pour l'affichage

    // Constructeurs
    public Message() {
    }

    public Message(Long idUtilisateur, String contenu) {
        this.idUtilisateur = idUtilisateur;
        this.contenu = contenu;
    }

    public Message(Long id, Long idUtilisateur, String contenu, LocalDateTime dateEnvoi) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.contenu = contenu;
        this.dateEnvoi = dateEnvoi;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(Long idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", idUtilisateur=" + idUtilisateur +
                ", contenu='" + contenu + '\'' +
                ", dateEnvoi=" + dateEnvoi +
                '}';
    }
}
