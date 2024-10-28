//Arnau Herrera
import java.util.*;

/** @file Terminal.java
 @brief Classe Node Terminal
 */

/** @class Node Terminal
 @brief Node Terminal d'una Xarxa de distribució d'aigua
 @author Arnau Herrera
 */

public class Terminal extends NodeAixeta {

    private final float _demandaPunta;
    private float _demandaActual;

    /** @brief Crea un node Terminal amb els paràmetres passats
     @pre cert
     @post S'ha creat un nou terminal amb identificador id, coordenades c i demanda punta demanda en l/s  */
    public Terminal(String id, Coordenades c, float demandaPunta)
    {
        super(id ,c);
        _demandaPunta = demandaPunta;
        _demandaActual = 0;
    }

    /** @brief Demanda Punta
     @pre cert
     @post Retorna la demanda punta d'aigua del terminal */
    public float demandaPunta()
    {
        return _demandaPunta;
    }

    /** @brief Demanda Actual
     @pre cert
     @post Retorna la demanda actual d'aigua del terminal */
    public float demandaActual()
    {
        return _demandaActual;
    }

    /** @brief Estableix la demanda actual
     @pre demanda >= 0
     @post La demanda d'aigua actual del terminal és demanda
     @exception "IllegalArgumentException" si demanda < 0*/
    public void establirDemandaActual(float demanda)
    {
        if (demanda < 0){
            throw new IllegalArgumentException("Demanda negativa, no vàlida");
        }

        _demandaActual = demanda;
    }

}
