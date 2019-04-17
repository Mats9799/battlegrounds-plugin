package com.matsg.battlegrounds.item.modifier;

import com.matsg.battlegrounds.api.util.AttributeModifier;
import com.matsg.battlegrounds.api.util.ValueObject;
import com.matsg.battlegrounds.util.Operator;
import com.matsg.battlegrounds.util.valueobject.FloatValueObject;

public class FloatAttributeModifier implements AttributeModifier<Float> {

    private Float value;
    private Operator operator;
    private String regex;

    public FloatAttributeModifier(Float value, Operator operator) {
        this.value = value;
        this.operator = operator;
    }

    public FloatAttributeModifier(Float value) {
        this(value, Operator.EQUALIZATION);
    }

    public FloatAttributeModifier(String regex) {
        this.regex = regex;
    }

    public ValueObject<Float> modify(ValueObject<Float> valueObject, String[] args) {
        Float value;
        Operator operator = Operator.fromText(regex.substring(0, 1));

        if (regex.contains("arg")) {
            int index = Integer.parseInt(regex.substring(4, regex.length())) - 1;
            value = Float.parseFloat(args[index]);
        } else {
            value = Float.parseFloat(regex.substring(1, regex.length()));
        }

        return new FloatValueObject(((Number) operator.apply(valueObject.getValue(), value)).floatValue());
    }
}