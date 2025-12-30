package com.dstober.onething.config;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

/**
 * Configuration to load .env file from project root directory. This allows the .env file to be
 * shared across frontend and backend.
 */
public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    try {

      String projectRoot = Paths.get("").toAbsolutePath().getParent().toString();
      System.out.println("=== DotenvConfig: Looking for .env in: " + projectRoot);

      Dotenv dotenv = Dotenv.configure().directory(projectRoot).ignoreIfMissing().load();

      System.out.println("=== DotenvConfig: Loaded .env file successfully");
      System.out.println("=== DotenvConfig: SUPABASE_HOST=" + dotenv.get("SUPABASE_HOST"));
      System.out.println("=== DotenvConfig: SUPABASE_PORT=" + dotenv.get("SUPABASE_PORT"));
      System.out.println("=== DotenvConfig: SUPABASE_DB=" + dotenv.get("SUPABASE_DB"));
      System.out.println("=== DotenvConfig: SUPABASE_USER=" + dotenv.get("SUPABASE_USER"));
      System.out.println(
          "=== DotenvConfig: SUPABASE_PASSWORD="
              + (dotenv.get("SUPABASE_PASSWORD") != null ? "***SET***" : "NULL"));

      Map<String, Object> envMap =
          dotenv.entries().stream()
              .collect(Collectors.toMap(DotenvEntry::getKey, DotenvEntry::getValue));

      applicationContext
          .getEnvironment()
          .getPropertySources()
          .addFirst(new MapPropertySource("dotenvProperties", envMap));

      System.out.println(
          "=== DotenvConfig: Added " + envMap.size() + " properties to Spring environment");
    } catch (Exception e) {
      System.err.println("=== DotenvConfig ERROR: Could not load .env file: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
