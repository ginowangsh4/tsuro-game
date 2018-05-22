package tsuro;

public class Pair<T, U> {
    public T first;
    public U second;

    public Pair(T a, U b) {
        first = a;
        second = b;
    }

    @Override
    public boolean equals(Object T) {
        if (!(T instanceof Pair)) {
            return false;
        }
        Pair p = (Pair) T;
        return (first.equals(p.first) && second.equals(p.second)) || (first.equals(p.second) && second.equals(p.first));
    }
}