/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;


import entities.User;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Stefan
 */
@Provider
public class BasicAuthFilter implements ContainerRequestFilter{

    @PersistenceContext(unitName = "CustomerServicePU")
    EntityManager em;
    
    //izmeniti telo ove metode
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        List<String> authHeaderValues = requestContext.getHeaders().get("Authorization");
        
        if(authHeaderValues != null && authHeaderValues.size() > 0){
            String authHeaderValue = authHeaderValues.get(0);
            String decodedAuthHeaderValue = new String(Base64.getDecoder().decode(authHeaderValue.replaceFirst("Basic ", "")),StandardCharsets.UTF_8);
            StringTokenizer stringTokenizer = new StringTokenizer(decodedAuthHeaderValue, ":");
            String username = stringTokenizer.nextToken();
            String password = stringTokenizer.nextToken();
            
            List<User> users = em.createNamedQuery("User.findByUsername",User.class).setParameter("username", username).getResultList();
            
            if(users.isEmpty()){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik ne postoji.").build();
                requestContext.abortWith(response);
                return;
            }
            
            User user = users.get(0);
            
            if(!user.getPassword().equals(password)){
                Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Neispravna lozinka.").build();
                requestContext.abortWith(response);
                return;
            }
            return;
        }
        
        Response response = Response.status(Response.Status.UNAUTHORIZED).entity("Posaljite kredencijale.").build();
        requestContext.abortWith(response);
        return;
    }
    
}