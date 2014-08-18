package com.base22.carbon.test.console;

public class BinaryTest {
	public static void main(String[] args) {
		BinaryTest datasetTest = new BinaryTest();
		datasetTest.execute();
	}

	public void execute() {
		int entryValue = 6; // 0000 0000 0000 0110
		int binaryMask = 3; // 0000 0000 0000 0011

		// 000

		int result = entryValue & binaryMask;
		// 0000 0000 0000 0110
		// 0000 0000 0000 0011
		// 0000 0000 0000 0010

		if ( result == binaryMask ) {
			System.out.println("You have permission.");
		} else {
			System.out.println("You don't have permission.");
		}
	}
}
