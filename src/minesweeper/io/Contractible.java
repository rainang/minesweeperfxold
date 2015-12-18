package minesweeper.io;

import java.util.stream.Stream;

public interface Contractible {

	/** Contract this object to bytes */
	byte[] toBytes();

	/** Restore this object from bytes */
	void fromBytes(byte[] bytes);

	static byte[] merge(byte[]... list) {
		byte[] bytes = new byte[Stream.of(list).mapToInt(e -> e.length).sum()];
		int i = 0;
		for(byte[] aBytes : list) {
			System.arraycopy(aBytes, 0, bytes, i, aBytes.length);
			i += aBytes.length;
		}
		return bytes;
	}
}
