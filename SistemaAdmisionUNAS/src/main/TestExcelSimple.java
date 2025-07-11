package main;

import model.Postulante;
import util.ExcelUtils;
import java.util.List;

/**
 * Test simple para importacion de Excel
 */
public class TestExcelSimple {
    
    public static void main(String[] args) {
        System.out.println("=== TEST IMPORTACION EXCEL SIMPLE ===");
        
        // Probar con el archivo de ejemplo
        String archivo = "ejemplos/postulantes_ejemplo_mejorado.csv";
        
        try {
            System.out.println("Importando: " + archivo);
            
            List<Postulante> postulantes = ExcelUtils.importarPostulantesDesdeExcel(archivo);
            
            System.out.println("Importados: " + postulantes.size() + " postulantes");
            
            // Mostrar los primeros 3
            for (int i = 0; i < Math.min(3, postulantes.size()); i++) {
                Postulante p = postulantes.get(i);
                System.out.println("\nPostulante " + (i+1) + ":");
                System.out.println("  Codigo: " + p.getCodigo());
                System.out.println("  Nombre: " + p.getApellidosNombres());
                System.out.println("  DNI: " + p.getDni());
                System.out.println("  Carrera: " + p.getOpcion1());
                System.out.println("  Nota AC: " + p.getNotaAC());
                System.out.println("  Nota CO: " + p.getNotaCO());
                System.out.println("  Nota Final: " + p.getNotaFinal());
            }
            
            System.out.println("\nImportacion exitosa!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
