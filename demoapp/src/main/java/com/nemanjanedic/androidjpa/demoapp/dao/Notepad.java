package com.nemanjanedic.androidjpa.demoapp.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "notepad")
public class Notepad {

    private Long id;

    private String name;

    private Long dateCreated;

    private Long dateModified;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @Column(name = "name", length = 250, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Notepad)) {
            return false;
        }
        if (id != null) {
            return id.equals(((Notepad) o).id);
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }
}
