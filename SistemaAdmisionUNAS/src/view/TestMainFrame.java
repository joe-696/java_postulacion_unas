package main;

import view.MainFrame;
import javax.swing.SwingUtilities;

/**
 * Clase para probar la ventana principal corregida
 * @author joe-696
 */
public class TestMainFrame {
    
    public static void main(String[] args) {
        System.out.println("🖥️ INICIANDO SISTEMA DE ADMISIÓN UNHEVAL");
        System.out.println("=========================================");
        
        // Configurar propiedades del sistema
        System.setProperty("swing.defaultlaf", "javax.swing.plaf.nimbus.NimbusLookAndFeel");
        
        // Ejecutar en el hilo de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("🚀 Creando ventana principal...");
                    MainFrame frame = new MainFrame();
                    
                    System.out.println("📱 Mostrando interfaz...");
                    frame.setVisible(true);
                    
                    System.out.println("✅ ¡Sistema iniciado exitosamente!");
                    System.out.println("📝 Pruebe el formulario de inscripción");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error iniciando interfaz: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}