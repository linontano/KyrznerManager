package com.example.kyrznermanager.clases;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Lote implements Serializable {
    public String estado;
    public String id_proveedor;
    public String id_lote;
    public Integer etapa;
    Integer cantidad;
    public Date fecha_ingreso;
    public Date fecha_salida;
    String descripcionProov;
    Integer cantidadPelado;

    public Lote(String estado, String id_proveedor, String id_lote, Integer etapa, Date fecha_ingreso, Date fecha_salida) {
        this.estado = estado;
        this.id_proveedor = id_proveedor;
        this.id_lote = id_lote;
        this.etapa = etapa;
        this.fecha_ingreso = fecha_ingreso;
        this.fecha_salida = fecha_salida;
    }

    public Lote(String estado, String id_proveedor, String id_lote, Integer etapa, Date fecha_ingreso, Date fecha_salida, String descripcionProov, Integer cantidad) {
        this.estado = estado;
        this.id_proveedor = id_proveedor;
        this.id_lote = id_lote;
        this.etapa = etapa;
        this.fecha_ingreso = fecha_ingreso;
        this.fecha_salida = fecha_salida;
        this.descripcionProov = descripcionProov;
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getId_proveedor() {
        return id_proveedor;
    }

    public void setId_proveedor(String id_proveedor) {
        this.id_proveedor = id_proveedor;
    }

    public String getId_lote() {
        return id_lote;
    }

    public void setId_lote(String id_lote) {
        this.id_lote = id_lote;
    }

    public Integer getEtapa() {
        return etapa;
    }

    public void setEtapa(Integer etapa) {
        this.etapa = etapa;
    }

    public Date getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(Date fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public Date getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(Date fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcionProov() {
        return descripcionProov;
    }

    public void setDescripcionProov(String descripcionProov) {
        this.descripcionProov = descripcionProov;
    }

    public Integer getCantidadPelado() {
        return cantidadPelado;
    }

    public void setCantidadPelado(Integer cantidadPelado) {
        this.cantidadPelado = cantidadPelado;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id_lote +" ("+this.descripcionProov+")";
    }
}
