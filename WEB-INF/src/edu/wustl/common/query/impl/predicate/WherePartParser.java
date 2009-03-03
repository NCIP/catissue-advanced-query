/* Generated By:JavaCC: Do not edit this line. WherePartParser.java */
package edu.wustl.common.query.impl.predicate;

import java.io.*;


public class WherePartParser implements WherePartParserConstants {

        private PredicateGenerator predicateGenerator;
        private String forVariable;
        private StringBuilder xQueryWherePart = new StringBuilder();

        public WherePartParser(String wherePart, PredicateGenerator predicateGenerator)
        {
                this(new StringReader(wherePart));
                this.predicateGenerator = predicateGenerator;
                xQueryWherePart = new StringBuilder();
        }


        public static void main(String[] args) throws ParseException, FileNotFoundException
        {
                InputStream in = new FileInputStream("WherePart7.txt");

                WherePartParser parser = new WherePartParser(in);
                parser.parse();

        }

  final public void parse() throws ParseException, ParseException {
        Token t = null;
    ConditionTree();
    jj_consume_token(0);
                //System.out.println("CONDITON PARSED SUCCESSFULLY !!");
                //System.out.println(xQueryWherePart);
                predicateGenerator.setXQueryWherePart(xQueryWherePart);
  }

  final private void ConditionTree() throws ParseException {
    if (jj_2_3(3)) {
      ConditionsOnOneEntity();
    } else if (jj_2_4(3)) {
      ParenthesizedConditionsOnOneEntity();
      label_1:
      while (true) {
        if (jj_2_1(3)) {
          ;
        } else {
          break label_1;
        }
        jj_consume_token(LOGICAL_OPERATOR);
        ConditionsOnAChild();
      }
    } else if (jj_2_5(3)) {
      ConditionsOnAChild();
      label_2:
      while (true) {
        if (jj_2_2(3)) {
          ;
        } else {
          break label_2;
        }
        jj_consume_token(LOGICAL_OPERATOR);
        ConditionsOnAChild();
      }
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final private void ParenthesizedConditionsOnOneEntity() throws ParseException {
    jj_consume_token(OPENING_PARENTHESIS);
    ConditionsOnOneEntity();
    jj_consume_token(CLOSING_PARENTHESIS);
  }

  final private void ConditionsOnOneEntity() throws ParseException {
        AbstractPredicate predicate = null;
    predicate = AtomicCondition();
                if(predicate != null)
                {
                        predicateGenerator.addPredicate(forVariable, predicate);
                }
    label_3:
    while (true) {
      if (jj_2_6(3)) {
        ;
      } else {
        break label_3;
      }
      jj_consume_token(LOGICAL_OPERATOR);
      predicate = AtomicCondition();
                        if(predicate != null)
                        {
                                predicateGenerator.addPredicate(forVariable, predicate);
                        }
    }
  }

  final private AbstractPredicate AtomicCondition() throws ParseException {
        AbstractPredicate predicate = null;
    if (jj_2_7(3)) {
      predicate = PrefixUnaryCondition();
                {if (true) return predicate;}
    } else if (jj_2_8(3)) {
      predicate = PrefixBinaryCondition();
                {if (true) return predicate;}
    } else if (jj_2_9(3)) {
      predicate = InfixCondition();
                {if (true) return predicate;}
    } else if (jj_2_10(3)) {
      predicate = NegationCondition();
                {if (true) return predicate;}
    } else if (jj_2_11(3)) {
      predicate = SelfPredicateCondition();
                {if (true) return predicate;}
    } else if (jj_2_12(3)) {
      TemporalCondition();
                {if (true) return null;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private void TemporalCondition() throws ParseException {
        Token t = null;
    jj_consume_token(TEMPORAL_CONDITION_OPEN);
    label_4:
    while (true) {
      if (jj_2_13(3)) {
        t = jj_consume_token(CHAR);
                        xQueryWherePart.append(t.image);
      } else if (jj_2_14(3)) {
        jj_consume_token(TEMPORAL_CONDITION_CLOSE);
                        xQueryWherePart.append(" and ");
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
      if (jj_2_15(3)) {
        ;
      } else {
        break label_4;
      }
    }
  }

  final private AbstractPredicate SelfPredicateCondition() throws ParseException {
        Token conditionAttribute = null;
        Token operator = null;
    conditionAttribute = jj_consume_token(CONDITION_ATTRIBUTE);
    operator = jj_consume_token(READYMADE_PREDICATE);
                        int separator = conditionAttribute.image.indexOf("/");
                        forVariable = conditionAttribute.image.substring(0, separator);
                        String attribute = conditionAttribute.image.substring(separator+1);
                        AbstractPredicate predicate = new SelfPredicate(attribute, operator.image);
                        {if (true) return predicate;}
    throw new Error("Missing return statement in function");
  }

  final private AbstractPredicate PrefixUnaryCondition() throws ParseException {
        Token conditionAttribute = null;
        Token operator = null;
    operator = jj_consume_token(PREFIX_UNARY_OPERATOR);
    conditionAttribute = jj_consume_token(CONDITION_ATTRIBUTE);
    jj_consume_token(CLOSING_PARENTHESIS);
                        int separator = conditionAttribute.image.indexOf("/");
                        forVariable = conditionAttribute.image.substring(0, separator);
                        String attribute = conditionAttribute.image.substring(separator+1);
                        AbstractPredicate predicate = new PrefixUnaryPredicate(attribute, operator.image);
                        {if (true) return predicate;}
    throw new Error("Missing return statement in function");
  }

  final private AbstractPredicate PrefixBinaryCondition() throws ParseException {
        Token conditionAttribute = null;
        Token operator = null;
        String rhs = null;
    operator = jj_consume_token(PREFIX_BINARY_OPERATOR);
    conditionAttribute = jj_consume_token(CONDITION_ATTRIBUTE);
    jj_consume_token(CLOSING_PARENTHESIS);
    jj_consume_token(COMMA);
    rhs = RHS();
    jj_consume_token(CLOSING_PARENTHESIS);
                int separator = conditionAttribute.image.indexOf("/");
                forVariable = conditionAttribute.image.substring(0, separator);
                String attribute = conditionAttribute.image.substring(separator+1);
                AbstractPredicate predicate = new PrefixBinaryPredicate(attribute, operator.image, rhs);
                {if (true) return predicate;}
    throw new Error("Missing return statement in function");
  }

  final private AbstractPredicate InfixCondition() throws ParseException {
        Token conditionAttribute = null;
        Token operator = null;
        String rhs = null;
    conditionAttribute = jj_consume_token(CONDITION_ATTRIBUTE);
    operator = jj_consume_token(INFIX_OPERATOR);
    rhs = RHS();
                int separator = conditionAttribute.image.indexOf("/");
                forVariable = conditionAttribute.image.substring(0, separator);
                String attribute = conditionAttribute.image.substring(separator+1);
                AbstractPredicate predicate = new InfixPredicate(attribute, operator.image, rhs);
                {if (true) return predicate;}
    throw new Error("Missing return statement in function");
  }

  final private AbstractPredicate NegationCondition() throws ParseException {
        AbstractPredicate predicate = null;
    jj_consume_token(23);
    predicate = AtomicCondition();
    jj_consume_token(CLOSING_PARENTHESIS);
                AbstractPredicate negationPredicate = new NegationPredicate(predicate);
                {if (true) return negationPredicate;}
    throw new Error("Missing return statement in function");
  }

  final private void ConditionsOnAChild() throws ParseException {
    jj_consume_token(OPENING_PARENTHESIS);
    ConditionTree();
    jj_consume_token(CLOSING_PARENTHESIS);
  }

  final private String RHS() throws ParseException {
        Token rhsToken = null;
        String rhs = null;
    if (jj_2_16(3)) {
      rhsToken = jj_consume_token(FUNCTION_CALL);
                {if (true) return rhsToken.image;}
    } else if (jj_2_17(3)) {
      rhsToken = jj_consume_token(CONSTANT);
                {if (true) return rhsToken.image;}
    } else if (jj_2_18(3)) {
      rhsToken = jj_consume_token(CONDITION_ATTRIBUTE);
                {if (true) return rhsToken.image;}
    } else if (jj_2_19(3)) {
      rhs = CSV();
                {if (true) return rhs;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final private String CSV() throws ParseException {
        StringBuilder rhs = new StringBuilder();
        Token constant = null;
    jj_consume_token(OPENING_PARENTHESIS);
                rhs.append('(');
    constant = jj_consume_token(CONSTANT);
                rhs.append(constant.image);
    label_5:
    while (true) {
      if (jj_2_20(3)) {
        ;
      } else {
        break label_5;
      }
      jj_consume_token(COMMA);
      constant = jj_consume_token(CONSTANT);
                        rhs.append(',').append(constant.image);
    }
    jj_consume_token(CLOSING_PARENTHESIS);
                rhs.append(')');
                {if (true) return rhs.toString();}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_11(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_12(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_13(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_2_14(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_14(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  private boolean jj_2_15(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_15(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  private boolean jj_2_16(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_16(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(15, xla); }
  }

  private boolean jj_2_17(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_17(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(16, xla); }
  }

  private boolean jj_2_18(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_18(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(17, xla); }
  }

  private boolean jj_2_19(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_19(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(18, xla); }
  }

  private boolean jj_2_20(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_20(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(19, xla); }
  }

  private boolean jj_3_19() {
    if (jj_3R_16()) return true;
    return false;
  }

  private boolean jj_3_14() {
    if (jj_scan_token(TEMPORAL_CONDITION_CLOSE)) return true;
    return false;
  }

  private boolean jj_3R_13() {
    if (jj_scan_token(23)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3_9() {
    if (jj_3R_12()) return true;
    return false;
  }

  private boolean jj_3_18() {
    if (jj_scan_token(CONDITION_ATTRIBUTE)) return true;
    return false;
  }

  private boolean jj_3_13() {
    if (jj_scan_token(CHAR)) return true;
    return false;
  }

  private boolean jj_3_15() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_13()) {
    jj_scanpos = xsp;
    if (jj_3_14()) return true;
    }
    return false;
  }

  private boolean jj_3R_10() {
    if (jj_scan_token(PREFIX_UNARY_OPERATOR)) return true;
    if (jj_scan_token(CONDITION_ATTRIBUTE)) return true;
    if (jj_scan_token(CLOSING_PARENTHESIS)) return true;
    return false;
  }

  private boolean jj_3R_8() {
    if (jj_scan_token(OPENING_PARENTHESIS)) return true;
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3_8() {
    if (jj_3R_11()) return true;
    return false;
  }

  private boolean jj_3_17() {
    if (jj_scan_token(CONSTANT)) return true;
    return false;
  }

  private boolean jj_3R_15() {
    if (jj_scan_token(TEMPORAL_CONDITION_OPEN)) return true;
    Token xsp;
    if (jj_3_15()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_15()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_20() {
    if (jj_scan_token(COMMA)) return true;
    if (jj_scan_token(CONSTANT)) return true;
    return false;
  }

  private boolean jj_3R_9() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_7()) {
    jj_scanpos = xsp;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3_9()) {
    jj_scanpos = xsp;
    if (jj_3_10()) {
    jj_scanpos = xsp;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) return true;
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_2() {
    if (jj_scan_token(LOGICAL_OPERATOR)) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_7() {
    if (jj_3R_10()) return true;
    return false;
  }

  private boolean jj_3R_18() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_16()) {
    jj_scanpos = xsp;
    if (jj_3_17()) {
    jj_scanpos = xsp;
    if (jj_3_18()) {
    jj_scanpos = xsp;
    if (jj_3_19()) return true;
    }
    }
    }
    return false;
  }

  private boolean jj_3_16() {
    if (jj_scan_token(FUNCTION_CALL)) return true;
    return false;
  }

  private boolean jj_3R_12() {
    if (jj_scan_token(CONDITION_ATTRIBUTE)) return true;
    if (jj_scan_token(INFIX_OPERATOR)) return true;
    if (jj_3R_18()) return true;
    return false;
  }

  private boolean jj_3_5() {
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_1() {
    if (jj_scan_token(LOGICAL_OPERATOR)) return true;
    if (jj_3R_6()) return true;
    return false;
  }

  private boolean jj_3_4() {
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_6() {
    if (jj_scan_token(LOGICAL_OPERATOR)) return true;
    if (jj_3R_9()) return true;
    return false;
  }

  private boolean jj_3R_17() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_3()) {
    jj_scanpos = xsp;
    if (jj_3_4()) {
    jj_scanpos = xsp;
    if (jj_3_5()) return true;
    }
    }
    return false;
  }

  private boolean jj_3_3() {
    if (jj_3R_7()) return true;
    return false;
  }

  private boolean jj_3_12() {
    if (jj_3R_15()) return true;
    return false;
  }

  private boolean jj_3R_6() {
    if (jj_scan_token(OPENING_PARENTHESIS)) return true;
    if (jj_3R_17()) return true;
    return false;
  }

  private boolean jj_3R_16() {
    if (jj_scan_token(OPENING_PARENTHESIS)) return true;
    if (jj_scan_token(CONSTANT)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_20()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(CLOSING_PARENTHESIS)) return true;
    return false;
  }

  private boolean jj_3_11() {
    if (jj_3R_14()) return true;
    return false;
  }

  private boolean jj_3R_14() {
    if (jj_scan_token(CONDITION_ATTRIBUTE)) return true;
    if (jj_scan_token(READYMADE_PREDICATE)) return true;
    return false;
  }

  private boolean jj_3R_11() {
    if (jj_scan_token(PREFIX_BINARY_OPERATOR)) return true;
    if (jj_scan_token(CONDITION_ATTRIBUTE)) return true;
    if (jj_scan_token(CLOSING_PARENTHESIS)) return true;
    return false;
  }

  private boolean jj_3R_7() {
    if (jj_3R_9()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_6()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_10() {
    if (jj_3R_13()) return true;
    return false;
  }

  /** Generated Token Manager. */
  public WherePartParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[20];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public WherePartParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public WherePartParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new WherePartParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public WherePartParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new WherePartParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public WherePartParser(WherePartParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(WherePartParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 0; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List jj_expentries = new java.util.ArrayList();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[24];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 0; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 24; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 20; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
            case 10: jj_3_11(); break;
            case 11: jj_3_12(); break;
            case 12: jj_3_13(); break;
            case 13: jj_3_14(); break;
            case 14: jj_3_15(); break;
            case 15: jj_3_16(); break;
            case 16: jj_3_17(); break;
            case 17: jj_3_18(); break;
            case 18: jj_3_19(); break;
            case 19: jj_3_20(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
