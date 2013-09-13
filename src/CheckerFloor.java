package obj3D;
import javax.media.j3d. * ;
import javax.vecmath. * ;
import java.util.ArrayList;

//clase para crear el suelo
public class CheckerFloor {
    private final static int FLOOR_LEN = 30;
    private final static Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
    private final static Color3f blank = new Color3f(0.0f, 0.0f, 0.0f);

    private BranchGroup floorBG;

    public CheckerFloor() {
        ArrayList < Point3f > blueCoords = new ArrayList < Point3f > ();
        ArrayList < Point3f > greenCoords = new ArrayList < Point3f > ();
        floorBG = new BranchGroup();

        boolean isBlue;
        for (int z = -FLOOR_LEN / 2; z <= (FLOOR_LEN / 2) - 1; z++) {
            isBlue = (z % 2 == 0) ? true : false;
            for (int x = -FLOOR_LEN / 2; x <= (FLOOR_LEN / 2) - 1; x++) {
                if (isBlue) createCoords(x, z, blueCoords);
                else createCoords(x, z, greenCoords);
                isBlue = !isBlue;
            }
        }
        floorBG.addChild(new ColouredTiles(blueCoords, white));
        floorBG.addChild(new ColouredTiles(greenCoords, blank));
    }


    private void createCoords(int x, int z, ArrayList < Point3f > coords) {
        float y = (float) 0;
        Point3f p1 = new Point3f(x, y, z + 1.0f);
        Point3f p2 = new Point3f(x + 1.0f, y, z + 1.0f);
        Point3f p3 = new Point3f(x + 1.0f, y, z);
        Point3f p4 = new Point3f(x, y, z);
        coords.add(p1);
        coords.add(p2);
        coords.add(p3);
        coords.add(p4);
    }

    public BranchGroup getBG() {
        return floorBG;
    }


}