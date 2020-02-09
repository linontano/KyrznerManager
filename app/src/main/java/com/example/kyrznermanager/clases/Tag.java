package com.example.kyrznermanager.clases;

import androidx.annotation.NonNull;

public class Tag {
    String id;
    String seen;
    String ok;

    public Tag(String id, String seen) {
        this.id = id;
        this.seen = seen;
    }



    public Tag() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getOk() {
        return ok;
    }

    public void setOk(String ok) {
        this.ok = ok;
    }

    @NonNull
    @Override
    public String toString() {
        return this.getId();
    }
}
