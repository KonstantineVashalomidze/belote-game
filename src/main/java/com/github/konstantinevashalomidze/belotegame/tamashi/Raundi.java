package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.ArrayList;
import java.util.List;

import static com.github.konstantinevashalomidze.belotegame.tamashi.RaundisFaza.*;

public class Raundi {

    private final List<Motamashe> motamasheebi;
    private final Gundi mokozireGundi;
    private final Gundi mowinaaghmdegeGundi;
    private final int kartisDamrigeblisPozicia;

    private KartisDamrigebeli kartisDamrigebeli;
    private KozirobisMdgomareoba kozirobisMdgomareoba;
    private KozirobisMmartveli kozirobisMmartveli;
    private Dasta darcheniliDasta;

    private Cveti koziriCveti;
    private Motamashe mokozire;

    private final List<Krugi> dasrulebuliKrugebi = new ArrayList<>();
    private Krugi mimdinareKrugi;
    private int mimdinareMotamashisPozicia;

    private RaundisFaza raundisFaza = KARTIS_DARIGEBA;

    private KombinaciisShedegi kombinaciisShedegi;

    private BelotisMayurebeli belotisMayurebeli;

    private DastisMomwodebeli dastisMomwodebeli;

    public Raundi(List<Motamashe> motamasheebi, Gundi mokozireGundi, Gundi mowinaaghmdegeGundi, int kartisDamrigeblisPozicia, DastisMomwodebeli dastisMomwodebeli) {
        this.motamasheebi = motamasheebi;
        this.mokozireGundi = mokozireGundi;
        this.mowinaaghmdegeGundi = mowinaaghmdegeGundi;
        this.kartisDamrigeblisPozicia = kartisDamrigeblisPozicia;
        this.dastisMomwodebeli = dastisMomwodebeli;
    }


    public void daarigeKarti() {
        kartisDamrigebeli = new KartisDamrigebeli(motamasheebi, kartisDamrigeblisPozicia, dastisMomwodebeli.sheqmeniDasta());
        KartisDarigebisShedegi kartisDarigebisShedegi = kartisDamrigebeli.pirveladDaarige();
        darcheniliDasta = kartisDarigebisShedegi.darcheniliDasta();

        kozirobisMdgomareoba = new KozirobisMdgomareoba(
                kartisDarigebisShedegi.amotrialebuliKarti(), kartisDamrigeblisPozicia);
        kozirobisMmartveli = new KozirobisMmartveli(motamasheebi, kozirobisMdgomareoba);

        kozirobisMmartveli.sheamowmeAmotrialebuliKartiValetze();

        if (kozirobisMdgomareoba.dasrulebulia()) {
            daasruleKoziroba();
        } else {
            raundisFaza = KOZIROBA;
        }
    }

    public void motamashemWaighoPirvelive(Motamashe motamashe) {
        sheamowmeSworFazashiTua(KOZIROBA);
        kozirobisMmartveli.ikozira(motamashe);
        if (kozirobisMdgomareoba.dasrulebulia()) daasruleKoziroba();
    }

    public void motamashemPasiTqva(Motamashe motamashe) {
        sheamowmeSworFazashiTua(KOZIROBA);
        kozirobisMmartveli.motamashemTqvaPasi(motamashe);
        if (kozirobisMdgomareoba.dasrulebulia()) daasruleKoziroba();
    }

    public void motamashemAcxadaMeoreshi(Motamashe motamashe, Cveti koziriCveti) {
        sheamowmeSworFazashiTua(KOZIROBA);
        kozirobisMmartveli.ikozira(motamashe, koziriCveti);
        if (kozirobisMdgomareoba.dasrulebulia()) daasruleKoziroba();
    }

    public void motamashemVinujdenshiWaigho(Cveti cveti) {
        sheamowmeSworFazashiTua(KOZIROBA);
        kozirobisMmartveli.vinujdenmaIkozira(cveti);
        if (kozirobisMdgomareoba.dasrulebulia()) daasruleKoziroba();
    }

    private void sheamowmeSworFazashiTua(RaundisFaza raundisFaza) {
        if (this.raundisFaza != raundisFaza) {
            throw new IllegalStateException("კაროჩე რაცხა პიზდეცი მოხდა და ვერ გეიგებ მაინც. 500 05 13 94 აქ დარეკე.");
        }
    }

    private void daasruleKoziroba() {
        koziriCveti = kozirobisMdgomareoba.koziriCveti();
        mokozire = kozirobisMdgomareoba.mokozire();
        kartisDamrigebeli.meoredDaarige(darcheniliDasta, mokozire, kozirobisMdgomareoba.amotrialebuliKarti());

        KombinaciisMmartveli mmartveli = new KombinaciisMmartveli(koziriCveti);
        kombinaciisShedegi = mmartveli.visiKombinaciaGadis(mokozireGundi, mowinaaghmdegeGundi);

        belotisMayurebeli = new BelotisMayurebeli(koziriCveti);

        raundisFaza = KRUGEBI;
        mimdinareMotamashisPozicia = (kartisDamrigeblisPozicia + 1) % 4;
        axaliKrugisDawyeba();
    }

    private void axaliKrugisDawyeba() {
        mimdinareKrugi = new Krugi(koziriCveti);
    }

    public void kartisTamashi(Motamashe motamashe, Karti karti, BelotisCxadeba belotisCxadeba) {
        sheamowmeSworFazashiTua(KRUGEBI);
        if (motamasheebi.indexOf(motamashe) != mimdinareMotamashisPozicia) {
            throw new IllegalStateException("აუ ვინაა ეს ყლე, არაა შენი ჯერი არა!!");
        }

        NatamashebiKartisShemmowmebeli shemmowmebeli = new NatamashebiKartisShemmowmebeli(koziriCveti);
        List<Karti> romeliKartebisTamashiaDasashvebi = shemmowmebeli.romeliKartebisTamashiSheidzleba(
                motamashe,
                mimdinareKrugi
        );
        if (!romeliKartebisTamashiaDasashvebi.contains(karti)) {
            throw new IllegalArgumentException("ვაი ბიძია... რას გვატყუებ ამდენ ხალხს");
        }

        belotisMayurebeli.belotiAnRebelotiCxadda(motamashe, karti, belotisCxadeba);

        motamashe.xeli().moishore(karti);
        mimdinareKrugi.motamashemChamovidaKarti(motamashe, karti);

        if (mimdinareKrugi.sruliKrugia()) {
            dasrulebuliKrugebi.add(mimdinareKrugi);

            if (dasrulebuliKrugebi.size() == 8) {
                raundisFaza = QULEBIS_DATVLA;
            }else {
                NatamashebiKarti gamarjcebuli = mimdinareKrugi.daadgineGamarjvebuli();
                mimdinareMotamashisPozicia = motamasheebi.indexOf(gamarjcebuli.motamashe());
                axaliKrugisDawyeba();
            }
        } else {
            mimdinareMotamashisPozicia = (mimdinareMotamashisPozicia + 1) % 4;
        }
    }


    public RaundisShedegi qula() {
        sheamowmeSworFazashiTua(QULEBIS_DATVLA);
        RaundisQulisGamomtvleli raundisQulisGamomtvleli = new RaundisQulisGamomtvleli(koziriCveti, mokozireGundi, mowinaaghmdegeGundi);
        return raundisQulisGamomtvleli.gamotvale(dasrulebuliKrugebi, kombinaciisShedegi, belotisMayurebeli);
    }

    public List<Karti> romeliKartebisTamashiaDasashvebi(Motamashe motamashe) {
        sheamowmeSworFazashiTua(KRUGEBI);
        return new NatamashebiKartisShemmowmebeli(koziriCveti).romeliKartebisTamashiSheidzleba(motamashe, mimdinareKrugi);
    }

    public RaundisFaza raundisFaza() {
        return raundisFaza;
    }

    public KozirobisMdgomareoba kozirobisMdgomareoba() {
        return kozirobisMdgomareoba;
    }

    public Krugi mimdinareKrugi() {
        return mimdinareKrugi;
    }

    public int mimdinareMotamashisPozicia() {
        return mimdinareMotamashisPozicia;
    }

    public KombinaciisShedegi kombinaciisShedegi() {
        return kombinaciisShedegi;
    }

    public Gundi mokozireGundi() {
        return mokozireGundi;
    }

    public Cveti koziriCveti() {
        return koziriCveti;
    }
}
