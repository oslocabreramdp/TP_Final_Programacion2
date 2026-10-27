package Enums;


public enum EstadoConservacion {
    EXCELENTE(15), MUY_BUENO(0), BUENO(-5), A_RECICLAR(-10);

    private final int porcentaje;

    EstadoConservacion(int porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getPorcentaje() {
        return porcentaje;
    }
}
