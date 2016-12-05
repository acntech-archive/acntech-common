# AcnTech Common
AcnTech Common Java Tools and Utils.

## Usage

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
