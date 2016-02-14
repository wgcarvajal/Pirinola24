package pirinola24.com.pirinola24.modelo;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by geovanny on 8/01/16.
 */
public class Producto
{
    public static String TABLA="Producto";
    public static String TABLASUBCATEGORIA="Subcategoria";
    public static String NOMBRE="prodnombre";
    public static String DESCRIPCION="proddescripcion";
    public static String TBLSUBCATEGORIA_NOMBRE="subcatnombre";
    public static String SUBCATEGORIA="subcategoria";
    public static String ID="objectId";


    private String id;
    private String nombre;
    private int precio;
    private String descripcion;
    private String subcategoria;
    private String urlImagen;
    private String urlImagenPedido;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public String getUrlImagenPedido() {
        return urlImagenPedido;
    }

    public void setUrlImagenPedido(String urlImagenPedido) {
        this.urlImagenPedido = urlImagenPedido;
    }







}
