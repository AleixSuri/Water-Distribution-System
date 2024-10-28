//Aleix Suriñach

/** @file Connexio.java
 @brief Classe Connexio
 */

/** @class Connexio
 @brief Node de connexió d'una xarxa de distribució d'aigua
 @author Aleix Suriñach
 */

public class Connexio extends NodeAixeta {

    /** @brief Crea un node Connexio amb els paràmetres passats
     @pre cert
     @post S'ha creat un nou node de connexió amb identificador id i coordenades c
     */
    public Connexio(String id, Coordenades c)
    {
        super(id ,c);
    }

}
