import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import dao.PropiedadesDAO;
import dao.UsuariosDAO;
import enums.*;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import gestores.GestorPropiedades;
import modelo.*;
import utils.SecurityUtils;
import java.sql.SQLException;
public class Menu {

    private Scanner sc;
    private UsuariosDAO usuariosDAO = new UsuariosDAO();
    private PropiedadesDAO propiedadesDAO = new PropiedadesDAO();
    private GestorPropiedades gestor = new GestorPropiedades();
    public Menu() {
        this.sc = new  Scanner(System.in);
    }

    public void primerMenu() {
        cargarPropiedades();
        int op;
        do{
            System.out.println("-------- BIENBENDIO A CUCHUFLITO PROPIEDADES ----------");
            System.out.println("1. INICIAR SECION");
            System.out.println("2. REGISTRARSE");
            System.out.println("3. VER CATALOGO");
            System.out.println("0. SALIR");
            op = leerNumero();
            switch(op){
                case 1:
                    iniciarSecion();
                    break;
                case 2:
                    registrarse();
                    break;
                case 3:
                    verCatalogo();
                    break;
                case 0:
                    System.out.println("chau");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }while (op != 0);
    }




    public void iniciarSecion() {
        boolean aceptado = false;
        do {
            System.out.println("Introduza el mail (0 para volver)");
            String mail = sc.nextLine();
            if (mail.equals("0")) {
                return;
            }
            System.out.println("Introduci la clave");
            String clave = sc.nextLine();

            try {
                Usuario usuario = usuariosDAO.buscarPorEmail(mail);
                if (usuario == null) {
                    System.out.println("Usuario no encontrado");
                } else if (!usuario.isActivo()) {
                    System.out.println("El usuario esta dado de baja, hablá con un administrador");
                } else {
                    String claveHash = SecurityUtils.hashSHA256(clave);
                    if (claveHash.equals(usuario.getContraseniaHash())) {
                        aceptado = true;
                        System.out.println("Correctamete ingresado");
                        switch (usuario.getRol()) {
                            case ADMINISTRADOR:
                                menuAdmin(usuario);
                                break;
                            case PRODUCTOR_DE_VENTA:
                                menuProductor(usuario);
                                break;
                            case CLIENTE:
                                menuCliente(usuario);
                                break;
                        }
                    } else {
                        System.out.println("la contrasenia esta mal");
                    }
                }

            } catch (SQLException e) {
                System.out.println("Error con la base de datos: " + e.getMessage());
                return;
            }
        }while (!aceptado);
    }




    public void registrarse() {

    }

    //roles
    public void menuAdmin(Usuario usuario) {
        int op;
        do {
            System.out.println("HOLA ADMINISTRADOR " + usuario.getNombre());
            System.out.println("1. Listar todas las propiedades");
            System.out.println("2. Filtrar propiedades");
            System.out.println("3. Publicar una propiedad");
            System.out.println("4. Eliminar una propiedad");
            System.out.println("0. Cerrar sesion");
            op = leerNumero();

            switch(op){
                case 1:
                    mostrarPropiedades(gestor.listar());
                    break;
                case 2:
                    menuFiltros(false);
                    break;
                case 3:
                    // publica la propiedad (queda DISPONIBLE)
                    System.out.println("Id de la propiedad a publicar:");
                    int idPublicar = leerNumero();

                    try {
                        gestor.publicar(idPublicar);
                        System.out.println("Propiedad publicada");
                    } catch (ElementoNoEncontradoException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    System.out.println("Id de la propiedad a eliminar:");
                    int idEliminar = leerNumero();

                    System.out.println("Seguro? (s/n)");
                    String seguro = sc.nextLine();
                    if (seguro.equalsIgnoreCase("s")) {
                        try {
                            gestor.eliminarElemento(idEliminar);
                            System.out.println("Propiedad eliminada");
                        } catch (ElementoNoEncontradoException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 0:
                    System.out.println("chau");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }while (op != 0);

    }
    public void menuProductor(Usuario usuario) {
        int op;
        do {
            System.out.println("HOLA PRODUCTOR " + usuario.getNombre());
            System.out.println("1. Filtrar propiedades");
            System.out.println("2. Reservar una propiedad");
            System.out.println("3. Vender una propiedad");
            System.out.println("0. Cerrar sesion");
            op = leerNumero();
            switch(op){
                case 1:
                    //menu filtors
                    menuFiltros(false);
                    break;
                case 2:
                    //reservar
                    System.out.println("Id de la propiedad a reservar:");
                    int idReservar = leerNumero();
                    try {
                        gestor.reservar(idReservar);
                        System.out.println("Propiedad reservada");
                    } catch (ElementoNoEncontradoException | IllegalStateException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    // vender
                    System.out.println("Id de la propiedad a vender:");
                    int idVender = leerNumero();
                    try {
                        Propiedad vendida = gestor.buscarPorId(idVender);
                        gestor.vender(idVender);
                        // se le suma la venta al productor
                        if (usuario instanceof ProductorDeVenta) {
                            ProductorDeVenta productor = (ProductorDeVenta) usuario;
                            productor.setCantidadVentas(productor.getCantidadVentas() + 1);
                            productor.setMontoVentas(productor.getMontoVentas() + vendida.getPrecio());
                            usuariosDAO.actualizar(productor);
                        }
                        System.out.println("Propiedad vendida por USD " + vendida.getPrecio());
                    } catch (ElementoNoEncontradoException | IllegalStateException e) {
                        System.out.println(e.getMessage());
                    } catch (SQLException e) {
                        System.out.println("Error con la base de datos: " + e.getMessage());
                    }
                    break;
                case 0:
                    System.out.println("chau");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        }while (op != 0);

    }
    public void menuCliente(Usuario usuario) {
        int op;
        do {
            System.out.println("Hola Cliente " + usuario.getNombre());
            System.out.println("1. Ver propiedades disponibles");
            System.out.println("2. Filtrar propiedades");
            System.out.println("0. Cerrar sesion");
            op = leerNumero();
            switch (op) {
                case 1:
                    mostrarPropiedades(gestor.buscarDisponibles());
                    break;
                case 2:
                    // el cliente solo ve las disponibles
                    menuFiltros(true);
                    break;
                case 0:
                    System.out.println("Sesion cerrada.");
                    break;
                default:
                    System.out.println("Opcion invalida");
            }
        } while (op != 0);

    }
    public void verCatalogo() {
        System.out.println(" CATALOGO ");
        mostrarPropiedades(gestor.buscarDisponibles());
    }

    // menu de filtros, si soloDisponibles es true saca las que no estan disponibles
    public void menuFiltros(boolean soloDisponibles) {
        System.out.println("Filtrar por:");
        System.out.println("1. Zona");
        System.out.println("2. Tipo (departamento o chalet)");
        System.out.println("3. Cantidad de ambientes minima");
        System.out.println("4. Calle");
        System.out.println("0. Volver");
        int op = leerNumero();
        List<Propiedad> resultado;
        switch (op) {
            case 1:
                System.out.println("1. NORTE - 2. SUR - 3. CENTRO");
                int z = leerNumero();
                if (z == 1) {
                    resultado = gestor.buscarPorZona(Zona.NORTE);
                } else if (z == 2) {
                    resultado = gestor.buscarPorZona(Zona.SUR);
                } else if (z == 3) {
                    resultado = gestor.buscarPorZona(Zona.CENTRO);
                } else {
                    System.out.println("Opcion invalida");
                    return;
                }
                break;
            case 2:
                System.out.println("1. DEPARTAMENTO -  2. CHALET");
                int t = leerNumero();
                if (t == 1) {
                    resultado = gestor.buscarPorTipo(TipoPropiedad.DEPARTAMENTO);
                } else if (t == 2) {
                    resultado = gestor.buscarPorTipo(TipoPropiedad.CHALET);
                } else {
                    System.out.println("Opcion invalida");
                    return;
                }
                break;
            case 3:
                System.out.println("Cuantos ambientes como minimo?");
                int amb = leerNumero();
                resultado = gestor.buscarPorAmbientes(amb);
                break;
            case 4:
                System.out.println("Escribi la calle (o una parte):");
                String calle = sc.nextLine();
                resultado = gestor.buscarPorCalle(calle);
                break;
            case 0:
                return;
            default:
                System.out.println("Opcion invalida");
                return;
        }

        if (soloDisponibles) {
            List<Propiedad> disponibles = new ArrayList<>();
            for (Propiedad p : resultado) {
                if (p.getEstado() == EstadoPublicacion.DISPONIBLE) {
                    disponibles.add(p);
                }
            }
            resultado = disponibles;
        }
        mostrarPropiedades(resultado);
    }

    public void cargarPropiedades() {
        try {
            List<Propiedad> lista = propiedadesDAO.listarTodos();
            for (Propiedad p : lista) {
                gestor.agregarElemento(p);
            }
        } catch (SQLException e) {
            System.out.println("Error con la base de datos: " + e.getMessage());
        } catch (ElementoDuplicadoException e) {
            System.out.println(e.getMessage());
        }
    }

    public void mostrarPropiedades(List<Propiedad> lista) {
        if (lista.isEmpty()) {
            //aunque va a ver esto no se veria
            System.out.println("No hay propiedades cargadas");
            return;
        }
        for (Propiedad p : lista) {
            System.out.println("------------------------------");
            System.out.println(p);
        }
        System.out.println("------------------------------");
        System.out.println("Total: " + lista.size());
    }

    // pide un numero y no deja seguir hasta que escriban un numero
    private int leerNumero() {
        while (true) {
            String texto = sc.nextLine();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingreso una letra, por favor ingrese un numero:");
            }
        }
    }



    // carga una propiedad nueva (la pueden usar el administrador y el productor)
    // la propiedad nueva queda en estado TASACION, despues se publica con la opcion de publicar
    public void cargarPropiedad() {
        System.out.println("CARGAR PROPIEDAD");

        // 1) tipo de propiedad
        System.out.println("Que tipo de propiedad es?");
        System.out.println("1. DEPARTAMENTO");
        System.out.println("2. CHALET");
        int opcionTipo = leerNumeroEntre(1, 2);
        TipoPropiedad tipo;
        if (opcionTipo == 1) {
            tipo = TipoPropiedad.DEPARTAMENTO;
        } else {
            tipo = TipoPropiedad.CHALET;
        }

        // 2) zona
        System.out.println("En que zona esta?");
        System.out.println("1. NORTE");
        System.out.println("2. SUR");
        System.out.println("3. CENTRO");
        int opcionZona = leerNumeroEntre(1, 3);
        Zona zona;
        if (opcionZona == 1) {
            zona = Zona.NORTE;
        } else if (opcionZona == 2) {
            zona = Zona.SUR;
        } else {
            zona = Zona.CENTRO;
        }

        // 3) direccion
        System.out.println("Calle:");
        String calle = leerTextoNoVacio();

        System.out.println("Numero de la calle:");
        int numero = leerNumeroEntre(1, 99999);

        // 4) descripcion (esta puede quedar vacia)
        System.out.println("Descripcion (si no queres poner nada apreta enter):");
        String descripcion = sc.nextLine();
        if (descripcion.isBlank()) {
            descripcion = " ";
        }

        // 5) ambientes, dormitorios y banios
        System.out.println("Cantidad de ambientes (de 1 a 8):");
        int ambientes = leerNumeroEntre(1, 8);

        // no puede tener mas dormitorios que ambientes
        System.out.println("Cantidad de dormitorios (de 0 a " + ambientes + "):");
        int dormitorios = leerNumeroEntre(0, ambientes);

        System.out.println("Cantidad de banios (de 1 a 6):");
        int banios = leerNumeroEntre(1, 6);

        // 6) balcon y cochera
        System.out.println("Tiene balcon? (s/n)");
        boolean balcon = leerSiNo();

        System.out.println("Tiene cochera? (s/n)");
        boolean cochera = leerSiNo();

        // 7) anio de construccion (no puede ser del futuro)
        int anioActual = LocalDate.now().getYear();
        System.out.println("Anio de construccion (de 1900 a " + anioActual + "):");
        int anioConstruccion = leerNumeroEntre(1900, anioActual);

        // 8) estado de conservacion
        System.out.println("Estado de conservacion:");
        System.out.println("1. EXCELENTE");
        System.out.println("2. MUY BUENO");
        System.out.println("3. BUENO");
        System.out.println("4. A RECICLAR");
        System.out.println("5. NO SE SABE");
        int opcionEstado = leerNumeroEntre(1, 5);
        EstadoConservacion estadoConservacion;
        if (opcionEstado == 1) {
            estadoConservacion = EstadoConservacion.EXCELENTE;
        } else if (opcionEstado == 2) {
            estadoConservacion = EstadoConservacion.MUY_BUENO;
        } else if (opcionEstado == 3) {
            estadoConservacion = EstadoConservacion.BUENO;
        } else if (opcionEstado == 4) {
            estadoConservacion = EstadoConservacion.A_RECICLAR;
        } else {
            estadoConservacion = EstadoConservacion.NADA;
        }

        // 9) superficies (la cubierta tiene que ser mayor a 0 porque con eso se calcula el precio)
        System.out.println("Superficie cubierta en m2 (mayor a 0):");
        double superficieCubierta = leerDecimalMayorA(0);

        System.out.println("Terreno en m2 (si no tiene poner 0):");
        double terreno = leerDecimalMayorOIgualA(0);

        // 10) datos que dependen del tipo
        Propiedad nueva;
        if (tipo == TipoPropiedad.DEPARTAMENTO) {
            System.out.println("Numero o letra del departamento (ej: 4B):");
            String numeroDpto = leerTextoNoVacio();

            System.out.println("Piso (de 0 a 100, 0 es planta baja):");
            int piso = leerNumeroEntre(0, 100);

            System.out.println("Tiene ascensor? (s/n)");
            boolean ascensor = leerSiNo();

            System.out.println("Tiene vista al mar? (s/n)");
            boolean vistaAlMar = leerSiNo();

            System.out.println("Disposicion:");
            System.out.println("1. FRENTE");
            System.out.println("2. CONTRA FRENTE");
            System.out.println("3. LATERAL");
            System.out.println("4. INTERNO");
            System.out.println("5. NINGUNA");
            int opcionDisposicion = leerNumeroEntre(1, 5);
            Disposicion disposicion;
            if (opcionDisposicion == 1) {
                disposicion = Disposicion.FRENTE;
            } else if (opcionDisposicion == 2) {
                disposicion = Disposicion.CONTRA_FRENTE;
            } else if (opcionDisposicion == 3) {
                disposicion = Disposicion.LATERAL;
            } else if (opcionDisposicion == 4) {
                disposicion = Disposicion.INTERNO;
            } else {
                disposicion = Disposicion.NINGUNA;
            }

            // verifico que no este cargado el mismo departamento (misma calle, numero y depto)
            for (Propiedad p : gestor.listar()) {
                if (p instanceof Departamento) {
                    Departamento d = (Departamento) p;
                    if (d.getDireccionCalle().equalsIgnoreCase(calle)
                            && d.getDireccionNumero() == numero
                            && d.getNumeroDpto().equalsIgnoreCase(numeroDpto)) {
                        System.out.println("Ese departamento ya esta cargado (id " + d.getId() + "), no se cargo de nuevo");
                        return;
                    }
                }
            }

            nueva = new Departamento(tipo, descripcion, zona, calle, numero, balcon, ambientes, dormitorios, banios,
                    cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta,
                    numeroDpto, piso, ascensor, vistaAlMar, disposicion);

        } else {
            System.out.println("Cantidad de pisos (de 1 a 5):");
            int cantPisos = leerNumeroEntre(1, 5);

            System.out.println("Tiene pileta? (s/n)");
            boolean pileta = leerSiNo();

            System.out.println("Tiene quincho? (s/n)");
            boolean quincho = leerSiNo();

            System.out.println("Tiene jardin? (s/n)");
            boolean jardin = leerSiNo();

            // verifico que no este cargado el mismo chalet (misma calle y numero)
            for (Propiedad p : gestor.listar()) {
                if (p instanceof Chalet
                        && p.getDireccionCalle().equalsIgnoreCase(calle)
                        && p.getDireccionNumero() == numero) {
                    System.out.println("Ese chalet ya esta cargado (id " + p.getId() + "), no se cargo de nuevo");
                    return;
                }
            }

            nueva = new Chalet(tipo, descripcion, zona, calle, numero, balcon, ambientes, dormitorios, banios,
                    cochera, anioConstruccion, estadoConservacion, terreno, superficieCubierta,
                    cantPisos, pileta, quincho, jardin);
        }

        // 11) muestro como quedo y pregunto si la guardo
        System.out.println("------------------------------");
        System.out.println(nueva);
        System.out.println("------------------------------");
        System.out.println("Guardar la propiedad? (s/n)");
        boolean guardar = leerSiNo();
        if (!guardar) {
            System.out.println("No se guardo la propiedad");
            return;
        }

        try {
            gestor.agregarElemento(nueva);
            System.out.println("Propiedad cargada con id " + nueva.getId() + ". Queda en TASACION hasta que se publique.");
        } catch (ElementoDuplicadoException e) {
            System.out.println(e.getMessage());
        }
    }


    private int leerNumeroEntre(int minimo, int maximo) {
        int numero = leerNumero();
        while (numero < minimo || numero > maximo) {
            System.out.println("El numero tiene que estar entre " + minimo + " y " + maximo + ", proba de nuevo:");
            numero = leerNumero();
        }
        return numero;
    }

    private String leerTextoNoVacio() {
        String texto = sc.nextLine();
        while (texto.isBlank()) {
            System.out.println("No puede estar vacio, escribi algo:");
            texto = sc.nextLine();
        }
        return texto.trim();
    }

    private boolean leerSiNo() {
        String respuesta = sc.nextLine();
        while (!respuesta.equalsIgnoreCase("s") && !respuesta.equalsIgnoreCase("n")) {
            System.out.println("Escribi s (si) o n (no):");
            respuesta = sc.nextLine();
        }
        return respuesta.equalsIgnoreCase("s");
    }

    private double leerDecimal() {
        while (true) {
            String texto = sc.nextLine().replace(",", ".");
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingreso una letra, por favor ingrese un numero (ej: 45.5):");
            }
        }
    }

    private double leerDecimalMayorA(double minimo) {
        double numero = leerDecimal();
        while (numero <= minimo) {
            System.out.println("Tiene que ser mayor a " + minimo + ", proba de nuevo:");
            numero = leerDecimal();
        }
        return numero;
    }

    private double leerDecimalMayorOIgualA(double minimo) {
        double numero = leerDecimal();
        while (numero < minimo) {
            System.out.println("No puede ser menor a " + minimo + ", proba de nuevo:");
            numero = leerDecimal();
        }
        return numero;
    }

}
