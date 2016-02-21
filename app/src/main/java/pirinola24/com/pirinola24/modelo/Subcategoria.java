package pirinola24.com.pirinola24.modelo;

/**
 * Created by geovanny on 18/01/16.
 */
public class Subcategoria
{
    public static final int CONDESCRIPCION=1;
    public static final int SINDESCRIPCION=2;
    public static final int ANUNCIO=3;

    public static String TABLA="Subcategoria";
    public static String ID="objectId";
    public static String IMGTITULO="imgTitulo";
    public static String POSICION="posicion";
    public static String NOMBRE="subcatnombre";
    public static String TIPOFRAGMENT="tipoFragment";

    private String nombre;
    private String urlImagenTitulo;
    private int tipoFragment;


    public void Subcategoria()
    {

    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUrlImagenTitulo() {
        return urlImagenTitulo;
    }

    public void setUrlImagenTitulo(String urlImagenTitulo) {
        this.urlImagenTitulo = urlImagenTitulo;
    }

    public int getTipoFragment()
    {
        return tipoFragment;
    }

    public void setTipoFragment(int tipoFragment)
    {
        this.tipoFragment=tipoFragment;
    }


}
