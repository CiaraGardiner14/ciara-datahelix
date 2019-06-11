package com.scottlogic.deg.generator.utils;

import com.scottlogic.deg.common.util.HeterogeneousTypeContainer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HeterogeneousTypeContainerTest {

    @Test
    void container_shouldReturnArbitraryTypes_ifGeneric() {
        HeterogeneousTypeContainer<Object> container = new HeterogeneousTypeContainer<>();
        String string = "a string";
        Integer integer = 3;
        container = container.put(String.class, string);
        container = container.put(Integer.class, integer);

        assertEquals(string, container.get(String.class).get());
        assertEquals(integer, container.get(Integer.class).get());
    }

    @Test
    void container_shouldReturnInterfaceTypes_ifSpecific() {
        HeterogeneousTypeContainer<Number> numbers = new HeterogeneousTypeContainer<>();
        Integer integerNumber = 2;
        Float floatNumber = 3.0f;

        numbers = numbers.put(Integer.class, integerNumber).put(Float.class, floatNumber);

        assertEquals(integerNumber, numbers.get(Integer.class).get());
        assertEquals(floatNumber, numbers.get(Float.class).get());

    }

}