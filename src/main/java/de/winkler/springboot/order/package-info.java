@org.springframework.modulith.ApplicationModule(
  allowedDependencies = {"logger", "user", "user::internal", "persistence"}
)
package de.winkler.springboot.order;
