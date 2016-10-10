package servidorso;

public class Camara {

    public boolean tomarFoto() {
        System.out.println("Tomando Foto...");
        try {
            Runtime.getRuntime().exec("raspistill -o imagen.jpg -t 1000");
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

}
