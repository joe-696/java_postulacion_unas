package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

/**
 * Configuración del Examen de Admisión UNHEVAL
 * @author joe-696
 */
public class ExamenConfig {
    private String nombre;
    private int numeroAsignaturas;
    private int tiposExamen; // 1, 2 o 3 tipos
    private double puntajeMinimo;
    private Date fechaExamen;
    private boolean activa;
    private Map<String, Integer> preguntasPorAsignatura;
    private Map<String, String> respuestasCorrectas;
    private Map<String, Double> curvasPorTipo;
    
    /**
     * Constructor vacío con configuración por defecto
     */
    public ExamenConfig() {
        this.nombre = "EXAMEN ADMISIÓN UNHEVAL 2025";
        this.numeroAsignaturas = 10;
        this.tiposExamen = 3;
        this.puntajeMinimo = 11.0;
        this.activa = true;
        this.preguntasPorAsignatura = new HashMap<>();
        this.respuestasCorrectas = new HashMap<>();
        this.curvasPorTipo = new HashMap<>();
        
        // Inicializar configuración por defecto
        inicializarConfiguracionPorDefecto();
    }
    
    /**
     * Constructor con parámetros
     */
    public ExamenConfig(String nombre, Date fechaExamen, double puntajeMinimo) {
        this();
        this.nombre = nombre;
        this.fechaExamen = fechaExamen;
        this.puntajeMinimo = puntajeMinimo;
    }
    
    /**
     * Inicializa la configuración por defecto del examen
     */
    private void inicializarConfiguracionPorDefecto() {
        // Asignaturas y número de preguntas
        preguntasPorAsignatura.put("APTITUD_VERBAL", 10);
        preguntasPorAsignatura.put("APTITUD_MATEMATICA", 10);
        preguntasPorAsignatura.put("COMUNICACION", 8);
        preguntasPorAsignatura.put("MATEMATICA", 12);
        preguntasPorAsignatura.put("HISTORIA_PERU", 6);
        preguntasPorAsignatura.put("GEOGRAFIA", 6);
        preguntasPorAsignatura.put("FILOSOFIA", 4);
        preguntasPorAsignatura.put("FISICA", 8);
        preguntasPorAsignatura.put("QUIMICA", 8);
        preguntasPorAsignatura.put("BIOLOGIA", 8);
        
        // Curvas por tipo de examen
        curvasPorTipo.put("INGENIERIA", 2.0);
        curvasPorTipo.put("FCA", 1.5);
        curvasPorTipo.put("MEDICINA", 1.0);
        curvasPorTipo.put("GENERAL", 2.0);
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getNumeroAsignaturas() {
        return numeroAsignaturas;
    }
    
    public void setNumeroAsignaturas(int numeroAsignaturas) {
        this.numeroAsignaturas = numeroAsignaturas;
    }
    
    public int getTiposExamen() {
        return tiposExamen;
    }
    
    public void setTiposExamen(int tiposExamen) {
        this.tiposExamen = tiposExamen;
    }
    
    public double getPuntajeMinimo() {
        return puntajeMinimo;
    }
    
    public void setPuntajeMinimo(double puntajeMinimo) {
        this.puntajeMinimo = puntajeMinimo;
    }
    
    public Date getFechaExamen() {
        return fechaExamen;
    }
    
    public void setFechaExamen(Date fechaExamen) {
        this.fechaExamen = fechaExamen;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    public Map<String, Integer> getPreguntasPorAsignatura() {
        return preguntasPorAsignatura;
    }
    
    public void setPreguntasPorAsignatura(Map<String, Integer> preguntasPorAsignatura) {
        this.preguntasPorAsignatura = preguntasPorAsignatura;
    }
    
    public Map<String, String> getRespuestasCorrectas() {
        return respuestasCorrectas;
    }
    
    public void setRespuestasCorrectas(Map<String, String> respuestasCorrectas) {
        this.respuestasCorrectas = respuestasCorrectas;
    }
    
    public Map<String, Double> getCurvasPorTipo() {
        return curvasPorTipo;
    }
    
    public void setCurvasPorTipo(Map<String, Double> curvasPorTipo) {
        this.curvasPorTipo = curvasPorTipo;
    }
    
    // ===== MÉTODOS DE UTILIDAD =====
    
    /**
     * Obtiene el total de preguntas del examen
     */
    public int getTotalPreguntas() {
        return preguntasPorAsignatura.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Obtiene la curva para un tipo de examen específico
     */
    public double getCurvaPorTipo(String tipoExamen) {
        return curvasPorTipo.getOrDefault(tipoExamen, 0.0);
    }
    
    /**
     * Establece la curva para un tipo de examen
     */
    public void setCurvaPorTipo(String tipoExamen, double curva) {
        curvasPorTipo.put(tipoExamen, curva);
    }
    
    /**
     * Obtiene el número de preguntas para una asignatura
     */
    public int getPreguntasAsignatura(String asignatura) {
        return preguntasPorAsignatura.getOrDefault(asignatura, 0);
    }
    
    /**
     * Establece el número de preguntas para una asignatura
     */
    public void setPreguntasAsignatura(String asignatura, int numPreguntas) {
        preguntasPorAsignatura.put(asignatura, numPreguntas);
    }
    
    /**
     * Verifica si la configuración es válida
     */
    public boolean esConfiguracionValida() {
        return nombre != null && !nombre.trim().isEmpty() &&
               numeroAsignaturas > 0 &&
               puntajeMinimo > 0 &&
               !preguntasPorAsignatura.isEmpty() &&
               getTotalPreguntas() > 0;
    }
    
    /**
     * Calcula el puntaje con curva aplicada
     */
    public double calcularPuntajeConCurva(double puntajeBase, String tipoExamen) {
        double curva = getCurvaPorTipo(tipoExamen);
        return puntajeBase + curva;
    }
    
    /**
     * Verifica si un puntaje es aprobatorio
     */
    public boolean esAprobatorio(double puntaje) {
        return puntaje >= puntajeMinimo;
    }
    
    // ===== MÉTODOS ESTÁNDAR =====
    
    @Override
    public String toString() {
        return String.format("ExamenConfig{nombre='%s', asignaturas=%d, tipos=%d, puntajeMin=%.2f, totalPreguntas=%d}",
                nombre, numeroAsignaturas, tiposExamen, puntajeMinimo, getTotalPreguntas());
    }
}