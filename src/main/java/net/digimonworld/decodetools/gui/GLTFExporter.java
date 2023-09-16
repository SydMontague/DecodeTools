package net.digimonworld.decodetools.gui;

import static de.javagl.jgltf.model.GltfConstants.GL_ARRAY_BUFFER;
import static de.javagl.jgltf.model.GltfConstants.GL_ELEMENT_ARRAY_BUFFER;
import static de.javagl.jgltf.model.GltfConstants.GL_FLOAT;
import static de.javagl.jgltf.model.GltfConstants.GL_UNSIGNED_BYTE;
import static de.javagl.jgltf.model.GltfConstants.GL_UNSIGNED_INT;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.Asset;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.io.GltfWriter;
import net.digimonworld.decodetools.core.Vector4;
import net.digimonworld.decodetools.res.kcap.HSMPKCAP;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.HSEMPayload;
import net.digimonworld.decodetools.res.payload.RTCLPayload;
import net.digimonworld.decodetools.res.payload.TNOJPayload;
import net.digimonworld.decodetools.res.payload.XDIOPayload;
import net.digimonworld.decodetools.res.payload.XTVOPayload;
import net.digimonworld.decodetools.res.payload.hsem.HSEMDrawEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMJointEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMMaterialEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMTextureEntry;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOAttribute;
import net.digimonworld.decodetools.res.payload.xtvo.XTVORegisterType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOVertex;

public class GLTFExporter {
    private static final String BUFFER_URI = "data:application/octet-stream;base64,";

    private final HSMPKCAP hsmp;
    private final GlTF instance;

    private Map<Short, Short> jointAssignment = new HashMap<>();
    private Map<Short, Short> textureAssignment = new HashMap<>();
    private HSEMMaterialEntry activeMaterial = null;

    private int geomId = 0;
    private Node rootNode = new Node();

    public GLTFExporter(HSMPKCAP hsmp) {
        this.hsmp = hsmp;
        this.instance = new GlTF();

        initialize();
    }

    public void export(File output) {
        File outputFile = new File(output, hsmp.getName() + ".gltf");
        try (OutputStream os = new FileOutputStream(outputFile)) {
            GltfWriter gltfWriter = new GltfWriter();
            gltfWriter.write(instance, os);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        createAssetTag();
        createTextures();
        createJoints();
        createLocations();
        createGeometry();

        Scene scene = new Scene();
        rootNode.setName(hsmp.getName());
        instance.addNodes(rootNode);
        if (hsmp.getTNOJ() != null)
            scene.addNodes(0); // skin root
        scene.addNodes(instance.getNodes().size() - 1); // Add each node to the scene

        instance.addScenes(scene);
        instance.setScene(instance.getScenes().size() - 1);
    }

    private void createAssetTag() {
        Asset inputAsset = new Asset();
        inputAsset.setVersion("2.0");
        inputAsset.setGenerator("jgltf-parent-2.0.3");
        instance.setAsset(inputAsset);
    }

    private void createTextures() {
        int imageId = 0;
        for (GMIOPayload gmio : hsmp.getGMIP().getGMIOEntries()) {
            String imageName = gmio.hasName() ? escapeName(gmio.getName()) : "image-" + imageId++;

            // Convert Buffered Images to Byte Array
            ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
            try {
                ImageIO.write(gmio.getImage(), "PNG", imagebuffer);
            }
            catch (IOException e) {
                // shouldn't ever happen
                e.printStackTrace();
            }

            // Embed Textures into GLTF
            Image image = new Image();
            image.setName(imageName);
            image.setUri("data:image/png;base64," + Base64.getEncoder().encodeToString(imagebuffer.toByteArray()));
            instance.addImages(image);

            // Create Texture and link it to the Image
            Texture texture = new Texture();
            texture.setName(imageName + "_texture");
            texture.setSource(instance.getImages().indexOf(image)); // Set the image index
            instance.addTextures(texture);

            // Create Material and link it to the Texture
            // TODO use actual material data from LRTM section
            Material material = new Material();
            material.setDoubleSided(true);
            material.setName(imageName + "_material");

            MaterialPbrMetallicRoughness pbrMetallicRoughness = new MaterialPbrMetallicRoughness();
            TextureInfo baseColorTextureInfo = new TextureInfo();
            baseColorTextureInfo.setIndex(instance.getTextures().indexOf(texture)); // Set the texture index
            pbrMetallicRoughness.setBaseColorTexture(baseColorTextureInfo);
            material.setPbrMetallicRoughness(pbrMetallicRoughness);

            instance.addMaterials(material);
        }
    }

    private void createJoints() {
        if (hsmp.getTNOJ() == null)
            return;

        List<float[]> matrixList = new ArrayList<>();

        for (int i = 0; i < hsmp.getTNOJ().getEntryCount(); i++) {
            TNOJPayload j = hsmp.getTNOJ().get(i);

            float[] rotation = new float[] { j.getRotationX(), j.getRotationY(), j.getRotationZ(), j.getRotationW() };
            float[] scale = new float[] { j.getLocalScaleX(), j.getLocalScaleY(), j.getLocalScaleZ() };
            float[] translation = new float[] { j.getXOffset(), j.getYOffset(), j.getZOffset() };

            Node node = new Node();
            node.setName(j.getName());
            node.setScale(scale);
            node.setRotation(rotation);
            node.setTranslation(translation);
            instance.addNodes(node);

            matrixList.add(j.getOffsetMatrix());

            if (j.getParentId() != -1) {
                Node parent = instance.getNodes().get(j.getParentId());

                // Add the current node as a child of the parent
                if (parent != null)
                    parent.addChildren(instance.getNodes().size() - 1);
            }
        }

        int bindPoseBuffer = matrixListToBuffer(matrixList);
        int bindPoseBufferView = createBufferView(bindPoseBuffer, 0, "bindPoseBufferView");
        int bindAccessor = createAccessor(bindPoseBufferView, GL_FLOAT, matrixList.size(), "MAT4", "BINDS");

        Skin jointsSkin = new Skin();
        jointsSkin.setName(hsmp.getName() + "-joint");
        for (Node node2 : instance.getNodes())
            jointsSkin.addJoints(instance.getNodes().indexOf(node2));

        jointsSkin.setInverseBindMatrices(bindAccessor);

        instance.addSkins(jointsSkin);
    }

    private void createLocations() {
        if (hsmp.getRTCL() == null)
            return;

        for (int i = 0; i < hsmp.getRTCL().getEntryCount(); i++) {
            RTCLPayload loc = hsmp.getRTCL().get(i);

            Map<String, String> extra = new HashMap<>();
            extra.put("loc_id", Integer.toString(loc.getLocIndex()));

            Node node = new Node();
            node.setName(loc.getName());
            // gltf prefers for default transoform matrices to be not specified
            if (!isIdentityMatrix(loc.getMatrix()))
                node.setMatrix(mirrorMatrix(loc.getMatrix()));
            node.setExtras(extra);
            instance.addNodes(node);

            if (loc.getParentBone() != -1) {
                Node parent = instance.getNodes().get(loc.getParentBone());

                // Add the current node as a child of the parent
                if (parent != null)
                    parent.addChildren(instance.getNodes().size() - 1);
            }
        }
    }

    private void createGeometry() {
        for (HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries())
            for (HSEMEntry entry : hsem.getEntries())
                processHSEM(entry);
    }

    private void processHSEMDraw(HSEMDrawEntry draw) {
        final XTVOPayload xtvo = hsmp.getXTVP().get(draw.getVertexId());
        final XDIOPayload xdio = hsmp.getXDIP().get(draw.getIndexId());
        final int vertexCount = xtvo.getVertices().size();
        MeshPrimitive primitive = new MeshPrimitive();

        // build indices
        List<Integer> indices = xdio.getFaces().stream()
                                    .flatMap(a -> Stream.of(a.getVert1(), a.getVert2(), a.getVert3()))
                                    .collect(Collectors.toList());

        int facesBuffer = intListToBuffer(indices);
        int indexBufferView = createBufferView(facesBuffer, GL_ELEMENT_ARRAY_BUFFER, "facesBufferView");
        int indexAccessor = createAccessor(indexBufferView, GL_UNSIGNED_INT, indices.size(), "SCALAR", "INDICES");
        primitive.setIndices(indexAccessor);

        // build positions
        int posBuffer = vertexAttribToBuffer(xtvo.getVertices(), XTVORegisterType.POSITION);
        int posBufferView = createBufferView(posBuffer, GL_ARRAY_BUFFER, "posBufferView");
        int posAccessor = createPosAccessor(posBufferView, xtvo.getVertices());
        primitive.addAttributes("POSITION", posAccessor);

        // build normals
        if (xtvo.getAttribute(XTVORegisterType.NORMAL).isPresent()) {
            int normalBuffer = vertexAttribToBuffer(xtvo.getVertices(), XTVORegisterType.NORMAL);
            int normalBufferView = createBufferView(normalBuffer, GL_ARRAY_BUFFER, "normalBufferView");
            int normalAccessor = createAccessor(normalBufferView, GL_FLOAT, vertexCount, "VEC3", "NORMALS");
            primitive.addAttributes("NORMAL", normalAccessor);
        }

        // build colors
        if (xtvo.getAttribute(XTVORegisterType.COLOR).isPresent()) {
            int colorsBuffer = vertexAttribToBuffer(xtvo.getVertices(), XTVORegisterType.COLOR);
            int colorsBufferView = createBufferView(colorsBuffer, GL_ARRAY_BUFFER, "colorBufferView");
            int colorsAccessor = createAccessor(colorsBufferView, GL_FLOAT, vertexCount, "VEC4", "COLOR");
            primitive.addAttributes("COLOR_0", colorsAccessor);
        }

        // build UVSet 0
        if (xtvo.getAttribute(XTVORegisterType.TEXTURE0).isPresent()) {
            int tex0Buffer = textureCoordToBuffer(xtvo.getVertices(), xtvo.getMTex0());
            int tex0BufferView = createBufferView(tex0Buffer, GL_ARRAY_BUFFER, "tex0BufferView");
            int tex0Accessor = createAccessor(tex0BufferView, GL_FLOAT, vertexCount, "VEC2", "TEXTURE0");
            primitive.addAttributes("TEXCOORD_0", tex0Accessor);
        }

        // build UVSet 1
        if (xtvo.getAttribute(XTVORegisterType.TEXTURE1).isPresent()) {
            int tex1Buffer = textureCoordToBuffer(xtvo.getVertices(), xtvo.getMTex1());
            int tex1BufferView = createBufferView(tex1Buffer, GL_ARRAY_BUFFER, "tex1BufferView");
            int tex1Accessor = createAccessor(tex1BufferView, GL_FLOAT, vertexCount, "VEC2", "TEXTURE1");
            primitive.addAttributes("TEXCOORD_1", tex1Accessor);
        }

        // build vertex weights
        if (xtvo.getAttribute(XTVORegisterType.WEIGHT).isPresent()) {
            int weightsBuffer = vertexAttribToBuffer(xtvo.getVertices(), XTVORegisterType.WEIGHT);
            int weightsBufferView = createBufferView(weightsBuffer, GL_ARRAY_BUFFER, "weightsBufferView");
            int weightsAccessor = createAccessor(weightsBufferView, GL_FLOAT, vertexCount, "VEC4", "WEIGHTS");
            primitive.addAttributes("WEIGHTS_0", weightsAccessor);

        }

        // build joint mapping
        if (xtvo.getAttribute(XTVORegisterType.IDX).isPresent()) {
            int jointsBuffer = jointDataToBuffer(xtvo.getVertices(), jointAssignment);
            int jointsBufferView = createBufferView(jointsBuffer, GL_ARRAY_BUFFER, "jointsBufferView");
            int jointsAccessor = createAccessor(jointsBufferView, GL_UNSIGNED_BYTE, vertexCount, "VEC4", "JOINTS");
            primitive.addAttributes("JOINTS_0", jointsAccessor);
        }

        // TODO deal with materials proper, support multiple textures and LRTM
        if (textureAssignment.getOrDefault((short) 0, (short) -1) != -1)
            primitive.setMaterial(textureAssignment.get((short) 0).intValue());

        // TODO add extras
        
        Mesh mesh = new Mesh();
        mesh.addPrimitives(primitive);
        instance.addMeshes(mesh);

        Node node = new Node();
        node.setName("geom-" + geomId++);
        node.setMesh(instance.getMeshes().size() - 1);
        if (xtvo.getAttribute(XTVORegisterType.IDX).isPresent())
            node.setSkin(0);
        instance.addNodes(node);
        rootNode.addChildren(instance.getNodes().size() - 1);
    }

    private void processHSEM(HSEMEntry entry) {

        switch (entry.getHSEMType()) {
            // unknown/unhandled
            case UNK03:
            case UNK07:
                break;

            case JOINT:
                ((HSEMJointEntry) entry).getJointAssignment().forEach(jointAssignment::put);
                break;

            case TEXTURE:
                ((HSEMTextureEntry) entry).getTextureAssignment().forEach(textureAssignment::put);
                break;

            case MATERIAL:
                activeMaterial = ((HSEMMaterialEntry) entry);
                break;

            case DRAW:
                processHSEMDraw((HSEMDrawEntry) entry);
                break;
        }

    }

    // =========================
    // Accessor and View builder
    // =========================

    private int createPosAccessor(int bufferView, List<XTVOVertex> vertices) {
        Number[] minValues = new Number[3];
        Number[] maxValues = new Number[3];

        for (XTVOVertex vertex : vertices) {
            List<Number> entry = vertex.getParameter(XTVORegisterType.POSITION).getValue();

            minValues[0] = Math.min(minValues[0].floatValue(), entry.get(0).floatValue());
            minValues[1] = Math.min(minValues[1].floatValue(), entry.get(1).floatValue());
            minValues[2] = Math.min(minValues[2].floatValue(), entry.get(2).floatValue());

            maxValues[0] = Math.max(maxValues[0].floatValue(), entry.get(0).floatValue());
            maxValues[1] = Math.max(maxValues[1].floatValue(), entry.get(1).floatValue());
            maxValues[2] = Math.max(maxValues[2].floatValue(), entry.get(2).floatValue());
        }

        Accessor accessor = new Accessor();
        accessor.setBufferView(bufferView);
        accessor.setComponentType(GL_FLOAT);
        accessor.setCount(vertices.size());
        accessor.setType("VEC3");
        accessor.setMin(minValues);
        accessor.setMax(maxValues);
        accessor.setName("POS");
        instance.addAccessors(accessor);

        return instance.getAccessors().size() - 1;
    }

    private int createAccessor(int bufferView, int componentType, int count, String type, String name) {
        Accessor accessor = new Accessor();
        accessor.setBufferView(bufferView);
        accessor.setComponentType(componentType);
        accessor.setCount(count);
        accessor.setType(type);
        accessor.setName(name);
        instance.addAccessors(accessor);
        return instance.getAccessors().size() - 1;
    }

    private int createBufferView(int buffer, int target, String name) {
        BufferView bufferView = new BufferView();
        bufferView.setBuffer(buffer);
        bufferView.setByteOffset(0);
        bufferView.setByteLength(instance.getBuffers().get(buffer).getByteLength());
        if (target != 0)
            bufferView.setTarget(target);
        bufferView.setName(name);
        instance.addBufferViews(bufferView);
        return instance.getBufferViews().size() - 1;
    }

    // ==============
    // Buffer Builder
    // ==============

    private int intListToBuffer(List<Integer> data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.size() * Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i : data)
            buffer.putInt(i);
        buffer.flip(); // Prepare the buffer for reading

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setByteLength(buffer.remaining());
        gltfBuffer.setUri(BUFFER_URI + Base64.getEncoder().encodeToString(buffer.array()));
        instance.addBuffers(gltfBuffer);
        return instance.getBuffers().size() - 1;
    }

    private int textureCoordToBuffer(List<XTVOVertex> vertices, float[] mTex) {
        ByteBuffer buffer = ByteBuffer.allocate(vertices.size() * 2 * Float.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        Vector4 mTex00 = new Vector4(mTex[2], 0f, 0f, mTex[0]);
        Vector4 mTex01 = new Vector4(0f, mTex[3], 0f, mTex[1]);

        for (XTVOVertex vertex : vertices) {
            Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(XTVORegisterType.TEXTURE0);
            if (entry == null)
                continue;

            Vector4 uvs = new Vector4(entry.getKey().getValue(entry.getValue().get(0)),
                                      entry.getKey().getValue(entry.getValue().get(1)), 0f, 1f);

            // Flip the V coordinate
            float u = uvs.dot(mTex00);
            float v = 1.0f - uvs.dot(mTex01);
            buffer.putFloat(u);
            buffer.putFloat(v);
        }

        buffer.flip(); // Prepare the buffer for reading

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setByteLength(buffer.remaining());
        gltfBuffer.setUri(BUFFER_URI + Base64.getEncoder().encodeToString(buffer.array()));
        instance.addBuffers(gltfBuffer);
        return instance.getBuffers().size() - 1;
    }

    private int vertexAttribToBuffer(List<XTVOVertex> vertices, XTVORegisterType type) {
        List<Float> floatList = vertices.stream().map(a -> a.getParameter(type))
                                        .flatMap(a -> a.getValue().stream().map(b -> a.getKey().getValue(b)))
                                        .collect(Collectors.toList());

        // Convert List<Float> to ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(floatList.size() * Float.BYTES);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (float f : floatList)
            byteBuffer.putFloat(f);

        byteBuffer.flip(); // Prepare the buffer for reading

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setByteLength(byteBuffer.remaining());
        gltfBuffer.setUri(BUFFER_URI + Base64.getEncoder().encodeToString(byteBuffer.array()));
        instance.addBuffers(gltfBuffer);

        return instance.getBuffers().size() - 1;
    }

    private int jointDataToBuffer(List<XTVOVertex> vertices, Map<Short, Short> jointMapping) {
        ByteBuffer jointsBuffer = ByteBuffer.allocate(vertices.size() * 4);
        jointsBuffer.order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < vertices.size(); i++) {
            XTVOVertex vertex = vertices.get(i);
            Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(XTVORegisterType.IDX);
            Entry<XTVOAttribute, List<Number>> weight = vertex.getParameter(XTVORegisterType.WEIGHT);

            for (int j = 0; j < 4; j++) {
                int joint = jointMapping.get((short) (entry.getValue().get(j).intValue() / 3));
                if (weight.getValue().get(j).floatValue() != 0.0f)
                    jointsBuffer.put((byte) joint);
                else
                    jointsBuffer.put((byte) 0);
            }
        }
        jointsBuffer.flip();

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setUri(BUFFER_URI + Base64.getEncoder().encodeToString(jointsBuffer.array()));
        gltfBuffer.setByteLength(jointsBuffer.remaining());
        instance.addBuffers(gltfBuffer);

        return instance.getBuffers().size() - 1;
    }

    private int matrixListToBuffer(List<float[]> matrices) {
        ByteBuffer mBuffer = ByteBuffer.allocate(matrices.size() * 16 * 4);
        mBuffer.order(ByteOrder.LITTLE_ENDIAN);

        // Java doesn't have a Arrays.stream overload for float arrays, doing everything in the steam would be a PITA.
        List<float[]> mirroredMatrices = matrices.stream().map(GLTFExporter::mirrorMatrix).collect(Collectors.toList());

        for (float[] matrix : mirroredMatrices)
            for (float value : matrix)
                mBuffer.putFloat(value);

        mBuffer.flip();

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setUri(BUFFER_URI + Base64.getEncoder().encodeToString(mBuffer.array()));
        gltfBuffer.setByteLength(mBuffer.remaining());
        instance.addBuffers(gltfBuffer);

        return instance.getBuffers().size() - 1;
    }

    // ================
    // Helper Functions
    // ================

    private static boolean isIdentityMatrix(float[] matrix) {
        // @formatter:off
        float[] identity = new float[] { 1.0f, 0.0f, 0.0f, 0.0f, 
                                         0.0f, 1.0f, 0.0f, 0.0f, 
                                         0.0f, 0.0f, 1.0f, 0.0f, 
                                         0.0f, 0.0f, 0.0f, 1.0f };
        // @formatter:on

        if (matrix.length != 16)
            return false;

        for (int i = 0; i < 16; i++)
            if (identity[i] != matrix[i])
                return false;

        return true;
    }

    private static float[] mirrorMatrix(float[] matrix) {
        float[] mirroredMatrix = new float[16];

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                mirroredMatrix[i * 4 + j] = matrix[j * 4 + i];

        return mirroredMatrix;
    }

    private static String escapeName(String string) {
        return string.replace("$", "_");
    }
}
