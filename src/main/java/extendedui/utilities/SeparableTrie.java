package extendedui.utilities;

public class SeparableTrie<T> {
    private static final int SIZE = 128;

    private T value;
    private final SeparableTrie<T>[] nodes = new SeparableTrie[SIZE];

    public T get(CharSequence sq) {
        return get(sq, 0);
    }

    public T get(CharSequence sq, int pos) {
        for (int i = 0; i < sq.length(); i++) {
            T res = getExact(sq, i);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public T getExact(CharSequence sq) {
        return getExact(sq, 0);
    }

    public T getExact(CharSequence sq, int pos) {
        if (pos >= sq.length()) {
            return value;
        }
        else {
            int ind = sq.charAt(pos);
            if (ind < SIZE && nodes[ind] != null) {
                return nodes[ind].getExact(sq, pos + 1);
            }
            return value;
        }
    }

    public T put(CharSequence sq, T item) {
        return put(sq, item, 0);
    }

    public T put(CharSequence sq, T item, int pos) {
        if (pos >= sq.length()) {
            if (value != null) {
                T ret = value;
                value = item;
                return ret;
            }
            value = item;
            return null;
        }
        else {
            int ind = sq.charAt(pos);
            if (ind >= SIZE) {
                return null;
            }
            if (nodes[ind] == null) {
                nodes[ind] = new SeparableTrie<>();
            }
            return nodes[ind].put(sq, item, pos + 1);
        }
    }

    public void putAll(T item, CharSequence... sqs) {
        for (CharSequence sq : sqs) {
            put(sq, item);
        }
    }

    public void putAllCaseInsensitive(T item, CharSequence... sqs) {
        for (CharSequence sq : sqs) {
            putCaseInsensitive(sq, item);
        }
    }

    public T putCaseInsensitive(CharSequence sq, T item) {
        return put(sq, item, 0);
    }

    public T putCaseInsensitive(CharSequence sq, T item, int pos) {
        if (pos >= sq.length()) {
            if (value != null) {
                T ret = value;
                value = item;
                return ret;
            }
            value = item;
            return null;
        }
        else {
            T res = null;
            int up = Character.toUpperCase(sq.charAt(pos));
            int lo = Character.toLowerCase(sq.charAt(pos));
            if (up < SIZE) {
                if (nodes[up] == null) {
                    nodes[up] = new SeparableTrie<>();
                }
                res = nodes[up].put(sq, item, pos + 1);
            }
            if (lo < SIZE) {
                if (nodes[lo] == null) {
                    nodes[lo] = new SeparableTrie<>();
                }
                T res2 = nodes[lo].put(sq, item, pos + 1);
                if (res == null) {
                    res = res2;
                }
            }
            return res;
        }
    }
}
