package net.sindarin27.farsightedmobs;

import com.mojang.serialization.Codec;

import java.util.List;

public class CodecUtility {
    static <E> Codec<List<E>> listOrSingle(final Codec<E> elementCodec) {
        return Codec.withAlternative(Codec.list(elementCodec), elementCodec.xmap(List::of, List::getFirst));
    }
}
