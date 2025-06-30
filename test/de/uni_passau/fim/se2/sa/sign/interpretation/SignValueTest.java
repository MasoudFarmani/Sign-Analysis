package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignValueTest {
    @Test
    void testSignValueJoin() {
        for (SignValue signValue : SignValue.values()) {
            assertThrows(IllegalStateException.class,
                    () -> SignValue.UNINITIALIZED_VALUE.join(signValue)
            );
            assertThrows(IllegalStateException.class,
                    () -> signValue.join(SignValue.UNINITIALIZED_VALUE)
            );
        }

        assertEquals(SignValue.PLUS_MINUS, SignValue.PLUS.join(SignValue.MINUS));
        assertEquals(SignValue.PLUS_MINUS, SignValue.MINUS.join(SignValue.PLUS));

        assertEquals(SignValue.ZERO_PLUS, SignValue.ZERO.join(SignValue.PLUS));
        assertEquals(SignValue.ZERO_PLUS, SignValue.PLUS.join(SignValue.ZERO));

        assertEquals(SignValue.ZERO_MINUS, SignValue.ZERO.join(SignValue.MINUS));
        assertEquals(SignValue.ZERO_MINUS, SignValue.MINUS.join(SignValue.ZERO));

        assertEquals(SignValue.TOP, SignValue.ZERO.join(SignValue.PLUS_MINUS));
        assertEquals(SignValue.TOP, SignValue.PLUS_MINUS.join(SignValue.ZERO));
        assertEquals(SignValue.TOP, SignValue.BOTTOM.join(SignValue.TOP));
        assertEquals(SignValue.TOP, SignValue.TOP.join(SignValue.BOTTOM));
        assertEquals(SignValue.TOP, SignValue.ZERO_PLUS.join(SignValue.ZERO_MINUS));
        assertEquals(SignValue.TOP, SignValue.ZERO_MINUS.join(SignValue.ZERO_PLUS));
        //==============
        SignTransferRelation tr = new SignTransferRelation();
        assertEquals(SignValue.TOP,
                tr.evaluate(TransferRelation.Operation.DIV, SignValue.MINUS, SignValue.MINUS)
                        .join(tr.evaluate(TransferRelation.Operation.DIV, SignValue.MINUS, SignValue.PLUS)));
        //==============

        assertEquals(SignValue.MINUS, SignValue.MINUS.join(SignValue.BOTTOM));
        assertEquals(SignValue.MINUS, SignValue.BOTTOM.join(SignValue.MINUS));

        assertEquals(SignValue.PLUS, SignValue.BOTTOM.join(SignValue.PLUS));
        assertEquals(SignValue.PLUS, SignValue.PLUS.join(SignValue.BOTTOM));
    }

    @Test
    void testIsLessOrEqual() {
        // BOTTOM is less than every sign value(except ∅)
        for (SignValue signValue : SignValue.values()) {
            if (signValue == SignValue.UNINITIALIZED_VALUE) continue;
            assertTrue(SignValue.BOTTOM.isLessOrEqual(signValue));
            assertTrue(signValue.isLessOrEqual(SignValue.TOP));
        }
        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.MINUS));
        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.PLUS_MINUS));

        // Not comparable
        assertFalse(SignValue.MINUS.isLessOrEqual(SignValue.ZERO_PLUS));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.ZERO_MINUS));

        assertTrue(SignValue.MINUS.isLessOrEqual(SignValue.ZERO_MINUS));
        assertTrue(SignValue.PLUS.isLessOrEqual(SignValue.PLUS_MINUS));
        assertTrue(SignValue.PLUS.isLessOrEqual(SignValue.ZERO_PLUS));
        assertTrue(SignValue.PLUS.isLessOrEqual(SignValue.PLUS));

        assertFalse(SignValue.MINUS.isLessOrEqual(SignValue.PLUS));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.MINUS));
        assertFalse(SignValue.ZERO.isLessOrEqual(SignValue.PLUS));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.ZERO));
        assertFalse(SignValue.PLUS.isLessOrEqual(SignValue.MINUS));
        assertFalse(SignValue.ZERO_MINUS.isLessOrEqual(SignValue.ZERO_PLUS));
        assertFalse(SignValue.ZERO_PLUS.isLessOrEqual(SignValue.ZERO_MINUS));
        assertFalse(SignValue.ZERO_PLUS.isLessOrEqual(SignValue.PLUS_MINUS));
        assertFalse(SignValue.PLUS_MINUS.isLessOrEqual(SignValue.ZERO_PLUS));
        assertFalse(SignValue.ZERO_MINUS.isLessOrEqual(SignValue.PLUS_MINUS));
        assertFalse(SignValue.PLUS_MINUS.isLessOrEqual(SignValue.ZERO_MINUS));
    }

    @Test
    void testIsZero() {
        assertTrue(SignValue.isZero(SignValue.ZERO));
    }

    @Test
    void testisMaybeZero() {
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO));
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeZero(SignValue.ZERO_PLUS));
        assertFalse(SignValue.isMaybeZero(SignValue.PLUS_MINUS));
    }

    @Test
    void testisNegative() {
        assertTrue(SignValue.isNegative(SignValue.MINUS));
    }

    @Test
    void isMaybeNegative() {
        assertTrue(SignValue.isMaybeNegative(SignValue.MINUS));
        assertTrue(SignValue.isMaybeNegative(SignValue.ZERO_MINUS));
        assertTrue(SignValue.isMaybeNegative(SignValue.PLUS_MINUS));
        assertFalse(SignValue.isMaybeNegative(SignValue.PLUS));
    }
}
