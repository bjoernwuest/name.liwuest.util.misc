package name.liwuest.util.jpa;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true) public class CUUIDConverter implements AttributeConverter<UUID, Object> {
	@Override public Object convertToDatabaseColumn(final UUID entityValue) { return Optional.ofNullable(entityValue).map(entityUuid -> entityUuid).orElse(null); }
	@Override public UUID convertToEntityAttribute(final Object databaseValue) { return Optional.ofNullable(databaseValue).map(databaseUuid -> UUID.class.cast(databaseUuid)).orElse(null); }
}
