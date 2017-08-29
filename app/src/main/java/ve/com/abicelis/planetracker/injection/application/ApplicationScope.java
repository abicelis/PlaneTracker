package ve.com.abicelis.planetracker.injection.application;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by abicelis on 28/8/2017.
 * Custom scope for global application objects
 * Functionally equivalent to standard dagger @Singleton scope,
 * only I find @ApplicationScope a bit more readable
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationScope {
}
