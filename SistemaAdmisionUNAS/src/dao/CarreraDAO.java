package dao;

import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para gestión de carreras
 * @author joe-696
 */
public class CarreraDAO {
    
    /**
     * Obtener todas las carreras activas
     */
    public List<String> obtenerTodasLasCarreras() {
        List<String> carreras = new ArrayList<>();
        String sql = "SELECT nombre FROM carreras WHERE activa = TRUE ORDER BY nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                carreras.add(rs.getString("nombre"));
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo carreras: " + e.getMessage());
        }
        
        return carreras;
    }
    
    /**
     * Agregar una nueva carrera
     */
    public boolean agregarCarrera(String nombreCarrera) {
        String sql = "INSERT INTO carreras (nombre) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreCarrera.toUpperCase().trim());
            int filas = pstmt.executeUpdate();
            
            // Forzar commit
            conn.commit();
            
            return filas > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) { // Duplicate key
                System.err.println("⚠️ La carrera ya existe: " + nombreCarrera);
            } else {
                System.err.println("❌ Error agregando carrera: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Verificar si una carrera existe
     */
    public boolean existeCarrera(String nombreCarrera) {
        String sql = "SELECT COUNT(*) FROM carreras WHERE nombre = ? AND activa = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreCarrera.toUpperCase().trim());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error verificando carrera: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Eliminar una carrera (marcar como inactiva)
     */
    public boolean eliminarCarrera(String nombreCarrera) {
        String sql = "UPDATE carreras SET activa = FALSE WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreCarrera.toUpperCase().trim());
            int filas = pstmt.executeUpdate();
            
            // Forzar commit
            conn.commit();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error eliminando carrera: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtener carreras con estadísticas
     */
    public List<String> obtenerCarrerasConEstadisticas() {
        List<String> carreras = new ArrayList<>();
        String sql = """
            SELECT c.nombre, COUNT(p.codigo) as total_postulantes
            FROM carreras c
            LEFT JOIN postulantes p ON (c.nombre = p.opcion1 OR c.nombre = p.opcion2)
            WHERE c.activa = TRUE
            GROUP BY c.nombre
            ORDER BY total_postulantes DESC, c.nombre
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                String carrera = rs.getString("nombre");
                int totalPostulantes = rs.getInt("total_postulantes");
                carreras.add(carrera + " (" + totalPostulantes + " postulantes)");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo carreras con estadísticas: " + e.getMessage());
            // Fallback a lista simple
            return obtenerTodasLasCarreras();
        }
        
        return carreras;
    }
}
