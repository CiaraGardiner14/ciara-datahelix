package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.Field;

public class FormatConstraint implements IConstraint {

    public final Field field;
    public final String format;

    public FormatConstraint(Field field, String format) {
        this.field = field;
        this.format = format;
    }

    @Override
    public String toDotLabel(){
        return String.format("%s has format '%s'", field.name, format);
    }

}
