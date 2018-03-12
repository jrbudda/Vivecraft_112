package com.mtbs3d.minecrift.utils;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import static java.awt.event.KeyEvent.*;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;


public class KeyboardSimulator {
	public static Robot robot;

	static{
        try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}		
	}

    public static void type(CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    public static void press(char character){
        int[] chars = getCodes(character);
        for (int i : chars) {
        	  robot.keyPress(i);
		}
    }
    
    public static void unpress(char character){
        int[] chars = getCodes(character);
        for (int i : chars) {
        	  robot.keyRelease(i);
		}
    }
    
	public static void type(char character) {
        int[] chars = getCodes(character);
        doType(chars, 0, chars.length);
	}
    
    public static int[] getCodes(char character) {
        switch (character) {
	        case 'a': return codes(VK_A); 
	        case 'b': return codes(VK_B); 
	        case 'c': return codes(VK_C); 
	        case 'd': return codes(VK_D); 
	        case 'e': return codes(VK_E); 
	        case 'f': return codes(VK_F); 
	        case 'g': return codes(VK_G); 
	        case 'h': return codes(VK_H); 
	        case 'i': return codes(VK_I); 
	        case 'j': return codes(VK_J); 
	        case 'k': return codes(VK_K); 
	        case 'l': return codes(VK_L); 
	        case 'm': return codes(VK_M); 
	        case 'n': return codes(VK_N); 
	        case 'o': return codes(VK_O); 
	        case 'p': return codes(VK_P); 
	        case 'q': return codes(VK_Q); 
	        case 'r': return codes(VK_R); 
	        case 's': return codes(VK_S); 
	        case 't': return codes(VK_T); 
	        case 'u': return codes(VK_U); 
	        case 'v': return codes(VK_V); 
	        case 'w': return codes(VK_W); 
	        case 'x': return codes(VK_X); 
	        case 'y': return codes(VK_Y); 
	        case 'z': return codes(VK_Z); 
	        case 'A': return codes(VK_SHIFT, VK_A); 
	        case 'B': return codes(VK_SHIFT, VK_B); 
	        case 'C': return codes(VK_SHIFT, VK_C); 
	        case 'D': return codes(VK_SHIFT, VK_D); 
	        case 'E': return codes(VK_SHIFT, VK_E); 
	        case 'F': return codes(VK_SHIFT, VK_F); 
	        case 'G': return codes(VK_SHIFT, VK_G); 
	        case 'H': return codes(VK_SHIFT, VK_H); 
	        case 'I': return codes(VK_SHIFT, VK_I); 
	        case 'J': return codes(VK_SHIFT, VK_J); 
	        case 'K': return codes(VK_SHIFT, VK_K); 
	        case 'L': return codes(VK_SHIFT, VK_L); 
	        case 'M': return codes(VK_SHIFT, VK_M); 
	        case 'N': return codes(VK_SHIFT, VK_N); 
	        case 'O': return codes(VK_SHIFT, VK_O); 
	        case 'P': return codes(VK_SHIFT, VK_P); 
	        case 'Q': return codes(VK_SHIFT, VK_Q); 
	        case 'R': return codes(VK_SHIFT, VK_R); 
	        case 'S': return codes(VK_SHIFT, VK_S); 
	        case 'T': return codes(VK_SHIFT, VK_T); 
	        case 'U': return codes(VK_SHIFT, VK_U); 
	        case 'V': return codes(VK_SHIFT, VK_V); 
	        case 'W': return codes(VK_SHIFT, VK_W); 
	        case 'X': return codes(VK_SHIFT, VK_X); 
	        case 'Y': return codes(VK_SHIFT, VK_Y); 
	        case 'Z': return codes(VK_SHIFT, VK_Z); 
	        case '`': return codes(VK_BACK_QUOTE); 
	        case '0': return codes(VK_0); 
	        case '1': return codes(VK_1); 
	        case '2': return codes(VK_2); 
	        case '3': return codes(VK_3); 
	        case '4': return codes(VK_4); 
	        case '5': return codes(VK_5); 
	        case '6': return codes(VK_6); 
	        case '7': return codes(VK_7); 
	        case '8': return codes(VK_8); 
	        case '9': return codes(VK_9); 
	        case '-': return codes(VK_MINUS); 
	        case '=': return codes(VK_EQUALS); 
	        case '~': return codes(VK_SHIFT,VK_BACK_QUOTE); 
	        case '!': return codes(VK_SHIFT,VK_1); 
	        case '@': return codes(VK_SHIFT,VK_2); 
	        case '#': return codes(VK_SHIFT,VK_3); 
	        case '$': return codes(VK_SHIFT,VK_4); 
	        case '%': return codes(VK_SHIFT, VK_5); 
	        case '^': return codes(VK_SHIFT,VK_6); 
	        case '&': return codes(VK_SHIFT,VK_7); 
	        case '*': return codes(VK_SHIFT,VK_8); 
	        case '(': return codes(VK_SHIFT,VK_9); 
	        case ')': return codes(VK_SHIFT,VK_0); 
	        case '_': return codes(VK_SHIFT,VK_MINUS); 
	        case '+': return codes(VK_SHIFT,VK_EQUALS); 
	        case '\t': return codes(VK_TAB); 
	        case '\n': return codes(VK_ENTER); 
	        case '[': return codes(VK_OPEN_BRACKET); 
	        case ']': return codes(VK_CLOSE_BRACKET); 
	        case '\\': return codes(VK_BACK_SLASH); 
	        case '{': return codes(VK_SHIFT, VK_OPEN_BRACKET); 
	        case '}': return codes(VK_SHIFT, VK_CLOSE_BRACKET); 
	        case '|': return codes(VK_SHIFT, VK_BACK_SLASH); 
	        case ';': return codes(VK_SEMICOLON); 
	        case ':': return codes(VK_SHIFT,VK_SEMICOLON); 
	        case '\'': return codes(VK_QUOTE); 
	        case '"': return codes(VK_SHIFT,VK_QUOTE); 
	        case ',': return codes(VK_COMMA); 
	        case '<': return codes(VK_SHIFT, VK_COMMA); 
	        case '.': return codes(VK_PERIOD); 
	        case '>': return codes(VK_SHIFT, VK_PERIOD); 
	        case '/': return codes(VK_SLASH); 
	        case '?': return codes(VK_SHIFT, VK_SLASH); 
	        case ' ': return codes(VK_SPACE); 
        	case '\b': return codes(VK_BACK_SPACE); 
        	case '\r': return codes(VK_ENTER); 
        	default: return codes();
	        //default: throw new IllegalArgumentException("Cannot type character " + character);
        }
    }
    
    public static int[] getLWJGLCodes(char character) {
        switch (character) {
	        case 'a': return codes(Keyboard.KEY_A);
	        case 'b': return codes(Keyboard.KEY_B);
	        case 'c': return codes(Keyboard.KEY_C);
	        case 'd': return codes(Keyboard.KEY_D);
	        case 'e': return codes(Keyboard.KEY_E);
	        case 'f': return codes(Keyboard.KEY_F);
	        case 'g': return codes(Keyboard.KEY_G);
	        case 'h': return codes(Keyboard.KEY_H);
	        case 'i': return codes(Keyboard.KEY_I);
	        case 'j': return codes(Keyboard.KEY_J);
	        case 'k': return codes(Keyboard.KEY_K);
	        case 'l': return codes(Keyboard.KEY_L);
	        case 'm': return codes(Keyboard.KEY_M);
	        case 'n': return codes(Keyboard.KEY_N);
	        case 'o': return codes(Keyboard.KEY_O);
	        case 'p': return codes(Keyboard.KEY_P);
	        case 'q': return codes(Keyboard.KEY_Q);
	        case 'r': return codes(Keyboard.KEY_R);
	        case 's': return codes(Keyboard.KEY_S);
	        case 't': return codes(Keyboard.KEY_T);
	        case 'u': return codes(Keyboard.KEY_U);
	        case 'v': return codes(Keyboard.KEY_V);
	        case 'w': return codes(Keyboard.KEY_W);
	        case 'x': return codes(Keyboard.KEY_X);
	        case 'y': return codes(Keyboard.KEY_Y);
	        case 'z': return codes(Keyboard.KEY_Z);
	        case 'A': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_A);
	        case 'B': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_B);
	        case 'C': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_C);
	        case 'D': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_D);
	        case 'E': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_E);
	        case 'F': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_F);
	        case 'G': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_G);
	        case 'H': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_H);
	        case 'I': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_I);
	        case 'J': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_J);
	        case 'K': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_K);
	        case 'L': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_L);
	        case 'M': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_M);
	        case 'N': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_N);
	        case 'O': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_O);
	        case 'P': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_P);
	        case 'Q': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_Q);
	        case 'R': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_R);
	        case 'S': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_S);
	        case 'T': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_T);
	        case 'U': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_U);
	        case 'V': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_V);
	        case 'W': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_W);
	        case 'X': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_X);
	        case 'Y': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_Y);
	        case 'Z': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_Z);
	        case '`': return codes(Keyboard.KEY_GRAVE);
	        case '0': return codes(Keyboard.KEY_0);
	        case '1': return codes(Keyboard.KEY_1);
	        case '2': return codes(Keyboard.KEY_2);
	        case '3': return codes(Keyboard.KEY_3);
	        case '4': return codes(Keyboard.KEY_4);
	        case '5': return codes(Keyboard.KEY_5);
	        case '6': return codes(Keyboard.KEY_6);
	        case '7': return codes(Keyboard.KEY_7);
	        case '8': return codes(Keyboard.KEY_8);
	        case '9': return codes(Keyboard.KEY_9);
	        case '-': return codes(Keyboard.KEY_MINUS);
	        case '=': return codes(Keyboard.KEY_EQUALS);
	        case '~': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_B);
	        case '!': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_1);
	        case '@': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_2);
	        case '#': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_3);
	        case '$': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_4);
	        case '%': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_5);
	        case '^': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_6);
	        case '&': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_7);
	        case '*': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_8);
	        case '(': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_9);
	        case ')': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_0);
	        case '_': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_MINUS);
	        case '+': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_EQUALS);
	        case '\t': return codes(Keyboard.KEY_TAB);
	        case '\n': return codes(Keyboard.KEY_RETURN);
	        case '[': return codes(Keyboard.KEY_LBRACKET);
	        case ']': return codes(Keyboard.KEY_RBRACKET);
	        case '\\': return codes(Keyboard.KEY_BACKSLASH);
	        case '{': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_LBRACKET);
	        case '}': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_RBRACKET);
	        case '|': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_BACKSLASH);
	        case ';': return codes(Keyboard.KEY_SEMICOLON);
	        case ':': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_SEMICOLON);
	        case '\'': return codes(Keyboard.KEY_APOSTROPHE);
	        case '"': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_APOSTROPHE);
	        case ',': return codes(Keyboard.KEY_COMMA);
	        case '<': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_COMMA);
	        case '.': return codes(Keyboard.KEY_PERIOD);
	        case '>': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_PERIOD);
	        case '/': return codes(Keyboard.KEY_SLASH);
	        case '?': return codes(Keyboard.KEY_LSHIFT, Keyboard.KEY_SLASH);
	        case ' ': return codes(Keyboard.KEY_SPACE);
        	case '\b': return codes(Keyboard.KEY_BACK);
        	case '\r': return codes(Keyboard.KEY_RETURN);
        	default: return codes();
	        //default: throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private static int[] codes(int... keyCodes) {
        return keyCodes;
    }

    private static void doType(int[] keyCodes, int offset, int length) {
    	try {
	        if (length == 0) {
	            return;
	        }
	        robot.keyPress(keyCodes[offset]);
	        doType(keyCodes, offset + 1, length - 1);
	        robot.keyRelease(keyCodes[offset]);
		} catch (Exception e) {
			System.out.println("Cannot type keycode: " + keyCodes[offset]);
		}
    }

	public static int translateFromAWT(int code) {
	    switch (code) {
			case KeyEvent.VK_ESCAPE: return Keyboard.KEY_ESCAPE;
			case KeyEvent.VK_1: return Keyboard.KEY_1;
			case KeyEvent.VK_2: return Keyboard.KEY_2;
			case KeyEvent.VK_3: return Keyboard.KEY_3;
			case KeyEvent.VK_4: return Keyboard.KEY_4;
			case KeyEvent.VK_5: return Keyboard.KEY_5;
			case KeyEvent.VK_6: return Keyboard.KEY_6;
			case KeyEvent.VK_7: return Keyboard.KEY_7;
			case KeyEvent.VK_8: return Keyboard.KEY_8;
			case KeyEvent.VK_9: return Keyboard.KEY_9;
			case KeyEvent.VK_0: return Keyboard.KEY_0;
			case KeyEvent.VK_MINUS: return Keyboard.KEY_MINUS;
			case KeyEvent.VK_EQUALS: return Keyboard.KEY_EQUALS;
			case KeyEvent.VK_BACK_SPACE: return Keyboard.KEY_BACK;
			case KeyEvent.VK_TAB: return Keyboard.KEY_TAB;
			case KeyEvent.VK_Q: return Keyboard.KEY_Q;
			case KeyEvent.VK_W: return Keyboard.KEY_W;
			case KeyEvent.VK_E: return Keyboard.KEY_E;
			case KeyEvent.VK_R: return Keyboard.KEY_R;
			case KeyEvent.VK_T: return Keyboard.KEY_T;
			case KeyEvent.VK_Y: return Keyboard.KEY_Y;
			case KeyEvent.VK_U: return Keyboard.KEY_U;
			case KeyEvent.VK_I: return Keyboard.KEY_I;
			case KeyEvent.VK_O: return Keyboard.KEY_O;
			case KeyEvent.VK_P: return Keyboard.KEY_P;
			case KeyEvent.VK_OPEN_BRACKET: return Keyboard.KEY_LBRACKET;
			case KeyEvent.VK_CLOSE_BRACKET: return Keyboard.KEY_RBRACKET;
			case KeyEvent.VK_ENTER: return Keyboard.KEY_RETURN;
			case KeyEvent.VK_CONTROL: return Keyboard.KEY_LCONTROL;
			case KeyEvent.VK_A: return Keyboard.KEY_A;
			case KeyEvent.VK_S: return Keyboard.KEY_S;
			case KeyEvent.VK_D: return Keyboard.KEY_D;
			case KeyEvent.VK_F: return Keyboard.KEY_F;
			case KeyEvent.VK_G: return Keyboard.KEY_G;
			case KeyEvent.VK_H: return Keyboard.KEY_H;
			case KeyEvent.VK_J: return Keyboard.KEY_J;
			case KeyEvent.VK_K: return Keyboard.KEY_K;
			case KeyEvent.VK_L: return Keyboard.KEY_L;
			case KeyEvent.VK_SEMICOLON: return Keyboard.KEY_SEMICOLON;
			case KeyEvent.VK_QUOTE: return Keyboard.KEY_APOSTROPHE;
			case KeyEvent.VK_DEAD_GRAVE: return Keyboard.KEY_GRAVE;
			case KeyEvent.VK_SHIFT: return Keyboard.KEY_LSHIFT;
			case KeyEvent.VK_BACK_SLASH: return Keyboard.KEY_BACKSLASH;
			case KeyEvent.VK_Z: return Keyboard.KEY_Z;
			case KeyEvent.VK_X: return Keyboard.KEY_X;
			case KeyEvent.VK_C: return Keyboard.KEY_C;
			case KeyEvent.VK_V: return Keyboard.KEY_V;
			case KeyEvent.VK_B: return Keyboard.KEY_B;
			case KeyEvent.VK_N: return Keyboard.KEY_N;
			case KeyEvent.VK_M: return Keyboard.KEY_M;
			case KeyEvent.VK_COMMA: return Keyboard.KEY_COMMA;
			case KeyEvent.VK_PERIOD: return Keyboard.KEY_PERIOD;
			case KeyEvent.VK_SLASH: return Keyboard.KEY_SLASH;
			case KeyEvent.VK_MULTIPLY: return Keyboard.KEY_MULTIPLY;
			case KeyEvent.VK_ALT: return Keyboard.KEY_LMENU;
			case KeyEvent.VK_SPACE: return Keyboard.KEY_SPACE;
			case KeyEvent.VK_CAPS_LOCK: return Keyboard.KEY_CAPITAL;
			case KeyEvent.VK_F1: return Keyboard.KEY_F1;
			case KeyEvent.VK_F2: return Keyboard.KEY_F2;
			case KeyEvent.VK_F3: return Keyboard.KEY_F3;
			case KeyEvent.VK_F4: return Keyboard.KEY_F4;
			case KeyEvent.VK_F5: return Keyboard.KEY_F5;
			case KeyEvent.VK_F6: return Keyboard.KEY_F6;
			case KeyEvent.VK_F7: return Keyboard.KEY_F7;
			case KeyEvent.VK_F8: return Keyboard.KEY_F8;
			case KeyEvent.VK_F9: return Keyboard.KEY_F9;
			case KeyEvent.VK_F10: return Keyboard.KEY_F10;
			case KeyEvent.VK_NUM_LOCK: return Keyboard.KEY_NUMLOCK;
			case KeyEvent.VK_SCROLL_LOCK: return Keyboard.KEY_SCROLL;
			case KeyEvent.VK_NUMPAD7: return Keyboard.KEY_NUMPAD7;
			case KeyEvent.VK_NUMPAD8: return Keyboard.KEY_NUMPAD8;
			case KeyEvent.VK_NUMPAD9: return Keyboard.KEY_NUMPAD9;
			case KeyEvent.VK_SUBTRACT: return Keyboard.KEY_SUBTRACT;
			case KeyEvent.VK_NUMPAD4: return Keyboard.KEY_NUMPAD4;
			case KeyEvent.VK_NUMPAD5: return Keyboard.KEY_NUMPAD5;
			case KeyEvent.VK_NUMPAD6: return Keyboard.KEY_NUMPAD6;
			case KeyEvent.VK_ADD: return Keyboard.KEY_ADD;
			case KeyEvent.VK_NUMPAD1: return Keyboard.KEY_NUMPAD1;
			case KeyEvent.VK_NUMPAD2: return Keyboard.KEY_NUMPAD2;
			case KeyEvent.VK_NUMPAD3: return Keyboard.KEY_NUMPAD3;
			case KeyEvent.VK_NUMPAD0: return Keyboard.KEY_NUMPAD0;
			case KeyEvent.VK_DECIMAL: return Keyboard.KEY_DECIMAL;
			case KeyEvent.VK_F11: return Keyboard.KEY_F11;
			case KeyEvent.VK_F12: return Keyboard.KEY_F12;
			case KeyEvent.VK_F13: return Keyboard.KEY_F13;
			case KeyEvent.VK_F14: return Keyboard.KEY_F14;
			case KeyEvent.VK_F15: return Keyboard.KEY_F15;
			case KeyEvent.VK_KANA: return Keyboard.KEY_KANA;
			case KeyEvent.VK_CONVERT: return Keyboard.KEY_CONVERT;
			case KeyEvent.VK_NONCONVERT: return Keyboard.KEY_NOCONVERT;
			case KeyEvent.VK_CIRCUMFLEX: return Keyboard.KEY_CIRCUMFLEX;
			case KeyEvent.VK_AT: return Keyboard.KEY_AT;
			case KeyEvent.VK_COLON: return Keyboard.KEY_COLON;
			case KeyEvent.VK_UNDERSCORE: return Keyboard.KEY_UNDERLINE;
			case KeyEvent.VK_KANJI: return Keyboard.KEY_KANJI;
			case KeyEvent.VK_STOP: return Keyboard.KEY_STOP;
			case KeyEvent.VK_DIVIDE: return Keyboard.KEY_DIVIDE;
			case KeyEvent.VK_PAUSE: return Keyboard.KEY_PAUSE;
			case KeyEvent.VK_HOME: return Keyboard.KEY_HOME;
			case KeyEvent.VK_UP: return Keyboard.KEY_UP;
			case KeyEvent.VK_PAGE_UP: return Keyboard.KEY_PRIOR;
			case KeyEvent.VK_LEFT: return Keyboard.KEY_LEFT;
			case KeyEvent.VK_RIGHT: return Keyboard.KEY_RIGHT;
			case KeyEvent.VK_END: return Keyboard.KEY_END;
			case KeyEvent.VK_DOWN: return Keyboard.KEY_DOWN;
			case KeyEvent.VK_PAGE_DOWN: return Keyboard.KEY_NEXT;
			case KeyEvent.VK_INSERT: return Keyboard.KEY_INSERT;
			case KeyEvent.VK_DELETE: return Keyboard.KEY_DELETE;
	    }
	    return Keyboard.KEY_NONE;
	}
	
	public static int translateToAWT(int code) {
	    switch (code) {
			case Keyboard.KEY_ESCAPE: return KeyEvent.VK_ESCAPE;
			case Keyboard.KEY_1: return KeyEvent.VK_1;
			case Keyboard.KEY_2: return KeyEvent.VK_2;
			case Keyboard.KEY_3: return KeyEvent.VK_3;
			case Keyboard.KEY_4: return KeyEvent.VK_4;
			case Keyboard.KEY_5: return KeyEvent.VK_5;
			case Keyboard.KEY_6: return KeyEvent.VK_6;
			case Keyboard.KEY_7: return KeyEvent.VK_7;
			case Keyboard.KEY_8: return KeyEvent.VK_8;
			case Keyboard.KEY_9: return KeyEvent.VK_9;
			case Keyboard.KEY_0: return KeyEvent.VK_0;
			case Keyboard.KEY_MINUS: return KeyEvent.VK_MINUS;
			case Keyboard.KEY_EQUALS: return KeyEvent.VK_EQUALS;
			case Keyboard.KEY_BACK: return KeyEvent.VK_BACK_SPACE;
			case Keyboard.KEY_TAB: return KeyEvent.VK_TAB;
			case Keyboard.KEY_Q: return KeyEvent.VK_Q;
			case Keyboard.KEY_W: return KeyEvent.VK_W;
			case Keyboard.KEY_E: return KeyEvent.VK_E;
			case Keyboard.KEY_R: return KeyEvent.VK_R;
			case Keyboard.KEY_T: return KeyEvent.VK_T;
			case Keyboard.KEY_Y: return KeyEvent.VK_Y;
			case Keyboard.KEY_U: return KeyEvent.VK_U;
			case Keyboard.KEY_I: return KeyEvent.VK_I;
			case Keyboard.KEY_O: return KeyEvent.VK_O;
			case Keyboard.KEY_P: return KeyEvent.VK_P;
			case Keyboard.KEY_LBRACKET: return KeyEvent.VK_OPEN_BRACKET;
			case Keyboard.KEY_RBRACKET: return KeyEvent.VK_CLOSE_BRACKET;
			case Keyboard.KEY_RETURN: return KeyEvent.VK_ENTER;
			//case Keyboard.KEY_LCONTROL: return KeyEvent.VK_CONTROL;
			case Keyboard.KEY_A: return KeyEvent.VK_A;
			case Keyboard.KEY_S: return KeyEvent.VK_S;
			case Keyboard.KEY_D: return KeyEvent.VK_D;
			case Keyboard.KEY_F: return KeyEvent.VK_F;
			case Keyboard.KEY_G: return KeyEvent.VK_G;
			case Keyboard.KEY_H: return KeyEvent.VK_H;
			case Keyboard.KEY_J: return KeyEvent.VK_J;
			case Keyboard.KEY_K: return KeyEvent.VK_K;
			case Keyboard.KEY_L: return KeyEvent.VK_L;
			case Keyboard.KEY_SEMICOLON: return KeyEvent.VK_SEMICOLON;
			case Keyboard.KEY_APOSTROPHE: return KeyEvent.VK_QUOTE;
			case Keyboard.KEY_GRAVE: return KeyEvent.VK_BACK_QUOTE;
			//case Keyboard.KEY_LSHIFT: return KeyEvent.VK_SHIFT;
			case Keyboard.KEY_BACKSLASH: return KeyEvent.VK_BACK_SLASH;
			case Keyboard.KEY_Z: return KeyEvent.VK_Z;
			case Keyboard.KEY_X: return KeyEvent.VK_X;
			case Keyboard.KEY_C: return KeyEvent.VK_C;
			case Keyboard.KEY_V: return KeyEvent.VK_V;
			case Keyboard.KEY_B: return KeyEvent.VK_B;
			case Keyboard.KEY_N: return KeyEvent.VK_N;
			case Keyboard.KEY_M: return KeyEvent.VK_M;
			case Keyboard.KEY_COMMA: return KeyEvent.VK_COMMA;
			case Keyboard.KEY_PERIOD: return KeyEvent.VK_PERIOD;
			case Keyboard.KEY_SLASH: return KeyEvent.VK_SLASH;
			case Keyboard.KEY_MULTIPLY: return KeyEvent.VK_MULTIPLY;
			//case Keyboard.KEY_LMENU: return KeyEvent.VK_ALT;
			case Keyboard.KEY_SPACE: return KeyEvent.VK_SPACE;
			case Keyboard.KEY_CAPITAL: return KeyEvent.VK_CAPS_LOCK;
			case Keyboard.KEY_F1: return KeyEvent.VK_F1;
			case Keyboard.KEY_F2: return KeyEvent.VK_F2;
			case Keyboard.KEY_F3: return KeyEvent.VK_F3;
			case Keyboard.KEY_F4: return KeyEvent.VK_F4;
			case Keyboard.KEY_F5: return KeyEvent.VK_F5;
			case Keyboard.KEY_F6: return KeyEvent.VK_F6;
			case Keyboard.KEY_F7: return KeyEvent.VK_F7;
			case Keyboard.KEY_F8: return KeyEvent.VK_F8;
			case Keyboard.KEY_F9: return KeyEvent.VK_F9;
			case Keyboard.KEY_F10: return KeyEvent.VK_F10;
			case Keyboard.KEY_NUMLOCK: return KeyEvent.VK_NUM_LOCK;
			case Keyboard.KEY_SCROLL: return KeyEvent.VK_SCROLL_LOCK;
			case Keyboard.KEY_NUMPAD7: return KeyEvent.VK_NUMPAD7;
			case Keyboard.KEY_NUMPAD8: return KeyEvent.VK_NUMPAD8;
			case Keyboard.KEY_NUMPAD9: return KeyEvent.VK_NUMPAD9;
			case Keyboard.KEY_SUBTRACT: return KeyEvent.VK_SUBTRACT;
			case Keyboard.KEY_NUMPAD4: return KeyEvent.VK_NUMPAD4;
			case Keyboard.KEY_NUMPAD5: return KeyEvent.VK_NUMPAD5;
			case Keyboard.KEY_NUMPAD6: return KeyEvent.VK_NUMPAD6;
			case Keyboard.KEY_ADD: return KeyEvent.VK_ADD;
			case Keyboard.KEY_NUMPAD1: return KeyEvent.VK_NUMPAD1;
			case Keyboard.KEY_NUMPAD2: return KeyEvent.VK_NUMPAD2;
			case Keyboard.KEY_NUMPAD3: return KeyEvent.VK_NUMPAD3;
			case Keyboard.KEY_NUMPAD0: return KeyEvent.VK_NUMPAD0;
			case Keyboard.KEY_DECIMAL: return KeyEvent.VK_DECIMAL;
			case Keyboard.KEY_F11: return KeyEvent.VK_F11;
			case Keyboard.KEY_F12: return KeyEvent.VK_F12;
			case Keyboard.KEY_F13: return KeyEvent.VK_F13;
			case Keyboard.KEY_F14: return KeyEvent.VK_F14;
			case Keyboard.KEY_F15: return KeyEvent.VK_F15;
			case Keyboard.KEY_KANA: return KeyEvent.VK_KANA;
			case Keyboard.KEY_CONVERT: return KeyEvent.VK_CONVERT;
			case Keyboard.KEY_NOCONVERT: return KeyEvent.VK_NONCONVERT;
			case Keyboard.KEY_CIRCUMFLEX: return KeyEvent.VK_CIRCUMFLEX;
			case Keyboard.KEY_AT: return KeyEvent.VK_AT;
			case Keyboard.KEY_COLON: return KeyEvent.VK_COLON;
			case Keyboard.KEY_UNDERLINE: return KeyEvent.VK_UNDERSCORE;
			case Keyboard.KEY_KANJI: return KeyEvent.VK_KANJI;
			case Keyboard.KEY_STOP: return KeyEvent.VK_STOP;
			case Keyboard.KEY_DIVIDE: return KeyEvent.VK_DIVIDE;
			case Keyboard.KEY_PAUSE: return KeyEvent.VK_PAUSE;
			case Keyboard.KEY_HOME: return KeyEvent.VK_HOME;
			case Keyboard.KEY_UP: return KeyEvent.VK_UP;
			case Keyboard.KEY_PRIOR: return KeyEvent.VK_PAGE_UP;
			case Keyboard.KEY_LEFT: return KeyEvent.VK_LEFT;
			case Keyboard.KEY_RIGHT: return KeyEvent.VK_RIGHT;
			case Keyboard.KEY_END: return KeyEvent.VK_END;
			case Keyboard.KEY_DOWN: return KeyEvent.VK_DOWN;
			case Keyboard.KEY_NEXT: return KeyEvent.VK_PAGE_DOWN;
			case Keyboard.KEY_INSERT: return KeyEvent.VK_INSERT;
			case Keyboard.KEY_DELETE: return KeyEvent.VK_DELETE;
	    }
	    return Keyboard.KEY_NONE;
	}

}

