package br.gov.ce.pge.nova_divida_utils.annotations.logs;

import org.springframework.boot.logging.LogLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogAutomatico {

    /**
     * Descrição do método para ser apresentada no log.
     * */
    String descricao();

    /**
    * Lista de argumentos do método que serão apresentados no log.
    * */
    String[] argumentos() default {};

    /**
     * Indica se irá gravar o retorno do método ao logar a saída do método.
     * Se {@link #gravarSaida()} for {@code  false} essa configuração será ignorada.
     * */
    boolean gravarRetorno() default false;

    /**
     * Indica se irá gerar log ao sair do método.
     * */
    boolean gravarSaida() default true;

    /**
     * Define o nível de saída do log gerado pelo método. {@link org.springframework.boot.logging.LogLevel}
     * */
    LogLevel logLevel() default LogLevel.INFO;


}
