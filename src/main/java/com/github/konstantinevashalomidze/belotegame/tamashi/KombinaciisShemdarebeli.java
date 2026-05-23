package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.List;

import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.ERTNAIREBI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.MIYOLEBA;

public class KombinaciisShemdarebeli {
    private final Cveti koziriCveti;


    public KombinaciisShemdarebeli(Cveti koziriCveti) {
        this.koziriCveti = koziriCveti;
    }


    // a metia b
    public boolean metia(Kombinacia a, Kombinacia b) {
        if (a.tipi() == ERTNAIREBI && b.tipi() == ERTNAIREBI) {
            if (a.qula() != b.qula()) {
                return a.qula() > b.qula();
            }

            return saatisIsrisMimartulebitPirveliMotamashea(a.motamashe(), b.motamashe());
        }
        if (a.tipi() == MIYOLEBA && b.tipi() == MIYOLEBA) {
            return mimdevrobaMetiaMimdevrobaze(a, b);
        }

        if (a.tipi() == ERTNAIREBI && b.tipi() == MIYOLEBA) {
            return ertnairebiMetiaMimdevrobaze(a, b);
        }

        if (a.tipi() == MIYOLEBA && b.tipi() == ERTNAIREBI) {
            return !ertnairebiMetiaMimdevrobaze(b, a);
        }

        return false;
    }

    private boolean ertnairebiMetiaMimdevrobaze(Kombinacia ertnairi, Kombinacia miyoleba) {
        if (miyoleba.sigrdze() >= 5 && miyoleba.cveti() == koziriCveti) {
            return false;
        }
        return saatisIsrisMimartulebitPirveliMotamashea(ertnairi.motamashe(), miyoleba.motamashe());
    }

    private boolean mimdevrobaMetiaMimdevrobaze(Kombinacia a, Kombinacia b) {
        if (a.sigrdze() != b.sigrdze()) return a.sigrdze() > b.sigrdze();

        if (a.yvelazeDidiRanki().nomeri() != b.yvelazeDidiRanki().nomeri()) {
            return a.yvelazeDidiRanki().nomeri() > b.yvelazeDidiRanki().nomeri();
        }

        boolean aKoziria = a.cveti() == koziriCveti;
        boolean bKoziria = b.cveti() == koziriCveti;
        if (aKoziria != bKoziria) return aKoziria;


        return saatisIsrisMimartulebitPirveliMotamashea(a.motamashe(), b.motamashe());
    }

    private boolean saatisIsrisMimartulebitPirveliMotamashea(Motamashe aMotamashe, Motamashe bMotamashe) {
        return aMotamashe.pozicia() < bMotamashe.pozicia();
    }

    public Kombinacia ipoveUdzlieresiKombinacia(List<Kombinacia> kombinaciebi) {
        if (kombinaciebi.isEmpty()) return null;
        Kombinacia udzlieresiKombinacia = kombinaciebi.getFirst();
        for (int i = 0; i < kombinaciebi.size(); i++) {
            if (metia(kombinaciebi.get(i), udzlieresiKombinacia)) {
                udzlieresiKombinacia = kombinaciebi.get(i);
            }
        }
        return udzlieresiKombinacia;
    }

}
