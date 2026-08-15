package dev.simplified.serverapi.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pins the generated builder, whose settings are carried by field initializers rather than by any
 * code a reader can see.
 */
class ServerConfigBuilderTest {

    @Nested
    @DisplayName("defaults")
    class Defaults {

        @Test
        @DisplayName("a field initializer is the builder's default for that setting")
        void fieldInitializer_isTheBuilderDefault() {
            ServerConfig config = ServerConfig.builder().build();

            assertThat(config.getPort(), is(8080));
            assertThat(config.getAddress(), is("0.0.0.0"));
            assertThat(config.getContextPath(), is("/"));
            assertThat(config.getMaxThreads(), is(200));
        }

        @Test
        @DisplayName("a boolean default is carried in whichever direction it was written")
        void booleanDefault_keepsItsWrittenDirection() {
            ServerConfig config = ServerConfig.builder().build();

            assertThat(config.isApiKeyAuthEnabled(), is(true));
            assertThat(config.isSpringdocEnabled(), is(true));
            assertThat(config.isActuatorEnabled(), is(false));
        }

        @Test
        @DisplayName("a reference default is a fresh instance per build, not a shared one")
        void referenceDefault_isNotSharedBetweenBuilds() {
            ServerConfig first = ServerConfig.builder().build();
            ServerConfig second = ServerConfig.builder().build();

            assertThat(first.getCompressionMimeTypes(), is(second.getCompressionMimeTypes()));
            assertThat(first.getCompressionMimeTypes(), not(sameInstance(second.getCompressionMimeTypes())));
        }
    }

    @Nested
    @DisplayName("negated setters")
    class Negated {

        @Test
        @DisplayName("the disable form disables")
        void disableForm_disables() {
            ServerConfig config = ServerConfig.builder()
                .isSpringdocDisabled()
                .isApiKeyAuthDisabled()
                .build();

            assertThat(config.isSpringdocEnabled(), is(false));
            assertThat(config.isApiKeyAuthEnabled(), is(false));
        }

        @Test
        @DisplayName("the enable form enables a setting that defaults off")
        void enableForm_enables() {
            ServerConfig config = ServerConfig.builder().isActuatorEnabled().build();

            assertThat(config.isActuatorEnabled(), is(true));
        }

        @Test
        @DisplayName("the typed negated setter takes the negated sense of its argument")
        void typedNegatedSetter_readsAsItsOwnName() {
            assertThat(ServerConfig.builder().withSpringdocDisabled(true).build().isSpringdocEnabled(), is(false));
            assertThat(ServerConfig.builder().withSpringdocDisabled(false).build().isSpringdocEnabled(), is(true));
        }
    }

    @Nested
    @DisplayName("entry points")
    class EntryPoints {

        @Test
        @DisplayName("from() seeds a builder from an existing instance")
        void from_seedsFromAnInstance() {
            ServerConfig original = ServerConfig.builder().withPort(9090).isActuatorEnabled().build();
            ServerConfig copy = ServerConfig.from(original).build();

            assertThat(copy.getPort(), is(9090));
            assertThat(copy.isActuatorEnabled(), is(true));
        }

        @Test
        @DisplayName("mutate() changes one setting and leaves the rest")
        void mutate_changesOneSetting() {
            ServerConfig original = ServerConfig.builder().withPort(9090).build();
            ServerConfig mutated = original.mutate().withPort(7070).build();

            assertThat(original.getPort(), is(9090));
            assertThat(mutated.getPort(), is(7070));
            assertThat(mutated.getAddress(), is(original.getAddress()));
        }
    }

    @Test
    @DisplayName("a @BuildFlag(nonNull) violation is rejected by build()")
    void nonNullFlag_isEnforcedByBuild() {
        assertThrows(
            RuntimeException.class,
            () -> ServerConfig.builder().withAddress(null).build()
        );
    }
}
