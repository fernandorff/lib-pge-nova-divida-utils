package br.gov.ce.pge.nova_divida_utils.annotations.logs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class LogAutomaticoImplTest {

    private static final String PROCEED = "Proceed";


    public static class TestClass {

        @LogAutomatico
        public void testMethod() {
            System.out.println("Test Method");
        }

        @LogAutomatico(descricao = "Test Method 2")
        public void testMethod2() {
            System.out.println("Test Method 2");
        }

        @LogAutomatico(descricao = "Test Method 3", gravarRetorno = true)
        public void testMethod3() {
            System.out.println("Test Method 3");
        }

        @LogAutomatico(descricao = "Test Method 4", gravarRetorno = true, argumentos = {"argumento"})
        public String testMethod4(String argumento) {
            System.out.println(argumento);
            return argumento;
        }

        @LogAutomatico(descricao = "Test Method 5", gravarRetorno = true, argumentos = {})
        public String testMethod5(String argumento) {
            System.out.println(argumento);
            return argumento;
        }

    }

    @InjectMocks
    private LogAutomaticoImpl logAutomatico;

    @Mock
    private MethodSignature signature;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @ParameterizedTest
    @MethodSource("testLogProvider")
    void logPadraoTest(Method method) throws Throwable {
        when(signature.getName()).thenReturn(method.getName());
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getThis()).thenReturn("This");
        when(joinPoint.proceed()).thenReturn(PROCEED);

        Object actualLogPadraoResult = logAutomatico.logPadrao(joinPoint);

        verify(joinPoint,atLeast(1)).getSignature();
        verify(joinPoint,atLeast(1)).getThis();
        verify(joinPoint).proceed();
        verify(signature,atLeast(1)).getName();
        assertEquals(PROCEED,actualLogPadraoResult);
    }

    public static Stream<Arguments> testLogProvider() throws NoSuchMethodException {
        return Stream.of(
                Arguments.of(TestClass.class.getMethod("testMethod")),
                Arguments.of(TestClass.class.getMethod("testMethod2")),
                Arguments.of(TestClass.class.getMethod("testMethod3"))
        );
    }

    @Test
    void logPadraoArgumentosTest() throws Throwable {
        Method method = TestClass.class.getMethod("testMethod4", String.class);
        when(signature.getName()).thenReturn(method.getName());
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"Argumento"});
        when(joinPoint.getThis()).thenReturn("This");
        when(joinPoint.proceed()).thenReturn(PROCEED);

        Object actualLogPadraoResult = logAutomatico.logPadrao(joinPoint);

        verify(joinPoint,atLeast(1)).getSignature();
        verify(joinPoint,atLeast(1)).getThis();
        verify(joinPoint).proceed();
        verify(signature,atLeast(1)).getName();
        assertEquals(PROCEED,actualLogPadraoResult);
    }

    @Test
    void logPadraoArgumentosVaziosTest() throws Throwable {
        Method method = TestClass.class.getMethod("testMethod5", String.class);
        when(signature.getName()).thenReturn(method.getName());
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getThis()).thenReturn("This");
        when(joinPoint.proceed()).thenReturn(PROCEED);

        Object actualLogPadraoResult = logAutomatico.logPadrao(joinPoint);

        verify(joinPoint,atLeast(1)).getSignature();
        verify(joinPoint,atLeast(1)).getThis();
        verify(joinPoint).proceed();
        verify(signature,atLeast(1)).getName();
        assertEquals(PROCEED,actualLogPadraoResult);
    }
}
