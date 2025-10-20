package com.example.dsms.repository;

import com.example.dsms.model.Vente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface VenteRepositoryDakar extends JpaRepository<Vente, UUID> {}
