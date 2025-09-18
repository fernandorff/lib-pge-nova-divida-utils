package br.gov.ce.pge.nova_divida_utils.annotations.logs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Habilita o log automático e estruturado para a entrada, saída e exceções de um método.
 * <p>
 * Esta anotação, quando colocada em um método, instrui o aspecto de logging a gerar
 * logs detalhados, facilitando o rastreamento e a depuração do fluxo de execução.
 * O formato e o conteúdo do log podem ser personalizados através dos atributos desta anotação.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAutomatico {

    /**
     * Define a descrição funcional do método, que será exibida no log para facilitar o entendimento.
     *
     * @return A descrição do propósito do método.
     */
    String descricao();

    /**
     * Define uma lista com os nomes dos parâmetros que devem ser registrados no log de ENTRADA.
     * <p>
     * Por padrão, se a lista estiver vazia, <b>todos</b> os parâmetros do método serão registrados.
     * Se um ou mais nomes de parâmetros forem fornecidos, apenas os parâmetros com os nomes
     * especificados serão incluídos no log.
     *
     * @return Um array com os nomes dos parâmetros a serem logados.
     */
    String[] argumentosEntrada() default {};

    /**
     * Controla se o valor de retorno do método deve ser incluído no log de SAÍDA.
     * <p>
     * Este atributo só tem efeito se {@link #gravarSaidaMetodo()} for {@code true}.
     *
     * @return {@code true} para logar o valor de retorno, {@code false} caso contrário. O padrão é {@code true}.
     */
    boolean gravarRetornoMetodo() default true;

    /**
     * Controla se um log de SAÍDA deve ser gerado quando o método é concluído com sucesso.
     * <p>
     * Se definido como {@code false}, nenhum log de SAÍDA será gerado, e a configuração
     * de {@link #gravarRetornoMetodo()} será ignorada.
     *
     * @return {@code true} para gerar o log de saída, {@code false} caso contrário. O padrão é {@code true}.
     */
    boolean gravarSaidaMetodo() default true;

}