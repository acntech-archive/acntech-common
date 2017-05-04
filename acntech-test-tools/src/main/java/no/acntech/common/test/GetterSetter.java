package no.acntech.common.test;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

class GetterSetter extends Getter {

    private Method setter;

    GetterSetter(PropertyDescriptor descriptor, Method getter, Method setter) {
        super(descriptor, getter);
        this.setter = setter;
    }

    Method getSetter() {
        return setter;
    }
}
