package com.achanr.glovercolorapp.common;

/**
 * @author Andrew Chanrasmi on 10/24/16
 */

public class Quadruple<T, U, V, W> {
    private T a;
    private U b;
    private V c;
    private W d;

    Quadruple(T a, U b, V c, W d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    T getA() {
        return a;
    }

    U getB() {
        return b;
    }

    V getC() {
        return c;
    }

    W getD() {
        return d;
    }
}
