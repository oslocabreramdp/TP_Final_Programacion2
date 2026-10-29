package Modelo;

import Enums.TipoUsuario;

import java.time.LocalDate;

public class ProductorDeVenta extends Usuario {

    private String cuil;
    private int cantidadVentas;
    private double montoVentas;

    public ProductorDeVenta(int idUsuario, String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String cuil, int cantidadVentas, double montoVentas) {
        super(idUsuario, TipoUsuario.PRODUCTOR_DE_VENTA, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.cuil = cuil;
        this.cantidadVentas = cantidadVentas;
        this.montoVentas = montoVentas;
    }

    public ProductorDeVenta(String nombre, String apellido, String telefono, String email, String contraseniaHash, boolean activo, LocalDate fechaAlta, String cuil, int cantidadVentas, double montoVentas) {
        super(TipoUsuario.PRODUCTOR_DE_VENTA, nombre, apellido, telefono, email, contraseniaHash, activo, fechaAlta);
        this.cuil = cuil;
        this.cantidadVentas = cantidadVentas;
        this.montoVentas = montoVentas;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public double getMontoVentas() {
        return montoVentas;
    }

    public void setMontoVentas(double montoVentas) {
        this.montoVentas = montoVentas;
    }

    @Override
    public String toString() {
        return "ProductorDeVenta{" +
                "cuil='" + cuil + '\'' +
                ", cantidadVentas=" + cantidadVentas +
                ", montoVentas=" + montoVentas +
                "} " + super.toString();
    }
}
