package pirinola24.com.pirinola24.modelo;

/**
 * Created by geovanny on 21/01/16.
 */
public class Pedido
{
    private String objectId;
    private String estado;
    private String peddireccion;
    private String pedformapago;
    private String pedobservaciones;
    private String pedpersonanombre;
    private String pedtelefono;
    private String ciudad;

    public String getPedtelefono()
    {
        return pedtelefono;
    }

    public void setPedtelefono(String pedtelefono)
    {
        this.pedtelefono = pedtelefono;
    }

    public String getPedpersonanombre()
    {
        return pedpersonanombre;
    }

    public void setPedpersonanombre(String pedpersonanombre)
    {
        this.pedpersonanombre = pedpersonanombre;
    }

    public String getPedobservaciones()
    {
        return pedobservaciones;
    }

    public void setPedobservaciones(String pedobservaciones)
    {
        this.pedobservaciones = pedobservaciones;
    }

    public String getPedformapago()
    {
        return pedformapago;
    }

    public void setPedformapago(String pedformapago)
    {
        this.pedformapago = pedformapago;
    }

    public String getPeddireccion()
    {
        return peddireccion;
    }

    public void setPeddireccion(String peddireccion)
    {
        this.peddireccion = peddireccion;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    public String getObjectId()
    {
        return objectId;
    }

    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }


    public String getCiudad()
    {
        return ciudad;
    }

    public void setCiudad(String ciudad)
    {
        this.ciudad = ciudad;
    }
}
