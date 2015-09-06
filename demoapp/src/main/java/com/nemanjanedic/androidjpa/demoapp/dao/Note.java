package com.nemanjanedic.androidjpa.demoapp.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "notes")
public class Note {

    private Long id;

    private String title;

    private String content;

    private Long dateCreated;

    private Long dateModified;

    private Notepad notepad;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Column(name = "title", length = 100, nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Column(name = "content", length = 500, nullable = true)
    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Column(name = "created", nullable = false)
    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Column(name = "modified", nullable = false)
    public Long getDateModified() {
        return dateModified;
    }

    public void setDateModified(final Long dateModified) {
        this.dateModified = dateModified;
    }

    @JoinColumn(name = "notepad_id")
    @ManyToOne(optional = false)
    public Notepad getNotepad() {
        return notepad;
    }

    public void setNotepad(final Notepad notepad) {
        this.notepad = notepad;
    }

}
