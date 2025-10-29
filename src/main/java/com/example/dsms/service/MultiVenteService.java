package com.example.dsms.service;

import com.example.dsms.model.Vente;
import com.example.dsms.repository.VenteRepositoryDakar;
import com.example.dsms.repository.VenteRepositoryStl;
import com.example.dsms.repository.VenteRepositoryThies;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service

public class MultiVenteService {

    private final VenteRepositoryDakar dakar;
    private final VenteRepositoryThies thies;
    private final VenteRepositoryStl stl;

    public MultiVenteService(VenteRepositoryDakar dakar, VenteRepositoryThies thies, VenteRepositoryStl stl) {
        this.dakar = dakar;
        this.thies = thies;
        this.stl = stl;
    }

    public List<Vente> findAllConsolidated() {
        Map<UUID, Vente> map = new HashMap<>();
        List<Vente> all = new ArrayList<>();
        all.addAll(dakar.findAll());
        all.addAll(thies.findAll());
        all.addAll(stl.findAll());
        for (Vente v : all) {
            if (v.getId()== null) continue;
            Vente cur = map.get(v.getId());
            if (cur == null || isNewer(v.getUpdatedAt(), cur.getUpdatedAt())) map.put(v.getId(), v);
        }
        return new ArrayList<>(map.values());
    }

    private boolean isNewer(LocalDateTime a, LocalDateTime b) {
        if (a == null) return false;
        if (b == null) return true;
        return a.isAfter(b);
    }

    @Transactional("dakarTransactionManager")
    public Vente saveToDakar(Vente v) { return dakar.save(v); }

    @Transactional("thiesTransactionManager")
    public Vente saveToThies(Vente v) { return thies.save(v); }

    @Transactional("stlTransactionManager")
    public Vente saveToStl(Vente v) { return stl.save(v); }

    // upsert across all three (called by SyncService)
    public void upsertToAll(Vente v) {
        saveToDakar(v);
        saveToThies(v);
        saveToStl(v);
    }
    @Transactional("dakarTransactionManager")
    public void deleteFromDakar(UUID id) {
        dakar.deleteById(id);
    }

    @Transactional("thiesTransactionManager")
    public void deleteFromThies(UUID id) {
        thies.deleteById(id);
    }

    @Transactional("stlTransactionManager")
    public void deleteFromStl(UUID id) {
        stl.deleteById(id);
    }

    public void deleteFromAll(UUID id) {
        try { deleteFromDakar(id); } catch (Exception ignored) {}
        try { deleteFromThies(id); } catch (Exception ignored) {}
        try { deleteFromStl(id); } catch (Exception ignored) {}
    }
    public Vente findById(UUID id) {
        return dakar.findById(id)
                .or(() -> thies.findById(id))
                .or(() -> stl.findById(id))
                .orElse(null);
    }

    public void updateInAll(Vente vente) {
        // met à jour dans toutes les bases
        upsertToAll(vente);
    }
}