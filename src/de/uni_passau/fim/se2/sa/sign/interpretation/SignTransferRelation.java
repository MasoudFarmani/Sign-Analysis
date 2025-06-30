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
      case MINUS -> SignValue.PLUS;
      case PLUS -> SignValue.MINUS;
      case ZERO_MINUS -> SignValue.ZERO_PLUS;
      case ZERO_PLUS -> SignValue.ZERO_MINUS;
      default -> pValue; // bottom/top/uninitialized/zero/plus_minus
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

    /**
     * I'm over approximating in the cases that the divisor definitely/maybe is ZERO,
     * ToDo: though I'm not sure about this approach, I'm keeping it -- Double Check!
     */
    private SignValue evaluateDiv(SignValue left, SignValue right) {
        switch (right){
            case ZERO, ZERO_MINUS, ZERO_PLUS, TOP -> {
                return SignValue.TOP;
            }
            default -> {
                return evaluateMul(left, right);
            }
        }
    }


    private SignValue evaluateMul(SignValue left, SignValue right) {
        return switch (left){
            case PLUS -> switch (right){
                case UNINITIALIZED_VALUE -> SignValue.TOP;
                default -> right;
            };
            case MINUS -> switch (right){
                case UNINITIALIZED_VALUE -> SignValue.TOP;
                default -> evaluate(Operation.NEG, right);
            };
            case ZERO -> switch (right){
                case UNINITIALIZED_VALUE -> SignValue.TOP;
                default -> SignValue.ZERO;
            };
            case ZERO_PLUS -> switch(right){
                case ZERO -> SignValue.ZERO;
                case PLUS, ZERO_PLUS -> SignValue.ZERO_PLUS;
                case MINUS, ZERO_MINUS -> SignValue.ZERO_MINUS;
                //case PLUS_MINUS, TOP, BOTTOM, ∅ -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case ZERO_MINUS -> switch (right){
                case ZERO -> SignValue.ZERO;
                case PLUS, ZERO_PLUS -> SignValue.ZERO_MINUS;
                case MINUS, ZERO_MINUS -> SignValue.ZERO_PLUS;
                //case PLUS_MINUS, TOP, BOTTOM, ∅ -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case PLUS_MINUS -> switch (right){
                case ZERO -> SignValue.ZERO;
                case PLUS, MINUS, PLUS_MINUS -> SignValue.PLUS_MINUS;
                //case ZERO_MINUS, ZERO_PLUS, TOP, BOTTOM, ∅ -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case TOP -> switch (right){
                case ZERO -> SignValue.ZERO;
                default -> SignValue.TOP;
            };
            default -> SignValue.TOP;
        };
    }

    private SignValue evaluateSub(SignValue left, SignValue right) {
        SignValue negatedRight = evaluate(Operation.NEG, right);
        return evaluateAdd(left, negatedRight);
    }

    private SignValue evaluateAdd(SignValue left, SignValue right) {
        return switch (left){
            case PLUS -> switch (right){
                case PLUS, ZERO, ZERO_PLUS -> SignValue.PLUS;
                //case MINUS, ZERO_MINUS, PLUS_MINUS, TOP, UNINITIALIZED_VALUE-> SignValue.TOP;
                default -> SignValue.TOP;

            };
            case MINUS -> switch (right){
                case MINUS, ZERO, ZERO_MINUS -> SignValue.MINUS;
                //case PLUS, ZERO_PLUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case ZERO -> switch (right){
                case UNINITIALIZED_VALUE -> SignValue.TOP;
                default -> right;
            };
            case ZERO_PLUS -> switch (right){
                case PLUS -> SignValue.PLUS;
                case ZERO, ZERO_PLUS -> SignValue.ZERO_PLUS;
                //case MINUS, ZERO_MINUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case ZERO_MINUS -> switch (right){
                case MINUS -> SignValue.MINUS;
                case ZERO, ZERO_MINUS -> SignValue.ZERO_MINUS;
                //case PLUS, ZERO_PLUS, PLUS_MINUS, TOP -> SignValue.TOP;
                default -> SignValue.TOP;
            };
            case PLUS_MINUS -> switch (right){
                case ZERO -> SignValue.PLUS_MINUS;
                default -> SignValue.TOP;
            };
            // left -> UNINITIALIZED_VALUE, TOP
            default -> SignValue.TOP;
        };
    }
}
