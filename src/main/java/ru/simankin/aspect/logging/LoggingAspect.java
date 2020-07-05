package ru.simankin.aspect.logging;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
@Aspect
public class LoggingAspect {

    private static final String WHITE_SPACE = " ";
    private static final String START = "start";
    private static final String END = "end";
    private static final String DOT = ".";
    private static final String COMMA = ",";
    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";
    private static final String EQUAL_SIGN = "=";

    @Around("@annotation(ToLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] arguments = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] parameters = method.getParameters();
        String methodName = method.getName();
        String simpleClassName = method.getDeclaringClass().getSimpleName();
        boolean isDebug = method.getAnnotation(ToLog.class).isDebug();

        String startLog = appendStartLog(arguments, parameters, methodName, simpleClassName);
        log(startLog, isDebug);

        Object proceed = joinPoint.proceed();

        String endLog = appendEndLog(proceed, methodName, simpleClassName);
        log(endLog, isDebug);
        return proceed;
    }

    private void log(String message, boolean isDebug) {
        if (isDebug) {
            log.debug(message);
        } else {
            log.info(message);
        }
    }

    private String appendStartLog(Object[] arguments, Object[] parameters, String methodName, String simpleClassName) {
        StringBuffer resultLog = new StringBuffer();
        resultLog.append(START);
        resultLog.append(WHITE_SPACE);
        resultLog.append(simpleClassName);
        resultLog.append(DOT);
        resultLog.append(methodName);
        int countArguments;
        if (arguments.length != parameters.length) {
            return resultLog.toString();
        }
        countArguments = arguments.length;
        resultLog.append(LEFT_BRACKET);
        for (int i = 0; i < countArguments; i++) {
            resultLog.append(arguments[i]);
            resultLog.append(EQUAL_SIGN);
            resultLog.append(parameters[i]);
            if (i < countArguments - 1) {
                resultLog.append(COMMA);
            }
        }
        resultLog.append(RIGHT_BRACKET);
        return resultLog.toString();
    }

    private String appendEndLog(Object proceed, String methodName, String simpleClassName) {
        StringBuffer resultLog = new StringBuffer();
        resultLog.append(END);
        resultLog.append(WHITE_SPACE);
        resultLog.append(simpleClassName);
        resultLog.append(DOT);
        resultLog.append(methodName);
        if (proceed == null) {
            return resultLog.toString();
        }
        resultLog.append(LEFT_BRACKET);
        resultLog.append(proceed);
        resultLog.append(RIGHT_BRACKET);
        return resultLog.toString();
    }
}
