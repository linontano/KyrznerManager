package com.example.kyrznermanager.clases;

import java.io.Serializable;
import java.util.Date;
@SuppressWarnings("serial")
public class Usuario implements Serializable {
    private String id_empleado;
    private String user;
    private String nombres;
    private String apellidos;
    private String tipo_id;
    private String id;
    private String ecivil;
    private Date nacimiento;
    private String [] contactos = new String [3];
    private Date contrato;
    private String estado;
    private String app;
    private Integer rol;


    public Usuario(String id_empleado, String user, String nombres, String apellidos, String tipo_id, String id, String ecivil, Date nacimiento, String[] contactos, Date contrato, String estado, String app, Integer rol) {
        this.id_empleado = id_empleado;
        this.user = user;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.tipo_id = tipo_id;
        this.id = id;
        this.ecivil = ecivil;
        this.nacimiento = nacimiento;
        this.contactos=contactos;
        this.contrato = contrato;
        this.estado = estado;
        this.app = app;
        this.rol = rol;
    }

    public String getId_empleado() {
        return id_empleado;
    }

    public String getUser() {
        return user;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getTipo_id() {
        return tipo_id;
    }

    public String getId() {
        return id;
    }

    public String getEcivil() {
        return ecivil;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public String[] getContactos() {
        return contactos;
    }

    public Date getContrato() {
        return contrato;
    }

    public String getEstado() {
        return estado;
    }

    public String getApp() {
        return app;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setEcivil(String ecivil) {
        this.ecivil = ecivil;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Integer getRol() {
        return rol;
    }

    public void setRol(Integer rol) {
        this.rol = rol;
    }
}
