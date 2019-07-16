package name.liwuest.util.rest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public @interface JsonRequestMapping {
    @AliasFor(annotation = RequestMapping.class, attribute = "value") String[] value() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "path") String[] path() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "method") RequestMethod[] method() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "params") String[] params() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "headers") String[] headers() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "consumes") String[] consumes() default {};
    @AliasFor(annotation = RequestMapping.class, attribute = "produces") String[] produces() default {};
}
