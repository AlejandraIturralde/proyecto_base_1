
package vista;

import dao.CuidadorDAO;
import dao.UsuarioDAO;
import modelo.Cuidador;
import modelo.Usuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class RegistroUsuario extends JFrame {

    private static final Color AZUL          = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO   = new Color(16, 62, 108);
    private static final Color AZUL_GRAD_1   = new Color(22, 104, 214);
    private static final Color AZUL_GRAD_2   = new Color(41, 137, 226);
    private static final Color FONDO_IZQ     = new Color(244, 249, 254);
    private static final Color FONDO_DER     = new Color(238, 246, 253);
    private static final Color FONDO_CAMPO   = new Color(247, 250, 253);
    private static final Color BORDE_CAMPO   = new Color(226, 236, 246);
    private static final Color GRIS_TEXTO    = new Color(112, 133, 153);
    private static final Color GRIS_PISTA    = new Color(150, 167, 182);
    private static final Color AMARILLO      = new Color(242, 194, 48);
    private static final Color PATRON        = new Color(228, 240, 250);

    private static final String FUENTE = "Segoe UI";

    private static final int PERSONA    = 0;
    private static final int TARJETA    = 1;
    private static final int CALENDARIO = 2;
    private static final int SOBRE      = 3;
    private static final int TELEFONO   = 4;
    private static final int UBICACION  = 5;
    private static final int CANDADO    = 6;
    private static final int OJO        = 7;
    private static final int HUELLA     = 8;
    private static final int FLECHA     = 9;
    private static final int CAMARA     = 10;

    private static final String[] MESES = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre",
            "Noviembre", "Diciembre"
    };

    private static final Map<String, List<String>> UBICACIONES_ECUADOR =
            crearUbicacionesEcuador();

    private static Map<String, List<String>> crearUbicacionesEcuador() {
        Map<String, List<String>> ubicaciones = new LinkedHashMap<>();

        ubicaciones.put("Azuay", List.of(
                "Cuenca", "Camilo Ponce Enríquez", "Chordeleg",
                "El Pan", "Girón", "Guachapala", "Gualaceo",
                "Nabón", "Oña", "Paute", "Pucará", "San Fernando",
                "Santa Isabel", "Sevilla de Oro", "Sígsig"));
        ubicaciones.put("Bolívar", List.of(
                "Guaranda", "Caluma", "Chillanes", "Chimbo",
                "Echeandía", "Las Naves", "San Miguel"));
        ubicaciones.put("Cañar", List.of(
                "Azogues", "Biblián", "Cañar", "Déleg", "El Tambo",
                "La Troncal", "Suscal"));
        ubicaciones.put("Carchi", List.of(
                "Tulcán", "Bolívar", "Espejo", "Mira", "Montúfar",
                "San Pedro de Huaca"));
        ubicaciones.put("Chimborazo", List.of(
                "Riobamba", "Alausí", "Chambo", "Chunchi", "Colta",
                "Cumandá", "Guamote", "Guano", "Pallatanga",
                "Penipe"));
        ubicaciones.put("Cotopaxi", List.of(
                "Latacunga", "La Maná", "Pangua", "Pujilí",
                "Salcedo", "Saquisilí", "Sigchos"));
        ubicaciones.put("El Oro", List.of(
                "Machala", "Arenillas", "Atahualpa", "Balsas",
                "Chilla", "El Guabo", "Huaquillas", "Las Lajas",
                "Marcabelí", "Pasaje", "Piñas", "Portovelo",
                "Santa Rosa", "Zaruma"));
        ubicaciones.put("Esmeraldas", List.of(
                "Esmeraldas", "Atacames", "Eloy Alfaro", "Muisne",
                "Quinindé", "Rioverde", "San Lorenzo"));
        ubicaciones.put("Galápagos", List.of(
                "San Cristóbal", "Isabela", "Santa Cruz"));
        ubicaciones.put("Guayas", List.of(
                "Guayaquil", "Alfredo Baquerizo Moreno (Jujan)",
                "Balao", "Balzar", "Colimes", "Daule", "Durán",
                "El Empalme", "El Triunfo",
                "General Antonio Elizalde (Bucay)", "Isidro Ayora",
                "Lomas de Sargentillo", "Marcelino Maridueña",
                "Milagro", "Naranjal", "Naranjito", "Nobol",
                "Palestina", "Pedro Carbo", "Playas", "Salitre",
                "Samborondón", "Santa Lucía", "Simón Bolívar",
                "Yaguachi"));
        ubicaciones.put("Imbabura", List.of(
                "Ibarra", "Antonio Ante", "Cotacachi", "Otavalo",
                "Pimampiro", "San Miguel de Urcuquí"));
        ubicaciones.put("Loja", List.of(
                "Loja", "Calvas", "Catamayo", "Celica",
                "Chaguarpamba", "Espíndola", "Gonzanamá", "Macará",
                "Olmedo", "Paltas", "Pindal", "Puyango", "Quilanga",
                "Saraguro", "Sozoranga", "Zapotillo"));
        ubicaciones.put("Los Ríos", List.of(
                "Babahoyo", "Baba", "Buena Fe", "Mocache",
                "Montalvo", "Palenque", "Pueblo Viejo", "Quevedo",
                "Quinsaloma", "Urdaneta", "Valencia", "Ventanas",
                "Vinces"));
        ubicaciones.put("Manabí", List.of(
                "Portoviejo", "24 de Mayo", "Bolívar", "Chone",
                "El Carmen", "Flavio Alfaro", "Jama", "Jaramijó",
                "Jipijapa", "Junín", "Manta", "Montecristi",
                "Olmedo", "Paján", "Pedernales", "Pichincha",
                "Puerto López", "Rocafuerte", "San Vicente",
                "Santa Ana", "Sucre", "Tosagua"));
        ubicaciones.put("Morona Santiago", List.of(
                "Morona", "Gualaquiza", "Huamboya", "Limón Indanza",
                "Logroño", "Pablo Sexto", "Palora", "San Juan Bosco",
                "Santiago", "Sucúa", "Taisha", "Tiwintza"));
        ubicaciones.put("Napo", List.of(
                "Tena", "Archidona", "Carlos Julio Arosemena Tola",
                "El Chaco", "Quijos"));
        ubicaciones.put("Orellana", List.of(
                "Francisco de Orellana", "Aguarico",
                "La Joya de los Sachas", "Loreto"));
        ubicaciones.put("Pastaza", List.of(
                "Pastaza", "Arajuno", "Mera", "Santa Clara"));
        ubicaciones.put("Pichincha", List.of(
                "Quito", "Cayambe", "Mejía", "Pedro Moncayo",
                "Pedro Vicente Maldonado", "Puerto Quito", "Rumiñahui",
                "San Miguel de los Bancos"));
        ubicaciones.put("Santa Elena", List.of(
                "Santa Elena", "La Libertad", "Salinas"));
        ubicaciones.put("Santo Domingo de los Tsáchilas", List.of(
                "Santo Domingo", "La Concordia"));
        ubicaciones.put("Sucumbíos", List.of(
                "Lago Agrio", "Cascales", "Cuyabeno",
                "Gonzalo Pizarro", "Putumayo", "Shushufindi",
                "Sucumbíos"));
        ubicaciones.put("Tungurahua", List.of(
                "Ambato", "Baños de Agua Santa", "Cevallos", "Mocha",
                "Patate", "Quero", "San Pedro de Pelileo",
                "Santiago de Píllaro", "Tisaleo"));
        ubicaciones.put("Zamora Chinchipe", List.of(
                "Zamora", "Centinela del Cóndor", "Chinchipe",
                "El Pangui", "Nangaritza", "Palanda", "Paquisha",
                "Yacuambi", "Yantzaza"));

        return Collections.unmodifiableMap(ubicaciones);
    }

    private CampoTexto txtNombre;
    private CampoTexto txtApellido;
    private CampoTexto txtCedula;
    private CampoTexto txtCorreo;
    private CampoTexto txtTelefono;
    private ComboBuscable cboProvincia;
    private ComboBuscable cboCiudad;
    private CampoTexto txtDireccionEspecifica;
    private String provinciaCiudades;

    private CampoClave txtContrasena;
    private CampoClave txtConfirmarContrasena;

    private SelectorFecha selectorFecha;

    private JCheckBox chkQuieroSerCuidador;

    private String rutaFoto = null;

    private JLabel lblVistaPrevia;
    private JLabel lblNombreArchivo;

    public RegistroUsuario() {
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Crear cuenta - Pet Home Boarding");
        setSize(1260, 850);
        setMinimumSize(new Dimension(1150, 760));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void crearComponentes() {

        JPanel principal = new JPanel(new BorderLayout());
        principal.setBackground(FONDO_DER);

        principal.add(crearPanelIzquierdo(), BorderLayout.WEST);
        principal.add(crearPanelDerecho(), BorderLayout.CENTER);

        setContentPane(principal);
    }

    private ImageIcon icono(int tipo, Color color, int lado) {

        BufferedImage imagen = new BufferedImage(
                lado, lado, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = imagen.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(color);
        g2.scale(lado / 100.0, lado / 100.0);
        g2.setStroke(new BasicStroke(
                8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND
        ));

        switch (tipo) {

            case PERSONA -> {
                g2.drawOval(34, 14, 32, 32);
                g2.draw(new Arc2D.Float(
                        18, 52, 64, 60, 0, 180, Arc2D.OPEN
                ));
            }

            case TARJETA -> {
                g2.drawRoundRect(8, 24, 84, 56, 12, 12);
                g2.drawOval(24, 38, 18, 18);
                g2.drawLine(56, 44, 80, 44);
                g2.drawLine(56, 60, 80, 60);
            }

            case CALENDARIO -> {
                g2.drawRoundRect(10, 22, 80, 70, 12, 12);
                g2.drawLine(10, 44, 90, 44);
                g2.drawLine(32, 10, 32, 30);
                g2.drawLine(68, 10, 68, 30);
            }

            case SOBRE -> {
                g2.drawRoundRect(8, 24, 84, 54, 10, 10);
                g2.drawLine(14, 30, 50, 56);
                g2.drawLine(86, 30, 50, 56);
            }

            case TELEFONO -> {
                g2.drawRoundRect(28, 8, 44, 84, 12, 12);
                g2.drawLine(44, 78, 56, 78);
            }

            case UBICACION -> {
                g2.draw(new Arc2D.Float(
                        22, 10, 56, 56, 0, 360, Arc2D.OPEN
                ));
                g2.drawLine(30, 56, 50, 90);
                g2.drawLine(70, 56, 50, 90);
                g2.fillOval(40, 28, 20, 20);
            }

            case CANDADO -> {
                g2.fillRoundRect(16, 46, 68, 44, 12, 12);
                g2.draw(new Arc2D.Float(
                        30, 16, 40, 44, 0, 180, Arc2D.OPEN
                ));
            }

            case OJO -> {
                g2.drawOval(6, 30, 88, 40);
                g2.fillOval(39, 39, 22, 22);
            }

            case HUELLA -> {
                g2.fillOval(26, 46, 48, 40);
                g2.fillOval(14, 20, 20, 26);
                g2.fillOval(40, 8, 20, 26);
                g2.fillOval(66, 20, 20, 26);
            }

            case FLECHA -> {
                g2.drawLine(78, 50, 22, 50);
                g2.drawLine(22, 50, 46, 26);
                g2.drawLine(22, 50, 46, 74);
            }

            case CAMARA -> {
                g2.drawRoundRect(8, 30, 84, 60, 12, 12);
                g2.drawLine(34, 30, 42, 16);
                g2.drawLine(66, 30, 58, 16);
                g2.drawLine(42, 16, 58, 16);
                g2.drawOval(34, 46, 32, 32);
            }

            default -> {
            }
        }

        g2.dispose();

        return new ImageIcon(imagen);
    }

    private JPanel crearPanelIzquierdo() {

        PanelPatron panel = new PanelPatron(FONDO_IZQ);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(560, 0));
        panel.setBorder(new EmptyBorder(34, 46, 34, 40));

        JPanel filaMarca = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 14, 0)
        );
        filaMarca.setOpaque(false);
        filaMarca.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaMarca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        filaMarca.add(crearLogo(58, 14));
        filaMarca.add(crearTextoMarca(19, 44));

        JLabel lblTitulo = new JLabel("Para toda mascota");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 40));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        PanelCaja guion = new PanelCaja(4, AMARILLO, null);
        guion.setMaximumSize(new Dimension(62, 6));
        guion.setPreferredSize(new Dimension(62, 6));
        guion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTexto = new JLabel(
                "<html><div style='text-align:center; width:420px;'>"
                        + "Alojamiento confiable para perros, gatos, aves,<br>"
                        + "conejos, peces, reptiles y más.<br>"
                        + "Porque cada mascota merece cuidado y cariño."
                        + "</div></html>"
        );
        lblTexto.setFont(new Font(FUENTE, Font.PLAIN, 15));
        lblTexto.setForeground(GRIS_TEXTO);
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTexto.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblIlustracion = new JLabel();
        lblIlustracion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIlustracion.setHorizontalAlignment(SwingConstants.CENTER);

        File dibujo = buscarArchivo("mascotas");

        if (dibujo != null) {
            Image imagen = escalarConCalidad(dibujo, 430, 260, false);
            if (imagen != null) {
                lblIlustracion.setIcon(new ImageIcon(imagen));
            }
        }

        JPanel filaPie = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 0)
        );
        filaPie.setOpaque(false);
        filaPie.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaPie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

        filaPie.add(crearPunto("Perfiles verificados"));
        filaPie.add(Box.createHorizontalStrut(4));
        filaPie.add(crearPunto("Fotos diarias"));
        filaPie.add(Box.createHorizontalStrut(4));
        filaPie.add(crearPunto("Reserva fácil"));

        panel.add(filaMarca);
        panel.add(Box.createVerticalStrut(46));
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(14));
        panel.add(guion);
        panel.add(Box.createVerticalStrut(22));
        panel.add(lblTexto);
        panel.add(Box.createVerticalStrut(18));
        panel.add(lblIlustracion);
        panel.add(Box.createVerticalGlue());
        panel.add(filaPie);

        return panel;
    }

    private JPanel crearTextoMarca(int tamano, int anchoGuion) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(
                "<html>Pet Home<br>Boarding</html>"
        );
        lbl.setFont(new Font(FUENTE, Font.BOLD, tamano));
        lbl.setForeground(AZUL);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelCaja guion = new PanelCaja(3, AMARILLO, null);
        guion.setMaximumSize(new Dimension(anchoGuion, 5));
        guion.setPreferredSize(new Dimension(anchoGuion, 5));
        guion.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(guion);

        return panel;
    }

    private JPanel crearPunto(String texto) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        panel.setOpaque(false);

        PanelCaja punto = new PanelCaja(6, AMARILLO, null);
        punto.setPreferredSize(new Dimension(7, 7));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lbl.setForeground(AZUL);

        panel.add(punto);
        panel.add(lbl);

        return panel;
    }

    private JPanel crearPanelDerecho() {

        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(FONDO_DER);

        PanelCaja tarjeta = new PanelCaja(24, Color.WHITE, null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(26, 40, 30, 40));

        JButton btnVolver = new JButton("Volver");
        btnVolver.setIcon(icono(FLECHA, GRIS_TEXTO, 16));
        btnVolver.setIconTextGap(7);
        btnVolver.setFont(new Font(FUENTE, Font.BOLD, 13));
        btnVolver.setForeground(GRIS_TEXTO);
        btnVolver.setBorderPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setMargin(new Insets(0, 0, 0, 0));
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel filaVolver = new JPanel(new BorderLayout());
        filaVolver.setOpaque(false);
        filaVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaVolver.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        filaVolver.add(btnVolver, BorderLayout.WEST);

        JPanel filaMarca = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 12, 0)
        );
        filaMarca.setOpaque(false);
        filaMarca.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaMarca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        filaMarca.add(crearLogo(52, 13));
        filaMarca.add(crearTextoMarca(16, 38));

        JLabel lblTitulo = new JLabel("Crear cuenta");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 31));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel(
                "Regístrate para reservar o para ofrecer cuidado."
        );
        lblSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSubtitulo.setForeground(GRIS_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtNombre = new CampoTexto("Ingresa tus nombres");
        txtApellido = new CampoTexto("Ingresa tus apellidos");
        txtCedula = new CampoTexto("10 dígitos");
        txtCorreo = new CampoTexto("ejemplo@correo.com");
        txtTelefono = new CampoTexto("09XXXXXXXX");
        cboProvincia = new ComboBuscable(
                "Selecciona una provincia",
                new ArrayList<>(UBICACIONES_ECUADOR.keySet()));
        cboCiudad = new ComboBuscable(
                "Selecciona una ciudad", Collections.emptyList());
        cboCiudad.setEnabled(false);
        txtDireccionEspecifica = new CampoTexto(
                "Ej. Cdla. Alborada, etapa 8, mz. 12");

        cboProvincia.addActionListener(e -> actualizarCiudades());
        cboProvincia.editorTexto().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                actualizarCiudades();
            }
        });

        txtContrasena = new CampoClave("Crea una contraseña");
        txtConfirmarContrasena =
                new CampoClave("Confirma tu contraseña");

        aplicarFiltroNumerico(txtCedula, 10);
        aplicarFiltroNumerico(txtTelefono, 10);

        selectorFecha = new SelectorFecha();

        JPanel rejilla = new JPanel(new GridBagLayout());
        rejilla.setOpaque(false);
        rejilla.setAlignmentX(Component.CENTER_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        colocar(rejilla, gbc, 0, 0, 1,
                bloque("Nombres", caja(PERSONA, txtNombre, false)));
        colocar(rejilla, gbc, 1, 0, 1,
                bloque("Apellidos", caja(PERSONA, txtApellido, false)));

        colocar(rejilla, gbc, 0, 1, 1,
                bloque("Cédula", caja(TARJETA, txtCedula, false)));
        colocar(rejilla, gbc, 1, 1, 1,
                bloque("Fecha de nacimiento", selectorFecha));

        colocar(rejilla, gbc, 0, 2, 1,
                bloque("Correo electrónico", caja(SOBRE, txtCorreo, false)));
        colocar(rejilla, gbc, 1, 2, 1,
                bloque("Teléfono", caja(TELEFONO, txtTelefono, false)));

        colocar(rejilla, gbc, 0, 3, 1,
                bloque("Provincia",
                        cajaCombo(UBICACION, cboProvincia)));
        colocar(rejilla, gbc, 1, 3, 1,
                bloque("Ciudad", cajaCombo(UBICACION, cboCiudad)));

        colocar(rejilla, gbc, 0, 4, 2,
                bloque("Dirección específica", caja(UBICACION,
                        txtDireccionEspecifica, false)));

        colocar(rejilla, gbc, 0, 5, 2,
                bloque("Foto de perfil (opcional)", crearCampoFoto()));

        colocar(rejilla, gbc, 0, 6, 1,
                bloque("Contraseña", caja(CANDADO, txtContrasena, true)));
        colocar(rejilla, gbc, 1, 6, 1,
                bloque("Confirmar contraseña",
                        caja(CANDADO, txtConfirmarContrasena, true)));

        PanelCaja cajaCuidador =
                new PanelCaja(14, FONDO_CAMPO, BORDE_CAMPO);
        cajaCuidador.setLayout(
                new BoxLayout(cajaCuidador, BoxLayout.Y_AXIS)
        );
        cajaCuidador.setBorder(new EmptyBorder(14, 16, 14, 16));
        cajaCuidador.setAlignmentX(Component.CENTER_ALIGNMENT);
        cajaCuidador.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 86)
        );

        chkQuieroSerCuidador = new JCheckBox(
                "Quiero ofrecer mi hogar para cuidar mascotas"
        );
        chkQuieroSerCuidador.setFont(new Font(FUENTE, Font.BOLD, 14));
        chkQuieroSerCuidador.setForeground(AZUL_OSCURO);
        chkQuieroSerCuidador.setOpaque(false);
        chkQuieroSerCuidador.setFocusPainted(false);
        chkQuieroSerCuidador.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkQuieroSerCuidador.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblAviso = new JLabel(
                "<html>Al terminar te pediremos unos datos más. "
                        + "Tu perfil quedará en revisión<br>"
                        + "hasta que un administrador lo apruebe.</html>"
        );
        lblAviso.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblAviso.setForeground(GRIS_TEXTO);
        lblAviso.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblAviso.setBorder(new EmptyBorder(4, 25, 0, 0));

        cajaCuidador.add(chkQuieroSerCuidador);
        cajaCuidador.add(lblAviso);

        BotonRedondeado btnCrear = new BotonRedondeado(
                "Crear cuenta", AZUL_GRAD_1, Color.WHITE,
                null, AZUL_GRAD_2
        );

        JLabel lblPie = new JLabel(
                "<html><span style='color:#708599;'>"
                        + "¿Ya tienes cuenta? </span>"
                        + "<b style='color:#1668D6;'>Inicia sesión</b></html>"
        );
        lblPie.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblPie.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel filaPie = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 0)
        );
        filaPie.setOpaque(false);
        filaPie.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaPie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        filaPie.add(lblPie);

        tarjeta.add(filaVolver);
        tarjeta.add(Box.createVerticalStrut(4));
        tarjeta.add(filaMarca);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(lblSubtitulo);
        tarjeta.add(Box.createVerticalStrut(22));
        tarjeta.add(rejilla);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(cajaCuidador);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(btnCrear);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(filaPie);

        btnVolver.addActionListener(e -> volverLogin());
        btnCrear.addActionListener(e -> registrarUsuario());

        lblPie.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                volverLogin();
            }
        });

        GridBagConstraints centro = new GridBagConstraints();
        centro.gridx = 0;
        centro.gridy = 0;

        fondo.add(tarjeta, centro);

        JScrollPane scroll = new JScrollPane(fondo);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO_DER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO_DER);
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearCampoFoto() {

        PanelCaja caja = new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(12, 0));
        caja.setBorder(new EmptyBorder(7, 10, 7, 10));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        caja.setPreferredSize(new Dimension(220, 58));

        lblVistaPrevia = new JLabel(icono(CAMARA, GRIS_PISTA, 26));
        lblVistaPrevia.setHorizontalAlignment(SwingConstants.CENTER);
        lblVistaPrevia.setPreferredSize(new Dimension(44, 44));

        lblNombreArchivo =
                new JLabel("Ningún archivo seleccionado");
        lblNombreArchivo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblNombreArchivo.setForeground(GRIS_PISTA);

        JButton btnElegir = new JButton("Seleccionar");
        btnElegir.setFont(new Font(FUENTE, Font.BOLD, 13));
        btnElegir.setForeground(AZUL);
        btnElegir.setBorderPainted(false);
        btnElegir.setContentAreaFilled(false);
        btnElegir.setFocusPainted(false);
        btnElegir.setMargin(new Insets(0, 0, 0, 0));
        btnElegir.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnElegir.addActionListener(e -> elegirFoto());

        caja.add(lblVistaPrevia, BorderLayout.WEST);
        caja.add(lblNombreArchivo, BorderLayout.CENTER);
        caja.add(btnElegir, BorderLayout.EAST);

        return caja;
    }

    private void elegirFoto() {

        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Selecciona tu foto de perfil");
        selector.setAcceptAllFileFilterUsed(false);

        selector.setFileFilter(new FileNameExtensionFilter(
                "Imágenes (PNG, JPG)", "png", "jpg", "jpeg"
        ));

        if (selector.showOpenDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = selector.getSelectedFile();
        String nombre = archivo.getName().toLowerCase();

        if (!nombre.endsWith(".png")
                && !nombre.endsWith(".jpg")
                && !nombre.endsWith(".jpeg")) {

            advertir("Solo se permiten imágenes PNG o JPG.");
            return;
        }

        rutaFoto = archivo.getAbsolutePath();

        lblNombreArchivo.setText(archivo.getName());
        lblNombreArchivo.setForeground(AZUL_OSCURO);

        Image previa = escalarConCalidad(archivo, 42, 42, true);

        if (previa != null) {
            lblVistaPrevia.setIcon(new ImageIcon(previa));
        }
    }

    private void colocar(JPanel panel, GridBagConstraints gbc,
                         int columna, int fila, int ancho,
                         JComponent componente) {

        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.gridwidth = ancho;
        gbc.insets = new Insets(
                0,
                columna == 0 ? 0 : 8,
                14,
                (columna == 0 && ancho == 1) ? 8 : 0
        );

        panel.add(componente, gbc);
    }

    private JPanel bloque(String etiqueta, JComponent campo) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(AZUL_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(campo);

        return panel;
    }

    private JPanel caja(int tipoIcono, JTextField campo,
                        boolean conOjo) {

        PanelCaja caja = new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(9, 0));
        caja.setBorder(new EmptyBorder(0, 12, 0, 12));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        caja.setPreferredSize(new Dimension(220, 42));

        campo.setBorder(null);
        campo.setOpaque(false);
        campo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        campo.setForeground(AZUL_OSCURO);

        caja.add(new JLabel(icono(tipoIcono, GRIS_PISTA, 17)),
                BorderLayout.WEST);
        caja.add(campo, BorderLayout.CENTER);

        if (conOjo) {

            JLabel ojo = new JLabel(icono(OJO, GRIS_PISTA, 18));
            ojo.setCursor(new Cursor(Cursor.HAND_CURSOR));

            ojo.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    JPasswordField clave = (JPasswordField) campo;

                    boolean oculta = clave.getEchoChar() != 0;

                    clave.setEchoChar(oculta ? (char) 0 : '•');

                    ojo.setIcon(icono(
                            OJO, oculta ? AZUL : GRIS_PISTA, 18
                    ));
                }
            });

            caja.add(ojo, BorderLayout.EAST);
        }

        return caja;
    }

    private JPanel cajaCombo(int tipoIcono, ComboBuscable combo) {
        PanelCaja caja = new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
        caja.setLayout(new BorderLayout(9, 0));
        caja.setBorder(new EmptyBorder(0, 12, 0, 8));
        caja.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        caja.setPreferredSize(new Dimension(220, 42));

        combo.setBorder(null);
        combo.setOpaque(false);
        combo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        combo.editorTexto().setBorder(null);
        combo.editorTexto().setOpaque(false);
        combo.editorTexto().setForeground(AZUL_OSCURO);

        caja.add(new JLabel(icono(tipoIcono, GRIS_PISTA, 17)),
                BorderLayout.WEST);
        caja.add(combo, BorderLayout.CENTER);
        return caja;
    }

    private void actualizarCiudades() {
        if (cboProvincia == null || cboCiudad == null
                || cboProvincia.estaActualizando()) {
            return;
        }

        String provincia = cboProvincia.valorValido();
        if (Objects.equals(provincia, provinciaCiudades)) {
            return;
        }

        provinciaCiudades = provincia;
        if (provincia == null) {
            cboCiudad.establecerOpciones(Collections.emptyList());
            cboCiudad.setEnabled(false);
        } else {
            cboCiudad.establecerOpciones(
                    UBICACIONES_ECUADOR.get(provincia));
            cboCiudad.setEnabled(true);
        }
    }

    private void aplicarFiltroNumerico(JTextField campo, int maximo) {
        PlainDocument documento = (PlainDocument) campo.getDocument();
        documento.setDocumentFilter(new FiltroNumerico(maximo));
    }


    private JComponent crearLogo(int lado, int radio) {

        File archivo = buscarArchivo("logo");

        if (archivo != null) {
            Image imagen = escalarConCalidad(archivo, lado, lado, true);
            if (imagen != null) {
                return new JLabel(new ImageIcon(imagen));
            }
        }

        return new JLabel(icono(HUELLA, AZUL, lado));
    }

    private File buscarArchivo(String prefijo) {

        File carpeta = new File(System.getProperty("user.dir"));
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return null;
        }

        for (File archivo : archivos) {

            String nombre = archivo.getName().toLowerCase();

            if (nombre.startsWith(prefijo)
                    && (nombre.endsWith(".png")
                    || nombre.endsWith(".jpg")
                    || nombre.endsWith(".jpeg"))) {

                return archivo;
            }
        }

        return null;
    }

    private Image escalarConCalidad(File archivo, int anchoFinal,
                                    int altoFinal, boolean redondear) {
        try {

            BufferedImage imagen = ImageIO.read(archivo);

            if (imagen == null) {
                return null;
            }

            // Mantener proporción si la imagen no es cuadrada
            double proporcion = (double) imagen.getWidth()
                    / imagen.getHeight();

            if (!redondear) {
                if (proporcion > (double) anchoFinal / altoFinal) {
                    altoFinal = (int) (anchoFinal / proporcion);
                } else {
                    anchoFinal = (int) (altoFinal * proporcion);
                }
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            while (ancho / 2 >= anchoFinal && alto / 2 >= altoFinal) {

                ancho = ancho / 2;
                alto = alto / 2;

                BufferedImage paso = new BufferedImage(
                        ancho, alto, BufferedImage.TYPE_INT_ARGB
                );

                Graphics2D g2 = paso.createGraphics();
                g2.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR
                );
                g2.drawImage(imagen, 0, 0, ancho, alto, null);
                g2.dispose();

                imagen = paso;
            }

            BufferedImage resultado = new BufferedImage(
                    anchoFinal, altoFinal, BufferedImage.TYPE_INT_ARGB
            );

            Graphics2D g2 = resultado.createGraphics();
            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );
            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (redondear) {
                g2.setClip(new RoundRectangle2D.Float(
                        0, 0, anchoFinal, altoFinal, 16, 16
                ));
            }

            g2.drawImage(imagen, 0, 0, anchoFinal, altoFinal, null);
            g2.dispose();

            return resultado;

        } catch (IOException e) {
            return null;
        }
    }

    private void registrarUsuario() {

        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String cedula = txtCedula.getText().trim();
        String correo = txtCorreo.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String provincia = cboProvincia.valorValido();
        actualizarCiudades();
        String ciudad = cboCiudad.valorValido();
        String direccionEspecifica =
                txtDireccionEspecifica.getText().trim();

        String contrasena =
                new String(txtContrasena.getPassword());
        String confirmar =
                new String(txtConfirmarContrasena.getPassword());

        if (nombre.isBlank() || apellido.isBlank()
                || cedula.isBlank() || correo.isBlank()
                || telefono.isBlank()
                || contrasena.isBlank() || confirmar.isBlank()) {

            advertir("Complete todos los campos.");
            return;
        }

        if (provincia == null) {
            advertir("Selecciona una provincia.");
            cboProvincia.requestFocusInWindow();
            return;
        }

        if (ciudad == null) {
            advertir("Selecciona una ciudad.");
            cboCiudad.requestFocusInWindow();
            return;
        }

        if (direccionEspecifica.isBlank()) {
            advertir("Ingresa tu dirección específica.");
            txtDireccionEspecifica.requestFocus();
            return;
        }

        if (selectorFecha.getFecha() == null) {
            advertir("Seleccione su fecha de nacimiento.");
            return;
        }

        if (selectorFecha.getFecha().isAfter(LocalDate.now())) {
            advertir("La fecha de nacimiento no puede ser futura.");
            return;
        }

        if (!cedula.matches("\\d{10}")) {
            advertir("La cédula debe tener exactamente 10 números.");
            txtCedula.requestFocus();
            return;
        }

        if (!telefono.matches("09\\d{8}")) {
            advertir("El teléfono debe empezar con 09 y tener 10 números.\n"
                    + "Ejemplo: 0991234567");
            txtTelefono.requestFocus();
            return;
        }

        if (!correo.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            advertir("Ingrese un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }

        if (contrasena.length() < 6) {
            advertir("La contraseña debe tener al menos 6 caracteres.");
            txtContrasena.requestFocus();
            return;
        }

        if (!contrasena.equals(confirmar)) {
            advertir("Las contraseñas no coinciden.");
            txtConfirmarContrasena.requestFocus();
            return;
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setCedula(cedula);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setDireccionDomicilio(provincia + ", " + ciudad
                + ", " + direccionEspecifica);
        usuario.setContrasenaHash(contrasena);
        usuario.setFechaNacimiento(
                Date.valueOf(selectorFecha.getFecha())
        );

        usuario.setFotoPerfil(rutaFoto);

        usuario.setRolSistema("USUARIO");

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        try {

            int idUsuario = usuarioDAO.registrarUsuario(usuario);

            if (idUsuario > 0) {

                usuario.setIdUsuario(idUsuario);

                if (chkQuieroSerCuidador.isSelected()) {
                    new DialogoCuidador(this, idUsuario)
                            .setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cuenta creada correctamente.",
                            "Registro exitoso",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                Login login = new Login();
                login.setVisible(true);
                dispose();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo crear la cuenta.\n"
                                + "Revise que el correo y la cédula "
                                + "no estén registrados.",
                        "Registro no completado",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (RuntimeException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error de registro",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void advertir(String mensaje) {
        JOptionPane.showMessageDialog(
                this, mensaje,
                "Datos incorrectos",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void volverLogin() {
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }

    private static class ComboBuscable extends JComboBox<String> {

        private final String pista;
        private final List<String> opciones = new ArrayList<>();
        private boolean actualizando;

        ComboBuscable(String pista, List<String> opcionesIniciales) {
            this.pista = pista;
            setEditable(true);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(220, 42));

            JTextField editor = editorTexto();
            editor.setFont(new Font(FUENTE, Font.PLAIN, 14));
            editor.setForeground(GRIS_PISTA);

            editor.getDocument().addDocumentListener(
                    new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) {
                            filtrar();
                        }

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                            filtrar();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                            filtrar();
                        }
                    });

            editor.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (pista.equals(editor.getText())) {
                        editor.selectAll();
                    }
                }
            });

            addActionListener(e -> editor.setForeground(
                    valorValido() == null ? GRIS_PISTA : AZUL_OSCURO));
            establecerOpciones(opcionesIniciales);
        }

        JTextField editorTexto() {
            return (JTextField) getEditor().getEditorComponent();
        }

        boolean estaActualizando() {
            return actualizando;
        }

        void establecerOpciones(List<String> nuevasOpciones) {
            actualizando = true;
            opciones.clear();
            if (nuevasOpciones != null) {
                opciones.addAll(nuevasOpciones);
            }

            DefaultComboBoxModel<String> modelo =
                    new DefaultComboBoxModel<>();
            modelo.addElement(pista);
            for (String opcion : opciones) {
                modelo.addElement(opcion);
            }
            setModel(modelo);
            setSelectedItem(pista);
            editorTexto().setForeground(GRIS_PISTA);
            actualizando = false;
        }

        String valorValido() {
            String texto = editorTexto().getText().trim();
            for (String opcion : opciones) {
                if (opcion.equalsIgnoreCase(texto)) {
                    return opcion;
                }
            }
            return null;
        }

        private void filtrar() {
            if (actualizando) {
                return;
            }

            String texto = editorTexto().getText();
            if (pista.equals(texto)) {
                return;
            }

            SwingUtilities.invokeLater(() -> {
                if (actualizando
                        || !texto.equals(editorTexto().getText())) {
                    return;
                }

                String busqueda = normalizar(texto);
                actualizando = true;
                DefaultComboBoxModel<String> filtrado =
                        new DefaultComboBoxModel<>();
                for (String opcion : opciones) {
                    if (busqueda.isBlank()
                            || normalizar(opcion).contains(busqueda)) {
                        filtrado.addElement(opcion);
                    }
                }

                setModel(filtrado);
                setSelectedItem(null);
                JTextField editor = editorTexto();
                editor.setText(texto);
                editor.setCaretPosition(texto.length());
                editor.setForeground(AZUL_OSCURO);
                actualizando = false;

                if (isDisplayable() && editor.hasFocus()
                        && filtrado.getSize() > 0) {
                    try {
                        showPopup();
                    } catch (IllegalComponentStateException ignored) {
                    }
                }
            });
        }

        private static String normalizar(String texto) {
            return Normalizer.normalize(texto,
                            Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase(Locale.ROOT)
                    .trim();
        }
    }

    private class CampoTexto extends JTextField {

        private final String pista;

        CampoTexto(String pista) {
            this.pista = pista;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarPista(g, this, pista);
        }
    }

    private class CampoClave extends JPasswordField {

        private final String pista;

        CampoClave(String pista) {
            this.pista = pista;
            setEchoChar('•');
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            dibujarPista(g, this, pista);
        }
    }

    private void dibujarPista(Graphics g, JTextField campo,
                              String pista) {

        if (!campo.getText().isEmpty()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        );

        g2.setColor(GRIS_PISTA);
        g2.setFont(campo.getFont());

        FontMetrics medidas = g2.getFontMetrics();
        int y = (campo.getHeight() + medidas.getAscent()
                - medidas.getDescent()) / 2;

        g2.drawString(pista, 2, y);
        g2.dispose();
    }

    private class SelectorFecha extends JPanel {

        private LocalDate seleccionada;
        private YearMonth mesMostrado = YearMonth.now().minusYears(20);

        private final JLabel texto = new JLabel("Selecciona la fecha");
        private final JPopupMenu emergente = new JPopupMenu();
        private final JLabel lblMes =
                new JLabel("", SwingConstants.CENTER);
        private final JPanel panelDias =
                new JPanel(new GridLayout(0, 7, 3, 3));

        SelectorFecha() {

            setLayout(new BorderLayout());
            setOpaque(false);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setPreferredSize(new Dimension(220, 42));

            PanelCaja caja =
                    new PanelCaja(10, FONDO_CAMPO, BORDE_CAMPO);
            caja.setLayout(new BorderLayout(9, 0));
            caja.setBorder(new EmptyBorder(0, 12, 0, 12));
            caja.setCursor(new Cursor(Cursor.HAND_CURSOR));

            texto.setFont(new Font(FUENTE, Font.PLAIN, 14));
            texto.setForeground(GRIS_PISTA);

            caja.add(new JLabel(icono(CALENDARIO, GRIS_PISTA, 17)),
                    BorderLayout.WEST);
            caja.add(texto, BorderLayout.CENTER);

            add(caja, BorderLayout.CENTER);

            construirEmergente();

            MouseAdapter abrir = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dibujarDias();
                    emergente.show(SelectorFecha.this, 0, getHeight());
                }
            };

            caja.addMouseListener(abrir);
            texto.addMouseListener(abrir);
        }

        private void construirEmergente() {

            JPanel panel = new JPanel(new BorderLayout(0, 8));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(12, 12, 12, 12));
            panel.setPreferredSize(new Dimension(340, 320));

            JPanel encabezado = new JPanel(new BorderLayout());
            encabezado.setOpaque(false);

            JPanel izquierda =
                    new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            izquierda.setOpaque(false);
            izquierda.add(flecha("«", -12));
            izquierda.add(flecha("‹", -1));

            JPanel derecha =
                    new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            derecha.setOpaque(false);
            derecha.add(flecha("›", 1));
            derecha.add(flecha("»", 12));

            lblMes.setFont(new Font(FUENTE, Font.BOLD, 15));
            lblMes.setForeground(AZUL_OSCURO);

            encabezado.add(izquierda, BorderLayout.WEST);
            encabezado.add(lblMes, BorderLayout.CENTER);
            encabezado.add(derecha, BorderLayout.EAST);

            panelDias.setOpaque(false);

            panel.add(encabezado, BorderLayout.NORTH);
            panel.add(panelDias, BorderLayout.CENTER);

            emergente.setBorder(
                    BorderFactory.createLineBorder(BORDE_CAMPO)
            );
            emergente.add(panel);
        }

        private JButton flecha(String texto, int meses) {

            JButton boton = new JButton(texto);
            boton.setFont(new Font(FUENTE, Font.BOLD, 15));
            boton.setForeground(AZUL);
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            boton.setMargin(new Insets(0, 0, 0, 0));
            boton.setBorder(null);
            boton.setPreferredSize(new Dimension(30, 28));
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            boton.addActionListener(e -> {
                mesMostrado = mesMostrado.plusMonths(meses);
                dibujarDias();
            });

            return boton;
        }

        private void dibujarDias() {

            panelDias.removeAll();

            lblMes.setText(
                    MESES[mesMostrado.getMonthValue() - 1]
                            + "  " + mesMostrado.getYear()
            );

            String[] nombres = {"L", "M", "M", "J", "V", "S", "D"};

            for (String nombre : nombres) {
                JLabel lbl = new JLabel(nombre, SwingConstants.CENTER);
                lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
                lbl.setForeground(GRIS_TEXTO);
                panelDias.add(lbl);
            }

            int desplazamiento =
                    mesMostrado.atDay(1).getDayOfWeek().getValue() - 1;

            for (int i = 0; i < desplazamiento; i++) {
                panelDias.add(new JLabel(""));
            }

            for (int dia = 1;
                 dia <= mesMostrado.lengthOfMonth();
                 dia++) {

                LocalDate fecha = mesMostrado.atDay(dia);

                JButton boton = new JButton(String.valueOf(dia));
                boton.setFont(new Font(FUENTE, Font.PLAIN, 13));
                boton.setFocusPainted(false);
                boton.setBorderPainted(false);
                boton.setOpaque(true);
                boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                boton.setMargin(new Insets(0, 0, 0, 0));
                boton.setBorder(null);
                boton.setPreferredSize(new Dimension(38, 30));

                if (fecha.equals(seleccionada)) {
                    boton.setBackground(AZUL);
                    boton.setForeground(Color.WHITE);
                } else {
                    boton.setBackground(Color.WHITE);
                    boton.setForeground(AZUL_OSCURO);
                }

                boton.addActionListener(e -> {
                    seleccionada = fecha;
                    texto.setText(fecha.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    ));
                    texto.setForeground(AZUL_OSCURO);
                    emergente.setVisible(false);
                });

                panelDias.add(boton);
            }

            panelDias.revalidate();
            panelDias.repaint();
        }

        LocalDate getFecha() {
            return seleccionada;
        }
    }
    private class DialogoCuidador extends JDialog {

        private final int idUsuario;

        private final JTextArea txtDescripcion = new JTextArea();
        private final JTextArea txtExperiencia = new JTextArea();

        DialogoCuidador(JFrame padre, int idUsuario) {

            super(padre, "Solicitud de cuidador", true);

            this.idUsuario = idUsuario;

            setSize(560, 570);
            setLocationRelativeTo(padre);
            setResizable(false);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(28, 34, 26, 34));

            JLabel lblTitulo = new JLabel("Cuéntanos sobre ti");
            lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 25));
            lblTitulo.setForeground(AZUL_OSCURO);
            lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblSub = new JLabel(
                    "<html>Esta información la revisará un administrador "
                            + "antes de<br>aprobar tu perfil de cuidador."
                            + "</html>"
            );
            lblSub.setFont(new Font(FUENTE, Font.PLAIN, 14));
            lblSub.setForeground(GRIS_TEXTO);
            lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(lblTitulo);
            panel.add(Box.createVerticalStrut(8));
            panel.add(lblSub);
            panel.add(Box.createVerticalStrut(22));
            panel.add(area("Descripción de tu perfil",
                    "Ej: Vivo en casa con patio cerrado y trabajo "
                            + "desde casa.",
                    txtDescripcion, 110));
            panel.add(Box.createVerticalStrut(16));
            panel.add(area("Tu experiencia cuidando mascotas",
                    "Ej: Tres años cuidando perros de amigos.",
                    txtExperiencia, 90));
            panel.add(Box.createVerticalStrut(22));

            BotonRedondeado btnEnviar = new BotonRedondeado(
                    "Enviar solicitud", AZUL_GRAD_1, Color.WHITE,
                    null, AZUL_GRAD_2
            );
            btnEnviar.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton btnOmitir = new JButton("Ahora no");
            btnOmitir.setFont(new Font(FUENTE, Font.BOLD, 13));
            btnOmitir.setForeground(GRIS_TEXTO);
            btnOmitir.setBorderPainted(false);
            btnOmitir.setContentAreaFilled(false);
            btnOmitir.setFocusPainted(false);
            btnOmitir.setMargin(new Insets(0, 0, 0, 0));
            btnOmitir.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnOmitir.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(btnEnviar);
            panel.add(Box.createVerticalStrut(10));
            panel.add(btnOmitir);

            btnEnviar.addActionListener(e -> enviarSolicitud());
            btnOmitir.addActionListener(e -> dispose());

            setContentPane(panel);
        }

        private JPanel area(String etiqueta, String pista,
                            JTextArea area, int alto) {

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);
            panel.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lbl = new JLabel(etiqueta);
            lbl.setFont(new Font(FUENTE, Font.BOLD, 13));
            lbl.setForeground(AZUL_OSCURO);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel lblPista = new JLabel(pista);
            lblPista.setFont(new Font(FUENTE, Font.PLAIN, 12));
            lblPista.setForeground(GRIS_PISTA);
            lblPista.setAlignmentX(Component.LEFT_ALIGNMENT);

            area.setFont(new Font(FUENTE, Font.PLAIN, 14));
            area.setForeground(AZUL_OSCURO);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setBorder(new EmptyBorder(9, 10, 9, 10));

            JScrollPane scroll = new JScrollPane(area);
            scroll.setBorder(
                    BorderFactory.createLineBorder(BORDE_CAMPO)
            );
            scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            scroll.setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, alto)
            );
            scroll.setPreferredSize(new Dimension(460, alto));

            panel.add(lbl);
            panel.add(Box.createVerticalStrut(3));
            panel.add(lblPista);
            panel.add(Box.createVerticalStrut(7));
            panel.add(scroll);

            return panel;
        }

        private void enviarSolicitud() {

            String descripcion = txtDescripcion.getText().trim();
            String experiencia = txtExperiencia.getText().trim();

            if (descripcion.isBlank() || experiencia.isBlank()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete los dos campos.",
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            Cuidador cuidador = new Cuidador();
            cuidador.setIdUsuario(idUsuario);
            cuidador.setDescripcionPerfil(descripcion);
            cuidador.setExperiencia(experiencia);
            cuidador.setEstadoVerificacion("PENDIENTE");

            try {

                CuidadorDAO cuidadorDAO = new CuidadorDAO();
                int idCuidador =
                        cuidadorDAO.registrarCuidador(cuidador);

                if (idCuidador > 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Cuenta creada y solicitud enviada.\n"
                                    + "Tu perfil de cuidador quedó "
                                    + "en revisión.",
                            "Listo",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

                dispose();

            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Error al enviar la solicitud",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    private static class PanelPatron extends JPanel {

        private final Color fondo;

        PanelPatron(Color fondo) {
            this.fondo = fondo;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(fondo);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(PATRON);

            Random aleatorio = new Random(11);
            int paso = 150;

            for (int y = -40; y < getHeight() + 150; y += paso) {
                for (int x = -40; x < getWidth() + 150; x += paso) {

                    int px = x + aleatorio.nextInt(50) - 25;
                    int py = y + aleatorio.nextInt(50) - 25;

                    if (aleatorio.nextInt(2) == 0) {
                        dibujarHuella(g2, px, py);
                    } else {
                        dibujarCasa(g2, px, py);
                    }
                }
            }

            g2.dispose();
        }

        private void dibujarHuella(Graphics2D g2, int x, int y) {
            g2.fillOval(x + 2, y + 12, 24, 19);
            g2.fillOval(x - 1, y + 1, 9, 11);
            g2.fillOval(x + 9, y - 3, 9, 11);
            g2.fillOval(x + 19, y + 1, 9, 11);
        }

        private void dibujarCasa(Graphics2D g2, int x, int y) {
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
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

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

            setFont(new Font(FUENTE, Font.BOLD, 15));
            setForeground(textoColor);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            setPreferredSize(new Dimension(440, 48));
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (fondoFinal != null) {
                g2.setPaint(new GradientPaint(
                        0, 0, fondo, getWidth(), 0, fondoFinal
                ));
            } else {
                g2.setPaint(fondo);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(0, 0, getWidth() - 1,
                        getHeight() - 1, 12, 12);
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }

    private static class FiltroNumerico extends DocumentFilter {

        private final int maximo;

        FiltroNumerico(int maximo) {
            this.maximo = maximo;
        }

        @Override
        public void insertString(FilterBypass fb, int offset,
                                 String texto, AttributeSet attr)
                throws BadLocationException {

            if (texto == null) {
                return;
            }

            String limpio = texto.replaceAll("\\D", "");

            if (fb.getDocument().getLength() + limpio.length()
                    <= maximo) {
                super.insertString(fb, offset, limpio, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length,
                            String texto, AttributeSet attr)
                throws BadLocationException {

            if (texto == null) {
                return;
            }

            String limpio = texto.replaceAll("\\D", "");

            if (fb.getDocument().getLength() - length
                    + limpio.length() <= maximo) {
                super.replace(fb, offset, length, limpio, attr);
            }
        }
    }
}