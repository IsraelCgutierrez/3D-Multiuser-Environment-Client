package obj3D;
import java.awt.event. * ;
import java.awt. * ;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.j3d. * ;

//clase encargada de los eventos del teclado
public class KeyBehavior extends Behavior {
    private WakeupOnAWTEvent key;
    private WakeupOnAWTEvent keyp;
    private WakeupOnAWTEvent keyr;
    private ChatClient chat;

    //la clase se inicializa con la hebra de comunicaciones con el servidor
    public KeyBehavior(ChatClient c) {
        chat = c; //para enviar los datos del teclado
        key = new WakeupOnAWTEvent(KeyEvent.KEY_EVENT_MASK); //mascara de teclado
        keyp = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED); //si se ha presionado una tecla
        keyr = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED); //si se ha dejado de presionar una tecla
    }

    //espera un evento del teclado
    public void initialize() {
        wakeupOn(key);
    }

    //busca el evento y recoge la informacion
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                for (int i = 0; i < event.length; i++) {
                    if (event[i].getID() == KeyEvent.KEY_PRESSED) { //si se ha presionado una tecla
                        try {
                            processKeyEvent((KeyEvent) event[i], 0);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(KeyBehavior.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        wakeupOn(keyr);
                    }
                    if (event[i].getID() == KeyEvent.KEY_RELEASED) { //si se ha dejado de presionar una tecla
                        try {
                            processKeyEvent((KeyEvent) event[i], 1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(KeyBehavior.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        wakeupOn(keyp);
                    }
                }

            }
        }
    }

    //envia la informacion del teclado al servidor
    private void processKeyEvent(KeyEvent eventKey, int pr) throws InterruptedException {
        int SD, AD;
        if (eventKey.isShiftDown()) {
            SD = 1;
        } else {
            SD = 0;
        } //estado de la tecla shif
        if (eventKey.isAltDown()) {
            AD = 1;
        } else {
            AD = 0;
        } //estado de la tecla Alt
        int k = eventKey.getKeyCode();
        if (k != KeyEvent.VK_ESCAPE) {
            chat.envia(pr + ":" + k + ":" + SD + ":" + AD);
        } else {
            chat.envia(2 + ":" + chat.id);
        }
    }
}