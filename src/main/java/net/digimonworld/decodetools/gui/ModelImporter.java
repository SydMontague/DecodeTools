package net.digimonworld.decodetools.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIBone;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIPropertyStore;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.AIVertexWeight;
import org.lwjgl.assimp.Assimp;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Utils;
import net.digimonworld.decodetools.gui.util.FunctionAction;
import net.digimonworld.decodetools.gui.util.LinAlg.Vector3;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.HSEMKCAP;
import net.digimonworld.decodetools.res.kcap.HSMPKCAP;
import net.digimonworld.decodetools.res.kcap.TNOJKCAP;
import net.digimonworld.decodetools.res.kcap.XDIPKCAP;
import net.digimonworld.decodetools.res.kcap.XTVPKCAP;
import net.digimonworld.decodetools.res.payload.HSEMPayload;
import net.digimonworld.decodetools.res.payload.TNOJPayload;
import net.digimonworld.decodetools.res.payload.XDIOPayload;
import net.digimonworld.decodetools.res.payload.XTVOPayload;
import net.digimonworld.decodetools.res.payload.hsem.HSEM07Entry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMDrawEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMJointEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMMaterialEntry;
import net.digimonworld.decodetools.res.payload.hsem.HSEMTextureEntry;
import net.digimonworld.decodetools.res.payload.xdio.XDIOFace;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOAttribute;
import net.digimonworld.decodetools.res.payload.xtvo.XTVORegisterType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOValueType;
import net.digimonworld.decodetools.res.payload.xtvo.XTVOVertex;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ModelImporter extends PayloadPanel {
    private static final float[] IDENTITY_MATRIX = { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };
    private static final String JOINT_PREFIX = "J_";
    
    private static final int IMPORT_FLAGS = Assimp.aiProcess_SplitByBoneCount | Assimp.aiProcess_LimitBoneWeights;
    private static final AIPropertyStore importProperties = Assimp.aiCreatePropertyStore();
    static {
        Assimp.aiSetImportPropertyInteger(importProperties, Assimp.AI_CONFIG_PP_SBBC_MAX_BONES, 16);
        Assimp.aiSetImportPropertyInteger(importProperties, Assimp.AI_CONFIG_PP_LBW_MAX_WEIGHTS, 4);
        Assimp.aiSetImportPropertyInteger(importProperties, Assimp.AI_CONFIG_IMPORT_COLLADA_USE_COLLADA_NAMES, 1);
    }
    
    private HSMPKCAP rootKCAP;
    private AIScene scene;
    
    // persistent, loaded or via GUI
    private List<AINode> jointNodes = new ArrayList<>();
    private Map<String, float[]> jointBones = new HashMap<>();
    private float[] hsemHeaderArray = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
    
    // Swing garbage
    private final JLabel lblInput = new JLabel("Input:");
    private final JLabel lblnone = new JLabel("(None)");
    private final JButton btnSelect = new JButton("Open");
    private final JList<String> list = new JList<>();
    private final JScrollPane scrollPane = new JScrollPane();
    private final JButton btnDown = new JButton("↓");
    private final JButton btnUp = new JButton("↑");
    private final JPanel panel = new JPanel();
    private final JButton btnSave = new JButton("Update HSMP");
    private final JLabel lblScale = new JLabel("Scale:");
    private final JSpinner spinner = new JSpinner();
    private final JPanel panel_1 = new JPanel();
    private final JComboBox<Integer> comboBox = new JComboBox<>();
    private final JLabel lblNewLabel = new JLabel("Shader:");
    private final JButton btnNewButton = new JButton("Joints to OBJ");
    private final JButton btnExportDAE = new JButton("Export to DAE");
    
    // generated

    public ModelImporter(HSMPKCAP rootKCAP) {
        btnNewButton.addActionListener(a -> {
            if (this.rootKCAP == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            try (PrintStream out = new PrintStream(fileDialogue.getSelectedFile())) {
                tnojToObj(out);
            }
            catch (FileNotFoundException e) {
                //
            }
        });
        
        btnExportDAE.addActionListener(a -> {
            if (this.rootKCAP == null)
                return;
            
            JFileChooser fileDialogue = new JFileChooser("./Output");
            fileDialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileDialogue.showSaveDialog(null);
            
            if (fileDialogue.getSelectedFile() == null)
                return;
            
            saveModel(fileDialogue.getSelectedFile());
        });
        
        lblScale.setLabelFor(spinner);
        lblNewLabel.setLabelFor(comboBox);
        comboBox.setModel(new DefaultComboBoxModel<Integer>(new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 }));
        comboBox.setSelectedIndex(8);
        spinner.setModel(new SpinnerNumberModel(0f, null, null, 0.001f));
        setSelectedFile(rootKCAP);
        setupLayout();
        
        btnSave.setAction(new FunctionAction("Update HSMP", a -> loadModel()));
        
        btnUp.setAction(new AbstractAction("↑") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selIndex = list.getSelectedIndex();
                if(selIndex == -1)
                    return;
                
                if(selIndex <= 0)
                    return;

                AINode preNode = jointNodes.get(selIndex - 1);
                AINode node = jointNodes.get(selIndex);
                
                jointNodes.set(selIndex, preNode);
                jointNodes.set(selIndex - 1, node);
                list.setSelectedIndex(selIndex - 1);
                list.requestFocus();
                list.repaint();
            }
        });

        btnDown.setAction(new AbstractAction("↓") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selIndex = list.getSelectedIndex();
                if(selIndex == -1)
                    return;
                
                if(selIndex + 1 >= jointNodes.size())
                    return;

                AINode nextNode = jointNodes.get(selIndex + 1);
                AINode node = jointNodes.get(selIndex);
                
                jointNodes.set(selIndex, nextNode);
                jointNodes.set(selIndex + 1, node);
                list.setSelectedIndex(selIndex + 1);
                list.requestFocus();
                list.repaint();
            }
        });
        
        btnSelect.setAction(new AbstractAction("Open") {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ModelImporter.this.rootKCAP == null)
                    return;
                
                JFileChooser fileDialogue = new JFileChooser("./Input");
                fileDialogue.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileDialogue.showOpenDialog(null);
                
                if (fileDialogue.getSelectedFile() == null)
                    return;
                
                scene = Assimp.aiImportFileExWithProperties(fileDialogue.getSelectedFile().getPath(), IMPORT_FLAGS, null, importProperties);

                if(scene == null) {
                    Main.LOGGER.severe(() -> "assimp Error while importing model: " + Assimp.aiGetErrorString());
                    return;
                }
                
                jointNodes = nodeList(scene.mRootNode());
                // automatically sort the joints based on best guess
                List<ResPayload> rootTNOJ = ModelImporter.this.rootKCAP.getTNOJ().getEntries();
                
                for(int i = rootTNOJ.size() - 1; i >= 0; i--) {
                    String name = ((TNOJPayload) rootTNOJ.get(i)).getName();
                    
                    for(int j = 0; j < jointNodes.size(); j++) {
                        AINode node = jointNodes.get(j);
                        if(node.mName().dataString().equalsIgnoreCase(name)) {
                            jointNodes.remove(j);
                            jointNodes.add(0, node);
                            break;
                        }
                    }
                }

                float scale = calculateModelScale();
                spinner.setValue(scale);
                org.lwjgl.PointerBuffer scene_meshes = scene.mMeshes();
                for (int i=0; i < scene.mNumMeshes(); ++i)
                {
                    AIMesh mesh = AIMesh.create(scene_meshes.get(i));
                    for (int j=0; j < mesh.mNumBones(); ++j)
                    {
                        AIBone bone = AIBone.create(mesh.mBones().get(j));
                        AIMatrix4x4 offsetMatrix = bone.mOffsetMatrix();

                        // Here we're going to bake a scale into the inverse bind pose
                        // Since we need to apply the scale the the translations only in the bind pose,
                        // and the translations are mixed up with the rotations in the inverse bind pose,
                        // AND because location-rotation matrices have very simple inverses...
                        // We're going to un-invert the translation part of the inverse bind pose,
                        // apply the scale,
                        // and then re-invert
                        // We'll ignore the rotation to save flops, since our baked-in scale won't affect the rotation

                        // Set up the variables we're going to need
                        Vector3 t = new Vector3( offsetMatrix.a4(), offsetMatrix.b4(), offsetMatrix.c4() );
                        Vector3 u = new Vector3( offsetMatrix.a1(), offsetMatrix.b1(), offsetMatrix.c1() );
                        Vector3 v = new Vector3( offsetMatrix.a2(), offsetMatrix.b2(), offsetMatrix.c2() );
                        Vector3 w = new Vector3( offsetMatrix.a3(), offsetMatrix.b3(), offsetMatrix.c3() );

                        // Retrieve the translation from the bind pose, and apply the scale
                        Vector3 s = new Vector3( -u.dot(t) * scale, -v.dot(t) * scale, -w.dot(t)* scale );

                        // Re-invert
                        Vector3 x = new Vector3( offsetMatrix.a1(), offsetMatrix.a2(), offsetMatrix.a3() );
                        Vector3 y = new Vector3( offsetMatrix.b1(), offsetMatrix.b2(), offsetMatrix.b3() );
                        Vector3 z = new Vector3( offsetMatrix.c1(), offsetMatrix.c2(), offsetMatrix.c3() );
                        float t_x = -x.dot(s);
                        float t_y = -y.dot(s);
                        float t_z = -z.dot(s);

                        // Save the matrix for later
                        float[] ibpm = {
                                offsetMatrix.a1(), offsetMatrix.a2(), offsetMatrix.a3(), t_x,
                                offsetMatrix.b1(), offsetMatrix.b2(), offsetMatrix.b3(), t_y,
                                offsetMatrix.c1(), offsetMatrix.c2(), offsetMatrix.c3(), t_z,
                                offsetMatrix.d1(), offsetMatrix.d2(), offsetMatrix.d3(), offsetMatrix.d4()
                        };

                        jointBones.put(bone.mName().dataString(), ibpm);
                    }
                }

                lblnone.setText(fileDialogue.getSelectedFile().getPath());
                
                AbstractListModel<String> model = new AbstractListModel<String>() {
                    @Override
                    public String getElementAt(int index) {
                        return jointNodes.get(index).mName().dataString();
                    }
                    
                    @Override
                    public int getSize() {
                        return jointNodes.size();
                    }
                };
                list.setModel(model);
            }
        });
    }
    
    private void setupLayout() {
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(lblInput)
                            .addGap(9)
                            .addComponent(lblnone, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
                        .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                    .addGap(12)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(btnSelect)
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE))
                        .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblnone)
                        .addComponent(btnSelect)
                        .addComponent(btnSave)
                        .addComponent(lblInput))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
                    .addContainerGap())
        );
        GroupLayout gl_panel_1 = new GroupLayout(panel_1);
        gl_panel_1.setHorizontalGroup(
            gl_panel_1.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addComponent(lblScale)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(spinner, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lblNewLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_panel_1.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(btnNewButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(btnExportDAE)))
                    .addContainerGap(26, Short.MAX_VALUE))
        );
        gl_panel_1.setVerticalGroup(
            gl_panel_1.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_1.createSequentialGroup()
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblScale)
                        .addComponent(spinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNewLabel)
                        .addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
                    .addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnNewButton)
                        .addComponent(btnExportDAE))
                    .addContainerGap())
        );
        panel_1.setLayout(gl_panel_1);
        scrollPane.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        scrollPane.setColumnHeaderView(panel);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(btnUp);
        panel.add(btnDown);
        setLayout(groupLayout);
    }
    
    public void saveModel(File output) {
        try {
            new ColldadaExporter(rootKCAP).export(output);
        }
        catch (TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("resource")
    public void loadModel() {
        List<XDIOPayload> xdioPayload = new ArrayList<>();
        List<XTVOPayload> xtvoPayload = new ArrayList<>();
        List<HSEMEntry> hsemPayload = new ArrayList<>();
        List<TNOJPayload> tnoj = loadJoints((float) spinner.getValue());
        
        int materialId = -1;
        // TODO add option to add new GMIO
        for (short i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            
            if (mesh.mMaterialIndex() != materialId) {
                hsemPayload.add(new HSEMMaterialEntry((short) 0, (short) mesh.mMaterialIndex()));
                HashMap<Short, Short> map = new HashMap<>();
                
                map.put((short) 0, (short) mesh.mMaterialIndex());
                map.put((short) 2, (short) (scene.mNumMaterials() - 1));
                hsemPayload.add(new HSEMTextureEntry(map));
                materialId = mesh.mMaterialIndex();
            }
            
            AIVector3D.Buffer mVertices = mesh.mVertices();
            AIVector3D.Buffer mNormals = mesh.mNormals();
            AIVector3D.Buffer tex0Buff = mesh.mTextureCoords(0);
            AIVector3D.Buffer tex1Buff = mesh.mTextureCoords(1);
            AIColor4D.Buffer mColor = mesh.mColors(0);
            PointerBuffer mBones = mesh.mBones();
            AIFace.Buffer mFaces = mesh.mFaces();
            
            float biggest = 0;
            for (int j = 0; j < mesh.mNumVertices(); j++) {
                AIVector3D pos = mVertices.get(j);
                biggest = Math.max(Math.abs(pos.x()), biggest);
                biggest = Math.max(Math.abs(pos.y()), biggest);
                biggest = Math.max(Math.abs(pos.z()), biggest);
            }
            float vertexScale = 32767f / biggest;
            
            short offsetCounter = 0;
            
            XTVOAttribute vertexAttrib = new XTVOAttribute(XTVORegisterType.POSITION, offsetCounter, (byte) 3, XTVOValueType.SHORT, 1.0f / (vertexScale / (float) spinner.getValue()));
            offsetCounter += 6;
            XTVOAttribute normalAttrib = new XTVOAttribute(XTVORegisterType.NORMAL, offsetCounter, (byte) 3, XTVOValueType.BYTE, 1.0f / 127f);
            offsetCounter += mNormals != null ? 3 : 0;
            XTVOAttribute colorAttrib = new XTVOAttribute(XTVORegisterType.COLOR, offsetCounter, (byte) 4, XTVOValueType.UBYTE, 1.0f / 255f);
            offsetCounter += mColor != null ? 4 : 0;
            XTVOAttribute idxAttrib = new XTVOAttribute(XTVORegisterType.IDX, offsetCounter, (byte) 4, XTVOValueType.UBYTE, 1.0f / 255f);
            offsetCounter += mesh.mNumBones() != 0 ? 4 : 0;
            XTVOAttribute wgtAttrib = new XTVOAttribute(XTVORegisterType.WEIGHT, offsetCounter, (byte) 4, XTVOValueType.UBYTE, 1.0f / 255f);
            offsetCounter += mesh.mNumBones() != 0 ? 4 : 0;
            
            offsetCounter = (short) (offsetCounter + offsetCounter % 2);
            XTVOAttribute tex0Attrib = new XTVOAttribute(XTVORegisterType.TEXTURE0, offsetCounter, (byte) 2, XTVOValueType.SHORT, 1.0f / 32767f);
            offsetCounter += tex0Buff != null ? 4 : 0;
            XTVOAttribute tex1Attrib = new XTVOAttribute(XTVORegisterType.TEXTURE1, offsetCounter, (byte) 2, XTVOValueType.SHORT, 1.0f / 32767f);
            
            List<XTVOAttribute> attribList = new ArrayList<>();
            attribList.add(vertexAttrib);
            attribList.add(mNormals != null ? normalAttrib : null);
            attribList.add(mColor != null ? colorAttrib : null);
            attribList.add(mesh.mNumBones() != 0 ? idxAttrib : null);
            attribList.add(mesh.mNumBones() != 0 ? wgtAttrib : null);
            attribList.add(tex0Buff != null ? tex0Attrib : null);
            attribList.add(tex1Buff != null ? tex1Attrib : null);
            attribList.removeIf(Objects::isNull);
            
            List<SortedMap<XTVOAttribute, List<Number>>> vertices = new ArrayList<>();
            
            for (int j = 0; j < mesh.mNumVertices(); j++) {
                SortedMap<XTVOAttribute, List<Number>> vertex = new TreeMap<>();
                
                AIVector3D pos = mVertices.get(j);
                vertex.put(vertexAttrib, List.of(pos.x() * vertexScale, pos.y() * vertexScale, pos.z() * vertexScale));
                
                if (mNormals != null) {
                    AIVector3D normal = mNormals.get(j);
                    vertex.put(normalAttrib, List.of(normal.x() * 127.0f, normal.y() * 127.0f, normal.z() * 127.0f));
                }
                if (mColor != null) {
                    AIColor4D color = mColor.get(j);
                    vertex.put(colorAttrib, List.of(color.r() * 255f, color.g() * 255f, color.b() * 255f, color.a() * 255f));
                }
                if (tex0Buff != null) {
                    AIVector3D tex0 = tex0Buff.get(j);
                    vertex.put(tex0Attrib, List.of((tex0.x() % 1.0f) * 32767f, (tex0.y() % 1.0f) * 32767f));
                }
                if (tex1Buff != null) {
                    AIVector3D tex1 = tex1Buff.get(j);
                    vertex.put(tex1Attrib, List.of((tex1.x() % 1.0f) * 32767f, (tex1.y() % 1.0f) * 32767f));
                }
                
                vertices.add(vertex);
            }

            Map<Short, Short> boneMapping = new HashMap<>();
            // bone mapping
            if (mesh.mNumBones() != 0) {
                for (short j = 0; j < mesh.mNumBones(); j++) {
                    AIBone bone = AIBone.create(mBones.get(j));
                    AIVertexWeight.Buffer weights = bone.mWeights();
                    
                    short id = 0;
                    for (; id < tnoj.size(); id++)
                        if (tnoj.get(id).getName().equals(bone.mName().dataString()))
                            break;
                    
                    boneMapping.put(j, id);
                    
                    for (int k = 0; k < bone.mNumWeights(); k++) {
                        AIVertexWeight weight = weights.get(k);
                        int vertexId = weight.mVertexId();
                        float val = weight.mWeight() * 255;
                        
                        vertices.get(vertexId).computeIfAbsent(idxAttrib, a -> new ArrayList<Number>()).add(j * 3);
                        vertices.get(vertexId).computeIfAbsent(wgtAttrib, a -> new ArrayList<Number>()).add(val);
                    }
                }
                
                vertices.forEach(a -> {
                    a.putIfAbsent(wgtAttrib, new ArrayList<>());
                    a.putIfAbsent(idxAttrib, new ArrayList<>());
                    Utils.padList(a.get(wgtAttrib), wgtAttrib.getCount(), 0);
                    Utils.padList(a.get(idxAttrib), idxAttrib.getCount(), 0);
                });
            }
            
            List<XTVOVertex> xtvoVertices = vertices.stream().map(XTVOVertex::new).collect(Collectors.toCollection(ArrayList::new));
            List<XDIOFace> faces = new ArrayList<>();
            for (int j = 0; j < mesh.mNumFaces(); j++) {
                AIFace face = mFaces.get(j);
                faces.add(new XDIOFace(face.mIndices().get(0), face.mIndices().get(1), face.mIndices().get(2)));
            }
            
            xtvoPayload.add(new XTVOPayload(null, attribList, xtvoVertices, (int) comboBox.getSelectedItem(), 5, (short) 0x3001, (short) 0, 0x00010309, 0x73, 0x01));
            xdioPayload.add(new XDIOPayload(null, faces, (short) 0x3001, (short) 0, 5));
            
            hsemPayload.add(new HSEM07Entry((short) 0x000F, (short) 0, (short) 0, (short) 0));
            if(!boneMapping.isEmpty())
                hsemPayload.add(new HSEMJointEntry(boneMapping));
            hsemPayload.add(new HSEMDrawEntry((short) 4, i, i, (short) 0, (short) 0, (short) 0, faces.size() * 3));
        }
        
        float[] headerArray = rootKCAP.getHSEM().get(0).getHeaderData();
        HSEMPayload hsemEntry = new HSEMPayload(null, hsemPayload, -1, 0, hsemHeaderArray, 1, 0);
        
        Main.LOGGER.info(String.format("XDIO: %d | XTVO: %d", xdioPayload.size(), xtvoPayload.size()));
        
        rootKCAP.setHSEM(new HSEMKCAP(rootKCAP, List.of(hsemEntry)));
        rootKCAP.setXDIP(new XDIPKCAP(rootKCAP, xdioPayload));
        rootKCAP.setXTVP(new XTVPKCAP(rootKCAP, xtvoPayload));
        if(!tnoj.isEmpty())
            rootKCAP.setTNOJ(new TNOJKCAP(rootKCAP, tnoj));
    }
    
    private float calculateModelScale() {
        if(jointNodes.isEmpty())
            return 1.0f;
        
        List<ResPayload> tnojEntries = rootKCAP.getTNOJ().getEntries();
        List<Float> floats = new ArrayList<>();
        
        for(AINode bla : jointNodes) {
            Optional<ResPayload> res = tnojEntries.stream().filter(a -> ((TNOJPayload) a).getName().equals(bla.mName().dataString())).findFirst();
            
            if(!res.isPresent())
                continue;
            
            float[] scales = getScales(bla);
            TNOJPayload p = ((TNOJPayload) res.get());
            float xFactor = p.getXOffset() / bla.mTransformation().a4() / scales[0];
            float yFactor = p.getYOffset() / bla.mTransformation().b4() / scales[1];
            float zFactor = p.getZOffset() / bla.mTransformation().c4() / scales[2];
            
            if(!Float.isNaN(xFactor) && Math.abs(xFactor) != 0.0f)
                floats.add(xFactor);
            if(!Float.isNaN(yFactor) && Math.abs(yFactor) != 0.0f)
                floats.add(yFactor);
            if(!Float.isNaN(zFactor) && Math.abs(zFactor) != 0.0f)
                floats.add(zFactor);
        }
        
        floats.sort(Comparator.naturalOrder());
        return floats.get(floats.size() / 2);
    }
    
    @SuppressWarnings("resource")
    private static float[] getScales(AINode node) {
        float[] scales = { 1.0f, 1.0f, 1.0f };
        
        AINode currentNode = node;
        do {
            AIVector3D pos = AIVector3D.create();
            AIQuaternion quat = AIQuaternion.create();
            AIVector3D sc = AIVector3D.create();
            Assimp.aiDecomposeMatrix(currentNode.mTransformation(), sc, quat, pos);

            scales[0] *= sc.x();
            scales[1] *= sc.y();
            scales[2] *= sc.z();
            
            currentNode = currentNode.mParent();
        } while(isJointNode(currentNode));
        
        return scales;
    }
    
    @SuppressWarnings("resource")
    private static boolean isJointNode(AINode node) {
        if(node == null)
            return false;
        
        String name = node.mName().dataString();
        
        return name.startsWith("J_") || name.startsWith("loc_");
    }
    
    @SuppressWarnings("resource")
    private static List<AINode> nodeList(AINode root) {
        Deque<AINode> nodeQueue = new LinkedList<>();
        nodeQueue.add(root);
        
        List<AINode> nodeList = new ArrayList<>();
        
        while (!nodeQueue.isEmpty()) {
            AINode node = nodeQueue.removeLast();

            for (int i = 0; i < node.mNumChildren(); i++)
                nodeQueue.add(AINode.create(node.mChildren().get(i)));
            
            if (!node.mName().dataString().startsWith(JOINT_PREFIX))
                continue;
            
            nodeList.add(node);
        }
        
        return nodeList;
    }

    @SuppressWarnings("resource")
    private List<TNOJPayload> loadJoints(float scale) {
        List<TNOJPayload> tnojList = new ArrayList<>();
        List<String> names = new ArrayList<>();
        
        for(AINode bla : jointNodes) {
            AINode parent = bla.mParent();
            AIMatrix4x4 trans = bla.mTransformation();
            
            String name = bla.mName().dataString();
            
            int parentId = parent != null ? names.indexOf(parent.mName().dataString()) : -1;
            
            if(parentId == -1 && parent != null && parent.mName().dataString().startsWith(JOINT_PREFIX))
                Main.LOGGER.severe(() -> "AINode " + name + " order is invalid, parent node has not been processed yet.");
            
            int unknown1 = 0;
            int unknown2 = 0;

            AIVector3D pos = AIVector3D.create();
            AIQuaternion quat = AIQuaternion.create();
            AIVector3D sc = AIVector3D.create();
            Assimp.aiDecomposeMatrix(trans, sc, quat, pos);

            float[] local_matrix = {
                    trans.a1(), trans.a2(), trans.a3(), trans.a4(),
                    trans.b1(), trans.b2(), trans.b3(), trans.b4(),
                    trans.c1(), trans.c2(), trans.c3(), trans.c4(),
                    trans.d1(), trans.d2(), trans.d3(), trans.d4()
            };
            float[] offsetVector = { trans.a4() * scale, trans.b4() * scale, trans.c4() * scale, 0.0f };
            float[] rotationQuat = { quat.x(), quat.y(), quat.z(), quat.w() }; // Need to extract the scale first
            float[] scaleVector = { 1.0f, 1.0f, 1.0f, 0.0f };
            float[] localScaleVector = { sc.x(), sc.y(), sc.z(), 0.0f };
            
            if (!name.startsWith(JOINT_PREFIX))
                continue;
            
            float[] ibpm = jointBones.getOrDefault(name, IDENTITY_MATRIX);
            names.add(name);
            tnojList.add(new TNOJPayload(null, parentId, name, unknown1, unknown2, ibpm, offsetVector, rotationQuat, scaleVector, localScaleVector));
        }
        
        return tnojList;
    }
    
    private void tnojToObj(PrintStream stream) {
        int vertexOffset = 1;
        
        for(ResPayload jnt : rootKCAP.getTNOJ()) {
            TNOJPayload tmp = (TNOJPayload) jnt;
            stream.println("g " + tmp.getName());
            stream.println(String.format("v %f %f %f", -tmp.getOffsetMatrix()[3], -tmp.getOffsetMatrix()[7], -tmp.getOffsetMatrix()[11]));
            stream.println("f " + vertexOffset++);
        }
    }

    @Override
    public void setSelectedFile(Object file) {
        if(file == rootKCAP)
            return;
        
        lblnone.setText("(None)");
        rootKCAP = null;
        if(scene != null)
            scene.close();
        scene = null;
        jointNodes.clear();
        
        if (file == null)
            return;
        
        if (!(file instanceof HSMPKCAP)) {
            Main.LOGGER.warning("Tried to select non-HSMP File in HSMPPanel.");
            return;
        }

        rootKCAP = (HSMPKCAP) file;
    }
}
