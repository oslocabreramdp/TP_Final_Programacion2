package Modelo;

import Enums.TipoUsuario;
import Interfaces.IGestorPropiedades;
import Interfaces.IGestorUsuarios;
import Utils.SecurityUtils;

import java.util.List;

public class Administrador extends Usuario implements IGestorUsuarios, IGestorPropiedades {

    private int legajo;

    public Administrador(TipoUsuario tipoUsuario, String nombre, String apellido, String telefono, String email, String contraseniaHash, int legajo) {
        super(tipoUsuario, nombre, apellido, telefono, email, contraseniaHash);
        this.legajo = legajo;
    }

    @Override
    public void cambiarContrasenia(String nuevaContrasenia) {
        String newpass=SecurityUtils.hashSHA256(nuevaContrasenia);
        setContraseniaHash(newpass);
    }

    @Override
    public void aprobarProductorDeVenta(Usuario productorDeVenta) {
        productorDeVenta.setActivo(true);

    }

    @Override
    public void cambiarEstadoUsuario(Usuario usuario, boolean nuevoEstado) {

        usuario.setActivo(nuevoEstado);

    }

    @Override
    public void actualizarDatosUsuario(Usuario usuarioDatosActualizados) {

    }

    @Override
    public List<Usuario> listarProductoresPendientes() {
        return List.of();
    }

    @Override
    public List<Usuario> listarUsuarios() {

        return List.of();
    }
}
