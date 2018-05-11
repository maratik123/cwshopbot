package edu.umd.cs.findbugs.annotations;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@SuppressWarnings("unused")
public @interface SuppressFBWarnings {
    String[] value() default {};

    String justification() default "";
}
