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
import java.util.StringJoiner;
import java.util.stream.IntStream;

@Slf4j
@Aspect
@Component
public class LogAutomaticoImpl {

    private static final String ENTRADA = "ENTRADA";
    private static final String SAIDA = "SAIDA";
    private static final String ERRO = "ERRO";
    private static final String SEPARATOR = " - ";

    private record LogMetadata(String longMethodSignature, String description, LogAutomatico annotation) {
        public static LogMetadata from(ProceedingJoinPoint joinPoint) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String longMethodSignature = signature.toLongString();

            Method method = signature.getMethod();
            LogAutomatico annotation = method.getAnnotation(LogAutomatico.class);
            String description = StringTools.normalize(annotation.descricao());

            return new LogMetadata(longMethodSignature, description, annotation);
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
        String errorClass = throwable.getClass().getSimpleName();

        StackTraceElement[] stackTrace = throwable.getStackTrace();
        String origin = "(Origem indisponível)";
        if (stackTrace != null && stackTrace.length > 0) {
            origin = stackTrace[0].toString();
        }

        String format = String.join(SEPARATOR,
                "@LogAutomatico (" + ERRO + ")",
                "Assinatura: {}",
                "Causa: {}",
                "Mensagem: '{}'",
                "Origem: {}"
        );
        log.error(format,
                metadata.longMethodSignature(),
                errorClass,
                throwable.getMessage(),
                origin,
                throwable);
    }

    private void logEntrada(ProceedingJoinPoint joinPoint, LogMetadata metadata) {
        LoggableArgument[] arguments = getArgumentos(joinPoint, metadata.annotation());
        gerarLog(metadata, ENTRADA, arguments);
    }

    private void logSaida(LogMetadata metadata, Object result) {
        LogAutomatico annotation = metadata.annotation();
        if (!annotation.gravarSaida()) {
            return;
        }
        Object[] resultToLog = getResultToLog(annotation, result);
        gerarLog(metadata, SAIDA, resultToLog);
    }

    private Object[] getResultToLog(LogAutomatico annotation, Object result) {
        if (annotation.gravarRetorno()) {
            return new Object[]{result};
        }
        return new Object[0];
    }

    private LoggableArgument[] getArgumentos(ProceedingJoinPoint joinPoint, LogAutomatico annotation) {
        String[] argumentsToLogNames = annotation.argumentos();
        if (argumentsToLogNames.length == 0) {
            return new LoggableArgument[0];
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] methodArguments = joinPoint.getArgs();
        List<String> argumentsToLog = Arrays.asList(argumentsToLogNames);

        return IntStream.range(0, parameterNames.length)
                .filter(index -> argumentsToLog.contains(parameterNames[index]))
                .mapToObj(index -> {
                    Object argValue = methodArguments[index];
                    String argName = parameterNames[index];
                    String argType = (argValue != null) ? argValue.getClass().getSimpleName() : "null";
                    return new LoggableArgument(argName, argType, argValue);
                })
                .toArray(LoggableArgument[]::new);
    }

    private void gerarLog(LogMetadata metadata, String moment, Object[] details) {
        List<Object> logArguments = new ArrayList<>();
        logArguments.add(moment);
        logArguments.add(metadata.longMethodSignature());
        logArguments.add(metadata.description());

        String baseFormat = String.join(SEPARATOR,
                "@LogAutomatico ({})",
                "Assinatura: {}",
                "Descricao: {}"
        );
        StringBuilder logFormat = new StringBuilder(baseFormat);

        appendDetailsToLog(logFormat, logArguments, details, moment);

        log.info(logFormat.toString(), logArguments.toArray());
    }

    private void appendDetailsToLog(StringBuilder logFormat, List<Object> logArguments, Object[] details, String moment) {
        if (details == null || details.length == 0) {
            return;
        }
        if (ENTRADA.equals(moment)) {
            appendEntryDetails(logFormat, logArguments, details);
            return;
        }
        appendExitDetails(logFormat, logArguments, details);
    }

    private void appendEntryDetails(StringBuilder logFormat, List<Object> logArguments, Object[] details) {
        logFormat.append(SEPARATOR).append("Parametros: {}");
        StringJoiner paramsString = new StringJoiner(", ");
        for (int i = 0; i < details.length; i++) {
            LoggableArgument detail = (LoggableArgument) details[i];
            String formattedValue = formatObjectValue(detail.type(), detail.value());
            paramsString.add((i + 1) + ". " + detail.name() + " = { " + formattedValue + " }");
        }
        logArguments.add(paramsString.toString());
    }

    private void appendExitDetails(StringBuilder logFormat, List<Object> logArguments, Object[] details) {
        logFormat.append(SEPARATOR).append("Retorno: {}");
        Object returnValue = details[0];
        String className = (returnValue != null) ? returnValue.getClass().getSimpleName() : "void";
        logArguments.add(formatObjectValue(className, returnValue));
    }

    private String formatObjectValue(String className, Object value) {
        if (value == null) {
            return className + "(null)";
        }
        String valueAsString = value.toString();
        if (valueAsString.startsWith(className + "(")) {
            return valueAsString;
        }
        return className + "(" + valueAsString + ")";
    }
}