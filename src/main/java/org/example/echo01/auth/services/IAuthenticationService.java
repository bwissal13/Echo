package org.example.echo01.auth.services;

import org.example.echo01.auth.entities.User;

public interface IAuthenticationService {
    User getCurrentUser();
} 