package util;

import model.Postulante;
import model.Carrera;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ALGORITMO PRINCIPAL DE ADMISIÓN UNAS
 * Implementa todas las reglas del proceso de admisión
 * @author joe-696
 */
public class AlgoritmoAdmision {
    
    private List<Postulante> postulantes;
    private Map<String, Carrera> carreras;
    
    /**
     * Constructor
     */
    public AlgoritmoAdmision() {
        this.carreras = new HashMap<>();
        inicializarCarreras();
    }
    
    /**
     * ALGORITMO PRINCIPAL - PROCESA TODO EL SISTEMA DE ADMISIÓN
     */
    public ResultadoAdmision procesarAdmision(List<Postulante> postulantes) {        
        System.out.println("\n🎯 INICIANDO ALGORITMO DE ADMISIÓN UNAS");
        System.out.println("=====================================");
        
        this.postulantes = new ArrayList<>(postulantes);
        
        // PASO 1: Validar y preparar datos
        prepararDatos();
        
        // PASO 2: Filtrar postulantes con puntaje aprobatorio
        List<Postulante> aprobatorios = filtrarAprobatorios();
        
        // PASO 3: Separar postulantes directos de alumnos libres
        Map<String, List<Postulante>> separados = separarPorTipo(aprobatorios);
        List<Postulante> postulantesDirectos = separados.get("POSTULANTES");
        List<Postulante> alumnosLibres = separados.get("ALUMNOS_LIBRES");
        
        // PASO 4: Procesar primera opción (postulantes directos)
        Map<String, List<Postulante>> ingresantesPrimeraOpcion = procesarPrimeraOpcion(postulantesDirectos);
        
        // PASO 5: Procesar segunda opción (no ingresados en primera)
        Map<String, List<Postulante>> ingresantesSegundaOpcion = procesarSegundaOpcion(postulantesDirectos, ingresantesPrimeraOpcion);
        
        // PASO 6: Aplicar curvas para llenar vacantes
        Map<String, List<Postulante>> ingresantesConCurva = aplicarCurvas(postulantesDirectos, ingresantesSegundaOpcion);
        
        // PASO 7: Procesar alumnos libres (solo vacantes restantes)
        Map<String, List<Postulante>> resultadoFinal = procesarAlumnosLibres(alumnosLibres, ingresantesConCurva);
        
        // PASO 8: Generar resultado final
        ResultadoAdmision resultado = new ResultadoAdmision(resultadoFinal, carreras);
        resultado.generarEstadisticas();
        
        System.out.println("✅ ALGORITMO DE ADMISIÓN COMPLETADO");
        return resultado;
    }
    
    /**
     * PASO 1: Preparar y limpiar datos
     */
    private void prepararDatos() {
        System.out.println("\n📋 PASO 1: Preparando datos...");
        
        // Calcular puntaje final para todos
        for (Postulante p : postulantes) {
            double puntajeFinal = p.getNotaAC() + p.getNotaCO();
            p.setPuntajeFinal(puntajeFinal);
            
            // Resetear estado de ingreso
            p.setIngreso(0);
            p.setIngresoA(null);
        }
        
        System.out.println("   ✅ " + postulantes.size() + " postulantes preparados");
    }
    
    /**
     * PASO 2: Filtrar postulantes con puntaje aprobatorio (≥ 11.0)
     */
    private List<Postulante> filtrarAprobatorios() {
        System.out.println("\n📊 PASO 2: Filtrando postulantes aprobatorios...");
        
        List<Postulante> aprobatorios = postulantes.stream()
            .filter(p -> p.getPuntajeFinal() >= 11.0)
            .collect(Collectors.toList());
        
        int noAprobatorios = postulantes.size() - aprobatorios.size();
        
        System.out.println("   ✅ Postulantes aprobatorios: " + aprobatorios.size());
        System.out.println("   ❌ Postulantes no aprobatorios: " + noAprobatorios);
        
        return aprobatorios;
    }
    
    /**
     * PASO 3: Separar postulantes directos de alumnos libres
     */
    private Map<String, List<Postulante>> separarPorTipo(List<Postulante> aprobatorios) {
        System.out.println("\n👥 PASO 3: Separando por tipo académico...");
        
        List<Postulante> postulantesDirectos = aprobatorios.stream()
            .filter(p -> "POSTULANTE".equals(p.getEstadoAcademico()))
            .collect(Collectors.toList());
        
        List<Postulante> alumnosLibres = aprobatorios.stream()
            .filter(p -> "ALUMNO_LIBRE".equals(p.getEstadoAcademico()))
            .collect(Collectors.toList());
        
        System.out.println("   📚 Postulantes directos: " + postulantesDirectos.size());
        System.out.println("   🎓 Alumnos libres: " + alumnosLibres.size());
        
        Map<String, List<Postulante>> resultado = new HashMap<>();
        resultado.put("POSTULANTES", postulantesDirectos);
        resultado.put("ALUMNOS_LIBRES", alumnosLibres);
        
        return resultado;
    }
    
    /**
     * PASO 4: Procesar primera opción de carrera
     */
    private Map<String, List<Postulante>> procesarPrimeraOpcion(List<Postulante> postulantesDirectos) {
        System.out.println("\n🎯 PASO 4: Procesando primera opción...");
        
        Map<String, List<Postulante>> ingresantesPorCarrera = new HashMap<>();
        
        // Agrupar postulantes por primera opción
        Map<String, List<Postulante>> postulantePorCarrera = postulantesDirectos.stream()
            .collect(Collectors.groupingBy(Postulante::getOpcion1));
        
        // Procesar cada carrera
        for (Map.Entry<String, List<Postulante>> entry : postulantePorCarrera.entrySet()) {
            String nombreCarrera = entry.getKey();
            List<Postulante> candidatos = entry.getValue();
            Carrera carrera = carreras.get(nombreCarrera);
            
            if (carrera == null) {
                System.out.println("   ⚠️ Carrera no encontrada: " + nombreCarrera);
                continue;
            }
            
            // Ordenar candidatos por puntaje (algoritmo de ordenamiento)
            candidatos.sort(this::compararPostulantes);
            
            // Seleccionar ingresantes hasta llenar vacantes
            List<Postulante> ingresantes = new ArrayList<>();
            int vacantesDisponibles = carrera.getVacantesDisponibles();
            
            for (int i = 0; i < Math.min(candidatos.size(), vacantesDisponibles); i++) {
                Postulante postulante = candidatos.get(i);
                postulante.setIngreso(1);
                postulante.setIngresoA(nombreCarrera);
                ingresantes.add(postulante);
                carrera.ocuparVacante();
            }
            
            ingresantesPorCarrera.put(nombreCarrera, ingresantes);
            System.out.println("   ✅ " + nombreCarrera + ": " + ingresantes.size() + "/" + candidatos.size() + " ingresantes");
        }
        
        return ingresantesPorCarrera;
    }
    
    /**
     * PASO 5: Procesar segunda opción para no ingresados
     */
    private Map<String, List<Postulante>> procesarSegundaOpcion(List<Postulante> postulantesDirectos, 
                                                               Map<String, List<Postulante>> ingresantesPrevios) {
        System.out.println("\n🎯 PASO 5: Procesando segunda opción...");
        
        Map<String, List<Postulante>> resultado = new HashMap<>(ingresantesPrevios);
        
        // Filtrar postulantes que NO ingresaron en primera opción
        List<Postulante> noIngresados = postulantesDirectos.stream()
            .filter(p -> p.getIngreso() == 0)
            .collect(Collectors.toList());
        
        System.out.println("   📊 Postulantes para segunda opción: " + noIngresados.size());
        
        // Agrupar por segunda opción
        Map<String, List<Postulante>> postulantePorCarrera = noIngresados.stream()
            .collect(Collectors.groupingBy(Postulante::getOpcion2));
        
        // Procesar cada carrera
        for (Map.Entry<String, List<Postulante>> entry : postulantePorCarrera.entrySet()) {
            String nombreCarrera = entry.getKey();
            List<Postulante> candidatos = entry.getValue();
            Carrera carrera = carreras.get(nombreCarrera);
            
            if (carrera == null || !carrera.tieneVacantesDisponibles()) {
                continue;
            }
            
            // Ordenar candidatos
            candidatos.sort(this::compararPostulantes);
            
            // Obtener lista actual de ingresantes
            List<Postulante> ingresantesActuales = resultado.getOrDefault(nombreCarrera, new ArrayList<>());
            int vacantesRestantes = carrera.getVacantesRestantes();
            
            int nuevosIngresantes = 0;
            for (int i = 0; i < Math.min(candidatos.size(), vacantesRestantes); i++) {
                Postulante postulante = candidatos.get(i);
                postulante.setIngreso(1);
                postulante.setIngresoA(nombreCarrera);
                ingresantesActuales.add(postulante);
                carrera.ocuparVacante();
                nuevosIngresantes++;
            }
            
            if (nuevosIngresantes > 0) {
                resultado.put(nombreCarrera, ingresantesActuales);
                System.out.println("   ✅ " + nombreCarrera + " (2da): +" + nuevosIngresantes + " ingresantes");
            }
        }
        
        return resultado;
    }
    
    /**
     * PASO 6: Aplicar curvas para llenar vacantes restantes
     */
    private Map<String, List<Postulante>> aplicarCurvas(List<Postulante> postulantesDirectos,
                                                        Map<String, List<Postulante>> ingresantesPrevios) {
        System.out.println("\n📈 PASO 6: Aplicando curvas...");
        
        Map<String, List<Postulante>> resultado = new HashMap<>(ingresantesPrevios);
        
        // Solo aplicar curvas si hay vacantes sin llenar
        for (Carrera carrera : carreras.values()) {
            if (!carrera.tieneVacantesDisponibles()) continue;
            
            String nombreCarrera = carrera.getNombre();
            double curva = carrera.getCurvaAplicada();
            
            if (curva <= 0) continue; // Sin curva definida
            
            System.out.println("   📈 Aplicando curva +" + curva + " puntos a " + nombreCarrera);
            
            // Buscar candidatos que con curva podrían ingresar
            List<Postulante> candidatosConCurva = postulantesDirectos.stream()
                .filter(p -> p.getIngreso() == 0) // No ingresó aún
                .filter(p -> p.getOpcion1().equals(nombreCarrera) || p.getOpcion2().equals(nombreCarrera))
                .filter(p -> (p.getPuntajeFinal() + curva) >= 11.0) // Con curva es aprobatorio
                .sorted(this::compararPostulantes)
                .collect(Collectors.toList());
            
            List<Postulante> ingresantesActuales = resultado.getOrDefault(nombreCarrera, new ArrayList<>());
            int vacantesRestantes = carrera.getVacantesRestantes();
            
            int ingresantesCurva = 0;
            for (int i = 0; i < Math.min(candidatosConCurva.size(), vacantesRestantes); i++) {
                Postulante postulante = candidatosConCurva.get(i);
                postulante.setPuntajeFinal(postulante.getPuntajeFinal() + curva);
                postulante.setIngreso(1);
                postulante.setIngresoA(nombreCarrera);
                ingresantesActuales.add(postulante);
                carrera.ocuparVacante();
                ingresantesCurva++;
            }
            
            if (ingresantesCurva > 0) {
                resultado.put(nombreCarrera, ingresantesActuales);
                System.out.println("   ✅ " + nombreCarrera + " (curva): +" + ingresantesCurva + " ingresantes");
            }
        }
        
        return resultado;
    }
    
    /**
     * PASO 7: Procesar alumnos libres (solo vacantes restantes)
     */
    private Map<String, List<Postulante>> procesarAlumnosLibres(List<Postulante> alumnosLibres,
                                                               Map<String, List<Postulante>> ingresantesPrevios) {
        System.out.println("\n🎓 PASO 7: Procesando alumnos libres...");
        
        Map<String, List<Postulante>> resultado = new HashMap<>(ingresantesPrevios);
        
        // Verificar si hay vacantes disponibles
        boolean hayVacantes = carreras.values().stream().anyMatch(Carrera::tieneVacantesDisponibles);
        
        if (!hayVacantes) {
            System.out.println("   ⚠️ No hay vacantes disponibles para alumnos libres");
            return resultado;
        }
        
        // Procesar alumnos libres para cada carrera con vacantes
        for (Carrera carrera : carreras.values()) {
            if (!carrera.tieneVacantesDisponibles()) continue;
            
            String nombreCarrera = carrera.getNombre();
            
            // Buscar alumnos libres que eligieron esta carrera
            List<Postulante> candidatos = alumnosLibres.stream()
                .filter(p -> p.getIngreso() == 0)
                .filter(p -> p.getOpcion1().equals(nombreCarrera) || p.getOpcion2().equals(nombreCarrera))
                .sorted(this::compararPostulantes)
                .collect(Collectors.toList());
            
            if (candidatos.isEmpty()) continue;
            
            List<Postulante> ingresantesActuales = resultado.getOrDefault(nombreCarrera, new ArrayList<>());
            int vacantesRestantes = carrera.getVacantesRestantes();
            
            int ingresantesLibres = 0;
            for (int i = 0; i < Math.min(candidatos.size(), vacantesRestantes); i++) {
                Postulante postulante = candidatos.get(i);
                postulante.setIngreso(1);
                postulante.setIngresoA(nombreCarrera);
                ingresantesActuales.add(postulante);
                carrera.ocuparVacante();
                ingresantesLibres++;
            }
            
            if (ingresantesLibres > 0) {
                resultado.put(nombreCarrera, ingresantesActuales);
                System.out.println("   ✅ " + nombreCarrera + " (libres): +" + ingresantesLibres + " alumnos libres");
            }
        }
        
        return resultado;
    }
    
    /**
     * ALGORITMO DE COMPARACIÓN PARA ORDENAMIENTO
     * Criterios de desempate para ordenar postulantes
     */
    private int compararPostulantes(Postulante p1, Postulante p2) {
        // 1. Por puntaje final (descendente)
        int comparacionPuntaje = Double.compare(p2.getPuntajeFinal(), p1.getPuntajeFinal());
        if (comparacionPuntaje != 0) return comparacionPuntaje;
        
        // 2. En caso de empate, por nota de Aptitud Académica (descendente)
        int comparacionAC = Double.compare(p2.getNotaAC(), p1.getNotaAC());
        if (comparacionAC != 0) return comparacionAC;
        
        // 3. En caso de empate, por nota de Conocimientos (descendente)
        int comparacionCO = Double.compare(p2.getNotaCO(), p1.getNotaCO());
        if (comparacionCO != 0) return comparacionCO;
        
        // 4. En caso de empate, por edad (más joven gana)
        if (p1.getFecNac() != null && p2.getFecNac() != null) {
            int comparacionEdad = p2.getFecNac().compareTo(p1.getFecNac());
            if (comparacionEdad != 0) return comparacionEdad;
        }
        
        // 5. En caso de empate, por fecha de inscripción (más temprano gana)
        if (p1.getInscripcion() != null && p2.getInscripcion() != null) {
            return p1.getInscripcion().compareTo(p2.getInscripcion());
        }
        
        return 0;
    }
    
    /**
     * Inicializar carreras con sus vacantes y curvas
     */
    private void inicializarCarreras() {
        // Carreras de Ingeniería (curva +2.0)
        carreras.put("INGENIERÍA DE SISTEMAS E INFORMÁTICA", 
            new Carrera("ISI", "INGENIERÍA DE SISTEMAS E INFORMÁTICA", "INGENIERÍA", 25));
        carreras.get("INGENIERÍA DE SISTEMAS E INFORMÁTICA").setCurvaAplicada(2.0);
        
        carreras.put("INGENIERÍA CIVIL", 
            new Carrera("CIV", "INGENIERÍA CIVIL", "INGENIERÍA", 20));
        carreras.get("INGENIERÍA CIVIL").setCurvaAplicada(2.0);
        
        // Carreras de Medicina (curva +1.0)
        carreras.put("MEDICINA HUMANA", 
            new Carrera("MED", "MEDICINA HUMANA", "MEDICINA", 30));
        carreras.get("MEDICINA HUMANA").setCurvaAplicada(1.0);
        
        carreras.put("ENFERMERÍA", 
            new Carrera("ENF", "ENFERMERÍA", "MEDICINA", 25));
        carreras.get("ENFERMERÍA").setCurvaAplicada(1.0);
        
        // Carreras de Ciencias Empresariales (curva +1.5)
        carreras.put("ADMINISTRACIÓN", 
            new Carrera("ADM", "ADMINISTRACIÓN", "CIENCIAS EMPRESARIALES", 35));
        carreras.get("ADMINISTRACIÓN").setCurvaAplicada(1.5);
        
        carreras.put("CONTABILIDAD", 
            new Carrera("CON", "CONTABILIDAD", "CIENCIAS EMPRESARIALES", 30));
        carreras.get("CONTABILIDAD").setCurvaAplicada(1.5);
        
        // Otras carreras (curva +1.0)
        carreras.put("DERECHO Y CIENCIAS POLÍTICAS", 
            new Carrera("DER", "DERECHO Y CIENCIAS POLÍTICAS", "DERECHO", 30));
        carreras.get("DERECHO Y CIENCIAS POLÍTICAS").setCurvaAplicada(1.0);
        
        carreras.put("PSICOLOGÍA", 
            new Carrera("PSI", "PSICOLOGÍA", "PSICOLOGÍA", 25));
        carreras.get("PSICOLOGÍA").setCurvaAplicada(1.0);
    }
    
    /**
     * Clase para encapsular el resultado del algoritmo
     */
    public static class ResultadoAdmision {
        private Map<String, List<Postulante>> ingresantesPorCarrera;
        private Map<String, Carrera> carreras;
        
        public ResultadoAdmision(Map<String, List<Postulante>> ingresantesPorCarrera, Map<String, Carrera> carreras) {
            this.ingresantesPorCarrera = ingresantesPorCarrera;
            this.carreras = carreras;
        }
        
        public void generarEstadisticas() {
            System.out.println("\n📊 ESTADÍSTICAS FINALES DEL PROCESO:");
            System.out.println("=====================================");
            
            int totalIngresantes = ingresantesPorCarrera.values().stream()
                .mapToInt(List::size)
                .sum();
            
            int totalVacantesOcupadas = carreras.values().stream()
                .mapToInt(Carrera::getVacantesOcupadas)
                .sum();
            
            int totalVacantesDisponibles = carreras.values().stream()
                .mapToInt(Carrera::getVacantesDisponibles)
                .sum();
            
            System.out.println("🎉 Total ingresantes: " + totalIngresantes);
            System.out.println("📊 Vacantes ocupadas: " + totalVacantesOcupadas + "/" + totalVacantesDisponibles);
            System.out.printf("📈 Porcentaje de ocupación: %.1f%%%n", 
                (double) totalVacantesOcupadas / totalVacantesDisponibles * 100);
            
            System.out.println("\n🏆 RESULTADOS POR CARRERA:");
            for (Map.Entry<String, List<Postulante>> entry : ingresantesPorCarrera.entrySet()) {
                String carrera = entry.getKey();
                List<Postulante> ingresantes = entry.getValue();
                Carrera carreraObj = carreras.get(carrera);
                
                if (carreraObj != null) {
                    System.out.printf("   %s: %d/%d (%.1f%% ocupación)%n",
                        carrera,
                        ingresantes.size(),
                        carreraObj.getVacantesDisponibles(),
                        carreraObj.getPorcentajeOcupacion()
                    );
                }
            }
        }
        
        // Getters
        public Map<String, List<Postulante>> getIngresantesPorCarrera() { return ingresantesPorCarrera; }
        public Map<String, Carrera> getCarreras() { return carreras; }
    }
}