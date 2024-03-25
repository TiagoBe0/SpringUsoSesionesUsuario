FROM eclipse-temurin:21-jdk-alpine


COPY target/SpringUsoSesionesUsuario-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","/app.war"]
