package com.xrbpowered.dailyproject.data;

public class InvalidFormatException extends Exception {
	private static final long serialVersionUID = -220600546698820020L;

	public InvalidFormatException() {
		super("Input Xml file has unsupported data structure.");
	}
}
