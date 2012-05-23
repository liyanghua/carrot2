package org.carrot2.util.attribute.it.testclasses;

import org.carrot2.util.attribute.*;

@Bindable
public class BindableWithAttributes
{
    /**
     * This is a title. And this is a follow-up.
     * 
     * @label abc
     */
    @Input
    @Output
    @Attribute
    public int attrInt = 10;
}
