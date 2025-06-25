package de.uni_passau.fim.se2.sa.sign.interpretation;

import com.google.common.base.Preconditions;

public class SignTransferRelation implements TransferRelation {

  @Override
  public SignValue evaluate(final int pValue) {
    if (pValue < 0) return SignValue.MINUS;
    if (pValue > 0) return SignValue.PLUS;
    return SignValue.ZERO;
  }

  @Override
  public SignValue evaluate(final Operation pOperation, final SignValue pValue) {
    Preconditions.checkState(pOperation == Operation.NEG);
    Preconditions.checkNotNull(pValue);
    return switch (pValue){
      case BOTTOM -> SignValue.BOTTOM;
      case TOP -> SignValue.TOP;
      case MINUS -> SignValue.PLUS;
      case ZERO -> SignValue.ZERO;
      case PLUS -> SignValue.MINUS;
      case ZERO_MINUS -> SignValue.ZERO_PLUS;
      case ZERO_PLUS -> SignValue.ZERO_MINUS;
      case PLUS_MINUS -> SignValue.PLUS_MINUS;
      default -> throw new IllegalArgumentException("Invalid sign value");
    };
  }

  @Override
  public SignValue evaluate(
      final Operation pOperation, final SignValue pLHS, final SignValue pRHS) {
    Preconditions.checkState(
        pOperation == Operation.ADD
            || pOperation == Operation.SUB
            || pOperation == Operation.MUL
            || pOperation == Operation.DIV);
    Preconditions.checkNotNull(pLHS);
    Preconditions.checkNotNull(pRHS);

    // TODO Implement me
    throw new UnsupportedOperationException("Implement me");
  }
}
