package org.user.service.dto;

import java.sql.Date;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetUserResponse {

	private String name;

    private String phone;

    private String password;


    private String email;

    private int age;

    private Date createdOn;

    private Date updatedOn;
}
