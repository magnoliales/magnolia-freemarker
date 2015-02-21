package com.magnoliales.handlebars.utils.dam;

import com.magnoliales.handlebars.annotations.Field;
import info.magnolia.dam.api.Asset;
import info.magnolia.dam.templating.functions.DamTemplatingFunctions;

import javax.inject.Inject;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

public class AssetReader implements Field.Reader {

    private DamTemplatingFunctions damTemplatingFunction;

    @Inject
    public AssetReader(DamTemplatingFunctions damTemplatingFunction) {
        this.damTemplatingFunction = damTemplatingFunction;
    }

    @Override
    public Asset read(Property property) throws RepositoryException {
        return damTemplatingFunction.getAsset(property.getString());
    }
}
