package com.carbonldp;

public enum SpringProfile {
	LOCAL( "local" ),
	DEV( "dev" ),
	QA( "qa" ),
	PROD( "prod" );

	public static final SpringProfile DEFAULT = LOCAL;

	private final String name;

	private SpringProfile( String name ) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public static final SpringProfile findByName( String name ) {
		for ( SpringProfile profile : values() ) {
			if ( profile.getName().equals( name ) ) return profile;
		}
		return null;
	}
}
