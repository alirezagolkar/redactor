package com.redactor.redactor.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created by NP on 4/2/2019.
 */
public class View {

    private String name;
    private String content;
    private LocalDateTime date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        View view = (View) o;

        if (!name.equals(view.name)) return false;
        if (!content.equals(view.content)) return false;
        return date.equals(view.date);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }
}
