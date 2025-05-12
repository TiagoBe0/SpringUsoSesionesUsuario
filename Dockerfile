# Stage 1: Build the WAR
FROM eclipse-temurin:23-jdk AS builder
WORKDIR /app

# Copia todo el proyecto y da permisos a mvnw
COPY . .
RUN chmod +x mvnw

# Empaqueta el WAR sin tests
RUN ./mvnw clean package -DskipTests

# Stage 2: Deploy en Tomcat
FROM tomcat:9.0-jre17
# Limpia la aplicación por defecto de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copia el WAR generado como ROOT.war para que arranque en /
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# Puerto que usa Tomcat por defecto
EXPOSE 8080

# Arranca Tomcat en foreground
CMD ["catalina.sh", "run"]
