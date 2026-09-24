

package vista;

import conexion.ConexionBD;
import modelo.Usuario;
import sesion.SesionUsuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PanelUsuario extends JFrame {

    private static final Color AZUL          = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO   = new Color(16, 62, 108);
    private static final Color AZUL_GRAD_1   = new Color(22, 104, 214);
    private static final Color AZUL_GRAD_2   = new Color(41, 137, 226);
    private static final Color FONDO         = new Color(238, 246, 253);
    private static final Color FONDO_CAMPO   = new Color(247, 250, 253);
    private static final Color BORDE_CAMPO   = new Color(226, 236, 246);
    private static final Color GRIS_TEXTO    = new Color(112, 133, 153);
    private static final Color GRIS_PISTA    = new Color(150, 167, 182);
    private static final Color AMARILLO      = new Color(242, 194, 48);
    private static final Color AMARILLO_PILL = new Color(252, 240, 205);
    private static final Color AMARILLO_TXT  = new Color(150, 113, 26);
    private static final Color VERDE         = new Color(34, 150, 105);
    private static final Color VERDE_PILL    = new Color(219, 243, 232);
    private static final Color ROJO          = new Color(199, 62, 62);
    private static final Color ROJO_PILL     = new Color(252, 226, 226);
    private static final Color MORADO        = new Color(116, 88, 190);
    private static final Color PATRON        = new Color(228, 240, 250);
    private static final Color LATERAL       = new Color(15, 57, 101);
    private static final Color LATERAL_HOVER = new Color(24, 78, 132);

    private static final String FUENTE = "Segoe UI";

    private static final String MARCA_CANCELADO_USUARIO =
            "[CANCELADO_POR_USUARIO]";
    private static final String MARCA_RECHAZADO_CUIDADOR =
            "[RECHAZADO_POR_CUIDADOR]";

    private static final int HUELLA    = 0;
    private static final int PERSONA   = 1;
    private static final int VISTO     = 2;
    private static final int EQUIS     = 3;
    private static final int RELOJ     = 4;
    private static final int SALIR     = 5;
    private static final int SOBRE     = 6;
    private static final int BUSCAR    = 7;
    private static final int MASCOTA   = 8;
    private static final int RESERVA   = 9;
    private static final int DINERO    = 10;
    private static final int CASA      = 11;
    private static final int ESTRELLA  = 12;
    private static final int UBICACION = 13;
    private static final int MAS       = 14;
    private static final int ACTUALIZAR = 15;
    private static final int TELEFONO  = 16;

    private static final DateTimeFormatter DIA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DIA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter HORA_ENTRADA =
            DateTimeFormatter.ofPattern("HH:mm")
                    .withResolverStyle(
                            java.time.format.ResolverStyle.STRICT);

    private static final String[] MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre",
            "Noviembre", "Diciembre"
    };

    private final Usuario usuario;
    private final Datos datos = new Datos();

    private final CardLayout layout = new CardLayout();
    private JPanel contenido;

    private JPanel cuerpoExplorar;
    private JPanel cuerpoMascotas;
    private JPanel cuerpoReservas;
    private JPanel cuerpoPagos;
    private JPanel cuerpoPerfil;

    private JComboBox<String> filtroCiudad;
    private final List<BotonMenu> menu = new ArrayList<>();

    public PanelUsuario(Usuario usuario) {
        this.usuario = usuario;
        configurarVentana();
        crearComponentes();
        mostrar("EXPLORAR");
    }

    private void configurarVentana() {
        setTitle("Pet Home Boarding");
        setSize(1340, 850);
        setMinimumSize(new Dimension(1160, 740));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FONDO);

        principal.add(crearBarraSuperior(), BorderLayout.NORTH);
        principal.add(crearMenuLateral(), BorderLayout.WEST);

        contenido = new JPanel(layout);
        contenido.setOpaque(false);

        cuerpoExplorar = new JPanel(new BorderLayout());
        cuerpoExplorar.setOpaque(false);
        cuerpoMascotas = new JPanel(new BorderLayout());
        cuerpoMascotas.setOpaque(false);
        cuerpoReservas = new JPanel(new BorderLayout());
        cuerpoReservas.setOpaque(false);
        cuerpoPagos = new JPanel(new BorderLayout());
        cuerpoPagos.setOpaque(false);
        cuerpoPerfil = new JPanel(new BorderLayout());
        cuerpoPerfil.setOpaque(false);

        contenido.add(crearExplorar(), "EXPLORAR");
        contenido.add(marco("Mis mascotas",
                        "Registra a tus compañeros para poder reservar",
                        cuerpoMascotas, this::cargarMascotas,
                        "Registrar mascota", this::formularioMascota),
                "MASCOTAS");
        contenido.add(marco("Mis reservas",
                "Estadías solicitadas, en curso y finalizadas",
                cuerpoReservas, this::cargarReservas,
                null, null), "RESERVAS");
        contenido.add(marco("Mis pagos",
                "Historial de pagos de alojamiento y transporte",
                cuerpoPagos, this::cargarPagos,
                null, null), "PAGOS");
        contenido.add(marco("Mi perfil",
                "Tus datos y tus contactos de emergencia",
                cuerpoPerfil, this::cargarPerfil,
                null, null), "PERFIL");

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);
    }

    private JPanel crearBarraSuperior() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(new EmptyBorder(14, 24, 14, 26));

        JPanel izquierda =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        izquierda.setOpaque(false);
        izquierda.add(crearLogo(44));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Pet Home Boarding");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblSub = new JLabel(
                "Un hogar seguro para tu mascota");
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblSub);
        izquierda.add(textos);

        JPanel derecha =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        derecha.setOpaque(false);

        JLabel lblHola = new JLabel(
                "Hola, " + texto(usuario.getNombre(), "Usuario"));
        lblHola.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblHola.setForeground(AZUL_OSCURO);

        derecha.add(lblHola);

        if (esCuidadorVerificado()) {

            JButton btnModo = new JButton("Modo cuidador");
            btnModo.setIcon(icono(CASA, AZUL, 15));
            btnModo.setIconTextGap(8);
            btnModo.setFont(new Font(FUENTE, Font.BOLD, 13));
            btnModo.setForeground(AZUL);
            btnModo.setBackground(Color.WHITE);
            btnModo.setFocusPainted(false);
            btnModo.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDE_CAMPO),
                    new EmptyBorder(8, 15, 8, 15)));
            btnModo.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnModo.addActionListener(e -> abrirModoCuidador());

            derecha.add(btnModo);
        }

        JButton btnSalir = new JButton("Cerrar sesión");
        btnSalir.setIcon(icono(SALIR, GRIS_TEXTO, 16));
        btnSalir.setIconTextGap(7);
        btnSalir.setFont(new Font(FUENTE, Font.BOLD, 13));
        btnSalir.setForeground(GRIS_TEXTO);
        btnSalir.setBorderPainted(false);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setMargin(new Insets(0, 0, 0, 0));
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> cerrarSesion());

        derecha.add(btnSalir);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    private boolean esCuidadorVerificado() {

        try {
            return datos.esCuidadorVerificado(
                    usuario.getIdUsuario());
        } catch (RuntimeException e) {
            return false;
        }
    }

    private void abrirModoCuidador() {

        PanelCuidador panel = new PanelCuidador(usuario);
        panel.setVisible(true);
        dispose();
    }

    private JPanel crearMenuLateral() {

        JPanel lateral = new JPanel();
        lateral.setBackground(LATERAL);
        lateral.setPreferredSize(new Dimension(226, 0));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(new EmptyBorder(22, 14, 18, 14));

        JLabel lblMenu = new JLabel("MI CUENTA");
        lblMenu.setFont(new Font(FUENTE, Font.BOLD, 11));
        lblMenu.setForeground(new Color(174, 201, 225));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMenu.setBorder(new EmptyBorder(0, 12, 12, 0));

        lateral.add(lblMenu);
        lateral.add(boton("Explorar", BUSCAR, "EXPLORAR"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Mis mascotas", MASCOTA, "MASCOTAS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Mis reservas", RESERVA, "RESERVAS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Mis pagos", DINERO, "PAGOS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Mi perfil", PERSONA, "PERFIL"));
        lateral.add(Box.createVerticalGlue());

        JLabel lblPie = new JLabel(
                "<html><b style='color:#FFFFFF;'>Pet Home Boarding</b>"
                        + "<br><span style='color:#AEC9E1;'>"
                        + "Tu mascota, como en casa</span></html>");
        lblPie.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblPie.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPie.setBorder(new EmptyBorder(12, 12, 0, 0));
        lateral.add(lblPie);

        return lateral;
    }

    private BotonMenu boton(String texto, int tipoIcono,
                            String seccion) {

        BotonMenu btn = new BotonMenu(
                texto, icono(tipoIcono, Color.WHITE, 18), seccion);
        btn.addActionListener(e -> mostrar(seccion));
        menu.add(btn);
        return btn;
    }

    private void mostrar(String seccion) {

        layout.show(contenido, seccion);

        for (BotonMenu b : menu) {
            b.setActivo(b.getSeccion().equals(seccion));
        }

        switch (seccion) {
            case "EXPLORAR" -> cargarAlojamientos();
            case "MASCOTAS" -> cargarMascotas();
            case "RESERVAS" -> cargarReservas();
            case "PAGOS" -> cargarPagos();
            case "PERFIL" -> cargarPerfil();
            default -> {
            }
        }
    }

    private JPanel marco(String titulo, String nota, JPanel cuerpo,
                         Runnable recargar, String textoBoton,
                         Runnable accionBoton) {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblNota = new JLabel(nota);
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNota.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblNota);

        JPanel acciones =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        acciones.setOpaque(false);

        if (textoBoton != null) {
            BotonRedondeado btn = new BotonRedondeado(
                    textoBoton, AZUL_GRAD_1, Color.WHITE,
                    null, AZUL_GRAD_2);
            btn.setIcon(icono(MAS, Color.WHITE, 16));
            btn.setIconTextGap(9);
            btn.setPreferredSize(new Dimension(200, 44));
            btn.addActionListener(e -> accionBoton.run());
            acciones.add(btn);
        }

        acciones.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> recargar.run()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(acciones, BorderLayout.EAST);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(cuerpo, BorderLayout.CENTER);

        return fondo;
    }

    private JButton botonBlanco(String texto, int tipoIcono,
                                java.awt.event.ActionListener a) {

        JButton btn = new JButton(texto);
        btn.setIcon(icono(tipoIcono, AZUL, 15));
        btn.setIconTextGap(8);
        btn.setFont(new Font(FUENTE, Font.BOLD, 13));
        btn.setForeground(AZUL);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO),
                new EmptyBorder(9, 16, 9, 16)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(a);

        return btn;
    }

    private JPanel crearExplorar() {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Explorar alojamientos");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblNota = new JLabel(
                "Solo aparecen cuidadores verificados por el "
                        + "administrador");
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNota.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblNota);

        filtroCiudad = new JComboBox<>();
        filtroCiudad.setFont(new Font(FUENTE, Font.PLAIN, 13));
        filtroCiudad.setPreferredSize(new Dimension(180, 40));
        filtroCiudad.setBackground(Color.WHITE);
        filtroCiudad.addActionListener(e -> cargarAlojamientos());

        JPanel derecha =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        derecha.setOpaque(false);
        derecha.add(new JLabel(icono(UBICACION, GRIS_TEXTO, 17)));
        derecha.add(filtroCiudad);
        derecha.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> cargarAlojamientos()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(cuerpoExplorar, BorderLayout.CENTER);

        return fondo;
    }

    private void cargarAlojamientos() {

        cuerpoExplorar.removeAll();

        try {

            if (filtroCiudad.getItemCount() == 0) {
                filtroCiudad.addItem("Todas las ciudades");
                for (String ciudad : datos.ciudades()) {
                    filtroCiudad.addItem(ciudad);
                }
            }

            String ciudad = filtroCiudad.getSelectedIndex() <= 0
                    ? null
                    : (String) filtroCiudad.getSelectedItem();

            List<Object[]> lista = datos.alojamientos(
                    ciudad, usuario.getIdUsuario());

            JPanel rejilla = new JPanel(new GridLayout(0, 3, 16, 16));
            rejilla.setOpaque(false);

            if (lista.isEmpty()) {
                cuerpoExplorar.add(vacio(
                                "No hay alojamientos disponibles."),
                        BorderLayout.CENTER);
            } else {
                for (Object[] fila : lista) {
                    rejilla.add(tarjetaAlojamiento(fila));
                }
                cuerpoExplorar.add(desplazable(rejilla),
                        BorderLayout.CENTER);
            }

        } catch (RuntimeException e) {
            cuerpoExplorar.add(error(e.getMessage()),
                    BorderLayout.CENTER);
        }

        cuerpoExplorar.revalidate();
        cuerpoExplorar.repaint();
    }

    private JPanel tarjetaAlojamiento(Object[] a) {

        int id = (int) a[0];
        String nombre = (String) a[1];
        String ciudad = (String) a[2];
        String tipo = (String) a[3];
        String descripcion = (String) a[4];
        double precio = (double) a[5];
        int capacidad = (int) a[6];
        String cuidador = (String) a[7];
        double calificacion = (double) a[8];
        int resenas = (int) a[9];

        String rutaFoto = ImagenAlojamientoUtil.principal(nombre, datos.fotoPrincipal(id));

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(0, 8));
        caja.setBorder(new EmptyBorder(16, 20, 16, 20));
        caja.setPreferredSize(new Dimension(320, 386));

        JLabel foto = cargarImagenLocal(rutaFoto, 280, 150);

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setOpaque(false);

        JPanel filaTipo =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaTipo.setOpaque(false);
        filaTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        filaTipo.add(pastilla(tipo, AMARILLO_PILL, AMARILLO_TXT));

        if (resenas > 0) {
            filaTipo.add(new Estrellas(calificacion, 15));
            JLabel lblCalif = new JLabel(
                    String.format("%.1f", calificacion)
                            + "  (" + resenas + ")");
            lblCalif.setFont(new Font(FUENTE, Font.BOLD, 12));
            lblCalif.setForeground(GRIS_TEXTO);
            filaTipo.add(lblCalif);
        } else {
            JLabel lblSin = new JLabel("Sin calificaciones todavía");
            lblSin.setFont(new Font(FUENTE, Font.PLAIN, 11));
            lblSin.setForeground(GRIS_PISTA);
            filaTipo.add(lblSin);
        }

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 18));
        lblNombre.setForeground(AZUL_OSCURO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblCiudad = new JLabel("  " + ciudad
                + "   ·   " + cuidador);
        lblCiudad.setIcon(icono(UBICACION, GRIS_PISTA, 14));
        lblCiudad.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblCiudad.setForeground(GRIS_TEXTO);
        lblCiudad.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDescripcion = new JLabel(
                "<html><div style='width:270px;'>"
                        + escapar(corto(texto(descripcion,
                        "Sin descripción"), 110))
                        + "</div></html>");
        lblDescripcion.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblDescripcion.setForeground(GRIS_TEXTO);
        lblDescripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnDetalles = new JButton("Ver detalles");
        btnDetalles.setFont(new Font(FUENTE, Font.BOLD, 12));
        btnDetalles.setForeground(AZUL);
        btnDetalles.setBorderPainted(false);
        btnDetalles.setContentAreaFilled(false);
        btnDetalles.setFocusPainted(false);
        btnDetalles.setMargin(new Insets(0, 0, 0, 0));
        btnDetalles.setHorizontalAlignment(SwingConstants.LEFT);
        Dimension tamanoDetalles = new Dimension(105, 24);
        btnDetalles.setMinimumSize(tamanoDetalles);
        btnDetalles.setPreferredSize(tamanoDetalles);
        btnDetalles.setMaximumSize(tamanoDetalles);
        btnDetalles.setVerticalAlignment(SwingConstants.CENTER);
        btnDetalles.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDetalles.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetalles.addActionListener(e -> detalleAlojamiento(id));

        arriba.add(filaTipo);
        arriba.add(Box.createVerticalStrut(5));
        arriba.add(lblNombre);
        arriba.add(Box.createVerticalStrut(3));
        arriba.add(lblCiudad);
        arriba.add(Box.createVerticalStrut(4));
        arriba.add(lblDescripcion);
        arriba.add(Box.createVerticalStrut(1));
        arriba.add(btnDetalles);

        JPanel abajo = new JPanel(new BorderLayout(10, 0));
        abajo.setOpaque(false);

        JPanel precios = new JPanel();
        precios.setLayout(new BoxLayout(precios, BoxLayout.Y_AXIS));
        precios.setOpaque(false);

        JLabel lblPrecio = new JLabel(dinero(precio));
        lblPrecio.setFont(new Font(FUENTE, Font.BOLD, 20));
        lblPrecio.setForeground(AZUL_OSCURO);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel("por noche  ·  cupo "
                + capacidad);
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblNota.setForeground(GRIS_PISTA);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        precios.add(lblPrecio);
        precios.add(lblNota);

        BotonRedondeado btnReservar = new BotonRedondeado(
                "Reservar", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnReservar.setPreferredSize(new Dimension(120, 42));
        btnReservar.addActionListener(
                e -> formularioReserva(id, nombre, precio));

        abajo.add(precios, BorderLayout.CENTER);
        abajo.add(btnReservar, BorderLayout.EAST);

        caja.add(foto, BorderLayout.NORTH);
        caja.add(arriba, BorderLayout.CENTER);
        caja.add(abajo, BorderLayout.SOUTH);

        return caja;
    }

    private void detalleAlojamiento(int idAlojamiento) {

        Object[] a;
        List<String> fotos;
        List<Object[]> categorias;
        List<Object[]> reglas;

        try {
            a = datos.alojamientoDetalle(idAlojamiento);
            if (a == null) {
                aviso("No se encontró el alojamiento.", false);
                return;
            }
            fotos = ImagenAlojamientoUtil.galeria((String) a[0], datos.fotos(idAlojamiento));
            categorias = datos.categoriasAceptadas(idAlojamiento);
            reglas = datos.reglasAlojamiento(idAlojamiento);
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        JDialog dialogo = new JDialog(this,
                "Detalle del alojamiento", true);
        dialogo.setSize(660, 720);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 24, 30));

        JLabel lblTitulo = new JLabel((String) a[0]);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(a[1] + "  ·  " + a[4]
                + "  ·  " + a[3]);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(4));
        columna.add(lblSub);
        columna.add(Box.createVerticalStrut(16));
        columna.add(galeria(fotos));
        columna.add(Box.createVerticalStrut(18));

        int resenas = (int) a[16];
        double promedio = (double) a[15];

        JPanel filaCuidador =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filaCuidador.setOpaque(false);
        filaCuidador.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCuidador.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblCuidador = new JLabel("Cuidador: " + a[14]);
        lblCuidador.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblCuidador.setForeground(AZUL_OSCURO);
        filaCuidador.add(lblCuidador);

        if (resenas > 0) {
            filaCuidador.add(new Estrellas(promedio, 15));
            JLabel lblProm = new JLabel(
                    String.format("%.1f", promedio)
                            + "  ·  " + resenas + " reseñas");
            lblProm.setFont(new Font(FUENTE, Font.PLAIN, 12));
            lblProm.setForeground(GRIS_TEXTO);
            filaCuidador.add(lblProm);
        } else {
            JLabel lblSin = new JLabel("Sin calificaciones todavía");
            lblSin.setFont(new Font(FUENTE, Font.PLAIN, 12));
            lblSin.setForeground(GRIS_PISTA);
            filaCuidador.add(lblSin);
        }

        columna.add(filaCuidador);
        columna.add(Box.createVerticalStrut(16));

        JPanel rejilla = new JPanel(new GridLayout(0, 2, 12, 12));
        rejilla.setOpaque(false);
        rejilla.setAlignmentX(Component.LEFT_ALIGNMENT);
        rejilla.add(dato("Precio por noche",
                dinero((double) a[8]), DINERO));
        rejilla.add(dato("Capacidad total",
                String.valueOf(a[7]), CASA));
        rejilla.add(dato("Dirección", (String) a[5], UBICACION));
        rejilla.add(dato("Referencia", (String) a[6], UBICACION));
        rejilla.add(dato("Patio",
                ((boolean) a[9]) ? "Sí" : "No", CASA));
        rejilla.add(dato("Cerramiento",
                ((boolean) a[10]) ? "Sí" : "No", CASA));
        rejilla.add(dato("Convive con otras mascotas",
                ((boolean) a[11]) ? "Sí" : "No", HUELLA));
        rejilla.add(dato("Ofrece transporte",
                ((boolean) a[13]) ? "Sí" : "No", UBICACION));
        rejilla.add(dato("Publicado el",
                a[17] == null ? null
                        : ((Timestamp) a[17]).toLocalDateTime()
                          .toLocalDate().format(DIA), RELOJ));

        columna.add(rejilla);
        columna.add(Box.createVerticalStrut(18));
        columna.add(bloqueTexto("Descripción", (String) a[2]));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueTexto("Reglas generales", (String) a[12]));
        columna.add(Box.createVerticalStrut(18));

        columna.add(subtitulo("Mascotas aceptadas"));
        columna.add(Box.createVerticalStrut(8));

        if (categorias.isEmpty()) {
            columna.add(lineaGris(
                    "El cuidador todavía no definió qué mascotas "
                            + "acepta."));
        } else {
            for (Object[] c : categorias) {
                String especie = (String) c[0];
                String categoria = (String) c[1];
                String aceptada = "UNICA".equalsIgnoreCase(categoria)
                        ? especie : especie + " - " + categoria;
                String linea = "   •  " + aceptada;
                if (c[2] != null) {
                    linea += "   ·   Máximo permitido: " + c[2];
                }
                if (c[3] != null && !((String) c[3]).isBlank()) {
                    linea += "   ·   " + c[3];
                }
                columna.add(lineaGris(linea));
            }
        }

        columna.add(Box.createVerticalStrut(18));
        columna.add(subtitulo("Compatibilidad y reglas"));
        columna.add(Box.createVerticalStrut(8));

        if (reglas.isEmpty()) {
            columna.add(lineaGris(
                    "Este alojamiento no tiene reglas registradas."));
        } else {
            for (Object[] rg : reglas) {
                boolean acepta = (boolean) rg[1];
                JLabel lbl = new JLabel(
                        (acepta ? "✓  Acepta: " : "✕  No acepta: ")
                                + rg[0]);
                lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
                lbl.setForeground(acepta ? VERDE : ROJO);
                lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                columna.add(lbl);
                if (rg[2] != null && !((String) rg[2]).isBlank()) {
                    columna.add(lineaGris(
                            "     Condiciones: " + rg[2]));
                }
                columna.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        BotonRedondeado btnReservar = new BotonRedondeado(
                "Reservar aquí", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnReservar.setPreferredSize(new Dimension(190, 46));
        btnReservar.addActionListener(e -> {
            dialogo.dispose();
            formularioReserva(idAlojamiento, (String) a[0],
                    (double) a[8]);
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnReservar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private JPanel galeria(List<String> rutas) {

        JPanel panel = new JPanel(new BorderLayout(8, 6));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        if (rutas.isEmpty()) {
            panel.add(cargarImagenLocal(null, 560, 210),
                    BorderLayout.CENTER);
            return panel;
        }

        final int[] indice = {0};

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(cargarImagenLocal(rutas.get(0), 560, 210),
                BorderLayout.CENTER);

        JLabel lblContador = new JLabel(
                "1 de " + rutas.size(), SwingConstants.CENTER);
        lblContador.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblContador.setForeground(GRIS_TEXTO);

        JButton anterior = flechaGaleria("‹");
        JButton siguiente = flechaGaleria("›");

        Runnable pintar = () -> {
            centro.removeAll();
            centro.add(cargarImagenLocal(
                            rutas.get(indice[0]), 560, 210),
                    BorderLayout.CENTER);
            lblContador.setText((indice[0] + 1) + " de "
                    + rutas.size());
            centro.revalidate();
            centro.repaint();
        };

        anterior.addActionListener(e -> {
            indice[0] = (indice[0] - 1 + rutas.size())
                    % rutas.size();
            pintar.run();
        });

        siguiente.addActionListener(e -> {
            indice[0] = (indice[0] + 1) % rutas.size();
            pintar.run();
        });

        panel.add(anterior, BorderLayout.WEST);
        panel.add(centro, BorderLayout.CENTER);
        panel.add(siguiente, BorderLayout.EAST);
        panel.add(lblContador, BorderLayout.SOUTH);

        return panel;
    }

    private JButton flechaGaleria(String texto) {

        JButton btn = new JButton(texto);
        btn.setFont(new Font(FUENTE, Font.BOLD, 20));
        btn.setForeground(AZUL);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(32, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private JLabel subtitulo(String t) {

        JLabel lbl = new JLabel(t);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 16));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        return lbl;
    }

    private JLabel lineaGris(String t) {

        JLabel lbl = new JLabel(
                "<html><div style='width:540px;'>"
                        + escapar(t) + "</div></html>");
        lbl.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lbl.setForeground(GRIS_TEXTO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        return lbl;
    }

    private JPanel bloqueTexto(String etiqueta, String valor) {

        PanelCaja caja = new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(13, 15, 13, 15));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblEtiqueta.setForeground(AZUL);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(
                "<html><div style='width:520px;'>"
                        + escapar(texto(valor, "Sin información"))
                        + "</div></html>");
        lblValor.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        caja.add(lblEtiqueta);
        caja.add(Box.createVerticalStrut(5));
        caja.add(lblValor);

        return caja;
    }

    private JLabel cargarImagenLocal(String ruta, int ancho, int alto) {

        JLabel lbl = new JLabel();
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setPreferredSize(new Dimension(ancho, alto));

        try {
            BufferedImage imagen = ImagenAlojamientoUtil.leer(ruta);

            if (imagen != null) {
                Image escalada = imagen.getScaledInstance(
                        ancho, alto, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(escalada));
                lbl.setText(null);
                return lbl;
            }
        } catch (Exception ignored) {
        }

        lbl.setText("Sin imagen");
        lbl.setForeground(GRIS_PISTA);
        return lbl;
    }

    private void formularioReserva(int idAlojamiento,
                                   String nombreAlojamiento,
                                   double precio) {

        List<Object[]> mascotas;

        try {
            mascotas = datos.mascotas(usuario.getIdUsuario());
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        if (mascotas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Primero registra una mascota.\n"
                            + "Ve a la sección Mis mascotas.",
                    "Sin mascotas",
                    JOptionPane.WARNING_MESSAGE);
            mostrar("MASCOTAS");
            return;
        }

        JDialog dialogo = new JDialog(this,
                "Reservar en " + nombreAlojamiento, true);
        dialogo.setSize(560, 700);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 22, 30));

        JLabel lblTitulo = new JLabel("Nueva reserva");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(nombreAlojamiento
                + "  ·  " + dinero(precio) + " por noche");
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cboMascota = new JComboBox<>();
        for (Object[] m : mascotas) {
            cboMascota.addItem(m[1] + "  ·  " + m[2]
                    + "  ·  " + m[4] + " kg");
        }
        cboMascota.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboMascota.setBackground(Color.WHITE);
        cboMascota.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JLabel lblCompatible = new JLabel(" ");
        lblCompatible.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblCompatible.setAlignmentX(Component.LEFT_ALIGNMENT);

        final boolean[] compatible = {false};

        Runnable revisar = () -> {

            int fila = cboMascota.getSelectedIndex();

            if (fila < 0) {
                return;
            }

            int idMascota = (int) mascotas.get(fila)[0];
            String nombreMascota = String.valueOf(
                    mascotas.get(fila)[1]);

            try {
                List<String> problemas = datos.incompatibilidades(
                        idAlojamiento, idMascota, nombreMascota);

                if (problemas.isEmpty()) {
                    compatible[0] = true;
                    lblCompatible.setText(
                            "<html><div style='width:460px;'>"
                                    + "✓ Tu mascota es compatible con "
                                    + "este alojamiento.</div></html>");
                    lblCompatible.setForeground(VERDE);
                } else {
                    compatible[0] = false;
                    StringBuilder sb = new StringBuilder(
                            "<html><div style='width:460px;'>");
                    for (String p : problemas) {
                        sb.append("⚠ ").append(escapar(p))
                                .append("<br>");
                    }
                    sb.append("</div></html>");
                    lblCompatible.setText(sb.toString());
                    lblCompatible.setForeground(ROJO);
                }

            } catch (RuntimeException ex) {
                compatible[0] = false;
                lblCompatible.setText(escapar(ex.getMessage()));
                lblCompatible.setForeground(ROJO);
            }
        };

        cboMascota.addActionListener(e -> revisar.run());

        SelectorFecha ingreso = new SelectorFecha();
        SelectorFecha salida = new SelectorFecha();

        JTextArea txtMensaje = new JTextArea();
        JTextArea txtInstrucciones = new JTextArea();

        JLabel lblTotal = new JLabel("Selecciona las fechas");
        lblTotal.setFont(new Font(FUENTE, Font.BOLD, 19));
        lblTotal.setForeground(AZUL);
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);

        Runnable calcular = () -> {

            LocalDate a = ingreso.getFecha();
            LocalDate b = salida.getFecha();

            if (a == null || b == null || !b.isAfter(a)) {
                lblTotal.setText("Selecciona las fechas");
                return;
            }

            long noches = java.time.temporal.ChronoUnit.DAYS
                    .between(a, b);

            lblTotal.setText("<html><div style='width:460px;'>"
                    + "Precio por noche: <b>" + dinero(precio)
                    + "</b><br>Número de noches: <b>" + noches
                    + "</b><br>TOTAL ALOJAMIENTO: <b>"
                    + dinero(noches * precio)
                    + "</b></div></html>");
        };

        ingreso.alCambiar(calcular);
        salida.alCambiar(calcular);

        JCheckBox chkReglas = new JCheckBox(
                "Acepto las reglas del alojamiento");
        chkReglas.setFont(new Font(FUENTE, Font.BOLD, 13));
        chkReglas.setForeground(AZUL_OSCURO);
        chkReglas.setOpaque(false);
        chkReglas.setFocusPainted(false);
        chkReglas.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(4));
        columna.add(lblSub);
        columna.add(Box.createVerticalStrut(22));
        columna.add(etiqueta("Mascota"));
        columna.add(Box.createVerticalStrut(6));
        columna.add(cboMascota);
        columna.add(Box.createVerticalStrut(8));
        columna.add(lblCompatible);
        columna.add(Box.createVerticalStrut(14));

        JPanel filaFechas = new JPanel(new GridLayout(1, 2, 12, 0));
        filaFechas.setOpaque(false);
        filaFechas.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaFechas.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 70));

        filaFechas.add(bloqueCampo("Fecha de ingreso", ingreso));
        filaFechas.add(bloqueCampo("Fecha de salida", salida));

        columna.add(filaFechas);
        columna.add(Box.createVerticalStrut(16));
        columna.add(areaTexto("Mensaje para el cuidador",
                txtMensaje, 70));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto(
                "Instrucciones de cuidado (comida, horarios)",
                txtInstrucciones, 70));
        columna.add(Box.createVerticalStrut(18));
        columna.add(lblTotal);
        columna.add(Box.createVerticalStrut(14));
        columna.add(chkReglas);

        BotonRedondeado btnConfirmar = new BotonRedondeado(
                "Confirmar reserva", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnConfirmar.setPreferredSize(new Dimension(210, 46));

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnConfirmar);

        btnConfirmar.addActionListener(e -> {

            LocalDate a = ingreso.getFecha();
            LocalDate b = salida.getFecha();

            if (a == null || b == null) {
                aviso("Selecciona las dos fechas.", false);
                return;
            }

            if (!b.isAfter(a)) {
                aviso("La salida debe ser después del ingreso.",
                        false);
                return;
            }

            if (a.isBefore(LocalDate.now())) {
                aviso("El ingreso no puede ser en el pasado.",
                        false);
                return;
            }

            if (!chkReglas.isSelected()) {
                aviso("Debes aceptar las reglas del alojamiento.",
                        false);
                return;
            }

            if (!compatible[0]) {
                aviso("Este alojamiento no acepta una de las "
                        + "características de tu mascota.", false);
                return;
            }

            int idMascota = (int) mascotas
                    .get(cboMascota.getSelectedIndex())[0];
            String nombreMascota = String.valueOf(mascotas
                    .get(cboMascota.getSelectedIndex())[1]);

            try {

                if (datos.tieneReservaSuperpuesta(idMascota, a, b)) {
                    aviso(nombreMascota + " ya tiene una reserva que "
                            + "coincide con esas fechas.\nElige otro "
                            + "período.", false);
                    return;
                }

                if (!datos.hayDisponibilidad(idAlojamiento, a, b)) {

                    aviso("El alojamiento no tiene cupo en esas "
                            + "fechas.", false);
                    return;
                }

                int idReserva = datos.registrarReserva(
                        a, b,
                        txtMensaje.getText().trim(),
                        txtInstrucciones.getText().trim(),
                        idMascota, idAlojamiento);

                if (idReserva > 0) {
                    JOptionPane.showMessageDialog(dialogo,
                            "Reserva enviada.\nEl cuidador debe "
                                    + "aceptarla.",
                            "Listo",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    mostrar("RESERVAS");
                }

            } catch (RuntimeException ex) {
                String mensaje = ex.getMessage();
                if (mensaje != null && mensaje.toLowerCase()
                        .contains("mascota ya tiene otra reserva")) {
                    aviso(nombreMascota + " ya tiene una reserva que "
                            + "coincide con esas fechas.\nElige otro "
                            + "período.", false);
                } else {
                    aviso(mensaje, true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);

        revisar.run();

        dialogo.setVisible(true);
    }

    private void cargarMascotas() {

        cuerpoMascotas.removeAll();

        try {
            List<Object[]> lista =
                    datos.mascotas(usuario.getIdUsuario());

            if (lista.isEmpty()) {
                cuerpoMascotas.add(vacio(
                                "Todavía no tienes mascotas registradas."),
                        BorderLayout.CENTER);
            } else {

                JPanel rejilla =
                        new JPanel(new GridLayout(0, 3, 16, 16));
                rejilla.setOpaque(false);

                for (Object[] m : lista) {
                    rejilla.add(tarjetaMascota(m));
                }

                cuerpoMascotas.add(desplazable(rejilla),
                        BorderLayout.CENTER);
            }

        } catch (RuntimeException e) {
            cuerpoMascotas.add(error(e.getMessage()),
                    BorderLayout.CENTER);
        }

        cuerpoMascotas.revalidate();
        cuerpoMascotas.repaint();
    }

    private JPanel tarjetaMascota(Object[] m) {

        int idMascota = (int) m[0];

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(16, 0));
        caja.setBorder(new EmptyBorder(20, 20, 20, 20));
        caja.setPreferredSize(new Dimension(300, 178));

        PanelCaja circulo = new PanelCaja(30, AMARILLO_PILL, null);
        circulo.setLayout(new GridBagLayout());
        circulo.setPreferredSize(new Dimension(60, 60));
        circulo.add(new JLabel(icono(HUELLA, AMARILLO_TXT, 30)));

        JPanel columnaIcono = new JPanel(new BorderLayout());
        columnaIcono.setOpaque(false);
        columnaIcono.add(circulo, BorderLayout.NORTH);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblNombre = new JLabel((String) m[1]);
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 19));
        lblNombre.setForeground(AZUL_OSCURO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEspecie = new JLabel(m[2] + "  ·  "
                + texto((String) m[3], "Sin raza"));
        lblEspecie.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblEspecie.setForeground(GRIS_TEXTO);
        lblEspecie.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDatos = new JLabel(m[4] + " kg  ·  "
                + texto(m[5] == null ? null : String.valueOf(m[5]),
                "?") + " años  ·  "
                + ("M".equals(m[6]) ? "Macho" : "Hembra"));
        lblDatos.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblDatos.setForeground(GRIS_PISTA);
        lblDatos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaCategoria =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaCategoria.setOpaque(false);
        filaCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCategoria.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 24));
        filaCategoria.add(pastilla(
                texto((String) m[7], "Sin categoría"),
                new Color(226, 240, 252), AZUL));

        JButton btnFicha = new JButton("Ver ficha completa");
        btnFicha.setFont(new Font(FUENTE, Font.BOLD, 12));
        btnFicha.setForeground(AZUL);
        btnFicha.setBorderPainted(false);
        btnFicha.setContentAreaFilled(false);
        btnFicha.setFocusPainted(false);
        btnFicha.setMargin(new Insets(0, 0, 0, 0));
        btnFicha.setHorizontalAlignment(SwingConstants.LEFT);
        btnFicha.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFicha.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFicha.addActionListener(e -> fichaMascota(idMascota));

        textos.add(lblNombre);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblEspecie);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblDatos);
        textos.add(Box.createVerticalStrut(9));
        textos.add(filaCategoria);
        textos.add(Box.createVerticalStrut(6));
        textos.add(btnFicha);

        caja.add(columnaIcono, BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);

        return caja;
    }

    private void fichaMascota(int idMascota) {

        JDialog dialogo = new JDialog(this,
                "Ficha de la mascota", true);
        dialogo.setSize(640, 700);
        dialogo.setLocationRelativeTo(this);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font(FUENTE, Font.BOLD, 13));
        pestanas.setBackground(Color.WHITE);

        Runnable recargar = () -> {

            int seleccion = Math.max(0, pestanas.getSelectedIndex());
            pestanas.removeAll();

            pestanas.addTab("Datos", pestanaDatosMascota(idMascota));
            pestanas.addTab("Características",
                    pestanaCaracteristicas(idMascota, dialogo));
            pestanas.addTab("Vacunas",
                    pestanaVacunas(idMascota, dialogo));

            if (seleccion < pestanas.getTabCount()) {
                pestanas.setSelectedIndex(seleccion);
            }

            pestanas.revalidate();
            pestanas.repaint();
        };

        recargar.run();

        dialogo.setContentPane(pestanas);
        dialogo.setVisible(true);
    }

    private JComponent pestanaDatosMascota(int idMascota) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 22, 26));

        try {
            Object[] m = datos.mascotaFicha(idMascota,
                    usuario.getIdUsuario());

            if (m == null) {
                columna.add(lineaGris("No se encontró la mascota."));
            } else {

                JLabel lblNombre = new JLabel((String) m[0]);
                lblNombre.setFont(new Font(FUENTE, Font.BOLD, 24));
                lblNombre.setForeground(AZUL_OSCURO);
                lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

                columna.add(lblNombre);
                columna.add(Box.createVerticalStrut(16));

                JPanel rejilla =
                        new JPanel(new GridLayout(0, 2, 12, 12));
                rejilla.setOpaque(false);
                rejilla.setAlignmentX(Component.LEFT_ALIGNMENT);

                rejilla.add(dato("Especie", (String) m[1], HUELLA));
                rejilla.add(dato("Raza", (String) m[2], HUELLA));
                rejilla.add(dato("Edad", m[3] == null ? null
                        : m[3] + " años", RELOJ));
                rejilla.add(dato("Sexo",
                        "M".equals(m[4]) ? "Macho" : "Hembra",
                        PERSONA));
                rejilla.add(dato("Peso", m[5] + " kg", HUELLA));
                rejilla.add(dato("Categoría según peso",
                        (String) m[6], CASA));
                rejilla.add(dato("Color", (String) m[7], HUELLA));
                rejilla.add(dato("Esterilizado",
                        m[8] == null ? null
                                : ((boolean) m[8] ? "Sí" : "No"),
                        VISTO));
                rejilla.add(dato("Microchip", (String) m[9],
                        BUSCAR));
                rejilla.add(dato("Convive con otros",
                        m[13] == null ? null
                                : ((boolean) m[13] ? "Sí" : "No"),
                        HUELLA));
                rejilla.add(dato("Veterinario", (String) m[14],
                        PERSONA));
                rejilla.add(dato("Teléfono veterinario",
                        (String) m[15], TELEFONO));

                columna.add(rejilla);
                columna.add(Box.createVerticalStrut(16));
                columna.add(bloqueTexto("Alergias", (String) m[10]));
                columna.add(Box.createVerticalStrut(10));
                columna.add(bloqueTexto("Enfermedades",
                        (String) m[11]));
                columna.add(Box.createVerticalStrut(10));
                columna.add(bloqueTexto("Comportamiento general",
                        (String) m[12]));
                columna.add(Box.createVerticalStrut(10));
                columna.add(bloqueTexto("Observaciones",
                        (String) m[16]));
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private JComponent pestanaCaracteristicas(int idMascota,
                                              JDialog padre) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));
        encabezado.add(subtitulo("Características y comportamiento"),
                BorderLayout.WEST);

        JButton btnAgregar = botonBlanco("Agregar característica",
                MAS, e -> formularioCaracteristica(idMascota, padre));
        encabezado.add(btnAgregar, BorderLayout.EAST);

        columna.add(encabezado);
        columna.add(Box.createVerticalStrut(14));

        try {
            List<Object[]> lista =
                    datos.caracteristicasMascota(idMascota);

            if (lista.isEmpty()) {
                columna.add(lineaGris(
                        "Esta mascota no tiene características "
                                + "registradas."));
            } else {
                for (Object[] c : lista) {

                    PanelCaja caja = new PanelCaja(12, FONDO_CAMPO,
                            BORDE_CAMPO);
                    caja.setLayout(new BorderLayout(10, 0));
                    caja.setBorder(new EmptyBorder(12, 14, 12, 14));
                    caja.setAlignmentX(Component.LEFT_ALIGNMENT);
                    caja.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 76));

                    JPanel textos = new JPanel();
                    textos.setLayout(
                            new BoxLayout(textos, BoxLayout.Y_AXIS));
                    textos.setOpaque(false);

                    JLabel lbl = new JLabel(c[1] + "  ·  "
                            + texto((String) c[2], "Sin nivel"));
                    lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
                    lbl.setForeground(AZUL_OSCURO);
                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel lblDesc = new JLabel(
                            texto((String) c[3], "Sin descripción"));
                    lblDesc.setFont(new Font(FUENTE, Font.PLAIN, 12));
                    lblDesc.setForeground(GRIS_TEXTO);
                    lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

                    textos.add(lbl);
                    textos.add(Box.createVerticalStrut(3));
                    textos.add(lblDesc);

                    int idRelacion = (int) c[0];

                    JButton btnQuitar = new JButton("Desactivar");
                    btnQuitar.setFont(new Font(FUENTE, Font.BOLD, 12));
                    btnQuitar.setForeground(ROJO);
                    btnQuitar.setBorderPainted(false);
                    btnQuitar.setContentAreaFilled(false);
                    btnQuitar.setFocusPainted(false);
                    btnQuitar.setCursor(
                            new Cursor(Cursor.HAND_CURSOR));
                    btnQuitar.addActionListener(e -> {
                        try {
                            datos.desactivarCaracteristica(idRelacion,
                                    usuario.getIdUsuario());
                            padre.dispose();
                            fichaMascota(idMascota);
                        } catch (RuntimeException ex) {
                            aviso(ex.getMessage(), true);
                        }
                    });

                    caja.add(textos, BorderLayout.CENTER);
                    caja.add(btnQuitar, BorderLayout.EAST);

                    columna.add(caja);
                    columna.add(Box.createVerticalStrut(8));
                }
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private void formularioCaracteristica(int idMascota,
                                          JDialog padre) {

        List<Object[]> catalogo;

        try {
            catalogo = datos.catalogoCaracteristicas();
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        if (catalogo.isEmpty()) {
            aviso("No hay elementos disponibles en el catálogo.",
                    false);
            return;
        }

        JDialog dialogo = new JDialog(padre,
                "Agregar característica", true);
        dialogo.setSize(460, 380);
        dialogo.setLocationRelativeTo(padre);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(24, 28, 20, 28));

        JComboBox<String> cbo = new JComboBox<>();
        for (Object[] c : catalogo) {
            cbo.addItem(c[1] + "  ·  " + c[2]);
        }
        cbo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cbo.setBackground(Color.WHITE);
        cbo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JComboBox<String> cboNivel = new JComboBox<>(
                new String[]{"LEVE", "MODERADO", "ALTO"});
        cboNivel.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboNivel.setBackground(Color.WHITE);
        cboNivel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JTextArea txtDescripcion = new JTextArea();

        columna.add(bloqueCampo("Característica", cbo));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Nivel", cboNivel));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Descripción (opcional)",
                txtDescripcion, 70));

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(160, 44));

        btnGuardar.addActionListener(e -> {

            int idCaracteristica =
                    (int) catalogo.get(cbo.getSelectedIndex())[0];

            try {
                datos.registrarCaracteristicaMascota(
                        (String) cboNivel.getSelectedItem(),
                        txtDescripcion.getText().trim(),
                        idMascota, idCaracteristica,
                        usuario.getIdUsuario());

                dialogo.dispose();
                padre.dispose();
                fichaMascota(idMascota);

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private JComponent pestanaVacunas(int idMascota, JDialog padre) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));
        encabezado.add(subtitulo("Vacunas"), BorderLayout.WEST);
        encabezado.add(botonBlanco("Registrar vacuna", MAS,
                        e -> formularioVacuna(idMascota, padre)),
                BorderLayout.EAST);

        columna.add(encabezado);
        columna.add(Box.createVerticalStrut(14));

        try {
            List<Object[]> lista = datos.vacunasMascota(idMascota);

            if (lista.isEmpty()) {
                columna.add(lineaGris(
                        "Esta mascota no tiene vacunas "
                                + "registradas."));
            } else {
                for (Object[] v : lista) {

                    PanelCaja caja = new PanelCaja(12, FONDO_CAMPO,
                            BORDE_CAMPO);
                    caja.setLayout(new BoxLayout(caja,
                            BoxLayout.Y_AXIS));
                    caja.setBorder(new EmptyBorder(12, 14, 12, 14));
                    caja.setAlignmentX(Component.LEFT_ALIGNMENT);
                    caja.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 92));

                    JLabel lbl = new JLabel((String) v[0]);
                    lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
                    lbl.setForeground(AZUL_OSCURO);
                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

                    Date aplicada = (Date) v[1];
                    Date proxima = (Date) v[2];

                    JLabel lblFechas = new JLabel(
                            "Aplicada: "
                                    + aplicada.toLocalDate()
                                    .format(DIA)
                                    + "     ·     Próxima dosis: "
                                    + (proxima == null ? "No indicada"
                                    : proxima.toLocalDate()
                                      .format(DIA))
                                    + "     ·     "
                                    + texto((String) v[3],
                                    "Sin veterinaria"));
                    lblFechas.setFont(
                            new Font(FUENTE, Font.PLAIN, 12));
                    lblFechas.setForeground(GRIS_TEXTO);
                    lblFechas.setAlignmentX(
                            Component.LEFT_ALIGNMENT);

                    caja.add(lbl);
                    caja.add(Box.createVerticalStrut(4));
                    caja.add(lblFechas);

                    if (proxima != null && proxima.toLocalDate()
                            .isBefore(LocalDate.now())) {

                        JLabel lblVencida =
                                new JLabel("⚠ Dosis vencida");
                        lblVencida.setFont(
                                new Font(FUENTE, Font.BOLD, 12));
                        lblVencida.setForeground(ROJO);
                        lblVencida.setAlignmentX(
                                Component.LEFT_ALIGNMENT);
                        caja.add(Box.createVerticalStrut(4));
                        caja.add(lblVencida);
                    }

                    columna.add(caja);
                    columna.add(Box.createVerticalStrut(8));
                }
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private void formularioVacuna(int idMascota, JDialog padre) {

        List<Object[]> catalogo;

        try {
            catalogo = datos.catalogoVacunas();
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        if (catalogo.isEmpty()) {
            aviso("No hay elementos disponibles en el catálogo.",
                    false);
            return;
        }

        JDialog dialogo = new JDialog(padre, "Registrar vacuna",
                true);
        dialogo.setSize(480, 470);
        dialogo.setLocationRelativeTo(padre);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(24, 28, 20, 28));

        JComboBox<String> cbo = new JComboBox<>();
        for (Object[] v : catalogo) {
            cbo.addItem((String) v[1]);
        }
        cbo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cbo.setBackground(Color.WHITE);
        cbo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        SelectorFecha aplicacion = new SelectorFecha();
        SelectorFecha proxima = new SelectorFecha();

        JTextField txtVeterinaria = campo();
        JTextArea txtObservaciones = new JTextArea();

        columna.add(bloqueCampo("Vacuna", cbo));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Fecha de vacunación", aplicacion),
                bloqueCampo("Próxima dosis (opcional)", proxima)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Veterinaria", txtVeterinaria));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Observaciones", txtObservaciones, 60));

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar vacuna", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(180, 44));

        btnGuardar.addActionListener(e -> {

            LocalDate f1 = aplicacion.getFecha();
            LocalDate f2 = proxima.getFecha();

            if (f1 == null) {
                aviso("Selecciona la fecha de vacunación.", false);
                return;
            }

            if (f2 != null && f2.isBefore(f1)) {
                aviso("La próxima dosis no puede ser anterior a la "
                        + "fecha de vacunación.", false);
                return;
            }

            int idVacuna =
                    (int) catalogo.get(cbo.getSelectedIndex())[0];

            try {
                datos.registrarVacunaMascota(f1, f2,
                        txtVeterinaria.getText().trim(),
                        txtObservaciones.getText().trim(),
                        idMascota, idVacuna,
                        usuario.getIdUsuario());

                dialogo.dispose();
                padre.dispose();
                fichaMascota(idMascota);

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private void formularioMascota() {

        List<Object[]> especies;

        try {
            especies = datos.especies();
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        JDialog dialogo =
                new JDialog(this, "Registrar mascota", true);
        dialogo.setSize(560, 700);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 22, 30));

        JLabel lblTitulo = new JLabel("Nueva mascota");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtNombre = campo();
        JTextField txtRaza = campo();
        JTextField txtColor = campo();
        JTextField txtPeso = campo();
        JTextField txtEdad = campo();
        JTextField txtMicrochip = campo();
        JTextField txtVeterinario = campo();
        JTextField txtTelefonoVet = campo();

        JComboBox<String> cboEspecie = new JComboBox<>();
        for (Object[] e : especies) {
            cboEspecie.addItem((String) e[1]);
        }
        cboEspecie.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboEspecie.setBackground(Color.WHITE);

        JButton btnBuscarEspecie = new JButton();
        btnBuscarEspecie.setIcon(icono(BUSCAR, AZUL, 16));
        btnBuscarEspecie.setToolTipText("Buscar especie");
        btnBuscarEspecie.setPreferredSize(new Dimension(42, 42));
        btnBuscarEspecie.setFocusPainted(false);
        btnBuscarEspecie.setBackground(Color.WHITE);
        btnBuscarEspecie.setBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO));
        btnBuscarEspecie.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        JPanel selectorEspecie = new JPanel(new BorderLayout(6, 0));
        selectorEspecie.setOpaque(false);
        selectorEspecie.add(cboEspecie, BorderLayout.CENTER);
        selectorEspecie.add(btnBuscarEspecie, BorderLayout.EAST);
        selectorEspecie.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        btnBuscarEspecie.addActionListener(ev -> {

            String buscada = JOptionPane.showInputDialog(
                    dialogo,
                    "Escribe el nombre de la especie:",
                    "Buscar especie",
                    JOptionPane.PLAIN_MESSAGE);

            if (buscada == null || buscada.isBlank()) {
                return;
            }

            String textoBuscado = buscada.trim();

            int coincidenciaParcial = -1;

            for (int i = 0; i < cboEspecie.getItemCount(); i++) {

                String especie = cboEspecie.getItemAt(i);

                if (especie.equalsIgnoreCase(textoBuscado)) {
                    cboEspecie.setSelectedIndex(i);
                    return;
                }

                if (coincidenciaParcial < 0
                        && especie.toLowerCase().contains(
                        textoBuscado.toLowerCase())) {
                    coincidenciaParcial = i;
                }
            }

            if (coincidenciaParcial >= 0) {
                cboEspecie.setSelectedIndex(coincidenciaParcial);
            } else {
                JOptionPane.showMessageDialog(
                        dialogo,
                        "No se encontró esa especie.",
                        "Buscar especie",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JComboBox<String> cboSexo =
                new JComboBox<>(new String[]{"Macho", "Hembra"});
        cboSexo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboSexo.setBackground(Color.WHITE);
        cboSexo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JCheckBox chkEsterilizado =
                new JCheckBox("Está esterilizada");
        chkEsterilizado.setFont(new Font(FUENTE, Font.PLAIN, 13));
        chkEsterilizado.setForeground(AZUL_OSCURO);
        chkEsterilizado.setOpaque(false);
        chkEsterilizado.setFocusPainted(false);
        chkEsterilizado.setAlignmentX(Component.LEFT_ALIGNMENT);

        JCheckBox chkConvive =
                new JCheckBox("Convive bien con otras mascotas");
        chkConvive.setFont(new Font(FUENTE, Font.PLAIN, 13));
        chkConvive.setForeground(AZUL_OSCURO);
        chkConvive.setOpaque(false);
        chkConvive.setFocusPainted(false);
        chkConvive.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtAlergias = new JTextArea();
        JTextArea txtComportamiento = new JTextArea();

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(20));

        columna.add(dosColumnas(
                bloqueCampo("Nombre", txtNombre),
                bloqueCampo("Especie", selectorEspecie)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Raza", txtRaza),
                bloqueCampo("Color", txtColor)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Peso en kg", txtPeso),
                bloqueCampo("Edad en años", txtEdad)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Sexo", cboSexo),
                bloqueCampo("Microchip", txtMicrochip)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Veterinario", txtVeterinario),
                bloqueCampo("Teléfono del veterinario",
                        txtTelefonoVet)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Alergias o enfermedades",
                txtAlergias, 60));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Comportamiento general",
                txtComportamiento, 60));
        columna.add(Box.createVerticalStrut(14));
        columna.add(chkEsterilizado);
        columna.add(chkConvive);

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar mascota", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(200, 46));

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        btnGuardar.addActionListener(e -> {

            if (txtNombre.getText().isBlank()
                    || txtPeso.getText().isBlank()
                    || txtEdad.getText().isBlank()) {
                aviso("Nombre, peso y edad son obligatorios.",
                        false);
                return;
            }

            double peso;
            int edad;

            try {
                peso = Double.parseDouble(
                        txtPeso.getText().trim().replace(",", "."));
                edad = Integer.parseInt(txtEdad.getText().trim());
            } catch (NumberFormatException ex) {
                aviso("Peso y edad deben ser números.", false);
                return;
            }

            if (peso <= 0 || edad < 0) {
                aviso("Peso y edad deben ser válidos.", false);
                return;
            }

            int idEspecie = idEspecieSeleccionada(
                    especies, (String) cboEspecie.getSelectedItem());

            if (idEspecie <= 0) {
                aviso("Selecciona una especie válida.", false);
                return;
            }

            try {

                int id = datos.registrarMascota(
                        txtNombre.getText().trim(),
                        cboSexo.getSelectedIndex() == 0 ? "M" : "H",
                        peso,
                        txtColor.getText().trim(),
                        chkEsterilizado.isSelected(),
                        txtMicrochip.getText().trim(),
                        txtAlergias.getText().trim(),
                        txtComportamiento.getText().trim(),
                        chkConvive.isSelected(),
                        txtVeterinario.getText().trim(),
                        txtTelefonoVet.getText().trim(),
                        txtRaza.getText().trim(),
                        usuario.getIdUsuario(),
                        idEspecie,
                        edad);

                if (id > 0) {
                    JOptionPane.showMessageDialog(dialogo,
                            "Mascota registrada correctamente.",
                            "Listo",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    cargarMascotas();
                }

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private int idEspecieSeleccionada(List<Object[]> especies,
                                      String nombreEspecie) {

        if (nombreEspecie == null) {
            return 0;
        }

        for (Object[] e : especies) {
            if (nombreEspecie.equalsIgnoreCase((String) e[1])) {
                return (int) e[0];
            }
        }

        return 0;
    }

    private void cargarReservas() {

        cuerpoReservas.removeAll();

        try {
            List<Object[]> lista =
                    datos.reservas(usuario.getIdUsuario());

            if (lista.isEmpty()) {
                cuerpoReservas.add(vacio(
                                "Todavía no tienes reservas."),
                        BorderLayout.CENTER);
            } else {

                JPanel columna = new JPanel();
                columna.setLayout(
                        new BoxLayout(columna, BoxLayout.Y_AXIS));
                columna.setOpaque(false);

                for (Object[] r : lista) {
                    columna.add(tarjetaReserva(r));
                    columna.add(Box.createVerticalStrut(12));
                }

                cuerpoReservas.add(desplazable(columna),
                        BorderLayout.CENTER);
            }

        } catch (RuntimeException e) {
            cuerpoReservas.add(error(e.getMessage()),
                    BorderLayout.CENTER);
        }

        cuerpoReservas.revalidate();
        cuerpoReservas.repaint();
    }

    private JPanel tarjetaReserva(Object[] r) {

        int idReserva = (int) r[0];
        String mascota = (String) r[1];
        String alojamiento = (String) r[2];
        String ciudad = (String) r[3];
        Date ingreso = (Date) r[4];
        Date salida = (Date) r[5];
        String estado = (String) r[6];
        double totalAlojamiento = (double) r[7];
        double precioNoche = (double) r[8];
        double pagado = (double) r[9];
        int idUsuarioCuidador = (int) r[10];
        String cuidador = (String) r[11];
        boolean calificada = (int) r[12] > 0;
        boolean ofreceTransporte = (boolean) r[13];
        String estadoTransporte = (String) r[14];
        double precioTransporte = (double) r[15];
        boolean pagoPendiente = (int) r[16] > 0;
        boolean pagoAprobado = (int) r[17] > 0;

        long noches = java.time.temporal.ChronoUnit.DAYS.between(
                ingreso.toLocalDate(), salida.toLocalDate());

        boolean hayTransporte = estadoTransporte != null
                && !estadoTransporte.isBlank();

        boolean transporteCotizado = hayTransporte
                && ("PROGRAMADO".equals(estadoTransporte)
                || "FINALIZADO".equals(estadoTransporte))
                && precioTransporte > 0.009;

        boolean transportePendiente =
                "PENDIENTE".equals(estadoTransporte);

        boolean transporteVivo = hayTransporte
                && !"CANCELADO".equals(estadoTransporte);

        double totalFinal = totalAlojamiento
                + (transporteCotizado ? precioTransporte : 0);

        double saldo = Math.max(0, totalFinal - pagado);

        boolean reservaAprobada = "ACEPTADA".equals(estado)
                || "EN_CURSO".equals(estado);

        boolean pagadoCompleto = totalFinal > 0
                && pagado >= totalFinal - 0.009;

        boolean decisionTransportePendiente = reservaAprobada
                && ofreceTransporte
                && !hayTransporte
                && !pagoPendiente
                && pagado <= 0.009;

        boolean puedePagar = reservaAprobada
                && !pagoPendiente
                && !pagadoCompleto
                && !transportePendiente
                && !decisionTransportePendiente
                && totalFinal > 0.009;

        boolean puedeContinuarSinTransporte =
                decisionTransportePendiente
                        && totalAlojamiento > 0.009;

        boolean puedeTransporte = reservaAprobada
                && ofreceTransporte
                && !transporteVivo
                && !pagoPendiente
                && pagado <= 0.009;

        boolean puedePreparar = reservaAprobada;

        boolean puedeCalificar = "FINALIZADA".equals(estado)
                && !calificada;

        boolean puedeCancelar = ("SOLICITADA".equals(estado)
                || "ACEPTADA".equals(estado)) && !pagoAprobado;

        boolean aceptada = "ACEPTADA".equals(estado);

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(18, 0));
        caja.setBorder(new EmptyBorder(20, 22, 20, 22));

        PanelCaja circulo = new PanelCaja(28, AMARILLO_PILL, null);
        circulo.setLayout(new GridBagLayout());
        circulo.setPreferredSize(new Dimension(56, 56));
        circulo.add(new JLabel(icono(HUELLA, AMARILLO_TXT, 28)));

        JPanel columnaIcono = new JPanel(new BorderLayout());
        columnaIcono.setOpaque(false);
        columnaIcono.setPreferredSize(new Dimension(60, 0));
        columnaIcono.add(circulo, BorderLayout.NORTH);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel filaTitulo =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaTitulo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblTitulo = new JLabel(mascota + " en "
                + alojamiento);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 17));
        lblTitulo.setForeground(AZUL_OSCURO);

        filaTitulo.add(lblTitulo);
        filaTitulo.add(pastillaEstado(estado));

        JLabel lblFechas = new JLabel(
                ingreso.toLocalDate().format(DIA) + "  →  "
                        + salida.toLocalDate().format(DIA)
                        + "      ·      " + ciudad
                        + "      ·      " + cuidador);
        lblFechas.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblFechas.setForeground(GRIS_TEXTO);
        lblFechas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblReserva = new JLabel("Reserva #" + idReserva);
        lblReserva.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblReserva.setForeground(AZUL);
        lblReserva.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(filaTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblReserva);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblFechas);

        int extras = 0;

        if (aceptada) {

            JLabel lblAviso = new JLabel(
                    "  El cuidador aceptó tu solicitud.");
            lblAviso.setIcon(icono(VISTO, VERDE, 13));
            lblAviso.setIconTextGap(4);
            lblAviso.setFont(new Font(FUENTE, Font.BOLD, 12));
            lblAviso.setForeground(VERDE);
            lblAviso.setAlignmentX(Component.LEFT_ALIGNMENT);

            textos.add(Box.createVerticalStrut(6));
            textos.add(lblAviso);
            extras++;
        }

        if ("FINALIZADA".equals(estado)) {
            JLabel lblEntregada = new JLabel(
                    "✓ Estadía finalizada. Tu mascota fue entregada.");
            lblEntregada.setFont(new Font(FUENTE, Font.BOLD, 12));
            lblEntregada.setForeground(VERDE);
            lblEntregada.setAlignmentX(Component.LEFT_ALIGNMENT);
            textos.add(Box.createVerticalStrut(6));
            textos.add(lblEntregada);
            extras++;

            if (!calificada) {
                JLabel lblExperiencia = new JLabel(
                        "¿Cómo fue tu experiencia con el cuidador?");
                lblExperiencia.setFont(new Font(FUENTE, Font.PLAIN, 12));
                lblExperiencia.setForeground(AZUL_OSCURO);
                lblExperiencia.setAlignmentX(Component.LEFT_ALIGNMENT);
                textos.add(Box.createVerticalStrut(4));
                textos.add(lblExperiencia);
                extras++;
            }
        }

        String detalleTransporte;

        if (!ofreceTransporte) {
            detalleTransporte = "No disponible";
        } else if (!hayTransporte) {
            detalleTransporte = (pagoPendiente || pagado > 0.009)
                    ? "Sin transporte" : "Pendiente de decisión";
        } else if (transportePendiente) {
            detalleTransporte = "Pendiente de cotización";
        } else if ("CANCELADO".equals(estadoTransporte)) {
            detalleTransporte = "Cancelado";
        } else {
            detalleTransporte = dinero(precioTransporte)
                    + "  (" + estadoTransporte + ")";
        }

        JLabel lblResumen = new JLabel(
                "<html><div style='width:520px;'>"
                        + "Precio por noche: <b>"
                        + dinero(precioNoche) + "</b>"
                        + " &nbsp;·&nbsp; Noches: <b>" + noches
                        + "</b><br>Hospedaje: <b>"
                        + dinero(totalAlojamiento) + "</b>"
                        + " &nbsp;·&nbsp; Transporte: <b>"
                        + detalleTransporte + "</b>"
                        + "<br>Total final: <b>"
                        + dinero(totalFinal) + "</b>"
                        + " &nbsp;·&nbsp; Pagado: <b>"
                        + dinero(pagado) + "</b>"
                        + " &nbsp;·&nbsp; Saldo: <b>"
                        + dinero(saldo) + "</b>"
                        + "</div></html>");
        lblResumen.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblResumen.setForeground(AZUL_OSCURO);
        lblResumen.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(Box.createVerticalStrut(6));
        textos.add(lblResumen);
        extras += 2;

        if (pagadoCompleto) {
            textos.add(Box.createVerticalStrut(6));
            textos.add(etiquetaEstado("✓ Pagado completamente",
                    VERDE));
            extras++;
        }

        if (pagoPendiente) {
            textos.add(Box.createVerticalStrut(6));
            textos.add(etiquetaEstado(
                    "Pago pendiente de verificación",
                    AMARILLO_TXT));
            extras++;
        }

        if (transportePendiente) {
            textos.add(Box.createVerticalStrut(6));
            textos.add(etiquetaEstado(
                    "Transporte pendiente de cotización del "
                            + "cuidador.", AMARILLO_TXT));
            extras++;
        }

        if ("CANCELADO".equals(estadoTransporte)) {
            textos.add(Box.createVerticalStrut(6));
            textos.add(etiquetaEstado(
                    "Solicitud de transporte no aceptada o "
                            + "cancelada.", ROJO));
            extras++;
        }

        JPanel acciones = new JPanel();
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.setOpaque(false);

        int botones = 0;

        if (puedePagar) {
            botones = agregarAccion(acciones, botones,
                    botonAccion("Realizar pago", true,
                            e -> formularioPago(idReserva,
                                    totalAlojamiento, precioNoche,
                                    noches,
                                    transporteCotizado
                                            ? precioTransporte : 0,
                                    transporteCotizado
                                            ? estadoTransporte : null,
                                    totalFinal, ofreceTransporte)));
        }

        if (puedeContinuarSinTransporte) {
            botones = agregarAccion(acciones, botones,
                    botonAccion("Continuar sin transporte", true,
                            e -> formularioPago(idReserva,
                                    totalAlojamiento, precioNoche,
                                    noches, 0, null,
                                    totalAlojamiento, true)));
        }

        if (puedePreparar) {
            botones = agregarAccion(acciones, botones,
                    botonAccion("Preparar estadía", false,
                            e -> prepararEstadia(idReserva)));
        }

        if (puedeTransporte) {
            botones = agregarAccion(acciones, botones,
                    botonAccion("Solicitar transporte", false,
                            e -> formularioTransporte(idReserva,
                                    ingreso, salida)));
        }

        if (transporteVivo) {
            botones = agregarAccion(acciones, botones,
                    botonAccion("Ver transporte", false,
                            e -> verTransporte(idReserva)));
        }

        if (puedeCalificar) {

            BotonRedondeado btnCalificar = new BotonRedondeado(
                    "★ Calificar cuidador", Color.WHITE,
                    AMARILLO_TXT, AMARILLO, null);
            btnCalificar.setFont(new Font(FUENTE, Font.BOLD, 13));
            ajustarBotonAccion(btnCalificar);
            btnCalificar.addActionListener(e -> formularioResena(
                    idReserva, idUsuarioCuidador, cuidador));

            botones = agregarAccion(acciones, botones, btnCalificar);
        }

        if (puedeCancelar) {

            BotonRedondeado btnCancelar = new BotonRedondeado(
                    "Cancelar reserva", Color.WHITE, ROJO,
                    ROJO, null);
            btnCancelar.setIcon(icono(EQUIS, ROJO, 14));
            btnCancelar.setIconTextGap(8);
            btnCancelar.setFont(new Font(FUENTE, Font.BOLD, 13));
            ajustarBotonAccion(btnCancelar);
            btnCancelar.addActionListener(
                    e -> cancelarReserva(idReserva));

            botones = agregarAccion(acciones, botones, btnCancelar);
        }

        if (calificada) {

            JLabel lblGracias = new JLabel("✓ Ya calificaste "
                    + "esta estadía");
            lblGracias.setFont(new Font(FUENTE, Font.PLAIN, 12));
            lblGracias.setForeground(GRIS_PISTA);
            lblGracias.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblGracias.setMaximumSize(new Dimension(200, 24));

            if (botones > 0) {
                acciones.add(Box.createVerticalStrut(8));
            }
            acciones.add(lblGracias);
        }

        JPanel columnaAcciones = new JPanel(new BorderLayout());
        columnaAcciones.setOpaque(false);
        columnaAcciones.setPreferredSize(new Dimension(215, 0));
        columnaAcciones.add(acciones, BorderLayout.NORTH);

        int altoTextos = 80 + extras * 22;
        int altoAcciones = botones > 0
                ? botones * 42 + (botones - 1) * 8 : 0;

        if (calificada) {
            altoAcciones += botones > 0 ? 32 : 24;
        }

        int alto = 40 + Math.max(altoTextos, altoAcciones);

        caja.setPreferredSize(new Dimension(940, alto));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        caja.setMinimumSize(new Dimension(600, alto));

        caja.add(columnaIcono, BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);
        caja.add(columnaAcciones, BorderLayout.EAST);

        return caja;
    }

    private JLabel etiquetaEstado(String t, Color color) {

        JLabel lbl = new JLabel(t);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        return lbl;
    }

    private BotonRedondeado botonAccion(String texto,
                                        boolean principal,
                                        java.awt.event.ActionListener a) {

        BotonRedondeado btn = principal
                ? new BotonRedondeado(texto, AZUL_GRAD_1,
                Color.WHITE, null, AZUL_GRAD_2)
                : new BotonRedondeado(texto, Color.WHITE, AZUL,
                AZUL, null);

        btn.setFont(new Font(FUENTE, Font.BOLD, 13));
        ajustarBotonAccion(btn);
        btn.addActionListener(a);

        return btn;
    }

    private int agregarAccion(JPanel acciones, int botones,
                              JComponent boton) {

        if (botones > 0) {
            acciones.add(Box.createVerticalStrut(8));
        }

        acciones.add(boton);

        return botones + 1;
    }

    private void ajustarBotonAccion(BotonRedondeado boton) {

        Dimension tamano = new Dimension(205, 42);

        boton.setPreferredSize(tamano);
        boton.setMinimumSize(tamano);
        boton.setMaximumSize(tamano);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }





    private void formularioTransporte(int idReserva, Date ingreso,
                                      Date salida) {

        JDialog dialogo = new JDialog(this,
                "Solicitar transporte", true);
        dialogo.setSize(540, 620);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(28, 32, 22, 32));

        JLabel lblTitulo = new JLabel("Solicitar transporte");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 23));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(
                "Servicio opcional para la reserva #" + idReserva);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> cboModalidad = new JComboBox<>(
                new String[]{"RECOGIDA_Y_ENTREGA", "RECOGIDA",
                        "ENTREGA"});
        cboModalidad.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboModalidad.setBackground(Color.WHITE);
        cboModalidad.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JTextField txtRecogida = campo();
        txtRecogida.setText(texto(
                usuario.getDireccionDomicilio(), ""));

        JTextField txtEntrega = campo();
        txtEntrega.setText(texto(
                usuario.getDireccionDomicilio(), ""));

        SelectorFecha fechaRecogida = new SelectorFecha();
        SelectorFecha fechaEntrega = new SelectorFecha();
        fechaRecogida.setFecha(ingreso.toLocalDate());
        fechaEntrega.setFecha(salida.toLocalDate());

        JSpinner horaRecogida = selectorHora();
        JSpinner horaEntrega = selectorHora();

        JPanel bloqueRecogida = bloqueCampo(
                "Dirección de recogida", txtRecogida);
        JPanel bloqueRecogidaFechaHora = dosColumnas(
                bloqueCampo("Fecha de recogida", fechaRecogida),
                bloqueCampo("Hora de recogida (HH:mm)",
                        horaRecogida));
        JPanel bloqueEntrega = bloqueCampo(
                "Dirección de entrega", txtEntrega);
        JPanel bloqueEntregaFechaHora = dosColumnas(
                bloqueCampo("Fecha de entrega", fechaEntrega),
                bloqueCampo("Hora de entrega (HH:mm)", horaEntrega));

        JTextArea txtObservaciones = new JTextArea();

        Runnable ajustar = () -> {

            String modalidad =
                    (String) cboModalidad.getSelectedItem();

            boolean recoge = !"ENTREGA".equals(modalidad);
            boolean entrega = !"RECOGIDA".equals(modalidad);

            bloqueRecogida.setVisible(recoge);
            bloqueRecogidaFechaHora.setVisible(recoge);
            bloqueEntrega.setVisible(entrega);
            bloqueEntregaFechaHora.setVisible(entrega);

            columna.revalidate();
            columna.repaint();
        };

        cboModalidad.addActionListener(e -> ajustar.run());

        JLabel lblInfo = new JLabel(
                "<html><div style='width:420px;'>"
                        + "La solicitud quedará PENDIENTE y sin "
                        + "precio hasta que el cuidador la acepte "
                        + "y la cotice.</div></html>");
        lblInfo.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblInfo.setForeground(GRIS_TEXTO);
        lblInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(4));
        columna.add(lblSub);
        columna.add(Box.createVerticalStrut(20));
        columna.add(bloqueCampo("Modalidad", cboModalidad));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueRecogida);
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueRecogidaFechaHora);
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueEntrega);
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueEntregaFechaHora);
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Observaciones (opcional)",
                txtObservaciones, 70));
        columna.add(Box.createVerticalStrut(14));
        columna.add(lblInfo);

        ajustar.run();

        BotonRedondeado btnConfirmar = new BotonRedondeado(
                "Confirmar solicitud", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnConfirmar.setPreferredSize(new Dimension(220, 46));

        btnConfirmar.addActionListener(e -> {

            String modalidad =
                    (String) cboModalidad.getSelectedItem();

            boolean recoge = !"ENTREGA".equals(modalidad);
            boolean entrega = !"RECOGIDA".equals(modalidad);

            String dirRecogida = recoge
                    ? txtRecogida.getText().trim() : null;
            String dirEntrega = entrega
                    ? txtEntrega.getText().trim() : null;

            if (recoge && (dirRecogida == null
                    || dirRecogida.isBlank())) {
                aviso("Completa la dirección de recogida.", false);
                return;
            }

            if (entrega && (dirEntrega == null
                    || dirEntrega.isBlank())) {
                aviso("Completa la dirección de entrega.", false);
                return;
            }

            LocalDate fechaR = recoge
                    ? fechaRecogida.getFecha() : null;
            LocalDate fechaE = entrega
                    ? fechaEntrega.getFecha() : null;
            LocalTime horaR;
            LocalTime horaE;

            try {
                if (recoge && fechaR == null) {
                    throw new IllegalArgumentException(
                            "Selecciona la fecha de recogida.");
                }
                if (entrega && fechaE == null) {
                    throw new IllegalArgumentException(
                            "Selecciona la fecha de entrega.");
                }
                horaR = recoge
                        ? leerHora(horaRecogida, "la recogida") : null;
                horaE = entrega
                        ? leerHora(horaEntrega, "la entrega") : null;
            } catch (IllegalArgumentException ex) {
                aviso(ex.getMessage(), false);
                return;
            }

            if (recoge && !fechaR.equals(ingreso.toLocalDate())) {
                aviso("La fecha de recogida debe coincidir con el "
                                + "ingreso de la reserva ("
                                + ingreso.toLocalDate().format(DIA) + ").",
                        false);
                return;
            }

            if (entrega && !fechaE.equals(salida.toLocalDate())) {
                aviso("La fecha de entrega debe coincidir con la "
                                + "salida de la reserva ("
                                + salida.toLocalDate().format(DIA) + ").",
                        false);
                return;
            }

            LocalDateTime fRecogida = recoge
                    ? fechaR.atTime(horaR) : null;
            LocalDateTime fEntrega = entrega
                    ? fechaE.atTime(horaE) : null;

            if (fRecogida != null && fEntrega != null
                    && !fEntrega.isAfter(fRecogida)) {
                aviso("La entrega debe ser posterior a la recogida.",
                        false);
                return;
            }

            try {
                datos.solicitarTransporte(modalidad, dirRecogida,
                        dirEntrega, fRecogida, fEntrega,
                        txtObservaciones.getText().trim(),
                        idReserva, usuario.getIdUsuario());

                JOptionPane.showMessageDialog(dialogo,
                        "Solicitud de transporte enviada.\n\n"
                                + "El cuidador debe aceptarla y "
                                + "cotizarla.",
                        "Transporte pendiente",
                        JOptionPane.INFORMATION_MESSAGE);

                dialogo.dispose();
                cargarReservas();

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnConfirmar);

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private JSpinner selectorHora() {
        SpinnerDateModel modelo = new SpinnerDateModel(
                new java.util.Date(), null, null,
                java.util.Calendar.MINUTE);
        JSpinner spinner = new JSpinner(modelo);
        JSpinner.DateEditor editor =
                new JSpinner.DateEditor(spinner, "HH:mm");
        spinner.setEditor(editor);
        spinner.setFont(new Font(FUENTE, Font.PLAIN, 14));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        spinner.setPreferredSize(new Dimension(190, 42));
        editor.getTextField().setHorizontalAlignment(
                SwingConstants.CENTER);
        editor.getTextField().setToolTipText(
                "Selecciona una hora en formato de 24 horas");
        editor.getTextField().setText("");
        return spinner;
    }

    private LocalTime leerHora(JSpinner spinner, String concepto) {
        JFormattedTextField campo = ((JSpinner.DateEditor)
                spinner.getEditor()).getTextField();
        String valor = campo.getText().trim();

        if (valor.isBlank()) {
            throw new IllegalArgumentException(
                    "Selecciona la hora de " + concepto + ".");
        }

        try {
            return LocalTime.parse(valor, HORA_ENTRADA);
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "La hora de " + concepto
                            + " debe usar el formato de 24 horas HH:mm.");
        }
    }

    private void verTransporte(int idReserva) {

        Object[] t;

        try {
            t = datos.transporte(idReserva, usuario.getIdUsuario());
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        if (t == null) {
            aviso("Esta reserva no tiene transporte.", false);
            return;
        }

        JDialog dialogo = new JDialog(this, "Transporte", true);
        dialogo.setSize(520, 560);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 22, 30));

        String estado = (String) t[1];

        JLabel lblTitulo = new JLabel("Transporte de la reserva #"
                + idReserva);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 21));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(16));

        JPanel rejilla = new JPanel(new GridLayout(0, 2, 12, 12));
        rejilla.setOpaque(false);
        rejilla.setAlignmentX(Component.LEFT_ALIGNMENT);
        rejilla.add(dato("Modalidad", (String) t[0], UBICACION));
        rejilla.add(dato("Estado", estado, RELOJ));
        rejilla.add(dato("Dirección de recogida", (String) t[3],
                UBICACION));
        rejilla.add(dato("Dirección de entrega", (String) t[4],
                UBICACION));
        rejilla.add(dato("Fecha y hora de recogida",
                t[5] == null ? null
                        : ((Timestamp) t[5]).toLocalDateTime()
                          .format(DIA_HORA), RELOJ));
        rejilla.add(dato("Fecha y hora de entrega",
                t[6] == null ? null
                        : ((Timestamp) t[6]).toLocalDateTime()
                          .format(DIA_HORA), RELOJ));
        rejilla.add(dato("Cotización",
                "PENDIENTE".equals(estado)
                        ? "Aún no cotizado"
                        : dinero((double) t[2]), DINERO));
        rejilla.add(dato("Pagado de la reserva",
                dinero((double) t[8]), DINERO));

        columna.add(rejilla);
        columna.add(Box.createVerticalStrut(16));
        columna.add(bloqueTexto("Observaciones",
                limpiarMarcas((String) t[7])));

        if ("CANCELADO".equals(estado)) {
            columna.add(Box.createVerticalStrut(12));
            columna.add(etiquetaEstado(
                    "Solicitud de transporte no aceptada por el "
                            + "cuidador.", ROJO));
        }

        BotonRedondeado btnCerrar = new BotonRedondeado(
                "Cerrar", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnCerrar.setPreferredSize(new Dimension(150, 44));
        btnCerrar.addActionListener(e -> dialogo.dispose());

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));

        if ("PENDIENTE".equals(estado)) {

            BotonRedondeado btnCancelar = new BotonRedondeado(
                    "Cancelar solicitud", Color.WHITE, ROJO,
                    ROJO, null);
            btnCancelar.setPreferredSize(new Dimension(190, 44));
            btnCancelar.addActionListener(e -> {

                int r = JOptionPane.showConfirmDialog(dialogo,
                        "¿Cancelar la solicitud de transporte?",
                        "Cancelar transporte",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (r != JOptionPane.YES_OPTION) {
                    return;
                }

                try {
                    datos.cancelarTransporte(idReserva,
                            usuario.getIdUsuario());
                    dialogo.dispose();
                    cargarReservas();
                } catch (RuntimeException ex) {
                    aviso(ex.getMessage(), true);
                }
            });

            pie.add(btnCancelar);
        }

        pie.add(btnCerrar);

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }





    private void prepararEstadia(int idReserva) {

        JDialog dialogo = new JDialog(this,
                "Preparar estadía · Reserva #" + idReserva, true);
        dialogo.setSize(640, 700);
        dialogo.setLocationRelativeTo(this);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font(FUENTE, Font.BOLD, 13));
        pestanas.setBackground(Color.WHITE);

        pestanas.addTab("Cuidados", pestanaCuidados(idReserva));
        pestanas.addTab("Medicación",
                pestanaMedicacion(idReserva, dialogo));
        pestanas.addTab("Accesorios",
                pestanaAccesorios(idReserva, dialogo));

        dialogo.setContentPane(pestanas);
        dialogo.setVisible(true);
    }

    private JComponent pestanaCuidados(int idReserva) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 20, 26));

        JTextField txtTipo = campo();
        JTextField txtCantidad = campo();
        JTextField txtHorarioComida = campo();
        JTextField txtHorarioPaseo = campo();
        JTextArea txtCuidados = new JTextArea();
        JTextArea txtComportamiento = new JTextArea();
        JTextArea txtObservaciones = new JTextArea();

        try {
            Object[] d = datos.detalleCuidado(idReserva,
                    usuario.getIdUsuario());

            if (d != null) {
                txtTipo.setText(texto((String) d[0], ""));
                txtCantidad.setText(texto((String) d[1], ""));
                txtHorarioComida.setText(texto((String) d[2], ""));
                txtHorarioPaseo.setText(texto((String) d[3], ""));
                txtCuidados.setText(texto((String) d[4], ""));
                txtComportamiento.setText(texto((String) d[5], ""));
                txtObservaciones.setText(texto((String) d[6], ""));
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        columna.add(subtitulo("Alimentación"));
        columna.add(Box.createVerticalStrut(10));
        columna.add(dosColumnas(
                bloqueCampo("Tipo de alimento", txtTipo),
                bloqueCampo("Cantidad", txtCantidad)));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueCampo("Horario de alimentación",
                txtHorarioComida));
        columna.add(Box.createVerticalStrut(18));
        columna.add(subtitulo("Paseos"));
        columna.add(Box.createVerticalStrut(10));
        columna.add(bloqueCampo("Horario de paseo",
                txtHorarioPaseo));
        columna.add(Box.createVerticalStrut(18));
        columna.add(areaTexto("Cuidados especiales",
                txtCuidados, 70));
        columna.add(Box.createVerticalStrut(12));
        columna.add(areaTexto("Comportamiento importante",
                txtComportamiento, 70));
        columna.add(Box.createVerticalStrut(12));
        columna.add(areaTexto("Observaciones",
                txtObservaciones, 70));

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar cuidados", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(190, 44));
        btnGuardar.setMaximumSize(new Dimension(190, 44));
        btnGuardar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGuardar.addActionListener(e -> {
            try {
                datos.guardarDetalleCuidado(
                        txtTipo.getText().trim(),
                        txtCantidad.getText().trim(),
                        txtHorarioComida.getText().trim(),
                        txtHorarioPaseo.getText().trim(),
                        txtCuidados.getText().trim(),
                        txtComportamiento.getText().trim(),
                        txtObservaciones.getText().trim(),
                        idReserva, usuario.getIdUsuario());

                JOptionPane.showMessageDialog(this,
                        "Cuidados guardados correctamente.",
                        "Listo", JOptionPane.INFORMATION_MESSAGE);

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        columna.add(Box.createVerticalStrut(18));
        columna.add(btnGuardar);

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private JComponent pestanaMedicacion(int idReserva,
                                         JDialog padre) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 20, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));
        encabezado.add(subtitulo("Medicación de la estadía"),
                BorderLayout.WEST);
        encabezado.add(botonBlanco("Agregar medicamento", MAS,
                        e -> formularioMedicamento(idReserva, padre)),
                BorderLayout.EAST);

        columna.add(encabezado);
        columna.add(Box.createVerticalStrut(14));

        try {
            List<Object[]> lista = datos.medicamentos(idReserva,
                    usuario.getIdUsuario());

            if (lista.isEmpty()) {
                columna.add(lineaGris(
                        "No registraste medicamentos para esta "
                                + "estadía."));
            } else {
                for (Object[] m : lista) {

                    PanelCaja caja = new PanelCaja(12, FONDO_CAMPO,
                            BORDE_CAMPO);
                    caja.setLayout(new BoxLayout(caja,
                            BoxLayout.Y_AXIS));
                    caja.setBorder(new EmptyBorder(12, 14, 12, 14));
                    caja.setAlignmentX(Component.LEFT_ALIGNMENT);
                    caja.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 94));

                    JLabel lbl = new JLabel(m[0] + "  ·  " + m[1]);
                    lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
                    lbl.setForeground(AZUL_OSCURO);
                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel lbl2 = new JLabel(
                            texto((String) m[2], "Sin frecuencia")
                                    + "  ·  "
                                    + texto((String) m[3],
                                    "Sin horario")
                                    + "  ·  Entregado: "
                                    + (m[6] == null ? "-"
                                    : m[6] + " "
                                      + texto((String) m[7], "")));
                    lbl2.setFont(new Font(FUENTE, Font.PLAIN, 12));
                    lbl2.setForeground(GRIS_TEXTO);
                    lbl2.setAlignmentX(Component.LEFT_ALIGNMENT);

                    caja.add(lbl);
                    caja.add(Box.createVerticalStrut(4));
                    caja.add(lbl2);

                    if (m[8] != null
                            && !((String) m[8]).isBlank()) {
                        caja.add(Box.createVerticalStrut(4));
                        caja.add(lineaGris(
                                "Indicaciones: " + m[8]));
                    }

                    columna.add(caja);
                    columna.add(Box.createVerticalStrut(8));
                }
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private void formularioMedicamento(int idReserva,
                                       JDialog padre) {

        JDialog dialogo = new JDialog(padre, "Agregar medicamento",
                true);
        dialogo.setSize(520, 620);
        dialogo.setLocationRelativeTo(padre);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(24, 28, 20, 28));

        JTextField txtNombre = campo();
        JTextField txtDosis = campo();
        JTextField txtFrecuencia = campo();
        JTextField txtHorario = campo();
        JTextField txtCantidad = campo();
        JTextField txtUnidad = campo();
        JTextArea txtIndicaciones = new JTextArea();

        SelectorFecha inicio = new SelectorFecha();
        SelectorFecha fin = new SelectorFecha();

        columna.add(dosColumnas(
                bloqueCampo("Nombre del medicamento", txtNombre),
                bloqueCampo("Dosis", txtDosis)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Frecuencia", txtFrecuencia),
                bloqueCampo("Horario", txtHorario)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Fecha de inicio", inicio),
                bloqueCampo("Fecha de fin", fin)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(dosColumnas(
                bloqueCampo("Cantidad entregada", txtCantidad),
                bloqueCampo("Unidad de medida", txtUnidad)));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Indicaciones", txtIndicaciones, 70));

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar medicamento", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(210, 44));

        btnGuardar.addActionListener(e -> {

            if (txtNombre.getText().isBlank()
                    || txtDosis.getText().isBlank()) {
                aviso("El nombre y la dosis son obligatorios.",
                        false);
                return;
            }

            LocalDate f1 = inicio.getFecha();
            LocalDate f2 = fin.getFecha();

            if (f1 != null && f2 != null && f2.isBefore(f1)) {
                aviso("La fecha de fin no puede ser anterior al "
                        + "inicio.", false);
                return;
            }

            BigDecimal cantidad = null;

            if (!txtCantidad.getText().isBlank()) {
                try {
                    cantidad = new BigDecimal(txtCantidad.getText()
                            .trim().replace(",", "."))
                            .setScale(2, RoundingMode.HALF_UP);
                } catch (NumberFormatException ex) {
                    aviso("La cantidad entregada debe ser un "
                            + "número.", false);
                    return;
                }

                if (cantidad.signum() < 0) {
                    aviso("La cantidad no puede ser negativa.",
                            false);
                    return;
                }
            }

            try {
                datos.registrarMedicamento(
                        txtNombre.getText().trim(),
                        txtDosis.getText().trim(),
                        txtFrecuencia.getText().trim(),
                        txtHorario.getText().trim(),
                        f1, f2, cantidad,
                        txtUnidad.getText().trim(),
                        txtIndicaciones.getText().trim(),
                        idReserva, usuario.getIdUsuario());

                dialogo.dispose();
                padre.dispose();
                prepararEstadia(idReserva);

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private JComponent pestanaAccesorios(int idReserva,
                                         JDialog padre) {

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(22, 26, 20, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));
        encabezado.add(subtitulo("Accesorios entregados"),
                BorderLayout.WEST);
        encabezado.add(botonBlanco("Agregar accesorio", MAS,
                        e -> formularioAccesorio(idReserva, padre)),
                BorderLayout.EAST);

        columna.add(encabezado);
        columna.add(Box.createVerticalStrut(14));

        try {
            List<Object[]> lista = datos.accesorios(idReserva,
                    usuario.getIdUsuario());

            if (lista.isEmpty()) {
                columna.add(lineaGris(
                        "No registraste accesorios para esta "
                                + "estadía."));
            } else {
                for (Object[] ac : lista) {

                    PanelCaja caja = new PanelCaja(12, FONDO_CAMPO,
                            BORDE_CAMPO);
                    caja.setLayout(new BoxLayout(caja,
                            BoxLayout.Y_AXIS));
                    caja.setBorder(new EmptyBorder(12, 14, 12, 14));
                    caja.setAlignmentX(Component.LEFT_ALIGNMENT);
                    caja.setMaximumSize(
                            new Dimension(Integer.MAX_VALUE, 94));

                    JLabel lbl = new JLabel(ac[0]
                            + "   ·   Cantidad: " + ac[2]);
                    lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
                    lbl.setForeground(AZUL_OSCURO);
                    lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel lbl2 = new JLabel(
                            "Entrega: "
                                    + texto((String) ac[3],
                                    "Sin registrar")
                                    + "     ·     Devolución: "
                                    + texto((String) ac[4],
                                    "Sin registrar"));
                    lbl2.setFont(new Font(FUENTE, Font.PLAIN, 12));
                    lbl2.setForeground(GRIS_TEXTO);
                    lbl2.setAlignmentX(Component.LEFT_ALIGNMENT);

                    caja.add(lbl);
                    caja.add(Box.createVerticalStrut(4));
                    caja.add(lineaGris(
                            texto((String) ac[1],
                                    "Sin descripción")));
                    caja.add(Box.createVerticalStrut(4));
                    caja.add(lbl2);

                    columna.add(caja);
                    columna.add(Box.createVerticalStrut(8));
                }
            }

        } catch (RuntimeException e) {
            columna.add(lineaGris(e.getMessage()));
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private void formularioAccesorio(int idReserva, JDialog padre) {

        JDialog dialogo = new JDialog(padre, "Agregar accesorio",
                true);
        dialogo.setSize(480, 430);
        dialogo.setLocationRelativeTo(padre);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(24, 28, 20, 28));

        JTextField txtNombre = campo();
        JTextField txtCantidad = campo();
        txtCantidad.setText("1");
        JTextField txtDescripcion = campo();
        JTextArea txtObservaciones = new JTextArea();

        columna.add(bloqueCampo("Nombre del accesorio", txtNombre));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Descripción", txtDescripcion));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Cantidad", txtCantidad));
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Observaciones", txtObservaciones, 70));

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar accesorio", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(190, 44));

        btnGuardar.addActionListener(e -> {

            if (txtNombre.getText().isBlank()) {
                aviso("Escribe el nombre del accesorio.", false);
                return;
            }

            int cantidad;

            try {
                cantidad = Integer.parseInt(
                        txtCantidad.getText().trim());
            } catch (NumberFormatException ex) {
                aviso("La cantidad debe ser un número.", false);
                return;
            }

            if (cantidad <= 0) {
                aviso("La cantidad debe ser mayor a cero.", false);
                return;
            }

            try {
                datos.registrarAccesorio(
                        txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(),
                        cantidad,
                        txtObservaciones.getText().trim(),
                        idReserva, usuario.getIdUsuario());

                dialogo.dispose();
                padre.dispose();
                prepararEstadia(idReserva);

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private void cancelarReserva(int idReserva) {

        Object[] estadoCancelacion;

        try {
            estadoCancelacion = datos.estadoCancelacion(idReserva,
                    usuario.getIdUsuario());
        } catch (RuntimeException e) {
            aviso(e.getMessage(), true);
            return;
        }

        String estadoActual = (String) estadoCancelacion[0];
        boolean pagoAprobado = (boolean) estadoCancelacion[1];
        boolean pagoPendiente = (boolean) estadoCancelacion[2];

        if (!"SOLICITADA".equals(estadoActual)
                && !"ACEPTADA".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this,
                    "Esta reserva ya no puede cancelarse en su estado actual.",
                    "Reserva no cancelable",
                    JOptionPane.WARNING_MESSAGE);
            cargarReservas();
            return;
        }

        if (pagoAprobado) {
            JOptionPane.showMessageDialog(this,
                    "No puedes cancelar esta reserva porque ya existe un pago aprobado.",
                    "Reserva no cancelable",
                    JOptionPane.WARNING_MESSAGE);
            cargarReservas();
            return;
        }

        String mensajeConfirmacion = pagoPendiente
                ? "Esta reserva tiene un pago pendiente.\n"
                  + "Al cancelar la reserva, ese pago será anulado.\n"
                  + "¿Deseas continuar?"
                : "¿Realmente deseas cancelar esta reserva?";

        int confirmar = JOptionPane.showConfirmDialog(this,
                mensajeConfirmacion, "Cancelar reserva",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            datos.cancelarReserva(idReserva,
                    usuario.getIdUsuario());

            JOptionPane.showMessageDialog(this,
                    "Reserva cancelada.", "Listo",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarReservas();

        } catch (RuntimeException e) {
            if ("No puedes cancelar esta reserva porque ya existe un pago aprobado."
                    .equals(e.getMessage())) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Reserva no cancelable",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                aviso(e.getMessage(), true);
            }
            cargarReservas();
        }
    }





    private void formularioResena(int idReserva, int idUsuarioCuidador,
                                  String nombreCuidador) {

        JDialog dialogo = new JDialog(this,
                "Calificar la estadía", true);
        dialogo.setSize(520, 500);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(30, 34, 24, 34));

        JLabel lblTitulo = new JLabel("¿Cómo te fue?");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Califica a " + nombreCuidador);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 14));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        SelectorEstrellas estrellas = new SelectorEstrellas();
        estrellas.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblNota = new JLabel("Toca una estrella");
        lblNota.setFont(new Font(FUENTE, Font.BOLD, 15));
        lblNota.setForeground(GRIS_PISTA);
        lblNota.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] frases = {
                "Muy mala experiencia", "Mala experiencia",
                "Aceptable", "Buena experiencia", "¡Excelente!"
        };

        estrellas.alCambiar(valor -> {
            lblNota.setText(valor + " de 5  ·  " + frases[valor - 1]);
            lblNota.setForeground(valor <= 2 ? ROJO : AZUL_OSCURO);
        });

        JTextArea txtComentario = new JTextArea();

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(6));
        columna.add(lblSub);
        columna.add(Box.createVerticalStrut(26));
        columna.add(estrellas);
        columna.add(Box.createVerticalStrut(12));
        columna.add(lblNota);
        columna.add(Box.createVerticalStrut(24));
        columna.add(areaTexto("Cuéntanos tu experiencia",
                txtComentario, 90));

        BotonRedondeado btnEnviar = new BotonRedondeado(
                "Enviar calificación", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnEnviar.setPreferredSize(new Dimension(220, 46));

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnEnviar);

        btnEnviar.addActionListener(e -> {

            int calificacionSeleccionada = estrellas.getValor();

            if (calificacionSeleccionada == 0) {
                aviso("Selecciona al menos una estrella.", false);
                return;
            }

            try {

                if (datos.yaCalifico(idReserva,
                        usuario.getIdUsuario())) {
                    aviso("Ya calificaste esta estadía.", false);
                    dialogo.dispose();
                    cargarReservas();
                    return;
                }

                datos.registrarResena(calificacionSeleccionada,
                        txtComentario.getText().trim(),
                        idReserva, usuario.getIdUsuario(),
                        idUsuarioCuidador);

                dialogo.dispose();

                JOptionPane.showMessageDialog(this,
                        "¡Gracias por tu calificación!",
                        "Listo",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarReservas();

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }





    private void formularioPago(int idReserva,
                                double totalAlojamiento,
                                double precioNoche,
                                long noches,
                                double precioTransporte,
                                String estadoTransporte,
                                double totalFinal,
                                boolean ofreceTransporte) {

        try {
            if (datos.existePagoPendiente(idReserva)) {
                aviso("Ya existe un pago pendiente de "
                        + "verificación para esta reserva.", false);
                cargarReservas();
                return;
            }
        } catch (RuntimeException ex) {
            aviso(ex.getMessage(), true);
            return;
        }

        JDialog dialogo = new JDialog(this, "Realizar pago", true);
        dialogo.setSize(520, 620);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(28, 32, 22, 32));

        JLabel lblTitulo = new JLabel("Realizar pago");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 23));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Reserva #" + idReserva);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelCaja resumen =
                new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        resumen.setLayout(new BoxLayout(resumen, BoxLayout.Y_AXIS));
        resumen.setBorder(new EmptyBorder(16, 18, 16, 18));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        resumen.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 190));

        resumen.add(subtitulo("Resumen de tu reserva"));
        resumen.add(Box.createVerticalStrut(12));
        resumen.add(lineaResumen("Hospedaje",
                dinero(precioNoche) + " × " + noches + " noches",
                dinero(totalAlojamiento)));
        resumen.add(Box.createVerticalStrut(8));
        resumen.add(lineaResumen("Transporte",
                precioTransporte > 0.009
                        ? texto(estadoTransporte, "Cotizado")
                        : (ofreceTransporte
                           ? "Sin transporte" : "No disponible"),
                precioTransporte > 0.009
                        ? dinero(precioTransporte) : "—"));
        resumen.add(Box.createVerticalStrut(12));

        JPanel separador = new JPanel();
        separador.setBackground(BORDE_CAMPO);
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setPreferredSize(new Dimension(400, 1));
        resumen.add(separador);
        resumen.add(Box.createVerticalStrut(12));

        JPanel filaTotal = new JPanel(new BorderLayout());
        filaTotal.setOpaque(false);
        filaTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaTotal.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 34));

        JLabel lblEtiquetaTotal = new JLabel("TOTAL A PAGAR");
        lblEtiquetaTotal.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblEtiquetaTotal.setForeground(AZUL_OSCURO);

        JLabel lblTotal = new JLabel(dinero(totalFinal));
        lblTotal.setFont(new Font(FUENTE, Font.BOLD, 22));
        lblTotal.setForeground(AZUL_OSCURO);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        filaTotal.add(lblEtiquetaTotal, BorderLayout.WEST);
        filaTotal.add(lblTotal, BorderLayout.EAST);
        resumen.add(filaTotal);

        JComboBox<String> cboForma = new JComboBox<>(
                new String[]{"TRANSFERENCIA", "EFECTIVO"});
        cboForma.setFont(new Font(FUENTE, Font.PLAIN, 14));
        cboForma.setBackground(Color.WHITE);
        cboForma.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));

        JButton btnCuenta = new JButton("Ver datos de cuenta");
        btnCuenta.setIcon(icono(DINERO, AZUL, 15));
        btnCuenta.setIconTextGap(8);
        btnCuenta.setFont(new Font(FUENTE, Font.BOLD, 13));
        btnCuenta.setForeground(AZUL);
        btnCuenta.setBackground(Color.WHITE);
        btnCuenta.setFocusPainted(false);
        btnCuenta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL),
                new EmptyBorder(9, 16, 9, 16)));
        btnCuenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCuenta.addActionListener(
                e -> mostrarDatosCuenta(dialogo));

        JPanel filaCuenta =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaCuenta.setOpaque(false);
        filaCuenta.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaCuenta.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 46));
        filaCuenta.add(btnCuenta);

        JTextField txtComprobante = campo();
        JTextArea txtObservaciones = new JTextArea();

        JPanel bloqueComprobante = bloqueCampo(
                "Número de comprobante / referencia",
                txtComprobante);

        JLabel lblEfectivo = new JLabel(
                "<html><div style='width:390px;'>"
                        + "<b>Pago en efectivo</b><br>"
                        + "El pago se realizará presencialmente y el "
                        + "cuidador deberá confirmarlo."
                        + "</div></html>");
        lblEfectivo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblEfectivo.setForeground(GRIS_TEXTO);

        PanelCaja cajaEfectivo =
                new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        cajaEfectivo.setLayout(new BorderLayout());
        cajaEfectivo.setBorder(new EmptyBorder(14, 16, 14, 16));
        cajaEfectivo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cajaEfectivo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 80));
        cajaEfectivo.add(lblEfectivo, BorderLayout.CENTER);
        cajaEfectivo.setVisible(false);

        BotonRedondeado btnConfirmar = new BotonRedondeado(
                "Enviar comprobante", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnConfirmar.setPreferredSize(new Dimension(230, 46));

        cboForma.addActionListener(e -> {

            boolean transferencia = "TRANSFERENCIA".equals(
                    cboForma.getSelectedItem());

            filaCuenta.setVisible(transferencia);
            bloqueComprobante.setVisible(transferencia);
            cajaEfectivo.setVisible(!transferencia);

            btnConfirmar.setText(transferencia
                    ? "Enviar comprobante"
                    : "Confirmar pago en efectivo");

            columna.revalidate();
            columna.repaint();
        });

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(4));
        columna.add(lblSub);
        columna.add(Box.createVerticalStrut(18));
        columna.add(resumen);
        columna.add(Box.createVerticalStrut(18));
        columna.add(bloqueCampo("Forma de pago", cboForma));
        columna.add(Box.createVerticalStrut(10));
        columna.add(filaCuenta);
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueComprobante);
        columna.add(cajaEfectivo);
        columna.add(Box.createVerticalStrut(14));
        columna.add(areaTexto("Observaciones",
                txtObservaciones, 60));

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnConfirmar);

        btnConfirmar.addActionListener(e -> {

            String forma = (String) cboForma.getSelectedItem();

            boolean transferencia = "TRANSFERENCIA".equals(forma);

            if (transferencia
                    && txtComprobante.getText().isBlank()) {
                aviso("Ingresa el número de comprobante o "
                        + "referencia de la transferencia.", false);
                return;
            }

            try {

                if (datos.existePagoPendiente(idReserva)) {
                    aviso("Ya existe un pago pendiente para esta "
                            + "reserva.", false);
                    dialogo.dispose();
                    cargarReservas();
                    return;
                }

                datos.registrarPago(BigDecimal.valueOf(totalFinal), forma,
                        transferencia
                                ? txtComprobante.getText().trim()
                                : null,
                        txtObservaciones.getText().trim(),
                        idReserva, usuario.getIdUsuario());

                if (transferencia) {
                    JOptionPane.showMessageDialog(dialogo,
                            "Comprobante enviado correctamente.\n\n"
                                    + "Tu pago está pendiente de "
                                    + "verificación.",
                            "Pago pendiente de verificación",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialogo,
                            "Pago en efectivo registrado.\n\n"
                                    + "Deberás entregar el dinero al "
                                    + "cuidador.",
                            "Pago pendiente de confirmación",
                            JOptionPane.INFORMATION_MESSAGE);
                }

                dialogo.dispose();
                cargarReservas();

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }

    private JPanel lineaResumen(String titulo, String detalle,
                                String valor) {

        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDetalle = new JLabel(detalle);
        lblDetalle.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblDetalle.setForeground(GRIS_TEXTO);
        lblDetalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(lbl);
        textos.add(Box.createVerticalStrut(2));
        textos.add(lblDetalle);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(textos, BorderLayout.CENTER);
        fila.add(lblValor, BorderLayout.EAST);

        return fila;
    }

    private String limpiarMarcas(String observaciones) {

        if (observaciones == null) {
            return null;
        }

        return observaciones
                .replace(MARCA_CANCELADO_USUARIO, "")
                .replace(MARCA_RECHAZADO_CUIDADOR, "")
                .trim();
    }

    private void mostrarDatosCuenta(JDialog padre) {

        JDialog dialogo = new JDialog(padre,
                "Datos para transferencia", true);
        dialogo.setSize(420, 360);
        dialogo.setLocationRelativeTo(padre);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 28, 24, 28));

        JLabel lblTitulo = new JLabel("Datos para transferencia");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelCaja caja = new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new GridLayout(4, 2, 10, 12));
        caja.setBorder(new EmptyBorder(18, 18, 18, 18));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        agregarDatoCuenta(caja, "BENEFICIARIO", "PETHOMEEC");
        agregarDatoCuenta(caja, "BANCO", "PICHINCHA");
        agregarDatoCuenta(caja, "TIPO DE CUENTA", "CORRIENTE");
        agregarDatoCuenta(caja, "NÚMERO DE CUENTA", "0013210018");

        JLabel lblNota = new JLabel(
                "<html><div style='width:330px;'>"
                        + "Realiza la transferencia y guarda tu "
                        + "comprobante.</div></html>");
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblNota.setForeground(GRIS_TEXTO);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        BotonRedondeado btnCerrar = new BotonRedondeado(
                "Entendido", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnCerrar.setPreferredSize(new Dimension(150, 42));
        btnCerrar.setMaximumSize(new Dimension(150, 42));
        btnCerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCerrar.addActionListener(e -> dialogo.dispose());

        columna.add(lblTitulo);
        columna.add(Box.createVerticalStrut(18));
        columna.add(caja);
        columna.add(Box.createVerticalStrut(16));
        columna.add(lblNota);
        columna.add(Box.createVerticalStrut(18));
        columna.add(btnCerrar);

        dialogo.setContentPane(columna);
        dialogo.setVisible(true);
    }

    private void agregarDatoCuenta(JPanel caja, String etiqueta,
                                   String valor) {

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblEtiqueta.setForeground(GRIS_TEXTO);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setHorizontalAlignment(SwingConstants.RIGHT);

        caja.add(lblEtiqueta);
        caja.add(lblValor);
    }

    private void cargarPagos() {

        cuerpoPagos.removeAll();

        try {
            List<Object[]> lista =
                    datos.pagos(usuario.getIdUsuario());

            if (lista.isEmpty()) {
                cuerpoPagos.add(vacio("Todavía no tienes pagos."),
                        BorderLayout.CENTER);
            } else {

                String[] columnas = {"#", "Reserva", "Fecha",
                        "Alojamiento", "Forma de pago",
                        "Tipo", "Estado", "Monto"};

                Object[][] filas = new Object[lista.size()][];

                for (int i = 0; i < lista.size(); i++) {

                    Object[] p = lista.get(i);

                    filas[i] = new Object[]{
                            p[0], p[1], p[2], p[3],
                            p[4], p[5], p[6], p[7]
                    };
                }

                cuerpoPagos.add(construirTabla(columnas, filas, 6),
                        BorderLayout.CENTER);
            }

        } catch (RuntimeException e) {
            cuerpoPagos.add(error(e.getMessage()),
                    BorderLayout.CENTER);
        }

        cuerpoPagos.revalidate();
        cuerpoPagos.repaint();
    }





    private void cargarPerfil() {

        cuerpoPerfil.removeAll();

        Object[] perfil;
        try {
            perfil = datos.perfilUsuario(usuario.getIdUsuario());
            if (perfil == null) {
                cuerpoPerfil.add(error("No se encontró tu perfil."),
                        BorderLayout.CENTER);
                return;
            }
        } catch (RuntimeException e) {
            cuerpoPerfil.add(error(e.getMessage()),
                    BorderLayout.CENTER);
            return;
        }

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setOpaque(false);

        PanelCaja tarjeta = new PanelCaja(16, Color.WHITE,
                BORDE_CAMPO);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(28, 30, 28, 30));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);

        String nombre = texto((String) perfil[0], "") + " "
                + texto((String) perfil[1], "");

        JPanel cabecera = new JPanel(new BorderLayout(18, 0));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 90));

        JPanel cajaAvatar = new JPanel(new GridBagLayout());
        cajaAvatar.setOpaque(false);
        cajaAvatar.setPreferredSize(new Dimension(86, 86));
        String rutaPerfil = (String) perfil[6];
        if (rutaPerfil == null || rutaPerfil.isBlank()) {
            cajaAvatar.add(new Avatar(80, iniciales(nombre)));
        } else {
            cajaAvatar.add(cargarImagenLocal(rutaPerfil, 80, 80));
        }

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblNombre = new JLabel(nombre.trim());
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblNombre.setForeground(AZUL_OSCURO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaRol =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaRol.setOpaque(false);
        filaRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaRol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        filaRol.add(pastilla(texto((String) perfil[7],
                        "USUARIO"),
                new Color(226, 240, 252), AZUL));

        textos.add(lblNombre);
        textos.add(Box.createVerticalStrut(8));
        textos.add(filaRol);

        cabecera.add(cajaAvatar, BorderLayout.WEST);
        cabecera.add(textos, BorderLayout.CENTER);
        cabecera.add(botonBlanco("Editar perfil", PERSONA,
                        e -> formularioEditarPerfil(perfil)),
                BorderLayout.EAST);

        JPanel rejilla = new JPanel(new GridLayout(0, 2, 14, 14));
        rejilla.setOpaque(false);
        rejilla.setAlignmentX(Component.LEFT_ALIGNMENT);

        rejilla.add(dato("Correo electrónico",
                (String) perfil[2], SOBRE));
        rejilla.add(dato("Teléfono",
                (String) perfil[3], TELEFONO));
        rejilla.add(dato("Cédula", (String) perfil[4], PERSONA));
        rejilla.add(dato("Dirección",
                (String) perfil[5], UBICACION));

        tarjeta.add(cabecera);
        tarjeta.add(Box.createVerticalStrut(26));
        tarjeta.add(rejilla);

        columna.add(tarjeta);
        columna.add(Box.createVerticalStrut(18));

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(24, 30, 24, 30));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setAlignmentX(Component.LEFT_ALIGNMENT);
        encabezado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 50));
        encabezado.add(subtitulo("Contactos de emergencia"),
                BorderLayout.WEST);
        encabezado.add(botonBlanco("Agregar", MAS,
                        e -> formularioContacto(null)),
                BorderLayout.EAST);

        caja.add(encabezado);
        caja.add(Box.createVerticalStrut(14));

        try {
            List<Object[]> lista =
                    datos.contactos(usuario.getIdUsuario());

            if (lista.isEmpty()) {
                caja.add(lineaGris(
                        "Todavía no registraste contactos de "
                                + "emergencia."));
            } else {
                for (Object[] c : lista) {
                    caja.add(tarjetaContacto(c));
                    caja.add(Box.createVerticalStrut(8));
                }
            }

        } catch (RuntimeException e) {
            caja.add(lineaGris(e.getMessage()));
        }

        columna.add(caja);

        if (!((boolean) perfil[8])) {
            columna.add(Box.createVerticalStrut(18));

            PanelCaja peligro = new PanelCaja(14, Color.WHITE,
                    new Color(244, 190, 190));
            peligro.setLayout(new BorderLayout(18, 0));
            peligro.setBorder(new EmptyBorder(18, 22, 18, 22));
            peligro.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel aviso = new JLabel(
                    "<html><b>Eliminar mi cuenta</b><br>"
                            + "La cuenta se inactivará conservando tu "
                            + "historial.</html>");
            aviso.setFont(new Font(FUENTE, Font.PLAIN, 12));
            aviso.setForeground(ROJO);

            JButton btnBaja = enlace("Eliminar mi cuenta", ROJO);
            btnBaja.addActionListener(e -> solicitarBajaCuenta());

            peligro.add(aviso, BorderLayout.CENTER);
            peligro.add(btnBaja, BorderLayout.EAST);
            columna.add(peligro);
        }

        cuerpoPerfil.add(desplazable(columna), BorderLayout.CENTER);
        cuerpoPerfil.revalidate();
        cuerpoPerfil.repaint();
    }

    private void formularioEditarPerfil(Object[] perfil) {
        JDialog dialogo = new JDialog(this, "Editar perfil", true);
        dialogo.setSize(560, 610);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 32, 22, 32));

        JLabel titulo = new JLabel("Editar perfil");
        titulo.setFont(new Font(FUENTE, Font.BOLD, 23));
        titulo.setForeground(AZUL_OSCURO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtNombre = campo();
        txtNombre.setText(texto((String) perfil[0], ""));
        JTextField txtTelefono = campo();
        txtTelefono.setText(texto((String) perfil[3], ""));
        JTextField txtDireccion = campo();
        txtDireccion.setText(texto((String) perfil[5], ""));

        String[] rutaFoto = {(String) perfil[6]};
        JPanel vistaFoto = new JPanel(new GridBagLayout());
        vistaFoto.setOpaque(false);
        vistaFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        vistaFoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        Runnable actualizarFoto = () -> {
            vistaFoto.removeAll();
            String nombreCompleto = txtNombre.getText().trim()
                    + " " + texto((String) perfil[1], "");
            if (rutaFoto[0] == null || rutaFoto[0].isBlank()) {
                vistaFoto.add(new Avatar(110,
                        iniciales(nombreCompleto)));
            } else {
                vistaFoto.add(cargarImagenLocal(rutaFoto[0],
                        110, 110));
            }
            vistaFoto.revalidate();
            vistaFoto.repaint();
        };

        JButton btnFoto = botonBlanco("Seleccionar foto", CASA,
                e -> {
                    JFileChooser selector = new JFileChooser();
                    selector.setDialogTitle("Seleccionar foto de perfil");
                    selector.setFileFilter(
                            new javax.swing.filechooser
                                    .FileNameExtensionFilter(
                                    "Imágenes (JPG, PNG)",
                                    "jpg", "jpeg", "png"));
                    if (selector.showOpenDialog(dialogo)
                            == JFileChooser.APPROVE_OPTION) {
                        rutaFoto[0] = selector.getSelectedFile()
                                .getAbsolutePath();
                        actualizarFoto.run();
                    }
                });

        columna.add(titulo);
        columna.add(Box.createVerticalStrut(14));
        columna.add(vistaFoto);
        columna.add(Box.createVerticalStrut(8));
        columna.add(btnFoto);
        columna.add(Box.createVerticalStrut(16));
        columna.add(bloqueCampo("Nombre", txtNombre));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueCampo("Teléfono", txtTelefono));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloqueCampo("Dirección", txtDireccion));

        BotonRedondeado guardar = new BotonRedondeado(
                "Guardar cambios", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        guardar.setPreferredSize(new Dimension(190, 44));

        guardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isBlank()) {
                aviso("El nombre es obligatorio.", false);
                return;
            }

            try {
                datos.actualizarPerfil(usuario.getIdUsuario(), nombre,
                        txtTelefono.getText().trim(),
                        txtDireccion.getText().trim(), rutaFoto[0]);
                dialogo.dispose();
                cargarPerfil();
            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(guardar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(contenedor);
        actualizarFoto.run();
        dialogo.setVisible(true);
    }

    private void solicitarBajaCuenta() {
        String mensaje = "¿Realmente deseas eliminar tu cuenta?\n\n"
                + "No podrás volver a iniciar sesión con esta cuenta.";
        Object[] opciones = {"Cancelar", "Eliminar mi cuenta"};
        int respuesta = JOptionPane.showOptionDialog(this, mensaje,
                "Eliminar mi cuenta", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opciones,
                opciones[0]);

        if (respuesta != 1) {
            return;
        }

        try {
            datos.inactivarCuenta(usuario.getIdUsuario());
            SesionUsuario.cerrarSesion();
            Login login = new Login();
            login.setVisible(true);
            dispose();
        } catch (RuntimeException e) {
            aviso(e.getMessage(), false);
        }
    }

    private JPanel tarjetaContacto(Object[] c) {

        int id = (int) c[0];
        boolean principal = (boolean) c[4];

        PanelCaja caja = new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(12, 0));
        caja.setBorder(new EmptyBorder(12, 14, 12, 14));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lbl = new JLabel((principal ? "★  " : "")
                + c[1] + "  ·  " + c[2]);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
        lbl.setForeground(principal ? AMARILLO_TXT : AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl2 = new JLabel(texto((String) c[3],
                "Sin parentesco")
                + (principal ? "   ·   Contacto principal" : ""));
        lbl2.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lbl2.setForeground(GRIS_TEXTO);
        lbl2.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(lbl);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lbl2);

        JPanel acciones =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        JButton btnEditar = enlace("Editar", AZUL);
        btnEditar.addActionListener(e -> formularioContacto(c));

        JButton btnEliminar = enlace("Eliminar", ROJO);
        btnEliminar.addActionListener(e -> {

            int r = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar este contacto de emergencia?",
                    "Eliminar contacto",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (r != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                datos.eliminarContacto(id, usuario.getIdUsuario());
                cargarPerfil();
            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        acciones.add(btnEditar);
        acciones.add(btnEliminar);

        caja.add(textos, BorderLayout.CENTER);
        caja.add(acciones, BorderLayout.EAST);

        return caja;
    }

    private JButton enlace(String texto, Color color) {

        JButton btn = new JButton(texto);
        btn.setFont(new Font(FUENTE, Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private void formularioContacto(Object[] contacto) {

        boolean edicion = contacto != null;

        JDialog dialogo = new JDialog(this,
                edicion ? "Editar contacto" : "Nuevo contacto",
                true);
        dialogo.setSize(460, 400);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 20, 30));

        JTextField txtNombre = campo();
        JTextField txtTelefono = campo();
        JTextField txtParentesco = campo();

        JCheckBox chkPrincipal =
                new JCheckBox("Marcar como contacto principal");
        chkPrincipal.setFont(new Font(FUENTE, Font.BOLD, 13));
        chkPrincipal.setForeground(AZUL_OSCURO);
        chkPrincipal.setOpaque(false);
        chkPrincipal.setFocusPainted(false);
        chkPrincipal.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (edicion) {
            txtNombre.setText((String) contacto[1]);
            txtTelefono.setText((String) contacto[2]);
            txtParentesco.setText(texto((String) contacto[3], ""));
            chkPrincipal.setSelected((boolean) contacto[4]);
        }

        columna.add(bloqueCampo("Nombre del contacto", txtNombre));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Teléfono", txtTelefono));
        columna.add(Box.createVerticalStrut(14));
        columna.add(bloqueCampo("Parentesco", txtParentesco));
        columna.add(Box.createVerticalStrut(16));
        columna.add(chkPrincipal);

        BotonRedondeado btnGuardar = new BotonRedondeado(
                "Guardar", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnGuardar.setPreferredSize(new Dimension(160, 44));

        btnGuardar.addActionListener(e -> {

            if (txtNombre.getText().isBlank()
                    || txtTelefono.getText().isBlank()) {
                aviso("El nombre y el teléfono son obligatorios.",
                        false);
                return;
            }

            try {
                if (edicion) {
                    datos.actualizarContacto((int) contacto[0],
                            txtNombre.getText().trim(),
                            txtTelefono.getText().trim(),
                            txtParentesco.getText().trim(),
                            chkPrincipal.isSelected(),
                            usuario.getIdUsuario());
                } else {
                    datos.crearContacto(
                            txtNombre.getText().trim(),
                            txtTelefono.getText().trim(),
                            txtParentesco.getText().trim(),
                            chkPrincipal.isSelected(),
                            usuario.getIdUsuario());
                }

                dialogo.dispose();
                cargarPerfil();

            } catch (RuntimeException ex) {
                aviso(ex.getMessage(), true);
            }
        });

        JPanel pie =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE_CAMPO));
        pie.add(btnGuardar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);

        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }





    private JTextField campo() {

        JTextField c = new JTextField();
        c.setFont(new Font(FUENTE, Font.PLAIN, 14));
        c.setForeground(AZUL_OSCURO);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        c.setPreferredSize(new Dimension(200, 42));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO),
                new EmptyBorder(0, 11, 0, 11)));

        return c;
    }

    private JLabel etiqueta(String t) {

        JLabel lbl = new JLabel(t);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        return lbl;
    }

    private JPanel bloqueCampo(String texto, JComponent campo) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(etiqueta(texto));
        panel.add(Box.createVerticalStrut(6));
        panel.add(campo);

        return panel;
    }

    private JPanel dosColumnas(JComponent a, JComponent b) {

        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.add(a);
        panel.add(b);

        return panel;
    }

    private JPanel areaTexto(String etiqueta, JTextArea area,
                             int alto) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        area.setFont(new Font(FUENTE, Font.PLAIN, 14));
        area.setForeground(AZUL_OSCURO);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(9, 10, 9, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, alto));
        scroll.setPreferredSize(new Dimension(400, alto));

        panel.add(etiqueta(etiqueta));
        panel.add(Box.createVerticalStrut(6));
        panel.add(scroll);

        return panel;
    }

    private JPanel dato(String etiqueta, String valor,
                        int tipoIcono) {

        PanelCaja caja = new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(10, 0));
        caja.setBorder(new EmptyBorder(12, 14, 12, 14));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        lblEtiqueta.setForeground(GRIS_PISTA);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(texto(valor, "No registrado"));
        lblValor.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(lblEtiqueta);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lblValor);

        JLabel lblIcono = new JLabel(icono(tipoIcono, AZUL, 20));
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcono.setVerticalAlignment(SwingConstants.CENTER);
        lblIcono.setPreferredSize(new Dimension(26, 26));
        caja.add(lblIcono, BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);

        return caja;
    }

    private JPanel pastilla(String texto, Color fondoPastilla,
                            Color colorTexto) {

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 11));
        lbl.setForeground(colorTexto);

        FontMetrics m = lbl.getFontMetrics(lbl.getFont());

        PanelCaja p = new PanelCaja(22, fondoPastilla, null);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setPreferredSize(
                new Dimension(m.stringWidth(texto) + 24, 22));
        p.add(lbl);

        return p;
    }

    private JPanel pastillaEstado(String estado) {

        return switch (estado) {
            case "FINALIZADA" -> pastilla(estado, VERDE_PILL, VERDE);
            case "CANCELADA", "RECHAZADA" ->
                    pastilla(estado, ROJO_PILL, ROJO);
            case "EN_CURSO" -> pastilla(estado,
                    new Color(238, 232, 251), MORADO);
            case "ACEPTADA" -> pastilla(estado,
                    new Color(226, 240, 252), AZUL);
            default -> pastilla(estado, AMARILLO_PILL, AMARILLO_TXT);
        };
    }

    private JScrollPane construirTabla(String[] columnas,
                                       Object[][] filas,
                                       int columnaEstado) {

        javax.swing.table.DefaultTableModel modelo =
                new javax.swing.table.DefaultTableModel(
                        filas, columnas) {
                    @Override
                    public boolean isCellEditable(int f, int c) {
                        return false;
                    }
                };

        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(36);
        tabla.setFont(new Font(FUENTE, Font.PLAIN, 12));
        tabla.setForeground(AZUL_OSCURO);
        tabla.setGridColor(new Color(237, 243, 249));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(226, 240, 252));
        tabla.setSelectionForeground(AZUL_OSCURO);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(
                new Font(FUENTE, Font.BOLD, 12));
        tabla.getTableHeader().setBackground(FONDO_CAMPO);
        tabla.getTableHeader().setForeground(GRIS_TEXTO);
        tabla.getTableHeader().setPreferredSize(
                new Dimension(0, 36));
        tabla.getTableHeader().setReorderingAllowed(false);

        javax.swing.table.DefaultTableCellRenderer pintor =
                new javax.swing.table.DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable t, Object v, boolean sel,
                            boolean foco, int fila, int col) {

                        Component c = super
                                .getTableCellRendererComponent(
                                        t, v, sel, foco, fila, col);

                        if (!sel) {
                            c.setBackground(fila % 2 == 0
                                    ? Color.WHITE : FONDO_CAMPO);
                        }

                        if (col == columnaEstado) {
                            setFont(new Font(FUENTE, Font.BOLD, 12));
                            c.setForeground(
                                    colorEstadoPago(
                                            String.valueOf(v)));
                        } else {
                            setFont(new Font(FUENTE, Font.PLAIN, 12));
                            if (!sel) {
                                c.setForeground(AZUL_OSCURO);
                            }
                        }

                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                };

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i)
                    .setCellRenderer(pintor);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private Color colorEstadoPago(String estado) {

        return switch (texto(estado, "").toUpperCase()) {
            case "APROBADO" -> VERDE;
            case "RECHAZADO", "ANULADO" -> ROJO;
            case "PENDIENTE" -> AMARILLO_TXT;
            default -> AZUL_OSCURO;
        };
    }

    private JScrollPane desplazable(JPanel columna) {

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(columna, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(envoltorio);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        return scroll;
    }

    private JPanel vacio(String mensaje) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(60, 30, 60, 30));

        JLabel lblIcono = new JLabel(icono(HUELLA, BORDE_CAMPO, 52));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(mensaje);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 15));
        lbl.setForeground(GRIS_PISTA);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        caja.add(lblIcono);
        caja.add(Box.createVerticalStrut(14));
        caja.add(lbl);

        JPanel envoltorio = new JPanel(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(caja, BorderLayout.NORTH);

        return envoltorio;
    }

    private JPanel error(String mensaje) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new GridBagLayout());

        JLabel lbl = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "No se pudieron cargar los datos.<br>"
                        + escapar(texto(mensaje, "Revisa la conexión."))
                        + "</div></html>");
        lbl.setFont(new Font(FUENTE, Font.BOLD, 14));
        lbl.setForeground(ROJO);
        caja.add(lbl);

        return caja;
    }

    private void aviso(String mensaje, boolean esError) {

        JOptionPane.showMessageDialog(this, mensaje,
                esError ? "Error" : "Datos incompletos",
                esError ? JOptionPane.ERROR_MESSAGE
                        : JOptionPane.WARNING_MESSAGE);
    }

    private class Estrellas extends JComponent {

        private final double valor;
        private final int lado;

        Estrellas(double valor, int lado) {
            this.valor = valor;
            this.lado = lado;
            Dimension tamano = new Dimension((lado + 3) * 5 + 2,
                    lado + 6);
            setPreferredSize(tamano);
            setMinimumSize(tamano);
            setMaximumSize(tamano);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < 5; i++) {

                g2.setColor(i < Math.round(valor)
                        ? AMARILLO : new Color(226, 232, 240));

                dibujarEstrella(g2, 1 + i * (lado + 3), 3, lado);
            }

            g2.dispose();
        }
    }

    private class SelectorEstrellas extends JComponent {

        private int valor = 0;
        private int sobre = 0;
        private Runnable escucha;

        private static final int LADO = 46;

        SelectorEstrellas() {

            setPreferredSize(new Dimension(LADO * 5 + 24, LADO + 10));
            setMaximumSize(new Dimension(LADO * 5 + 24, LADO + 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    int nuevo = posicion(e.getX());
                    if (nuevo >= 1 && nuevo <= 5) {
                        valor = nuevo;
                        if (escucha != null) {
                            escucha.run();
                        }
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    sobre = 0;
                    repaint();
                }
            });

            addMouseMotionListener(
                    new java.awt.event.MouseMotionAdapter() {
                        @Override
                        public void mouseMoved(MouseEvent e) {
                            sobre = posicion(e.getX());
                            repaint();
                        }
                    });
        }

        private int posicion(int x) {
            return Math.min(5, x / (LADO + 6) + 1);
        }

        void alCambiar(java.util.function.IntConsumer accion) {
            this.escucha = () -> accion.accept(valor);
        }

        int getValor() {
            return valor;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int marcadas = sobre > 0 ? sobre : valor;

            for (int i = 0; i < 5; i++) {

                boolean activa = i < marcadas;

                g2.setColor(activa
                        ? (marcadas <= 2 ? ROJO : AMARILLO)
                        : new Color(230, 236, 243));

                dibujarEstrella(g2, i * (LADO + 6), 4, LADO);
            }

            g2.dispose();
        }
    }

    private void dibujarEstrella(Graphics2D g2, int x, int y,
                                 int lado) {

        Polygon estrella = new Polygon();
        double centro = lado / 2.0;

        for (int i = 0; i < 10; i++) {

            double radio = (i % 2 == 0)
                    ? centro * 0.95 : centro * 0.40;
            double angulo = Math.toRadians(-90 + i * 36);

            estrella.addPoint(
                    (int) Math.round(x + centro
                            + radio * Math.cos(angulo)),
                    (int) Math.round(y + centro
                            + radio * Math.sin(angulo)));
        }

        g2.fillPolygon(estrella);
    }

    private class SelectorFecha extends JPanel {

        private LocalDate seleccionada;
        private YearMonth mesMostrado = YearMonth.now();
        private Runnable escucha;

        private final JLabel texto = new JLabel("Seleccionar");
        private final JPopupMenu emergente = new JPopupMenu();
        private final JLabel lblMes =
                new JLabel("", SwingConstants.CENTER);
        private final JPanel panelDias =
                new JPanel(new GridLayout(0, 7, 3, 3));

        SelectorFecha() {

            setLayout(new BorderLayout());
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(200, 42));

            PanelCaja caja =
                    new PanelCaja(10, Color.WHITE, BORDE_CAMPO);
            caja.setLayout(new BorderLayout(9, 0));
            caja.setBorder(new EmptyBorder(0, 12, 0, 12));
            caja.setCursor(new Cursor(Cursor.HAND_CURSOR));

            texto.setFont(new Font(FUENTE, Font.PLAIN, 14));
            texto.setForeground(GRIS_PISTA);

            caja.add(new JLabel(icono(RELOJ, GRIS_PISTA, 16)),
                    BorderLayout.WEST);
            caja.add(texto, BorderLayout.CENTER);

            add(caja, BorderLayout.CENTER);

            construir();

            MouseAdapter abrir = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dibujar();
                    emergente.show(SelectorFecha.this, 0,
                            getHeight());
                }
            };

            caja.addMouseListener(abrir);
            texto.addMouseListener(abrir);
        }

        void alCambiar(Runnable accion) {
            this.escucha = accion;
        }

        private void construir() {

            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            panel.setPreferredSize(new Dimension(320, 300));

            JPanel encabezado = new JPanel(new BorderLayout());
            encabezado.setOpaque(false);

            encabezado.add(flecha("‹", -1), BorderLayout.WEST);
            encabezado.add(flecha("›", 1), BorderLayout.EAST);

            lblMes.setFont(new Font(FUENTE, Font.BOLD, 15));
            lblMes.setForeground(AZUL_OSCURO);
            encabezado.add(lblMes, BorderLayout.CENTER);

            panelDias.setOpaque(false);

            panel.add(encabezado, BorderLayout.NORTH);
            panel.add(panelDias, BorderLayout.CENTER);

            emergente.setBorder(
                    BorderFactory.createLineBorder(BORDE_CAMPO));
            emergente.add(panel);
        }

        private JButton flecha(String texto, int meses) {

            JButton btn = new JButton(texto);
            btn.setFont(new Font(FUENTE, Font.BOLD, 16));
            btn.setForeground(AZUL);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setBorder(null);
            btn.setPreferredSize(new Dimension(30, 28));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                mesMostrado = mesMostrado.plusMonths(meses);
                dibujar();
            });

            return btn;
        }

        private void dibujar() {

            panelDias.removeAll();

            lblMes.setText(MESES[mesMostrado.getMonthValue() - 1]
                    + "  " + mesMostrado.getYear());

            for (String n : new String[]{"L", "M", "M", "J",
                    "V", "S", "D"}) {

                JLabel lbl = new JLabel(n, SwingConstants.CENTER);
                lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
                lbl.setForeground(GRIS_TEXTO);
                panelDias.add(lbl);
            }

            int desplazamiento = mesMostrado.atDay(1)
                    .getDayOfWeek().getValue() - 1;

            for (int i = 0; i < desplazamiento; i++) {
                panelDias.add(new JLabel(""));
            }

            for (int d = 1; d <= mesMostrado.lengthOfMonth(); d++) {

                LocalDate f = mesMostrado.atDay(d);

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font(FUENTE, Font.PLAIN, 13));
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setOpaque(true);
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setBorder(null);
                btn.setPreferredSize(new Dimension(36, 30));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                boolean pasado = f.isBefore(LocalDate.now());

                if (f.equals(seleccionada)) {
                    btn.setBackground(AZUL);
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(pasado
                            ? new Color(205, 214, 222) : AZUL_OSCURO);
                }

                btn.addActionListener(e -> {
                    seleccionada = f;
                    texto.setText(f.format(DIA));
                    texto.setForeground(AZUL_OSCURO);
                    emergente.setVisible(false);
                    if (escucha != null) {
                        escucha.run();
                    }
                });

                panelDias.add(btn);
            }

            panelDias.revalidate();
            panelDias.repaint();
        }

        LocalDate getFecha() {
            return seleccionada;
        }

        void setFecha(LocalDate fecha) {
            seleccionada = fecha;
            if (fecha == null) {
                texto.setText("Seleccionar");
                texto.setForeground(GRIS_PISTA);
                return;
            }
            mesMostrado = YearMonth.from(fecha);
            texto.setText(fecha.format(DIA));
            texto.setForeground(AZUL_OSCURO);
        }
    }





    private static final class Datos {

        boolean esCuidadorVerificado(int idUsuario) {

            String sql = """
                    SELECT COUNT(*) FROM cuidador
                    WHERE id_usuario = ?
                      AND estado_verificacion = 'VERIFICADO'
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        Object[] perfilUsuario(int idUsuario) {
            String sql = """
                    SELECT u.nombre, u.apellido, u.email, u.telefono,
                           u.cedula, u.direccion_domicilio,
                           u.foto_perfil, u.rol_sistema,
                           EXISTS (
                               SELECT 1
                               FROM cuidador c
                               WHERE c.id_usuario = u.id_usuario
                                 AND c.estado_verificacion = 'VERIFICADO'
                           )
                    FROM usuario u
                    WHERE u.id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6),
                                rs.getString(7), rs.getString(8),
                                rs.getBoolean(9)
                        };
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return null;
        }

        void actualizarPerfil(int idUsuario, String nombre,
                              String telefono, String direccion,
                              String fotoPerfil) {
            String sql = """
                    UPDATE usuario
                    SET nombre = ?,
                        telefono = ?,
                        direccion_domicilio = ?,
                        foto_perfil = ?
                    WHERE id_usuario = ?
                      AND activo
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, vacio(telefono));
                ps.setString(3, vacio(direccion));
                ps.setString(4, vacio(fotoPerfil));
                ps.setInt(5, idUsuario);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "No se pudo actualizar el perfil.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void inactivarCuenta(int idUsuario) {
            String sqlEstado = """
                    SELECT EXISTS (
                               SELECT 1 FROM cuidador c
                               WHERE c.id_usuario = u.id_usuario
                                 AND c.estado_verificacion = 'VERIFICADO'
                           ),
                           EXISTS (
                               SELECT 1
                               FROM reserva r
                               JOIN mascota m
                                    ON m.id_mascota = r.id_mascota
                               WHERE m.id_usuario = u.id_usuario
                                 AND r.estado_reserva IN (
                                     'SOLICITADA','ACEPTADA','EN_CURSO')
                           ),
                           EXISTS (
                               SELECT 1
                               FROM pago p
                               JOIN reserva r
                                    ON r.id_reserva = p.id_reserva
                               JOIN mascota m
                                    ON m.id_mascota = r.id_mascota
                               WHERE m.id_usuario = u.id_usuario
                                 AND p.estado_pago = 'PENDIENTE'
                           )
                    FROM usuario u
                    WHERE u.id_usuario = ?
                      AND u.activo
                    FOR UPDATE OF u
                    """;

            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    boolean cuidador;
                    boolean reservas;
                    boolean pagos;

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlEstado)) {
                        ps.setInt(1, idUsuario);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new RuntimeException(
                                        "La cuenta ya no está activa.");
                            }
                            cuidador = rs.getBoolean(1);
                            reservas = rs.getBoolean(2);
                            pagos = rs.getBoolean(3);
                        }
                    }

                    if (cuidador) {
                        throw new RuntimeException(
                                "Una cuenta con perfil de cuidador "
                                        + "verificado debe inactivar su "
                                        + "actividad desde el panel del "
                                        + "cuidador.");
                    }

                    if (reservas || pagos) {
                        throw new RuntimeException(
                                "No puedes eliminar tu cuenta mientras "
                                        + "tengas reservas activas o pagos "
                                        + "pendientes.");
                    }

                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE usuario SET activo = FALSE "
                                    + "WHERE id_usuario = ? AND activo")) {
                        ps.setInt(1, idUsuario);
                        if (ps.executeUpdate() == 0) {
                            throw new RuntimeException(
                                    "No se pudo inactivar la cuenta.");
                        }
                    }

                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw e;
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<String> ciudades() {

            List<String> lista = new ArrayList<>();

            String sql = """
                    SELECT DISTINCT ciudad FROM alojamiento
                    WHERE activo ORDER BY ciudad
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(rs.getString(1));
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> alojamientos(String ciudad, int idUsuario) {

            String sql = """
                    SELECT a.id_alojamiento, a.nombre, a.ciudad,
                           a.tipo_alojamiento, a.descripcion,
                           a.precio_noche, a.capacidad_total,
                           u.nombre || ' ' || u.apellido,
                           COALESCE((SELECT AVG(re.calificacion)
                                       FROM resena re
                                      WHERE re.id_usuario_evaluado
                                            = u.id_usuario), 0),
                           (SELECT COUNT(*) FROM resena re
                             WHERE re.id_usuario_evaluado
                                   = u.id_usuario)
                    FROM alojamiento a
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    JOIN usuario u ON u.id_usuario = c.id_usuario
                    WHERE a.activo
                      AND c.estado_verificacion = 'VERIFICADO'
                      AND c.id_usuario <> ?
                      AND (? IS NULL OR a.ciudad = ?)
                    ORDER BY 9 DESC, a.precio_noche
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);
                ps.setString(2, ciudad);
                ps.setString(3, ciudad);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getDouble(6),
                                rs.getInt(7), rs.getString(8),
                                rs.getDouble(9), rs.getInt(10)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        Object[] alojamientoDetalle(int idAlojamiento) {

            String sql = """
                    SELECT a.nombre, a.tipo_alojamiento,
                           a.descripcion, a.provincia, a.ciudad,
                           a.direccion, a.referencia,
                           a.capacidad_total, a.precio_noche,
                           a.tiene_patio, a.tiene_cerramiento,
                           a.convive_con_mascotas,
                           a.reglas_generales, a.ofrece_transporte,
                           u.nombre || ' ' || u.apellido,
                           COALESCE((SELECT AVG(re.calificacion)
                                       FROM resena re
                                      WHERE re.id_usuario_evaluado
                                            = u.id_usuario), 0),
                           (SELECT COUNT(*) FROM resena re
                             WHERE re.id_usuario_evaluado
                                   = u.id_usuario),
                           a.fecha_publicacion
                    FROM alojamiento a
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    JOIN usuario u ON u.id_usuario = c.id_usuario
                    WHERE a.id_alojamiento = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6),
                                rs.getString(7), rs.getInt(8),
                                rs.getDouble(9), rs.getBoolean(10),
                                rs.getBoolean(11), rs.getBoolean(12),
                                rs.getString(13), rs.getBoolean(14),
                                rs.getString(15), rs.getDouble(16),
                                rs.getInt(17), rs.getTimestamp(18)
                        };
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        String fotoPrincipal(int idAlojamiento) {

            String sql = """
                    SELECT url_foto
                    FROM foto_alojamiento
                    WHERE id_alojamiento = ?
                    ORDER BY es_foto_principal DESC,
                             orden_visualizacion NULLS LAST,
                             id_foto
                    LIMIT 1
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(1);
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        List<String> fotos(int idAlojamiento) {

            String sql = """
                    SELECT url_foto
                    FROM foto_alojamiento
                    WHERE id_alojamiento = ?
                    ORDER BY es_foto_principal DESC,
                             orden_visualizacion NULLS LAST,
                             id_foto
                    """;

            List<String> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(rs.getString(1));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> categoriasAceptadas(int idAlojamiento) {

            String sql = """
                    SELECT e.nombre_especie, ct.nombre_categoria,
                           aca.cantidad_maxima, aca.observaciones
                    FROM alojamiento_categoria_aceptada aca
                    JOIN categoria_tamano ct
                         ON ct.id_categoria = aca.id_categoria
                    JOIN especie e ON e.id_especie = ct.id_especie
                    WHERE aca.id_alojamiento = ?
                      AND aca.estado
                    ORDER BY e.nombre_especie, ct.peso_min_kg
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Integer maximo = rs.getObject(3) == null
                                ? null : rs.getInt(3);
                        lista.add(new Object[]{
                                rs.getString(1), rs.getString(2),
                                maximo, rs.getString(4)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> reglasAlojamiento(int idAlojamiento) {

            String sql = """
                    SELECT ca.nombre_caracteristica, r.acepta,
                           r.condiciones
                    FROM regla_alojamiento_caracteristica r
                    JOIN caracteristica ca
                         ON ca.id_caracteristica = r.id_caracteristica
                    WHERE r.id_alojamiento = ?
                      AND r.estado
                    ORDER BY r.acepta DESC, ca.nombre_caracteristica
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getString(1), rs.getBoolean(2),
                                rs.getString(3)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<String> incompatibilidades(int idAlojamiento,
                                        int idMascota,
                                        String nombreMascota) {

            List<String> problemas = new ArrayList<>();

            String sqlCategoria = """
                    SELECT ct.nombre_categoria,
                           EXISTS (
                             SELECT 1
                             FROM alojamiento_categoria_aceptada aca
                             WHERE aca.id_alojamiento = ?
                               AND aca.id_categoria = ct.id_categoria
                               AND aca.estado)
                    FROM mascota m
                    LEFT JOIN categoria_tamano ct
                      ON ct.id_categoria =
                         fn_categoria_mascota(m.id_mascota)
                    WHERE m.id_mascota = ?::integer
                    """;

            String sqlCaracteristicas = """
                    SELECT ca.nombre_caracteristica
                    FROM mascota_caracteristica mc
                    JOIN caracteristica ca
                         ON ca.id_caracteristica = mc.id_caracteristica
                    JOIN regla_alojamiento_caracteristica r
                         ON r.id_caracteristica = mc.id_caracteristica
                    WHERE mc.id_mascota = ?
                      AND mc.estado
                      AND r.id_alojamiento = ?
                      AND r.estado
                      AND r.acepta = FALSE
                    """;

            try (Connection c = ConexionBD.conectar()) {

                try (PreparedStatement ps =
                             c.prepareStatement(sqlCategoria)) {

                    ps.setInt(1, idAlojamiento);
                    ps.setInt(2, idMascota);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {

                            boolean aceptada = rs.getBoolean(2);

                            if (!aceptada) {
                                problemas.add(
                                        "Este alojamiento no acepta la "
                                                + "categoría de "
                                                + nombreMascota + ".");
                            }
                        } else {
                            problemas.add(
                                    "No se pudo verificar la categoría de "
                                            + nombreMascota + ".");
                        }
                    }
                }

                try (PreparedStatement ps =
                             c.prepareStatement(sqlCaracteristicas)) {

                    ps.setInt(1, idMascota);
                    ps.setInt(2, idAlojamiento);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            problemas.add(
                                    "Este alojamiento no acepta una "
                                            + "característica de tu "
                                            + "mascota: "
                                            + rs.getString(1) + ".");
                        }
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return problemas;
        }

        List<Object[]> especies() {

            List<Object[]> lista = new ArrayList<>();

            String sql = "SELECT id_especie, nombre_especie "
                    + "FROM especie ORDER BY nombre_especie";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getString(2)});
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> mascotas(int idUsuario) {

            String sql = """
                    SELECT m.id_mascota, m.nombre, e.nombre_especie,
                           m.raza, m.peso_kg, m.edad, m.sexo,
                           (SELECT ct.nombre_categoria
                              FROM categoria_tamano ct
                             WHERE ct.id_categoria
                                   = fn_categoria_mascota(m.id_mascota))
                    FROM mascota m
                    JOIN especie e ON e.id_especie = m.id_especie
                    WHERE m.id_usuario = ?
                    ORDER BY m.nombre
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Integer edad = rs.getObject(6) == null
                                ? null : rs.getInt(6);
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getDouble(5), edad,
                                rs.getString(7), rs.getString(8)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        Object[] mascotaFicha(int idMascota, int idUsuario) {

            String sql = """
                    SELECT m.nombre, e.nombre_especie, m.raza,
                           m.edad, m.sexo, m.peso_kg,
                           (SELECT ct.nombre_categoria
                              FROM categoria_tamano ct
                             WHERE ct.id_categoria
                                   = fn_categoria_mascota(m.id_mascota)),
                           m.color, m.esterilizado, m.microchip,
                           m.alergias, m.enfermedades,
                           m.comportamiento_general,
                           m.convive_con_otros,
                           m.nombre_veterinario_contacto,
                           m.telefono_veterinario_contacto,
                           m.observaciones
                    FROM mascota m
                    JOIN especie e ON e.id_especie = m.id_especie
                    WHERE m.id_mascota = ?
                      AND m.id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idMascota);
                ps.setInt(2, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                        Integer edad = rs.getObject(4) == null
                                ? null : rs.getInt(4);
                        Boolean esterilizado =
                                rs.getObject(9) == null
                                        ? null : rs.getBoolean(9);
                        Boolean convive = rs.getObject(14) == null
                                ? null : rs.getBoolean(14);

                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), edad,
                                rs.getString(5), rs.getDouble(6),
                                rs.getString(7), rs.getString(8),
                                esterilizado, rs.getString(10),
                                rs.getString(11), rs.getString(12),
                                rs.getString(13), convive,
                                rs.getString(15), rs.getString(16),
                                rs.getString(17)
                        };
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        List<Object[]> catalogoCaracteristicas() {

            String sql = """
                    SELECT id_caracteristica, nombre_caracteristica,
                           categoria
                    FROM caracteristica
                    WHERE estado
                    ORDER BY categoria, nombre_caracteristica
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getString(2),
                            rs.getString(3)});
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> caracteristicasMascota(int idMascota) {

            String sql = """
                    SELECT mc.id_mascota_caracteristica,
                           ca.nombre_caracteristica,
                           mc.nivel, mc.descripcion
                    FROM mascota_caracteristica mc
                    JOIN caracteristica ca
                         ON ca.id_caracteristica = mc.id_caracteristica
                    WHERE mc.id_mascota = ?
                      AND mc.estado
                    ORDER BY ca.nombre_caracteristica
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idMascota);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getString(4)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        void registrarCaracteristicaMascota(String nivel,
                                            String descripcion,
                                            int idMascota,
                                            int idCaracteristica,
                                            int idUsuario) {

            if (!esMascotaDelUsuario(idMascota, idUsuario)) {
                throw new RuntimeException(
                        "Esta mascota no te pertenece.");
            }

            String sql = "SELECT fn_registrar_mascota_caracteristica("
                    + "?::varchar,?::text,?::boolean,?::integer,"
                    + "?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nivel);
                ps.setString(2, vacio(descripcion));
                ps.setBoolean(3, true);
                ps.setInt(4, idMascota);
                ps.setInt(5, idCaracteristica);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void desactivarCaracteristica(int idRelacion,
                                      int idUsuario) {

            String sql = """
                    UPDATE mascota_caracteristica mc
                    SET estado = FALSE
                    FROM mascota m
                    WHERE mc.id_mascota_caracteristica = ?
                      AND m.id_mascota = mc.id_mascota
                      AND m.id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idRelacion);
                ps.setInt(2, idUsuario);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "No se pudo desactivar la "
                                    + "característica.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> catalogoVacunas() {

            String sql = "SELECT id_vacuna, nombre_vacuna "
                    + "FROM vacuna ORDER BY nombre_vacuna";

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getString(2)});
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        List<Object[]> vacunasMascota(int idMascota) {

            String sql = """
                    SELECT v.nombre_vacuna, mv.fecha_vacunacion,
                           mv.fecha_proxima_dosis,
                           mv.veterinaria_aplicacion,
                           mv.observaciones
                    FROM mascota_vacuna mv
                    JOIN vacuna v ON v.id_vacuna = mv.id_vacuna
                    WHERE mv.id_mascota = ?
                    ORDER BY mv.fecha_vacunacion DESC
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idMascota);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getString(1), rs.getDate(2),
                                rs.getDate(3), rs.getString(4),
                                rs.getString(5)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        void registrarVacunaMascota(LocalDate aplicacion,
                                    LocalDate proxima,
                                    String veterinaria,
                                    String observaciones,
                                    int idMascota, int idVacuna,
                                    int idUsuario) {

            if (!esMascotaDelUsuario(idMascota, idUsuario)) {
                throw new RuntimeException(
                        "Esta mascota no te pertenece.");
            }

            String sql = "SELECT fn_registrar_mascota_vacuna("
                    + "?::date,?::date,?::varchar,?::text,"
                    + "?::integer,?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(aplicacion));

                if (proxima == null) {
                    ps.setNull(2, Types.DATE);
                } else {
                    ps.setDate(2, Date.valueOf(proxima));
                }

                ps.setString(3, vacio(veterinaria));
                ps.setString(4, vacio(observaciones));
                ps.setInt(5, idMascota);
                ps.setInt(6, idVacuna);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private boolean esMascotaDelUsuario(int idMascota,
                                            int idUsuario) {

            String sql = "SELECT COUNT(*) FROM mascota "
                    + "WHERE id_mascota = ? AND id_usuario = ?";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idMascota);
                ps.setInt(2, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private boolean esReservaDelUsuario(int idReserva,
                                            int idUsuario) {

            String sql = """
                    SELECT COUNT(*)
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    WHERE r.id_reserva = ?
                      AND m.id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);
                ps.setInt(2, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        int registrarMascota(String nombre, String sexo,
                             double peso, String color,
                             boolean esterilizado, String microchip,
                             String alergias, String comportamiento,
                             boolean convive, String veterinario,
                             String telefonoVet, String raza,
                             int idUsuario, int idEspecie,
                             int edad) {

            String sql = "SELECT fn_registrar_mascota("
                    + "?::varchar,"
                    + "?::char,"
                    + "?::numeric,"
                    + "?::varchar,"
                    + "?::boolean,"
                    + "?::varchar,"
                    + "?::text,"
                    + "?::text,"
                    + "?::text,"
                    + "?::boolean,"
                    + "?::varchar,"
                    + "?::varchar,"
                    + "?::varchar,"
                    + "?::text,"
                    + "?::integer,"
                    + "?::integer,"
                    + "?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setString(2, sexo);
                ps.setBigDecimal(3, BigDecimal.valueOf(peso));
                ps.setString(4, vacio(color));
                ps.setBoolean(5, esterilizado);
                ps.setString(6, vacio(microchip));
                ps.setString(7, vacio(alergias));
                ps.setString(8, null);
                ps.setString(9, vacio(comportamiento));
                ps.setBoolean(10, convive);
                ps.setString(11, vacio(veterinario));
                ps.setString(12, vacio(telefonoVet));
                ps.setString(13, vacio(raza));
                ps.setString(14, null);
                ps.setInt(15, idUsuario);
                ps.setInt(16, idEspecie);
                ps.setInt(17, edad);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        boolean tieneReservaSuperpuesta(int idMascota,
                                        LocalDate ingreso,
                                        LocalDate salida) {

            String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM reserva r
                        WHERE r.id_mascota = ?
                          AND r.estado_reserva IN (
                              'SOLICITADA', 'ACEPTADA', 'EN_CURSO')
                          AND r.fecha_ingreso < ?
                          AND r.fecha_salida > ?
                    )
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idMascota);
                ps.setDate(2, Date.valueOf(salida));
                ps.setDate(3, Date.valueOf(ingreso));

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        boolean hayDisponibilidad(int idAlojamiento, LocalDate a,
                                  LocalDate b) {

            String sql = "SELECT fn_alojamiento_disponible("
                    + "?::integer, ?::date, ?::date, NULL::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idAlojamiento);
                ps.setDate(2, Date.valueOf(a));
                ps.setDate(3, Date.valueOf(b));

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        int registrarReserva(LocalDate ingreso, LocalDate salida,
                             String mensaje, String instrucciones,
                             int idMascota, int idAlojamiento) {

            String sql = "SELECT fn_registrar_reserva("
                    + "?::date,?::date,?::text,?::text,?::text,"
                    + "?::numeric,?::boolean,?::integer,?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setDate(1, Date.valueOf(ingreso));
                ps.setDate(2, Date.valueOf(salida));
                ps.setString(3, vacio(mensaje));
                ps.setString(4, vacio(instrucciones));
                ps.setString(5, null);
                ps.setBigDecimal(6, BigDecimal.ZERO);
                ps.setBoolean(7, true);
                ps.setInt(8, idMascota);
                ps.setInt(9, idAlojamiento);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> reservas(int idUsuario) {

            String sql = """
                    SELECT r.id_reserva, m.nombre, a.nombre,
                           a.ciudad, r.fecha_ingreso,
                           r.fecha_salida, r.estado_reserva,
                           COALESCE(r.total_acordado, 0),
                           COALESCE(r.precio_noche_acordado, 0),
                           COALESCE((SELECT SUM(p.monto) FROM pago p
                                      WHERE p.id_reserva = r.id_reserva
                                        AND p.estado_pago
                                            = 'APROBADO'), 0),
                           u.id_usuario,
                           u.nombre || ' ' || u.apellido,
                           (SELECT COUNT(*) FROM resena re
                             WHERE re.id_reserva = r.id_reserva
                               AND re.id_usuario_autor = ?),
                           a.ofrece_transporte,
                           (SELECT t.estado_transporte
                              FROM transporte t
                             WHERE t.id_reserva = r.id_reserva),
                           COALESCE((SELECT t.precio_acordado
                              FROM transporte t
                             WHERE t.id_reserva = r.id_reserva), 0),
                            (SELECT COUNT(*) FROM pago p
                              WHERE p.id_reserva = r.id_reserva
                                AND p.estado_pago = 'PENDIENTE'),
                            (SELECT COUNT(*) FROM pago p
                              WHERE p.id_reserva = r.id_reserva
                                AND p.estado_pago = 'APROBADO')
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    JOIN usuario u ON u.id_usuario = c.id_usuario
                    WHERE m.id_usuario = ?
                    ORDER BY r.id_reserva DESC
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);
                ps.setInt(2, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getDate(5), rs.getDate(6),
                                rs.getString(7), rs.getDouble(8),
                                rs.getDouble(9), rs.getDouble(10),
                                rs.getInt(11), rs.getString(12),
                                rs.getInt(13), rs.getBoolean(14),
                                rs.getString(15), rs.getDouble(16),
                                rs.getInt(17), rs.getInt(18)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        Object[] estadoCancelacion(int idReserva, int idUsuario) {

            String sql = """
                    SELECT r.estado_reserva,
                           EXISTS (
                               SELECT 1
                               FROM pago p
                               WHERE p.id_reserva = r.id_reserva
                                 AND p.estado_pago = 'APROBADO'
                           ),
                           EXISTS (
                               SELECT 1
                               FROM pago p
                               WHERE p.id_reserva = r.id_reserva
                                 AND p.estado_pago = 'PENDIENTE'
                           )
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    WHERE r.id_reserva = ?::integer
                      AND m.id_usuario = ?::integer
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);
                ps.setInt(2, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new RuntimeException(
                                "La reserva no existe o no te pertenece.");
                    }
                    return new Object[]{
                            rs.getString(1), rs.getBoolean(2),
                            rs.getBoolean(3)
                    };
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void cancelarReserva(int idReserva, int idUsuario) {

            Connection c = null;
            int aislamientoAnterior = Connection.TRANSACTION_READ_COMMITTED;

            try {
                c = ConexionBD.conectar();
                aislamientoAnterior = c.getTransactionIsolation();
                c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                c.setAutoCommit(false);

                String estadoActual;
                String verificarReserva = """
                        SELECT r.estado_reserva
                        FROM reserva r
                        JOIN mascota m ON m.id_mascota = r.id_mascota
                        WHERE r.id_reserva = ?::integer
                          AND m.id_usuario = ?::integer
                        FOR UPDATE OF r
                        """;

                try (PreparedStatement ps =
                             c.prepareStatement(verificarReserva)) {
                    ps.setInt(1, idReserva);
                    ps.setInt(2, idUsuario);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new RuntimeException(
                                    "La reserva no existe o no te pertenece.");
                        }
                        estadoActual = rs.getString(1);
                    }
                }

                if (!"SOLICITADA".equals(estadoActual)
                        && !"ACEPTADA".equals(estadoActual)) {
                    throw new RuntimeException(
                            "Esta reserva ya no puede cancelarse en su estado actual.");
                }

                String bloquearPagos = """
                        SELECT p.id_pago
                        FROM pago p
                        WHERE p.id_reserva = ?::integer
                        FOR UPDATE
                        """;

                try (PreparedStatement ps = c.prepareStatement(bloquearPagos)) {
                    ps.setInt(1, idReserva);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                        }
                    }
                }

                boolean pagoAprobado;
                String verificarAprobado = """
                        SELECT EXISTS (
                            SELECT 1
                            FROM pago
                            WHERE id_reserva = ?::integer
                              AND estado_pago = 'APROBADO'
                        )
                        """;

                try (PreparedStatement ps =
                             c.prepareStatement(verificarAprobado)) {
                    ps.setInt(1, idReserva);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        pagoAprobado = rs.getBoolean(1);
                    }
                }

                if (pagoAprobado) {
                    throw new RuntimeException(
                            "No puedes cancelar esta reserva porque ya existe un pago aprobado.");
                }

                try (CallableStatement cs = c.prepareCall(
                        "CALL sp_cancelar_reserva(?::integer, ?::integer)")) {
                    cs.setInt(1, idReserva);
                    cs.setInt(2, idUsuario);
                    cs.execute();
                }

                String anularPendientes = """
                        UPDATE pago
                        SET estado_pago = 'ANULADO'
                        WHERE id_reserva = ?::integer
                          AND estado_pago = 'PENDIENTE'
                        """;

                try (PreparedStatement ps =
                             c.prepareStatement(anularPendientes)) {
                    ps.setInt(1, idReserva);
                    ps.executeUpdate();
                }

                c.commit();

            } catch (SQLException e) {
                if (c != null) {
                    try {
                        c.rollback();
                    } catch (SQLException ignored) {
                    }
                }
                throw new RuntimeException(mensaje(e), e);
            } catch (RuntimeException e) {
                if (c != null) {
                    try {
                        c.rollback();
                    } catch (SQLException ignored) {
                    }
                }
                throw e;
            } finally {
                if (c != null) {
                    try {
                        c.setAutoCommit(true);
                        c.setTransactionIsolation(aislamientoAnterior);
                    } catch (SQLException ignored) {
                    }
                    try {
                        c.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        }



        Object[] transporte(int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = """
                    SELECT t.modalidad, t.estado_transporte,
                           t.precio_acordado, t.direccion_recogida,
                           t.direccion_entrega,
                           t.fecha_hora_recogida,
                           t.fecha_hora_entrega, t.observaciones,
                           COALESCE((SELECT SUM(p.monto) FROM pago p
                                      WHERE p.id_reserva = t.id_reserva
                                        AND p.estado_pago
                                            = 'APROBADO'), 0)
                    FROM transporte t
                    WHERE t.id_reserva = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getDouble(3), rs.getString(4),
                                rs.getString(5), rs.getTimestamp(6),
                                rs.getTimestamp(7), rs.getString(8),
                                rs.getDouble(9)
                        };
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        void solicitarTransporte(String modalidad,
                                 String direccionRecogida,
                                 String direccionEntrega,
                                 LocalDateTime fechaRecogida,
                                 LocalDateTime fechaEntrega,
                                 String observaciones,
                                 int idReserva, int idUsuario) {

            String sqlReserva = """
                    SELECT r.estado_reserva
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    WHERE r.id_reserva = ?
                      AND m.id_usuario = ?
                    FOR UPDATE OF r
                    """;

            String sqlPagos = """
                    SELECT COUNT(*)
                    FROM pago
                    WHERE id_reserva = ?
                      AND estado_pago IN ('PENDIENTE', 'APROBADO')
                    """;

            String sqlEstado = """
                    SELECT estado_transporte
                    FROM transporte
                    WHERE id_reserva = ?
                    FOR UPDATE
                    """;

            String sqlActualizar = """
                    UPDATE transporte
                    SET modalidad = ?,
                        precio_acordado = 0.00,
                        direccion_recogida = ?,
                        direccion_entrega = ?,
                        fecha_hora_recogida = ?,
                        fecha_hora_entrega = ?,
                        observaciones = ?,
                        estado_transporte = 'PENDIENTE'
                    WHERE id_reserva = ?
                      AND estado_transporte = 'CANCELADO'
                    """;

            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);

                try {
                    String estadoReserva;

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlReserva)) {
                        ps.setInt(1, idReserva);
                        ps.setInt(2, idUsuario);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new RuntimeException(
                                        "Esta reserva no te pertenece.");
                            }
                            estadoReserva = rs.getString(1);
                        }
                    }

                    if (!("ACEPTADA".equals(estadoReserva)
                            || "EN_CURSO".equals(estadoReserva))) {
                        throw new RuntimeException(
                                "La reserva no está habilitada para "
                                        + "solicitar transporte.");
                    }

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlPagos)) {
                        ps.setInt(1, idReserva);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getInt(1) > 0) {
                                throw new RuntimeException(
                                        "Ya existe un pago para esta "
                                                + "reserva, no puedes "
                                                + "solicitar transporte "
                                                + "ahora.");
                            }
                        }
                    }

                    String estadoActual = null;
                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlEstado)) {
                        ps.setInt(1, idReserva);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                estadoActual = rs.getString(1);
                            }
                        }
                    }

                    if (estadoActual == null) {
                        insertarTransporte(c, modalidad,
                                direccionRecogida, direccionEntrega,
                                fechaRecogida, fechaEntrega,
                                observaciones, idReserva);
                    } else {
                        if (!"CANCELADO".equals(estadoActual)) {
                            throw new RuntimeException(
                                    "Esta reserva ya tiene un transporte "
                                            + "activo.");
                        }

                        try (PreparedStatement ps =
                                     c.prepareStatement(sqlActualizar)) {
                            ps.setString(1, modalidad);
                            ps.setString(2, vacio(direccionRecogida));
                            ps.setString(3, vacio(direccionEntrega));
                            fijarTimestamp(ps, 4, fechaRecogida);
                            fijarTimestamp(ps, 5, fechaEntrega);
                            ps.setString(6, vacio(observaciones));
                            ps.setInt(7, idReserva);

                            if (ps.executeUpdate() == 0) {
                                throw new RuntimeException(
                                        "No se pudo registrar el "
                                                + "transporte.");
                            }
                        }
                    }

                    c.commit();

                } catch (SQLException e) {
                    rollback(c);
                    throw e;
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private void insertarTransporte(Connection c, String modalidad,
                                        String direccionRecogida,
                                        String direccionEntrega,
                                        LocalDateTime fechaRecogida,
                                        LocalDateTime fechaEntrega,
                                        String observaciones,
                                        int idReserva) {

            String sql = "SELECT fn_registrar_transporte("
                    + "?::varchar,?::numeric,?::varchar,?::varchar,"
                    + "?::timestamp,?::timestamp,?::text,?::integer)";

            try (PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, modalidad);
                ps.setBigDecimal(2, BigDecimal.ZERO
                        .setScale(2, RoundingMode.HALF_UP));
                ps.setString(3, vacio(direccionRecogida));
                ps.setString(4, vacio(direccionEntrega));
                fijarTimestamp(ps, 5, fechaRecogida);
                fijarTimestamp(ps, 6, fechaEntrega);
                ps.setString(7, vacio(observaciones));
                ps.setInt(8, idReserva);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private void fijarTimestamp(PreparedStatement ps, int indice,
                                    LocalDateTime valor)
                throws SQLException {

            if (valor == null) {
                ps.setNull(indice, Types.TIMESTAMP);
            } else {
                ps.setTimestamp(indice, Timestamp.valueOf(valor));
            }
        }

        void cancelarTransporte(int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = """
                    UPDATE transporte
                    SET estado_transporte = 'CANCELADO',
                        observaciones = CASE
                            WHEN COALESCE(observaciones,'') LIKE ?
                                THEN observaciones
                            ELSE TRIM(? || E'\\n'
                                 || COALESCE(observaciones,''))
                        END
                    WHERE id_reserva = ?
                      AND estado_transporte = 'PENDIENTE'
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, "%" + MARCA_CANCELADO_USUARIO + "%");
                ps.setString(2, MARCA_CANCELADO_USUARIO);
                ps.setInt(3, idReserva);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "Este transporte ya no puede "
                                    + "cancelarse.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }



        Object[] detalleCuidado(int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = """
                    SELECT tipo_alimento, cantidad_alimento,
                           horario_alimentacion, horario_paseo,
                           cuidados_especiales,
                           comportamiento_importante, observaciones
                    FROM detalle_cuidado
                    WHERE id_reserva = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6),
                                rs.getString(7)
                        };
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        void guardarDetalleCuidado(String tipoAlimento,
                                   String cantidad,
                                   String horarioComida,
                                   String horarioPaseo,
                                   String cuidados,
                                   String comportamiento,
                                   String observaciones,
                                   int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            boolean existe = detalleCuidado(idReserva, idUsuario)
                    != null;

            if (existe) {

                String sql = """
                        UPDATE detalle_cuidado
                        SET tipo_alimento = ?,
                            cantidad_alimento = ?,
                            horario_alimentacion = ?,
                            horario_paseo = ?,
                            cuidados_especiales = ?,
                            comportamiento_importante = ?,
                            observaciones = ?
                        WHERE id_reserva = ?
                        """;

                try (Connection c = ConexionBD.conectar();
                     PreparedStatement ps = c.prepareStatement(sql)) {

                    ps.setString(1, vacio(tipoAlimento));
                    ps.setString(2, vacio(cantidad));
                    ps.setString(3, vacio(horarioComida));
                    ps.setString(4, vacio(horarioPaseo));
                    ps.setString(5, vacio(cuidados));
                    ps.setString(6, vacio(comportamiento));
                    ps.setString(7, vacio(observaciones));
                    ps.setInt(8, idReserva);

                    ps.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException(mensaje(e), e);
                }

                return;
            }

            String sql = "SELECT fn_registrar_detalle_cuidado("
                    + "?::varchar,?::varchar,?::varchar,?::varchar,"
                    + "?::text,?::text,?::text,?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, vacio(tipoAlimento));
                ps.setString(2, vacio(cantidad));
                ps.setString(3, vacio(horarioComida));
                ps.setString(4, vacio(horarioPaseo));
                ps.setString(5, vacio(cuidados));
                ps.setString(6, vacio(comportamiento));
                ps.setString(7, vacio(observaciones));
                ps.setInt(8, idReserva);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> medicamentos(int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = """
                    SELECT nombre_medicamento, dosis, frecuencia,
                           horario_medicamento, fecha_inicio,
                           fecha_fin, cantidad_entregada,
                           unidad_medida, indicaciones
                    FROM medicacion_reserva
                    WHERE id_reserva = ?
                    ORDER BY id_medicacion_reserva
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getDate(5), rs.getDate(6),
                                rs.getBigDecimal(7),
                                rs.getString(8), rs.getString(9)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        void registrarMedicamento(String nombre, String dosis,
                                  String frecuencia, String horario,
                                  LocalDate inicio, LocalDate fin,
                                  BigDecimal cantidad,
                                  String unidad, String indicaciones,
                                  int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = "SELECT fn_registrar_medicacion_reserva("
                    + "?::varchar,?::varchar,?::varchar,?::varchar,"
                    + "?::date,?::date,?::numeric,?::numeric,"
                    + "?::numeric,?::varchar,?::text,?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setString(2, dosis);
                ps.setString(3, vacio(frecuencia));
                ps.setString(4, vacio(horario));

                if (inicio == null) {
                    ps.setNull(5, Types.DATE);
                } else {
                    ps.setDate(5, Date.valueOf(inicio));
                }

                if (fin == null) {
                    ps.setNull(6, Types.DATE);
                } else {
                    ps.setDate(6, Date.valueOf(fin));
                }

                if (cantidad == null) {
                    ps.setNull(7, Types.NUMERIC);
                } else {
                    ps.setBigDecimal(7, cantidad);
                }

                ps.setNull(8, Types.NUMERIC);
                ps.setNull(9, Types.NUMERIC);
                ps.setString(10, vacio(unidad));
                ps.setString(11, vacio(indicaciones));
                ps.setInt(12, idReserva);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> accesorios(int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = """
                    SELECT nombre_accesorio, descripcion, cantidad,
                           estado_entrega, estado_devolucion,
                           observaciones
                    FROM accesorio_entregado
                    WHERE id_reserva = ?
                    ORDER BY id_accesorio_entregado
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getInt(3), rs.getString(4),
                                rs.getString(5), rs.getString(6)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        void registrarAccesorio(String nombre, String descripcion,
                                int cantidad, String observaciones,
                                int idReserva, int idUsuario) {

            if (!esReservaDelUsuario(idReserva, idUsuario)) {
                throw new RuntimeException(
                        "Esta reserva no te pertenece.");
            }

            String sql = "SELECT fn_registrar_accesorio_entregado("
                    + "?::varchar,?::varchar,?::integer,?::varchar,"
                    + "?::varchar,?::text,?::integer)";

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setString(1, nombre);
                ps.setString(2, vacio(descripcion));
                ps.setInt(3, cantidad);
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
                ps.setString(6, vacio(observaciones));
                ps.setInt(7, idReserva);

                ps.executeQuery();

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }



        boolean existePagoPendiente(int idReserva) {

            String sql = """
                    SELECT COUNT(*) FROM pago
                    WHERE id_reserva = ?
                      AND estado_pago = 'PENDIENTE'
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void registrarPago(BigDecimal montoMostrado, String forma,
                           String comprobante, String observaciones,
                           int idReserva, int idUsuario) {

            String sqlReserva = """
                    SELECT COALESCE(r.total_acordado, 0),
                           r.estado_reserva
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    WHERE r.id_reserva = ?
                      AND m.id_usuario = ?
                    FOR UPDATE OF r
                    """;

            String sqlTransporte = """
                    SELECT estado_transporte, precio_acordado
                    FROM transporte
                    WHERE id_reserva = ?
                    FOR UPDATE
                    """;

            String sqlPagos = """
                    SELECT estado_pago, monto
                    FROM pago
                    WHERE id_reserva = ?
                    FOR UPDATE
                    """;

            String sqlComprobante = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM pago
                        WHERE numero_comprobante = ?
                    )
                    """;

            String sqlRegistro = """
                    SELECT fn_registrar_pago(
                        ?::timestamp,
                        ?::numeric,
                        ?::varchar,
                        ?::varchar,
                        ?::varchar,
                        ?::varchar,
                        ?::text,
                        ?::integer)
                    """;

            try (Connection c = ConexionBD.conectar()) {

                c.setAutoCommit(false);

                try {
                    BigDecimal totalAlojamiento;
                    String estadoReserva;

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlReserva)) {
                        ps.setInt(1, idReserva);
                        ps.setInt(2, idUsuario);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new RuntimeException(
                                        "La reserva no existe o no te "
                                                + "pertenece.");
                            }
                            totalAlojamiento = rs.getBigDecimal(1)
                                    .setScale(2, RoundingMode.HALF_UP);
                            estadoReserva = rs.getString(2);
                        }
                    }

                    if (!("ACEPTADA".equals(estadoReserva)
                            || "EN_CURSO".equals(estadoReserva))) {
                        throw new RuntimeException(
                                "La reserva todavía no está habilitada "
                                        + "para pagar.");
                    }

                    BigDecimal precioTransporte = BigDecimal.ZERO
                            .setScale(2, RoundingMode.HALF_UP);

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlTransporte)) {
                        ps.setInt(1, idReserva);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                String estadoTransporte = rs.getString(1);
                                BigDecimal precio = rs.getBigDecimal(2);

                                if ("PENDIENTE".equals(
                                        estadoTransporte)) {
                                    throw new RuntimeException(
                                            "El transporte sigue pendiente "
                                                    + "de cotización.");
                                }

                                if ("PROGRAMADO".equals(
                                        estadoTransporte)
                                        || "FINALIZADO".equals(
                                        estadoTransporte)) {
                                    if (precio == null
                                            || precio.signum() <= 0) {
                                        throw new RuntimeException(
                                                "La cotización del transporte "
                                                        + "no es válida.");
                                    }
                                    precioTransporte = precio.setScale(2,
                                            RoundingMode.HALF_UP);
                                }
                            }
                        }
                    }

                    BigDecimal totalFinal = totalAlojamiento
                            .add(precioTransporte)
                            .setScale(2, RoundingMode.HALF_UP);
                    BigDecimal pagado = BigDecimal.ZERO
                            .setScale(2, RoundingMode.HALF_UP);
                    boolean pendiente = false;

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlPagos)) {
                        ps.setInt(1, idReserva);

                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String estadoPago = rs.getString(1);
                                if ("PENDIENTE".equals(estadoPago)) {
                                    pendiente = true;
                                } else if ("APROBADO".equals(
                                        estadoPago)) {
                                    pagado = pagado.add(
                                            rs.getBigDecimal(2));
                                }
                            }
                        }
                    }

                    if (pendiente) {
                        throw new RuntimeException(
                                "Ya existe un pago pendiente para esta "
                                        + "reserva.");
                    }

                    if (pagado.compareTo(totalFinal) >= 0) {
                        throw new RuntimeException(
                                "La reserva ya está pagada completamente.");
                    }

                    if (pagado.signum() > 0) {
                        throw new RuntimeException(
                                "La reserva ya tiene un pago aprobado "
                                        + "parcial. No es posible crear el "
                                        + "pago único completo desde este "
                                        + "panel.");
                    }

                    String comprobanteNormalizado =
                            comprobante == null
                                    ? null : comprobante.trim();

                    if ("TRANSFERENCIA".equals(forma)) {
                        if (comprobanteNormalizado == null
                                || comprobanteNormalizado.isBlank()) {
                            throw new RuntimeException(
                                    "Ingresa el número de comprobante o "
                                            + "referencia de la "
                                            + "transferencia.");
                        }

                        try (PreparedStatement ps =
                                     c.prepareStatement(sqlComprobante)) {
                            ps.setString(1, comprobanteNormalizado);
                            try (ResultSet rs = ps.executeQuery()) {
                                if (rs.next() && rs.getBoolean(1)) {
                                    throw new RuntimeException(
                                            "Este número de comprobante ya "
                                                    + "fue registrado.\n"
                                                    + "Ingresa un número "
                                                    + "diferente.");
                                }
                            }
                        }
                    } else {
                        comprobanteNormalizado = null;
                    }

                    BigDecimal esperado = montoMostrado.setScale(2,
                            RoundingMode.HALF_UP);

                    if (esperado.compareTo(totalFinal) != 0) {
                        throw new RuntimeException(
                                "El total de la reserva cambió. Cierra "
                                        + "este formulario y vuelve a abrirlo "
                                        + "para ver el monto actualizado.");
                    }

                    try (PreparedStatement ps =
                                 c.prepareStatement(sqlRegistro)) {
                        ps.setNull(1, Types.TIMESTAMP);
                        ps.setBigDecimal(2, totalFinal);
                        ps.setString(3, forma);
                        ps.setString(4, "PENDIENTE");
                        if (comprobanteNormalizado == null) {
                            ps.setNull(5, Types.VARCHAR);
                        } else {
                            ps.setString(5, comprobanteNormalizado);
                        }
                        ps.setString(6, "COMPLETO");
                        ps.setString(7, vacio(observaciones));
                        ps.setInt(8, idReserva);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next() || rs.getInt(1) <= 0) {
                                throw new RuntimeException(
                                        "No se pudo registrar el pago.");
                            }
                        }
                    }

                    c.commit();

                } catch (SQLException e) {
                    rollback(c);
                    throw e;
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }

            } catch (SQLException e) {
                if ("23505".equals(e.getSQLState())) {
                    throw new RuntimeException(
                            "Este número de comprobante ya fue "
                                    + "registrado.\nIngresa un número "
                                    + "diferente.", e);
                }
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private void rollback(Connection c) {
            try {
                c.rollback();
            } catch (SQLException ignored) {

            }
        }

        List<Object[]> pagos(int idUsuario) {

            String sql = """
                    SELECT p.id_pago, p.id_reserva,
                           TO_CHAR(COALESCE(p.fecha_pago,
                             p.fecha_registro), 'DD/MM/YYYY'),
                           a.nombre, p.forma_pago, p.tipo_pago,
                           p.estado_pago,
                           '$ ' || TO_CHAR(p.monto, 'FM999G999D00'),
                           p.observaciones
                    FROM pago p
                    JOIN reserva r ON r.id_reserva = p.id_reserva
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    WHERE m.id_usuario = ?
                    ORDER BY COALESCE(p.fecha_pago,
                                      p.fecha_registro) DESC,
                             p.id_pago DESC
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getInt(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6),
                                rs.getString(7), rs.getString(8),
                                rs.getString(9)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }



        boolean yaCalifico(int idReserva, int idAutor) {

            String sql = """
                    SELECT COUNT(*) FROM resena
                    WHERE id_reserva = ?
                      AND id_usuario_autor = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idReserva);
                ps.setInt(2, idAutor);

                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getInt(1) > 0;
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void registrarResena(int calificacion, String comentario,
                             int idReserva, int idAutor,
                             int idEvaluado) {

            if (calificacion < 1 || calificacion > 5) {
                throw new IllegalArgumentException(
                        "Selecciona una calificación entre 1 y 5 estrellas.");
            }

            String validar = """
                    SELECT uc.id_usuario
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN alojamiento a
                      ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador cu ON cu.id_cuidador = a.id_cuidador
                    JOIN usuario uc ON uc.id_usuario = cu.id_usuario
                    WHERE r.id_reserva = ?::integer
                      AND m.id_usuario = ?::integer
                      AND r.estado_reserva = 'FINALIZADA'
                    FOR UPDATE OF r
                    """;

            String duplicada = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM resena
                        WHERE id_reserva = ?::integer
                          AND id_usuario_autor = ?::integer
                    )
                    """;

            String registrar = "SELECT fn_registrar_resena("
                    + "?::integer,?::text,?::integer,?::integer,"
                    + "?::integer)";

            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);

                try {
                    int idUsuarioCuidador;

                    try (PreparedStatement ps = c.prepareStatement(validar)) {
                        ps.setInt(1, idReserva);
                        ps.setInt(2, idAutor);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                        "La reserva no está finalizada o no pertenece al usuario.");
                            }
                            idUsuarioCuidador = rs.getInt(1);
                        }
                    }

                    if (idUsuarioCuidador != idEvaluado) {
                        throw new SQLException(
                                "El cuidador evaluado no corresponde a la reserva.");
                    }

                    try (PreparedStatement ps = c.prepareStatement(duplicada)) {
                        ps.setInt(1, idReserva);
                        ps.setInt(2, idAutor);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next() && rs.getBoolean(1)) {
                                throw new SQLException(
                                        "Ya calificaste esta estadía.");
                            }
                        }
                    }

                    try (PreparedStatement ps = c.prepareStatement(registrar)) {
                        ps.setInt(1, calificacion);
                        ps.setString(2, vacio(comentario));
                        ps.setInt(3, idReserva);
                        ps.setInt(4, idAutor);
                        ps.setInt(5, idUsuarioCuidador);
                        ps.executeQuery();
                    }

                    c.commit();
                } catch (SQLException | RuntimeException e) {
                    c.rollback();
                    throw e;
                } finally {
                    c.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }



        List<Object[]> contactos(int idUsuario) {

            String sql = """
                    SELECT id_contacto_emergencia, nombre_contacto,
                           telefono_contacto, parentesco,
                           es_contacto_principal
                    FROM contacto_emergencia
                    WHERE id_usuario = ?
                    ORDER BY es_contacto_principal DESC,
                             nombre_contacto
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idUsuario);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getBoolean(5)
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return lista;
        }

        void crearContacto(String nombre, String telefono,
                           String parentesco, boolean principal,
                           int idUsuario) {

            Connection c = null;

            try {
                c = ConexionBD.conectar();
                c.setAutoCommit(false);

                if (principal) {
                    quitarPrincipal(c, idUsuario);
                }

                try (PreparedStatement ps = c.prepareStatement("""
                        INSERT INTO contacto_emergencia (
                            nombre_contacto, telefono_contacto,
                            parentesco, es_contacto_principal,
                            id_usuario)
                        VALUES (?, ?, ?, ?, ?)
                        """)) {

                    ps.setString(1, nombre);
                    ps.setString(2, telefono);
                    ps.setString(3, vacio(parentesco));
                    ps.setBoolean(4, principal);
                    ps.setInt(5, idUsuario);
                    ps.executeUpdate();
                }

                c.commit();

            } catch (SQLException e) {
                revertir(c);
                throw new RuntimeException(mensaje(e), e);
            } finally {
                cerrar(c);
            }
        }

        void actualizarContacto(int idContacto, String nombre,
                                String telefono, String parentesco,
                                boolean principal, int idUsuario) {

            Connection c = null;

            try {
                c = ConexionBD.conectar();
                c.setAutoCommit(false);

                if (principal) {
                    quitarPrincipal(c, idUsuario);
                }

                try (PreparedStatement ps = c.prepareStatement("""
                        UPDATE contacto_emergencia
                        SET nombre_contacto = ?,
                            telefono_contacto = ?,
                            parentesco = ?,
                            es_contacto_principal = ?
                        WHERE id_contacto_emergencia = ?
                          AND id_usuario = ?
                        """)) {

                    ps.setString(1, nombre);
                    ps.setString(2, telefono);
                    ps.setString(3, vacio(parentesco));
                    ps.setBoolean(4, principal);
                    ps.setInt(5, idContacto);
                    ps.setInt(6, idUsuario);

                    if (ps.executeUpdate() == 0) {
                        throw new SQLException(
                                "Este contacto no te pertenece.");
                    }
                }

                c.commit();

            } catch (SQLException e) {
                revertir(c);
                throw new RuntimeException(mensaje(e), e);
            } finally {
                cerrar(c);
            }
        }

        private void quitarPrincipal(Connection c, int idUsuario)
                throws SQLException {

            try (PreparedStatement ps = c.prepareStatement("""
                    UPDATE contacto_emergencia
                    SET es_contacto_principal = FALSE
                    WHERE id_usuario = ?
                      AND es_contacto_principal
                    """)) {

                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
        }

        private void revertir(Connection c) {

            if (c == null) {
                return;
            }

            try {
                c.rollback();
            } catch (SQLException ignorado) {

            }
        }

        private void cerrar(Connection c) {

            if (c == null) {
                return;
            }

            try {
                c.setAutoCommit(true);
                c.close();
            } catch (SQLException ignorado) {

            }
        }

        void eliminarContacto(int idContacto, int idUsuario) {

            String sql = """
                    DELETE FROM contacto_emergencia
                    WHERE id_contacto_emergencia = ?
                      AND id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, idContacto);
                ps.setInt(2, idUsuario);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "Este contacto no te pertenece.");
                }

            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private String vacio(String t) {
            return t == null || t.isBlank() ? null : t;
        }

        private String mensaje(SQLException e) {

            String m = e.getMessage();

            if (m == null || m.isBlank()) {
                return "Error al comunicarse con la base de datos.";
            }

            int error = m.indexOf("ERROR:");
            if (error >= 0) {
                m = m.substring(error + 6);
            }

            int detalle = m.indexOf("Detail:");
            if (detalle >= 0) {
                m = m.substring(0, detalle);
            }

            int donde = m.indexOf("Where:");
            if (donde >= 0) {
                m = m.substring(0, donde);
            }

            int donde2 = m.indexOf("Donde:");
            if (donde2 >= 0) {
                m = m.substring(0, donde2);
            }

            return m.trim();
        }
    }





    private String dinero(double v) {
        return "$ " + String.format("%,.2f", v);
    }

    private String texto(String v, String defecto) {
        return v == null || v.isBlank() ? defecto : v;
    }

    private String corto(String t, int max) {

        if (t == null) {
            return "";
        }

        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }

    private String escapar(String t) {

        if (t == null) {
            return "";
        }

        return t.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String iniciales(String nombre) {

        String limpio = texto(nombre, "U").trim();

        if (limpio.isBlank()) {
            return "U";
        }

        String[] partes = limpio.split("\\s+");

        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0)
                    + partes[1].charAt(0)).toUpperCase();
        }

        return limpio.substring(0, 1).toUpperCase();
    }

    private void cerrarSesion() {

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        SesionUsuario.cerrarSesion();

        Login login = new Login();
        login.setVisible(true);
        dispose();
    }

    private JComponent crearLogo(int lado) {

        File carpeta = new File(System.getProperty("user.dir"));
        File[] archivos = carpeta.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {

                String nombre = archivo.getName().toLowerCase();

                if (nombre.startsWith("logo")
                        && (nombre.endsWith(".png")
                        || nombre.endsWith(".jpg")
                        || nombre.endsWith(".jpeg"))) {

                    try {
                        BufferedImage imagen =
                                ImageIO.read(archivo);

                        if (imagen == null) {
                            continue;
                        }

                        BufferedImage resultado =
                                new BufferedImage(lado, lado,
                                        BufferedImage.TYPE_INT_ARGB);

                        Graphics2D g2 = resultado.createGraphics();
                        g2.setRenderingHint(
                                RenderingHints.KEY_INTERPOLATION,
                                RenderingHints
                                        .VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(
                                RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setClip(new RoundRectangle2D.Float(
                                0, 0, lado, lado, 13, 13));
                        g2.drawImage(imagen, 0, 0, lado, lado, null);
                        g2.dispose();

                        return new JLabel(new ImageIcon(resultado));

                    } catch (IOException e) {
                        continue;
                    }
                }
            }
        }

        return new JLabel(icono(HUELLA, AZUL, lado));
    }

    private ImageIcon icono(int tipo, Color color, int lado) {

        BufferedImage imagen = new BufferedImage(
                lado, lado, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = imagen.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(color);
        g2.scale(lado / 100.0, lado / 100.0);
        g2.setStroke(new BasicStroke(9f,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {

            case HUELLA -> {
                g2.fillOval(26, 46, 48, 40);
                g2.fillOval(14, 20, 20, 26);
                g2.fillOval(40, 8, 20, 26);
                g2.fillOval(66, 20, 20, 26);
            }

            case PERSONA -> {
                g2.drawOval(34, 14, 32, 32);
                g2.draw(new Arc2D.Float(18, 52, 64, 60,
                        0, 180, Arc2D.OPEN));
            }

            case VISTO -> {
                g2.setStroke(new BasicStroke(13f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g2.drawLine(18, 52, 40, 74);
                g2.drawLine(40, 74, 82, 26);
            }

            case EQUIS -> {
                g2.setStroke(new BasicStroke(13f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));
                g2.drawLine(24, 24, 76, 76);
                g2.drawLine(76, 24, 24, 76);
            }

            case RELOJ -> {
                g2.drawRoundRect(10, 22, 80, 70, 12, 12);
                g2.drawLine(10, 44, 90, 44);
                g2.drawLine(32, 10, 32, 30);
                g2.drawLine(68, 10, 68, 30);
            }

            case SALIR -> {
                g2.drawLine(52, 12, 20, 12);
                g2.drawLine(20, 12, 20, 88);
                g2.drawLine(20, 88, 52, 88);
                g2.drawLine(46, 50, 88, 50);
                g2.drawLine(88, 50, 68, 30);
                g2.drawLine(88, 50, 68, 70);
            }

            case SOBRE -> {
                g2.drawRoundRect(8, 24, 84, 54, 10, 10);
                g2.drawLine(14, 30, 50, 56);
                g2.drawLine(86, 30, 50, 56);
            }

            case BUSCAR -> {
                g2.drawOval(14, 14, 56, 56);
                g2.drawLine(62, 62, 88, 88);
            }

            case MASCOTA -> {
                g2.fillOval(30, 44, 40, 34);
                g2.fillOval(18, 22, 17, 22);
                g2.fillOval(41, 12, 17, 22);
                g2.fillOval(64, 22, 17, 22);
            }

            case RESERVA -> {
                g2.drawRoundRect(10, 20, 80, 70, 10, 10);
                g2.drawLine(10, 40, 90, 40);
                g2.drawLine(30, 8, 30, 28);
                g2.drawLine(70, 8, 70, 28);
                g2.fillOval(28, 54, 10, 10);
                g2.fillOval(46, 54, 10, 10);
                g2.fillOval(64, 54, 10, 10);
            }

            case DINERO -> {
                g2.drawOval(10, 10, 80, 80);
                g2.drawLine(50, 22, 50, 78);
                g2.draw(new Arc2D.Float(31, 25, 38, 30,
                        65, 240, Arc2D.OPEN));
                g2.draw(new Arc2D.Float(31, 45, 38, 30,
                        245, 240, Arc2D.OPEN));
            }

            case CASA -> {
                g2.drawLine(8, 48, 50, 12);
                g2.drawLine(50, 12, 92, 48);
                g2.drawLine(20, 46, 20, 90);
                g2.drawLine(80, 46, 80, 90);
                g2.drawLine(20, 90, 80, 90);
            }

            case ESTRELLA -> {
                Polygon estrella = new Polygon();

                for (int i = 0; i < 10; i++) {
                    double radio = (i % 2 == 0) ? 46 : 19;
                    double angulo = Math.toRadians(-90 + i * 36);
                    estrella.addPoint(
                            (int) Math.round(
                                    50 + radio * Math.cos(angulo)),
                            (int) Math.round(
                                    50 + radio * Math.sin(angulo)));
                }

                g2.fillPolygon(estrella);
            }

            case UBICACION -> {
                g2.drawOval(20, 8, 60, 60);
                g2.fillOval(42, 28, 16, 16);
                g2.drawLine(23, 52, 50, 92);
                g2.drawLine(77, 52, 50, 92);
            }

            case MAS -> {
                g2.drawLine(50, 20, 50, 80);
                g2.drawLine(20, 50, 80, 50);
            }

            case ACTUALIZAR -> {
                g2.draw(new Arc2D.Float(15, 15, 70, 70,
                        30, 280, Arc2D.OPEN));
                g2.drawLine(76, 11, 86, 31);
                g2.drawLine(86, 31, 65, 31);
            }

            case TELEFONO -> {
                java.awt.geom.Path2D.Float telefono =
                        new java.awt.geom.Path2D.Float();
                telefono.moveTo(21, 16);
                telefono.curveTo(12, 22, 13, 37, 20, 49);
                telefono.curveTo(31, 68, 50, 84, 69, 89);
                telefono.curveTo(80, 92, 89, 85, 92, 76);
                telefono.lineTo(77, 62);
                telefono.curveTo(73, 58, 67, 58, 62, 63);
                telefono.lineTo(56, 70);
                telefono.curveTo(44, 64, 35, 55, 29, 43);
                telefono.lineTo(36, 36);
                telefono.curveTo(41, 31, 40, 24, 35, 19);
                telefono.closePath();
                g2.fill(telefono);
            }

            default -> {
            }
        }

        g2.dispose();

        return new ImageIcon(imagen);
    }

    private static class PanelPatron extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(FONDO);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(PATRON);

            Random aleatorio = new Random(11);
            int paso = 170;

            for (int y = -40; y < getHeight() + 170; y += paso) {
                for (int x = -40; x < getWidth() + 170; x += paso) {

                    int px = x + aleatorio.nextInt(50) - 25;
                    int py = y + aleatorio.nextInt(50) - 25;

                    if (aleatorio.nextInt(2) == 0) {
                        huella(g2, px, py);
                    } else {
                        casa(g2, px, py);
                    }
                }
            }

            g2.dispose();
        }

        private void huella(Graphics2D g2, int x, int y) {
            g2.fillOval(x + 2, y + 12, 24, 19);
            g2.fillOval(x - 1, y + 1, 9, 11);
            g2.fillOval(x + 9, y - 3, 9, 11);
            g2.fillOval(x + 19, y + 1, 9, 11);
        }

        private void casa(Graphics2D g2, int x, int y) {
            g2.setStroke(new BasicStroke(2.4f));
            g2.drawRect(x + 2, y + 11, 24, 18);
            g2.drawLine(x - 3, y + 11, x + 14, y - 2);
            g2.drawLine(x + 14, y - 2, x + 31, y + 11);
        }
    }

    private static class PanelCaja extends JPanel {

        private final int radio;
        private final Color fondo;
        private final Color borde;

        PanelCaja(int radio, Color fondo, Color borde) {
            this.radio = radio;
            this.fondo = fondo;
            this.borde = borde;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                    radio, radio);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1,
                        getHeight() - 1, radio, radio);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class Avatar extends JComponent {

        private final int lado;
        private final String iniciales;

        Avatar(int lado, String iniciales) {
            this.lado = lado;
            this.iniciales = iniciales;
            setPreferredSize(new Dimension(lado, lado));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(AMARILLO);
            g2.fillOval(0, 0, lado, lado);

            g2.setColor(AZUL_OSCURO);
            g2.setFont(new Font(FUENTE, Font.BOLD,
                    Math.max(14, lado / 3)));

            FontMetrics m = g2.getFontMetrics();

            g2.drawString(iniciales,
                    (lado - m.stringWidth(iniciales)) / 2,
                    (lado - m.getHeight()) / 2 + m.getAscent());

            g2.dispose();
        }
    }

    private static class BotonRedondeado extends JButton {

        private final Color fondo;
        private final Color borde;
        private final Color fondoFinal;

        BotonRedondeado(String texto, Color fondo, Color textoColor,
                        Color borde, Color fondoFinal) {
            super(texto);
            this.fondo = fondo;
            this.borde = borde;
            this.fondoFinal = fondoFinal;

            setFont(new Font(FUENTE, Font.BOLD, 14));
            setForeground(textoColor);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setMargin(new Insets(0, 0, 0, 0));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            setPreferredSize(new Dimension(180, 46));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (fondoFinal != null) {
                g2.setPaint(new GradientPaint(0, 0, fondo,
                        getWidth(), 0, fondoFinal));
            } else {
                g2.setPaint(fondo);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 11, 11);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1,
                        getHeight() - 1, 11, 11);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonMenu extends JButton {

        private final String seccion;
        private boolean activo;

        BotonMenu(String texto, Icon icono, String seccion) {
            super(texto, icono);
            this.seccion = seccion;

            setHorizontalAlignment(SwingConstants.LEFT);
            setIconTextGap(13);
            setFont(new Font(FUENTE, Font.BOLD, 13));
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(11, 14, 11, 14));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(196, 44));
            setAlignmentX(Component.LEFT_ALIGNMENT);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!activo) {
                        setOpaque(true);
                        setBackground(LATERAL_HOVER);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!activo) {
                        setOpaque(false);
                        repaint();
                    }
                }
            });
        }

        String getSeccion() {
            return seccion;
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            setOpaque(activo);
            setBackground(activo ? AZUL : LATERAL);
            repaint();
        }
    }
}

