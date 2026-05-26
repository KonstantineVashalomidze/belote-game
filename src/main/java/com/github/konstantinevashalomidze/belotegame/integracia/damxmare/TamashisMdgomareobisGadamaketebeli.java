package com.github.konstantinevashalomidze.belotegame.integracia.damxmare;

import com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesia;
import com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi;
import com.github.konstantinevashalomidze.belotegame.integracia.rr.*;
import com.github.konstantinevashalomidze.belotegame.tamashi.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.DASRULEBULI;
import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.LODINI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.RaundisFaza.*;

@Component
public class TamashisMdgomareobisGadamaketebeli {
    public TamashisMdgomareobisPasuxi gadaaketePasxuad(TamashisSesia sesia, String zedmetsaxeli) {
        if (sesia.statusi() == LODINI) {
            return lodinisPasuxi(sesia);
        }
        Tamashi tamashi = sesia.tamashi();
        Raundi raundi = tamashi.mimdinareRaundi();
        Map<String, Integer> zedmetsaxeliDaMotamashisPozicia = sesia.zedmetsaxeliDaMotamashisPozicia();
        Map<Integer, String> motamashisPoziciaDaZedmetsaxeli = sheatrialeMapi(zedmetsaxeliDaMotamashisPozicia);


        int mimdinaresPozicia = raundi.raundisFaza() == KOZIROBA
                ? raundi.kozirobisMdgomareoba().mimdinareMotamashisPozicia()
                : raundi.mimdinareMotamashisPozicia();

        String mimdinareMotamashisZedmetsaxeli = motamashisPoziciaDaZedmetsaxeli.get(mimdinaresPozicia);

        Integer momtxovnisPozica = zedmetsaxeliDaMotamashisPozicia.get(zedmetsaxeli);
        if (momtxovnisPozica == null) {
            throw new IllegalArgumentException("მოთამაშე ვერ მოიძებნა");
        }
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

        if (raundi.raundisFaza() == KRUGEBI
        || raundi.raundisFaza() == QULEBIS_DATVLA
        || raundi.raundisFaza() == KOMBINACIIS_DEKLARACIA) {
            koziriCveti = raundi.koziriCveti().name();
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

        List<KombinaciisPasuxi> datvliliKombinaciebi = null;
        String kombinaciisGamarjvebuliGundi = null;

        if (raundi.kombinaciisShedegi() != null) {
            KombinaciisShedegi kombinaciisShedegi = raundi.kombinaciisShedegi();
            datvliliKombinaciebi = kombinaciisShedegi.kombinaciebi().stream()
                    .map(k -> new KombinaciisPasuxi(
                            k.tipi().name(),
                            k.qula(),
                            k.cveti() != null ? k.cveti().name() : null,
                            k.yvelazeDidiRanki().name(),
                            k.sigrdze()
                    ))
                    .toList();
            kombinaciisGamarjvebuliGundi = kombinaciisShedegi.gundi() == tamashi.gundiA() ? "A" : "B";
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
            gamarjvebuliGundi, datvliliKombinaciebi, kombinaciisGamarjvebuliGundi,
null,
            raundi.kozirobisMdgomareoba().kozirobisFaza().name(),
            raundi.kozirobisMdgomareoba().sityvaVinujdenzea()
        );

    }

    private Map<Integer, String> sheatrialeMapi(Map<String, Integer> zedmetsaxeliDaMotamashisPozicia) {
        Map<Integer, String> shetrialebuli = new HashMap<>();
        zedmetsaxeliDaMotamashisPozicia.forEach((zedmetsaxeli, pozicia) -> shetrialebuli.put(pozicia, zedmetsaxeli));
        return shetrialebuli;
    }

    private TamashisMdgomareobisPasuxi lodinisPasuxi(TamashisSesia sesia) {
        List<MotamashePasuxi> motamasheebi = sesia.zedmetsaxeliDaRomelGundshia().entrySet().stream()
                .map(e -> new MotamashePasuxi(e.getKey(), e.getValue()))
                .toList();

        return new TamashisMdgomareobisPasuxi(
                sesia.otaxisId(),
                LODINI.name(),
                null,
                null,
                null,
                null,
                null,
                new QulisPasuxi(0, 0, 0),
                null,
                null,
                null,
                motamasheebi,
                null,
                null
        );
    }

}
