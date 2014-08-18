package com.base22.carbon.security.models;

import java.util.Set;
import java.util.UUID;

import com.base22.carbon.security.utils.AuthenticationUtil;

public abstract class UUIDObject {
	protected UUID uuid;

	public Long getId() {
		return this.getUuid().getLeastSignificantBits();
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getUuidString() {
		return uuid.toString();
	}

	public String getMinimizedUuidString() {
		return AuthenticationUtil.minimizeUUID(uuid);
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public void setUuid(String uuidString) {
		if ( uuidString.length() == 32 ) {
			StringBuilder uuidStringBuilder = new StringBuilder(uuidString);
			uuidStringBuilder.insert(8, "-");
			uuidStringBuilder.insert(13, "-");
			uuidStringBuilder.insert(18, "-");
			uuidStringBuilder.insert(23, "-");
			uuidString = uuidStringBuilder.toString();
		}
		uuid = UUID.fromString(uuidString);
	}

	public static String[] getMinimizedUUIDStrings(Set<? extends UUIDObject> uuidObjects) {
		String[] minimizedUUIDStrings = new String[uuidObjects.size()];
		UUIDObject[] uuidObjectsArray = uuidObjects.toArray(new UUIDObject[0]);

		for (int i = 0; i < uuidObjects.size(); i++) {
			minimizedUUIDStrings[i] = uuidObjectsArray[i].getMinimizedUuidString();
		}

		return minimizedUUIDStrings;
	}

	@Override
	public boolean equals(Object o) {
		if ( o == null ) {
			return false;
		}
		if ( o.getClass() != this.getClass() ) {
			return false;
		}
		return this.getClass().cast(o).getUuid().equals(this.getUuid());
	}

	@Override
	public int hashCode() {
		return this.getUuid().hashCode() * this.getClass().hashCode();
	}
}
