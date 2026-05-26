package com.github.konstantinevashalomidze.belotegame.tamashi;

import java.util.List;

import static com.github.konstantinevashalomidze.belotegame.tamashi.Ranki.VALETI;

public class KozirobisMmartveli {

    private final List<Motamashe> motamasheebi;
    private final KozirobisMdgomareoba kozirobisMdgomareoba;

    public KozirobisMmartveli(List<Motamashe> motamasheebi, KozirobisMdgomareoba kozirobisMdgomareoba) {
        this.motamasheebi = motamasheebi;
        this.kozirobisMdgomareoba = kozirobisMdgomareoba;
    }


    public void sheamowmeAmotrialebuliKartiValetze() {
        Karti amotrialebuliKarti = kozirobisMdgomareoba.amotrialebuliKarti();
        if (amotrialebuliKarti.ranki() == VALETI) {
            Motamashe pirveliMotamashe = motamasheebi.get((kozirobisMdgomareoba.kartisDamrigebelisPozicia() + 1) % 4);
            kozirobisMdgomareoba.ikozira(pirveliMotamashe, amotrialebuliKarti.cveti());
        }
    }


    public void motamashemTqvaPasi(Motamashe motamashe) {
        davushvitRomSityvisTqmisUflebaaqvs(motamashe);


        if (kozirobisMdgomareoba.kozirobisFaza() == KozirobisFaza.PIRVELI_KRUGI
                && kozirobisMdgomareoba.sityvaVinujdenzea()) {
            kozirobisMdgomareoba.shemdegiKrugiDaiwyo();
        } else {
            kozirobisMdgomareoba.sityvaShemdegMotamashezeGadavida();
        }
    }

    public void ikozira(Motamashe motamashe) {
        davushvitRomSityvisTqmisUflebaaqvs(motamashe);
        Cveti cveti = kozirobisMdgomareoba.amotrialebuliKarti().cveti();
        kozirobisMdgomareoba.ikozira(motamashe, cveti);
    }

    public void ikozira(Motamashe motamashe, Cveti cveti) {
        davushvitRomSityvisTqmisUflebaaqvs(motamashe);

        if (cveti == kozirobisMdgomareoba.amotrialebuliKarti().cveti()) {
            throw new IllegalArgumentException("წინა ხელში უნდა გეკოზირა თუ გინდოდა მაგი.");
        }
        kozirobisMdgomareoba.ikozira(motamashe, cveti);
    }


    public void vinujdenmaIkozira(Cveti cveti) {
        Motamashe vinujdeni = motamasheebi.get(kozirobisMdgomareoba.kartisDamrigebelisPozicia());
        if (cveti == kozirobisMdgomareoba.amotrialebuliKarti().cveti()) {
            throw new IllegalArgumentException("თუ გინდოდა წინა კრუგზე წაგეღო კვერცხო.");
        }
        kozirobisMdgomareoba.ikozira(vinujdeni, cveti);
    }

    private void davushvitRomSityvisTqmisUflebaaqvs(Motamashe motamashe) {
        if (motamasheebi.indexOf(motamashe) != kozirobisMdgomareoba.mimdinareMotamashisPozicia()) {
            throw new IllegalStateException("შენი ჯერი არაა ბლიად, რა მოტყანი ტვინი.");
        }
    }


}
