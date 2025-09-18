package br.gov.ce.pge.nova_divida_utils.annotations.logs;

import br.gov.ce.pge.nova_divida_utils.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class LogAutomaticoImpl {

    private static final String ENTRADA = "ENTRADA";
    private static final String SAIDA = "SAIDA";
    private static final String ERRO = "ERRO";

    private record LogMetadata(
            String returnType,
            String declaringType,
            String methodName,
            String parameterTypes,
            String description,
            LogAutomatico annotation
    ) {
        public static LogMetadata from(ProceedingJoinPoint joinPoint) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            String returnType = signature.getReturnType().getName();
            String declaringType = signature.getDeclaringTypeName();
            String methodName = signature.getName();

            String parameterTypes = Arrays.stream(signature.getParameterTypes())
                    .map(Class::getName)
                    .collect(Collectors.joining(", "));

            Method method = signature.getMethod();
            LogAutomatico annotation = method.getAnnotation(LogAutomatico.class);
            String description = StringTools.normalize(annotation.descricao());

            return new LogMetadata(returnType, declaringType, methodName, parameterTypes, description, annotation);
        }
    }

    private record LoggableArgument(String name, String type, Object value) {}

    @Around("@annotation(LogAutomatico)")
    public Object logPadrao(ProceedingJoinPoint joinPoint) throws Throwable {
        LogMetadata metadata = LogMetadata.from(joinPoint);
        try {
            logEntrada(joinPoint, metadata);
            Object result = joinPoint.proceed();
            logSaida(metadata, result);
            return result;
        } catch (Throwable throwable) {
            logErro(metadata, throwable);
            throw throwable;
        }
    }

    private void logErro(LogMetadata metadata, Throwable throwable) {
        StringBuilder format = new StringBuilder();
        buildLogHeader(format, ERRO, metadata);

        String errorClass = throwable.getClass().getSimpleName();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        String origin = "(Origem indisponível)";
        if (stackTrace != null && stackTrace.length > 0) {
            origin = stackTrace[0].toString();
        }

        format.append("\n\t\t - Causa: ").append(errorClass);
        format.append("\n\t\t - Mensagem: '").append(throwable.getMessage()).append("'");
        format.append("\n\t\t - Origem: ").append(origin);
        format.append("\n");

        log.error(format.toString(), throwable);
    }

    private void logEntrada(ProceedingJoinPoint joinPoint, LogMetadata metadata) {
        LoggableArgument[] arguments = getArgumentos(joinPoint, metadata.annotation());
        gerarLog(metadata, ENTRADA, arguments);
    }

    private void logSaida(LogMetadata metadata, Object result) {
        LogAutomatico annotation = metadata.annotation();
        if (!annotation.gravarSaidaMetodo()) { // Corrected method name
            return;
        }
        Object[] resultToLog = getResultToLog(annotation, result);
        gerarLog(metadata, SAIDA, resultToLog);
    }

    private Object[] getResultToLog(LogAutomatico annotation, Object result) {
        if (annotation.gravarRetornoMetodo()) {
            return new Object[]{result};
        }
        return new Object[0];
    }

    private LoggableArgument[] getArgumentos(ProceedingJoinPoint joinPoint, LogAutomatico annotation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] methodArguments = joinPoint.getArgs();

        if (parameterNames.length == 0) {
            return new LoggableArgument[0];
        }

        List<String> argumentsToLog = Arrays.asList(annotation.argumentosEntrada());
        boolean logAll = argumentsToLog.isEmpty();

        return IntStream.range(0, parameterNames.length)
                .filter(index -> logAll || argumentsToLog.contains(parameterNames[index]))
                .mapToObj(index -> {
                    Object argValue = methodArguments[index];
                    String argName = parameterNames[index];
                    String argType = (argValue != null) ? argValue.getClass().getSimpleName() : "null";
                    return new LoggableArgument(argName, argType, argValue);
                })
                .toArray(LoggableArgument[]::new);
    }

    private void buildLogHeader(StringBuilder builder, String moment, LogMetadata metadata) {
        builder.append("\n\t@LogAutomatico (").append(moment).append(")");
        builder.append("\n\t\t - Descricao: ").append(metadata.description());
        builder.append("\n\t\t - Assinatura:");
        builder.append("\n\t\t\t - Retorno: ").append(metadata.returnType());
        builder.append("\n\t\t\t - Classe: ").append(metadata.declaringType());
        builder.append("\n\t\t\t - Metodo: ").append(metadata.methodName()).append("(").append(metadata.parameterTypes()).append(")");
    }

    private void gerarLog(LogMetadata metadata, String moment, Object[] details) {
        StringBuilder logFormat = new StringBuilder();
        buildLogHeader(logFormat, moment, metadata);

        List<Object> logArguments = new ArrayList<>();
        appendDetailsToLog(logFormat, logArguments, details, moment);

        logFormat.append("\n");
        log.info(logFormat.toString(), logArguments.toArray());
    }

    private void appendDetailsToLog(StringBuilder logFormat, List<Object> logArguments, Object[] details, String moment) {
        if (ENTRADA.equals(moment)) {
            appendEntryDetails(logFormat, details);
            return;
        }
        if (details != null && details.length > 0) {
            appendExitDetails(logFormat, logArguments, details);
        }
    }

    private void appendEntryDetails(StringBuilder logFormat, Object[] details) {
        logFormat.append("\n\t\t - Parametros:");
        appendAllParameterDetails(logFormat, details);
    }

    private void appendAllParameterDetails(StringBuilder logFormat, Object[] details) {
        if (details.length == 0) {
            logFormat.append(" (vazio)");
            return;
        }
        for (int i = 0; i < details.length; i++) {
            LoggableArgument detail = (LoggableArgument) details[i];
            String formattedValue = formatObjectValue(detail.type(), detail.value());
            logFormat.append("\n\t\t\t").append(i + 1).append(". ").append(detail.name()).append(" = ").append(formattedValue);
        }
    }

    private void appendExitDetails(StringBuilder logFormat, List<Object> logArguments, Object[] details) {
        logFormat.append("\n\t\t - Retorno: {}");
        Object returnValue = details[0];
        String className = (returnValue != null) ? returnValue.getClass().getSimpleName() : "void";
        logArguments.add(formatObjectValue(className, returnValue));
    }

    private String formatObjectValue(String className, Object value) {
        if (value == null) {
            return "(null)";
        }
        String valueAsString = value.toString();
        if (valueAsString.startsWith(className + "(")) {
            return valueAsString;
        }
        return className + "(" + valueAsString + ")";
    }
}