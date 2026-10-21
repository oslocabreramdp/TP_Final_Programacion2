package Interfaces;

import Modelo.Usuario;

import java.util.List;

public interface IGestorUsuarios {
    void aprobarProductorDeVenta(Usuario productorDeVenta);
    void cambiarEstadoUsuario(Usuario usuario, boolean nuevoEstado);
    void actualizarDatosUsuario(Usuario usuarioDatosActualizados);
    List<Usuario> listarProductoresPendientes();
    List<Usuario> listarUsuarios();
}
