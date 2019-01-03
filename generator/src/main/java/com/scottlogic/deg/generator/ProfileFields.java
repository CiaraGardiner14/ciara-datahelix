package com.scottlogic.deg.generator;

import com.scottlogic.deg.generator.inputs.validation.ProfileVisitor;
import com.scottlogic.deg.generator.inputs.validation.VisitableProfileElement;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ProfileFields implements Iterable<Field>, VisitableProfileElement {
    private final List<Field> fields;

    public ProfileFields(List<Field> fields) {
        this.fields = fields;
    }

    public Field getByName(String fieldName) {
        return this.fields.stream()
            .filter(f -> f.name.equals(fieldName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Profile fields do not contain " + fieldName));
    }
    
    public int size() {
        return this.fields.size();
    }

    @Override
    public Iterator<Field> iterator() {
        return fields.iterator();
    }

    public Stream<Field> stream() {
        return this.fields.stream();
    }

    @Override
    public void accept(ProfileVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        ProfileFields profileFields = (ProfileFields) obj;
        return fields.equals(profileFields.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }
}
