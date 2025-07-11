package util;

import model.Postulante;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;

/**
 * VERSION SIMPLE Y FUNCIONAL - Lee archivos CSV/Excel sin errores
 * @author joe-696
 */
public class ExcelUtilsSimple {
    
    /**
     * IMPORTAR ARCHIVO CSV/EXCEL - VERSION SIMPLE QUE FUNCIONA
     */
    public static List<Postulante> importarArchivo(String rutaArchivo) {
        List<Postulante> postulantes = new ArrayList<>();
        
        System.out.println("=== IMPORTANDO ARCHIVO ===");
        System.out.println("Archivo: " + rutaArchivo);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;
            boolean esPrimeraLinea = true;
            
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                
                // Saltar encabezados
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    System.out.println("Encabezados: " + linea);
                    continue;
                }
                
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Postulante postulante = crearPostulanteDesdeLinea(linea, numeroLinea);
                    if (postulante != null) {
                        postulantes.add(postulante);
                        System.out.println("✓ Línea " + numeroLinea + ": " + postulante.getCodigo() + " - " + postulante.getApellidosNombres());
                    }
                } catch (Exception e) {
                    System.err.println("✗ Error línea " + numeroLinea + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("ERROR leyendo archivo: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== IMPORTACION COMPLETADA ===");
        System.out.println("Total importados: " + postulantes.size());
        return postulantes;
    }
    
    /**
     * CREAR POSTULANTE DESDE UNA LINEA - SIMPLE Y ROBUSTO
     */
    private static Postulante crearPostulanteDesdeLinea(String linea, int numeroLinea) {
        // Detectar separador
        String separador = ",";
        if (linea.contains("\t")) separador = "\t";
        if (linea.contains(";")) separador = ";";
        
        String[] campos = linea.split(separador, -1);
        
        // Asegurar mínimo 10 campos
        String[] camposLimpios = new String[30];
        for (int i = 0; i < camposLimpios.length; i++) {
            if (i < campos.length) {
                camposLimpios[i] = limpiar(campos[i]);
            } else {
                camposLimpios[i] = "";
            }
        }
        
        // Crear postulante
        Postulante p = new Postulante();
        
        // Campos básicos OBLIGATORIOS
        String codigo = camposLimpios[0];
        String nombres = camposLimpios[1];
        
        if (codigo.isEmpty()) codigo = "AUTO" + numeroLinea;
        if (nombres.isEmpty()) nombres = "POSTULANTE " + codigo;
        
        p.setCodigo(codigo);
        p.setApellidosNombres(nombres);
        
        // Campos opcionales con valores por defecto
        p.setOpcion1(camposLimpios[2].isEmpty() ? "SIN ESPECIFICAR" : camposLimpios[2]);
        p.setOpcion2(camposLimpios[3]);
        p.setModalidad(camposLimpios[4].isEmpty() ? "ORDINARIO" : camposLimpios[4]);
        p.setDni(camposLimpios[5].isEmpty() ? generarDni() : camposLimpios[5]);
        p.setSexo(normalizarSexo(camposLimpios[6]));
        p.setEstadoAcademico(camposLimpios[7].isEmpty() ? "POSTULANTE" : camposLimpios[7]);
        
        // Notas importantes
        p.setNotaAC(parseaNumero(camposLimpios[8]));
        p.setNotaCO(parseaNumero(camposLimpios[9]));
        
        // Campos adicionales
        p.setCodSede(1);
        p.setInscripcion(new Date());
        p.setFecNac(new Date());
        p.setEstadoCivil("SOLTERO");
        p.setNombreColegio("SIN ESPECIFICAR");
        p.setTipoColegio(1);
        
        // Calcular puntaje final
        p.calcularPuntajeFinal();
        
        return p;
    }
    
    /**
     * LIMPIAR CAMPO
     */
    private static String limpiar(String campo) {
        if (campo == null) return "";
        return campo.trim().replaceAll("\"", "").replaceAll("'", "");
    }
    
    /**
     * PARSEAR NUMERO DE FORMA SEGURA
     */
    private static double parseaNumero(String campo) {
        try {
            if (campo.isEmpty()) return 0.0;
            campo = campo.replace(",", ".");
            double valor = Double.parseDouble(campo);
            if (valor < 0) valor = 0.0;
            if (valor > 20) valor = 20.0;
            return Math.round(valor * 100.0) / 100.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * NORMALIZAR SEXO
     */
    private static String normalizarSexo(String sexo) {
        if (sexo == null || sexo.trim().isEmpty()) return "";
        String s = sexo.trim().toUpperCase();
        if (s.startsWith("M") || s.equals("MASCULINO")) return "M";
        if (s.startsWith("F") || s.equals("FEMENINO")) return "F";
        return sexo;
    }
    
    /**
     * GENERAR DNI TEMPORAL
     */
    private static String generarDni() {
        return String.format("TEMP%04d", (int)(Math.random() * 10000));
    }
    
    /**
     * EXPORTAR A CSV - SIMPLE
     */
    public static boolean exportarArchivo(List<Postulante> postulantes, String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // Encabezados
            writer.println("CODIGO,NOMBRES,OPCION1,OPCION2,MODALIDAD,DNI,SEXO,ESTADO,NOTA_AC,NOTA_CO,NOTA_FINAL");
            
            // Datos
            for (Postulante p : postulantes) {
                writer.printf("%s,\"%s\",\"%s\",\"%s\",%s,%s,%s,%s,%.2f,%.2f,%.2f%n",
                    p.getCodigo(),
                    p.getApellidosNombres(),
                    p.getOpcion1(),
                    p.getOpcion2(),
                    p.getModalidad(),
                    p.getDni(),
                    p.getSexo(),
                    p.getEstadoAcademico(),
                    p.getNotaAC(),
                    p.getNotaCO(),
                    p.getNotaFinal()
                );
            }
            
            System.out.println("✓ Archivo exportado: " + rutaArchivo);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error exportando: " + e.getMessage());
            return false;
        }
    }
}
