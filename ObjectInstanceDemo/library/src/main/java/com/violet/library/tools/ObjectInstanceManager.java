package com.violet.library.tools;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.violet.library.annotation.AgainFragmentInstance;
import com.violet.library.annotation.AgainInstance;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shalei
 * @Date: 2019-10-29 15:11
 * @Desc: 保存activity/fragment 中field
 * 目前支持的类型
 * 1.基本类型
 * 2.基本类型的包装类
 * 3.基本类型数组
 * 4.基本类型包装类数组
 * 5.一级List集合
 * 6.String类型
 * 7.String类型数组
 * 8.String类型集合
 * 9.Serializable接口
 * 10.Parcelable接口对象
 * 11.Parcelable接口对象数组 不支持
 * 12.Parcelable接口对象集合
 * 13.复杂类型自己封装Parcelable接口对象
 **/
public class ObjectInstanceManager {


    /**
     * 当app意外被杀死，重启后，重新实例化fragment
     *
     * @param fragmentManager activity 的 SupportFragmentManager， fragment 的 childFragmentManager
     * @param activityOrFragment 必须是fragment或者activity
     */
    public void againFragmentInstance(FragmentManager fragmentManager, Object activityOrFragment) {
        if (fragmentManager.getFragments().size() <= 0) return;
        List<Fragment> fragments = fragmentManager.getFragments();
        Field[] declaredFields = activityOrFragment.getClass().getDeclaredFields();
        if (declaredFields.length == 0) return;
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(AgainFragmentInstance.class)) {
                AgainFragmentInstance annotation = field.getAnnotation(AgainFragmentInstance.class);
                for (Fragment fragment : fragments) {
                    //注解中不带key，并且key不等于当前的tag 跳过
                    if (!TextUtils.isEmpty(annotation.tag())
                            && !annotation.tag().equals(fragment.getTag())) {
                        continue;
                    }
                    if (fragment.getClass().getName()
                            .equals(field.getType().getName())) {
                        field.setAccessible(true);
                        try {
                            field.set(activityOrFragment, fragment);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private Object againListType(@NonNull Bundle savedInstanceState, @NonNull Field field, String key) {
        ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
        Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
        Object val = null;
        for (Type type : listActualTypeArguments) {
            if (type == Integer.class) {
                val = savedInstanceState.getIntegerArrayList(key);
            } else if (type == Short.class) {
                short[] shortArray = savedInstanceState.getShortArray(key);
                val = Shorts.asList(shortArray == null ? new short[]{} : shortArray);
            } else if (type == Long.class) {
                long[] longArray = savedInstanceState.getLongArray(key);
                val = Longs.asList(longArray == null ? new long[]{} : longArray);
            } else if (type == Float.class) {
                float[] floatArray = savedInstanceState.getFloatArray(key);
                val = Floats.asList(floatArray == null ? new float[]{} : floatArray);
            } else if (type == Double.class) {
                double[] doubleArray = savedInstanceState.getDoubleArray(key);
                val = Doubles.asList(doubleArray == null ? new double[]{} : doubleArray);
            } else if (type == String.class) {
                val = savedInstanceState.getStringArrayList(key);
            } else if (isSerializable(type.getClass())) {
                val = savedInstanceState.getSerializable(key);
            } else if (isParcelable(type.getClass())) {
                val = savedInstanceState.getParcelableArrayList(key);
            }
        }
        return val;
    }

    /**
     * 重新实例化 带有@AgainInstance注解 的field
     *
     * @param savedInstanceState
     * @param activityOrFragment
     */
    public void againFieldInstance(@NonNull Bundle savedInstanceState, @NonNull Object activityOrFragment) {
        Field[] declaredFields = activityOrFragment.getClass().getDeclaredFields();
        if (declaredFields.length == 0) return;
        for (Field field : declaredFields) {
            try {
                if (field.isAnnotationPresent(AgainInstance.class)) {
                    field.setAccessible(true);
                    AgainInstance annotation = field.getAnnotation(AgainInstance.class);
                    String key = annotation.key();
                    //如果没有指定key，就直接使用属性名称
                    if(TextUtils.isEmpty(key)) {
                        key = field.getName();
                    }
                    Object val = null;
                    if (field.getType() == short.class) {
                        val = savedInstanceState.getShort(key);
                    } else if (field.getType() == int.class) {
                        val = savedInstanceState.getInt(key);
                    } else if (field.getType() == long.class) {
                        val = savedInstanceState.getLong(key);
                    } else if (field.getType() == float.class) {
                        val = savedInstanceState.getFloat(key);
                    } else if (field.getType() == double.class) {
                        val = savedInstanceState.getDouble(key);
                    } else if (field.getType() == String.class) {
                        val = savedInstanceState.getString(key);
                    } else if (!isParcelableArray(field.getType()) && isSerializable(field.getType())) {
                        val = savedInstanceState.getSerializable(key);
                    } else if (isParcelable(field.getType())) {
                        val = savedInstanceState.getParcelable(key);
                    } else if (field.getType() == short[].class || field.getType() == Short[].class) {
                        val = savedInstanceState.getShortArray(key);
                    } else if (field.getType() == int[].class || field.getType() == Integer[].class) {
                        val = savedInstanceState.getIntArray(key);
                    } else if (field.getType() == long[].class || field.getType() == Long[].class) {
                        val = savedInstanceState.getLongArray(key);
                    } else if (field.getType() == float[].class || field.getType() == Float[].class) {
                        val = savedInstanceState.getFloatArray(key);
                    } else if (field.getType() == double[].class || field.getType() == Double[].class) {
                        val = savedInstanceState.getDoubleArray(key);
                    } else if (isParcelableArray(field.getType())) { //不支持Parcelable[]
//                        ArrayList<Parcelable> parcelableArrayList = savedInstanceState.getParcelableArrayList(key);
//                        Parcelable[] parcelables = parcelableArrayList.toArray(new Parcelable[]{});
//                        val = parcelables;
                    } else if (field.getType() == List.class || field.getType() == ArrayList.class) {
                        val = againListType(savedInstanceState, field, key);
                    } else {
                        throw new RuntimeException(activityOrFragment.getClass().getName() + ": 该类型不支持" +
                                field.getType().getName() +
                                ", 支持的类型 包括基本类型，基本类型包装类，实现了Serializable，Parcelable接口对象，数组，一级List集合");
                    }
                    field.set(activityOrFragment, val);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveListType(@NonNull Bundle outState, @NonNull Object activityOrFragment, Field field, String key) throws IllegalAccessException {
        try {
            ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
            Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
            for (Type type : listActualTypeArguments) {
                if (type == Integer.class) {
                    outState.putIntegerArrayList(key, (ArrayList<Integer>) field.get(activityOrFragment));
                } else if (type == Short.class) {
                    List<Short> st = (List<Short>) field.get(activityOrFragment);
                    if (st == null) break;
                    outState.putShortArray(key, Shorts.toArray(st));
                    break;
                } else if (type == Long.class) {
                    List<Long> lt = (List<Long>) field.get(activityOrFragment);
                    if (lt == null) break;
                    outState.putLongArray(key, Longs.toArray(lt));
                    break;
                } else if (type == Float.class) {
                    List<Float> lt = (List<Float>) field.get(activityOrFragment);
                    if (lt == null) break;
                    outState.putFloatArray(key, Floats.toArray(lt));
                    break;
                } else if (type == Double.class) {
                    List<Double> lt = (List<Double>) field.get(activityOrFragment);
                    if (lt == null) break;
                    outState.putDoubleArray(key, Doubles.toArray(lt));
                    break;
                } else if (type == String.class) {
                    ArrayList<String> lt = (ArrayList<String>) field.get(activityOrFragment);
                    if (lt == null) break;
                    outState.putStringArrayList(key, lt);
                    break;
                } else if (isSerializable(type.getClass())) {
                    ArrayList rs = (ArrayList) field.get(activityOrFragment);
                    if (rs == null) break;
                    outState.putSerializable(key, rs);
                    break;
                } else if (isParcelable(type.getClass())) {
                    ArrayList<Parcelable> pl = (ArrayList<Parcelable>) field.get(activityOrFragment);
                    if (pl == null) break;
                    outState.putParcelableArrayList(key, pl);
                    break;
                } else {
                    throw new RuntimeException(activityOrFragment.getClass().getName() +
                            "--> List/ArrayList<" + type.getClass().getName() + "> " +
                            field.getName() +
                            "该字段类型不支持, 支持的类型 包括基本类型，基本类型包装类，实现了Serializable，Parcelable接口对象，数组，一级List集合");
                }
            }
        } catch (ClassCastException e) {
            throw new RuntimeException(activityOrFragment.getClass().getName() + "--> " + field.getName() + ": List/ArrayList 集合必须指定泛型类型");
        }
    }

    /**
     * 保存 @AgainInstance注解绑定的field
     *
     * @param outState           通过Bundle
     * @param activityOrFragment 只能是activity或者fragment
     */
    public void saveField(Bundle outState, Object activityOrFragment) {
        Field[] declaredFields = activityOrFragment.getClass().getDeclaredFields();
        if (declaredFields.length == 0) return;
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(AgainInstance.class)) {
                field.setAccessible(true);
                AgainInstance annotation = field.getAnnotation(AgainInstance.class);
                String key = annotation.key();
                //如果没有自定义key，那么直接使用属性名
                if(TextUtils.isEmpty(key)) {
                    key = field.getName();
                }
                try {
                    if (field.get(activityOrFragment) == null) continue;
                    if (field.getType() == short.class) {
                        outState.putShort(key, field.getShort(activityOrFragment));
                    } else if (field.getType() == int.class) {
                        outState.putInt(key, field.getInt(activityOrFragment));
                    } else if (field.getType() == long.class) {
                        outState.putLong(key, field.getLong(activityOrFragment));
                    } else if (field.getType() == float.class) {
                        outState.putFloat(key, field.getFloat(activityOrFragment));
                    } else if (field.getType() == double.class) {
                        outState.putDouble(key, field.getDouble(activityOrFragment));
                    } else if (field.getType() == String.class) {
                        outState.putString(key, (String) field.get(activityOrFragment));
                    } else if (!isParcelableArray(field.getType()) && isSerializable(field.getType())) {
                        outState.putSerializable(key, (Serializable) field.get(activityOrFragment));
                    } else if (isParcelable(field.getType())) {
                        outState.putParcelable(key, (Parcelable) field.get(activityOrFragment));
                    } else if (field.getType() == short[].class || field.getType() == Short[].class) {
                        outState.putShortArray(key, (short[]) field.get(activityOrFragment));
                    } else if (field.getType() == int[].class || field.getType() == Integer[].class) {
                        outState.putIntArray(key, (int[]) field.get(activityOrFragment));
                    } else if (field.getType() == long[].class || field.getType() == Long[].class) {
                        outState.putLongArray(key, (long[]) field.get(activityOrFragment));
                    } else if (field.getType() == float[].class || field.getType() == Float[].class) {
                        outState.putFloatArray(key, (float[]) field.get(activityOrFragment));
                    } else if (field.getType() == double[].class || field.getType() == Double[].class) {
                        outState.putDoubleArray(key, (double[]) field.get(activityOrFragment));
                    } else if (isParcelableArray(field.getType())) { //不支持
//                        Parcelable[] parcelables = (Parcelable[]) field.get(activityOrFragment);
//                        ArrayList<Parcelable> parcelables1 = (ArrayList<Parcelable>) Arrays.asList(parcelables);
//                        outState.putParcelableArrayList(key, parcelables1);
                        throw new RuntimeException(activityOrFragment.getClass().getName() + "--> 属性字段:" + field.getName() + " 不支持Parcelable[]类型 ");
                    } else if (field.getType() == List.class || field.getType() == ArrayList.class) {
                        saveListType(outState, activityOrFragment, field, key);
                    } else {
                        throw new RuntimeException(activityOrFragment.getClass().getName() + ": 该类型不支持" +
                                field.getType().getName() +
                                ", 支持的类型 包括基本类型，基本类型包装类，实现了Serializable，Parcelable接口对象，数组，一级List集合");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isSerializable(Class<?> type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        if (genericInterfaces.length == 0) return false;
        for (Type t : genericInterfaces) {
            if (t == Serializable.class) return true;
        }
        return false;
    }

    private boolean isSerializableArray(Class<?> type) {
        if (type.isArray()) {
            //获取到数组的类型
            Class<?> componentType = type.getComponentType();
            assert componentType != null;
            return isSerializable(componentType);
        }
        return false;
    }

    private boolean isParcelable(Class<?> type) {
        Type[] genericInterfaces = type.getGenericInterfaces();
        if (genericInterfaces.length == 0) return false;
        for (Type t : genericInterfaces) {
            if (t == Parcelable.class) return true;
        }
        return false;
    }

    private boolean isParcelableArray(Class<?> type) {
        if (type.isArray()) {
            //获取到数组的类型
            Class<?> componentType = type.getComponentType();
            assert componentType != null;
            return isParcelable(componentType);
        }
        return false;
    }

    private boolean isListEmpty(List list) {
        return list == null || list.isEmpty();
    }
}
