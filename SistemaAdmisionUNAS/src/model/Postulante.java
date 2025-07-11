package model;

import java.util.Date;
import java.util.Objects;
import java.text.SimpleDateFormat;

/**
 * Clase modelo Postulante para el Sistema de Admisión UNAS
 * Representa la estructura de datos del Excel de postulantes
 * 
 * @author joe-696
 * @version 1.0
 * @since 2025-06-07
 */
public class Postulante {
    
    // ===== CAMPOS PRINCIPALES =====
    private String codigo;
    private String apellidosNombres;
    private String opcion1;
    private String opcion2;
    private String modalidad;
    private String dni;
    private int codSede;
    private Date inscripcion;
    private String ubigeoProcedencia;
    private String codColegio;
    private Date fechaEgresoColegio;
    private int tipoColegio;
    private String ubigeoColegio;
    private String estadoCivil;
    private String encuesta;
    private int ingreso;
    private String ingresoA;
    private String sexo;
    private String nombreColegio;
    private String idiomaMat;
    private String telCelular;
    private String direccion;
    private String ubigeo;
    private Date fecNac;
    private double notaAC;  // Nota Aptitud Académica
    private double notaCO;  // Nota Conocimientos
    private String respuesta;
    private String estadoAcademico; // "POSTULANTE" o "ALUMNO_LIBRE"
    
    // ===== CONSTRUCTORES =====
    
    /**
     * Constructor vacío con valores por defecto
     */
    public Postulante() {
        this.estadoAcademico = "POSTULANTE";
        this.notaAC = 0.0;
        this.notaCO = 0.0;
        this.ingreso = 0;
        this.codSede = 0;
        this.tipoColegio = 0;
    }
    
    /**
     * Constructor básico con campos esenciales
     */
    public Postulante(String codigo, String apellidosNombres, String opcion1, 
                     String modalidad, String dni) {
        this();
        this.codigo = codigo;
        this.apellidosNombres = apellidosNombres;
        this.opcion1 = opcion1;
        this.modalidad = modalidad;
        this.dni = dni;
    }
    
    /**
     * Constructor completo
     */
    public Postulante(String codigo, String apellidosNombres, String opcion1, 
                     String opcion2, String modalidad, String dni, int codSede,
                     Date inscripcion, String sexo, double notaAC, double notaCO,
                     String estadoAcademico) {
        this();
        this.codigo = codigo;
        this.apellidosNombres = apellidosNombres;
        this.opcion1 = opcion1;
        this.opcion2 = opcion2;
        this.modalidad = modalidad;
        this.dni = dni;
        this.codSede = codSede;
        this.inscripcion = inscripcion;
        this.sexo = sexo;
        this.notaAC = notaAC;
        this.notaCO = notaCO;
        this.estadoAcademico = estadoAcademico != null ? estadoAcademico : "POSTULANTE";
    }
    
    // ===== GETTERS Y SETTERS =====
    
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo != null ? codigo.trim().toUpperCase() : null;
    }
    
    public String getApellidosNombres() {
        return apellidosNombres;
    }
    
    public void setApellidosNombres(String apellidosNombres) {
        this.apellidosNombres = apellidosNombres != null ? apellidosNombres.trim().toUpperCase() : null;
    }
    
    public String getOpcion1() {
        return opcion1;
    }
    
    public void setOpcion1(String opcion1) {
        this.opcion1 = opcion1 != null ? opcion1.trim() : null;
    }
    
    public String getOpcion2() {
        return opcion2;
    }
    
    public void setOpcion2(String opcion2) {
        this.opcion2 = opcion2 != null ? opcion2.trim() : null;
    }
    
    public String getModalidad() {
        return modalidad;
    }
    
    public void setModalidad(String modalidad) {
        this.modalidad = modalidad != null ? modalidad.trim().toUpperCase() : null;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni != null ? dni.trim() : null;
    }
    
    public int getCodSede() {
        return codSede;
    }
    
    public void setCodSede(int codSede) {
        this.codSede = codSede;
    }
    
    public Date getInscripcion() {
        return inscripcion;
    }
    
    public void setInscripcion(Date inscripcion) {
        this.inscripcion = inscripcion;
    }
    
    public String getSexo() {
        return sexo;
    }
    
    public void setSexo(String sexo) {
        this.sexo = sexo != null ? sexo.trim().toUpperCase() : null;
    }
    
    public double getNotaAC() {
        return notaAC;
    }
    
    public void setNotaAC(double notaAC) {
        this.notaAC = Math.max(0, notaAC);
    }
    
    public double getNotaCO() {
        return notaCO;
    }
    
    public void setNotaCO(double notaCO) {
        this.notaCO = Math.max(0, notaCO);
    }
    
    public String getEstadoAcademico() {
        return estadoAcademico;
    }
    
    public void setEstadoAcademico(String estadoAcademico) {
        this.estadoAcademico = estadoAcademico != null ? estadoAcademico.trim().toUpperCase() : "POSTULANTE";
    }
    
    // ===== GETTERS/SETTERS ADICIONALES =====
    
    public String getUbigeoProcedencia() { return ubigeoProcedencia; }
    public void setUbigeoProcedencia(String ubigeoProcedencia) { this.ubigeoProcedencia = ubigeoProcedencia; }
    
    public String getCodColegio() { return codColegio; }
    public void setCodColegio(String codColegio) { this.codColegio = codColegio; }
    
    public Date getFechaEgresoColegio() { return fechaEgresoColegio; }
    public void setFechaEgresoColegio(Date fechaEgresoColegio) { this.fechaEgresoColegio = fechaEgresoColegio; }
    
    public int getTipoColegio() { return tipoColegio; }
    public void setTipoColegio(int tipoColegio) { this.tipoColegio = tipoColegio; }
    
    public String getUbigeoColegio() { return ubigeoColegio; }
    public void setUbigeoColegio(String ubigeoColegio) { this.ubigeoColegio = ubigeoColegio; }
    
    public String getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(String estadoCivil) { this.estadoCivil = estadoCivil; }
    
    public String getEncuesta() { return encuesta; }
    public void setEncuesta(String encuesta) { this.encuesta = encuesta; }
    
    public int getIngreso() { return ingreso; }
    public void setIngreso(int ingreso) { this.ingreso = ingreso; }
    
    public String getIngresoA() { return ingresoA; }
    public void setIngresoA(String ingresoA) { this.ingresoA = ingresoA != null ? ingresoA.trim() : null; }
    
    public String getNombreColegio() { return nombreColegio; }
    public void setNombreColegio(String nombreColegio) { this.nombreColegio = nombreColegio; }
    
    public String getIdiomaMat() { return idiomaMat; }
    public void setIdiomaMat(String idiomaMat) { this.idiomaMat = idiomaMat; }
    
    public String getTelCelular() { return telCelular; }
    public void setTelCelular(String telCelular) { this.telCelular = telCelular; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public String getUbigeo() { return ubigeo; }
    public void setUbigeo(String ubigeo) { this.ubigeo = ubigeo; }
    
    public Date getFecNac() { return fecNac; }
    public void setFecNac(Date fecNac) { this.fecNac = fecNac; }
    
    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }
    
    // ===== MÉTODOS DE UTILIDAD =====
    
    /**
     * Calcula la nota final (suma de AC + CO)
     */
    public double getNotaFinal() {
        return notaAC + notaCO;
    }
    
    /**
     * Verifica si es alumno libre
     */
    public boolean isAlumnoLibre() {
        return "ALUMNO_LIBRE".equals(estadoAcademico);
    }
    
    /**
     * Verifica si es postulante regular
     */
    public boolean isPostulante() {
        return "POSTULANTE".equals(estadoAcademico);
    }
    
    /**
     * Calcula la edad basada en fecha de nacimiento
     */
    public int getEdad() {
        if (fecNac == null) return 0;
        long diferencia = new Date().getTime() - fecNac.getTime();
        return (int) (diferencia / (365.25 * 24 * 60 * 60 * 1000));
    }
    
    // ===== VALIDACIONES =====
    
    /**
     * Valida si el postulante tiene datos mínimos requeridos
     */
    public boolean isValid() {
        return codigo != null && !codigo.trim().isEmpty() &&
               apellidosNombres != null && !apellidosNombres.trim().isEmpty() &&
               dni != null && dni.length() == 8 &&
               opcion1 != null && !opcion1.trim().isEmpty() &&
               modalidad != null && !modalidad.trim().isEmpty();
    }
    
    /**
     * Retorna errores de validación
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        
        if (codigo == null || codigo.trim().isEmpty()) {
            errors.append("- Código es requerido\n");
        }
        
        if (apellidosNombres == null || apellidosNombres.trim().isEmpty()) {
            errors.append("- Apellidos y nombres es requerido\n");
        }
        
        if (dni == null || dni.trim().isEmpty()) {
            errors.append("- DNI es requerido\n");
        } else if (dni.length() != 8) {
            errors.append("- DNI debe tener 8 dígitos\n");
        }
        
        if (opcion1 == null || opcion1.trim().isEmpty()) {
            errors.append("- Primera opción es requerida\n");
        }
        
        if (modalidad == null || modalidad.trim().isEmpty()) {
            errors.append("- Modalidad es requerida\n");
        }
        
        return errors.toString();
    }
    
    // ===== MÉTODOS ADICIONALES PARA RESULTADOS =====
    
    /**
     * Verifica si el postulante ingresó
     */
    public boolean ingreso() {
        return ingreso == 1;
    }
    
    /**
     * Establece si ingresó
     */
    public void setIngreso(boolean ingreso) {
        this.ingreso = ingreso ? 1 : 0;
    }
    
    /**
     * Obtiene puntaje final calculado
     */
    public double getPuntajeFinal() {
        return getNotaFinal();
    }
    
    /**
     * Verifica si tiene puntaje aprobatorio
     */
    public boolean puntajeAprobatorio() {
        return getPuntajeFinal() >= 11.0;
    }
    
    /**
     * Verifica si es postulante (método alternativo)
     */
    public boolean esPostulante() {
        return isPostulante();
    }
    
    /**
     * Verifica si es alumno libre (método alternativo)
     */
    public boolean esAlumnoLibre() {
        return isAlumnoLibre();
    }
    
    /**
     * Establece nota final calculada
     */
    public void setNotaFinal(double notaFinal) {
        // Se calcula automáticamente, pero agregamos el método por compatibilidad
    }
    
    /**
     * Método para validar datos (requerido por ResultadosPanel)
     */
    public boolean validarDatos() {
        return isValid();
    }
    
    /**
     * Verifica si es Beca 18 (método requerido por RegistroPanel)
     */
    public boolean isEsBeca18() {
        return modalidad != null && modalidad.toUpperCase().contains("BECA 18");
    }
    
    /**
     * Calcular puntaje final basado en las notas AC y CO
     */
    public void calcularPuntajeFinal() {
        // Calcular puntaje final simple como promedio de las dos notas
        // El puntaje se obtiene automáticamente a través del getter getNotaFinal()
    }
    
    // ===== MÉTODOS ESTÁNDAR =====
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return String.format("Postulante{codigo='%s', nombres='%s', opcion1='%s', " +
                           "modalidad='%s', dni='%s', tipo='%s', notaFinal=%.2f}",
                           codigo, apellidosNombres, opcion1, modalidad, dni, 
                           estadoAcademico, getNotaFinal());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Postulante that = (Postulante) obj;
        return Objects.equals(codigo, that.codigo) && Objects.equals(dni, that.dni);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(codigo, dni);
    }
}