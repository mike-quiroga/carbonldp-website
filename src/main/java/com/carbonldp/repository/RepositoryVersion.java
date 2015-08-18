package com.carbonldp.repository;

import com.carbonldp.Consts;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class RepositoryVersion implements Comparable<RepositoryVersion> {
	private final int major;
	private final int minor;
	private final int fix;
	private final String label;

	public RepositoryVersion( String version ) {
		this.label = getLabel( version );
		version = getVersionWithoutLabel( version );

		String[] versionParts = version.split( "\\." );
		if ( versionParts.length != 3 ) throw new IllegalArgumentException( "The version needs to follow the format {major}.{minor}.{fix}" );
		try {
			this.major = Integer.parseInt( versionParts[0] );
			this.minor = Integer.parseInt( versionParts[1] );
			this.fix = Integer.parseInt( versionParts[2] );
		} catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "Couldn't parse version number. The version parts must be integers.", e );
		}

	}

	private String getVersionWithoutLabel( String version ) {
		return version.split( Consts.DASH )[0];
	}

	private String getLabel( String version ) {
		String[] versionParts = version.split( Consts.DASH );
		if ( versionParts.length != 2 ) return null;
		return versionParts[2];
	}

	@Override
	public int compareTo( RepositoryVersion version ) {
		if ( this.getMajor() > version.getMajor() ) return 1;
		else if ( this.getMajor() < version.getMajor() ) return - 1;
		else if ( this.getMinor() > version.getMinor() ) return 1;
		else if ( this.getMinor() < version.getMinor() ) return - 1;
		else if ( this.getFix() > version.getFix() ) return 1;
		else if ( this.getFix() < version.getFix() ) return - 1;
		else return 0;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;

		RepositoryVersion that = (RepositoryVersion) o;

		if ( major != that.major ) return false;
		if ( minor != that.minor ) return false;
		return fix == that.fix;

	}

	@Override
	public int hashCode() {
		int result = major;
		result = 31 * result + minor;
		result = 31 * result + fix;
		return result;
	}

	@Override
	public String toString() {
		String string = String.valueOf( this.getMajor() )
			+ Consts.PERIOD
			+ String.valueOf( this.getMinor() )
			+ Consts.PERIOD
			+ String.valueOf( this.getFix() );
		if ( this.getLabel() != null ) string += Consts.DASH + this.getLabel();
		return string;
	}

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getFix() {
		return fix;
	}

	public String getLabel() {
		return label;
	}
}
