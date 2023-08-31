package net.digimonworld.decodetools.gui;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Vector4;
import net.digimonworld.decodetools.gui.util.LinAlg.*;
import net.digimonworld.decodetools.res.kcap.HSMPKCAP;
import net.digimonworld.decodetools.res.payload.GMIOPayload;
import net.digimonworld.decodetools.res.payload.HSEMPayload;
import net.digimonworld.decodetools.res.payload.RTCLPayload;
import net.digimonworld.decodetools.res.payload.TNOJPayload;
import net.digimonworld.decodetools.res.payload.XDIOPayload;
import net.digimonworld.decodetools.res.payload.XTVOPayload;
import net.digimonworld.decodetools.res.payload.hsem.HSEMDrawEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntryType;
import net.digimonworld.decodetools.res.payload.hsem.HSEMJointEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMTextureEntry;
import net.digimonworld.decodetools.res.payload.xdio.XDIOFace;
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
import de.javagl.jgltf.impl.v2.MaterialNormalTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.io.*;
import de.javagl.jgltf.model.v2.GltfCreatorV2;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.*;
import java.time.format.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.awt.image.*;

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
		List<Node> meshList = new ArrayList<Node>();

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

		int numHSEMPayloads = hsmp.getHSEM().getHSEMEntries().size();
		System.out.println("Number of HSEM Payloads : " + numHSEMPayloads);

		int meshId = 0;
		int geomId = 0;

		// Process joints and create joint nodes
		List<Node> jointNodes = new ArrayList<>();

		Map<Integer, Node> jointMap = new HashMap<>();

		if (hsmp.getTNOJ() != null) {
			for (int i = 0; i < hsmp.getTNOJ().getEntryCount(); i++) {

				TNOJPayload j = hsmp.getTNOJ().get(i);
				Node joints = new Node();
				joints.setName(j.getName());
				joints.setTranslation(new float[] { j.getXOffset(), j.getYOffset(), j.getZOffset() });
				joints.setRotation(
						new float[] { j.getRotationX(), j.getRotationY(), j.getRotationZ(), j.getRotationW() });
				joints.setScale(new float[] { j.getLocalScaleX(), j.getLocalScaleY(), j.getLocalScaleZ() });
				jointNodes.add(joints);
				jointMap.put(i, joints);

				if (j.getParentId() != -1) {
					Node parent = jointMap.get(j.getParentId());
					parent.addChildren(jointNodes.indexOf(joints));
				}
				gltf.addNodes(joints);

			}

			Skin jointsSkin = new Skin();
			jointsSkin.setName(hsmp.getName() + "-joint");
			List<Integer> joints = new ArrayList<>();
			for (Node node2 : jointNodes) {
				joints.add(jointNodes.indexOf(node2));
			}
			jointsSkin.setJoints(joints);
			gltf.addSkins(jointsSkin);

		}

		for (HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries()) {

			String containerNodeName = hsmp.getName() + "-mesh." + meshId; // Use the HSEM name and meshId for the
																			// container
			Node containerNode = new Node();
			containerNode.setName(containerNodeName);
			gltf.addNodes(containerNode);
			List<Node> childNodes = new ArrayList<>(); // Create a list to hold child nodes for this container
			Map<Short, Short> currentAssignments = new HashMap<>();
			short currentTexture = 0;

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

				meshName = "geom-" + geomId;

				List<Integer> indices = new ArrayList<>();
				xdio.getFaces().forEach(face -> {
					indices.add(face.getVert1());
					indices.add(face.getVert2());
					indices.add(face.getVert3());
				});

				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				byte[] posBytes = vertexAttribToByteArray(xtvo.getVertices(), XTVORegisterType.POSITION);
				if (posBytes != null) {
					baos.write(posBytes, 0, posBytes.length);
				}

				byte[] normalBytes = null;
				if (xtvo.getAttribute(XTVORegisterType.NORMAL).isPresent()) {
					normalBytes = vertexAttribToByteArray(xtvo.getVertices(), XTVORegisterType.NORMAL);
					baos.write(normalBytes, 0, normalBytes.length);
				}

				byte[] uvBytes = null;
				if (xtvo.getAttribute(XTVORegisterType.TEXTURE0).isPresent()) {
					uvBytes = textureCoordToList(xtvo, XTVORegisterType.TEXTURE0);
					baos.write(uvBytes, 0, uvBytes.length);
				}

				if (xtvo.getAttribute(XTVORegisterType.TEXTURE1).isPresent()) {
					uvBytes = textureCoordToList(xtvo, XTVORegisterType.TEXTURE1);
					baos.write(uvBytes, 0, uvBytes.length);
				}

				byte[] colorBytes = null;
				if (xtvo.getAttribute(XTVORegisterType.COLOR).isPresent()) {
					colorBytes = vertexAttribToByteArray(xtvo.getVertices(), XTVORegisterType.COLOR);
					baos.write(colorBytes, 0, colorBytes.length);
				}

				byte[] faceBytes = intListToByteBuffer(indices);
				if (faceBytes != null) {
					baos.write(faceBytes, 0, faceBytes.length);
				}

				byte[] weightBytes = null;
				if (xtvo.getAttribute(XTVORegisterType.WEIGHT).isPresent()) {
					weightBytes = vertexAttribToByteArray(xtvo.getVertices(), XTVORegisterType.WEIGHT);
					baos.write(weightBytes, 0, weightBytes.length);
				}

				byte[] jointBytes = null;
				if (xtvo.getAttribute(XTVORegisterType.IDX).isPresent()) {
					ByteBuffer buffer = ByteBuffer.allocate(xtvo.getVertices().size() * 4);
					buffer.order(ByteOrder.LITTLE_ENDIAN);

					for (XTVOVertex vertex : xtvo.getVertices()) {
						Entry<XTVOAttribute, List<Number>> entry2 = vertex.getParameter(XTVORegisterType.IDX);

						for (int j = 0; j < 4; j++) {
							int joint = entry2.getValue().get(j).intValue() / 3;
							buffer.put((byte) joint);
						}
					}
					jointBytes = buffer.array();
					baos.write(jointBytes, 0, jointBytes.length);
				}

				byte[] combinedData = baos.toByteArray();

				Buffer buffer = new Buffer();
				buffer.setByteLength(combinedData.length);
				buffer.setUri(
						"data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(combinedData));

				gltf.addBuffers(buffer);

				// Create buffer views
				BufferView posBufferView = createBufferView(gltf, buffer, posBytes, 34962);
				BufferView normalBufferView = createBufferView(gltf, buffer, normalBytes, 34962);
				BufferView texBufferView = createBufferView(gltf, buffer, uvBytes, 34962);
				BufferView colorBufferView = createBufferView(gltf, buffer, colorBytes, 34962);
				BufferView indexBufferView = createBufferView(gltf, buffer, faceBytes, 34963);
				BufferView jointsBufferView = createBufferView(gltf, buffer, jointBytes, 34963);
				BufferView weightBufferView = createBufferView(gltf, buffer, weightBytes, 34962);

				// Add buffer views to GLTF
				Stream.of(posBufferView, normalBufferView, texBufferView, colorBufferView, indexBufferView,
						jointsBufferView, weightBufferView).filter(Objects::nonNull).forEach(gltf::addBufferViews);

				// Create Accessors

				Accessor posAccessor = createAccessor(gltf, posBufferView, 5126, posBytes.length / 12, "VEC3", "POS");
				Accessor normalAccessor = createAccessor(gltf, normalBufferView, GLTFComponent.FLOAT.get(),
						normalBytes != null ? normalBytes.length / 12 : 0, "VEC3", "NORMALS");
				Accessor texAccessor = createAccessor(gltf, texBufferView, GLTFComponent.FLOAT.get(),
						uvBytes.length / 8, "VEC2", "TEXTURES");
				Accessor colorAccessor = createAccessor(gltf, colorBufferView, GLTFComponent.FLOAT.get(),
						colorBytes != null ? colorBytes.length / 16 : 0, "VEC4", "COLOR");
				Accessor indexAccessor = createAccessor(gltf, indexBufferView, GLTFComponent.UNSIGNED_INT.get(),
						indices.size(), "SCALAR", "INDICES");
				Accessor jointsAccessor = createAccessor(gltf, jointsBufferView, GLTFComponent.UNSIGNED_BYTE.get(),
						jointBytes.length / 4, "VEC4", "JOINTS");
				Accessor weightAccessor = createAccessor(gltf, weightBufferView, GLTFComponent.FLOAT.get(),
						weightBytes.length / 16, "VEC4", "WEIGHTS");

				// Add Accessors
				Stream.of(posAccessor, normalAccessor, texAccessor, colorAccessor, indexAccessor, jointsAccessor,
						weightAccessor).filter(Objects::nonNull).forEach(gltf::addAccessors);

				Node node = new Node();
				node.setMesh(geomId);
				node.setName(meshName);
				gltf.addNodes(node); // add the nodes to the glTF model
				meshList.add(node);
				childNodes.add(node); // Add the node to the child nodes list for this container
				containerNode.addChildren(gltf.getNodes().indexOf(node));

				Mesh mesh = new Mesh();
				mesh.setName(meshName);

				gltf.addMeshes(mesh);

				MeshPrimitive primitive = new MeshPrimitive();
				primitive.addAttributes("POSITION", gltf.getAccessors().indexOf(posAccessor));
				if (normalBytes != null) {
					primitive.addAttributes("NORMAL", gltf.getAccessors().indexOf(normalAccessor));
				}
				if (uvBytes != null) {
					primitive.addAttributes("TEXCOORD_0", gltf.getAccessors().indexOf(texAccessor));
				}
				if (colorBytes != null) {
					primitive.addAttributes("COLOR_0", gltf.getAccessors().indexOf(colorAccessor));
				}
				if (jointBytes != null) {
					primitive.addAttributes("JOINTS_0", gltf.getAccessors().indexOf(jointsAccessor));
				}

				if (weightBytes != null) {
					primitive.addAttributes("WEIGHTS_0", gltf.getAccessors().indexOf(weightAccessor));
				}

				primitive.setIndices(gltf.getAccessors().indexOf(indexAccessor));
				primitive.setMaterial((int) currentTexture);
				mesh.addPrimitives(primitive);

				geomId++;
			}
			meshId++; // Increment the meshId after processing each HSEM Payload

		}

		Scene scene = new Scene();
		gltf.setScene(0);

		for (Node node : meshList) {
			scene.addNodes(gltf.getNodes().indexOf(node)); // Add each node to the scene
		}

		gltf.addScenes(scene);

		File outputFile = new File(output, hsmp.getName() + ".gltf");
		try (OutputStream os = new FileOutputStream(outputFile)) {
			GltfWriter gltfWriter = new GltfWriter();
			gltfWriter.write(gltf, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static byte[] textureCoordToList(XTVOPayload xtvo, XTVORegisterType type) {
		List<Float> list = new ArrayList<>(xtvo.getVertices().size() * 2);

		if (type != XTVORegisterType.TEXTURE0 && type != XTVORegisterType.TEXTURE1)
			throw new IllegalArgumentException("Can't create texture coord list for non-texture register!");

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

		// Create byte array from List<Float>
		ByteBuffer buffer = ByteBuffer.allocate(list.size() * 4);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		for (Float uv : list) {
			buffer.putFloat(uv);
		}

		return buffer.array();
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

	private BufferView createBufferView(GlTF gltf, Buffer buffer, byte[] data, int target) {
		if (currentBuffer != buffer) {
			currentBuffer = buffer;
			currentOffset = 0; // Reset currentOffset to 0 for new buffer
		}

		BufferView bufferView = null;
		if (data != null) {
			bufferView = new BufferView();
			bufferView.setBuffer(gltf.getBuffers().indexOf(buffer));
			bufferView.setByteOffset(currentOffset);
			bufferView.setByteLength(data.length);
			bufferView.setTarget(target);
			gltf.addBufferViews(bufferView);

			updateCurrentOffset(bufferView); // Update the currentOffset
		}
		return bufferView;
	}

	private void updateCurrentOffset(BufferView bufferView) {
		if (bufferView == null) {
			currentOffset = 0;
		} else {
			currentOffset = bufferView.getByteOffset() + bufferView.getByteLength();
		}
	}

	public static byte[] intListToByteBuffer(List<Integer> data) {
		ByteBuffer buffer = ByteBuffer.allocate(data.size() * Integer.BYTES);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i : data) {
			buffer.putInt(i);
		}
		return buffer.array();
	}

	private static byte[] vertexAttribToShort(List<XTVOVertex> vertices, XTVORegisterType type) {
		// Get the values for the given register type
		// Convert List<Float> to byte[]
		ByteBuffer byteBuffer = ByteBuffer.allocate(2 * Float.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		return byteBuffer.array();
	}

	private static byte[] vertexAttribToByteArray(List<XTVOVertex> vertices, XTVORegisterType type) {
		// Get the values for the given register type
		List<Float> floatList = vertices.stream().map(a -> a.getParameter(type))
				.flatMap(a -> a.getValue().stream().map(b -> a.getKey().getValue(b))).collect(Collectors.toList());

		// Convert List<Float> to byte[]
		ByteBuffer byteBuffer = ByteBuffer.allocate(floatList.size() * Float.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);

		for (float f : floatList) {
			byteBuffer.putFloat(f);
		}

		return byteBuffer.array();
	}

	private void printWeights(List<XTVOVertex> vertices, byte[] weightBytes) {
		ByteBuffer buffer = ByteBuffer.wrap(weightBytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		int numVertices = vertices.size();
		int numWeightsPerVertex = 4; // Assuming each vertex has 4 weights

		for (int i = 0; i < numVertices; i++) {
			System.out.print("Vertex " + i + " weights: ");
			for (int j = 0; j < numWeightsPerVertex; j++) {
				float weight = buffer.getFloat();
				System.out.print(weight + " ");
			}
			System.out.println();
		}
	}

	private static String escapeName(String string) {
		return string.replace("$", "_");
	}

}