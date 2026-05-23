package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.List;

public class Tamashi {

    private final List<Motamashe> motamasheebi;
    private final Gundi gundiA;
    private final Gundi gundiB;

    private int kartisDamrigebelisPozicia = 3;

    private Raundi mimdinareRaundi;

    private Gundi mokozireGundi;

    private int gayinuliQula;

    private final DastisMomwodebeli dastisMomwodebeli;

    public Tamashi(Motamashe m0, Motamashe m1, Motamashe m2, Motamashe m3, DastisMomwodebeli dastisMomwodebeli) {
        motamasheebi = List.of(m0, m1, m2, m3);
        gundiA = new Gundi(m0, m2);
        gundiB = new Gundi(m1, m3);
        this.dastisMomwodebeli = dastisMomwodebeli;
    }


    public void daiwyeRaundi() {
        if (tamashiDasrulebulia()) {
            throw new IllegalStateException("დამთავრდა თამაში");
        }
        mimdinareRaundi = new Raundi(motamasheebi, gundiA, gundiB, kartisDamrigebelisPozicia, dastisMomwodebeli);
        mimdinareRaundi.daarigeKarti();
    }

    private boolean tamashiDasrulebulia() {
        return gundiA.mogebulia() || gundiB.mogebulia();
    }


    public void kozirobisDasrulebistanave() {
        mokozireGundi = mimdinareRaundi.mokozireGundi();
    }


    public void raundisDasrulebistanave(RaundisShedegi raundisShedegi) {
        Gundi mowinaaghmdegeGundi = mowinaaghmdegeGundi(mokozireGundi);
        switch (raundisShedegi.tipi()) {
            case CHVEULEBRIVI -> {
                mokozireGundi.daamateQula(raundisShedegi.mokozirisQulebi());
                mowinaaghmdegeGundi.daamateQula(raundisShedegi.mowinaaghmdegisQulebi());
                gayinuliQulbisDamatebaTuRamea(
                        raundisShedegi.mokozirisQulebi() > raundisShedegi.mowinaaghmdegisQulebi()
                        ? mokozireGundi
                        : mowinaaghmdegeGundi
                );
            }
            case CHAVARDNA -> {
                mokozireGundi.daamateQula(raundisShedegi.mokozirisQulebi());
                mowinaaghmdegeGundi.daamateQula(raundisShedegi.mowinaaghmdegisQulebi());
                gayinuliQulbisDamatebaTuRamea(mowinaaghmdegeGundi);
            }
            case KAPO -> {
                Gundi kaposGamketebeliGundi = mimdinareRaundi.mokozireGundi() == mokozireGundi
                        ? mokozireGundi : mowinaaghmdegeGundi;
                kaposGamketebeliGundi.daamateQula(raundisShedegi.mokozirisQulebi());
                mowinaaghmdegeGundi.daamateQula(raundisShedegi.mowinaaghmdegisQulebi());
                gayinuliQulbisDamatebaTuRamea(kaposGamketebeliGundi);
            }
            case FRE -> {
                gayinuliQula += raundisShedegi.mowinaaghmdegisQulebi();
                mowinaaghmdegeGundi.daamateQula(gayinuliQula);
            }
        }
    }

    private void gayinuliQulbisDamatebaTuRamea(Gundi gundi) {
        if (gayinuliQula > 0) {
            gundi.daamateQula(gayinuliQula);
            gayinuliQula = 0;
        }
    }


    private Gundi mowinaaghmdegeGundi(Gundi mokozireGundi) {
        return mokozireGundi == gundiA ? gundiB : gundiA;
    }

    public Gundi gamarjvebuliGundia() {
        if (gundiA.mogebulia()) return gundiA;
        if (gundiB.mogebulia()) return gundiB;
        return null;
    }

    public Raundi mimdinareRaundi() {
        return mimdinareRaundi;
    }

    public Gundi gundiA() {
        return gundiA;
    }

    public Gundi gundiB() {
        return gundiB;
    }

    public int kartisDamrigebelisPozicia() {
        return kartisDamrigebelisPozicia;
    }




}
