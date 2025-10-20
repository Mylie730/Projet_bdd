package com.example.dsms.service;

import com.example.dsms.model.Vente;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);
    private final MultiVenteService multiVenteService;

    public SyncService(MultiVenteService multiVenteService) {
        this.multiVenteService = multiVenteService;
    }

    @Scheduled(fixedDelayString = "#{T(java.lang.Long).valueOf('${sync.interval-seconds:60}') * 1000}")
    public void scheduledSync() {
        logger.info("Démarrage sync automatique...");
        try {
            List<Vente> consolidated = multiVenteService.findAllConsolidated();
            for (Vente v : consolidated) {
                multiVenteService.upsertToAll(v);
            }
            logger.info("Sync terminé. {} enregistrements traités.", consolidated.size());
        } catch (Exception e) {
            logger.error("Erreur lors du sync automatique", e);
        }
    }
}