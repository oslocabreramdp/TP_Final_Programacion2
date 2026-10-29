package Modelo;
import Enums.EstadoConservacion;
import Enums.TipoPropiedad;
import Enums.Zona;

public class Chalet extends Propiedad{

    private  int cantPisos;
    private  boolean pileta;
    private boolean quincho;
    private boolean jardin;


    public Chalet(Zona zona, int direccionNumero, String direccionCalle, TipoPropiedad tipoPropiedad) {
        super(zona, direccionNumero, direccionCalle, tipoPropiedad);
        this.cantPisos = 1;
        this.pileta = false;
        this.quincho = false;
        this.jardin = false;
    }

    public Chalet(TipoPropiedad tipoPropiedad, String descripcion, Zona zona, String direccionCalle, int direccionNumero,
                  boolean balcon, int ambientes, int banios, boolean cochera, int anioConstruccion,
                  EstadoConservacion estadoConservacion, double terreno, double superficieCubierta, int cantPisos,
                  boolean pileta, boolean quincho, boolean jardin) {
        super(tipoPropiedad, descripcion, zona, direccionCalle, direccionNumero, balcon, ambientes, banios, cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta);
        this.cantPisos = cantPisos;
        this.pileta = pileta;
        this.quincho = quincho;
        this.jardin = jardin;
    }

    public boolean isJardin() {
        return jardin;
    }

    public void setJardin(boolean jardin) {
        this.jardin = jardin;
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

        if (getCantPisos() > 1) {
            porcentajeTotal += (getCantPisos() - 1) * 3;
        }
        if (isCochera()) {
            porcentajeTotal += 5;
        }

        double precioFinal = precio_base_zona * (1 + porcentajeTotal / 100.0);

        return precioFinal;
    }

    @Override
    public String toString() {
        return "Chalet{" +
                "cantPisos=" + cantPisos +
                ", pileta=" + pileta +
                ", quincho=" + quincho + ", jardin=" + jardin +
                "} " + super.toString();
    }
}
