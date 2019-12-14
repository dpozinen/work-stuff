package main.java.work.stuff.parser; /* Dariy */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {
	private static final String PATH_TEMPLATE = "path to folder\\%s";
	private static final String DEFAULT_FILE = "path to file";

	/**
	 * Writes to provided fileName
	 * @param s the string to write
	 * @param fileName the name of the file that will be created at {@link #PATH_TEMPLATE}
	 */
	static void log(String s, String fileName) {
		Path path = Paths.get(String.format(PATH_TEMPLATE, fileName));
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

	/**
	 * Writes to provided fileName
	 * @param u the uri to write
	 * @param fileName the name of the file that will be created at {@link #PATH_TEMPLATE}
	 */
	static void log(Uri u, String fileName) {
		String s = String.valueOf(u);
		Path path = Paths.get(String.format(PATH_TEMPLATE, fileName));
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

	/**
	 * Writes to provided fileName
	 * @param u the uri to write
	 * @param fileName the name of the file that will be created at {@link #PATH_TEMPLATE}
	 */
	static void log(UriWrapper u, String fileName) {
		String s = String.valueOf(u);
		Path path = Paths.get(String.format(PATH_TEMPLATE, fileName));
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

	/**
	 * Writes to default file at {@link FileUtils#DEFAULT_FILE}
	 * @param s the string to write
	 */
	static void log(String s) {
		Path path = Paths.get(DEFAULT_FILE);
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

	/**
	 * Writes to provided fileName
	 * @param s the string to write
	 * @param fileName the name of the file that will be created at {@link #PATH_TEMPLATE}
	 */
	static void logOver(String s, String fileName) {
		Path path = Paths.get(String.format(PATH_TEMPLATE, fileName));
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

	/**
	 * Writes to default file at {@link FileUtils#DEFAULT_FILE}
	 * @param s the string to write
	 */
	static void logOver(String s) {
		Path path = Paths.get(DEFAULT_FILE);
		try {
			if (Files.notExists(path)) Files.createFile(path);
			Files.write(path, s.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) { System.err.printf("%n%n ERROR WRITING <%n%s%n> TO FILE <%s> %n%n", s, path); }
	}

}
