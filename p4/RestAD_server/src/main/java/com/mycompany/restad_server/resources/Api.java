package com.mycompany.restad_server.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Imagen;
import model.ImagenDAO;
import model.UserDAO;

/**
 *
 * @author
 */
@Path("api")
public class Api {

    /**
     * OPERACIONES DEL SERVICIO REST
     */
    /**
     * POST method to login in the application
     *
     * @param username
     * @param password
     * @return
     */
    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @FormParam("password") String password) {
        UserDAO userDao = new UserDAO();
        int ok = 0;
        try {
            ok = userDao.authenticate(username, password);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userDao.close();
        }
        String json = "{\"result\":" + ok + "}";
        return Response.ok(json).build();
    }
    
    /**
     * POST method to register in the application
     *
     * @param username
     * @param password
     * @param password2
     * @return
     */
    @Path("register_user")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register_user(@FormParam("username") String username, @FormParam("password") String password, @FormParam("password2") String password2) {
        int ok = 0;
        if (username == null || password == null || password2 == null) ok = 2;
        else if (!password.equals(password2)) ok = 3;
        else {
        
            UserDAO userDao = new UserDAO();

            try {
                if (userDao.exists(username)) ok = 4;
                else {
                    boolean created = userDao.create(username, password);
                    if (!created) ok = 100;
                }
            } catch (Exception e) {
                e.printStackTrace();
                ok = 100;
            } finally {
                userDao.close();
            }
        }
        String json = "{\"result\":" + ok + "}";
        return Response.ok(json).build();
    }
    /**
     * POST method to register a new image – File is not uploaded
     *
     * @param title
     * @param description
     * @param keywords
     * @param author
     * @param creator
     * @param capt_date
     * @return
     */
    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerImage(@FormParam("title") String title,
            @FormParam("description") String description,
            @FormParam("keywords") String keywords,
            @FormParam("author") String author,
            @FormParam("creator") String creator,
            @FormParam("capture") String capt_date) {

        LocalDate fechaAlta = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
        String timestamp = now.format(formatter);

        String nombreFichero = timestamp + "_nO";

        try {
            Imagen img = new Imagen();
            img.setTitulo(title);
            img.setDescripcion(description);
            img.setPalabrasClave(keywords);
            img.setAutor(author);
            img.setCreador(creator);
            img.setFechaCreacion(java.sql.Date.valueOf(LocalDate.parse(capt_date)));
            img.setFechaAlta(java.sql.Date.valueOf(fechaAlta));
            img.setNombreFichero(nombreFichero);
            img.setNombreOriginal("nO");

            ImagenDAO dao = new ImagenDAO();
            try {
                dao.create(img);
            } finally {
                dao.close();
            }
            
            Map<String, Object> body = new HashMap<>();
            body.put("title", "Imagen registrada");
            body.put("message", "Imagen registrada correctamente");
            body.put("backUrl", "menu.jsp");
            body.put("registerUrl", "registrarImagen.jsp");
            
            return Response.status(Response.Status.CREATED).entity(body).build();

        } catch (Exception ex) {
            Map<String, Object> err = new HashMap<>();
            err.put("code", 11);
            return Response.serverError().entity(err).build();
        }
    }
 /**
 * POST method to modify an existing image
 * @param id
 * @param title
 * @param description
 * @param keywords
 * @param author
 * @param creator, used for checking image ownership
 * @param capt_date
 * @return
 */
 @Path("modify")
 @POST
 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 @Produces(MediaType.APPLICATION_JSON)
 public Response modifyImage (@FormParam("id") String id,
 @FormParam("title") String title,
 @FormParam("description") String description,
 @FormParam("keywords") String keywords,
 @FormParam("author") String author,
 @FormParam("creator") String creator,
 @FormParam("capture") String capt_date) {
     try {
        int ID = Integer.parseInt(id);
        ImagenDAO dao = new ImagenDAO();
        try {
            Imagen img = dao.findById(ID);
            img.setTitulo(title);
            img.setAutor(author);
            img.setPalabrasClave(keywords);
            img.setDescripcion(description);
            if (capt_date != null && !capt_date.isEmpty()) {
                img.setFechaCreacion(java.sql.Date.valueOf(capt_date));
            } else {
                img.setFechaCreacion(null);
            }
            dao.update(img);
        } finally {
            dao.close();
        }
        Map<String, Object> body = new HashMap<>();
        body.put("title", "Imagen modificada");
        body.put("message", "Image data updated successfully!");
        body.put("backUrl", "menu.jsp");
        return Response.status(Response.Status.CREATED).entity(body).build();
     } catch (Exception e) {
        Map<String, Object> err = new HashMap<>();
        err.put("code", 11);
        return Response.serverError().entity(err).build();
     }
 }
 /**
 * POST method to delete an existing image
 * @param id
 * @param creator
 * @return
 */
 @Path("delete")
 @POST
 @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 @Produces(MediaType.APPLICATION_JSON)
 public Response deleteImage (@FormParam("id") String id,
 @FormParam("creator") String creator) {
    int result = 0;
    try {
        int ID = Integer.parseInt(id);
        ImagenDAO dao = new ImagenDAO();
        try {
            Imagen img = dao.findById(ID);
            if (img == null) result = -1;
            else if (!creator.equals(img.getCreador())) result = -1;
            else dao.delete(ID);
        } finally {
            dao.close();
        }
        Map<String, Object> body = new HashMap<>();
        body.put("result", result);
        return Response.status(Response.Status.CREATED).entity(body).build();
     } catch (Exception e) {
        Map<String, Object> err = new HashMap<>();
        err.put("result", 11);
        return Response.serverError().entity(err).build();
     }
 }
 
 /**
 * GET method to search images by id
 * @param id
 * @return
 */
 @Path("searchID/{id}")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchByID (@PathParam("id") int id) {
     try {
        ImagenDAO dao = new ImagenDAO();
        try {
            Imagen img = dao.findById(id);
            Map<String, Object> body = new HashMap<>();
            body.put("id", img.getId());
            body.put("title", img.getTitulo());
            body.put("description", img.getDescripcion());
            body.put("keywords", img.getPalabrasClave());
            body.put("author", img.getAutor());
            body.put("creator", img.getCreador());
            body.put("capture", img.getFechaCreacion() == null ? null : img.getFechaCreacion().toString());
            body.put("upload", img.getFechaAlta() == null ? null : img.getFechaAlta().toString());
            body.put("filename", img.getNombreFichero());
            body.put("originalName", img.getNombreOriginal());
            return Response.ok(body).build();
        } finally {
            dao.close();
        }
        
        
     } catch (Exception e) {
        Map<String, Object> err = new HashMap<>();
        err.put("code", 11);
        return Response.serverError().entity(err).build(); 
     }

   
 }
 
  private static Map<String,Object> toBody(Imagen img) {
        Map<String,Object> body = new HashMap<>();
        body.put("id",           img.getId());
        body.put("titulo",        img.getTitulo());
        body.put("descripcion",  img.getDescripcion());
        body.put("palabrasClave",     img.getPalabrasClave());
        body.put("autor",       img.getAutor());
        body.put("creador",      img.getCreador());
        body.put("fechaCreacion",      img.getFechaCreacion() == null ? null : img.getFechaCreacion().toString());
        body.put("fechaAlta",       img.getFechaAlta()     == null ? null : img.getFechaAlta().toString());
        body.put("nombreFichero",     img.getNombreFichero());
        body.put("nombreOriginal", img.getNombreOriginal());
        return body;
    }
 
 
 /**
 * GET method to search images by title
 * @param title
 * @return
 */
 @Path("searchTitle/{title}")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchByTitle (@PathParam("title") String title) {
     try {
        ImagenDAO dao = new ImagenDAO();
        try {
            List<Imagen> list = dao.search(title, null, null, null);
            List<Map<String, Object>> out = new ArrayList<>(list.size());
            for (Imagen img : list) out.add(toBody(img));
            return Response.ok(out).build();
        } finally {
            dao.close();
        }
        
        
     } catch (Exception e) {
        Map<String, Object> err = new HashMap<>();
        err.put("code", 11);
        return Response.serverError().entity(err).build(); 
     }
 }
 /**
 * GET method to search all images
 * @return
 */
 @Path("allImages")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response allImages (){
     try {
         ImagenDAO dao = new ImagenDAO();
         try {
             List<Imagen> list = dao.findAll();
             List<Map<String, Object>> out = new ArrayList<>(list.size());
             for (Imagen img : list) {
                 out.add(toBody(img));
             }
             return Response.ok(out).build();
         } finally {
             dao.close();
         }
     } catch (Exception e) {
         Map<String, Object> err = new HashMap<>();
         err.put("code", 11);
         return Response.serverError().entity(err).build();
     }
 }
 
 
 /**
 * GET method to search images by creation date. Date format should be
 * yyyy-mm-dd
 * @param date
 * @return
 */
 @Path("searchCreationDate/{date}")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchByCreationDate (@PathParam("date") String date){
     try {
         ImagenDAO dao = new ImagenDAO();
         try {
             List<Imagen> list = dao.search(null, null, null, date);
             List<Map<String, Object>> out = new ArrayList<>(list.size());
             for (Imagen img : list) {
                 out.add(toBody(img));
             }
             return Response.ok(out).build();
         } finally {
             dao.close();
         }
     } catch (Exception e) {
         Map<String, Object> err = new HashMap<>();
         err.put("code", 11);
         return Response.serverError().entity(err).build();
     }
 }
 /**
 * GET method to search images by author
 * @param author
 * @return
 */
 @Path("searchAuthor/{author}")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchByAuthor (@PathParam("author") String author) {
     try {
         ImagenDAO dao = new ImagenDAO();
         try {
             List<Imagen> list = dao.search(null, author, null, null);
             List<Map<String, Object>> out = new ArrayList<>(list.size());
             for (Imagen img : list) {
                 out.add(toBody(img));
             }
             return Response.ok(out).build();
         } finally {
             dao.close();
         }
     } catch (Exception e) {
         Map<String, Object> err = new HashMap<>();
         err.put("code", 11);
         return Response.serverError().entity(err).build();
     }
 }
 /**
 * GET method to search images by keyword
 * @param keywords
 * @return
 */
 @Path("searchKeywords/{keywords}")
 @GET
 @Produces(MediaType.APPLICATION_JSON)
 public Response searchByKeywords (@PathParam("keywords") String keywords) {
     try {
         ImagenDAO dao = new ImagenDAO();
         try {
             List<Imagen> list = dao.search(null, null, keywords, null);
             List<Map<String, Object>> out = new ArrayList<>(list.size());
             for (Imagen img : list) {
                 out.add(toBody(img));
             }
             return Response.ok(out).build();
         } finally {
             dao.close();
         }
     } catch (Exception e) {
         Map<String, Object> err = new HashMap<>();
         err.put("code", 11);
         return Response.serverError().entity(err).build();
     }
 }
}
