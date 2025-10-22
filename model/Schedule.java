package com.schooladmin.model;


public class Schedule {
    private int scheduleId;
    private String className;  // e.g., "Class 10"
    private String section;   // e.g., "A"
    private String subject;
    private int teacherId;    // FK to Employee (Teacher)
    private String dayOfWeek; // e.g., "Monday"
    private String timeSlot;  // e.g., "9:00-10:00 AM"
    private String roomNo;
    private int principalId;  // FK to Employee (Principal)

    // Default constructor
    public Schedule() {}

    // Parameterized constructor
    public Schedule(int scheduleId, String className, String section, String subject, int teacherId,
                    String dayOfWeek, String timeSlot, String roomNo, int principalId) {
        this.scheduleId = scheduleId;
        this.className = className;
        this.section = section;
        this.subject = subject;
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        this.roomNo = roomNo;
        this.principalId = principalId;
    }

    // Getters and Setters
    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public int getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(int principalId) {
        this.principalId = principalId;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "scheduleId=" + scheduleId +
                ", className='" + className + '\'' +
                ", section='" + section + '\'' +
                ", subject='" + subject + '\'' +
                ", teacherId=" + teacherId +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                ", timeSlot='" + timeSlot + '\'' +
                ", roomNo='" + roomNo + '\'' +
                ", principalId=" + principalId +
                '}';
    }
}
