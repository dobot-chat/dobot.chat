package chat.dobot.bot.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoBot {
    String id();
    String nome() default "Meu bot (-_-)";
    String descricao() default "";
}
