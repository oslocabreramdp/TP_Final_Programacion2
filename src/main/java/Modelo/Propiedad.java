package Modelo;
import Enums.EstadoConservacion;
import Enums.EstadoPublicacion;
import Enums.TipoOperacion;
import Enums.TipoPropiedad;
import Enums.Zona;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Propiedad {
    private int id;
    private TipoPropiedad tipoPropiedad;
    private static int contador = 1;
    private String titulo;
    private String descripcion;
    private Zona zona;
    private String direccionCalle;
    private int direccionNumero;
    private String localidad;
    private int precio;
    private boolean balcon;
    private TipoOperacion tipoOperacion;
    private int ambientes;
    private int dormitorios;
    private int banios;
    private boolean cochera;
    private int anioConstruccion;
    private LocalDate fechaPublicacion;
    private Cliente propietario;
    private EstadoPublicacion estado;
    private EstadoConservacion estadoConservacion;
    private double superficieTotal;
    private double superficieDescubierta;
    private double superficieCubierta;
    private boolean ocupacion;

    //constructor
    public Propiedad(String titulo, String descripcion, String direccionCalle, int direccionNumero, String localidad, int precio, TipoOperacion tipoOperacion,
                     int ambientes, int dormitorios, boolean balcon, int banios, boolean cochera, int anioConstruccion, double superficieTotal,double superficieDescubierta , double superficieCubierta, Cliente propietario, EstadoConservacion estadoConservacion) {
        this.id = contador ++;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.direccionCalle = direccionCalle;
        this.direccionNumero = direccionNumero;
        this.localidad = localidad;
        this.precio = precio;
        this.balcon = balcon;
        this.tipoOperacion = tipoOperacion;
        this.ambientes = ambientes;
        this.dormitorios = dormitorios;
        this.banios = banios;
        this.cochera = cochera;
        this.anioConstruccion = anioConstruccion;
        this.superficieTotal = superficieTotal;
        this.superficieDescubierta = superficieDescubierta;
        this.superficieCubierta= superficieCubierta;
        this.propietario = propietario;
        this.estadoConservacion = estadoConservacion;
        this.fechaPublicacion = LocalDate.now();
        this.estado = EstadoPublicacion.TASACION;
        this.ocupacion = false;
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

    public String getTitulo() {
        return titulo;
    }

    public boolean isBalcon() {
        return balcon;
    }

    public void setBalcon(boolean balcon) {
        this.balcon = balcon;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
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

    public double getSuperficieDescubierta() {
        return superficieDescubierta;
    }

    public void setSuperficieDescubierta(double superficieDescubierta) {
        this.superficieDescubierta = superficieDescubierta;
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

    public Cliente getPropietario() {
        return propietario;
    }

    public void setPropietario(Cliente propietario) {
        this.propietario = propietario;
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

    public double getSuperficieTotal() {
        return superficieTotal;
    }

    public void setSuperficieTotal(double superficieTotal) {
        this.superficieTotal = superficieTotal;
    }

    public boolean isOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(boolean ocupacion) {
        this.ocupacion = ocupacion;
    }

    public double calcularPrecioPorZona(){
        double precio_base_zona;
        precio_base_zona = getZona().getMetros_cubiertos_zona() * getSuperficieCubierta();
        return  precio_base_zona;
    }

    public abstract double calularPrecioFinal();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Propiedad propiedad = (Propiedad) o;
        return direccionNumero == propiedad.direccionNumero && Objects.equals(direccionCalle, propiedad.direccionCalle) && Objects.equals(propietario, propiedad.propietario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccionCalle, direccionNumero, propietario);
    }

    @Override
    public String toString() {
        return "Propiedad{" +
                "id='" + id + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", direccionCalle='" + direccionCalle + '\'' +
                ", direccionNumero=" + direccionNumero +
                ", localidad='" + localidad + '\'' +
                ", precio=USD " + precio +
                ", balcon=" + balcon +
                ", tipoOperacion=" + tipoOperacion +
                ", ambientes=" + ambientes +
                ", dormitorios=" + dormitorios +
                ", banios=" + banios +
                ", cochera=" + cochera +
                ", anioConstruccion=" + anioConstruccion +
                ", fechaPublicacion=" + fechaPublicacion +
                ", propietario=" + propietario +
                ", estado=" + estado +
                ", estadoConservacion=" + estadoConservacion +
                ", superficieTotal=" + superficieTotal +
                ", superficieDescubierta=" + superficieDescubierta +
                ", superficieCubierta=" + superficieCubierta +
                ", ocupacion=" + ocupacion +
                '}';
    }
}
