package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.List;

public class KartisQulisGamomtvleli {
    private final Cveti koziriCveti;

    public KartisQulisGamomtvleli(Cveti koziriCveti) {
        this.koziriCveti = koziriCveti;
    }


    public int gamotvale(List<Krugi> krugebi, boolean boloKrugia) {
        int qula = krugebi.stream()
                .flatMap(k -> k.natamashebiKartebi().stream())
                .mapToInt(nk -> kartisQula(nk.karti()))
                .sum();

        if (boloKrugia) {
            qula += 10;
        }

        return qula;
    }

    private int kartisQula(Karti karti) {
        boolean koziria = karti.cveti() == koziriCveti;
        return koziria ? koziriKartisQula(karti.ranki()) : kartisQula(karti.ranki());
    }

    private int koziriKartisQula(Ranki ranki) {
        return switch (ranki) {
            case VALETI -> 20;
            case CXRA -> 14;
            case TUZI -> 11;
            case ATI -> 10;
            case KAROLI -> 4;
            case DAMA -> 3;
            case RVA -> 0;
            case SHVIDI -> 0;
        };
    }

    private int kartisQula(Ranki ranki) {
        return switch (ranki) {
            case TUZI -> 11;
            case ATI -> 10;
            case KAROLI -> 4;
            case DAMA -> 3;
            case VALETI -> 2;
            case CXRA -> 0;
            case RVA -> 0;
            case SHVIDI -> 0;
        };
    }

}
