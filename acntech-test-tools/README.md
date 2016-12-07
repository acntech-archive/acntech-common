# AcnTech Test Tools
AcnTech Testing Tools and Utils.

## JavaBeanTester usage

Test the getters and setters of the Java class *TestBean*:
```
JavaBeanTester.test(TestBean.class);
```

Test the getters and setters of a Java class *TestBean*, except those for the fields *myString*, *myInteger* and *myObject*:
```
JavaBeanTester.test(TestBean.class, "myString", "myInteger", "myObject");
```

Test the getters and setters of the Java classes *TestBean* and *AnotherTestBean*:
```
JavaBeanTester.test(TestBean.class, AnotherTestBean.class);
```

## TestReflectionUtils usage

Set private field of an object:
```
TestReflectionUtils.setPrivateField(targetObject, "myFieldName", myFieldValue);
```

Invoke private method of an object:
```
TestReflectionUtils.invokePrivateMethod(targetObject, "myMethodName", firstMethodArg, secondMethodArg);
```

## ExceptionTester usage

Test exception:
```
ExceptionTester.test(MyException.class, MyOtherException.class);
```
