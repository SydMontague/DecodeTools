package net.digimonworld.decodetools.gui;

import net.digimonworld.decodetools.core.Vector4;
import net.digimonworld.decodetools.res.kcap.HSMPKCAP;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.HSEMPayload;
import net.digimonworld.decodetools.res.payload.TNOJPayload;
import net.digimonworld.decodetools.res.payload.XDIOPayload;
import net.digimonworld.decodetools.res.payload.XTVOPayload;
import net.digimonworld.decodetools.res.payload.hsem.HSEMDrawEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntryType;
import net.digimonworld.decodetools.res.payload.hsem.HSEMJointEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMTextureEntry;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOAttribute;
import net.digimonworld.decodetools.res.payload.xtvo.XTVORegisterType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOVertex;

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
import de.javagl.jgltf.model.io.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class GLTFExporter {
    private final HSMPKCAP hsmp;
    private int currentOffset = 0;
    private Buffer currentBuffer; // Track the current buffer

    public GLTFExporter(HSMPKCAP hsmp) {
        this.hsmp = hsmp;
    }

    public void export(File output) throws IOException {

        GlTF gltf = new GlTF();

        Asset inputAsset = new Asset();
        inputAsset.setVersion("2.0");
        inputAsset.setGenerator("jgltf-parent-2.0.3");
        gltf.setAsset(inputAsset);

        String meshName = null;

        getTextures(hsmp, gltf);
        String containerNodeName = null;

        int numHSEMPayloads = hsmp.getHSEM().getHSEMEntries().size();
        System.out.println("Number of HSEM Payloads : " + numHSEMPayloads);

        int meshId = 0;
        int geomId = 0;

        // Process joints and create joint nodes
        List<Node> jointNodes = new ArrayList<>();
        List<Node> containerNodesList = new ArrayList<>();

        List<float[]> matrixList = new ArrayList<>();
        Map<Integer, Node> jointMap = new HashMap<>();

        if (hsmp.getTNOJ() != null) {

            for (int i = 0; i < hsmp.getTNOJ().getEntryCount(); i++) {

                TNOJPayload j = hsmp.getTNOJ().get(i);

                float[] rotation = new float[] { j.getRotationX(), j.getRotationY(), j.getRotationZ(),
                                                 j.getRotationW() };
                float[] scale = new float[] { j.getLocalScaleX(), j.getLocalScaleY(), j.getLocalScaleZ() };
                float[] translation = new float[] { j.getXOffset(), j.getYOffset(), j.getZOffset() };
                Node joint = new Node();
                joint.setName(j.getName());
                joint.setScale(scale);

                joint.setRotation(rotation);
                joint.setTranslation(translation);
                jointNodes.add(joint);
                jointMap.put(i, joint);
                matrixList.add(j.getOffsetMatrix());

                if (j.getParentId() != -1) {
                    Node parent = jointMap.get(j.getParentId());
                    if (parent != null) {
                        // Add the current node as a child of the parent
                        parent.addChildren(i);
                    }
                }
            }
            gltf.setNodes(jointNodes);

            if (jointNodes.size() > 1) {
                Skin jointsSkin = new Skin();
                jointsSkin.setName(hsmp.getName() + "-joint");
                for (Node node2 : jointNodes) {
                    jointsSkin.addJoints(jointNodes.indexOf(node2));
                }
                jointsSkin.setInverseBindMatrices(6); // Accessor is always the same so for now it's fine to hardcode
                // this value here...

                gltf.addSkins(jointsSkin);
            }
        }

        for (HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries()) {

            Node containerNode = new Node();
            containerNodeName = hsmp.getName() + "-mesh." + meshId;
            containerNode.setName(containerNodeName);
            gltf.addNodes(containerNode);
            containerNodesList.add(containerNode);
            Map<Short, Short> currentAssignments = new HashMap<>();
            short currentTexture = -1;

            for (HSEMEntry entry : hsem.getEntries()) {

                switch (entry.getHSEMType()) {
                    case JOINT:
                        ((HSEMJointEntry) entry).getJointAssignment().forEach(currentAssignments::put);
                        break;
                    case TEXTURE:
                        currentTexture = ((HSEMTextureEntry) entry).getTextureAssignment().getOrDefault((short) 0,
                                                                                                        currentTexture);
                        break;
                    default:
                        break;
                }

                if (entry.getHSEMType() != HSEMEntryType.DRAW)
                    continue;

                HSEMDrawEntry draw = (HSEMDrawEntry) entry;
                XTVOPayload xtvo = hsmp.getXTVP().get(draw.getVertexId());
                XDIOPayload xdio = hsmp.getXDIP().get(draw.getIndexId());

                List<Integer> indices = new ArrayList<>();
                xdio.getFaces().forEach(face -> {
                    indices.add(face.getVert1());
                    indices.add(face.getVert2());
                    indices.add(face.getVert3());
                });

                Buffer posBuffer = vertexAttribToBuffer(xtvo, xtvo.getVertices(), XTVORegisterType.POSITION);
                Buffer normalBuffer = vertexAttribToBuffer(xtvo, xtvo.getVertices(), XTVORegisterType.NORMAL);
                Buffer tex0Buffer = textureCoordToBuffer(xtvo, XTVORegisterType.TEXTURE0);
                Buffer tex1Buffer = textureCoordToBuffer(xtvo, XTVORegisterType.TEXTURE1);
                Buffer colorsBuffer = vertexAttribToBuffer(xtvo, xtvo.getVertices(), XTVORegisterType.COLOR);
                Buffer facesBuffer = intListToBuffer(indices);
                Buffer jointsBuffer = jointDataToBuffer(xtvo, xtvo.getVertices(), currentAssignments);
                Buffer weightsBuffer = vertexAttribToBuffer(xtvo, xtvo.getVertices(), XTVORegisterType.WEIGHT);
                Buffer bindPoseBuffer = matrixListToBuffer(matrixList, jointNodes);

                Stream.of(posBuffer, normalBuffer, tex0Buffer, tex1Buffer, colorsBuffer, facesBuffer, jointsBuffer,
                          weightsBuffer, bindPoseBuffer)
                      .filter(Objects::nonNull).forEach(gltf::addBuffers);

                // Create BufferViews
                BufferView posBufferView = createBufferView(gltf, posBuffer, 34962, "posBufferView");
                BufferView normalBufferView = createBufferView(gltf, normalBuffer, 34962, "normalBufferView");
                BufferView tex0BufferView = createBufferView(gltf, tex0Buffer, 34962, "tex0BufferView");
                BufferView tex1BufferView = createBufferView(gltf, tex1Buffer, 34962, "tex1BufferView");
                BufferView colorBufferView = createBufferView(gltf, colorsBuffer, 34962, "colorBufferView");
                BufferView indexBufferView = createBufferView(gltf, facesBuffer, 34963, "facesBufferView");
                BufferView jointsBufferView = createBufferView(gltf, jointsBuffer, 34962, "jointsBufferView");
                BufferView weightsBufferView = createBufferView(gltf, weightsBuffer, 34962, "weightsBufferView");
                BufferView bindPoseBufferView = createBufferView(gltf, bindPoseBuffer, 34962, "bindPoseBufferView"); // Technically
                                                                                                                     // IBM
                                                                                                                     // has
                                                                                                                     // no
                                                                                                                     // target,
                                                                                                                     // this
                                                                                                                     // is
                                                                                                                     // a
                                                                                                                     // bug
                                                                                                                     // in
                                                                                                                     // jgltf

                Stream.of(posBufferView, normalBufferView, tex0BufferView, tex1BufferView, colorBufferView,
                          indexBufferView, jointsBufferView, weightsBufferView, bindPoseBufferView)
                      .filter(Objects::nonNull).forEach(gltf::addBufferViews);

                // Create Accessors

                Accessor posAccessor = createPosAccessor(gltf, posBuffer, posBufferView, 5126,
                                                         posBuffer.getByteLength() / 12, "VEC3", "POS");
                Accessor normalAccessor = createAccessor(gltf, normalBufferView, GLTFComponent.FLOAT.get(),
                                                         normalBuffer != null ? normalBuffer.getByteLength() / 12 : 0,
                                                         "VEC3", "NORMALS");
                Accessor tex0Accessor = createAccessor(gltf, tex0BufferView, GLTFComponent.FLOAT.get(),
                                                       tex0Buffer != null ? tex0Buffer.getByteLength() / 8 : 0, "VEC2",
                                                       "TEXTURES");
                Accessor tex1Accessor = createAccessor(gltf, tex1BufferView, GLTFComponent.FLOAT.get(),
                                                       tex1Buffer != null ? tex1Buffer.getByteLength() / 8 : 0, "VEC2",
                                                       "TEXTURES");
                Accessor colorAccessor = createAccessor(gltf, colorBufferView, GLTFComponent.FLOAT.get(),
                                                        colorsBuffer != null ? colorsBuffer.getByteLength() / 16 : 0,
                                                        "VEC4", "COLOR");
                Accessor indexAccessor = createAccessor(gltf, indexBufferView, GLTFComponent.UNSIGNED_INT.get(),
                                                        indices.size(), "SCALAR", "INDICES");

                Accessor jointsAccessor = createAccessor(gltf, jointsBufferView, GLTFComponent.UNSIGNED_BYTE.get(),
                                                         jointsBuffer != null ? jointsBuffer.getByteLength() / 4 : 0,
                                                         "VEC4", "JOINTS");

                Accessor weightsAccessor = createAccessor(gltf, weightsBufferView, GLTFComponent.FLOAT.get(),
                                                          weightsBuffer != null ? weightsBuffer.getByteLength() / 16
                                                                                : 0,
                                                          "VEC4", "WEIGHTS");

                Accessor bindAccessor = createAccessor(gltf, bindPoseBufferView, GLTFComponent.FLOAT.get(),
                                                       bindPoseBuffer != null ? bindPoseBuffer.getByteLength() / 64 : 0,
                                                       "MAT4", "BINDS");
                // Add Accessors
                Stream.of(posAccessor, normalAccessor, tex0Accessor, tex1Accessor, colorAccessor, indexAccessor,
                          jointsAccessor, weightsAccessor, bindAccessor)
                      .filter(Objects::nonNull).forEach(gltf::addAccessors);

                meshName = "geom-" + geomId;
                Node meshNode = new Node();
                meshNode.setMesh(geomId);
                meshNode.setName(meshName);
                if (jointNodes.size() > 1) { // Maps have a Joint but Skinning destroys the hierarchy
                    meshNode.setSkin(0);
                }
                gltf.addNodes(meshNode); // add the nodes to the glTF model

                Mesh mesh = new Mesh();
                mesh.setName(meshName);
                gltf.addMeshes(mesh);
                containerNode.addChildren(gltf.getNodes().indexOf(meshNode));

                MeshPrimitive primitive = new MeshPrimitive();
                primitive.addAttributes("POSITION", gltf.getAccessors().indexOf(posAccessor));

                if (normalBuffer != null) {
                    primitive.addAttributes("NORMAL", gltf.getAccessors().indexOf(normalAccessor));
                }
                if (tex0Buffer != null) {
                    primitive.addAttributes("TEXCOORD_0", gltf.getAccessors().indexOf(tex0Accessor));
                }
                if (tex1Buffer != null) {
                    primitive.addAttributes("TEXCOORD_0", gltf.getAccessors().indexOf(tex1Accessor));
                }
                if (colorsBuffer != null) {
                    primitive.addAttributes("COLOR_0", gltf.getAccessors().indexOf(colorAccessor));
                }

                if (jointsBuffer != null) {
                    primitive.addAttributes("JOINTS_0", gltf.getAccessors().indexOf(jointsAccessor));
                }

                if (weightsBuffer != null) {
                    primitive.addAttributes("WEIGHTS_0", gltf.getAccessors().indexOf(weightsAccessor));
                }

                primitive.setIndices(gltf.getAccessors().indexOf(indexAccessor));
                if (currentTexture != -1) {
                    primitive.setMaterial((int) currentTexture);
                }
                mesh.addPrimitives(primitive);

                geomId++;
            }
            meshId++;
        }

        Scene scene = new Scene();
        gltf.setScene(0);

        // Set up root node
        Node root = new Node();
        gltf.addNodes(root);
        root.setName(hsmp.getName());

        for (Node node : containerNodesList) {
            root.addChildren(gltf.getNodes().indexOf(node));
        }
        scene.addNodes(gltf.getNodes().indexOf(root)); // Add each node to the scene

        gltf.addScenes(scene);

        File outputFile = new File(output, hsmp.getName() + ".gltf");
        try (OutputStream os = new FileOutputStream(outputFile)) {
            GltfWriter gltfWriter = new GltfWriter();
            gltfWriter.write(gltf, os);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Buffer textureCoordToBuffer(XTVOPayload xtvo, XTVORegisterType type) {
        if (type != XTVORegisterType.TEXTURE0 && type != XTVORegisterType.TEXTURE1) {
            throw new IllegalArgumentException("Can't create texture coord list for non-texture register!");
        }

        List<Float> list = new ArrayList<>(xtvo.getVertices().size() * 2);

        if (xtvo.getAttribute(type).isPresent()) {
            float[] mTex = type == XTVORegisterType.TEXTURE0 ? xtvo.getMTex0() : xtvo.getMTex1();
            Vector4 mTex00 = new Vector4(mTex[2], 0f, 0f, mTex[0]);
            Vector4 mTex01 = new Vector4(0f, mTex[3], 0f, mTex[1]);

            for (XTVOVertex vertex : xtvo.getVertices()) {
                Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(XTVORegisterType.TEXTURE0);
                if (entry == null)
                    continue;

                Vector4 uvs = new Vector4(entry.getKey().getValue(entry.getValue().get(0)),
                                          entry.getKey().getValue(entry.getValue().get(1)), 0f, 1f);

                // Flip the V coordinate
                float u = uvs.dot(mTex00);
                float v = 1.0f - uvs.dot(mTex01);
                list.add(u);
                list.add(v);
            }

            // Convert List<Float> to ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocate(list.size() * Float.BYTES);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            for (Float uv : list) {
                buffer.putFloat(uv);
            }

            buffer.flip(); // Prepare the buffer for reading

            Buffer gltfBuffer = new Buffer();
            gltfBuffer.setByteLength(buffer.remaining());
            gltfBuffer.setUri("data:application/octet-stream;base64,"
                              + Base64.getEncoder().encodeToString(buffer.array()));
            return gltfBuffer;
        }

        return null; // Return null if the attribute is not present
    }

    private Accessor createPosAccessor(GlTF gltf, Buffer posBuffer, BufferView bufferView, int componentType, int count,
                                       String type, String name) {
        Accessor accessor = null;
        Number[] minValues = new Number[3];
        Number[] maxValues = new Number[3];

        if (bufferView != null) {

            String uri = posBuffer.getUri();
            String base64Data = uri.substring("data:application/octet-stream;base64,".length());
            ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(base64Data));
            if (buffer == null || buffer.remaining() % 12 != 0) {
                throw new IllegalArgumentException("Input data is invalid. It should be a non-null buffer with a length that is a multiple of 12.");
            }

            float minX = Float.POSITIVE_INFINITY;
            float minY = Float.POSITIVE_INFINITY;
            float minZ = Float.POSITIVE_INFINITY;

            float maxX = Float.NEGATIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            float maxZ = Float.NEGATIVE_INFINITY;

            while (buffer.hasRemaining()) {
                float x = buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
                float y = buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();
                float z = buffer.order(ByteOrder.LITTLE_ENDIAN).getFloat();

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            }

            minValues[0] = (minX == Float.POSITIVE_INFINITY) ? 0 : minX;
            minValues[1] = (minY == Float.POSITIVE_INFINITY) ? 0 : minY;
            minValues[2] = (minZ == Float.POSITIVE_INFINITY) ? 0 : minZ;

            maxValues[0] = (maxX == Float.NEGATIVE_INFINITY) ? 0 : maxX;
            maxValues[1] = (maxY == Float.NEGATIVE_INFINITY) ? 0 : maxY;
            maxValues[2] = (maxZ == Float.NEGATIVE_INFINITY) ? 0 : maxZ;

            accessor = new Accessor();
            accessor.setBufferView(gltf.getBufferViews().indexOf(bufferView));
            accessor.setComponentType(componentType);
            accessor.setCount(count);
            accessor.setType(type);
            accessor.setMin(minValues);
            accessor.setMax(maxValues);
            accessor.setName(name);
        }
        return accessor;
    }

    private Accessor createAccessor(GlTF gltf, BufferView bufferView, int componentType, int count, String type,
                                    String name) {
        Accessor accessor = null;
        if (bufferView != null) {
            accessor = new Accessor();
            accessor.setBufferView(gltf.getBufferViews().indexOf(bufferView));
            accessor.setComponentType(componentType);
            accessor.setCount(count);
            accessor.setType(type);
            accessor.setName(name);
        }
        return accessor;
    }

    private BufferView createBufferView(GlTF gltf, Buffer buffer, int target, String name) {
        BufferView bufferView = null;
        if (buffer != null && buffer.getByteLength() > 0) {
            bufferView = new BufferView();
            bufferView.setBuffer(gltf.getBuffers().indexOf(buffer));
            bufferView.setByteOffset(0);
            bufferView.setByteLength(buffer.getByteLength());
            bufferView.setTarget(target);
            bufferView.setName(name);
        }
        return bufferView;
    }

    private static Buffer intListToBuffer(List<Integer> data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.size() * Integer.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i : data) {
            buffer.putInt(i);
        }
        buffer.flip(); // Prepare the buffer for reading

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setByteLength(buffer.remaining());
        gltfBuffer.setUri("data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(buffer.array()));
        return gltfBuffer;
    }

    private static Buffer vertexAttribToBuffer(XTVOPayload xtvo, List<XTVOVertex> vertices, XTVORegisterType type) {
        Buffer gltfBuffer = null; // Initialize gltfBuffer to null

        // Get the values for the given register type
        if (xtvo.getAttribute(type).isPresent()) {
            List<Float> floatList = vertices.stream().map(a -> a.getParameter(type))
                                            .flatMap(a -> a.getValue().stream().map(b -> a.getKey().getValue(b)))
                                            .collect(Collectors.toList());

            // Convert List<Float> to ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.allocate(floatList.size() * Float.BYTES);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

            for (float f : floatList) {
                byteBuffer.putFloat(f);
            }

            byteBuffer.flip(); // Prepare the buffer for reading

            gltfBuffer = new Buffer();
            gltfBuffer.setByteLength(byteBuffer.remaining());
            gltfBuffer.setUri("data:application/octet-stream;base64,"
                              + Base64.getEncoder().encodeToString(byteBuffer.array()));
        }

        return gltfBuffer; // Return gltfBuffer (might be null if the condition is not met)
    }

    private static Buffer jointDataToBuffer(XTVOPayload xtvo, List<XTVOVertex> vertices,
                                            Map<Short, Short> currentAssignments) {
        Buffer gltfBuffer = null;

        if (xtvo.getAttribute(XTVORegisterType.IDX).isPresent()) {
            ByteBuffer jointsBuffer = ByteBuffer.allocate(vertices.size() * 4);
            jointsBuffer.order(ByteOrder.LITTLE_ENDIAN);
            
            for (int i = 0; i < xtvo.getVertices().size(); i++) {

                XTVOVertex vertex = xtvo.getVertices().get(i);
                Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(XTVORegisterType.IDX);

                for (int j = 0; j < 4; j++) {
                    int joint = currentAssignments.get((short) (entry.getValue().get(j).intValue() / 3));
                    if (joint != 0) {
                        jointsBuffer.put((byte) joint);
                    }
                }
            }
            jointsBuffer.flip();

            if (jointsBuffer.remaining()!=0)
            {
            gltfBuffer = new Buffer();
            gltfBuffer.setUri("data:application/octet-stream;base64,"
                              + Base64.getEncoder().encodeToString(jointsBuffer.array()));     
            gltfBuffer.setByteLength(jointsBuffer.remaining());
            }
           
        }

        return gltfBuffer;
    }

    private static Buffer matrixListToBuffer(List<float[]> matrixList, List<Node> jointNodes) {
        if (jointNodes.size() <= 1) {
            return null; // Return null if there are not enough joint nodes
        }

        ByteBuffer mBuffer = ByteBuffer.allocate(matrixList.size() * 16 * 4);
        mBuffer.order(ByteOrder.LITTLE_ENDIAN);

        List<float[]> rotatedMatrixList = rotateMatricesCounterclockwise(matrixList);

        for (float[] matrix : rotatedMatrixList) {
            for (float value : matrix) {
                mBuffer.putFloat(value);
            }
        }
        mBuffer.flip();

        Buffer gltfBuffer = new Buffer();
        gltfBuffer.setUri("data:application/octet-stream;base64,"
                + Base64.getEncoder().encodeToString(mBuffer.array()));
        gltfBuffer.setByteLength(mBuffer.remaining());

        return gltfBuffer;
    }


    private void getTextures(HSMPKCAP hsmp, GlTF gltf) throws IOException {
        int imageId = 0;
        for (GMIOPayload gmio : hsmp.getGMIP().getGMIOEntries()) {
            String imageName = gmio.hasName() ? escapeName(gmio.getName()) : "image-" + imageId++;

            // Convert Buffered Images to Byte Array
            ByteArrayOutputStream imagebuffer = new ByteArrayOutputStream();
            ImageIO.write(gmio.getImage(), "PNG", imagebuffer);
            byte[] imageData = imagebuffer.toByteArray();

            // Embed Textures into GLTF
            Image image = new Image();
            image.setName(imageName);
            image.setUri("data:image/png;base64," + Base64.getEncoder().encodeToString(imageData));
            gltf.addImages(image);

            // Create Texture and link it to the Image
            Texture texture = new Texture();
            texture.setName(imageName + "_texture");
            texture.setSource(gltf.getImages().indexOf(image)); // Set the image index
            gltf.addTextures(texture);

            // Create Material and link it to the Texture
            Material material = new Material();
            material.setDoubleSided(true);
            material.setName(imageName + "_material");

            MaterialPbrMetallicRoughness pbrMetallicRoughness = new MaterialPbrMetallicRoughness();
            TextureInfo baseColorTextureInfo = new TextureInfo();
            baseColorTextureInfo.setIndex(gltf.getTextures().indexOf(texture)); // Set the texture index
            pbrMetallicRoughness.setBaseColorTexture(baseColorTextureInfo);
            material.setPbrMetallicRoughness(pbrMetallicRoughness);
            gltf.addMaterials(material);
        }
    }

    public static List<float[]> rotateMatricesCounterclockwise(List<float[]> matrices) {
        List<float[]> rotatedMatrices = new ArrayList<>();

        for (float[] matrix : matrices) {
            float[] rotatedMatrix = new float[16];

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    rotatedMatrix[i * 4 + j] = matrix[j * 4 + i];
                }
            }
            rotatedMatrices.add(rotatedMatrix);
        }
        return rotatedMatrices;
    }

    private static String escapeName(String string) {
        return string.replace("$", "_");
    }
}
