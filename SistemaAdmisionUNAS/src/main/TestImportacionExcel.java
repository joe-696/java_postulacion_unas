package main;

import util.DatabaseConnection;
import util.ExcelUtils;
import model.Postulante;
import java.util.List;

/**
 * Test para verificar importacion de Excel con campos vacios
 * @author joe-696
 */
public class TestImportacionExcel {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE IMPORTACION EXCEL ===");
        System.out.println("Prueba de manejo de campos vacios");
        
        // Inicializar sistema
        DatabaseConnection.inicializar();
        
        // Probar importacion del archivo de ejemplo
        String rutaEjemplo = "ejemplos/postulantes_ejemplo_mejorado.csv";
        
        try {
            System.out.println("\nImportando archivo de ejemplo...");
            List<Postulante> postulantes = ExcelUtils.importarPostulantesDesdeExcel(rutaEjemplo);
            
            System.out.println("\nRESULTADOS:");
            System.out.println("Total importados: " + postulantes.size());
            
            // Mostrar algunos ejemplos
            for (int i = 0; i < Math.min(5, postulantes.size()); i++) {
                Postulante p = postulantes.get(i);
                System.out.println("\nPostulante " + (i+1) + ":");
                System.out.println("  Codigo: " + p.getCodigo());
                System.out.println("  Nombre: " + p.getApellidosNombres());
                System.out.println("  DNI: " + p.getDni());
                System.out.println("  Carrera: " + p.getOpcion1());
                System.out.println("  Modalidad: " + p.getModalidad());
                System.out.println("  Nota AC: " + p.getNotaAC());
                System.out.println("  Nota CO: " + p.getNotaCO());
            }
            
            System.out.println("\nPrueba completada exitosamente!");
            
        } catch (Exception e) {
            System.err.println("Error en prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
