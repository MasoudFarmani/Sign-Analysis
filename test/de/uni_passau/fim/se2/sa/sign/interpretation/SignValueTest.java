package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignValueTest {
    @Test
    void testSignValueJoin() {
        assertEquals(SignValue.PLUS_MINUS, SignValue.PLUS.join(SignValue.MINUS));
        assertEquals(SignValue.ZERO_PLUS, SignValue.ZERO.join(SignValue.PLUS));
        assertEquals(SignValue.TOP, SignValue.ZERO.join(SignValue.PLUS_MINUS));
        assertEquals(SignValue.MINUS, SignValue.MINUS.join(SignValue.BOTTOM));
        assertEquals(SignValue.PLUS, SignValue.BOTTOM.join(SignValue.PLUS));
    }

    @Test
    void testIsLessOrEqual(){
        assertTrue(SignValue.BOTTOM.isLessOrEqual(SignValue.MINUS));
        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.MINUS));
        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.PLUS_MINUS));
        assertTrue(SignValue.PLUS.isLessOrEqual(SignValue.PLUS_MINUS));
        assertTrue(SignValue.PLUS.isLessOrEqual(SignValue.TOP));
        assertTrue(SignValue.BOTTOM.isLessOrEqual(SignValue.TOP));
        assertFalse(SignValue.MINUS.isLessOrEqual(SignValue.PLUS));
        assertFalse(SignValue.ZERO.isLessOrEqual(SignValue.PLUS));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.MINUS));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.ZERO));
    }

    @Test
    void testIsZero(){
        assertTrue(SignValue.isZero(SignValue.ZERO));
    }

    @Test
    void testisMaybeZero(){
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO));
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_PLUS));
        assertFalse(SignValue.isMaybeZero(SignValue.PLUS_MINUS));
    }

    @Test
    void testisNegative(){
        assertTrue(SignValue.isNegative(SignValue.MINUS));
    }

    @Test
    void isMaybeNegative(){
        assertTrue(SignValue.isMaybeNegative(SignValue.MINUS));
        assertTrue(SignValue.isMaybeNegative(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeNegative(SignValue.PLUS_MINUS));
        assertFalse(SignValue.isMaybeNegative(SignValue.PLUS));
    }
}
