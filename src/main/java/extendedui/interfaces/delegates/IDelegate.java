package extendedui.interfaces.delegates;

import basemod.ReflectionHacks;

import java.lang.invoke.MethodHandles;

// Common ancestor for all ActionTX and FuncTX interfaces
public interface IDelegate {
    MethodHandles.Lookup IMPL_LOOKUP = ReflectionHacks.getPrivateStatic(MethodHandles.Lookup.class, "IMPL_LOOKUP");
}
