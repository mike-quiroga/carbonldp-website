package com.base22.carbon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public abstract class ConvertInputStream {

	public static String toString(InputStream inputStream) throws IOException {
		String inputStreamString = null;

		Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter("\\A");

		inputStreamString = scanner.hasNext() ? scanner.next() : "";

		scanner.close();
		inputStream.close();

		return inputStreamString;
	}

	public static String toString(InputStream inputStream, String charsetName) throws IOException {
		String inputStreamString = null;

		Scanner scanner = new Scanner(inputStream, charsetName);
		scanner.useDelimiter("\\A");

		inputStreamString = scanner.hasNext() ? scanner.next() : "";

		scanner.close();
		inputStream.close();

		return inputStreamString;
	}
}
