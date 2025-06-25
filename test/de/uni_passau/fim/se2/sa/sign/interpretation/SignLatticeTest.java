package de.uni_passau.fim.se2.sa.sign.interpretation;

import de.uni_passau.fim.se2.sa.sign.lattice.SignLattice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    }

    @Test
    public void testisLessOrEqual() {
        SignLattice signLattice = new SignLattice();
        assertTrue(signLattice.isLessOrEqual(signLattice.bottom(), signLattice.top()));
    }
}
