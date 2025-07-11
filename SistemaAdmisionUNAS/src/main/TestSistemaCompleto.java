package main;

import model.Postulante;
import util.ExcelUtils;
import util.AlgoritmoAdmision;
import java.util.List;

/**
 * Prueba completa del Sistema de Admisión UNAS
 * Demuestra la importación de datos y el algoritmo de admisión
 * @author joe-696
 */
public class TestSistemaCompleto {
    
    public static void main(String[] args) {
        System.out.println("🎓 SISTEMA COMPLETO DE ADMISIÓN UNAS");
        System.out.println("====================================");
        
        // PASO 1: Importar/Generar datos de postulantes
        System.out.println("\n🔄 PASO 1: Generando datos de postulantes...");
        List<Postulante> postulantes = ExcelUtils.importarPostulantesSimulado();
        
        // PASO 2: Validar integridad de datos
        System.out.println("\n🔍 PASO 2: Validando integridad...");
        List<String> errores = ExcelUtils.validarIntegridad(postulantes);
        if (!errores.isEmpty()) {
            System.out.println("❌ Errores encontrados:");
            errores.forEach(System.out::println);
            return;
        }
        
        // PASO 3: Mostrar estadísticas iniciales
        ExcelUtils.generarReporteEstadistico(postulantes);
        
        // PASO 4: Procesar algoritmo de admisión
        System.out.println("\n🚀 PASO 4: Ejecutando algoritmo de admisión...");
        AlgoritmoAdmision algoritmo = new AlgoritmoAdmision();
        AlgoritmoAdmision.ResultadoAdmision resultado = algoritmo.procesarAdmision(postulantes);
        
        // PASO 5: Exportar resultados
        System.out.println("\n💾 PASO 5: Exportando resultados...");
        ExcelUtils.exportarResultados(postulantes, "resultados_admision_2025.csv");
        
        // PASO 6: Mostrar top 10 ingresantes por carrera
        mostrarTopIngresantes(resultado);
        
        System.out.println("\n🎉 ¡PROCESO COMPLETO FINALIZADO!");
        System.out.println("📄 Revisa el archivo: resultados_admision_2025.csv");
    }
    
    /**
     * Muestra los top ingresantes por carrera
     */
    private static void mostrarTopIngresantes(AlgoritmoAdmision.ResultadoAdmision resultado) {
        System.out.println("\n🏆 TOP 5 INGRESANTES POR CARRERA:");
        System.out.println("==================================");
        
        for (var entry : resultado.getIngresantesPorCarrera().entrySet()) {
            String carrera = entry.getKey();
            List<Postulante> ingresantes = entry.getValue();
            
            System.out.println("\n📚 " + carrera + ":");
            
            // Ordenar por puntaje final descendente
            ingresantes.sort((p1, p2) -> Double.compare(p2.getPuntajeFinal(), p1.getPuntajeFinal()));
            
            // Mostrar top 5
            for (int i = 0; i < Math.min(5, ingresantes.size()); i++) {
                Postulante p = ingresantes.get(i);
                System.out.printf("   %d. %s - %.2f pts (%s)%n",
                    i + 1,
                    p.getApellidosNombres(),
                    p.getPuntajeFinal(),
                    p.getEstadoAcademico()
                );
            }
        }
    }
}