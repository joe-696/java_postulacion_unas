package util;

import model.Postulante;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.*;

/**
 * Utilidades para manejo de archivos Excel y datos de postulantes
 * Simula la importación real para fines académicos
 * @author joe-696
 */
public class ExcelUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Exportar postulantes a CSV/Excel
     */
    public static boolean exportarPostulantesAExcel(List<Postulante> postulantes, String rutaArchivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // Encabezados detallados
            writer.println("CODIGO,APELLIDOS_NOMBRES,OPCION1,OPCION2,MODALIDAD,DNI,SEXO,ESTADO_ACADEMICO,NOTA_AC,NOTA_CO,PUNTAJE_FINAL,ESTADO_INGRESO,FECHA_INSCRIPCION");
            
            // Datos
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for (Postulante p : postulantes) {
                String fechaInscripcion = p.getInscripcion() != null ? sdf.format(p.getInscripcion()) : "";
                String estadoIngreso = p.getPuntajeFinal() >= 11.0 ? "INGRESÓ" : "NO INGRESÓ";
                
                writer.printf("%s,\"%s\",\"%s\",\"%s\",%s,%s,%s,%s,%.2f,%.2f,%.2f,%s,%s%n",
                    p.getCodigo(),
                    p.getApellidosNombres(),
                    p.getOpcion1() != null ? p.getOpcion1() : "",
                    p.getOpcion2() != null ? p.getOpcion2() : "",
                    p.getModalidad() != null ? p.getModalidad() : "",
                    p.getDni(),
                    p.getSexo() != null ? p.getSexo() : "",
                    p.getEstadoAcademico(),
                    p.getNotaAC(),
                    p.getNotaCO(),
                    p.getPuntajeFinal(),
                    estadoIngreso,
                    fechaInscripcion
                );
            }
            
            System.out.println("✅ Archivo exportado: " + rutaArchivo);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error exportando: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Importar postulantes desde archivo Excel/CSV con formato específico UNAS
     * Soporta archivos .xlsx, .xls, .csv y .txt
     */
    public static List<Postulante> importarPostulantesDesdeExcel(String rutaArchivo) {
        List<Postulante> postulantes = new ArrayList<>();
        
        try {
            System.out.println("📥 Importando postulantes desde: " + rutaArchivo);
            
            // Determinar tipo de archivo
            String extension = obtenerExtension(rutaArchivo).toLowerCase();
            
            switch (extension) {
                case "csv":
                case "txt":
                    postulantes = importarDesdeCSV(rutaArchivo);
                    break;
                case "xlsx":
                case "xls":
                    // Para Excel real necesitarías Apache POI, por ahora usamos CSV
                    JOptionPane.showMessageDialog(null,
                        "⚠️ Para archivos Excel (.xlsx/.xls):\n\n" +
                        "1. Abra el archivo en Excel\n" +
                        "2. Guárdelo como CSV (separado por comas)\n" +
                        "3. Seleccione el archivo CSV guardado\n\n" +
                        "Esto asegura compatibilidad total.",
                        "Formato Excel",
                        JOptionPane.INFORMATION_MESSAGE);
                    return postulantes;
                default:
                    throw new IllegalArgumentException("Formato no soportado: " + extension);
            }
            
            System.out.println("✅ Importación completada: " + postulantes.size() + " postulantes");
            
        } catch (Exception e) {
            System.err.println("❌ Error importando archivo: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "❌ Error importando archivo:\n" + e.getMessage(),
                "Error de Importación",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return postulantes;
    }
    
    /**
     * Importar desde archivo CSV/TXT
     */
    private static List<Postulante> importarDesdeCSV(String rutaArchivo) throws IOException {
        List<Postulante> postulantes = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo, java.nio.charset.StandardCharsets.UTF_8))) {
            String linea;
            boolean esPrimeraLinea = true;
            int lineaNumero = 0;
            
            while ((linea = reader.readLine()) != null) {
                lineaNumero++;
                
                // Saltar encabezados
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    continue;
                }
                
                // Saltar líneas vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Postulante postulante = parsearLineaExcel(linea, lineaNumero);
                    if (postulante != null) {
                        postulantes.add(postulante);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error en línea " + lineaNumero + ": " + e.getMessage());
                }
            }
        }
        
        return postulantes;
    }
    
    /**
     * Obtener extensión de archivo
     */
    private static String obtenerExtension(String rutaArchivo) {
        int lastDot = rutaArchivo.lastIndexOf('.');
        return lastDot > 0 ? rutaArchivo.substring(lastDot + 1) : "";
    }
    
    /**
     * Parsear una línea del Excel/CSV según el formato UNAS
     * Soporta separación por comas, tabulaciones o punto y coma
     */
    private static Postulante parsearLineaExcel(String linea, int numeroLinea) {
        // Detectar separador automáticamente
        String separador = detectarSeparador(linea);
        String[] campos = linea.split(separador, -1); // -1 mantiene campos vacíos
        
        if (campos.length < 6) {  // Mínimo: código, nombre, opcion1, modalidad, dni, sexo
            System.err.println("⚠️ Línea " + numeroLinea + " incompleta, mínimo 6 campos requeridos, encontrados: " + campos.length);
            return null;
        }
        
        try {
            Postulante postulante = new Postulante();
            
            // Mapear campos básicos (siempre presentes)
            postulante.setCodigo(limpiarCampo(campos[0]));                    // CODIGO
            postulante.setApellidosNombres(limpiarCampo(campos[1]));          // Apellidos y Nombres
            
            if (campos.length > 2) postulante.setOpcion1(limpiarCampo(campos[2]));                   // OPCION 1
            if (campos.length > 3) postulante.setOpcion2(limpiarCampo(campos[3]));                   // OPCION 2
            if (campos.length > 4) postulante.setModalidad(limpiarCampo(campos[4]));                 // MODALIDAD
            if (campos.length > 5) postulante.setDni(limpiarCampo(campos[5]));                       // DNI
            
            // Campos opcionales con verificación de índice
            if (campos.length > 6) postulante.setCodSede(parsearEntero(campos[6], 1));               // CODSEDE
            if (campos.length > 7) postulante.setInscripcion(parsearFecha(campos[7]));               // INSCRIPCION
            if (campos.length > 8) postulante.setUbigeoProcedencia(limpiarCampo(campos[8]));         // UBIGEO PROCEDENCIA
            if (campos.length > 9) postulante.setCodColegio(limpiarCampo(campos[9]));                // CODCOLEGIO
            if (campos.length > 10) postulante.setFechaEgresoColegio(parsearFecha(campos[10]));       // FECHA EGRESOCOLEGIO
            if (campos.length > 11) postulante.setTipoColegio(parsearEntero(campos[11], 2));          // TIPOCOLEGIO
            if (campos.length > 12) postulante.setUbigeoColegio(limpiarCampo(campos[12]));            // UBIGEO COLEGIO
            if (campos.length > 13) postulante.setEstadoCivil(limpiarCampo(campos[13]));              // ESTADO CIVIL
            if (campos.length > 14) postulante.setEncuesta(limpiarCampo(campos[14]));                 // ENCUESTA
            if (campos.length > 15) postulante.setIngreso(parsearEntero(campos[15], 0));              // INGRESO
            if (campos.length > 16) postulante.setIngresoA(limpiarCampo(campos[16]));                 // INGRESO A
            if (campos.length > 17) postulante.setSexo(normalizarSexo(limpiarCampo(campos[17])));     // SEXO
            if (campos.length > 18) postulante.setNombreColegio(limpiarCampo(campos[18]));            // NOMBRE COLEGIO
            if (campos.length > 19) postulante.setIdiomaMat(limpiarCampo(campos[19]));                // IDIOMA MAT
            if (campos.length > 20) postulante.setTelCelular(limpiarCampo(campos[20]));               // TELCELULAR
            if (campos.length > 21) postulante.setDireccion(limpiarCampo(campos[21]));                // DIRECCION
            if (campos.length > 22) postulante.setUbigeo(limpiarCampo(campos[22]));                   // UBIGEO
            if (campos.length > 23) postulante.setFecNac(parsearFecha(campos[23]));                   // FECNAC
            if (campos.length > 24) postulante.setNotaAC(parsearDouble(campos[24]));                  // NOTAAC
            if (campos.length > 25) postulante.setNotaCO(parsearDouble(campos[25]));                  // NOTACO
            if (campos.length > 26) postulante.setRespuesta(limpiarCampo(campos[26]));                // RESPUESTA
            
            // Estado académico (por defecto POSTULANTE)
            if (campos.length > 27 && !limpiarCampo(campos[27]).isEmpty()) {
                postulante.setEstadoAcademico(limpiarCampo(campos[27]));
            } else {
                postulante.setEstadoAcademico("POSTULANTE");
            }
            
            // Validaciones básicas
            if (postulante.getCodigo().isEmpty() || postulante.getApellidosNombres().isEmpty()) {
                System.err.println("⚠️ Línea " + numeroLinea + ": Código o nombre vacío");
                return null;
            }
            
            // Calcular puntaje final
            postulante.calcularPuntajeFinal();
            
            return postulante;
            
        } catch (Exception e) {
            System.err.println("❌ Error parseando línea " + numeroLinea + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Detectar separador automáticamente
     */
    private static String detectarSeparador(String linea) {
        // Contar ocurrencias de posibles separadores
        int comas = (int) linea.chars().filter(ch -> ch == ',').count();
        int tabs = (int) linea.chars().filter(ch -> ch == '\t').count();
        int puntoComas = (int) linea.chars().filter(ch -> ch == ';').count();
        
        // Retornar el más común
        if (tabs > comas && tabs > puntoComas) {
            return "\t";
        } else if (puntoComas > comas) {
            return ";";
        } else {
            return ",";
        }
    }
    
    /**
     * Normalizar valores de sexo
     */
    private static String normalizarSexo(String sexo) {
        if (sexo == null || sexo.trim().isEmpty()) return "";
        
        String sexoNorm = sexo.trim().toUpperCase();
        
        // Convertir variaciones comunes
        if (sexoNorm.startsWith("M") || sexoNorm.equals("MASCULINO") || sexoNorm.equals("HOMBRE")) {
            return "M";
        } else if (sexoNorm.startsWith("F") || sexoNorm.equals("FEMENINO") || sexoNorm.equals("MUJER")) {
            return "F";
        }
        
        return sexo; // Retornar original si no coincide
    }
    
    // Métodos auxiliares para parseo
    private static String limpiarCampo(String campo) {
        if (campo == null) return "";
        return campo.trim().replaceAll("\"", "");
    }
    
    private static int parsearEntero(String campo, int valorDefault) {
        try {
            String limpio = limpiarCampo(campo);
            return limpio.isEmpty() ? valorDefault : Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return valorDefault;
        }
    }
    
    private static double parsearDouble(String campo) {
        try {
            String limpio = limpiarCampo(campo);
            return limpio.isEmpty() ? 0.0 : Double.parseDouble(limpio);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private static Date parsearFecha(String campo) {
        try {
            String limpio = limpiarCampo(campo);
            if (limpio.isEmpty()) return new Date();
            
            // Intentar varios formatos de fecha
            String[] formatos = {"dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};
            
            for (String formato : formatos) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(formato);
                    return sdf.parse(limpio);
                } catch (ParseException ignored) {
                    // Continuar con siguiente formato
                }
            }
            
            return new Date(); // Fecha actual si no se puede parsear
        } catch (Exception e) {
            return new Date();
        }
    }
    
    /**
     * SIMULA la importación desde Excel con datos realistas
     * En un proyecto real usaríamos Apache POI
     */
    public static List<Postulante> importarPostulantesSimulado() {
        System.out.println("📊 Generando datos simulados de postulantes...");
        
        List<Postulante> postulantes = new ArrayList<>();
        
        // Datos de carreras más demandadas en UNAS
        String[] carrerasPopulares = {
            "INGENIERÍA DE SISTEMAS E INFORMÁTICA",
            "MEDICINA HUMANA", 
            "INGENIERÍA CIVIL",
            "ADMINISTRACIÓN",
            "CONTABILIDAD",
            "DERECHO Y CIENCIAS POLÍTICAS",
            "PSICOLOGÍA",
            "ENFERMERÍA",
            "INGENIERÍA INDUSTRIAL",
            "ECONOMÍA"
        };
        
        String[] nombres = {
            "GARCIA LOPEZ, JUAN CARLOS", "RODRIGUEZ PEREZ, MARIA ELENA",
            "MARTINEZ SILVA, CARLOS EDUARDO", "TORRES MENDOZA, ANA LUCIA",
            "GOMEZ CASTRO, LUIS FERNANDO", "VARGAS RIOS, SOFIA ALEXANDRA",
            "MORALES VEGA, DIEGO SEBASTIAN", "HERRERA FLORES, VALERIA NICOLE",
            "JIMENEZ RAMOS, MIGUEL ANGEL", "SANTOS CRUZ, ISABELLA MARIA",
            "MENDOZA GUTIERREZ, ADRIAN JOSE", "CHAVEZ MORENO, CAMILA FERNANDA",
            "RUIZ DELGADO, DANIEL ALEXANDER", "ORTEGA LUNA, NATALIA ESPERANZA",
            "SILVA ROJAS, KEVIN ANDRES", "PAREDES WONG, ANDREA VALENTINA",
            "FERNANDEZ DIAZ, SEBASTIAN DAVID", "RAMIREZ SOTO, LUCIANA ISABEL",
            "CASTILLO HERRERA, BRANDON NICOLAS", "AGUIRRE VALDEZ, ANTONELLA SOFIA",
            "QUISPE HUAMAN, JOSE ANTONIO", "SALVADOR ESPINOZA, CARLA MILAGROS",
            "PONTE VILLANUEVA, FRANCO ALBERTO", "ROJAS CAMPOS, ALEJANDRA BELEN",
            "LEON TORRES, RICARDO MANUEL"
        };
        
        String[] modalidades = {"ORDINARIO", "ORDINARIO", "ORDINARIO", "EXONERADO", "BECA 18"};
        String[] sexos = {"M", "F"};
        String[] estadosCivil = {"SOLTERO", "SOLTERO", "SOLTERO", "CASADO"};
        
        Random random = new Random(System.currentTimeMillis());
        
        // Generar 50 postulantes de prueba
        for (int i = 0; i < 50; i++) {
            Postulante p = new Postulante();
            
            // Código secuencial
            p.setCodigo(String.format("2025%03d", i + 1));
            
            // Nombre 
            p.setApellidosNombres(nombres[i % nombres.length]);
            
            // DNI secuencial válido
            p.setDni(String.format("%08d", 10000000 + i));
            
            // Opciones de carrera
            String opcion1 = carrerasPopulares[random.nextInt(carrerasPopulares.length)];
            String opcion2;
            do {
                opcion2 = carrerasPopulares[random.nextInt(carrerasPopulares.length)];
            } while (opcion1.equals(opcion2));
            
            p.setOpcion1(opcion1);
            p.setOpcion2(opcion2);
            
            // Modalidad y otros datos
            p.setModalidad(modalidades[random.nextInt(modalidades.length)]);
            p.setSexo(sexos[random.nextInt(sexos.length)]);
            p.setEstadoCivil(estadosCivil[random.nextInt(estadosCivil.length)]);
            
            // Sede y colegio
            p.setCodSede(random.nextInt(3) + 1);
            p.setTipoColegio(random.nextInt(2) + 1); // 1=Estatal, 2=Particular
            p.setNombreColegio(generarNombreColegio(p.getTipoColegio()));
            
            // Fechas
            p.setInscripcion(generarFechaInscripcion(random));
            p.setFecNac(generarFechaNacimiento(random));
            p.setFechaEgresoColegio(generarFechaEgreso(random));
            
            // Notas realistas según distribución normal
            p.setNotaAC(generarNotaRealista(random, 12.0, 3.0));
            p.setNotaCO(generarNotaRealista(random, 11.5, 2.5));
            
            // Determinar estado académico
            determinarEstadoAcademico(p);
            
            // Contacto
            p.setTelCelular(String.format("9%08d", random.nextInt(100000000)));
            p.setDireccion("JR. " + (random.nextInt(20) + 1) + " DE ENERO " + (random.nextInt(500) + 100));
            
            postulantes.add(p);
        }
        
        // Agregar algunos casos especiales
        agregarCasosEspeciales(postulantes);
        
        System.out.println("✅ " + postulantes.size() + " postulantes simulados generados");
        return postulantes;
    }
    
    /**
     * Genera una nota realista usando distribución normal
     */
    private static double generarNotaRealista(Random random, double media, double desviacion) {
        double nota = random.nextGaussian() * desviacion + media;
        // Limitar entre 0 y 20
        nota = Math.max(0, Math.min(20, nota));
        // Redondear a 2 decimales
        return Math.round(nota * 100.0) / 100.0;
    }
    
    /**
     * Determina si es POSTULANTE o ALUMNO_LIBRE
     */
    private static void determinarEstadoAcademico(Postulante postulante) {
        Date fechaEgreso = postulante.getFechaEgresoColegio();
        Date fechaActual = new Date();
        
        if (fechaEgreso == null) {
            postulante.setEstadoAcademico("ALUMNO_LIBRE");
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaActual);
            cal.add(Calendar.YEAR, -1); // Hace 1 año
            
            if (fechaEgreso.before(cal.getTime())) {
                postulante.setEstadoAcademico("POSTULANTE");
            } else {
                postulante.setEstadoAcademico("ALUMNO_LIBRE");
            }
        }
    }
    
    /**
     * Genera fechas de inscripción realistas
     */
    private static Date generarFechaInscripcion(Random random) {
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.JANUARY, 15); // Base: 15 de enero 2025
        cal.add(Calendar.DAY_OF_YEAR, random.nextInt(30)); // +0 a 30 días
        return cal.getTime();
    }
    
    /**
     * Genera fechas de nacimiento realistas (17-25 años)
     */
    private static Date generarFechaNacimiento(Random random) {
        Calendar cal = Calendar.getInstance();
        int edad = 17 + random.nextInt(9); // 17-25 años
        cal.add(Calendar.YEAR, -edad);
        cal.add(Calendar.DAY_OF_YEAR, random.nextInt(365));
        return cal.getTime();
    }
    
    /**
     * Genera fechas de egreso de colegio
     */
    private static Date generarFechaEgreso(Random random) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 20);
        
        // Egresos entre 2020-2024
        int año = 2020 + random.nextInt(5);
        cal.set(Calendar.YEAR, año);
        
        return cal.getTime();
    }
    
    /**
     * Genera nombres de colegios realistas
     */
    private static String generarNombreColegio(int tipo) {
        if (tipo == 1) { // Estatal
            String[] colegiosEstatales = {
                "I.E. LEONCIO PRADO",
                "I.E. HERMILIO VALDIZAN", 
                "I.E. NUESTRA SEÑORA DE LAS MERCEDES",
                "I.E. JUANA MORENO",
                "I.E. MILAGRO DE FATIMA"
            };
            return colegiosEstatales[new Random().nextInt(colegiosEstatales.length)];
        } else { // Particular
            String[] colegiosParticulares = {
                "COLEGIO CLARETIANO",
                "COLEGIO SANTA ELIZABETH",
                "COLEGIO SAN VICENTE DE PAUL",
                "COLEGIO PERUANO BRITANICO",
                "COLEGIO ROBERT M. SMITH"
            };
            return colegiosParticulares[new Random().nextInt(colegiosParticulares.length)];
        }
    }
    
    /**
     * Agrega casos especiales para testing
     */
    private static void agregarCasosEspeciales(List<Postulante> postulantes) {
        // Caso 1: Postulante con nota muy alta
        Postulante excelente = new Postulante();
        excelente.setCodigo("EXCEL001");
        excelente.setApellidosNombres("GENIO MATEMATICO, ALBERT EINSTEIN");
        excelente.setDni("99999999");
        excelente.setOpcion1("INGENIERÍA DE SISTEMAS E INFORMÁTICA");
        excelente.setOpcion2("MEDICINA HUMANA");
        excelente.setModalidad("ORDINARIO");
        excelente.setSexo("M");
        excelente.setNotaAC(19.5);
        excelente.setNotaCO(19.8);
        excelente.setEstadoAcademico("POSTULANTE");
        excelente.setInscripcion(new Date());
        postulantes.add(excelente);
        
        // Caso 2: Alumno libre con buena nota
        Postulante alumnoLibre = new Postulante();
        alumnoLibre.setCodigo("AL999");
        alumnoLibre.setApellidosNombres("ESTUDIANTE LIBRE, MARIA CURIE");
        alumnoLibre.setDni("88888888");
        alumnoLibre.setOpcion1("MEDICINA HUMANA");
        alumnoLibre.setOpcion2("ENFERMERÍA");
        alumnoLibre.setModalidad("ORDINARIO");
        alumnoLibre.setSexo("F");
        alumnoLibre.setNotaAC(16.5);
        alumnoLibre.setNotaCO(17.0);
        alumnoLibre.setEstadoAcademico("ALUMNO_LIBRE");
        alumnoLibre.setInscripcion(new Date());
        postulantes.add(alumnoLibre);
        
        // Caso 3: Postulante con nota límite
        Postulante limite = new Postulante();
        limite.setCodigo("LIM001");
        limite.setApellidosNombres("JUSTO APROBADO, PEDRO PASCAL");
        limite.setDni("77777777");
        limite.setOpcion1("ADMINISTRACIÓN");
        limite.setOpcion2("CONTABILIDAD");
        limite.setModalidad("ORDINARIO");
        limite.setSexo("M");
        limite.setNotaAC(5.5);
        limite.setNotaCO(5.5);
        limite.setEstadoAcademico("POSTULANTE");
        limite.setInscripcion(new Date());
        postulantes.add(limite);
    }
    
    /**
     * Generar reporte estadístico
     */
    public static void generarReporteEstadistico(List<Postulante> postulantes) {
        System.out.println("\n📊 REPORTE ESTADÍSTICO DE POSTULANTES:");
        System.out.println("==========================================");
        
        // Estadísticas básicas
        int totalPostulantes = postulantes.size();
        int postulantesDirectos = (int) postulantes.stream().filter(Postulante::esPostulante).count();
        int alumnosLibres = (int) postulantes.stream().filter(Postulante::esAlumnoLibre).count();
        
        System.out.println("👥 Total postulantes: " + totalPostulantes);
        System.out.println("📚 Postulantes directos: " + postulantesDirectos);
        System.out.println("🎓 Alumnos libres: " + alumnosLibres);
        
        // Estadísticas de notas
        double promedioAC = postulantes.stream().mapToDouble(Postulante::getNotaAC).average().orElse(0);
        double promedioCO = postulantes.stream().mapToDouble(Postulante::getNotaCO).average().orElse(0);
        double promedioFinal = postulantes.stream().mapToDouble(Postulante::getNotaFinal).average().orElse(0);
        
        System.out.printf("📊 Promedio Aptitud Académica: %.2f%n", promedioAC);
        System.out.printf("📊 Promedio Conocimientos: %.2f%n", promedioCO);
        System.out.printf("📊 Promedio Final: %.2f%n", promedioFinal);
        
        // Postulantes con puntaje aprobatorio
        long aprobatorios = postulantes.stream().filter(p -> p.getNotaFinal() >= 11.0).count();
        System.out.println("✅ Postulantes con puntaje aprobatorio (≥11): " + aprobatorios);
    }
    
    /**
     * Validar integridad de datos
     */
    public static List<String> validarIntegridad(List<Postulante> postulantes) {
        List<String> errores = new ArrayList<>();
        Set<String> codigosUsados = new HashSet<>();
        Set<String> dnisUsados = new HashSet<>();
        
        for (Postulante p : postulantes) {
            // Verificar duplicados
            if (codigosUsados.contains(p.getCodigo())) {
                errores.add("Código duplicado: " + p.getCodigo());
            }
            codigosUsados.add(p.getCodigo());
            
            if (dnisUsados.contains(p.getDni())) {
                errores.add("DNI duplicado: " + p.getDni());
            }
            dnisUsados.add(p.getDni());
            
            // Verificar datos requeridos
            if (!p.validarDatos()) {
                errores.add("Datos incompletos: " + p.getCodigo());
            }
        }
        
        if (errores.isEmpty()) {
            System.out.println("✅ Integridad de datos verificada - Sin errores");
        } else {
            System.out.println("⚠️ Encontrados " + errores.size() + " errores de integridad");
        }
        
        return errores;
    }
}