package com.example.dsms.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "ventes")
public class Vente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "date_vente")
    private LocalDate dateVente;

    @Column
    private Double montant;

    @Column(length = 200)
    private String produit;

    @Column(length = 50)
    private String region;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Vente() {}

    public Vente(UUID id, LocalDate dateVente, Double montant, String produit, String region, LocalDateTime updatedAt) {
        this.id = id;
        this.dateVente = dateVente;
        this.montant = montant;
        this.produit = produit;
        this.region = region;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (dateVente == null) dateVente = LocalDate.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getDateVente() { return dateVente; }
    public void setDateVente(LocalDate dateVente) { this.dateVente = dateVente; }

    public Double getMontant() { return montant; }
    public void setMontant(Double montant) { this.montant = montant; }

    public String getProduit() { return produit; }
    public void setProduit(String produit) { this.produit = produit; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vente)) return false;
        Vente vente = (Vente) o;
        return Objects.equals(id, vente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vente{" +
                "id=" + id +
                ", dateVente=" + dateVente +
                ", montant=" + montant +
                ", produit='" + produit + '\'' +
                ", region='" + region + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

