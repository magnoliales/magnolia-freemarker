package com.magnoliales.handlebars.mapper;

import com.google.inject.Injector;
import com.magnoliales.handlebars.annotations.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

public class NodeObjectMapperImpl implements NodeObjectMapper {

    public static final Logger logger = LoggerFactory.getLogger(NodeObjectMapperImpl.class);

    private final Injector injector;

    @Inject
    public NodeObjectMapperImpl(Injector injector) {
        this.injector = injector;
    }

    @Override
    public Object map(Node objectNode) {
        try {
            String objectClassName = objectNode.getProperty(CLASS_PROPERTY).getString();
            Class<?> objectClass = Class.forName(objectClassName);
            Object object = injector.getInstance(objectClass);
            injector.injectMembers(object);
            return map(objectClass, object, objectNode);
        } catch (ClassNotFoundException | RepositoryException | InstantiationException | IllegalAccessException e) {
            logger.error("Cannot map node {}", objectNode);
            return null;
        }
    }

    private Object map(Class<?> objectClass, Object object, Node objectNode)
            throws IllegalAccessException, InstantiationException, RepositoryException, ClassNotFoundException {

        while (!objectClass.equals(Object.class)) {
            for (java.lang.reflect.Field field : objectClass.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if (objectNode.hasProperty(fieldName)) {
                    Property property = objectNode.getProperty(fieldName);
                    Class<? extends Field.Reader> readerClass = field.getAnnotation(Field.class).reader();
                    if (readerClass == Field.Reader.class) {
                        switch (property.getType()) {
                            case PropertyType.STRING:
                            case PropertyType.URI:
                            case PropertyType.NAME:
                            case PropertyType.BINARY:
                            case PropertyType.UNDEFINED:
                            case PropertyType.PATH:
                                field.set(object, property.getString());
                                break;
                            case PropertyType.BOOLEAN:
                                field.set(object, property.getBoolean());
                                break;
                            case PropertyType.DOUBLE:
                                field.set(object, property.getDouble());
                                break;
                            case PropertyType.LONG:
                                field.set(object, property.getLong());
                                break;
                            case PropertyType.DATE:
                                field.set(object, property.getDate());
                                break;
                            case PropertyType.DECIMAL:
                            case PropertyType.REFERENCE:
                            case PropertyType.WEAKREFERENCE:
                            default:
                                throw new AssertionError("Not implemented");
                        }
                    } else {
                        Field.Reader reader = injector.getInstance(readerClass);
                        injector.injectMembers(reader);
                        try {
                            field.set(object, reader.read(property));
                        } catch (RepositoryException | IllegalFormatException e) {
                            logger.error("Cannot set field {}, {}", fieldName, e);
                        }
                    }
                } else if (objectNode.hasNode(fieldName)) {
                    Class<?> propertyClass = field.getType();
                    Object property = propertyClass.newInstance();
                    Node propertyNode = objectNode.getNode(fieldName);
                    field.set(object, map(propertyClass, property, propertyNode));
                } else if (field.getType().isArray()) {
                    Class<?> itemSuperclass = field.getType().getComponentType();
                    List<Object> items = new ArrayList<>();
                    NodeIterator iterator = objectNode.getNodes();
                    while (iterator.hasNext()) {
                        Node itemNode = iterator.nextNode();
                        String itemClassName = itemNode.getProperty(CLASS_PROPERTY).getString();
                        Class<?> itemClass = Class.forName(itemClassName);
                        if (itemSuperclass.isAssignableFrom(itemClass)) {
                            Object item = itemClass.newInstance();
                            items.add(map(itemClass, item, itemNode));
                        }
                    }
                    if (items.size() > 0) {
                        field.set(object, toArray(items, itemSuperclass));
                    }
                }
            }
            objectClass = objectClass.getSuperclass();
            if (objectNode.hasProperty(PARENT_PROPERTY)) {
                String supplierId = objectNode.getProperty(PARENT_PROPERTY).getString();
                objectNode = objectNode.getSession().getNodeByIdentifier(supplierId);
            }
            map(objectClass, object, objectNode);
        }
        return object;
    }

    private <T> T[] toArray(List<?> list, Class<T> type) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(type, list.size());
        int i = 0;
        for (Object item : list) {
            array[i++] = type.cast(item);
        }
        return array;
    }
}
