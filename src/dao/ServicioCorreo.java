
package correo;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;

public class ServicioCorreo {

    private static final String REMITENTE =
            "lauramartinezt35@gmail.com";

    private static final String CLAVE_APP =
            "vwtq wgda nzzo jpby";

    private static final String NOMBRE_VISIBLE =
            "Pet Home Boarding";

    private static final String SERVIDOR = "smtp.gmail.com";
    private static final int PUERTO = 465;
    private static final int ESPERA_MS = 20000;

    private static final String ALFABETO =
            "ABCDEFGHJKMNPQRSTUVWXYZ23456789";

    private static final int LARGO_CODIGO = 8;

    private static final SecureRandom AZAR = new SecureRandom();

    public static String generarCodigoTemporal() {

        StringBuilder codigo = new StringBuilder(LARGO_CODIGO);

        for (int i = 0; i < LARGO_CODIGO; i++) {
            codigo.append(
                    ALFABETO.charAt(AZAR.nextInt(ALFABETO.length()))
            );
        }

        return codigo.toString();
    }

    public static void enviarCodigoTemporal(String destino,
                                            String nombre,
                                            String codigo)
            throws Exception {

        String saludo = (nombre == null || nombre.isBlank())
                ? "Hola"
                : "Hola " + nombre;

        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;
                            background:#F2F8FD;padding:32px 16px;">
                  <div style="max-width:520px;margin:0 auto;
                              background:#FFFFFF;border-radius:20px;
                              padding:32px 36px;
                              border:1px solid #D6E7F5;">

                    <h1 style="margin:0;font-size:20px;
                               color:#10456B;text-align:center;">
                      Pet Home Boarding
                    </h1>

                    <div style="width:48px;height:5px;
                                background:#F5C242;border-radius:5px;
                                margin:10px auto 24px auto;"></div>

                    <p style="font-size:15px;color:#10456B;
                              margin:0 0 14px 0;">
                      %s,
                    </p>

                    <p style="font-size:14px;color:#788A9A;
                              line-height:1.6;margin:0 0 24px 0;">
                      Recibimos una solicitud para restablecer la
                      contraseña de tu cuenta. Esta es tu
                      contraseña temporal:
                    </p>

                    <div style="background:#FCF0CD;
                                border-radius:14px;padding:18px;
                                text-align:center;margin-bottom:24px;">
                      <span style="font-family:Consolas,monospace;
                                   font-size:30px;font-weight:bold;
                                   letter-spacing:5px;color:#96711A;">
                        %s
                      </span>
                    </div>

                    <p style="font-size:14px;color:#788A9A;
                              line-height:1.6;margin:0 0 8px 0;">
                      Inicia sesión con esta contraseña y luego
                      cámbiala desde tu perfil.
                    </p>

                    <p style="font-size:13px;color:#A9B7C3;
                              line-height:1.6;margin:20px 0 0 0;">
                      Si no fuiste tú quien pidió el cambio,
                      ignora este mensaje: tu contraseña anterior
                      dejó de funcionar, pero nadie puede entrar
                      sin este código.
                    </p>

                  </div>
                </div>
                """.formatted(escapar(saludo), codigo);

        enviar(destino,
                "Tu contraseña temporal - Pet Home Boarding",
                html);
    }

    private static void enviar(String destino,
                               String asunto,
                               String cuerpoHtml) throws Exception {

        if (REMITENTE.startsWith("tucorreo")) {
            throw new Exception(
                    "Falta configurar el correo y la contraseña "
                            + "de aplicación en ServicioCorreo.java"
            );
        }

        SSLSocketFactory fabrica =
                (SSLSocketFactory) SSLSocketFactory.getDefault();

        try (SSLSocket socket =
                     (SSLSocket) fabrica.createSocket(SERVIDOR, PUERTO)) {

            socket.setSoTimeout(ESPERA_MS);
            socket.startHandshake();

            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            );

            OutputStream salida = socket.getOutputStream();

            leerRespuesta(entrada, 220);

            enviarLinea(salida, "EHLO pethomeboarding");
            leerRespuesta(entrada, 250);

            enviarLinea(salida, "AUTH LOGIN");
            leerRespuesta(entrada, 334);

            enviarLinea(salida, aBase64(REMITENTE));
            leerRespuesta(entrada, 334);

            enviarLinea(salida, aBase64(CLAVE_APP.replace(" ", "")));
            leerRespuesta(entrada, 235);

            enviarLinea(salida, "MAIL FROM:<" + REMITENTE + ">");
            leerRespuesta(entrada, 250);

            enviarLinea(salida, "RCPT TO:<" + destino + ">");
            leerRespuesta(entrada, 250);

            enviarLinea(salida, "DATA");
            leerRespuesta(entrada, 354);

            enviarCrudo(salida, armarMensaje(destino, asunto, cuerpoHtml));
            leerRespuesta(entrada, 250);

            enviarLinea(salida, "QUIT");
        }
    }

    private static String armarMensaje(String destino,
                                       String asunto,
                                       String cuerpoHtml) {

        String fecha = ZonedDateTime.now().format(
                DateTimeFormatter.ofPattern(
                        "EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH
                )
        );

        String cuerpo = Base64.getMimeEncoder().encodeToString(
                cuerpoHtml.getBytes(StandardCharsets.UTF_8)
        );

        return "From: " + codificarTexto(NOMBRE_VISIBLE)
                + " <" + REMITENTE + ">\r\n"
                + "To: <" + destino + ">\r\n"
                + "Subject: " + codificarTexto(asunto) + "\r\n"
                + "Date: " + fecha + "\r\n"
                + "MIME-Version: 1.0\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Transfer-Encoding: base64\r\n"
                + "\r\n"
                + cuerpo
                + "\r\n.\r\n";
    }

    private static void enviarLinea(OutputStream salida, String texto)
            throws Exception {
        enviarCrudo(salida, texto + "\r\n");
    }

    private static void enviarCrudo(OutputStream salida, String texto)
            throws Exception {
        salida.write(texto.getBytes(StandardCharsets.UTF_8));
        salida.flush();
    }

    private static String leerRespuesta(BufferedReader entrada,
                                        int esperado)
            throws Exception {

        StringBuilder completa = new StringBuilder();
        String linea;

        while ((linea = entrada.readLine()) != null) {

            completa.append(linea).append('\n');

            if (linea.length() < 4 || linea.charAt(3) != '-') {
                break;
            }
        }

        if (linea == null) {
            throw new Exception(
                    "El servidor de correo cerró la conexión."
            );
        }

        int codigo;

        try {
            codigo = Integer.parseInt(linea.substring(0, 3));
        } catch (NumberFormatException e) {
            throw new Exception(
                    "Respuesta inesperada del servidor: " + linea
            );
        }

        if (codigo != esperado) {
            throw new Exception(traducirError(codigo, linea));
        }

        return completa.toString();
    }

    private static String traducirError(int codigo, String linea) {

        if (codigo == 535) {
            return "Gmail rechazó el usuario o la contraseña.\n\n"
                    + "Revisa que en ServicioCorreo.java estés usando "
                    + "la contraseña de aplicación de 16 letras, no la "
                    + "contraseña normal de tu cuenta, y que la "
                    + "verificación en 2 pasos esté activada.";
        }

        if (codigo == 550 || codigo == 553) {
            return "Gmail no aceptó la dirección de destino. "
                    + "Verifica que el correo esté bien escrito.";
        }

        if (codigo == 421 || codigo == 454) {
            return "Gmail está limitando el envío en este momento. "
                    + "Espera un momento y vuelve a intentar.";
        }

        return "El servidor de correo respondió: " + linea;
    }

    private static String aBase64(String texto) {
        return Base64.getEncoder().encodeToString(
                texto.getBytes(StandardCharsets.UTF_8)
        );
    }

    private static String codificarTexto(String texto) {
        return "=?UTF-8?B?" + aBase64(texto) + "?=";
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    public static void main(String[] args) {

        String miCorreoDePrueba = "escribe.aqui.tu.correo@gmail.com";

        try {
            String codigo = generarCodigoTemporal();

            System.out.println("Código generado: " + codigo);
            System.out.println("Enviando a " + miCorreoDePrueba + "...");

            enviarCodigoTemporal(miCorreoDePrueba, "Prueba", codigo);

            System.out.println("Enviado. Revisa tu bandeja de entrada.");

        } catch (Exception e) {
            System.out.println("No se pudo enviar:");
            System.out.println(e.getMessage());
        }
    }
}