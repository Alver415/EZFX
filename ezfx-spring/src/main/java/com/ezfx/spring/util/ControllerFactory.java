package com.ezfx.spring.util;

import javafx.util.Callback;

public interface ControllerFactory<T> extends Callback<Class<T>, T> {

}
