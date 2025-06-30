package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignTransferRelationTest {

    @Test
    public void testEvaluateValue() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertEquals(SignValue.ZERO, transferRelation.evaluate(0));
        assertEquals(SignValue.MINUS, transferRelation.evaluate(-1));
        assertEquals(SignValue.PLUS, transferRelation.evaluate(1));
    }

    @Test
    public void testEvaluateUnaryOpValue() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.NEG, null)
        );
        assertThrows(IllegalStateException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, null)
        );

        assertEquals(SignValue.UNINITIALIZED_VALUE, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.UNINITIALIZED_VALUE));
        assertEquals(SignValue.BOTTOM, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.BOTTOM));
        assertEquals(SignValue.MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.PLUS));
        assertEquals(SignValue.PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.MINUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.PLUS_MINUS));
        assertEquals(SignValue.ZERO, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO_MINUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO_PLUS));
    }

    @Test
    public void testEvaluateBinaryOpValue() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertThrows(IllegalStateException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.PLUS, SignValue.PLUS)
        );
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, null, SignValue.PLUS)
        );
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.PLUS, null)
        );
        assertEquals(
                SignValue.TOP,
                transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.MINUS, SignValue.UNINITIALIZED_VALUE)
        );
        assertEquals(
                SignValue.TOP,
                transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.ZERO, SignValue.UNINITIALIZED_VALUE)
        );
        assertEquals(
                SignValue.PLUS_MINUS,
                transferRelation.evaluate(TransferRelation.Operation.SUB, SignValue.ZERO, SignValue.PLUS_MINUS)
        );
        // 0 = 0 * (+-)
        assertEquals(
                SignValue.ZERO,
                transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.ZERO, SignValue.PLUS_MINUS)
        );
        // 0 = (+-) * 0
        assertEquals(
                SignValue.ZERO,
                transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.PLUS_MINUS, SignValue.ZERO)
        );
        // 0 = 0 * (*)
        assertEquals(
                SignValue.ZERO,
                transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.ZERO, SignValue.TOP)
        );
        assertEquals(
                SignValue.ZERO,
                transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.ZERO, SignValue.PLUS_MINUS)
        );
        assertEquals(
                SignValue.TOP,
                transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.ZERO, SignValue.TOP)
        );
        assertEquals(
                SignValue.TOP,
                transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.PLUS, SignValue.ZERO_MINUS)
        );
        assertEquals(
                transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.MINUS, SignValue.MINUS),
                transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.MINUS, SignValue.MINUS)
        );
        assertEquals(
                SignValue.TOP,
                transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.PLUS, SignValue.MINUS)
        );
        assertEquals(
                SignValue.PLUS,
                transferRelation.evaluate(TransferRelation.Operation.SUB, SignValue.PLUS, SignValue.MINUS)
        );

        assertEquals(SignValue.TOP, transferRelation.evaluate(TransferRelation.Operation.SUB, SignValue.PLUS_MINUS, SignValue.PLUS_MINUS));

        assertEquals(
                transferRelation.evaluate(
                        TransferRelation.Operation.SUB,
                        SignValue.PLUS,
                        SignValue.MINUS),
                transferRelation.evaluate(
                        TransferRelation.Operation.ADD,
                        SignValue.PLUS,
                        transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.MINUS)
                )
        );

    }
}
