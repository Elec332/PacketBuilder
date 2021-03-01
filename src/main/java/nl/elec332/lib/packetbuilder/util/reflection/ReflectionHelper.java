package nl.elec332.lib.packetbuilder.util.reflection;

import nl.elec332.lib.packetbuilder.api.util.TypedValueReference;
import nl.elec332.lib.packetbuilder.api.util.UnsafeConsumer;
import nl.elec332.lib.packetbuilder.api.util.UnsafeSupplier;
import nl.elec332.lib.packetbuilder.api.util.ValueReference;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Elec332 on 2/28/2021
 */
public class ReflectionHelper {

    public static <T> ValueReference<T> wrapField(Field f, final Object instance) {
        return wrapField(f, instance, MethodHandles.lookup());
    }

    @SuppressWarnings("unchecked")
    public static <T> ValueReference<T> wrapField(Field f, final Object instance, MethodHandles.Lookup lookup) {
        try {
            lookup = lookup.in(f.getDeclaringClass());
            MethodHandle getter = lookup.unreflectGetter(f);
            MethodHandle setter = Modifier.isFinal(f.getModifiers()) ? null : lookup.unreflectSetter(f);
            return TypedValueReference.wrap((Class<T>) f.getType(), UnsafeSupplier.wrap(() -> (T) getter.invoke(instance)), setter == null ? null : UnsafeConsumer.wrap(v -> setter.invoke(instance, v)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Field> getFields(Class<?> clazz, Class<? extends Annotation> fieldAnnotation, Class<?> stopType) {
        List<Field> ret = new ArrayList<>();
        if (stopType == null) {
            stopType = Object.class;
        }
        getFields(clazz, fieldAnnotation, stopType, ret);
        return ret;
    }

    private static void getFields(Class<?> clazz, Class<? extends Annotation> fieldAnnotation, Class<?> stopType, List<Field> list) {
        if (!stopType.isAssignableFrom(clazz) || clazz == stopType) {
            return;
        }
        getFields(clazz.getSuperclass(), fieldAnnotation, stopType, list);
        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(fieldAnnotation)) {
                list.add(f);
            }
        }
    }

    public static Method getMethod(Class<?> root, String name, Class<?>... params) {
        Method ret;
        if (root == null || root == Object.class) {
            return null;
        }
        try {
            ret = root.getDeclaredMethod(name, params);
        } catch (Exception e) {
            ret = getMethod(root.getSuperclass(), name, params);
        }
        return ret;
    }

    public static boolean isNonStaticInternalClass(Class<?> clazz) {
        return clazz != clazz.getNestHost();
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?> culprit, Class<?>... otherParams) {
        Constructor<T> ret;
        try {
            Class<?>[] pv = add(culprit, otherParams);
            if (isNonStaticInternalClass(clazz)) {
                pv = add(clazz, pv);
            }
            ret = clazz.getConstructor(pv);
            ret.setAccessible(true);
            return ret;
        } catch (NoSuchMethodException e) {
            int offset = 0;
            if (isNonStaticInternalClass(clazz)) {
                offset++;
            }
            for (Constructor<?> ctor : clazz.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == otherParams.length + 1 + offset && ctor.getParameterTypes()[offset].isAssignableFrom(culprit)) {
                    ret = (Constructor<T>) ctor;
                    ret.setAccessible(true);
                    return ret;
                }
            }
            System.out.println(Arrays.toString(clazz.getConstructors()));
            throw new RuntimeException(e);
        }
    }

    private static Class<?>[] add(Class<?> culprit, Class<?>... otherParams) {
        Class<?>[] ret = new Class[otherParams.length + 1];
        ret[0] = culprit;
        System.arraycopy(otherParams, 0, ret, 1, otherParams.length);
        return ret;
    }

}
