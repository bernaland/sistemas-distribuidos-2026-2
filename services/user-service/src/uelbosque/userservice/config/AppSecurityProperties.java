package uelbosque.userservice.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {
    @NotBlank
    private String issuer;
    @NotBlank
    private String serviceClientId;
    @NotBlank
    private String serviceClientSecret;
    @NotBlank
    private String webClientId;
    @NotBlank
    private String webClientSecret;
    @NotBlank
    private String webClientRedirectUri;

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getServiceClientId() {
        return serviceClientId;
    }

    public void setServiceClientId(String serviceClientId) {
        this.serviceClientId = serviceClientId;
    }

    public String getServiceClientSecret() {
        return serviceClientSecret;
    }

    public void setServiceClientSecret(String serviceClientSecret) {
        this.serviceClientSecret = serviceClientSecret;
    }

    public String getWebClientId() {
        return webClientId;
    }

    public void setWebClientId(String webClientId) {
        this.webClientId = webClientId;
    }

    public String getWebClientSecret() {
        return webClientSecret;
    }

    public void setWebClientSecret(String webClientSecret) {
        this.webClientSecret = webClientSecret;
    }

    public String getWebClientRedirectUri() {
        return webClientRedirectUri;
    }

    public void setWebClientRedirectUri(String webClientRedirectUri) {
        this.webClientRedirectUri = webClientRedirectUri;
    }
}
