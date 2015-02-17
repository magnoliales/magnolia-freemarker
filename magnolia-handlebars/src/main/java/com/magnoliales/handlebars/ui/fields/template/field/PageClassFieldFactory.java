package com.magnoliales.handlebars.ui.fields.template.field;

import com.magnoliales.handlebars.ui.fields.template.PageClass;
import com.magnoliales.handlebars.setup.registry.HandlebarsRegistry;
import com.vaadin.data.Item;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;

public class PageClassFieldFactory<D extends FieldDefinition>
        extends AbstractFieldFactory<PageClassFieldDefinition, PageClass> {

    private HandlebarsRegistry handlebarsRegistry;

    @Inject
    public PageClassFieldFactory(PageClassFieldDefinition definition,
                                 Item relatedFieldItem,
                                 HandlebarsRegistry handlebarsRegistry) {
        super(definition, relatedFieldItem);
        this.handlebarsRegistry = handlebarsRegistry;
    }

    @Override
    protected PageClassField createFieldComponent() {
        String pageId = ((JcrNodeAdapter) item).getItemId().getUuid();
        return new PageClassField(handlebarsRegistry, pageId);
    }

    @Override
    protected Class<?> getFieldType() {
        return PageClassField.class;
    }

    @Override
    protected Class<?> getDefinitionType() {
        return PageClassFieldDefinition.class;
    }
}