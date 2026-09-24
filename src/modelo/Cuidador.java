package modelo;

import java.sql.Timestamp;

public class Cuidador {

    private int idCuidador;
    private String descripcionPerfil;
    private String experiencia;
    private Timestamp fechaSolicitud;
    private String estadoVerificacion;
    private Timestamp fechaVerificacion;
    private String observacion;
    private int idUsuario;

    /*
     * Integer porque puede ser NULL mientras
     * ningún administrador haya verificado.
     */
    private Integer idAdministradorVerificador;

    public Cuidador() {
    }

    // Constructor para solicitar ser cuidador
    public Cuidador(
            String descripcionPerfil,
            String experiencia,
            int idUsuario
    ) {
        this.descripcionPerfil = descripcionPerfil;
        this.experiencia = experiencia;
        this.idUsuario = idUsuario;
    }

    public int getIdCuidador() {
        return idCuidador;
    }

    public void setIdCuidador(int idCuidador) {
        this.idCuidador = idCuidador;
    }

    public String getDescripcionPerfil() {
        return descripcionPerfil;
    }

    public void setDescripcionPerfil(
            String descripcionPerfil
    ) {
        this.descripcionPerfil = descripcionPerfil;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public Timestamp getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(
            Timestamp fechaSolicitud
    ) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getEstadoVerificacion() {
        return estadoVerificacion;
    }

    public void setEstadoVerificacion(
            String estadoVerificacion
    ) {
        this.estadoVerificacion = estadoVerificacion;
    }

    public Timestamp getFechaVerificacion() {
        return fechaVerificacion;
    }

    public void setFechaVerificacion(
            Timestamp fechaVerificacion
    ) {
        this.fechaVerificacion = fechaVerificacion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdAdministradorVerificador() {
        return idAdministradorVerificador;
    }

    public void setIdAdministradorVerificador(
            Integer idAdministradorVerificador
    ) {
        this.idAdministradorVerificador =
                idAdministradorVerificador;
    }
}