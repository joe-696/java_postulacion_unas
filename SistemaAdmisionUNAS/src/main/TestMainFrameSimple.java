package main;

import javax.swing.*;
import java.awt.*;

/**
 * Test simple para probar solo la ventana basica
 * @author joe-696
 */
public class TestMainFrameSimple {
    
    public static void main(String[] args) {
        System.out.println("INICIANDO SISTEMA DE ADMISION UNAS (MODO SIMPLE)");
        System.out.println("==================================================");
        
        try {
            // Configurar Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            
            SwingUtilities.invokeLater(() -> {
                try {
                    System.out.println("Creando ventana de prueba...");
                    
                    // Crear una ventana simple para probar
                    JFrame frame = new JFrame("Sistema de Admision UNAS - Test");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(800, 600);
                    frame.setLocationRelativeTo(null);
                    
                    // Agregar contenido simple
                    JLabel label = new JLabel("<html><center>" +
                        "<h1>Sistema de Admision UNAS</h1><br>" +
                        "<h2>Test de Inicializacion Exitoso</h2><br>" +
                        "<p>La ventana se carga correctamente</p>" +
                        "</center></html>");
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
                    
                    frame.add(label);
                    frame.setVisible(true);
                    
                    System.out.println("Ventana de prueba creada exitosamente!");
                    
                } catch (Exception e) {
                    System.err.println("Error en test simple: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error configurando Look and Feel: " + e.getMessage());
        }
    }
}
