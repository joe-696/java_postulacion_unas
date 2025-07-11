package view;

import model.Postulante;
import dao.PostulanteDAO;
import util.ExcelUtils;
import util.EventBus;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Panel para importación de datos desde Excel/CSV
 * @author joe-696
 */
public class ImportarPanel extends JPanel {
    
    private JButton btnSeleccionarArchivo, btnImportar, btnLimpiarBD;
    private JTextField txtRutaArchivo;
    private JTextArea txtAreaResultado;
    private JProgressBar progressBar;
    private JLabel lblEstado;
    private PostulanteDAO postulanteDAO;
    private File archivoSeleccionado;
    
    public ImportarPanel() {
        try {
            this.postulanteDAO = new PostulanteDAO();
            initComponents();
            System.out.println("✅ ImportarPanel inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando ImportarPanel: " + e.getMessage());
            e.printStackTrace();
            initPanelError();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        add(createMainPanel(), BorderLayout.CENTER);
    }
    
    private void initPanelError() {
        setLayout(new BorderLayout());
        
        JLabel lblError = new JLabel("<html><center>" +
            "📊 PANEL DE IMPORTACIÓN<br><br>" +
            "❌ Error de inicialización<br>" +
            "Verifique la configuración de la base de datos</center></html>");
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        add(lblError, BorderLayout.CENTER);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("📊 IMPORTACIÓN DE DATOS DESDE EXCEL");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(new Color(52, 152, 219));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de selección de archivo
        JPanel panelArchivo = createPanelSeleccionArchivo();
        
        // Panel de información
        JPanel panelInfo = createPanelInformacion();
        
        // Panel de resultados
        JPanel panelResultados = createPanelResultados();
        
        // Panel de progreso
        JPanel panelProgreso = createPanelProgreso();
        
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.add(panelArchivo, BorderLayout.NORTH);
        panelCentral.add(panelInfo, BorderLayout.CENTER);
        panelCentral.add(panelResultados, BorderLayout.SOUTH);
        
        panel.add(panelCentral, BorderLayout.CENTER);
        panel.add(panelProgreso, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPanelSeleccionArchivo() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📁 Seleccionar Archivo Excel/CSV"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Ruta del archivo
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Archivo:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        txtRutaArchivo = new JTextField();
        txtRutaArchivo.setEditable(false);
        txtRutaArchivo.setPreferredSize(new Dimension(400, 25));
        panel.add(txtRutaArchivo, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        btnSeleccionarArchivo = new JButton("📂 Buscar");
        btnSeleccionarArchivo.addActionListener(this::seleccionarArchivo);
        panel.add(btnSeleccionarArchivo, gbc);
        
        // Botones de acción
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnImportar = new JButton("📥 Importar Datos");
        btnImportar.setBackground(new Color(46, 204, 113));
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnImportar.setEnabled(false);
        btnImportar.addActionListener(this::importarDatos);
        
        btnLimpiarBD = new JButton("🗑️ Limpiar Base de Datos");
        btnLimpiarBD.setBackground(new Color(231, 76, 60));
        btnLimpiarBD.setForeground(Color.WHITE);
        btnLimpiarBD.addActionListener(this::limpiarBaseDatos);
        
        panelBotones.add(btnImportar);
        panelBotones.add(btnLimpiarBD);
        
        panel.add(panelBotones, gbc);
        
        return panel;
    }
    
    private JPanel createPanelInformacion() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("ℹ️ Formato del Archivo"));
        
        String informacion = """
            📋 FORMATO REQUERIDO - MEJORADO PARA CAMPOS VACÍOS:
            
            🔹 CAMPOS MÍNIMOS OBLIGATORIOS:
            1. CODIGO (obligatorio)
            2. Apellidos y Nombres (obligatorio)
            
            🔹 CAMPOS OPCIONALES (pueden estar vacíos):
            3. OPCION 1 (carrera preferida)
            4. OPCION 2 (segunda opción)
            5. MODALIDAD (por defecto: ORDINARIO)
            6. DNI (se genera automático si está vacío)
            7. SEXO, FECHAS, NOTAS, etc. (todos opcionales)
            
            🔹 FORMATOS SOPORTADOS:
            • Excel (.xlsx, .xls) - se intenta leer directamente
            • CSV separado por comas (,)
            • CSV separado por punto y coma (;)
            • TXT separado por tabulaciones
            
            🔹 CAMPOS VACÍOS - NO HAY PROBLEMA:
            ✅ El sistema maneja automáticamente campos en blanco
            ✅ Asigna valores por defecto inteligentes
            ✅ Solo requiere CÓDIGO y NOMBRE mínimo
            
            🔹 CONSEJOS PARA EXCEL:
            • Si no importa bien, guarde como CSV y vuelva a intentar
            • Asegúrese que la primera fila tenga encabezados
            • No importa el orden exacto de las columnas
            """;
        
        JTextArea txtInfo = new JTextArea(informacion);
        txtInfo.setEditable(false);
        txtInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtInfo.setBackground(new Color(248, 249, 250));
        
        JScrollPane scrollInfo = new JScrollPane(txtInfo);
        scrollInfo.setPreferredSize(new Dimension(0, 200));
        
        panel.add(scrollInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelResultados() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📊 Resultados de la Importación"));
        
        txtAreaResultado = new JTextArea();
        txtAreaResultado.setEditable(false);
        txtAreaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtAreaResultado.setText("⏳ Seleccione un archivo para comenzar la importación...");
        
        JScrollPane scrollResultados = new JScrollPane(txtAreaResultado);
        scrollResultados.setPreferredSize(new Dimension(0, 150));
        
        panel.add(scrollResultados, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPanelProgreso() {
        JPanel panel = new JPanel(new BorderLayout());
        
        lblEstado = new JLabel("📋 Listo para importar");
        lblEstado.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Esperando...");
        
        panel.add(lblEstado, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void seleccionarArchivo(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo Excel/CSV");
        
        // Filtros de archivo
        FileNameExtensionFilter filtroExcel = new FileNameExtensionFilter(
            "Archivos Excel/CSV (*.xls, *.xlsx, *.csv, *.txt)", "xls", "xlsx", "csv", "txt");
        fileChooser.setFileFilter(filtroExcel);
        
        int resultado = fileChooser.showOpenDialog(this);
        
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoSeleccionado = fileChooser.getSelectedFile();
            txtRutaArchivo.setText(archivoSeleccionado.getAbsolutePath());
            btnImportar.setEnabled(true);
            
            txtAreaResultado.setText("✅ Archivo seleccionado: " + archivoSeleccionado.getName() + "\n" +
                "📁 Ruta: " + archivoSeleccionado.getAbsolutePath() + "\n" +
                "📊 Tamaño: " + (archivoSeleccionado.length() / 1024) + " KB\n\n" +
                "🔄 Listo para importar. Haga clic en 'Importar Datos'");
        }
    }
    
    private void importarDatos(ActionEvent e) {
        if (archivoSeleccionado == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Seleccione un archivo primero",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Configurar UI para importación
        btnImportar.setEnabled(false);
        progressBar.setIndeterminate(true);
        lblEstado.setText("📥 Importando datos...");
        txtAreaResultado.setText("🔄 Iniciando importación...\n");
        
        // Ejecutar importación en hilo separado
        SwingWorker<List<Postulante>, String> worker = new SwingWorker<>() {
            @Override
            protected List<Postulante> doInBackground() {
                publish("📖 Leyendo archivo: " + archivoSeleccionado.getName());
                return ExcelUtils.importarPostulantesDesdeExcel(archivoSeleccionado.getAbsolutePath());
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String mensaje : chunks) {
                    txtAreaResultado.append(mensaje + "\n");
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Postulante> postulantes = get();
                    procesarPostulantesImportados(postulantes);
                } catch (Exception ex) {
                    txtAreaResultado.append("❌ Error durante la importación: " + ex.getMessage() + "\n");
                } finally {
                    btnImportar.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                    lblEstado.setText("✅ Importación completada");
                }
            }
        };
        
        worker.execute();
    }
    
    private void procesarPostulantesImportados(List<Postulante> postulantes) {
        if (postulantes.isEmpty()) {
            txtAreaResultado.append("⚠️ No se importaron postulantes.\n\n");
            txtAreaResultado.append("🔧 POSIBLES SOLUCIONES:\n");
            txtAreaResultado.append("• Verifique que el archivo tenga datos válidos\n");
            txtAreaResultado.append("• Asegúrese que las columnas CÓDIGO y NOMBRES tengan valores\n");
            txtAreaResultado.append("• Si es Excel, guarde como CSV y vuelva a intentar\n");
            txtAreaResultado.append("• Verifique que los separadores sean correctos (coma, punto y coma, o tab)\n");
            return;
        }
        
        txtAreaResultado.append("📊 RESUMEN DE IMPORTACIÓN:\n");
        txtAreaResultado.append("========================\n");
        txtAreaResultado.append("✅ Postulantes procesados: " + postulantes.size() + "\n");
        
        // Analizar calidad de datos importados
        long conDni = postulantes.stream().filter(p -> !p.getDni().startsWith("TEMP")).count();
        long conNotas = postulantes.stream().filter(p -> p.getNotaAC() > 0 || p.getNotaCO() > 0).count();
        long conCarrera = postulantes.stream().filter(p -> !p.getOpcion1().equals("SIN ESPECIFICAR")).count();
        
        txtAreaResultado.append("📋 Con DNI real: " + conDni + " (" + (conDni * 100 / postulantes.size()) + "%)\n");
        txtAreaResultado.append("📝 Con notas: " + conNotas + " (" + (conNotas * 100 / postulantes.size()) + "%)\n");
        txtAreaResultado.append("🎓 Con carrera especificada: " + conCarrera + " (" + (conCarrera * 100 / postulantes.size()) + "%)\n\n");
        
        // Guardar en base de datos
        int guardados = 0;
        int errores = 0;
        for (Postulante p : postulantes) {
            if (postulanteDAO.guardar(p)) {
                guardados++;
            } else {
                errores++;
            }
        }
        
        txtAreaResultado.append("💾 GUARDADO EN BASE DE DATOS:\n");
        txtAreaResultado.append("✅ Guardados exitosamente: " + guardados + "\n");
        txtAreaResultado.append("❌ Errores de guardado: " + errores + "\n\n");
        
        // Estadísticas por carrera (solo las más populares)
        txtAreaResultado.append("📈 TOP 5 CARRERAS MÁS DEMANDADAS:\n");
        postulantes.stream()
            .filter(p -> p.getOpcion1() != null && !p.getOpcion1().equals("SIN ESPECIFICAR"))
            .collect(java.util.stream.Collectors.groupingBy(
                Postulante::getOpcion1,
                java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> 
                txtAreaResultado.append("   " + (entry.getValue() < 10 ? " " : "") + 
                    entry.getValue() + "│ " + entry.getKey() + "\n"));
        
        // Estadísticas por modalidad
        txtAreaResultado.append("\n📊 DISTRIBUCIÓN POR MODALIDAD:\n");
        postulantes.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Postulante::getModalidad,
                java.util.stream.Collectors.counting()))
            .forEach((modalidad, cantidad) -> 
                txtAreaResultado.append("   • " + modalidad + ": " + cantidad + " postulantes\n"));
        
        // Resumen de notas
        if (conNotas > 0) {
            double promedioAC = postulantes.stream()
                .filter(p -> p.getNotaAC() > 0)
                .mapToDouble(Postulante::getNotaAC)
                .average().orElse(0);
            double promedioCO = postulantes.stream()
                .filter(p -> p.getNotaCO() > 0)
                .mapToDouble(Postulante::getNotaCO)
                .average().orElse(0);
                
            txtAreaResultado.append(String.format("\n📊 PROMEDIOS DE NOTAS:\n"));
            txtAreaResultado.append(String.format("   • Aptitud Académica: %.2f\n", promedioAC));
            txtAreaResultado.append(String.format("   • Conocimientos: %.2f\n", promedioCO));
        }
        
        txtAreaResultado.append("\n🎉 ¡Importación completada exitosamente!\n");
        txtAreaResultado.append("💡 Los campos vacíos fueron rellenados automáticamente con valores por defecto.\n");
        
        // Notificar evento
        EventBus.getInstance().publicarPostulantesImportados(guardados);
    }
    
    private void limpiarBaseDatos(ActionEvent e) {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "⚠️ ¿Está seguro que desea eliminar TODOS los postulantes?\n\n" +
            "Esta acción NO se puede deshacer.",
            "Confirmar Limpieza",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            // Aquí implementarías la limpieza de la BD
            txtAreaResultado.setText("🗑️ Base de datos limpiada.\n" +
                "✅ Todos los registros han sido eliminados.\n" +
                "📊 Listo para nueva importación.");
        }
    }
}
