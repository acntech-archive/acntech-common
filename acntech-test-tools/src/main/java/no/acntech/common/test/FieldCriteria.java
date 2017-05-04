package no.acntech.common.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Search criteria for fields. Used when searching for fields in classes using <b>TestReflectionUtils</b>.
 *
 * @see no.acntech.common.test.TestReflectionUtils#findClasses(Package, ClassCriteria)
 */
public class FieldCriteria {

    public static final String INTERNAL_CLASS_FIELD = "class";
    private Set<String> excludeFields;

    private FieldCriteria() {
    }

    public Set<String> getExcludeFields() {
        if (excludeFields == null) {
            excludeFields = new HashSet<>();
        }
        excludeFields.add(INTERNAL_CLASS_FIELD);
        return excludeFields;
    }

    /**
     * Creates a field criteria builder.
     *
     * @return the criteria builder.
     */
    public static Builder createDefault() {
        return new Builder(new FieldCriteria());
    }

    /**
     * Builder class for <b>FieldCriteria</b>.
     */
    public static class Builder {

        private FieldCriteria fieldCriteria;

        private Builder(FieldCriteria fieldCriteria) {
            if (fieldCriteria == null) {
                throw new IllegalArgumentException("Field criteria is null");
            }
            this.fieldCriteria = fieldCriteria;
        }

        public Builder doExcludeFields(String... fields) {
            if (fieldCriteria.excludeFields == null) {
                fieldCriteria.excludeFields = new HashSet<>();
            }
            if (fields != null) {
                fieldCriteria.excludeFields.addAll(Arrays.asList(fields));
            }
            return this;
        }

        public Builder doIncludeFields(String... fields) {
            if (fieldCriteria.excludeFields == null) {
                fieldCriteria.excludeFields = new HashSet<>();
            }
            if (fields != null) {
                fieldCriteria.excludeFields.removeAll(Arrays.asList(fields));
            }
            return this;
        }

        public FieldCriteria build() {
            return fieldCriteria;
        }
    }
}
