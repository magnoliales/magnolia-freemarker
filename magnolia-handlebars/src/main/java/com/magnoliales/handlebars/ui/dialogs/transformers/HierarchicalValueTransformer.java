package com.magnoliales.handlebars.ui.dialogs.transformers;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public class HierarchicalValueTransformer extends BasicTransformer<Object> {

    private static final Logger logger = LoggerFactory.getLogger(HierarchicalValueTransformer.class);

    private final Item item;
    private final String propertyName;

    public HierarchicalValueTransformer(Item relatedFormItem,
                                        ConfiguredFieldDefinition definition,
                                        Class<Object> type) {
        super(relatedFormItem, definition, type);

        String[] path = definition.getName().split("\\.");
        this.propertyName = path[path.length - 1];
        try {
            this.item = getItem((JcrNodeAdapter) relatedFormItem, path);
        } catch (RepositoryException e) {
            logger.error("Cannot descend to property {} from node {}", definition.getName(), relatedFormItem);
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T> Property<T> getOrCreateProperty(Class<T> type) {
        @SuppressWarnings("unchecked")
        Property<T> property = item.getItemProperty(propertyName);
        if (property == null) {
            property = new DefaultProperty<T>(type, null);
            item.addItemProperty(propertyName, property);
        }
        return property;
    }

    private Item getItem(JcrNodeAdapter item, String[] path) throws RepositoryException {
        for (int i = 0; i < path.length - 1; i++) {
            JcrNodeAdapter childItem = (JcrNodeAdapter) item.getChild(path[i]);
            if (childItem == null) {
                Node node = item.getJcrItem();
                Node childNode = null;
                if (node.hasNode(path[i])) {
                    childNode = node.getNode(path[i]);
                }
                if (childNode != null) {
                    childItem = new JcrNodeAdapter(childNode);
                } else {
                    childItem = new JcrNewNodeAdapter(item.getJcrItem(), NodeTypes.ContentNode.NAME, path[i]);
                }
            }
            item.addChild(childItem);
            childItem.setParent(item);
            item = childItem;
        }
        return item;
    }
}
