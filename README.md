# quarkus-jwt Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .


## Step to add JWT in quarkus

Step:1. In class to add @RequestScoped
Add a RequestScoped as Quarkus uses a default scoping of ApplicationScoped and this will produce undesirable behavior since JWT claims are naturally request scoped.

Step 2. Add this two dependancy on pow.xml
```shell script
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-jwt-build</artifactId>
</dependency>
```

Step 3. Congfigure methode  with Anotation

```shell script
package com.bmw.boundary;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/user")
@RequestScoped
public class UserResource {

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.birthdate)
    String birthdate;

    @GET
    @Path("permit-all")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    public String getUser(@Context SecurityContext ctx) {
        return getResponseString(ctx);
    }

    @GET
    @Path("roles-allowed")
    @RolesAllowed({ "User", "Admin" })
    @Produces(MediaType.TEXT_PLAIN)
    public String getAllowedRole(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + jwt.getClaim("birthdate").toString();
    }

    @GET
    @Path("roles-allowed-admin")
    @RolesAllowed("Admin")
    @Produces(MediaType.TEXT_PLAIN)
    public String getAdminRoll(@Context SecurityContext ctx) {
        return getResponseString(ctx) + ", birthdate: " + birthdate;
    }

    @GET
    @Path("deny-all")
    @DenyAll
    @Produces(MediaType.TEXT_PLAIN)
    public String getDeny(@Context SecurityContext ctx) {
        throw new InternalServerErrorException("This method must not be invoked");
    }

    private String getResponseString(SecurityContext ctx) {
        String name;
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = ctx.getUserPrincipal().getName();
        }
        return String.format("hello + %s,"
                        + " isHttps: %s,"
                        + " authScheme: %s,"
                        + " hasJWT: %s",
                name, ctx.isSecure(), ctx.getAuthenticationScheme(), hasJwt());
    }

    private boolean hasJwt() {
        return jwt.getClaimNames() != null;
    }
}

```

Step 4. Generate private and public key and convert the to PKCS#8 format

Open ssl can be used to generate a private key than to generate a public key from a private key
Note: If Git is installed in Windows, Openssl will also be installed. To check in the Git console if open ssl is installed, command: 

```shell script
openssl version
```

Commands:
Step1: Generating a Private Key:

```shell script
openssl genrsa -out rsaPrivateKey.pem 2048
```
Step2: Generating a Public from Private Key:
```shell script
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
```

Step3: Generating a Public from Private Key:
```shell script
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
```
Step4: Run this command to generate Token (be carefule about path)
```shell script
```
In case path dont work use absolute path
mvn exec:java -Dexec.mainClass=com.bmw.GenerateToken -Dexec.classpathScope=test -Dsmallrye.jwt.sign.key.location=privateKey.pem 
## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```


