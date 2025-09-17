package br.gov.ce.pge.nova_divida_utils.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StringToolsTest {

  private static final String TEXT_STRING = "text string";
  private static final String ABCDE_STRING = "abcde";
  private static final String AEIOU_STRING = "aeiou";
  private static final String ZERO_STRING = "00000";

  @Test
  void testToLong() {
    // Test for valid string that can be converted to long
    assertEquals(12345L, StringTools.toLong("12345"));

    // Test for string that cannot be converted to long
    assertNull(StringTools.toLong(TEXT_STRING));

    // Test for null
    assertNull(StringTools.toLong(null));
  }

  @Test
  void testFill() {
    // Test for positive length
    assertEquals(ZERO_STRING, StringTools.fill('0', 5));

    // Test for zero length
    assertEquals("", StringTools.fill('0', 0));
  }

  @Test
  void testLPad() {
    // Test for string shorter than length
    assertEquals("00abc", StringTools.lPad("abc", 5, '0'));

    // Test for string equal to length
    assertEquals(ABCDE_STRING, StringTools.lPad(ABCDE_STRING, 5, '0'));

    // Test for string longer than length
    assertEquals(TEXT_STRING, StringTools.lPad(TEXT_STRING, 5, '0'));

    // Test for null string
    assertEquals(ZERO_STRING, StringTools.lPad(null, 5, '0'));
  }

  @Test
  void testRPad() {
    // Test for string shorter than length
    assertEquals("abc00", StringTools.rPad("abc", 5, '0'));

    // Test for string equal to length
    assertEquals(ABCDE_STRING, StringTools.rPad(ABCDE_STRING, 5, '0'));

    // Test for string longer than length
    assertEquals(TEXT_STRING, StringTools.rPad(TEXT_STRING, 5, '0'));

    // Test for null string
    assertEquals(ZERO_STRING, StringTools.rPad(null, 5, '0'));
  }

  @Test
  void testRemoveCharacters() {
    // Test removing single character
    assertEquals("bcdef", StringTools.removeCharacters("abcdef", 'a'));

    // Test removing multiple characters
    assertEquals(AEIOU_STRING, StringTools.removeCharacters("abcdefghijklmnopqrstuvwxyz", 'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'));

    // Test removing character that does not exist in string
    assertEquals(TEXT_STRING, StringTools.removeCharacters(TEXT_STRING, 'z'));

    // Test removing null character
    assertEquals(TEXT_STRING, StringTools.removeCharacters(TEXT_STRING));
  }

  @ParameterizedTest
  @MethodSource("gerarValoresAcento")
  void testNormalize(String entrada, String saida) {
    assertEquals(saida, StringTools.normalize(entrada));
  }

  public static Stream<Arguments> gerarValoresAcento() {
    return Stream.of(
            Arguments.arguments( "áéióú", AEIOU_STRING),
            Arguments.arguments( "abçdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyz"),
            Arguments.arguments( "ãõ", "ao"),
            Arguments.arguments( "âêîôû", AEIOU_STRING),
            Arguments.arguments( "äëïöü", AEIOU_STRING)
    );
  }

  @ParameterizedTest
  @MethodSource("gerarValoresAcento")
  void removePunctuationTest(String entrada, String saida) {
    assertEquals(saida, StringTools.removePunctuationExcept(StringTools.normalize(entrada)));
  }

  public static Stream<Arguments> gerarValoresPontuacao() {
    return Stream.of(
            Arguments.arguments( "ã~e~iõ~u´´´´´`````", AEIOU_STRING),
            Arguments.arguments( "%¨&*&¨%$¨&*&¨%¨&*+_)(*&¨%$#@!@#$%¨&.", "."),
            Arguments.arguments( "?°?°?²³£¢°¬¢?°°?³³°.-", ".-"),
            Arguments.arguments( "T&xto d& &x&mplo - t&$t&.", "Txto d xmplo - tt.")
    );
  }

}
