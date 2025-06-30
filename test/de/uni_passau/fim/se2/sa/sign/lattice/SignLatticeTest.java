package de.uni_passau.fim.se2.sa.sign.lattice;

import de.uni_passau.fim.se2.sa.sign.interpretation.SignValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SignLatticeTest {
    @Test
    public void testTop() {
        SignLattice signLattice = new SignLattice();
        assertEquals(SignValue.TOP, signLattice.top());
    }

    @Test
    public void testBottom() {
        SignLattice signLattice = new SignLattice();
        assertEquals(SignValue.BOTTOM, signLattice.bottom());
    }

    @Test
    public void testJoin() {
        SignLattice signLattice = new SignLattice();
        assertEquals(SignValue.PLUS_MINUS, signLattice.join(SignValue.MINUS, SignValue.PLUS));
        assertEquals(SignValue.ZERO_PLUS, signLattice.join(SignValue.ZERO, SignValue.PLUS));
        assertEquals(SignValue.ZERO_MINUS, signLattice.join(SignValue.MINUS, SignValue.ZERO));
        assertEquals(SignValue.TOP, signLattice.join(SignValue.ZERO_MINUS, SignValue.PLUS));
        assertEquals(SignValue.TOP, signLattice.join(SignValue.ZERO_PLUS, SignValue.MINUS));
        assertEquals(SignValue.PLUS, signLattice.join(SignValue.BOTTOM, SignValue.PLUS));
    }

    @Test
    public void testisLessOrEqual() {
        SignLattice signLattice = new SignLattice();
        assertTrue(signLattice.isLessOrEqual(signLattice.bottom(), signLattice.top()));
        assertTrue(signLattice.isLessOrEqual(SignValue.MINUS, SignValue.ZERO_MINUS));
        assertFalse(signLattice.isLessOrEqual(SignValue.PLUS, SignValue.MINUS));
    }
}
