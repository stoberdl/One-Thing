package com.dstober.onething.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dstober.onething.TestDataFactory;
import com.dstober.onething.model.Frequency;
import com.dstober.onething.model.TimeBracket;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DtoValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  // LoginRequest tests
  @Test
  void loginRequest_Valid() {
    LoginRequest request = TestDataFactory.createLoginRequest();

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void loginRequest_EmailNull() {
    LoginRequest request = new LoginRequest(null, "password123");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  void loginRequest_EmailInvalid() {
    LoginRequest request = new LoginRequest("invalid-email", "password123");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
  }

  @Test
  void loginRequest_PasswordShort() {
    LoginRequest request = new LoginRequest("test@example.com", "short");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("password"));
  }

  @Test
  void loginRequest_PasswordExactly8Chars_Valid() {
    LoginRequest request = new LoginRequest("test@example.com", "12345678");

    Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  // RegisterRequest tests
  @Test
  void registerRequest_Valid() {
    RegisterRequest request = TestDataFactory.createRegisterRequest();

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void registerRequest_NameTooShort() {
    RegisterRequest request = new RegisterRequest("test@example.com", "password123", "A");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
  }

  @Test
  void registerRequest_NameExactly2Chars_Valid() {
    RegisterRequest request = new RegisterRequest("test@example.com", "password123", "AB");

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void registerRequest_NameTooLong() {
    String longName = "A".repeat(101);
    RegisterRequest request = new RegisterRequest("test@example.com", "password123", longName);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
  }

  @Test
  void registerRequest_NameExactly100Chars_Valid() {
    String name100 = "A".repeat(100);
    RegisterRequest request = new RegisterRequest("test@example.com", "password123", name100);

    Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  // TaskCreateRequest tests
  @Test
  void taskCreateRequest_Valid() {
    TaskCreateRequest request = TestDataFactory.createTaskCreateRequest();

    Set<ConstraintViolation<TaskCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void taskCreateRequest_FrequencyWeekly_Valid() {
    TaskCreateRequest request =
        new TaskCreateRequest("Task", 1L, TimeBracket.UNDER_FIFTEEN, Frequency.WEEKLY, null);

    Set<ConstraintViolation<TaskCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void taskCreateRequest_FrequencyYearly_Valid() {
    TaskCreateRequest request =
        new TaskCreateRequest("Task", 1L, TimeBracket.UNDER_FIFTEEN, Frequency.YEARLY, null);

    Set<ConstraintViolation<TaskCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void taskCreateRequest_FrequencyNull_Invalid() {
    TaskCreateRequest request =
        new TaskCreateRequest("Task", 1L, TimeBracket.UNDER_FIFTEEN, null, null);

    Set<ConstraintViolation<TaskCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("frequency"));
  }

  @Test
  void taskCreateRequest_NameBlank() {
    TaskCreateRequest request =
        new TaskCreateRequest("", 1L, TimeBracket.UNDER_FIFTEEN, Frequency.MONTHLY, null);

    Set<ConstraintViolation<TaskCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
  }

  // CategoryCreateRequest tests
  @Test
  void categoryCreateRequest_Valid() {
    CategoryCreateRequest request = TestDataFactory.createCategoryCreateRequest();

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void categoryCreateRequest_ColorValid_UpperCase() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#FFFFFF");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void categoryCreateRequest_ColorValid_LowerCase() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#ffffff");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void categoryCreateRequest_ColorValid_MixedCase() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#AbCdEf");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isEmpty();
  }

  @Test
  void categoryCreateRequest_ColorInvalid_NoHash() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "FFFFFF");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
  }

  @Test
  void categoryCreateRequest_ColorInvalid_TooShort() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#FFF");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
  }

  @Test
  void categoryCreateRequest_ColorInvalid_TooLong() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#FFFFFFF");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
  }

  @Test
  void categoryCreateRequest_ColorInvalid_NonHexChars() {
    CategoryCreateRequest request = new CategoryCreateRequest("Category", null, "icon", "#GGGGGG");

    Set<ConstraintViolation<CategoryCreateRequest>> violations = validator.validate(request);

    assertThat(violations).isNotEmpty();
    assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
  }
}
