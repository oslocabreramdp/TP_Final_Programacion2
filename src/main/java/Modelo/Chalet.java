package Modelo;

import Enums.Moneda;
import Enums.TipoOperacion;

public class Chalet extends Propiedad{

    private  int cantPisos;
    private  boolean pileta;
    private boolean quincho;

    public Chalet(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, double precio, Moneda moneda, TipoOperacion tipoOperacion, int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal, double superficieDescubierta, double superficieCubierta, Cliente propietario, boolean aptoCredito, int cantPisos, boolean pileta, boolean quincho) {
        super(titulo, descripcion, direccionCalle, direccionNumero, localidad, precio, moneda, tipoOperacion, ambientes, dormitorios, balcon, banios, cochera, anioConstruccion, superficieTotal, superficieDescubierta, superficieCubierta, propietario, aptoCredito);
        this.cantPisos = cantPisos;
        this.pileta = pileta;
        this.quincho = quincho;
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
    public String toString() {
        return "Chalet{" +
                "cantPisos=" + cantPisos +
                ", pileta=" + pileta +
                ", quincho=" + quincho +
                "} " + super.toString();
    }
}
