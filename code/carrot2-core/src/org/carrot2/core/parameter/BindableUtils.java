package org.carrot2.core.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import org.carrot2.core.constraint.*;

final class BindableUtils
{
    /**
     * Caches the sets of declared fields determined for class hierarchies by the
     * {@link #getFieldsFromBindableHierarchy(Class)} method.
     */
    private static final Map<Class<?>, Collection<Field>> FIELD_CACHE = new WeakHashMap<Class<?>, Collection<Field>>();

    public static Collection<Field> getFieldsFromBindableHierarchy(Class<?> clazz)
    {
        synchronized (FIELD_CACHE)
        {
            Collection<Field> fields = FIELD_CACHE.get(clazz);
            if (fields == null)
            {
                fields = new HashSet<Field>();

                if (clazz.getAnnotation(Bindable.class) != null)
                {
                    fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
                }

                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null)
                {
                    fields.addAll(getFieldsFromBindableHierarchy(superClass));
                }

                FIELD_CACHE.put(clazz, fields);
            }
            return fields;
        }
    }

    public static String getKey(Field field)
    {
        Parameter attributeAnnotation = field.getAnnotation(Parameter.class);

        if (attributeAnnotation == null || "".equals(attributeAnnotation.key()))
        {
            final Class<?> declaringClass = field.getDeclaringClass();
            Bindable classAnnotation = declaringClass.getAnnotation(Bindable.class);
            if (classAnnotation == null)
            {
                throw new IllegalArgumentException();
            }

            final String classPrefix = classAnnotation.prefix();
            if ("".equals(classPrefix))
            {
                return declaringClass.getName() + "." + field.getName();
            }
            else
            {
                return classPrefix + "." + field.getName();
            }

        }
        else
        {
            return attributeAnnotation.key();
        }
    }

    /**
     *
     */
    public static Constraint getConstraint(final Field field)
    {
        List<Constraint> constraints = new ArrayList<Constraint>();
        for (Annotation annotation : field.getAnnotations())
        {
            if (ConstraintFactory.isConstraintAnnotation(annotation.annotationType()))
            {
                constraints.add(ConstraintFactory.createConstraint(annotation));
            }
        }
        Constraint constraint = null;
        if (constraints.size() == 1)
        {
            constraint = constraints.get(0);
        }
        else if (constraints.size() > 1)
        {
            constraint = new CompoundConstraint(constraints);
        }
        return constraint;
    }
}