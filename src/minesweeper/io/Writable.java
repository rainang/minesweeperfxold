package minesweeper.io;

import java.io.File;

public interface Writable extends Contractible {

	default void write(File file) {
		if(file != null && !file.isDirectory())
			IO.writeBytes(toBytes(), file.getAbsolutePath());
	}
}
