package util;

import model.Postulante;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.*;
import javax.swing.JOptionPane;

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
     * MEJORADO: Maneja campos en blanco de forma robusta
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
                    System.out.println("📋 Detectado archivo Excel, intentando importación directa...");
                    postulantes = importarExcelDirecto(rutaArchivo);
                    break;
                default:
                    throw new IllegalArgumentException("Formato no soportado: " + extension);
            }
            
            System.out.println("✅ Importación completada: " + postulantes.size() + " postulantes procesados");
            
            if (postulantes.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "⚠️ No se pudieron importar datos.\n\n" +
                    "Verifique que:\n" +
                    "• El archivo contenga datos válidos\n" +
                    "• Tenga al menos CÓDIGO y NOMBRES\n" +
                    "• Use el formato correcto de separadores",
                    "Sin Datos Importados",
                    JOptionPane.WARNING_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error importando archivo: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                "❌ Error importando archivo:\n" + e.getMessage() + "\n\n" +
                "💡 Sugerencias:\n" +
                "• Verifique que el archivo no esté abierto en Excel\n" +
                "• Asegúrese de que tenga datos válidos\n" +
                "• Pruebe guardando como CSV desde Excel",
                "Error de Importación",
                JOptionPane.ERROR_MESSAGE);
        }
        
        return postulantes;
    }
    
    /**
     * Importar desde archivo CSV/TXT con manejo robusto de campos vacíos
     */
    private static List<Postulante> importarDesdeCSV(String rutaArchivo) throws IOException {
        List<Postulante> postulantes = new ArrayList<>();
        int procesados = 0;
        int errores = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo, java.nio.charset.StandardCharsets.UTF_8))) {
            String linea;
            boolean esPrimeraLinea = true;
            int lineaNumero = 0;
            
            System.out.println("📖 Procesando archivo CSV: " + rutaArchivo);
            
            while ((linea = reader.readLine()) != null) {
                lineaNumero++;
                
                // Saltar encabezados
                if (esPrimeraLinea) {
                    esPrimeraLinea = false;
                    System.out.println("📋 Encabezados detectados: " + linea.substring(0, Math.min(linea.length(), 100)) + "...");
                    continue;
                }
                
                // Saltar líneas completamente vacías
                if (linea.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    Postulante postulante = parsearLineaExcel(linea, lineaNumero);
                    if (postulante != null) {
                        postulantes.add(postulante);
                        procesados++;
                        
                        if (procesados % 10 == 0) {
                            System.out.println("📊 Procesados: " + procesados + " postulantes");
                        }
                    } else {
                        errores++;
                    }
                } catch (Exception e) {
                    errores++;
                    System.err.println("❌ Error en línea " + lineaNumero + ": " + e.getMessage());
                }
            }
        }
        
        System.out.println("✅ Importación CSV completada:");
        System.out.println("   📊 Procesados exitosamente: " + procesados);
        System.out.println("   ⚠️ Líneas con errores: " + errores);
        
        return postulantes;
    }
    
    /**
     * Importar archivos Excel directamente (simulado)
     * En un proyecto real usaríamos Apache POI
     */
    private static List<Postulante> importarExcelDirecto(String rutaArchivo) throws IOException {
        System.out.println("📋 Intentando importación directa de Excel...");
        
        // Mostrar diálogo informativo pero intentar leer como texto plano
        int opcion = JOptionPane.showConfirmDialog(null,
            "🔄 IMPORTACIÓN DE EXCEL\n\n" +
            "El sistema intentará leer el archivo Excel.\n" +
            "Si tiene problemas, guarde como CSV.\n\n" +
            "¿Continuar con la importación?",
            "Importar Excel",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (opcion != JOptionPane.YES_OPTION) {
            return new ArrayList<>();
        }
        
        try {
            // Intentar leer como texto plano (a veces funciona con archivos Excel simples)
            return importarDesdeCSV(rutaArchivo);
        } catch (Exception e) {
            System.err.println("❌ No se pudo leer como Excel directo: " + e.getMessage());
            
            JOptionPane.showMessageDialog(null,
                "❌ No se pudo importar el archivo Excel directamente.\n\n" +
                "📋 SOLUCIÓN:\n" +
                "1. Abra el archivo en Excel\n" +
                "2. Vaya a 'Archivo' → 'Guardar como'\n" +
                "3. Seleccione formato 'CSV (separado por comas)'\n" +
                "4. Guarde con nuevo nombre\n" +
                "5. Importe el archivo CSV creado\n\n" +
                "Esto garantiza compatibilidad total.",
                "Error Excel",
                JOptionPane.ERROR_MESSAGE);
            
            return new ArrayList<>();
        }
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
     * MEJORADO: Manejo ultra-robusto de campos en blanco
     * Soporta separación por comas, tabulaciones o punto y coma
     */
    private static Postulante parsearLineaExcel(String linea, int numeroLinea) {
        try {
            // Detectar separador automáticamente
            String separador = detectarSeparador(linea);
            String[] campos = linea.split(separador, -1); // -1 mantiene campos vacíos al final
            
            // Información de debug
            if (numeroLinea <= 5) {
                System.out.println("🔍 Línea " + numeroLinea + " - Separador: '" + 
                    (separador.equals("\t") ? "TAB" : separador) + "', Campos: " + campos.length);
            }
            
            // Expandir array si es necesario (asegurar mínimo 30 campos)
            String[] camposExpandidos = new String[Math.max(30, campos.length)];
            System.arraycopy(campos, 0, camposExpandidos, 0, campos.length);
            for (int i = campos.length; i < camposExpandidos.length; i++) {
                camposExpandidos[i] = ""; // Rellenar con vacíos
            }
            campos = camposExpandidos;
            
            // Validar campos mínimos requeridos
            String codigo = limpiarCampo(campos[0]);
            String nombres = limpiarCampo(campos[1]);
            
            if (codigo.isEmpty() && nombres.isEmpty()) {
                System.out.println("⚠️ Línea " + numeroLinea + " - Ambos campos principales vacíos, omitiendo");
                return null;
            }
            
            // Si no hay código, generar uno temporal
            if (codigo.isEmpty()) {
                codigo = "AUTO" + String.format("%04d", numeroLinea);
                System.out.println("🔧 Línea " + numeroLinea + " - Generando código automático: " + codigo);
            }
            
            // Si no hay nombres, usar código como nombre
            if (nombres.isEmpty()) {
                nombres = "POSTULANTE " + codigo;
                System.out.println("🔧 Línea " + numeroLinea + " - Usando nombre por defecto");
            }
            
            // Crear postulante
            Postulante postulante = new Postulante();
            
            // Campos obligatorios (siempre con valores)
            postulante.setCodigo(codigo);
            postulante.setApellidosNombres(nombres);
            
            // Campos principales con valores por defecto inteligentes
            postulante.setOpcion1(limpiarCampoConDefault(campos[2], "SIN ESPECIFICAR"));
            postulante.setOpcion2(limpiarCampoConDefault(campos[3], ""));
            postulante.setModalidad(limpiarCampoConDefault(campos[4], "ORDINARIO"));
            postulante.setDni(limpiarCampoConDefault(campos[5], generarDniTemporal()));
            
            // Campos adicionales permitiendo valores vacíos
            postulante.setCodSede(parsearEnteroSeguro(campos[6], 1));
            postulante.setInscripcion(parsearFechaSegura(campos[7]));
            postulante.setUbigeoProcedencia(limpiarCampo(campos[8]));
            postulante.setCodColegio(limpiarCampo(campos[9]));
            postulante.setFechaEgresoColegio(parsearFechaSegura(campos[10]));
            postulante.setTipoColegio(parsearEnteroSeguro(campos[11], 1));
            postulante.setUbigeoColegio(limpiarCampo(campos[12]));
            postulante.setEstadoCivil(limpiarCampoConDefault(campos[13], "SOLTERO"));
            postulante.setEncuesta(limpiarCampo(campos[14]));
            postulante.setIngreso(parsearEnteroSeguro(campos[15], 0));
            postulante.setIngresoA(limpiarCampo(campos[16]));
            postulante.setSexo(normalizarSexo(limpiarCampo(campos[17])));
            postulante.setNombreColegio(limpiarCampoConDefault(campos[18], "SIN ESPECIFICAR"));
            postulante.setIdiomaMat(limpiarCampo(campos[19]));
            postulante.setTelCelular(limpiarCampo(campos[20]));
            postulante.setDireccion(limpiarCampo(campos[21]));
            postulante.setUbigeo(limpiarCampo(campos[22]));
            postulante.setFecNac(parsearFechaSegura(campos[23]));
            
            // Notas - campos críticos para el cálculo
            postulante.setNotaAC(parsearDoubleSeguro(campos[24]));
            postulante.setNotaCO(parsearDoubleSeguro(campos[25]));
            
            // Campos finales
            postulante.setRespuesta(limpiarCampo(campos[26]));
            postulante.setEstadoAcademico(limpiarCampoConDefault(campos[27], "POSTULANTE"));
            
            // Asegurar valores por defecto críticos
            if (postulante.getInscripcion() == null) {
                postulante.setInscripcion(new Date());
            }
            
            if (postulante.getFecNac() == null) {
                // Fecha de nacimiento por defecto (18 años atrás)
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -18);
                postulante.setFecNac(cal.getTime());
            }
            
            // Calcular puntaje final
            postulante.calcularPuntajeFinal();
            
            // Debug para primeras líneas
            if (numeroLinea <= 3) {
                System.out.println("✅ Línea " + numeroLinea + " - Postulante creado: " + 
                    postulante.getCodigo() + " | " + postulante.getApellidosNombres() + 
                    " | AC:" + postulante.getNotaAC() + " | CO:" + postulante.getNotaCO());
            }
            
            return postulante;
            
        } catch (Exception e) {
            System.err.println("❌ Error parseando línea " + numeroLinea + ": " + e.getMessage());
            System.err.println("   📝 Contenido: " + linea.substring(0, Math.min(linea.length(), 100)) + "...");
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
    
    // Métodos auxiliares para parseo MEJORADOS Y SEGUROS
    private static String limpiarCampo(String campo) {
        if (campo == null) return "";
        String limpio = campo.trim().replaceAll("\"", "").replaceAll("'", "");
        // Remover caracteres de control y espacios múltiples
        limpio = limpio.replaceAll("\\s+", " ").trim();
        return limpio;
    }
    
    /**
     * Limpiar campo con valor por defecto si está vacío
     */
    private static String limpiarCampoConDefault(String campo, String valorDefault) {
        String limpio = limpiarCampo(campo);
        return limpio.isEmpty() ? valorDefault : limpio;
    }
    
    /**
     * Parsear entero de forma segura con valor por defecto
     */
    private static int parsearEnteroSeguro(String campo, int valorDefault) {
        try {
            String limpio = limpiarCampo(campo);
            if (limpio.isEmpty()) return valorDefault;
            
            // Remover decimales si los hay
            if (limpio.contains(".")) {
                limpio = limpio.substring(0, limpio.indexOf("."));
            }
            
            return Integer.parseInt(limpio);
        } catch (NumberFormatException e) {
            return valorDefault;
        }
    }
    
    /**
     * Parsear double de forma segura
     */
    private static double parsearDoubleSeguro(String campo) {
        try {
            String limpio = limpiarCampo(campo);
            if (limpio.isEmpty()) return 0.0;
            
            // Manejar diferentes formatos decimales
            limpio = limpio.replace(",", ".");
            
            double valor = Double.parseDouble(limpio);
            
            // Validar rango razonable para notas (0-20)
            if (valor < 0) valor = 0.0;
            if (valor > 20) valor = 20.0;
            
            return Math.round(valor * 100.0) / 100.0; // 2 decimales
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Parsear fecha de forma ultra-segura
     */
    private static Date parsearFechaSegura(String campo) {
        try {
            String limpio = limpiarCampo(campo);
            if (limpio.isEmpty()) return new Date();
            
            // Intentar múltiples formatos de fecha
            String[] formatos = {
                "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd", 
                "MM/dd/yyyy", "dd.MM.yyyy", "yyyy/MM/dd",
                "dd/MM/yy", "dd-MM-yy", "yy-MM-dd"
            };
            
            for (String formato : formatos) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat(formato);
                    sdf.setLenient(false); // Validación estricta
                    Date fecha = sdf.parse(limpio);
                    
                    // Validar que la fecha sea razonable (entre 1900 y 2030)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(fecha);
                    int año = cal.get(Calendar.YEAR);
                    
                    if (año >= 1900 && año <= 2030) {
                        return fecha;
                    }
                } catch (ParseException ignored) {
                    // Continuar con siguiente formato
                }
            }
            
            System.out.println("⚠️ Fecha no reconocida: '" + limpio + "', usando fecha actual");
            return new Date(); // Fecha actual si no se puede parsear
            
        } catch (Exception e) {
            return new Date();
        }
    }
    
    /**
     * Generar DNI temporal único para casos donde no se proporciona
     */
    private static String generarDniTemporal() {
        return String.format("TEMP%04d", (int)(Math.random() * 10000));
    }
    
    // Métodos de compatibilidad (mantener para no romper código existente)
    private static int parsearEntero(String campo, int valorDefault) {
        return parsearEnteroSeguro(campo, valorDefault);
    }
    
    private static double parsearDouble(String campo) {
        return parsearDoubleSeguro(campo);
    }
    
    private static Date parsearFecha(String campo) {
        return parsearFechaSegura(campo);
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