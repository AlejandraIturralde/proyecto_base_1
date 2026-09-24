
package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class PantallaBienvenida extends JFrame {

    private static final Color AZUL          = new Color(30, 136, 213);
    private static final Color AZUL_CIRCULO  = new Color(24, 121, 194);
    private static final Color AZUL_TARJETA  = new Color(59, 155, 221);
    private static final Color AZUL_OSCURO   = new Color(16, 69, 107);
    private static final Color FONDO_CLARO   = new Color(242, 248, 253);
    private static final Color GRIS_TEXTO    = new Color(90, 113, 131);
    private static final Color AMARILLO      = new Color(245, 194, 66);
    private static final Color AMARILLO_PILL = new Color(251, 236, 196);
    private static final Color AMARILLO_TXT  = new Color(138, 106, 32);
    private static final Color BORDE_SUAVE   = new Color(206, 228, 244);
    private static final Color PATRON        = new Color(216, 234, 249);
    private static final Color AZUL_GRAD_1   = new Color(19, 96, 158);
    private static final Color AZUL_GRAD_2   = new Color(45, 145, 216);

    private static final String FUENTE = "Segoe UI";

    public PantallaBienvenida() {
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Pet Home Boarding");
        setSize(1150, 770);
        setMinimumSize(new Dimension(1050, 740));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void crearComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(FONDO_CLARO);

        panelPrincipal.add(crearPanelIzquierdo(), BorderLayout.WEST);
        panelPrincipal.add(crearPanelDerecho(), BorderLayout.CENTER);

        setContentPane(panelPrincipal);
    }

    private JPanel crearPanelIzquierdo() {

        PanelAzul panel = new PanelAzul();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(470, 0));
        panel.setBorder(new EmptyBorder(48, 45, 40, 45));

        JPanel filaMarca = new JPanel();
        filaMarca.setLayout(new BoxLayout(filaMarca, BoxLayout.X_AXIS));
        filaMarca.setOpaque(false);
        filaMarca.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaMarca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel textoMarca = new JPanel();
        textoMarca.setLayout(new BoxLayout(textoMarca, BoxLayout.Y_AXIS));
        textoMarca.setOpaque(false);

        JLabel lblNombre = new JLabel(
                "<html>Pet Home<br>Boarding</html>"
        );
        lblNombre.setFont(new Font(FUENTE, Font.BOLD, 33));
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        PanelRedondeado guion =
                new PanelRedondeado(6, AMARILLO, null);
        guion.setMaximumSize(new Dimension(62, 7));
        guion.setPreferredSize(new Dimension(62, 7));
        guion.setAlignmentX(Component.LEFT_ALIGNMENT);

        textoMarca.add(lblNombre);
        textoMarca.add(Box.createVerticalStrut(12));
        textoMarca.add(guion);

        filaMarca.add(crearLogo());
        filaMarca.add(Box.createHorizontalStrut(22));
        filaMarca.add(textoMarca);
        filaMarca.add(Box.createHorizontalGlue());

        JLabel lblSubtitulo = new JLabel(
                "<html>Cuidadores verificados y alojamientos<br>"
                        + "con fotos reales, cerca de ti.</html>"
        );
        lblSubtitulo.setFont(new Font(FUENTE, Font.PLAIN, 16));
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(filaMarca);
        panel.add(Box.createVerticalStrut(28));
        panel.add(lblSubtitulo);
        panel.add(Box.createVerticalStrut(26));
        panel.add(crearTarjetaTestimonio());
        panel.add(Box.createVerticalStrut(28));
        panel.add(crearItemVerificado("Perfiles verificados"));
        panel.add(Box.createVerticalStrut(20));
        panel.add(crearItemVerificado("Alojamientos con fotos"));
        panel.add(Box.createVerticalStrut(20));
        panel.add(crearItemVerificado("Reserva en 3 pasos"));
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearLogo() {

        PanelRedondeado marco = new PanelRedondeado(
                30, Color.WHITE, new GridBagLayout()
        );
        marco.setPreferredSize(new Dimension(136, 136));
        marco.setMaximumSize(new Dimension(136, 136));
        marco.setMinimumSize(new Dimension(136, 136));
        marco.setBorder(new EmptyBorder(6, 6, 6, 6));

        File archivo = buscarArchivoLogo();

        if (archivo != null) {

            Image escalada = escalarConCalidad(archivo, 124, 124);

            if (escalada != null) {
                marco.add(new JLabel(new ImageIcon(escalada)));
                return marco;
            }
        }

        JLabel patita = new JLabel("🐾");
        patita.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 54));
        patita.setForeground(AZUL);
        marco.add(patita);

        return marco;
    }

    private Image escalarConCalidad(File archivo,
                                    int anchoFinal,
                                    int altoFinal) {

        try {

            BufferedImage imagen = ImageIO.read(archivo);

            if (imagen == null) {
                return null;
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            while (ancho / 2 >= anchoFinal
                    && alto / 2 >= altoFinal) {

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
            g2.drawImage(imagen, 0, 0, anchoFinal, altoFinal, null);
            g2.dispose();

            return resultado;

        } catch (IOException e) {
            return null;
        }
    }

    private File buscarArchivoLogo() {

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

    private JPanel crearTarjetaTestimonio() {

        PanelRedondeado tarjeta =
                new PanelRedondeado(18, AZUL_TARJETA, null);

        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(20, 22, 20, 22));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 172));
        tarjeta.setMinimumSize(new Dimension(0, 172));

        JPanel filaEstrellas =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaEstrellas.setOpaque(false);
        filaEstrellas.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaEstrellas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblEstrellas = new JLabel("★ ★ ★ ★ ★");
        lblEstrellas.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 19));
        lblEstrellas.setForeground(AMARILLO);

        JLabel lblPuntaje = new JLabel("   4.9 de 5");
        lblPuntaje.setFont(new Font(FUENTE, Font.BOLD, 14));
        lblPuntaje.setForeground(Color.WHITE);

        filaEstrellas.add(lblEstrellas);
        filaEstrellas.add(lblPuntaje);

        JLabel lblComentario = new JLabel(
                "<html>“Dejé a Manchas cuatro días y me<br>"
                        + "mandaban fotos todas las tardes.”</html>"
        );
        lblComentario.setFont(new Font(FUENTE, Font.BOLD, 15));
        lblComentario.setForeground(Color.WHITE);
        lblComentario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel filaAutora =
                new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filaAutora.setOpaque(false);
        filaAutora.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaAutora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        filaAutora.setMinimumSize(new Dimension(0, 38));
        filaAutora.setPreferredSize(new Dimension(300, 38));

        PanelRedondeado avatar =
                new PanelRedondeado(30, AMARILLO, new GridBagLayout());
        avatar.setPreferredSize(new Dimension(34, 34));

        JLabel lblIniciales = new JLabel("MC");
        lblIniciales.setFont(new Font(FUENTE, Font.BOLD, 12));
        lblIniciales.setForeground(AZUL_OSCURO);
        avatar.add(lblIniciales);

        JLabel lblAutora = new JLabel("   María C.  ·  Quito");
        lblAutora.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblAutora.setForeground(Color.WHITE);

        filaAutora.add(avatar);
        filaAutora.add(lblAutora);

        tarjeta.add(filaEstrellas);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblComentario);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(filaAutora);

        return tarjeta;
    }

    private JPanel crearItemVerificado(String texto) {

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        fila.setMinimumSize(new Dimension(0, 34));
        fila.setPreferredSize(new Dimension(300, 34));

        PanelRedondeado circulo =
                new PanelRedondeado(28, Color.WHITE, new GridBagLayout());
        circulo.setPreferredSize(new Dimension(30, 30));

        JLabel lblVisto = new JLabel("✓");
        lblVisto.setFont(new Font("Segoe UI Symbol", Font.BOLD, 16));
        lblVisto.setForeground(AZUL);
        circulo.add(lblVisto);

        JLabel lblTexto = new JLabel("   " + texto);
        lblTexto.setFont(new Font(FUENTE, Font.BOLD, 15));
        lblTexto.setForeground(Color.WHITE);

        fila.add(circulo);
        fila.add(lblTexto);

        return fila;
    }

    private JPanel crearPanelDerecho() {

        PanelPatron fondo = new PanelPatron();
        fondo.setLayout(new GridBagLayout());
        fondo.setBackground(FONDO_CLARO);

        PanelRedondeado tarjeta =
                new PanelRedondeado(22, Color.WHITE, null);

        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(58, 48, 50, 48));

        JLabel lblBienvenido = new JLabel("Bienvenido");
        lblBienvenido.setFont(new Font(FUENTE, Font.BOLD, 16));
        lblBienvenido.setForeground(AMARILLO_TXT);

        FontMetrics medidas = lblBienvenido.getFontMetrics(
                lblBienvenido.getFont()
        );

        int anchoPastilla = medidas.stringWidth("Bienvenido") + 40;
        int altoPastilla = 30;

        PanelRedondeado pastilla = new PanelRedondeado(
                altoPastilla, AMARILLO_PILL,
                new FlowLayout(FlowLayout.CENTER, 0, 0)
        );
        pastilla.setMaximumSize(
                new Dimension(anchoPastilla, altoPastilla)
        );
        pastilla.setPreferredSize(
                new Dimension(anchoPastilla, altoPastilla)
        );
        pastilla.setAlignmentX(Component.CENTER_ALIGNMENT);
        pastilla.add(lblBienvenido);

        JLabel lblTitulo = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "Un hogar seguro<br>para tu mascota"
                        + "</div></html>"
        );
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 37));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblDescripcion = new JLabel(
                "<html><div style='text-align:center;'>"
                        + "Conecta con cuidadores confiables y reserva<br>"
                        + "el espacio ideal en minutos.</div></html>"
        );
        lblDescripcion.setFont(new Font(FUENTE, Font.PLAIN, 16));
        lblDescripcion.setForeground(GRIS_TEXTO);
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescripcion.setHorizontalAlignment(SwingConstants.CENTER);

        BotonRedondeado btnIniciarSesion = new BotonRedondeado(
                "Iniciar sesión", AZUL_GRAD_1, Color.WHITE, null, AZUL_GRAD_2
        );

        BotonRedondeado btnCrearCuenta = new BotonRedondeado(
                "Crear una cuenta", Color.WHITE, AZUL, BORDE_SUAVE
        );

        tarjeta.add(pastilla);
        tarjeta.add(Box.createVerticalStrut(26));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(lblDescripcion);
        tarjeta.add(Box.createVerticalStrut(32));
        tarjeta.add(btnIniciarSesion);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(btnCrearCuenta);

        btnIniciarSesion.addActionListener(e -> abrirLogin());
        btnCrearCuenta.addActionListener(e -> abrirRegistro());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        fondo.add(tarjeta, gbc);

        return fondo;
    }

    private void abrirLogin() {
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }

    private void abrirRegistro() {
        RegistroUsuario registro = new RegistroUsuario();
        registro.setVisible(true);
        dispose();
    }

    private static class PanelPatron extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(PATRON);

            Random aleatorio = new Random(11);
            int paso = 115;

            for (int y = -30; y < getHeight() + 120; y += paso) {
                for (int x = -30; x < getWidth() + 120; x += paso) {

                    int px = x + aleatorio.nextInt(46) - 23;
                    int py = y + aleatorio.nextInt(46) - 23;

                    switch (aleatorio.nextInt(3)) {
                        case 0 -> dibujarHuella(g2, px, py);
                        case 1 -> dibujarCasa(g2, px, py);
                        default -> dibujarHueso(g2, px, py);
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
            g2.setStroke(new BasicStroke(2.6f));
            g2.drawRect(x + 2, y + 11, 24, 18);
            g2.drawLine(x - 3, y + 11, x + 14, y - 2);
            g2.drawLine(x + 14, y - 2, x + 31, y + 11);
        }

        private void dibujarHueso(Graphics2D g2, int x, int y) {
            g2.fillRoundRect(x + 7, y + 9, 20, 8, 5, 5);
            g2.fillOval(x, y + 4, 11, 9);
            g2.fillOval(x, y + 12, 11, 9);
            g2.fillOval(x + 23, y + 4, 11, 9);
            g2.fillOval(x + 23, y + 12, 11, 9);
        }
    }

    private class PanelAzul extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(AZUL);
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(AZUL_CIRCULO);
            g2.fillOval(getWidth() - 150, -200, 420, 420);
            g2.fillOval(-180, getHeight() - 340, 480, 480);

            g2.dispose();
        }
    }

    private static class PanelRedondeado extends JPanel {

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

    private static class BotonRedondeado extends JButton {

        private final Color fondo;
        private final Color borde;
        private final Color fondoFinal;

        BotonRedondeado(String texto, Color fondo,
                        Color textoColor, Color borde) {
            this(texto, fondo, textoColor, borde, null);
        }

        BotonRedondeado(String texto, Color fondo,
                        Color textoColor, Color borde,
                        Color fondoFinal) {
            super(texto);
            this.fondo = fondo;
            this.borde = borde;
            this.fondoFinal = fondoFinal;

            setFont(new Font(FUENTE, Font.BOLD, 16));
            setForeground(textoColor);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
            setPreferredSize(new Dimension(424, 56));
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

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

            if (borde != null) {
                g2.setColor(borde);
                g2.drawRoundRect(
                        0, 0, getWidth() - 1, getHeight() - 1, 14, 14
                );
            }

            g2.dispose();

            super.paintComponent(g);
        }
    }
}