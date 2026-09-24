
package vista;

import correo.ServicioCorreo;
import dao.UsuarioDAO;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.ExecutionException;

public class RecuperarContrasena extends JDialog {

    private static final Color AZUL        = new Color(30, 136, 213);
    private static final Color AZUL_OSCURO = new Color(16, 69, 107);
    private static final Color AZUL_GRAD_1 = new Color(19, 96, 158);
    private static final Color AZUL_GRAD_2 = new Color(45, 145, 216);
    private static final Color GRIS_TEXTO  = new Color(120, 138, 154);
    private static final Color BORDE_SUAVE = new Color(214, 231, 245);
    private static final Color ROJO        = new Color(200, 70, 70);

    private static final String FUENTE = "Segoe UI";
    private static final int ANCHO_CAMPO = 360;

    private JTextField txtCorreo;
    private Login.BotonRedondeado btnEnviar;
    private JLabel lblEstado;

    private String correoEnviado = null;

    public RecuperarContrasena(Frame padre) {

        super(padre, "Recuperar contraseña", true);

        construir();

        pack();
        setResizable(false);
        setLocationRelativeTo(padre);
    }

    public String getCorreoEnviado() {
        return correoEnviado;
    }

    private void construir() {

        Login.PanelFondo fondo = new Login.PanelFondo();
        fondo.setLayout(new GridBagLayout());

        Login.PanelRedondeado tarjeta =
                new Login.PanelRedondeado(20, Color.WHITE, null);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(32, 40, 30, 40));

        JComponent logo = Login.crearLogo(64);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Recuperar contraseña");
        lblTitulo.setFont(new Font(FUENTE, Font.BOLD, 24));
        lblTitulo.setForeground(AZUL_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAyuda = new JLabel(
                "<html><div style='text-align:center;width:340px;'>"
                        + "Escribe el correo de tu cuenta y te "
                        + "enviaremos una contraseña temporal para "
                        + "que puedas entrar."
                        + "</div></html>"
        );
        lblAyuda.setFont(new Font(FUENTE, Font.PLAIN, 13));
        lblAyuda.setForeground(GRIS_TEXTO);
        lblAyuda.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtCorreo = new JTextField();

        JPanel cajaCorreo = crearCaja(
                new Login.Icono(Login.Icono.SOBRE, GRIS_TEXTO, 17),
                txtCorreo
        );

        lblEstado = new JLabel(" ");
        lblEstado.setFont(new Font(FUENTE, Font.PLAIN, 12));
        lblEstado.setForeground(GRIS_TEXTO);
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblEstado.setPreferredSize(new Dimension(ANCHO_CAMPO, 34));
        lblEstado.setMaximumSize(new Dimension(ANCHO_CAMPO, 34));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        btnEnviar = new Login.BotonRedondeado(
                "Enviar contraseña temporal",
                AZUL_GRAD_1, Color.WHITE, null, AZUL_GRAD_2, true
        );

        Login.BotonRedondeado btnVolver = new Login.BotonRedondeado(
                "Volver a iniciar sesión",
                Color.WHITE, AZUL, BORDE_SUAVE, null, false
        );
        btnVolver.setFont(new Font(FUENTE, Font.PLAIN, 13));

        tarjeta.add(logo);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(lblTitulo);
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(lblAyuda);
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(etiqueta("Correo electrónico"));
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(cajaCorreo);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(lblEstado);
        tarjeta.add(Box.createVerticalStrut(6));
        tarjeta.add(btnEnviar);
        tarjeta.add(Box.createVerticalStrut(11));
        tarjeta.add(btnVolver);

        btnEnviar.addActionListener(e -> enviarCodigo());
        btnVolver.addActionListener(e -> dispose());
        txtCorreo.addActionListener(e -> enviarCodigo());

        getRootPane().registerKeyboardAction(
                (ActionEvent e) -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(30, 40, 30, 40);

        fondo.add(tarjeta, gbc);

        setContentPane(fondo);
    }

    private void enviarCodigo() {

        String correo = txtCorreo.getText().trim();

        if (correo.isBlank()) {
            mostrarEstado("Escribe tu correo electrónico.", ROJO);
            return;
        }

        if (!correo.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarEstado("Ese correo no tiene un formato válido.", ROJO);
            return;
        }

        btnEnviar.setEnabled(false);
        txtCorreo.setEnabled(false);
        mostrarEstado("Enviando correo, espera un momento...", GRIS_TEXTO);

        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                UsuarioDAO usuarioDAO = new UsuarioDAO();

                Usuario usuario =
                        usuarioDAO.buscarUsuarioPorCorreo(correo);

                if (usuario == null) {
                    throw new Exception(
                            "No encontramos una cuenta activa con "
                                    + "ese correo."
                    );
                }

                String contrasenaAnterior =
                        usuario.getContrasenaHash();

                String codigo =
                        ServicioCorreo.generarCodigoTemporal();

                boolean guardado = usuarioDAO.cambiarContrasena(
                        usuario.getIdUsuario(), codigo
                );

                if (!guardado) {
                    throw new Exception(
                            "No se pudo actualizar la contraseña "
                                    + "en la base de datos."
                    );
                }

                try {
                    ServicioCorreo.enviarCodigoTemporal(
                            correo,
                            usuario.getNombre(),
                            codigo
                    );

                } catch (Exception errorCorreo) {

                    usuarioDAO.cambiarContrasena(
                            usuario.getIdUsuario(),
                            contrasenaAnterior
                    );

                    throw errorCorreo;
                }

                return correo;
            }

            @Override
            protected void done() {

                try {
                    correoEnviado = get();

                    JOptionPane.showMessageDialog(
                            RecuperarContrasena.this,
                            "Listo. Te enviamos una contraseña "
                                    + "temporal a:\n" + correoEnviado
                                    + "\n\nRevisa tu bandeja de "
                                    + "entrada y también la carpeta "
                                    + "de spam.",
                            "Correo enviado",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    dispose();

                } catch (InterruptedException e) {

                    Thread.currentThread().interrupt();
                    reactivar("Se interrumpió el envío.");

                } catch (ExecutionException e) {

                    Throwable causa = e.getCause();

                    String mensaje = (causa == null
                            || causa.getMessage() == null
                            || causa.getMessage().isBlank())
                            ? "No se pudo enviar el correo."
                            : causa.getMessage();

                    if (mensaje.length() > 60) {

                        JOptionPane.showMessageDialog(
                                RecuperarContrasena.this,
                                mensaje,
                                "No se pudo enviar",
                                JOptionPane.WARNING_MESSAGE
                        );

                        reactivar(" ");

                    } else {
                        reactivar(mensaje);
                    }
                }
            }

            private void reactivar(String mensaje) {
                btnEnviar.setEnabled(true);
                txtCorreo.setEnabled(true);
                mostrarEstado(mensaje, ROJO);
                txtCorreo.requestFocusInWindow();
            }

        }.execute();
    }

    private void mostrarEstado(String texto, Color color) {

        lblEstado.setForeground(color);
        lblEstado.setText(
                "<html><div style='text-align:center;width:340px;'>"
                        + texto + "</div></html>"
        );
    }

    private JPanel etiqueta(String texto) {

        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font(FUENTE, Font.BOLD, 12));
        lbl.setForeground(AZUL_OSCURO);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(
                new Dimension(ANCHO_CAMPO,
                        lbl.getPreferredSize().height)
        );
        panel.add(lbl, BorderLayout.WEST);

        return panel;
    }

    private JPanel crearCaja(Login.Icono izquierda, JTextField campo) {

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
        campo.setForeground(AZUL_OSCURO);

        caja.add(izquierda, BorderLayout.WEST);
        caja.add(campo, BorderLayout.CENTER);

        return caja;
    }
}