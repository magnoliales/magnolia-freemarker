package com.magnoliales.handlebars.templates;

import com.magnoliales.handlebars.annotations.Page;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.rendering.template.TemplateAvailability;
import info.magnolia.rendering.template.configured.ConfiguredTemplateDefinition;

import javax.annotation.Nullable;

public class HandlebarsTemplateDefinition extends ConfiguredTemplateDefinition
        implements Comparable<HandlebarsTemplateDefinition> {

    private Class<?> templateType;
    private boolean singleton;
    private HandlebarsTemplateDefinition parent;

    public HandlebarsTemplateDefinition(Class<?> templateType,
                                        TemplateAvailability templateAvailability,
                                        SimpleTranslator translator,
                                        HandlebarsTemplateDefinition parent) {

        super(templateAvailability);
        this.templateType = templateType;
        this.parent = parent;

        Page page = templateType.getAnnotation(Page.class);

        setId(templateType.getName());
        String name = translator.translate("templates." + templateType.getName());
        setName(name);

        setTemplateScript(page.templateScript());
        setTitle(templateType.getName());
        setRenderType("handlebars");

        singleton = page.singleton();
    }

    public Class<?> getTemplateType() {
        return templateType;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public HandlebarsTemplateDefinition getParent() {
        if (parent == null) {
            throw new IllegalStateException("parent template is not defined");
        }
        return parent;
    }

    @Override
    public int compareTo(HandlebarsTemplateDefinition definition) {
        return getName().compareTo(definition.getName());
    }
}