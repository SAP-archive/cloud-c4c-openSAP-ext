package com.sap.samples.odata;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.DatatypeConverter;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;

public class BasicAuthenticationHeaderProvider {

    private static final String BASIC_AUTHENTICATION_PREFIX = "Basic ";
    private static final String SEPARATOR = ":";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String PASSWORD_PROPERTY = "Password";
    private static final String USER_PROPERTY = "User";

    /**
     * Provides a valid header object with properly encoded credentials, using the supplied destination configuration.
     *  
     * @param destinationConfiguration
     * @return
     */
    public AuthenticationHeader getAuthenticationHeader(DestinationConfiguration destinationConfiguration) {
        StringBuilder userPass = new StringBuilder();
        userPass.append(destinationConfiguration.getProperty(USER_PROPERTY));
        userPass.append(SEPARATOR);
        userPass.append(destinationConfiguration.getProperty(PASSWORD_PROPERTY));
        String encodedPassword = DatatypeConverter.printBase64Binary(userPass.toString().getBytes(StandardCharsets.UTF_8));
        AuthenticationHeaderImpl basicAuthentication = new AuthenticationHeaderImpl(AUTHORIZATION_HEADER, BASIC_AUTHENTICATION_PREFIX + encodedPassword);
        return basicAuthentication;
    }

}
