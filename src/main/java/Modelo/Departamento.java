package Modelo;

import Enums.Disposicion;
import Enums.EstadoConservacion;
import Enums.TipoOperacion;

import java.time.LocalDate;

public class Departamento extends Propiedad{

    private String numeroDpto;
    private int piso;
    private int montoExpensa;
    private boolean ascensor;
    private boolean suite;
    private boolean vistaAlMar;
    private Disposicion disposicion;

    public Departamento(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, int precio, TipoOperacion tipoOperacion, int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal, double superficieDescubierta, double superficieCubierta, Cliente propietario, EstadoConservacion estadoConservacion, String numeroDpto, int piso, int montoExpensa, boolean acensor, boolean suite, boolean vistaAlMar, Disposicion disposicion) {
        super(titulo, descripcion, direccionCalle, direccionNumero, localidad, precio, tipoOperacion, ambientes, dormitorios, balcon, banios, cochera, anioConstruccion, superficieTotal, superficieDescubierta, superficieCubierta, propietario, estadoConservacion);
        this.numeroDpto = numeroDpto;
        this.piso = piso;
        this.montoExpensa = montoExpensa;
        this.ascensor = acensor;
        this.suite = suite;
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

    public int getMontoExpensa() {
        return montoExpensa;
    }

    public void setMontoExpensa(int montoExpensa) {
        this.montoExpensa = montoExpensa;
    }

    public boolean isAcensor() {
        return ascensor;
    }

    public void setAcensor(boolean acensor) {
        this.ascensor = acensor;
    }

    public boolean isSuite() {
        return suite;
    }

    public void setSuite(boolean suite) {
        this.suite = suite;
    }

    public boolean isVistaAlMar() {
        return vistaAlMar;
    }

    public void setVistaAlMar(boolean vistaAlMar) {
        this.vistaAlMar = vistaAlMar;
    }

    @Override
    public double calularPrecioFinal() {
        double precio_base_zona = calcularPrecioPorZona();
        int porcentajeTotal = 0;

        //viene del enum el numero
        porcentajeTotal += getEstadoConservacion().getPorcentaje();

       // Antigüedad reta 1% por año pasados los 10, tope -30. No aplica si es A_RECICLAR
        int antiguedad = LocalDate.now().getYear() - getAnioConstruccion();
        if (antiguedad > 10 && getEstadoConservacion() != EstadoConservacion.A_RECICLAR) {
            int descuento = antiguedad - 10;
            if (descuento > 30) {
                descuento = 30;
            }
            porcentajeTotal -= descuento;
        }

        // Ambientes por cada amb u 2%
        if (getAmbientes() > 1) {
            porcentajeTotal += (getAmbientes() - 1) * 2;
        }

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

        if (isSuite()) {
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

        // Mínimo 30.000 no puede haber un depto mas barato que eso
        if (precioFinal < 30000) {
            precioFinal = 30000;
        }

        return precioFinal;
    }


    @Override
    public String toString() {
        return "Departamento{" +
                "numeroDpto='" + numeroDpto + '\'' +
                ", piso=" + piso +
                ", montoExpensa=" + montoExpensa +
                ", acensor=" + ascensor +
                ", suite=" + suite +
                ", vistaAlMar=" + vistaAlMar +
                "} " + super.toString();
    }
}
