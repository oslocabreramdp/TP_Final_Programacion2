package Modelo;
import Enums.TipoUsuario;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Usuario {



    private int idUsuario;
    private TipoUsuario rol;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String contraseniaHash;
    private boolean activo;
    private LocalDate fechaAlta;

    public Usuario(int idUsuario, TipoUsuario rol, String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.contraseniaHash = contraseniaHash;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
    }

    public Usuario(TipoUsuario rol, String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta) {
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.contraseniaHash = contraseniaHash;
        this.activo = activo;
        this.fechaAlta = fechaAlta;
    }

    public TipoUsuario getRol() {
        return rol;
    }

    public void setRol(TipoUsuario rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseniaHash() {
        return contraseniaHash;
    }

    public void setContraseniaHash(String contraseniaHash) {
        this.contraseniaHash = contraseniaHash;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDate fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getId() {
        return idUsuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", rol=" + rol +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", contraseniaHash='" + contraseniaHash + '\'' +
                ", activo=" + activo +
                ", fechaAlta=" + fechaAlta +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Usuario usuario)) return false;
        return Objects.equals(email, usuario.email);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(email);
    }


}

