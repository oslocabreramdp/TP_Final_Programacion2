package Modelo;

import Enums.Disposicion;
import Enums.EstadoConservacion;
import Enums.TipoOperacion;


public class Chalet extends Propiedad{

    private  int cantPisos;
    private  boolean pileta;
    private boolean quincho;
    private boolean aptoCredito;
    private boolean jardin;

    public Chalet(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, int precio, TipoOperacion tipoOperacion, int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal, double superficieDescubierta, double superficieCubierta, Cliente propietario, EstadoConservacion estadoConservacion, int cantPisos, boolean pileta, boolean quincho, boolean aptoCredito, boolean jardin) {
        super(titulo, descripcion, direccionCalle, direccionNumero, localidad, precio, tipoOperacion, ambientes, dormitorios, balcon, banios, cochera, anioConstruccion, superficieTotal, superficieDescubierta, superficieCubierta, propietario, estadoConservacion);
        this.cantPisos = cantPisos;
        this.pileta = pileta;
        this.quincho = quincho;
        this.aptoCredito = aptoCredito;
        this.jardin = jardin;
    }

    public boolean isJardin() {
        return jardin;
    }

    public void setJardin(boolean jardin) {
        this.jardin = jardin;
    }

    public boolean isAptoCredito() {
        return aptoCredito;
    }

    public void setAptoCredito(boolean aptoCredito) {
        this.aptoCredito = aptoCredito;
    }

    public int getCantPisos() {
        return cantPisos;
    }

    public void setCantPisos(int cantPisos) {
        this.cantPisos = cantPisos;
    }

    public boolean isPileta() {
        return pileta;
    }

    public void setPileta(boolean pileta) {
        this.pileta = pileta;
    }

    public boolean isQuincho() {
        return quincho;
    }

    public void setQuincho(boolean quincho) {
        this.quincho = quincho;
    }

    @Override
    public double calcularPrecioFinal() {
        double precio_base_zona = calcularPrecioPorZona();
        // Estado de conservacion, antiguedad y ambientes (metodo comun en Propiedad)
        int porcentajeTotal = calcularPorcentajeComun();

        if (isJardin()) {
            porcentajeTotal += 3;
        }
        if (isPileta()) {
            porcentajeTotal += 5;
        }
        if (isQuincho()) {
            porcentajeTotal += 5;
        }
        if (isAptoCredito()) {
            porcentajeTotal += 5;
        }

        if (getCantPisos() > 1) {
            porcentajeTotal += (getCantPisos() - 1) * 3;
        }
        if (isCochera()) {
            porcentajeTotal += 5;
        }

        double precioFinal = precio_base_zona * (1 + porcentajeTotal / 100.0);

        // Mínimo 50.000 no puede haber un depto mas barato que eso
        if (precioFinal < 50000) {
            precioFinal = 50000;
        }

        return precioFinal;
    }

    @Override
    public String toString() {
        return "Chalet{" +
                "cantPisos=" + cantPisos +
                ", pileta=" + pileta +
                ", quincho=" + quincho + ", jardin=" + jardin +
                ", aptoCredito=" + aptoCredito +
                "} " + super.toString();
    }
}
