package view;

// Imports específicos del proyecto
import model.Postulante;
import dao.PostulanteDAO;
import dao.CarreraDAO;
import util.EventBus;

// Imports de Java/Swing
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Panel de inscripción de postulantes
 * @author joe-696
 */
public class InscripcionPanel extends JPanel {
    
    // Componentes de la interfaz
    private JTextField txtCodigo, txtApellidos, txtDni, txtTelefono, txtDireccion, txtColegio;
    private JComboBox<String> cmbOpcion1, cmbOpcion2, cmbModalidad, cmbSexo, cmbEstadoAcademico;
    private JSpinner spnNotaAC, spnNotaCO;
    private JFormattedTextField txtFechaNac;
    private JButton btnGuardar, btnLimpiar, btnGenerar;
    private JTextArea txtAreaInfo;
    
    // DAO para persistencia
    private PostulanteDAO postulanteDAO;
    private CarreraDAO carreraDAO;
    
    public InscripcionPanel() {
        try {
            this.postulanteDAO = new PostulanteDAO();
            this.carreraDAO = new CarreraDAO();
            initComponents();
            
            // 🔄 SUSCRIBIRSE A EVENTOS DE CARRERAS
            suscribirseAEventos();
            
            System.out.println("✅ InscripcionPanel inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando InscripcionPanel: " + e.getMessage());
            e.printStackTrace();
            initPanelError();
        }
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel principal con scroll
        JScrollPane scrollPane = new JScrollPane(createMainPanel());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void initPanelError() {
        setLayout(new BorderLayout());
        
        JLabel lblError = new JLabel("<html><center>" +
            "📝 PANEL DE INSCRIPCIÓN<br><br>" +
            "❌ Error de inicialización<br>" +
            "Verifique la configuración de la base de datos</center></html>");
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        add(lblError, BorderLayout.CENTER);
    }
    
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("📝 INSCRIPCIÓN DE POSTULANTES");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(new Color(46, 204, 113));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Panel de formulario
        JPanel formPanel = createFormPanel();
        
        // Panel de botones
        JPanel buttonPanel = createButtonPanel();
        
        // Panel de información
        JPanel infoPanel = createInfoPanel();
        
        panel.add(titulo, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Panel inferior con botones e información
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Datos del Postulante"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Fila 1: Código y Generar
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Código:"), gbc);
        
        gbc.gridx = 1;
        txtCodigo = new JTextField(15);
        txtCodigo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        panel.add(txtCodigo, gbc);
        
        gbc.gridx = 2;
        btnGenerar = new JButton("🎲 Generar");
        btnGenerar.setToolTipText("Generar código automático");
        btnGenerar.addActionListener(e -> generarCodigo());
        panel.add(btnGenerar, gbc);
        
        // Fila 2: Apellidos y Nombres
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Apellidos y Nombres:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtApellidos = new JTextField(25);
        panel.add(txtApellidos, gbc);
        
        // Fila 3: DNI
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("DNI:"), gbc);
        
        gbc.gridx = 1;
        txtDni = new JTextField(15);
        txtDni.setToolTipText("Documento de identidad (8 dígitos)");
        panel.add(txtDni, gbc);
        
        // Fila 4: Primera Opción
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Primera Opción:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbOpcion1 = new JComboBox<>(getCarreras());
        panel.add(cmbOpcion1, gbc);
        
        // Fila 5: Segunda Opción
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        panel.add(new JLabel("Segunda Opción:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        cmbOpcion2 = new JComboBox<>(getCarreras());
        panel.add(cmbOpcion2, gbc);
        
        // Fila 6: Modalidad
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1;
        panel.add(new JLabel("Modalidad:"), gbc);
        
        gbc.gridx = 1;
        cmbModalidad = new JComboBox<>(new String[]{
            "ORDINARIO", "EXONERADO", "BECA 18", "DISCAPACITADO"
        });
        panel.add(cmbModalidad, gbc);
        
        // Fila 7: Sexo
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Sexo:"), gbc);
        
        gbc.gridx = 1;
        cmbSexo = new JComboBox<>(new String[]{"M", "F"});
        panel.add(cmbSexo, gbc);
        
        // Fila 8: Estado Académico
        gbc.gridx = 0; gbc.gridy = 7;
        panel.add(new JLabel("Estado Académico:"), gbc);
        
        gbc.gridx = 1;
        cmbEstadoAcademico = new JComboBox<>(new String[]{
            "POSTULANTE", "ALUMNO_LIBRE"
        });
        panel.add(cmbEstadoAcademico, gbc);
        
        // Fila 9: Teléfono
        gbc.gridx = 0; gbc.gridy = 8;
        panel.add(new JLabel("Teléfono:"), gbc);
        
        gbc.gridx = 1;
        txtTelefono = new JTextField(15);
        txtTelefono.setToolTipText("Número de teléfono o celular");
        panel.add(txtTelefono, gbc);
        
        // Fila 10: Dirección
        gbc.gridx = 0; gbc.gridy = 9;
        panel.add(new JLabel("Dirección:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtDireccion = new JTextField(25);
        panel.add(txtDireccion, gbc);
        
        // Fila 11: Colegio
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 1;
        panel.add(new JLabel("Colegio:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2;
        txtColegio = new JTextField(25);
        panel.add(txtColegio, gbc);
        
        // Fila 12: Fecha de Nacimiento
        gbc.gridx = 0; gbc.gridy = 11; gbc.gridwidth = 1;
        panel.add(new JLabel("Fecha Nacimiento:"), gbc);
        
        gbc.gridx = 1;
        try {
            txtFechaNac = new JFormattedTextField(new SimpleDateFormat("dd/MM/yyyy"));
            txtFechaNac.setColumns(10);
            txtFechaNac.setToolTipText("Formato: dd/MM/yyyy");
        } catch (Exception e) {
            txtFechaNac = new JFormattedTextField();
            txtFechaNac.setColumns(10);
        }
        panel.add(txtFechaNac, gbc);
        
        // Fila 13: Nota Aptitud Académica
        gbc.gridx = 0; gbc.gridy = 12;
        panel.add(new JLabel("Nota Aptitud Académica:"), gbc);
        
        gbc.gridx = 1;
        spnNotaAC = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 20.0, 0.1));
        panel.add(spnNotaAC, gbc);
        
        // Fila 14: Nota Conocimientos
        gbc.gridx = 0; gbc.gridy = 13;
        panel.add(new JLabel("Nota Conocimientos:"), gbc);
        
        gbc.gridx = 1;
        spnNotaCO = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 20.0, 0.1));
        panel.add(spnNotaCO, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnGuardar = new JButton("💾 Guardar Postulante");
        btnGuardar.setBackground(new Color(46, 204, 113));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.addActionListener(e -> guardarPostulante());
        
        btnLimpiar = new JButton("🧹 Limpiar Formulario");
        btnLimpiar.setBackground(new Color(52, 152, 219));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        btnLimpiar.setPreferredSize(new Dimension(180, 40));
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        panel.add(btnGuardar);
        panel.add(btnLimpiar);
        
        return panel;
    }
    
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Información del Sistema"));
        
        txtAreaInfo = new JTextArea(5, 0);
        txtAreaInfo.setEditable(false);
        txtAreaInfo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        txtAreaInfo.setBackground(new Color(248, 249, 250));
        txtAreaInfo.setText(
            "💡 INSTRUCCIONES:\n" +
            "• Complete todos los campos requeridos\n" +
            "• Use el botón 'Generar' para crear un código automático\n" +
            "• Las notas deben estar entre 0 y 20\n" +
            "• El DNI debe tener exactamente 8 dígitos\n" +
            "• Al guardar, los datos se almacenan en la base de datos H2"
        );
        
        JScrollPane scrollInfo = new JScrollPane(txtAreaInfo);
        scrollInfo.setPreferredSize(new Dimension(0, 120));
        
        panel.add(scrollInfo, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String[] getCarreras() {
        try {
            List<String> carrerasDB = carreraDAO.obtenerTodasLasCarreras();
            
            // Agregar opción por defecto al inicio
            String[] carreras = new String[carrerasDB.size() + 1];
            carreras[0] = "-- Seleccione una carrera --";
            
            for (int i = 0; i < carrerasDB.size(); i++) {
                carreras[i + 1] = carrerasDB.get(i);
            }
            
            return carreras;
            
        } catch (Exception e) {
            System.err.println("❌ Error obteniendo carreras: " + e.getMessage());
            
            // Fallback a lista por defecto
            return new String[]{
                "-- Seleccione una carrera --",
                "INGENIERÍA DE SISTEMAS E INFORMÁTICA",
                "MEDICINA HUMANA",
                "INGENIERÍA CIVIL",
                "ADMINISTRACIÓN",
                "CONTABILIDAD",
                "DERECHO Y CIENCIAS POLÍTICAS",
                "PSICOLOGÍA",
                "ENFERMERÍA",
                "INGENIERÍA INDUSTRIAL",
                "ECONOMÍA"
            };
        }
    }
    
    private void generarCodigo() {
        String codigo = "2025" + String.format("%03d", (int)(Math.random() * 1000));
        txtCodigo.setText(codigo);
        
        // Mostrar información
        txtAreaInfo.setText(
            "🎲 CÓDIGO GENERADO:\n" +
            "• Código: " + codigo + "\n" +
            "• Año: 2025\n" +
            "• Número secuencial aleatorio\n" +
            "• Puede modificarlo si es necesario"
        );
    }
    
    private void guardarPostulante() {
        if (postulanteDAO == null) {
            JOptionPane.showMessageDialog(this,
                "❌ Error: Sistema de base de datos no disponible",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Validar campos requeridos
            if (!validarCampos()) {
                return;
            }
            
            // Crear objeto Postulante
            Postulante postulante = new Postulante();
            
            // Campos básicos
            postulante.setCodigo(txtCodigo.getText().trim().toUpperCase());
            postulante.setApellidosNombres(txtApellidos.getText().trim().toUpperCase());
            postulante.setDni(txtDni.getText().trim());
            
            // Opciones de carrera
            if (cmbOpcion1.getSelectedIndex() > 0) {
                postulante.setOpcion1(cmbOpcion1.getSelectedItem().toString());
            }
            if (cmbOpcion2.getSelectedIndex() > 0) {
                postulante.setOpcion2(cmbOpcion2.getSelectedItem().toString());
            }
            
            // Otros campos
            postulante.setModalidad(cmbModalidad.getSelectedItem().toString());
            postulante.setSexo(cmbSexo.getSelectedItem().toString());
            postulante.setEstadoAcademico(cmbEstadoAcademico.getSelectedItem().toString());
            postulante.setTelCelular(txtTelefono.getText().trim());
            postulante.setDireccion(txtDireccion.getText().trim());
            postulante.setNombreColegio(txtColegio.getText().trim());
            
            // Fecha de nacimiento
            if (txtFechaNac.getValue() != null) {
                postulante.setFecNac((Date) txtFechaNac.getValue());
            }
            
            // Notas
            postulante.setNotaAC((Double) spnNotaAC.getValue());
            postulante.setNotaCO((Double) spnNotaCO.getValue());
            
            // Fecha de inscripción (actual)
            postulante.setInscripcion(new Date());
            
            // Guardar en base de datos
            if (postulanteDAO.guardar(postulante)) {
                
                // 🔄 NOTIFICAR EVENTO - Sincronización automática
                EventBus.getInstance().publicarPostulanteAgregado();
                
                JOptionPane.showMessageDialog(this,
                    "✅ Postulante registrado exitosamente\n\n" +
                    "Código: " + postulante.getCodigo() + "\n" +
                    "Nombre: " + postulante.getApellidosNombres() + "\n" +
                    "DNI: " + postulante.getDni() + "\n\n" +
                    "📢 Sincronizando con otros módulos...",
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
                
                limpiarFormulario();
                
                // Actualizar información
                txtAreaInfo.setText(
                    "✅ POSTULANTE GUARDADO:\n" +
                    "• Código: " + postulante.getCodigo() + "\n" +
                    "• Nombre: " + postulante.getApellidosNombres() + "\n" +
                    "• Nota Final: " + String.format("%.1f", postulante.getNotaFinal()) + "\n" +
                    "• Estado: " + postulante.getEstadoAcademico()
                );
                
            } else {
                JOptionPane.showMessageDialog(this,
                    "❌ Error al guardar el postulante\n" +
                    "Verifique que el código y DNI no estén duplicados",
                    "Error de Guardado",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            System.err.println("Error guardando postulante: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "❌ Error inesperado: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void suscribirseAEventos() {
        EventBus.getInstance().suscribirCarreras(event -> {
            SwingUtilities.invokeLater(() -> {
                if ("CARRERA_AGREGADA".equals(event.getTipo())) {
                    // Actualizar comboboxes de opciones
                    actualizarComboBoxCarreras();
                    
                    System.out.println("🔄 Carreras actualizadas en Inscripción: " + event.getNombreCarrera());
                }
            });
        });
    }
    
    /**
     * Actualizar ComboBoxes con las carreras disponibles
     */
    private void actualizarComboBoxCarreras() {
        String[] carreras = getCarreras();
        
        // Guardar selecciones actuales
        String opcion1Actual = (String) cmbOpcion1.getSelectedItem();
        String opcion2Actual = (String) cmbOpcion2.getSelectedItem();
        
        // Actualizar modelos
        DefaultComboBoxModel<String> modelo1 = new DefaultComboBoxModel<>(carreras);
        DefaultComboBoxModel<String> modelo2 = new DefaultComboBoxModel<>(carreras);
        
        cmbOpcion1.setModel(modelo1);
        cmbOpcion2.setModel(modelo2);
        
        // Restaurar selecciones si aún existen
        if (opcion1Actual != null && contains(carreras, opcion1Actual)) {
            cmbOpcion1.setSelectedItem(opcion1Actual);
        }
        if (opcion2Actual != null && contains(carreras, opcion2Actual)) {
            cmbOpcion2.setSelectedItem(opcion2Actual);
        }
    }
    
    /**
     * Verificar si un array contiene un elemento
     */
    private boolean contains(String[] array, String item) {
        for (String s : array) {
            if (s.equals(item)) return true;
        }
        return false;
    }
    
    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();
        
        // Código
        if (txtCodigo.getText().trim().isEmpty()) {
            errores.append("• Código es requerido\n");
        }
        
        // Apellidos y nombres
        if (txtApellidos.getText().trim().isEmpty()) {
            errores.append("• Apellidos y nombres es requerido\n");
        }
        
        // DNI
        String dni = txtDni.getText().trim();
        if (dni.isEmpty()) {
            errores.append("• DNI es requerido\n");
        } else if (dni.length() != 8 || !dni.matches("\\d+")) {
            errores.append("• DNI debe tener exactamente 8 dígitos\n");
        }
        
        // Primera opción
        if (cmbOpcion1.getSelectedIndex() == 0) {
            errores.append("• Primera opción de carrera es requerida\n");
        }
        
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(this,
                "❌ Errores de validación:\n\n" + errores.toString(),
                "Campos Requeridos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtApellidos.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtColegio.setText("");
        txtFechaNac.setValue(null);
        
        cmbOpcion1.setSelectedIndex(0);
        cmbOpcion2.setSelectedIndex(0);
        cmbModalidad.setSelectedIndex(0);
        cmbSexo.setSelectedIndex(0);
        cmbEstadoAcademico.setSelectedIndex(0);
        
        spnNotaAC.setValue(0.0);
        spnNotaCO.setValue(0.0);
        
        txtAreaInfo.setText(
            "🧹 FORMULARIO LIMPIADO:\n" +
            "• Todos los campos han sido vaciados\n" +
            "• Puede comenzar a registrar un nuevo postulante\n" +
            "• Use el botón 'Generar' para crear un código automático"
        );
        
        txtCodigo.requestFocus();
    }
}