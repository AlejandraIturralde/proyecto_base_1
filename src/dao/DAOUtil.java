package dao;

import java.sql.SQLException;

final class DAOUtil {

    private DAOUtil() {
    }

    static RuntimeException error(SQLException e) {
        return new RuntimeException(mensajePostgreSQL(e), e);
    }

    static String mensajePostgreSQL(SQLException e) {
        String mensaje = e.getMessage();

        if (mensaje == null || mensaje.isBlank()) {
            return "Ocurrió un error en la base de datos.";
        }

        int inicio = mensaje.indexOf("ERROR:");
        if (inicio >= 0) {
            mensaje = mensaje.substring(inicio + 6);
        }

        int detalle = mensaje.indexOf("Detail:");
        if (detalle >= 0) {
            mensaje = mensaje.substring(0, detalle);
        }

        int donde = mensaje.indexOf("Where:");
        if (donde >= 0) {
            mensaje = mensaje.substring(0, donde);
        }

        return mensaje.trim();
    }
}
