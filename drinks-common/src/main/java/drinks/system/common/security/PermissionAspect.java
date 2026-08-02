package drinks.system.common.security;

import drinks.system.common.exception.ForbiddenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * AOP aspect that enforces permission-based authorization on methods annotated
 * with {@link RequiresPermission}.
 *
 * <p>This aspect intercepts calls to annotated controller methods, retrieves
 * the current authenticated user from the SecurityContext, and verifies that
 * the user's permissions include the required permission. If not, a
 * {@link ForbiddenException} is thrown (resulting in HTTP 403).</p>
 */
@Aspect
@Component
public class PermissionAspect {

    /**
     * Intercepts methods annotated with @RequiresPermission and checks if the
     * authenticated user has the required permission.
     *
     * @param joinPoint the method being intercepted
     * @param requiresPermission the annotation containing the required permission
     * @return the result of the method execution if authorized
     * @throws Throwable if the method throws or if authorization fails
     */
    @Around("@annotation(requiresPermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequiresPermission requiresPermission) throws Throwable {
        String requiredPermission = requiresPermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new ForbiddenException("No se pudo verificar los permisos del usuario");
        }

        if (!userPrincipal.hasPermission(requiredPermission)) {
            throw ForbiddenException.forPermission(requiredPermission);
        }

        return joinPoint.proceed();
    }
}
