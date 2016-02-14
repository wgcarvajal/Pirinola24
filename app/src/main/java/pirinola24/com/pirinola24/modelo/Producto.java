package pirinola24.com.pirinola24.modelo;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by geovanny on 8/01/16.
 */
public class Producto
{
    public static String TABLA="Producto";
    public static String TABLAIMAGEN="Imagen";
    public static String TABLAPRECIO="Precio";
    public static String TABLACATEGORIA="Categoria";
    public static String TABLASUBCATEGORIA="Subcategoria";
    public static String NOMBREING="prodnombre";
    public static String NOMBREESP="prodnombreesp";
    public static String DESCRIPCIONING="proddescripcion";
    public static String DESCRIPCIONESP="proddescripcionesp";
    public static String CATEGORIANOMBRE="catnombre";
    public static String TBLSUBCATEGORIA_CATEGORIA="categoria";
    public static String TBLSUBCATEGORIA_NOMBRE="subcatnombre";
    public static String TBLSUBCATEGORIA_NOMBREESP="subcatnombresp";
    public static String SUBCATEGORIA="subcategoria";
    public static String TBLPRECIO_PRODUCTO="producto";
    public static String TBLPRECIO_FECHACREACION="createdAt";
    public static String TBLPRECIO_VALOR="prevalor";
    public static String ID="objectId";
    public static String TBLIMAGEN_PRODUCTO="producto";
    public static String TBLIMAGEN_IMGFILE="imgFile";
    public static String TBLPRECIO_PREFECHAFIN="prefechafin";
    public static String TBLSUBCATEGORIA_FECHACREACION="createdAt";


    private String id;
    private String nombreesp;
    private String nombreing;
    private int precio;
    private String descripcionesp;
    private String descripcionIng;
    private String categoria;
    private String subcategoriaing;
    private String subcategoriaesp;
    private String urlImagen;
    private String urlImagenPedido;

    public String getNombreesp()
    {
        return nombreesp;
    }

    public void setNombreesp(String nombreesp)
    {
        this.nombreesp = nombreesp;
    }

    public String getNombreing()
    {
        return nombreing;
    }

    public void setNombreing(String nombreing)
    {
        this.nombreing = nombreing;
    }

    public String getDescripcionesp()
    {
        return descripcionesp;
    }

    public void setDescripcionesp(String descripcionesp)
    {
        this.descripcionesp = descripcionesp;
    }

    public String getDescripcionIng()
    {
        return descripcionIng;
    }

    public void setDescripcionIng(String descripcionIng)
    {
        this.descripcionIng = descripcionIng;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getCategoria()
    {
        return categoria;
    }

    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }



    public String getSubcategoriaing()
    {
        return subcategoriaing;
    }

    public void setSubcategoriaing(String subcategoriaing)
    {
        this.subcategoriaing = subcategoriaing;
    }

    public String getSubcategoriaesp()
    {
        return subcategoriaesp;
    }

    public void setSubcategoriaesp(String subcategoriaesp)
    {
        this.subcategoriaesp = subcategoriaesp;
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
