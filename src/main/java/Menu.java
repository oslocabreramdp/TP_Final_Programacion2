import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import dao.PropiedadesDAO;
import dao.UsuariosDAO;
import enums.EstadoPublicacion;
import enums.TipoPropiedad;
import enums.Zona;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import gestores.GestorPropiedades;
import modelo.ProductorDeVenta;
import modelo.Propiedad;
import modelo.Usuario;
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
}
