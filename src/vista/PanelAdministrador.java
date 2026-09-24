
package vista;

import conexion.ConexionBD;
import dao.CuidadorDAO;
import dao.UsuarioDAO;
import modelo.Cuidador;
import modelo.Usuario;
import sesion.SesionUsuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;








public class PanelAdministrador extends JFrame {





    private static final Color AZUL          = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO   = new Color(16, 62, 108);
    private static final Color AZUL_GRAD_1   = new Color(22, 104, 214);
    private static final Color AZUL_GRAD_2   = new Color(41, 137, 226);
    private static final Color FONDO         = new Color(238, 246, 253);
    private static final Color FONDO_CAMPO   = new Color(247, 250, 253);
    private static final Color BORDE_CAMPO   = new Color(226, 236, 246);
    private static final Color GRIS_TEXTO    = new Color(112, 133, 153);
    private static final Color GRIS_PISTA    = new Color(150, 167, 182);
    private static final Color GRIS_REJILLA  = new Color(237, 243, 249);
    private static final Color AMARILLO      = new Color(242, 194, 48);
    private static final Color AMARILLO_PILL = new Color(252, 240, 205);
    private static final Color AMARILLO_TXT  = new Color(150, 113, 26);
    private static final Color VERDE         = new Color(34, 150, 105);
    private static final Color VERDE_PILL    = new Color(219, 243, 232);
    private static final Color ROJO          = new Color(199, 62, 62);
    private static final Color ROJO_PILL     = new Color(252, 226, 226);
    private static final Color MORADO        = new Color(116, 88, 190);
    private static final Color NARANJA       = new Color(230, 145, 56);
    private static final Color CELESTE       = new Color(70, 180, 210);
    private static final Color PATRON        = new Color(228, 240, 250);
    private static final Color LATERAL       = new Color(15, 57, 101);
    private static final Color LATERAL_HOVER = new Color(24, 78, 132);

    private static final Color[] SERIE = {
            AZUL, AMARILLO, VERDE, MORADO, NARANJA, CELESTE, ROJO
    };

    private static final String FUENTE = "Segoe UI";
    private static final int ANCHO_CONTENIDO_MINIMO = 1400;
    private static final int ANCHO_TABLA_MINIMO = 1340;


    private static final int HUELLA     = 0;
    private static final int PERSONA    = 1;
    private static final int VISTO      = 2;
    private static final int EQUIS      = 3;
    private static final int RELOJ      = 4;
    private static final int SALIR      = 5;
    private static final int SOBRE      = 6;
    private static final int TABLERO    = 7;
    private static final int BANDEJA    = 8;
    private static final int USUARIOS   = 9;
    private static final int RESERVA    = 10;
    private static final int DINERO     = 11;
    private static final int CASA       = 12;
    private static final int REPORTE    = 13;
    private static final int OJO        = 14;
    private static final int TELEFONO   = 15;
    private static final int UBICACION  = 16;
    private static final int TARJETA    = 17;
    private static final int ACTUALIZAR = 18;
    private static final int ESTRELLA   = 19;
    private static final int TENDENCIA  = 20;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");





    private final Usuario administrador;

    private final CuidadorDAO cuidadorDAO = new CuidadorDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Consultas consultas = new Consultas();

    private final CardLayout layout = new CardLayout();
    private JPanel contenido;

    private JPanel cuerpoDashboard;
    private JPanel cuerpoReportes;
    private JPanel listaSolicitudes;
    private JPanel listaPagos;
    private JLabel lblContador;
    private JLabel lblContadorPagos;
    private JLabel lblAviso;
    private DashboardDatos datosDashboard;

    private String estadoActual = "PENDIENTE";
    private String seccionVisible = "";
    private final JButton[] filtros = new JButton[3];
    private final List<BotonMenu> menu = new ArrayList<>();
    private final javax.swing.Timer actualizadorDashboard =
            new javax.swing.Timer(30_000, e -> {
                if ("DASHBOARD".equals(seccionVisible) && isShowing()) {
                    construirDashboard();
                }
            });

    public PanelAdministrador(Usuario administrador) {
        this.administrador = administrador;
        configurarVentana();
        crearComponentes();
        mostrar("DASHBOARD");
        actualizadorDashboard.setInitialDelay(30_000);
        actualizadorDashboard.start();
    }

    private void configurarVentana() {
        setTitle("Panel de administración - Pet Home Boarding");
        setSize(1360, 860);
        setMinimumSize(new Dimension(1180, 760));
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

        cuerpoDashboard = new JPanel(new BorderLayout());
        cuerpoDashboard.setOpaque(false);

        cuerpoReportes = new JPanel(new BorderLayout());
        cuerpoReportes.setOpaque(false);

        contenido.add(envolverSeccion(marco("Dashboard general",
                "Indicadores clave del negocio en tiempo real",
                cuerpoDashboard, () -> construirDashboard())), "DASHBOARD");

        contenido.add(envolverSeccion(crearSeccionSolicitudes()),
                "SOLICITUDES");

        contenido.add(envolverSeccion(crearSeccionPagos()),
                "PAGOS_PENDIENTES");

        contenido.add(envolverSeccion(marco("Reportes administrativos",
                "Consulta, filtra y exporta información real del negocio",
                cuerpoReportes, () -> construirReportes())), "REPORTES");

        contenido.add(envolverSeccion(crearSeccionTabla(
                "Cuidadores", "Perfiles verificados y su actividad",
                Consultas.SQL_CUIDADORES)), "CUIDADORES");

        contenido.add(envolverSeccion(crearSeccionTabla(
                "Usuarios", "Comunidad registrada en la plataforma",
                Consultas.SQL_USUARIOS)), "USUARIOS");

        contenido.add(envolverSeccion(crearSeccionTabla(
                "Reservas", "Historial de estadías registradas",
                Consultas.SQL_RESERVAS)), "RESERVAS");

        contenido.add(envolverSeccion(crearSeccionTabla(
                "Ingresos", "Detalle de pagos recibidos",
                Consultas.SQL_PAGOS)), "INGRESOS");

        contenido.add(envolverSeccion(crearSeccionTabla(
                "Alojamientos", "Casas publicadas por los cuidadores",
                Consultas.SQL_ALOJAMIENTOS)), "ALOJAMIENTOS");

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);
    }

    private JScrollPane envolverSeccion(JComponent seccion) {
        JPanel lienzo = new LienzoAdaptable(ANCHO_CONTENIDO_MINIMO);
        lienzo.setLayout(new BorderLayout());
        lienzo.setOpaque(false);
        lienzo.add(seccion, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(lienzo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        return scroll;
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

        JLabel lblTitulo = new JLabel("Panel de administración");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 20));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblSub = new JLabel(
                "Gestión y análisis de Pet Home Boarding"
        );
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
                "Hola, " + texto(administrador.getNombre(), "Administrador")
        );
        lblHola.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblHola.setForeground(AZUL_OSCURO);

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

        derecha.add(lblHola);
        derecha.add(btnSalir);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }





    private JPanel crearMenuLateral() {

        JPanel lateral = new JPanel();
        lateral.setBackground(LATERAL);
        lateral.setPreferredSize(new Dimension(226, 0));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(new EmptyBorder(22, 14, 18, 14));

        JLabel lblMenu = new JLabel("MENÚ PRINCIPAL");
        lblMenu.setFont(new Font(FUENTE, Font.BOLD, 11));
        lblMenu.setForeground(new Color(174, 201, 225));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMenu.setBorder(new EmptyBorder(0, 12, 12, 0));

        lateral.add(lblMenu);
        lateral.add(boton("Dashboard", TABLERO, "DASHBOARD"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Solicitudes", BANDEJA, "SOLICITUDES"));

        lblAviso = new JLabel();
        lblAviso.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblAviso.setForeground(AMARILLO);
        lblAviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAviso.setBorder(new EmptyBorder(3, 46, 3, 0));
        lateral.add(lblAviso);

        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Pagos por verificar", DINERO,
                "PAGOS_PENDIENTES"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Cuidadores", PERSONA, "CUIDADORES"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Usuarios", USUARIOS, "USUARIOS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Reservas", RESERVA, "RESERVAS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Ingresos", DINERO, "INGRESOS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Alojamientos", CASA, "ALOJAMIENTOS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(boton("Reportes", REPORTE, "REPORTES"));
        lateral.add(Box.createVerticalGlue());

        JLabel lblPie = new JLabel(
                "<html><b style='color:#FFFFFF;'>Pet Home Boarding</b>"
                        + "<br><span style='color:#AEC9E1;'>"
                        + "Panel administrativo</span></html>"
        );
        lblPie.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblPie.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPie.setBorder(new EmptyBorder(12, 12, 0, 0));

        lateral.add(lblPie);

        return lateral;
    }

    private BotonMenu boton(String texto, int tipoIcono,
                            String seccion) {

        BotonMenu btn = new BotonMenu(
                texto, icono(tipoIcono, Color.WHITE, 18), seccion
        );
        btn.addActionListener(e -> mostrar(seccion));
        menu.add(btn);
        return btn;
    }

    private void mostrar(String seccion) {

        seccionVisible = seccion;
        layout.show(contenido, seccion);

        for (BotonMenu btn : menu) {
            btn.setActivo(btn.getSeccion().equals(seccion));
        }

        switch (seccion) {
            case "DASHBOARD" -> construirDashboard();
            case "SOLICITUDES" -> cargarSolicitudes();
            case "PAGOS_PENDIENTES" -> cargarPagosPendientes();
            case "REPORTES" -> construirReportes();
            default -> {
            }
        }
    }





    private JPanel marco(String titulo, String subtitulo,
                         JPanel cuerpo, Runnable recargar) {

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

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSub.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblSub);

        JPanel cajaBoton =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        cajaBoton.setOpaque(false);
        if ("Dashboard general".equals(titulo)) {
            cajaBoton.add(botonBlanco("Guardar Dashboard como PDF",
                    REPORTE, e -> exportarDashboard()));
        }
        cajaBoton.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> recargar.run()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(cajaBoton, BorderLayout.EAST);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(cuerpo, BorderLayout.CENTER);

        return fondo;
    }

    private JButton botonBlanco(String texto, int tipoIcono,
                                java.awt.event.ActionListener accion) {

        JButton btn = new JButton(texto);
        btn.setIcon(icono(tipoIcono, AZUL, 15));
        btn.setIconTextGap(8);
        btn.setFont(new Font(FUENTE, Font.BOLD, 13));
        btn.setForeground(AZUL);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO),
                new EmptyBorder(9, 16, 9, 16)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(accion);

        return btn;
    }





    private void construirDashboard() {

        cuerpoDashboard.removeAll();

        Consultas.Resumen r;
        LinkedHashMap<String, Double> ingresosMes;
        LinkedHashMap<String, Double> reservasMes;
        LinkedHashMap<String, Double> estadosReserva;
        LinkedHashMap<String, Double> solicitudes;
        List<Object[]> ranking;
        List<Object[]> alojamientos;

        try {
            r = consultas.resumen();
            ingresosMes = consultas.ingresosPorMes(12);
            reservasMes = consultas.reservasPorMes(12);
            estadosReserva = consultas.reservasPorEstado();
            solicitudes = consultas.solicitudesPorEstado();
            ranking = consultas.rankingCuidadores(6);
            alojamientos = consultas.rankingAlojamientos(6);

        } catch (RuntimeException e) {
            cuerpoDashboard.add(error(e.getMessage()),
                    BorderLayout.CENTER);
            cuerpoDashboard.revalidate();
            return;
        }

        if (lblAviso != null) {
            lblAviso.setText(r.pendientes == 0
                    ? "" : r.pendientes + " por revisar");
        }

        datosDashboard = new DashboardDatos(r, ingresosMes, reservasMes,
                estadosReserva, solicitudes, ranking, alojamientos);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setOpaque(false);



        JPanel fila1 = new JPanel(new GridLayout(1, 4, 15, 0));
        fila1.setOpaque(false);
        fila1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        fila1.add(kpi("Ingresos aprobados", dinero(r.ingresos),
                "Pagos aprobados históricos", DINERO, VERDE));
        fila1.add(kpi("Ticket promedio aprobado", dinero(r.ticket),
                "Monto medio realmente pagado", TENDENCIA, MORADO));
        fila1.add(kpi("Reservas totales", entero(r.reservas),
                r.reservasFinalizadas + " finalizadas",
                RESERVA, NARANJA));
        fila1.add(kpi("Pagos por verificar",
                entero(r.pagosPendientes),
                "Transferencias sin revisar",
                DINERO, r.pagosPendientes > 0 ? ROJO : GRIS_TEXTO));

        JPanel fila2 = new JPanel(new GridLayout(1, 4, 15, 0));
        fila2.setOpaque(false);
        fila2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        fila2.add(kpi("Cuidadores verificados",
                entero(r.cuidadores),
                porcentaje(r.tasaAprobacion) + " de aprobación",
                PERSONA, AZUL));
        fila2.add(kpi("Alojamientos activos",
                entero(r.alojamientos),
                "Publicados en la plataforma", CASA, CELESTE));
        fila2.add(kpi("Usuarios registrados", entero(r.usuarios),
                r.mascotas + " mascotas registradas",
                USUARIOS, AZUL_GRAD_1));
        fila2.add(kpi("Solicitudes pendientes",
                entero(r.pendientes), "Cuidadores por revisar",
                BANDEJA, r.pendientes > 0 ? ROJO : GRIS_TEXTO));



        JPanel fila3 = new JPanel(new GridLayout(1, 2, 15, 0));
        fila3.setOpaque(false);
        fila3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));
        fila3.setPreferredSize(new Dimension(ANCHO_CONTENIDO_MINIMO, 350));

        fila3.add(caja("Evolución de ingresos",
                "Pagos aprobados por mes · haga clic en un punto",
                new GraficoArea(ingresosMes, VERDE, true)));

        fila3.add(caja("Demanda de reservas",
                "Solicitudes por mes · haga clic en un punto",
                new GraficoArea(reservasMes, AZUL, false)));

        JPanel fila4 = new JPanel(new GridLayout(1, 2, 15, 0));
        fila4.setOpaque(false);
        fila4.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        fila4.setPreferredSize(new Dimension(ANCHO_CONTENIDO_MINIMO, 380));

        fila4.add(caja("Estado de las reservas",
                "Distribución real · haga clic en un segmento",
                new GraficoDona(estadosReserva)));

        fila4.add(caja("Estado de las solicitudes",
                "Estados reales · haga clic en un segmento",
                new GraficoDona(solicitudes)));

        JPanel fila5 = new JPanel(new GridLayout(1, 2, 15, 0));
        fila5.setOpaque(false);
        fila5.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        fila5.add(tabla("Mejores cuidadores",
                "Ingresos aprobados y reseñas reales",
                new String[]{"#", "ID Cuidador", "Cuidador", "Calificación",
                        "Reseñas", "Reservas", "Ingresos"},
                ranking));

        fila5.add(tabla("Alojamientos más solicitados",
                "Reservas únicas e ingresos aprobados",
                new String[]{"#", "Alojamiento", "Ciudad",
                        "Reservas", "Ingresos"},
                alojamientos));

        columna.add(fila1);
        columna.add(Box.createVerticalStrut(15));
        columna.add(fila2);
        columna.add(Box.createVerticalStrut(20));
        columna.add(fila3);
        columna.add(Box.createVerticalStrut(15));
        columna.add(fila4);
        columna.add(Box.createVerticalStrut(15));
        columna.add(fila5);
        columna.add(Box.createVerticalStrut(10));

        cuerpoDashboard.add(desplazable(columna),
                BorderLayout.CENTER);
        cuerpoDashboard.revalidate();
        cuerpoDashboard.repaint();
    }

    private void exportarDashboard() {
        try {
            DashboardDatos datos = new DashboardDatos(
                    consultas.resumen(),
                    consultas.ingresosPorMes(12),
                    consultas.reservasPorMes(12),
                    consultas.reservasPorEstado(),
                    consultas.solicitudesPorEstado(),
                    consultas.rankingCuidadores(6),
                    consultas.rankingAlojamientos(6));
            datosDashboard = datos;
            guardarDashboardPdf(datos,
                    "Dashboard_PetHome_" + LocalDate.now() + ".pdf");
        } catch (RuntimeException e) {
            avisoReporte("No se pudieron consultar los datos del dashboard: "
                    + e.getMessage());
        }
    }





    private void construirReportes() {
        cuerpoReportes.removeAll();

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font(FUENTE, Font.BOLD, 13));
        pestanas.setBackground(Color.WHITE);
        pestanas.addTab("Reporte mensual", crearReporteMensual());
        pestanas.addTab("Reporte general", crearReporteGeneral());

        cuerpoReportes.add(pestanas, BorderLayout.CENTER);
        cuerpoReportes.revalidate();
        cuerpoReportes.repaint();
    }

    private JPanel crearReporteMensual() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 10, 10, 10));

        JComboBox<MesOpcion> cmbMes = new JComboBox<>();
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril",
                "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
                "Octubre", "Noviembre", "Diciembre"};
        for (int i = 0; i < meses.length; i++) {
            cmbMes.addItem(new MesOpcion(i + 1, meses[i]));
        }
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        JComboBox<Integer> cmbAnio = new JComboBox<>();
        try {
            for (Integer anio : consultas.aniosDisponibles()) {
                cmbAnio.addItem(anio);
            }
        } catch (RuntimeException e) {
            cmbAnio.addItem(LocalDate.now().getYear());
        }
        cmbAnio.setSelectedItem(LocalDate.now().getYear());
        if (cmbAnio.getSelectedItem() == null && cmbAnio.getItemCount() > 0) {
            cmbAnio.setSelectedIndex(cmbAnio.getItemCount() - 1);
        }

        JButton btnGenerar = botonPrimario("Generar reporte");
        JButton btnPdf = botonBlanco("Guardar como PDF", REPORTE,
                e -> {
                });
        btnPdf.setEnabled(false);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT,
                12, 10));
        controles.setOpaque(false);
        controles.add(campoFiltro("Mes", cmbMes, 170));
        controles.add(campoFiltro("Año", cmbAnio, 110));
        controles.add(btnGenerar);
        controles.add(btnPdf);

        PanelCaja barra = new PanelCaja(14, Color.WHITE, BORDE_CAMPO);
        barra.setLayout(new BorderLayout());
        barra.setBorder(new EmptyBorder(4, 10, 4, 10));
        barra.add(controles, BorderLayout.WEST);

        DefaultTableModel modelo = modeloReporte();
        JTable tabla = tablaReporte(modelo);
        JScrollPane scroll = scrollReporte(tabla);
        JPanel resumen = new JPanel();
        resumen.setOpaque(false);
        JLabel lblFilas = new JLabel("Selecciona un mes y genera el reporte");
        lblFilas.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblFilas.setForeground(GRIS_TEXTO);

        JPanel resultados = new JPanel(new BorderLayout(0, 12));
        resultados.setOpaque(false);
        resultados.add(resumen, BorderLayout.NORTH);
        resultados.add(scroll, BorderLayout.CENTER);
        resultados.add(lblFilas, BorderLayout.SOUTH);

        ReporteResultado[] actual = new ReporteResultado[1];

        Runnable generar = () -> {
            MesOpcion mes = (MesOpcion) cmbMes.getSelectedItem();
            Integer anio = (Integer) cmbAnio.getSelectedItem();
            if (mes == null || anio == null) {
                avisoReporte("Selecciona un mes y un año válidos.");
                return;
            }
            try {
                ReporteResultado resultado =
                        consultas.reporteMensual(anio, mes.numero);
                actual[0] = resultado;
                actualizarReporte(resultado, modelo, resumen, lblFilas);
                btnPdf.setEnabled(true);
            } catch (RuntimeException e) {
                avisoReporte(e.getMessage());
            }
        };

        btnGenerar.addActionListener(e -> generar.run());
        btnPdf.addActionListener(e -> {
            if (actual[0] != null) {
                MesOpcion mes = (MesOpcion) cmbMes.getSelectedItem();
                int anio = (Integer) cmbAnio.getSelectedItem();
                guardarPdf(actual[0], String.format(
                        "Reporte_Mensual_PetHome_%04d-%02d.pdf",
                        anio, mes.numero));
            }
        });

        panel.add(barra, BorderLayout.NORTH);
        panel.add(resultados, BorderLayout.CENTER);
        SwingUtilities.invokeLater(generar);
        return panel;
    }

    private JPanel crearReporteGeneral() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(16, 10, 10, 10));

        JTextField txtDesde = campoFecha();
        JTextField txtHasta = campoFecha();
        JComboBox<Opcion> cmbEspecie = new JComboBox<>();
        JComboBox<String> cmbCiudad = new JComboBox<>();
        JComboBox<String> cmbEstado = new JComboBox<>();
        JComboBox<Opcion> cmbCuidador = new JComboBox<>();
        JComboBox<Opcion> cmbAlojamiento = new JComboBox<>();

        cmbEspecie.addItem(new Opcion(null, "Todas las especies"));
        cmbCiudad.addItem("Todas las ciudades");
        cmbEstado.addItem("Todos los estados");
        cmbCuidador.addItem(new Opcion(null, "Todos los cuidadores"));
        cmbAlojamiento.addItem(new Opcion(null, "Todos los alojamientos"));

        for (String estado : new String[]{"SOLICITADA", "ACEPTADA",
                "RECHAZADA", "CANCELADA", "EN_CURSO", "FINALIZADA"}) {
            cmbEstado.addItem(estado);
        }

        try {
            for (Opcion opcion : consultas.especies()) {
                cmbEspecie.addItem(opcion);
            }
            for (String ciudad : consultas.ciudades()) {
                cmbCiudad.addItem(ciudad);
            }
            for (Opcion opcion : consultas.cuidadores()) {
                cmbCuidador.addItem(opcion);
            }
            for (Opcion opcion : consultas.alojamientos()) {
                cmbAlojamiento.addItem(opcion);
            }
        } catch (RuntimeException e) {
            avisoReporte(e.getMessage());
        }

        JPanel campos = new JPanel(new GridLayout(2, 4, 10, 8));
        campos.setOpaque(false);
        campos.add(campoFiltro("Desde (dd/MM/yyyy)", txtDesde, 145));
        campos.add(campoFiltro("Hasta (dd/MM/yyyy)", txtHasta, 145));
        campos.add(campoFiltro("Especie", cmbEspecie, 180));
        campos.add(campoFiltro("Ciudad", cmbCiudad, 180));
        campos.add(campoFiltro("Estado de reserva", cmbEstado, 180));
        campos.add(campoFiltro("Cuidador", cmbCuidador, 200));
        campos.add(campoFiltro("Alojamiento", cmbAlojamiento, 200));

        JButton btnAplicar = botonPrimario("Aplicar filtros");
        JButton btnLimpiar = botonBlanco("Limpiar filtros", ACTUALIZAR,
                e -> {
                });
        JButton btnPdf = botonBlanco("Guardar como PDF", REPORTE,
                e -> {
                });
        btnPdf.setEnabled(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT,
                10, 4));
        botones.setOpaque(false);
        botones.add(btnLimpiar);
        botones.add(btnAplicar);
        botones.add(btnPdf);

        PanelCaja filtros = new PanelCaja(14, Color.WHITE, BORDE_CAMPO);
        filtros.setLayout(new BorderLayout(10, 10));
        filtros.setBorder(new EmptyBorder(12, 14, 12, 14));
        filtros.add(campos, BorderLayout.CENTER);
        filtros.add(botones, BorderLayout.SOUTH);

        DefaultTableModel modelo = modeloReporte();
        JTable tabla = tablaReporte(modelo);
        JPanel resumen = new JPanel();
        resumen.setOpaque(false);
        JLabel lblFilas = new JLabel("Aplica los filtros para consultar");
        lblFilas.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblFilas.setForeground(GRIS_TEXTO);

        JPanel resultados = new JPanel(new BorderLayout(0, 12));
        resultados.setOpaque(false);
        resultados.add(resumen, BorderLayout.NORTH);
        resultados.add(scrollReporte(tabla), BorderLayout.CENTER);
        resultados.add(lblFilas, BorderLayout.SOUTH);

        ReporteResultado[] actual = new ReporteResultado[1];

        Runnable aplicar = () -> {
            try {
                LocalDate desde = fechaOpcional(txtDesde.getText());
                LocalDate hasta = fechaOpcional(txtHasta.getText());
                if (desde != null && hasta != null && desde.isAfter(hasta)) {
                    avisoReporte("La fecha Desde no puede ser posterior a Hasta.");
                    return;
                }

                Opcion especie = (Opcion) cmbEspecie.getSelectedItem();
                String ciudad = cmbCiudad.getSelectedIndex() <= 0
                        ? null : (String) cmbCiudad.getSelectedItem();
                String estado = cmbEstado.getSelectedIndex() <= 0
                        ? null : (String) cmbEstado.getSelectedItem();
                Opcion cuidador = (Opcion) cmbCuidador.getSelectedItem();
                Opcion alojamiento =
                        (Opcion) cmbAlojamiento.getSelectedItem();

                ReporteResultado resultado = consultas.reporteGeneral(
                        desde, hasta,
                        especie == null ? null : especie.id,
                        ciudad, estado,
                        cuidador == null ? null : cuidador.id,
                        alojamiento == null ? null : alojamiento.id,
                        especie == null ? "Todas las especies" : especie.nombre,
                        cuidador == null ? "Todos los cuidadores"
                                : cuidador.nombre,
                        alojamiento == null ? "Todos los alojamientos"
                                : alojamiento.nombre);
                actual[0] = resultado;
                actualizarReporte(resultado, modelo, resumen, lblFilas);
                btnPdf.setEnabled(true);
            } catch (DateTimeParseException e) {
                avisoReporte("Usa el formato dd/MM/yyyy en las fechas.");
            } catch (RuntimeException e) {
                avisoReporte(e.getMessage());
            }
        };

        btnAplicar.addActionListener(e -> aplicar.run());
        btnLimpiar.addActionListener(e -> {
            txtDesde.setText("");
            txtHasta.setText("");
            cmbEspecie.setSelectedIndex(0);
            cmbCiudad.setSelectedIndex(0);
            cmbEstado.setSelectedIndex(0);
            cmbCuidador.setSelectedIndex(0);
            cmbAlojamiento.setSelectedIndex(0);
            aplicar.run();
        });
        btnPdf.addActionListener(e -> {
            if (actual[0] != null) {
                guardarPdf(actual[0], "Reporte_General_PetHome_"
                        + LocalDate.now() + ".pdf");
            }
        });

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(resultados, BorderLayout.CENTER);
        SwingUtilities.invokeLater(aplicar);
        return panel;
    }

    private JPanel campoFiltro(String etiqueta, JComponent campo,
                               int ancho) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel titulo = new JLabel(etiqueta);
        titulo.setFont(new Font(FUENTE, Font.BOLD, 11));
        titulo.setForeground(GRIS_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setFont(new Font(FUENTE, Font.PLAIN, 12));
        campo.setPreferredSize(new Dimension(ancho, 34));
        campo.setMaximumSize(new Dimension(ancho, 34));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(campo);
        return panel;
    }

    private JTextField campoFecha() {
        JTextField campo = new JTextField();
        campo.setToolTipText("Deja vacío para no filtrar por fecha");
        return campo;
    }

    private JButton botonPrimario(String texto) {
        BotonRedondeado boton = new BotonRedondeado(texto,
                AZUL_GRAD_1, Color.WHITE, null, AZUL_GRAD_2);
        boton.setPreferredSize(new Dimension(165, 38));
        return boton;
    }

    private DefaultTableModel modeloReporte() {
        return new DefaultTableModel(ReporteResultado.COLUMNAS, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JTable tablaReporte(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getParent() instanceof JViewport
                        && getPreferredSize().width < getParent().getWidth();
            }
        };
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tabla.setRowHeight(34);
        tabla.setFont(new Font(FUENTE, Font.PLAIN, 11));
        tabla.setForeground(AZUL_OSCURO);
        tabla.setGridColor(GRIS_REJILLA);
        tabla.setSelectionBackground(new Color(226, 240, 252));
        tabla.setSelectionForeground(AZUL_OSCURO);
        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setFont(new Font(FUENTE, Font.BOLD, 11));
        cabecera.setBackground(FONDO_CAMPO);
        cabecera.setForeground(GRIS_TEXTO);
        cabecera.setReorderingAllowed(false);
        int[] anchos = {78, 110, 135, 100, 85, 145, 100, 145,
                95, 95, 65, 105, 90, 90, 90, 115, 105};
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }
        return tabla;
    }

    private JScrollPane scrollReporte(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE_CAMPO));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    private void actualizarReporte(ReporteResultado resultado,
                                   DefaultTableModel modelo,
                                   JPanel resumen, JLabel lblFilas) {
        modelo.setRowCount(0);
        for (Object[] fila : resultado.filas) {
            modelo.addRow(fila);
        }

        resumen.removeAll();
        int columnas = Math.min(5, Math.max(1, resultado.resumen.size()));
        resumen.setLayout(new GridLayout(0, columnas, 8, 8));
        for (Map.Entry<String, String> dato : resultado.resumen.entrySet()) {
            PanelCaja tarjeta = new PanelCaja(12, Color.WHITE, BORDE_CAMPO);
            tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
            tarjeta.setBorder(new EmptyBorder(9, 11, 9, 11));
            JLabel etiqueta = new JLabel(dato.getKey());
            etiqueta.setFont(new Font(FUENTE, Font.PLAIN, 10));
            etiqueta.setForeground(GRIS_TEXTO);
            JLabel valor = new JLabel(dato.getValue());
            valor.setFont(new Font(FUENTE, Font.BOLD, 15));
            valor.setForeground(AZUL_OSCURO);
            tarjeta.add(etiqueta);
            tarjeta.add(Box.createVerticalStrut(2));
            tarjeta.add(valor);
            resumen.add(tarjeta);
        }
        lblFilas.setText(resultado.filas.size()
                + (resultado.filas.size() == 1
                ? " reserva mostrada" : " reservas mostradas"));
        resumen.revalidate();
        resumen.repaint();
    }

    private LocalDate fechaOpcional(String texto) {
        String valor = texto == null ? "" : texto.trim();
        return valor.isEmpty() ? null : LocalDate.parse(valor,
                DateTimeFormatter.ofPattern("dd/MM/uuuu")
                        .withResolverStyle(java.time.format.ResolverStyle.STRICT));
    }

    private void guardarPdf(ReporteResultado reporte,
                            String nombreSugerido) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar reporte como PDF");
        selector.setSelectedFile(new File(nombreSugerido));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(),
                    archivo.getName() + ".pdf");
        }

        if (archivo.exists()) {
            int reemplazar = JOptionPane.showConfirmDialog(this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Confirmar reemplazo", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (reemplazar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            GeneradorPdf.generar(archivo, reporte.titulo,
                    reporte.detalles, reporte.resumen,
                    reporte.columnas, reporte.filas);
            JOptionPane.showMessageDialog(this,
                    "PDF guardado correctamente en:\n"
                            + archivo.getAbsolutePath(),
                    "Reporte exportado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            avisoReporte("No se pudo guardar el PDF: " + e.getMessage());
        }
    }

    private void guardarDashboardPdf(DashboardDatos datos,
                                     String nombreSugerido) {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Guardar dashboard completo como PDF");
        selector.setSelectedFile(new File(nombreSugerido));
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        if (!archivo.getName().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            archivo = new File(archivo.getParentFile(),
                    archivo.getName() + ".pdf");
        }

        if (archivo.exists()) {
            int reemplazar = JOptionPane.showConfirmDialog(this,
                    "El archivo ya existe. ¿Deseas reemplazarlo?",
                    "Confirmar reemplazo", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (reemplazar != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            escribirPdfDashboard(archivo, crearPaginasDashboard(datos));
            JOptionPane.showMessageDialog(this,
                    "Dashboard con gráficos guardado correctamente en:\n"
                            + archivo.getAbsolutePath(),
                    "Dashboard exportado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            avisoReporte("No se pudo guardar el dashboard: "
                    + e.getMessage());
        }
    }

    private List<BufferedImage> crearPaginasDashboard(DashboardDatos datos) {
        List<BufferedImage> paginas = new ArrayList<>();
        paginas.add(paginaResumenDashboard(datos));
        paginas.add(paginaGraficosDashboard(datos));
        paginas.add(paginaRankingsDashboard(datos));
        return paginas;
    }

    private BufferedImage paginaResumenDashboard(DashboardDatos datos) {
        BufferedImage pagina = nuevaPaginaPdf();
        Graphics2D g2 = prepararPaginaPdf(pagina);
        cabeceraPaginaPdf(g2, "Dashboard administrativo",
                "Resumen ejecutivo de Pet Home Boarding", datos, 1);

        Consultas.Resumen r = datos.resumen;
        String[][] indicadores = {
                {"Ingresos aprobados", dinero(r.ingresos),
                        "Pagos aprobados históricos"},
                {"Ticket promedio", dinero(r.ticket),
                        "Promedio realmente pagado"},
                {"Reservas totales", entero(r.reservas),
                        r.reservasFinalizadas + " finalizadas"},
                {"Pagos por verificar", entero(r.pagosPendientes),
                        "Transferencias pendientes"},
                {"Cuidadores verificados", entero(r.cuidadores),
                        porcentaje(r.tasaAprobacion) + " de aprobación"},
                {"Alojamientos activos", entero(r.alojamientos),
                        "Publicados en la plataforma"},
                {"Usuarios registrados", entero(r.usuarios),
                        r.mascotas + " mascotas registradas"},
                {"Solicitudes pendientes", entero(r.pendientes),
                        "Cuidadores por revisar"},
                {"Reseñas registradas", entero(r.resenas),
                        "Valoraciones de la comunidad"},
                {"Calificación promedio", String.format("%.2f", r.calificacion),
                        "Promedio de reseñas reales"}
        };

        int margen = 64;
        int separacion = 18;
        int ancho = (1684 - margen * 2 - separacion * 4) / 5;
        int alto = 190;
        for (int i = 0; i < indicadores.length; i++) {
            int columna = i % 5;
            int fila = i / 5;
            int x = margen + columna * (ancho + separacion);
            int y = 158 + fila * (alto + 20);
            dibujarKpiPdf(g2, x, y, ancho, alto,
                    indicadores[i][0], indicadores[i][1],
                    indicadores[i][2], SERIE[i % SERIE.length]);
        }

        int yPanel = 600;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(margen, yPanel, 1684 - margen * 2, 430, 24, 24);
        g2.setColor(BORDE_CAMPO);
        g2.drawRoundRect(margen, yPanel, 1684 - margen * 2, 430, 24, 24);

        g2.setColor(AZUL_OSCURO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 26));
        g2.drawString("Lectura operativa", margen + 32, yPanel + 54);
        g2.setColor(GRIS_TEXTO);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 17));
        g2.drawString("Indicadores calculados directamente con los registros actuales de PostgreSQL.",
                margen + 32, yPanel + 84);

        double finalizadas = r.reservas == 0 ? 0
                : r.reservasFinalizadas * 100.0 / r.reservas;
        dibujarIndicadorHorizontalPdf(g2, margen + 32, yPanel + 130,
                700, "Reservas finalizadas", finalizadas, CELESTE);
        dibujarIndicadorHorizontalPdf(g2, margen + 32, yPanel + 235,
                700, "Solicitudes de cuidadores aprobadas",
                r.tasaAprobacion, VERDE);

        int xDetalle = margen + 820;
        g2.setColor(FONDO_CAMPO);
        g2.fillRoundRect(xDetalle, yPanel + 118, 680, 238, 18, 18);
        g2.setColor(AZUL_OSCURO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 19));
        g2.drawString("Trazabilidad del reporte", xDetalle + 28,
                yPanel + 160);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 16));
        g2.setColor(GRIS_TEXTO);
        g2.drawString("• Consultas ejecutadas al momento de exportar",
                xDetalle + 28, yPanel + 202);
        g2.drawString("• Importes: únicamente pagos APROBADOS",
                xDetalle + 28, yPanel + 238);
        g2.drawString("• Rankings: reservas, reseñas e ingresos reales",
                xDetalle + 28, yPanel + 274);
        g2.drawString("• Sin valores simulados ni datos precargados",
                xDetalle + 28, yPanel + 310);

        piePaginaPdf(g2, datos, 1);
        g2.dispose();
        return pagina;
    }

    private BufferedImage paginaGraficosDashboard(DashboardDatos datos) {
        BufferedImage pagina = nuevaPaginaPdf();
        Graphics2D g2 = prepararPaginaPdf(pagina);
        cabeceraPaginaPdf(g2, "Gráficos del dashboard",
                "Tendencias y distribuciones construidas con datos reales",
                datos, 2);

        int margen = 64;
        int separacion = 20;
        int ancho = (1684 - margen * 2 - separacion) / 2;
        int alto = 450;

        dibujarCajaGraficoPdf(g2, margen, 145, ancho, alto,
                "Evolución de ingresos",
                "Pagos aprobados por mes calendario",
                new GraficoArea(datos.ingresosMes, VERDE, true));
        dibujarCajaGraficoPdf(g2, margen + ancho + separacion, 145,
                ancho, alto, "Demanda de reservas",
                "Solicitudes por mes calendario",
                new GraficoArea(datos.reservasMes, AZUL, false));
        dibujarCajaGraficoPdf(g2, margen, 615, ancho, alto,
                "Estado de las reservas",
                "Distribución de todas las reservas registradas",
                new GraficoDona(datos.estadosReserva));
        dibujarCajaGraficoPdf(g2, margen + ancho + separacion, 615,
                ancho, alto, "Estado de las solicitudes",
                "Cuidadores verificados, pendientes y rechazados",
                new GraficoDona(datos.solicitudes));

        piePaginaPdf(g2, datos, 2);
        g2.dispose();
        return pagina;
    }

    private BufferedImage paginaRankingsDashboard(DashboardDatos datos) {
        BufferedImage pagina = nuevaPaginaPdf();
        Graphics2D g2 = prepararPaginaPdf(pagina);
        cabeceraPaginaPdf(g2, "Rankings operativos",
                "Resultados ordenados a partir de actividad registrada",
                datos, 3);

        dibujarTablaPdf(g2, 64, 150, 1556, 420,
                "Mejores cuidadores",
                "Incluye el ID real del cuidador para su verificación",
                new String[]{"#", "ID Cuidador", "Cuidador", "Calificación",
                        "Reseñas", "Reservas", "Ingresos"},
                datos.rankingCuidadores,
                new double[]{0.06, 0.11, 0.25, 0.15, 0.11, 0.13, 0.19});

        dibujarTablaPdf(g2, 64, 605, 1556, 420,
                "Alojamientos más solicitados",
                "Reservas únicas e ingresos aprobados por alojamiento",
                new String[]{"#", "Alojamiento", "Ciudad", "Reservas",
                        "Ingresos"}, datos.rankingAlojamientos,
                new double[]{0.08, 0.34, 0.25, 0.14, 0.19});

        piePaginaPdf(g2, datos, 3);
        g2.dispose();
        return pagina;
    }

    private BufferedImage nuevaPaginaPdf() {
        return new BufferedImage(1684, 1190, BufferedImage.TYPE_INT_RGB);
    }

    private Graphics2D prepararPaginaPdf(BufferedImage pagina) {
        Graphics2D g2 = pagina.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(FONDO);
        g2.fillRect(0, 0, pagina.getWidth(), pagina.getHeight());
        return g2;
    }

    private void cabeceraPaginaPdf(Graphics2D g2, String titulo,
                                   String subtitulo, DashboardDatos datos,
                                   int pagina) {
        g2.setPaint(new GradientPaint(0, 0, AZUL_OSCURO,
                1684, 0, AZUL_GRAD_1));
        g2.fillRect(0, 0, 1684, 112);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(FUENTE, Font.BOLD, 31));
        g2.drawString(titulo, 64, 52);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 16));
        g2.setColor(new Color(222, 238, 252));
        g2.drawString(subtitulo, 64, 82);

        String generado = datos.generado.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        g2.setFont(new Font(FUENTE, Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        String derecha = "Generado: " + generado + "  ·  Página " + pagina;
        g2.drawString(derecha, 1620 - fm.stringWidth(derecha), 68);
    }

    private void piePaginaPdf(Graphics2D g2, DashboardDatos datos,
                              int pagina) {
        g2.setColor(BORDE_CAMPO);
        g2.drawLine(64, 1128, 1620, 1128);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 13));
        g2.setColor(GRIS_TEXTO);
        g2.drawString("Pet Home Boarding · Panel administrativo · Datos consultados en PostgreSQL",
                64, 1158);
        String numero = pagina + " / 3";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(numero, 1620 - fm.stringWidth(numero), 1158);
    }

    private void dibujarKpiPdf(Graphics2D g2, int x, int y,
                               int ancho, int alto, String titulo,
                               String valor, String nota, Color acento) {
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(BORDE_CAMPO);
        g2.drawRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(acento);
        g2.fillRoundRect(x, y, 9, alto, 22, 22);
        g2.fillRect(x + 5, y, 5, alto);
        g2.setColor(GRIS_TEXTO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 15));
        g2.drawString(textoRecortadoPdf(titulo, g2.getFontMetrics(),
                ancho - 42), x + 26, y + 43);
        g2.setColor(AZUL_OSCURO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 31));
        g2.drawString(textoRecortadoPdf(valor, g2.getFontMetrics(),
                ancho - 42), x + 26, y + 99);
        g2.setColor(GRIS_PISTA);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 13));
        g2.drawString(textoRecortadoPdf(nota, g2.getFontMetrics(),
                ancho - 42), x + 26, y + 145);
    }

    private void dibujarIndicadorHorizontalPdf(Graphics2D g2, int x,
                                               int y, int ancho,
                                               String titulo, double valor,
                                               Color color) {
        double limitado = Math.max(0, Math.min(100, valor));
        g2.setFont(new Font(FUENTE, Font.BOLD, 17));
        g2.setColor(AZUL_OSCURO);
        g2.drawString(titulo, x, y);
        String porcentaje = String.format("%.1f%%", limitado);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(porcentaje, x + ancho - fm.stringWidth(porcentaje), y);
        g2.setColor(GRIS_REJILLA);
        g2.fillRoundRect(x, y + 22, ancho, 22, 11, 11);
        g2.setColor(color);
        g2.fillRoundRect(x, y + 22,
                Math.max(8, (int) (ancho * limitado / 100.0)),
                22, 11, 11);
    }

    private void dibujarCajaGraficoPdf(Graphics2D g2, int x, int y,
                                       int ancho, int alto, String titulo,
                                       String nota, JComponent grafico) {
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(BORDE_CAMPO);
        g2.drawRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(AZUL_OSCURO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 21));
        g2.drawString(titulo, x + 24, y + 38);
        g2.setColor(GRIS_TEXTO);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 14));
        g2.drawString(nota, x + 24, y + 64);

        int anchoGrafico = ancho - 48;
        int altoGrafico = alto - 92;
        grafico.setSize(anchoGrafico, altoGrafico);
        Graphics2D graficoPdf = (Graphics2D) g2.create(
                x + 24, y + 78, anchoGrafico, altoGrafico);
        grafico.printAll(graficoPdf);
        graficoPdf.dispose();
    }

    private void dibujarTablaPdf(Graphics2D g2, int x, int y,
                                 int ancho, int alto, String titulo,
                                 String nota, String[] columnas,
                                 List<Object[]> filas, double[] pesos) {
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(BORDE_CAMPO);
        g2.drawRoundRect(x, y, ancho, alto, 22, 22);
        g2.setColor(AZUL_OSCURO);
        g2.setFont(new Font(FUENTE, Font.BOLD, 21));
        g2.drawString(titulo, x + 24, y + 38);
        g2.setColor(GRIS_TEXTO);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 14));
        g2.drawString(nota, x + 24, y + 64);

        int inicioX = x + 24;
        int inicioY = y + 84;
        int anchoTabla = ancho - 48;
        int altoFila = 44;
        g2.setColor(FONDO_CAMPO);
        g2.fillRoundRect(inicioX, inicioY, anchoTabla, altoFila, 10, 10);

        int[] anchos = new int[columnas.length];
        int acumulado = 0;
        for (int i = 0; i < columnas.length; i++) {
            anchos[i] = i == columnas.length - 1
                    ? anchoTabla - acumulado
                    : (int) Math.round(anchoTabla * pesos[i]);
            acumulado += anchos[i];
        }

        g2.setFont(new Font(FUENTE, Font.BOLD, 14));
        g2.setColor(GRIS_TEXTO);
        int cursorX = inicioX;
        for (int i = 0; i < columnas.length; i++) {
            g2.drawString(textoRecortadoPdf(columnas[i],
                            g2.getFontMetrics(), anchos[i] - 20),
                    cursorX + 10, inicioY + 28);
            cursorX += anchos[i];
        }

        int maximoFilas = Math.min(filas.size(),
                Math.max(0, (alto - 145) / altoFila));
        g2.setFont(new Font(FUENTE, Font.PLAIN, 14));
        for (int fila = 0; fila < maximoFilas; fila++) {
            int filaY = inicioY + altoFila * (fila + 1);
            g2.setColor(fila % 2 == 0 ? Color.WHITE : FONDO_CAMPO);
            g2.fillRect(inicioX, filaY, anchoTabla, altoFila);
            g2.setColor(AZUL_OSCURO);
            cursorX = inicioX;
            Object[] valores = filas.get(fila);
            for (int columna = 0; columna < columnas.length; columna++) {
                String valor = columna < valores.length
                        && valores[columna] != null
                        ? String.valueOf(valores[columna]) : "-";
                g2.drawString(textoRecortadoPdf(valor,
                                g2.getFontMetrics(), anchos[columna] - 20),
                        cursorX + 10, filaY + 28);
                cursorX += anchos[columna];
            }
            g2.setColor(GRIS_REJILLA);
            g2.drawLine(inicioX, filaY + altoFila - 1,
                    inicioX + anchoTabla, filaY + altoFila - 1);
        }

        if (filas.isEmpty()) {
            g2.setColor(GRIS_PISTA);
            g2.setFont(new Font(FUENTE, Font.ITALIC, 15));
            g2.drawString("No hay registros disponibles.",
                    inicioX + 10, inicioY + altoFila + 32);
        }
    }

    private String textoRecortadoPdf(String texto, FontMetrics fm,
                                     int anchoMaximo) {
        if (texto == null || fm.stringWidth(texto) <= anchoMaximo) {
            return texto == null ? "" : texto;
        }
        String puntos = "…";
        int limite = texto.length();
        while (limite > 1 && fm.stringWidth(
                texto.substring(0, limite) + puntos) > anchoMaximo) {
            limite--;
        }
        return texto.substring(0, limite) + puntos;
    }

    private void escribirPdfDashboard(File archivo,
                                      List<BufferedImage> paginas)
            throws IOException {
        List<byte[]> imagenes = new ArrayList<>();
        for (BufferedImage pagina : paginas) {
            ByteArrayOutputStream imagen = new ByteArrayOutputStream();
            if (!ImageIO.write(pagina, "jpg", imagen)) {
                throw new IOException("No hay un codificador JPEG disponible.");
            }
            imagenes.add(imagen.toByteArray());
        }

        int cantidadObjetos = 2 + imagenes.size() * 3;
        byte[][] objetos = new byte[cantidadObjetos + 1][];
        objetos[1] = bytesPdf("<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < imagenes.size(); i++) {
            hijos.append(3 + i * 3).append(" 0 R ");
        }
        objetos[2] = bytesPdf("<< /Type /Pages /Kids ["
                + hijos + "] /Count " + imagenes.size() + " >>");

        for (int i = 0; i < imagenes.size(); i++) {
            int objetoPagina = 3 + i * 3;
            int objetoImagen = objetoPagina + 1;
            int objetoContenido = objetoPagina + 2;
            String nombreImagen = "Img" + (i + 1);

            objetos[objetoPagina] = bytesPdf("<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 842 595] /Resources << /XObject << /"
                    + nombreImagen + " " + objetoImagen
                    + " 0 R >> >> /Contents " + objetoContenido + " 0 R >>");

            byte[] jpeg = imagenes.get(i);
            ByteArrayOutputStream objetoJpeg = new ByteArrayOutputStream();
            objetoJpeg.write(bytesPdf("<< /Type /XObject /Subtype /Image "
                    + "/Width 1684 /Height 1190 /ColorSpace /DeviceRGB "
                    + "/BitsPerComponent 8 /Filter /DCTDecode /Length "
                    + jpeg.length + " >>\nstream\n"));
            objetoJpeg.write(jpeg);
            objetoJpeg.write(bytesPdf("\nendstream"));
            objetos[objetoImagen] = objetoJpeg.toByteArray();

            byte[] contenido = bytesPdf("q\n842 0 0 595 0 0 cm\n/"
                    + nombreImagen + " Do\nQ\n");
            ByteArrayOutputStream objetoFlujo = new ByteArrayOutputStream();
            objetoFlujo.write(bytesPdf("<< /Length " + contenido.length
                    + " >>\nstream\n"));
            objetoFlujo.write(contenido);
            objetoFlujo.write(bytesPdf("endstream"));
            objetos[objetoContenido] = objetoFlujo.toByteArray();
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write(bytesPdf("%PDF-1.4\n%âãÏÓ\n"));
        long[] posiciones = new long[cantidadObjetos + 1];
        for (int i = 1; i <= cantidadObjetos; i++) {
            posiciones[i] = pdf.size();
            pdf.write(bytesPdf(i + " 0 obj\n"));
            pdf.write(objetos[i]);
            pdf.write(bytesPdf("\nendobj\n"));
        }

        long inicioXref = pdf.size();
        pdf.write(bytesPdf("xref\n0 " + (cantidadObjetos + 1)
                + "\n0000000000 65535 f \n"));
        for (int i = 1; i <= cantidadObjetos; i++) {
            pdf.write(bytesPdf(String.format(Locale.US,
                    "%010d 00000 n \n", posiciones[i])));
        }
        pdf.write(bytesPdf("trailer\n<< /Size " + (cantidadObjetos + 1)
                + " /Root 1 0 R >>\nstartxref\n" + inicioXref
                + "\n%%EOF\n"));

        try (OutputStream salida = new BufferedOutputStream(
                new FileOutputStream(archivo))) {
            pdf.writeTo(salida);
        }
    }

    private byte[] bytesPdf(String texto) {
        return texto.getBytes(StandardCharsets.ISO_8859_1);
    }

    private void avisoReporte(String mensaje) {
        JOptionPane.showMessageDialog(this,
                texto(mensaje, "No se pudo completar la operación."),
                "Reportes", JOptionPane.WARNING_MESSAGE);
    }






    private JPanel kpi(String titulo, String valor, String nota,
                       int tipoIcono, Color color) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(14, 0));
        caja.setBorder(new EmptyBorder(18, 18, 18, 18));

        PanelCaja marco = new PanelCaja(13, aclarar(color, 0.86f), null);
        marco.setLayout(new GridBagLayout());
        marco.setPreferredSize(new Dimension(48, 48));
        marco.add(new JLabel(icono(tipoIcono, color, 23)));

        JPanel columnaIcono = new JPanel(new BorderLayout());
        columnaIcono.setOpaque(false);
        columnaIcono.add(marco, BorderLayout.NORTH);

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblTitulo.setForeground(GRIS_TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font(FUENTE, Font.BOLD, 26));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel("<html>" + nota + "</html>");
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblNota.setForeground(GRIS_PISTA);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(lblValor);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblNota);

        caja.add(textos, BorderLayout.CENTER);
        caja.add(columnaIcono, BorderLayout.EAST);

        return caja;
    }


    private JPanel caja(String titulo, String nota,
                        JComponent grafico) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(0, 10));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(
                new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );
        encabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel(nota);
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblNota.setForeground(GRIS_PISTA);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(2));
        encabezado.add(lblNota);

        caja.add(encabezado, BorderLayout.NORTH);
        caja.add(grafico, BorderLayout.CENTER);

        return caja;
    }


    private JPanel tabla(String titulo, String nota,
                         String[] columnas, List<Object[]> filas) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(0, 12));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(
                new BoxLayout(encabezado, BoxLayout.Y_AXIS)
        );
        encabezado.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNota = new JLabel(nota);
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 11));
        lblNota.setForeground(GRIS_PISTA);
        lblNota.setAlignmentX(Component.LEFT_ALIGNMENT);

        encabezado.add(lblTitulo);
        encabezado.add(Box.createVerticalStrut(2));
        encabezado.add(lblNota);

        caja.add(encabezado, BorderLayout.NORTH);
        caja.add(construirTabla(columnas,
                        filas.toArray(new Object[0][]), false),
                BorderLayout.CENTER);

        return caja;
    }

    private JScrollPane construirTabla(String[] columnas,
                                       Object[][] filas) {
        return construirTabla(columnas, filas, true);
    }

    private JScrollPane construirTabla(String[] columnas,
                                       Object[][] filas,
                                       boolean tablaPrincipal) {

        DefaultTableModel modelo =
                new DefaultTableModel(filas, columnas) {
                    @Override
                    public boolean isCellEditable(int f, int c) {
                        return false;
                    }
                };

        JTable tabla = new JTable(modelo);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setRowHeight(36);
        tabla.setFont(new Font(FUENTE, Font.PLAIN, 12));
        tabla.setForeground(AZUL_OSCURO);
        tabla.setGridColor(GRIS_REJILLA);
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(new Color(226, 240, 252));
        tabla.setSelectionForeground(AZUL_OSCURO);
        tabla.setFillsViewportHeight(true);

        JTableHeader cabecera = tabla.getTableHeader();
        cabecera.setFont(new Font(FUENTE, Font.BOLD, 12));
        cabecera.setBackground(FONDO_CAMPO);
        cabecera.setForeground(GRIS_TEXTO);
        cabecera.setPreferredSize(new Dimension(0, 36));
        cabecera.setReorderingAllowed(false);


        DefaultTableCellRenderer pintor =
                new DefaultTableCellRenderer() {
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

                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        return c;
                    }
                };

        int[] anchos = new int[tabla.getColumnCount()];
        int anchoTotal = 0;
        FontMetrics fmCeldas = tabla.getFontMetrics(tabla.getFont());
        FontMetrics fmCabecera = cabecera.getFontMetrics(cabecera.getFont());

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i)
                    .setCellRenderer(pintor);
            int ancho = fmCabecera.stringWidth(columnas[i]) + 34;
            for (int fila = 0; fila < tabla.getRowCount(); fila++) {
                Object valor = tabla.getValueAt(fila, i);
                if (valor != null) {
                    ancho = Math.max(ancho,
                            fmCeldas.stringWidth(String.valueOf(valor)) + 34);
                }
            }
            anchos[i] = tablaPrincipal
                    ? Math.min(340, Math.max(105, ancho))
                    : Math.min(190, Math.max(70, ancho));
            anchoTotal += anchos[i];
        }

        int anchoObjetivo = tablaPrincipal ? ANCHO_TABLA_MINIMO : 620;
        if (anchos.length > 0 && anchoTotal < anchoObjetivo) {
            int extra = (anchoObjetivo - anchoTotal) / anchos.length;
            for (int i = 0; i < anchos.length; i++) {
                anchos[i] += extra;
            }
        }

        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(
                BorderFactory.createLineBorder(BORDE_CAMPO)
        );
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private JScrollPane desplazable(JPanel columna) {

        JPanel envoltorio = new LienzoAdaptable(ANCHO_CONTENIDO_MINIMO - 70);
        envoltorio.setLayout(new BorderLayout());
        envoltorio.setOpaque(false);
        envoltorio.add(columna, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(envoltorio);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.getHorizontalScrollBar().setUnitIncrement(20);

        return scroll;
    }

    private JPanel error(String mensaje) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new GridBagLayout());

        JLabel lbl = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "No se pudieron cargar los datos.<br>"
                        + escapar(texto(mensaje, "Revise la conexión."))
                        + "</div></html>"
        );
        lbl.setFont(new Font(FUENTE, Font.BOLD, 14));
        lbl.setForeground(ROJO);
        caja.add(lbl);

        return caja;
    }





    private JPanel crearSeccionTabla(String titulo, String nota,
                                     String sql) {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);

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

        JLabel lblTotal = new JLabel();
        lblTotal.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblTotal.setForeground(GRIS_TEXTO);

        JPanel derecha =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 6));
        derecha.setOpaque(false);
        derecha.add(lblTotal);

        Runnable cargar = () -> {

            cuerpo.removeAll();

            try {
                Consultas.Tabla datos = consultas.consultar(sql);

                lblTotal.setText(datos.filas.size()
                        + " registros");

                cuerpo.add(construirTabla(datos.columnas,
                                datos.filas.toArray(new Object[0][])),
                        BorderLayout.CENTER);

            } catch (RuntimeException e) {
                cuerpo.add(error(e.getMessage()),
                        BorderLayout.CENTER);
            }

            cuerpo.revalidate();
            cuerpo.repaint();
        };

        derecha.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> cargar.run()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(cuerpo, BorderLayout.CENTER);

        SwingUtilities.invokeLater(cargar);

        return fondo;
    }





    private JPanel crearSeccionPagos() {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Pagos por verificar");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblNota = new JLabel(
                "Revise la transferencia en la banca de Pichincha "
                        + "antes de aprobar"
        );
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNota.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblNota);

        lblContadorPagos = new JLabel();
        lblContadorPagos.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblContadorPagos.setForeground(GRIS_TEXTO);

        JPanel derecha =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 6));
        derecha.setOpaque(false);
        derecha.add(lblContadorPagos);
        derecha.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> cargarPagosPendientes()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(derecha, BorderLayout.EAST);

        listaPagos = new JPanel();
        listaPagos.setLayout(
                new BoxLayout(listaPagos, BoxLayout.Y_AXIS));
        listaPagos.setOpaque(false);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(desplazable(listaPagos), BorderLayout.CENTER);

        return fondo;
    }

    private void cargarPagosPendientes() {

        if (listaPagos == null) {
            return;
        }

        listaPagos.removeAll();

        try {
            List<Object[]> lista = consultas.pagosPendientes();

            lblContadorPagos.setText(lista.size()
                    + (lista.size() == 1 ? " pago" : " pagos"));

            if (lista.isEmpty()) {
                listaPagos.add(vacio(
                        "No hay transferencias por verificar."));
            } else {
                for (Object[] p : lista) {
                    listaPagos.add(tarjetaPago(p));
                    listaPagos.add(Box.createVerticalStrut(12));
                }
            }

        } catch (RuntimeException e) {
            listaPagos.add(error(e.getMessage()));
        }

        listaPagos.revalidate();
        listaPagos.repaint();
    }

    private JPanel tarjetaPago(Object[] p) {

        int idPago = (int) p[0];
        int idReserva = (int) p[1];
        String cliente = (String) p[2];
        String alojamiento = (String) p[3];
        double monto = (double) p[4];
        String forma = (String) p[5];
        String comprobante = (String) p[6];
        String fecha = (String) p[7];

        PanelCaja caja = new PanelCaja(14, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(16, 0));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));
        caja.setPreferredSize(new Dimension(1080, 176));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 176));

        JPanel columnaAvatar = new JPanel(new GridBagLayout());
        columnaAvatar.setOpaque(false);
        columnaAvatar.setPreferredSize(new Dimension(60, 0));
        columnaAvatar.add(new Avatar(52, iniciales(cliente)));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel filaNombre =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaNombre.setOpaque(false);
        filaNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaNombre.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblNombre = new JLabel(cliente
                + "      ·      Reserva #" + idReserva);
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblNombre.setForeground(AZUL_OSCURO);

        filaNombre.add(lblNombre);
        filaNombre.add(pastilla("PENDIENTE"));

        JLabel lblAlojamiento = new JLabel(alojamiento
                + "      ·      Pago #" + idPago
                + "      ·      " + texto(fecha, "Sin fecha"));
        lblAlojamiento.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblAlojamiento.setForeground(GRIS_TEXTO);
        lblAlojamiento.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMonto = new JLabel("Monto: " + dinero(monto)
                + "      ·      Forma: " + forma
                + "      ·      Referencia: "
                + texto(comprobante, "Sin referencia"));
        lblMonto.setFont(new Font(FUENTE, Font.BOLD, 13));
        lblMonto.setForeground(AZUL);
        lblMonto.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(filaNombre);
        textos.add(Box.createVerticalStrut(7));
        textos.add(lblAlojamiento);
        textos.add(Box.createVerticalStrut(8));
        textos.add(lblMonto);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));

        BotonRedondeado btnAprobar = new BotonRedondeado(
                "Aprobar", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        btnAprobar.setIcon(icono(VISTO, Color.WHITE, 15));
        btnAprobar.setIconTextGap(8);
        btnAprobar.setPreferredSize(new Dimension(180, 44));
        btnAprobar.setMaximumSize(new Dimension(180, 44));
        btnAprobar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAprobar.addActionListener(e -> resolverPago(idPago, true));

        BotonRedondeado btnRechazar = new BotonRedondeado(
                "Rechazar", Color.WHITE, ROJO, ROJO, null);
        btnRechazar.setIcon(icono(EQUIS, ROJO, 14));
        btnRechazar.setIconTextGap(8);
        btnRechazar.setPreferredSize(new Dimension(180, 44));
        btnRechazar.setMaximumSize(new Dimension(180, 44));
        btnRechazar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRechazar.addActionListener(e -> resolverPago(idPago, false));

        acciones.add(btnAprobar);
        acciones.add(Box.createVerticalStrut(10));
        acciones.add(btnRechazar);

        JPanel columnaBoton = new JPanel(new BorderLayout());
        columnaBoton.setOpaque(false);
        columnaBoton.setPreferredSize(new Dimension(196, 0));
        columnaBoton.add(acciones, BorderLayout.NORTH);

        caja.add(columnaAvatar, BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);
        caja.add(columnaBoton, BorderLayout.EAST);

        return caja;
    }

    private void resolverPago(int idPago, boolean aprobar) {

        if (aprobar) {

            int r = JOptionPane.showConfirmDialog(this,
                    "¿Confirma que encontró esta transferencia "
                            + "en la cuenta bancaria?",
                    "Aprobar pago",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (r != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                consultas.aprobarPago(idPago);

                JOptionPane.showMessageDialog(this,
                        "Pago aprobado correctamente.",
                        "Pago aprobado",
                        JOptionPane.INFORMATION_MESSAGE);

                cargarPagosPendientes();

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Error al aprobar el pago",
                        JOptionPane.ERROR_MESSAGE);
            }

            return;
        }

        String motivo = JOptionPane.showInputDialog(this,
                "Motivo del rechazo:",
                "No se encontró la transferencia.");

        if (motivo == null) {
            return;
        }

        try {
            consultas.rechazarPago(idPago, motivo);

            JOptionPane.showMessageDialog(this,
                    "Pago rechazado.",
                    "Pago rechazado",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarPagosPendientes();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error al rechazar el pago",
                    JOptionPane.ERROR_MESSAGE);
        }
    }





    private JPanel crearSeccionSolicitudes() {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new BorderLayout());
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setOpaque(false);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblTitulo = new JLabel("Solicitudes de cuidadores");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
        lblTitulo.setForeground(AZUL_OSCURO);

        JLabel lblNota = new JLabel(
                "Abra una solicitud para ver el perfil completo "
                        + "y decidir"
        );
        lblNota.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNota.setForeground(GRIS_TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(lblNota);

        JPanel cajaBoton =
                new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 6));
        cajaBoton.setOpaque(false);
        cajaBoton.add(botonBlanco("Actualizar", ACTUALIZAR,
                e -> cargarSolicitudes()));

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(cajaBoton, BorderLayout.EAST);

        JPanel barraFiltros = new JPanel(new BorderLayout());
        barraFiltros.setOpaque(false);
        barraFiltros.setBorder(new EmptyBorder(0, 0, 16, 0));

        JPanel grupo =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        grupo.setOpaque(false);

        filtros[0] = filtro("Pendientes", "PENDIENTE");
        filtros[1] = filtro("Aprobadas", "VERIFICADO");
        filtros[2] = filtro("Rechazadas", "RECHAZADO");

        for (JButton f : filtros) {
            grupo.add(f);
        }

        lblContador = new JLabel();
        lblContador.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblContador.setForeground(GRIS_TEXTO);

        barraFiltros.add(grupo, BorderLayout.WEST);
        barraFiltros.add(lblContador, BorderLayout.EAST);

        arriba.add(encabezado);
        arriba.add(barraFiltros);

        listaSolicitudes = new JPanel();
        listaSolicitudes.setLayout(
                new BoxLayout(listaSolicitudes, BoxLayout.Y_AXIS)
        );
        listaSolicitudes.setOpaque(false);

        fondo.add(arriba, BorderLayout.NORTH);
        fondo.add(desplazable(listaSolicitudes),
                BorderLayout.CENTER);

        return fondo;
    }

    private JButton filtro(String texto, String estado) {

        JButton btn = new JButton(texto);
        btn.setFont(new Font(FUENTE, Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setPreferredSize(new Dimension(132, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            estadoActual = estado;
            cargarSolicitudes();
        });

        return btn;
    }

    private void pintarFiltros() {

        String[] estados = {"PENDIENTE", "VERIFICADO", "RECHAZADO"};

        for (int i = 0; i < filtros.length; i++) {

            if (filtros[i] == null) {
                continue;
            }

            boolean activo = estados[i].equals(estadoActual);

            filtros[i].setBackground(activo ? AZUL : Color.WHITE);
            filtros[i].setForeground(
                    activo ? Color.WHITE : GRIS_TEXTO);
            filtros[i].setBorder(BorderFactory.createLineBorder(
                    activo ? AZUL : BORDE_CAMPO));
        }
    }

    private void cargarSolicitudes() {

        if (listaSolicitudes == null) {
            return;
        }

        pintarFiltros();
        listaSolicitudes.removeAll();

        try {
            List<Cuidador> lista =
                    cuidadorDAO.listarPorEstado(estadoActual);

            lblContador.setText(lista.size()
                    + (lista.size() == 1
                    ? " solicitud" : " solicitudes"));

            if (lista.isEmpty()) {
                listaSolicitudes.add(vacio(
                        "No hay solicitudes en este estado."));
            } else {
                for (Cuidador c : lista) {
                    listaSolicitudes.add(fila(c));
                    listaSolicitudes.add(Box.createVerticalStrut(12));
                }
            }

            if ("PENDIENTE".equals(estadoActual)
                    && lblAviso != null) {
                lblAviso.setText(lista.isEmpty()
                        ? "" : lista.size() + " por revisar");
            }

        } catch (RuntimeException e) {
            listaSolicitudes.add(error(e.getMessage()));
        }

        listaSolicitudes.revalidate();
        listaSolicitudes.repaint();
    }

    private JPanel vacio(String mensaje) {

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(50, 30, 50, 30));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JLabel lblIcono = new JLabel(icono(HUELLA, BORDE_CAMPO, 48));
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(mensaje);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 15));
        lbl.setForeground(GRIS_PISTA);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        caja.add(lblIcono);
        caja.add(Box.createVerticalStrut(14));
        caja.add(lbl);

        return caja;
    }

    private JPanel fila(Cuidador cuidador) {

        Usuario duenio =
                usuarioDAO.buscarUsuarioPorId(cuidador.getIdUsuario());

        String nombre = duenio == null
                ? "Usuario #" + cuidador.getIdUsuario()
                : (texto(duenio.getNombre(), "") + " "
                + texto(duenio.getApellido(), "")).trim();

        if (nombre.isBlank()) {
            nombre = "Usuario #" + cuidador.getIdUsuario();
        }

        PanelCaja caja = new PanelCaja(14, Color.WHITE, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(16, 0));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));
        caja.setPreferredSize(new Dimension(ANCHO_CONTENIDO_MINIMO - 90, 112));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 112));
        caja.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel columnaAvatar = new JPanel(new GridBagLayout());
        columnaAvatar.setOpaque(false);
        columnaAvatar.setPreferredSize(new Dimension(72, 72));
        columnaAvatar.setMinimumSize(new Dimension(72, 72));
        columnaAvatar.add(new Avatar(56, iniciales(nombre)));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel filaNombre =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaNombre.setOpaque(false);
        filaNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaNombre.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 28));

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblNombre.setForeground(AZUL_OSCURO);

        filaNombre.add(lblNombre);
        filaNombre.add(pastilla(cuidador.getEstadoVerificacion()));

        JLabel lblDatos = new JLabel(
                (duenio == null ? "Sin correo"
                        : texto(duenio.getCorreo(), "Sin correo"))
                        + "      ·      Solicitud #"
                        + cuidador.getIdCuidador()
                        + "      ·      Fecha: "
                        + fecha(cuidador.getFechaSolicitud())
        );
        lblDatos.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblDatos.setForeground(GRIS_TEXTO);
        lblDatos.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(filaNombre);
        textos.add(Box.createVerticalStrut(5));
        textos.add(lblDatos);

        JButton btnVer = new JButton("Ver perfil");
        btnVer.setIcon(icono(OJO, AZUL, 16));
        btnVer.setIconTextGap(8);
        btnVer.setFont(new Font(FUENTE, Font.BOLD, 13));
        btnVer.setForeground(AZUL);
        btnVer.setBorderPainted(false);
        btnVer.setContentAreaFilled(false);
        btnVer.setFocusPainted(false);
        btnVer.setMargin(new Insets(0, 0, 0, 0));
        btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVer.addActionListener(e -> detalle(cuidador, duenio));

        JPanel columnaBoton = new JPanel(new GridBagLayout());
        columnaBoton.setOpaque(false);
        columnaBoton.setPreferredSize(new Dimension(130, 0));
        columnaBoton.add(btnVer);

        caja.add(columnaAvatar, BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);
        caja.add(columnaBoton, BorderLayout.EAST);

        MouseAdapter abrir = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                detalle(cuidador, duenio);
            }
        };

        caja.addMouseListener(abrir);
        textos.addMouseListener(abrir);

        return caja;
    }

    private JPanel pastilla(String estado) {

        String valor = texto(estado, "PENDIENTE");

        Color fondoPastilla;
        Color colorTexto;

        switch (valor) {
            case "VERIFICADO", "APROBADO" -> {
                fondoPastilla = VERDE_PILL;
                colorTexto = VERDE;
            }
            case "RECHAZADO" -> {
                fondoPastilla = ROJO_PILL;
                colorTexto = ROJO;
            }
            default -> {
                fondoPastilla = AMARILLO_PILL;
                colorTexto = AMARILLO_TXT;
            }
        }

        JLabel lbl = new JLabel(valor);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 11));
        lbl.setForeground(colorTexto);

        FontMetrics m = lbl.getFontMetrics(lbl.getFont());

        PanelCaja p = new PanelCaja(22, fondoPastilla, null);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setPreferredSize(
                new Dimension(m.stringWidth(valor) + 26, 22));
        p.add(lbl);

        return p;
    }





    private void detalle(Cuidador cuidador, Usuario duenio) {

        JDialog dialogo = new JDialog(
                this, "Perfil del solicitante", true);
        dialogo.setSize(720, 780);
        dialogo.setLocationRelativeTo(this);

        String nombre = duenio == null
                ? "Usuario #" + cuidador.getIdUsuario()
                : (texto(duenio.getNombre(), "") + " "
                + texto(duenio.getApellido(), "")).trim();

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(28, 32, 26, 32));


        JPanel cabecera = new JPanel(new BorderLayout(18, 0));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 84));

        JPanel cajaAvatar = new JPanel(new GridBagLayout());
        cajaAvatar.setOpaque(false);
        cajaAvatar.setPreferredSize(new Dimension(78, 78));
        cajaAvatar.add(new Avatar(72, iniciales(nombre)));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 23));
        lblNombre.setForeground(AZUL_OSCURO);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaEstado =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaEstado.setOpaque(false);
        filaEstado.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaEstado.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 26));
        filaEstado.add(pastilla(cuidador.getEstadoVerificacion()));

        textos.add(lblNombre);
        textos.add(Box.createVerticalStrut(7));
        textos.add(filaEstado);

        cabecera.add(cajaAvatar, BorderLayout.WEST);
        cabecera.add(textos, BorderLayout.CENTER);


        JPanel rejilla = new JPanel(new GridLayout(0, 2, 12, 12));
        rejilla.setOpaque(false);
        rejilla.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (duenio != null) {
            rejilla.add(dato("Correo electrónico",
                    duenio.getCorreo(), SOBRE));
            rejilla.add(dato("Teléfono",
                    duenio.getTelefono(), TELEFONO));
            rejilla.add(dato("Cédula",
                    duenio.getCedula(), TARJETA));
            rejilla.add(dato("Fecha de nacimiento",
                    duenio.getFechaNacimiento() == null
                            ? null
                            : duenio.getFechaNacimiento().toString(),
                    RELOJ));
            rejilla.add(dato("Dirección",
                    duenio.getDireccionDomicilio(), UBICACION));
            rejilla.add(dato("Rol actual",
                    duenio.getRolSistema(), PERSONA));
        }

        JLabel lblSeccion = new JLabel("Perfil de cuidador");
        lblSeccion.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblSeccion.setForeground(AZUL_OSCURO);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(cabecera);
        columna.add(Box.createVerticalStrut(24));
        columna.add(rejilla);
        columna.add(Box.createVerticalStrut(22));
        columna.add(lblSeccion);
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloque("Descripción del perfil",
                cuidador.getDescripcionPerfil()));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloque("Experiencia",
                cuidador.getExperiencia()));
        columna.add(Box.createVerticalStrut(12));
        columna.add(bloque("Fecha de solicitud",
                fecha(cuidador.getFechaSolicitud())));

        if (cuidador.getObservacion() != null
                && !cuidador.getObservacion().isBlank()) {
            columna.add(Box.createVerticalStrut(12));
            columna.add(bloque("Observación del administrador",
                    cuidador.getObservacion()));
        }

        columna.add(Box.createVerticalStrut(24));


        if ("PENDIENTE".equals(cuidador.getEstadoVerificacion())) {

            JPanel acciones = new JPanel(new GridLayout(1, 2, 12, 0));
            acciones.setOpaque(false);
            acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
            acciones.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 48));

            BotonRedondeado btnAprobar = new BotonRedondeado(
                    "Aprobar cuidador", AZUL_GRAD_1, Color.WHITE,
                    null, AZUL_GRAD_2);
            btnAprobar.setIcon(icono(VISTO, Color.WHITE, 16));
            btnAprobar.setIconTextGap(9);

            BotonRedondeado btnRechazar = new BotonRedondeado(
                    "Rechazar", Color.WHITE, ROJO, ROJO, null);
            btnRechazar.setIcon(icono(EQUIS, ROJO, 15));
            btnRechazar.setIconTextGap(9);

            btnAprobar.addActionListener(e -> {
                if (resolver(cuidador, true)) {
                    dialogo.dispose();
                }
            });

            btnRechazar.addActionListener(e -> {
                if (resolver(cuidador, false)) {
                    dialogo.dispose();
                }
            });

            acciones.add(btnAprobar);
            acciones.add(btnRechazar);
            columna.add(acciones);

        } else {

            JLabel lblRevisado = new JLabel(
                    "Revisada el "
                            + fecha(cuidador.getFechaVerificacion())
            );
            lblRevisado.setFont(new Font(FUENTE, Font.PLAIN, 13));
            lblRevisado.setForeground(GRIS_TEXTO);
            lblRevisado.setAlignmentX(Component.LEFT_ALIGNMENT);
            columna.add(lblRevisado);
        }

        JScrollPane scroll = new JScrollPane(columna);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        dialogo.setContentPane(scroll);
        dialogo.setVisible(true);

        cargarSolicitudes();
    }

    private JPanel dato(String etiqueta, String valor,
                        int tipoIcono) {

        PanelCaja caja = new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(10, 0));
        caja.setBorder(new EmptyBorder(11, 13, 11, 13));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        lblEtiqueta.setForeground(GRIS_PISTA);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(
                texto(valor, "No registrado"));
        lblValor.setFont(new Font(FUENTE, Font.BOLD, 13));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(lblEtiqueta);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lblValor);

        caja.add(new JLabel(icono(tipoIcono, AZUL, 17)),
                BorderLayout.WEST);
        caja.add(textos, BorderLayout.CENTER);

        return caja;
    }

    private JPanel bloque(String etiqueta, String valor) {

        PanelCaja caja = new PanelCaja(12, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(14, 16, 14, 16));
        caja.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblEtiqueta.setForeground(AZUL);
        lblEtiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(
                "<html><div style='width:560px;'>"
                        + escapar(texto(valor, "Sin información"))
                        + "</div></html>"
        );
        lblValor.setFont(new Font(FUENTE, Font.PLAIN, 14));
        lblValor.setForeground(AZUL_OSCURO);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        caja.add(lblEtiqueta);
        caja.add(Box.createVerticalStrut(6));
        caja.add(lblValor);

        return caja;
    }







    private boolean resolver(Cuidador cuidador, boolean aprobar) {

        String observacion = JOptionPane.showInputDialog(
                this,
                aprobar ? "Observación de la aprobación:"
                        : "Motivo del rechazo:",
                aprobar ? "Perfil aprobado."
                        : "No cumple los requisitos."
        );

        if (observacion == null) {
            return false;
        }

        try {

            boolean cambiado = cuidadorDAO.cambiarEstadoVerificacion(
                    cuidador.getIdCuidador(),
                    aprobar ? "VERIFICADO" : "RECHAZADO",
                    observacion,
                    administrador.getIdUsuario()
            );

            if (!cambiado) {
                JOptionPane.showMessageDialog(this,
                        "La solicitud ya fue revisada.",
                        "Sin cambios",
                        JOptionPane.WARNING_MESSAGE);
                return true;
            }

            if (aprobar) {
                usuarioDAO.actualizarRol(
                        cuidador.getIdUsuario(), "CUIDADOR");
            }

            JOptionPane.showMessageDialog(this,
                    aprobar
                            ? "Cuidador aprobado.\nSu rol cambió a "
                            + "CUIDADOR."
                            : "Solicitud rechazada.\nLa persona sigue "
                            + "como usuario normal.",
                    "Listo",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error al procesar la solicitud",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }






    private class GraficoArea extends JComponent {

        private final LinkedHashMap<String, Double> datos;
        private final Color color;
        private final boolean esDinero;
        private final List<Point> puntos = new ArrayList<>();
        private final List<String> etiquetas = new ArrayList<>();
        private final List<Double> valores = new ArrayList<>();
        private int indiceActivo = -1;

        GraficoArea(LinkedHashMap<String, Double> datos,
                    Color color, boolean esDinero) {
            this.datos = datos;
            this.color = color;
            this.esDinero = esDinero;
            setPreferredSize(new Dimension(560, 250));
            setMinimumSize(new Dimension(320, 210));
            setToolTipText("");
            ToolTipManager.sharedInstance().registerComponent(this);

            MouseAdapter eventos = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int nuevo = indiceCercano(e.getPoint());
                    if (nuevo != indiceActivo) {
                        indiceActivo = nuevo;
                        setCursor(nuevo >= 0
                                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                : Cursor.getDefaultCursor());
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    indiceActivo = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    int indice = indiceCercano(e.getPoint());
                    if (indice >= 0) {
                        JOptionPane.showMessageDialog(
                                PanelAdministrador.this,
                                etiquetas.get(indice) + ": "
                                        + valorDetalle(valores.get(indice)),
                                esDinero ? "Ingreso real de PostgreSQL"
                                        : "Reservas reales de PostgreSQL",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            };
            addMouseListener(eventos);
            addMouseMotionListener(eventos);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int indice = indiceCercano(event.getPoint());
            return indice < 0 ? null
                    : etiquetas.get(indice) + ": "
                    + valorDetalle(valores.get(indice));
        }

        private String valorDetalle(double valor) {
            return esDinero ? dinero(valor) : entero((int) valor);
        }

        private int indiceCercano(Point punto) {
            int encontrado = -1;
            double distancia = 14;
            for (int i = 0; i < puntos.size(); i++) {
                double actual = puntos.get(i).distance(punto);
                if (actual <= distancia) {
                    distancia = actual;
                    encontrado = i;
                }
            }
            return encontrado;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (datos.size() < 2) {
                sinDatos(g2, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            int izq = 54;
            int der = 14;
            int arriba = 18;
            int abajo = 30;

            int ancho = getWidth() - izq - der;
            int alto = getHeight() - arriba - abajo;

            double maximo = 0;
            for (double v : datos.values()) {
                maximo = Math.max(maximo, v);
            }
            if (maximo <= 0) {
                maximo = 1;
            }


            g2.setFont(new Font(FUENTE, Font.PLAIN, 10));

            for (int i = 0; i <= 4; i++) {

                int y = arriba + alto - alto * i / 4;

                g2.setColor(GRIS_REJILLA);
                g2.drawLine(izq, y, izq + ancho, y);

                double valor = maximo * i / 4;

                g2.setColor(GRIS_PISTA);
                String etiqueta = esDinero
                        ? cortoDinero(valor)
                        : String.valueOf((int) valor);

                g2.drawString(etiqueta,
                        izq - 8 - g2.getFontMetrics()
                                .stringWidth(etiqueta), y + 4);
            }

            int cantidad = datos.size();
            int[] xs = new int[cantidad];
            int[] ys = new int[cantidad];

            puntos.clear();
            etiquetas.clear();
            valores.clear();

            int i = 0;
            for (Map.Entry<String, Double> entrada : datos.entrySet()) {
                double valor = entrada.getValue();
                xs[i] = izq + ancho * i / (cantidad - 1);
                ys[i] = arriba + alto
                        - (int) (valor / maximo * alto);
                puntos.add(new Point(xs[i], ys[i]));
                etiquetas.add(entrada.getKey());
                valores.add(valor);
                i++;
            }


            GeneralPath area = new GeneralPath();
            area.moveTo(xs[0], arriba + alto);

            for (int j = 0; j < cantidad; j++) {
                area.lineTo(xs[j], ys[j]);
            }

            area.lineTo(xs[cantidad - 1], arriba + alto);
            area.closePath();

            g2.setPaint(new GradientPaint(
                    0, arriba,
                    new Color(color.getRed(), color.getGreen(),
                            color.getBlue(), 90),
                    0, arriba + alto,
                    new Color(color.getRed(), color.getGreen(),
                            color.getBlue(), 8)
            ));
            g2.fill(area);


            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.8f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            for (int j = 0; j < cantidad - 1; j++) {
                g2.drawLine(xs[j], ys[j], xs[j + 1], ys[j + 1]);
            }


            g2.setFont(new Font(FUENTE, Font.PLAIN, 9));
            int salto = Math.max(1, cantidad / 7);

            i = 0;
            for (String etiqueta : datos.keySet()) {

                g2.setColor(Color.WHITE);
                int radio = i == indiceActivo ? 7 : 4;
                g2.fillOval(xs[i] - radio, ys[i] - radio,
                        radio * 2, radio * 2);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.2f));
                g2.drawOval(xs[i] - radio, ys[i] - radio,
                        radio * 2, radio * 2);

                if (i % salto == 0 || i == cantidad - 1) {
                    g2.setColor(GRIS_PISTA);
                    FontMetrics m = g2.getFontMetrics();
                    g2.drawString(etiqueta,
                            xs[i] - m.stringWidth(etiqueta) / 2,
                            getHeight() - 10);
                }

                i++;
            }


            g2.setColor(AZUL_OSCURO);
            g2.setFont(new Font(FUENTE, Font.BOLD, 12));

            double ultimo = new ArrayList<>(datos.values())
                    .get(cantidad - 1);

            String texto = esDinero
                    ? cortoDinero(ultimo)
                    : String.valueOf((int) ultimo);

            g2.drawString(texto,
                    xs[cantidad - 1]
                            - g2.getFontMetrics()
                            .stringWidth(texto) - 2,
                    ys[cantidad - 1] - 12);

            if (indiceActivo >= 0 && indiceActivo < puntos.size()) {
                Point punto = puntos.get(indiceActivo);
                String detalle = etiquetas.get(indiceActivo) + "  ·  "
                        + valorDetalle(valores.get(indiceActivo));
                g2.setFont(new Font(FUENTE, Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int anchoCaja = fm.stringWidth(detalle) + 20;
                int altoCaja = 28;
                int xCaja = Math.max(4, Math.min(getWidth() - anchoCaja - 4,
                        punto.x - anchoCaja / 2));
                int yCaja = punto.y - 42;
                if (yCaja < 4) {
                    yCaja = punto.y + 14;
                }
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(xCaja, yCaja, anchoCaja, altoCaja, 10, 10);
                g2.setColor(BORDE_CAMPO);
                g2.drawRoundRect(xCaja, yCaja, anchoCaja, altoCaja, 10, 10);
                g2.setColor(AZUL_OSCURO);
                g2.drawString(detalle, xCaja + 10, yCaja + 18);
            }

            g2.dispose();
        }
    }


    private class GraficoDona extends JComponent {

        private final LinkedHashMap<String, Double> datos;
        private final List<SectorDona> sectores = new ArrayList<>();
        private int indiceActivo = -1;

        GraficoDona(LinkedHashMap<String, Double> datos) {
            this.datos = datos;
            setPreferredSize(new Dimension(560, 285));
            setMinimumSize(new Dimension(340, 250));
            setToolTipText("");
            ToolTipManager.sharedInstance().registerComponent(this);

            MouseAdapter eventos = new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int nuevo = indiceSector(e.getPoint());
                    if (nuevo != indiceActivo) {
                        indiceActivo = nuevo;
                        setCursor(nuevo >= 0
                                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                : Cursor.getDefaultCursor());
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    indiceActivo = -1;
                    setCursor(Cursor.getDefaultCursor());
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    int indice = indiceSector(e.getPoint());
                    if (indice >= 0) {
                        SectorDona sector = sectores.get(indice);
                        JOptionPane.showMessageDialog(
                                PanelAdministrador.this,
                                sector.etiqueta + ": "
                                        + entero((int) sector.valor)
                                        + " (" + porcentajeSector(sector) + ")",
                                "Dato real de PostgreSQL",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            };
            addMouseListener(eventos);
            addMouseMotionListener(eventos);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            int indice = indiceSector(event.getPoint());
            if (indice < 0) {
                return null;
            }
            SectorDona sector = sectores.get(indice);
            return sector.etiqueta + ": " + entero((int) sector.valor)
                    + " (" + porcentajeSector(sector) + ")";
        }

        private String porcentajeSector(SectorDona sector) {
            double total = 0;
            for (double valor : datos.values()) {
                total += valor;
            }
            return total <= 0 ? "0,0%"
                    : String.format("%.1f%%", sector.valor / total * 100);
        }

        private int indiceSector(Point punto) {
            for (int i = 0; i < sectores.size(); i++) {
                SectorDona sector = sectores.get(i);
                double dx = punto.x - sector.centroX;
                double dy = sector.centroY - punto.y;
                double radio = Math.sqrt(dx * dx + dy * dy);
                if (radio < sector.radioInterior
                        || radio > sector.radioExterior + 6) {
                    continue;
                }
                double angulo = Math.toDegrees(Math.atan2(dy, dx));
                if (angulo < 0) {
                    angulo += 360;
                }
                double distancia = normalizar(sector.inicio - angulo);
                if (distancia <= sector.porcion + 0.0001) {
                    return i;
                }
            }
            return -1;
        }

        private double normalizar(double angulo) {
            double valor = angulo % 360;
            return valor < 0 ? valor + 360 : valor;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            double total = 0;
            for (double v : datos.values()) {
                total += v;
            }

            if (total <= 0) {
                sinDatos(g2, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            int espacioLeyenda = Math.max(220, getWidth() / 2);
            int diametro = Math.min(getHeight() - 24,
                    Math.min(220, getWidth() - espacioLeyenda - 24));
            diametro = Math.max(diametro, 130);

            int x = 10;
            int y = (getHeight() - diametro) / 2;

            double inicio = 90;
            int indice = 0;
            sectores.clear();

            for (Map.Entry<String, Double> entrada : datos.entrySet()) {

                double valor = entrada.getValue();
                double porcion = valor / total * 360;
                int expansion = indice == indiceActivo ? 4 : 0;

                g2.setColor(SERIE[indice % SERIE.length]);
                g2.fill(new Arc2D.Double(x - expansion, y - expansion,
                        diametro + expansion * 2,
                        diametro + expansion * 2,
                        inicio, -porcion, Arc2D.PIE));

                sectores.add(new SectorDona(entrada.getKey(), valor,
                        inicio, porcion, x + diametro / 2.0,
                        y + diametro / 2.0, diametro / 2.0,
                        diametro * 0.29));

                inicio -= porcion;
                indice++;
            }

            int hueco = (int) (diametro * 0.58);
            g2.setColor(Color.WHITE);
            g2.fillOval(x + (diametro - hueco) / 2,
                    y + (diametro - hueco) / 2, hueco, hueco);

            g2.setColor(AZUL_OSCURO);
            g2.setFont(new Font(FUENTE, Font.BOLD, 21));

            String totalTexto = String.valueOf((int) total);
            FontMetrics m = g2.getFontMetrics();

            g2.drawString(totalTexto,
                    x + diametro / 2 - m.stringWidth(totalTexto) / 2,
                    y + diametro / 2 + 2);

            g2.setColor(GRIS_PISTA);
            g2.setFont(new Font(FUENTE, Font.PLAIN, 10));
            m = g2.getFontMetrics();

            g2.drawString("total",
                    x + diametro / 2 - m.stringWidth("total") / 2,
                    y + diametro / 2 + 18);

            int lx = x + diametro + 28;
            int altoLeyenda = datos.size() * 31;
            int ly = Math.max(18,
                    (getHeight() - altoLeyenda) / 2 + 10);
            indice = 0;

            for (Map.Entry<String, Double> e : datos.entrySet()) {

                if (indice == indiceActivo) {
                    g2.setColor(aclarar(SERIE[indice % SERIE.length], 0.88f));
                    g2.fillRoundRect(lx - 8, ly - 16,
                            Math.max(170, getWidth() - lx - 3), 29, 9, 9);
                }

                g2.setColor(SERIE[indice % SERIE.length]);
                g2.fillRoundRect(lx, ly - 9, 11, 11, 4, 4);

                g2.setColor(AZUL_OSCURO);
                g2.setFont(new Font(FUENTE, Font.BOLD, 12));
                g2.drawString(e.getKey(), lx + 19, ly + 1);

                g2.setColor(GRIS_TEXTO);
                g2.setFont(new Font(FUENTE, Font.PLAIN, 11));
                g2.drawString((int) (double) e.getValue() + "   ("
                                + String.format("%.1f",
                                e.getValue() / total * 100) + "%)",
                        lx + 19, ly + 16);

                ly += 31;
                indice++;
            }

            g2.dispose();
        }

        private class SectorDona {
            final String etiqueta;
            final double valor;
            final double inicio;
            final double porcion;
            final double centroX;
            final double centroY;
            final double radioExterior;
            final double radioInterior;

            SectorDona(String etiqueta, double valor,
                       double inicio, double porcion,
                       double centroX, double centroY,
                       double radioExterior, double radioInterior) {
                this.etiqueta = etiqueta;
                this.valor = valor;
                this.inicio = inicio;
                this.porcion = porcion;
                this.centroX = centroX;
                this.centroY = centroY;
                this.radioExterior = radioExterior;
                this.radioInterior = radioInterior;
            }
        }
    }


    private class GraficoBarras extends JComponent {

        private final LinkedHashMap<String, Double> datos;
        private final boolean esDinero;

        GraficoBarras(LinkedHashMap<String, Double> datos,
                      boolean esDinero) {
            this.datos = datos;
            this.esDinero = esDinero;
            setPreferredSize(new Dimension(360, 210));
            setMinimumSize(new Dimension(240, 170));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (datos.isEmpty()) {
                sinDatos(g2, getWidth(), getHeight());
                g2.dispose();
                return;
            }

            double maximo = 0;
            for (double v : datos.values()) {
                maximo = Math.max(maximo, v);
            }
            if (maximo <= 0) {
                maximo = 1;
            }

            int alto = Math.min(52,
                    getHeight() / Math.max(1, datos.size()));
            int anchoBarra = getWidth() - 86;
            int indice = 0;

            for (Map.Entry<String, Double> e : datos.entrySet()) {

                int y = indice * alto + 4;
                int largo = (int) (e.getValue() / maximo * anchoBarra);

                g2.setColor(AZUL_OSCURO);
                g2.setFont(new Font(FUENTE, Font.BOLD, 12));
                g2.drawString(corto(e.getKey(), 22), 0, y + 12);

                g2.setColor(GRIS_REJILLA);
                g2.fillRoundRect(0, y + 19, anchoBarra, 13, 7, 7);

                Color color = SERIE[indice % SERIE.length];

                g2.setPaint(new GradientPaint(
                        0, 0, color, Math.max(largo, 1), 0,
                        aclarar(color, 0.35f)));
                g2.fillRoundRect(0, y + 19,
                        Math.max(largo, 6), 13, 7, 7);

                g2.setColor(color);
                g2.setFont(new Font(FUENTE, Font.BOLD, 12));

                String valor = esDinero
                        ? cortoDinero(e.getValue())
                        : String.valueOf((int) (double) e.getValue());

                g2.drawString(valor, anchoBarra + 10, y + 30);

                indice++;
            }

            g2.dispose();
        }
    }

    private void sinDatos(Graphics2D g2, int ancho, int alto) {

        g2.setColor(GRIS_PISTA);
        g2.setFont(new Font(FUENTE, Font.PLAIN, 13));

        String texto = "Todavía no hay datos suficientes";
        FontMetrics m = g2.getFontMetrics();

        g2.drawString(texto,
                ancho / 2 - m.stringWidth(texto) / 2, alto / 2);
    }









    private static final class MesOpcion {
        final int numero;
        final String nombre;

        MesOpcion(int numero, String nombre) {
            this.numero = numero;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private static final class Opcion {
        final Integer id;
        final String nombre;

        Opcion(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        @Override
        public String toString() {
            return nombre;
        }
    }

    private static final class ReporteResultado {
        static final String[] COLUMNAS = {
                "ID Reserva", "Fecha solicitud", "Cliente", "Mascota",
                "Especie", "Alojamiento", "Ciudad", "Cuidador",
                "Fecha ingreso", "Fecha salida", "Noches",
                "Estado reserva", "Hospedaje", "Transporte",
                "Total final", "Estado pago", "Monto aprobado"
        };

        final String titulo;
        final String[] columnas;
        final List<String> detalles = new ArrayList<>();
        final LinkedHashMap<String, String> resumen = new LinkedHashMap<>();
        final List<Object[]> filas = new ArrayList<>();
        final LinkedHashMap<String, Integer> estados = new LinkedHashMap<>();
        double ingresosAprobados;

        ReporteResultado(String titulo) {
            this(titulo, COLUMNAS);
        }

        ReporteResultado(String titulo, String[] columnas) {
            this.titulo = titulo;
            this.columnas = columnas;
        }
    }

    private static final class DashboardDatos {
        final Consultas.Resumen resumen;
        final LinkedHashMap<String, Double> ingresosMes;
        final LinkedHashMap<String, Double> reservasMes;
        final LinkedHashMap<String, Double> estadosReserva;
        final LinkedHashMap<String, Double> solicitudes;
        final List<Object[]> rankingCuidadores;
        final List<Object[]> rankingAlojamientos;
        final java.time.LocalDateTime generado;

        DashboardDatos(Consultas.Resumen resumen,
                       LinkedHashMap<String, Double> ingresosMes,
                       LinkedHashMap<String, Double> reservasMes,
                       LinkedHashMap<String, Double> estadosReserva,
                       LinkedHashMap<String, Double> solicitudes,
                       List<Object[]> rankingCuidadores,
                       List<Object[]> rankingAlojamientos) {
            this.resumen = resumen;
            this.ingresosMes = ingresosMes;
            this.reservasMes = reservasMes;
            this.estadosReserva = estadosReserva;
            this.solicitudes = solicitudes;
            this.rankingCuidadores = rankingCuidadores;
            this.rankingAlojamientos = rankingAlojamientos;
            this.generado = java.time.LocalDateTime.now();
        }
    }

    private static final class Consultas {

        static final String SQL_CUIDADORES = """
                SELECT c.id_cuidador AS "ID Cuidador",
                       u.nombre || ' ' || u.apellido AS "Cuidador",
                       u.email AS "Correo",
                       u.telefono AS "Teléfono",
                       c.estado_verificacion AS "Estado",
                       COUNT(DISTINCT a.id_alojamiento) AS "Alojamientos",
                       COALESCE(TO_CHAR(c.fecha_verificacion,
                                'DD/MM/YYYY'), '-') AS "Verificado"
                FROM cuidador c
                JOIN usuario u ON u.id_usuario = c.id_usuario
                LEFT JOIN alojamiento a
                       ON a.id_cuidador = c.id_cuidador
                GROUP BY c.id_cuidador, u.nombre, u.apellido,
                         u.email, u.telefono, c.estado_verificacion,
                         c.fecha_verificacion
                ORDER BY c.estado_verificacion, u.nombre
                """;

        static final String SQL_USUARIOS = """
                SELECT u.nombre || ' ' || u.apellido AS "Usuario",
                       u.email AS "Correo",
                       u.telefono AS "Teléfono",
                       u.rol_sistema AS "Rol",
                       COUNT(m.id_mascota) AS "Mascotas",
                       TO_CHAR(u.fecha_registro,
                               'DD/MM/YYYY') AS "Registro",
                       CASE WHEN u.activo THEN 'Sí'
                            ELSE 'No' END AS "Activo"
                FROM usuario u
                LEFT JOIN mascota m ON m.id_usuario = u.id_usuario
                GROUP BY u.id_usuario
                ORDER BY u.fecha_registro DESC
                """;

        static final String SQL_RESERVAS = """
                SELECT r.id_reserva AS "ID Reserva",
                       m.nombre AS "Mascota",
                       a.nombre AS "Alojamiento",
                       a.ciudad AS "Ciudad",
                       TO_CHAR(r.fecha_ingreso,
                               'DD/MM/YYYY') AS "Ingreso",
                       TO_CHAR(r.fecha_salida,
                               'DD/MM/YYYY') AS "Salida",
                       r.estado_reserva AS "Estado",
                       '$ ' || TO_CHAR(r.total_acordado,
                               'FM999G999D00') AS "Total"
                FROM reserva r
                JOIN mascota m ON m.id_mascota = r.id_mascota
                JOIN alojamiento a
                     ON a.id_alojamiento = r.id_alojamiento
                ORDER BY r.id_reserva DESC
                """;

        static final String SQL_PAGOS = """
                SELECT p.id_pago AS "ID Pago",
                       p.id_reserva AS "ID Reserva",
                       TO_CHAR(COALESCE(p.fecha_pago,
                               p.fecha_registro),
                               'DD/MM/YYYY') AS "Fecha",
                       p.tipo_pago AS "Tipo",
                       p.forma_pago AS "Forma de pago",
                       p.estado_pago AS "Estado",
                       p.numero_comprobante AS "Comprobante",
                       '$ ' || TO_CHAR(p.monto,
                               'FM999G999D00') AS "Monto"
                FROM pago p
                ORDER BY COALESCE(p.fecha_pago,
                                  p.fecha_registro) DESC
                """;

        static final String SQL_ALOJAMIENTOS = """
                SELECT a.nombre AS "Alojamiento",
                       a.ciudad AS "Ciudad",
                       a.tipo_alojamiento AS "Tipo",
                       a.capacidad_total AS "Capacidad",
                       '$ ' || TO_CHAR(a.precio_noche,
                               'FM999G999D00') AS "Precio noche",
                       CASE WHEN a.activo THEN 'Publicado'
                            ELSE 'Oculto' END AS "Estado",
                       COUNT(DISTINCT r.id_reserva) AS "Reservas"
                FROM alojamiento a
                LEFT JOIN reserva r
                       ON r.id_alojamiento = a.id_alojamiento
                GROUP BY a.id_alojamiento
                ORDER BY COUNT(DISTINCT r.id_reserva) DESC
                """;

        static class Resumen {
            int usuarios;
            int mascotas;
            int cuidadores;
            int alojamientos;
            int reservas;
            int reservasFinalizadas;
            int pendientes;
            int pagosPendientes;
            int resenas;
            double ingresos;
            double ticket;
            double calificacion;
            double tasaAprobacion;
        }

        static class Tabla {
            String[] columnas;
            List<Object[]> filas = new ArrayList<>();
        }

        Resumen resumen() {

            String sql = """
                    SELECT
                      (SELECT COUNT(*) FROM usuario) usuarios,
                      (SELECT COUNT(*) FROM mascota) mascotas,
                      (SELECT COUNT(*) FROM cuidador
                         WHERE estado_verificacion='VERIFICADO')
                         cuidadores,
                      (SELECT COUNT(*) FROM cuidador) total_cuidadores,
                      (SELECT COUNT(*) FROM cuidador
                         WHERE estado_verificacion='PENDIENTE')
                         pendientes,
                      (SELECT COUNT(*)
                         FROM pago p
                         JOIN reserva r
                           ON r.id_reserva = p.id_reserva
                        WHERE p.estado_pago='PENDIENTE'
                          AND p.forma_pago='TRANSFERENCIA'
                          AND r.estado_reserva <> 'CANCELADA')
                         pagos_pendientes,
                      (SELECT COUNT(*) FROM alojamiento
                         WHERE activo) alojamientos,
                      (SELECT COUNT(*) FROM reserva) reservas,
                      (SELECT COUNT(*) FROM reserva
                         WHERE estado_reserva='FINALIZADA')
                         finalizadas,
                      (SELECT COUNT(*) FROM resena) resenas,
                       COALESCE((SELECT SUM(monto)
                          FROM pago WHERE estado_pago='APROBADO'),0)
                          ingresos,
                       COALESCE((SELECT AVG(monto)
                          FROM pago WHERE estado_pago='APROBADO'),0) ticket,
                      COALESCE((SELECT AVG(calificacion)
                         FROM resena),0) calificacion
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                Resumen r = new Resumen();

                if (rs.next()) {
                    r.usuarios = rs.getInt("usuarios");
                    r.mascotas = rs.getInt("mascotas");
                    r.cuidadores = rs.getInt("cuidadores");
                    r.pendientes = rs.getInt("pendientes");
                    r.pagosPendientes = rs.getInt("pagos_pendientes");
                    r.alojamientos = rs.getInt("alojamientos");
                    r.reservas = rs.getInt("reservas");
                    r.reservasFinalizadas = rs.getInt("finalizadas");
                    r.resenas = rs.getInt("resenas");
                    r.ingresos = rs.getDouble("ingresos");
                    r.ticket = rs.getDouble("ticket");
                    r.calificacion = rs.getDouble("calificacion");

                    int totalCuidadores =
                            rs.getInt("total_cuidadores");

                    r.tasaAprobacion = totalCuidadores == 0
                            ? 0
                            : r.cuidadores * 100.0 / totalCuidadores;
                }

                return r;

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        List<Object[]> pagosPendientes() {

            String sql = """
                    SELECT p.id_pago,
                           p.id_reserva,
                           u.nombre || ' ' || u.apellido,
                           a.nombre,
                           p.monto,
                           p.forma_pago,
                           p.numero_comprobante,
                           TO_CHAR(COALESCE(p.fecha_pago,
                             p.fecha_registro), 'DD/MM/YYYY')
                    FROM pago p
                    JOIN reserva r ON r.id_reserva = p.id_reserva
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN usuario u ON u.id_usuario = m.id_usuario
                    WHERE p.estado_pago = 'PENDIENTE'
                      AND p.forma_pago = 'TRANSFERENCIA'
                      AND r.estado_reserva <> 'CANCELADA'
                    ORDER BY p.id_pago DESC
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new Object[]{
                            rs.getInt(1), rs.getInt(2),
                            rs.getString(3), rs.getString(4),
                            rs.getDouble(5), rs.getString(6),
                            rs.getString(7), rs.getString(8)
                    });
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return lista;
        }

        void aprobarPago(int idPago) {
            String validar = """
                    SELECT p.estado_pago, r.estado_reserva
                    FROM pago p
                    JOIN reserva r ON r.id_reserva = p.id_reserva
                    WHERE p.id_pago = ?::integer
                    FOR UPDATE OF p, r
                    """;

            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);

                try {
                    try (PreparedStatement ps = c.prepareStatement(validar)) {
                        ps.setInt(1, idPago);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException("El pago no existe.");
                            }
                            if (!"PENDIENTE".equals(rs.getString(1))) {
                                throw new SQLException(
                                        "El pago ya fue procesado.");
                            }
                            if ("CANCELADA".equals(rs.getString(2))) {
                                throw new SQLException(
                                        "No se puede aprobar un pago de una reserva cancelada.");
                            }
                        }
                    }

                    try (CallableStatement cs = c.prepareCall(
                            "CALL sp_aprobar_pago(?::integer)")) {
                        cs.setInt(1, idPago);
                        cs.execute();
                    }

                    c.commit();
                } catch (SQLException e) {
                    c.rollback();
                    throw e;
                } finally {
                    c.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        void rechazarPago(int idPago, String motivo) {
            try (Connection c = ConexionBD.conectar();
                 CallableStatement cs = c.prepareCall(
                         "CALL sp_rechazar_pago(?::integer, ?::text)")) {
                cs.setInt(1, idPago);
                cs.setString(2,
                        motivo == null || motivo.isBlank()
                                ? null : motivo.trim());
                cs.execute();
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }






        LinkedHashMap<String, Double> ingresosPorMes(int meses) {
            String sql = """
                    WITH meses AS (
                        SELECT GENERATE_SERIES(
                            DATE_TRUNC('month', CURRENT_DATE)
                                - ((?::integer - 1) * INTERVAL '1 month'),
                            DATE_TRUNC('month', CURRENT_DATE),
                            INTERVAL '1 month'
                        ) AS mes
                    ), totales AS (
                        SELECT DATE_TRUNC('month', fecha_pago) AS mes,
                               SUM(monto) AS total
                        FROM pago
                        WHERE estado_pago = 'APROBADO'
                          AND fecha_pago IS NOT NULL
                        GROUP BY DATE_TRUNC('month', fecha_pago)
                    )
                    SELECT TO_CHAR(m.mes, 'MM/YY') AS etiqueta,
                           COALESCE(t.total, 0) AS total
                    FROM meses m
                    LEFT JOIN totales t ON t.mes = m.mes
                    ORDER BY m.mes
                    """;
            return serieMensual(sql, meses);
        }

        LinkedHashMap<String, Double> reservasPorMes(int meses) {
            String sql = """
                    WITH meses AS (
                        SELECT GENERATE_SERIES(
                            DATE_TRUNC('month', CURRENT_DATE)
                                - ((?::integer - 1) * INTERVAL '1 month'),
                            DATE_TRUNC('month', CURRENT_DATE),
                            INTERVAL '1 month'
                        ) AS mes
                    ), totales AS (
                        SELECT DATE_TRUNC('month', fecha_solicitud) AS mes,
                               COUNT(*) AS total
                        FROM reserva
                        GROUP BY DATE_TRUNC('month', fecha_solicitud)
                    )
                    SELECT TO_CHAR(m.mes, 'MM/YY') AS etiqueta,
                           COALESCE(t.total, 0) AS total
                    FROM meses m
                    LEFT JOIN totales t ON t.mes = m.mes
                    ORDER BY m.mes
                    """;
            return serieMensual(sql, meses);
        }

        private LinkedHashMap<String, Double> serieMensual(
                String sql, int meses) {

            LinkedHashMap<String, Double> datos = new LinkedHashMap<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, meses);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        datos.put(rs.getString("etiqueta"),
                                rs.getDouble("total"));
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return datos;
        }

        LinkedHashMap<String, Double> reservasPorEstado() {
            return serie("""
                    WITH estados(estado, orden) AS (
                        VALUES ('SOLICITADA', 1), ('ACEPTADA', 2),
                               ('RECHAZADA', 3), ('CANCELADA', 4),
                               ('EN_CURSO', 5), ('FINALIZADA', 6)
                    ), totales AS (
                        SELECT estado_reserva, COUNT(*) AS valor
                        FROM reserva
                        GROUP BY estado_reserva
                    )
                    SELECT e.estado, COALESCE(t.valor, 0)
                    FROM estados e
                    LEFT JOIN totales t ON t.estado_reserva = e.estado
                    ORDER BY e.orden
                    """);
        }

        LinkedHashMap<String, Double> solicitudesPorEstado() {
            return serie("""
                    SELECT estado_verificacion, COUNT(*) valor
                    FROM cuidador GROUP BY estado_verificacion
                    ORDER BY valor DESC
                    """);
        }


        private LinkedHashMap<String, Double> serie(String sql) {

            LinkedHashMap<String, Double> datos =
                    new LinkedHashMap<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    datos.put(rs.getString(1), rs.getDouble(2));
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return datos;
        }

        List<Object[]> rankingCuidadores(int limite) {

            String sql = """
                    WITH reservas_cuidador AS (
                        SELECT a.id_cuidador,
                               COUNT(DISTINCT r.id_reserva) AS reservas
                        FROM alojamiento a
                        LEFT JOIN reserva r
                               ON r.id_alojamiento = a.id_alojamiento
                        GROUP BY a.id_cuidador
                    ), pagos_cuidador AS (
                        SELECT a.id_cuidador,
                               SUM(p.monto) AS ingresos
                        FROM pago p
                        JOIN reserva r ON r.id_reserva = p.id_reserva
                        JOIN alojamiento a
                             ON a.id_alojamiento = r.id_alojamiento
                        WHERE p.estado_pago = 'APROBADO'
                        GROUP BY a.id_cuidador
                    ), resenas_cuidador AS (
                        SELECT c.id_cuidador,
                               AVG(re.calificacion) AS calificacion,
                               COUNT(re.id_resena) AS cantidad
                        FROM cuidador c
                        JOIN usuario u ON u.id_usuario = c.id_usuario
                        LEFT JOIN resena re
                               ON re.id_usuario_evaluado = u.id_usuario
                        GROUP BY c.id_cuidador
                    )
                    SELECT c.id_cuidador,
                           u.nombre || ' ' || u.apellido AS nombre,
                           rc.calificacion,
                           COALESCE(rc.cantidad, 0) AS resenas,
                           COALESCE(rv.reservas, 0) AS reservas,
                           COALESCE(pc.ingresos, 0) AS ingresos
                    FROM cuidador c
                    JOIN usuario u ON u.id_usuario = c.id_usuario
                    LEFT JOIN reservas_cuidador rv
                           ON rv.id_cuidador = c.id_cuidador
                    LEFT JOIN pagos_cuidador pc
                           ON pc.id_cuidador = c.id_cuidador
                    LEFT JOIN resenas_cuidador rc
                           ON rc.id_cuidador = c.id_cuidador
                    WHERE c.estado_verificacion = 'VERIFICADO'
                    ORDER BY ingresos DESC, nombre
                    LIMIT ?::integer
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, limite);

                try (ResultSet rs = ps.executeQuery()) {
                    int pos = 1;
                    while (rs.next()) {
                        int cantidadResenas = rs.getInt(4);
                        lista.add(new Object[]{
                                pos++,
                                rs.getInt(1),
                                rs.getString(2),
                                cantidadResenas == 0 ? "Sin reseñas"
                                        : String.format("%.1f",
                                        rs.getDouble(3)),
                                cantidadResenas,
                                rs.getInt(5),
                                "$ " + String.format("%,.2f",
                                        rs.getDouble(6))
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return lista;
        }

        List<Object[]> rankingAlojamientos(int limite) {

            String sql = """
                    WITH reservas_alojamiento AS (
                        SELECT id_alojamiento,
                               COUNT(DISTINCT id_reserva) AS reservas
                        FROM reserva
                        GROUP BY id_alojamiento
                    ), pagos_alojamiento AS (
                        SELECT r.id_alojamiento,
                               SUM(p.monto) AS ingresos
                        FROM pago p
                        JOIN reserva r ON r.id_reserva = p.id_reserva
                        WHERE p.estado_pago = 'APROBADO'
                        GROUP BY r.id_alojamiento
                    )
                    SELECT a.nombre, a.ciudad,
                           COALESCE(ra.reservas, 0) AS reservas,
                           COALESCE(pa.ingresos, 0) AS ingresos
                    FROM alojamiento a
                    LEFT JOIN reservas_alojamiento ra
                           ON ra.id_alojamiento = a.id_alojamiento
                    LEFT JOIN pagos_alojamiento pa
                           ON pa.id_alojamiento = a.id_alojamiento
                    ORDER BY reservas DESC, a.nombre
                    LIMIT ?::integer
                    """;

            List<Object[]> lista = new ArrayList<>();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {

                ps.setInt(1, limite);

                try (ResultSet rs = ps.executeQuery()) {
                    int pos = 1;
                    while (rs.next()) {
                        lista.add(new Object[]{
                                pos++,
                                rs.getString(1),
                                rs.getString(2),
                                rs.getInt(3),
                                "$ " + String.format("%,.2f",
                                        rs.getDouble(4))
                        });
                    }
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return lista;
        }

        List<Integer> aniosDisponibles() {
            String sql = """
                    SELECT DISTINCT EXTRACT(YEAR FROM fecha)::integer AS anio
                    FROM (
                        SELECT fecha_solicitud AS fecha FROM reserva
                        UNION ALL
                        SELECT fecha_pago FROM pago WHERE fecha_pago IS NOT NULL
                        UNION ALL
                        SELECT fecha_registro FROM pago
                    ) datos
                    WHERE fecha IS NOT NULL
                    ORDER BY anio
                    """;
            List<Integer> anios = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    anios.add(rs.getInt(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            if (anios.isEmpty()) {
                anios.add(LocalDate.now().getYear());
            }
            return anios;
        }

        List<Opcion> especies() {
            return opciones("""
                    SELECT id_especie, nombre_especie
                    FROM especie
                    ORDER BY nombre_especie
                    """);
        }

        List<String> ciudades() {
            String sql = """
                    SELECT DISTINCT TRIM(ciudad)
                    FROM alojamiento
                    WHERE ciudad IS NOT NULL
                      AND TRIM(ciudad) <> ''
                    ORDER BY TRIM(ciudad)
                    """;
            List<String> ciudades = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ciudades.add(rs.getString(1));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return ciudades;
        }

        List<Opcion> cuidadores() {
            return opciones("""
                    SELECT c.id_cuidador,
                           u.nombre || ' ' || u.apellido
                    FROM cuidador c
                    JOIN usuario u ON u.id_usuario = c.id_usuario
                    ORDER BY u.nombre, u.apellido
                    """);
        }

        List<Opcion> alojamientos() {
            return opciones("""
                    SELECT id_alojamiento, nombre
                    FROM alojamiento
                    ORDER BY nombre
                    """);
        }

        private List<Opcion> opciones(String sql) {
            List<Opcion> opciones = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    opciones.add(new Opcion(rs.getInt(1), rs.getString(2)));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return opciones;
        }

        ReporteResultado reporteMensual(int anio, int mes) {
            YearMonth periodo = YearMonth.of(anio, mes);
            LocalDate inicio = periodo.atDay(1);
            LocalDate fin = periodo.plusMonths(1).atDay(1);
            String sql = consultaDetalle("""
                    AND r.fecha_solicitud >= ?::timestamp
                    AND r.fecha_solicitud < ?::timestamp
                    """);
            String[] meses = {"Enero", "Febrero", "Marzo", "Abril",
                    "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
                    "Octubre", "Noviembre", "Diciembre"};
            ReporteResultado resultado = new ReporteResultado(
                    "REPORTE ADMINISTRATIVO MENSUAL");
            resultado.detalles.add("Mes seleccionado: " + meses[mes - 1]);
            resultado.detalles.add("Año seleccionado: " + anio);
            resultado.detalles.add("Fecha/hora de generación: "
                    + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(inicio.atStartOfDay()));
                ps.setTimestamp(2, Timestamp.valueOf(fin.atStartOfDay()));
                cargarFilasReporte(ps, resultado);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            String pagos = """
                    SELECT
                      COALESCE((SELECT SUM(monto) FROM pago
                        WHERE estado_pago = 'APROBADO'
                          AND fecha_pago >= ?::timestamp
                          AND fecha_pago < ?::timestamp), 0),
                      (SELECT COUNT(*) FROM pago
                        WHERE estado_pago = 'APROBADO'
                          AND fecha_pago >= ?::timestamp
                          AND fecha_pago < ?::timestamp),
                      (SELECT COUNT(*) FROM pago p
                        JOIN reserva r
                          ON r.id_reserva = p.id_reserva
                        WHERE p.estado_pago = 'PENDIENTE'
                          AND r.estado_reserva <> 'CANCELADA'
                          AND p.fecha_registro >= ?::timestamp
                          AND p.fecha_registro < ?::timestamp)
                    """;
            double ingresos = 0;
            int aprobados = 0;
            int pendientes = 0;
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(pagos)) {
                Timestamp desde = Timestamp.valueOf(inicio.atStartOfDay());
                Timestamp hasta = Timestamp.valueOf(fin.atStartOfDay());
                for (int i = 1; i <= 6; i += 2) {
                    ps.setTimestamp(i, desde);
                    ps.setTimestamp(i + 1, hasta);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ingresos = rs.getDouble(1);
                        aprobados = rs.getInt(2);
                        pendientes = rs.getInt(3);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            resultado.resumen.put("Reservas solicitadas en el período",
                    String.valueOf(resultado.filas.size()));
            agregarEstadosMensuales(resultado);
            resultado.resumen.put(
                    "Ingresos aprobados cobrados en el período",
                    dineroInforme(ingresos));
            resultado.resumen.put("Pagos aprobados",
                    String.valueOf(aprobados));
            resultado.resumen.put("Pagos pendientes",
                    String.valueOf(pendientes));
            return resultado;
        }

        ReporteResultado reporteGeneral(LocalDate desde, LocalDate hasta,
                                        Integer idEspecie, String ciudad,
                                        String estado, Integer idCuidador,
                                        Integer idAlojamiento,
                                        String nombreEspecie,
                                        String nombreCuidador,
                                        String nombreAlojamiento) {
            StringBuilder condiciones = new StringBuilder();
            List<Object> parametros = new ArrayList<>();

            if (desde != null) {
                condiciones.append(
                        " AND r.fecha_solicitud::date >= ?::date\n");
                parametros.add(desde);
            }
            if (hasta != null) {
                condiciones.append(
                        " AND r.fecha_solicitud::date <= ?::date\n");
                parametros.add(hasta);
            }
            if (idEspecie != null) {
                condiciones.append(" AND e.id_especie = ?::integer\n");
                parametros.add(idEspecie);
            }
            if (ciudad != null) {
                condiciones.append(" AND a.ciudad = ?::varchar\n");
                parametros.add(ciudad);
            }
            if (estado != null) {
                condiciones.append(
                        " AND r.estado_reserva = ?::varchar\n");
                parametros.add(estado);
            }
            if (idCuidador != null) {
                condiciones.append(" AND c.id_cuidador = ?::integer\n");
                parametros.add(idCuidador);
            }
            if (idAlojamiento != null) {
                condiciones.append(
                        " AND a.id_alojamiento = ?::integer\n");
                parametros.add(idAlojamiento);
            }

            ReporteResultado resultado = new ReporteResultado(
                    "REPORTE ADMINISTRATIVO GENERAL");
            DateTimeFormatter fecha = DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy");
            resultado.detalles.add("Fecha/hora de generación: "
                    + java.time.LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            resultado.detalles.add("Desde: "
                    + (desde == null ? "Sin fecha" : desde.format(fecha)));
            resultado.detalles.add("Hasta: "
                    + (hasta == null ? "Sin fecha" : hasta.format(fecha)));
            resultado.detalles.add("Especie: " + nombreEspecie);
            resultado.detalles.add("Ciudad: "
                    + (ciudad == null ? "Todas las ciudades" : ciudad));
            resultado.detalles.add("Estado: "
                    + (estado == null ? "Todos los estados" : estado));
            resultado.detalles.add("Cuidador: " + nombreCuidador);
            resultado.detalles.add("Alojamiento: " + nombreAlojamiento);

            String sql = consultaDetalle(condiciones.toString());
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                for (int i = 0; i < parametros.size(); i++) {
                    Object valor = parametros.get(i);
                    int indice = i + 1;
                    if (valor instanceof LocalDate) {
                        ps.setDate(indice, Date.valueOf((LocalDate) valor));
                    } else if (valor instanceof Integer) {
                        ps.setInt(indice, (Integer) valor);
                    } else {
                        ps.setString(indice, String.valueOf(valor));
                    }
                }
                cargarFilasReporte(ps, resultado);
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            resultado.resumen.put("Total de reservas",
                    String.valueOf(resultado.filas.size()));
            resultado.resumen.put("Finalizadas",
                    cantidadEstado(resultado, "FINALIZADA"));
            resultado.resumen.put("Canceladas",
                    cantidadEstado(resultado, "CANCELADA"));
            resultado.resumen.put("Rechazadas",
                    cantidadEstado(resultado, "RECHAZADA"));
            resultado.resumen.put("Aceptadas",
                    cantidadEstado(resultado, "ACEPTADA"));
            resultado.resumen.put("En curso",
                    cantidadEstado(resultado, "EN_CURSO"));
            resultado.resumen.put("Ingresos aprobados",
                    dineroInforme(resultado.ingresosAprobados));
            return resultado;
        }

        private String consultaDetalle(String condiciones) {
            return """
                    WITH pagos_reserva AS (
                        SELECT id_reserva,
                               STRING_AGG(DISTINCT estado_pago, ', '
                                   ORDER BY estado_pago) AS estados_pago,
                               COALESCE(SUM(monto) FILTER (
                                   WHERE estado_pago = 'APROBADO'), 0)
                                   AS monto_aprobado
                        FROM pago
                        GROUP BY id_reserva
                    ), transporte_reserva AS (
                        SELECT id_reserva,
                               COALESCE(MAX(precio_acordado) FILTER (
                                   WHERE estado_transporte IN
                                   ('PROGRAMADO', 'FINALIZADO')), 0)
                                   AS transporte
                        FROM transporte
                        GROUP BY id_reserva
                    )
                    SELECT r.id_reserva,
                           TO_CHAR(r.fecha_solicitud,
                                   'DD/MM/YYYY HH24:MI'),
                           uc.nombre || ' ' || uc.apellido AS cliente,
                           m.nombre AS mascota,
                           e.nombre_especie AS especie,
                           a.nombre AS alojamiento,
                           a.ciudad,
                           ucu.nombre || ' ' || ucu.apellido AS cuidador,
                           TO_CHAR(r.fecha_ingreso, 'DD/MM/YYYY'),
                           TO_CHAR(r.fecha_salida, 'DD/MM/YYYY'),
                           r.fecha_salida - r.fecha_ingreso AS noches,
                           r.estado_reserva,
                           COALESCE(r.total_acordado, 0) AS hospedaje,
                           COALESCE(tr.transporte, 0) AS transporte,
                           COALESCE(r.total_acordado, 0)
                               + COALESCE(tr.transporte, 0) AS total_final,
                           COALESCE(pr.estados_pago, 'SIN PAGO'),
                           COALESCE(pr.monto_aprobado, 0)
                    FROM reserva r
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN especie e ON e.id_especie = m.id_especie
                    JOIN usuario uc ON uc.id_usuario = m.id_usuario
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    JOIN usuario ucu ON ucu.id_usuario = c.id_usuario
                    LEFT JOIN pagos_reserva pr
                           ON pr.id_reserva = r.id_reserva
                    LEFT JOIN transporte_reserva tr
                           ON tr.id_reserva = r.id_reserva
                    WHERE 1 = 1
                    """ + condiciones + """
                    ORDER BY r.id_reserva DESC
                    """;
        }

        private void cargarFilasReporte(PreparedStatement ps,
                                        ReporteResultado resultado)
                throws SQLException {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String estado = rs.getString(12);
                    double aprobado = rs.getDouble(17);
                    resultado.estados.put(estado,
                            resultado.estados.getOrDefault(estado, 0) + 1);
                    resultado.ingresosAprobados += aprobado;
                    resultado.filas.add(new Object[]{
                            rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6),
                            rs.getString(7), rs.getString(8), rs.getString(9),
                            rs.getString(10), rs.getInt(11), estado,
                            dineroInforme(rs.getDouble(13)),
                            dineroInforme(rs.getDouble(14)),
                            dineroInforme(rs.getDouble(15)), rs.getString(16),
                            dineroInforme(aprobado)
                    });
                }
            }
        }

        private void agregarEstadosMensuales(ReporteResultado resultado) {
            resultado.resumen.put("Solicitadas",
                    cantidadEstado(resultado, "SOLICITADA"));
            resultado.resumen.put("Aceptadas",
                    cantidadEstado(resultado, "ACEPTADA"));
            resultado.resumen.put("En curso",
                    cantidadEstado(resultado, "EN_CURSO"));
            resultado.resumen.put("Finalizadas",
                    cantidadEstado(resultado, "FINALIZADA"));
            resultado.resumen.put("Canceladas",
                    cantidadEstado(resultado, "CANCELADA"));
            resultado.resumen.put("Rechazadas",
                    cantidadEstado(resultado, "RECHAZADA"));
        }

        private String cantidadEstado(ReporteResultado resultado,
                                      String estado) {
            return String.valueOf(resultado.estados.getOrDefault(estado, 0));
        }

        private String dineroInforme(double valor) {
            return "$ " + String.format("%,.2f", valor);
        }


        Tabla consultar(String sql) {

            Tabla tabla = new Tabla();

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                ResultSetMetaData meta = rs.getMetaData();
                int columnas = meta.getColumnCount();

                tabla.columnas = new String[columnas];

                for (int i = 1; i <= columnas; i++) {
                    tabla.columnas[i - 1] = meta.getColumnLabel(i);
                }

                while (rs.next()) {

                    Object[] fila = new Object[columnas];

                    for (int i = 1; i <= columnas; i++) {
                        Object valor = rs.getObject(i);
                        fila[i - 1] = valor == null ? "-" : valor;
                    }

                    tabla.filas.add(fila);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            return tabla;
        }
    }





    private String dinero(double v) {
        return "$ " + String.format("%,.2f", v);
    }

    private String cortoDinero(double v) {

        if (v >= 1000) {
            return "$" + String.format("%.1f", v / 1000) + "k";
        }

        return "$" + String.format("%.0f", v);
    }

    private String entero(int v) {
        return String.format("%,d", v);
    }

    private String porcentaje(double v) {
        return String.format("%.1f", v) + "%";
    }

    private String corto(String t, int max) {

        if (t == null) {
            return "";
        }

        return t.length() <= max ? t : t.substring(0, max - 1) + "…";
    }

    private String texto(String valor, String defecto) {
        return valor == null || valor.isBlank() ? defecto : valor;
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

    private String fecha(Timestamp t) {
        return t == null
                ? "Sin fecha"
                : t.toLocalDateTime().format(FORMATO);
    }

    private static Color aclarar(Color c, float cantidad) {
        return new Color(
                (int) (c.getRed() + (255 - c.getRed()) * cantidad),
                (int) (c.getGreen() + (255 - c.getGreen()) * cantidad),
                (int) (c.getBlue() + (255 - c.getBlue()) * cantidad)
        );
    }

    private void cerrarSesion() {

        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
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

                    Image imagen = escalar(archivo, lado);

                    if (imagen != null) {
                        return new JLabel(new ImageIcon(imagen));
                    }
                }
            }
        }

        return new JLabel(icono(HUELLA, AZUL, lado));
    }

    private Image escalar(File archivo, int lado) {

        try {
            BufferedImage imagen = ImageIO.read(archivo);

            if (imagen == null) {
                return null;
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            while (ancho / 2 >= lado && alto / 2 >= lado) {

                ancho /= 2;
                alto /= 2;

                BufferedImage paso = new BufferedImage(
                        ancho, alto, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2 = paso.createGraphics();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(imagen, 0, 0, ancho, alto, null);
                g2.dispose();

                imagen = paso;
            }

            BufferedImage resultado = new BufferedImage(
                    lado, lado, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2 = resultado.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setClip(new RoundRectangle2D.Float(
                    0, 0, lado, lado, 13, 13));
            g2.drawImage(imagen, 0, 0, lado, lado, null);
            g2.dispose();

            return resultado;

        } catch (IOException e) {
            return null;
        }
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

            case USUARIOS -> {
                g2.drawOval(24, 16, 30, 30);
                g2.draw(new Arc2D.Float(8, 52, 62, 58,
                        0, 180, Arc2D.OPEN));
                g2.drawOval(64, 24, 22, 22);
                g2.draw(new Arc2D.Float(58, 56, 38, 32,
                        0, 150, Arc2D.OPEN));
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
                g2.drawOval(8, 8, 84, 84);
                g2.drawLine(50, 28, 50, 52);
                g2.drawLine(50, 52, 70, 64);
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

            case TABLERO -> {
                g2.fillRoundRect(8, 8, 36, 36, 8, 8);
                g2.fillRoundRect(56, 8, 36, 22, 8, 8);
                g2.fillRoundRect(56, 42, 36, 50, 8, 8);
                g2.fillRoundRect(8, 56, 36, 36, 8, 8);
            }

            case BANDEJA -> {
                g2.drawRoundRect(18, 8, 64, 84, 9, 9);
                g2.drawLine(32, 32, 68, 32);
                g2.drawLine(32, 50, 68, 50);
                g2.drawLine(32, 68, 56, 68);
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

            case REPORTE -> {
                g2.drawLine(14, 86, 14, 16);
                g2.drawLine(14, 86, 90, 86);
                g2.fillRoundRect(28, 52, 14, 34, 4, 4);
                g2.fillRoundRect(49, 34, 14, 52, 4, 4);
                g2.fillRoundRect(70, 18, 14, 68, 4, 4);
            }

            case OJO -> {
                g2.draw(new Arc2D.Float(8, 24, 84, 52,
                        0, 180, Arc2D.OPEN));
                g2.draw(new Arc2D.Float(8, 24, 84, 52,
                        180, 180, Arc2D.OPEN));
                g2.fillOval(40, 40, 20, 20);
            }

            case TELEFONO -> {
                java.awt.geom.Path2D.Float telefono =
                        new java.awt.geom.Path2D.Float();
                telefono.moveTo(21, 16);
                telefono.curveTo(14, 23, 15, 39, 25, 56);
                telefono.curveTo(35, 73, 50, 86, 64, 88);
                telefono.curveTo(73, 89, 82, 80, 84, 72);
                telefono.lineTo(64, 57);
                telefono.lineTo(53, 69);
                telefono.curveTo(44, 65, 35, 56, 31, 47);
                telefono.lineTo(43, 36);
                telefono.closePath();
                g2.fill(telefono);
            }

            case UBICACION -> {
                g2.drawOval(20, 8, 60, 60);
                g2.fillOval(42, 28, 16, 16);
                g2.drawLine(23, 52, 50, 92);
                g2.drawLine(77, 52, 50, 92);
            }

            case TARJETA -> {
                g2.drawRoundRect(8, 24, 84, 56, 12, 12);
                g2.drawOval(24, 38, 18, 18);
                g2.drawLine(56, 44, 80, 44);
                g2.drawLine(56, 60, 80, 60);
            }

            case ACTUALIZAR -> {
                g2.draw(new Arc2D.Float(15, 15, 70, 70,
                        30, 280, Arc2D.OPEN));
                g2.drawLine(76, 11, 86, 31);
                g2.drawLine(86, 31, 65, 31);
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

            case TENDENCIA -> {
                g2.drawLine(12, 78, 38, 50);
                g2.drawLine(38, 50, 56, 64);
                g2.drawLine(56, 64, 88, 24);
                g2.drawLine(88, 24, 66, 24);
                g2.drawLine(88, 24, 88, 46);
            }

            default -> {
            }
        }

        g2.dispose();

        return new ImageIcon(imagen);
    }





    private static class LienzoAdaptable extends JPanel
            implements Scrollable {

        private final int anchoMinimo;

        LienzoAdaptable(int anchoMinimo) {
            this.anchoMinimo = anchoMinimo;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension base = super.getPreferredSize();
            int ancho = Math.max(anchoMinimo, base.width);
            if (getParent() instanceof JViewport viewport) {
                ancho = Math.max(ancho, viewport.getExtentSize().width);
            }
            return new Dimension(ancho, Math.max(1, base.height));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect,
                                              int orientation,
                                              int direction) {
            return 20;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect,
                                               int orientation,
                                               int direction) {
            return orientation == SwingConstants.HORIZONTAL
                    ? Math.max(100, visibleRect.width - 100)
                    : Math.max(100, visibleRect.height - 100);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return getParent() instanceof JViewport viewport
                    && viewport.getExtentSize().width >= anchoMinimo;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
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
            Dimension dimension = new Dimension(lado + 4, lado + 4);
            setPreferredSize(dimension);
            setMinimumSize(dimension);
            setMaximumSize(dimension);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(AMARILLO);
            g2.fillOval(2, 2, lado - 1, lado - 1);

            g2.setColor(AZUL_OSCURO);
            g2.setFont(new Font(FUENTE, Font.BOLD,
                    Math.max(14, lado / 3)));

            FontMetrics m = g2.getFontMetrics();

            g2.drawString(iniciales,
                    2 + (lado - m.stringWidth(iniciales)) / 2,
                    2 + (lado - m.getHeight()) / 2 + m.getAscent());

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
            setPreferredSize(new Dimension(190, 46));
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

