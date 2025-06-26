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

    if(pLHS == SignValue.BOTTOM || pRHS == SignValue.BOTTOM)
        return SignValue.BOTTOM;

    return switch (pOperation){
        case ADD -> evaluateAdd(pLHS, pRHS);
        case SUB -> evaluateSub(pLHS, pRHS);
        case MUL -> evaluateMul(pLHS, pRHS);
        case DIV -> evaluateDiv(pLHS, pRHS);
        //Adding default just to make it exhaustive!
        default -> throw new IllegalArgumentException("Unsupported Operation");
    };
  }

    private SignValue evaluateDiv(SignValue left, SignValue right) {
        return null;
    }

    private SignValue evaluateMul(SignValue left, SignValue right) {
        return null;
    }

    private SignValue evaluateSub(SignValue left, SignValue right) {
        SignValue negatedRight = evaluate(Operation.NEG, right);
        return evaluateAdd(left, negatedRight);
    }

    private SignValue evaluateAdd(SignValue left, SignValue right) {
        return switch (left){
            case PLUS -> switch (right){
                case PLUS, ZERO, ZERO_PLUS -> SignValue.PLUS;
                case MINUS, ZERO_MINUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.BOTTOM;

            };
            case MINUS -> switch (right){
                case MINUS, ZERO, ZERO_MINUS -> SignValue.MINUS;
                case PLUS, ZERO_PLUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.BOTTOM;
            };
            case ZERO -> right;
            case ZERO_PLUS -> switch (right){
                case PLUS -> SignValue.PLUS;
                case ZERO, ZERO_PLUS -> SignValue.ZERO_PLUS;
                case MINUS, ZERO_MINUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.BOTTOM;
            };
            case ZERO_MINUS -> switch (right){
                case MINUS -> SignValue.MINUS;
                case ZERO, ZERO_MINUS -> SignValue.ZERO_MINUS;
                case PLUS, ZERO_PLUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.BOTTOM;
            };
            case PLUS_MINUS -> switch (right){
                case ZERO -> SignValue.PLUS_MINUS;
                default -> SignValue.TOP;
            };
            case TOP -> SignValue.TOP;
            default -> SignValue.BOTTOM;
        };
    }
}
