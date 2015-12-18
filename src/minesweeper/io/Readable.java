package minesweeper.io;

import java.io.File;

public interface Readable extends Contractible {

	default void read(File file) {
		if(file != null && !file.isDirectory() && file.exists())
			fromBytes(IO.readBytes(file));
	}
}
