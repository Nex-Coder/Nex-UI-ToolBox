package lib;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class SystemUtil {

    /**
     * This method disables the illegal access warning which is unnecessarily hard to disable. This will take care of
     * that. APIs with illegal reflection calls like JFoenix are the usual suspects triggering the warning.
     */
    public static void disableReflectionWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe = (Unsafe) theUnsafe.get(null);

            Class<?> clazz = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = clazz.getDeclaredField("logger");

            unsafe.putObjectVolatile(clazz, unsafe.staticFieldOffset(logger), null);
            unsafe.loadFence();
        } catch (Exception ignored) {}
    }
}
