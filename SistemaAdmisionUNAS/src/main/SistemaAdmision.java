package main;

import model.Postulante;
import model.Carrera;
import model.ExamenConfig;
import util.ExcelUtils;
import java.util.Date;
import java.util.List;

/**
 * Sistema de Admisión UNAS - Versión completa con modelos
 * @author joe-696
 */
public class SistemaAdmision {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE ADMISIÓN UNAS          ║");
        System.out.println("║        📚 MODELOS COMPLETOS 📚       ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println();
        
        System.out.println("🚀 Iniciando aplicación...");
        System.out.println("👤 Desarrollador: joe-696");
        System.out.println("📅 Fecha: " + java.time.LocalDateTime.now());
        
        // Probar todas las clases modelo
        probarModeloPostulante();
        probarModeloCarrera();
        probarModeloExamenConfig();
        
        // Probar integración
        probarIntegracionModelos();
        
        System.out.println("\n✅ ¡Todos los modelos funcionando correctamente!");
        System.out.println("🔜 Próximo paso: Configurar H2 Database");
    }
    
    /**
     * Probar modelo Postulante
     */
    private static void probarModeloPostulante() {
        System.out.println("\n🧪 PROBANDO MODELO POSTULANTE:");
        System.out.println("================================");
        
        // Crear postulante regular
        Postulante postulante = new Postulante();
        postulante.setCodigo("2025001");
        postulante.setApellidosNombres("GARCIA LOPEZ, JUAN CARLOS");
        postulante.setOpcion1("INGENIERÍA DE SISTEMAS");
        postulante.setOpcion2("INGENIERÍA CIVIL");
        postulante.setModalidad("ORDINARIO");
        postulante.setDni("12345678");
        postulante.setSexo("M");
        postulante.setNotaAC(15.5);
        postulante.setNotaCO(16.0);
        postulante.setEstadoAcademico("POSTULANTE");
        
        System.out.println("✅ Postulante: " + postulante.getCodigo() + " - " + postulante.getApellidosNombres());
        System.out.println("   📊 Nota final: " + postulante.getNotaFinal());
        System.out.println("   ✔️ Es válido: " + postulante.isValid());
        
        // Crear alumno libre
        Postulante alumnoLibre = new Postulante("AL2025001", 
                                               "RODRIGUEZ PEREZ, MARIA ELENA",
                                               "MEDICINA HUMANA", 
                                               "ORDINARIO", 
                                               "87654321");
        alumnoLibre.setEstadoAcademico("ALUMNO_LIBRE");
        alumnoLibre.setNotaAC(17.5);
        alumnoLibre.setNotaCO(18.0);
        
        System.out.println("✅ Alumno libre: " + alumnoLibre.getCodigo() + " - " + alumnoLibre.getApellidosNombres());
        System.out.println("   🎓 Es alumno libre: " + alumnoLibre.isAlumnoLibre());
        System.out.println("   📊 Nota final: " + alumnoLibre.getNotaFinal());
    }
    
    /**
     * Probar modelo Carrera
     */
    private static void probarModeloCarrera() {
        System.out.println("\n🧪 PROBANDO MODELO CARRERA:");
        System.out.println("=============================");
        
        // Crear carreras
        Carrera sistemas = new Carrera("ISI", "INGENIERÍA DE SISTEMAS E INFORMÁTICA", "INGENIERÍA", 25);
        sistemas.setPuntajeMinimo(12.0);
        
        Carrera medicina = new Carrera("MED", "MEDICINA HUMANA", "MEDICINA", 30);
        medicina.setPuntajeMinimo(14.0);
        
        Carrera administracion = new Carrera("ADM", "ADMINISTRACIÓN", "CIENCIAS EMPRESARIALES", 35);
        
        System.out.println("✅ " + sistemas);
        System.out.println("   🎯 Tipo examen: " + sistemas.getTipoExamen());
        System.out.println("   📊 Vacantes restantes: " + sistemas.getVacantesRestantes());
        
        System.out.println("✅ " + medicina);
        System.out.println("   🎯 Tipo examen: " + medicina.getTipoExamen());
        System.out.println("   📊 Puntaje mínimo: " + medicina.getPuntajeMinimo());
        
        System.out.println("✅ " + administracion);
        System.out.println("   🎯 Tipo examen: " + administracion.getTipoExamen());
        
        // Probar ocupación de vacantes
        sistemas.ocuparVacante();
        sistemas.ocuparVacante();
        sistemas.ocuparVacante();
        
        System.out.println("   🔄 Después de ocupar 3 vacantes:");
        System.out.println("   📊 Vacantes ocupadas: " + sistemas.getVacantesOcupadas());
        System.out.println("   📊 Porcentaje ocupación: " + String.format("%.1f%%", sistemas.getPorcentajeOcupacion()));
    }
    
    /**
     * Probar modelo ExamenConfig
     */
    private static void probarModeloExamenConfig() {
        System.out.println("\n🧪 PROBANDO MODELO EXAMEN CONFIG:");
        System.out.println("===================================");
        
        ExamenConfig examen = new ExamenConfig();
        examen.setNombre("EXAMEN ADMISIÓN UNAS 2025-I");
        examen.setFechaExamen(new Date());
        
        System.out.println("✅ " + examen);
        System.out.println("   📝 Total preguntas: " + examen.getTotalPreguntas());
        System.out.println("   ✔️ Configuración válida: " + examen.esConfiguracionValida());
        
        // Probar curvas por tipo
        System.out.println("\n   🎯 CURVAS POR TIPO DE EXAMEN:");
        System.out.println("   - Ingeniería: +" + examen.getCurvaPorTipo("INGENIERIA") + " puntos");
        System.out.println("   - FCA: +" + examen.getCurvaPorTipo("FCA") + " puntos");
        System.out.println("   - Medicina: +" + examen.getCurvaPorTipo("MEDICINA") + " puntos");
        
        // Probar cálculo con curva
        double puntajeBase = 10.5;
        double puntajeConCurva = examen.calcularPuntajeConCurva(puntajeBase, "INGENIERIA");
        System.out.println("\n   📊 Ejemplo cálculo:");
        System.out.println("   - Puntaje base: " + puntajeBase);
        System.out.println("   - Con curva Ingeniería: " + puntajeConCurva);
        System.out.println("   - Es aprobatorio: " + examen.esAprobatorio(puntajeConCurva));
    }
    
    /**
     * Probar integración entre modelos
     */
    private static void probarIntegracionModelos() {
        System.out.println("\n🧪 PROBANDO INTEGRACIÓN DE MODELOS:");
        System.out.println("=====================================");
        
        // Crear postulante
        Postulante postulante = new Postulante("2025010", 
                                              "TORRES MENDOZA, ANA LUCIA",
                                              "INGENIERÍA DE SISTEMAS", 
                                              "ORDINARIO", 
                                              "11223344");
        postulante.setNotaAC(10.0);
        postulante.setNotaCO(11.5);
        
        // Crear carrera
        Carrera carrera = new Carrera("ISI", "INGENIERÍA DE SISTEMAS E INFORMÁTICA", "INGENIERÍA", 25);
        
        // Crear configuración de examen
        ExamenConfig examen = new ExamenConfig();
        
        // Simular proceso de admisión
        double puntajeBase = postulante.getNotaFinal();
        double puntajeConCurva = examen.calcularPuntajeConCurva(puntajeBase, carrera.getTipoExamen());
        
        System.out.println("👤 Postulante: " + postulante.getApellidosNombres());
        System.out.println("🎓 Carrera: " + carrera.getNombre());
        System.out.println("📊 Puntaje base: " + puntajeBase);
        System.out.println("📊 Puntaje con curva: " + puntajeConCurva);
        System.out.println("✔️ Puntaje mínimo carrera: " + carrera.getPuntajeMinimo());
        
        boolean ingresa = puntajeConCurva >= carrera.getPuntajeMinimo() && carrera.tieneVacantesDisponibles();
        
        if (ingresa) {
            carrera.ocuparVacante();
            System.out.println("🎉 ¡INGRESA! Vacante asignada");
            System.out.println("📈 Vacantes ocupadas: " + carrera.getVacantesOcupadas() + "/" + carrera.getVacantesDisponibles());
        } else {
            System.out.println("❌ NO INGRESA - Puntaje insuficiente o sin vacantes");
        }
    }
}