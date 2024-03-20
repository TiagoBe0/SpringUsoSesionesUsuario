

FROM amazon-corretto:21-alpime-jdk
COPY target/SpringUsoSesionesUsuario-0.0.1-SNAPSHOT.war app.war
ENTRYPOINT ["java","-jar","/app.war"]
