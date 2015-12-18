package minesweeper.io;

import java.io.File;

public interface Writable extends Contractible {

	default void write(String filename) {
		if(filename != null && !filename.isEmpty())
			IO.writeBytes(toBytes(), filename);
	}

	default void write(File file) {
		if(file != null && !file.isDirectory())
			IO.writeBytes(toBytes(), file.getAbsolutePath());
	}
}
