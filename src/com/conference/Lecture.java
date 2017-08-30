package com.conference;

import java.sql.Date;

public class Lecture {
    private String lectureId;
    private String title;
    private String room;
    private Date date;

    public Lecture(String lectureId, String title, String room, Date date) {
        this.lectureId = lectureId;
        this.title = title;
        this.room = room;
        this.date = date;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
