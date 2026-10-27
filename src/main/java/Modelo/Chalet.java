package Modelo;

import Enums.EstadoConservacion;
import Enums.TipoOperacion;

public class Chalet extends Propiedad{

    private  int cantPisos;
    private  boolean pileta;
    private boolean quincho;
    private boolean aptoCredito;

    public Chalet(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, int precio, TipoOperacion tipoOperacion, int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal, double superficieDescubierta, double superficieCubierta, Cliente propietario, boolean aptoCredito, EstadoConservacion estadoConservacion, int cantPisos, boolean pileta, boolean quincho) {
        super(titulo, descripcion, direccionCalle, direccionNumero, localidad, precio, tipoOperacion, ambientes, dormitorios, balcon, banios, cochera, anioConstruccion, superficieTotal, superficieDescubierta, superficieCubierta, propietario, estadoConservacion);
        this.cantPisos = cantPisos;
        this.pileta = pileta;
        this.quincho = quincho;
        this.aptoCredito = aptoCredito;
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
    public double calularPrecioFinal() {
        return 0;
    }

    @Override
    public String toString() {
        return "Chalet{" +
                "cantPisos=" + cantPisos +
                ", pileta=" + pileta +
                ", quincho=" + quincho +
                ", aptoCredito=" + aptoCredito +
                "} " + super.toString();
    }
}
