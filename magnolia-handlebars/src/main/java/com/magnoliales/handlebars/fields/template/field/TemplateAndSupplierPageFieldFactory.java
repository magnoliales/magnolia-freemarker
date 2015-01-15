package com.magnoliales.handlebars.fields.template.field;

import com.magnoliales.handlebars.fields.template.TemplateAndSupplierPage;
import com.magnoliales.handlebars.utils.HandlebarsRegistry;
import com.vaadin.data.Item;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

import javax.inject.Inject;

public class TemplateAndSupplierPageFieldFactory<D extends FieldDefinition>
        extends AbstractFieldFactory<TemplateAndSupplierPageFieldDefinition, TemplateAndSupplierPage> {

    private HandlebarsRegistry handlebarsRegistry;

    @Inject
    public TemplateAndSupplierPageFieldFactory(TemplateAndSupplierPageFieldDefinition definition,
                                               Item relatedFieldItem,
                                               HandlebarsRegistry handlebarsRegistry) {
        super(definition, relatedFieldItem);
        this.handlebarsRegistry = handlebarsRegistry;
    }

    @Override
    protected TemplateAndSupplierPageField createFieldComponent() {
        return new TemplateAndSupplierPageField(handlebarsRegistry);
    }

    @Override
    protected Class<?> getFieldType() {
        return TemplateAndSupplierPageField.class;
    }

    @Override
    protected Class<?> getDefinitionType() {
        return TemplateAndSupplierPageFieldDefinition.class;
    }
}