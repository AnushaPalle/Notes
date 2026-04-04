package com.shopflow.monolith.users.contract;

/** Public contract of the users module: the only thing other modules may depend on. */
public interface UserQuery {
    boolean exists(Long userId);
}
