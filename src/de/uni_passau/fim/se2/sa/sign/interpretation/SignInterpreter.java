package de.uni_passau.fim.se2.sa.sign.interpretation;

import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;

import static jdk.internal.org.objectweb.asm.Type.*;

public class SignInterpreter extends Interpreter<SignValue> implements Opcodes {

  private final String pClassName;
  private final Map<String, MethodNode> methods;

  public SignInterpreter(final String pClassName, final Map<String, MethodNode> methods) {
    this(ASM9, pClassName, methods);
  }

  /**
   * Constructs a new {@link Interpreter}.
   *
   * @param pAPI The ASM API version supported by this interpreter. Must be one of {@link #ASM4},
   *     {@link #ASM5}, {@link #ASM6}, {@link #ASM7}, {@link #ASM8}, or {@link #ASM9}
   * @param pClassName The name of the class that contains the method to be analyzed.
   * @param methods All methods of the class that contains the method to be analyzed.
   */
  protected SignInterpreter(final int pAPI, final String pClassName, final Map<String, MethodNode> methods) {
    super(pAPI);
    if (getClass() != SignInterpreter.class) {
      throw new IllegalStateException();
    }

    this.pClassName = pClassName;
    this.methods = methods;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue newValue(final Type pType) {
    if (pType == null) return SignValue.UNINITIALIZED_VALUE;
    return switch (pType.getSort()){
        case INT, BYTE, CHAR, SHORT, BOOLEAN -> SignValue.TOP;
        default -> SignValue.UNINITIALIZED_VALUE;
    };
  }

  /** {@inheritDoc} */
  @Override
  public SignValue newOperation(final AbstractInsnNode pInstruction) throws AnalyzerException {

    SignTransferRelation tr = new SignTransferRelation();
    return switch (pInstruction.getOpcode()){
        case ICONST_M1 -> tr.evaluate(-1);
        case ICONST_0 -> tr.evaluate(0);
        case ICONST_1 -> tr.evaluate(1);
        case ICONST_2 -> tr.evaluate(2);
        case ICONST_3 -> tr.evaluate(3);
        case ICONST_4 -> tr.evaluate(4);
        case ICONST_5 -> tr.evaluate(5);
        case ILOAD-> SignValue.TOP;
        case ARRAYLENGTH -> SignValue.ZERO_PLUS;
        case GETFIELD, GETSTATIC -> "I".equals(((FieldInsnNode) pInstruction).desc) ? SignValue.TOP : SignValue.UNINITIALIZED_VALUE;
        case BIPUSH, SIPUSH -> tr.evaluate(((IntInsnNode)pInstruction).operand);
        case LDC -> {
            LdcInsnNode ldcInsn = (LdcInsnNode)pInstruction;
            if (ldcInsn.cst instanceof Integer) yield tr.evaluate((Integer) ldcInsn.cst);
            yield SignValue.UNINITIALIZED_VALUE;
        }
        default -> SignValue.UNINITIALIZED_VALUE;
    };
  }

  /** {@inheritDoc} */
  @Override
  public SignValue copyOperation(final AbstractInsnNode pInstruction, final SignValue pValue) {
    return pValue;
  }

  /** {@inheritDoc} */
  @Override
  public SignValue unaryOperation(final AbstractInsnNode pInstruction, final SignValue pValue)
      throws AnalyzerException {

    SignTransferRelation tr = new SignTransferRelation();
    return switch (pInstruction.getOpcode()){
        case INEG -> tr.evaluate(TransferRelation.Operation.NEG, pValue);
        case IALOAD -> SignValue.TOP;
        case IRETURN -> pValue;
        case IINC -> {
            IincInsnNode iinc = (IincInsnNode) pInstruction;
            int incValue = iinc.incr;
            yield tr.evaluate(TransferRelation.Operation.ADD, pValue, tr.evaluate(incValue));
        }
        default -> SignValue.UNINITIALIZED_VALUE;
    };
  }

  /** {@inheritDoc} */
  @Override
  public SignValue binaryOperation(
      final AbstractInsnNode pInstruction, final SignValue pValue1, final SignValue pValue2) {

      SignTransferRelation tr = new SignTransferRelation();
      return switch (pInstruction.getOpcode()) {
          case IADD -> tr.evaluate(TransferRelation.Operation.ADD, pValue1, pValue2);
          case ISUB -> tr.evaluate(TransferRelation.Operation.SUB, pValue1, pValue2);
          case IMUL -> tr.evaluate(TransferRelation.Operation.MUL, pValue1, pValue2);
          case IDIV -> tr.evaluate(TransferRelation.Operation.DIV, pValue1, pValue2);
          default -> SignValue.UNINITIALIZED_VALUE;
      };
  }

  /** {@inheritDoc} */
  @Override
  public SignValue ternaryOperation(
      final AbstractInsnNode pInstruction,
      final SignValue pValue1,
      final SignValue pValue2,
      final SignValue pValue3) {
    return null; // Nothing to do.
  }

  /** {@inheritDoc} */
  @Override
  public SignValue naryOperation(
      final AbstractInsnNode pInstruction, final List<? extends SignValue> pValues) {
    // TODO Implement me
    throw new UnsupportedOperationException("Implement me");
  }

  /** {@inheritDoc} */
  @Override
  public void returnOperation(
      final AbstractInsnNode pInstruction, final SignValue pValue, final SignValue pExpected) {
    // Nothing to do.
  }

  /** {@inheritDoc} */
  @Override
  public SignValue merge(final SignValue pValue1, final SignValue pValue2) {
    // TODO Implement me
    throw new UnsupportedOperationException("Implement me");
  }
}
