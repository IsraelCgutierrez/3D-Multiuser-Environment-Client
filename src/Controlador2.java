package obj3D;
import java.io.IOException;
import javax.vecmath.Point3d;

//clase que interpreta los comandos del servidor y los plasma en la escena
public class Controlador2 {
    WrapObj3D w;
    ChatClient chat;
    Objeto[] objetos;
    int thisid;
    //como parametro la clase que contiene el nodo principal y el universo
    public Controlador2(WrapObj3D ww) {
        w = ww;
        int maximos = 256; //usuarios maximos
        objetos = new Objeto[maximos]; //vector de objetos
        for (int i = 0; i < 256; i++) //se rellenan todos con null para poder identificarlos
        objetos[i] = null;
    }
    //esta función separa los comandos por cada objeto que el servidor envia
    //para poder actualizar cada uno, estos comandos estan separados por el caracter /
    //devuelve 0 si no ha habido ningun error
    public int llega(String delServidor) {
        int r = 0; //variable de control de errores, si es 0 no hay error.
        String[] cadaObjeto;
        cadaObjeto = delServidor.split("/"); //separa los comandos para cada objeto
        for (int i = 0; i < cadaObjeto.length; i++) { //bucle para analizar cada comando
            r += actualizarObjeto(cadaObjeto[i]);
        }
        return r;
    }

    //función que analiza los comandos del servidor
    //comprueba el formato del mensaje por si ha llegado con errores
    //comprueba los casos del servidor
    //si el servidor envia primero un valor 0 se trata de un mensaje
    //que contiene el id nuevo del cliente y todos sus nuevos parametros
    //si se trata del valor 1 es una actualizacion de parametros del objeto
    //identificado con el id
    //si se trata del valor 2 es una eliminacion del objeto
    private int actualizarObjeto(String string) {
        int r = 0; //variable de control de errores, si existe un error se le suma 1
        String[] datos;
        datos = string.split(","); //pasa a un vector los parametros separados por ,
        if (datos.length >= 3) { //comprueba si tiene los primeros 3 comandos con los que trabajar
            int servidor = Integer.parseInt(datos[0]); //tipo de mensaje que envia el servidor
            int id = Integer.parseInt(datos[1]); //identificador del objeto al que se le aplica los cambios
            Objeto o = null; //objeto que será modificado
            if (servidor == 0 || servidor == 1) { //en el caso de que sea nuevo o modificado
                try {
                    o = objetos[id]; //comprueba si el objeto existe y esta creado
                } catch (Exception e) {
                    o = null; //devuelve null si no esta creado
                }

                if (o == null) { //no existe hay que crearlo
                    if (datos.length == 13) { //comprueba si tiene los dados suficientes para crearlo
                        float x = Float.valueOf(datos[3]).floatValue(); //parametros del objeto nuevo
                        float y = Float.valueOf(datos[4]).floatValue();
                        float z = Float.valueOf(datos[5]).floatValue();
                        float rx = Float.valueOf(datos[6]).floatValue();
                        float ry = Float.valueOf(datos[7]).floatValue();
                        float rz = Float.valueOf(datos[8]).floatValue();
                        float cx = Float.valueOf(datos[9]).floatValue();
                        float cy = Float.valueOf(datos[10]).floatValue();
                        float cz = Float.valueOf(datos[11]).floatValue();
                        float t = Float.valueOf(datos[12]).floatValue();
                        if (servidor == 0) { //asignacion de identificacion
                            try { //espera de 1 segundo para que pueda crear y renderizar el objeto
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            o = w.addObjeto(id, x, y, z, rx, ry, rz, cx, cy, cz, t); //crea el nuevo Objeto
                            o.actualiza(datos); //inicializa las variables para actualizar
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                            objetos[id] = o; //agrega al vector objetos
                            thisid = id; //guarda el identificador
                            w.id = thisid; //agrega el identificador al escenario
                            chat.setID(thisid); //agrega el identificador a la hebra de comunicaciones
                            w.initUserPosition(x, y, z); //inicializa la posicion de la camara
                            w.orbit.setRotationCenter(new Point3d(x, y, z)); //actualiza el centro de rotacion de la camara
                        } else { //se ha conectado un usuario nuevo, se crea el objeto
                            o = w.addObjeto(id, x, y, z, rx, ry, rz, cx, cy, cz, t);
                            o.actualiza(datos);
                            objetos[id] = o;
                        }
                    } else { //usuario desconocido sin datos suficientes para generar nuevo objeto
                        try {
                            chat.envia("3:" + datos[1]); //pide al servidor informacion sobre el objeto
                        } catch (InterruptedException ex) {
                            System.out.print("no puede recuperar informacion de: " + string);
                        }
                    }
                } else { //actualiza los parametros de un objeto ya creado
                    o.actualiza(datos);
                }
            }
            if (servidor == 2) { //comando para borrar un objeto del escenario
                if (id == thisid) {
                    try {
                        w.s.close();
                    } catch (IOException ex) {
                        System.out.print("imposible cerrar el socket");
                    }
                    System.exit(0);
                } else {
                    try {
                        o = objetos[id]; //busca el objeto
                    } catch (Exception e) {
                        o = null;
                    }
                    if (o == null) { //si no lo encuentra no pide denuevo la informacion del objeto al servidor
                        System.out.println("intenta borrar un objeto no creado");
                    } else { //elimina el objeto del vector y de la escena
                        objetos[id] = null;
                        w.borra(o);
                    }
                }
            }
        } else { //se ha producido un error en el mensaje
            r += 1;
            System.out.print("Error formato mensaje:" + string);
        }
        return r;
    }

    //función para agregar la hebra
    void addC(ChatClient c) {
        chat = c;
    }
}