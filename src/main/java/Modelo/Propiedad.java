package Modelo;
import Enums.EstadoConservacion;
import Enums.EstadoPublicacion;
import Enums.TipoPropiedad;
import Enums.Zona;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Propiedad {
    private int id;
    private TipoPropiedad tipoPropiedad;
    private String descripcion;
    private Zona zona;
    private String direccionCalle;
    private int direccionNumero;
    private int precio;
    private boolean balcon;
    private int ambientes;
    private int dormitorios;
    private int banios;
    private boolean cochera;
    private int anioConstruccion;
    private LocalDate fechaPublicacion;
    private EstadoPublicacion estado;
    private EstadoConservacion estadoConservacion;
    private double terreno;
    private double superficieCubierta;

    //constructores
    public Propiedad(Zona zona, int direccionNumero, String direccionCalle, TipoPropiedad tipoPropiedad) {

        this.tipoPropiedad = tipoPropiedad;
        this.descripcion = " ";
        this.zona = zona;
        this.direccionCalle = direccionCalle;
        this.direccionNumero = direccionNumero;
        this.balcon = false;
        this.ambientes = 1;
        this.banios = 1;
        this.cochera = false;
        this.anioConstruccion = 0;
        this.fechaPublicacion = LocalDate.now();
        this.estado = EstadoPublicacion.TASACION;
        this.estadoConservacion = EstadoConservacion.NADA;
        this.terreno = 0;
        this.superficieCubierta = 0;
        this.precio = (int) calcularPrecioFinal();
    }

    public Propiedad(TipoPropiedad tipoPropiedad, String descripcion, Zona zona, String direccionCalle, int direccionNumero, boolean balcon, int ambientes, int banios,
                     boolean cochera, int anioConstruccion, EstadoConservacion estadoConservacion, double terreno, double superficieCubierta) {
        this.id = id;
        this.tipoPropiedad = tipoPropiedad;
        this.descripcion = descripcion;
        this.zona = zona;
        this.direccionCalle = direccionCalle;
        this.direccionNumero = direccionNumero;
        this.balcon = balcon;
        this.ambientes = ambientes;
        this.banios = banios;
        this.cochera = cochera;
        this.anioConstruccion = anioConstruccion;
        this.fechaPublicacion = LocalDate.now();
        this.estado = EstadoPublicacion.TASACION;
        this.estadoConservacion = estadoConservacion;
        this.terreno = terreno;
        this.superficieCubierta = superficieCubierta;
        this.precio = (int) calcularPrecioFinal();
    }


    public int getId() {
        return id;
    }

    public TipoPropiedad getTipoPropiedad() {
        return tipoPropiedad;
    }

    public void setTipoPropiedad(TipoPropiedad tipoPropiedad) {
        this.tipoPropiedad = tipoPropiedad;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public boolean isBalcon() {
        return balcon;
    }

    public void setBalcon(boolean balcon) {
        this.balcon = balcon;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccionCalle() {
        return direccionCalle;
    }

    public void setDireccionCalle(String direccionCalle) {
        this.direccionCalle = direccionCalle;
    }

    public int getDireccionNumero() {
        return direccionNumero;
    }

    public void setDireccionNumero(int direccionNumero) {
        this.direccionNumero = direccionNumero;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public int getAmbientes() {
        return ambientes;
    }

    public void setAmbientes(int ambientes) {
        this.ambientes = ambientes;
    }

    public int getDormitorios() {
        return dormitorios;
    }


    public double getSuperficieCubierta() {
        return superficieCubierta;
    }

    public void setSuperficieCubierta(double superficieCubierta) {
        this.superficieCubierta = superficieCubierta;
    }

    public void setDormitorios(int dormitorios) {
        this.dormitorios = dormitorios;
    }

    public int getBanios() {
        return banios;
    }

    public void setBanios(int banios) {
        this.banios = banios;
    }

    public boolean isCochera() {
        return cochera;
    }

    public void setCochera(boolean cochera) {
        this.cochera = cochera;
    }

    public int getAnioConstruccion() {
        return anioConstruccion;
    }

    public void setAnioConstruccion(int anioConstruccion) {
        this.anioConstruccion = anioConstruccion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }


    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public EstadoConservacion getEstadoConservacion() {
        return estadoConservacion;
    }

    public void setEstadoConservacion(EstadoConservacion estadoConservacion) {
        this.estadoConservacion = estadoConservacion;
    }

    public double getTerreno() {
        return terreno;
    }

    public void setTerreno(double terreno) {
        this.terreno = terreno;
    }

    public double calcularPrecioPorZona() {
        double precio_base_zona;
        precio_base_zona = getZona().getMetros_cubiertos_zona() * getSuperficieCubierta();
        return precio_base_zona;
    }

    // Porcentaje que comparten todas las propiedades:
    // estado de conservacion, antiguedad y ambientes.
    protected int calcularPorcentajeComun() {
        int porcentaje = 0;

        //viene del enum el numero
        porcentaje += getEstadoConservacion().getPorcentaje();

        // Antigüedad resta 1% por año pasados los 10, tope -30. No aplica si es A_RECICLAR
        int antiguedad = LocalDate.now().getYear() - getAnioConstruccion();
        if (antiguedad > 10 && getEstadoConservacion() != EstadoConservacion.A_RECICLAR) {
            int descuento = antiguedad - 10;
            if (descuento > 30) {
                descuento = 30;
            }
            porcentaje -= descuento;
        }

        // Ambientes 2% por cada ambiente despues del primero
        if (getAmbientes() > 1) {
            porcentaje += (getAmbientes() - 1) * 2;
        }
        return porcentaje;
    }

    public abstract double calcularPrecioFinal();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Propiedad propiedad = (Propiedad) o;
        return direccionNumero == propiedad.direccionNumero && Objects.equals(direccionCalle, propiedad.direccionCalle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccionCalle, direccionNumero);
    }

    @Override
    public String toString() {
        return "Propiedad{" +
                "id=" + id +
                ", tipoPropiedad=" + tipoPropiedad +
                ", descripcion='" + descripcion + '\'' +
                ", zona=" + zona +
                ", direccionCalle='" + direccionCalle + '\'' +
                ", direccionNumero=" + direccionNumero +
                ", precio=" + precio +
                ", balcon=" + balcon +
                ", ambientes=" + ambientes +
                ", dormitorios=" + dormitorios +
                ", banios=" + banios +
                ", cochera=" + cochera +
                ", anioConstruccion=" + anioConstruccion +
                ", fechaPublicacion=" + fechaPublicacion +
                ", estado=" + estado +
                ", estadoConservacion=" + estadoConservacion +
                ", terreno=" + terreno +
                ", superficieCubierta=" + superficieCubierta +
                '}';
    }
}
