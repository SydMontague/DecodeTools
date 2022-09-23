package net.digimonworld.decodetools.gui.util.LinAlg;

public class TransformMatrix extends Matrix {
    public TransformMatrix(float[][] contents) {
       super(contents);
       if (contents.length != 4 || contents[0].length != 4) // Raggedness will be checked by superclass
           throw new IllegalArgumentException("Attempted to initialize a TransformMatrix with an incompatible float[][]");
    }

    public static TransformMatrix fromTransforms(Vector3 pos, Quaternion quat, Vector3 scale) {
        RotationMatrix rot_matrix = quat.toMatrix();

        float[][] out = new float[4][4];

        // Inner product of (rotation . scale) - scale is a diagonal matrix
        // Do it manually to save flops - could do it automatically by subclassing Matrix as DiagonalMatrix and
        // setting up a bunch of method overloads, but is it worth it for this little code?
        out[0][0] = rot_matrix.get(0, 0) * scale.get(0);
        out[1][0] = rot_matrix.get(1, 0) * scale.get(0);
        out[2][0] = rot_matrix.get(2, 0) * scale.get(0);

        out[0][1] = rot_matrix.get(0, 1) * scale.get(1);
        out[1][1] = rot_matrix.get(1, 1) * scale.get(1);
        out[2][1] = rot_matrix.get(2, 1) * scale.get(1);

        out[0][2] = rot_matrix.get(0, 2) * scale.get(2);
        out[1][2] = rot_matrix.get(1, 2) * scale.get(2);
        out[2][2] = rot_matrix.get(2, 2) * scale.get(2);

        // Inner product of (translation . rotscale) - translation is a Frobenius matrix
        out[0][3] = pos.get(0);
        out[1][3] = pos.get(1);
        out[2][3] = pos.get(2);

        // Add affine coordinate
        out[3][0] = 0;
        out[3][1] = 0;
        out[3][2] = 0;
        out[3][3] = 1;

        return new TransformMatrix(out);
    }



}
