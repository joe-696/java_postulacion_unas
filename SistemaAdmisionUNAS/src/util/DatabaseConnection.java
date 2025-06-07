package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión a base de datos H2 para Sistema de Admisión
 * @author joe-696
 */
public class DatabaseConnection {
    
    // Configuración de H2 Database
    private static final String DB_URL = "jdbc:h2:~/sistemaadmision;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private static Connection connection = null;
    
    /**
     * Obtener conexión a la base de datos
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("🔗 Estableciendo conexión a H2 Database...");
                
                // Cargar driver H2
                Class.forName("org.h2.Driver");
                
                // Crear conexión
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                
                System.out.println("✅ Conexión H2 establecida exitosamente");
                
                // Crear tablas si no existen
                crearTablasIniciales();
                
                return connection;
            }
            return connection;
            
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: No se encontró el driver H2");
            System.err.println("   Asegúrate de agregar h2-2.1.214.jar a las librerías");
            return null;
        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("❌ ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Crear tablas iniciales si no existen
     */
    private static void crearTablasIniciales() {
        try {
            Statement stmt = connection.createStatement();
            
            // Tabla postulantes
            String sqlPostulantes = """
                CREATE TABLE IF NOT EXISTS postulantes (
                    codigo VARCHAR(20) PRIMARY KEY,
                    apellidos_nombres VARCHAR(200) NOT NULL,
                    opcion1 VARCHAR(100),
                    opcion2 VARCHAR(100),
                    modalidad VARCHAR(50),
                    dni VARCHAR(8) UNIQUE,
                    cod_sede INTEGER DEFAULT 1,
                    inscripcion DATE,
                    ubigeo_procedencia VARCHAR(10),
                    cod_colegio VARCHAR(20),
                    fecha_egreso_colegio DATE,
                    tipo_colegio INTEGER DEFAULT 1,
                    ubigeo_colegio VARCHAR(10),
                    estado_civil VARCHAR(20),
                    encuesta VARCHAR(50),
                    ingreso INTEGER DEFAULT 0,
                    ingreso_a VARCHAR(100),
                    sexo VARCHAR(1),
                    nombre_colegio VARCHAR(200),
                    idioma_mat VARCHAR(2),
                    tel_celular VARCHAR(15),
                    direccion VARCHAR(200),
                    ubigeo VARCHAR(10),
                    fec_nac DATE,
                    nota_ac DECIMAL(4,2) DEFAULT 0.00,
                    nota_co DECIMAL(4,2) DEFAULT 0.00,
                    nota_final DECIMAL(4,2) DEFAULT 0.00,
                    estado_academico VARCHAR(20) DEFAULT 'POSTULANTE',
                    respuesta VARCHAR(100)
                )
                """;
                
            stmt.execute(sqlPostulantes);
            System.out.println("✅ Tabla 'postulantes' creada/verificada");
            
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error creando tablas: " + e.getMessage());
        }
    }
    
    /**
     * Probar conexión
     */
    public static boolean probarConexion() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Prueba de conexión exitosa");
                return true;
            } else {
                System.out.println("❌ Conexión fallida");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error probando conexión: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cerrar conexión
     */
    public static void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexión H2 cerrada");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error cerrando conexión: " + e.getMessage());
        }
    }
}