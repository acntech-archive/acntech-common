package no.acntech.common.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ClassCriteria {

    private boolean recursiveSearch;
    private boolean excludeInterfaces;
    private boolean excludeEnums;
    private boolean excludeAnnotations;
    private boolean excludeMemberClasses;
    private Set<String> excludePathRegex;

    private ClassCriteria() {
    }

    private ClassCriteria(boolean recursiveSearch, boolean excludeInterfaces, boolean excludeEnums, boolean excludeAnnotations, boolean excludeMemberClasses) {
        this.recursiveSearch = recursiveSearch;
        this.excludeInterfaces = excludeInterfaces;
        this.excludeEnums = excludeEnums;
        this.excludeAnnotations = excludeAnnotations;
        this.excludeMemberClasses = excludeMemberClasses;
    }

    public boolean isRecursiveSearch() {
        return recursiveSearch;
    }

    private void setRecursiveSearch(boolean recursiveSearch) {
        this.recursiveSearch = recursiveSearch;
    }

    public boolean isExcludeInterfaces() {
        return excludeInterfaces;
    }

    private void setExcludeInterfaces(boolean excludeInterfaces) {
        this.excludeInterfaces = excludeInterfaces;
    }

    public boolean isExcludeEnums() {
        return excludeEnums;
    }

    private void setExcludeEnums(boolean excludeEnums) {
        this.excludeEnums = excludeEnums;
    }

    public boolean isExcludeAnnotations() {
        return excludeAnnotations;
    }

    private void setExcludeAnnotations(boolean excludeAnnotations) {
        this.excludeAnnotations = excludeAnnotations;
    }

    public boolean isExcludeMemberClasses() {
        return excludeMemberClasses;
    }

    private void setExcludeMemberClasses(boolean excludeMemberClasses) {
        this.excludeMemberClasses = excludeMemberClasses;
    }

    public Set<String> getExcludePathRegex() {
        if (excludePathRegex == null) {
            excludePathRegex = new HashSet<>();
        }
        return excludePathRegex;
    }

    private void setExcludePathRegex(Set<String> excludePathRegex) {
        this.excludePathRegex = excludePathRegex;
    }

    /**
     * Creates a class criteria builder with the following preset properties:
     * <ul>
     * <li>Non recursive</li>
     * <li>Include interfaces</li>
     * <li>Include enums</li>
     * <li>Include annotations</li>
     * <li>Include member classes</li>
     * </ul>
     *
     * @return the criteria builder.
     */
    public static Builder createDefault() {
        return new Builder(new ClassCriteria(Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
    }

    /**
     * Creates a class criteria builder with the following preset properties:
     * <ul>
     * <li>Recursive</li>
     * <li>Include interfaces</li>
     * <li>Include enums</li>
     * <li>Include annotations</li>
     * <li>Include member classes</li>
     * </ul>
     *
     * @return the criteria builder.
     */
    public static Builder createRecursive() {
        return new Builder(new ClassCriteria(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE));
    }

    public static class Builder {

        private ClassCriteria classCriteria;

        private Builder(ClassCriteria classCriteria) {
            this.classCriteria = classCriteria;
        }

        public Builder doRecursiveSearch() {
            classCriteria.setRecursiveSearch(Boolean.TRUE);
            return this;
        }

        public Builder doExcludeInterfaces() {
            classCriteria.setExcludeInterfaces(Boolean.TRUE);
            return this;
        }

        public Builder doExcludeEnums() {
            classCriteria.setExcludeEnums(Boolean.TRUE);
            return this;
        }

        public Builder doExcludeAnnotations() {
            classCriteria.setExcludeAnnotations(Boolean.TRUE);
            return this;
        }

        public Builder doExcludeMemberClasses() {
            classCriteria.setExcludeMemberClasses(Boolean.TRUE);
            return this;
        }

        public Builder doExcludePaths(String... pathRegexes) {
            if (pathRegexes == null) {
                classCriteria.setExcludePathRegex(new HashSet<String>());
            } else {
                classCriteria.setExcludePathRegex(new HashSet<>(Arrays.asList(pathRegexes)));
            }
            return this;
        }

        public Builder doNonRecursiveSearch() {
            classCriteria.setRecursiveSearch(Boolean.FALSE);
            return this;
        }

        public Builder doIncludeInterfaces() {
            classCriteria.setExcludeInterfaces(Boolean.FALSE);
            return this;
        }

        public Builder doIncludeEnums() {
            classCriteria.setExcludeEnums(Boolean.FALSE);
            return this;
        }

        public Builder doIncludeAnnotations() {
            classCriteria.setExcludeAnnotations(Boolean.FALSE);
            return this;
        }

        public Builder doIncludeMemberClasses() {
            classCriteria.setExcludeMemberClasses(Boolean.FALSE);
            return this;
        }

        public Builder doExcludeAll() {
            classCriteria.setExcludeInterfaces(Boolean.TRUE);
            classCriteria.setExcludeEnums(Boolean.TRUE);
            classCriteria.setExcludeAnnotations(Boolean.TRUE);
            classCriteria.setExcludeMemberClasses(Boolean.TRUE);
            return this;
        }

        public Builder doIncludeAll() {
            classCriteria.setExcludeInterfaces(Boolean.FALSE);
            classCriteria.setExcludeEnums(Boolean.FALSE);
            classCriteria.setExcludeAnnotations(Boolean.FALSE);
            classCriteria.setExcludeMemberClasses(Boolean.FALSE);
            return this;
        }

        public ClassCriteria build() {
            return classCriteria;
        }
    }
}
