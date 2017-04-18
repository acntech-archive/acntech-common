package no.acntech.common.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Search criteria for classes. Used when searching for classes in packages using <b>TestReflectionUtils</b>.
 *
 * @see no.acntech.common.test.TestReflectionUtils#findClasses(Package, ClassCriteria)
 */
public class ClassCriteria {

    /**
     * Default max class limit.
     * Used if no other limit explicitly set.
     */
    public static final int DEFAULT_MAX_CLASS_LIMIT = 100;
    private int maxClassLimit;
    private boolean recursiveSearch;
    private boolean excludeInterfaces;
    private boolean excludeEnums;
    private boolean excludeAnnotations;
    private boolean excludeMemberClasses;
    private Set<String> excludePathRegex;

    private ClassCriteria() {
    }

    public int getMaxClassLimit() {
        return maxClassLimit;
    }

    public boolean isRecursiveSearch() {
        return recursiveSearch;
    }

    public boolean isExcludeInterfaces() {
        return excludeInterfaces;
    }

    public boolean isExcludeEnums() {
        return excludeEnums;
    }

    public boolean isExcludeAnnotations() {
        return excludeAnnotations;
    }

    public boolean isExcludeMemberClasses() {
        return excludeMemberClasses;
    }

    public Set<String> getExcludePathRegex() {
        if (excludePathRegex == null) {
            excludePathRegex = new HashSet<>();
        }
        return excludePathRegex;
    }

    /**
     * Creates a class criteria builder with the following preset properties:
     * <ul>
     * <li>Non recursive</li>
     * <li>Include interfaces</li>
     * <li>Include enums</li>
     * <li>Include annotations</li>
     * <li>Include member classes</li>
     * <li>Max class limit set to <b>ClassCriteria.DEFAULT_MAX_CLASS_LIMIT</b></li>
     * </ul>
     *
     * @return the criteria builder.
     * @see no.acntech.common.test.ClassCriteria#DEFAULT_MAX_CLASS_LIMIT
     */
    public static Builder createDefault() {
        return new Builder(new ClassCriteria())
                .doNonRecursiveSearch()
                .doIncludeAll()
                .withMaxClassLimit(DEFAULT_MAX_CLASS_LIMIT);
    }

    /**
     * Creates a class criteria builder with the following preset properties:
     * <ul>
     * <li>Recursive</li>
     * <li>Include interfaces</li>
     * <li>Include enums</li>
     * <li>Include annotations</li>
     * <li>Include member classes</li>
     * <li>Max class limit set to <b>ClassCriteria.DEFAULT_MAX_CLASS_LIMIT</b></li>
     * </ul>
     *
     * @return the criteria builder.
     * @see no.acntech.common.test.ClassCriteria#DEFAULT_MAX_CLASS_LIMIT
     */
    public static Builder createRecursive() {
        return new Builder(new ClassCriteria())
                .doRecursiveSearch()
                .doIncludeAll()
                .withMaxClassLimit(DEFAULT_MAX_CLASS_LIMIT);
    }

    /**
     * Builder class for <b>ClassCriteria</b>.
     */
    public static class Builder {

        private ClassCriteria classCriteria;

        private Builder(ClassCriteria classCriteria) {
            if (classCriteria == null) {
                throw new IllegalArgumentException("Class criteria is null");
            }
            this.classCriteria = classCriteria;
        }

        public Builder withMaxClassLimit(int maxClassLimit) {
            classCriteria.maxClassLimit = maxClassLimit;
            return this;
        }

        public Builder doRecursiveSearch() {
            classCriteria.recursiveSearch = Boolean.TRUE;
            return this;
        }

        public Builder doExcludeInterfaces() {
            classCriteria.excludeInterfaces = Boolean.TRUE;
            return this;
        }

        public Builder doExcludeEnums() {
            classCriteria.excludeEnums = Boolean.TRUE;
            return this;
        }

        public Builder doExcludeAnnotations() {
            classCriteria.excludeAnnotations = Boolean.TRUE;
            return this;
        }

        public Builder doExcludeMemberClasses() {
            classCriteria.excludeMemberClasses = Boolean.TRUE;
            return this;
        }

        public Builder doExcludePaths(String... pathRegexes) {
            if (classCriteria.excludePathRegex == null) {
                classCriteria.excludePathRegex = new HashSet<>();
            }
            if (pathRegexes != null) {
                classCriteria.excludePathRegex.addAll(Arrays.asList(pathRegexes));
            }
            return this;
        }

        public Builder doNonRecursiveSearch() {
            classCriteria.recursiveSearch = Boolean.FALSE;
            return this;
        }

        public Builder doIncludeInterfaces() {
            classCriteria.excludeInterfaces = Boolean.FALSE;
            return this;
        }

        public Builder doIncludeEnums() {
            classCriteria.excludeEnums = Boolean.FALSE;
            return this;
        }

        public Builder doIncludeAnnotations() {
            classCriteria.excludeAnnotations = Boolean.FALSE;
            return this;
        }

        public Builder doIncludeMemberClasses() {
            classCriteria.excludeMemberClasses = Boolean.FALSE;
            return this;
        }

        public Builder doExcludeAll() {
            classCriteria.excludeInterfaces = Boolean.TRUE;
            classCriteria.excludeEnums = Boolean.TRUE;
            classCriteria.excludeAnnotations = Boolean.TRUE;
            classCriteria.excludeMemberClasses = Boolean.TRUE;
            return this;
        }

        public Builder doIncludeAll() {
            classCriteria.excludeInterfaces = Boolean.FALSE;
            classCriteria.excludeEnums = Boolean.FALSE;
            classCriteria.excludeAnnotations = Boolean.FALSE;
            classCriteria.excludeMemberClasses = Boolean.FALSE;
            return this;
        }

        public ClassCriteria build() {
            return classCriteria;
        }
    }
}
