package de.phoenixstaffel.decodetools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.lwjgl.assimp.AINode;

import de.phoenixstaffel.decodetools.arcv.ARCVFile;
import de.phoenixstaffel.decodetools.core.Access;
import de.phoenixstaffel.decodetools.gui.JLogWindow;
import de.phoenixstaffel.decodetools.gui.MainWindow;

//TODO store settings and preferences
public class Main {
    public static final Logger LOGGER = Logger.getLogger("Decode Tool");
    
    private Main() {
        // no implementation
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 3 && "rebuild".equalsIgnoreCase(args[0])) {
            File input = new File(args[1]);
            File output = new File(args[2]);
            
            if (!input.isDirectory()) {
                LOGGER.severe("The input value must be a directory.");
                return;
            }
            
            if (!output.isDirectory())
                output.mkdirs();
            
            ARCVFile arcv = new ARCVFile(input, true);
            arcv.saveFiles(output);
        }
        else {
            new JLogWindow(LOGGER, Logger.getLogger(Access.class.getName())).setVisible(true);
            new MainWindow().setVisible(true);
        }
        
/*
        try(FileAccess data = new FileAccess(new File("Input/camp/EndrollPack.pack")); 
                PrintStream eData = new PrintStream("DecodeCredits.csv")) {
            DataMiner enemyData = new DataMiner(data, new Structure(new File("MinerDefinitions/DecodeCredits.yml")));

            eData.println(enemyData.asCSV());
        }*/
        /*
        Files.walk(Paths.get("./Input/tx/")).filter(a -> a.getFileName().toString().startsWith("Tex")).forEach(a -> {
            if (!a.toFile().isFile())
                return;
            try (Access b = new FileAccess(a.toFile())) {
                ResFile f = new ResFile(b);
                
                if(f.getRoot().getType() == Payload.GMIO) {
                    ImageIO.write(((GMIOPayload) f.getRoot()).getImage(), "PNG", new File("Output/ExtraMaps/"+a.getFileName()+".png"));
                    return;
                }
                AbstractKCAP root = (AbstractKCAP) f.getRoot(); //(AbstractKCAP) ((AbstractKCAP) f.getRoot()).get(1);

                System.out.println(root.getEntries().size());
                List<ResPayload> list = root.getElementsWithType(Payload.GMIO);
                list = list.stream().filter(g -> g.getParent() == root).collect(Collectors.toList());
                
                int i = 0;
                
                for(ResPayload x : list) {
                    GMIOPayload p = (GMIOPayload) x;
                    ImageIO.write(p.getImage(), "PNG", new File("Output/ExtraMaps/"+a.getFileName()+"_"+i+++".png"));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        */
        
        /*
        Files.walk(Paths.get("./Input/map/")).filter(a -> a.toString().endsWith(".res")).forEach(a -> {
            if (!a.toFile().isFile())
                return;
            try (Access b = new FileAccess(a.toFile())) {
                //System.out.println("Opening " + a);
                ResFile f = new ResFile(b);
                AbstractKCAP root = (AbstractKCAP) ((AbstractKCAP) f.getRoot()).get(1);
                
                List<ResPayload> list = root.getElementsWithType(Payload.GENERIC);
                list = list.stream().filter(g -> g.getParent() == root).filter(g -> g.getSize() % 0x4C == 0).collect(Collectors.toList());
                
                if(list.size()!= 0) {
                    GenericPayload pl = (GenericPayload) list.get(0);
                    
                    Path file = Files.createTempFile("Spawns", ".data");
                    
                    try(FileAccess dest = new FileAccess(file.toFile())) {
                        pl.writeKCAP(dest, null);
                        
                        dest.setPosition(0);
                        
                        while(dest.getPosition() < dest.getSize()) {
                            System.out.print(a.getFileName().toString().replace(".res", "")); // map
                            System.out.print(',');
                            System.out.print(dest.readFloat()); // X
                            System.out.print(',');
                            System.out.print(dest.readFloat()); // Y
                            System.out.print(',');
                            System.out.print(dest.readFloat()); // Z
                            
                            for(int i = 0; i < 8; i++) {
                                System.out.print(',');
                                System.out.print(dest.readInteger()); // itemId
                                System.out.print(',');
                                System.out.print(dest.readFloat()); // weight
                            }
                            
                            System.out.println();
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });*/
        /*
        Access access = new FileAccess(new File("Input/Digimon/Partner/digi31.res"));
        ResFile digi31 = new ResFile(access);
        access.close();
        HSMPKCAP digi31hsmp = ((HSMPKCAP) ((NormalKCAP) digi31.getRoot()).get(0));
        
        //new ModelImporter(digi31hsmp, "Gabumon.dae").setVisible(true);;
        
        AIPropertyStore store = Assimp.aiCreatePropertyStore();
        Assimp.aiSetImportPropertyInteger(store, Assimp.AI_CONFIG_PP_SBBC_MAX_BONES, 16);
        Assimp.aiSetImportPropertyInteger(store, Assimp.AI_CONFIG_PP_LBW_MAX_WEIGHTS, 4);
        Assimp.aiSetImportPropertyInteger(store, Assimp.AI_CONFIG_IMPORT_COLLADA_USE_COLLADA_NAMES, 1);
        
        AIScene scene = Assimp.aiImportFileExWithProperties("Gabumon.dae", Assimp.aiProcess_SplitByBoneCount | Assimp.aiProcess_LimitBoneWeights, null, store);
        
        XTVOAttribute vertexAttrib = new XTVOAttribute(XTVORegisterType.POSITION, (short) 0, (byte) 3, XTVOValueType.SHORT, 1.0f / 1024.0f);
        XTVOAttribute normalAttrib = new XTVOAttribute(XTVORegisterType.NORMAL, (short) 6, (byte) 3, XTVOValueType.BYTE, 1.0f / 127f);
        // color attrib
        XTVOAttribute idxAttrib = new XTVOAttribute(XTVORegisterType.IDX, (short) 9, (byte) 4, XTVOValueType.UBYTE, 1.0f / 255f);
        XTVOAttribute wgtAttrib = new XTVOAttribute(XTVORegisterType.WEIGHT, (short) 13, (byte) 4, XTVOValueType.UBYTE, 1.0f / 255f);
        XTVOAttribute tex0Attrib = new XTVOAttribute(XTVORegisterType.TEXTURE0, (short) 18, (byte) 2, XTVOValueType.SHORT, 1.0f / 512f);
        // texture1 attrib
        
        List<XDIOPayload> xdioPayload = new ArrayList<>();
        List<XTVOPayload> xtvoPayload = new ArrayList<>();
        List<HSEMEntry> hsemPayload = new ArrayList<>();
        // material, texture
        // for each -> 07, joint, draw
        HSEMMaterialEntry matEntry = new HSEMMaterialEntry((short) 0, (short) 0);
        Map<Short, Short> texAssignments = new HashMap<>();
        texAssignments.put((short) 0, (short) 0);
        texAssignments.put((short) 2, (short) 1);
        HSEMTextureEntry texEntry = new HSEMTextureEntry(texAssignments);
        hsemPayload.add(matEntry);
        hsemPayload.add(texEntry);
        
        Deque<AINode> blaList = new LinkedList<>();
        blaList.add(scene.mRootNode());
        
        List<TNOJPayload> tnoj = new ArrayList<>();
        List<String> names = new ArrayList<>();
        
        float[] identMatrix = { 1.0f, 0.0f, 0.0f, 0.0f,
                                0.0f, 1.0f, 0.0f, 0.0f,
                                0.0f, 0.0f, 1.0f, 0.0f,
                                0.0f, 0.0f, 0.0f, 1.0f};
        
        while (!blaList.isEmpty()) {
            AINode bla = blaList.removeLast();
            AINode parent = bla.mParent();
            
            String name = bla.mName().dataString();
            
            int parentId = parent != null ? names.indexOf(parent.mName().dataString()) : -1;
            int unknown1 = 0;
            int unknown2 = 0;


            AIMatrix4x4 trans = bla.mTransformation();
            float[] parentMatrix = parentId != -1 ? tnoj.get(parentId).getOffsetMatrix() : identMatrix;
            
            float scale = 19.312534313608478743465495428831f;
            
            float[] offsetVector = { trans.a4() * scale, trans.b4() * scale, trans.c4() * scale, 0.0f };
            float[] unknownVector = { 0.0f, 0.0f, 0.0f, 1.0f };
            float[] scaleVector = { 1.0f, 1.0f, 1.0f, 0.0f };
            float[] localScaleVector = { 1.0f, 1.0f, 1.0f, 0.0f };
            
            for (int i = bla.mNumChildren() - 1; i >= 0 ; i--) {
                AINode child = AINode.create(bla.mChildren().get(i));
                
                if(child.mName().dataString().startsWith("J_ear"))
                    blaList.addFirst(child);
                else 
                    blaList.add(child);
                
            }

            if(!name.startsWith("J_"))
                continue;
            
            names.add(name);
            tnoj.add(new TNOJPayload(null, parentId, name, unknown1, unknown2, parentMatrix, 
                                     offsetVector, unknownVector, scaleVector, localScaleVector));
        }

        digi31hsmp.setTNOJ(new TNOJKCAP(digi31hsmp, tnoj));
        
        // 1226.7298314
        
        // 19.312534313608478743465495428831
        for(short i = 0; i < scene.mNumMeshes(); i++) {
            AIMesh mesh = AIMesh.create(scene.mMeshes().get(i));
            AIVector3D.Buffer mVertices = mesh.mVertices();
            AIVector3D.Buffer mNormals = mesh.mNormals();
            PointerBuffer mBones = mesh.mBones();
            AIVector3D.Buffer tex0Buff = mesh.mTextureCoords(0);
            AIFace.Buffer mFaces = mesh.mFaces();
            

            AIVector3D.Buffer tex1Buff = mesh.mTextureCoords(1);
            
            HSEM07Entry unk07Entry = new HSEM07Entry((short) 0x000F, (short) 0, (short) 0, (short) 0);
            hsemPayload.add(unk07Entry);

            List<SortedMap<XTVOAttribute, List<Number>>> vertices = new ArrayList<>();

            for (int j = 0; j < mesh.mNumVertices(); j++) {
                SortedMap<XTVOAttribute, List<Number>> vertex = new TreeMap<>();
                
                AIVector3D pos = mVertices.get(j);
                vertex.put(vertexAttrib, List.of(pos.x() * 19776.0f, pos.y() * 19776.0f, pos.z() * 19776.0f));
                
                AIVector3D normal = mNormals.get(j);
                vertex.put(normalAttrib, List.of(normal.x() * 127.0f, normal.y() * 127.0f, normal.z() * 127.0f));
                
                AIVector3D tex0 = tex0Buff.get(j);
                vertex.put(tex0Attrib, List.of(tex0.x() * 512f, ((tex0.y() - 1) * 512f)));
                
                vertices.add(vertex);
            }
            
            Map<Short, Short> boneMapping = new HashMap<>();
            
            for (short j = 0; j < mesh.mNumBones(); j++) {
                AIBone bone = AIBone.create(mBones.get(j));
                AIVertexWeight.Buffer weights = bone.mWeights();

                TNOJKCAP tnojKCAP = digi31hsmp.getTNOJ();
                short id = 0;
                for(; id < tnojKCAP.getEntryCount(); id++)
                    if(tnojKCAP.get(id).getName().equals(bone.mName().dataString())) {
                        break;
                    }

                boneMapping.put(j, id);
                
                for(int k = 0; k < bone.mNumWeights(); k++) {
                    AIVertexWeight weight = weights.get(k);
                    int vertexId = weight.mVertexId();
                    float val = weight.mWeight() * 255;
                    
                    vertices.get(vertexId).computeIfAbsent(idxAttrib, a -> new ArrayList<Number>()).add(j * 3);
                    vertices.get(vertexId).computeIfAbsent(wgtAttrib, a -> new ArrayList<Number>()).add(val);
                }
            }
            
            hsemPayload.add(new HSEMJointEntry(boneMapping));
            
            vertices.forEach(a -> {
                a.putIfAbsent(wgtAttrib, new ArrayList<Number>());
                a.compute(wgtAttrib, (k, v) -> {
                    for(int size = v.size(); size < k.getCount(); size++)
                        v.add(0);
                    
                    return v;
                });
                a.putIfAbsent(idxAttrib, new ArrayList<Number>());
                a.compute(idxAttrib, (k, v) -> {
                    for(int size = v.size(); size < k.getCount(); size++)
                        v.add(0);
                    
                    return v;
                });
            });
            
            List<XTVOAttribute> attribList = List.of(vertexAttrib, normalAttrib, idxAttrib, wgtAttrib, tex0Attrib);
            List<XTVOVertex> xtvoVertices = vertices.stream().map(XTVOVertex::new).collect(Collectors.toCollection(ArrayList::new));
            
            short id = 0;
            int shaderId = 8;
            XTVOPayload xtvo = new XTVOPayload(null, attribList, xtvoVertices, shaderId, 5, (short) 0x3001, id, 0x00010309, 0x73, 0x01);

            
            List<XDIOFace> faces = new ArrayList<>();
            for(int j = 0; j < mesh.mNumFaces(); j++) {
                AIFace face = mFaces.get(j);
                faces.add(new XDIOFace(face.mIndices().get(0), face.mIndices().get(1), face.mIndices().get(2)));
            }
            
            hsemPayload.add(new HSEMDrawEntry((short) 4, i, i, (short) 0, (short) 0, (short) 0, faces.size() * 3));
            
            XDIOPayload xdio = new XDIOPayload(null, faces, (short) 0x3001, (short) 0, 5);
            
            xdioPayload.add(xdio);
            xtvoPayload.add(xtvo);
        }
        
        scene.close();
        
        
        float[] headerArray = { 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f };
        
        HSEMPayload hsemEntry = new HSEMPayload(null, hsemPayload, -1, 0, headerArray, 1, 0);
        
        digi31hsmp.setXDIP(new XDIPKCAP(digi31hsmp, xdioPayload));
        digi31hsmp.setXTVP(new XTVPKCAP(digi31hsmp, xtvoPayload));
        digi31hsmp.setHSEM(new HSEMKCAP(digi31hsmp, List.of(hsemEntry)));
        
        digi31.repack(new File("output/digi31.newModel.res"));
        
        System.out.println("Export complete");*/
//        
//        
//        
//        for(int i = 0; i < scene.mNumMeshes(); i++) {
//            System.out.println(AIMesh.create(scene.mMeshes().get(i)).mNumBones());
//            System.out.println(AIMesh.create(scene.mMeshes().get(i)).mNumVertices());
//
//            for(int j = 0; j < AIMesh.create(scene.mMeshes().get(i)).mNumBones(); j++)
//                System.out.println(AIBone.create(AIMesh.create(scene.mMeshes().get(i)).mBones().get(j)).mName().dataString());
//        }
//
//        AIMesh mesh;
//        AIBone bone;
//        AIVertexWeight weight;
//        
//        
//        System.out.println(scene.mNumMeshes());
//        System.out.println(scene.mRootNode().mNumChildren());
//        
//        System.out.println();
//        
//        Deque<Bla> blaList = new LinkedList<>();
//        blaList.add(new Bla(scene.mRootNode(), 0));
//        
//        while(!blaList.isEmpty()) {
//            Bla bla = blaList.removeLast();
//            
//            String tab = "";
//            for(int i = 0; i < bla.depth; i++)
//                tab += "  ";
//        
//            System.out.println(tab + bla.node.mName().dataString());
//            
//            AIMatrix4x4 trans = bla.node.mTransformation();
//            
//            System.out.println(tab + trans.a1() + " " + trans.a2() + " " + trans.a3() + " " + trans.a4());
//            System.out.println(tab + trans.b1() + " " + trans.b2() + " " + trans.b3() + " " + trans.b4());
//            System.out.println(tab + trans.c1() + " " + trans.c2() + " " + trans.c3() + " " + trans.c4());
//            System.out.println(tab + trans.d1() + " " + trans.d2() + " " + trans.d3() + " " + trans.d4());
//            
//            for(int i = 0; i < bla.node.mNumChildren(); i++)
//                blaList.add(new Bla(AINode.create(bla.node.mChildren().get(i)), bla.depth + 1));
//            
//        }
//
//        scene.close();
//        store.close();
        
        //try(Access a = new FileAccess(new File("Input\\Keep\\GlobalKeepData.res"))) {
        //    String s = new DataMiner(a, new Structure(new File("MinerDefinitions\\DecodeArenaLootTables.yml"))).asCSV();
        //    System.out.println(s);
        //}
    }
    
    static class Bla {
        AINode node;
        int depth;
        
        public Bla(AINode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }
}
