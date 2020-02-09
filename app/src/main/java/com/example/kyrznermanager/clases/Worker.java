package com.example.kyrznermanager.clases;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Worker implements Parcelable {
    private String user;
    private String nombre_completo;
    private String id_empleado;

    public Worker(String user, String nombre_completo, String id_empleado) {
        this.setUser(user);
        this.setNombre_completo(nombre_completo);
        this.setId_empleado(id_empleado);
    }

    protected Worker(Parcel in) {
        user = in.readString();
        nombre_completo = in.readString();
        id_empleado = in.readString();
    }

    public static final Creator<Worker> CREATOR = new Creator<Worker>() {
        @Override
        public Worker createFromParcel(Parcel in) {
            return new Worker(in);
        }

        @Override
        public Worker[] newArray(int size) {
            return new Worker[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(String id_empleado) {
        this.id_empleado = id_empleado;
    }

    @NonNull
    @Override
    public String toString() {
        return this.nombre_completo+" ("+this.getUser()+")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(nombre_completo);
        dest.writeString(id_empleado);
    }
}
