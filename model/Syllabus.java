package com.schooladmin.model;

import java.util.Date;

public class Syllabus {
    private int syllabusId;
    private String className;  // e.g., "Class 10"
    private String subject;
    private String topic;
    private int chapterNo;
    private int teacherId;  // FK to Employee (Teacher)
    private Date lastUpdated;

    // Default constructor
    public Syllabus() {}

    // Parameterized constructor
    public Syllabus(int syllabusId, String className, String subject, String topic, int chapterNo,
                   int teacherId, Date lastUpdated) {
        this.syllabusId = syllabusId;
        this.className = className;
        this.subject = subject;
        this.topic = topic;
        this.chapterNo = chapterNo;
        this.teacherId = teacherId;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(int syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getChapterNo() {
        return chapterNo;
    }

    public void setChapterNo(int chapterNo) {
        this.chapterNo = chapterNo;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public java.sql.Date getLastUpdated() {
        return (java.sql.Date) lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return "Syllabus{" +
                "syllabusId=" + syllabusId +
                ", className='" + className + '\'' +
                ", subject='" + subject + '\'' +
                ", topic='" + topic + '\'' +
                ", chapterNo=" + chapterNo +
                ", teacherId=" + teacherId +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}

