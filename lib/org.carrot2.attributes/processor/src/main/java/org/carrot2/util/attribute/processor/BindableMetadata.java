
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.processor;

import java.util.*;

import org.carrot2.util.attribute.*;

/**
 * Human-readable metadata for a {@link Bindable} type.
 */
public class BindableMetadata extends CommonMetadata
{
    private Map<String, AttributeMetadata> attributeMetadataInternal; 

    BindableMetadata()
    {
    }

    /**
     * Returns metadata for all attributes in the bindable type.
     * 
     * @return metadata for all attributes in the bindable type. Key in the map represents
     *         the attribute key as defined by {@link Attribute#key()}. The returned map
     *         is unmodifiable.
     */
    public Map<String, AttributeMetadata> getAttributeMetadata()
    {
        return Collections.unmodifiableMap(attributeMetadataInternal);
    }

    void setAttributeMetadata(Map<String, AttributeMetadata> map)
    {
        attributeMetadataInternal = map;
    }

    @Override
    public String toString()
    {
        return "[" + title + ", " + label + ", " + description + "]";
    }

    /**
     * Load metadata of a given bindable class, merged with bindable attributes from 
     * parent classes marked with {@link Bindable} as well.
     */
    public static BindableMetadata forClassWithParents(final Class<?> clazz)
    {
        IBindableDescriptor descriptor = BindableDescriptorFactory.getDescriptor(clazz); 
    
        final BindableMetadata bindable = new BindableMetadata();
        bindable.setDescription(descriptor.getDescription());
        bindable.setLabel(descriptor.getLabel());
        bindable.setTitle(descriptor.getTitle());
        bindable.setAttributeMetadata(
            asAttributeMetadata(descriptor.getAttributes()));
        return bindable;
    }

    private static Map<String, AttributeMetadata> asAttributeMetadata(Set<AttributeInfo> attributes)
    {
        final Map<String, AttributeMetadata> map = new HashMap<String, AttributeMetadata>();
        for (AttributeInfo attr : attributes)
        {
            map.put(attr.fieldName, 
                new AttributeMetadata(
                    attr.title, attr.label, attr.description, attr.group, attr.level));
        }
        return map;
    }
}
