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
     * SIMULA la importación desde Excel con datos realistas
     * En un proyecto real usaríamos Apache POI
     */
    public static List<Postulante> importarPostulantesDesdeExcel(String rutaArchivo) {
        System.out.println("📊 Simulando importación desde: " + rutaArchivo);
        return importarPostulantesSimulado();
    }
    
    /**
     * Genera datos simulados realistas para pruebas
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