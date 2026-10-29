package Modelo;

import Enums.Disposicion;
import Enums.EstadoConservacion;
import Enums.TipoPropiedad;
import Enums.Zona;


public class Departamento extends Propiedad{

    private String numeroDpto;
    private int piso;
    private boolean ascensor;
    private boolean vistaAlMar;
    private Disposicion disposicion;


    public Departamento(Zona zona, int direccionNumero, String direccionCalle, TipoPropiedad tipoPropiedad) {
        super(zona, direccionNumero, direccionCalle, tipoPropiedad);
        this.numeroDpto = "";
        this.piso = 0;
        this.ascensor = false;
        this.vistaAlMar = false;
        this.disposicion = Disposicion.NINGUNA;
    }

    public Departamento(TipoPropiedad tipoPropiedad, String descripcion, Zona zona, String direccionCalle, int direccionNumero, boolean balcon, int ambientes, int banios, boolean cochera, int anioConstruccion,
                        EstadoConservacion estadoConservacion, double terreno, double superficieCubierta, String numeroDpto, int piso, int montoExpensa, boolean ascensor, boolean suite, boolean vistaAlMar, Disposicion disposicion) {
        super(tipoPropiedad, descripcion, zona, direccionCalle, direccionNumero, balcon, ambientes, banios, cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta);
        this.numeroDpto = numeroDpto;
        this.piso = piso;
        this.ascensor = ascensor;
        this.vistaAlMar = vistaAlMar;
        this.disposicion = disposicion;
    }

    public Disposicion getDisposicion() {
        return disposicion;
    }

    public void setDisposicion(Disposicion disposicion) {
        this.disposicion = disposicion;
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

    public boolean isAcensor() {
        return ascensor;
    }

    public void setAcensor(boolean acensor) {
        this.ascensor = acensor;
    }

    public boolean isVistaAlMar() {
        return vistaAlMar;
    }

    public void setVistaAlMar(boolean vistaAlMar) {
        this.vistaAlMar = vistaAlMar;
    }

    @Override
    public double calcularPrecioFinal() {
        double precio_base_zona = calcularPrecioPorZona();
        // Estado de conservacion, antiguedad y ambientes (metodo comun en Propiedad)
        int porcentajeTotal = calcularPorcentajeComun();
        if (isCochera()) {
            porcentajeTotal += 10;
        }
        if (isAcensor()) {
            porcentajeTotal += 5;
        } else {
            porcentajeTotal -= 5;
        }
        if (isBalcon()) {
            porcentajeTotal += 3;
        }
        if (isVistaAlMar()) {
            porcentajeTotal += 10;
        }
        if (disposicion == Disposicion.FRENTE) {
            porcentajeTotal += 5;
        } else if (disposicion == Disposicion.INTERNO) {
            porcentajeTotal -= 5;
        }
        double precioFinal = precio_base_zona * (1 + porcentajeTotal / 100.0);

        return precioFinal;
    }


    @Override
    public String toString() {
        return "Departamento{" +
                "numeroDpto='" + numeroDpto + '\'' +
                ", piso=" + piso +
                ", acensor=" + ascensor +
                ", vistaAlMar=" + vistaAlMar +
                "} " + super.toString();
    }
}
