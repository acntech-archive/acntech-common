package no.acntech.common.test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

class Getter {

    private PropertyDescriptor descriptor;
    private Method getter;

    Getter(PropertyDescriptor descriptor, Method getter) {
        this.descriptor = descriptor;
        this.getter = getter;
    }

    PropertyDescriptor getDescriptor() {
        return descriptor;
    }

    Method getGetter() {
        return getter;
    }
}
