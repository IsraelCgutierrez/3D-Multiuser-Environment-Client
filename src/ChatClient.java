package obj3D;
import java.io. * ;
import java.awt. * ;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

//hebra encargada de las comunicaciones de entrada y salida con el servidor
public class ChatClient implements Runnable {
    protected DataInputStream i;
    protected DataOutputStream o;
    protected Thread listener;
    protected int id;
    protected Controlador2 cc;

    //la clase se crea con los parametros de entrada y salida del socket y el objeto de la clase que interpreta los comandos
    public ChatClient(InputStream i, OutputStream o, Controlador2 ccc) {
        this.i = new DataInputStream(new BufferedInputStream(i)); //entrada de datos desde el servidor
        this.o = new DataOutputStream(new BufferedOutputStream(o)); //salida de datos del servidor
        this.cc = ccc; //clase para interpretar los comandos
        listener = new Thread(this); //creacion de la hebra
        listener.start(); //inicio de la hebra
    }
    //agrega el identificador para poder recuperar informacion en caso de error
    void setID(int i) {
        this.id = i;
    }

    //hebra infinita que espera mensajes del servidor y los descomprime
    public void run() {
        try {
            while (true) { //hebra infinita
                String line;
                line = i.readUTF(); //la hebra se queda esperando en este punto
                byte[] input = new sun.misc.BASE64Decoder().decodeBuffer(line); //pasa el mensaje comprimido de base 64 a una cadena de bytes
                Inflater decompresser = new Inflater(); //inicializa el descompresor
                decompresser.setInput(input, 0, input.length);
                byte[] result = new byte[100 * 1024];
                int resultLength = 0;
                try {
                    resultLength = decompresser.inflate(result); //descomprime los bytes
                } catch (DataFormatException ex) {
                    System.out.print("Error al descomprimir");
                }
                decompresser.end(); //finaliza la descompresion
                if (resultLength > 0) { //comprueba si ha descomprimido algo
                    String line2 = new String(result, 0, resultLength, "UTF-8"); //pasa el resultado de descomprimir a una cadena
                    //fin de la descompresion
                    int r = cc.llega(line2); //envia la informacion para ser analizada
                    if (r > 0) { //comprobacion de errores
                        System.out.print("\r\n L: " + input.length + " " + line.length() + " " + line2 + "\r\n");
                        System.out.print("Error en la interpretacion de los comandos");
                    }
                }
            }
        } catch (IOException ex) {
            //ex.printStackTrace ();
            System.out.print("no se puede conectar con el servidor \r\n");
        } finally {
            listener = null;
            System.out.print("Se ha perdido la conexion con el servidor.");
            try {
                o.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //función para enviar datos al servidor
    public void envia(String mensaje) throws InterruptedException {
        try {
            o.writeUTF(mensaje);
            o.flush();
        } catch (IOException ex) {
            System.out.print("Imposible enviar datos al servidor \r\n");
            listener.stop();
        }
    }

    //avisa al servidor de la desconeccion al cerrar la ventana
    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            try {
                envia("2:" + id);
            } catch (InterruptedException ex) {
                System.out.print("Imposible enviar datos al servidor \r\n");
            }
            if (listener != null) listener.stop();
            return true;
        }
        return handleEvent(e);
    }
}