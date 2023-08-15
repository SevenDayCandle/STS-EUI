package extendedui.utilities;

import java.util.Objects;

// Copied and modified from https://github.com/EatYourBeetS/STS-AnimatorMod

public class TupleT2<V1, V2> {
    public V1 v1;
    public V2 v2;

    public TupleT2() {

    }

    public TupleT2(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public TupleT2<V1, V2> clear() {
        this.v1 = null;
        this.v2 = null;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TupleT2) {
            TupleT2 b = (TupleT2) other;
            return v1 == b.v1 && v2 == b.v2;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(v1, v2);
    }

    public TupleT2<V1, V2> set(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
        return this;
    }

    @Override
    public String toString() {
        return this.v1 + ": " + this.v2;
    }
}
