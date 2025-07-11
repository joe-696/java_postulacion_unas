package view;

import model.Carrera;
import model.ExamenConfig;
import dao.PostulanteDAO;
import util.DatabaseConnection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 * Panel de administración del sistema de admisión
 * @author joe-696
 */
public class AdministracionPanel extends JPanel {
    
    // Componentes para configuración de carreras
    private JTable tablaCarreras;
    private DefaultTableModel modeloCarreras;
    private JTextField txtNombreCarrera, txtVacantes, txtCurva;
    private JComboBox<String> cmbTipoExamen;
    private JButton btnAgregarCarrera, btnEliminarCarrera, btnGuardarVacantes;
    
    // Componentes para configuración de examen
    private JSpinner spnNumAsignaturas, spnTiposExamen;
    private JTable tablaAsignaturas;
    private DefaultTableModel modeloAsignaturas;
    private JTextField txtNombreAsignatura, txtNumPreguntas;
    private JComboBox<String> cmbTipoExamenAsignatura;
    private JButton btnAgregarAsignatura, btnEliminarAsignatura, btnGuardarExamen;
    
    // Componentes para respuestas correctas
    private JTextArea txtRespuestasIngenieria, txtRespuestasFCA, txtRespuestasGeneral;
    private JButton btnGuardarRespuestas;
    
    // Configuración global
    private ExamenConfig configuracionExamen;
    private Map<String, Carrera> mapaCarreras;
    
    public AdministracionPanel() {
        try {
            this.configuracionExamen = new ExamenConfig();
            this.mapaCarreras = new HashMap<>();
            initComponents();
            cargarDatosIniciales();
            System.out.println("✅ AdministracionPanel inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando AdministracionPanel: " + e.getMessage());
            e.printStackTrace();
            initPanelError();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título principal
        JLabel titulo = new JLabel("⚙️ ADMINISTRACIÓN DEL SISTEMA DE ADMISIÓN");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titulo.setForeground(new Color(241, 196, 15));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel principal con pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // Pestaña 1: Configuración de Carreras y Vacantes
        tabbedPane.addTab("🎓 Carreras y Vacantes", createCarrerasPanel());
        
        // Pestaña 2: Configuración de Examen
        tabbedPane.addTab("📝 Configuración de Examen", createExamenPanel());
        
        // Pestaña 3: Respuestas Correctas
        tabbedPane.addTab("✅ Respuestas Correctas", createRespuestasPanel());
        
        // Pestaña 4: Curvas y Criterios
        tabbedPane.addTab("📈 Curvas y Criterios", createCurvasPanel());
        
        add(titulo, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private void initPanelError() {
        setLayout(new BorderLayout());
        
        JLabel lblError = new JLabel("<html><center>" +
            "⚙️ PANEL DE ADMINISTRACIÓN<br><br>" +
            "❌ Error de inicialización<br>" +
            "Verifique la configuración del sistema</center></html>");
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        add(lblError, BorderLayout.CENTER);
    }
    
    private JPanel createCarrerasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Panel superior: Formulario para agregar carreras
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("➕ Agregar/Editar Carrera"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1: Nombre de carrera
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre de Carrera:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtNombreCarrera = new JTextField(30);
        formPanel.add(txtNombreCarrera, gbc);
        
        // Fila 2: Vacantes y Tipo de Examen
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Vacantes:"), gbc);
        gbc.gridx = 1;
        txtVacantes = new JTextField(10);
        formPanel.add(txtVacantes, gbc);
        
        gbc.gridx = 2;
        formPanel.add(new JLabel("Tipo de Examen:"), gbc);
        gbc.gridx = 3;
        cmbTipoExamen = new JComboBox<>(new String[]{
            "GENERAL", "INGENIERIA", "FCA", "MEDICINA"
        });
        formPanel.add(cmbTipoExamen, gbc);
        
        // Fila 3: Curva y botones
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Curva (puntos):"), gbc);
        gbc.gridx = 1;
        txtCurva = new JTextField(10);
        txtCurva.setText("2.0");
        formPanel.add(txtCurva, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JPanel buttonFormPanel = new JPanel(new FlowLayout());
        
        btnAgregarCarrera = new JButton("➕ Agregar");
        btnAgregarCarrera.setBackground(new Color(46, 204, 113));
        btnAgregarCarrera.setForeground(Color.WHITE);
        btnAgregarCarrera.addActionListener(e -> agregarCarrera());
        
        btnEliminarCarrera = new JButton("🗑️ Eliminar");
        btnEliminarCarrera.setBackground(new Color(231, 76, 60));
        btnEliminarCarrera.setForeground(Color.WHITE);
        btnEliminarCarrera.addActionListener(e -> eliminarCarrera());
        
        buttonFormPanel.add(btnAgregarCarrera);
        buttonFormPanel.add(btnEliminarCarrera);
        formPanel.add(buttonFormPanel, gbc);
        
        // Panel central: Tabla de carreras
        String[] columnasCarreras = {
            "Carrera", "Vacantes", "Tipo Examen", "Curva", "Estado"
        };
        modeloCarreras = new DefaultTableModel(columnasCarreras, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3; // Solo vacantes y curva editables
            }
        };
        
        tablaCarreras = new JTable(modeloCarreras);
        tablaCarreras.setRowHeight(25);
        tablaCarreras.getTableHeader().setBackground(new Color(241, 196, 15));
        tablaCarreras.getTableHeader().setForeground(Color.WHITE);
        tablaCarreras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Event handler para selección de fila
        tablaCarreras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarCarreraSeleccionada();
            }
        });
        
        JScrollPane scrollCarreras = new JScrollPane(tablaCarreras);
        scrollCarreras.setPreferredSize(new Dimension(0, 300));
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollCarreras, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createExamenPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Panel superior: Configuración general
        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(BorderFactory.createTitledBorder("⚙️ Configuración General del Examen"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        configPanel.add(new JLabel("Número de Asignaturas:"), gbc);
        gbc.gridx = 1;
        spnNumAsignaturas = new JSpinner(new SpinnerNumberModel(10, 1, 20, 1));
        spnNumAsignaturas.setPreferredSize(new Dimension(80, 25));
        configPanel.add(spnNumAsignaturas, gbc);
        
        gbc.gridx = 2;
        configPanel.add(new JLabel("Tipos de Examen:"), gbc);
        gbc.gridx = 3;
        spnTiposExamen = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        spnTiposExamen.setPreferredSize(new Dimension(80, 25));
        configPanel.add(spnTiposExamen, gbc);
        
        // Panel medio: Formulario para asignaturas
        JPanel formAsigPanel = new JPanel(new GridBagLayout());
        formAsigPanel.setBorder(BorderFactory.createTitledBorder("📚 Configurar Asignaturas"));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formAsigPanel.add(new JLabel("Nombre Asignatura:"), gbc);
        gbc.gridx = 1;
        txtNombreAsignatura = new JTextField(20);
        formAsigPanel.add(txtNombreAsignatura, gbc);
        
        gbc.gridx = 2;
        formAsigPanel.add(new JLabel("N° Preguntas:"), gbc);
        gbc.gridx = 3;
        txtNumPreguntas = new JTextField(5);
        formAsigPanel.add(txtNumPreguntas, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formAsigPanel.add(new JLabel("Tipo Examen:"), gbc);
        gbc.gridx = 1;
        cmbTipoExamenAsignatura = new JComboBox<>(new String[]{
            "GENERAL", "INGENIERIA", "FCA", "MEDICINA"
        });
        formAsigPanel.add(cmbTipoExamenAsignatura, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JPanel buttonAsigPanel = new JPanel(new FlowLayout());
        
        btnAgregarAsignatura = new JButton("➕ Agregar");
        btnAgregarAsignatura.setBackground(new Color(52, 152, 219));
        btnAgregarAsignatura.setForeground(Color.WHITE);
        btnAgregarAsignatura.addActionListener(e -> agregarAsignatura());
        
        btnEliminarAsignatura = new JButton("🗑️ Eliminar");
        btnEliminarAsignatura.setBackground(new Color(231, 76, 60));
        btnEliminarAsignatura.setForeground(Color.WHITE);
        btnEliminarAsignatura.addActionListener(e -> eliminarAsignatura());
        
        buttonAsigPanel.add(btnAgregarAsignatura);
        buttonAsigPanel.add(btnEliminarAsignatura);
        formAsigPanel.add(buttonAsigPanel, gbc);
        
        // Tabla de asignaturas
        String[] columnasAsig = {
            "Asignatura", "N° Preguntas", "Tipo Examen", "Estado"
        };
        modeloAsignaturas = new DefaultTableModel(columnasAsig, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo número de preguntas editable
            }
        };
        
        tablaAsignaturas = new JTable(modeloAsignaturas);
        tablaAsignaturas.setRowHeight(25);
        tablaAsignaturas.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaAsignaturas.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollAsig = new JScrollPane(tablaAsignaturas);
        scrollAsig.setPreferredSize(new Dimension(0, 200));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(configPanel, BorderLayout.NORTH);
        topPanel.add(formAsigPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollAsig, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRespuestasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JLabel info = new JLabel("<html><b>Instrucciones:</b> Ingrese las respuestas correctas separadas por comas.<br>" +
                                "Ejemplo: a,b,c,d,a,b,c,d,a,b (para 10 preguntas)</html>");
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.setBackground(new Color(240, 248, 255));
        info.setOpaque(true);
        
        // Panel principal con tres secciones
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        // Respuestas para Ingeniería
        JPanel panelIng = new JPanel(new BorderLayout());
        panelIng.setBorder(BorderFactory.createTitledBorder("🔧 Respuestas Examen de INGENIERÍA"));
        
        txtRespuestasIngenieria = new JTextArea(4, 0);
        txtRespuestasIngenieria.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtRespuestasIngenieria.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtRespuestasIngenieria.setText("a,b,c,d,a,b,c,d,a,b,c,d,a,b,c,d,a,b,c,d");
        
        JScrollPane scrollIng = new JScrollPane(txtRespuestasIngenieria);
        panelIng.add(scrollIng, BorderLayout.CENTER);
        
        // Respuestas para FCA
        JPanel panelFCA = new JPanel(new BorderLayout());
        panelFCA.setBorder(BorderFactory.createTitledBorder("💼 Respuestas Examen de FCA"));
        
        txtRespuestasFCA = new JTextArea(4, 0);
        txtRespuestasFCA.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtRespuestasFCA.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtRespuestasFCA.setText("b,a,d,c,b,a,d,c,b,a,d,c,b,a,d,c,b,a,d,c");
        
        JScrollPane scrollFCA = new JScrollPane(txtRespuestasFCA);
        panelFCA.add(scrollFCA, BorderLayout.CENTER);
        
        // Respuestas para General/Medicina
        JPanel panelGen = new JPanel(new BorderLayout());
        panelGen.setBorder(BorderFactory.createTitledBorder("🏥 Respuestas Examen GENERAL/MEDICINA"));
        
        txtRespuestasGeneral = new JTextArea(4, 0);
        txtRespuestasGeneral.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtRespuestasGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        txtRespuestasGeneral.setText("c,d,a,b,c,d,a,b,c,d,a,b,c,d,a,b,c,d,a,b");
        
        JScrollPane scrollGen = new JScrollPane(txtRespuestasGeneral);
        panelGen.add(scrollGen, BorderLayout.CENTER);
        
        mainPanel.add(panelIng);
        mainPanel.add(panelFCA);
        mainPanel.add(panelGen);
        
        panel.add(info, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCurvasPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        // Panel de información
        JTextArea infoArea = new JTextArea(
            "CRITERIOS DE DESEMPATE Y CURVAS:\n\n" +
            "1. NOTA MÍNIMA PARA INGRESO: 11.0 puntos\n\n" +
            "2. CRITERIOS DE DESEMPATE (en orden):\n" +
            "   • Mayor puntaje final\n" +
            "   • Menor edad (más joven)\n" +
            "   • Fecha de inscripción más temprana\n\n" +
            "3. APLICACIÓN DE CURVAS:\n" +
            "   • Solo se aplican cuando no se llenan las vacantes\n" +
            "   • Cada carrera puede tener su propia curva\n" +
            "   • Los puntos de curva se suman al puntaje final\n\n" +
            "4. PRIORIDAD DE INGRESO:\n" +
            "   • Primero: POSTULANTES (≥ 6 meses en 5to secundaria)\n" +
            "   • Segundo: ALUMNOS LIBRES (solo si sobran vacantes)\n\n" +
            "5. SISTEMA DE OPCIONES:\n" +
            "   • Primera opción: prioridad máxima\n" +
            "   • Segunda opción: solo si no ingresa por primera"
        );
        
        infoArea.setEditable(false);
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        infoArea.setBackground(new Color(248, 249, 250));
        infoArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollInfo = new JScrollPane(infoArea);
        scrollInfo.setBorder(BorderFactory.createTitledBorder("📋 Información de Criterios"));
        
        panel.add(scrollInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnGuardarVacantes = new JButton("💾 Guardar Configuración de Carreras");
        btnGuardarVacantes.setBackground(new Color(46, 204, 113));
        btnGuardarVacantes.setForeground(Color.WHITE);
        btnGuardarVacantes.setPreferredSize(new Dimension(250, 35));
        btnGuardarVacantes.addActionListener(e -> guardarConfiguracionCarreras());
        
        btnGuardarExamen = new JButton("📝 Guardar Configuración de Examen");
        btnGuardarExamen.setBackground(new Color(52, 152, 219));
        btnGuardarExamen.setForeground(Color.WHITE);
        btnGuardarExamen.setPreferredSize(new Dimension(250, 35));
        btnGuardarExamen.addActionListener(e -> guardarConfiguracionExamen());
        
        btnGuardarRespuestas = new JButton("✅ Guardar Respuestas Correctas");
        btnGuardarRespuestas.setBackground(new Color(241, 196, 15));
        btnGuardarRespuestas.setForeground(Color.WHITE);
        btnGuardarRespuestas.setPreferredSize(new Dimension(220, 35));
        btnGuardarRespuestas.addActionListener(e -> guardarRespuestasCorrectas());
        
        panel.add(btnGuardarVacantes);
        panel.add(btnGuardarExamen);
        panel.add(btnGuardarRespuestas);
        
        return panel;
    }
    
    private void cargarDatosIniciales() {
        cargarCarrerasExistentes();
        cargarAsignaturasExistentes();
    }
    
    private void cargarCarrerasExistentes() {
        // Carreras predefinidas según UNAS
        String[][] carrerasIniciales = {
            {"INGENIERÍA DE SISTEMAS E INFORMÁTICA", "30", "INGENIERIA", "2.0"},
            {"INGENIERÍA CIVIL", "25", "INGENIERIA", "2.0"},
            {"INGENIERÍA INDUSTRIAL", "25", "INGENIERIA", "2.0"},
            {"ARQUITECTURA", "15", "INGENIERIA", "2.0"},
            {"ADMINISTRACIÓN", "40", "FCA", "1.5"},
            {"CONTABILIDAD", "35", "FCA", "1.5"},
            {"ECONOMÍA", "30", "FCA", "1.5"},
            {"DERECHO Y CIENCIAS POLÍTICAS", "35", "GENERAL", "2.0"},
            {"MEDICINA HUMANA", "20", "MEDICINA", "1.0"},
            {"ENFERMERÍA", "25", "MEDICINA", "1.0"},
            {"OBSTETRICIA", "20", "MEDICINA", "1.0"},
            {"PSICOLOGÍA", "25", "GENERAL", "2.0"},
            {"EDUCACIÓN INICIAL", "30", "GENERAL", "2.0"},
            {"EDUCACIÓN PRIMARIA", "35", "GENERAL", "2.0"}
        };
        
        modeloCarreras.setRowCount(0);
        for (String[] carrera : carrerasIniciales) {
            modeloCarreras.addRow(new Object[]{
                carrera[0], carrera[1], carrera[2], carrera[3], "Activa"
            });
            
            // Agregar al mapa
            Carrera nuevaCarrera = new Carrera("CAR" + (mapaCarreras.size() + 1), 
                carrera[0], "FACULTAD", Integer.parseInt(carrera[1]));
            nuevaCarrera.setTipoExamen(carrera[2]);
            nuevaCarrera.setCurvaAplicada(Double.parseDouble(carrera[3]));
            mapaCarreras.put(carrera[0], nuevaCarrera);
        }
    }
    
    private void cargarAsignaturasExistentes() {
        // Asignaturas predefinidas
        String[][] asignaturasIniciales = {
            {"APTITUD VERBAL", "10", "GENERAL"},
            {"APTITUD MATEMÁTICA", "10", "GENERAL"},
            {"COMUNICACIÓN", "8", "GENERAL"},
            {"MATEMÁTICA", "12", "INGENIERIA"},
            {"FÍSICA", "10", "INGENIERIA"},
            {"QUÍMICA", "8", "INGENIERIA"},
            {"HISTORIA DEL PERÚ", "6", "GENERAL"},
            {"GEOGRAFÍA", "6", "GENERAL"},
            {"FILOSOFÍA", "4", "FCA"},
            {"ECONOMÍA BÁSICA", "8", "FCA"},
            {"BIOLOGÍA", "10", "MEDICINA"},
            {"ANATOMÍA", "8", "MEDICINA"}
        };
        
        modeloAsignaturas.setRowCount(0);
        for (String[] asignatura : asignaturasIniciales) {
            modeloAsignaturas.addRow(new Object[]{
                asignatura[0], asignatura[1], asignatura[2], "Activa"
            });
        }
    }
    
    private void agregarCarrera() {
        String nombre = txtNombreCarrera.getText().trim();
        String vacantesStr = txtVacantes.getText().trim();
        String tipoExamen = (String) cmbTipoExamen.getSelectedItem();
        String curvaStr = txtCurva.getText().trim();
        
        if (nombre.isEmpty() || vacantesStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Complete todos los campos obligatorios",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int vacantes = Integer.parseInt(vacantesStr);
            double curva = Double.parseDouble(curvaStr);
            
            // Verificar duplicados
            for (int i = 0; i < modeloCarreras.getRowCount(); i++) {
                if (nombre.equals(modeloCarreras.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(this,
                        "La carrera ya existe",
                        "Duplicado",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Agregar a la tabla
            modeloCarreras.addRow(new Object[]{
                nombre, vacantes, tipoExamen, curva, "Activa"
            });
            
            // Agregar al mapa
            Carrera nuevaCarrera = new Carrera("CAR" + (mapaCarreras.size() + 1), 
                nombre, "FACULTAD", vacantes);
            nuevaCarrera.setTipoExamen(tipoExamen);
            nuevaCarrera.setCurvaAplicada(curva);
            mapaCarreras.put(nombre, nuevaCarrera);
            
            // Limpiar formulario
            txtNombreCarrera.setText("");
            txtVacantes.setText("");
            txtCurva.setText("2.0");
            
            JOptionPane.showMessageDialog(this,
                "Carrera agregada exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Vacantes y curva deben ser números válidos",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCarrera() {
        int filaSeleccionada = tablaCarreras.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una carrera para eliminar",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nombreCarrera = (String) modeloCarreras.getValueAt(filaSeleccionada, 0);
        
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la carrera:\n" + nombreCarrera + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            modeloCarreras.removeRow(filaSeleccionada);
            mapaCarreras.remove(nombreCarrera);
            
            JOptionPane.showMessageDialog(this,
                "Carrera eliminada exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cargarCarreraSeleccionada() {
        int filaSeleccionada = tablaCarreras.getSelectedRow();
        if (filaSeleccionada != -1) {
            txtNombreCarrera.setText((String) modeloCarreras.getValueAt(filaSeleccionada, 0));
            txtVacantes.setText(modeloCarreras.getValueAt(filaSeleccionada, 1).toString());
            cmbTipoExamen.setSelectedItem(modeloCarreras.getValueAt(filaSeleccionada, 2));
            txtCurva.setText(modeloCarreras.getValueAt(filaSeleccionada, 3).toString());
        }
    }
    
    private void agregarAsignatura() {
        String nombre = txtNombreAsignatura.getText().trim();
        String preguntasStr = txtNumPreguntas.getText().trim();
        String tipoExamen = (String) cmbTipoExamenAsignatura.getSelectedItem();
        
        if (nombre.isEmpty() || preguntasStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Complete todos los campos",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int preguntas = Integer.parseInt(preguntasStr);
            
            modeloAsignaturas.addRow(new Object[]{
                nombre, preguntas, tipoExamen, "Activa"
            });
            
            txtNombreAsignatura.setText("");
            txtNumPreguntas.setText("");
            
            JOptionPane.showMessageDialog(this,
                "Asignatura agregada exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "El número de preguntas debe ser válido",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarAsignatura() {
        int filaSeleccionada = tablaAsignaturas.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione una asignatura para eliminar",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        modeloAsignaturas.removeRow(filaSeleccionada);
        JOptionPane.showMessageDialog(this,
            "Asignatura eliminada",
            "Éxito",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarConfiguracionCarreras() {
        JOptionPane.showMessageDialog(this,
            "✅ Configuración de carreras guardada en memoria.\n\n" +
            "Características guardadas:\n" +
            "• " + modeloCarreras.getRowCount() + " carreras configuradas\n" +
            "• Vacantes y curvas personalizadas\n" +
            "• Tipos de examen asignados\n\n" +
            "Nota: En un sistema real, esto se guardaría en base de datos.",
            "Configuración Guardada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarConfiguracionExamen() {
        JOptionPane.showMessageDialog(this,
            "✅ Configuración de examen guardada en memoria.\n\n" +
            "Configuración actual:\n" +
            "• " + spnNumAsignaturas.getValue() + " asignaturas configuradas\n" +
            "• " + spnTiposExamen.getValue() + " tipos de examen\n" +
            "• " + modeloAsignaturas.getRowCount() + " materias activas\n\n" +
            "Nota: En un sistema real, esto se guardaría en base de datos.",
            "Configuración Guardada",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void guardarRespuestasCorrectas() {
        String respuestasIng = txtRespuestasIngenieria.getText().trim();
        String respuestasFCA = txtRespuestasFCA.getText().trim();
        String respuestasGen = txtRespuestasGeneral.getText().trim();
        
        if (respuestasIng.isEmpty() || respuestasFCA.isEmpty() || respuestasGen.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Complete todas las respuestas correctas",
                "Validación",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this,
            "✅ Respuestas correctas guardadas en memoria.\n\n" +
            "Respuestas configuradas:\n" +
            "• INGENIERÍA: " + respuestasIng.split(",").length + " respuestas\n" +
            "• FCA: " + respuestasFCA.split(",").length + " respuestas\n" +
            "• GENERAL/MEDICINA: " + respuestasGen.split(",").length + " respuestas\n\n" +
            "Estas respuestas se usarán para el cálculo automático de puntajes.",
            "Respuestas Guardadas",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Getters para acceso desde otras clases
    public Map<String, Carrera> getMapaCarreras() {
        return mapaCarreras;
    }
    
    public ExamenConfig getConfiguracionExamen() {
        return configuracionExamen;
    }
}