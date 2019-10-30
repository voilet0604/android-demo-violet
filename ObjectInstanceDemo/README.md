## app 被系统意外杀死(包括横竖屏切换) Activity/Fragment中的字段通过自定义注解+反射实现自动恢复

> 当App意外被杀死，如长时间滞留后台，横竖屏切换，这时再进入app，并不是正常启动app(不会走入口流程)。
>   这时候Activity/Fragment 中字段就需要临时保存和恢复。

```kotlin
  override fun onSaveInstanceState(outState: Bundle) {
          super.onSaveInstanceState(outState)
           //通过Bundle 保存临时数据
    }
  override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           if(savedInstanceState != null) {
            //Bundle 重新恢复数据
           }
   }
```
> 上面代码比较简单，唯一的问题就是需要自己反复的保存和读取，重复代码比较多。
> 下面通过自定义注解和反射的方式实现字段自动恢复(可以通过开发者选项，打开不保留后台选项，模拟后台杀死app，或者横竖屏切换)

> 创建一个基类 BaseActivity 或者 BaseFragment

```kotlin
    abstract class BaseActivity : Fragment(), IDisplay { 
             //通过 ObjectInstanceManager实现保存和恢复字段，包括恢复字段类型为fragment
             private var mOIM: ObjectInstanceManager = ObjectInstanceManager()
    
             override fun onCreate(savedInstanceState: Bundle?) {
                 super.onCreate(savedInstanceState)
                 //....
                 if(savedInstanceState != null) {
                    //重新恢复fragment
                    mOIM.againFragmentInstance(supportFragmentManager, this)
                    //重新恢复字段
                    mOIM.againFieldInstance(savedInstanceState, this)
                 }
             }
         
             override fun onSaveInstanceState(outState: Bundle) {
                 super.onSaveInstanceState(outState)
                 //保存字段
                 mOIM.saveField(outState, this)
             }
       }
```
> 使用
```kotlin
        class MainActivity : BaseAct() {
        
            //添加AgainFragmentInstance注解，可以重新实例化fragment
            @AgainFragmentInstance
            private var mainFragment: MainFragment? = null
        
            //未指定key， 字段名作为key
            @AgainInstance
            private val age = 10
            /**
             * 可以手动指定key
             */
            @AgainInstance(key = "price")
            private val mPrice = 2000
        
            //第一次正常初始化字段
            override fun onInitData() {
                //fragment 只要初始化一次
                mainFragment = MainFragment.newInstance("this is main_fragment")
                supportFragmentManager.beginTransaction().replace(R.id.contentPanel, mainFragment!!)
                    .commitAllowingStateLoss()
            }
        
            override fun onResume() {
                super.onResume()
                Timber.d("${mainFragment == null}")
                Timber.d("age $age")
                Timber.d("user $mUser")
                Timber.d("price $mPrice")
                Timber.d("userList $mUserLis")
                Timber.d("userMap $mUserMap")
                Timber.d("map $mMap")
            }
        
        }
```
### 实现逻辑
> 主要通过自定义注解来标识需要重新实例化的字段，再通过反射把字段存储到Bundle，和从Bundle重新获取重新赋值。

1. @AgainInstance 标识字段
```java
    @Keep
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AgainInstance {
        /**
         * 通过onSaveInstanceState保存
         * 不指定key的情况使用字段名作为key
         * @return key作为 Bundle的key
         */
        String key() default "";
    }
```

2. 找到有@AgainInstance标识的字段，判断类型根据不同类型，存放Bundle中
```java
    public class ObjectInstanceManager {
    
        /**
         * 保存 @AgainInstance注解绑定的field
         *
         * @param outState           通过Bundle
         * @param activityOrFragment 只能是activity或者fragment
         */
        public void saveField(Bundle outState, Object activityOrFragment) {
            //获取对象中的所有字段
            Field[] declaredFields = activityOrFragment.getClass().getDeclaredFields();
            if (declaredFields.length == 0) return;
            for (Field field : declaredFields) {
                //标记 AgainInstance注解的才处理
                if (field.isAnnotationPresent(AgainInstance.class)) {
                    field.setAccessible(true);
                    AgainInstance annotation = field.getAnnotation(AgainInstance.class);
                    String key = annotation.key();
                    //如果没有自定义key，那么直接使用属性名
                    if(TextUtils.isEmpty(key)) {
                        key = field.getName();
                    }
                    try {
                        //如果字段 值为null，不处理
                        if (field.get(activityOrFragment) == null) continue;
                        //以下根据字段的不同类型，保存到Bundle中
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
   
    }

```

3. 重新还原字段值
```java
    public class ObjectInstanceManager {
    
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
    }

```

4. 字段类型为 Fragment 类型 重新还原

```java
    public class ObjectInstanceManager {
    
    
        /**
         * 当app意外被杀死，重启后，重新实例化fragment
         *
         * @param fragmentManager activity 的 SupportFragmentManager， fragment 的 childFragmentManager
         * @param activityOrFragment 必须是fragment或者activity
         */
        public void againFragmentInstance(FragmentManager fragmentManager, Object activityOrFragment) {
            if (fragmentManager.getFragments().size() <= 0) return;
            //找出fragment 列表
            List<Fragment> fragments = fragmentManager.getFragments();
            //获取字段列表
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
                        //两种情况, 注解没有设置tag，那么直接判断字段类型的全类名和fragment列表中的fragment的全类名比较是否同一个。
                        //注解设置了tag，并且fragment的tag === 自定义注解的tag，那么再做上面的全类名比较
                        if (fragment.getClass().getName().equals(field.getType().getName())) {
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
    }
```