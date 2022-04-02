package stseffekseer.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class FieldInfo<T>
{
    private final Field field;

    public void Set(Object instance, T value) throws RuntimeException
    {
        try
        {
            field.set(instance, value);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public T Get(Object instance) throws RuntimeException
    {
        try
        {
            return (T)field.get(instance);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean IsStatic() {
        return Modifier.isStatic(this.field.getModifiers());
    }

    public FieldInfo(Field field)
    {
        this.field = field;
    }
}