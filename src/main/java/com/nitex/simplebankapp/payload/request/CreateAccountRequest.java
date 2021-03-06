package com.nitex.simplebankapp.payload.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Min;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    private String accountName;
    private String accountPassword;
    @Min(value = 500, message = "Minimum Initial deposit  cannot be less than #500.00")
    private Double initialDeposit;
}
