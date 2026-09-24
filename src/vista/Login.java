
package vista;

import conexion.ConexionBD;
import dao.UsuarioDAO;
import modelo.Usuario;
import sesion.SesionUsuario;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class Login extends JFrame {

    private static final Color AZUL = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO = new Color(16, 69, 107);
    private static final Color AZUL_GRAD_1 = new Color(19, 96, 158);
    private static final Color AZUL_GRAD_2 = new Color(45, 145, 216);
    private static final Color FONDO_SUP = new Color(77, 165, 233);
    private static final Color FONDO_INF = new Color(26, 124, 199);
    private static final Color GRIS_TEXTO = new Color(120, 138, 154);
    private static final Color AMARILLO = new Color(245, 194, 66);
    private static final Color AMARILLO_PILL = new Color(252, 240, 205);
    private static final Color AMARILLO_TXT = new Color(150, 113, 26);
    private static final Color BORDE_SUAVE = new Color(214, 231, 245);
    private static final Color GRIS_LINEA = new Color(226, 234, 241);

    private static final String FUENTE = "Segoe UI";
    private static final int ANCHO_CAMPO = 360;

    private JTextField txtCorreo;
    private JPasswordField txtContrasena;
    private boolean correoConPista = true;

    public Login() {
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Iniciar sesión - Pet Home Boarding");
        setSize(1000, 720);
        setMinimumSize(new Dimension(900, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void crearComponentes() {

        PanelFondo fondo = new PanelFondo();
        fondo.setLayout(new GridBagLayout());

        PanelRedondeado tarjeta = new PanelRedondeado(20, Color.WHITE, null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(30, 40, 28, 40));


        JButton btnVolver = new JButton("←  Volver");
        btnVolver.setFont(new Font(FUENTE, Font.BOLD, 12));
        btnVolver.setForeground(GRIS_TEXTO);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVolver.setMargin(new Insets(0, 0, 0, 0));
        btnVolver.setHorizontalAlignment(SwingConstants.LEFT);

        Box filaVolver = Box.createHorizontalBox();
        filaVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        filaVolver.setMinimumSize(new Dimension(ANCHO_CAMPO, 24));
        filaVolver.setPreferredSize(new Dimension(ANCHO_CAMPO, 24));
        filaVolver.setMaximumSize(new Dimension(ANCHO_CAMPO, 24));
        filaVolver.add(btnVolver);
        filaVolver.add(Box.createHorizontalGlue());

        JComponent logo = crearLogo(76);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMarca = new JLabel("Pet Home Boarding");
        lblMarca.setFont(new Font(FUENTE, Font.BOLD, 17));
        lblMarca.setForeground(AZUL_OSCURO);
        lblMarca.setAlignmentX(Component.CENTER_ALIGNMENT);

        PanelRedondeado guion = new PanelRedondeado(5, AMARILLO, null);
        guion.setMaximumSize(new Dimension(48, 5));
        guion.setPreferredSize(new Dimension(48, 5));
        guion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblBienvenido = new JLabel("Bienvenido");
        lblBienvenido.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblBienvenido.setForeground(AMARILLO_TXT);

        Icono huellaPastilla = new Icono(Icono.HUELLA, AMARILLO_TXT, 14);
        FontMetrics medidas = lblBienvenido.getFontMetrics(lblBienvenido.getFont());
        int anchoPastilla = medidas.stringWidth("Bienvenido") + 56;

        PanelRedondeado pastilla = new PanelRedondeado(
                28, AMARILLO_PILL,
                new FlowLayout(FlowLayout.CENTER, 6, 0)
        );
        pastilla.setMaximumSize(new Dimension(anchoPastilla, 28));
        pastilla.setPreferredSize(new Dimension(anchoPastilla, 28));
        pastilla.setAlignmentX(Component.CENTER_ALIGNMENT);
        pastilla.add(huellaPastilla);
        pastilla.add(lblBienvenido);

        JLabel lblTitulo = new JLabel("Iniciar sesión");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 28));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Ingresa a tu cuenta para continuar");
        lblSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblSubtitulo.setForeground(GRIS_TEXTO);
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtCorreo = new JTextField("ejemplo@correo.com");
        txtCorreo.setForeground(GRIS_TEXTO);
        prepararPista();

        JPanel cajaCorreo = crearCaja(
                new Icono(Icono.SOBRE, GRIS_TEXTO, 17),
                txtCorreo,
                null
        );

        txtContrasena = new JPasswordField();
        txtContrasena.setEchoChar('•');

        Icono ojo = new Icono(Icono.OJO, GRIS_TEXTO, 18);
        ojo.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel cajaContrasena = crearCaja(
                new Icono(Icono.CANDADO, GRIS_TEXTO, 17),
                txtContrasena,
                ojo
        );

        JCheckBox chkMostrar = new JCheckBox("Mostrar contraseña");
        chkMostrar.setFont(new Font(FUENTE, Font.PLAIN, 12));
        chkMostrar.setForeground(GRIS_TEXTO);
        chkMostrar.setOpaque(false);
        chkMostrar.setFocusPainted(false);
        chkMostrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        BotonRedondeado btnEntrar = new BotonRedondeado(
                "Iniciar sesión",
                AZUL_GRAD_1,
                Color.WHITE,
                null,
                AZUL_GRAD_2,
                true
        );

        BotonRedondeado btnRegistro = new BotonRedondeado(
                "¿No tienes cuenta?  Crear una cuenta",
                Color.WHITE,
                AZUL,
                BORDE_SUAVE,
                null,
                false
        );
        btnRegistro.setFont(new Font(FUENTE, Font.PLAIN, 13));

        JPanel separador = new JPanel();
        separador.setLayout(new BoxLayout(separador, BoxLayout.X_AXIS));
        separador.setOpaque(false);
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);
        separador.setMaximumSize(new Dimension(ANCHO_CAMPO, 20));
        separador.add(crearLinea());
        separador.add(Box.createHorizontalStrut(10));
        separador.add(new Icono(Icono.HUELLA, BORDE_SUAVE, 16));
        separador.add(Box.createHorizontalStrut(10));
        separador.add(crearLinea());

        JLabel lblPie = new JLabel(
                "<html><span style='color:#788A9A;'>"
                        + "¿Olvidaste tu contraseña? </span>"
                        + "<b style='color:#1E88D5;'>Recupérala aquí</b>"
                        + "</html>"
        );
        lblPie.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblPie.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPie.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tarjeta.add(filaVolver);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(logo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblMarca);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(guion);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(pastilla);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(5));
        tarjeta.add(lblSubtitulo);
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(etiqueta("Correo electrónico"));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(cajaCorreo);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(etiqueta("Contraseña"));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(cajaContrasena);
        tarjeta.add(Box.createVerticalStrut(9));
        tarjeta.add(alinearIzquierda(chkMostrar));
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(btnEntrar);
        tarjeta.add(Box.createVerticalStrut(11));
        tarjeta.add(btnRegistro);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(separador);
        tarjeta.add(Box.createVerticalStrut(14));
        tarjeta.add(lblPie);

        btnVolver.addActionListener(e -> {
            PantallaBienvenida bienvenida = new PantallaBienvenida();
            bienvenida.setVisible(true);
            dispose();
        });

        btnEntrar.addActionListener(e -> iniciarSesion());
        btnRegistro.addActionListener(e -> abrirRegistro());
        txtContrasena.addActionListener(e -> iniciarSesion());

        chkMostrar.addActionListener(e ->
                txtContrasena.setEchoChar(
                        chkMostrar.isSelected() ? (char) 0 : '•'
                )
        );

        ojo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                chkMostrar.setSelected(!chkMostrar.isSelected());
                txtContrasena.setEchoChar(
                        chkMostrar.isSelected() ? (char) 0 : '•'
                );
            }
        });

        lblPie.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                abrirRecuperarContrasena();
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        fondo.add(tarjeta, gbc);

        setContentPane(fondo);
    }

    private JPanel crearLinea() {
        JPanel linea = new JPanel();
        linea.setBackground(GRIS_LINEA);
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        linea.setPreferredSize(new Dimension(120, 1));
        return linea;
    }

    private JPanel etiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(AZUL_OSCURO);
        return alinearIzquierda(lbl);
    }

    private JPanel alinearIzquierda(JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(
                ANCHO_CAMPO,
                componente.getPreferredSize().height
        ));
        panel.add(componente, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearCaja(Icono izquierda, JTextField campo, Icono derecha) {
        JPanel caja = new JPanel(new BorderLayout(9, 0));
        caja.setBackground(Color.WHITE);
        caja.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.setMaximumSize(new Dimension(ANCHO_CAMPO, 44));
        caja.setPreferredSize(new Dimension(ANCHO_CAMPO, 44));

        caja.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE_SUAVE),
                        new EmptyBorder(0, 12, 0, 12)
                )
        );

        campo.setBorder(null);
        campo.setFont(new Font(FUENTE, Font.PLAIN, 14));
        campo.setOpaque(false);

        caja.add(izquierda, BorderLayout.WEST);
        caja.add(campo, BorderLayout.CENTER);

        if (derecha != null) {
            caja.add(derecha, BorderLayout.EAST);
        }

        return caja;
    }

    private void prepararPista() {
        txtCorreo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (correoConPista) {
                    txtCorreo.setText("");
                    txtCorreo.setForeground(AZUL_OSCURO);
                    correoConPista = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtCorreo.getText().isBlank()) {
                    txtCorreo.setText("ejemplo@correo.com");
                    txtCorreo.setForeground(GRIS_TEXTO);
                    correoConPista = true;
                }
            }
        });
    }

    private void escribirCorreo(String correo) {
        correoConPista = false;
        txtCorreo.setForeground(AZUL_OSCURO);
        txtCorreo.setText(correo);
    }

    static JComponent crearLogo(int lado) {
        File archivo = buscarArchivoLogo();

        if (archivo != null) {
            Image escalada = escalarConCalidad(archivo, lado, lado);
            if (escalada != null) {
                JLabel lbl = new JLabel(new ImageIcon(escalada));
                lbl.setBorder(new EmptyBorder(0, 0, 0, 0));
                return lbl;
            }
        }

        return new Icono(Icono.HUELLA, AZUL, (int) (lado * 0.74));
    }

    private static File buscarArchivoLogo() {
        File carpeta = new File(System.getProperty("user.dir"));
        File[] archivos = carpeta.listFiles();

        if (archivos == null) {
            return null;
        }

        for (File archivo : archivos) {
            String nombre = archivo.getName().toLowerCase();
            if (nombre.startsWith("logo")
                    && (nombre.endsWith(".png")
                    || nombre.endsWith(".jpg")
                    || nombre.endsWith(".jpeg"))) {
                return archivo;
            }
        }

        return null;
    }

    private static Image escalarConCalidad(File archivo, int anchoFinal, int altoFinal) {
        try {
            BufferedImage imagen = ImageIO.read(archivo);
            if (imagen == null) {
                return null;
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            while (ancho / 2 >= anchoFinal && alto / 2 >= altoFinal) {
                ancho /= 2;
                alto /= 2;

                BufferedImage paso = new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_ARGB
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
                    anchoFinal,
                    altoFinal,
                    BufferedImage.TYPE_INT_ARGB
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

            g2.setClip(new RoundRectangle2D.Float(
                    0, 0, anchoFinal, altoFinal, 20, 20
            ));
            g2.drawImage(imagen, 0, 0, anchoFinal, altoFinal, null);
            g2.dispose();

            return resultado;

        } catch (IOException e) {
            return null;
        }
    }

    private void iniciarSesion() {
        String correo = correoConPista ? "" : txtCorreo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        if (correo.isBlank() || contrasena.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Complete el correo y la contraseña.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuario = usuarioDAO.iniciarSesion(correo, contrasena);

            if (usuario == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Correo o contraseña incorrectos.",
                        "No se pudo iniciar sesión",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            SesionUsuario.iniciarSesion(usuario);

            if ("ADMINISTRADOR".equals(usuario.getRolSistema())) {
                PanelAdministrador panel = new PanelAdministrador(usuario);
                panel.setVisible(true);

            } else if (esCuidadorVerificado(usuario.getIdUsuario())) {
                PanelCuidador panel = new PanelCuidador(usuario);
                panel.setVisible(true);

            } else {
                PanelUsuario panel = new PanelUsuario(usuario);
                panel.setVisible(true);
            }

            dispose();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean esCuidadorVerificado(int idUsuario) {
        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM cuidador
                    WHERE id_usuario = ?
                      AND estado_verificacion = 'VERIFICADO'
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo verificar el perfil de cuidador: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private void abrirRegistro() {
        RegistroUsuario registro = new RegistroUsuario();
        registro.setVisible(true);
        dispose();
    }

    private void abrirRecuperarContrasena() {
        RecuperarContrasena ventana = new RecuperarContrasena(this);
        ventana.setVisible(true);

        String correo = ventana.getCorreoEnviado();
        if (correo != null) {
            escribirCorreo(correo);
            txtContrasena.setText("");
            txtContrasena.requestFocusInWindow();
        }
    }

    static class Icono extends JComponent {

        static final int SOBRE = 0;
        static final int CANDADO = 1;
        static final int OJO = 2;
        static final int HUELLA = 3;

        private final int tipo;
        private final Color color;
        private final int lado;

        Icono(int tipo, Color color, int lado) {
            this.tipo = tipo;
            this.color = color;
            this.lado = lado;
            setPreferredSize(new Dimension(lado, lado));
            setMinimumSize(new Dimension(lado, lado));
            setMaximumSize(new Dimension(lado, lado));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.setColor(color);

            double escala = lado / 100.0;
            g2.scale(escala, escala);
            g2.setStroke(new BasicStroke(9f));

            switch (tipo) {
                case SOBRE -> {
                    g2.drawRoundRect(8, 22, 84, 56, 10, 10);
                    g2.drawLine(12, 28, 50, 54);
                    g2.drawLine(88, 28, 50, 54);
                }
                case CANDADO -> {
                    g2.fillRoundRect(16, 44, 68, 46, 10, 10);
                    g2.draw(new Arc2D.Float(
                            29, 14, 42, 46, 0, 180, Arc2D.OPEN
                    ));
                }
                case OJO -> {
                    g2.drawOval(6, 28, 88, 44);
                    g2.fillOval(38, 38, 24, 24);
                }
                case HUELLA -> {
                    g2.fillOval(26, 46, 48, 40);
                    g2.fillOval(14, 20, 20, 26);
                    g2.fillOval(40, 8, 20, 26);
                    g2.fillOval(66, 20, 20, 26);
                }
                default -> {
                }
            }

            g2.dispose();
        }
    }

    static class PanelFondo extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int ancho = getWidth();
            int alto = getHeight();

            g2.setPaint(new GradientPaint(
                    0, 0, FONDO_SUP,
                    ancho, alto, FONDO_INF
            ));
            g2.fillRect(0, 0, ancho, alto);

            g2.setColor(new Color(255, 255, 255, 18));
            g2.fillOval(ancho - 420, -260, 700, 700);
            g2.fillOval(-320, alto - 420, 700, 700);

            g2.setColor(new Color(255, 255, 255, 14));
            g2.fillOval(-200, -180, 520, 520);

            Random aleatorio = new Random(11);
            int paso = 122;

            for (int y = -40; y < alto + 120; y += paso) {
                for (int x = -40; x < ancho + 120; x += paso) {
                    int px = x + aleatorio.nextInt(46) - 23;
                    int py = y + aleatorio.nextInt(46) - 23;
                    int tipo = aleatorio.nextInt(6);

                    Graphics2D f = (Graphics2D) g2.create();
                    f.translate(px, py);
                    f.scale(0.44, 0.44);
                    f.setColor(new Color(255, 255, 255, 70));
                    f.setStroke(new BasicStroke(
                            6.5f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_ROUND
                    ));

                    switch (tipo) {
                        case 0 -> dibujarHuella(f);
                        case 1 -> dibujarHueso(f);
                        case 2 -> dibujarPez(f);
                        case 3 -> dibujarTortuga(f);
                        case 4 -> dibujarGato(f);
                        default -> dibujarPerro(f);
                    }

                    f.dispose();
                }
            }

            g2.dispose();
        }

        private void dibujarHuella(Graphics2D g2) {
            g2.fill(new Ellipse2D.Float(28, 45, 44, 37));
            g2.fill(new Ellipse2D.Float(12, 22, 18, 24));
            g2.fill(new Ellipse2D.Float(36, 8, 18, 24));
            g2.fill(new Ellipse2D.Float(62, 16, 18, 24));
        }

        private void dibujarHueso(Graphics2D g2) {
            Area a = new Area(new RoundRectangle2D.Float(24, 42, 52, 16, 12, 12));
            a.add(new Area(new Ellipse2D.Float(6, 30, 26, 22)));
            a.add(new Area(new Ellipse2D.Float(6, 48, 26, 22)));
            a.add(new Area(new Ellipse2D.Float(68, 30, 26, 22)));
            a.add(new Area(new Ellipse2D.Float(68, 48, 26, 22)));
            g2.fill(a);
        }

        private void dibujarPez(Graphics2D g2) {
            Path2D cuerpo = new Path2D.Float();
            cuerpo.moveTo(8, 52);
            cuerpo.curveTo(26, 26, 58, 26, 74, 52);
            cuerpo.curveTo(58, 78, 26, 78, 8, 52);
            cuerpo.closePath();
            g2.draw(cuerpo);

            Path2D cola = new Path2D.Float();
            cola.moveTo(74, 52);
            cola.curveTo(82, 44, 88, 34, 94, 26);
            cola.curveTo(96, 42, 96, 62, 94, 78);
            cola.curveTo(88, 70, 82, 60, 74, 52);
            cola.closePath();
            g2.draw(cola);

            g2.fill(new Ellipse2D.Float(22, 44, 8, 8));
        }

        private void dibujarTortuga(Graphics2D g2) {
            g2.draw(new Ellipse2D.Float(20, 26, 60, 50));
            g2.draw(new Ellipse2D.Float(42, 6, 18, 20));
            g2.draw(new Ellipse2D.Float(12, 22, 18, 14));
            g2.draw(new Ellipse2D.Float(72, 22, 18, 14));
            g2.draw(new Ellipse2D.Float(14, 62, 18, 14));
            g2.draw(new Ellipse2D.Float(70, 62, 18, 14));

            Path2D caparazon = new Path2D.Float();
            caparazon.moveTo(50, 36);
            caparazon.lineTo(63, 44);
            caparazon.lineTo(63, 58);
            caparazon.lineTo(50, 66);
            caparazon.lineTo(37, 58);
            caparazon.lineTo(37, 44);
            caparazon.closePath();
            g2.draw(caparazon);

            g2.draw(new Line2D.Float(50, 36, 50, 28));
            g2.draw(new Line2D.Float(63, 44, 72, 39));
            g2.draw(new Line2D.Float(63, 58, 72, 63));
            g2.draw(new Line2D.Float(50, 66, 50, 74));
            g2.draw(new Line2D.Float(37, 58, 28, 63));
            g2.draw(new Line2D.Float(37, 44, 28, 39));
        }

        private void dibujarGato(Graphics2D g2) {
            Path2D cuerpo = new Path2D.Float();
            cuerpo.moveTo(30, 88);
            cuerpo.curveTo(26, 66, 30, 52, 40, 44);
            cuerpo.lineTo(60, 44);
            cuerpo.curveTo(70, 54, 72, 70, 70, 88);
            cuerpo.closePath();
            g2.draw(cuerpo);

            Path2D cabeza = new Path2D.Float();
            cabeza.moveTo(34, 34);
            cabeza.curveTo(34, 20, 44, 14, 52, 14);
            cabeza.curveTo(62, 14, 70, 22, 70, 34);
            cabeza.curveTo(70, 44, 62, 48, 52, 48);
            cabeza.curveTo(42, 48, 34, 44, 34, 34);
            cabeza.closePath();
            g2.draw(cabeza);

            Path2D orejaIzq = new Path2D.Float();
            orejaIzq.moveTo(36, 24);
            orejaIzq.lineTo(34, 6);
            orejaIzq.lineTo(48, 16);
            g2.draw(orejaIzq);

            Path2D orejaDer = new Path2D.Float();
            orejaDer.moveTo(68, 24);
            orejaDer.lineTo(70, 6);
            orejaDer.lineTo(56, 16);
            g2.draw(orejaDer);

            Path2D cola = new Path2D.Float();
            cola.moveTo(70, 86);
            cola.curveTo(86, 86, 92, 74, 86, 62);
            g2.draw(cola);
        }

        private void dibujarPerro(Graphics2D g2) {
            Area a = new Area(new RoundRectangle2D.Float(20, 36, 54, 28, 22, 22));
            a.add(new Area(new Ellipse2D.Float(62, 16, 28, 26)));
            a.add(new Area(new RoundRectangle2D.Float(80, 28, 18, 13, 9, 9)));

            Path2D oreja = new Path2D.Float();
            oreja.moveTo(64, 24);
            oreja.lineTo(58, 2);
            oreja.lineTo(78, 14);
            oreja.closePath();
            a.add(new Area(oreja));

            a.add(new Area(new RoundRectangle2D.Float(24, 56, 10, 30, 7, 7)));
            a.add(new Area(new RoundRectangle2D.Float(38, 56, 10, 30, 7, 7)));
            a.add(new Area(new RoundRectangle2D.Float(56, 56, 10, 30, 7, 7)));

            Path2D cola = new Path2D.Float();
            cola.moveTo(24, 42);
            cola.curveTo(12, 34, 6, 20, 12, 12);
            cola.curveTo(20, 16, 22, 30, 30, 40);
            cola.closePath();
            a.add(new Area(cola));

            g2.draw(a);
            g2.fill(new Ellipse2D.Float(74, 26, 6, 6));
        }
    }

    static class PanelRedondeado extends JPanel {

        private final int radio;
        private final Color fondo;

        PanelRedondeado(int radio, Color fondo, LayoutManager layout) {
            super(layout);
            this.radio = radio;
            this.fondo = fondo;
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class BotonRedondeado extends JButton {

        private final Color fondo;
        private final Color borde;
        private final Color fondoFinal;
        private final boolean conHuella;

        BotonRedondeado(
                String texto,
                Color fondo,
                Color textoColor,
                Color borde,
                Color fondoFinal,
                boolean conHuella
        ) {
            super(texto);
            this.fondo = fondo;
            this.borde = borde;
            this.fondoFinal = fondoFinal;
            this.conHuella = conHuella;

            setFont(new Font(FUENTE, Font.BOLD, 14));
            setForeground(textoColor);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(ANCHO_CAMPO, 44));
            setPreferredSize(new Dimension(ANCHO_CAMPO, 44));
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
                        0, 0, fondo,
                        getWidth(), 0, fondoFinal
                ));
            } else {
                g2.setPaint(fondo);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        12, 12
                );
            }

            if (conHuella) {
                g2.setColor(new Color(255, 255, 255, 210));
                Graphics2D h = (Graphics2D) g2.create();
                h.translate(getWidth() - 42, getHeight() / 2.0 - 9);
                h.scale(0.19, 0.19);
                h.fillOval(26, 46, 48, 40);
                h.fillOval(14, 20, 20, 26);
                h.fillOval(40, 8, 20, 26);
                h.fillOval(66, 20, 20, 26);
                h.dispose();
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}