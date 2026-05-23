package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.ERTNAIREBI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.MIYOLEBA;

public class KombinaciisAmomcnobi {
    private final Cveti koziriCveti;

    public KombinaciisAmomcnobi(Cveti koziriCveti) {
        this.koziriCveti = koziriCveti;
    }

    public List<Kombinacia> ipoveKombinaciebi(Motamashe motamashe) {
        Xeli xeli = motamashe.xeli();

        List<Kombinacia> otxiErtnairebi = ipoveOtxiErtnairebi(motamashe, xeli);
        List<Kombinacia> kombinaciebi = new ArrayList<>(otxiErtnairebi);

        Set<Karti> gamoyenebuliRankebi = xeli.kartebi().stream()
                        .filter(k -> otxiErtnairebi.stream()
                                .anyMatch(ko -> ko.yvelazeDidiRanki() == k.ranki()))
                                .collect(Collectors.toSet());

        kombinaciebi.addAll(ipoveMiyolebebi(motamashe, xeli, gamoyenebuliRankebi));

        return kombinaciebi;
    }

    private List<Kombinacia> ipoveOtxiErtnairebi(Motamashe motamashe, Xeli xeli) {
        Map<Ranki, Long> rankisMixedvitDatvla = xeli.kartebi().stream()
                .collect(Collectors.groupingBy(Karti::ranki, Collectors.counting()));

        List<Kombinacia> shedegi = new ArrayList<>();

        for (var erteuli : rankisMixedvitDatvla.entrySet()) {
            if (erteuli.getValue() == 4) {
                Ranki ranki =  erteuli.getKey();
                int qula = otxiErtnairisQula(ranki);
                if (qula > 0) {
                    // otx ertnairs ar aqvs cveti magito null
                    // kozirnoi clke dardeba mere
                    shedegi.add(new Kombinacia(
                            ERTNAIREBI,
                            qula,
                            null,
                            ranki,
                            4,
                            motamashe
                    ));
                }
            }
        }

        return shedegi;
    }

    private int otxiErtnairisQula(Ranki ranki) {
        return switch (ranki) {
            case VALETI -> 200;
            case CXRA -> 150;
            case ATI, DAMA, KAROLI, TUZI -> 100;
            default -> 0;
        };
    }

    private List<Kombinacia> ipoveMiyolebebi(Motamashe motamashe, Xeli xeli, Set<Karti> gamoyenebuliRankebi) {
        List<Kombinacia> miyolebebi = new ArrayList<>();
        // kartebis dajgufeba cvetis mixedvit
        Map<Cveti, List<Ranki>> cvetisMixedvit = xeli.kartebi().stream()
                .filter(k -> !gamoyenebuliRankebi.contains(k))
                .collect(Collectors.groupingBy(
                        Karti::cveti,
                        Collectors.mapping(Karti::ranki, Collectors.toList())
                ));

        for (var erteuli : cvetisMixedvit.entrySet()) {
            Cveti cveti = erteuli.getKey();
            List<Ranki> rankebi = erteuli.getValue();
            
            rankebi.sort(Comparator.comparingInt(Ranki::nomeri));
            
            miyolebebi.addAll(ipoveMiyolebebiDidMiyolebashi(motamashe, cveti, rankebi));
        }

        return miyolebebi;
    }

    private List<Kombinacia> ipoveMiyolebebiDidMiyolebashi(Motamashe motamashe, Cveti cveti, List<Ranki> dasortiliRankebi) {
        List<Kombinacia> shedegi = new ArrayList<>();
        boolean[] gamoyenebuli = new boolean[dasortiliRankebi.size()];
    
        while (true){
            int sauketesosSawyisi = -1;
            int sauketesosSigrdze = 0;
            
            for (int i = 0; i < dasortiliRankebi.size(); i++) {
                if (gamoyenebuli[i]) continue;
                
                int sigrdze = 1;
                while (i + sigrdze < dasortiliRankebi.size()
                && !gamoyenebuli[i + sigrdze]
                && dasortiliRankebi.get(i + sigrdze).nomeri() == 
                dasortiliRankebi.get(i).nomeri() + sigrdze
                && sigrdze < 5) {
                    sigrdze++;
                }
            
                if (sigrdze > sauketesosSigrdze) {
                    sauketesosSigrdze = sigrdze;
                    sauketesosSawyisi = i;
                }
            }

            if (sauketesosSigrdze < 3) {
                break;
            }

            Ranki yvelazeMaghali = dasortiliRankebi.get(sauketesosSawyisi + sauketesosSigrdze - 1);
            int qula = switch (sauketesosSigrdze) {
                case 3 -> 20;
                case 4 -> 50;
                default -> 100;
            };

            shedegi.add(new Kombinacia(MIYOLEBA, qula, cveti, yvelazeMaghali, sauketesosSigrdze, motamashe));

            for (int k = sauketesosSawyisi; k < sauketesosSawyisi + sauketesosSigrdze; k++) {
                gamoyenebuli[k] = true;
            }
        }

        return shedegi;
    }


}
