package org.hl7.tinkar.component;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.hl7.tinkar.common.util.id.PublicId;
import org.hl7.tinkar.common.util.id.PublicIds;

import java.util.Optional;
import java.util.UUID;

public interface ChronologyService {

    default <T extends Chronology<V>, V extends Version> Optional<T> getChronology(UUID... uuids) {
        return getChronology(PublicIds.of(uuids));
    }

    default <T extends Chronology<V>, V extends Version> Optional<T> getChronology(Component component) {
        return getChronology(component.publicId());
    }

    <T extends Chronology<V>, V extends Version> Optional<T> getChronology(PublicId publicId);

    void putChronology(Chronology chronology);
}
