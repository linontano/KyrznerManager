package com.example.kyrznermanager.clases;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Proovedor implements Parcelable {
    public String idProovedor;
    public String descripcion;

    public Proovedor(String idProovedor, String descripcion) {
        this.idProovedor = idProovedor;
        this.descripcion = descripcion;
    }

    protected Proovedor(Parcel in) {
        idProovedor = in.readString();
        descripcion = in.readString();
    }

    public static final Creator<Proovedor> CREATOR = new Creator<Proovedor>() {
        @Override
        public Proovedor createFromParcel(Parcel in) {
            return new Proovedor(in);
        }

        @Override
        public Proovedor[] newArray(int size) {
            return new Proovedor[size];
        }
    };

    public String getIdProovedor() {
        return idProovedor;
    }

    public void setIdProovedor(String idProovedor) {
        this.idProovedor = idProovedor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Proovedor() {
    }

    @NonNull
    @Override
    public String toString() {
        return this.descripcion.trim()+" - "+this.idProovedor.trim() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idProovedor);
        dest.writeString(descripcion);
    }
}
