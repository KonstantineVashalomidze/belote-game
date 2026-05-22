package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Krugi {
    private final Cveti koziriCveti;
    private final List<NatamashebiKarti> natamashebiKartebi = new ArrayList<>();
    
    public Krugi(Cveti koziriCveti) {
        this.koziriCveti = koziriCveti;
    }
    
    
    public void motamashemChamovidaKarti(Motamashe motamashe, Karti karti) {
        natamashebiKartebi.add(new NatamashebiKarti(karti, motamashe));
    }
    
    public Cveti raaNatarebi() {
        if (natamashebiKartebi.isEmpty()) return null;
        return natamashebiKartebi.getFirst().karti().cveti();
    }
    
    public boolean mokozirda() {
        if (natamashebiKartebi.isEmpty()) return false;
        Cveti natarebiCveti = raaNatarebi();
        return natamashebiKartebi.stream()
                .anyMatch(nk -> nk.karti().cveti() == koziriCveti && koziriCveti != natarebiCveti);
    }
    
    
    public Karti udzlieresiKoziri() {
        return natamashebiKartebi.stream()
                .map(NatamashebiKarti::karti)
                .filter(karti -> karti.cveti() == koziriCveti)
                .max(Comparator.comparingInt(c -> koziriKartisIndexi(c.ranki())))
                .orElse(null);
    }
    
    public boolean sruliKrugia() {
        return natamashebiKartebi.size() == 4;
    }
    
    
    public List<NatamashebiKarti> natamashebiKartebi() {
        return natamashebiKartebi;
    }
    
    public NatamashebiKarti daadgineGamarjvebuli() {
        if (!sruliKrugia()) throw new IllegalStateException("სამიგაქ, ჯერ არ ჩამოსულა ყველა");
        if (mokozirda()) {
            return natamashebiKartebi.stream()
                    .filter(nk -> nk.karti().cveti() == koziriCveti)
                    .max(Comparator.comparingInt(nk -> koziriKartisIndexi(nk.karti().ranki())))
                    .orElseThrow();
        } else {
            Cveti natarebiCveti = raaNatarebi();
            return natamashebiKartebi.stream()
                    .filter(nk -> nk.karti().cveti() == natarebiCveti)
                    .max(Comparator.comparingInt(nk -> kartisIndexi(nk.karti().ranki())))
                    .orElseThrow();
        }
    }

    private int kartisIndexi(Ranki ranki) {
        return switch (ranki) {
            case TUZI -> 7;
            case ATI -> 6;
            case KAROLI -> 5;
            case DAMA -> 4;
            case VALETI -> 3;
            case CXRA -> 2;
            case RVA -> 1;
            case SHVIDI -> 0;
        };
    }

    private int koziriKartisIndexi(Ranki ranki) {
        return switch (ranki) {
            case VALETI -> 7;
            case CXRA -> 6;
            case TUZI -> 5;
            case ATI -> 4;
            case KAROLI -> 3;
            case DAMA -> 2;
            case RVA -> 1;
            case SHVIDI -> 0;
        };
    }

}
