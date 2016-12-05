package no.acntech.common.test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

class GetterSetter {

    private PropertyDescriptor descriptor;
    private Method getter;
    private Method setter;

    GetterSetter(PropertyDescriptor descriptor, Method getter, Method setter) {
        this.descriptor = descriptor;
        this.getter = getter;
        this.setter = setter;
    }

    PropertyDescriptor getDescriptor() {
        return descriptor;
    }

    Method getGetter() {
        return getter;
    }

    Method getSetter() {
        return setter;
    }
}
