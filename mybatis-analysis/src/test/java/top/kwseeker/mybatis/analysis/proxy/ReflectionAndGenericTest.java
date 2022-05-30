package top.kwseeker.mybatis.analysis.proxy;

import org.junit.Test;

import java.lang.reflect.*;
import java.util.*;

/**
 * Mybatis中设计的反射和泛型相关操作测试
 * 关于泛型与反射参考：
 * 《Java Reflection In Action》（书好像有点太老了，才讲到Java1.5）, 其他的书又讲的太浅
 * 《Java核心技术 卷1》 C12.9
 * 为表达泛型类型声明：Java1.5在reflect包提供了一个新的接口Type
 *  Class<T>实现Type接口  (通过getXxxClass、getXxxInterfaces等方法获取)
 *  Type接口有四种子接口   (通过getGenericXxx等方法获取， 它们是包含的关系)
 *      TypeVariable: 描述泛型类型变量（如：<K,V>、<T extends Comparable<? super T>>）
 *      WildcardType: 描述通配符（如：<? super T>）
 *      ParameterizedType: 描述泛型类或接口类型（如 HashMap<K,V>、Comparable<? super T>）
 *      GenericArrayType: 描述泛型数组（如 T[]）
 *      关系：这四种接口包括包含关系、并列关系
 *          包含关系：
 *              比如 testGetWildcardType()：
 *                  ParameterizedType： "java.util.Map<? extends K, ? extends V>" 包含两个 WildcardType '0 = {WildcardTypeImpl@859} "? extends K" 1 = {WildcardTypeImpl@860} "? extends V"'
 *                  WildcardType: "? extends K"   包含    TypeVariable "K"(作为上边界)
 */
public class ReflectionAndGenericTest {

    @Test
    public void testGenericReflection() throws Exception {
        //String className = "java.util.Collections";
        String className = "java.util.HashMap";

        Class<?> clazz = Class.forName(className);
        printClass(clazz);
        for (Method m : clazz.getDeclaredMethods()) {
            printMethod(m);
        }
    }

    /**
     * 获取通配符泛型类型
     */
    @Test
    public void testGetWildcardType() throws NoSuchMethodException {
        Class<?> clazz = HashMap.class;
        Method putAllMethod =clazz.getDeclaredMethod("putAll", Map.class);

        //printMethod(putAllMethod);
        Type[] gpTypes = putAllMethod.getGenericParameterTypes();           //ParameterizedType： java.util.Map<? extends K, ? extends V>
        for (Type gpType : gpTypes) {
            if (gpType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) gpType;
                Type[] actualTypeArguments = pt.getActualTypeArguments();       // WildcardType：
                                                                                // 0 = {WildcardTypeImpl@859} "? extends K"
                                                                                // 1 = {WildcardTypeImpl@860} "? extends V"
                for (Type actualTypeArgument : actualTypeArguments) {
                    if (actualTypeArgument instanceof WildcardType) {
                        System.out.println(actualTypeArgument);//? extends K; ? extends V
                        WildcardType wt = (WildcardType) actualTypeArgument;
                        Type[] upperBounds = wt.getUpperBounds();                   //TypeVariable: K
                        for (Type upperBound : upperBounds) {
                            System.out.println("upperBound: " + upperBound);//K; V
                        }
                        Type[] lowerBounds = wt.getLowerBounds();
                        for (Type lowerBound : lowerBounds) {
                            System.out.println("lowerBounds: " + lowerBound);
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGenericReflectionApi() {
        Class<?> clazz;

        clazz = AbstractMap.class;
        Class<?> sc = clazz.getSuperclass();        //Object, Superclass 不包括接口类型
        Type gsc = clazz.getGenericSuperclass();    //Object
        Class<?>[] ic = clazz.getInterfaces();      //{Map}，getInterfaces() 不包含泛型信息
        Type[] gis = clazz.getGenericInterfaces();  //{Map<K,V>}，getGenericInterfaces 包含泛型信息

        clazz = HashMap.class;
        sc = clazz.getSuperclass();             //AbstractMap
        gsc = clazz.getGenericSuperclass();     //AbstractMap<K,V>
        ic = clazz.getInterfaces();             //{Map, Cloneable, Serializable}
        gis = clazz.getGenericInterfaces();     //{Map<K,V>, Cloneable, Serializable}, ！！！这里不带泛型的两个接口类型居然也返回了
    }

    private void printClass(Class<?> clazz) {
        System.out.print(clazz);
        //getTypeParameters() 获取类名后面尖括号中的泛型数组(TypeVariable[])
        //比如：
        // Collections 这里为 TypeVariable[0]
        // HashMap 这里为 TypeVariable[2]
        printTypes(clazz.getTypeParameters(), "<", ", ", ">", true);
        //获取带有泛型的超类
        //比如：
        // HashMap 这里返回 ParameterizedType类型 值：java.util.AbstractMap<K,V>
        Type sc = clazz.getGenericSuperclass();
        if (sc != null) {
            System.out.print(" extends ");
            printType(sc, false);
        }
        printTypes(clazz.getGenericInterfaces(), " implements ", ", ", "", false);
        System.out.println();
    }

    private void printMethod(Method method) {
        String name = method.getName();
        System.out.print(Modifier.toString(method.getModifiers()));
        System.out.print(" ");
        printTypes(method.getTypeParameters(), "<", ", ", "> ", true);

        printType(method.getGenericReturnType(), false);
        System.out.print(" ");
        System.out.print(name);
        System.out.print("(");
        printTypes(method.getGenericParameterTypes(), "", ", ", "", false);
        System.out.println(")");
    }

    private void printTypes(Type[] types, String pre, String sep, String suf, boolean isDefinition) {
        if (pre.equals(" extends ") && Arrays.equals(types, new Type[] {Object.class}))
            return;
        if (types.length > 0)
            System.out.print(pre);
        for (int i = 0; i < types.length; i++) {
            if (i > 0)
                System.out.print(sep);
            printType(types[i], isDefinition);
        }
        if (types.length > 0) {
            System.out.print(suf);
        }
    }

    private void printType(Type type, boolean isDefinition) {
        if (type instanceof Class) {
            Class<?> t = (Class<?>) type;
            System.out.print(t.getName());
        } else if (type instanceof TypeVariable) {
            TypeVariable<?> t = (TypeVariable<?>) type;
            System.out.print(t.getName());
            if (isDefinition) {
                printTypes(t.getBounds(), " extends ", " & ", "", false);
            }
        } else if (type instanceof WildcardType) {
            WildcardType t = (WildcardType) type;
            System.out.print("?");
            printTypes(t.getUpperBounds(), " extends ", " & ", "", false);
            printTypes(t.getLowerBounds(), " super ", " & ", "", false);
        } else if (type instanceof ParameterizedType) {      //比如：java.util.AbstractMap<K, V>
            ParameterizedType t = (ParameterizedType) type;
            Type owner = t.getOwnerType();  //比如：此类型为 O<T>.I<S>，则返回 O<T> 的表示形式
            if (owner != null) {
                printType(owner, false);
                System.out.println(".");
            }
            printType(t.getRawType(), false);       // getRawType() 获取不带泛型信息的原始类型，比如java.util.AbstractMap<K, V> 返回 java.util.AbstractMap
            printTypes(t.getActualTypeArguments(), "<", ", ", ">", false);   // getActualTypeArguments() 获取Type类型的泛型类型变量TypeVariable
                                                                                                    // 比如java.util.AbstractMap<K, V> 返回 {K,V}
        } else if (type instanceof GenericArrayType) {
            GenericArrayType t = (GenericArrayType) type;
            System.out.print("");
            printType(t.getGenericComponentType(), isDefinition);
            System.out.print("[]");
        }
    }
 }
