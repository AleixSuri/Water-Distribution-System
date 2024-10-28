//Aleix Suriñach

/** @file Canonada.java
 @brief Classe Canonada
 */

/** @class Canonada
 @brief Canonada de la xarxa de distribució d'aigua
 @author Aleix Suriñach
 */

public class Canonada {

    private final NodeAixeta _node1;
    private final NodeAixeta _node2;
    private final float _capacitat;

    /** @brief Crea una Canonada amb els paràmetres passats
     @pre capacitat > 0
     @post Crea una canonada que connecta node1 i node2 amb la capacitat indicada
     */
    public Canonada(NodeAixeta node1, NodeAixeta node2, float capacitat)
    {
        _node1 = node1;
        _node2 = node2;
        _capacitat = capacitat;
    }

    /** @brief Node entrada
     @pre ---
     @post Retorna el node d'inici de la canonada
     */
    public NodeAixeta node1()
    {
        return _node1;
    }

    /** @brief Node sortida
     @pre ---
     @post Retorna el node de destí de la canonada
     */
    public NodeAixeta node2()
    {
        return _node2;
    }

    /** @brief Capacitat
     @pre capacitat > 0
     @post Retorna la capacitat de la canonada
     */
    public float capacitat()
    {
        return _capacitat;
    }
}

