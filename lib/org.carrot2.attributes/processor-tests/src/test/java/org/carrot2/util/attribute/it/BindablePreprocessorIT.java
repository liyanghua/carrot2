package org.carrot2.util.attribute.it;

import org.carrot2.util.attribute.it.testclasses.BindableWithAttributesDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class BindablePreprocessorIT {
    @Test
    public void checkMetadataGenerated() {
        // Just check that we have access to it. It'll fail with
        // class not found if not generated.
        Assert.assertTrue(BindableWithAttributesDescriptor.class != null);
    }
    
    @Test
    public void checkJavaDocAvailable() {
        Assert.assertEquals("abc", BindableWithAttributesDescriptor.attributes.attrInt.label);
        Assert.assertEquals("This is a title", BindableWithAttributesDescriptor.attributes.attrInt.title);
        Assert.assertEquals("And this is a follow-up.", BindableWithAttributesDescriptor.attributes.attrInt.description);
    }    
}
