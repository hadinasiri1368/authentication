package org.authentication.dto.ResponseDto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private Long id;
    private String name;
    private String family;
    private String birthDate;
    private String nationalCode;
    private String mobileNumber;
    private Boolean isForeigners;
    private Boolean isCompany;
    private String companyNationalCode;
    private String establishDate;
    private String managerName;
    private String managerNationalCode;
    private String managerBirthDate;
    private String managerMobileNumber;
    private String idNumber;
}
