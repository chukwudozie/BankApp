package com.nitex.simplebankapp.payload.response;

import com.nitex.simplebankapp.model.Account;
import jdk.jfr.SettingDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponse {

    private int responseCode;
    private  boolean success;
    private String message;
    private AccountResponse accountResponse;
}
