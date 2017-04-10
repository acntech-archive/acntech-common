# AcnTech Test Tools
AcnTech Testing Tools and Utils.

## JavaBeanTester usage

Test the getters and setters of the Java class *TestBean*:
```
JavaBeanTester.testClass(TestBean.class);
```

Test the getters and setters of a Java class *TestBean*, except those for the fields *myString*, *myInteger* and *myObject*:
```
JavaBeanTester.testClass(TestBean.class, "myString", "myInteger", "myObject");
```

Test the getters and setters of the Java classes *TestBean* and *AnotherTestBean*:
```
JavaBeanTester.testClasses(TestBean.class, AnotherTestBean.class);
```

Test the getters and setters of all Java classes in the package *TestBean* exists in:
```
JavaBeanTester.testClasses(TestBean.class.getPackage());
```

Test the getters and setters of all Java classes in the package *TestBean* exists in, filtered by the search criteria set by the *ClassCriteria*:
```
JavaBeanTester.testClasses(TestBean.class.getPackage(), ClassCriteria.createDefault().build());
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

Test exceptions:
```
ExceptionTester.testException(MyException.class);
```

Test more than one exception:
```
ExceptionTester.testExceptions(MyException.class, MyOtherException.class);
```
