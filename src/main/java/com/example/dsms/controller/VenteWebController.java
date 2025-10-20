package com.example.dsms.controller;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/ventes")
public class VenteWebController {

    private final MultiVenteService multiVenteService;

    public VenteWebController(MultiVenteService multiVenteService) {
        this.multiVenteService = multiVenteService;
    }

    @GetMapping
    public String listeVentes(Model model) {
        model.addAttribute("ventes", multiVenteService.findAllConsolidated());
        return "ventes";
    }

    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("vente", new Vente());
        return "ajout";
    }

    @PostMapping("/ajouter")
    public String ajouterVente(@ModelAttribute Vente vente) {
        if (vente.getRegion() != null) {
            switch (vente.getRegion().toUpperCase()) {
                case "DAKAR" -> multiVenteService.saveToDakar(vente);
                case "THIES" -> multiVenteService.saveToThies(vente);
                case "STLOUIS" -> multiVenteService.saveToStl(vente);
            }
        }
        return "redirect:/ventes";
    }

    @GetMapping("/sync")
    public String synchroniser() {
        multiVenteService.findAllConsolidated().forEach(multiVenteService::upsertToAll);
        return "redirect:/ventes";
    }
    @GetMapping("/delete/{id}")
    public String supprimerVente(@PathVariable("id") UUID id) {
        // Supprime la vente dans les 3 bases (par sécurité)
        multiVenteService.deleteFromAll(id);
        return "redirect:/ventes";
    }

}

