import dao.UsuariosDAO;
import enums.*;
import excepciones.ContraseniaInvalidaException;
import excepciones.ElementoDuplicadoException;
import excepciones.ElementoNoEncontradoException;
import gestores.GestorPropiedades;
import gestores.GestorUsuarios;
import modelo.*;
import utils.SecurityUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {

    private final Scanner sc;
    private final GestorPropiedades gestorPropiedades;
    private final GestorUsuarios gestorUsuarios;
    // Opcional si querés rankings desde el menú sin tocar GestorUsuarios:
    private final UsuariosDAO usuariosDAO;

    public Menu(GestorPropiedades gestorPropiedades, GestorUsuarios gestorUsuarios) {
        this.sc = new Scanner(System.in);
        this.gestorPropiedades = gestorPropiedades;
        this.gestorUsuarios = gestorUsuarios;
        this.usuariosDAO = new UsuariosDAO();
    }

    public Menu() {
        this.sc = new Scanner(System.in);
        this.gestorPropiedades = new GestorPropiedades();
        this.gestorUsuarios = new GestorUsuarios(this.gestorPropiedades);
        this.usuariosDAO = new UsuariosDAO();
    }

    public void primerMenu() {
        cargarPropiedadesIniciales();
        int op;
        do {
            System.out.println("\n-------- BIENVENIDO A CUCHUFLITO PROPIEDADES --------");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Ver catálogo (disponibles)");
            System.out.println("0. Salir");
            op = leerNumero();

            switch (op) {
                case 1 -> iniciarSesion();
                case 2 -> registrarse();
                case 3 -> mostrarPropiedades(gestorPropiedades.buscarDisponibles());
                case 0 -> System.out.println("¡Chau!");
                default -> System.out.println("Opción inválida.");
            }
        } while (op != 0);
    }

    public void iniciarSesion() {
        System.out.println("Ingrese el email (0 para volver):");
        String mail = leerTextoNoVacio();
        if (mail.equals("0")) return;

        System.out.println("Ingrese la contraseña:");
        String clave = sc.nextLine();

        try {
            Usuario usuario = gestorUsuarios.buscarPorMail(mail);
            if (!usuario.isActivo()) {
                System.out.println("El usuario está inactivo o pendiente de aprobación. Contacte a un administrador.");
                return;
            }
            String hashIngresado = SecurityUtils.hashSHA256(clave);
            if (!hashIngresado.equals(usuario.getContraseniaHash())) {
                System.out.println("Contraseña incorrecta.");
                return;
            }
            System.out.println("Ingreso correcto. ¡Bienvenido/a " + usuario.getNombre() + "!");
            menuSesion(usuario);
        } catch (ElementoNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    public void registrarse() {
        System.out.println("\n--- REGISTRO DE USUARIO ---");
        System.out.println("1. Cliente");
        System.out.println("2. Productor de venta (requiere aprobación de un administrador)");
        int tipoOp = leerNumeroEntre(1, 2);

        System.out.println("Nombre:");
        String nombre = leerTextoNoVacio();
        System.out.println("Apellido:");
        String apellido = leerTextoNoVacio();
        System.out.println("Teléfono:");
        String telefono = leerTextoNoVacio();
        String email = leerEmailValido();

        String claveHash = null;
        while (claveHash == null) {
            System.out.println("Contraseña (más de 8 caracteres, al menos 1 número y 1 mayúscula):");
            String clave = sc.nextLine();
            try {
                validarContrasenia(clave);
                claveHash = SecurityUtils.hashSHA256(clave);
            } catch (ContraseniaInvalidaException e) {
                System.out.println("Contraseña inválida: " + e.getMessage());
            }
        }

        LocalDate hoy = LocalDate.now();
        Usuario nuevo;

        if (tipoOp == 1) {
            String dni = leerDniValido();
            nuevo = new Cliente(nombre, apellido, telefono, email, claveHash, true, hoy, dni);
        } else {
            String cuil = leerCuilValido();
            nuevo = new ProductorDeVenta(nombre, apellido, telefono, email, claveHash, false, hoy, cuil, 0, 0.0);
        }

        try {
            usuariosDAO.insertar(nuevo);
            gestorUsuarios.agregarElemento(nuevo);
            System.out.println("Registro completado con éxito.");
        } catch (SQLException e) {
            System.out.println("Error al guardar usuario en la base de datos: " + e.getMessage());
        } catch (ElementoDuplicadoException e) {
            System.out.println(e.getMessage());
        }
    }

    // Menú unificado: muestra acciones según el rol del usuario autenticado
    public void menuSesion(Usuario usuario) {
        boolean esAdmin = usuario instanceof Administrador;
        boolean esProductor = usuario instanceof ProductorDeVenta;
        boolean esCliente = usuario instanceof Cliente;

        int op;
        do {
            System.out.println("\n=== SESIÓN ACTIVA: " + usuario.getNombre() + " (" + usuario.getRol() + ") ===");
            System.out.println("1. Filtrar propiedades");

            if (esCliente) {
                System.out.println("2. Ver propiedades disponibles");
            }
            if (esProductor || esAdmin) {
                System.out.println("3. Cargar nueva propiedad (queda en TASACIÓN)");
            }
            if (esProductor) {
                System.out.println("4. Reservar propiedad");
            }
            if (esAdmin) {
                System.out.println("5. Registrar venta (asignar a productor)");
                System.out.println("6. Listar todas las propiedades");
                System.out.println("7. Publicar propiedad (pasar a DISPONIBLE)");
                System.out.println("8. Eliminar propiedad");
                System.out.println("9. Gestionar usuarios (listar / activar-desactivar cliente / aprobar productor)");
                System.out.println("10. Ranking de productores de venta");
                System.out.println("11. Filtrar propiedades por estado");
            }
            System.out.println("0. Cerrar sesión");
            op = leerNumero();

            if (op == 0) {
                System.out.println("Sesión cerrada.");
                return;
            }
            if (op == 1) {
                menuFiltros(esCliente);
            } else if (op == 2 && esCliente) {
                mostrarPropiedades(gestorPropiedades.buscarDisponibles());
            } else if (op == 3 && (esProductor || esAdmin)) {
                cargarPropiedad();
            } else if (op == 4 && esProductor) {
                reservarPropiedad();
            } else if (op == 5 && esAdmin) {
                registrarVenta();
            } else if (op == 6 && esAdmin) {
                mostrarPropiedades(gestorPropiedades.listar());
            } else if (op == 7 && esAdmin) {
                publicarPropiedad();
            } else if (op == 8 && esAdmin) {
                eliminarPropiedad();
            } else if (op == 9 && esAdmin) {
                menuGestionUsuarios((Administrador) usuario);
            } else if (op == 10 && esAdmin) {
                menuRankingProductores();
            } else if (op == 11 && esAdmin) {
                filtrarPropiedadesPorEstado();
            } else {
                System.out.println("Opción no válida para tu rol.");
            }
        } while (true);
    }

    private void publicarPropiedad() {
        System.out.println("ID de la propiedad a publicar:");
        int id = leerNumero();
        try {
            gestorPropiedades.publicar(id);
            System.out.println("Propiedad publicada (estado DISPONIBLE).");
        } catch (ElementoNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private void eliminarPropiedad() {
        System.out.println("ID de la propiedad a eliminar:");
        int id = leerNumero();
        if (!leerSiNoConfirmacion("¿Seguro que desea eliminar la propiedad ID " + id + "?")) {
            System.out.println("Operación cancelada.");
            return;
        }
        try {
            gestorPropiedades.eliminarElemento(id);
            System.out.println("Propiedad eliminada.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println(e.getMessage());
        }
    }

    private void reservarPropiedad() {
        System.out.println("ID de la propiedad a reservar:");
        int id = leerNumero();
        try {
            gestorPropiedades.reservar(id);
            System.out.println("Propiedad reservada.");
        } catch (ElementoNoEncontradoException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void registrarVenta() {
        System.out.println("\n--- REGISTRAR VENTA (ADMINISTRADOR) ---");
        try {
            List<ProductorDeVenta> productores = usuariosDAO.listarProductoresPorCantidadDesc();
            if (productores.isEmpty()) {
                System.out.println("No hay productores de venta registrados para asignar la operación.");
                return;
            }

            System.out.println("Productores disponibles:");
            for (ProductorDeVenta p : productores) {
                System.out.println("ID: " + p.getId() + " | " + p.getNombre() + " " + p.getApellido()
                        + " | Activo: " + (p.isActivo() ? "Sí" : "No")
                        + " | Ventas: " + p.getCantidadVentas());
            }

            System.out.println("Ingrese el ID del productor de venta que concretó la operación:");
            int idProductor = leerNumero();
            Usuario u = gestorUsuarios.buscarPorId(idProductor);
            if (!(u instanceof ProductorDeVenta productor)) {
                throw new IllegalArgumentException("El ID " + idProductor + " no corresponde a un Productor de Venta.");
            }
            if (!productor.isActivo()) {
                throw new IllegalStateException("El productor de venta seleccionado no está activo (debe estar aprobado por un administrador).");
            }

            System.out.println("Ingrese el ID de la propiedad vendida:");
            int idPropiedad = leerNumero();
            Propiedad vendida = gestorPropiedades.buscarPorId(idPropiedad);

            gestorPropiedades.vender(idPropiedad);
            productor.setCantidadVentas(productor.getCantidadVentas() + 1);
            productor.setMontoVentas(productor.getMontoVentas() + vendida.getPrecio());
            usuariosDAO.actualizar(productor);

            System.out.println("Venta registrada: propiedad ID " + idPropiedad
                    + " por USD " + vendida.getPrecio()
                    + " asignada a " + productor.getNombre() + " " + productor.getApellido() + ".");
        } catch (ElementoNoEncontradoException | IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error en base de datos al registrar la venta: " + e.getMessage());
        }
    }

    private void menuGestionUsuarios(Administrador admin) {
        System.out.println("\n--- GESTIÓN DE USUARIOS ---");
        System.out.println("1. Listar todos los usuarios");
        System.out.println("2. Dar de alta / baja a un cliente");
        System.out.println("3. Aprobar productor de venta");
        System.out.println("0. Volver");
        int op = leerNumero();

        try {
            switch (op) {
                case 1 -> {
                    List<Usuario> lista = usuariosDAO.listarTodos();
                    for (Usuario u : lista) {
                        System.out.println("------------------------------");
                        System.out.println(u);
                    }
                }
                case 2 -> {
                    System.out.println("ID del cliente:");
                    int idCliente = leerNumero();
                    Usuario u = gestorUsuarios.buscarPorId(idCliente);
                    if (!(u instanceof Cliente)) {
                        System.out.println("El usuario indicado no es un Cliente.");
                        return;
                    }
                    System.out.println("1. Activar (alta) | 2. Desactivar (baja)");
                    boolean nuevoEstado = leerNumeroEntre(1, 2) == 1;
                    usuariosDAO.cambiarEstado(idCliente, nuevoEstado);
                    u.setActivo(nuevoEstado);
                    System.out.println("Estado del cliente actualizado.");
                }
                case 3 -> {
                    System.out.println("ID del productor de venta a aprobar:");
                    int idProd = leerNumero();
                    gestorUsuarios.aprobarProductor(admin.getId(), idProd);
                    System.out.println("Productor aprobado y activado.");
                }
                case 0 -> { }
                default -> System.out.println("Opción inválida.");
            }
        } catch (ElementoNoEncontradoException | IllegalArgumentException | SecurityException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private void menuRankingProductores() {
        System.out.println("\n--- RANKING DE PRODUCTORES DE VENTA ---");
        System.out.println("1. Ordenar por cantidad de propiedades vendidas");
        System.out.println("2. Ordenar por monto total vendido");
        int op = leerNumeroEntre(1, 2);

        try {
            List<ProductorDeVenta> productores = (op == 1)
                    ? usuariosDAO.listarProductoresPorCantidadDesc()
                    : usuariosDAO.listarProductoresPorMontoDesc();

            if (productores.isEmpty()) {
                System.out.println("No hay productores registrados.");
                return;
            }
            for (ProductorDeVenta p : productores) {
                System.out.println("------------------------------");
                System.out.println(p.getNombre() + " " + p.getApellido()
                        + " | Ventas: " + p.getCantidadVentas()
                        + " | Monto: USD " + p.getMontoVentas());
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar productores: " + e.getMessage());
        }
    }

    public static void validarContrasenia(String clave) throws ContraseniaInvalidaException {
        if (clave == null || clave.length() <= 8) {
            throw new ContraseniaInvalidaException("Debe tener más de 8 caracteres.");
        }
        if (!clave.matches(".*\\d.*")) {
            throw new ContraseniaInvalidaException("Debe incluir al menos un número.");
        }
        if (!clave.matches(".*[A-Z].*")) {
            throw new ContraseniaInvalidaException("Debe incluir al menos una letra mayúscula.");
        }
    }

    public void menuFiltros(boolean soloDisponibles) {
        System.out.println("Filtrar por:");
        System.out.println("1. Zona");
        System.out.println("2. Tipo (Departamento o Chalet)");
        System.out.println("3. Cantidad de ambientes mínima");
        System.out.println("4. Calle");
        System.out.println("0. Volver");
        int op = leerNumero();
        List<Propiedad> resultado;

        switch (op) {
            case 1 -> {
                System.out.println("1. NORTE - 2. SUR - 3. CENTRO");
                int z = leerNumeroEntre(1, 3);
                Zona zona = (z == 1) ? Zona.NORTE : (z == 2) ? Zona.SUR : Zona.CENTRO;
                resultado = gestorPropiedades.buscarPorZona(zona);
            }
            case 2 -> {
                System.out.println("1. DEPARTAMENTO - 2. CHALET");
                int t = leerNumeroEntre(1, 2);
                TipoPropiedad tipo = (t == 1) ? TipoPropiedad.DEPARTAMENTO : TipoPropiedad.CHALET;
                resultado = gestorPropiedades.buscarPorTipo(tipo);
            }
            case 3 -> {
                System.out.println("¿Cuántos ambientes como mínimo?");
                int amb = leerNumero();
                resultado = gestorPropiedades.buscarPorAmbientes(amb);
            }
            case 4 -> {
                System.out.println("Escribí la calle (o parte):");
                String calle = leerTextoNoVacio();
                resultado = gestorPropiedades.buscarPorCalle(calle);
            }
            case 0 -> { return; }
            default -> {
                System.out.println("Opción inválida.");
                return;
            }
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

    public void cargarPropiedad() {
        System.out.println("\n--- CARGAR PROPIEDAD (QUEDARÁ EN TASACIÓN) ---");
        System.out.println("1. DEPARTAMENTO | 2. CHALET");
        TipoPropiedad tipo = (leerNumeroEntre(1, 2) == 1) ? TipoPropiedad.DEPARTAMENTO : TipoPropiedad.CHALET;

        System.out.println("Zona: 1. NORTE | 2. SUR | 3. CENTRO");
        int z = leerNumeroEntre(1, 3);
        Zona zona = (z == 1) ? Zona.NORTE : (z == 2) ? Zona.SUR : Zona.CENTRO;

        System.out.println("Calle:");
        String calle = leerTextoNoVacio();
        System.out.println("Número de calle:");
        int numero = leerNumeroEntre(1, 99999);

        System.out.println("Descripción (Enter para dejar vacía):");
        String descripcion = sc.nextLine();
        if (descripcion.isBlank()) descripcion = " ";

        System.out.println("Ambientes (1 a 8):");
        int ambientes = leerNumeroEntre(1, 8);
        System.out.println("Dormitorios (0 a " + ambientes + "):");
        int dormitorios = leerNumeroEntre(0, ambientes);
        System.out.println("Baños (1 a 6):");
        int banios = leerNumeroEntre(1, 6);

        System.out.println("¿Tiene balcón? (s/n)");
        boolean balcon = leerSiNoConfirmacion("");
        System.out.println("¿Tiene cochera? (s/n)");
        boolean cochera = leerSiNoConfirmacion("");

        int anioActual = LocalDate.now().getYear();
        System.out.println("Año de construcción (1900 a " + anioActual + "):");
        int anioConstruccion = leerNumeroEntre(1900, anioActual);

        System.out.println("Estado de conservación: 1. EXCELENTE | 2. MUY BUENO | 3. BUENO | 4. A RECICLAR | 5. NINGUNO");
        int est = leerNumeroEntre(1, 5);
        EstadoConservacion conservacion = switch (est) {
            case 1 -> EstadoConservacion.EXCELENTE;
            case 2 -> EstadoConservacion.MUY_BUENO;
            case 3 -> EstadoConservacion.BUENO;
            case 4 -> EstadoConservacion.A_RECICLAR;
            default -> EstadoConservacion.NADA;
        };

        System.out.println("Superficie cubierta en m² (mayor a 0):");
        double supCubierta = leerDecimalMayorA(0);
        System.out.println("Terreno en m² (0 si no aplica):");
        double terreno = leerDecimalMayorOIgualA(0);

        Propiedad nueva;
        if (tipo == TipoPropiedad.DEPARTAMENTO) {
            System.out.println("Número o letra de departamento:");
            String numDpto = leerTextoNoVacio();
            System.out.println("Piso (0 = planta baja):");
            int piso = leerNumeroEntre(0, 100);
            System.out.println("¿Tiene ascensor? (s/n)");
            boolean ascensor = leerSiNoConfirmacion("");
            System.out.println("¿Tiene vista al mar? (s/n)");
            boolean vistaMar = leerSiNoConfirmacion("");
            System.out.println("Disposición: 1. FRENTE | 2. CONTRA_FRENTE | 3. LATERAL | 4. INTERNO | 5. NINGUNA");
            int dispOp = leerNumeroEntre(1, 5);
            Disposicion disp = switch (dispOp) {
                case 1 -> Disposicion.FRENTE;
                case 2 -> Disposicion.CONTRA_FRENTE;
                case 3 -> Disposicion.LATERAL;
                case 4 -> Disposicion.INTERNO;
                default -> Disposicion.NINGUNA;
            };

            nueva = new Departamento(tipo, descripcion, zona, calle, numero, balcon, ambientes,
                    dormitorios, banios, cochera, anioConstruccion, conservacion, terreno, supCubierta,
                    numDpto, piso, ascensor, vistaMar, disp);
        } else {
            System.out.println("Cantidad de pisos (1 a 5):");
            int cantPisos = leerNumeroEntre(1, 5);
            System.out.println("¿Tiene pileta? (s/n)");
            boolean pileta = leerSiNoConfirmacion("");
            System.out.println("¿Tiene quincho? (s/n)");
            boolean quincho = leerSiNoConfirmacion("");
            System.out.println("¿Tiene jardín? (s/n)");
            boolean jardin = leerSiNoConfirmacion("");

            nueva = new Chalet(tipo, descripcion, zona, calle, numero, balcon, ambientes,
                    dormitorios, banios, cochera, anioConstruccion, conservacion, terreno, supCubierta,
                    cantPisos, pileta, quincho, jardin);
        }

        try {
            gestorPropiedades.agregarElemento(nueva);
            System.out.println("Propiedad cargada en estado TASACIÓN. Un administrador deberá publicarla para que esté DISPONIBLE.");
        } catch (ElementoDuplicadoException e) {
            System.out.println(e.getMessage());
        }
    }

    private void cargarPropiedadesIniciales() {
        try {
            for (Propiedad p : new dao.PropiedadesDAO().listarTodos()) {
                gestorPropiedades.agregarElemento(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar propiedades iniciales: " + e.getMessage());
        } catch (ElementoDuplicadoException e) {
            System.out.println(e.getMessage());
        }
    }

    public void mostrarPropiedades(List<Propiedad> lista) {
        if (lista.isEmpty()) {
            System.out.println("No hay propiedades para mostrar.");
            return;
        }
        for (Propiedad p : lista) {
            System.out.println("------------------------------");
            System.out.println(p);
        }
        System.out.println("------------------------------");
        System.out.println("Total: " + lista.size());
    }

    private int leerNumero() {
        while (true) {
            String texto = sc.nextLine();
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido:");
            }
        }
    }

    private int leerNumeroEntre(int minimo, int maximo) {
        int n = leerNumero();
        while (n < minimo || n > maximo) {
            System.out.println("Debe estar entre " + minimo + " y " + maximo + ":");
            n = leerNumero();
        }
        return n;
    }

    private String leerTextoNoVacio() {
        String texto = sc.nextLine();
        while (texto.isBlank()) {
            System.out.println("No puede estar vacío, escriba nuevamente:");
            texto = sc.nextLine();
        }
        return texto.trim();
    }

    private boolean leerSiNoConfirmacion(String mensaje) {
        if (!mensaje.isBlank()) {
            System.out.println(mensaje + " (s/n)");
        }
        String resp = sc.nextLine().trim();
        while (!resp.equalsIgnoreCase("s") && !resp.equalsIgnoreCase("n")) {
            System.out.println("Escriba 's' o 'n':");
            resp = sc.nextLine().trim();
        }
        return resp.equalsIgnoreCase("s");
    }

    private double leerDecimal() {
        while (true) {
            String texto = sc.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número decimal válido:");
            }
        }
    }

    private double leerDecimalMayorA(double minimo) {
        double n = leerDecimal();
        while (n <= minimo) {
            System.out.println("Debe ser mayor a " + minimo + ":");
            n = leerDecimal();
        }
        return n;
    }

    private double leerDecimalMayorOIgualA(double minimo) {
        double n = leerDecimal();
        while (n < minimo) {
            System.out.println("No puede ser menor a " + minimo + ":");
            n = leerDecimal();
        }
        return n;
    }

    private String leerEmailValido() {
        while (true) {
            System.out.println("Email:");
            String email = leerTextoNoVacio();
            if (esEmailValido(email)) {
                return email;
            }
            System.out.println("Email inválido. Debe contener '@' y un dominio válido (ej: usuario@dominio.com).");
        }
    }

    public static boolean esEmailValido(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        int arrobaIndex = email.indexOf('@');
        // Debe existir '@' y no haber más de un '@'
        if (arrobaIndex <= 0 || arrobaIndex != email.lastIndexOf('@')) {
            return false;
        }
        String dominio = email.substring(arrobaIndex + 1);
        // El dominio debe tener al menos un punto, no empezar/terminar con punto y no tener espacios
        return dominio.contains(".")
                && !dominio.startsWith(".")
                && !dominio.endsWith(".")
                && !email.contains(" ");
    }
    private String leerDniValido() {
        while (true) {
            System.out.println("DNI (solo números, sin puntos ni letras):");
            String dni = leerTextoNoVacio();
            if (esDniValido(dni)) {
                return dni;
            }
            System.out.println("DNI inválido. Debe contener solo números (7 a 8 dígitos).");
        }
    }

    public static boolean esDniValido(String dni) {
        return dni != null && dni.matches("\\d{7,8}");
    }

    private String leerCuilValido() {
        while (true) {
            System.out.println("CUIL (11 números, sin guiones ni letras):");
            String cuil = leerTextoNoVacio();
            if (esCuilValido(cuil)) {
                return cuil;
            }
            System.out.println("CUIL inválido. Debe contener exactamente 11 números.");
        }
    }

    public static boolean esCuilValido(String cuil) {
        return cuil != null && cuil.matches("\\d{11}");
    }

    private void filtrarPropiedadesPorEstado() {
        System.out.println("\n--- FILTRAR PROPIEDADES POR ESTADO ---");
        System.out.println("1. DISPONIBLE");
        System.out.println("2. NO_DISPONIBLE");
        System.out.println("3. VENDIDA");
        System.out.println("4. TASACION");
        System.out.println("0. Volver");

        int op = leerNumero();
        if (op == 0) {
            return;
        }

        EstadoPublicacion estadoBuscado = switch (op) {
            case 1 -> EstadoPublicacion.DISPONIBLE;
            case 2 -> EstadoPublicacion.NO_DISPONIBLE;
            case 3 -> EstadoPublicacion.VENDIDA;
            case 4 -> EstadoPublicacion.TASACION;
            default -> {
                System.out.println("Opción inválida.");
                yield null;
            }
        };

        if (estadoBuscado == null) {
            return;
        }

        List<Propiedad> filtradas = new ArrayList<>();
        for (Propiedad p : gestorPropiedades.listar()) {
            if (p.getEstado() == estadoBuscado) {
                filtradas.add(p);
            }
        }

        mostrarPropiedades(filtradas);
    }
}