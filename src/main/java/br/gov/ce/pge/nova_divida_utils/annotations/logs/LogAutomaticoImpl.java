package br.gov.ce.pge.nova_divida_utils.annotations.logs;

import br.gov.ce.pge.nova_divida_utils.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Aspect
@Component
public class LogAutomaticoImpl {

    @Around("@annotation(br.gov.ce.pge.nova_divida_utils.annotations.logs.LogAutomatico)")
    public Object logPadrao(ProceedingJoinPoint joinPoint) throws Throwable {
        gerarLogEntrada(joinPoint);
        Object retorno = joinPoint.proceed();
        gerarLogSaida(joinPoint, retorno);
        return retorno;
    }

    private void gerarLogEntrada(ProceedingJoinPoint joinPoint) {
        try {
            Class<?> aClass = joinPoint.getThis().getClass();
            String nomeClasse = aClass.getSimpleName();
            var canonicalName = aClass.getCanonicalName();
            var name = aClass.getName();
            var descriptorString = aClass.descriptorString();
            String methodName = joinPoint.getSignature().getName();
            Optional<String> descricaoMethod = getDescricaoMethod(joinPoint);
            Object[] argumentos = getArgumentos(joinPoint);
            if (argumentos.length == 0)
                gerarLog(nomeClasse, methodName, "ENTRADA", descricaoMethod);
            else {
                gerarLog(nomeClasse, methodName, "ENTRADA", descricaoMethod, argumentos);
            }
        } catch (Exception ex) {
            log.error("ERRO NA GERACAO DO LOG DE ENTRADA", ex);
        }
    }

    private void gerarLogSaida(ProceedingJoinPoint joinPoint, Object retorno) {
        try {
            if (this.gravarSaidaMetodo(joinPoint)){
                String nomeClasse = joinPoint.getThis().getClass().getSimpleName();
                String methodName = joinPoint.getSignature().getName();
                Optional<String> descricaoMethod = this.getDescricaoMethod(joinPoint);

                if (this.gravarRetorno(joinPoint)) {

                    this.gerarLog(nomeClasse, methodName, "SAIDA", descricaoMethod, retorno);
                } else {
                    this.gerarLog(nomeClasse, methodName, "SAIDA", descricaoMethod);
                }
            }
        } catch (Exception ex) {
            log.error("ERRO NA GERACAO DO LOG DE SAIDA", ex);
        }
    }

    private boolean gravarRetorno(ProceedingJoinPoint joinPoint) {
        LogAutomatico logAutomatico = getMethodFromJoinPoint(joinPoint).getDeclaredAnnotation(LogAutomatico.class);
        if (logAutomatico != null)
            return logAutomatico.gravarRetorno();
        return false;
    }

    private Optional<String> getDescricaoMethod(ProceedingJoinPoint joinPoint) {
        Method method = getMethodFromJoinPoint(joinPoint);
        LogAutomatico logAutomatico = method.getDeclaredAnnotation(LogAutomatico.class);
        if (logAutomatico == null || StringUtils.isBlank(logAutomatico.descricao()))
            return Optional.empty();
        return Optional.of(StringTools.normalize(logAutomatico.descricao()));
    }

    private boolean gravarSaidaMetodo(ProceedingJoinPoint joinPoint) {
        Method method = getMethodFromJoinPoint(joinPoint);
        LogAutomatico logAutomatico = method.getDeclaredAnnotation(LogAutomatico.class);
        return logAutomatico == null || logAutomatico.gravarSaida();
    }

    private Method getMethodFromJoinPoint(ProceedingJoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    private Object[] getArgumentos(ProceedingJoinPoint joinPoint) {
        var parametros = new ArrayList<>();
        Method method = getMethodFromJoinPoint(joinPoint);
        LogAutomatico logAutomatico = method.getDeclaredAnnotation(LogAutomatico.class);
        if (logAutomatico != null && logAutomatico.argumentos() != null) {
            var argumentos = Arrays.asList(logAutomatico.argumentos());
            for (int i = 0; i < method.getParameterCount(); i++) {
                if(argumentos.contains(method.getParameters()[i].getName()))
                    parametros.add(joinPoint.getArgs()[i]);
            }
        }
        return parametros.toArray();
    }


    private void gerarLog(String nomeClasse, String methodName, String entrada, Optional<String> descricaoMethod, Object[] argumentos) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{} - {} - {}");
        stringBuilder.append( " - {}".repeat(argumentos.length));
        if (descricaoMethod.isPresent()) {
            List<Object> text = new ArrayList<>(Arrays.asList(nomeClasse, methodName, entrada, descricaoMethod.get()));
            text.addAll(Arrays.asList(argumentos));
            log.info( stringBuilder.append( " - {}" ).toString(), text.toArray());
        } else {
            List<Object> text = new ArrayList<>(Arrays.asList(nomeClasse, methodName, entrada));
            text.addAll(Arrays.asList(argumentos));
            log.info(stringBuilder.toString(), text.toArray());
        }
    }

    private void gerarLog(String nomeClasse, String methodName, String saida, Optional<String> descricaoMethod, Object retornoOptional) {
        if (descricaoMethod.isPresent())
            log.info("{} - {} - {} - {} - {}", nomeClasse, methodName, saida, descricaoMethod.get(), retornoOptional);
        else
            log.info("{} - {} - {} - {}", nomeClasse, methodName, saida, retornoOptional);
    }

    private void gerarLog(String nomeClasse, String methodName, String momento, Optional<String> descricaoMethod) {
        if (descricaoMethod.isPresent())
            log.info("{} - {} - {} - {}", nomeClasse, methodName, momento, descricaoMethod.get());
        else
            log.info( "{} - {} - {}", nomeClasse, methodName, momento);
    }

}