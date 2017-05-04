# AcnTech Test Tools
AcnTech Testing Tools and Utils.

## JavaBeanTester
The *no.acntech.common.test.JavaBeanTester* can be used to test all the *getters* and *setters* of a Java object.
It will also test *getters* for fields set through class constructors.

The *no.acntech.common.test.JavaBeanTester* will try to create the Java type of the field related to *getters* and *setters*.
The Java type is either created from a set of pre-initialized primitives and JVM classes,
or it is created as a mock object by the use of Mockito,
or it is created by calling *TestReflectionUtils.createBean(Class<?> clazz).
If it is not able to create the type for a given field then the test will fail.
See further down how you can mitigate failing type creation by adding custom type factories.

Test the getters and setters of the Java class *TestBean*:
```
JavaBeanTester.testClass(TestBean.class);
```

Test the *getters* and *setters* of a Java class *TestBean*,
except those for the fields *myString*, *myInteger* and *myObject*:
```
JavaBeanTester.testClass(TestBean.class, "myString", "myInteger", "myObject"...);
```

Test the *getters* and *setters* of the Java classes *TestBean* and *AnotherTestBean*:
```
JavaBeanTester.testClasses(TestBean.class, AnotherTestBean.class...);
```

Test the *getters* and *setters* of all Java classes in the package *TestBean* exists in:
```
JavaBeanTester.testClasses(TestBean.class.getPackage());
```

Test the *getters* and *setters* of all Java classes in the package *TestBean* exists in, filtered by the search criteria set by the *ClassCriteria*:
```
JavaBeanTester.testClasses(TestBean.class.getPackage(), ClassCriteria.createDefault().build());
```

## ExceptionTester
The *no.acntech.common.test.ExceptionTester* can be used to test an exception.
Will test all available constructors, and do a *get* of the exception details.

Test exceptions:
```
ExceptionTester.testException(MyException.class);
```

Test more than one exception:
```
ExceptionTester.testExceptions(MyException.class, MyOtherException.class...);
```

## TestTypeFactory
The *no.acntech.common.test.TestTypeFactory* is used to create types (objects) with dummy values for the *JavaBeanTester*.

```
String testString = TestTypeFactory.createType(String.class);
```
```
TestBean testBean = TestTypeFactory.createType(TestBean.class);
```

The factory tries to instantiate an object using the following steps:

1. Using a set of predefined primitives and common JVM value objects.
2. Using [Mockito](http://mockito.org) to create a mock object (Mockito must be on the classpath).
3. Using [EasyMock](http://easymock.org) to create a mock object (EasyMock must be on the classpath).
4. Using *TestReflectionUtils.createBean(class)* to create an object.

If non of the steps are able to produce an object an exception is thrown.

The factory can be extended to create custom types if needed.
Custom types can be created by extending the *no.acntech.common.test.BasicType* interface.

```
import no.acntech.common.test.BasicType;

public class MyCustomType implements BasicType<MyCustomType> {

   @Override
   public boolean isType(Class<MyCustomType> clazz) {
      return MyCustomType.class.isAssignableFrom(clazz);
   }
   
   @Override
   public MyCustomType getType(Class<MyCustomType> clazz) {
      return new MyCustomType();
   }
}
```

Then this custom type can be added to the factory before use.

```
TestTypeFactory.addBasicType(new MyCustomType());

MyCustomType myCustomObject = TestTypeFactory.createType(MyCustomType.class);
```

## TestReflectionUtils
The *no.acntech.common.test.TestReflectionUtils* can be used to manipulate the private members of an object.

Set private field *myFieldName* of the object *targetObject* with value *myFieldValue*:
```
TestReflectionUtils.setPrivateField(targetObject, "myFieldName", myFieldValue);
```

Invoke private method *myMethodName* of the object *targetObject*, which has method arguments *firstMethodArg* and *secondMethodArg*:
```
Object returnValue = TestReflectionUtils.invokePrivateMethod(targetObject, "myMethodName", firstMethodArg, secondMethodArg);
```
If the method has a non-void return value the invocation will return the return value of the method, otherwise *null* is returned.
