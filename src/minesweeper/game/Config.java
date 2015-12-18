package minesweeper.game;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import minesweeper.MinesweeperFX;
import minesweeper.io.Contractible;
import minesweeper.io.IO;
import minesweeper.io.Readable;
import minesweeper.io.Writable;

public class Config implements Writable, Readable {
	
	private static final Map<String, BooleanProperty>  MAP_BOOL   = new HashMap<>();
	private static final Map<String, Property<Number>> MAP_NUMBER = new HashMap<>();
	private static final Map<String, StringProperty>   MAP_STRING = new HashMap<>();

	private static Config INSTANCE;
	
	public Config() {
		addBool(this, "Timer", "Minutes", "Milliseconds", "Grid", "Mines", "Highlight", "Pause", "No Flagging",
				"Efficiency", "Speed");
		addDouble(this, "Resolution");
		addString(this, "Date Format", "Name Filter");
		MAP_BOOL.get("Timer").set(true);
		MAP_BOOL.get("Grid").set(true);
		MAP_BOOL.get("Highlight").set(true);
		MAP_BOOL.get("Mines").set(true);
		MAP_BOOL.get("Efficiency").set(true);
		MAP_BOOL.get("Speed").set(true);
		MAP_NUMBER.get("Resolution").setValue(30);
		INSTANCE = this;
	}
	
	private static void addBool(Config config, String... names) {
		Arrays.stream(names).forEach(name -> MAP_BOOL.put(name, new SimpleBooleanProperty(config, name)));
	}
	
	public static boolean bool(String name) {
		if(!MAP_BOOL.containsKey(name))
			System.out.println("Boolean property does not exist: '" + name + "'");
		return MAP_BOOL.get(name).get();
	}
	
	public static void boolBind(String name, BooleanProperty property) {
		if(!MAP_BOOL.containsKey(name))
			System.out.println("Boolean property does not exist: '" + name + "'");
		property.bindBidirectional(MAP_BOOL.get(name));
	}
	
	public static void boolListen(String name, ChangeListener<? super Boolean> listener) {
		if(!MAP_BOOL.containsKey(name))
			System.out.println("Boolean property does not exist: '" + name + "'");
		MAP_BOOL.get(name).addListener(listener);
	}
	
	private static void addString(Config config, String... names) {
		Arrays.stream(names).forEach(name -> MAP_STRING.put(name, new SimpleStringProperty(config, name, "")));
	}
	
	public static String string(String name) {
		if(!MAP_STRING.containsKey(name))
			System.out.println("String property does not exist: '" + name + "'");
		return MAP_STRING.get(name).get();
	}
	
	public static void stringBind(String name, StringProperty property) {
		if(!MAP_STRING.containsKey(name))
			System.out.println("String property does not exist: '" + name + "'");
		property.bindBidirectional(MAP_STRING.get(name));
	}
	
	private static void addDouble(Config config, String... names) {
		Arrays.stream(names).forEach(name -> MAP_NUMBER.put(name, new SimpleDoubleProperty(config, name)));
	}
	
	public static Number number(String name) {
		if(!MAP_NUMBER.containsKey(name))
			System.out.println("Number property does not exist: '" + name + "'");
		return MAP_NUMBER.get(name).getValue();
	}
	
	public static int getInt(String name) {
		return number(name).intValue();
	}
	
	public static void numberBind(String name, Property<Number> property) {
		if(!MAP_NUMBER.containsKey(name))
			System.out.println("Number property does not exist: '" + name + "'");
		property.bindBidirectional(MAP_NUMBER.get(name));
	}
	
	public static void numberListen(String name, ChangeListener<? super Number> listener) {
		if(!MAP_NUMBER.containsKey(name))
			System.out.println("Number property does not exist: '" + name + "'");
		MAP_NUMBER.get(name).addListener(listener);
	}
	
	public static void load() {
		if(!IO.FILE_CONFIG.exists())
			INSTANCE.read(IO.PATH_CFG);
		else
			loadSettings();
	}
	
	public static void save() {
		INSTANCE.write(IO.PATH_CFG);
	}

	@Override
	public byte[] toBytes() {
		byte[] version = MinesweeperFX.VERSION.getBytes();
		byte[] format = string("Date Format").getBytes();
		byte[] filter = string("Name Filter").getBytes();
		byte[] info = new byte[] {
				(byte)version.length, Difficulty.get() == null ? 0 : (byte)Difficulty.get().id,
				(byte)(getInt("Resolution")/6 - 4), (byte)format.length, (byte)filter.length
		};
		int[] aInt = Arrays.asList(bool("Timer"), bool("Minutes"), bool("Milliseconds"), bool("Mines"), bool("Grid"),
								   bool("Highlight"), bool("No Flagging"), bool("Efficiency"), bool("Speed")).stream()
						   .mapToInt(e -> e ? 0 : 1).toArray();
		byte[] bools = new byte[aInt.length];
		for(int i = 0; i < aInt.length; i++)
			bools[i] = (byte)aInt[i];
		return Contractible.merge(info, version, format, filter, bools);
	}

	@Override
	public void fromBytes(byte[] bytes) {
		byte[] version = new byte[bytes[0]];
		Difficulty.set(Difficulty.values()[bytes[1]]);
		MAP_NUMBER.get("Resolution").setValue((bytes[2] + 4)*6);
		byte[] format = new byte[bytes[3]];
		byte[] filter = new byte[bytes[4]];
		System.arraycopy(bytes, 5, version, 0, version.length);
		System.arraycopy(bytes, 5 + version.length, format, 0, format.length);
		System.arraycopy(bytes, 5 + version.length + format.length, filter, 0, filter.length);
		String v = Charset.defaultCharset().decode(ByteBuffer.wrap(version)).toString();
		if(!Objects.equals(v, MinesweeperFX.VERSION))
			System.out.println("Loading old version " + v);
		MAP_STRING.get("Date Format").set(Charset.defaultCharset().decode(ByteBuffer.wrap(format)).toString());
		MAP_STRING.get("Name Filter").set(Charset.defaultCharset().decode(ByteBuffer.wrap(filter)).toString());
		int i = version.length + format.length + filter.length + 5;
		MAP_BOOL.get("Timer").set(bytes[i++] == 0);
		MAP_BOOL.get("Minutes").set(bytes[i++] == 0);
		MAP_BOOL.get("Milliseconds").set(bytes[i++] == 0);
		MAP_BOOL.get("Mines").set(bytes[i++] == 0);
		MAP_BOOL.get("Grid").set(bytes[i++] == 0);
		MAP_BOOL.get("Highlight").set(bytes[i++] == 0);
		MAP_BOOL.get("No Flagging").set(bytes[i++] == 0);
		MAP_BOOL.get("Efficiency").set(bytes[i++] == 0);
		MAP_BOOL.get("Speed").set(bytes[i] == 0);
	}

	private static void loadSettings() {
		Map<String, String> hashMap = new HashMap<>();
		IO.read(IO.FILE_CONFIG.getAbsolutePath())
		  .forEach(s -> hashMap.put(s.substring(0, s.indexOf(":")), s.substring(s.indexOf(":") + 1)));

		Difficulty.set(Difficulty.valueOf(hashMap.get("difficulty")));

		hashMap.keySet().stream().filter(MAP_NUMBER::containsKey).map(MAP_NUMBER::get).forEach(property -> {
			if(property instanceof DoubleProperty)
				property.setValue(Double.parseDouble(hashMap.get(property.getName())));
		});
		hashMap.keySet().stream().filter(MAP_BOOL::containsKey).map(MAP_BOOL::get)
			   .forEach(property -> property.set(Boolean.parseBoolean(hashMap.get(property.getName()))));
		hashMap.keySet().stream().filter(MAP_STRING::containsKey).map(MAP_STRING::get)
			   .forEach(property -> property.set(hashMap.get(property.getName())));
		if(getInt("Resolution") != 24 && getInt("Resolution") != 30 && getInt("Resolution") != 36)
			MAP_NUMBER.get("Resolution").setValue(30);
	}
}
