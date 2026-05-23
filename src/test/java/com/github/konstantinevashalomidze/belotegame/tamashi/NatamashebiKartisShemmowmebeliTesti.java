package com.github.konstantinevashalomidze.belotegame.tamashi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.konstantinevashalomidze.belotegame.tamashi.Cveti.*;
import static com.github.konstantinevashalomidze.belotegame.tamashi.Ranki.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NatamashebiKartisShemmowmebeliTesti {
    private static final Cveti KOZIRI = GULI;
    private NatamashebiKartisShemmowmebeli shemmowmebeli;
    private Motamashe motamashe;

    @BeforeEach
    void moawyve() {
        shemmowmebeli = new NatamashebiKartisShemmowmebeli(KOZIRI);
        motamashe = new Motamashe(0);
    }

    private void daamateXelshi(Karti... kartebi) {
        for (var karti : kartebi) {
            motamashe.xeli().daimate(karti);
        }
    }

    @Test
    void pirveliKartiKrugis_yvelaferiLegaluria() {
        daamateXelshi(
                new Karti(GULI, TUZI),
                new Karti(YVAVI, KAROLI)
        );
        Krugi krugi = new Krugi(KOZIRI);
        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(2, romeliKartebisTamashiSheidzleba.size());
    }

    @Test
    void cvetzeCvetiUndaChamovides_rocaYavsCveti() {
        daamateXelshi(
            new Karti(YVAVI, TUZI),
            new Karti(JVARI, KAROLI)
        );
        Krugi krugi = new Krugi(KOZIRI);
        krugi.motamashemChamovidaKarti(new Motamashe(1), new Karti(YVAVI, SHVIDI));

        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(1, romeliKartebisTamashiSheidzleba.size());
        assertEquals(new Karti(YVAVI, TUZI), romeliKartebisTamashiSheidzleba.getFirst());

    }

    @Test
    void koziritUndaGachras_rocaArYavsCveti() {
        daamateXelshi(
                new Karti(GULI, SHVIDI),
                new Karti(JVARI, KAROLI)
        );

        Krugi krugi = new Krugi(KOZIRI);
        krugi.motamashemChamovidaKarti(new Motamashe(1), new Karti(YVAVI, SHVIDI));

        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(1, romeliKartebisTamashiSheidzleba.size());
        assertEquals(new Karti(GULI, SHVIDI), romeliKartebisTamashiSheidzleba.getFirst());
    }


    @Test
    void koziritUndaGadaaxtes_rocaYavsMaghaliKoziri() {
        daamateXelshi(
                new Karti(GULI, TUZI),
                new Karti(GULI, SHVIDI)
        );

        Krugi krugi = new Krugi(KOZIRI);
        krugi.motamashemChamovidaKarti(new Motamashe(1), new Karti(YVAVI, SHVIDI));
        krugi.motamashemChamovidaKarti(new Motamashe(2), new Karti(GULI, ATI));

        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(1, romeliKartebisTamashiSheidzleba.size());
        assertEquals(new Karti(GULI, TUZI), romeliKartebisTamashiSheidzleba.getFirst());
    }

    @Test
    void nebismieriKoziriSheudzliaItamashos_rocaAtYavsMaghaliKoziri() {
        daamateXelshi(
                new Karti(GULI, DAMA),
                new Karti(GULI, SHVIDI)
        );
        Krugi krugi = new Krugi(KOZIRI);
        krugi.motamashemChamovidaKarti(new Motamashe(1), new Karti(YVAVI, SHVIDI));
        krugi.motamashemChamovidaKarti(new Motamashe(2), new Karti(GULI, ATI));

        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(2, romeliKartebisTamashiSheidzleba.size());


    }

    @Test
    void luboi_rocaArcCvetiYavsDaArcKoziri() {
        daamateXelshi(
                new Karti(JVARI, TUZI),
                new Karti(WKENTI, KAROLI)
        );
        Krugi krugi = new Krugi(KOZIRI);
        krugi.motamashemChamovidaKarti(new Motamashe(1), new Karti(YVAVI, SHVIDI));

        List<Karti> romeliKartebisTamashiSheidzleba = shemmowmebeli.romeliKartebisTamashiSheidzleba(motamashe, krugi);

        assertEquals(2, romeliKartebisTamashiSheidzleba.size());
    }


}
