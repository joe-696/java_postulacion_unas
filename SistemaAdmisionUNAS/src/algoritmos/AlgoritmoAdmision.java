package algoritmos;

import model.Postulante;
import dao.PostulanteDAO;
import java.util.*;

/**
 * Algoritmo de procesamiento de admisión UNAS
 * @author joe-696
 */
public class AlgoritmoAdmision {
    
    private PostulanteDAO postulanteDAO;
    
    public AlgoritmoAdmision() {
        this.postulanteDAO = new PostulanteDAO();
    }
    
    /**
     * Procesar admisión completa
     */
    public Map<String, List<Postulante>> procesarAdmision() {
        Map<String, List<Postulante>> resultados = new HashMap<>();
        
        try {
            // Cargar todos los postulantes
            List<Postulante> postulantes = postulanteDAO.obtenerTodos();
            
            // Filtrar solo los que tienen puntaje
            List<Postulante> conPuntaje = new ArrayList<>();
            for (Postulante p : postulantes) {
                if (p.getNotaFinal() > 0) {
                    conPuntaje.add(p);
                }
            }
            
            // Aplicar lógica de admisión básica
            List<Postulante> ingresantes = new ArrayList<>();
            List<Postulante> noIngresantes = new ArrayList<>();
            
            for (Postulante p : conPuntaje) {
                if (p.getNotaFinal() >= 11.0) {
                    ingresantes.add(p);
                } else {
                    noIngresantes.add(p);
                }
            }
            
            resultados.put("INGRESANTES", ingresantes);
            resultados.put("NO_INGRESANTES", noIngresantes);
            
            System.out.println("✅ Algoritmo procesado: " + ingresantes.size() + " ingresantes");
            
        } catch (Exception e) {
            System.err.println("❌ Error en algoritmo: " + e.getMessage());
        }
        
        return resultados;
    }
}