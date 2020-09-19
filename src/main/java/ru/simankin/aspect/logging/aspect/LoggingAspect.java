package ru.simankin.aspect.logging.aspect;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import ru.simankin.aspect.logging.annotation.Logging;

@Slf4j
@Aspect
public class LoggingAspect {

    private static final String WHITE_SPACE = " ";
    private static final String IN = "IN";
    private static final String OUT = "OUT";
    private static final String ERROR = "ERROR";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String EQUAL_SIGN = "=";

    @Around("@annotation(ru.simankin.aspect.logging.annotation.Logging)")
    public Object logWithLoggingAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] parameterNames = signature.getParameterNames();
        String methodName = method.getName();
        String simpleClassName = method.getDeclaringClass().getSimpleName();
        boolean isDebug = method.getAnnotation(Logging.class).isDebug();

        String inLog = appendInLog(arguments, parameterNames, methodName, simpleClassName);
        log(inLog, isDebug);

        Object proceed = new Object();
        try {
            proceed = joinPoint.proceed();

            String outLog = appendOutLog(proceed, methodName, simpleClassName);
            log(outLog, isDebug);
        } catch (Exception e) {
            String errorLog = appendErrorLog(methodName, simpleClassName, e);
            log.error(errorLog);
        }

        return proceed;
    }

    private void log(String message, boolean isDebug) {
        if (isDebug) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }

    private String appendInLog(Object[] arguments, Object[] parameterNames, String methodName, String simpleClassName) {
        StringBuilder resultLog = new StringBuilder();
        resultLog.append(simpleClassName)
                .append(DOT)
                .append(methodName)
                .append(DOT)
                .append(IN);
        int countArguments;

        if (arguments.length == 0 || parameterNames.length == 0 || arguments.length != parameterNames.length) {
            return resultLog.toString();
        }

        countArguments = arguments.length;

        resultLog.append(LEFT_BRACKET);
        for (int i = 0; i < countArguments; i++) {
            resultLog.append(parameterNames[i])
                    .append(EQUAL_SIGN)
                    .append(arguments[i]);
            if (i < countArguments - 1) {
                resultLog.append(COMMA)
                        .append(WHITE_SPACE);
            }
        }
        resultLog.append(RIGHT_BRACKET);

        return resultLog.toString();
    }

    private String appendOutLog(Object proceed, String methodName, String simpleClassName) {
        StringBuilder resultLog = new StringBuilder();
        resultLog.append(simpleClassName)
                .append(DOT)
                .append(methodName)
                .append(DOT)
                .append(OUT);

        if (proceed == null) {
            return resultLog.toString();
        }

        resultLog.append(LEFT_BRACKET)
                .append(proceed)
                .append(RIGHT_BRACKET);

        return resultLog.toString();
    }

    private String appendErrorLog(String methodName, String simpleClassName, Throwable e) {
        StringBuilder resultLog = new StringBuilder();
        resultLog.append(simpleClassName)
                .append(DOT)
                .append(methodName)
                .append(DOT)
                .append(ERROR)
                .append(WHITE_SPACE)
                .append(e);

        return resultLog.toString();
    }
}
