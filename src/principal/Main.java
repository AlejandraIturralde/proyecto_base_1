package principal;

import vista.PantallaBienvenida;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.out.println(
                    "No se pudo aplicar el estilo visual."
            );
        }

        SwingUtilities.invokeLater(() -> {
            PantallaBienvenida pantalla =
                    new PantallaBienvenida();
            pantalla.setVisible(true);
        });
    }
}
