package dao;

import model.Postulante;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones con Postulantes
 * Compatible con el modelo Postulante completo
 * @author joe-696
 */
public class PostulanteDAO {
    
    /**
     * Guardar postulante en base de datos
     */
    public boolean guardar(Postulante postulante) {
        // Verificar conexión primero
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión a la base de datos");
            return false;
        }
        
        String sql = """
            INSERT INTO postulantes (
                codigo, apellidos_nombres, opcion1, opcion2, modalidad, dni, 
                cod_sede, inscripcion, ubigeo_procedencia, cod_colegio, 
                fecha_egreso_colegio, tipo_colegio, ubigeo_colegio, estado_civil,
                encuesta, ingreso, ingreso_a, sexo, nombre_colegio, idioma_mat,
                tel_celular, direccion, ubigeo, fec_nac, nota_ac, nota_co,
                nota_final, estado_academico, respuesta
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Campos básicos
            pstmt.setString(1, postulante.getCodigo());
            pstmt.setString(2, postulante.getApellidosNombres());
            pstmt.setString(3, postulante.getOpcion1());
            pstmt.setString(4, postulante.getOpcion2());
            pstmt.setString(5, postulante.getModalidad());
            pstmt.setString(6, postulante.getDni());
            pstmt.setInt(7, postulante.getCodSede());
            
            // Fecha de inscripción
            if (postulante.getInscripcion() != null) {
                pstmt.setDate(8, new java.sql.Date(postulante.getInscripcion().getTime()));
            } else {
                pstmt.setDate(8, new java.sql.Date(System.currentTimeMillis()));
            }
            
            // Campos adicionales
            pstmt.setString(9, postulante.getUbigeoProcedencia());
            pstmt.setString(10, postulante.getCodColegio());
            
            // Fecha egreso colegio
            if (postulante.getFechaEgresoColegio() != null) {
                pstmt.setDate(11, new java.sql.Date(postulante.getFechaEgresoColegio().getTime()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }
            
            pstmt.setInt(12, postulante.getTipoColegio());
            pstmt.setString(13, postulante.getUbigeoColegio());
            pstmt.setString(14, postulante.getEstadoCivil());
            pstmt.setString(15, postulante.getEncuesta());
            pstmt.setInt(16, postulante.getIngreso());
            pstmt.setString(17, postulante.getIngresoA());
            pstmt.setString(18, postulante.getSexo());
            pstmt.setString(19, postulante.getNombreColegio());
            pstmt.setString(20, postulante.getIdiomaMat());
            pstmt.setString(21, postulante.getTelCelular());
            pstmt.setString(22, postulante.getDireccion());
            pstmt.setString(23, postulante.getUbigeo());
            
            // Fecha nacimiento
            if (postulante.getFecNac() != null) {
                pstmt.setDate(24, new java.sql.Date(postulante.getFecNac().getTime()));
            } else {
                pstmt.setNull(24, Types.DATE);
            }
            
            // Notas
            pstmt.setDouble(25, postulante.getNotaAC());
            pstmt.setDouble(26, postulante.getNotaCO());
            pstmt.setDouble(27, postulante.getNotaFinal());
            pstmt.setString(28, postulante.getEstadoAcademico());
            pstmt.setString(29, postulante.getRespuesta());
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Postulante guardado exitosamente: " + postulante.getCodigo());
                return true;
            } else {
                System.err.println("❌ No se pudo guardar el postulante");
                return false;
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) { // Duplicate key
                System.err.println("❌ Error: Ya existe un postulante con código " + postulante.getCodigo() + " o DNI " + postulante.getDni());
            } else {
                System.err.println("❌ Error SQL guardando postulante: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtener todos los postulantes
     */
    public List<Postulante> obtenerTodos() {
        List<Postulante> postulantes = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn == null) {
            System.err.println("❌ No se pudo obtener conexión para listar postulantes");
            return postulantes;
        }
        
        String sql = "SELECT * FROM postulantes ORDER BY codigo";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Postulante p = new Postulante();
                
                // Mapear todos los campos del ResultSet al objeto Postulante
                p.setCodigo(rs.getString("codigo"));
                p.setApellidosNombres(rs.getString("apellidos_nombres"));
                p.setOpcion1(rs.getString("opcion1"));
                p.setOpcion2(rs.getString("opcion2"));
                p.setModalidad(rs.getString("modalidad"));
                p.setDni(rs.getString("dni"));
                p.setCodSede(rs.getInt("cod_sede"));
                p.setInscripcion(rs.getDate("inscripcion"));
                p.setUbigeoProcedencia(rs.getString("ubigeo_procedencia"));
                p.setCodColegio(rs.getString("cod_colegio"));
                p.setFechaEgresoColegio(rs.getDate("fecha_egreso_colegio"));
                p.setTipoColegio(rs.getInt("tipo_colegio"));
                p.setUbigeoColegio(rs.getString("ubigeo_colegio"));
                p.setEstadoCivil(rs.getString("estado_civil"));
                p.setEncuesta(rs.getString("encuesta"));
                p.setIngreso(rs.getInt("ingreso"));
                p.setIngresoA(rs.getString("ingreso_a"));
                p.setSexo(rs.getString("sexo"));
                p.setNombreColegio(rs.getString("nombre_colegio"));
                p.setIdiomaMat(rs.getString("idioma_mat"));
                p.setTelCelular(rs.getString("tel_celular"));
                p.setDireccion(rs.getString("direccion"));
                p.setUbigeo(rs.getString("ubigeo"));
                p.setFecNac(rs.getDate("fec_nac"));
                p.setNotaAC(rs.getDouble("nota_ac"));
                p.setNotaCO(rs.getDouble("nota_co"));
                p.setEstadoAcademico(rs.getString("estado_academico"));
                p.setRespuesta(rs.getString("respuesta"));
                
                postulantes.add(p);
            }
            
            System.out.println("✅ Cargados " + postulantes.size() + " postulantes");
            
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo postulantes: " + e.getMessage());
        }
        
        return postulantes;
    }
    
    /**
     * Buscar postulante por código
     */
    public Postulante buscarPorCodigo(String codigo) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;
        
        String sql = "SELECT * FROM postulantes WHERE codigo = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Postulante p = new Postulante();
                    
                    // Mapear campos básicos para búsqueda
                    p.setCodigo(rs.getString("codigo"));
                    p.setApellidosNombres(rs.getString("apellidos_nombres"));
                    p.setOpcion1(rs.getString("opcion1"));
                    p.setOpcion2(rs.getString("opcion2"));
                    p.setModalidad(rs.getString("modalidad"));
                    p.setDni(rs.getString("dni"));
                    p.setSexo(rs.getString("sexo"));
                    p.setNombreColegio(rs.getString("nombre_colegio"));
                    p.setTelCelular(rs.getString("tel_celular"));
                    p.setDireccion(rs.getString("direccion"));
                    p.setEstadoAcademico(rs.getString("estado_academico"));
                    p.setNotaAC(rs.getDouble("nota_ac"));
                    p.setNotaCO(rs.getDouble("nota_co"));
                    
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error buscando postulante: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Eliminar postulante
     */
    public boolean eliminar(String codigo) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        
        String sql = "DELETE FROM postulantes WHERE codigo = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, codigo);
            
            int filasAfectadas = pstmt.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Postulante eliminado: " + codigo);
                return true;
            } else {
                System.err.println("❌ No se encontró postulante con código: " + codigo);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error eliminando postulante: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Buscar postulantes por modalidad
     */
    public List<Postulante> buscarPorModalidad(String modalidad) {
        List<Postulante> postulantes = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn == null) return postulantes;
        
        String sql = "SELECT * FROM postulantes WHERE modalidad = ? ORDER BY apellidos_nombres";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, modalidad);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Postulante p = new Postulante();
                    p.setCodigo(rs.getString("codigo"));
                    p.setApellidosNombres(rs.getString("apellidos_nombres"));
                    p.setOpcion1(rs.getString("opcion1"));
                    p.setOpcion2(rs.getString("opcion2"));
                    p.setModalidad(rs.getString("modalidad"));
                    p.setDni(rs.getString("dni"));
                    p.setEstadoAcademico(rs.getString("estado_academico"));
                    postulantes.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error buscando por modalidad: " + e.getMessage());
        }
        
        return postulantes;
    }
    
    /**
     * Actualizar postulante
     */
    public boolean actualizar(Postulante postulante) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;
        
        String sql = """
            UPDATE postulantes SET 
                apellidos_nombres = ?, opcion1 = ?, opcion2 = ?, modalidad = ?,
                dni = ?, sexo = ?, nombre_colegio = ?, tel_celular = ?,
                direccion = ?, estado_academico = ?, nota_ac = ?, nota_co = ?
            WHERE codigo = ?
            """;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postulante.getApellidosNombres());
            pstmt.setString(2, postulante.getOpcion1());
            pstmt.setString(3, postulante.getOpcion2());
            pstmt.setString(4, postulante.getModalidad());
            pstmt.setString(5, postulante.getDni());
            pstmt.setString(6, postulante.getSexo());
            pstmt.setString(7, postulante.getNombreColegio());
            pstmt.setString(8, postulante.getTelCelular());
            pstmt.setString(9, postulante.getDireccion());
            pstmt.setString(10, postulante.getEstadoAcademico());
            pstmt.setDouble(11, postulante.getNotaAC());
            pstmt.setDouble(12, postulante.getNotaCO());
            pstmt.setString(13, postulante.getCodigo());
            
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando postulante: " + e.getMessage());
            return false;
        }
    }
}