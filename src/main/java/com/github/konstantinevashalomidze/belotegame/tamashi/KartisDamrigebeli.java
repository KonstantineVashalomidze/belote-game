package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.List;

public class KartisDamrigebeli {
    private final List<Motamashe> motamasheebi; // saatis isris mimart. indexi 0-3
    private final int kartisDamrigebelisPozicia; // romelia dileri am raundshi
    private final Dasta dasta;

    public KartisDamrigebeli(List<Motamashe> motamasheebi, int kartisDamrigebelisPozicia, Dasta dasta) {
        this.motamasheebi = motamasheebi;
        this.kartisDamrigebelisPozicia = kartisDamrigebelisPozicia;
        this.dasta = dasta;
    }

    public KartisDarigebisShedegi pirveladDaarige() {
        dasta.achexe();

        for (int step = 1; step <= 4; step++) {
            int motamashisPozicia = (kartisDamrigebelisPozicia + step) % 4;
            for (int micema = 0; micema < 5; micema++) {
                motamasheebi.get(motamashisPozicia).xeli().daimate(dasta.ertisKartisAmogheba());
            }
        }
        Karti amotrialebuliKarti = dasta.ertisKartisAmogheba();

        return new KartisDarigebisShedegi(dasta, amotrialebuliKarti);
    }

    public void meoredDaarige(Dasta darcheniliDasta, Motamashe mokozire, Karti amotrialebuliKarti) {
        mokozire.xeli().daimate(amotrialebuliKarti);

        for (int i = 1; i <= 4; i++) {
            int motamashisPozicia = (kartisDamrigebelisPozicia + i) % 4;
            Motamashe motamashe = motamasheebi.get(motamashisPozicia);
            int dasarigebeliKartebisRaodenoba = motamashe == mokozire ? 2 : 3;
            for (int j = 0; j < dasarigebeliKartebisRaodenoba; j++) {
                motamashe.xeli().daimate(darcheniliDasta.ertisKartisAmogheba());
            }
        }

    }


}
