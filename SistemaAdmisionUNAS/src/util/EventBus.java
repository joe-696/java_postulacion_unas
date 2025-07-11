package util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Sistema de eventos para sincronización entre módulos
 * @author joe-696
 */
public class EventBus {
    
    private static EventBus instance;
    private List<Consumer<CarreraEvent>> carreraListeners;
    private List<Consumer<PostulanteEvent>> postulanteListeners;
    
    private EventBus() {
        this.carreraListeners = new ArrayList<>();
        this.postulanteListeners = new ArrayList<>();
    }
    
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
    
    // Eventos de carreras
    public void suscribirCarreras(Consumer<CarreraEvent> listener) {
        carreraListeners.add(listener);
    }
    
    public void publicarCarreraAgregada(String nombreCarrera) {
        CarreraEvent event = new CarreraEvent("CARRERA_AGREGADA", nombreCarrera);
        carreraListeners.forEach(listener -> listener.accept(event));
    }
    
    public void publicarCarreraEliminada(String nombreCarrera) {
        CarreraEvent event = new CarreraEvent("CARRERA_ELIMINADA", nombreCarrera);
        carreraListeners.forEach(listener -> listener.accept(event));
    }
    
    // Eventos de postulantes
    public void suscribirPostulantes(Consumer<PostulanteEvent> listener) {
        postulanteListeners.add(listener);
    }
    
    public void publicarPostulanteAgregado() {
        PostulanteEvent event = new PostulanteEvent("POSTULANTE_AGREGADO");
        postulanteListeners.forEach(listener -> listener.accept(event));
    }
    
    public void publicarPostulantesImportados(int cantidad) {
        PostulanteEvent event = new PostulanteEvent("POSTULANTES_IMPORTADOS", cantidad);
        postulanteListeners.forEach(listener -> listener.accept(event));
    }
    
    public void notificarPostulante(String tipo, Object data) {
        PostulanteEvent event = new PostulanteEvent(tipo);
        postulanteListeners.forEach(listener -> listener.accept(event));
    }
    
    // Clases de eventos
    public static class CarreraEvent {
        private String tipo;
        private String nombreCarrera;
        
        public CarreraEvent(String tipo, String nombreCarrera) {
            this.tipo = tipo;
            this.nombreCarrera = nombreCarrera;
        }
        
        public String getTipo() { return tipo; }
        public String getNombreCarrera() { return nombreCarrera; }
    }
    
    public static class PostulanteEvent {
        private String tipo;
        private int cantidad;
        
        public PostulanteEvent(String tipo) {
            this.tipo = tipo;
            this.cantidad = 1;
        }
        
        public PostulanteEvent(String tipo, int cantidad) {
            this.tipo = tipo;
            this.cantidad = cantidad;
        }
        
        public String getTipo() { return tipo; }
        public int getCantidad() { return cantidad; }
    }
}
