package org.example.echo01.common.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo01.auth.dtos.UserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionDto {
    private Long id;
    private UserDto targetUser;
    private UserDto subscriber;
    private boolean notifyOnNewBook;
} 