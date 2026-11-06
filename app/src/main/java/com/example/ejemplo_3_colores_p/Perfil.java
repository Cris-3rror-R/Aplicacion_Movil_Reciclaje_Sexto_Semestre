package com.example.ejemplo_3_colores_p;

public class Perfil {
    String Nombre;
    String Apellidos;
    String Genero;
    String Telefono;
    String Nacimiento;
    String Boletin;
    String Notificacion;
    String Recomendaciones;
    String Usuario;
    String Foto;

    public Perfil(){

    }


    public Perfil(String nombre, String apellidos, String genero, String telefono, String nacimiento, String boletin, String notificacion, String recomendaciones, String usuario, String foto) {
        Nombre = nombre;
        Apellidos = apellidos;
        Genero = genero;
        Telefono = telefono;
        Nacimiento = nacimiento;
        Boletin = boletin;
        Notificacion = notificacion;
        Recomendaciones = recomendaciones;
        Usuario = usuario;
        Foto = foto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public String getGenero() {
        return Genero;
    }

    public void setGenero(String genero) {
        Genero = genero;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getNacimiento() {
        return Nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        Nacimiento = nacimiento;
    }

    public String getBoletin() {
        return Boletin;
    }

    public void setBoletin(String boletin) {
        Boletin = boletin;
    }

    public String getNotificacion() {
        return Notificacion;
    }

    public void setNotificacion(String notificacion) {
        Notificacion = notificacion;
    }

    public String getRecomendaciones() {
        return Recomendaciones;
    }

    public void setRecomendaciones(String recomendaciones) {
        Recomendaciones = recomendaciones;
    }

    public String getUsuario() {
        return Usuario;
    }

    public void setUsuario(String usuario) {
        Usuario = usuario;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

}
