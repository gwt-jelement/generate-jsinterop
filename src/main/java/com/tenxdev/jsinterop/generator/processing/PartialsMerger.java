package com.tenxdev.jsinterop.generator.processing;

import com.tenxdev.jsinterop.generator.logging.Logger;
import com.tenxdev.jsinterop.generator.model.*;

public class PartialsMerger extends AbstractDefinitionMerger {
    private final Model model;

    public PartialsMerger(Model model, Logger logger) {
        super(logger);
        this.model = model;
    }

    public void processModel() {
        processPartials();
    }

    private void processPartials() {
        logger.info(Logger.LEVEL_INFO, () -> "Merging partial definitions");
        model.getDefinitions().stream().filter(info -> !info.getPartialDefinitions().isEmpty()).forEach(definitionInfo -> {
            Definition primaryDefinition = definitionInfo.getDefinition();
            for (PartialDefinition partialDefinition : definitionInfo.getPartialDefinitions()) {
                if (partialDefinition instanceof PartialInterfaceDefinition && primaryDefinition instanceof InterfaceDefinition) {
                    mergeInterfaces((InterfaceDefinition) primaryDefinition, (InterfaceDefinition) partialDefinition);
                } else if (partialDefinition instanceof PartialDictionaryDefinition && primaryDefinition instanceof DictionaryDefinition) {
                    mergeDictionaries((DictionaryDefinition) primaryDefinition, (DictionaryDefinition) partialDefinition);
                } else {
                    reportTypeMismatch(primaryDefinition, partialDefinition);
                }
            }
        });
    }

}
