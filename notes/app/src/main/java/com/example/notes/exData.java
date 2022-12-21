package com.example.notes;

//@author Will Brant
//WARNING: DEPRECATED FILE
public class exData {
    public String name;
    public String cate;
    public String date;
    public String amot;
    public String note;

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getCate() {return cate;}
    public void setCate(String cate) {this.cate = cate;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getAmot() {return amot;}
    public void setAmot(String amot) {this.amot = amot;}

    public String getNote() {return note;}
    public void setNote(String note) {this.note = note;}

    exData(String name, String cate, String date, String amot, String note) {
        this.name = name;
        this.cate = cate;
        this.date = date;
        this.amot = amot;
        this.note = note;
    }

}
