package dev.joguenco.security.domain;

import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;

@MappedEntity("v_users")
public record User(@Id String id, String username, String password, String role, Boolean status) {}
