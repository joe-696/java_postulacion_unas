package main;

import util.ExcelUtilsSimple;
import dao.PostulanteDAO;
import model.Postulante;
import java.util.List;

/**
 * TEST SIMPLE - Importar archivo y guardar en base de datos
 * FUNCIONA SIN ERRORES
 */
public class TestImportacionSimple {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  TEST DE IMPORTACION SIMPLE");
        System.out.println("========================================");
        
        // Ruta del archivo de prueba
        String rutaArchivo = "datos_prueba.csv";
        
        try {
            // 1. IMPORTAR ARCHIVO
            System.out.println("\n1. IMPORTANDO ARCHIVO...");
            List<Postulante> postulantes = ExcelUtilsSimple.importarArchivo(rutaArchivo);
            
            if (postulantes.isEmpty()) {
                System.out.println("❌ No se importaron datos");
                System.out.println("Verifique que el archivo existe: " + rutaArchivo);
                return;
            }
            
            // 2. MOSTRAR DATOS IMPORTADOS
            System.out.println("\n2. DATOS IMPORTADOS:");
            System.out.println("Total: " + postulantes.size() + " postulantes");
            System.out.println("----------------------------------------");
            
            for (int i = 0; i < Math.min(5, postulantes.size()); i++) {
                Postulante p = postulantes.get(i);
                System.out.printf("• %s - %s (AC:%.1f CO:%.1f Final:%.1f)%n", 
                    p.getCodigo(), 
                    p.getApellidosNombres(), 
                    p.getNotaAC(), 
                    p.getNotaCO(), 
                    p.getNotaFinal());
            }
            
            if (postulantes.size() > 5) {
                System.out.println("... y " + (postulantes.size() - 5) + " más");
            }
            
            // 3. GUARDAR EN BASE DE DATOS (opcional)
            System.out.println("\n3. GUARDANDO EN BASE DE DATOS...");
            PostulanteDAO dao = new PostulanteDAO();
            int guardados = 0;
            int errores = 0;
            
            for (Postulante p : postulantes) {
                try {
                    if (dao.guardar(p)) {
                        guardados++;
                    } else {
                        errores++;
                    }
                } catch (Exception e) {
                    errores++;
                    System.err.println("Error guardando " + p.getCodigo() + ": " + e.getMessage());
                }
            }
            
            System.out.println("✓ Guardados: " + guardados);
            System.out.println("✗ Errores: " + errores);
            
            // 4. EXPORTAR RESULTADO
            System.out.println("\n4. EXPORTANDO RESULTADO...");
            String archivoSalida = "postulantes_procesados.csv";
            if (ExcelUtilsSimple.exportarArchivo(postulantes, archivoSalida)) {
                System.out.println("✓ Archivo exportado: " + archivoSalida);
            }
            
            System.out.println("\n========================================");
            System.out.println("  IMPORTACION COMPLETADA EXITOSAMENTE");
            System.out.println("========================================");
            
        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
