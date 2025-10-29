package com.example.dsms.controller;

import com.example.dsms.model.Vente;
import com.example.dsms.service.MultiVenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/ventes")
public class VenteWebController {

    private final MultiVenteService multiVenteService;

    public VenteWebController(MultiVenteService multiVenteService) {
        this.multiVenteService = multiVenteService;
    }

    @GetMapping
    public String listeVentes(@RequestParam(required = false) String region, Model model) {
        // Récupération de TOUTES les ventes consolidées
        List<Vente> ventes = multiVenteService.findAllConsolidated();

        // Si une région est précisée, on filtre
        if (region != null && !region.isBlank()) {
            ventes.removeIf(v -> !v.getRegion().equalsIgnoreCase(region));
            model.addAttribute("region", region.toUpperCase());
        } else {
            model.addAttribute("region", "TOUTES");
        }

        model.addAttribute("ventes", ventes);
        return "ventes";
    }

    // ✅ Formulaire d’ajout
    @GetMapping("/ajouter")
    public String formAjout(Model model) {
        model.addAttribute("vente", new Vente());
        return "ajout";
    }

    // ✅ Ajouter une vente
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

    // ✅ Synchroniser manuellement
    @GetMapping("/sync")
    public String synchroniser() {
        multiVenteService.findAllConsolidated().forEach(multiVenteService::upsertToAll);
        return "redirect:/ventes";
    }

    // ✅ Supprimer une vente
    @GetMapping("/delete/{id}")
    public String supprimerVente(@PathVariable("id") UUID id) {
        // Supprime la vente dans les 3 bases (par sécurité)
        multiVenteService.deleteFromAll(id);
        return "redirect:/ventes";
    }
    // ✅ Formulaire de modification
    @GetMapping("/edit/{id}")
    public String formModifier(@PathVariable("id") UUID id, Model model) {
        Vente vente = multiVenteService.findById(id);
        if (vente == null) return "redirect:/ventes";
        model.addAttribute("vente", vente);
        return "edit";
    }

    // ✅ Mise à jour de la vente
    @PostMapping("/update")
    public String modifierVente(@ModelAttribute Vente vente) {
        multiVenteService.updateInAll(vente);
        return "redirect:/ventes";
    }

}

