package de.uni_passau.fim.se2.sa.sign.interpretation;

import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static de.uni_passau.fim.se2.sa.sign.interpretation.SignValue.*;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SignTransferRelationTest {

    @Test
    public void testEvaluateValue() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertEquals(SignValue.ZERO, transferRelation.evaluate(0));
        assertEquals(SignValue.MINUS, transferRelation.evaluate(-1));
        assertEquals(PLUS, transferRelation.evaluate(1));
    }

    @Test
    public void testEvaluateUnaryOpNeg() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.NEG, null)
        );
        assertThrows(IllegalStateException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, null)
        );

        assertEquals(SignValue.UNINITIALIZED_VALUE, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.UNINITIALIZED_VALUE));
        assertEquals(SignValue.BOTTOM, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.BOTTOM));
        assertEquals(SignValue.MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, PLUS));
        assertEquals(PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.MINUS));
        assertEquals(SignValue.PLUS_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.PLUS_MINUS));
        assertEquals(SignValue.ZERO, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO));
        assertEquals(SignValue.ZERO_PLUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO_MINUS));
        assertEquals(SignValue.ZERO_MINUS, transferRelation.evaluate(TransferRelation.Operation.NEG, SignValue.ZERO_PLUS));
    }

    @Test
    public void testEvaluateBinaryOpAdd() {
        SignTransferRelation transferRelation = new SignTransferRelation();

        // Adding Everything(except ⊥) with ∅ should return ⊤
        // Adding Everything with ⊥ should return ⊥
        for (SignValue signValue : SignValue.values()) {
            if (signValue != SignValue.BOTTOM) {
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.ADD, signValue, SignValue.UNINITIALIZED_VALUE)
                );
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.UNINITIALIZED_VALUE, signValue)
                );
            }
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.ADD, signValue, SignValue.BOTTOM)
            );
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.ADD, SignValue.BOTTOM, signValue)
            );
        }

        Map<Pair<SignValue, SignValue>, SignValue> testCasesAdd = Map.ofEntries(
                entry(Pair.of(PLUS, MINUS), TOP),
                entry(Pair.of(MINUS, PLUS), TOP),
                entry(Pair.of(PLUS, PLUS), PLUS),
                entry(Pair.of(PLUS, ZERO), PLUS),
                entry(Pair.of(ZERO, PLUS), PLUS),
                entry(Pair.of(PLUS, ZERO_PLUS), PLUS),
                entry(Pair.of(ZERO_PLUS, PLUS), PLUS),
                entry(Pair.of(PLUS, ZERO_MINUS), TOP),
                entry(Pair.of(ZERO_MINUS, PLUS), TOP),
                entry(Pair.of(PLUS, PLUS_MINUS), TOP),
                entry(Pair.of(PLUS_MINUS, PLUS), TOP),
                entry(Pair.of(PLUS, TOP), TOP),
                entry(Pair.of(TOP, PLUS), TOP),
                entry(Pair.of(MINUS, MINUS), MINUS),
                entry(Pair.of(MINUS, ZERO), MINUS),
                entry(Pair.of(ZERO, MINUS), MINUS),
                entry(Pair.of(MINUS, ZERO_MINUS), MINUS),
                entry(Pair.of(ZERO_MINUS, MINUS), MINUS),
                entry(Pair.of(MINUS, ZERO_PLUS), TOP),
                entry(Pair.of(ZERO_PLUS, MINUS), TOP),
                entry(Pair.of(MINUS, PLUS_MINUS), TOP),
                entry(Pair.of(PLUS_MINUS, MINUS), TOP)
        );
        for (var entry : testCasesAdd.entrySet()) {
            assertEquals(entry.getValue(), transferRelation.evaluate(TransferRelation.Operation.ADD, entry.getKey().getFirst(), entry.getKey().getSecond()));
        }
    }

    @Test
    public void testEvaluateBinaryOpSub() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        for (SignValue signValue : SignValue.values()) {
            for (SignValue subSignValue : SignValue.values()) {
                assertEquals(
                        transferRelation.evaluate(TransferRelation.Operation.NEG, transferRelation.evaluate(TransferRelation.Operation.SUB, subSignValue, signValue)),
                        transferRelation.evaluate(TransferRelation.Operation.SUB, signValue, subSignValue)
                );
            }
        }
        assertEquals(
                SignValue.PLUS_MINUS,
                transferRelation.evaluate(TransferRelation.Operation.SUB, SignValue.ZERO, SignValue.PLUS_MINUS)
        );
        assertEquals(
                TOP,
                transferRelation.evaluate(TransferRelation.Operation.SUB, PLUS_MINUS, PLUS_MINUS)
        );

    }

    @Test
    public void testEvaluateBinaryOpMul() {
        SignTransferRelation transferRelation = new SignTransferRelation();

        // Multiplying Everything(except ⊥ and 0) with ∅ should return ⊤
        // Multiplying Everything with ⊥ should return ⊥
        for (SignValue signValue : SignValue.values()) {
            if (signValue != SignValue.BOTTOM && signValue != ZERO) {
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.MUL, signValue, SignValue.UNINITIALIZED_VALUE)
                );
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.UNINITIALIZED_VALUE, signValue)
                );
            }
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.MUL, signValue, SignValue.BOTTOM)
            );
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.MUL, SignValue.BOTTOM, signValue)
            );
        }

        Map<Pair<SignValue, SignValue>, SignValue> testCasesMul = Map.ofEntries(
                entry(Pair.of(ZERO, PLUS), ZERO),
                entry(Pair.of(ZERO, ZERO_PLUS), ZERO),
                entry(Pair.of(ZERO, TOP), ZERO),
                entry(Pair.of(PLUS, ZERO), ZERO),
                entry(Pair.of(ZERO_PLUS, ZERO), ZERO),
                entry(Pair.of(TOP, ZERO), ZERO),
                entry(Pair.of(ZERO, PLUS_MINUS), ZERO),
                entry(Pair.of(PLUS_MINUS, ZERO), ZERO),

                entry(Pair.of(PLUS, PLUS), PLUS),
                entry(Pair.of(PLUS, MINUS), MINUS),
                entry(Pair.of(MINUS, PLUS), MINUS),
                entry(Pair.of(PLUS, ZERO_PLUS), ZERO_PLUS),
                entry(Pair.of(ZERO_PLUS, PLUS), ZERO_PLUS),
                entry(Pair.of(PLUS, ZERO_MINUS), ZERO_MINUS),
                entry(Pair.of(ZERO_MINUS, PLUS), ZERO_MINUS),
                entry(Pair.of(PLUS, PLUS_MINUS), PLUS_MINUS),
                entry(Pair.of(PLUS_MINUS, PLUS), PLUS_MINUS),

                entry(Pair.of(MINUS, MINUS), PLUS),
                entry(Pair.of(MINUS, ZERO_PLUS), ZERO_MINUS),
                entry(Pair.of(ZERO_PLUS, MINUS), ZERO_MINUS),
                entry(Pair.of(MINUS, ZERO_MINUS), ZERO_PLUS),
                entry(Pair.of(ZERO_MINUS, MINUS), ZERO_PLUS),
                entry(Pair.of(MINUS, PLUS_MINUS), PLUS_MINUS),
                entry(Pair.of(PLUS_MINUS, MINUS), PLUS_MINUS)
        );

        for (var entry : testCasesMul.entrySet()) {
            assertEquals(entry.getValue(), transferRelation.evaluate(TransferRelation.Operation.MUL, entry.getKey().getFirst(), entry.getKey().getSecond()));
        }
    }

    @Test
    public void testEvaluateBinaryOpDiv() {
        SignTransferRelation transferRelation = new SignTransferRelation();

        // Since division by zero is undefined, any value divided by zero should yield ⊥.
        // Zero divided by anything (except 0 and ⊥) should result in ZERO.
        // TOP divided by anything (except 0 and ⊥) should result in TOP.
        // Anything (except 0 and ⊥) divided by TOP should result in TOP.
        for (SignValue signValue : SignValue.values()) {
            assertEquals(BOTTOM, transferRelation.evaluate(TransferRelation.Operation.DIV, signValue, ZERO));
            if (signValue != ZERO && signValue != BOTTOM) {
                assertEquals(ZERO, transferRelation.evaluate(TransferRelation.Operation.DIV, ZERO, signValue));
                assertEquals(TOP, transferRelation.evaluate(TransferRelation.Operation.DIV, TOP, signValue));
                assertEquals(TOP, transferRelation.evaluate(TransferRelation.Operation.DIV, signValue, TOP));
            }
        }

        // Dividing Everything(except ⊥ and 0) by ∅ should return ⊤
        // Dividing Everything by ⊥ should return ⊥
        for (SignValue signValue : SignValue.values()) {
            if (signValue != SignValue.BOTTOM && signValue != ZERO) {
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.DIV, signValue, SignValue.UNINITIALIZED_VALUE)
                );
                assertEquals(
                        SignValue.TOP,
                        transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.UNINITIALIZED_VALUE, signValue)
                );
            }
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.DIV, signValue, SignValue.BOTTOM)
            );
            assertEquals(
                    SignValue.BOTTOM,
                    transferRelation.evaluate(TransferRelation.Operation.DIV, SignValue.BOTTOM, signValue)
            );
        }

        Map<Pair<SignValue, SignValue>, SignValue> testCasesMul = Map.ofEntries(
                entry(Pair.of(PLUS, PLUS), ZERO_PLUS),
                entry(Pair.of(MINUS, MINUS), ZERO_PLUS),
                entry(Pair.of(PLUS, MINUS), ZERO_MINUS),
                entry(Pair.of(MINUS, PLUS), ZERO_MINUS),
                entry(Pair.of(PLUS, ZERO_MINUS), ZERO_MINUS),
                entry(Pair.of(ZERO_MINUS, PLUS), ZERO_MINUS),
                entry(Pair.of(PLUS, ZERO_PLUS), ZERO_PLUS),
                entry(Pair.of(ZERO_PLUS, PLUS), ZERO_PLUS),
                entry(Pair.of(PLUS, PLUS_MINUS), TOP),
                entry(Pair.of(PLUS_MINUS, PLUS_MINUS), TOP)

        );

        for (var entry : testCasesMul.entrySet()) {
            assertEquals(entry.getValue(), transferRelation.evaluate(TransferRelation.Operation.DIV, entry.getKey().getFirst(), entry.getKey().getSecond()));
        }

    }

    @Test
    public void testEvaluateBinaryOpValue() {
        SignTransferRelation transferRelation = new SignTransferRelation();
        assertThrows(IllegalStateException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.NEG, PLUS, PLUS)
        );
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, null, PLUS)
        );
        assertThrows(NullPointerException.class,
                () -> transferRelation.evaluate(TransferRelation.Operation.ADD, PLUS, null)
        );
    }
}
