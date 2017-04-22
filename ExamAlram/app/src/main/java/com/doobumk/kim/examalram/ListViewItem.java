package com.doobumk.kim.examalram;

/**
 * Created by User on 2017-04-20.
 */

public class ListViewItem {
    private String subject;
    private String time;
    private String percent;
    private String date;

    public ListViewItem(String time, String percent, String date) {
        this.time = time;
        this.percent = percent;
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public String getTime() {
        return time;
    }

    public String getPercent() {
        return percent;
    }

    public String getDate() {
        return date;
    }
}
