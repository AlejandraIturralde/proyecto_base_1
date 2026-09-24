

package dao;

import conexion.ConexionBD;
import modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public int registrarUsuario(Usuario usuario) {

        String sql = """
                SELECT fn_registrar_usuario(
                    ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCedula());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getCorreo());
            ps.setString(
                    6,
                    usuario.getContrasenaHash()
            );
            ps.setDate(
                    7,
                    usuario.getFechaNacimiento()
            );
            ps.setString(8, usuario.getFotoPerfil());
            ps.setString(
                    9,
                    usuario.getDireccionDomicilio()
            );

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return 0;
    }

    public Usuario iniciarSesion(
            String correo,
            String contrasenaHash
    ) {

        String sql = """
                SELECT *
                FROM usuario
                WHERE LOWER(email) = LOWER(?)
                  AND contrasena_hash = ?
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, contrasenaHash);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return null;
    }

    public List<Usuario> listarUsuarios() {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
                SELECT *
                FROM usuario
                WHERE activo = TRUE
                ORDER BY nombre, apellido
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return usuarios;
    }

    public Usuario buscarUsuarioPorId(int idUsuario) {

        String sql = """
                SELECT *
                FROM usuario
                WHERE id_usuario = ?
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return null;
    }

    public Usuario buscarUsuarioPorCorreo(
            String correo
    ) {

        String sql = """
                SELECT *
                FROM usuario
                WHERE LOWER(email) = LOWER(?)
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, correo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }

        return null;
    }

    public boolean actualizarUsuario(
            Usuario usuario
    ) {

        String sql = """
                UPDATE usuario
                SET nombre = ?,
                    apellido = ?,
                    cedula = ?,
                    telefono = ?,
                    email = ?,
                    fecha_nacimiento = ?,
                    foto_perfil = ?,
                    direccion_domicilio = ?
                WHERE id_usuario = ?
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCedula());
            ps.setString(4, usuario.getTelefono());
            ps.setString(5, usuario.getCorreo());
            ps.setDate(
                    6,
                    usuario.getFechaNacimiento()
            );
            ps.setString(7, usuario.getFotoPerfil());
            ps.setString(
                    8,
                    usuario.getDireccionDomicilio()
            );
            ps.setInt(9, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    public boolean cambiarContrasena(
            int idUsuario,
            String nuevaContrasenaHash
    ) {

        String sql = """
                UPDATE usuario
                SET contrasena_hash = ?
                WHERE id_usuario = ?
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, nuevaContrasenaHash);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    public boolean desactivarUsuario(int idUsuario) {

        String sql = """
                UPDATE usuario
                SET activo = FALSE
                WHERE id_usuario = ?
                  AND activo = TRUE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    public boolean reactivarUsuario(int idUsuario) {

        String sql = """
                UPDATE usuario
                SET activo = TRUE
                WHERE id_usuario = ?
                  AND activo = FALSE
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }


    public boolean actualizarRol(int idUsuario, String rol) {

        String sql = """
                UPDATE usuario
                SET rol_sistema = ?
                WHERE id_usuario = ?
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, rol);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    obtenerMensajePostgreSQL(e)
            );
        }
    }

    public boolean existeCorreo(String correo) {

        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM usuario
                    WHERE LOWER(email) = LOWER(?)
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, correo);

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

    public boolean existeCedula(String cedula) {

        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM usuario
                    WHERE cedula = ?
                )
                """;

        try (Connection conexion = ConexionBD.conectar();
             PreparedStatement ps =
                     conexion.prepareStatement(sql)) {

            ps.setString(1, cedula);

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

    private Usuario mapearUsuario(
            ResultSet rs
    ) throws SQLException {

        Usuario usuario = new Usuario();

        usuario.setIdUsuario(
                rs.getInt("id_usuario")
        );
        usuario.setNombre(
                rs.getString("nombre")
        );
        usuario.setApellido(
                rs.getString("apellido")
        );
        usuario.setCedula(
                rs.getString("cedula")
        );
        usuario.setTelefono(
                rs.getString("telefono")
        );
        usuario.setCorreo(
                rs.getString("email")
        );
        usuario.setContrasenaHash(
                rs.getString("contrasena_hash")
        );
        usuario.setFechaNacimiento(
                rs.getDate("fecha_nacimiento")
        );
        usuario.setFotoPerfil(
                rs.getString("foto_perfil")
        );
        usuario.setDireccionDomicilio(
                rs.getString("direccion_domicilio")
        );
        usuario.setFechaRegistro(
                rs.getTimestamp("fecha_registro")
        );
        usuario.setRolSistema(
                rs.getString("rol_sistema")
        );
        usuario.setActivo(
                rs.getBoolean("activo")
        );

        return usuario;
    }

    private String obtenerMensajePostgreSQL(
            SQLException e
    ) {

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