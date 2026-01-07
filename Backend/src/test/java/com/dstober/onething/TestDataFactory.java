package com.dstober.onething;

import com.dstober.onething.dto.*;
import com.dstober.onething.model.*;

/**
 * Factory class for creating test data objects.
 * Provides default values for all required fields.
 */
public class TestDataFactory {

    public static final Long DEFAULT_USER_ID = 1L;
    public static final Long DEFAULT_TASK_ID = 1L;
    public static final Long DEFAULT_CATEGORY_ID = 1L;
    public static final String DEFAULT_EMAIL = "test@example.com";
    public static final String DEFAULT_PASSWORD = "password123";
    public static final String DEFAULT_ENCODED_PASSWORD = "$2a$10$encodedPassword";
    public static final String DEFAULT_NAME = "Test User";
    public static final String DEFAULT_TASK_NAME = "Test Task";
    public static final String DEFAULT_CATEGORY_NAME = "Test Category";
    public static final String DEFAULT_ICON = "briefcase";
    public static final String DEFAULT_COLOR = "#FF5733";

    // User creation methods
    public static User createUser() {
        User user = new User(DEFAULT_EMAIL, DEFAULT_ENCODED_PASSWORD, DEFAULT_NAME);
        user.setId(DEFAULT_USER_ID);
        return user;
    }

    public static User createUser(Long id, String email, String name) {
        User user = new User(email, DEFAULT_ENCODED_PASSWORD, name);
        user.setId(id);
        return user;
    }

    public static User createUserWithPassword(String rawPassword) {
        User user = new User(DEFAULT_EMAIL, rawPassword, DEFAULT_NAME);
        user.setId(DEFAULT_USER_ID);
        return user;
    }

    // Task creation methods
    public static Task createTask() {
        Task task = new Task();
        task.setId(DEFAULT_TASK_ID);
        task.setName(DEFAULT_TASK_NAME);
        task.setCategoryId(DEFAULT_CATEGORY_ID);
        task.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
        task.setPriority(2);
        task.setUserId(DEFAULT_USER_ID);
        return task;
    }

    public static Task createTask(Long id, String name, Long userId) {
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setCategoryId(DEFAULT_CATEGORY_ID);
        task.setTimeBracket(TimeBracket.FIFTEEN_TO_THIRTY);
        task.setPriority(2);
        task.setUserId(userId);
        return task;
    }

    public static Task createTaskWithParent(Long parentId) {
        Task task = createTask();
        task.setParentId(parentId);
        return task;
    }

    // Category creation methods
    public static Category createCategory() {
        return new Category(DEFAULT_CATEGORY_NAME, DEFAULT_ICON, DEFAULT_COLOR, DEFAULT_USER_ID);
    }

    public static Category createCategory(Long userId) {
        return new Category(DEFAULT_CATEGORY_NAME, DEFAULT_ICON, DEFAULT_COLOR, userId);
    }

    public static Category createCategory(String name, String icon, String color, Long userId) {
        return new Category(name, icon, color, userId);
    }

    // DTO creation methods
    public static TaskCreateRequest createTaskCreateRequest() {
        return new TaskCreateRequest(
                DEFAULT_TASK_NAME,
                DEFAULT_CATEGORY_ID,
                TimeBracket.FIFTEEN_TO_THIRTY,
                2,
                null
        );
    }

    public static TaskCreateRequest createTaskCreateRequest(String name, Integer priority) {
        return new TaskCreateRequest(
                name,
                DEFAULT_CATEGORY_ID,
                TimeBracket.FIFTEEN_TO_THIRTY,
                priority,
                null
        );
    }

    public static TaskCreateRequest createTaskCreateRequestWithParent(Long parentId) {
        return new TaskCreateRequest(
                DEFAULT_TASK_NAME,
                DEFAULT_CATEGORY_ID,
                TimeBracket.FIFTEEN_TO_THIRTY,
                2,
                parentId
        );
    }

    public static CategoryCreateRequest createCategoryCreateRequest() {
        return new CategoryCreateRequest(DEFAULT_CATEGORY_NAME, null, DEFAULT_ICON, DEFAULT_COLOR);
    }

    public static CategoryCreateRequest createCategoryCreateRequest(String name, String icon, String color) {
        return new CategoryCreateRequest(name, null, icon, color);
    }

    public static RegisterRequest createRegisterRequest() {
        return new RegisterRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD, DEFAULT_NAME);
    }

    public static RegisterRequest createRegisterRequest(String email, String password, String name) {
        return new RegisterRequest(email, password, name);
    }

    public static LoginRequest createLoginRequest() {
        return new LoginRequest(DEFAULT_EMAIL, DEFAULT_PASSWORD);
    }

    public static LoginRequest createLoginRequest(String email, String password) {
        return new LoginRequest(email, password);
    }

    public static AuthResponse createAuthResponse(String token) {
        return new AuthResponse(token, DEFAULT_USER_ID, DEFAULT_EMAIL, DEFAULT_NAME);
    }

    // UserPrincipal creation
    public static com.dstober.onething.security.UserPrincipal createUserPrincipal() {
        return new com.dstober.onething.security.UserPrincipal(
                DEFAULT_USER_ID, DEFAULT_EMAIL, DEFAULT_ENCODED_PASSWORD, DEFAULT_NAME);
    }

    public static com.dstober.onething.security.UserPrincipal createUserPrincipal(Long id, String email) {
        return new com.dstober.onething.security.UserPrincipal(
                id, email, DEFAULT_ENCODED_PASSWORD, DEFAULT_NAME);
    }

    // Authentication helper
    public static org.springframework.security.core.Authentication createAuthentication() {
        com.dstober.onething.security.UserPrincipal userPrincipal = createUserPrincipal();
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
    }

    public static org.springframework.security.core.Authentication createAuthentication(Long userId) {
        com.dstober.onething.security.UserPrincipal userPrincipal = createUserPrincipal(userId, DEFAULT_EMAIL);
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities());
    }
}
