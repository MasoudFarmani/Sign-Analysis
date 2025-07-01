package de.uni_passau.fim.se2.sa.sign;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import de.uni_passau.fim.se2.sa.sign.interpretation.SignInterpreter;
import de.uni_passau.fim.se2.sa.sign.interpretation.SignValue;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public class SignAnalysisImpl implements SignAnalysis {

  @Override
  public SortedSetMultimap<Integer, AnalysisResult> analyse(
      final String pClassName, final String pMethodName) throws AnalyzerException, IOException {

    String[] parts = pMethodName.split(":");
    String methodName = parts[0];
    String methodDesc = parts[1];

    String classPath = pClassName.replace('.', '/') + ".class";
    InputStream classStream = getClass().getClassLoader().getResourceAsStream(classPath);
    ClassReader classReader = new ClassReader(classStream);
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);

    MethodNode targetMethod = null;
    Map<String, MethodNode> methods = new HashMap<>();

    for (MethodNode method : classNode.methods) {
      String key = method.name + method.desc;
      methods.put(key, method);
      if (method.name.equals(methodName) && method.desc.equals(methodDesc)) {
        targetMethod = method;
      }
    }
    SignInterpreter interpreter = new SignInterpreter(pClassName, methods);
    Analyzer<SignValue> analyzer = new Analyzer<>(interpreter);
    analyzer.analyze(pClassName, targetMethod);

    Frame<SignValue>[] frames = analyzer.getFrames();
    AbstractInsnNode[] instructions = targetMethod.instructions.toArray();

    List<Pair<AbstractInsnNode, Frame<SignValue>>> pairs = new ArrayList<>();
    for (int i = 0; i < instructions.length; i++) {
      if (frames[i] != null) {
        pairs.add(new Pair<>(instructions[i], frames[i]));
      }
    }

    return extractAnalysisResults(pairs);
  }

  /**
   * Extracts the analysis results from the given pairs of instructions and frames.
   *
   * <p>The result is a {@link SortedSetMultimap} that maps line numbers to the analysis results.
   * For each line number, there can be multiple analysis results.  The method expects a list of
   * pairs of instructions and frames.  The instructions are expected to be in the same order as
   * they are in the method.  The frames are expected to be the frames that are computed for the
   * instructions.  The method will extract the analysis results from the frames and map them to
   * the line numbers of the instructions.
   *
   * @param pPairs The pairs of instructions and frames.
   * @return The analysis results.
   */
  private SortedSetMultimap<Integer, AnalysisResult> extractAnalysisResults(
      final List<Pair<AbstractInsnNode, Frame<SignValue>>> pPairs) {
    final SortedSetMultimap<Integer, AnalysisResult> result = TreeMultimap.create();
    int lineNumber = -1;

    for (final Pair<AbstractInsnNode, Frame<SignValue>> pair : pPairs) {
      final AbstractInsnNode instruction = pair.key();
      final Frame<SignValue> frame = pair.value();
      if (instruction instanceof LineNumberNode lineNumberNode) {
        lineNumber = lineNumberNode.line;
      }

      if (isDivByZero(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.DIVISION_BY_ZERO);
      } else if (isMaybeDivByZero(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.MAYBE_DIVISION_BY_ZERO);
      }

      if (isNegativeArrayIndex(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.NEGATIVE_ARRAY_INDEX);
      } else if (isMaybeNegativeArrayIndex(instruction, frame)) {
        result.put(lineNumber, AnalysisResult.MAYBE_NEGATIVE_ARRAY_INDEX);
      }
    }

    return result;
  }

  private boolean isDivByZero(final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IDIV) {
      SignValue divisor = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isZero(divisor);
    }
    return false;
  }

  private boolean isMaybeDivByZero(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IDIV) {
      SignValue divisor = pFrame.getStack(pFrame.getStackSize() - 1);
      return SignValue.isMaybeZero(divisor) && !SignValue.isZero(divisor);
    }
    return false;
  }

  /*
  For IALOAD: stack has [..., index]
  For IASTORE: stack has [..., index, value]
   */
  private boolean isNegativeArrayIndex(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IALOAD || pInstruction.getOpcode() == Opcodes.IASTORE) {
      SignValue index = pFrame.getStack(pFrame.getStackSize() - (pInstruction.getOpcode() == Opcodes.IASTORE ? 2 : 1));
      return SignValue.isNegative(index);
    }
    return false;
  }

  private boolean isMaybeNegativeArrayIndex(
      final AbstractInsnNode pInstruction, final Frame<SignValue> pFrame) {
    if (pInstruction.getOpcode() == Opcodes.IALOAD || pInstruction.getOpcode() == Opcodes.IASTORE) {
      SignValue index = pFrame.getStack(pFrame.getStackSize() - (pInstruction.getOpcode() == Opcodes.IASTORE ? 2 : 1));
      return SignValue.isMaybeNegative(index) && !SignValue.isNegative(index);
    }
    return false;
  }

  private record Pair<K, V>(K key, V value) {

    @Override
    public String toString() {
      return "Pair{key=" + key + ", value=" + value + '}';
    }
  }
}
