package no.acntech.common.test.testsubject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DummyObjectWithAdvancedTypes {

    private DummyEnum dummyEnumType;
    private DummyFinalObject dummyFinalObj;
    private DummyObjectWithNoDefaultConstructor dummyObjectNoDefaultConstructor;
    private List<String> strList;
    private Date date;
    private Calendar calendar;

    public DummyEnum getDummyEnumType() {
        return dummyEnumType;
    }

    public void setDummyEnumType(DummyEnum dummyEnumType) {
        this.dummyEnumType = dummyEnumType;
    }

    public DummyFinalObject getDummyFinalObj() {
        return dummyFinalObj;
    }

    public void setDummyFinalObj(DummyFinalObject dummyFinalObj) {
        this.dummyFinalObj = dummyFinalObj;
    }

    public DummyObjectWithNoDefaultConstructor getDummyObjectNoDefaultConstructor() {
        return dummyObjectNoDefaultConstructor;
    }

    public void setDummyObjectNoDefaultConstructor(DummyObjectWithNoDefaultConstructor dummyObjectNoDefaultConstructor) {
        this.dummyObjectNoDefaultConstructor = dummyObjectNoDefaultConstructor;
    }

    public List<String> getStrList() {
        return strList;
    }

    public void setStrList(List<String> strList) {
        this.strList = strList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
