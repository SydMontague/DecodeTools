package net.digimonworld.decodetools.gui;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntryType;
import net.digimonworld.decodetools.res.payload.hsem.HSEMJointEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMTextureEntry;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOAttribute;
import net.digimonworld.decodetools.res.payload.xtvo.XTVORegisterType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOVertex;

public class ColldadaExporter {
    
    private final Document doc;
    private final HSMPKCAP hsmp;
    
    public ColldadaExporter(HSMPKCAP hsmp) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        docFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        //Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new URL("https://www.khronos.org/files/collada_schema_1_5"));
        //docFactory.setSchema(schema);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        
        this.doc = docBuilder.newDocument();
        this.hsmp = hsmp;
    }

    public void export(File output) throws TransformerException {
        Element root = doc.createElement("COLLADA");
        root.setAttribute("xmlns", "http://www.collada.org/2008/03/COLLADASchema");
        root.setAttribute("version", "1.5.0");
        doc.appendChild(root);
        
        Element asset = doc.createElement("asset");
        asset.appendChild(createTextElement("created", DateTimeFormatter.ISO_INSTANT.format(Instant.now())));
        asset.appendChild(createTextElement("modified", DateTimeFormatter.ISO_INSTANT.format(Instant.now())));
        asset.appendChild(createTextElement("revision", "1.0.0"));
        asset.appendChild(createTextElement("up_axis", "Y_UP"));
        root.appendChild(asset);

        Element libEffects = doc.createElement("library_effects");
        Element libImages = doc.createElement("library_images");
        Element libMaterials = doc.createElement("library_materials");
        
        
        List<String> images = new ArrayList<>();
        int imageId = 0;
        for (GMIOPayload gmio : hsmp.getGMIP().getGMIOEntries()) {
            // image
            Element image = doc.createElement("image");
            String imageName = gmio.hasName() ? escapeName(gmio.getName()) : "image-"+imageId++;
            image.setAttribute("id", imageName);
            Element initFrom = doc.createElement("init_from");
            initFrom.appendChild(createTextElement("ref", "images/" + imageName + ".png"));
            image.appendChild(initFrom);
            libImages.appendChild(image);
            
            images.add(imageName);
            
            try {
                File imageFile = new File(output, "images/" + imageName + ".png");
                imageFile.mkdirs();
                ImageIO.write(gmio.getImage(), "PNG", imageFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            
            // Effect
            Element effect = doc.createElement("effect");
            effect.setAttribute("id", imageName + "-effect");
            Element profile = doc.createElement("profile_COMMON");
            Element technique = doc.createElement("technique");
            technique.setAttribute("sid", "COMMON");
            Element lambert = doc.createElement("lambert");
            
            Element samplerParam = doc.createElement("newparam");
            samplerParam.setAttribute("sid", imageName + "-sampler");
            Element sampler = doc.createElement("sampler2D");
            Element instanceImage = doc.createElement("instance_image");
            instanceImage.setAttribute("url", "#" + imageName);
            
            sampler.appendChild(instanceImage);
            samplerParam.appendChild(sampler);
            profile.appendChild(samplerParam);
            
            Element diffuse = doc.createElement("diffuse");
            Element texture = doc.createElement("texture");
            texture.setAttribute("texture", imageName + "-sampler");
            texture.setAttribute("texcoord", "UVSET0");
            diffuse.appendChild(texture);
            lambert.appendChild(diffuse);
            technique.appendChild(lambert);
            profile.appendChild(technique);
            effect.appendChild(profile);
            libEffects.appendChild(effect);
            
            // Material
            Element material = doc.createElement("material");
            material.setAttribute("id", imageName + "-material");
            Element instanceEffect = doc.createElement("instance_effect");
            instanceEffect.setAttribute("url", "#" + imageName + "-effect");
            material.appendChild(instanceEffect);
            libMaterials.appendChild(material);
        }
        
        root.appendChild(libImages);
        root.appendChild(libMaterials);
        root.appendChild(libEffects);
        
        Element libGeometries = doc.createElement("library_geometries");
        Element libController = doc.createElement("library_controllers");
        Element libVisualScenes = doc.createElement("library_visual_scenes");
        root.appendChild(libGeometries);
        root.appendChild(libController);
        root.appendChild(libVisualScenes);
        
        Element visualScene = doc.createElement("visual_scene");
        visualScene.setAttribute("id", "mainScene");
        Element rootNode = doc.createElement("node");
        rootNode.setAttribute("id", hsmp.getName());
        visualScene.appendChild(rootNode);
        
        Element meshNode = doc.createElement("node");
        meshNode.setAttribute("id", hsmp.getName() + "-mesh");
        
        Element jointNode = doc.createElement("node");
        jointNode.setAttribute("id", hsmp.getName() + "-joint");

        Map<Integer, Element> jointMap = new HashMap<>();
        
        if(hsmp.getTNOJ() != null)
            for(int i = 0; i < hsmp.getTNOJ().getEntryCount(); i++) {
                TNOJPayload j = hsmp.getTNOJ().get(i);
                
                Element elem = doc.createElement("node");
                elem.setAttribute("id", j.getName());
                elem.setAttribute("name", j.getName());
                elem.setAttribute("sid", j.getName());
                elem.setAttribute("type", "JOINT");
                
                Element translate = createTextElement("translate", j.getXOffset() + " " + j.getYOffset() + " " + j.getZOffset());
                
                double[] angles = j.getAngles();
                
                Element rotX = createTextElement("rotate", "1 0 0 " + angles[0]);
                Element rotY = createTextElement("rotate", "0 1 0 " + angles[1]);
                Element rotZ = createTextElement("rotate", "0 0 1 " + angles[2]);
                Element scale = createTextElement("scale", j.getLocalScaleX() + " " + j.getLocalScaleY() + " " + j.getLocalScaleZ());
                translate.setAttribute("sid", "translate");
                rotX.setAttribute("sid", "rotateX");
                rotY.setAttribute("sid", "rotateY");
                rotZ.setAttribute("sid", "rotateZ");
                scale.setAttribute("sid", "scale");
                
                elem.appendChild(translate);
                elem.appendChild(rotX);
                elem.appendChild(rotY);
                elem.appendChild(rotZ);
                elem.appendChild(scale);
                
                jointMap.put(i, elem);
                if(j.getParentId() != -1) 
                    jointMap.get(j.getParentId()).appendChild(elem);
                else
                    jointNode.appendChild(elem);
            }
        

        if(hsmp.getRTCL() != null)
            for(int i = 0; i < hsmp.getRTCL().getEntryCount(); i++) {
                RTCLPayload j = hsmp.getRTCL().get(i);
                
                Element elem = doc.createElement("node");
                elem.setAttribute("id", j.getName());
                elem.setAttribute("sid", j.getName());
                elem.setAttribute("name", j.getName());
                elem.setAttribute("type", "JOINT");
                
                Element matrix = createTextElement("matrix", floatArrayToString(j.getMatrix()));
                elem.appendChild(matrix);
                
                if(jointMap.containsKey(j.getParentBone()))
                    jointMap.get(j.getParentBone()).appendChild(elem);
                else 
                    jointNode.appendChild(elem);
            }
        
        rootNode.appendChild(meshNode);
        rootNode.appendChild(jointNode);
        
        int meshId = 0;
        for(HSEMPayload hsem : hsmp.getHSEM().getHSEMEntries()) {
            Map<Short, Short> currentAssignments = new HashMap<>();
            short currentTexture = -1;
            
            for(HSEMEntry entry : hsem.getEntries()) {
                
                switch(entry.getHSEMType()) {
                    case JOINT:
                        ((HSEMJointEntry) entry).getJointAssignment().forEach(currentAssignments::put);
                        break;
                    case TEXTURE:
                        currentTexture = ((HSEMTextureEntry) entry).getTextureAssignment().getOrDefault((short) 0, currentTexture);
                        break;
                    default: break;
                }
                
                if(entry.getHSEMType() != HSEMEntryType.DRAW)
                    continue;

                HSEMDrawEntry draw = (HSEMDrawEntry) entry;
                XTVOPayload xtvo = hsmp.getXTVP().get(draw.getVertexId());
                XDIOPayload xdio = hsmp.getXDIP().get(draw.getIndexId());
                final String meshName = "geom-" + meshId++;
                
                // create scene node
                Element node = doc.createElement("node");
                node.setAttribute("id", meshName + "-node");
                
                Element instance;
                
                if(xtvo.getAttribute(XTVORegisterType.IDX).isEmpty()) {
                    instance = doc.createElement("instance_geometry");
                    instance.setAttribute("url", "#" + meshName);
                }
                else {
                    instance = doc.createElement("instance_controller");
                    instance.setAttribute("url", "#" + meshName + "-skin");
                    instance.appendChild(createTextElement("skeleton", "#mainScene"));
                }
                
                if(currentTexture != -1) {
                    Element bindMat = doc.createElement("bind_material");
                    Element tech = doc.createElement("technique_common");
                    
                    Element instanceMaterial = doc.createElement("instance_material");
                    instanceMaterial.setAttribute("symbol", meshName + "-material");
                    instanceMaterial.setAttribute("target", "#" + images.get(currentTexture) + "-material");
                    Element vertexInput = doc.createElement("bind_vertex_input");
                    vertexInput.setAttribute("semantic", "UVSET0");
                    vertexInput.setAttribute("input_semantic", "TEXCOORD");
                    vertexInput.setAttribute("input_set", "0");
                    instanceMaterial.appendChild(vertexInput);
                    
                    tech.appendChild(instanceMaterial);
                    bindMat.appendChild(tech);
                    instance.appendChild(bindMat);
                }

                node.appendChild(instance);
                meshNode.appendChild(node);
                
                // create controller
                if(xtvo.getAttribute(XTVORegisterType.IDX).isPresent()) {
                    List<TNOJPayload> joints = currentAssignments.values().stream().map(a -> hsmp.getTNOJ().get(a)).collect(Collectors.toList());
                    
                    Element controller = doc.createElement("controller");
                    controller.setAttribute("id", meshName + "-skin");
                    controller.setAttribute("name", meshName + "-armature");
                    
                    Element skin = doc.createElement("skin");
                    skin.setAttribute("source", "#" + meshName);

                    List<String> names = joints.stream().map(TNOJPayload::getName).collect(Collectors.toList());
                    List<String> bindPose = joints.stream().flatMapToDouble(a -> IntStream.range(0, 16).mapToDouble(b -> a.getOffsetMatrix()[b])).mapToObj(Double::toString).collect(Collectors.toList());
                    List<String> weights = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.WEIGHT);
                    
                    skin.appendChild(createMeshSource(meshName + "-skin-joints", ParamType.NAME, names, Arrays.asList("JOINT")));
                    skin.appendChild(createMeshSource(meshName + "-skin-poses", ParamType.FLOAT_MATRIX, bindPose, Arrays.asList("TRANSFORM")));
                    skin.appendChild(createMeshSource(meshName + "-skin-weights", ParamType.FLOAT, weights, Arrays.asList("WEIGHT")));
                    
                    Element jointsElement = doc.createElement("joints");
                    jointsElement.appendChild(createUnsharedInput("JOINT", "#" + meshName + "-skin-joints"));
                    jointsElement.appendChild(createUnsharedInput("INV_BIND_MATRIX", "#" + meshName + "-skin-poses"));
                    skin.appendChild(jointsElement);
                    
                    Element vertexWeights = doc.createElement("vertex_weights");
                    vertexWeights.setAttribute("count", Integer.toString(xtvo.getVertices().size()));
                    vertexWeights.appendChild(createSharedInput(0, "JOINT", "#" + meshName + "-skin-joints", Optional.empty()));
                    vertexWeights.appendChild(createSharedInput(1, "WEIGHT", "#" + meshName + "-skin-weights", Optional.empty()));
                    
                    List<String> vcountBuilder = new ArrayList<>();
                    List<String> builder = new ArrayList<>();

                    for(int i = 0; i < xtvo.getVertices().size(); i++) {
                        XTVOVertex vertex = xtvo.getVertices().get(i);
                        Map.Entry<XTVOAttribute, List<Number>> bla = vertex.getParameter(XTVORegisterType.IDX);

                        int boneCount = 0;
                        
                        for(int j = 0; j < 4; j++) {
                            int joint = bla.getValue().get(j).intValue() / 3;
                            
                            double weight = Double.parseDouble(weights.get(i * 4 + j));
                            if(joint != 0 || weight > 0) {
                                boneCount++;
                                builder.add(Integer.toString(joint));
                                builder.add(Integer.toString(i * 4 + j));
                            }
                        }
                        
                        vcountBuilder.add(Integer.toString(boneCount));
                    }
                    
                    String vcountList = vcountBuilder.stream().collect(Collectors.joining(" "));
                    String vList = builder.stream().collect(Collectors.joining(" "));

                    vertexWeights.appendChild(createTextElement("vcount", vcountList));
                    vertexWeights.appendChild(createTextElement("v", vList));
                    skin.appendChild(vertexWeights);
                    
                    controller.appendChild(skin);
                    libController.appendChild(controller);
                }
                
                // create geometry
                Element geometry = doc.createElement("geometry");
                geometry.setAttribute("id", meshName);
                
                Element mesh = doc.createElement("mesh");
                Element triangles = doc.createElement("triangles");
                triangles.setAttribute("count", Integer.toString(xdio.getFaces().size()));
                if(currentTexture != -1)
                    triangles.setAttribute("material", meshName + "-material");
                
                List<String> pos = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.POSITION);
                mesh.appendChild(createMeshSource(meshName + "-pos", ParamType.FLOAT, pos, Arrays.asList("X", "Y", "Z")));

                triangles.appendChild(createSharedInput(0, "VERTEX", "#" + meshName + "-vertices", Optional.empty()));
                
                if(xtvo.getAttribute(XTVORegisterType.NORMAL).isPresent()) {
                    List<String> normal = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.NORMAL);
                    mesh.appendChild(createMeshSource(meshName + "-normal", ParamType.FLOAT, normal, Arrays.asList("X", "Y", "Z")));
                    triangles.appendChild(createSharedInput(0, "NORMAL", "#" + meshName + "-normal", Optional.empty()));
                }
                if(xtvo.getAttribute(XTVORegisterType.COLOR).isPresent()) {
                    List<String> normal = vertexAttribToList(xtvo.getVertices(), XTVORegisterType.COLOR);
                    mesh.appendChild(createMeshSource(meshName + "-color", ParamType.FLOAT, normal, Arrays.asList("R", "G", "B")));
                    triangles.appendChild(createSharedInput(0, "COLOR", "#" + meshName + "-color", Optional.empty()));
                }
                if(xtvo.getAttribute(XTVORegisterType.TEXTURE0).isPresent()) {
                    List<String> normal = textureCoordToList(xtvo, XTVORegisterType.TEXTURE0);
                    mesh.appendChild(createMeshSource(meshName + "-texcoord0", ParamType.FLOAT, normal, Arrays.asList("S", "T")));
                    triangles.appendChild(createSharedInput(0, "TEXCOORD", "#" + meshName + "-texcoord0", Optional.of(0)));
                }
                if(xtvo.getAttribute(XTVORegisterType.TEXTURE1).isPresent()) {
                    List<String> normal = textureCoordToList(xtvo, XTVORegisterType.TEXTURE1);
                    mesh.appendChild(createMeshSource(meshName + "-texcoord1", ParamType.FLOAT, normal, Arrays.asList("S", "T")));
                    triangles.appendChild(createSharedInput(0, "TEXCOORD", "#" + meshName + "-texcoord1", Optional.of(1)));
                }
                
                String indexString = xdio.getFaces().stream().flatMap(a -> List.of(a.getVert1(), a.getVert2(), a.getVert3()).stream()).map(Object::toString).collect(Collectors.joining(" "));
                Element indices = createTextElement("p", indexString);
                
                triangles.appendChild(indices);

                Element vertices = doc.createElement("vertices");
                vertices.setAttribute("id", meshName + "-vertices");
                vertices.appendChild(createUnsharedInput("POSITION", "#" + meshName + "-pos"));
                
                mesh.appendChild(vertices);
                mesh.appendChild(triangles);
                
                geometry.appendChild(mesh);

                libGeometries.appendChild(geometry);
            }
        }

        libVisualScenes.appendChild(visualScene);
        
        Element scene = doc.createElement("scene");
        Element sceneInstance = doc.createElement("instance_visual_scene");
        sceneInstance.setAttribute("url", "#mainScene");
        scene.appendChild(sceneInstance);
        root.appendChild(scene);
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(output, hsmp.getName() + ".dae"));
        transformer.transform(source, result);
    }
    
    private static List<String> textureCoordToList(XTVOPayload xtvo, XTVORegisterType type) {
        List<String> list = new ArrayList<>(xtvo.getVertices().size() * 2);
        
        if(type != XTVORegisterType.TEXTURE0 && type != XTVORegisterType.TEXTURE1)
            throw new IllegalArgumentException("Can't create texture coord list for non-texture register!");
        
        float[] mTex = type == XTVORegisterType.TEXTURE0 ? xtvo.getMTex0() : xtvo.getMTex1();
        Vector4 mTex00 = new Vector4(mTex[2], 0f, 0f, mTex[0]);
        Vector4 mTex01 = new Vector4(0f, mTex[3], 0f, mTex[1]);
        
        for(XTVOVertex vertex : xtvo.getVertices()) {
            Entry<XTVOAttribute, List<Number>> entry = vertex.getParameter(XTVORegisterType.TEXTURE0);
            if (entry == null)
                continue;
            
            Vector4 uvs = new Vector4(entry.getKey().getValue(entry.getValue().get(0)), entry.getKey().getValue(entry.getValue().get(1)), 0f, 1f);
            
            list.add(Float.toString(uvs.dot(mTex00)));
            list.add(Float.toString(uvs.dot(mTex01)));
        }
        
        return list;
    }
    
    private static List<String> vertexAttribToList(List<XTVOVertex> vertices, XTVORegisterType type) {
        return vertices.stream().map(a -> a.getParameter(type)).flatMap(a -> a.getValue().stream().map(b -> a.getKey().getValue(b))).map(Object::toString).collect(Collectors.toList());
    }
    
    
    private Element createMeshSource(String name, ParamType type, List<String> pos, List<String> params) {
        Element posSource = doc.createElement("source");
        posSource.setAttribute("id", name);
        
        Element posArray = doc.createElement(type.getElement());
        posArray.setAttribute("id", name + "-array");
        posArray.setAttribute("count", Integer.toString(pos.size()));
        posArray.setTextContent(String.join(" ", pos));
        
        Element posTechniqueCommon = doc.createElement("technique_common");
        
        Element posAccessor = doc.createElement("accessor");
        posAccessor.setAttribute("source", "#"+name+"-array");
        posAccessor.setAttribute("count", Integer.toString(pos.size() / params.size() / type.getElementCount()));
        posAccessor.setAttribute("stride", Integer.toString(params.size() * type.getElementCount()));
        
        for(String param : params) {
            Element paramElem = doc.createElement("param");
            paramElem.setAttribute("name", param);
            paramElem.setAttribute("type", type.getType());
            posAccessor.appendChild(paramElem);
        }
        
        posTechniqueCommon.appendChild(posAccessor);

        posSource.appendChild(posArray);
        posSource.appendChild(posTechniqueCommon);
        
        return posSource;
    }
    
    private Element createTextElement(String name, String text) {
        Element elem = doc.createElement(name);
        elem.setTextContent(text);
        return elem;
    }
    
    private Element createSharedInput(int offset, String semantic, String source, Optional<Integer> set) {
        Element sharedInput = doc.createElement("input");
        sharedInput.setAttribute("offset", Integer.toString(offset));
        sharedInput.setAttribute("semantic", semantic);
        sharedInput.setAttribute("source", source);
        set.ifPresent(a -> sharedInput.setAttribute("set", Integer.toString(a)));
        
        return sharedInput;
    }
    
    private Element createUnsharedInput(String semantic, String source) {
        Element unsharedInput = doc.createElement("input");
        unsharedInput.setAttribute("semantic", semantic);
        unsharedInput.setAttribute("source", source);
        
        return unsharedInput;
    }
    
    private String floatArrayToString(float[] array) {
        List<String> tmp = new ArrayList<>(array.length);
        
        for(float f : array)
            tmp.add(Float.toString(f));
        
        return tmp.stream().collect(Collectors.joining(" "));
    }
    
    private static String escapeName(String string) {
        return string.replace("$", "_");
    }
    
    enum ParamType {
        BOOL("bool", "bool_array", 1),
        FLOAT("float", "float_array", 1),
        FLOAT_MATRIX("float4x4", "float_array", 16),
        IDREF("idref", "IDREF_array", 1),
        INT("int", "int_array", 1),
        NAME("name", "Name_array", 1),
        SIDREF("sidref", "SIDREF_array", 1),
        TOKEN("token", "token_array", 1);
        
        private final String type;
        private final String element;
        private final int elementCount;
        
        private ParamType(String type, String element, int elementCount) {
            this.type = type;
            this.element = element;
            this.elementCount = elementCount;
        }
        
        public String getType() {
            return type;
        }
        
        public String getElement() {
            return element;
        }
        
        public int getElementCount() {
            return elementCount;
        }
    }
}
