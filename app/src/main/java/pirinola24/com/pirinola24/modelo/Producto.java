package pirinola24.com.pirinola24.modelo;

import android.graphics.Bitmap;

import com.parse.ParseFile;

/**
 * Created by geovanny on 8/01/16.
 */
public class Producto
{
    public static String TABLA="Producto";
    public static String POSICION="posicion";
    public static String PRECIO="precio";
    public static String IMAGEN="imgFile";
    public static String NOMBRE="prodnombre";
    public static String DESCRIPCION="proddescripcion";
    public static String SUBCATEGORIA="subcategoria";
    public static String ID="objectId";


    private String objectId;
    private int precio;
    private String proddescripcion;
    private String prodnombre;
    private String subcategoria;
    private String imgFile;

    public String getImgFile() {
        return imgFile;
    }

    public void setImgFile(String imgFile) {
        this.imgFile = imgFile;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getProdnombre() {
        return prodnombre;
    }

    public void setProdnombre(String prodnombre) {
        this.prodnombre = prodnombre;
    }

    public String getProddescripcion() {
        return proddescripcion;
    }

    public void setProddescripcion(String proddescripcion) {
        this.proddescripcion = proddescripcion;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
