package com.example.unicon.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecaptchaResponse {
    private boolean success;
    @JsonProperty("challenge_ts")
    private String challengeTs;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;

    @Override
    public String toString() {
        return String.format("RecaptchaResponse{success=%b, challengeTs='%s', hostname='%s', errorCodes=%s}",
                success, challengeTs, hostname, errorCodes);
    }
}