package minesweeperfx.logic;

import com.sun.corba.se.impl.orbutil.fsm.StateEngineImpl;
import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.corba.se.spi.orbutil.fsm.StateImpl;
import minesweeperfx.MinesweeperFX;

public class FSM {
	
	public static final StateImpl START   = new StateImpl("Start");
	public static final StateImpl RESTART = new StateImpl("Restart");
	public static final StateImpl PLAYING = new StateImpl("Playing");
	public static final StateImpl PAUSE   = new StateImpl("Pause");
	public static final StateImpl WIN     = new StateImpl("Win");
	public static final StateImpl LOSE    = new StateImpl("Lose");
	
	public static final Input GAME_NEW     = new InputImpl("New Game");
	public static final Input GAME_RESTART = new InputImpl("Restart Game");
	public static final Input BEGIN_GAME   = new InputImpl("First Click");
	public static final Input PAUSE_GAME   = new InputImpl("Pause");
	public static final Input WIN_GAME     = new InputImpl("Win");
	public static final Input LOSE_GAME    = new InputImpl("Lose");
	
	public static final StateImpl IDLE        = new StateImpl("Idle");
	public static final StateImpl PRIMARY     = new StateImpl("Primary");
	public static final StateImpl SECONDARY   = new StateImpl("Secondary");
	public static final StateImpl COMBO_START = new StateImpl("Combo Start");
	public static final StateImpl COMBO_END   = new StateImpl("Combo End");
	
	public static final Input LEFT_CLICK    = new InputImpl("Left Click");
	public static final Input RIGHT_CLICK   = new InputImpl("Right Click");
	public static final Input RELEASE       = new InputImpl("Release");
	public static final Input COMBO_CLICK   = new InputImpl("Combo Start");
	public static final Input COMBO_RELEASE = new InputImpl("Combo End");
	
	public static final com.sun.corba.se.spi.orbutil.fsm.FSM GAME_STATE  = createFSM();
	public static final com.sun.corba.se.spi.orbutil.fsm.FSM INPUT_STATE = createInputState();
	
	private static com.sun.corba.se.spi.orbutil.fsm.FSM createFSM() {
		StateEngineImpl sei = new StateEngineImpl();
		Game game = MinesweeperFX.getGame();
		
		sei.add(PLAYING, WIN_GAME, (fsm, input) -> game.gameOver(true), WIN);
		sei.add(PLAYING, LOSE_GAME, (fsm, input) -> game.gameOver(false), LOSE);
		
		sei.add(START, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		sei.add(RESTART, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		sei.add(PLAYING, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		sei.add(PAUSE, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		sei.add(WIN, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		sei.add(LOSE, GAME_NEW, (fsm, input) -> game.newGame(false), START);
		
		sei.add(PLAYING, GAME_RESTART, (fsm, input) -> game.newGame(true), RESTART);
		sei.add(PAUSE, GAME_RESTART, (fsm, input) -> game.newGame(true), RESTART);
		sei.add(WIN, GAME_RESTART, (fsm, input) -> game.newGame(true), RESTART);
		sei.add(LOSE, GAME_RESTART, (fsm, input) -> game.newGame(true), RESTART);
		sei.add(RESTART, GAME_RESTART, (fsm, input) -> {}, RESTART);
		sei.add(START, GAME_RESTART, (fsm, input) -> game.newGame(true), RESTART);
		
		sei.add(START, BEGIN_GAME, (fsm, input) -> game.startGame(true), PLAYING);
		sei.add(RESTART, BEGIN_GAME, (fsm, input) -> game.startGame(false), PLAYING);
		sei.add(PAUSE, PAUSE_GAME, (fsm, input) -> game.pause(), PLAYING);
		
		sei.add(PLAYING, PAUSE_GAME, (fsm, input) -> game.pause(), PAUSE);
		sei.add(START, PAUSE_GAME, (fsm, input) -> {}, START);
		sei.add(RESTART, PAUSE_GAME, (fsm, input) -> {}, RESTART);
		sei.add(WIN, PAUSE_GAME, (fsm, input) -> {}, WIN);
		sei.add(LOSE, PAUSE_GAME, (fsm, input) -> {}, LOSE);
		
		sei.done();
		
		return sei.makeFSM(START);
	}
	
	public static com.sun.corba.se.spi.orbutil.fsm.FSM createInputState() {
		StateEngineImpl sei = new StateEngineImpl();
		Game game = MinesweeperFX.getGame();
		
		sei.add(IDLE, LEFT_CLICK, (fsm, input) -> {
			if(isInputAllowed())
				game.peek();
		}, PRIMARY);
		sei.add(IDLE, RIGHT_CLICK, (fsm, input) -> {
			if(isInputAllowed())
				game.flag();
		}, SECONDARY);
		sei.add(PRIMARY, RELEASE, (fsm, input) -> {
			if(isInputAllowed())
				game.open();
		}, IDLE);
		sei.add(SECONDARY, RELEASE, null, IDLE);
		sei.add(PRIMARY, COMBO_CLICK, (fsm, input) -> {
			if(isInputAllowed())
				game.peekCombo();
		}, COMBO_START);
		sei.add(SECONDARY, COMBO_CLICK, (fsm, input) -> {
			if(isInputAllowed())
				game.peekCombo();
		}, COMBO_START);
		sei.add(COMBO_START, COMBO_RELEASE, (fsm, input) -> {
			if(isInputAllowed())
				game.openCombo();
		}, COMBO_END);
		sei.add(COMBO_END, RELEASE, null, IDLE);
		sei.add(COMBO_END, COMBO_CLICK, (fsm, input) -> {
			if(isInputAllowed())
				game.peekCombo();
		}, COMBO_START);
		sei.done();
		return sei.makeFSM(IDLE);
	}

	private static boolean isInputAllowed() {
		return GAME_STATE.getState() == START || GAME_STATE.getState() == RESTART || GAME_STATE.getState() == PLAYING;
	}
}