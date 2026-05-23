package com.github.konstantinevashalomidze.belotegame.tamashi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.konstantinevashalomidze.belotegame.tamashi.Cveti.*;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.ERTNAIREBI;
import static com.github.konstantinevashalomidze.belotegame.tamashi.KombinaciisTipi.MIYOLEBA;
import static com.github.konstantinevashalomidze.belotegame.tamashi.Ranki.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KombinaciisShemdarebeliTesti {

    private static final Cveti KOZIRI = GULI;
    private KombinaciisShemdarebeli shemdarebeli;


    @BeforeEach
    void moawyve() {
        shemdarebeli = new KombinaciisShemdarebeli(KOZIRI);
    }

    @Test
    void grdzeliMiyolebaMokleMiyolebasUgebs() {
        Kombinacia a = new Kombinacia(
                MIYOLEBA,
                20,
                WKENTI,
                TUZI,
                3,
                new Motamashe(0)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                50,
                JVARI,
                TUZI,
                4,
                new Motamashe(1)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));
    }

    @Test
    void ertiDaigiveSigrdzisDaRankisKoziriUgebsAraKozirs() {
        Kombinacia a = new Kombinacia(
                MIYOLEBA,
                20,
                WKENTI,
                TUZI,
                3,
                new Motamashe(0)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                20,
                KOZIRI,
                TUZI,
                3,
                new Motamashe(1)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));
    }


    @Test
    void ertiDaIgiveSigrdzisMaghaliKartiUgebsKozirisas() {
        Kombinacia a = new Kombinacia(
                MIYOLEBA,
                20,
                KOZIRI,
                KAROLI,
                3,
                new Motamashe(0)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                20,
                WKENTI,
                TUZI,
                3,
                new Motamashe(1)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));

    }


    @Test
    void ertiDaIgiveSigrdzisDaRankisDaArcertiAraaKoziriPoziciisMixedvitPirveliMotamashisGadis() {
        Kombinacia a = new Kombinacia(
                MIYOLEBA,
                20,
                JVARI,
                KAROLI,
                3,
                new Motamashe(1)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                20,
                WKENTI,
                KAROLI,
                3,
                new Motamashe(0)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));

    }

    @Test
    void maghaliQulaIgebs() {
        Kombinacia a = new Kombinacia(
                ERTNAIREBI,
                100,
                null,
                TUZI,
                4,
                new Motamashe(0)
        );

        Kombinacia b = new Kombinacia(
                ERTNAIREBI,
                150,
                null,
                CXRA,
                4,
                new Motamashe(1)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));

    }

    @Test
    void koziriXutiMiyolebaUgebsOtxErtnairs() {
        Kombinacia a = new Kombinacia(
                ERTNAIREBI,
                100,
                null,
                TUZI,
                4,
                new Motamashe(0)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                100,
                KOZIRI,
                TUZI,
                5,
                new Motamashe(1)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));

    }


    @Test
    void xutiMiyolebaOtxiErtnairiPoziciitPirveliIgebs() {
        Kombinacia a = new Kombinacia(
                ERTNAIREBI,
                100,
                null,
                TUZI,
                4,
                new Motamashe(1)
        );

        Kombinacia b = new Kombinacia(
                MIYOLEBA,
                100,
                JVARI,
                TUZI,
                5,
                new Motamashe(0)

        );

        assertFalse(shemdarebeli.metia(a, b));
        assertTrue(shemdarebeli.metia(b, a));

    }


}
