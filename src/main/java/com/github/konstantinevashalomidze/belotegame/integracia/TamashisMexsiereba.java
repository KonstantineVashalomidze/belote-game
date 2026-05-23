package com.github.konstantinevashalomidze.belotegame.integracia;

import com.github.konstantinevashalomidze.belotegame.tamashi.DastisMomwodebeli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.konstantinevashalomidze.belotegame.integracia.TamashisSesiisStatusi.LODINI;

@Component
public class TamashisMexsiereba {
    private final Map<String, TamashisSesia> sesiebi = new ConcurrentHashMap<>();
    private final Map<String, String> zedmetsaxeliDaOtaxisId = new ConcurrentHashMap<>();
    private final DastisMomwodebeli dastisMomwodebeli;

    public TamashisMexsiereba(DastisMomwodebeli dastisMomwodebeli) {
        this.dastisMomwodebeli = dastisMomwodebeli;
    }

    public TamashisSesia sheqmeniTamashi(String zedmetsaxeli) {
        sheamowmeSworiZedmetsaxeliTuaq(zedmetsaxeli);
        if (saxeliGamoyenebulia(zedmetsaxeli)) {
            throw new IllegalArgumentException("სახელი უკვე გამოყენებულია");
        }

        String otaxisId = sheqmeniOtaxisId();
        TamashisSesia sesia = new TamashisSesia(otaxisId);
        sesia.daimate(zedmetsaxeli, "A");
        sesiebi.put(otaxisId, sesia);
        zedmetsaxeliDaOtaxisId.put(zedmetsaxeli, otaxisId);
        return sesia;
    }

    public TamashisSesia shediOtaxshi(String otaxisId, String zedmetsaxeli, String gudni) {
        sheamowmeSworiZedmetsaxeliTuaq(zedmetsaxeli);
        if (saxeliGamoyenebulia(zedmetsaxeli)) {
            throw new IllegalArgumentException("სახელი უკვე გამოყენებულია");
        }
        TamashisSesia sesia = sesia(otaxisId);
        if (sesia.statusi() != LODINI) {
            throw new IllegalStateException("თამაში უკვე დაწყებულია");
        }

        sesia.daimate(zedmetsaxeli, gudni);
        zedmetsaxeliDaOtaxisId.put(zedmetsaxeli, otaxisId);
        if (sesia.mzadaa()) {
            sesia.daiwyeTamashi(dastisMomwodebeli);
        }
        return sesia;
    }

    public TamashisSesia daabruneSesiaZedmetsaxelisMixedvit(String zedmetsaxeli) {
        String otaxisId = zedmetsaxeliDaOtaxisId.get(zedmetsaxeli);
        if (otaxisId == null) {
            throw new IllegalArgumentException("ეგეთი სახელით არვინ არ არის");
        }
        return sesiebi.get(otaxisId);
    }


    public TamashisSesia sesia(String otaxisId) {
        TamashisSesia sesia = sesiebi.get(otaxisId);
        if (sesia == null) {
            throw new IllegalArgumentException("თამაში ვერ ვიპოვეთ მაგ კოდით");
        }

        return sesia;
    }

    private String sheqmeniOtaxisId() {
        Random shemtxvevitoba = new Random();
        String id;
        do {
            id = String.format("%04d", shemtxvevitoba.nextInt(10000));
        } while (sesiebi.containsKey(id));
        return id;
    }

    private boolean saxeliGamoyenebulia(String zedmetsaxeli) {
        return zedmetsaxeliDaOtaxisId.containsKey(zedmetsaxeli);
    }

    private void sheamowmeSworiZedmetsaxeliTuaq(String zedmetsaxeli) {
        if (zedmetsaxeli == null || zedmetsaxeli.isBlank()) {
            throw new IllegalArgumentException("სახელი ცარიელი არ უნდა იყვეს");
        }

        if (!zedmetsaxeli.matches("[a-z]+")) {
            throw new IllegalArgumentException("სახელი უნდა იყოს ინგლისური ასოები, მოხოლოდ და მხოლოდ დაბლები");
        }

        if (zedmetsaxeli.length() > 30) {
            throw new IllegalArgumentException("გააჯვი რა, რაამბავი სახელი გქვიებია");
        }
    }



}
