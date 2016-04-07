package pirinola24.com.pirinola24.modelo;

import java.io.Serializable;

public class Ciudad implements Serializable
{
    private String objectId;
    private String nombre;
    private String email;

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
