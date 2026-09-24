
package vista;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GeneradorPdf {

    private static final Charset WIN_ANSI = Charset.forName("windows-1252");
    private static final double ANCHO = 842;
    private static final double ALTO = 595;
    private static final double MARGEN = 22;

    private GeneradorPdf() {
    }

    public static void generar(File archivo, String titulo,
                               List<String> detalles,
                               LinkedHashMap<String, String> resumen,
                               String[] columnas,
                               List<Object[]> filas) throws IOException {
        List<Pagina> paginas = new ArrayList<>();
        paginas.add(paginaResumen(titulo, detalles, resumen));
        agregarTabla(paginas, titulo, columnas, filas);

        for (int i = 0; i < paginas.size(); i++) {
            Pagina pagina = paginas.get(i);
            pagina.lienzo.texto(ANCHO - 92, 14, 6.5, false,
                    "Página " + (i + 1) + " de " + paginas.size());
        }

        escribirPdf(archivo, paginas);
    }

    private static Pagina paginaResumen(String titulo,
                                        List<String> detalles,
                                        LinkedHashMap<String, String> resumen) {
        Pagina pagina = new Pagina();
        Lienzo lienzo = pagina.lienzo;
        encabezado(lienzo, titulo, false);

        double y = 493;
        lienzo.texto(MARGEN, y, 10, true, "DATOS DEL REPORTE");
        y -= 18;
        for (String detalle : detalles) {
            for (String linea : envolver(detalle, 110)) {
                lienzo.texto(MARGEN, y, 8.5, false, linea);
                y -= 12;
            }
        }

        y -= 10;
        lienzo.texto(MARGEN, y, 10, true, "RESUMEN");
        y -= 18;
        int indice = 0;
        double anchoTarjeta = (ANCHO - MARGEN * 2 - 12) / 2;
        double altoTarjeta = 34;
        for (Map.Entry<String, String> dato : resumen.entrySet()) {
            int columna = indice % 2;
            int fila = indice / 2;
            double x = MARGEN + columna * (anchoTarjeta + 12);
            double superior = y - fila * (altoTarjeta + 8);
            lienzo.relleno(0.95, 0.98, 1.0);
            lienzo.rectanguloRelleno(x, superior - altoTarjeta,
                    anchoTarjeta, altoTarjeta);
            lienzo.relleno(0.08, 0.24, 0.40);
            lienzo.texto(x + 8, superior - 13, 7.3, false,
                    acortar(dato.getKey(), 54));
            lienzo.texto(x + 8, superior - 27, 10, true,
                    dato.getValue());
            indice++;
        }

        if (resumen.isEmpty()) {
            lienzo.texto(MARGEN, y, 9, false, "Sin datos");
        }
        return pagina;
    }

    private static void agregarTabla(List<Pagina> paginas, String titulo,
                                     String[] columnas,
                                     List<Object[]> filas) {
        double[] anchos = anchosColumnas(columnas, filas);
        Pagina pagina = nuevaPaginaTabla(titulo, false);
        paginas.add(pagina);
        double y = 492;
        y = encabezadoTabla(pagina.lienzo, columnas, anchos, y);

        if (filas.isEmpty()) {
            pagina.lienzo.texto(MARGEN, y - 24, 9, false,
                    "Sin datos para los filtros seleccionados.");
            return;
        }

        for (Object[] fila : filas) {
            List<List<String>> celdas = new ArrayList<>();
            int maxLineas = 1;
            for (int i = 0; i < columnas.length; i++) {
                String valor = i < fila.length && fila[i] != null
                        ? String.valueOf(fila[i]) : "-";
                int maxCaracteres = Math.max(4,
                        (int) (anchos[i] / 3.05));
                List<String> lineas = envolver(valor, maxCaracteres);
                celdas.add(lineas);
                maxLineas = Math.max(maxLineas, lineas.size());
            }

            double altoFila = Math.max(20, 7 + maxLineas * 6.4);
            if (y - altoFila < 28) {
                pagina = nuevaPaginaTabla(titulo, true);
                paginas.add(pagina);
                y = 492;
                y = encabezadoTabla(pagina.lienzo, columnas, anchos, y);
            }

            double x = MARGEN;
            for (int i = 0; i < columnas.length; i++) {
                pagina.lienzo.trazo(0.83, 0.89, 0.94);
                pagina.lienzo.rectangulo(x, y - altoFila,
                        anchos[i], altoFila);
                List<String> lineas = celdas.get(i);
                for (int j = 0; j < lineas.size(); j++) {
                    pagina.lienzo.texto(x + 2.5,
                            y - 8.5 - j * 6.4,
                            5.2, false, lineas.get(j));
                }
                x += anchos[i];
            }
            y -= altoFila;
        }
    }

    private static Pagina nuevaPaginaTabla(String titulo,
                                           boolean continuacion) {
        Pagina pagina = new Pagina();
        encabezado(pagina.lienzo, titulo, continuacion);
        pagina.lienzo.texto(MARGEN, 514, 9, true,
                continuacion ? "TABLA DETALLADA - CONTINUACIÓN"
                        : "TABLA DETALLADA");
        return pagina;
    }

    private static void encabezado(Lienzo lienzo, String titulo,
                                   boolean continuacion) {
        lienzo.relleno(0.06, 0.23, 0.40);
        lienzo.rectanguloRelleno(0, ALTO - 72, ANCHO, 72);
        lienzo.relleno(1, 1, 1);
        lienzo.texto(MARGEN, ALTO - 28, 16, true,
                "PET HOME BOARDING");
        lienzo.texto(MARGEN, ALTO - 49, 11, true,
                titulo + (continuacion ? " - CONTINUACIÓN" : ""));
        lienzo.relleno(0.08, 0.24, 0.40);
    }

    private static double encabezadoTabla(Lienzo lienzo,
                                          String[] columnas,
                                          double[] anchos, double y) {
        List<List<String>> textos = new ArrayList<>();
        int maxLineas = 1;
        for (int i = 0; i < columnas.length; i++) {
            int maxCaracteres = Math.max(4,
                    (int) (anchos[i] / 3.15));
            List<String> lineas = envolver(columnas[i], maxCaracteres);
            textos.add(lineas);
            maxLineas = Math.max(maxLineas, lineas.size());
        }
        double alto = Math.max(24, 8 + maxLineas * 6.8);
        double x = MARGEN;
        for (int i = 0; i < columnas.length; i++) {
            lienzo.relleno(0.88, 0.94, 0.98);
            lienzo.rectanguloRelleno(x, y - alto, anchos[i], alto);
            lienzo.trazo(0.70, 0.80, 0.88);
            lienzo.rectangulo(x, y - alto, anchos[i], alto);
            for (int j = 0; j < textos.get(i).size(); j++) {
                lienzo.relleno(0.06, 0.23, 0.40);
                lienzo.texto(x + 2.5, y - 9 - j * 6.8,
                        5.4, true, textos.get(i).get(j));
            }
            x += anchos[i];
        }
        lienzo.relleno(0.08, 0.24, 0.40);
        return y - alto;
    }

    private static double[] anchosColumnas(String[] columnas,
                                           List<Object[]> filas) {
        double[] pesos = new double[columnas.length];
        double suma = 0;
        for (int i = 0; i < columnas.length; i++) {
            int maximo = columnas[i].length();
            int limiteFilas = Math.min(filas.size(), 200);
            for (int j = 0; j < limiteFilas; j++) {
                Object[] fila = filas.get(j);
                if (i < fila.length && fila[i] != null) {
                    maximo = Math.max(maximo,
                            String.valueOf(fila[i]).length());
                }
            }
            pesos[i] = Math.max(6, Math.min(21, maximo));
            if (i == 0 || i == 10) {
                pesos[i] = Math.min(pesos[i], 9);
            }
            suma += pesos[i];
        }

        double disponible = ANCHO - MARGEN * 2;
        double[] anchos = new double[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            anchos[i] = disponible * pesos[i] / suma;
        }
        return anchos;
    }

    private static List<String> envolver(String texto, int maximo) {
        String limpio = texto == null ? "-"
                : texto.replace('\n', ' ').replace('\r', ' ').trim();
        if (limpio.isEmpty()) {
            limpio = "-";
        }
        List<String> lineas = new ArrayList<>();
        String[] palabras = limpio.split("\\s+");
        StringBuilder actual = new StringBuilder();
        for (String palabra : palabras) {
            if (palabra.length() > maximo) {
                if (actual.length() > 0) {
                    lineas.add(actual.toString());
                    actual.setLength(0);
                }
                int inicio = 0;
                while (inicio < palabra.length()) {
                    int fin = Math.min(palabra.length(), inicio + maximo);
                    lineas.add(palabra.substring(inicio, fin));
                    inicio = fin;
                }
            } else if (actual.length() == 0) {
                actual.append(palabra);
            } else if (actual.length() + 1 + palabra.length() <= maximo) {
                actual.append(' ').append(palabra);
            } else {
                lineas.add(actual.toString());
                actual.setLength(0);
                actual.append(palabra);
            }
        }
        if (actual.length() > 0) {
            lineas.add(actual.toString());
        }
        if (lineas.isEmpty()) {
            lineas.add("-");
        }
        return lineas;
    }

    private static String acortar(String texto, int maximo) {
        if (texto == null || texto.length() <= maximo) {
            return texto == null ? "" : texto;
        }
        return texto.substring(0, maximo - 3) + "...";
    }

    private static void escribirPdf(File archivo,
                                    List<Pagina> paginas) throws IOException {
        int totalObjetos = 4 + paginas.size() * 2;
        byte[][] objetos = new byte[totalObjetos + 1][];
        objetos[1] = ascii("<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder hijos = new StringBuilder("[");
        for (int i = 0; i < paginas.size(); i++) {
            hijos.append(5 + i * 2).append(" 0 R ");
        }
        hijos.append(']');
        objetos[2] = ascii("<< /Type /Pages /Kids " + hijos
                + " /Count " + paginas.size() + " >>");
        objetos[3] = ascii("<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");
        objetos[4] = ascii("<< /Type /Font /Subtype /Type1 "
                + "/BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");

        for (int i = 0; i < paginas.size(); i++) {
            int idPagina = 5 + i * 2;
            int idContenido = idPagina + 1;
            objetos[idPagina] = ascii("<< /Type /Page /Parent 2 0 R "
                    + "/MediaBox [0 0 842 595] "
                    + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> "
                    + "/Contents " + idContenido + " 0 R >>");
            byte[] contenido = paginas.get(i).lienzo.bytes();
            ByteArrayOutputStream objetoContenido = new ByteArrayOutputStream();
            escribir(objetoContenido, ascii("<< /Length "
                    + contenido.length + " >>\nstream\n"));
            escribir(objetoContenido, contenido);
            escribir(objetoContenido, ascii("\nendstream"));
            objetos[idContenido] = objetoContenido.toByteArray();
        }

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        escribir(pdf, "%PDF-1.4\n%âãÏÓ\n".getBytes(WIN_ANSI));
        long[] posiciones = new long[objetos.length];
        for (int i = 1; i < objetos.length; i++) {
            posiciones[i] = pdf.size();
            escribir(pdf, ascii(i + " 0 obj\n"));
            escribir(pdf, objetos[i]);
            escribir(pdf, ascii("\nendobj\n"));
        }

        long inicioXref = pdf.size();
        escribir(pdf, ascii("xref\n0 " + objetos.length + "\n"));
        escribir(pdf, ascii("0000000000 65535 f \n"));
        for (int i = 1; i < objetos.length; i++) {
            escribir(pdf, ascii(String.format("%010d 00000 n \n",
                    posiciones[i])));
        }
        escribir(pdf, ascii("trailer\n<< /Size " + objetos.length
                + " /Root 1 0 R >>\nstartxref\n" + inicioXref
                + "\n%%EOF\n"));

        try (BufferedOutputStream salida = new BufferedOutputStream(
                new FileOutputStream(archivo))) {
            pdf.writeTo(salida);
        }
    }

    private static byte[] ascii(String texto) {
        return texto.getBytes(StandardCharsets.ISO_8859_1);
    }

    private static void escribir(ByteArrayOutputStream salida,
                                 byte[] datos) throws IOException {
        salida.write(datos);
    }

    private static final class Pagina {
        final Lienzo lienzo = new Lienzo();
    }

    private static final class Lienzo {
        private final ByteArrayOutputStream salida = new ByteArrayOutputStream();

        void texto(double x, double y, double tamano, boolean negrita,
                   String texto) {
            comando("BT /" + (negrita ? "F2" : "F1") + " "
                    + numero(tamano) + " Tf " + numero(x) + " "
                    + numero(y) + " Td (");
            byte[] bytes = limpiar(texto).getBytes(WIN_ANSI);
            for (byte valor : bytes) {
                int b = valor & 0xFF;
                if (b == '(' || b == ')' || b == '\\') {
                    salida.write('\\');
                }
                salida.write(b);
            }
            comando(") Tj ET\n");
        }

        void relleno(double r, double g, double b) {
            comando(numero(r) + " " + numero(g) + " "
                    + numero(b) + " rg\n");
        }

        void trazo(double r, double g, double b) {
            comando(numero(r) + " " + numero(g) + " "
                    + numero(b) + " RG\n");
        }

        void rectanguloRelleno(double x, double y, double ancho,
                               double alto) {
            comando(numero(x) + " " + numero(y) + " "
                    + numero(ancho) + " " + numero(alto) + " re f\n");
        }

        void rectangulo(double x, double y, double ancho, double alto) {
            comando(numero(x) + " " + numero(y) + " "
                    + numero(ancho) + " " + numero(alto) + " re S\n");
        }

        byte[] bytes() {
            return salida.toByteArray();
        }

        private void comando(String comando) {
            byte[] bytes = comando.getBytes(StandardCharsets.ISO_8859_1);
            salida.write(bytes, 0, bytes.length);
        }

        private String limpiar(String texto) {
            return texto == null ? ""
                    : texto.replace('\n', ' ').replace('\r', ' ');
        }

        private String numero(double valor) {
            return String.format(java.util.Locale.US, "%.2f", valor);
        }
    }
}
