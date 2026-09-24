package vista;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public final class ImagenAlojamientoUtil {

    private static final String BASE = "/imagenes/alojamientos/";

    private ImagenAlojamientoUtil() {
    }

    public static String principal(String nombre, String rutaAlterna) {
        String ruta = buscar(nombre, "principal");
        return ruta != null ? ruta : rutaAlterna;
    }

    public static List<String> galeria(String nombre, List<String> rutasAlternas) {
        List<String> rutas = new ArrayList<>();

        String principal = buscar(nombre, "principal");
        String secundaria = buscar(nombre, "secundaria");

        if (principal != null) {
            rutas.add(principal);
        }

        if (secundaria != null) {
            rutas.add(secundaria);
        }

        if (rutas.isEmpty() && rutasAlternas != null) {
            for (String ruta : rutasAlternas) {
                if (ruta != null && !ruta.isBlank()) {
                    rutas.add(ruta);
                }
            }
        }

        return rutas;
    }

    public static BufferedImage leer(String ruta) throws IOException {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        URL recurso = recurso(ruta);
        if (recurso != null) {
            return ImageIO.read(recurso);
        }

        File archivo = new File(ruta);
        if (archivo.exists() && archivo.isFile()) {
            return ImageIO.read(archivo);
        }

        return null;
    }

    private static String buscar(String nombre, String tipo) {
        String base = nombreBase(nombre);

        if (base.isBlank()) {
            return null;
        }

        String[] extensiones = {"jpg", "jpeg", "png"};

        for (String extension : extensiones) {
            String ruta = BASE + base + "_" + tipo + "." + extension;
            if (recurso(ruta) != null) {
                return ruta;
            }

            File archivo = new File("src" + ruta.replace('/', File.separatorChar));
            if (archivo.exists() && archivo.isFile()) {
                return archivo.getPath();
            }
        }

        return null;
    }

    private static URL recurso(String ruta) {
        String normalizada = ruta.startsWith("/") ? ruta : "/" + ruta;
        return ImagenAlojamientoUtil.class.getResource(normalizada);
    }

    private static String nombreBase(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "";
        }

        String limpio = Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]+", " ")
                .trim();

        if (limpio.isBlank()) {
            return "";
        }

        String[] palabras = limpio.split("\\s+");
        StringBuilder resultado = new StringBuilder();

        for (String palabra : palabras) {
            if (resultado.length() > 0) {
                resultado.append('_');
            }

            resultado.append(Character.toUpperCase(palabra.charAt(0)));

            if (palabra.length() > 1) {
                resultado.append(palabra.substring(1).toLowerCase());
            }
        }

        return resultado.toString();
    }
}
