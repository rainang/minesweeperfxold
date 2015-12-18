package minesweeper.io;

import java.util.stream.Stream;

public interface Contractible {

	/** Contract this object to bytes */
	byte[] toBytes();

	/** Restore this object from bytes */
	void fromBytes(byte[] bytes);

	static byte[] contract(int i) {
		return new byte[] { (byte)(i/128), (byte)(i%128) };
	}

	static int expand(byte[] bytes) {
		return bytes[0]*128 + bytes[1];
	}

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
