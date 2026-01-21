package soup587.exturaddon.lua;
import org.figuramc.figura.lua.LuaNotNil;
import org.figuramc.figura.lua.LuaTypeManager;
import org.figuramc.figura.lua.docs.FiguraDocsManager;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LuaTypeFunctions {
    public static boolean[] getRequiredNotNil(Method method) {
        Parameter[] params = method.getParameters();
        boolean[] result = new boolean[params.length];
        for (int i = 0; i < params.length; i++)
            if (params[i].isAnnotationPresent(LuaNotNil.class))
                result[i] = true;
        return result;
    }
    public static class StaticFunctionWithoutArgs extends VarArgFunction{
        public final Method method;
		public final LuaTypeManager typeManager;
        public StaticFunctionWithoutArgs(LuaTypeManager typeManager, Method method){
            super();
			this.typeManager = typeManager;
            this.method = method;
        }
        @Override
        public Varargs invoke(Varargs args) {
            return invokeMethod();
        }

        public Varargs invokeMethod(Object obj, Object[] args) {
            // Invoke the wrapped method
            Object result;
            try {
                result = method.invoke(obj, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw e.getCause() instanceof LuaError l ? l : new LuaError(e.getCause());
            }
            // Convert the return value
            return result instanceof Varargs v ? v : typeManager.javaToLua(result);
        }
        public Varargs invokeMethod(Object obj) {return invokeMethod(obj, null);}
        public Varargs invokeMethod() {return invokeMethod(null, null);}


        @Override
        public String tojstring() {return "function: " + method.getName();}
    }
    public static class InstanceFunctionWithoutArgs extends StaticFunctionWithoutArgs{

        public Object caller;
        public final Class<?> clazz;

        public InstanceFunctionWithoutArgs(LuaTypeManager typeManager, Method method){
            super(typeManager, method);
            clazz = method.getDeclaringClass();
        }
        public void getCaller(Varargs args){
            try {
                caller = args.checkuserdata(1, clazz);
            } catch (LuaError e) {
                String methodName = method.getName();
                String targetType = typeManager.getTypeName(clazz);
                throw new LuaError(String.format(
                        "Use a colon (:) to call %s on a %s, instead of a dot.\nFor example, change .%s( to :%s(",
                        methodName, targetType, methodName, methodName
                ));
            }
        }
        @Override
        public Varargs invoke(Varargs args) {
            getCaller(args);
            return invokeMethod(caller);
        }
    }
    public static class FunctionWithArgs extends InstanceFunctionWithoutArgs{
        public final Class<?>[] argumentTypes;
        public final boolean isStatic, canOffset;
        public final Object[] actualArgs, defaultArgs;
        public final boolean[] requiredNotNil;

        public FunctionWithArgs(LuaTypeManager typeManager,Method method){
            super(typeManager, method);
            isStatic = Modifier.isStatic(method.getModifiers());
            argumentTypes = method.getParameterTypes();

            canOffset = argumentTypes.length > 0 && !argumentTypes[0].isAssignableFrom(clazz);
            actualArgs = new Object[argumentTypes.length];
            defaultArgs = new Object[argumentTypes.length];
            requiredNotNil = LuaTypeFunctions.getRequiredNotNil(method);
            for(int i = 0; i < argumentTypes.length; i++){
                defaultArgs[i] = switch (argumentTypes[i].getName()) {
                    case "double" -> 0D;
                    case "int" -> 0;
                    case "long" -> 0L;
                    case "float" -> 0f;
                    case "boolean" -> false;
                    default -> null;
                };
            }
        }

        @Override
        public Varargs invoke(Varargs args) {
            int offset;
            if (isStatic) {
                // dirty hack for QOL of ignoring the first argument if the method is static and the arg matches the class type
                offset = canOffset && args.isuserdata(1) && clazz.isAssignableFrom(args.checkuserdata(1).getClass()) ? 2 : 1;
            }else{
                offset = 2;
                getCaller(args);
            }


            // Fill in actualArgs from args
            for (int i = 0; i < argumentTypes.length; i++) {
                int argIndex = i + offset;
                if (args.isnil(argIndex)){
                    if(requiredNotNil[i])
                        throw new LuaError("bad argument: " + method.getName() + " " + argIndex + " does not allow nil values, expected " + FiguraDocsManager.getNameFor(argumentTypes[i]));
                }else if (argIndex <=  args.narg()) {
                    try {
                        actualArgs[i] = switch (argumentTypes[i].getName()) {
                            case "java.lang.Number", "java.lang.Double", "double" -> args.checkdouble(argIndex);
                            case "java.lang.String" -> args.checkjstring(argIndex);
                            case "java.lang.Boolean", "boolean" -> args.toboolean(argIndex);
                            case "java.lang.Float", "float" -> (float) args.checkdouble(argIndex);
                            case "java.lang.Integer", "int" -> args.checkint(argIndex);
                            case "java.lang.Long", "long" -> args.checklong(argIndex);
                            case "org.luaj.vm2.LuaTable" -> args.checktable(argIndex);
                            case "org.luaj.vm2.LuaFunction" -> args.checkfunction(argIndex);
                            case "org.luaj.vm2.LuaValue" -> args.arg(argIndex);
                            case "java.lang.Object" -> typeManager.luaToJava(args.arg(argIndex));
                            default -> argumentTypes[i].getName().startsWith("[") ? typeManager.luaVarargToJava(args, argIndex, argumentTypes[i]) : args.checkuserdata(argIndex, argumentTypes[i]);
                        };
                    } catch (LuaError err) {
                        String expectedType = FiguraDocsManager.getNameFor(argumentTypes[i]);
                        String actualType;
                        if (args.arg(argIndex).type() == LuaValue.TUSERDATA)
                            actualType = FiguraDocsManager.getNameFor(args.arg(argIndex).checkuserdata().getClass());
                        else
                            actualType = args.arg(argIndex).typename();
                        throw new LuaError("bad argument #" + argIndex + " to '" + method.getName() + "' (" + expectedType + " expected, but got " + actualType+")");
                    }
                    continue;
                }
                actualArgs[i] = defaultArgs[i];

            }

            return invokeMethod(caller, actualArgs);
        }
    }
}
