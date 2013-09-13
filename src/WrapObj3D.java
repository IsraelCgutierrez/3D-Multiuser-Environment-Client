package obj3D;
import java.io.IOException;
import javax.swing. * ;
import java.awt. * ;
import com.sun.j3d.utils.universe. * ;
import javax.media.j3d. * ;
import javax.vecmath. * ;
import com.sun.j3d.utils.behaviors.vp. * ;
import java.net. * ;
//clase que crea el universo virtual y toda la escenografía
public class WrapObj3D extends JPanel {
    private static final int PWIDTH = 1024; // Largo del panel
    private static final int PHEIGHT = 576; //Alto del panel
    private static final int BOUNDSIZE = 200; //ancho de renderizado
    private SimpleUniverse su; //clase para crear un universo rapido
    private BranchGroup sceneBG; //nodo principal de la escenografía
    private BoundingSphere bounds; //necesario para delimitar la luz, el cielo, etc
    public OrbitBehavior orbit; //para controlar la posición de visión
    public Controlador2 control; //clase para interpretar los mensajes del servidor
    public int id; //identificador del usuario
    ViewingPlatform vp; //la plataforma de vicion del universo
    TransformGroup steerTG; //grupo de transformacion de vicion
    Socket s = null; //Socket para conectar con el servidor
    //se encarga de se encarga de crear un universo virtual
    //y las herramientas necesarias para una comunicación con el servidor
    public WrapObj3D(String IP, String puerto) {
        setLayout(new BorderLayout()); //Borde del JPanel
        setOpaque(false); //JPanel visible
        setPreferredSize(new Dimension(PWIDTH, PHEIGHT)); //tamaño de la ventana del JPanel
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //configuracion predeterminada de simpleUniverse
        Canvas3D canvas3D = new Canvas3D(config); //Canvas3D
        add("Center", canvas3D); //Agrega la ventana 2D de canvas3D al JPanel
        canvas3D.setFocusable(true); //parametros para poder activar el controlador
        canvas3D.requestFocus(); //del teclado y asi poder enviar información al servidor
        su = new SimpleUniverse(canvas3D); // construye la clase simpleUniverse a partir del canvas3D
        vp = su.getViewingPlatform(); //plataforma de vista del nuevo universo creado
        createSceneGraph(IP, puerto); //función para crear la escenografía necesaria
        orbitControls(canvas3D); //función para crear un control sobre el angulo de visión
        su.addBranchGraph(sceneBG); //agrega el nodo al universo virtual
    }

    //funcion que crea la escenografia
    private void createSceneGraph(String IP, String puerto) {
        sceneBG = new BranchGroup(); //nodo principal desde el cual, todo lo que depende de este empieza a ser renderizado
        sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_READ); //grupo de instrucciones para
        sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE); //agregar la capacidad de crear
        sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND); //modificar en posicion, tamaño, rotacion, etc
        sceneBG.setCapability(BranchGroup.ALLOW_DETACH); //y destruir objetos en tiempo real
        bounds = new BoundingSphere(new Point3d(0, 0, 0), BOUNDSIZE); //esqueleto necesario para establecer las luces y el cielo
        lightScene(); //función que agrega dos luces
        addBackground(); //función que agrega el cielo
        sceneBG.addChild(new CheckerFloor().getBG()); //función que agrega el suelo
        control = new Controlador2(this); //clase para interpretar los comandos del servidor
        try {
            s = new Socket(IP, Integer.parseInt(puerto)); //creacion del socket con los parametros del servidor
        } catch (UnknownHostException ex) {
            throw new RuntimeException("Error la ip " + IP + " no es valida\r\n");

        } catch (IOException ex) {

            throw new RuntimeException("Error no se puede conectar con el servidor " + IP + ":" + puerto + "\r\n");
        }

        ChatClient c = null; //clase hebra que controla las comunicaciones con el servidor
        try {
            c = new ChatClient(s.getInputStream(), s.getOutputStream(), control); //creacion de la clase con los parametros del socket y la case de interpretacion de comandos
        } catch (IOException ex) {
            throw new RuntimeException("Error en la conexion al socket");
        }

        control.addC(c); //a la clase que interpreta los comandos se le pasa la hebra de comunicaciones como parámetro
        KeyBehavior kb = new KeyBehavior(c); //clase para capturar las entradas del teclado, tiene como parametro la hebra de comunicaciones
        kb.setSchedulingBounds(bounds); //agrega los limites al KeyBehavior
        sceneBG.addChild(kb); //agrega el KeyBehavior al nodo principal
        sceneBG.compile(); //compila el nodo
    }

    //función para agregar las luces a la escena
    private void lightScene() {
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        AmbientLight ambientLightNode = new AmbientLight(white);
        ambientLightNode.setInfluencingBounds(bounds);
        sceneBG.addChild(ambientLightNode);
        Vector3f light1Direction = new Vector3f(-1.0f, -1.0f, -1.0f);
        Vector3f light2Direction = new Vector3f(1.0f, -1.0f, 1.0f);
        DirectionalLight light1 = new DirectionalLight(white, light1Direction);
        light1.setInfluencingBounds(bounds);
        sceneBG.addChild(light1);
        DirectionalLight light2 = new DirectionalLight(white, light2Direction);
        light2.setInfluencingBounds(bounds);
        sceneBG.addChild(light2);
    }

    //función para agregar el suelo a la escena
    private void addBackground() {
        Background back = new Background();
        back.setApplicationBounds(bounds);
        back.setColor(0.17f, 0.65f, 0.92f);
        sceneBG.addChild(back);
    }

    //creacion los controles de camara
    private void orbitControls(Canvas3D c) {
        orbit = new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
        orbit.setSchedulingBounds(bounds);
        vp.setViewPlatformBehavior(orbit);
        steerTG = vp.getViewPlatformTransform();
    }

    //unicializacion de la posicion de la camara
    public void initUserPosition(float x, float y, float z) {
        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);
        Vector3f mv = new Vector3f();
        t3d.get(mv);
        if (mv.getX() == 0 && mv.getY() == 0 && mv.getZ() == 0) {
            mv = new Vector3f(0, 5f, 0);
        }
        t3d.lookAt(new Point3d(mv.getX(), mv.getY(), mv.getZ()), new Point3d(x, y, z), new Vector3d(0, 1, 0));
        t3d.invert();
        steerTG.setTransform(t3d);
    }

    //funcion para cambiar de posicion la camara
    public void viewPoint(float x, float y, float z) {

        Transform3D t3d = new Transform3D();
        steerTG.getTransform(t3d);
        Vector3f mv = new Vector3f();
        t3d.get(mv);
        t3d.lookAt(new Point3d(mv.getX(), mv.getY(), mv.getZ()), new Point3d(x, y, z), new Vector3d(0, 1, 0));
        t3d.invert();
        steerTG.setTransform(t3d);
        orbit.setRotationCenter(new Point3d(x, y, z));
    }

    //función que agrega objetos a la escena
    public Objeto addObjeto(int id, float x, float y, float z, float rx, float ry, float rz, float cx, float cy, float cz, float t) {
        Objeto objeto = new Objeto(id, new Vector3f(x, y, z), new Vector3f(rx, ry, rz), new Vector3f(cx, cy, cz), t, this); //clase objeto
        objeto.getBG().setCapability(BranchGroup.ALLOW_DETACH); //capacidad para borrar objeto
        sceneBG.addChild(objeto.getBG()); //agrega el nuevo objeto al nodo principal
        return objeto;
    }
    //funcion para borrar un objeto
    public void borra(Objeto g) {
        sceneBG.removeChild(g.getBG());
    }
}