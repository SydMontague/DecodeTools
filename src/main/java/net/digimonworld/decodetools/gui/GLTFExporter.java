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

		GlTF gltf = new GlTF();

		Asset inputAsset = new Asset();
		inputAsset.setVersion("2.0");
		inputAsset.setGenerator("jgltf-parent-2.0.3");
		gltf.setAsset(inputAsset);

		int meshId = 0;
		for (HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries()) {					
			for (HSEMEntry entry : hsem.getEntries()) {			
				
				if (entry.getHSEMType() != HSEMEntryType.DRAW)
					continue;

				HSEMDrawEntry draw = (HSEMDrawEntry) entry;
				XTVOPayload xtvo = hsmp.getXTVP().get(draw.getVertexId());
				XDIOPayload xdio = hsmp.getXDIP().get(draw.getIndexId());

				final String meshName = "geom-" + meshId;

				// Create positions, normals,faces
				List<Float> positions = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.POSITION);
				List<Float> normals = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.NORMAL);	
			//	List<Float> uvcord= textureCoordToList(xtvo, XTVORegisterType.TEXTURE0);				 
					    
				List<Integer> indices = new ArrayList<>();
				xdio.getFaces().forEach(face -> {
					indices.add(face.getVert1());
					indices.add(face.getVert2());
					indices.add(face.getVert3());
				});
			
				byte[] posBytes = floatListToByteArray(positions);
				byte[] normalBytes = floatListToByteArray(normals);
			//	byte[] uvBytes = floatListToByteArray(uvcord);
			
				byte[] faceBytes = intListToByteBuffer(indices);
				int totalSize = posBytes.length + normalBytes.length + faceBytes.length ;
				ByteBuffer byteBuffer = ByteBuffer.allocate(totalSize);

				byteBuffer.put(posBytes);
				byteBuffer.put(normalBytes);
			//	byteBuffer.put(uvBytes);	
				byteBuffer.put(faceBytes);
			
				byte[] combinedData = byteBuffer.array();
				Buffer buffer = new Buffer();
				buffer.setByteLength(combinedData.length);
			
				buffer.setUri(
						"data:application/octet-stream;base64," + Base64.getEncoder().encodeToString(combinedData));

				gltf.addBuffers(buffer);
				int bufferIndex = gltf.getBuffers().size(); // get the current number of buffers
				// Create buffer views
				BufferView posBufferView = new BufferView();
				posBufferView.setBuffer(bufferIndex - 1); // use the first buffer
				posBufferView.setByteOffset(0); // start at the beginning
				posBufferView.setByteLength(posBytes.length);
			//	posBufferView.setTarget(34962);

				BufferView normalBufferView = new BufferView();
				normalBufferView.setBuffer(bufferIndex - 1); // use the first buffer
				normalBufferView.setByteOffset(posBufferView.getByteOffset() + posBufferView.getByteLength());
				normalBufferView.setByteLength(normalBytes.length);
			//	normalBufferView.setTarget(34962);
			
			//	BufferView texBufferView = new BufferView();
			//	texBufferView.setBuffer(0); // use the first buffer
			//	texBufferView.setByteOffset(normalBufferView.getByteOffset() + normalBufferView.getByteLength());
			//	texBufferView.setByteLength(uvBytes.length);
			//	texBufferView.setTarget(34962);

				BufferView indexBufferView = new BufferView();
				indexBufferView.setBuffer(bufferIndex - 1); // use the first buffer
				indexBufferView.setByteOffset(normalBufferView.getByteOffset() + normalBufferView.getByteLength());
				indexBufferView.setByteLength(faceBytes.length);
			//	indexBufferView.setTarget(34963);
			

				// Add buffer views to GLTF
				gltf.addBufferViews(posBufferView);
				gltf.addBufferViews(normalBufferView);
				gltf.addBufferViews(indexBufferView);
			//	gltf.addBufferViews(texBufferView);

				// Create accessors
				Accessor posAccessor = new Accessor();
				posAccessor.setBufferView(gltf.getBufferViews().indexOf(posBufferView)); // use the first buffer view
				posAccessor.setComponentType(5126);// FLOAT
				posAccessor.setCount(positions.size() / 3); // each position has 3 components (x, y, z)
				posAccessor.setType("VEC3");

				Accessor normalAccessor = new Accessor();
				normalAccessor.setBufferView(gltf.getBufferViews().indexOf(normalBufferView));
				normalAccessor.setComponentType(5126);
				normalAccessor.setCount(normals.size() / 3); // each normal has 3 components (x, y, z)
				normalAccessor.setType("VEC3");

		//		Accessor texAccessor = new Accessor();
		//		texAccessor.setBufferView(2);
		//		texAccessor.setComponentType(5126); // UINT
		//		texAccessor.setCount(uvcord.size());
		//		texAccessor.setType("VEC2");

				Accessor indexAccessor = new Accessor();
				indexAccessor.setBufferView(gltf.getBufferViews().indexOf(indexBufferView));
				indexAccessor.setComponentType(5125); // UINT
				indexAccessor.setCount(indices.size());
				indexAccessor.setType("SCALAR");

				// Add Accessors
				gltf.addAccessors(posAccessor);
				gltf.addAccessors(normalAccessor);
			//	gltf.addAccessors(texAccessor);
				gltf.addAccessors(indexAccessor);

				Node node = new Node();
				node.setMesh(meshId);
				node.setName(meshName);
				gltf.addNodes(node); // add the nodes to the glTF model

				// Create a scene with the node
				Scene scene = new Scene();
				gltf.addScenes(scene);
				gltf.setScene(0);
				scene.addNodes(gltf.getNodes().indexOf(node));

				Mesh mesh = new Mesh();
				mesh.setName(meshName);

				MeshPrimitive primitive = new MeshPrimitive();
				primitive.addAttributes("POSITION", gltf.getAccessors().indexOf(posAccessor));
				primitive.addAttributes("NORMAL", gltf.getAccessors().indexOf(normalAccessor));
			//	primitive.addAttributes("TEXCOORD_0", gltf.getAccessors().indexOf(texAccessor));
				primitive.setIndices(gltf.getAccessors().indexOf(indexAccessor));

				mesh.addPrimitives(primitive);

				gltf.addMeshes(mesh);
				meshId++;
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
		
	public static byte[] uvCoordinatesToByteArray(XTVOPayload xtvo, XTVORegisterType type) {
		int numVertices = xtvo.getVertices().size();
		byte[] byteArray = new byte[numVertices * 8];
		ByteBuffer buffer = ByteBuffer.wrap(byteArray);

		if (type != XTVORegisterType.TEXTURE0 && type != XTVORegisterType.TEXTURE1) {
			throw new IllegalArgumentException("Can't create texture coord byte array for non-texture register!");
		}

		float[] mTex = type == XTVORegisterType.TEXTURE0 ? xtvo.getMTex0() : xtvo.getMTex1();
		Vector4 mTex00 = new Vector4(mTex[2], 0f, 0f, mTex[0]);
		Vector4 mTex01 = new Vector4(0f, mTex[3], 0f, mTex[1]);

		for (int i = 0; i < numVertices; i++) {
			XTVOVertex vertex = xtvo.getVertices().get(i);
			Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(type);

			if (entry == null) {
				continue;
			}

			Vector4 uvs = new Vector4(entry.getKey().getValue(entry.getValue().get(0)),
					entry.getKey().getValue(entry.getValue().get(1)), 0f, 1f);

			float u = uvs.dot(mTex00);
			float v = uvs.dot(mTex01);

			buffer.putFloat(u);
			buffer.putFloat(v);
		}

		return byteArray;
	}

	private static List<Float> textureCoordToList(XTVOPayload xtvo, XTVORegisterType type) {
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

			list.add(uvs.dot(mTex00));
			list.add(uvs.dot(mTex01));
		}

		return list;
	}
	
	public static byte[] floatListToByteArray(List<Float> floatList) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(floatList.size() * Float.BYTES);
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