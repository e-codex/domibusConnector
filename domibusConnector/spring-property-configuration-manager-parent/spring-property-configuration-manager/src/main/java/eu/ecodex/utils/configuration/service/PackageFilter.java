package eu.ecodex.utils.configuration.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PackageFilter implements Predicate<Map.Entry<String, Object>> {

    private final List<String> basePackageFilter;

    public PackageFilter(String... basePackageFilter) {
        this.basePackageFilter = Arrays.asList(basePackageFilter);
    }

    public PackageFilter(List<String> basePackageFilter) {
        this.basePackageFilter = basePackageFilter;
    }

    @Override
    public boolean test(Map.Entry<String, Object> entry) {
        if (this.basePackageFilter == null || this.basePackageFilter.isEmpty()) {
            return true;
        }
        return basePackageFilter
                .stream()
                .anyMatch(filter -> entry.getValue().getClass().getPackage().getName().startsWith(filter));
    }
}



