package com.carbonldp.test.repository.services;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.LocalFileRepository;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

//not sure if parameters tests should be done

public class FileRepositoryIT extends AbstractIT {

	@Autowired
	private AppRepository appRepository;

	FileRepository fileRepository;
	UUID uuid;
	String location;
	App app;

	@BeforeClass( dependsOnMethods = "setRepository" )
	protected void setUp() {
		app = appRepository.findByRootContainer( SimpleValueFactory.getInstance().createIRI( "https://local.carbonldp.com/apps/test-blog/" ) );
		fileRepository = new LocalFileRepository();
		uuid = UUID.randomUUID();
		applicationContextTemplate.runInAppContext( app, () -> {
			location = getFilesDirectory();
			File appDir = new File( location );
			appDir.mkdir();
			if ( ! location.endsWith( "/" ) ) location += "/";
			createFile( location.concat( uuid.toString() ) );

		} );

	}

	@Test
	public void existsTest() {
		applicationContextTemplate.runInAppContext( app, () -> Assert.assertTrue( fileRepository.exists( uuid ) ) );
	}

	@Test
	public void notExistsTest() {
		applicationContextTemplate.runInAppContext( app, () -> Assert.assertFalse( fileRepository.exists( UUID.randomUUID() ) ) );
	}

	@Test
	public void getTest() {
		applicationContextTemplate.runInAppContext( app, () -> {
			File file = fileRepository.get( uuid );
			Assert.assertEquals( getFileContent( file ), "Hello World" );
		} );
	}

	@Test
	public void saveTest() {
		applicationContextTemplate.runInAppContext( app, () -> {
			UUID uuid = fileRepository.save( new File( location.concat( this.uuid.toString() ) ) );
			Assert.assertEquals( getFileContent( new File( location.concat( uuid.toString() ) ) ), "Hello World" );
		} );

	}

	@Test( priority = 100 )
	public void deleteTest() {
		applicationContextTemplate.runInAppContext( app, () -> {
			fileRepository.delete( uuid );
			Assert.assertFalse( new File( location.concat( uuid.toString() ) ).exists() );
		} );

	}

	@Test( priority = 101 )
	public void deleteAppDirectoryTest() {
		String directory = Vars.getInstance().getAppsFilesDirectory();
		if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
		directory = directory.concat( app.getRepositoryID() );
		Assert.assertTrue( new File( directory ).exists() );
		fileRepository.deleteDirectory( app );
		Assert.assertFalse( new File( directory ).exists() );
	}

	private String getFileContent( File file ) {
		try {
			return new String( Files.readAllBytes( file.toPath() ) );
		} catch ( IOException e ) {
			throw new RuntimeException( "file couldn't be read, nested Exception: ", e );
		}

	}

	private String getFilesDirectory() {
		String directory;
		AppContext appContext = AppContextHolder.getContext();
		directory = Vars.getInstance().getAppsFilesDirectory();
		if ( ! directory.endsWith( Consts.SLASH ) ) directory = directory.concat( Consts.SLASH );
		directory = directory.concat( appContext.getApplication().getRepositoryID() );
		return directory;
	}

	private void createFile( String location ) {
		File file = new File( location );
		try {
			file.createNewFile();
		} catch ( IOException e ) {
			throw new SkipException( "file couldn't be created", e );
		}
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream( location );
		} catch ( FileNotFoundException e ) {
			throw new SkipException( "file couldn't be created", e );
		}

		try {
			outputStream.write( "Hello World".getBytes() );
		} catch ( IOException e ) {
			throw new SkipException( "file couldn't be filled", e );
		}

		try {
			outputStream.close();
		} catch ( IOException e ) {
			throw new SkipException( "file couldn't be closed", e );
		}
	}
}
