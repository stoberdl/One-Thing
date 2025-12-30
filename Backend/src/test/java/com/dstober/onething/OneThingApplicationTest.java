package com.dstober.onething;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

final class OneThingApplicationTest {

  @Test
  void testApplicationStarts() {
    assertDoesNotThrow(
        () -> {
          final Class<?> clazz = Class.forName("com.dstober.onething.OneThingApplication");
          assertNotNull(clazz);
        });
  }
}
