package com.ygznsl.noskurt.oyla.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Vote implements Serializable {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("tr", "TR"));

    private int u, o;
    private String vd;

    public Date getVoteDate() throws ParseException {
        return DATE_FORMAT.parse(vd);
    }

    public void setVoteDate(Date voteDate){
        vd = DATE_FORMAT.format(voteDate);
    }

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

        Vote vote = (Vote) o1;

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
        return "Vote{" +
                "u=" + u +
                ", o=" + o +
                ", vd='" + vd + '\'' +
                '}';
    }

}
