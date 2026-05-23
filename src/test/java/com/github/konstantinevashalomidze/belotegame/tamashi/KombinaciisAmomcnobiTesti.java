package com.github.konstantinevashalomidze.belotegame.tamashi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static com.github.konstantinevashalomidze.belotegame.tamashi.Cveti.*;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.ERTNAIREBI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.MIYOLEBA;
import static com.github.konstantinevashalomidze.belotegame.tamashi.Ranki.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KombinaciisAmomcnobiTesti {
    private static final Cveti KOZIRI = GULI;
    private KombinaciisAmomcnobi amomcnobi;
    private Motamashe motamashe;

    @BeforeEach
    void moawyve() {
        amomcnobi = new KombinaciisAmomcnobi(KOZIRI);
        motamashe = new Motamashe(0);
    }


    private void daamateXelshi(Karti... kartebi) {
        for (var karti : kartebi) {
            motamashe.xeli().daimate(karti);
        }
    }

    @Test
    void poulobsSamsMiyolebit() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(YVAVI, RVA),
                new Karti(YVAVI, CXRA)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);

        assertEquals(1, kombinaciebi.size());
        assertEquals(20, kombinaciebi.getFirst().qula());
        assertEquals(3, kombinaciebi.getFirst().sigrdze());
    }


    @Test
    void poulobsOtxsMiyolebit() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(YVAVI, RVA),
                new Karti(YVAVI, CXRA),
                new Karti(YVAVI, ATI)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);

        assertEquals(1, kombinaciebi.size());
        assertEquals(50, kombinaciebi.getFirst().qula());
        assertEquals(4, kombinaciebi.getFirst().sigrdze());
    }

    @Test
    void poulobsXutsMiyolebit() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(YVAVI, RVA),
                new Karti(YVAVI, CXRA),
                new Karti(YVAVI, ATI),
                new Karti(YVAVI, VALETI)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);

        assertEquals(1, kombinaciebi.size());
        assertEquals(100, kombinaciebi.getFirst().qula());
        assertEquals(5, kombinaciebi.getFirst().sigrdze());
    }

    @Test
    void poulobsXutsMiyolebitDaMereDanarchenSamsMiyolebit() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(YVAVI, RVA),
                new Karti(YVAVI, CXRA),
                new Karti(YVAVI, ATI),
                new Karti(YVAVI, VALETI),
                new Karti(YVAVI, DAMA),
                new Karti(YVAVI, KAROLI),
                new Karti(YVAVI, TUZI)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);

        assertEquals(2, kombinaciebi.size());

        assertEquals(100, kombinaciebi.getFirst().qula());
        assertEquals(5, kombinaciebi.getFirst().sigrdze());

        assertEquals(20, kombinaciebi.getLast().qula());
        assertEquals(3, kombinaciebi.getLast().sigrdze());

    }

    @Test
    void poulobsOrass() {
        daamateXelshi(
                new Karti(GULI, VALETI),
                new Karti(YVAVI, VALETI),
                new Karti(JVARI, VALETI),
                new Karti(WKENTI, VALETI)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);

        assertEquals(1, kombinaciebi.size());
        assertEquals(200, kombinaciebi.getFirst().qula());
        assertEquals(ERTNAIREBI, kombinaciebi.getFirst().tipi());

    }

    @Test
    void arPoulobsOtxShvidians() {
        daamateXelshi(
                new Karti(GULI, SHVIDI),
                new Karti(YVAVI, SHVIDI),
                new Karti(JVARI, SHVIDI),
                new Karti(WKENTI, SHVIDI)
        );
        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertTrue(kombinaciebi.isEmpty());
    }

    @Test
    void detectsOtxiMiyolebaDaOtxiErtnairi() {
        daamateXelshi(
                new Karti(GULI, TUZI),
                new Karti(YVAVI, TUZI),
                new Karti(JVARI, TUZI),
                new Karti(WKENTI, TUZI),
                new Karti(YVAVI, KAROLI),
                new Karti(YVAVI, DAMA),
                new Karti(YVAVI, VALETI),
                new Karti(YVAVI, ATI)

        );
        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertEquals(2, kombinaciebi.size());
        assertTrue(kombinaciebi.stream().anyMatch(k -> k.tipi().equals(MIYOLEBA)));
        assertTrue(kombinaciebi.stream().anyMatch(k -> {
            if (k.tipi().equals(MIYOLEBA)) {
                return k.sigrdze() == 4;
            }
            return false;
        }));
        assertTrue(kombinaciebi.stream().anyMatch(k -> k.tipi().equals(ERTNAIREBI)));
    }

    @Test
    void oriKartisMiyolebaArItvelba() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(YVAVI, RVA)
        );

        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertTrue(kombinaciebi.isEmpty());
    }


    @Test
    void rankisMiyolebaTuCvetiArEmtxvevaArUndaItvelbodes() {
        daamateXelshi(
                new Karti(YVAVI, SHVIDI),
                new Karti(WKENTI, RVA),
                new Karti(GULI, CXRA)
        );
        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertTrue(kombinaciebi.isEmpty());
    }

    @Test
    void miyolebaBelotRebelotitMaincItvelba() {
        daamateXelshi(
                new Karti(KOZIRI, TUZI),
                new Karti(KOZIRI, KAROLI),
                new Karti(KOZIRI, DAMA)
        );
        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertEquals(1, kombinaciebi.size());
        assertEquals(20, kombinaciebi.getFirst().qula());
    }

    @Test
    void kombinaciaTuaraaArUndaDaabrunosAraferi() {
        daamateXelshi(
                new Karti(KOZIRI, SHVIDI),
                new Karti(KOZIRI, CXRA),
                new Karti(KOZIRI, TUZI)
        );
        List<Kombinacia> kombinaciebi = amomcnobi.ipoveKombinaciebi(motamashe);
        assertTrue(kombinaciebi.isEmpty());
    }


}
