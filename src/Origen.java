//Aleix Suriñach

/** @file Origen.java
 @brief Classe Origen
 */

/** @class Origen
 @brief Node origen d'una xarxa de distribució d'aigua
 @author Aleix Suriñach
 */

public class Origen extends NodeAixeta {

    private float _cabal;

    /** @brief Crea un node Origen amb els paràmetres passats
     @pre ---
     @post S'ha creat un nou origen amb identificador id i coordenades c
     */
    public Origen(String id, Coordenades c)
    {
        super(id ,c);
        _cabal = 0;
    }

    /** @brief Cabal
     @pre ---
     @post Retorna el cabal d'aigua que surt de l'origen
     */
    public float cabal()
    {
        return _cabal;
    }

    /** @brief Establir Cabal
     @pre cabal >= 0
     @post El cabal d'aigua que surt de l'origen és cabal
     @exception "IllegalArgumentException" si cabal < 0
     */
    public void establirCabal(float cabal)
    {
        if (cabal < 0){
            throw new IllegalArgumentException("Cabal negatiu, no vàlid");
        }

        _cabal = cabal;
    }

}
