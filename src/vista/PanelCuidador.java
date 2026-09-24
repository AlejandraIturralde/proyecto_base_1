

package vista;

import conexion.ConexionBD;
import modelo.Usuario;
import sesion.SesionUsuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelCuidador extends JFrame {

    private static final Color AZUL = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO = new Color(16, 62, 108);
    private static final Color AZUL_GRAD_1 = new Color(22, 104, 214);
    private static final Color AZUL_GRAD_2 = new Color(41, 137, 226);
    private static final Color FONDO = new Color(238, 246, 253);
    private static final Color BORDE = new Color(226, 236, 246);
    private static final Color GRIS = new Color(112, 133, 153);
    private static final Color GRIS_CLARO = new Color(150, 167, 182);
    private static final Color VERDE = new Color(34, 150, 105);
    private static final Color VERDE_PILL = new Color(219, 243, 232);
    private static final Color ROJO = new Color(199, 62, 62);
    private static final Color ROJO_PILL = new Color(252, 226, 226);
    private static final Color AMARILLO = new Color(242, 194, 48);
    private static final Color AMARILLO_PILL = new Color(252, 240, 205);
    private static final Color AMARILLO_TXT = new Color(150, 113, 26);
    private static final Color LATERAL = new Color(15, 57, 101);
    private static final Color LATERAL_HOVER = new Color(24, 78, 132);

    private static final String FUENTE = "Segoe UI";
    private static final String MARCA_RECHAZADO_CUIDADOR =
            "[RECHAZADO_POR_CUIDADOR]";
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Usuario usuario;
    private final Datos datos = new Datos();

    private final CardLayout layout = new CardLayout();
    private JPanel contenido;
    private final List<BotonMenu> botonesMenu = new ArrayList<>();

    private JPanel cuerpoInicio;
    private JPanel cuerpoAlojamientos;
    private JPanel cuerpoSolicitudes;
    private JPanel cuerpoReservas;
    private JPanel cuerpoEfectivo;
    private JPanel cuerpoPerfil;

    private JLabel lblPendientes;
    private JLabel lblActivas;
    private JLabel lblAlojamientos;
    private JLabel lblIngresos;

    public PanelCuidador(Usuario usuario) {
        this.usuario = usuario;

        if (!datos.esCuidadorVerificado(usuario.getIdUsuario())) {
            JOptionPane.showMessageDialog(
                    null,
                    "Esta cuenta no tiene un perfil de cuidador verificado.",
                    "Acceso no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            SwingUtilities.invokeLater(() -> {
                PanelUsuario panel = new PanelUsuario(usuario);
                panel.setVisible(true);
            });
            dispose();
            return;
        }

        configurarVentana();
        crearComponentes();
        mostrar("INICIO");
    }

    private void configurarVentana() {
        setTitle("Pet Home Boarding - Cuidador");
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

        cuerpoInicio = nuevoCuerpo();
        cuerpoAlojamientos = nuevoCuerpo();
        cuerpoSolicitudes = nuevoCuerpo();
        cuerpoReservas = nuevoCuerpo();
        cuerpoEfectivo = nuevoCuerpo();
        cuerpoPerfil = nuevoCuerpo();

        contenido.add(marco(
                "Panel del cuidador",
                "Resumen de sus alojamientos y solicitudes",
                cuerpoInicio), "INICIO");
        contenido.add(marco(
                "Mis alojamientos",
                "Solo se muestran los alojamientos asociados a su perfil",
                cuerpoAlojamientos), "ALOJAMIENTOS");
        contenido.add(marco(
                "Solicitudes recibidas",
                "Decida si puede cuidar a cada mascota en las fechas solicitadas",
                cuerpoSolicitudes), "SOLICITUDES");
        contenido.add(marco(
                "Mis reservas",
                "Reservas aceptadas, en curso y finalizadas de sus alojamientos",
                cuerpoReservas), "RESERVAS");
        contenido.add(marco(
                "Pagos en efectivo",
                "Confirme el dinero que recibió al entregarse la mascota",
                cuerpoEfectivo), "EFECTIVO");
        contenido.add(marco(
                "Mi perfil de cuidador",
                "Información vinculada a esta cuenta",
                cuerpoPerfil), "PERFIL");

        principal.add(contenido, BorderLayout.CENTER);
        setContentPane(principal);
    }

    private JPanel nuevoCuerpo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        return p;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(new EmptyBorder(14, 24, 14, 26));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        izquierda.setOpaque(false);
        izquierda.add(Login.crearLogo(44));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Pet Home Boarding");
        titulo.setFont(new Font(FUENTE, Font.BOLD, 20));
        titulo.setForeground(AZUL_OSCURO);

        JLabel subtitulo = new JLabel("Modo cuidador");
        subtitulo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        subtitulo.setForeground(GRIS);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(2));
        textos.add(subtitulo);
        izquierda.add(textos);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        derecha.setOpaque(false);

        JLabel hola = new JLabel("Hola, " + seguro(usuario.getNombre(), "Cuidador"));
        hola.setFont(new Font(FUENTE, Font.BOLD, 14));
        hola.setForeground(AZUL_OSCURO);

        JButton btnUsuario = botonSuperior("Cambiar a modo usuario", AZUL);
        btnUsuario.addActionListener(e -> cambiarAModoUsuario());

        JButton btnSalir = botonSuperior("Cerrar sesión", GRIS);
        btnSalir.addActionListener(e -> cerrarSesion());

        derecha.add(hola);
        derecha.add(btnUsuario);
        derecha.add(btnSalir);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        return barra;
    }

    private JButton botonSuperior(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font(FUENTE, Font.BOLD, 12));
        b.setForeground(color);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(8, 12, 8, 12)));
        return b;
    }

    private JPanel crearMenuLateral() {
        JPanel lateral = new JPanel();
        lateral.setBackground(LATERAL);
        lateral.setPreferredSize(new Dimension(226, 0));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBorder(new EmptyBorder(22, 14, 18, 14));

        JLabel etiqueta = new JLabel("MODO CUIDADOR");
        etiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        etiqueta.setForeground(new Color(174, 201, 225));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        etiqueta.setBorder(new EmptyBorder(0, 12, 12, 0));
        lateral.add(etiqueta);

        lateral.add(menu("Inicio", "INICIO"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(menu("Mis alojamientos", "ALOJAMIENTOS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(menu("Solicitudes", "SOLICITUDES"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(menu("Mis reservas", "RESERVAS"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(menu("Pagos en efectivo", "EFECTIVO"));
        lateral.add(Box.createVerticalStrut(4));
        lateral.add(menu("Mi perfil", "PERFIL"));
        lateral.add(Box.createVerticalGlue());

        JLabel pie = new JLabel(
                "<html><b style='color:#FFFFFF;'>Pet Home Boarding</b>"
                        + "<br><span style='color:#AEC9E1;'>"
                        + "Gestión de cuidador</span></html>");
        pie.setFont(new Font(FUENTE, Font.PLAIN, 11));
        pie.setAlignmentX(Component.LEFT_ALIGNMENT);
        pie.setBorder(new EmptyBorder(12, 12, 0, 0));
        lateral.add(pie);

        return lateral;
    }

    private BotonMenu menu(String texto, String seccion) {
        BotonMenu b = new BotonMenu(texto, seccion);
        b.addActionListener(e -> mostrar(seccion));
        botonesMenu.add(b);
        return b;
    }

    private JPanel marco(String titulo, String nota, JPanel cuerpo) {
        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setOpaque(false);
        fondo.setBorder(new EmptyBorder(22, 26, 22, 26));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font(FUENTE, Font.BOLD, 25));
        t.setForeground(AZUL_OSCURO);
        JLabel n = new JLabel(nota);
        n.setFont(new Font(FUENTE, Font.PLAIN, 13));
        n.setForeground(GRIS);
        textos.add(t);
        textos.add(Box.createVerticalStrut(4));
        textos.add(n);

        JButton actualizar = botonSuperior("Actualizar", AZUL);
        actualizar.addActionListener(e -> recargarActual());

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(actualizar, BorderLayout.EAST);
        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(cuerpo, BorderLayout.CENTER);
        return fondo;
    }

    private void mostrar(String seccion) {
        layout.show(contenido, seccion);
        for (BotonMenu b : botonesMenu) {
            b.setActivo(b.seccion.equals(seccion));
        }

        switch (seccion) {
            case "INICIO" -> cargarInicio();
            case "ALOJAMIENTOS" -> cargarAlojamientos();
            case "SOLICITUDES" -> cargarSolicitudes();
            case "RESERVAS" -> cargarReservas();
            case "EFECTIVO" -> cargarEfectivo();
            case "PERFIL" -> cargarPerfil();
            default -> { }
        }
    }

    private void recargarActual() {
        for (BotonMenu b : botonesMenu) {
            if (b.activo) {
                mostrar(b.seccion);
                return;
            }
        }
        mostrar("INICIO");
    }





    private void cargarInicio() {
        cuerpoInicio.removeAll();
        try {
            Object[] resumen = datos.resumen(usuario.getIdUsuario());

            JPanel tarjetas = new JPanel(new GridLayout(1, 4, 14, 14));
            tarjetas.setOpaque(false);

            lblPendientes = valorGrande(String.valueOf(resumen[0]), AMARILLO_TXT);
            lblActivas = valorGrande(String.valueOf(resumen[1]), AZUL);
            lblAlojamientos = valorGrande(String.valueOf(resumen[2]), VERDE);
            lblIngresos = valorGrande(dinero((double) resumen[3]), AZUL_OSCURO);

            tarjetas.add(tarjetaResumen("Solicitudes pendientes", lblPendientes,
                    "Esperan su decisión"));
            tarjetas.add(tarjetaResumen("Reservas activas", lblActivas,
                    "Aceptadas o en curso"));
            tarjetas.add(tarjetaResumen("Mis alojamientos", lblAlojamientos,
                    "Asociados a su cuenta"));
            tarjetas.add(tarjetaResumen("Pagos aprobados", lblIngresos,
                    "De sus reservas"));

            JPanel cont = new JPanel(new BorderLayout(0, 18));
            cont.setOpaque(false);
            cont.add(tarjetas, BorderLayout.NORTH);

            List<Object[]> ultimas = datos.solicitudes(usuario.getIdUsuario(), 3);
            JPanel recientes = new JPanel();
            recientes.setLayout(new BoxLayout(recientes, BoxLayout.Y_AXIS));
            recientes.setOpaque(false);

            JLabel titulo = new JLabel("Solicitudes más recientes");
            titulo.setFont(new Font(FUENTE, Font.BOLD, 18));
            titulo.setForeground(AZUL_OSCURO);
            titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
            recientes.add(titulo);
            recientes.add(Box.createVerticalStrut(10));

            if (ultimas.isEmpty()) {
                recientes.add(vacio("No tiene solicitudes pendientes."));
            } else {
                for (Object[] s : ultimas) {
                    recientes.add(tarjetaSolicitud(s, true));
                    recientes.add(Box.createVerticalStrut(10));
                }
            }

            cont.add(desplazable(recientes), BorderLayout.CENTER);
            cuerpoInicio.add(cont, BorderLayout.CENTER);

        } catch (RuntimeException e) {
            cuerpoInicio.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoInicio);
    }

    private JLabel valorGrande(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FUENTE, Font.BOLD, 25));
        l.setForeground(color);
        return l;
    }

    private JPanel tarjetaResumen(String titulo, JLabel valor, String detalle) {
        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(18, 18, 18, 18));
        caja.setPreferredSize(new Dimension(220, 120));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font(FUENTE, Font.BOLD, 12));
        t.setForeground(GRIS);
        JLabel d = new JLabel(detalle);
        d.setFont(new Font(FUENTE, Font.PLAIN, 11));
        d.setForeground(GRIS_CLARO);

        caja.add(t);
        caja.add(Box.createVerticalStrut(8));
        caja.add(valor);
        caja.add(Box.createVerticalStrut(5));
        caja.add(d);
        return caja;
    }





    private void cargarAlojamientos() {
        cuerpoAlojamientos.removeAll();

        JPanel encabezado = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 0, 0));
        encabezado.setOpaque(false);
        encabezado.setBorder(new EmptyBorder(0, 0, 14, 0));

        BotonRedondeado agregar = new BotonRedondeado(
                "+ Agregar alojamiento", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        agregar.setPreferredSize(new Dimension(205, 42));
        agregar.addActionListener(e -> formularioAlojamiento(null));
        encabezado.add(agregar);
        cuerpoAlojamientos.add(encabezado, BorderLayout.NORTH);

        try {
            List<Object[]> lista = datos.alojamientos(usuario.getIdUsuario());
            if (lista.isEmpty()) {
                cuerpoAlojamientos.add(vacio(
                                "Todavía no tiene alojamientos asociados a su perfil."),
                        BorderLayout.CENTER);
            } else {
                JPanel rejilla = new JPanel(new GridLayout(0, 2, 16, 16));
                rejilla.setOpaque(false);
                for (Object[] a : lista) {
                    rejilla.add(tarjetaAlojamiento(a));
                }
                cuerpoAlojamientos.add(desplazable(rejilla), BorderLayout.CENTER);
            }
        } catch (RuntimeException e) {
            cuerpoAlojamientos.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoAlojamientos);
    }

    private JPanel tarjetaAlojamiento(Object[] a) {
        int id = (int) a[0];
        String nombre = (String) a[1];
        String tipo = (String) a[2];
        String ciudad = (String) a[3];
        String direccion = (String) a[4];
        String descripcion = (String) a[5];
        int capacidad = (int) a[6];
        double precio = (double) a[7];
        boolean transporte = (boolean) a[8];
        boolean activo = (boolean) a[9];

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new BorderLayout(0, 10));
        caja.setBorder(new EmptyBorder(14, 18, 16, 18));
        caja.setPreferredSize(new Dimension(430, 385));

        JLabel foto = cargarImagen(ImagenAlojamientoUtil.principal(nombre, datos.fotoPrincipal(id)), 390, 150);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.add(pastilla(tipo, AMARILLO_PILL, AMARILLO_TXT));
        fila.add(pastilla(activo ? "PUBLICADO" : "NO PUBLICADO",
                activo ? VERDE_PILL : ROJO_PILL,
                activo ? VERDE : ROJO));

        JLabel n = new JLabel(nombre);
        n.setFont(new Font(FUENTE, Font.BOLD, 19));
        n.setForeground(AZUL_OSCURO);
        n.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ubi = new JLabel(ciudad + "  ·  " + direccion);
        ubi.setFont(new Font(FUENTE, Font.PLAIN, 12));
        ubi.setForeground(GRIS);
        ubi.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("<html><div style='width:380px;'>"
                + escapar(corto(seguro(descripcion, "Sin descripción"), 150))
                + "</div></html>");
        desc.setFont(new Font(FUENTE, Font.PLAIN, 12));
        desc.setForeground(GRIS);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(fila);
        centro.add(Box.createVerticalStrut(8));
        centro.add(n);
        centro.add(Box.createVerticalStrut(4));
        centro.add(ubi);
        centro.add(Box.createVerticalStrut(7));
        centro.add(desc);

        JPanel abajo = new JPanel();
        abajo.setLayout(new BoxLayout(abajo, BoxLayout.Y_AXIS));
        abajo.setOpaque(false);

        JPanel economia = new JPanel(new BorderLayout());
        economia.setOpaque(false);
        economia.setAlignmentX(Component.LEFT_ALIGNMENT);
        economia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel p = new JLabel(dinero(precio) + " por noche");
        p.setFont(new Font(FUENTE, Font.BOLD, 17));
        p.setForeground(AZUL_OSCURO);
        JLabel info = new JLabel("Cupo " + capacidad
                + (transporte ? "  ·  Ofrece transporte" : ""));
        info.setFont(new Font(FUENTE, Font.PLAIN, 11));
        info.setForeground(GRIS_CLARO);
        economia.add(p, BorderLayout.WEST);
        economia.add(info, BorderLayout.EAST);

        JPanel acciones = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        acciones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton editar = botonAccionAlojamiento(
                "Editar alojamiento", AZUL);
        editar.addActionListener(e -> formularioAlojamiento(id));

        JButton estado = botonAccionAlojamiento(
                activo ? "Desactivar alojamiento"
                        : "Publicar alojamiento",
                activo ? ROJO : VERDE);
        estado.addActionListener(e -> cambiarPublicacion(
                id, activo));

        acciones.add(editar);
        acciones.add(estado);

        abajo.add(economia);
        abajo.add(Box.createVerticalStrut(8));
        abajo.add(acciones);

        caja.add(foto, BorderLayout.NORTH);
        caja.add(centro, BorderLayout.CENTER);
        caja.add(abajo, BorderLayout.SOUTH);
        return caja;
    }





    private void cargarSolicitudes() {
        cuerpoSolicitudes.removeAll();
        try {
            List<Object[]> lista = datos.solicitudes(usuario.getIdUsuario(), 0);
            if (lista.isEmpty()) {
                cuerpoSolicitudes.add(vacio(
                                "No tiene solicitudes pendientes en este momento."),
                        BorderLayout.CENTER);
            } else {
                JPanel columna = new JPanel();
                columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
                columna.setOpaque(false);
                for (Object[] s : lista) {
                    columna.add(tarjetaSolicitud(s, false));
                    columna.add(Box.createVerticalStrut(12));
                }
                cuerpoSolicitudes.add(desplazable(columna), BorderLayout.CENTER);
            }
        } catch (RuntimeException e) {
            cuerpoSolicitudes.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoSolicitudes);
    }

    private JPanel tarjetaSolicitud(Object[] s, boolean compacta) {
        int idReserva = (int) s[0];
        String mascota = (String) s[1];
        String especie = (String) s[2];
        String raza = (String) s[3];
        double peso = (double) s[4];
        String propietario = (String) s[5];
        String telefono = (String) s[6];
        String email = (String) s[7];
        String alojamiento = (String) s[8];
        Date ingreso = (Date) s[9];
        Date salida = (Date) s[10];
        String mensaje = (String) s[11];
        String instrucciones = (String) s[12];
        double total = (double) s[13];

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new BorderLayout(18, 0));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, compacta ? 170 : 230));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        titulo.setOpaque(false);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel nom = new JLabel(mascota + "  ·  " + especie
                + "  ·  " + seguro(raza, "Sin raza"));
        nom.setFont(new Font(FUENTE, Font.BOLD, 17));
        nom.setForeground(AZUL_OSCURO);
        titulo.add(nom);
        titulo.add(pastilla("SOLICITADA", AMARILLO_PILL, AMARILLO_TXT));

        JLabel reserva = new JLabel("Reserva #" + idReserva);
        reserva.setFont(new Font(FUENTE, Font.BOLD, 12));
        reserva.setForeground(AZUL);
        reserva.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fecha = new JLabel(
                ingreso.toLocalDate().format(FECHA) + "  →  "
                        + salida.toLocalDate().format(FECHA)
                        + "     ·     " + alojamiento
                        + "     ·     " + String.format("%.2f kg", peso));
        fecha.setFont(new Font(FUENTE, Font.PLAIN, 12));
        fecha.setForeground(GRIS);
        fecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel duenio = new JLabel("Propietario: " + propietario
                + "  ·  " + seguro(telefono, "Sin teléfono")
                + "  ·  " + seguro(email, "Sin correo"));
        duenio.setFont(new Font(FUENTE, Font.PLAIN, 12));
        duenio.setForeground(GRIS);
        duenio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel msg = new JLabel("<html><b>Mensaje:</b> "
                + escapar(seguro(mensaje, "Sin mensaje")) + "</html>");
        msg.setFont(new Font(FUENTE, Font.PLAIN, 12));
        msg.setForeground(AZUL_OSCURO);
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel inst = new JLabel("<html><b>Cuidados:</b> "
                + escapar(seguro(instrucciones, "Sin instrucciones adicionales"))
                + "</html>");
        inst.setFont(new Font(FUENTE, Font.PLAIN, 12));
        inst.setForeground(AZUL_OSCURO);
        inst.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel monto = new JLabel("Total de la reserva: " + dinero(total));
        monto.setFont(new Font(FUENTE, Font.BOLD, 12));
        monto.setForeground(AZUL);
        monto.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(reserva);
        textos.add(Box.createVerticalStrut(4));
        textos.add(fecha);
        textos.add(Box.createVerticalStrut(5));
        textos.add(duenio);
        if (!compacta) {
            textos.add(Box.createVerticalStrut(8));
            textos.add(msg);
            textos.add(Box.createVerticalStrut(4));
            textos.add(inst);
        }
        textos.add(Box.createVerticalStrut(6));
        textos.add(monto);

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));

        BotonRedondeado aceptar = new BotonRedondeado(
                "Aceptar", AZUL_GRAD_1, Color.WHITE, null, AZUL_GRAD_2);
        aceptar.setPreferredSize(new Dimension(145, 40));
        aceptar.setMaximumSize(new Dimension(145, 40));
        aceptar.setAlignmentX(Component.CENTER_ALIGNMENT);
        aceptar.addActionListener(e -> resolverReserva(idReserva, true));

        BotonRedondeado rechazar = new BotonRedondeado(
                "Rechazar", Color.WHITE, ROJO, ROJO, null);
        rechazar.setPreferredSize(new Dimension(145, 40));
        rechazar.setMaximumSize(new Dimension(145, 40));
        rechazar.setAlignmentX(Component.CENTER_ALIGNMENT);
        rechazar.addActionListener(e -> resolverReserva(idReserva, false));

        acciones.add(aceptar);
        acciones.add(Box.createVerticalStrut(8));
        acciones.add(rechazar);

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.setPreferredSize(new Dimension(155, 0));
        derecha.add(acciones, BorderLayout.NORTH);

        caja.add(textos, BorderLayout.CENTER);
        caja.add(derecha, BorderLayout.EAST);
        return caja;
    }

    private void resolverReserva(int idReserva, boolean aceptar) {
        String accion = aceptar ? "aceptar" : "rechazar";
        int r = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que desea " + accion + " esta solicitud?",
                aceptar ? "Aceptar reserva" : "Rechazar reserva",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (r != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (aceptar) {
                datos.aceptarReserva(idReserva, usuario.getIdUsuario());
                JOptionPane.showMessageDialog(
                        this,
                        "Reserva aceptada.\nEl usuario podrá verla como ACEPTADA.",
                        "Reserva aceptada",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                datos.rechazarReserva(idReserva, usuario.getIdUsuario());
                JOptionPane.showMessageDialog(
                        this,
                        "Solicitud rechazada.",
                        "Reserva rechazada",
                        JOptionPane.INFORMATION_MESSAGE);
            }
            cargarSolicitudes();
            cargarInicio();
        } catch (RuntimeException e) {
            aviso(e.getMessage());
        }
    }





    private void cargarReservas() {
        cuerpoReservas.removeAll();
        try {
            List<Object[]> lista = datos.reservas(usuario.getIdUsuario());
            if (lista.isEmpty()) {
                cuerpoReservas.add(vacio(
                                "Todavía no tiene reservas procesadas."),
                        BorderLayout.CENTER);
            } else {
                JPanel columna = new JPanel();
                columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
                columna.setOpaque(false);
                for (Object[] r : lista) {
                    columna.add(tarjetaReserva(r));
                    columna.add(Box.createVerticalStrut(12));
                }
                cuerpoReservas.add(desplazable(columna), BorderLayout.CENTER);
            }
        } catch (RuntimeException e) {
            cuerpoReservas.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoReservas);
    }

    private JPanel tarjetaReserva(Object[] r) {
        int id = (int) r[0];
        String mascota = (String) r[1];
        String propietario = (String) r[2];
        String alojamiento = (String) r[3];
        Date ingreso = (Date) r[4];
        Date salida = (Date) r[5];
        String estado = (String) r[6];
        double totalAlojamiento = (double) r[7];
        double pagado = (double) r[8];
        String estadoTransporte = (String) r[9];
        double precioTransporte = (double) r[10];
        boolean ofreceTransporte = (boolean) r[11];
        Timestamp fechaHoraEntrega = (Timestamp) r[12];
        boolean puedeEntregar = (boolean) r[13];

        boolean transporteCotizado =
                ("PROGRAMADO".equals(estadoTransporte)
                        || "FINALIZADO".equals(estadoTransporte))
                        && precioTransporte > 0.009;
        double totalFinal = totalAlojamiento
                + (transporteCotizado ? precioTransporte : 0);

        String detalleTransporte;
        if (!ofreceTransporte) {
            detalleTransporte = "No disponible";
        } else if (estadoTransporte == null) {
            detalleTransporte = "Pendiente de decisión";
        } else if ("PENDIENTE".equals(estadoTransporte)) {
            detalleTransporte = "Pendiente de cotización";
        } else if ("CANCELADO".equals(estadoTransporte)) {
            detalleTransporte = "Cancelado";
        } else {
            detalleTransporte = dinero(precioTransporte);
        }

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new BorderLayout());
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 158));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("Reserva #" + id + "  ·  " + mascota
                + " en " + alojamiento);
        titulo.setFont(new Font(FUENTE, Font.BOLD, 16));
        titulo.setForeground(AZUL_OSCURO);
        fila.add(titulo);
        fila.add(pastillaEstado(estado));

        JLabel fechas = new JLabel(ingreso.toLocalDate().format(FECHA)
                + "  →  " + salida.toLocalDate().format(FECHA)
                + "     ·     Propietario: " + propietario);
        fechas.setFont(new Font(FUENTE, Font.PLAIN, 12));
        fechas.setForeground(GRIS);
        fechas.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel pagos = new JLabel("<html>Hospedaje "
                + dinero(totalAlojamiento)
                + "  ·  Transporte: " + detalleTransporte
                + "  ·  Total final " + dinero(totalFinal)
                + "  ·  Pagado " + dinero(pagado) + "</html>");
        pagos.setFont(new Font(FUENTE, Font.BOLD, 12));
        pagos.setForeground(AZUL);
        pagos.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(fila);
        textos.add(Box.createVerticalStrut(7));
        textos.add(fechas);
        textos.add(Box.createVerticalStrut(7));
        textos.add(pagos);

        caja.add(textos, BorderLayout.CENTER);

        boolean puedeCotizar = "PENDIENTE".equals(estadoTransporte)
                && ("ACEPTADA".equals(estado)
                || "EN_CURSO".equals(estado));

        if (puedeCotizar || puedeEntregar) {
            JPanel acciones = new JPanel();
            acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
            acciones.setOpaque(false);

            if (puedeCotizar) {
                BotonRedondeado cotizar = new BotonRedondeado(
                        "Cotizar transporte", AZUL_GRAD_1, Color.WHITE,
                        null, AZUL_GRAD_2);
                cotizar.setPreferredSize(new Dimension(190, 42));
                cotizar.setMaximumSize(new Dimension(190, 42));
                cotizar.setAlignmentX(Component.CENTER_ALIGNMENT);
                cotizar.addActionListener(e -> formularioCotizacion(id));
                acciones.add(cotizar);
            }

            if (puedeEntregar) {
                if (puedeCotizar) {
                    acciones.add(Box.createVerticalStrut(8));
                }
                BotonRedondeado entregar = new BotonRedondeado(
                        "Entregar mascota", VERDE, Color.WHITE,
                        null, null);
                entregar.setPreferredSize(new Dimension(190, 42));
                entregar.setMaximumSize(new Dimension(190, 42));
                entregar.setAlignmentX(Component.CENTER_ALIGNMENT);
                String momento = fechaHoraEntrega == null
                        ? salida.toLocalDate().format(FECHA)
                        : fechaHoraEntrega.toLocalDateTime()
                          .format(FECHA_HORA);
                entregar.setToolTipText("Entrega habilitada desde " + momento);
                entregar.addActionListener(e -> entregarMascota(id));
                acciones.add(entregar);
            }

            JPanel columnaAcciones = new JPanel(new BorderLayout());
            columnaAcciones.setOpaque(false);
            columnaAcciones.setPreferredSize(new Dimension(205, 0));
            columnaAcciones.add(acciones, BorderLayout.NORTH);
            caja.add(columnaAcciones, BorderLayout.EAST);
        }

        return caja;
    }

    private void entregarMascota(int idReserva) {
        try {
            String impedimento = datos.impedimentoEntrega(
                    idReserva, usuario.getIdUsuario());
            if (impedimento != null) {
                aviso(impedimento);
                cargarReservas();
                return;
            }
        } catch (RuntimeException e) {
            aviso(e.getMessage());
            return;
        }

        Object[] opciones = {"Cancelar", "Confirmar entrega"};
        int respuesta = JOptionPane.showOptionDialog(this,
                "¿Confirmas que la mascota fue entregada a su propietario?",
                "Entregar mascota", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[1]);

        if (respuesta != 1) {
            return;
        }

        try {
            datos.entregarMascota(idReserva, usuario.getIdUsuario());
            JOptionPane.showMessageDialog(this,
                    "La mascota fue entregada y la reserva finalizó.",
                    "Entrega confirmada",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarReservas();
            cargarInicio();
        } catch (RuntimeException e) {
            aviso(e.getMessage());
            cargarReservas();
        }
    }

    private JButton botonAccionAlojamiento(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font(FUENTE, Font.BOLD, 11));
        boton.setForeground(color);
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                new EmptyBorder(7, 10, 7, 10)));
        return boton;
    }

    private void cambiarPublicacion(int idAlojamiento,
                                    boolean actualmenteActivo) {
        String accion = actualmenteActivo ? "desactivar" : "publicar";
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Desea " + accion + " este alojamiento?",
                actualmenteActivo ? "Desactivar alojamiento"
                        : "Publicar alojamiento",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (actualmenteActivo) {
                datos.despublicarAlojamiento(idAlojamiento,
                        usuario.getIdUsuario());
            } else {
                datos.publicarAlojamiento(idAlojamiento,
                        usuario.getIdUsuario());
            }
            cargarAlojamientos();
            cargarInicio();
        } catch (RuntimeException e) {
            aviso(e.getMessage());
        }
    }

    private void formularioAlojamiento(Integer idAlojamiento) {
        Object[] actual = null;
        List<Object[]> categorias;

        try {
            if (idAlojamiento != null) {
                actual = datos.alojamientoPropio(idAlojamiento,
                        usuario.getIdUsuario());
                if (actual == null) {
                    aviso("El alojamiento no existe o no le pertenece.");
                    return;
                }
            }
            categorias = datos.categoriasConfigurables(
                    idAlojamiento == null ? 0 : idAlojamiento);
        } catch (RuntimeException e) {
            aviso(e.getMessage());
            return;
        }

        final Object[] datosActuales = actual;
        JDialog dialogo = new JDialog(this,
                idAlojamiento == null ? "Agregar alojamiento"
                        : "Editar alojamiento", true);
        dialogo.setSize(760, 760);
        dialogo.setLocationRelativeTo(this);

        JTextField txtNombre = nuevoCampo();
        JTextField txtTipo = nuevoCampo();
        JTextArea txtDescripcion = nuevaArea();
        JTextField txtProvincia = nuevoCampo();
        JTextField txtCiudad = nuevoCampo();
        JTextField txtDireccion = nuevoCampo();
        JTextField txtReferencia = nuevoCampo();
        JTextField txtPrecio = nuevoCampo();
        JCheckBox chkPatio = new JCheckBox("Tiene patio");
        JCheckBox chkCerramiento = new JCheckBox("Tiene cerramiento");
        JCheckBox chkConvive = new JCheckBox("Convive con mascotas");
        JCheckBox chkTransporte = new JCheckBox("Ofrece transporte");
        JTextArea txtReglas = nuevaArea();

        for (JCheckBox check : new JCheckBox[]{chkPatio,
                chkCerramiento, chkConvive, chkTransporte}) {
            check.setOpaque(false);
            check.setFont(new Font(FUENTE, Font.BOLD, 12));
            check.setForeground(AZUL_OSCURO);
        }

        if (datosActuales != null) {
            txtNombre.setText(seguro((String) datosActuales[0], ""));
            txtTipo.setText(seguro((String) datosActuales[1], ""));
            txtDescripcion.setText(seguro(
                    (String) datosActuales[2], ""));
            txtProvincia.setText(seguro(
                    (String) datosActuales[3], ""));
            txtCiudad.setText(seguro((String) datosActuales[4], ""));
            txtDireccion.setText(seguro(
                    (String) datosActuales[5], ""));
            txtReferencia.setText(seguro(
                    (String) datosActuales[6], ""));
            txtPrecio.setText(String.valueOf(datosActuales[8]));
            chkPatio.setSelected((boolean) datosActuales[9]);
            chkCerramiento.setSelected((boolean) datosActuales[10]);
            chkConvive.setSelected((boolean) datosActuales[11]);
            txtReglas.setText(seguro((String) datosActuales[12], ""));
            chkTransporte.setSelected((boolean) datosActuales[13]);
        }

        JPanel datosGenerales = new JPanel();
        datosGenerales.setLayout(new BoxLayout(
                datosGenerales, BoxLayout.Y_AXIS));
        datosGenerales.setBackground(Color.WHITE);
        datosGenerales.setBorder(new EmptyBorder(20, 24, 20, 24));
        datosGenerales.add(bloqueFormulario("Nombre", txtNombre));
        datosGenerales.add(Box.createVerticalStrut(10));
        datosGenerales.add(bloqueFormulario(
                "Tipo de alojamiento", txtTipo));
        datosGenerales.add(Box.createVerticalStrut(10));
        datosGenerales.add(areaFormulario("Descripción",
                txtDescripcion, 76));
        datosGenerales.add(Box.createVerticalStrut(10));

        JPanel ubicacion = new JPanel(new GridLayout(0, 2, 12, 10));
        ubicacion.setOpaque(false);
        ubicacion.setAlignmentX(Component.LEFT_ALIGNMENT);
        ubicacion.add(bloqueFormulario("Provincia", txtProvincia));
        ubicacion.add(bloqueFormulario("Ciudad", txtCiudad));
        ubicacion.add(bloqueFormulario("Dirección", txtDireccion));
        ubicacion.add(bloqueFormulario("Referencia", txtReferencia));
        ubicacion.add(bloqueFormulario("Precio por noche", txtPrecio));
        datosGenerales.add(ubicacion);
        datosGenerales.add(Box.createVerticalStrut(12));

        JPanel opciones = new JPanel(new GridLayout(2, 2, 10, 8));
        opciones.setOpaque(false);
        opciones.setAlignmentX(Component.LEFT_ALIGNMENT);
        opciones.add(chkPatio);
        opciones.add(chkCerramiento);
        opciones.add(chkConvive);
        opciones.add(chkTransporte);
        datosGenerales.add(opciones);
        datosGenerales.add(Box.createVerticalStrut(10));
        datosGenerales.add(areaFormulario("Reglas generales",
                txtReglas, 76));

        JPanel configuracion = new JPanel();
        configuracion.setLayout(new BoxLayout(
                configuracion, BoxLayout.Y_AXIS));
        configuracion.setBackground(Color.WHITE);
        configuracion.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel lblCategorias = new JLabel("Mascotas aceptadas");
        lblCategorias.setFont(new Font(FUENTE, Font.BOLD, 17));
        lblCategorias.setForeground(AZUL_OSCURO);
        lblCategorias.setAlignmentX(Component.LEFT_ALIGNMENT);
        configuracion.add(lblCategorias);
        configuracion.add(Box.createVerticalStrut(10));

        List<Object[]> controlesCategoria = new ArrayList<>();
        if (categorias.isEmpty()) {
            configuracion.add(new JLabel(
                    "No existen especies/categorías configuradas en la base."));
        } else {
            for (Object[] categoria : categorias) {
                int idCategoria = (int) categoria[0];
                String especie = (String) categoria[1];
                String nombreCategoria = (String) categoria[2];
                boolean seleccionada = (boolean) categoria[3];
                int maximo = categoria[4] == null
                        ? 1 : (int) categoria[4];

                String etiqueta = "UNICA".equalsIgnoreCase(
                        nombreCategoria) ? especie
                        : especie + " - " + nombreCategoria;

                JCheckBox check = new JCheckBox(etiqueta, seleccionada);
                check.setOpaque(false);
                check.setFont(new Font(FUENTE, Font.BOLD, 12));
                check.setForeground(AZUL_OSCURO);

                JSpinner cantidad = new JSpinner(
                        new SpinnerNumberModel(maximo, 1, 999, 1));
                configurarSpinner(cantidad);
                cantidad.setEnabled(seleccionada);
                check.addActionListener(e -> cantidad.setEnabled(
                        check.isSelected()));

                JPanel filaCategoria = new JPanel(new BorderLayout(12, 0));
                filaCategoria.setOpaque(false);
                filaCategoria.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        new EmptyBorder(8, 10, 8, 10)));
                filaCategoria.setAlignmentX(Component.LEFT_ALIGNMENT);
                filaCategoria.setMaximumSize(
                        new Dimension(Integer.MAX_VALUE, 48));

                JLabel lblMax = new JLabel("Cantidad máxima");
                lblMax.setFont(new Font(FUENTE, Font.PLAIN, 11));
                lblMax.setForeground(GRIS);
                JPanel limite = new JPanel(new FlowLayout(
                        FlowLayout.RIGHT, 8, 0));
                limite.setOpaque(false);
                limite.add(lblMax);
                limite.add(cantidad);

                filaCategoria.add(check, BorderLayout.CENTER);
                filaCategoria.add(limite, BorderLayout.EAST);
                configuracion.add(filaCategoria);
                configuracion.add(Box.createVerticalStrut(6));
                controlesCategoria.add(new Object[]{
                        idCategoria, check, cantidad
                });
            }
        }

        configuracion.add(Box.createVerticalStrut(14));
        JLabel lblFoto = new JLabel("Foto principal");
        lblFoto.setFont(new Font(FUENTE, Font.BOLD, 17));
        lblFoto.setForeground(AZUL_OSCURO);
        lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        configuracion.add(lblFoto);
        configuracion.add(Box.createVerticalStrut(8));

        String[] fotoNueva = {null};
        String fotoActual = datosActuales == null
                ? null : (String) datosActuales[14];
        JPanel vistaFoto = new JPanel(new GridBagLayout());
        vistaFoto.setOpaque(false);
        vistaFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        vistaFoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        Runnable refrescarFoto = () -> {
            vistaFoto.removeAll();
            String ruta = fotoNueva[0] == null
                    ? fotoActual : fotoNueva[0];
            vistaFoto.add(cargarImagen(ruta, 300, 150));
            vistaFoto.revalidate();
            vistaFoto.repaint();
        };

        JButton elegirFoto = botonAccionAlojamiento(
                "Seleccionar nueva foto", AZUL);
        elegirFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        elegirFoto.addActionListener(e -> {
            JFileChooser selector = new JFileChooser();
            selector.setDialogTitle("Seleccionar foto del alojamiento");
            selector.setFileFilter(new javax.swing.filechooser
                    .FileNameExtensionFilter(
                    "Imágenes (JPG, PNG)", "jpg", "jpeg", "png"));
            if (selector.showOpenDialog(dialogo)
                    == JFileChooser.APPROVE_OPTION) {
                fotoNueva[0] = selector.getSelectedFile()
                        .getAbsolutePath();
                refrescarFoto.run();
            }
        });

        configuracion.add(vistaFoto);
        configuracion.add(Box.createVerticalStrut(8));
        configuracion.add(elegirFoto);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(new Font(FUENTE, Font.BOLD, 13));
        pestanas.addTab("Datos del alojamiento",
                desplazable(datosGenerales));
        pestanas.addTab("Mascotas y foto",
                desplazable(configuracion));

        BotonRedondeado guardar = new BotonRedondeado(
                idAlojamiento == null ? "Crear alojamiento"
                        : "Guardar cambios",
                AZUL_GRAD_1, Color.WHITE, null, AZUL_GRAD_2);
        guardar.setPreferredSize(new Dimension(190, 44));

        guardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            String tipo = txtTipo.getText().trim();
            String ciudad = txtCiudad.getText().trim();
            String direccion = txtDireccion.getText().trim();

            if (nombre.isBlank() || tipo.isBlank()
                    || ciudad.isBlank() || direccion.isBlank()) {
                aviso("Complete nombre, tipo, ciudad y dirección.");
                return;
            }

            BigDecimal precio;
            try {
                precio = new BigDecimal(txtPrecio.getText().trim()
                        .replace(',', '.')).setScale(2,
                        RoundingMode.HALF_UP);
            } catch (NumberFormatException ex) {
                aviso("Ingrese un precio por noche válido.");
                return;
            }

            if (precio.signum() < 0) {
                aviso("El precio por noche no puede ser negativo.");
                return;
            }

            List<Object[]> seleccionadas = new ArrayList<>();
            int capacidadTotal = 0;
            for (Object[] control : controlesCategoria) {
                JCheckBox check = (JCheckBox) control[1];
                if (check.isSelected()) {
                    int cantidad = ((Number) ((JSpinner) control[2])
                            .getValue()).intValue();
                    if (cantidad <= 0) {
                        aviso("Cada cantidad máxima debe ser mayor que cero.");
                        pestanas.setSelectedIndex(1);
                        return;
                    }
                    seleccionadas.add(new Object[]{control[0], cantidad});
                    capacidadTotal += cantidad;
                }
            }

            if (seleccionadas.isEmpty()) {
                aviso("Configura al menos una mascota aceptada antes "
                        + "de guardar el alojamiento.");
                pestanas.setSelectedIndex(1);
                return;
            }

            AlojamientoEdicion edicion = new AlojamientoEdicion();
            edicion.idAlojamiento = idAlojamiento;
            edicion.nombre = nombre;
            edicion.tipo = tipo;
            edicion.descripcion = txtDescripcion.getText().trim();
            edicion.provincia = txtProvincia.getText().trim();
            edicion.ciudad = ciudad;
            edicion.direccion = direccion;
            edicion.referencia = txtReferencia.getText().trim();
            edicion.capacidad = capacidadTotal;
            edicion.precio = precio;
            edicion.patio = chkPatio.isSelected();
            edicion.cerramiento = chkCerramiento.isSelected();
            edicion.convive = chkConvive.isSelected();
            edicion.reglas = txtReglas.getText().trim();
            edicion.transporte = chkTransporte.isSelected();
            edicion.categorias = seleccionadas;
            edicion.fotoNueva = fotoNueva[0];

            try {
                datos.guardarAlojamiento(edicion,
                        usuario.getIdUsuario());
                dialogo.dispose();
                cargarAlojamientos();
                cargarInicio();
            } catch (RuntimeException ex) {
                aviso(ex.getMessage());
            }
        });

        JPanel pie = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE));
        pie.add(guardar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(pestanas, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(contenedor);
        refrescarFoto.run();
        dialogo.setVisible(true);
    }

    private JTextField nuevoCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(250, 38));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return campo;
    }

    private JTextArea nuevaArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(FUENTE, Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private void configurarSpinner(JSpinner spinner) {
        spinner.setFont(new Font(FUENTE, Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(75, 34));
    }

    private JPanel bloqueFormulario(String titulo, JComponent campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(new Font(FUENTE, Font.BOLD, 11));
        etiqueta.setForeground(GRIS);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(4));
        panel.add(campo);
        return panel;
    }

    private JPanel areaFormulario(String titulo, JTextArea area,
                                  int alto) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(400, alto));
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
        return bloqueFormulario(titulo, scroll);
    }

    private void formularioCotizacion(int idReserva) {
        Object[] t;

        try {
            t = datos.transportePendiente(idReserva,
                    usuario.getIdUsuario());
        } catch (RuntimeException e) {
            aviso(e.getMessage());
            return;
        }

        if (t == null) {
            aviso("La solicitud ya no está pendiente o no pertenece "
                    + "a uno de sus alojamientos.");
            cargarReservas();
            return;
        }

        JDialog dialogo = new JDialog(this,
                "Cotizar transporte", true);
        dialogo.setSize(560, 560);
        dialogo.setLocationRelativeTo(this);

        JPanel columna = new JPanel();
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.setBackground(Color.WHITE);
        columna.setBorder(new EmptyBorder(26, 30, 22, 30));

        JLabel titulo = new JLabel("Cotizar transporte");
        titulo.setFont(new Font(FUENTE, Font.BOLD, 23));
        titulo.setForeground(AZUL_OSCURO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Reserva #" + idReserva);
        subtitulo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        subtitulo.setForeground(GRIS);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel detalle = new JPanel(new GridLayout(0, 2, 12, 12));
        detalle.setOpaque(false);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);
        detalle.add(dato("Modalidad", (String) t[0]));
        detalle.add(dato("Dirección de recogida", (String) t[1]));
        detalle.add(dato("Dirección de entrega", (String) t[2]));
        detalle.add(dato("Fecha y hora de recogida",
                t[3] == null ? null
                        : ((Timestamp) t[3]).toLocalDateTime()
                          .format(FECHA_HORA)));
        detalle.add(dato("Fecha y hora de entrega",
                t[4] == null ? null
                        : ((Timestamp) t[4]).toLocalDateTime()
                          .format(FECHA_HORA)));
        detalle.add(dato("Observaciones", (String) t[5]));

        JTextField txtPrecio = new JTextField();
        txtPrecio.setFont(new Font(FUENTE, Font.PLAIN, 14));
        txtPrecio.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42));
        txtPrecio.setPreferredSize(new Dimension(240, 42));

        JLabel lblPrecio = new JLabel("Precio del transporte");
        lblPrecio.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblPrecio.setForeground(AZUL_OSCURO);
        lblPrecio.setAlignmentX(Component.LEFT_ALIGNMENT);

        columna.add(titulo);
        columna.add(Box.createVerticalStrut(4));
        columna.add(subtitulo);
        columna.add(Box.createVerticalStrut(18));
        columna.add(detalle);
        columna.add(Box.createVerticalStrut(18));
        columna.add(lblPrecio);
        columna.add(Box.createVerticalStrut(6));
        columna.add(txtPrecio);

        BotonRedondeado rechazar = new BotonRedondeado(
                "Rechazar solicitud", Color.WHITE, ROJO, ROJO, null);
        rechazar.setPreferredSize(new Dimension(180, 44));

        BotonRedondeado confirmar = new BotonRedondeado(
                "Guardar cotización", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        confirmar.setPreferredSize(new Dimension(190, 44));

        confirmar.addActionListener(e -> {
            BigDecimal precio;

            try {
                precio = new BigDecimal(
                        txtPrecio.getText().trim().replace(',', '.'))
                        .setScale(2, RoundingMode.HALF_UP);
            } catch (NumberFormatException ex) {
                aviso("Ingrese un precio válido para el transporte.");
                return;
            }

            if (precio.signum() <= 0) {
                aviso("El precio del transporte debe ser mayor que cero.");
                return;
            }

            try {
                datos.cotizarTransporte(idReserva,
                        usuario.getIdUsuario(), precio);
                dialogo.dispose();
                JOptionPane.showMessageDialog(this,
                        "Transporte cotizado y programado correctamente.",
                        "Cotización registrada",
                        JOptionPane.INFORMATION_MESSAGE);
                cargarReservas();
            } catch (RuntimeException ex) {
                aviso(ex.getMessage());
            }
        });

        rechazar.addActionListener(e -> {
            int respuesta = JOptionPane.showConfirmDialog(dialogo,
                    "¿Rechazar esta solicitud de transporte?",
                    "Rechazar transporte",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }

            try {
                datos.rechazarTransporte(idReserva,
                        usuario.getIdUsuario());
                dialogo.dispose();
                cargarReservas();
            } catch (RuntimeException ex) {
                aviso(ex.getMessage());
            }
        });

        JPanel pie = new JPanel(new FlowLayout(
                FlowLayout.RIGHT, 12, 14));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, BORDE));
        pie.add(rechazar);
        pie.add(confirmar);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.add(columna, BorderLayout.CENTER);
        contenedor.add(pie, BorderLayout.SOUTH);
        dialogo.setContentPane(contenedor);
        dialogo.setVisible(true);
    }





    private void cargarEfectivo() {
        cuerpoEfectivo.removeAll();
        try {
            List<Object[]> lista =
                    datos.pagosEfectivoPendientes(usuario.getIdUsuario());

            if (lista.isEmpty()) {
                cuerpoEfectivo.add(vacio(
                                "No tiene pagos en efectivo por confirmar."),
                        BorderLayout.CENTER);
            } else {
                JPanel columna = new JPanel();
                columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
                columna.setOpaque(false);
                for (Object[] p : lista) {
                    columna.add(tarjetaEfectivo(p));
                    columna.add(Box.createVerticalStrut(12));
                }
                cuerpoEfectivo.add(desplazable(columna), BorderLayout.CENTER);
            }
        } catch (RuntimeException e) {
            cuerpoEfectivo.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoEfectivo);
    }

    private JPanel tarjetaEfectivo(Object[] p) {
        int idPago = (int) p[0];
        int idReserva = (int) p[1];
        String mascota = (String) p[2];
        String propietario = (String) p[3];
        String alojamiento = (String) p[4];
        double monto = (double) p[5];
        String fecha = (String) p[6];

        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new BorderLayout(18, 0));
        caja.setBorder(new EmptyBorder(18, 20, 18, 20));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titulo = new JLabel("Reserva #" + idReserva
                + "  ·  " + mascota + " en " + alojamiento);
        titulo.setFont(new Font(FUENTE, Font.BOLD, 16));
        titulo.setForeground(AZUL_OSCURO);
        fila.add(titulo);
        fila.add(pastilla("PENDIENTE", AMARILLO_PILL, AMARILLO_TXT));

        JLabel duenio = new JLabel("Propietario: " + propietario
                + "     ·     Registrado el " + seguro(fecha, "-"));
        duenio.setFont(new Font(FUENTE, Font.PLAIN, 12));
        duenio.setForeground(GRIS);
        duenio.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel detalle = new JLabel("Pago en efectivo  ·  Monto: "
                + dinero(monto));
        detalle.setFont(new Font(FUENTE, Font.BOLD, 13));
        detalle.setForeground(AZUL);
        detalle.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(fila);
        textos.add(Box.createVerticalStrut(7));
        textos.add(duenio);
        textos.add(Box.createVerticalStrut(7));
        textos.add(detalle);

        BotonRedondeado confirmar = new BotonRedondeado(
                "Confirmar dinero recibido", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2);
        confirmar.setPreferredSize(new Dimension(215, 42));
        confirmar.setMaximumSize(new Dimension(215, 42));
        confirmar.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmar.addActionListener(e -> confirmarEfectivo(idPago, monto));

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.add(confirmar);

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setOpaque(false);
        derecha.setPreferredSize(new Dimension(225, 0));
        derecha.add(acciones, BorderLayout.NORTH);

        caja.add(textos, BorderLayout.CENTER);
        caja.add(derecha, BorderLayout.EAST);
        return caja;
    }

    private void confirmarEfectivo(int idPago, double monto) {
        int r = JOptionPane.showConfirmDialog(
                this,
                "¿Confirma que recibió " + dinero(monto)
                        + " en efectivo?",
                "Confirmar dinero recibido",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (r != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            datos.confirmarEfectivo(idPago, usuario.getIdUsuario());

            JOptionPane.showMessageDialog(
                    this,
                    "Pago en efectivo confirmado correctamente.",
                    "Pago confirmado",
                    JOptionPane.INFORMATION_MESSAGE);

            cargarEfectivo();
            cargarInicio();

        } catch (RuntimeException e) {
            aviso(e.getMessage());
        }
    }





    private void cargarPerfil() {
        cuerpoPerfil.removeAll();
        try {
            Object[] p = datos.perfil(usuario.getIdUsuario());
            if (p == null) {
                cuerpoPerfil.add(error("No se encontró el perfil del cuidador."),
                        BorderLayout.CENTER);
            } else {
                PanelCaja caja = new PanelCaja(18, Color.WHITE, BORDE);
                caja.setLayout(new BorderLayout(24, 0));
                caja.setBorder(new EmptyBorder(24, 26, 24, 26));

                JLabel foto = cargarImagen((String) p[10], 150, 150);
                foto.setPreferredSize(new Dimension(150, 150));

                JPanel datosPerfil = new JPanel(new GridLayout(0, 2, 18, 14));
                datosPerfil.setOpaque(false);
                datosPerfil.add(dato("Nombre", p[0] + " " + p[1]));
                datosPerfil.add(dato("Correo", (String) p[2]));
                datosPerfil.add(dato("Teléfono", (String) p[3]));
                datosPerfil.add(dato("Dirección", (String) p[4]));
                datosPerfil.add(dato("Estado", (String) p[5]));
                datosPerfil.add(dato("Experiencia", (String) p[6]));
                datosPerfil.add(dato("Descripción", (String) p[7]));
                datosPerfil.add(dato("Observación de verificación", (String) p[8]));
                datosPerfil.add(dato("ID de usuario", String.valueOf(p[9])));
                datosPerfil.add(dato("ID de cuidador", String.valueOf(p[11])));

                caja.add(foto, BorderLayout.WEST);
                caja.add(datosPerfil, BorderLayout.CENTER);

                JButton inactivar = botonAccionAlojamiento(
                        "Inactivar actividad como cuidador", ROJO);
                inactivar.setAlignmentX(Component.LEFT_ALIGNMENT);
                inactivar.addActionListener(
                        e -> solicitarInactivacionCuidador());

                JPanel columna = new JPanel();
                columna.setLayout(new BoxLayout(
                        columna, BoxLayout.Y_AXIS));
                columna.setOpaque(false);
                caja.setAlignmentX(Component.LEFT_ALIGNMENT);
                columna.add(caja);
                columna.add(Box.createVerticalStrut(18));
                columna.add(inactivar);
                cuerpoPerfil.add(columna, BorderLayout.NORTH);
            }
        } catch (RuntimeException e) {
            cuerpoPerfil.add(error(e.getMessage()), BorderLayout.CENTER);
        }
        refrescar(cuerpoPerfil);
    }

    private void solicitarInactivacionCuidador() {
        Object[] opciones = {"Cancelar", "Inactivar"};
        int respuesta = JOptionPane.showOptionDialog(
                this,
                "¿Deseas inactivar temporalmente tu actividad como "
                        + "cuidador?\n\n"
                        + "Tus alojamientos dejarán de estar publicados "
                        + "y no recibirás nuevas reservas.\n"
                        + "Tu cuenta seguirá disponible en modo usuario.",
                "Inactivar actividad como cuidador",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (respuesta != 1) {
            return;
        }

        try {
            datos.inactivarActividad(usuario.getIdUsuario());
            JOptionPane.showMessageDialog(
                    this,
                    "La actividad como cuidador fue inactivada. "
                            + "Su cuenta de usuario sigue activa.",
                    "Actividad inactivada",
                    JOptionPane.INFORMATION_MESSAGE);
            cargarAlojamientos();
            cargarInicio();
            cargarPerfil();
        } catch (RuntimeException e) {
            aviso(e.getMessage());
        }
    }

    private JPanel dato(String etiqueta, String valor) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel e = new JLabel(etiqueta);
        e.setFont(new Font(FUENTE, Font.BOLD, 12));
        e.setForeground(AZUL);
        JLabel v = new JLabel("<html>" + escapar(seguro(valor, "Sin información")) + "</html>");
        v.setFont(new Font(FUENTE, Font.PLAIN, 13));
        v.setForeground(AZUL_OSCURO);
        e.setAlignmentX(Component.LEFT_ALIGNMENT);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(e);
        p.add(Box.createVerticalStrut(3));
        p.add(v);
        return p;
    }





    private void cambiarAModoUsuario() {
        PanelUsuario panel = new PanelUsuario(usuario);
        panel.setVisible(true);
        dispose();
    }

    private void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (r != JOptionPane.YES_OPTION) {
            return;
        }
        SesionUsuario.cerrarSesion();
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }





    private static class AlojamientoEdicion {

        Integer idAlojamiento;
        String nombre;
        String tipo;
        String descripcion;
        String provincia;
        String ciudad;
        String direccion;
        String referencia;
        int capacidad;
        BigDecimal precio;
        boolean patio;
        boolean cerramiento;
        boolean convive;
        String reglas;
        boolean transporte;
        List<Object[]> categorias;
        String fotoNueva;
    }

    private class Datos {

        boolean esCuidadorVerificado(int idUsuario) {
            String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM cuidador
                        WHERE id_usuario = ?
                          AND estado_verificacion = 'VERIFICADO'
                    )
                    """;
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        Object[] resumen(int idUsuario) {
            String sql = """
                    SELECT
                        COUNT(*) FILTER (WHERE r.estado_reserva = 'SOLICITADA') AS pendientes,
                        COUNT(*) FILTER (WHERE r.estado_reserva IN ('ACEPTADA','EN_CURSO')) AS activas,
                        COUNT(DISTINCT a.id_alojamiento) AS alojamientos,
                        COALESCE((
                            SELECT SUM(p.monto)
                            FROM pago p
                            JOIN reserva rp ON rp.id_reserva = p.id_reserva
                            JOIN alojamiento ap ON ap.id_alojamiento = rp.id_alojamiento
                            JOIN cuidador cp ON cp.id_cuidador = ap.id_cuidador
                            WHERE cp.id_usuario = ?
                              AND p.estado_pago = 'APROBADO'
                        ), 0) AS ingresos
                    FROM cuidador c
                    LEFT JOIN alojamiento a ON a.id_cuidador = c.id_cuidador
                    LEFT JOIN reserva r ON r.id_alojamiento = a.id_alojamiento
                    WHERE c.id_usuario = ?
                    """;
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getInt("pendientes"),
                                rs.getInt("activas"),
                                rs.getInt("alojamientos"),
                                rs.getDouble("ingresos")
                        };
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return new Object[]{0, 0, 0, 0.0};
        }

        List<Object[]> alojamientos(int idUsuario) {
            String sql = """
                    SELECT a.id_alojamiento, a.nombre, a.tipo_alojamiento,
                           a.ciudad, a.direccion, a.descripcion,
                           a.capacidad_total, a.precio_noche,
                           a.ofrece_transporte, a.activo
                    FROM alojamiento a
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    WHERE c.id_usuario = ?
                    ORDER BY a.activo DESC, a.fecha_publicacion DESC, a.nombre
                    """;
            List<Object[]> lista = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6),
                                rs.getInt(7), rs.getDouble(8), rs.getBoolean(9),
                                rs.getBoolean(10)
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return lista;
        }

        Object[] alojamientoPropio(int idAlojamiento, int idUsuario) {
            String sql = """
                    SELECT a.nombre, a.tipo_alojamiento, a.descripcion,
                           a.provincia, a.ciudad, a.direccion,
                           a.referencia, a.capacidad_total, a.precio_noche,
                           a.tiene_patio, a.tiene_cerramiento,
                           a.convive_con_mascotas, a.reglas_generales,
                           a.ofrece_transporte,
                           (SELECT f.url_foto
                            FROM foto_alojamiento f
                            WHERE f.id_alojamiento = a.id_alojamiento
                            ORDER BY f.es_foto_principal DESC,
                                     f.orden_visualizacion NULLS LAST,
                                     f.id_foto DESC
                            LIMIT 1)
                    FROM alojamiento a
                    JOIN cuidador c
                      ON c.id_cuidador = a.id_cuidador
                    WHERE a.id_alojamiento = ?
                      AND c.id_usuario = ?
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idAlojamiento);
                ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6),
                                rs.getString(7), rs.getInt(8),
                                rs.getBigDecimal(9), rs.getBoolean(10),
                                rs.getBoolean(11), rs.getBoolean(12),
                                rs.getString(13), rs.getBoolean(14),
                                rs.getString(15)
                        };
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return null;
        }

        List<Object[]> categoriasConfigurables(int idAlojamiento) {
            String sql = """
                    SELECT ct.id_categoria,
                           e.nombre_especie,
                           ct.nombre_categoria,
                           COALESCE(aca.estado, FALSE),
                           aca.cantidad_maxima
                    FROM categoria_tamano ct
                    JOIN especie e ON e.id_especie = ct.id_especie
                    LEFT JOIN alojamiento_categoria_aceptada aca
                      ON aca.id_categoria = ct.id_categoria
                     AND aca.id_alojamiento = ?
                    ORDER BY e.nombre_especie,
                             ct.peso_min_kg,
                             ct.nombre_categoria
                    """;
            List<Object[]> lista = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idAlojamiento);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Integer cantidad = (Integer) rs.getObject(5);
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2),
                                rs.getString(3), rs.getBoolean(4),
                                cantidad
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return lista;
        }

        void guardarAlojamiento(AlojamientoEdicion d, int idUsuario) {
            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    int idCuidador = idCuidadorVerificado(c, idUsuario);
                    int idAlojamiento;

                    if (d.idAlojamiento == null) {



                        try (Statement st = c.createStatement()) {
                            st.execute("LOCK TABLE alojamiento IN "
                                    + "SHARE ROW EXCLUSIVE MODE");
                        }
                        idAlojamiento = registrarAlojamiento(
                                c, d, idCuidador);
                        for (Object[] categoria : d.categorias) {
                            registrarCategoria(c, idAlojamiento,
                                    (int) categoria[0],
                                    (int) categoria[1]);
                        }
                    } else {
                        idAlojamiento = d.idAlojamiento;
                        bloquearAlojamientoPropio(
                                c, idAlojamiento, idUsuario);
                        actualizarAlojamiento(
                                c, d, idAlojamiento, idUsuario);

                        String desactivar = """
                                UPDATE alojamiento_categoria_aceptada aca
                                SET estado = FALSE
                                FROM alojamiento a
                                JOIN cuidador cu
                                  ON cu.id_cuidador = a.id_cuidador
                                WHERE aca.id_alojamiento = a.id_alojamiento
                                  AND a.id_alojamiento = ?
                                  AND cu.id_usuario = ?
                                """;
                        try (PreparedStatement ps =
                                     c.prepareStatement(desactivar)) {
                            ps.setInt(1, idAlojamiento);
                            ps.setInt(2, idUsuario);
                            ps.executeUpdate();
                        }

                        for (Object[] categoria : d.categorias) {
                            registrarCategoria(
                                    c,
                                    idAlojamiento,
                                    (int) categoria[0],
                                    (int) categoria[1]
                            );
                        }
                    }

                    if (d.fotoNueva != null
                            && !d.fotoNueva.isBlank()) {
                        registrarFoto(c, idAlojamiento, d.fotoNueva);
                    }

                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw new RuntimeException(mensaje(e), e);
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private int idCuidadorVerificado(Connection c, int idUsuario)
                throws SQLException {
            String sql = """
                    SELECT id_cuidador
                    FROM cuidador
                    WHERE id_usuario = ?
                      AND estado_verificacion = 'VERIFICADO'
                    FOR UPDATE
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new RuntimeException(
                    "Solo un cuidador verificado puede administrar "
                            + "alojamientos.");
        }

        private int registrarAlojamiento(Connection c,
                                         AlojamientoEdicion d,
                                         int idCuidador)
                throws SQLException {
            String sql = """
                    SELECT fn_registrar_alojamiento(
                        ?::varchar, ?::varchar, ?::text, ?::varchar,
                        ?::varchar, ?::varchar, ?::varchar, ?::integer,
                        ?::numeric, ?::boolean, ?::boolean, ?::boolean,
                        ?::text, ?::boolean, ?::integer)
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, d.nombre);
                ps.setString(2, d.tipo);
                ps.setString(3, d.descripcion);
                ps.setString(4, d.provincia);
                ps.setString(5, d.ciudad);
                ps.setString(6, d.direccion);
                ps.setString(7, d.referencia);
                ps.setInt(8, d.capacidad);
                ps.setBigDecimal(9, d.precio);
                ps.setBoolean(10, d.patio);
                ps.setBoolean(11, d.cerramiento);
                ps.setBoolean(12, d.convive);
                ps.setString(13, d.reglas);
                ps.setBoolean(14, d.transporte);
                ps.setInt(15, idCuidador);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return rs.getInt(1);
                    }
                }
            }
            throw new RuntimeException(
                    "La base de datos no devolvio el alojamiento creado.");
        }

        private void registrarCategoria(Connection c, int idAlojamiento,
                                        int idCategoria, int cantidad)
                throws SQLException {
            String sql = """
                    SELECT fn_registrar_categoria_aceptada(
                        ?::integer, ?::text, ?::integer, ?::integer)
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, cantidad);
                ps.setNull(2, Types.VARCHAR);
                ps.setInt(3, idAlojamiento);
                ps.setInt(4, idCategoria);
                ps.executeQuery();
            }
        }

        private void registrarFoto(Connection c, int idAlojamiento,
                                   String ruta) throws SQLException {
            String sql = """
                    SELECT fn_registrar_foto_alojamiento(
                        ?::varchar, ?::varchar, ?::integer,
                        ?::boolean, ?::integer)
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, ruta);
                ps.setString(2, "Foto principal");
                ps.setInt(3, 1);
                ps.setBoolean(4, true);
                ps.setInt(5, idAlojamiento);
                ps.executeQuery();
            }
        }

        private void bloquearAlojamientoPropio(Connection c,
                                               int idAlojamiento,
                                               int idUsuario)
                throws SQLException {
            String sql = """
                    SELECT a.id_alojamiento
                    FROM alojamiento a
                    JOIN cuidador cu
                      ON cu.id_cuidador = a.id_cuidador
                    WHERE a.id_alojamiento = ?
                      AND cu.id_usuario = ?
                    FOR UPDATE OF a
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idAlojamiento);
                ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return;
                    }
                }
            }
            throw new RuntimeException(
                    "El alojamiento no existe o no le pertenece.");
        }

        private void actualizarAlojamiento(Connection c,
                                           AlojamientoEdicion d,
                                           int idAlojamiento,
                                           int idUsuario)
                throws SQLException {
            String sql = """
                    UPDATE alojamiento a
                    SET nombre = ?::varchar,
                        tipo_alojamiento = ?::varchar,
                        descripcion = ?::text,
                        provincia = ?::varchar,
                        ciudad = ?::varchar,
                        direccion = ?::varchar,
                        referencia = ?::varchar,
                        capacidad_total = ?::integer,
                        precio_noche = ?::numeric,
                        tiene_patio = ?::boolean,
                        tiene_cerramiento = ?::boolean,
                        convive_con_mascotas = ?::boolean,
                        reglas_generales = ?::text,
                        ofrece_transporte = ?::boolean
                    FROM cuidador cu
                    WHERE a.id_cuidador = cu.id_cuidador
                      AND a.id_alojamiento = ?::integer
                      AND cu.id_usuario = ?::integer
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, d.nombre);
                ps.setString(2, d.tipo);
                ps.setString(3, d.descripcion);
                ps.setString(4, d.provincia);
                ps.setString(5, d.ciudad);
                ps.setString(6, d.direccion);
                ps.setString(7, d.referencia);
                ps.setInt(8, d.capacidad);
                ps.setBigDecimal(9, d.precio);
                ps.setBoolean(10, d.patio);
                ps.setBoolean(11, d.cerramiento);
                ps.setBoolean(12, d.convive);
                ps.setString(13, d.reglas);
                ps.setBoolean(14, d.transporte);
                ps.setInt(15, idAlojamiento);
                ps.setInt(16, idUsuario);
                if (ps.executeUpdate() != 1) {
                    throw new RuntimeException(
                            "No se pudo actualizar el alojamiento.");
                }
            }
        }

        void publicarAlojamiento(int idAlojamiento, int idUsuario) {
            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    bloquearAlojamientoPropio(c, idAlojamiento, idUsuario);
                    String categorias = """
                            SELECT EXISTS (
                                SELECT 1
                                FROM alojamiento_categoria_aceptada
                                WHERE id_alojamiento = ?
                                  AND estado = TRUE)
                            """;
                    boolean configurado;
                    try (PreparedStatement ps =
                                 c.prepareStatement(categorias)) {
                        ps.setInt(1, idAlojamiento);
                        try (ResultSet rs = ps.executeQuery()) {
                            configurado = rs.next() && rs.getBoolean(1);
                        }
                    }
                    if (!configurado) {
                        throw new RuntimeException(
                                "Configura al menos una mascota aceptada "
                                        + "antes de publicar el alojamiento.");
                    }
                    try (CallableStatement cs = c.prepareCall(
                            "CALL sp_publicar_alojamiento("
                                    + "?::integer, ?::integer)")) {
                        cs.setInt(1, idAlojamiento);
                        cs.setInt(2, idUsuario);
                        cs.execute();
                    }
                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw new RuntimeException(mensaje(e), e);
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void despublicarAlojamiento(int idAlojamiento, int idUsuario) {
            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    bloquearAlojamientoPropio(c, idAlojamiento, idUsuario);
                    if (tieneReservasActivas(
                            c, idAlojamiento, idUsuario)) {
                        throw new RuntimeException(
                                "No puedes desactivar este alojamiento "
                                        + "porque tiene reservas pendientes "
                                        + "o activas.");
                    }
                    llamarDespublicar(c, idAlojamiento, idUsuario);
                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw new RuntimeException(mensaje(e), e);
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void inactivarActividad(int idUsuario) {
            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    idCuidadorVerificado(c, idUsuario);
                    List<Integer> alojamientos = new ArrayList<>();
                    String propios = """
                            SELECT a.id_alojamiento
                            FROM alojamiento a
                            JOIN cuidador cu
                              ON cu.id_cuidador = a.id_cuidador
                            WHERE cu.id_usuario = ?
                            ORDER BY a.id_alojamiento
                            FOR UPDATE OF a
                            """;
                    try (PreparedStatement ps =
                                 c.prepareStatement(propios)) {
                        ps.setInt(1, idUsuario);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                alojamientos.add(rs.getInt(1));
                            }
                        }
                    }

                    if (tieneReservasActivas(c, null, idUsuario)) {
                        throw new RuntimeException(
                                "No puedes inactivar tu actividad como "
                                        + "cuidador mientras tengas reservas "
                                        + "pendientes o activas.");
                    }
                    if (tieneSituacionesPendientes(c, idUsuario)) {
                        throw new RuntimeException(
                                "No puedes inactivar tu actividad como "
                                        + "cuidador mientras existan pagos "
                                        + "o transportes pendientes.");
                    }

                    for (int idAlojamiento : alojamientos) {
                        llamarDespublicar(c, idAlojamiento, idUsuario);
                    }
                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw new RuntimeException(mensaje(e), e);
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        private boolean tieneReservasActivas(Connection c,
                                             Integer idAlojamiento,
                                             int idUsuario)
                throws SQLException {
            String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM reserva r
                        JOIN alojamiento a
                          ON a.id_alojamiento = r.id_alojamiento
                        JOIN cuidador cu
                          ON cu.id_cuidador = a.id_cuidador
                        WHERE cu.id_usuario = ?
                          AND (?::integer IS NULL
                               OR a.id_alojamiento = ?::integer)
                          AND r.estado_reserva IN
                              ('SOLICITADA', 'ACEPTADA', 'EN_CURSO'))
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                if (idAlojamiento == null) {
                    ps.setNull(2, Types.INTEGER);
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(2, idAlojamiento);
                    ps.setInt(3, idAlojamiento);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            }
        }

        private boolean tieneSituacionesPendientes(Connection c,
                                                   int idUsuario)
                throws SQLException {
            String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM pago p
                        JOIN reserva r
                          ON r.id_reserva = p.id_reserva
                        JOIN alojamiento a
                          ON a.id_alojamiento = r.id_alojamiento
                        JOIN cuidador cu
                          ON cu.id_cuidador = a.id_cuidador
                        WHERE cu.id_usuario = ?
                          AND p.estado_pago = 'PENDIENTE'
                        UNION ALL
                        SELECT 1
                        FROM transporte t
                        JOIN reserva r
                          ON r.id_reserva = t.id_reserva
                        JOIN alojamiento a
                          ON a.id_alojamiento = r.id_alojamiento
                        JOIN cuidador cu
                          ON cu.id_cuidador = a.id_cuidador
                        WHERE cu.id_usuario = ?
                          AND t.estado_transporte IN
                              ('PENDIENTE', 'PROGRAMADO'))
                    """;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() && rs.getBoolean(1);
                }
            }
        }

        private void llamarDespublicar(Connection c,
                                       int idAlojamiento,
                                       int idUsuario)
                throws SQLException {
            try (CallableStatement cs = c.prepareCall(
                    "CALL sp_despublicar_alojamiento("
                            + "?::integer, ?::integer)")) {
                cs.setInt(1, idAlojamiento);
                cs.setInt(2, idUsuario);
                cs.execute();
            }
        }

        private void rollback(Connection c) {
            try {
                c.rollback();
            } catch (SQLException ignored) {

            }
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
                    return rs.next() ? rs.getString(1) : null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> solicitudes(int idUsuario, int limite) {
            String sql = """
                    SELECT r.id_reserva,
                           m.nombre,
                           e.nombre_especie,
                           m.raza,
                           m.peso_kg,
                           u.nombre || ' ' || u.apellido AS propietario,
                           u.telefono,
                           u.email,
                           a.nombre AS alojamiento,
                           r.fecha_ingreso,
                           r.fecha_salida,
                           r.mensaje_solicitud,
                           r.instrucciones_generales,
                           COALESCE(r.total_acordado, 0)
                    FROM reserva r
                    JOIN alojamiento a ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN especie e ON e.id_especie = m.id_especie
                    JOIN usuario u ON u.id_usuario = m.id_usuario
                    WHERE c.id_usuario = ?
                      AND r.estado_reserva = 'SOLICITADA'
                    ORDER BY r.id_reserva DESC
                    """ + (limite > 0 ? " LIMIT " + limite : "");

            List<Object[]> lista = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getDouble(5), rs.getString(6),
                                rs.getString(7), rs.getString(8), rs.getString(9),
                                rs.getDate(10), rs.getDate(11), rs.getString(12),
                                rs.getString(13), rs.getDouble(14)
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return lista;
        }

        void aceptarReserva(int idReserva, int idUsuarioCuidador) {
            try (Connection c = ConexionBD.conectar();
                 CallableStatement cs = c.prepareCall(
                         "CALL sp_aceptar_reserva(?, ?)") ) {
                cs.setInt(1, idReserva);
                cs.setInt(2, idUsuarioCuidador);
                cs.execute();
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void rechazarReserva(int idReserva, int idUsuarioCuidador) {
            try (Connection c = ConexionBD.conectar();
                 CallableStatement cs = c.prepareCall(
                         "CALL sp_rechazar_reserva(?, ?)") ) {
                cs.setInt(1, idReserva);
                cs.setInt(2, idUsuarioCuidador);
                cs.execute();
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> reservas(int idUsuario) {
            String sql = """
                    SELECT r.id_reserva,
                           m.nombre,
                           u.nombre || ' ' || u.apellido AS propietario,
                           a.nombre,
                           r.fecha_ingreso,
                           r.fecha_salida,
                           r.estado_reserva,
                           COALESCE(r.total_acordado, 0),
                           COALESCE((
                               SELECT SUM(p.monto)
                               FROM pago p
                               WHERE p.id_reserva = r.id_reserva
                                 AND p.estado_pago = 'APROBADO'
                           ), 0) AS pagado,
                           t.estado_transporte,
                           COALESCE(t.precio_acordado, 0),
                           a.ofrece_transporte,
                           t.fecha_hora_entrega,
                           CASE
                               WHEN r.estado_reserva NOT IN
                                    ('ACEPTADA', 'EN_CURSO') THEN FALSE
                               WHEN t.id_transporte IS NOT NULL THEN
                                    t.estado_transporte = 'PROGRAMADO'
                                    AND t.fecha_hora_entrega IS NOT NULL
                                    AND CURRENT_TIMESTAMP
                                        >= t.fecha_hora_entrega
                               ELSE CURRENT_DATE >= r.fecha_salida
                           END AS puede_entregar
                    FROM reserva r
                    JOIN alojamiento a ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN usuario u ON u.id_usuario = m.id_usuario
                    LEFT JOIN transporte t ON t.id_reserva = r.id_reserva
                    WHERE c.id_usuario = ?
                      AND r.estado_reserva <> 'SOLICITADA'
                    ORDER BY r.id_reserva DESC
                    """;
            List<Object[]> lista = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getDate(5), rs.getDate(6),
                                rs.getString(7), rs.getDouble(8), rs.getDouble(9),
                                rs.getString(10), rs.getDouble(11),
                                rs.getBoolean(12), rs.getTimestamp(13),
                                rs.getBoolean(14)
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return lista;
        }

        String impedimentoEntrega(int idReserva, int idUsuarioCuidador) {
            String sql = """
                    SELECT r.estado_reserva,
                           r.fecha_salida,
                           t.id_transporte,
                           t.estado_transporte,
                           t.fecha_hora_entrega,
                           CASE
                               WHEN t.id_transporte IS NOT NULL THEN
                                    t.estado_transporte = 'PROGRAMADO'
                                    AND t.fecha_hora_entrega IS NOT NULL
                                    AND CURRENT_TIMESTAMP
                                        >= t.fecha_hora_entrega
                               ELSE CURRENT_DATE >= r.fecha_salida
                           END
                    FROM reserva r
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    LEFT JOIN transporte t ON t.id_reserva = r.id_reserva
                    WHERE r.id_reserva = ?::integer
                      AND c.id_usuario = ?::integer
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idReserva);
                ps.setInt(2, idUsuarioCuidador);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return "La reserva no existe o no pertenece a uno de tus alojamientos.";
                    }
                    String estado = rs.getString(1);
                    if (!"ACEPTADA".equals(estado)
                            && !"EN_CURSO".equals(estado)) {
                        return "La reserva ya no puede finalizarse en su estado actual.";
                    }
                    if (rs.getObject(3) != null) {
                        if (!"PROGRAMADO".equals(rs.getString(4))) {
                            return "El transporte no está programado para la entrega.";
                        }
                        if (rs.getTimestamp(5) == null) {
                            return "El transporte no tiene una fecha y hora de entrega válida.";
                        }
                    }
                    if (!rs.getBoolean(6)) {
                        return "Todavía no ha llegado el momento de entregar la mascota.";
                    }
                    return null;
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void entregarMascota(int idReserva, int idUsuarioCuidador) {
            try (Connection c = ConexionBD.conectar()) {
                c.setAutoCommit(false);
                try {
                    Date fechaSalida;
                    String estado;
                    String reserva = """
                            SELECT r.estado_reserva, r.fecha_salida
                            FROM reserva r
                            JOIN alojamiento a
                                 ON a.id_alojamiento = r.id_alojamiento
                            JOIN cuidador cu
                                 ON cu.id_cuidador = a.id_cuidador
                            WHERE r.id_reserva = ?::integer
                              AND cu.id_usuario = ?::integer
                            FOR UPDATE OF r
                            """;
                    try (PreparedStatement ps = c.prepareStatement(reserva)) {
                        ps.setInt(1, idReserva);
                        ps.setInt(2, idUsuarioCuidador);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new RuntimeException(
                                        "La reserva no existe o no pertenece a uno de tus alojamientos.");
                            }
                            estado = rs.getString(1);
                            fechaSalida = rs.getDate(2);
                        }
                    }

                    if (!"ACEPTADA".equals(estado)
                            && !"EN_CURSO".equals(estado)) {
                        throw new RuntimeException(
                                "La reserva ya no puede finalizarse en su estado actual.");
                    }

                    Integer idTransporte = null;
                    String estadoTransporte = null;
                    Timestamp fechaEntrega = null;
                    String transporte = """
                            SELECT id_transporte, estado_transporte,
                                   fecha_hora_entrega
                            FROM transporte
                            WHERE id_reserva = ?::integer
                            FOR UPDATE
                            """;
                    try (PreparedStatement ps = c.prepareStatement(transporte)) {
                        ps.setInt(1, idReserva);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                idTransporte = rs.getInt(1);
                                estadoTransporte = rs.getString(2);
                                fechaEntrega = rs.getTimestamp(3);
                            }
                        }
                    }

                    if (idTransporte != null) {
                        if (!"PROGRAMADO".equals(estadoTransporte)
                                || fechaEntrega == null) {
                            throw new RuntimeException(
                                    "El transporte no está listo para confirmar la entrega.");
                        }
                        String momento = """
                                SELECT CURRENT_TIMESTAMP
                                       >= ?::timestamp
                                """;
                        try (PreparedStatement ps = c.prepareStatement(momento)) {
                            ps.setTimestamp(1, fechaEntrega);
                            try (ResultSet rs = ps.executeQuery()) {
                                rs.next();
                                if (!rs.getBoolean(1)) {
                                    throw new RuntimeException(
                                            "Todavía no ha llegado el momento de entregar la mascota.");
                                }
                            }
                        }
                    } else {
                        String momento = "SELECT CURRENT_DATE >= ?::date";
                        try (PreparedStatement ps = c.prepareStatement(momento)) {
                            ps.setDate(1, fechaSalida);
                            try (ResultSet rs = ps.executeQuery()) {
                                rs.next();
                                if (!rs.getBoolean(1)) {
                                    throw new RuntimeException(
                                            "Todavía no ha llegado el día de salida de la mascota.");
                                }
                            }
                        }
                    }

                    String finalizarReserva = """
                            UPDATE reserva
                            SET estado_reserva = 'FINALIZADA'
                            WHERE id_reserva = ?::integer
                              AND estado_reserva IN ('ACEPTADA', 'EN_CURSO')
                            """;
                    try (PreparedStatement ps =
                                 c.prepareStatement(finalizarReserva)) {
                        ps.setInt(1, idReserva);
                        if (ps.executeUpdate() != 1) {
                            throw new RuntimeException(
                                    "La reserva cambió de estado y no pudo finalizarse.");
                        }
                    }

                    if (idTransporte != null) {
                        String finalizarTransporte = """
                                UPDATE transporte
                                SET estado_transporte = 'FINALIZADO'
                                WHERE id_transporte = ?::integer
                                  AND estado_transporte = 'PROGRAMADO'
                                """;
                        try (PreparedStatement ps =
                                     c.prepareStatement(finalizarTransporte)) {
                            ps.setInt(1, idTransporte);
                            if (ps.executeUpdate() != 1) {
                                throw new RuntimeException(
                                        "El transporte cambió de estado y la entrega no pudo confirmarse.");
                            }
                        }
                    }

                    c.commit();
                } catch (SQLException e) {
                    rollback(c);
                    throw new RuntimeException(mensaje(e), e);
                } catch (RuntimeException e) {
                    rollback(c);
                    throw e;
                } finally {
                    try {
                        c.setAutoCommit(true);
                    } catch (SQLException ignored) {
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        Object[] transportePendiente(int idReserva,
                                     int idUsuarioCuidador) {
            String sql = """
                    SELECT t.modalidad,
                           t.direccion_recogida,
                           t.direccion_entrega,
                           t.fecha_hora_recogida,
                           t.fecha_hora_entrega,
                           t.observaciones
                    FROM transporte t
                    JOIN reserva r ON r.id_reserva = t.id_reserva
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    WHERE t.id_reserva = ?
                      AND c.id_usuario = ?
                      AND t.estado_transporte = 'PENDIENTE'
                      AND r.estado_reserva IN ('ACEPTADA', 'EN_CURSO')
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idReserva);
                ps.setInt(2, idUsuarioCuidador);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2),
                                rs.getString(3), rs.getTimestamp(4),
                                rs.getTimestamp(5), rs.getString(6)
                        };
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }

            return null;
        }

        void cotizarTransporte(int idReserva, int idUsuarioCuidador,
                               BigDecimal precio) {
            String sql = """
                    UPDATE transporte t
                    SET precio_acordado = ?,
                        estado_transporte = 'PROGRAMADO'
                    FROM reserva r
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    WHERE t.id_reserva = ?
                      AND r.id_reserva = t.id_reserva
                      AND c.id_usuario = ?
                      AND t.estado_transporte = 'PENDIENTE'
                      AND r.estado_reserva IN ('ACEPTADA', 'EN_CURSO')
                      AND NOT EXISTS (
                          SELECT 1
                          FROM pago p
                          WHERE p.id_reserva = t.id_reserva
                            AND p.estado_pago IN ('PENDIENTE',
                                                  'APROBADO')
                      )
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setBigDecimal(1, precio.setScale(2,
                        RoundingMode.HALF_UP));
                ps.setInt(2, idReserva);
                ps.setInt(3, idUsuarioCuidador);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "No se pudo cotizar el transporte. Verifique "
                                    + "que siga pendiente y que la reserva "
                                    + "no tenga un pago activo.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        void rechazarTransporte(int idReserva,
                                int idUsuarioCuidador) {
            String sql = """
                    UPDATE transporte t
                    SET estado_transporte = 'CANCELADO',
                        observaciones = CASE
                            WHEN COALESCE(t.observaciones, '') LIKE ?
                                THEN t.observaciones
                            ELSE TRIM(? || E'\\n'
                                 || COALESCE(t.observaciones, ''))
                        END
                    FROM reserva r
                    JOIN alojamiento a
                         ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c
                         ON c.id_cuidador = a.id_cuidador
                    WHERE t.id_reserva = ?
                      AND r.id_reserva = t.id_reserva
                      AND c.id_usuario = ?
                      AND t.estado_transporte = 'PENDIENTE'
                    """;

            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, "%" + MARCA_RECHAZADO_CUIDADOR + "%");
                ps.setString(2, MARCA_RECHAZADO_CUIDADOR);
                ps.setInt(3, idReserva);
                ps.setInt(4, idUsuarioCuidador);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "La solicitud ya no puede rechazarse.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        List<Object[]> pagosEfectivoPendientes(int idUsuario) {
            String sql = """
                    SELECT p.id_pago,
                           p.id_reserva,
                           m.nombre,
                           u.nombre || ' ' || u.apellido AS propietario,
                           a.nombre,
                           p.monto,
                           TO_CHAR(COALESCE(p.fecha_pago,
                             p.fecha_registro), 'DD/MM/YYYY')
                    FROM pago p
                    JOIN reserva r ON r.id_reserva = p.id_reserva
                    JOIN alojamiento a ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    JOIN mascota m ON m.id_mascota = r.id_mascota
                    JOIN usuario u ON u.id_usuario = m.id_usuario
                    WHERE c.id_usuario = ?
                      AND p.forma_pago = 'EFECTIVO'
                      AND p.estado_pago = 'PENDIENTE'
                    ORDER BY p.id_pago DESC
                    """;
            List<Object[]> lista = new ArrayList<>();
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Object[]{
                                rs.getInt(1), rs.getInt(2), rs.getString(3),
                                rs.getString(4), rs.getString(5),
                                rs.getDouble(6), rs.getString(7)
                        });
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return lista;
        }

        void confirmarEfectivo(int idPago, int idUsuarioCuidador) {
            String sql = """
                    UPDATE pago p
                    SET estado_pago = 'APROBADO',
                        fecha_pago = COALESCE(p.fecha_pago, NOW())
                    FROM reserva r
                    JOIN alojamiento a ON a.id_alojamiento = r.id_alojamiento
                    JOIN cuidador c ON c.id_cuidador = a.id_cuidador
                    WHERE p.id_pago = ?
                      AND r.id_reserva = p.id_reserva
                      AND c.id_usuario = ?
                      AND p.forma_pago = 'EFECTIVO'
                      AND p.estado_pago = 'PENDIENTE'
                    """;
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idPago);
                ps.setInt(2, idUsuarioCuidador);

                if (ps.executeUpdate() == 0) {
                    throw new RuntimeException(
                            "No puede confirmar este pago. "
                                    + "Verifique que la reserva sea suya "
                                    + "y que el pago siga pendiente.");
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
        }

        Object[] perfil(int idUsuario) {
            String sql = """
                    SELECT u.nombre, u.apellido, u.email, u.telefono,
                           u.direccion_domicilio,
                           c.estado_verificacion,
                           c.experiencia,
                           c.descripcion_perfil,
                           c.observacion,
                           u.id_usuario,
                           u.foto_perfil,
                           c.id_cuidador
                    FROM usuario u
                    JOIN cuidador c ON c.id_usuario = u.id_usuario
                    WHERE u.id_usuario = ?
                    LIMIT 1
                    """;
            try (Connection c = ConexionBD.conectar();
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Object[]{
                                rs.getString(1), rs.getString(2), rs.getString(3),
                                rs.getString(4), rs.getString(5), rs.getString(6),
                                rs.getString(7), rs.getString(8), rs.getString(9),
                                rs.getInt(10), rs.getString(11), rs.getInt(12)
                        };
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(mensaje(e), e);
            }
            return null;
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
            return m.trim();
        }
    }





    private JScrollPane desplazable(Component c) {
        JScrollPane s = new JScrollPane(c);
        s.setBorder(null);
        s.setOpaque(false);
        s.getViewport().setOpaque(false);
        s.getVerticalScrollBar().setUnitIncrement(18);
        s.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return s;
    }

    private JPanel vacio(String mensaje) {
        PanelCaja caja = new PanelCaja(16, Color.WHITE, BORDE);
        caja.setLayout(new GridBagLayout());
        caja.setBorder(new EmptyBorder(45, 30, 45, 30));
        JLabel l = new JLabel(mensaje);
        l.setFont(new Font(FUENTE, Font.BOLD, 14));
        l.setForeground(GRIS_CLARO);
        caja.add(l);
        return caja;
    }

    private JPanel error(String mensaje) {
        PanelCaja caja = new PanelCaja(16, ROJO_PILL, new Color(244, 190, 190));
        caja.setLayout(new GridBagLayout());
        caja.setBorder(new EmptyBorder(28, 30, 28, 30));
        JLabel l = new JLabel("<html>" + escapar(seguro(mensaje, "Error")) + "</html>");
        l.setFont(new Font(FUENTE, Font.BOLD, 13));
        l.setForeground(ROJO);
        caja.add(l);
        return caja;
    }

    private JPanel pastilla(String texto, Color fondo, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(FUENTE, Font.BOLD, 10));
        l.setForeground(color);
        FontMetrics fm = l.getFontMetrics(l.getFont());
        PanelCaja p = new PanelCaja(20, fondo, null);
        p.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p.setPreferredSize(new Dimension(fm.stringWidth(texto) + 22, 22));
        p.add(l);
        return p;
    }

    private JPanel pastillaEstado(String estado) {
        return switch (estado) {
            case "ACEPTADA" -> pastilla(estado, new Color(226, 240, 252), AZUL);
            case "EN_CURSO" -> pastilla(estado, new Color(238, 232, 251), new Color(116, 88, 190));
            case "FINALIZADA" -> pastilla(estado, VERDE_PILL, VERDE);
            case "RECHAZADA", "CANCELADA" -> pastilla(estado, ROJO_PILL, ROJO);
            default -> pastilla(estado, AMARILLO_PILL, AMARILLO_TXT);
        };
    }

    private JLabel cargarImagen(String ruta, int ancho, int alto) {
        JLabel l = new JLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setVerticalAlignment(SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(ancho, alto));
        l.setOpaque(true);
        l.setBackground(new Color(247, 250, 253));

        try {
            BufferedImage img = ImagenAlojamientoUtil.leer(ruta);

            if (img != null) {
                Image esc = img.getScaledInstance(
                        ancho, alto, Image.SCALE_SMOOTH);
                l.setIcon(new ImageIcon(esc));
                l.setText(null);
                return l;
            }
        } catch (Exception ignored) {
        }

        l.setText("Sin imagen");
        l.setForeground(GRIS_CLARO);
        return l;
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Pet Home Boarding",
                JOptionPane.WARNING_MESSAGE);
    }

    private void refrescar(JPanel p) {
        p.revalidate();
        p.repaint();
    }

    private String dinero(double v) {
        return "$ " + String.format("%,.2f", v);
    }

    private String seguro(String s, String defecto) {
        return s == null || s.isBlank() ? defecto : s;
    }

    private String corto(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private String escapar(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
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
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    radio, radio);
            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        radio, radio);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonRedondeado extends JButton {
        private final Color fondo;
        private final Color borde;
        private final Color segundo;

        BotonRedondeado(String texto, Color fondo, Color textoColor,
                        Color borde, Color segundo) {
            super(texto);
            this.fondo = fondo;
            this.borde = borde;
            this.segundo = segundo;
            setForeground(textoColor);
            setFont(new Font(FUENTE, Font.BOLD, 13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (segundo != null) {
                g2.setPaint(new GradientPaint(0, 0, fondo,
                        getWidth(), getHeight(), segundo));
            } else {
                g2.setColor(fondo);
            }
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                    14, 14);
            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                        14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class BotonMenu extends JButton {
        private final String seccion;
        private boolean activo;

        BotonMenu(String texto, String seccion) {
            super(texto);
            this.seccion = seccion;
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(new Font(FUENTE, Font.BOLD, 13));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            setPreferredSize(new Dimension(195, 44));
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        void setActivo(boolean activo) {
            this.activo = activo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (activo || getModel().isRollover()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(activo ? LATERAL_HOVER : new Color(255,255,255,18));
                g2.fill(new RoundRectangle2D.Float(
                        0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}

