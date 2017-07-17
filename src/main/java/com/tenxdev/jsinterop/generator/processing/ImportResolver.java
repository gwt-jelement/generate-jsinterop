package com.tenxdev.jsinterop.generator.processing;

import com.tenxdev.jsinterop.generator.errors.ErrorReporter;
import com.tenxdev.jsinterop.generator.model.DefinitionInfo;
import com.tenxdev.jsinterop.generator.model.Model;
import com.tenxdev.jsinterop.generator.processing.packageusage.PackageUsageModelVisitor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ImportResolver {

    public void processModel(Model model) {
        Map<DefinitionInfo, List<String>> packagesMap = new PackageUsageModelVisitor().accept(model);
        packagesMap.entrySet().stream()
                .filter(entry->!entry.getValue().isEmpty())
                .forEach(entry->processPackagesForDefinition(entry.getKey(), entry.getValue()));
    }

    private void processPackagesForDefinition(DefinitionInfo definitionInfo, List<String> packages) {
        definitionInfo.setImportedPackages(packages.stream()
                .filter(packageName->needsImport(definitionInfo, packageName))
                .collect(Collectors.toList()));
    }


    private boolean needsImport(DefinitionInfo definitionInfo, String packageName) {
        return packageName!=null && !definitionInfo.getPackgeName().equals(packageName);
    }
}
