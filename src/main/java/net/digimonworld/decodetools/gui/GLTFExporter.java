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
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.io.*;
import de.javagl.jgltf.model.v2.GltfCreatorV2;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.*;

import java.awt.image.*;

public class GLTFExporter {
	private final HSMPKCAP hsmp;

	public GLTFExporter(HSMPKCAP hsmp) {
		this.hsmp = hsmp;
	}

	public void export(File output) throws IOException {

		DefaultGltfModel gltfModel = new DefaultGltfModel();
		GlTF gltf = GltfCreatorV2.create(gltfModel);

		Asset inputAsset = new Asset();
		inputAsset.setVersion("2.0");
		inputAsset.setGenerator("jgltf-parent-2.0.3");
		gltf.setAsset(inputAsset);

		int meshId = 0;
		for (HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries()) {
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

				final String meshName = "geom-" + meshId++;

				// Create positions, normals,faces
				List<Float> positions = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.POSITION);
				List<Float> normals = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.NORMAL);
				List<Integer> indices = new ArrayList<>();
				xdio.getFaces().forEach(face -> {
					indices.add(face.getVert1());
					indices.add(face.getVert2());
					indices.add(face.getVert3());
				});

				byte[] posBytes = floatListToByteArray(positions);
				byte[] normalBytes = floatListToByteArray(normals);
				byte[] faceBytes = intListToByteBuffer(indices);

				byte[] combinedData = new byte[posBytes.length + normalBytes.length + faceBytes.length];
				System.arraycopy(posBytes, 0, combinedData, 0, posBytes.length);
				System.arraycopy(normalBytes, 0, combinedData, posBytes.length, normalBytes.length);
				System.arraycopy(faceBytes, 0, combinedData, posBytes.length + normalBytes.length, faceBytes.length);

				Buffer buffer = new Buffer();
				buffer.setByteLength(combinedData.length);
				buffer.setUri(
						"data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(combinedData));

				gltf.addBuffers(buffer);

				// Create buffer views
				BufferView posBufferView = new BufferView();
				posBufferView.setBuffer(0); // use the first buffer
				posBufferView.setByteOffset(0); // start at the beginning
				posBufferView.setByteLength(posBytes.length);
				posBufferView.setTarget(34962);

				BufferView normalBufferView = new BufferView();
				normalBufferView.setBuffer(0); // use the first buffer
				normalBufferView.setByteOffset(posBufferView.getByteOffset() + posBufferView.getByteLength());
				normalBufferView.setByteLength(normalBytes.length);
				normalBufferView.setTarget(34962);

				BufferView indexBufferView = new BufferView();
				indexBufferView.setBuffer(0); // use the first buffer
				indexBufferView.setByteOffset(normalBufferView.getByteOffset() + normalBufferView.getByteLength());
				indexBufferView.setByteLength(faceBytes.length);
				indexBufferView.setTarget(34963);

				// Add buffer views to GLTF
				gltf.addBufferViews(posBufferView);
				gltf.addBufferViews(normalBufferView);
				gltf.addBufferViews(indexBufferView);

				// Create accessors
				Accessor posAccessor = new Accessor();
				posAccessor.setBufferView(0); // use the first buffer view
				posAccessor.setComponentType(5126);// FLOAT
				posAccessor.setCount(positions.size() / 3); // each position has 3 components (x, y, z)
				posAccessor.setType("VEC3");

				Accessor normalAccessor = new Accessor();
				normalAccessor.setBufferView(1); // use the second buffer view
				normalAccessor.setComponentType(5126);
				normalAccessor.setCount(normals.size() / 3); // each normal has 3 components (x, y, z)
				normalAccessor.setType("VEC3");

				Accessor indexAccessor = new Accessor();
				indexAccessor.setBufferView(2); // use the third buffer view
				indexAccessor.setComponentType(5123);
				indexAccessor.setCount(indices.size());
				indexAccessor.setType("SCALAR");

				// Add Accessors
				gltf.addAccessors(posAccessor);
				gltf.addAccessors(normalAccessor);
				gltf.addAccessors(indexAccessor);

				Node node = new Node();
				node.setMesh(meshId);
				node.setName(meshName);
				gltf.addNodes(node); // add the nodes to the glTF model

				// Create a scene with the node
				Scene scene = new Scene();
				scene.addNodes(hsmp.getEntryCount());
				gltf.addScenes(scene);
				gltf.setScene(0);

				Mesh mesh = new Mesh();
				mesh.setName(meshName);

				MeshPrimitive primitive = new MeshPrimitive();
				primitive.addAttributes("POSITION", 0);
				primitive.addAttributes("NORMAL", 1);

				mesh.addPrimitives(primitive);

				gltf.addMeshes(mesh);

			}
		}

		File outputFile = new File(output, hsmp.getName() + ".gltf");
		try (OutputStream os = new FileOutputStream(outputFile)) {
			GltfWriter gltfWriter = new GltfWriter();
			gltfWriter.write(gltf, os);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static byte[] floatListToByteArray(List<Float> floatList) {
		int floatCount = floatList.size();
		ByteBuffer byteBuffer = ByteBuffer.allocate(floatCount * Float.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		for (float f : floatList) {
			byteBuffer.putFloat(f);
		}
		return byteBuffer.array();
	}

	public static byte[] intListToByteBuffer(List<Integer> data) {
		ByteBuffer buffer = ByteBuffer.allocate(data.size() * Integer.BYTES);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		for (int i : data) {
			buffer.putInt(i);
		}
		return buffer.array();
	}

	private static List<Float> vertexAttribToList(List<XTVOVertex> vertices, XTVORegisterType type) {
		return vertices.stream().map(a -> a.getParameter(type))
				.flatMap(a -> a.getValue().stream().map(b -> a.getKey().getValue(b))).collect(Collectors.toList());
	}
}