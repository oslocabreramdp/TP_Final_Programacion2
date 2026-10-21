package Modelo;

import Enums.Moneda;
import Enums.TipoOperacion;

public class Departamento extends Propiedad{

    private String numeroDpto;
    private int piso;
    private double montoExpensa;
    private boolean acensor;

    public Departamento(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, double precio, Moneda moneda, TipoOperacion tipoOperacion, int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal, double superficieDescubierta, double superficieCubierta, Cliente propietario, boolean aptoCredito, String numeroDpto, int piso, double montoExpensa, boolean acensor) {
        super(titulo, descripcion, direccionCalle, direccionNumero, localidad, precio, moneda, tipoOperacion, ambientes, dormitorios, balcon, banios, cochera, anioConstruccion, superficieTotal, superficieDescubierta, superficieCubierta, propietario, aptoCredito);
        this.numeroDpto = numeroDpto;
        this.piso = piso;
        this.montoExpensa = montoExpensa;
        this.acensor = acensor;
    }

    public String getNumeroDpto() {
        return numeroDpto;
    }

    public void setNumeroDpto(String numeroDpto) {
        this.numeroDpto = numeroDpto;
    }

    public int getPiso() {
        return piso;
    }

    public void setPiso(int piso) {
        this.piso = piso;
    }

    public double getMontoExpensa() {
        return montoExpensa;
    }

    public void setMontoExpensa(double montoExpensa) {
        this.montoExpensa = montoExpensa;
    }

    public boolean isAcensor() {
        return acensor;
    }

    public void setAcensor(boolean acensor) {
        this.acensor = acensor;
    }

    @Override
    public String toString() {
        return "Departamento{" +
                "numeroDpto='" + numeroDpto + '\'' +
                ", piso=" + piso +
                ", montoExpensa=" + montoExpensa +
                ", acensor=" + acensor +
                "} " + super.toString();
    }
}
