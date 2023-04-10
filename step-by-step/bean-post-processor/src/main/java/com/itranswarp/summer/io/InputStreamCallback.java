package com.itranswarp.summer.io;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface InputStreamCallback<T> {

    T doWithInputStream(InputStream stream) throws IOException;
}
