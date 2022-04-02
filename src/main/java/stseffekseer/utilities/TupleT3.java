package stseffekseer.utilities;

import java.util.Objects;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class TupleT3<V1, V2, V3>
{
    public V1 V1;
    public V2 V2;
    public V3 V3;

    public TupleT3()
    {

    }

    public TupleT3(V1 v1, V2 v2, V3 v3)
    {
        this.V1 = v1;
        this.V2 = v2;
        this.V3 = v3;
    }

    public TupleT3<V1, V2, V3> Set(V1 v1, V2 v2, V3 v3)
    {
        this.V1 = v1;
        this.V2 = v2;
        this.V3 = v3;
        return this;
    }

    public TupleT3<V1, V2, V3> Clear()
    {
        this.V1 = null;
        this.V2 = null;
        this.V3 = null;
        return this;
    }

    @Override
    public String toString()
    {
        return this.V1 + ": " + this.V2 + ", " + this.V3;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof TupleT3)
        {
            TupleT3 b = (TupleT3) other;
            return V1 == b.V1 && V2 == b.V2 && V3 == b.V3;
        }

        return false;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(V1, V2, V3);
    }
}
