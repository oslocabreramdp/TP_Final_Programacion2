package Modelo;

import Enums.TipoUsuario;

import java.time.LocalDate;

public class Administrador extends Usuario {

    private String legajo;

    public Administrador(int idUsuario, String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String legajo) {
        super(idUsuario, TipoUsuario.ADMINISTRADOR, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.legajo = legajo;
    }

    public Administrador(String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String legajo) {
        super(TipoUsuario.ADMINISTRADOR, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.legajo = legajo;
    }

    public String getLegajo() {
        return legajo;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "legajo='" + legajo + '\'' +
                "} " + super.toString();
    }
}
