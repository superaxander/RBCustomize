package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.IInputHandler;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.HashMap;

public class InputLib extends TwoArgFunction {
    private static HashMap<String, Integer> keyMap = new HashMap<>();

    static {
        keyMap.put("SPACE", 32);
        keyMap.put("APOSTROPHE", 39);
        keyMap.put("COMMA", 44);
        keyMap.put("MINUS", 45);
        keyMap.put("PERIOD", 46);
        keyMap.put("SLASH", 47);
        keyMap.put("0", 48);
        keyMap.put("1", 49);
        keyMap.put("2", 50);
        keyMap.put("3", 51);
        keyMap.put("4", 52);
        keyMap.put("5", 53);
        keyMap.put("6", 54);
        keyMap.put("7", 55);
        keyMap.put("8", 56);
        keyMap.put("9", 57);
        keyMap.put("SEMICOLON", 59);
        keyMap.put("EQUAL", 61);
        keyMap.put("A", 65);
        keyMap.put("B", 66);
        keyMap.put("C", 67);
        keyMap.put("D", 68);
        keyMap.put("E", 69);
        keyMap.put("F", 70);
        keyMap.put("G", 71);
        keyMap.put("H", 72);
        keyMap.put("I", 73);
        keyMap.put("J", 74);
        keyMap.put("K", 75);
        keyMap.put("L", 76);
        keyMap.put("M", 77);
        keyMap.put("N", 78);
        keyMap.put("O", 79);
        keyMap.put("P", 80);
        keyMap.put("Q", 81);
        keyMap.put("R", 82);
        keyMap.put("S", 83);
        keyMap.put("T", 84);
        keyMap.put("U", 85);
        keyMap.put("V", 86);
        keyMap.put("W", 87);
        keyMap.put("X", 88);
        keyMap.put("Y", 89);
        keyMap.put("Z", 90);
        keyMap.put("LEFT_BRACKET", 91);
        keyMap.put("BACKSLASH", 92);
        keyMap.put("RIGHT_BRACKET", 93);
        keyMap.put("GRAVE_ACCENT", 96);
        keyMap.put("WORLD_1", 161);
        keyMap.put("WORLD_2", 162);
        keyMap.put("ESCAPE", 256);
        keyMap.put("ENTER", 257);
        keyMap.put("TAB", 258);
        keyMap.put("BACKSPACE", 259);
        keyMap.put("INSERT", 260);
        keyMap.put("DELETE", 261);
        keyMap.put("RIGHT", 262);
        keyMap.put("LEFT", 263);
        keyMap.put("DOWN", 264);
        keyMap.put("UP", 265);
        keyMap.put("PAGE_UP", 266);
        keyMap.put("PAGE_DOWN", 267);
        keyMap.put("HOME", 268);
        keyMap.put("END", 269);
        keyMap.put("CAPS_LOCK", 280);
        keyMap.put("SCROLL_LOCK", 281);
        keyMap.put("NUM_LOCK", 282);
        keyMap.put("PRINT_SCREEN", 283);
        keyMap.put("PAUSE", 284);
        keyMap.put("F1", 290);
        keyMap.put("F2", 291);
        keyMap.put("F3", 292);
        keyMap.put("F4", 293);
        keyMap.put("F5", 294);
        keyMap.put("F6", 295);
        keyMap.put("F7", 296);
        keyMap.put("F8 ", 297);
        keyMap.put("F9 ", 298);
        keyMap.put("F10", 299);
        keyMap.put("F11", 300);
        keyMap.put("F12", 301);
        keyMap.put("F13", 302);
        keyMap.put("F14", 303);
        keyMap.put("F15", 304);
        keyMap.put("F16", 305);
        keyMap.put("F17", 306);
        keyMap.put("F18", 307);
        keyMap.put("F19", 308);
        keyMap.put("F20", 309);
        keyMap.put("F21", 310);
        keyMap.put("F22", 311);
        keyMap.put("F23", 312);
        keyMap.put("F24", 313);
        keyMap.put("F25", 314);
        keyMap.put("KP_0", 320);
        keyMap.put("KP_1", 321);
        keyMap.put("KP_2", 322);
        keyMap.put("KP_3", 323);
        keyMap.put("KP_4", 324);
        keyMap.put("KP_5", 325);
        keyMap.put("KP_6", 326);
        keyMap.put("KP_7", 327);
        keyMap.put("KP_8", 328);
        keyMap.put("KP_9", 329);
        keyMap.put("KP_DECIMAL", 330);
        keyMap.put("KP_DIVIDE", 331);
        keyMap.put("KP_MULTIPLY", 332);
        keyMap.put("KP_SUBTRACT", 333);
        keyMap.put("KP_ADD", 334);
        keyMap.put("KP_ENTER", 335);
        keyMap.put("KP_EQUAL", 336);
        keyMap.put("LEFT_SHIFT", 340);
        keyMap.put("LEFT_CONTROL", 341);
        keyMap.put("LEFT_ALT", 342);
        keyMap.put("LEFT_SUPER", 343);
        keyMap.put("RIGHT_SHIFT", 344);
        keyMap.put("RIGHT_CONTROL", 345);
        keyMap.put("RIGHT_ALT", 346);
        keyMap.put("RIGHT_SUPER", 347);
        keyMap.put("MENU", 348);
    }

    private final IInputHandler input;

    public InputLib(IInputHandler input) {
        this.input = input;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable input = new LuaTable();
        input.set("getMouseX", new FunctionWrapper(this::getMouseX));
        input.set("getMouseY", new FunctionWrapper(this::getMouseY));
        input.set("isKeyDown", new FunctionWrapper(this::isKeyDown));
        input.set("wasKeyPressed", new FunctionWrapper(this::wasKeyPressed));
        LuaTable keys = new LuaTable();
        keyMap.forEach((name, key) -> keys.set(name, valueOf(key)));
        input.set("keys", keys);
        env.set("input", input);
        return input;
    }

    private Varargs isKeyDown(Varargs varargs) { // key -> boolean
        LuaValue lKey = varargs.arg1();
        if(!lKey.isint())
            return argerror(1, "Expected an int value for argument 'key'");
        return valueOf(input.isKeyDown(lKey.toint()));
    }

    private Varargs wasKeyPressed(Varargs varargs) { // key -> boolean
        LuaValue lKey = varargs.arg1();
        if(!lKey.isint())
            return argerror(1, "Expected an int value for argument 'key'");
        return valueOf(input.wasKeyPressed(lKey.toint()));
    }

    private Varargs isMouseDown(Varargs varargs) { // button -> boolean
        LuaValue lButton = varargs.arg1();
        if(!lButton.isint())
            return argerror(1, "Expected an int value for argument 'button'");
        return valueOf(input.isMouseDown(lButton.toint()));
    }

    private Varargs wasMousePressed(Varargs varargs) { // key -> boolean
        LuaValue lButton = varargs.arg1();
        if(!lButton.isint())
            return argerror(1, "Expected an int value for argument 'button'");
        return valueOf(input.wasMousePressed(lButton.toint()));
    }

    private Varargs getMouseX(Varargs varargs) {
        return valueOf(input.getMouseX());
    }

    private Varargs getMouseY(Varargs varargs) {
        return valueOf(input.getMouseY());
    }
}
