package com.nishikatakagi.ProductDigital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleUserDto {

    @JsonProperty("at_hash")
    private String atHash;

    @JsonProperty("sub")
    private String sub;

    @JsonProperty("email_verified")
    private boolean emailVerified;

    @JsonProperty("iss")
    private String iss;

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("nonce")
    private String nonce;

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("aud")
    private List<String> aud;

    @JsonProperty("azp")
    private String azp;

    @JsonProperty("name")
    private String name;

    @JsonProperty("hd")
    private String hd;

    @JsonProperty("exp")
    private String exp;

    @JsonProperty("family_name")
    private  String familyName;

    @JsonProperty("iat")
    private String iat;

    @JsonProperty("email")
    private String email;

    @Override
    public String toString() {
        return givenName + "/" + email + "/" + name;
    }
}

