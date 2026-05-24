package com.github.konstantinevashalomidze.belotegame.integracia.damxmare;

import com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesia;
import com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.KartisPasuxi;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.NatamashebiKartisPasuxi;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.QulisPasuxi;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.TamashisMdgomareobisPasuxi;
import com.github.konstantinevashalomidze.belotegame.tamashi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.DASRULEBULI;
import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.LODINI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.RaundisFaza.*;

public class TamashisMdgomareobisGadamaketebeli {
    public TamashisMdgomareobisPasuxi gadaaketePasxuad(TamashisSesia sesia, String zedmetsaxeli) {
        if (sesia.statusi() == LODINI) {
            return lodinisPasuxi(sesia);
        }
        Tamashi tamashi = sesia.tamashi();
        Raundi raundi = tamashi.mimdinareRaundi();
        Map<String, Integer> zedmetsaxeliDaMotamashisPozicia = sesia.zedmetsaxeliDaMotamashisPozicia();
        Map<Integer, String> motamashisPoziciaDaZedmetsaxeli = sheatrialeMapi(zedmetsaxeliDaMotamashisPozicia);


        String mimdinareMotamashisZedmetsaxeli = motamashisPoziciaDaZedmetsaxeli.get(raundi.mimdinareMotamashisPozicia());

        int momtxovnisPozica = zedmetsaxeliDaMotamashisPozicia.get(mimdinareMotamashisZedmetsaxeli);
        Motamashe momtxovniMotamashe = tamashi.motamasheebi().get(momtxovnisPozica);
        List<KartisPasuxi> momtxovnisXeli = momtxovniMotamashe.xeli().kartebi().stream()
                .map(k -> new KartisPasuxi(k.cveti().name(), k.ranki().name()))
                .toList();

        List<NatamashebiKartisPasuxi> mimdinareKrugi = null;
        String koziriCveti = null;
        String amotrialebuliKarti = null;
        if (raundi.raundisFaza() == KOZIROBA) {
            amotrialebuliKarti = raundi.kozirobisMdgomareoba().amotrialebuliKarti().cveti().name() +
                    "_" + raundi.kozirobisMdgomareoba().amotrialebuliKarti().ranki().name();
        }

        if (raundi.raundisFaza() == KRUGEBI || raundi.raundisFaza() == QULEBIS_DATVLA) {
            koziriCveti = raundi.koziriCveti().name();
            mimdinareKrugi = raundi.mimdinareKrugi().natamashebiKartebi().stream()
                    .map(nk -> new NatamashebiKartisPasuxi(
                            motamashisPoziciaDaZedmetsaxeli.get(nk.motamashe().pozicia()),
                            nk.karti().cveti().name(),
                            nk.karti().ranki().name()
                    ))
                    .toList();
        }

        QulisPasuxi qulebi = new QulisPasuxi(
                tamashi.gundiA().qula(),
                tamashi.gundiB().qula(),
                tamashi.gayinuliQula()
        );

        String gamarjvebuliGundi = null;
        if (tamashi.tamashiDasrulebulia()) {
            gamarjvebuliGundi = tamashi.gamarjvebuliGundia() == tamashi.gundiA() ? "A" : "B";
            sesia.statusi(DASRULEBULI);
        }

        return new TamashisMdgomareobisPasuxi(
            sesia.otaxisId(),
            raundi.raundisFaza().name(),
            mimdinareMotamashisZedmetsaxeli,
            momtxovnisXeli,
            mimdinareKrugi,
            koziriCveti,
            amotrialebuliKarti,
            qulebi,
            gamarjvebuliGundi
        );

    }

    private Map<Integer, String> sheatrialeMapi(Map<String, Integer> zedmetsaxeliDaMotamashisPozicia) {
        Map<Integer, String> shetrialebuli = new HashMap<>();
        zedmetsaxeliDaMotamashisPozicia.forEach((zedmetsaxeli, pozicia) -> shetrialebuli.put(pozicia, zedmetsaxeli));
        return shetrialebuli;
    }

    private TamashisMdgomareobisPasuxi lodinisPasuxi(TamashisSesia sesia) {
        return new TamashisMdgomareobisPasuxi(
                sesia.otaxisId(),
                LODINI.name(),
                null,
                null,
                null,
                null,
                null,
                new QulisPasuxi(0, 0, 0),
                null
        );
    }

}
