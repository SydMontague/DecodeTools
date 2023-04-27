package net.digimonworld.decodetools.res.payload.xtvo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import net.digimonworld.decodetools.Main;
import net.digimonworld.decodetools.core.Utils;

import java.util.SortedMap;
import java.util.TreeMap;

//TODO allow change of vertex order?
public class XTVOVertex {
    private SortedMap<XTVOAttribute, List<Number>> vertexParams = new TreeMap<>();
    
    public XTVOVertex(SortedMap<XTVOAttribute, List<Number>> vertexParams) {
        this.vertexParams = vertexParams;
    }
    
    public XTVOVertex(ByteBuffer source, Collection<XTVOAttribute> attributes) {
        attributes.forEach(a -> {
            List<Number> list = new ArrayList<>();
            
            source.position(Utils.align(source.position(), a.getValueType().getAlignment()));
            
            for (int i = 0; i < a.getCount(); i++)
                list.add(a.getValueType().read(source));
            
            vertexParams.put(a, list);
        });
    }
    
    public ByteBuffer write() {
        checkNumberCount();
        
        int size = 0;
        
        for (XTVOAttribute attr : vertexParams.keySet()) {
            size = Utils.align(size, attr.getValueType().getAlignment());
            size += attr.getCount() * attr.getValueType().getAlignment();
        }
        
        size = Utils.align(size, 2); // all data is aligned to two bytes
        
        ByteBuffer buff = ByteBuffer.allocate(size);
        buff.order(ByteOrder.LITTLE_ENDIAN);
        
        vertexParams.forEach((a, b) -> {
            buff.position(Utils.align(buff.position(), a.getValueType().getAlignment()));
            
            for(int i = 0; i < a.getCount(); i++)
                a.getValueType().write(buff, i < b.size() ? b.get(i) : 0);
        });

        buff.position(Utils.align(buff.position(), 2));
        buff.flip();
        return buff;
    }
    
    public Float getX() {
        Entry<XTVOAttribute, List<Number>> positionEntry = getParameter(XTVORegisterType.POSITION);
        if (positionEntry == null || positionEntry.getValue().size() < 1) {
            return null;
        }
        return positionEntry.getValue().get(0).floatValue();
    }
    
    public Float getY() {
        Entry<XTVOAttribute, List<Number>> positionEntry = getParameter(XTVORegisterType.POSITION);
        if (positionEntry == null || positionEntry.getValue().size() < 2) {
            return null;
        }
        return positionEntry.getValue().get(1).floatValue();
    }

    public Float getZ() {
        Entry<XTVOAttribute, List<Number>> positionEntry = getParameter(XTVORegisterType.POSITION);
        if (positionEntry == null || positionEntry.getValue().size() < 3) {
            return null;
        }
        return positionEntry.getValue().get(2).floatValue();
    }
    
    public Float getNormalX() {
        Entry<XTVOAttribute, List<Number>> normalEntry = getParameter(XTVORegisterType.NORMAL);
        if (normalEntry == null || normalEntry.getValue().size() < 3) {
            return null;
        }
        return normalEntry.getValue().get(0).floatValue();
    }
    
    public Float getNormalY() {
        Entry<XTVOAttribute, List<Number>> normalEntry = getParameter(XTVORegisterType.NORMAL);
        if (normalEntry == null || normalEntry.getValue().size() < 3) {
            return null;
        }
        return normalEntry.getValue().get(1).floatValue();
    }
    
    public Float getNormalZ() {
        Entry<XTVOAttribute, List<Number>> normalEntry = getParameter(XTVORegisterType.NORMAL);
        if (normalEntry == null || normalEntry.getValue().size() < 3) {
            return null;
        }
        return normalEntry.getValue().get(2).floatValue();
    }
    
    public Entry<XTVOAttribute, List<Number>> getParameter(XTVORegisterType position) {
        return vertexParams.entrySet().stream().filter(a -> a.getKey().getRegisterId() == position).findFirst().orElse(null);
    }
    
    private void checkNumberCount() {
        vertexParams.forEach((k, v) -> {
            if(k.getCount() != v.size())
                Main.LOGGER.warning(() -> "XTVO Vertex, register " + k.getRegisterId() + " has not enough values.");
        });
    }
}
