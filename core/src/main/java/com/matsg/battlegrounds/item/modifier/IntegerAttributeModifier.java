package com.matsg.battlegrounds.item.modifier;

import com.matsg.battlegrounds.api.util.AttributeModifier;
import com.matsg.battlegrounds.api.util.ValueObject;
import com.matsg.battlegrounds.util.Operator;
import com.matsg.battlegrounds.util.data.IntegerValueObject;

public class IntegerAttributeModifier implements AttributeModifier<Integer> {

    private Integer value;
    private Operator operator;
    private String regex;

    public IntegerAttributeModifier(Integer value, Operator operator) {
        this.value = value;
        this.operator = operator;
    }

    public IntegerAttributeModifier(Integer value) {
        this(value, Operator.EQUALIZATION);
    }

    public IntegerAttributeModifier(String regex) {
        this.regex = regex;
    }

    public ValueObject<Integer> modify(ValueObject<Integer> valueObject, String[] args) {
        // If the modification value does not have to parsed from text, apply the operator right away
        if (value != null && operator != null) {
            return new IntegerValueObject(operator.apply(valueObject.getValue(), value).intValue());
        }

        Integer value;
        Operator operator = Operator.fromText(regex.substring(0, 1));

        try {
            if (regex.contains("arg")) {
                int index = Integer.parseInt(regex.substring(4)) - 1;
                value = Integer.parseInt(args[index]);
            } else {
                value = Integer.parseInt(regex.substring(1));
            }
        } catch (NumberFormatException e) {
            throw new AttributeModificationException("Unable to modify integer attribute with regex " + regex, e);
        }

        return new IntegerValueObject((int) operator.apply(valueObject.getValue(), value));
    }
}
