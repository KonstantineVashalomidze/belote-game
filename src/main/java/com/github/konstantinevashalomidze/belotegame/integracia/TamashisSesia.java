package com.github.konstantinevashalomidze.belotegame.integracia;

import com.github.konstantinevashalomidze.belotegame.tamashi.DastisMomwodebeli;
import com.github.konstantinevashalomidze.belotegame.tamashi.Motamashe;
import com.github.konstantinevashalomidze.belotegame.tamashi.Tamashi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.LODINI;
import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.TAMASHI;

public class TamashisSesia {
    private final String otaxisId;
    private final Map<String, Integer> zedmetsaxeliDaMotamashisPozicia;
    private final Map<String, String> zedmetsaxeliDaRomelGundshia; // "A" an "B"
    private Tamashi tamashi;
    private TamashisSesiisStatusi statusi = LODINI;

    public TamashisSesia(String otaxisId) {
        this.otaxisId = otaxisId;
        zedmetsaxeliDaMotamashisPozicia = new HashMap<>();
        zedmetsaxeliDaRomelGundshia = new HashMap<>();
    }

    public String otaxisId() {
        return otaxisId;
    }

    public Tamashi tamashi() {
        return tamashi;
    }

    public TamashisSesiisStatusi statusi() {
        return statusi;
    }

    public void statusi(TamashisSesiisStatusi statusi) {
        this.statusi = statusi;
    }


    public Map<String, Integer> zedmetsaxeliDaMotamashisPozicia() {
        return zedmetsaxeliDaMotamashisPozicia;
    }


    public boolean zedmetsaxeliGamoyenebulia(String zedmetsaxeli) {
        return zedmetsaxeliDaMotamashisPozicia.containsKey(zedmetsaxeli);
    }

    public boolean gundiSavsea(String gundi) {
        return zedmetsaxeliDaRomelGundshia.values().stream()
                .filter(e -> e.equals(gundi))
                .count() >= 2;
    }

    public boolean mzadaa() {
        return zedmetsaxeliDaMotamashisPozicia.size() == 4;
    }

    public void daimate(String zedmetsaxeli, String gundi) {
        if (gundiSavsea(gundi)) {
            throw new IllegalStateException("გუნდი სავსა");
        }
        zedmetsaxeliDaRomelGundshia.put(zedmetsaxeli, gundi);
    }


    public void daiwyeTamashi(DastisMomwodebeli dastisMomwodebeli) {
        if (!mzadaa()) {
            throw new IllegalStateException("ოთხივე მოთამაშე მზად უნდა იყვეს");
        }

        List<String> gundiA = zedmetsaxeliDaRomelGundshia.entrySet().stream()
                .filter(e -> "A".equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> gundiB = zedmetsaxeliDaRomelGundshia.entrySet().stream()
                .filter(e -> "B".equals(e.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Collections.shuffle(gundiA);
        Collections.shuffle(gundiB);

        Motamashe m0 = new Motamashe(0);
        Motamashe m1 = new Motamashe(1);
        Motamashe m2 = new Motamashe(2);
        Motamashe m3 = new Motamashe(3);

        zedmetsaxeliDaMotamashisPozicia.put(gundiA.getFirst(), 0);
        zedmetsaxeliDaMotamashisPozicia.put(gundiB.getFirst(), 1);
        zedmetsaxeliDaMotamashisPozicia.put(gundiA.getLast(), 2);
        zedmetsaxeliDaMotamashisPozicia.put(gundiB.getLast(), 3);

        int kartisDamrigebeli = (int) (Math.random() * 4);

        tamashi = new Tamashi(m0, m1, m2, m3, dastisMomwodebeli);
        statusi = TAMASHI;
        tamashi.daiwyeRaundi();

    }

    public Motamashe motamashisPozicia(String zedmetsaxeli) {
        Integer pozicia = zedmetsaxeliDaMotamashisPozicia.get(zedmetsaxeli);
        if (pozicia == null) {
            throw new IllegalArgumentException("მასეთი ზედმეტსახელით არავინაა");
        }

        return tamashi.motamasheebi().get(pozicia);
    }

}
