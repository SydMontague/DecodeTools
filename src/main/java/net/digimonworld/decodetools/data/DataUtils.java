package net.digimonworld.decodetools.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.digimonworld.decodetools.core.StreamAccess;
import net.digimonworld.decodetools.data.keepdata.GenericKeepData;
import net.digimonworld.decodetools.res.ResPayload;
import net.digimonworld.decodetools.res.kcap.AbstractKCAP;
import net.digimonworld.decodetools.res.kcap.NormalKCAP;
import net.digimonworld.decodetools.res.payload.GenericPayload;

public class DataUtils {
    private DataUtils() {}

    public static <T> List<T> convertGenericToList(GenericPayload input, Function<StreamAccess, T> generator) {
        List<T> list = new ArrayList<>();
        try (StreamAccess access = new StreamAccess(input.getData())) {
            while (access.getPosition() < access.getSize())
                list.add(generator.apply(access));
        }
        return list;
    }
    
    public static GenericPayload convertListToGeneric(Collection<? extends GenericKeepData> data) {
        int size = data.stream().collect(Collectors.summingInt(GenericKeepData::getSize));
        byte[] buffer = new byte[size];
        
        try (StreamAccess access = new StreamAccess(buffer)) {
            data.forEach(a -> a.write(access));
        }
        
        return new GenericPayload(null, buffer);
    }
    
    public static <T> List<T> convertKCAPtoList(AbstractKCAP input, Function<StreamAccess, T> generator) {
        if(input == null)
            return Collections.emptyList();
        
        return input.getEntries().stream().map(a -> new StreamAccess(((GenericPayload) a).getData())).map(generator).collect(Collectors.toList());
    }
    
    public static NormalKCAP convertListToKCAP(List<? extends DecodeData> data, boolean genericAligned, boolean isUnknownFlagSet) {
        return new NormalKCAP(null, data.stream().map(DecodeData::toPayload).collect(Collectors.toList()), genericAligned, isUnknownFlagSet);
    }

    public static <T> Optional<T> castOptional(ResPayload payload, Class<T> clazz) {
        if(clazz == null)
            throw new ClassCastException("Can't cast to null class!");
        
        if(payload == null || payload.getType() == null)
            return Optional.empty();
        
        return Optional.of(clazz.cast(payload));
    }
}
