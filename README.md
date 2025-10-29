# 🏪 DSMS — Système de Synchronisation Multi-Sites des Ventes

**DSMS** est une application Spring Boot permettant de synchroniser automatiquement les données de vente entre trois bases de données PostgreSQL distinctes correspondant aux régions **Dakar**, **Thiès** et **Saint-Louis**.  
Elle offre une interface web simple basée sur **Thymeleaf** pour visualiser, ajouter et synchroniser les ventes en temps réel.

---

## 🚀 Fonctionnalités principales

- 🔗 Connexion simultanée à 3 bases PostgreSQL (Dakar, Thiès, Saint-Louis)
- 🔄 Synchronisation automatique des ventes toutes les 60 secondes
- 🧩 Synchronisation manuelle via un bouton dans l’interface web
- 🧾 Affichage consolidé de toutes les ventes sur une seule page
- ✨ Interface HTML/Thymeleaf avec un design simple et réactif
- 💰 Gestion des montants en FCFA
- 🗓️ Ajout automatique de la date de vente (date du jour)

---

## 🏗️ Architecture du projet

com.example.dsms 
│
├── config/ # Configurations des 3 bases de données
│ ├── DakarDataSourceConfig.java
│ ├── ThiesDataSourceConfig.java
│ └── StlDataSourceConfig.java
│
├── model/ # Entité JPA (table ventes)
│ └── Vente.java
│
├── repository/ # Repositories JPA pour chaque région
│ ├── VenteRepositoryDakar.java
│ ├── VenteRepositoryThies.java
│ └── VenteRepositoryStl.java
│
├── service/ # Logique métier et synchronisation
│ ├── MultiVenteService.java
│ └── SyncService.java
│
├── controller/ # Contrôleurs Web
│ └── VenteController.java
│
└── templates/ # Pages Thymeleaf
├── fragments/header.html
├── ventes.html
└── ajout.html

---

## 🧠 Fonctionnement général

### 🔗 1. Multi-sources de données

Chaque région (Dakar, Thiès, St-Louis) dispose :
- de sa propre **base PostgreSQL** ;
- de sa propre **configuration (`DataSourceConfig`)** ;
- et de son **repository**.

Exemple de configuration :
```java
@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.dsms.repository",
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = com.example.dsms.repository.VenteRepositoryDakar.class
        ),
        entityManagerFactoryRef = "dakarEntityManagerFactory",
        transactionManagerRef = "dakarTransactionManager"
)
public class DakarDataSourceConfig {

}
```
### 🔄 2. Synchronisation automatique

Le service SyncService utilise **@Scheduled** pour exécuter une synchronisation régulière :
```java
@Scheduled(fixedDelayString = "#{T(java.lang.Long).valueOf('${sync.interval-seconds:60}') * 1000}")
public void scheduledSync() {
logger.info("Démarrage sync automatique...");
List<Vente> consolidated = multiVenteService.findAllConsolidated();
for (Vente v : consolidated) {
multiVenteService.upsertToAll(v);}
}
```
⏱️ Par défaut, la synchronisation s’effectue toutes les 60 secondes.

### 🧩 3. Synchronisation manuelle

L’utilisateur peut déclencher une synchronisation manuelle via un bouton dans l’interface web :
```html
<div class="actions">
    <a class="btn" th:href="@{/ventes/ajouter}">+ Ajouter une vente</a>
    <a class="btn sync" th:href="@{/ventes/sync}">🔁 Synchroniser maintenant</a>
</div>
```

### 🧾 4. Affichage consolidé des ventes
Toutes les ventes des 3 régions sont affichées sur une seule page web :
```java
@GetMapping
public String listeVentes(Model model) {
    model.addAttribute("ventes", multiVenteService.findAllConsolidated());
    return "ventes";
}
```
### ✨ 5. Interface web avec Thymeleaf
| Page                  | Rôle                                   |
|-----------------------|----------------------------------------|
| `/ventes`             | Affiche la liste consolidée des ventes |
| `/ajout`              | Permet d’ajouter une nouvelle vente    |
| `/edit`               | Permet de modifier une  vente          |
| `/sync` *(optionnel)* | Lance la synchronisation manuellement  |

### 🧱 6. Structure de la table vente
🧾 code entité Vente pour illustrer la structure de la table des ventes :

```java
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
```
---
## ⚙️ Configuration
Les paramètres de connexion aux bases de données sont définis dans `application.yml` :
```YAML
spring:
   jpa:
      hibernate:
         ddl-auto: update
      show-sql: true
      properties:
         hibernate:
            format_sql: true

app:
   datasources:
      dakar:
         url: jdbc:postgresql://localhost:5432/ventes_dakar
         username: dsms_user
         password: dsms_pass
      thies:
         url: jdbc:postgresql://localhost:5432/ventes_thies
         username: dsms_user
         password: dsms_pass
      stl:
         url: jdbc:postgresql://localhost:5432/ventes_stlouis
         username: dsms_user
         password: dsms_pass
```
---
## ▶️ Lancer le projet localement
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/Mylie730/Projet_bdd.git
    cd Projet_bdd
    ```
2️. Compiler et exécuter :
```bash
    mvn spring-boot:run
```
L’application sera accessible à l’adresse :

  👉 http://localhost:8080/ventes

---
## 🧩 Auteurs

👩‍💻 Émilie Napele GOMES et 👨‍💻 Samsidine Pascal Ehemba SONKO

🎓 Master 1 – Ingénierie Logicielle

🏫 Université Numérique Cheikh Hamidou Kane

📅 Année : 2025

---
📖 Notes complémentaires

La synchronisation s’appuie sur le champ updatedAt pour détecter les données récentes

Le projet est compatible Spring Boot DevTools (rechargement automatique pendant le dev)

Peut être étendu pour inclure d’autres régions ou des statistiques de ventes

---