package com.xrbpowered.dailyproject.data;

public class InvalidFormatException extends Exception {
	public InvalidFormatException() {
		super("Input file has unsupported data structure.");
	}
}
