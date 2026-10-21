package Modelo;
import Enums.TipoUsuario;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Usuario {

    private static int contador=1;

    private int idUsuario;
    private TipoUsuario rol;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String contraseniaHash;
    private boolean activo;
    private LocalDate fechaAlta;

    public Usuario(TipoUsuario tipoUsuario, String nombre, String apellido, String telefono, String email, String contraseniaHash) {
        this.idUsuario=contador++;
        this.rol = tipoUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.contraseniaHash = contraseniaHash;
        this.activo = true;
        this.fechaAlta = LocalDate.now();
    };

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

    public int getIdUsuario() {
        return idUsuario;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", tipoUsuario=" + rol +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
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

    public abstract void cambiarContrasenia(String nuevaContrasenia);





}

