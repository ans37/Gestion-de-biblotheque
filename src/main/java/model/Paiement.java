package model;

import java.time.LocalDateTime;

public class Paiement {
    private Long id;
    private Long idUtilisateur;
    private Long idLivre;
    private Double montant;
    private LocalDateTime datePaiement;
    private StatutPaiement statut;
    private String modePaiement;
    private String referenceTransaction;

    // Constructeurs
    public Paiement() {
        this.datePaiement = LocalDateTime.now();
        this.statut = StatutPaiement.NON_PAYE;
    }

    public Paiement(Long idUtilisateur, Long idLivre, Double montant, String modePaiement) {
        this();
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
        this.montant = montant;
        this.modePaiement = modePaiement;
    }

    public Paiement(Long id, Long idUtilisateur, Long idLivre, Double montant,
                    LocalDateTime datePaiement, StatutPaiement statut,
                    String modePaiement, String referenceTransaction) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.idLivre = idLivre;
        this.montant = montant;
        this.datePaiement = datePaiement;
        this.statut = statut;
        this.modePaiement = modePaiement;
        this.referenceTransaction = referenceTransaction;
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdLivre() { return idLivre; }
    public void setIdLivre(Long idLivre) { this.idLivre = idLivre; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public LocalDateTime getDatePaiement() { return datePaiement; }
    public void setDatePaiement(LocalDateTime datePaiement) { this.datePaiement = datePaiement; }

    public StatutPaiement getStatut() { return statut; }
    public void setStatut(StatutPaiement statut) { this.statut = statut; }

    public String getModePaiement() { return modePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }

    public String getReferenceTransaction() { return referenceTransaction; }
    public void setReferenceTransaction(String referenceTransaction) {
        this.referenceTransaction = referenceTransaction;
    }

    public boolean isPaye() {
        return statut == StatutPaiement.PAYE;
    }

    public void marquerCommePaye() {
        this.statut = StatutPaiement.PAYE;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", montant=" + montant +
                ", statut=" + statut +
                ", ref=" + referenceTransaction +
                '}';
    }
}