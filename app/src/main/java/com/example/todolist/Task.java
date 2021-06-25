package com.example.todolist;

public class Task {
    public String title;
    public String content;
    public String startDate;
    public String endDate;
    public String key;

    public Task(){};

    public Task(String title,String content,String startDate,String endDate)
    {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.key = key;
    }
}
