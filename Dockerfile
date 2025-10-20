# Utilise une image Java 21 avec Maven préinstallé
FROM maven:3.9.6-eclipse-temurin-21

# Dossier de travail
WORKDIR /app

# Copier le contenu du projet
COPY . .

# Compiler le projet
RUN mvn clean package -DskipTests

# Exposer le port 8080
EXPOSE 8080

# Démarrer l'application
CMD ["java", "-jar", "target/base_de_donnees-0.0.1-SNAPSHOT.jar"]
