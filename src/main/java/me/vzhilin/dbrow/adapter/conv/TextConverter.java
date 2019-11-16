package me.vzhilin.dbrow.adapter.conv;

import me.vzhilin.dbrow.adapter.Converter;

public final class TextConverter implements Converter {
    public final static TextConverter INSTANCE = new TextConverter();

    @Override
    public Object fromString(String text) {
        return text;
    }

    @Override
    public String toString(Object o) {
        return String.valueOf(o);
    }
}
