//Arnau Herrera
import java.lang.Math;

/** @file Coordenades.java
 @brief Classe Coordenades
 */

/** @class Coordenades
 @brief Coordenades geogràfiques (latitud, longitud)
 @author Arnau Herrera
 */

public class Coordenades {

    private double alatitud, alongitud; //en decimal
    public static final double RADI_TERRA = 6371; //km

    /** @brief Crea un objecte coordenades amb longitud i latitud expressada en graus
     @pre 0 <= grausLatitud <= 60, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S',
          0 <= grausLongitud <= 60, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'
        0 <= grausLatitud <= 90, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S',
        0 <= grausLongitud <= 180, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'

     @post Crea unes coordenades amb els valors indicats
     @exception "IllegalArgumentException" si es viola la precondició*/
    public Coordenades(int grausLatitud, int minutsLatitud, float segonsLatitud, char direccioLatitud, int grausLongitud, int minutsLongitud, float segonsLongitud, char direccioLongitud)
    {
        //comprovació valors latitud
        if (!((0 <= grausLatitud && grausLatitud <= 90) && (0 <= minutsLatitud && minutsLatitud <= 60)
                && (0 <= segonsLatitud && segonsLatitud <= 60) && (direccioLatitud == 'N' || direccioLatitud == 'S'))) {
            throw new IllegalArgumentException("Valors de la latitud incorrectes: " + grausLatitud +":"+minutsLatitud+":"+segonsLatitud+":"+direccioLatitud);
        }

        //comprovació valors longitud
        if(!((0 <= grausLongitud && grausLongitud <= 180) && (0 <= minutsLongitud && minutsLongitud <= 60)
                    && (0 <= segonsLongitud && segonsLongitud <= 60) && (direccioLongitud == 'E' || direccioLongitud == 'W'))) {
            throw new IllegalArgumentException("Valors de la longitud incorrectes: " + grausLongitud + ":" + minutsLongitud + ":" + segonsLongitud + ":" + direccioLongitud);
        }

        //Guardar atributs
        alatitud = grausLatitud + (minutsLatitud/60.0) + (segonsLatitud/3600.0);
        if (direccioLatitud == 'S') {
            alatitud *= -1;
        }

        alongitud = grausLongitud + (minutsLongitud/60.0) + (segonsLongitud/3600.0);
        if (direccioLongitud == 'W') {
            alongitud *= -1;
        }
    }

    /** @brief Crea un objecte coordenades amb longitud i latitud expressada en radians
     @pre -90 <= latitud <= 90, -180 <= longitud <= 180
     @post Crea unes coordenades amb els valors indicats
     @exception "IllegalArgumentException" si es viola la precondició*/
    public Coordenades(float latitud, float longitud)
    {
        if (!(-90 <= latitud && latitud <= 90)) {
            throw new IllegalArgumentException("Valors de la latiud incorrectes");
        }

        if(!(-180 <= longitud && longitud <= 180)) {
            throw new IllegalArgumentException("Valors de la longitud incorrectes");
        }

        alatitud = latitud;
        alongitud = longitud;
    }

    /** @brief Distancia entre coordenades
     @pre ---
     @post Retorna la distància entre aquestes coordenades i c, expressada en km
    */
    public double distancia(Coordenades c)
    {
        //Convertim a radiants
        double latitud1 = Math.toRadians(alatitud);
        double longitud1 = Math.toRadians(alongitud);
        double latitud2 = Math.toRadians(c.alatitud);
        double longitud2 = Math.toRadians(c.alongitud);

        //Càlcul distància
        double latitudTotal = latitud2 - latitud1;
        double longitudTotal = longitud2 - longitud1;
        double a = Math.sin(latitudTotal / 2) * Math.sin(latitudTotal / 2) +
                Math.cos(latitud1) * Math.cos(latitud2) *
                        Math.sin(longitudTotal / 2) * Math.sin(longitudTotal / 2);

        return RADI_TERRA * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }


    /** @brief Latitud
     @pre ---
     @post Retorna la latitud
     */
    public double getAlatitud()
    {
        return alatitud;
    }

    /** @brief Longitud
     @pre ---
     @post Retorna la longitud
     */
    public double getAlongitud()
    {
        return alongitud;
    }

}
