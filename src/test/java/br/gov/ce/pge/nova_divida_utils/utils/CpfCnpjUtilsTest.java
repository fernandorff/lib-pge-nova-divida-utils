package br.gov.ce.pge.nova_divida_utils.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfCnpjUtilsTest {

    @ParameterizedTest
    @CsvSource({"00000000191, 000.000.001-91",
                "12345678000100, 12.345.678/0001-00",
                "0, 000.000.000-00",
                "123456789123456789, 123.456.789-12"})
    void formatCpfCnpjValidosTest(String valorNaoFormatado, String formatado) {
        assertEquals(CpfCnpjUtils.formatCpfCnpj(valorNaoFormatado), formatado);
    }

    @ParameterizedTest
    @CsvSource({
            "aaaaaaaaaaa, aaaaaaaaaaa",
            "12345678OOO1OO, 12345678OOO1OO"})
    void formatCnpjInvalidosTest(String valorNaoFormatado, String formatado) {
        assertEquals(CpfCnpjUtils.formatCpfCnpj(valorNaoFormatado), formatado);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "000.000.001-91",
            "156.846.111-92",
            "013.163.591-31",
            "44.679.387/0001-20",
            "72.039.208/0001-25",
            "44.529.023/0001-63"})
    void verificaCpfCnpjValidos(String valorNaoFormatado) {
        assertTrue(CpfCnpjUtils.checkCPFCNPJ(valorNaoFormatado));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "            ",
            "              ",
            "",
            "853.085.059-94",
            "156.846.111-93",
            "013.163.591-32",
            "013.163.591-322",
            "013.163.591-3",
            "44.679.387/0001-21",
            "72.039.208/0001-26",
            "44.529.023/0001-64",
            "44.529.023/0001-644",
            "44.529.023/0001-6",
            "00000000000000"})
    void verificaCpfCnpjInvalidos(String valorNaoFormatado) {
        assertFalse(CpfCnpjUtils.checkCPFCNPJ(valorNaoFormatado));
    }



}
