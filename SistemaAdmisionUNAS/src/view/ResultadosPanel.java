package view;

// Imports específicos primero
import model.Postulante;
import dao.PostulanteDAO;
import util.ExcelUtils;

// Imports de Java/Swing
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

/**
 * Panel de resultados del examen de admisión
 * @author joe-696
 */
public class ResultadosPanel extends JPanel {
    
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroEstado;
    private JTextField txtBuscarNombre;
    private JButton btnProcesarAdmision, btnExportarExcel, btnActualizar;
    private JLabel lblTotalPostulantes, lblIngresantes, lblNoIngresantes;
    private JTextArea txtResumenProceso;
    private PostulanteDAO postulanteDAO;
    private List<Postulante> resultadosCompletos;
    
    public ResultadosPanel() {
        try {
            this.postulanteDAO = new PostulanteDAO();
            this.resultadosCompletos = new ArrayList<>();
            initComponents();
            cargarResultados();
            System.out.println("✅ ResultadosPanel inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando ResultadosPanel: " + e.getMessage());
            e.printStackTrace();
            initPanelError();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central con tabla
        add(createMainPanel(), BorderLayout.CENTER);
        
        // Panel inferior con botones
        add(createFooterPanel(), BorderLayout.SOUTH);
    }
    
    private void initPanelError() {
        setLayout(new BorderLayout());
        
        JLabel lblError = new JLabel("<html><center>" +
            "🏆 PANEL DE RESULTADOS<br><br>" +
            "❌ Error de inicialización<br>" +
            "Verifique que la base de datos esté configurada</center></html>");
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        add(lblError, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("🏆 RESULTADOS DEL EXAMEN DE ADMISIÓN");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titulo.setForeground(new Color(46, 204, 113));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Panel de estadísticas
        JPanel statsPanel = createStatsPanel();
        
        // Panel de filtros
        JPanel filtersPanel = createFiltersPanel();
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(filtersPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        
        lblTotalPostulantes = createStatCard("Total Evaluados", "0", new Color(52, 152, 219));
        lblIngresantes = createStatCard("Ingresantes", "0", new Color(46, 204, 113));
        lblNoIngresantes = createStatCard("No Ingresantes", "0", new Color(231, 76, 60));
        
        panel.add(lblTotalPostulantes);
        panel.add(lblIngresantes);
        panel.add(lblNoIngresantes);
        
        return panel;
    }
    
    private JLabel createStatCard(String titulo, String valor, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblValor = new JLabel(valor);
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        lblValor.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(5));
        card.add(lblValor);
        
        JLabel container = new JLabel();
        container.setLayout(new BorderLayout());
        container.add(card);
        
        return container;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Filtros de Resultados"));
        
        // Filtro por estado de ingreso
        panel.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "INGRESÓ", "NO INGRESÓ"
        });
        cmbFiltroEstado.addActionListener(e -> filtrarResultados());
        panel.add(cmbFiltroEstado);
        
        panel.add(Box.createHorizontalStrut(15));
        
        // Búsqueda por nombre
        panel.add(new JLabel("Buscar:"));
        txtBuscarNombre = new JTextField(20);
        txtBuscarNombre.setToolTipText("Buscar por nombre o apellido");
        txtBuscarNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarResultados();
            }
        });
        panel.add(txtBuscarNombre);
        
        // Botón procesar
        btnProcesarAdmision = new JButton("🎯 Procesar Admisión");
        btnProcesarAdmision.setBackground(new Color(46, 204, 113));
        btnProcesarAdmision.setForeground(Color.WHITE);
        btnProcesarAdmision.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnProcesarAdmision.addActionListener(e -> procesarAdmision());
        
        panel.add(Box.createHorizontalStrut(20));
        panel.add(btnProcesarAdmision);
        
        return panel;
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear tabla de resultados
        createTable();
        
        // Scroll pane para la tabla
        JScrollPane scrollTable = new JScrollPane(tablaResultados);
        scrollTable.setPreferredSize(new Dimension(0, 300));
        
        // Panel de resumen
        JPanel resumenPanel = createResumenPanel();
        
        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, resumenPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.6);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void createTable() {
        String[] columnas = {
            "Puesto", "Código", "Apellidos y Nombres", "Primera Opción", 
            "Puntaje Final", "Estado", "Modalidad"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaResultados = new JTable(modeloTabla);
        configurarTabla();
    }
    
    private void configurarTabla() {
        // Configuración básica
        tablaResultados.setRowHeight(25);
        tablaResultados.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        tablaResultados.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tablaResultados.getTableHeader().setBackground(new Color(46, 204, 113));
        tablaResultados.getTableHeader().setForeground(Color.WHITE);
        tablaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResultados.setGridColor(new Color(220, 220, 220));
        tablaResultados.setShowGrid(true);
        
        // Configurar ancho de columnas
        int[] anchosColumnas = {60, 80, 200, 180, 80, 100, 120};
        for (int i = 0; i < anchosColumnas.length && i < tablaResultados.getColumnCount(); i++) {
            tablaResultados.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnas[i]);
        }
        
        // Renderer personalizado para estado
        if (tablaResultados.getColumnCount() > 5) {
            tablaResultados.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
                    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                    
                    if (value != null) {
                        String estado = value.toString();
                        if ("INGRESÓ".equals(estado)) {
                            setBackground(isSelected ? table.getSelectionBackground() : new Color(212, 237, 218));
                            setForeground(isSelected ? table.getSelectionForeground() : new Color(21, 87, 36));
                            setText("✅ INGRESÓ");
                        } else if ("NO INGRESÓ".equals(estado)) {
                            setBackground(isSelected ? table.getSelectionBackground() : new Color(248, 215, 218));
                            setForeground(isSelected ? table.getSelectionForeground() : new Color(114, 28, 36));
                            setText("❌ NO INGRESÓ");
                        } else {
                            setBackground(table.getBackground());
                            setForeground(table.getForeground());
                            setText(estado);
                        }
                    } else {
                        setBackground(table.getBackground());
                        setForeground(table.getForeground());
                    }
                    
                    return this;
                }
            });
        }
    }
    
    private JPanel createResumenPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("📊 Resumen del Proceso"));
        
        txtResumenProceso = new JTextArea(6, 0);
        txtResumenProceso.setEditable(false);
        txtResumenProceso.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtResumenProceso.setBackground(new Color(248, 249, 250));
        txtResumenProceso.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollResumen = new JScrollPane(txtResumenProceso);
        panel.add(scrollResumen, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setPreferredSize(new Dimension(120, 35));
        btnActualizar.addActionListener(e -> cargarResultados());
        
        btnExportarExcel = new JButton("📊 Exportar CSV");
        btnExportarExcel.setBackground(new Color(46, 204, 113));
        btnExportarExcel.setForeground(Color.WHITE);
        btnExportarExcel.setPreferredSize(new Dimension(150, 35));
        btnExportarExcel.addActionListener(e -> exportarResultados());
        
        panel.add(btnActualizar);
        panel.add(btnExportarExcel);
        
        return panel;
    }
    
    private void cargarResultados() {
        if (postulanteDAO == null) {
            System.err.println("❌ PostulanteDAO no inicializado");
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                resultadosCompletos = postulanteDAO.obtenerTodos();
                mostrarResultados();
                actualizarEstadisticas();
                generarResumenProceso();
                System.out.println("✅ Resultados cargados: " + resultadosCompletos.size() + " postulantes");
            } catch (Exception e) {
                System.err.println("❌ Error cargando resultados: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error cargando resultados: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void mostrarResultados() {
        if (modeloTabla == null) return;
        
        modeloTabla.setRowCount(0);
        
        // Filtrar postulantes con puntaje y ordenar por puntaje final
        List<Postulante> conPuntaje = new ArrayList<>();
        for (Postulante p : resultadosCompletos) {
            if (p.getNotaFinal() > 0) {
                conPuntaje.add(p);
            }
        }
        
        // Ordenar por puntaje descendente
        conPuntaje.sort((p1, p2) -> Double.compare(p2.getNotaFinal(), p1.getNotaFinal()));
        
        int puesto = 1;
        for (Postulante p : conPuntaje) {
            String estado = p.getNotaFinal() >= 11.0 ? "INGRESÓ" : "NO INGRESÓ";
            
            Object[] fila = {
                p.getNotaFinal() >= 11.0 ? puesto++ : "-",
                p.getCodigo(),
                p.getApellidosNombres(),
                p.getOpcion1() != null ? p.getOpcion1() : "",
                String.format("%.1f", p.getNotaFinal()),
                estado,
                p.getModalidad() != null ? p.getModalidad() : ""
            };
            
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarEstadisticas() {
        int total = 0;
        int ingresantes = 0;
        int noIngresantes = 0;
        
        for (Postulante p : resultadosCompletos) {
            if (p.getNotaFinal() > 0) {
                total++;
                if (p.getNotaFinal() >= 11.0) {
                    ingresantes++;
                } else {
                    noIngresantes++;
                }
            }
        }
        
        actualizarStatCard(lblTotalPostulantes, String.valueOf(total));
        actualizarStatCard(lblIngresantes, String.valueOf(ingresantes));
        actualizarStatCard(lblNoIngresantes, String.valueOf(noIngresantes));
    }
    
    private void actualizarStatCard(JLabel card, String valor) {
        try {
            if (card != null && card.getComponentCount() > 0) {
                JPanel cardPanel = (JPanel) card.getComponent(0);
                if (cardPanel.getComponentCount() > 2) {
                    JLabel lblValor = (JLabel) cardPanel.getComponent(2);
                    lblValor.setText(valor);
                }
            }
        } catch (Exception e) {
            System.err.println("Error actualizando stat card: " + e.getMessage());
        }
    }
    
    private void generarResumenProceso() {
        if (txtResumenProceso == null) return;
        
        StringBuilder resumen = new StringBuilder();
        
        resumen.append("═══════════════════════════════════════════════════════════════\n");
        resumen.append("                    RESUMEN DE RESULTADOS\n");
        resumen.append("═══════════════════════════════════════════════════════════════\n\n");
        
        // Estadísticas generales
        int totalExaminados = 0;
        int ingresantes = 0;
        double puntajePromedio = 0.0;
        double puntajeMaximo = 0.0;
        
        for (Postulante p : resultadosCompletos) {
            if (p.getNotaFinal() > 0) {
                totalExaminados++;
                puntajePromedio += p.getNotaFinal();
                if (p.getNotaFinal() > puntajeMaximo) {
                    puntajeMaximo = p.getNotaFinal();
                }
                if (p.getNotaFinal() >= 11.0) {
                    ingresantes++;
                }
            }
        }
        
        if (totalExaminados > 0) {
            puntajePromedio = puntajePromedio / totalExaminados;
        }
        
        resumen.append("📊 ESTADÍSTICAS GENERALES:\n");
        resumen.append(String.format("   • Total de examinados: %d\n", totalExaminados));
        resumen.append(String.format("   • Total ingresantes: %d\n", ingresantes));
        resumen.append(String.format("   • Porcentaje de ingreso: %.1f%%\n", 
            totalExaminados > 0 ? (ingresantes * 100.0 / totalExaminados) : 0));
        resumen.append(String.format("   • Puntaje promedio: %.1f\n", puntajePromedio));
        resumen.append(String.format("   • Puntaje máximo: %.1f\n", puntajeMaximo));
        resumen.append(String.format("   • Nota mínima de ingreso: 11.0 puntos\n\n"));
        
        // Fecha de proceso
        resumen.append("📅 Última actualización: ");
        resumen.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date()));
        
        txtResumenProceso.setText(resumen.toString());
        txtResumenProceso.setCaretPosition(0);
    }
    
    private void filtrarResultados() {
        // Implementación básica de filtros
        mostrarResultados();
    }
    
    private void procesarAdmision() {
        JOptionPane.showMessageDialog(this,
            "🎯 Función de procesamiento de admisión\n" +
            "será implementada próximamente.\n\n" +
            "Por ahora, los resultados se muestran\n" +
            "basados en puntaje ≥ 11.0 para ingreso.",
            "Procesar Admisión",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportarResultados() {
        if (resultadosCompletos == null || resultadosCompletos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay datos para exportar",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("Resultados_Admision_" +
            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String rutaArchivo = fileChooser.getSelectedFile().getAbsolutePath();
            if (!rutaArchivo.endsWith(".csv")) {
                rutaArchivo += ".csv";
            }
            
            if (ExcelUtils.exportarPostulantesAExcel(resultadosCompletos, rutaArchivo)) {
                JOptionPane.showMessageDialog(this,
                    "✅ Resultados exportados exitosamente:\n" + rutaArchivo,
                    "Exportación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al exportar los resultados",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Método público para actualizar desde MainFrame
     */
    public void actualizarResultados() {
        cargarResultados();
    }
}