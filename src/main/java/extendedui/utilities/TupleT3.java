package extendedui.utilities;

import java.util.Objects;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class TupleT3<V1, V2, V3>
{
    public V1 v1;
    public V2 v2;
    public V3 v3;

    public TupleT3()
    {

    }

    public TupleT3(V1 v1, V2 v2, V3 v3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public TupleT3<V1, V2, V3> set(V1 v1, V2 v2, V3 v3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        return this;
    }

    public TupleT3<V1, V2, V3> clear()
    {
        this.v1 = null;
        this.v2 = null;
        this.v3 = null;
        return this;
    }

    @Override
    public String toString()
    {
        return this.v1 + ": " + this.v2 + ", " + this.v3;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof TupleT3)
        {
            TupleT3 b = (TupleT3) other;
            return v1 == b.v1 && v2 == b.v2 && v3 == b.v3;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(v1, v2, v3);
    }
}
