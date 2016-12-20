/**
 * MIT License
 * 
 * Copyright (c) 2016 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.tao.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * reflection utilities
 * 
 * @author iMinusMinus
 * @version 0.0.1
 *
 */
public class ReflectionUtils {
	
    private static Map<String, Class<?>> primitive;
    
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

    static {
        primitive = new HashMap<String, Class<?>>();
        primitive.put("boolean", Boolean.TYPE);
        primitive.put("byte", Byte.TYPE);
        primitive.put("char", Character.TYPE);
        primitive.put("short", Short.TYPE);
        primitive.put("int", Integer.TYPE);
        primitive.put("long", Long.TYPE);
        primitive.put("float", Float.TYPE);
        primitive.put("double", Double.TYPE);

        primitive.put("void", Void.TYPE);
        //type in class file
        primitive.put("Z", Boolean.TYPE);
        primitive.put("B", Byte.TYPE);
        primitive.put("C", Character.TYPE);
        primitive.put("S", Short.TYPE);
        primitive.put("I", Integer.TYPE);
        primitive.put("J", Long.TYPE);
        primitive.put("F", Float.TYPE);
        primitive.put("D", Double.TYPE);

        primitive.put("V", Void.TYPE);
    }

    /**
     * find class by class name.
     * 
     * @see #findClass(String, ClassLoader)
     * @param clazz class name
     * @return Class
     */
    public static Class<?> findClass(String clazz) {
        return findClass(clazz, ReflectionUtils.class.getClassLoader());
    }

    /**
     * find class by class name with given class loader.
     * 
     * @param clazz class name
     * @param loader class loader
     * @return Class
     */
    public static Class<?> findClass(String clazz, ClassLoader loader) {
        if (primitive.get(clazz) != null)
            return primitive.get(clazz);
        try {
            return Class.forName(clazz, true, loader);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * find method of specified class, include super class, but exclude interface
     * 
     * @param clazz class
     * @param methodName method name
     * @return method list
     */
    public static List<Method> findMethod(Class<?> clazz, String methodName) {
        assert(clazz != null);
        List<Method> methods = new ArrayList<Method>();
        if(!Object.class.equals(clazz)) {
            Method[] m = clazz.getDeclaredMethods();
            for(Method method : m) {
                if(method.getName().equals(methodName))
                    methods.add(method);
            }
            clazz = clazz.getSuperclass();
        }
        return methods;
    }
    
    /**
     * find method of specified class and specified parameter types, include super class, but exclude interface
     * 
     * @see #findMethod(Class, String, List)
     * @param clazz class name
     * @param methodName method name
     * @param parameterTypes parameter type name
     * @return Method
     */
    public static Method findMethod(String clazz, String methodName, List<String> parameterTypes) {
        Class<?> clz = findClass(clazz);
        if(clz == null) return null;
        return findMethod(clz, methodName, parameterTypes);
    }
    
    /**
     * find method of specified class and specified parameter types, include super class, but exclude interface
     * 
     * @param clazz class
     * @param methodName method name
     * @param parameterTypes parameter type name
     * @return Method
     */
    public static Method findMethod(Class<?> clazz, String methodName, List<String> parameterTypes) {
        assert(clazz != null);
        if(!Object.class.equals(clazz)) {
            Method[] method = clazz.getDeclaredMethods();
            for(Method m : method) {
                if(m.getName().equals(methodName)) {
                    Class<?>[] c = m.getParameterTypes();
                    TypeVariable<Method>[] t = m.getTypeParameters();
                    if(c.length == 0 && (parameterTypes == null || parameterTypes.isEmpty())) return m;
                    else if(c.length != parameterTypes.size()) continue;
                    int same = 0, step = 0;
                    for(int i = 0; i < c.length; i++) {
                        if(c[i].getName().equals(parameterTypes.get(i)) || 
                                c[i].equals(primitive.get(parameterTypes.get(i)))) 
                            same++;
                        else if(step < t.length && t[step].getName().equals(parameterTypes.get(i)) && c[i].equals(Object.class)) {
                            step++;
                            same++;
                        }
                    }
                    if(same == c.length) return m;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
    
    /**
     * based on class name, produce a instance
     *
     * @see #newInstance(Class)
     * @param clazz class name
     * @return class instance
     */
    public static Object newInstance(String clazz) {
        return newInstance(findClass(clazz));
    }

    /**
     * class must have no arguments constructor!
     * <ul>we can create a object as below:
     * <li>new</li>
     * <li>class.newInstance()</li>
     * <li>constructor.newInstance()</li>
     * <li>new ObjectInputStream(is).readObject()</li>
     * <li>object.clone()</li>
     * </ul>
     * 
     * @param clazz class
     * @return instance or null, if class have not no arguments constructor 
     */
    public static <T> T newInstance(Class<T> clazz) {
        assert(!clazz.isInterface());
        try {
            if(clazz.isMemberClass() && (clazz.getModifiers() & Modifier.STATIC) == 0) {
                Class<?> ec = clazz.getEnclosingClass();
                Object outter = newInstance(ec);
                Constructor<?> c = clazz.getDeclaredConstructor(ec);;
                c.setAccessible(true);
                return (T) c.newInstance(outter);
            }
            return clazz.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * @see #invokeMethod(Object, String, List, String)
     * @param clazz class name
     * @param methodName method name
     * @param parameterTypes parameter types
     * @param parameters json parameter
     * @return the result after invoke method
     */
    public static Object invokeMethod(String clazz, String methodName, List<String> parameterTypes, String parameters) {
        return invokeMethod(newInstance(clazz), methodName, parameterTypes, parameters);
    }
    
    /**
     * @see #invokeMethod(Method, Object, Object[])
     * @param target mostly, a spring bean
     * @param methodName method name
     * @param parameterTypes parameter types
     * @param parameters json parameter
     * @return the result after invoke method
     */
    public static Object invokeMethod(Object target, String methodName, List<String> parameterTypes, String parameters) {
        Method method = findMethod(target.getClass(), methodName, parameterTypes);
        Object[] args = new Object[parameterTypes.size()];
        Class<?>[] classes = new Class<?>[parameterTypes.size()];
        for(int i = 0; i < classes.length; i++) {
            classes[i] = findClass(parameterTypes.get(i));
        }
        try {
            args = JsonUtils.parse(parameters, classes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return invokeMethod(method, target, args);
    }
    
    /**
     * invoke method on target using args
     * @param method class method
     * @param target instance or class if method is static
     * @param args arguments
     * @return the result after invoke method
     */
    public static Object invokeMethod(Method method, Object target, Object[] args) {
        assert(method != null);
        assert(target != null);
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } 
    }

}
