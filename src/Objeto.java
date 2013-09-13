package obj3D;
import javax.media.j3d. * ;
import javax.vecmath. * ;

//clase que controla la representacion de cada elemento en el servidor
public class Objeto {
    Transform3D t3d; //clases para guardar y aplicar las transformaciones
    Transform3D rotacion = new Transform3D();
    Vector3f moveVec;
    private BranchGroup grupoObjeto; //nodo del que parten todos los objetos 3d que componen este objeto
    TransformGroup midPtTG; //agrupa objetos que pueden cambiar sus propiedades
    private Esfera esf; //objeto de la clase esfera que crea las esferas en 3D
    int id; //identificador del elemento, representado en el servidor
    float tamaño; //radio del objeto
    Vector3f posicion; //vector de posicion
    Vector3f rot; //vector de rotacion
    Vector3f color; //vector de color
    WrapObj3D w;

    public int getId() {
        return id;
    }
    //se inicializa con los vectores posicion, rotacion, color, tamaño
    public Objeto(int idd, Vector3f posxyz, Vector3f r, Vector3f c, float t, WrapObj3D ww) {
        t3d = new Transform3D(); //inicializa las variables
        moveVec = new Vector3f();
        grupoObjeto = new BranchGroup();
        w = ww;
        posicion = posxyz; //guarda sus parametros
        t3d.set(posicion); //inicializa su posicion
        midPtTG = new TransformGroup(t3d);
        midPtTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); // habilita su capacidad de transformacion
        midPtTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        grupoObjeto.addChild(midPtTG); //agrega el grupo al arbol
        rot = r; //guarda sus parametros
        color = c;
        tamaño = t;
        id = idd;
        esf = new Esfera(t, posicion, color, this); // genera el objeto en 3D
        midPtTG.addChild(esf.getBaseTG()); //agrega el objeto al arbol
        midPtTG.setUserData((Object) this);

    }

    //cambia de color el objeto
    public void setAPP(float x, float y, float z) {
        esf.colorear(x, y, z);
    }

    //devuelve el nodo principal del objeto para ser agregado a la raiz
    public BranchGroup getBG() {
        return grupoObjeto;
    }

    // función que aplica los cambios enviados por el servidor
    //selecciona los valores correspondientes usando la mascara
    void actualiza(String[] datos) {
        int masc = Integer.parseInt(datos[2]); //transorma la mascara a binario
        int bin1, bin2, bin3, bin4, bin5, bin6, bin7, bin8, bin9, bin10;
        int mod1, mod2, mod3, mod4, mod5, mod6, mod7, mod8, mod9, mod10;

        bin1 = masc / 2;
        mod1 = masc % 2;
        bin2 = bin1 / 2;
        mod2 = bin1 % 2;
        bin3 = bin2 / 2;
        mod3 = bin2 % 2;
        bin4 = bin3 / 2;
        mod4 = bin3 % 2;
        bin5 = bin4 / 2;
        mod5 = bin4 % 2;
        bin6 = bin5 / 2;
        mod6 = bin5 % 2;
        bin7 = bin6 / 2;
        mod7 = bin6 % 2;
        bin8 = bin7 / 2;
        mod8 = bin7 % 2;
        bin9 = bin8 / 2;
        mod9 = bin8 % 2;
        bin10 = bin9 / 2;
        mod10 = bin9 % 2;
        //comprueba si la mascara habilita cada uno de los valores
        //de ser asi coge el valor y aumenta el contador para que
        //el siguiente parametro compruebe si se trata de un valor
        //dirigido para ese parametro, de esta forma se descompone
        //el formato del mensaje
        int i = 3;
        if (mod1 == 1) {
            posicion.x = Float.valueOf(datos[i]).floatValue();
            moveVec.setX(posicion.x);
            i++;
        }
        if (mod2 == 1) {
            posicion.y = Float.valueOf(datos[i]).floatValue();
            moveVec.setY(posicion.y);
            i++;
        }
        if (mod3 == 1) {
            posicion.z = Float.valueOf(datos[i]).floatValue();
            moveVec.setZ(posicion.z);
            i++;
        }
        if (mod1 == 1 || mod2 == 1 || mod3 == 1) { //si detecta que ha habido algun cambio en la posicion
            t3d.setTranslation(moveVec);
            midPtTG.setTransform(t3d);
            if (w.id == id) {
                w.viewPoint(posicion.x, posicion.y, posicion.z);
            }
        }

        if (mod4 == 1) {
            rot.x = Float.valueOf(datos[i]).floatValue();
            rotacion.rotX(rot.x);
            i++;
        }
        if (mod5 == 1) {
            rot.y = Float.valueOf(datos[i]).floatValue();
            rotacion.rotY(rot.y);
            i++;
        }
        if (mod6 == 1) {
            rot.z = Float.valueOf(datos[i]).floatValue();
            rotacion.rotZ(rot.z);
            i++;
        }
        if (mod4 == 1 || mod5 == 1 || mod6 == 1) { //detecta si hay un cambio en rotacion
            midPtTG.getTransform(t3d);
            t3d.mul(rotacion);
            midPtTG.setTransform(t3d);
        }

        if (mod7 == 1) {
            color.x = Float.valueOf(datos[i]).floatValue();
            i++;
        }
        if (mod8 == 1) {
            color.y = Float.valueOf(datos[i]).floatValue();
            i++;
        }
        if (mod9 == 1) {
            color.z = Float.valueOf(datos[i]).floatValue();
            i++;
        }
        if (mod7 == 1 || mod8 == 1 || mod9 == 1) { //si alguno de los parametros de color cambia
            setAPP(color.x, color.y, color.z);
        }
        if (mod10 == 1) { //si cambia el tamaño
            if (tamaño != Float.valueOf(datos[i]).floatValue()) {
                escala(tamaño);
                tamaño = Float.valueOf(datos[i]).floatValue();
            }
        }
    }

    //ejecuta el cambio de tamaño.
    public void escala(float ta) {
        t3d.setScale(ta / tamaño);
        midPtTG.setTransform(t3d);
    }
}