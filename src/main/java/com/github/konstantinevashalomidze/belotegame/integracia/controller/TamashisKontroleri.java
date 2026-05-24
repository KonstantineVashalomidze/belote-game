package com.github.konstantinevashalomidze.belotegame.integracia.controller;

import com.github.konstantinevashalomidze.belotegame.integracia.TamashisMexsiereba;
import com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesia;
import com.github.konstantinevashalomidze.belotegame.integracia.damxmare.TamashisMdgomareobisGadamaketebeli;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.*;
import com.github.konstantinevashalomidze.belotegame.tamashi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.github.konstantinevashalomidze.belotegame.tamashi.RaundisFaza.QULEBIS_DATVLA;

@RestController
@RequestMapping("/api/tamashi")
public class TamashisKontroleri {
    private final TamashisMexsiereba mexsiereba;
    private final TamashisMdgomareobisGadamaketebeli gadamaketebeli;

    @Autowired
    public TamashisKontroleri(TamashisMexsiereba mexsiereba, TamashisMdgomareobisGadamaketebeli gadamaketebeli) {
        this.mexsiereba = mexsiereba;
        this.gadamaketebeli = gadamaketebeli;
    }


    @PostMapping("/sheqmeni")
    public ResponseEntity<?> sheqmeniTamashi(@RequestBody TamashisSheqmnisMotxovna motxovna) {
        try {
            TamashisSesia sesia = mexsiereba.sheqmeniTamashi(motxovna.zedmetsaxeli());
            return ResponseEntity.ok(new TamashisSheqmnisPasuxi(sesia.otaxisId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/shesvla")
    public ResponseEntity<?> shediTamasshi(@RequestBody TamashshiShesvlisMotxovna motxovna) {
        try {
            mexsiereba.shediOtaxshi(motxovna.otaxisId(), motxovna.zedmetsaxeli(), motxovna.gundi());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{otaxisId}/mdgomareoba")
    public ResponseEntity<?> tamashisMdgomareoba(
            @PathVariable String otaxisId,
            @RequestParam String zedmetsaxeli
    ) {
        try {
            TamashisSesia sesia = mexsiereba.sesia(otaxisId);
            TamashisMdgomareobisPasuxi mdgomareoba = gadamaketebeli.gadaaketePasxuad(sesia, zedmetsaxeli);
            return ResponseEntity.ok(mdgomareoba);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{otaxisId}/koziroba/pirvelshi")
    public ResponseEntity<?> ikoziraMotamashem(
            @PathVariable String otaxisId,
            @RequestParam KozirobisMotxovna motxovna
            ) {
        try {
            TamashisSesia sesia = mexsiereba.sesia(otaxisId);
            Motamashe motamashe = sesia.motamashisPozicia(motxovna.zedmetsaxeli());
            sesia.tamashi().mimdinareRaundi().motamashemWaighoPirvelive(motamashe);
            sesia.tamashi().kozirobisDasrulebistanave();
            return ResponseEntity.ok(gadamaketebeli.gadaaketePasxuad(sesia, motxovna.zedmetsaxeli()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{otaxisId}/koziroba/pasi")
    public ResponseEntity<?> motamashemTqvaPasi(
            @PathVariable String otaxisId,
            @RequestBody KozirobisMotxovna motxovna
    ) {
        try {
            TamashisSesia sesia = mexsiereba.sesia(otaxisId);
            Motamashe motamashe= sesia.motamashisPozicia(motxovna.zedmetsaxeli());
            sesia.tamashi().mimdinareRaundi().motamashemPasiTqva(motamashe);
            return ResponseEntity.ok(gadamaketebeli.gadaaketePasxuad(sesia, motxovna.zedmetsaxeli()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{otaxisId}/koziroba/meoreshi")
    public ResponseEntity<?> ikoziraMotamashemMeoreshi(
            @PathVariable String otaxisId,
            @RequestBody KozirisCxadebisMotxovna motxovna
    ) {
        try {
            TamashisSesia sesia = mexsiereba.sesia(otaxisId);
            Motamashe motamashe= sesia.motamashisPozicia(motxovna.zedmetsaxeli());
            Cveti rashicIkozira = Cveti.valueOf(motxovna.cveti());
            sesia.tamashi().mimdinareRaundi().motamashemAcxadaMeoreshi(motamashe, rashicIkozira);
            sesia.tamashi().kozirobisDasrulebistanave();
            return ResponseEntity.ok(gadamaketebeli.gadaaketePasxuad(sesia, motxovna.zedmetsaxeli()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/{otaxisId}/kartis-chamosvla")
    public ResponseEntity<?> motamashemChamovidaKarti(
            @PathVariable String otaxisId,
            @RequestBody KartisTamashisMotxovna motxovna
    ) {

        try {
            TamashisSesia sesia = mexsiereba.sesia(otaxisId);
            Motamashe motamashe= sesia.motamashisPozicia(motxovna.zedmetsaxeli());

            Karti karti = new Karti(
                    Cveti.valueOf(motxovna.cveti()),
                    Ranki.valueOf(motxovna.ranki())
            );
            sesia.tamashi().mimdinareRaundi().kartisTamashi(motamashe, karti, motxovna.belotisCxadeba());

            if (sesia.tamashi().mimdinareRaundi().raundisFaza() == QULEBIS_DATVLA) {
                RaundisShedegi shedegi = sesia.tamashi().mimdinareRaundi().qula();
                sesia.tamashi().raundisDasrulebistanave(shedegi);
                if (!sesia.tamashi().tamashiDasrulebulia()) {
                    sesia.tamashi().daiwyeRaundi();
                }
            }
            return ResponseEntity.ok(gadamaketebeli.gadaaketePasxuad(sesia, motxovna.zedmetsaxeli()));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }



}
