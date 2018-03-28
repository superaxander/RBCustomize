package alexanders.mods.rbcustomize.lua;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.construction.KnowledgeBasedRecipe;
import de.ellpeck.rockbottom.api.construction.resource.IUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ItemUseInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResUseInfo;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;

public class RecipesLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue arg1, LuaValue env) {
        LuaTable recipes = new LuaTable();
        recipes.set("add", new FunctionWrapper(this::addRecipe));
        env.set("recipes", recipes);
        return recipes;
    }

    private Varargs addRecipe(Varargs varargs) { // type, name, output, inputs... --> recipe, ok //TODO: add support for supplying knowledge based recipe information names
        if (varargs.narg() < 4) return argerror("Expected at least 4 arguments");
        LuaValue lType = varargs.arg(1);
        LuaValue lName = varargs.arg(2);
        LuaValue lOutput = varargs.arg(3);
        Varargs lInputs = varargs.subargs(4);

        int inputSize = lInputs.narg();
        IUseInfo[] inputs = new IUseInfo[inputSize];

        for (int i = 1; i <= inputSize; i++) {
            LuaValue lInput = lInputs.arg(i);
            if (!lInput.istable()) {
                return argerror(3 + i, "Expected a table value for argument input");
            }
            LuaValue lResource = lInput.get("name");
            if (lResource.isnil()) {
                inputs[i - 1] = new ItemUseInfo(ItemsLib.parseItemInstance(3 + i, lInput));
            } else {
                LuaValue lAmount = lInput.get("amount");
                if (!lAmount.isint()) return argerror(3 + i, "Expected a number value for field 'amount' in ResUseInfo");
                int amount = lAmount.toint();
                if (!lResource.isstring()) return argerror(3 + i, "Expected a string value for field 'name' in ResUseInfo");
                String resource = lResource.tojstring();
                if (RockBottomAPI.getResourceRegistry().getResources(resource).isEmpty()) return argerror(3 + i, "No items corresponding to that resource were found!");
                inputs[i - 1] = new ResUseInfo(resource, amount);
            }
        }

        if (!lType.isstring()) return argerror(1, "Expected a string value for argument 'type'");
        if (!lName.isstring()) return argerror(2, "Expected a string value for argument 'name'");
        if (!lOutput.istable()) return argerror(3, "Expected a table value for argument 'output'");

        String type = lType.tojstring();

        IResourceName name;
        try {
            name = RockBottomAPI.createRes(lName.tojstring());
        } catch (IllegalArgumentException e) {
            return argerror(2, "Specified name is not a valid resource name");
        }
        IRecipe recipe = RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(name);

        ItemInstance output = ItemsLib.parseItemInstance(3, lOutput);

        if (recipe == null) {
            switch (type) {
                case "manual":
                    recipe = new BasicRecipe(name, output, inputs).registerManual();
                    break;
                case "manual_knowledge":
                    recipe = new KnowledgeBasedRecipe(name, output, inputs).registerManual();
                    break;
                default:
                    return argerror(1, "Unrecognized recipe type");
            }
        } else {
            return varargsOf(NIL, LuaBoolean.FALSE);
        }

        return varargsOf(valueOf(recipe.getName().toString()), TRUE); //TODO: support multiple outputs
    }
}
