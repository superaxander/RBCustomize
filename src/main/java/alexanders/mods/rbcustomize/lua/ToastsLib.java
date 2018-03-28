package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.toast.IToaster;
import de.ellpeck.rockbottom.api.toast.Toast;
import de.ellpeck.rockbottom.api.util.Util;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class ToastsLib extends TwoArgFunction {
    private final IToaster toaster;

    public ToastsLib(IToaster toaster) {
        this.toaster = toaster;
    }

    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable toasts = new LuaTable();
        toasts.set("create", new FunctionWrapper(this::createToast));
        toasts.set("display", new FunctionWrapper(this::displayToast));
        toasts.set("cancel", new FunctionWrapper(this::cancelToast));
        toasts.set("cancelAll", new FunctionWrapper(this::cancelAllToasts));
        env.set("toasts", toasts);
        return toasts;
    }

    private Varargs createToast(Varargs varargs) { // title, description, time, icon --> toast
        if (toaster == null) return error("Toaster not available");
        LuaValue lTitle = varargs.arg(1);
        LuaValue lDescription = varargs.arg(2);
        LuaValue lTime = varargs.arg(3);
        LuaValue lIcon = varargs.arg(4);

        if (!lTitle.isstring()) return argerror(1, "Expected a string value for argument 'title'");
        if (!lDescription.isstring()) return argerror(1, "Expected a string value for argument 'description'");
        if (!lTime.isint()) return argerror(1, "Expected an int value for argument 'time'");
        ChatComponent title;
        String sTitle = lTitle.tojstring();
        if (Util.isResourceName(sTitle)) title = new ChatComponentText(RockBottomAPI.getGame().getAssetManager().localize(RockBottomAPI.createRes(sTitle)));
        else title = new ChatComponentText(sTitle);

        ChatComponent description;
        String sDescription = lDescription.tojstring();
        if (Util.isResourceName(sDescription)) description = new ChatComponentText(RockBottomAPI.getGame().getAssetManager().localize(RockBottomAPI.createRes(sDescription)));
        else description = new ChatComponentText(sDescription);

        int displayTime = lTime.toint();

        if (lIcon.isstring()) {
            String sIcon = lIcon.tojstring();
            if (Util.isResourceName(sIcon)) return userdataOf(new Toast(RockBottomAPI.createRes(sIcon), title, description, displayTime));
            else return argerror(4, "Expected a resource name for argument 'icon'");
        } else if (lIcon.isnil()) {
            return userdataOf(new Toast(title, description, displayTime));
        } else {
            return argerror(4, "Expected a string or nil value for argument 'icon'");
        }
    }


    private Varargs displayToast(Varargs varargs) { // toast
        if (toaster == null) return error("Toaster not available");
        LuaValue lToast = varargs.arg(1);
        if (!lToast.isuserdata(Toast.class)) return argerror(1, "Expected a Toast value for argument 'toast'");
        toaster.displayToast((Toast) lToast.touserdata());
        return NIL;
    }

    private Varargs cancelToast(Varargs varargs) { // toast
        if (toaster == null) return error("Toaster not available");
        LuaValue lToast = varargs.arg(1);
        if (!lToast.isuserdata(Toast.class)) return argerror(1, "Expected a Toast value for argument 'toast'");
        toaster.cancelToast((Toast) lToast.touserdata());
        return NIL;
    }

    private Varargs cancelAllToasts(Varargs varargs) {
        if (toaster == null) return error("Toaster not available");
        toaster.cancelAllToasts();
        return NIL;
    }
}
