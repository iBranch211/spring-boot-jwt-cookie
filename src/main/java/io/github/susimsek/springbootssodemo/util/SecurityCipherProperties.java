package io.github.susimsek.springbootssodemo.util;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@ConfigurationProperties(prefix = "cipher")
public class SecurityCipherProperties {

    String key;
}
