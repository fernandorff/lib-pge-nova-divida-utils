package br.gov.ce.pge.nova_divida_utils.utils;

import java.text.ParseException;

/**
 * Classe utilitári com as regras de validação de cpf/cnpj's.
 */
public final class CpfCnpjUtils {
	
    /** Quantidade de números em um documento cpf. */
    public static final int CPF_DOC_LENGTH = 11;
    /** Quantidade de números em um documento cnpj. */
    public static final int CNPJ_DOC_LENGTH = 14;
    /** Caracteres utilizados na formatação de cpf's e cnpj's. */
    private static final char[] DOC_FORMATING_CHARACTERS = new char[] {' ', '.', '-', '/'};

    /** Máscara de cpf. */
    private static final String CPF_MASK = "###.###.###-##";

    /** Máscara de cnpj. */
    private static final String CNPJ_MASK = "##.###.###/####-##";

    private CpfCnpjUtils() {
    	// Do nothing. Constutor protegito para evitar instanciação de classe utilitária.
    }

    /**
     * Formata um cpf/cnpj.<BR>
     * String com tamanho 14 sero consideras cnpj e as demais cpf. Caso ocorra algum erro de parser, o cpf
     * sem formatao ser retornado.
     * @param unformattedCpfCnpj Cpf/Cnjp sem caracteres de formatao.
     * @return cpf/cnpj formatado ou valor original em caso de erro de parser.
     */
    public static String formatCpfCnpj(final String unformattedCpfCnpj) {
        if (unformattedCpfCnpj.length() == CpfCnpjUtils.CNPJ_DOC_LENGTH) {
            return CpfCnpjUtils.formatCnpj(unformattedCpfCnpj);
        }
        return CpfCnpjUtils.formatCpf(unformattedCpfCnpj);
    }

    /**
     * Formata um cpf.<BR>
     * Caso ocorra algum erro de parser, o cpf sem formatao ser retornado.
     * @param unformattedCpf Cpf sem caracteres de formatao.
     * @return cpf formatado ou valor original em caso de erro de parser.
     */
    public static String formatCpf(final String unformattedCpf) {
        try {
            return CpfCnpjUtils.formatString(StringTools.lPad(unformattedCpf, CpfCnpjUtils.CPF_DOC_LENGTH, '0'), CpfCnpjUtils.CPF_MASK);
        } catch (ParseException e) {
            return unformattedCpf;
        }
    }

    /**
     * Formata um cpf.<BR>
     * Caso ocorra algum erro de parser, o cpf sem formatao ser retornado.
     * @param unformattedCnpj Cnpj sem caracteres de formatao.
     * @return cpf formatado ou valor original em caso de erro de parser.
     */
    public static String formatCnpj(final String unformattedCnpj) {
        try {
            return CpfCnpjUtils.formatString(StringTools.lPad(unformattedCnpj, CpfCnpjUtils.CNPJ_DOC_LENGTH, '0'), CpfCnpjUtils.CNPJ_MASK);
        } catch (ParseException e) {
            return unformattedCnpj;
        }
    }

    /**
     * Valida se um cpf ou um cnpj é válido. O tamanho do número será utilizado para determinar o tipo do
     * documento. Este método ignora caracteres de formatao dos documentos, portando a string passada pode
     * conter ambos: somente números ou o números com a formatao padro do documento.
     * @param cpfCnpj Número de cpf ou cnpj.
     * @return <code>true</code> se o número for um cpf ou cnpj válido, <code>false</code> caso contrário.
     */
    public static boolean checkCPFCNPJ(String cpfCnpj) {
    	if (isCPFLength(cpfCnpj)) {
    		return checkCPF(cpfCnpj);
    	} else if(isCNPJLength(cpfCnpj)) {
    		return checkCNPJ(cpfCnpj);
    	} else return false;
    }
    
    public static boolean isCPFLength(String value) {
    	if (value == null) return false;
    	value = StringTools.removeCharacters(value, CpfCnpjUtils.DOC_FORMATING_CHARACTERS);
    	return value.length() == CpfCnpjUtils.CPF_DOC_LENGTH; 
    }
    
    public static boolean isCNPJLength(String value) {
    	if (value == null) return false;
    	value = StringTools.removeCharacters(value, CpfCnpjUtils.DOC_FORMATING_CHARACTERS);
    	return value.length() == CpfCnpjUtils.CNPJ_DOC_LENGTH; 
    }

    /**
     * Valida se um cpf é válido. O tamanho do número será utilizado para determinar o tipo do documento. Este
     * método ignora caracteres de formatação dos documentos, portando a string passada pode conter ambos:
     * somente números ou o números com a formatação padrao do documento.
     * @param cpf Número de cpf.
     * @return <code>true</code> se o cpf for válido, <code>false</code> caso contrário.
     */
    public static boolean checkCPF(String cpf) {
        if (cpf != null) {
            cpf = StringTools.removeCharacters(cpf, CpfCnpjUtils.DOC_FORMATING_CHARACTERS);

            return cpf.length() == CpfCnpjUtils.CPF_DOC_LENGTH && StringTools.toLong(cpf) != null && CpfCnpjUtils.isCPF(cpf);
        }

        return false;
    }

    /**
     * Valida se um cnpj é válido. O tamanho do nmero ser utilizado para determinar o tipo do documento.
     * Este método ignora caracteres de formatação dos documentos, portando a string passada pode conter
     * ambos: somente números ou o números com a formatação padrão do documento.
     * @param cnpj Número de cnpj.
     * @return <code>true</code> se o cnpj for válido, <code>false</code> caso contrário.
     */
    public static boolean checkCNPJ(String cnpj) {
        if (cnpj != null) {
            cnpj = StringTools.removeCharacters(cnpj, CpfCnpjUtils.DOC_FORMATING_CHARACTERS);

            return cnpj.length() == CpfCnpjUtils.CNPJ_DOC_LENGTH && StringTools.toLong(cnpj) != null && CpfCnpjUtils.isCNPJ(cnpj);
        }

        return false;
    }

    /**
     * Verifica se o número passado é um cpf.
     * @param cpf Número do cpf.
     * @return <code>true</code> se o número for um cpf válido, <code>false</code> caso contrário.
     */
    private static boolean isCPF(String cpf) {
        if (cpf.length() != CpfCnpjUtils.CPF_DOC_LENGTH) {
            return false;
        }

        String rcpf1 = cpf.substring(0, 9);
        String rcpf2 = cpf.substring(9);

        int d1 = 0;
        for (int i = 0; i < 9; i++) {
            d1 += Integer.parseInt(rcpf1.substring(i, i + 1)) * (10 - i);
        }

        d1 = 11 - (d1 % 11);

        if (d1 > 9) {
            d1 = 0;
        }

        if (Integer.parseInt(rcpf2.substring(0, 1)) != d1) {
            return false;
        }

        d1 *= 2;
        for (int i = 0; i < 9; i++) {
            d1 += Integer.parseInt(rcpf1.charAt(i) + "") * (11 - i);
        }

        d1 = 11 - (d1 % 11);
        if (d1 > 9) {
            d1 = 0;
        }

        if (Integer.parseInt(rcpf2.charAt(1) + "") != d1) {
            return false;
        }

        // Verifica se o cpf contém todos os dígitos iguais.
        boolean contemNumeroIguais = StringTools.toLong(cpf) - StringTools.toLong(StringTools.fill(cpf.charAt(0), cpf.length())) == 0; 
        return !contemNumeroIguais;
    }

    /**
     * Verifica se o número passado  um cnpj.
     * @param cnpj Número do cnpj.
     * @return <code>true</code> se o número for um cnpj válido, <code>false</code> caso contrário.
     */
    private static boolean isCNPJ(String cnpj) {
        StringBuilder cnpjTMP = new StringBuilder();
        char ch;
        for (int x = 1; x <= cnpj.length(); x++) {
            ch = cnpj.charAt(x - 1);
            cnpjTMP.append(ch);
        }

        cnpj = cnpjTMP.toString();

        if (cnpj.length() != CpfCnpjUtils.CNPJ_DOC_LENGTH) {
            return false;
        } else if (cnpj.equals("00000000000000")) {
            return false;
        } else {
            int[] numero = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            numero[1] = Integer.parseInt(cnpj.substring(0, 1));
            numero[2] = Integer.parseInt(cnpj.substring(1, 2));
            numero[3] = Integer.parseInt(cnpj.substring(2, 3));
            numero[4] = Integer.parseInt(cnpj.substring(3, 4));
            numero[5] = Integer.parseInt(cnpj.substring(4, 5));
            numero[6] = Integer.parseInt(cnpj.substring(5, 6));
            numero[7] = Integer.parseInt(cnpj.substring(6, 7));
            numero[8] = Integer.parseInt(cnpj.substring(7, 8));
            numero[9] = Integer.parseInt(cnpj.substring(8, 9));
            numero[10] = Integer.parseInt(cnpj.substring(9, 10));
            numero[11] = Integer.parseInt(cnpj.substring(10, 11));
            numero[12] = Integer.parseInt(cnpj.substring(11, 12));
            numero[13] = Integer.parseInt(cnpj.substring(12, 13));
            numero[14] = Integer.parseInt(cnpj.substring(13, 14));

            int soma = numero[1] * 5 + numero[2] * 4 + numero[3] * 3 + numero[4] * 2 + numero[5] * 9 + numero[6] * 8 + numero[7] * 7 + numero[8] * 6 + numero[9] * 5 + numero[10] * 4 + numero[11] * 3 + numero[12] * 2;

            soma = soma - (11 * (soma / 11));
            int resultado1;
            int resultado2;
            
            if (soma == 0 || soma == 1) {
                resultado1 = 0;
            } else {
                resultado1 = 11 - soma;
            }
            
            if (resultado1 == numero[13]) {
                soma = numero[1] * 6 + numero[2] * 5 + numero[3] * 4 + numero[4] * 3 + numero[5] * 2 + numero[6] * 9 + numero[7] * 8 + numero[8] * 7 + numero[9] * 6 + numero[10] * 5 + numero[11] * 4 + numero[12] * 3 + numero[13] * 2;
                soma = soma - (11 * (soma / 11));
                if (soma == 0 || soma == 1) {
                    resultado2 = 0;
                } else {
                    resultado2 = 11 - soma;
                }
                
                return resultado2 == numero[14];
            }
            return false;
        }
    }

    /**
     * Formata uma string.
     * @param value String a ser formatado.
     * @param mask Máscara.
     * @return Valor formatado.
     * @throws ParseException Caso ocorra um erro ao fazer o parser.
     */
    private static String formatString(String value, String mask) throws ParseException {
        javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter(mask);
        mf.setValueContainsLiteralCharacters(false);
        return mf.valueToString(value);
    }    
    
}