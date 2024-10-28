//Aleix Suriñach

/** @file NodeAixeta.java
 @brief Classe NodeAixeta
 */

/** @class NodeAixeta
 @brief Node d'una xarxa de distribució d'aigua
 @author Aleix Suriñach
 */

public class NodeAixeta {

    private final String _id;
    private final Coordenades _coordenades;
    private boolean _aixetaOberta;

    /** @brief Crea un node Aixeta amb els paràmetres passats
     @pre cert
     @post S'ha creat un nou node amb identificador id i coordenades c  */
    public NodeAixeta(String id, Coordenades c)
    {
        _id = id;
        _coordenades = c;
        _aixetaOberta = true;
    }

    /** @brief Identificador del node
     @pre cert
     @post Retorna l'identificador del node  */
    public String id()
    {
        return _id;
    }

    /** @brief Coordenades del node
     @pre cert
     @post Retorna les coordenades del node  */
    public Coordenades coordenades()
    {
        return _coordenades;
    }

    /** @brief Estat de l'aixeta
     @pre cert
     @post Diu si l'aixeta del node està oberta  */
    public boolean aixetaOberta()
    {
        return _aixetaOberta;
    }

    /** @brief Obre l'aixeta
     @pre cert
     @post L'aixeta del node està oberta  */
    public void obrirAixeta()
    {
        _aixetaOberta = true;
    }

    /** @brief Tanca l'aixeta
     @pre cert
     @post L'aixeta del node està tancada  */
    public void tancarAixeta()
    {
        _aixetaOberta = false;
    }

    /** @brief Canvia l'estat
     @pre cert
     @post L'aixeta del node cambiarà a l'estat contrari a l'actual  */
    public void canviarEstat()
    {
        _aixetaOberta = !_aixetaOberta;
    }

    /** @brief Obre l'aixeta
     @pre cert
     @post True si el node actual és més petit que el node passat per paràmetre  */
    public boolean idPetit(NodeAixeta node)
    {
        return _id.compareTo(node.id()) == -1;
    }
}
