package no.acntech.common.test.testsubject;

public class DummyObjectWithNoDefaultConstructor {

    private String str;

    public DummyObjectWithNoDefaultConstructor(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }
}
