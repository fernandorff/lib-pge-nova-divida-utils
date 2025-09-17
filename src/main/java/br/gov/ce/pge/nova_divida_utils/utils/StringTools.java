package br.gov.ce.pge.nova_divida_utils.utils;

import java.text.Normalizer;
import java.util.Arrays;

/**
 * Class utilitária usada para a manipulação de String<code>java.lang.String</code>
 */
public final class StringTools {

    private StringTools() {
        // Do nothing. Constutor protegito para evitar instanciação de classe utilitária.
    }

    /**
     * Converte para Long ou retorna null
     *
     * @param value
     * @return
     */
    public static Long toLong(String value) {
        try {
            return Long.valueOf( value.trim() );
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Cria uma string com o tamanho especificado, usando o caracter informado.
     */
    public static String fill(char ch, int length) {
        char[] chars = new char[length];
        Arrays.fill( chars, 0, chars.length, ch );
        return new String( chars );
    }

    /**
     * Completa a string com caracteres à esquerda se esta for menor que o tamanho máximo.
     *
     * @param str            String a ser completada.
     * @param length         Tamanho máximo da string.
     * @param completingChar Caractere utilizado para completar a string até que ela fique com o tamanho máximo.
     * @return String completada com _char at� o tamanho length.
     */
    public static String lPad(String str, int length, char completingChar) {
        str = (str == null ? "" : str);
        int strLength = str.length();
        if (strLength < length) {
            char[] chars = new char[length];
            Arrays.fill( chars, 0, length - strLength, completingChar );
            if (strLength > 0) {
                str.getChars( 0, strLength, chars, length - strLength );
            }
            return new String( chars );
        }
        return str;
    }

    /**
     * Completa a string com caracteres à direita se esta for menor que o tamanho máximo.
     *
     * @param str            String a ser completada.
     * @param length         Tamanho máximo da string.
     * @param completingChar Caractere utilizado para completar a string até que ela fique com o tamanho máximo.
     * @return String completada com _char até o tamanho length.
     */
    public static String rPad(String str, int length, char completingChar) {
        str = (str == null ? "" : str);
        int strLength = str.length();
        if (strLength < length) {
            char[] chars = new char[length];
            if (strLength > 0) {
                str.getChars( 0, strLength, chars, 0 );
            }
            Arrays.fill( chars, strLength, length, completingChar );
            return new String( chars );
        }
        return str;
    }

    /**
     * Remove os caracteres de uma string.
     *
     * @param str String a ser completada.
     * @return
     */
    public static String removeCharacters(final String str, char... characters) {
        StringBuilder ret = new StringBuilder( str );

        /* varrer o texto, procurando caracteres */
        int pos = -1;
        for (char character : characters) {
            while ((pos = ret.indexOf( String.valueOf( character ) )) != -1) {
                ret.deleteCharAt( pos );
            }
        }
        return ret.toString();
    }

	/**
	 * Normaliza uma String.
	 * @param str String a ser normalizada.
	 * @return a String normalizada.
	 */
    public static String normalize(String str) {
        if (str == null)
            return null;
        return Normalizer
                .normalize( str, Normalizer.Form.NFD )
                .replaceAll( "\\p{M}", "" );
    }

    /**
     * Remove toda a pontuação, exceto '.', '-' e ' '.
     * @param str com pontuação
     * @return String sem pontuação
     */
    public static String removePunctuationExcept(String str) {
        if (str == null)
            return null;
        return str.replaceAll( "[^a-zA-Z0-9.\\s-]", "" );
    }

}