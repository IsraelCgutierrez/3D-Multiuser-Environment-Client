package obj3D;
import java.io.IOException;
import javax.swing. * ;
import java.awt. * ;
//clase principal extencion de JFrame para crear una ventana.
public class Obj3D extends JFrame {
    public Obj3D(String IP, String puerto) throws IOException, InterruptedException {
        super("Obj3D");
        Container c = getContentPane(); //contenedor
        c.setLayout(new BorderLayout()); //borde
        WrapObj3D w3d = new WrapObj3D(IP, puerto); //JPanel
        c.add(w3d, BorderLayout.CENTER); //JPanel al contenedor
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //cerrar ventana
        pack(); //empaquetado
        setResizable(false); //evitar el cambio de tamaño
        setVisible(true); //para que pueda ser visible
    }
    //comprueba si esta instalado java 3D
    public static boolean hasJ3D() {
        try {
            Class.forName("com.sun.j3d.utils.universe.SimpleUniverse");
            return true;
        }
        catch (ClassNotFoundException e) {
            System.err.println("Java 3D no instalado");
            return false;
        }

    }
    //clase principal
    public static void main(String[] args) throws IOException, InterruptedException {
        if (hasJ3D()) {
            if (args.length == 2) {
                new Obj3D(args[0], args[1]);
            } else {
                System.out.println("Parametros: IP Puerto");
            }

        }
    }
}