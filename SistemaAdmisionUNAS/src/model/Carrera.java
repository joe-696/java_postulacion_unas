package model;

import java.util.Objects;

/**
 * Clase modelo para representar una Carrera universitaria
 * @author joe-696
 */
public class Carrera {
    private String codigo;
    private String nombre;
    private String facultad;
    private int vacantesDisponibles;
    private int vacantesOcupadas;
    private double puntajeMinimo;
    private double curvaAplicada;
    private String tipoExamen; // "INGENIERIA", "FCA", "MEDICINA", "GENERAL"
    private boolean activa;
    
    /**
     * Constructor vacío
     */
    public Carrera() {
        this.vacantesOcupadas = 0;
        this.puntajeMinimo = 11.0;
        this.curvaAplicada = 0.0;
        this.tipoExamen = "GENERAL";
        this.activa = true;
    }
    
    /**
     * Constructor con parámetros principales
     */
    public Carrera(String codigo, String nombre, String facultad, int vacantesDisponibles) {
        this();
        this.codigo = codigo;
        this.nombre = nombre;
        this.facultad = facultad;
        this.vacantesDisponibles = vacantesDisponibles;
        this.tipoExamen = determinarTipoExamen(nombre);
    }
    
    /**
     * Determina el tipo de examen según el nombre de la carrera
     */
    private String determinarTipoExamen(String nombreCarrera) {
        if (nombreCarrera == null) return "GENERAL";
        
        String nombre = nombreCarrera.toUpperCase();
        if (nombre.contains("INGENIERIA") || nombre.contains("SISTEMAS")) {
            return "INGENIERIA";
        } else if (nombre.contains("ADMINISTRACION") || 
                   nombre.contains("CONTABILIDAD") || 
                   nombre.contains("ECONOMIA")) {
            return "FCA";
        } else if (nombre.contains("MEDICINA") || 
                   nombre.contains("ENFERMERIA") || 
                   nombre.contains("ODONTOLOGIA")) {
            return "MEDICINA";
        }
        return "GENERAL";
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
        this.tipoExamen = determinarTipoExamen(nombre);
    }
    
    public String getFacultad() {
        return facultad;
    }
    
    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }
    
    public int getVacantesDisponibles() {
        return vacantesDisponibles;
    }
    
    public void setVacantesDisponibles(int vacantesDisponibles) {
        this.vacantesDisponibles = vacantesDisponibles;
    }
    
    public int getVacantesOcupadas() {
        return vacantesOcupadas;
    }
    
    public void setVacantesOcupadas(int vacantesOcupadas) {
        this.vacantesOcupadas = vacantesOcupadas;
    }
    
    public double getPuntajeMinimo() {
        return puntajeMinimo;
    }
    
    public void setPuntajeMinimo(double puntajeMinimo) {
        this.puntajeMinimo = puntajeMinimo;
    }
    
    public double getCurvaAplicada() {
        return curvaAplicada;
    }
    
    public void setCurvaAplicada(double curvaAplicada) {
        this.curvaAplicada = curvaAplicada;
    }
    
    public String getTipoExamen() {
        return tipoExamen;
    }
    
    public void setTipoExamen(String tipoExamen) {
        this.tipoExamen = tipoExamen;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    public void setActiva(boolean activa) {
        this.activa = activa;
    }
    
    // ===== MÉTODOS DE UTILIDAD =====
    
    /**
     * Obtiene las vacantes restantes
     */
    public int getVacantesRestantes() {
        return vacantesDisponibles - vacantesOcupadas;
    }
    
    /**
     * Verifica si tiene vacantes disponibles
     */
    public boolean tieneVacantesDisponibles() {
        return getVacantesRestantes() > 0;
    }
    
    /**
     * Ocupa una vacante
     */
    public boolean ocuparVacante() {
        if (tieneVacantesDisponibles()) {
            vacantesOcupadas++;
            return true;
        }
        return false;
    }
    
    /**
     * Libera una vacante
     */
    public boolean liberarVacante() {
        if (vacantesOcupadas > 0) {
            vacantesOcupadas--;
            return true;
        }
        return false;
    }
    
    /**
     * Calcula el porcentaje de ocupación
     */
    public double getPorcentajeOcupacion() {
        if (vacantesDisponibles == 0) return 0.0;
        return (double) vacantesOcupadas / vacantesDisponibles * 100.0;
    }
    
    /**
     * Verifica si la carrera está completa
     */
    public boolean estaCompleta() {
        return vacantesOcupadas >= vacantesDisponibles;
    }
    
    // ===== MÉTODOS ESTÁNDAR =====
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Carrera carrera = (Carrera) obj;
        return Objects.equals(codigo, carrera.codigo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
    
    @Override
    public String toString() {
        return String.format("Carrera{codigo='%s', nombre='%s', facultad='%s', vacantes=%d/%d, tipo='%s'}", 
                codigo, nombre, facultad, vacantesOcupadas, vacantesDisponibles, tipoExamen);
    }
}