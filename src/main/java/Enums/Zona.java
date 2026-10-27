package Enums;

public enum Zona {

    NORTE(  1200), SUR(1400), CENTRO(1500);

    private final int metros_cubiertos_zona;

    Zona(int metros_cubiertos_zona) {
        this.metros_cubiertos_zona = metros_cubiertos_zona;
    }

    public int getMetros_cubiertos_zona() {
        return metros_cubiertos_zona;
    }
}
