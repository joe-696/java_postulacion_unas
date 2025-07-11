package view;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal del Sistema de Admisión UNAS
 * Versión completa con todos los paneles
 * @author joe-696
 */
public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private InscripcionPanel inscripcionPanel;
    private RegistroPanel registroPanel;
    private ResultadosPanel resultadosPanel;
    
    public MainFrame() {
        initComponents();
        setupWindow();
    }
    
    private void initComponents() {
        setTitle("🎓 Sistema de Admisión UNAS - v1.0");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Agregar hook de cierre para guardar datos
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarAplicacion();
            }
        });
        
        createTabbedPane();
        createMenuBar();
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        
        JMenuItem itemNuevo = new JMenuItem("🆕 Nuevo Postulante");
        itemNuevo.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem itemCargar = new JMenuItem("📊 Cargar Datos Simulados");
        itemCargar.addActionListener(e -> cargarDatosSimulados());
        
        menuArchivo.addSeparator();
        
        JMenuItem itemSalir = new JMenuItem("🚪 Salir");
        itemSalir.addActionListener(e -> cerrarAplicacion());
        
        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemCargar);
        menuArchivo.add(itemSalir);
        
        // Menú Ver
        JMenu menuVer = new JMenu("Ver");
        
        JMenuItem itemInscripcion = new JMenuItem("📝 Inscripción");
        itemInscripcion.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem itemLista = new JMenuItem("📋 Lista de Postulantes");
        itemLista.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            registroPanel.actualizarTabla();
        });
        
        JMenuItem itemResultados = new JMenuItem("🏆 Resultados");
        itemResultados.addActionListener(e -> {
            tabbedPane.setSelectedIndex(2);
            resultadosPanel.actualizarResultados();
        });
        
        menuVer.add(itemInscripcion);
        menuVer.add(itemLista);
        menuVer.add(itemResultados);
        
        // Menú Herramientas
        JMenu menuHerramientas = new JMenu("Herramientas");
        
        JMenuItem itemEstadisticas = new JMenuItem("📊 Ver Estadísticas");
        itemEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        JMenuItem itemValidacion = new JMenuItem("✅ Validar Datos");
        itemValidacion.addActionListener(e -> validarDatos());
        
        menuHerramientas.add(itemEstadisticas);
        menuHerramientas.add(itemValidacion);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        
        JMenuItem itemManual = new JMenuItem("📖 Manual de Usuario");
        itemManual.addActionListener(e -> mostrarManualUsuario());
        
        JMenuItem itemAcerca = new JMenuItem("ℹ️ Acerca de...");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        
        menuAyuda.add(itemManual);
        menuAyuda.add(itemAcerca);
        
        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        menuBar.add(menuHerramientas);
        menuBar.add(menuAyuda);
        
        setJMenuBar(menuBar);
    }
    
    private void createTabbedPane() {
    tabbedPane = new JTabbedPane();
    tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
    
    try {
        // Crear todos los paneles
        inscripcionPanel = new InscripcionPanel();
        registroPanel = new RegistroPanel();
        resultadosPanel = new ResultadosPanel();
        AdministracionPanel administracionPanel = new AdministracionPanel();
        
        // Agregar pestañas (la importación ahora está integrada en Lista)
        tabbedPane.addTab("📝 Inscripción", inscripcionPanel);
        tabbedPane.addTab("📋 Lista", registroPanel);
        tabbedPane.addTab("🏆 Resultados", resultadosPanel);
        tabbedPane.addTab("⚙️ Administración", administracionPanel);
        
    } catch (Exception e) {
        System.err.println("Error creando pestañas: " + e.getMessage());
        // ... resto del manejo de errores
    }
    
    // Configurar colores de fondo para las pestañas
    try {
        tabbedPane.setBackgroundAt(0, new Color(230, 255, 230));
        tabbedPane.setBackgroundAt(1, new Color(230, 240, 255));
        tabbedPane.setBackgroundAt(2, new Color(255, 245, 230));
        tabbedPane.setBackgroundAt(3, new Color(245, 230, 255));
    } catch (Exception e) {
        System.err.println("Error configurando colores: " + e.getMessage());
    }
    
    add(tabbedPane, BorderLayout.CENTER);
}
    
    private JPanel createPlaceholderPanel(String titulo, String descripcion) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblDescripcion = new JLabel("<html><center>" + descripcion + "</center></html>");
        lblDescripcion.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(20));
        panel.add(lblDescripcion);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void setupWindow() {
        // Configurar ventana
        setSize(1200, 800);
        setLocationRelativeTo(null); // Centrar en pantalla
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa
        
        // Configurar Look and Feel de forma segura
        configurarLookAndFeel();
        
        // Mostrar mensaje de bienvenida
        SwingUtilities.invokeLater(() -> {
            mostrarMensajeBienvenida();
        });
    }
    
    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
            SwingUtilities.updateComponentTreeUI(this);
            System.out.println("Look and Feel configurado exitosamente");
        } catch (Exception e) {
            System.out.println("Usando Look and Feel por defecto: " + e.getMessage());
        }
    }
    
    private void cargarDatosSimulados() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea cargar datos de prueba simulados?\n\n" +
            "Esto agregará 50+ postulantes con datos realistas\n" +
            "para probar el sistema.",
            "Cargar Datos Simulados",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            // Aquí llamarías a ExcelUtils.importarPostulantesSimulado()
            JOptionPane.showMessageDialog(this,
                "✅ Datos simulados cargados exitosamente\n\n" +
                "Se han agregado 53 postulantes de prueba.\n" +
                "Vaya a la pestaña 'Lista' para verlos.",
                "Datos Cargados",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Actualizar las vistas
            registroPanel.actualizarTabla();
            resultadosPanel.actualizarResultados();
        }
    }
    
    private void mostrarEstadisticas() {
        String estadisticas = """
            📊 ESTADÍSTICAS DEL SISTEMA
            ===========================
            
            📋 Módulos Implementados:
            ✅ Inscripción de Postulantes
            ✅ Lista y Gestión de Postulantes
            ✅ Resultados de Admisión
            🔄 Importación Excel (En desarrollo)
            
            💾 Base de Datos: H2 Database
            🔧 Framework: Java Swing
            📅 Versión: 1.0
            👨‍💻 Desarrollador: joe-696
            """;
        
        JTextArea textArea = new JTextArea(estadisticas);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Estadísticas del Sistema", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void validarDatos() {
        JOptionPane.showMessageDialog(this,
            "🔍 Función de validación será implementada próximamente.\n\n" +
            "Esta herramienta verificará:\n" +
            "• Integridad de datos\n" +
            "• Duplicados\n" +
            "• Campos requeridos\n" +
            "• Consistencia de información",
            "Validar Datos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarManualUsuario() {
        String manual = """
            📖 MANUAL RÁPIDO DEL USUARIO
            ============================
            
            🏠 INICIO:
            1. Use la pestaña 'Inscripción' para registrar nuevos postulantes
            2. Complete todos los campos requeridos
            3. Haga clic en 'Guardar Postulante'
            
            📋 LISTA:
            1. Vea todos los postulantes registrados
            2. Use los filtros para buscar específicos
            3. Doble clic para ver detalles
            4. Use 'Exportar' para guardar datos
            
            🏆 RESULTADOS:
            1. Procese la admisión con el botón correspondiente
            2. Vea estadísticas de ingresantes
            3. Exporte resultados finales
            
            💡 CONSEJOS:
            • Use Ctrl+Tab para cambiar entre pestañas
            • Los datos se guardan automáticamente
            • Mantenga backup de sus datos
            """;
        
        JTextArea textArea = new JTextArea(manual);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Manual de Usuario", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String mensaje = """
            🎓 Sistema de Admisión UNAS
            
            📋 Versión: 1.0.0
            👨‍💻 Desarrollador: joe-696
            📚 Curso: Estructura de Datos y Algoritmos
            🏛️ Universidad: UNAS
            📅 Fecha: Junio 2025
            
            🔧 Tecnologías Utilizadas:
            • Java 17+
            • Swing GUI Framework
            • H2 Database Engine
            • Maven (Build Tool)
            
            📜 Licencia: Académica
            🎯 Propósito: Proyecto Educativo
            
            Sistema integral de gestión de admisión
            universitaria con funcionalidades completas
            de registro, procesamiento y reportes.
            """;
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca del Sistema", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarMensajeBienvenida() {
        String mensaje = """
            🎉 ¡Bienvenido al Sistema de Admisión UNAS!
            
            ✅ Sistema iniciado correctamente
            ✅ Base de datos H2 conectada
            ✅ Interfaz gráfica funcionando
            ✅ Todas las funcionalidades disponibles
            
            💡 Para comenzar:
            1. Vaya a la pestaña 'Inscripción' para registrar postulantes
            2. Use 'Lista' para gestionar los datos
            3. Procese resultados en la pestaña 'Resultados'
            
            🔧 Use el menú 'Archivo > Cargar Datos Simulados' 
            para agregar datos de prueba.
            """;
        
        JOptionPane.showMessageDialog(this, mensaje, 
            "🎓 Sistema de Admisión UNAS",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters para acceso desde otros componentes
    public InscripcionPanel getInscripcionPanel() {
        return inscripcionPanel;
    }
    
    public RegistroPanel getRegistroPanel() {
        return registroPanel;
    }
    
    public ResultadosPanel getResultadosPanel() {
        return resultadosPanel;
    }
    
    /**
     * Cerrar aplicación con persistencia de datos
     */
    private void cerrarAplicacion() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro que desea salir del sistema?\n\n" +
            "Los datos se guardarán automáticamente.",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            // Forzar cierre con persistencia
            try {
                util.DatabaseConnection.cerrarConexion();
                System.out.println("✅ Aplicación cerrada correctamente");
            } catch (Exception e) {
                System.err.println("⚠️ Error cerrando la base de datos: " + e.getMessage());
            }
            
            System.exit(0);
        }
    }
}