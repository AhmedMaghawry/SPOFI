package com.ezzat.spofi.Model;

import java.io.Serializable;

public class Report implements Serializable {

    private String ownerName;
    private int ownerRate;
    private Location location;
    private String date;
    private String hour;
    private String comment;
    private String contentUrl;
    private ReportType type;
    private String reportId;
    private String state;
    private int score;

    public Report() {
        state = ReportState.Pending.toString();
        score = 0;
    }

    public Report(String ownerName, int ownerRate, Location location, String date, String hour, String contentUrl, ReportType type) {
        this.ownerName = ownerName;
        this.ownerRate = ownerRate;
        this.location = location;
        this.date = date;
        this.hour = hour;
        this.contentUrl = contentUrl;
        this.type = type;
        state = ReportState.Pending.toString();
        score = 0;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getOwnerRate() {
        return ownerRate;
    }

    public void setOwnerRate(int ownerRate) {
        this.ownerRate = ownerRate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public ReportState getState() {
        return ReportState.valueOf(state);
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
