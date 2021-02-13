package org.hl7.tinkar.entity;

import io.activej.bytebuf.ByteBuf;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.hl7.tinkar.component.*;
import org.hl7.tinkar.component.graph.DiTree;
import org.hl7.tinkar.component.graph.Vertex;
import org.hl7.tinkar.component.location.PlanarPoint;
import org.hl7.tinkar.component.location.SpatialPoint;
import org.hl7.tinkar.entity.graph.DiGraphEntity;
import org.hl7.tinkar.entity.graph.DiTreeEntity;
import org.hl7.tinkar.entity.internal.Get;
import org.hl7.tinkar.lombok.dto.FieldDataType;
import org.hl7.tinkar.lombok.dto.graph.DiTreeDTO;

import java.time.Instant;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SemanticEntityVersion
        extends EntityVersion
        implements SemanticVersion {

    protected final MutableList<Object> fields = Lists.mutable.empty();

    @Override
    public FieldDataType dataType() {
        return FieldDataType.SEMANTIC_VERSION;
    }

    private SemanticEntity getSemanticEntity() {
        return (SemanticEntity) this.chronology;
    }

    @Override
    protected void finishVersionFill(ByteBuf readBuf, byte formatVersion) {
        fields.clear();
        int fieldCount = readBuf.readInt();
        for (int i = 0; i < fieldCount; i++) {
            FieldDataType dataType = FieldDataType.fromToken(readBuf.readByte());
            fields.add(readDataType(readBuf, dataType, formatVersion));
        }
    }

    public static Object readDataType(ByteBuf readBuf, FieldDataType dataType, byte formatVersion) {
        switch (dataType) {
            case BOOLEAN:
                return readBuf.readBoolean();
            case FLOAT:
                return readBuf.readFloat();
             case BYTE_ARRAY: {
                int length = readBuf.readInt();
                byte[] bytes = new byte[length];
                readBuf.read(bytes);
                return bytes;
            }
            case INTEGER:
                return readBuf.readInt();
            case STRING: {
                int length = readBuf.readInt();
                byte[] bytes = new byte[length];
                readBuf.read(bytes);
                return new String(bytes, UTF_8);
            }
            case DITREE:
                return DiTreeEntity.make(readBuf, formatVersion);
           case DIGRAPH:
                return DiGraphEntity.make(readBuf, formatVersion);
            case CONCEPT:
                return ConceptProxy.make(readBuf.readInt());
            case SEMANTIC:
                return SemanticProxy.make(readBuf.readInt());
            case PATTERN_FOR_SEMANTIC:
                return PatternForSemanticProxy.make(readBuf.readInt());
            case IDENTIFIED_THING:
                return EntityProxy.make(readBuf.readInt());
            case INSTANT:
                return Instant.ofEpochSecond(readBuf.readLong(), readBuf.readInt());
            case PLANAR_POINT:
                return new PlanarPoint(readBuf.readInt(), readBuf.readInt());
            case SPATIAL_POINT:
                return new SpatialPoint(readBuf.readInt(), readBuf.readInt(), readBuf.readInt());
             default:
                throw new UnsupportedOperationException("Can't handle field read of type: " +
                        dataType);
        }
    }

    @Override
    protected int subclassFieldBytesSize() {
        int size = 0;
        for (Object field: fields) {
            if (field instanceof Boolean) {
                size += 2;
            } else if (field instanceof Float) {
                size += 5;
            } else if (field instanceof byte[]) {
                byte[] byteArray = (byte[]) field;
                size += (5 + byteArray.length);
            } else if (field instanceof Integer) {
                size += 5;
            } else if (field instanceof String) {
                String string = (String) field;
                size += (5 + (string.length() * 2)); // token, length, upper bound on string bytes (average < 16 bit chars for UTF8...).
            } else if (field instanceof Entity) {
                size += 5;
            } else if (field instanceof EntityProxy) {
                size += 5;
            } else if (field instanceof Component) {
                size += 5;
            } else if (field instanceof DiTreeEntity) {
                DiTreeEntity treeEntity = (DiTreeEntity) field;
                size += treeEntity.size();
            } else {
                throw new UnsupportedOperationException("Can't handle field size for type: " +
                        field.getClass().getName());
            }        }
        return size;
    }

    @Override
    protected void writeVersionFields(ByteBuf writeBuf) {
        writeBuf.writeInt(fields.size());
        for (Object field: fields) {
            writeField(writeBuf, field);
        }
    }

    public static void writeField(ByteBuf writeBuf, Object field) {
        if (field instanceof Boolean) {
            writeBuf.writeByte(FieldDataType.BOOLEAN.token);
            writeBuf.writeBoolean((Boolean) field);
        } else if (field instanceof Float) {
            writeBuf.writeByte(FieldDataType.FLOAT.token);
            writeBuf.writeFloat((Float) field);
        } else if (field instanceof byte[]) {
            byte[] byteArray = (byte[]) field;
            writeBuf.writeByte(FieldDataType.BYTE_ARRAY.token);
            writeBuf.writeInt(byteArray.length);
            writeBuf.write(byteArray);
        } else if (field instanceof Integer) {
            writeBuf.writeByte(FieldDataType.INTEGER.token);
            writeBuf.writeInt((Integer) field);
        } else if (field instanceof Instant) {
            writeBuf.writeByte(FieldDataType.INSTANT.token);
            Instant instantField = (Instant) field;
            writeBuf.writeLong(instantField.getEpochSecond());
            writeBuf.writeInt(instantField.getNano());
        } else if (field instanceof String) {
            String string = (String) field;
            writeBuf.writeByte(FieldDataType.STRING.token);
            byte[] bytes = string.getBytes(UTF_8);
            writeBuf.writeInt(bytes.length);
            writeBuf.write(bytes);
        } else if (field instanceof Concept) {
            Concept concept = (Concept) field;
            writeBuf.writeByte(FieldDataType.CONCEPT.token);
            if (field instanceof ComponentWithNid) {
                writeBuf.writeInt(((ComponentWithNid) field).nid());
            } else {
                writeBuf.writeInt(Get.entityService().nidForPublicId(concept));
            }
        } else if (field instanceof Semantic) {
            Semantic semantic = (Semantic) field;
            writeBuf.writeByte(FieldDataType.SEMANTIC.token);
            if (field instanceof ComponentWithNid) {
                writeBuf.writeInt(((ComponentWithNid) field).nid());
            } else {
                writeBuf.writeInt(Get.entityService().nidForPublicId(semantic));
            }
        } else if (field instanceof PatternForSemantic) {
            PatternForSemantic pattern = (PatternForSemantic) field;
            writeBuf.writeByte(FieldDataType.PATTERN_FOR_SEMANTIC.token);
            if (field instanceof ComponentWithNid) {
                writeBuf.writeInt(((ComponentWithNid) field).nid());
            } else {
                writeBuf.writeInt(Get.entityService().nidForPublicId(pattern));
            }
        } else if (field instanceof Entity) {
            Entity entity = (Entity) field;
            writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
            writeBuf.writeInt(entity.nid);
        } else if (field instanceof EntityProxy) {
            EntityProxy proxy = (EntityProxy) field;
            writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
            writeBuf.writeInt(proxy.nid);
        } else if (field instanceof Component) {
            Component component = (Component) field;
            writeBuf.writeByte(FieldDataType.IDENTIFIED_THING.token);
            writeBuf.writeInt(Get.entityService().nidForPublicId(component));
        } else if (field instanceof DiTreeEntity) {
            DiTreeEntity diTreeEntity = (DiTreeEntity) field;
            writeBuf.writeByte(FieldDataType.DITREE.token);
            writeBuf.write(diTreeEntity.getBytes());
        } else if (field instanceof PlanarPoint) {
            writeBuf.writeByte(FieldDataType.PLANAR_POINT.token);
            PlanarPoint point = (PlanarPoint) field;
            writeBuf.writeInt(point.x);
            writeBuf.writeInt(point.y);
        } else if (field instanceof SpatialPoint) {
            writeBuf.writeByte(FieldDataType.SPATIAL_POINT.token);
            SpatialPoint point = (SpatialPoint) field;
            writeBuf.writeInt(point.x);
            writeBuf.writeInt(point.y);
            writeBuf.writeInt(point.z);
        } else {
            throw new UnsupportedOperationException("Can't handle field write of type: " +
                    field.getClass().getName());
        }
    }

    @Override
    public Component referencedComponent() {
        return Get.entityService().getEntityFast(getSemanticEntity().referencedComponentNid);
    }

    @Override
    public PatternForSemantic patternForSemantic() {
        return Get.entityService().getEntityFast(getSemanticEntity().definitionNid);
    }

    @Override
    public ImmutableList<Object> fields() {
        return fields.toImmutable();
    }

    public static SemanticEntityVersion make(SemanticEntity semanticEntity, ByteBuf readBuf, byte formatVersion) {
        SemanticEntityVersion version = new SemanticEntityVersion();
        version.fill(semanticEntity, readBuf, formatVersion);
        return version;
    }

    public static SemanticEntityVersion make(SemanticEntity semanticEntity, SemanticVersion versionToCopy) {
        SemanticEntityVersion version = new SemanticEntityVersion();
        version.fill(semanticEntity, versionToCopy);
        version.fields.clear();
        for (Object obj: versionToCopy.fields()) {
            if (obj instanceof Boolean) {
               version.fields.add(obj);
            } else if (obj instanceof Float) {
                version.fields.add(obj);
            } else if (obj instanceof byte[]) {
                version.fields.add(obj);
            } else if (obj instanceof Integer) {
                version.fields.add(obj);
            } else if (obj instanceof String) {
                version.fields.add(obj);
            } else if (obj instanceof Component) {
                Component component = (Component) obj;
                version.fields.add(EntityProxy.make(Get.entityService().nidForPublicId(component)));
            } else if (obj instanceof DiTreeDTO) {
                DiTree<Vertex> component = (DiTree<Vertex>) obj;
                version.fields.add(DiTreeEntity.make(component));
            } else {
                throw new UnsupportedOperationException("Can't handle field conversion of type: " +
                        obj.getClass().getName());
            }
        }
        return version;
    }
}
