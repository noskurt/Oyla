package com.ygznsl.noskurt.oyla.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public final class Vote implements Serializable {

    private static final Locale locale = new Locale("tr", "TR");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", locale);

    private int u, o;
    private String vd;

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public int getO() {
        return o;
    }

    public void setO(int o) {
        this.o = o;
    }

    public String getVd() {
        return vd;
    }

    public void setVd(String vd) {
        this.vd = vd;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        final Vote vote = (Vote) o1;
        return u == vote.u && o == vote.o;
    }

    @Override
    public int hashCode() {
        int result = u;
        result = 31 * result + o;
        return result;
    }

    @Override
    public String toString() {
        return String.format(locale, "Vote{u=%s, o=%s, vd=%s}", u, o, vd);
    }

}
