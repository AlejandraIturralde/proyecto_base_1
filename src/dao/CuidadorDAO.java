
package dao;

import conexion.ConexionBD;
import modelo.Cuidador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CuidadorDAO {

    public int registrarCuidador(Cuidador cuidador) {

        String sql = """
                SELECT fn_registrar_cuidador(
                    ?, ?, ?
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    cuidador.getDescripcionPerfil()
            );

            ps.setString(
                    2,
                    cuidador.getExperiencia()
            );

            ps.setInt(
                    3,
                    cuidador.getIdUsuario()
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int idCuidador = rs.getInt(1);

                    cuidador.setIdCuidador(idCuidador);

                    return idCuidador;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return 0;
    }

    public Cuidador buscarCuidadorPorId(
            int idCuidador
    ) {

        String sql = """
                SELECT *
                FROM cuidador
                WHERE id_cuidador = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idCuidador);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearCuidador(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return null;
    }

    /*
     * MÃ©todo fundamental:
     * permite saber si el usuario ya tiene
     * un perfil de cuidador.
     */
    public Cuidador buscarCuidadorPorUsuario(
            int idUsuario
    ) {

        String sql = """
                SELECT *
                FROM cuidador
                WHERE id_usuario = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearCuidador(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return null;
    }

    public boolean existeCuidadorPorUsuario(
            int idUsuario
    ) {

        String sql = """
                SELECT EXISTS (
                    SELECT 1
                    FROM cuidador
                    WHERE id_usuario = ?
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return false;
    }

    /*
     * El usuario solamente puede modificar
     * su descripciÃ³n y experiencia.
     */
    public boolean actualizarPerfil(
            Cuidador cuidador
    ) {

        String sql = """
                UPDATE cuidador
                SET descripcion_perfil = ?,
                    experiencia = ?
                WHERE id_cuidador = ?
                  AND id_usuario = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(
                    1,
                    cuidador.getDescripcionPerfil()
            );

            ps.setString(
                    2,
                    cuidador.getExperiencia()
            );

            ps.setInt(
                    3,
                    cuidador.getIdCuidador()
            );

            ps.setInt(
                    4,
                    cuidador.getIdUsuario()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    /*
     * Para una futura pantalla de administrador.
     */
    public List<Cuidador> listarPendientes() {

        List<Cuidador> cuidadores =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM cuidador
                WHERE estado_verificacion = 'PENDIENTE'
                ORDER BY fecha_solicitud
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cuidadores.add(mapearCuidador(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return cuidadores;
    }


    public List<Cuidador> listarPorEstado(String estado) {

        List<Cuidador> lista = new ArrayList<>();

        String sql = """
                SELECT *
                FROM cuidador
                WHERE estado_verificacion = ?
                ORDER BY fecha_solicitud DESC
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estado);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(mapearCuidador(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return lista;
    }

    public boolean cambiarEstadoVerificacion(
            int idCuidador,
            String estadoVerificacion,
            String observacion,
            int idAdministrador
    ) {

        String sql = """
                UPDATE cuidador
                SET estado_verificacion = ?,
                    observacion = ?,
                    fecha_verificacion = CURRENT_TIMESTAMP,
                    id_administrador_verificador = ?
                WHERE id_cuidador = ?
                  AND estado_verificacion = 'PENDIENTE'
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, estadoVerificacion);
            ps.setString(2, observacion);
            ps.setInt(3, idAdministrador);
            ps.setInt(4, idCuidador);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    private Cuidador mapearCuidador(
            ResultSet rs
    ) throws SQLException {

        Cuidador cuidador = new Cuidador();

        cuidador.setIdCuidador(
                rs.getInt("id_cuidador")
        );

        cuidador.setDescripcionPerfil(
                rs.getString("descripcion_perfil")
        );

        cuidador.setExperiencia(
                rs.getString("experiencia")
        );

        cuidador.setFechaSolicitud(
                rs.getTimestamp("fecha_solicitud")
        );

        cuidador.setEstadoVerificacion(
                rs.getString("estado_verificacion")
        );

        cuidador.setFechaVerificacion(
                rs.getTimestamp("fecha_verificacion")
        );

        cuidador.setObservacion(
                rs.getString("observacion")
        );

        cuidador.setIdUsuario(
                rs.getInt("id_usuario")
        );

        int idAdministrador = rs.getInt(
                "id_administrador_verificador"
        );

        if (rs.wasNull()) {
            cuidador.setIdAdministradorVerificador(null);
        } else {
            cuidador.setIdAdministradorVerificador(
                    idAdministrador
            );
        }

        return cuidador;
    }

    private String obtenerMensajePostgreSQL(
            SQLException e
    ) {

        String mensaje = e.getMessage();

        if (mensaje == null || mensaje.isBlank()) {
            return "OcurriÃ³ un error en la base de datos.";
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