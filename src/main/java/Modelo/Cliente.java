package Modelo;

import Enums.TipoUsuario;

import java.time.LocalDate;

public class Cliente extends Usuario {

    String dni;

    public Cliente(int idUsuario, String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String dni) {
        super(idUsuario, TipoUsuario.CLIENTE, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.dni = dni;

    }

    public Cliente(String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String dni) {
        super(TipoUsuario.CLIENTE, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.dni = dni;
    }

    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "dni='" + dni + '\'' +
                "} " + super.toString();
    }
}
