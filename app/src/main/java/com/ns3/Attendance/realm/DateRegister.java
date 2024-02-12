package com.ns3.Attendance.realm;


import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DateRegister extends RealmObject
{

    @PrimaryKey
    private int dateID;

    private Date dateToday;
    private int value;
    private RealmList<Student> studentPresent;

    public Date getDateToday() { return dateToday;}
    public void setDateToday(Date dateToday) { this.dateToday = dateToday; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public RealmList<Student> getStudentPresent() {return studentPresent;}
    public void setStudentPresent(RealmList<Student> studentPresent) {this.studentPresent = studentPresent;}

    public int getDateID() {return dateID;}
    public void setDateID(int dateID) {this.dateID = dateID;}

}
