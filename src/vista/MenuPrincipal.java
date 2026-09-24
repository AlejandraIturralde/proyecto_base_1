package vista;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame {

    private JButton btnUsuarios;
    private JButton btnMascotas;
    private JButton btnAlojamientos;
    private JButton btnReservas;
    private JButton btnPagos;
    private JButton btnTransporte;
    private JButton btnResenas;
    private JButton btnCerrarSesion;

    public MenuPrincipal() {
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Pet Home Boarding - Menú principal");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void crearComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(15, 15));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        JLabel lblTitulo = new JLabel(
                "PET HOME BOARDING",
                SwingConstants.CENTER
        );

        lblTitulo.setFont(
                new Font("Arial", Font.BOLD, 28)
        );

        JLabel lblBienvenida = new JLabel(
                "Sistema de gestión de hospedaje para mascotas",
                SwingConstants.CENTER
        );

        lblBienvenida.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        JPanel panelEncabezado = new JPanel(
                new GridLayout(2, 1, 5, 5)
        );

        panelEncabezado.add(lblTitulo);
        panelEncabezado.add(lblBienvenida);

        JPanel panelOpciones = new JPanel(
                new GridLayout(3, 3, 15, 15)
        );

        btnUsuarios = crearBoton("Usuarios");
        btnMascotas = crearBoton("Mascotas");
        btnAlojamientos = crearBoton("Alojamientos");
        btnReservas = crearBoton("Reservas");
        btnPagos = crearBoton("Pagos");
        btnTransporte = crearBoton("Transporte");
        btnResenas = crearBoton("Reseñas");
        btnCerrarSesion = crearBoton("Cerrar sesión");

        panelOpciones.add(btnUsuarios);
        panelOpciones.add(btnMascotas);
        panelOpciones.add(btnAlojamientos);
        panelOpciones.add(btnReservas);
        panelOpciones.add(btnPagos);
        panelOpciones.add(btnTransporte);
        panelOpciones.add(btnResenas);
        panelOpciones.add(btnCerrarSesion);

        panelPrincipal.add(panelEncabezado, BorderLayout.NORTH);
        panelPrincipal.add(panelOpciones, BorderLayout.CENTER);

        add(panelPrincipal);

        configurarEventos();
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);

        boton.setFont(
                new Font("Arial", Font.BOLD, 16)
        );

        boton.setFocusPainted(false);

        return boton;
    }

    private void configurarEventos() {

        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        btnUsuarios.addActionListener(e ->
                mostrarModuloNoDisponible("Usuarios")
        );

        btnMascotas.addActionListener(e ->
                mostrarModuloNoDisponible("Mascotas")
        );

        btnAlojamientos.addActionListener(e ->
                mostrarModuloNoDisponible("Alojamientos")
        );

        btnReservas.addActionListener(e ->
                mostrarModuloNoDisponible("Reservas")
        );

        btnPagos.addActionListener(e ->
                mostrarModuloNoDisponible("Pagos")
        );

        btnTransporte.addActionListener(e ->
                mostrarModuloNoDisponible("Transporte")
        );

        btnResenas.addActionListener(e ->
                mostrarModuloNoDisponible("Reseñas")
        );
    }

    private void mostrarModuloNoDisponible(String modulo) {

        JOptionPane.showMessageDialog(
                this,
                "El módulo de " + modulo + " se desarrollará próximamente.",
                "Módulo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void cerrarSesion() {

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar la sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {

            Login login = new Login();
            login.setVisible(true);

            dispose();
        }
    }
}