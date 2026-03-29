package dev.sbs.serverapi.config;

import com.google.gson.Gson;
import dev.sbs.api.SimplifiedApi;
import dev.sbs.serverapi.security.SecurityHeaderInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

import java.util.List;

/**
 * Framework-level web configuration that registers the {@link SecurityHeaderInterceptor}
 * and configures HTTP message converters for the Gson-based ecosystem.
 *
 * <p>Message converter ordering is significant: {@link StringHttpMessageConverter} is
 * registered first so that {@code text/html} responses (such as Cloudflare-style error
 * pages) serialize correctly, followed by {@link GsonHttpMessageConverter} for JSON.</p>
 *
 * <p>If a consumer defines a {@link Gson} {@code @Bean}, it is used automatically.
 * Otherwise the {@link SimplifiedApi#getGson()} instance is used as a fallback.</p>
 */
@Configuration
public class ServerWebConfig implements WebMvcConfigurer {

    private final @NotNull Gson gson;

    public ServerWebConfig(@NotNull ObjectProvider<Gson> gsonProvider) {
        this.gson = gsonProvider.getIfAvailable(SimplifiedApi::getGson);
    }

    @Bean
    public @NotNull MappedInterceptor securityHeaderMappedInterceptor() {
        return new MappedInterceptor(new String[]{"/**"}, new SecurityHeaderInterceptor());
    }

    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(this.gson);
        converters.add(gsonConverter);
    }

}
