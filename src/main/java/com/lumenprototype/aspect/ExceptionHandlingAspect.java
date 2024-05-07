package com.lumenprototype.aspect;

import com.lumenprototype.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Aspect
@Slf4j
@Component
public class ExceptionHandlingAspect {

    /**
     * 애플리케이션 내 모든 메서드 실행을 감싸 예외 처리 로직을 제공합니다.
     * 발생하는 예외 유형에 따라 적절한 사용자 정의 예외로 변환하여 던집니다.
     * 이는 애플리케이션의 예외 처리를 일관되게 관리할 수 있게 도와줍니다.
     *
     * @param joinPoint 메서드 실행에 대한 조인 포인트입니다.
     *                  이를 통해 메서드 실행 전후로 추가적인 로직을 실행할 수 있습니다.
     * @return 메서드의 실행 결과를 반환합니다. 예외가 발생하면 사용자 정의 예외로 변환하여 던집니다.
     * @throws Throwable 메서드 실행 중 발생할 수 있는 예외입니다. 사용자 정의 예외로 처리됩니다.
     */
    @Around("execution(* com.lumenprototype..*.*(..))")
    public Object handleExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 실제 타겟 메서드를 실행합니다.
            return joinPoint.proceed();
        } catch (DataAccessException e) {
            // 데이터 접근 중 예외가 발생한 경우 로깅 후 사용자 정의 예외로 변환하여 던집니다.
            log.error("DataAccessException in {} : {}", joinPoint.getSignature(), e.getMessage());
            throw new CustomException("Database error", e);
        } catch (AccessDeniedException e) {
            // 접근 권한이 거부된 경우 로깅 후 사용자 정의 예외로 변환하여 던집니다.
            log.error("AccessDeniedException in {} : {}", joinPoint.getSignature(), e.getMessage());
            throw new CustomException("Access denied", e);
        } catch (Exception e) {
            // 기타 예외 처리 시 로깅 후 사용자 정의 예외로 변환하여 던집니다.
            log.error("Unexpected exception in {} : {}", joinPoint.getSignature(), e.getMessage());
            throw new CustomException("Unexpected error", e);
        }
    }
}