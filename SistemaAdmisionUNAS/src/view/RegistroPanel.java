package view;

import model.Postulante;
import dao.PostulanteDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.text.SimpleDateFormat;
import java.io.File;
import util.EventBus;
import util.ExcelUtils;

/**
 * Panel para mostrar lista de postulantes registrados
 * @author joe-696
 */
public class RegistroPanel extends JPanel {
    
    private JTable tablaPostulantes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> cmbFiltroEstado, cmbFiltroModalidad;
    private JLabel lblTotalPostulantes, lblPostulantesDirectos, lblAlumnosLibres;
    private JButton btnActualizar, btnEliminar, btnExportar, btnImportar;
    private PostulanteDAO postulanteDAO;
    private List<Postulante> listaPostulantes;
    
    public RegistroPanel() {
        this.postulanteDAO = new PostulanteDAO();
        initComponents();
        setupEventHandlers();
        
        // 🔄 SUSCRIBIRSE A EVENTOS PARA SINCRONIZACIÓN
        suscribirseAEventos();
        
        // Cargar datos al inicializar
        actualizarTabla();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel superior con título y estadísticas
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Panel central con tabla
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Panel inferior con botones
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("📋 LISTA DE POSTULANTES REGISTRADOS");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titulo.setForeground(new Color(52, 152, 219));
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
        
        // Crear tarjetas de estadísticas
        lblTotalPostulantes = createStatCard("Total Postulantes", "0", new Color(52, 152, 219));
        lblPostulantesDirectos = createStatCard("Postulantes Directos", "0", new Color(46, 204, 113));
        lblAlumnosLibres = createStatCard("Alumnos Libres", "0", new Color(241, 196, 15));
        
        panel.add(lblTotalPostulantes);
        panel.add(lblPostulantesDirectos);
        panel.add(lblAlumnosLibres);
        
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
        
        // Envolver en JLabel para que sea compatible
        JLabel container = new JLabel();
        container.setLayout(new BorderLayout());
        container.add(card);
        container.setOpaque(false);
        
        return container;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Filtros y Búsqueda"));
        
        // Campo de búsqueda
        panel.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(20);
        txtBuscar.setToolTipText("Buscar por código, nombres o DNI");
        panel.add(txtBuscar);
        
        // Filtro por modalidad
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Modalidad:"));
        cmbFiltroModalidad = new JComboBox<>(new String[]{
            "Todas", "ORDINARIO", "EXONERADO", "BECA 18", "DEPORTISTA CALIFICADO"
        });
        panel.add(cmbFiltroModalidad);
        
        // Filtro por estado académico
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>(new String[]{
            "Todos", "POSTULANTE", "ALUMNO_LIBRE"
        });
        panel.add(cmbFiltroEstado);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Crear modelo de tabla
        String[] columnas = {
            "Código", "Apellidos y Nombres", "DNI", "Primera Opción", "Segunda Opción",
            "Modalidad", "Estado Académico", "Sexo", "Fecha Inscripción"
        };
        
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        
        tablaPostulantes = new JTable(modeloTabla);
        configurarTabla();
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(tablaPostulantes);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void configurarTabla() {
        // Configuración básica
        tablaPostulantes.setRowHeight(25);
        tablaPostulantes.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        tablaPostulantes.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        tablaPostulantes.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaPostulantes.getTableHeader().setForeground(Color.WHITE);
        tablaPostulantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPostulantes.setGridColor(new Color(230, 230, 230));
        tablaPostulantes.setShowGrid(true);
        
        // Configurar ancho de columnas
        int[] anchosColumnas = {80, 250, 80, 200, 200, 120, 130, 60, 120};
        for (int i = 0; i < anchosColumnas.length && i < tablaPostulantes.getColumnCount(); i++) {
            tablaPostulantes.getColumnModel().getColumn(i).setPreferredWidth(anchosColumnas[i]);
        }
        
        // Renderer personalizado para estado académico
        tablaPostulantes.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value != null) {
                    String estado = value.toString();
                    if ("POSTULANTE".equals(estado)) {
                        setBackground(isSelected ? table.getSelectionBackground() : new Color(212, 237, 218));
                        setForeground(isSelected ? table.getSelectionForeground() : new Color(21, 87, 36));
                        setText("✅ " + estado);
                    } else if ("ALUMNO_LIBRE".equals(estado)) {
                        setBackground(isSelected ? table.getSelectionBackground() : new Color(255, 243, 205));
                        setForeground(isSelected ? table.getSelectionForeground() : new Color(133, 100, 4));
                        setText("⚠️ " + estado);
                    }
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                
                return this;
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setBackground(new Color(52, 152, 219));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setPreferredSize(new Dimension(120, 35));
        
        btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.setBackground(new Color(231, 76, 60));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(120, 35));
        
        btnExportar = new JButton("📊 Exportar");
        btnExportar.setBackground(new Color(46, 204, 113));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setPreferredSize(new Dimension(120, 35));
        
        // Botón para importar datos
        btnImportar = new JButton("📥 Importar");
        btnImportar.setBackground(new Color(155, 89, 182));
        btnImportar.setForeground(Color.WHITE);
        btnImportar.setPreferredSize(new Dimension(120, 35));
        btnImportar.setToolTipText("Importar postulantes desde Excel/CSV");
        
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnExportar);
        panel.add(btnImportar);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Actualizar tabla
        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarTabla();
            }
        });
        
        // Eliminar postulante
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarPostulante();
            }
        });
        
        // Exportar datos
        btnExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarDatos();
            }
        });
        
        // Importar datos
        btnImportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                importarDatos();
            }
        });
        
        // Búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filtrarTabla();
            }
        });
        
        // Filtros
        cmbFiltroModalidad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtrarTabla();
            }
        });
        
        cmbFiltroEstado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filtrarTabla();
            }
        });
        
        // Doble clic para ver detalles
        tablaPostulantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetallesPostulante();
                }
            }
        });
    }
    
    public void actualizarTabla() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cargar datos
                listaPostulantes = postulanteDAO.obtenerTodos();
                
                // Limpiar tabla
                modeloTabla.setRowCount(0);
                
                // Llenar tabla
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                for (Postulante p : listaPostulantes) {
                    Object[] fila = {
                        p.getCodigo(),
                        p.getApellidosNombres(),
                        p.getDni(),
                        p.getOpcion1(),
                        p.getOpcion2(),
                        p.getModalidad(),
                        p.getEstadoAcademico(),
                        p.getSexo(),
                        p.getInscripcion() != null ? sdf.format(p.getInscripcion()) : ""
                    };
                    modeloTabla.addRow(fila);
                }
                
                // Actualizar estadísticas
                actualizarEstadisticas();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error cargando datos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void actualizarEstadisticas() {
        if (listaPostulantes == null) return;
        
        int total = listaPostulantes.size();
        int postulantesDirectos = 0;
        int alumnosLibres = 0;
        
        for (Postulante p : listaPostulantes) {
            if ("POSTULANTE".equals(p.getEstadoAcademico())) {
                postulantesDirectos++;
            } else if ("ALUMNO_LIBRE".equals(p.getEstadoAcademico())) {
                alumnosLibres++;
            }
        }
        
        // Actualizar las tarjetas
        actualizarStatCard(lblTotalPostulantes, String.valueOf(total));
        actualizarStatCard(lblPostulantesDirectos, String.valueOf(postulantesDirectos));
        actualizarStatCard(lblAlumnosLibres, String.valueOf(alumnosLibres));
    }
    
    private void actualizarStatCard(JLabel card, String valor) {
        try {
            // Buscar el JLabel con el valor dentro del contenedor
            JPanel panel = (JPanel) card.getComponent(0);
            JLabel lblValor = (JLabel) panel.getComponent(2);
            lblValor.setText(valor);
        } catch (Exception e) {
            System.err.println("Error actualizando estadística: " + e.getMessage());
        }
    }
    
    private void filtrarTabla() {
        // Implementación básica de filtrado
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        String modalidadSeleccionada = (String) cmbFiltroModalidad.getSelectedItem();
        String estadoSeleccionado = (String) cmbFiltroEstado.getSelectedItem();
        
        if (listaPostulantes == null) return;
        
        // Limpiar tabla
        modeloTabla.setRowCount(0);
        
        // Aplicar filtros
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Postulante p : listaPostulantes) {
            boolean cumpleFiltroTexto = textoBusqueda.isEmpty() ||
                p.getCodigo().toLowerCase().contains(textoBusqueda) ||
                p.getApellidosNombres().toLowerCase().contains(textoBusqueda) ||
                p.getDni().toLowerCase().contains(textoBusqueda);
            
            boolean cumpleFiltroModalidad = "Todas".equals(modalidadSeleccionada) ||
                modalidadSeleccionada.equals(p.getModalidad());
            
            boolean cumpleFiltroEstado = "Todos".equals(estadoSeleccionado) ||
                estadoSeleccionado.equals(p.getEstadoAcademico());
            
            if (cumpleFiltroTexto && cumpleFiltroModalidad && cumpleFiltroEstado) {
                Object[] fila = {
                    p.getCodigo(),
                    p.getApellidosNombres(),
                    p.getDni(),
                    p.getOpcion1(),
                    p.getOpcion2(),
                    p.getModalidad(),
                    p.getEstadoAcademico(),
                    p.getSexo(),
                    p.getInscripcion() != null ? sdf.format(p.getInscripcion()) : ""
                };
                modeloTabla.addRow(fila);
            }
        }
    }
    
    private void eliminarPostulante() {
        int filaSeleccionada = tablaPostulantes.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "⚠️ Seleccione un postulante para eliminar",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al postulante?\n\n" +
            "Código: " + codigo + "\n" +
            "Nombre: " + nombre + "\n\n" +
            "⚠️ Esta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                if (postulanteDAO.eliminar(codigo)) {
                    JOptionPane.showMessageDialog(this,
                        "✅ Postulante eliminado exitosamente",
                        "Eliminación Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    actualizarTabla();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "❌ Error al eliminar el postulante",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "❌ Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportarDatos() {
        if (listaPostulantes == null || listaPostulantes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "⚠️ No hay datos para exportar",
                "Sin Datos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "📊 Función de exportación será implementada próximamente",
            "Exportar Datos",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void verDetallesPostulante() {
        int filaSeleccionada = tablaPostulantes.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        String codigo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Postulante postulante = postulanteDAO.buscarPorCodigo(codigo);
        
        if (postulante != null) {
            String detalles = String.format("""
                📋 DETALLES DEL POSTULANTE
                ========================
                
                Código: %s
                Apellidos y Nombres: %s
                DNI: %s
                Sexo: %s
                
                OPCIONES DE CARRERA:
                Primera Opción: %s
                Segunda Opción: %s
                Modalidad: %s
                
                ESTADO ACADÉMICO: %s
                
                CONTACTO:
                Teléfono: %s
                Dirección: %s
                """,
                postulante.getCodigo(),
                postulante.getApellidosNombres(),
                postulante.getDni(),
                postulante.getSexo(),
                postulante.getOpcion1(),
                postulante.getOpcion2(),
                postulante.getModalidad(),
                postulante.getEstadoAcademico(),
                postulante.getTelCelular() != null ? postulante.getTelCelular() : "No especificado",
                postulante.getDireccion() != null ? postulante.getDireccion() : "No especificada"
            );
            
            JTextArea textArea = new JTextArea(detalles);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Detalles del Postulante", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Importar datos desde archivo Excel/CSV
     */
    private void importarDatos() {
        // Configurar selector de archivos
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo para importar postulantes");
        
        // Filtros de archivo
        FileNameExtensionFilter filtroCSV = new FileNameExtensionFilter(
            "Archivos CSV/TXT (*.csv, *.txt)", "csv", "txt");
        FileNameExtensionFilter filtroExcel = new FileNameExtensionFilter(
            "Archivos Excel (*.xlsx, *.xls)", "xlsx", "xls");
        FileNameExtensionFilter filtroTodos = new FileNameExtensionFilter(
            "Todos los archivos soportados", "csv", "txt", "xlsx", "xls");
        
        fileChooser.addChoosableFileFilter(filtroTodos);
        fileChooser.addChoosableFileFilter(filtroCSV);
        fileChooser.addChoosableFileFilter(filtroExcel);
        fileChooser.setFileFilter(filtroTodos);
        
        // Mostrar selector
        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            
            // Confirmar importación
            int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de importar datos desde:\n" + archivoSeleccionado.getName() + "?\n\n" +
                "Esta acción agregará nuevos postulantes a la base de datos.",
                "Confirmar Importación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                realizarImportacion(archivoSeleccionado);
            }
        }
    }
    
    /**
     * Realizar la importación del archivo seleccionado
     */
    private void realizarImportacion(File archivo) {
        // Crear dialog de progreso
        JDialog dialogProgreso = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Importando datos...", true);
        dialogProgreso.setSize(400, 150);
        dialogProgreso.setLocationRelativeTo(this);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Procesando archivo...");
        progressBar.setStringPainted(true);
        
        JLabel lblEstado = new JLabel("Iniciando importación...", SwingConstants.CENTER);
        
        JPanel panelProgreso = new JPanel(new BorderLayout());
        panelProgreso.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelProgreso.add(lblEstado, BorderLayout.NORTH);
        panelProgreso.add(progressBar, BorderLayout.CENTER);
        
        dialogProgreso.add(panelProgreso);
        
        // Realizar importación en hilo separado
        SwingWorker<String, String> worker = new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                publish("Leyendo archivo...");
                
                // Importar usando ExcelUtils
                List<Postulante> postulantesImportados = ExcelUtils.importarPostulantesDesdeExcel(archivo.getAbsolutePath());
                
                if (postulantesImportados.isEmpty()) {
                    return "No se encontraron datos válidos en el archivo.";
                }
                
                publish("Guardando en base de datos...");
                
                // Guardar postulantes en la base de datos
                int exitosos = 0;
                int errores = 0;
                
                for (Postulante postulante : postulantesImportados) {
                    try {
                        if (postulanteDAO.guardar(postulante)) {
                            exitosos++;
                        } else {
                            errores++;
                        }
                    } catch (Exception e) {
                        errores++;
                        System.err.println("Error guardando postulante " + postulante.getCodigo() + ": " + e.getMessage());
                    }
                }
                
                return String.format("Importación completada:\n✅ %d postulantes importados exitosamente\n❌ %d errores", 
                    exitosos, errores);
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    lblEstado.setText(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                dialogProgreso.dispose();
                
                try {
                    String resultado = get();
                    
                    // Mostrar resultado
                    JOptionPane.showMessageDialog(RegistroPanel.this,
                        resultado,
                        "Resultado de Importación",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Actualizar tabla
                    actualizarTabla();
                    
                    // Notificar otros paneles
                    EventBus.getInstance().notificarPostulante("POSTULANTES_IMPORTADOS", null);
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RegistroPanel.this,
                        "Error durante la importación:\n" + e.getMessage(),
                        "Error de Importación",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
        dialogProgreso.setVisible(true);
    }
    
    /**
     * Suscribirse a eventos para sincronización automática
     */
    private void suscribirseAEventos() {
        // Escuchar eventos de postulantes
        EventBus.getInstance().suscribirPostulantes(event -> {
            SwingUtilities.invokeLater(() -> {
                switch (event.getTipo()) {
                    case "POSTULANTE_AGREGADO":
                        actualizarTabla();
                        System.out.println("🔄 Tabla actualizada: nuevo postulante agregado");
                        break;
                    case "POSTULANTES_IMPORTADOS":
                        actualizarTabla();
                        System.out.println("🔄 Tabla actualizada: " + event.getCantidad() + " postulantes importados");
                        break;
                }
            });
        });
    }
}