package obj3D;

import javax.media.j3d. * ;
import javax.vecmath. * ;
import com.sun.j3d.utils.geometry.Sphere;


public class Esfera {
    private TransformGroup baseTG;
    private Transform3D t3d = new Transform3D();
    private Sphere esfera, foreEsfera, foreEsfera2, foreEsfera3, foreEsfera4, foreEsfera5;
    private TransformGroup jointXTG, jointYTG, jointZTG;
    public Vector3f color;
    float radio;

    //clase para generar el Objeto 3D
    public Esfera(float ra, Vector3f posnVec, Vector3f c, Objeto ob) {
        color = c;
        radio = ra;
        hacerEsfera();
    }
    //funcion que colorea el objeto
    public void colorear(float x, float y, float z) {
        esfera.setAppearance(setAPP(x, y, z, 1)); //pinta de forma transparente
        Appearance ap = setAPP(x, y, z, 0); //objetos opacos
        if (radio > 0.5) {
            foreEsfera.setAppearance(ap);
            foreEsfera2.setAppearance(ap);
            foreEsfera3.setAppearance(ap);
            foreEsfera4.setAppearance(ap);
        } else {
            foreEsfera5.setAppearance(ap);
        }
    }

    //funcion que genera una apariencia de color y transparencia
    //los parametros xyz son los RGB y t es el nivel de transparencia
    public Appearance setAPP(float x, float y, float z, int t) {
        Appearance esferaApp = new Appearance();
        TextureAttributes ta = new TextureAttributes();
        ta.setTextureMode(TextureAttributes.MODULATE);
        esferaApp.setTextureAttributes(ta);
        Color3f colorAmbiente, colorDifuso, colorEspecular, colorAlumEspecular;
        int brillo;
        if (t == 1) {
            colorAmbiente = new Color3f(x, y, z);
            colorDifuso = new Color3f(0, 0, 0);
            colorEspecular = new Color3f(0, 0, 0);
            colorAlumEspecular = new Color3f(0f, 0f, 0f);
            brillo = 128;
        } else {
            colorAmbiente = new Color3f(x, y, z);
            colorDifuso = new Color3f(0, 0, 0);
            colorEspecular = new Color3f(x, y, z);
            colorAlumEspecular = new Color3f(1f, 1f, 1f);
            brillo = 128;
        }
        Material esferaMaterial = new Material(colorAmbiente, colorDifuso, colorEspecular, colorAlumEspecular, brillo);
        esferaMaterial.setLightingEnable(true);
        esferaApp.setMaterial(esferaMaterial);
        esferaApp.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
        esferaApp.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
        if (t == 1) { //aplica la propiedad de transparencia
            TransparencyAttributes ta2 = new TransparencyAttributes(TransparencyAttributes.NICEST, 0f, TransparencyAttributes.BLEND_ONE, TransparencyAttributes.BLEND_ONE);
            esferaApp.setTransparencyAttributes(ta2);
        }

        return esferaApp;
    }

    //función que genera las o la esfera segun su radio
    private void hacerEsfera() {
        t3d.set(new Vector3f(0, 0, 0)); //iniciada en el centro
        baseTG = new TransformGroup(t3d);
        Appearance esferaApp = setAPP(color.x, color.y, color.z, 1); //color transparente
        esfera = new Sphere(radio, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, (int)(30), esferaApp); //genera una esfera
        esfera.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY); //le aplica las capacidades necesarias para poder modificarla en tiempo real
        esfera.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        esfera.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
        esfera.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
        esfera.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
        baseTG.addChild(esfera);

        esferaApp = setAPP(color.x, color.y, color.z, 0); //apariencia para las esferas de color oscuro
        t3d.set(new Vector3f(0, 0, 0));
        TransformGroup baseTopTG = new TransformGroup(t3d);
        baseTG.addChild(baseTopTG);

        //transformaciones en los 3 ejes para modificar su forma
        t3d.rotX(Math.toRadians(-90));
        jointXTG = new TransformGroup(t3d);
        jointXTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        jointXTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        baseTopTG.addChild(jointXTG);


        jointYTG = new TransformGroup();
        jointYTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        jointYTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        jointXTG.addChild(jointYTG);


        jointZTG = new TransformGroup();
        jointZTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        jointZTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        jointYTG.addChild(jointZTG);


        if (radio > 0.5) { //si es controlado por el usuario o por el ordenador
            //cabeza situada sobre la esfera, de radio la mitad de la esfera principal
            //tambien tiene las capacidades de modificacion de parametros
            t3d.set(new Vector3f(0, (0), radio + radio / 2));
            TransformGroup midTG = new TransformGroup(t3d);
            jointZTG.addChild(midTG);
            foreEsfera = new Sphere(radio / 2, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, 30, esferaApp);
            foreEsfera.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
            foreEsfera.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            foreEsfera.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            foreEsfera.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            foreEsfera.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
            midTG.addChild(foreEsfera);

            //dentro y
            t3d.set(new Vector3f(0, (radio / 2), 0));
            TransformGroup midTG2 = new TransformGroup(t3d);
            jointZTG.addChild(midTG2);
            foreEsfera2 = new Sphere(radio / 2, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, 30, esferaApp);
            foreEsfera2.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
            foreEsfera2.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            foreEsfera2.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            foreEsfera2.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            foreEsfera2.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
            midTG2.addChild(foreEsfera2);

            //dentro x1
            t3d.set(new Vector3f(radio - radio / 3, 0, 0));
            TransformGroup midTG3 = new TransformGroup(t3d);
            jointZTG.addChild(midTG3);
            foreEsfera3 = new Sphere(radio / 3, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, 30, esferaApp);
            foreEsfera3.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
            foreEsfera3.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            foreEsfera3.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            foreEsfera3.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            foreEsfera3.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
            midTG3.addChild(foreEsfera3);
            //dentro x2
            t3d.set(new Vector3f((radio - radio / 3) * -1, 0, 0));
            TransformGroup midTG4 = new TransformGroup(t3d);
            jointZTG.addChild(midTG4);
            foreEsfera4 = new Sphere(radio / 3, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, 30, esferaApp);
            foreEsfera4.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
            foreEsfera4.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            foreEsfera4.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            foreEsfera4.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            foreEsfera4.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
            midTG4.addChild(foreEsfera4);
        } else { //esfera para los objetos pequeños
            t3d.set(new Vector3f(0, (0), 0));
            TransformGroup midTG = new TransformGroup(t3d);
            jointZTG.addChild(midTG);
            foreEsfera5 = new Sphere(radio / 2, Sphere.GENERATE_NORMALS | Sphere.ENABLE_APPEARANCE_MODIFY, 30, esferaApp);
            foreEsfera5.setCapability(Sphere.ENABLE_APPEARANCE_MODIFY);
            foreEsfera5.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
            foreEsfera5.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
            foreEsfera5.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_READ);
            foreEsfera5.setCapability(Shape3D.ALLOW_APPEARANCE_OVERRIDE_WRITE);
            midTG.addChild(foreEsfera5);
        }
    }

    public TransformGroup getBaseTG() {
        return baseTG;
    }
    public Appearance getAppearance() {
        return esfera.getAppearance();
    }

}