/**
 @mainpage BeWater
 @author Aleix Suriñach Cobos i Arnau Herrera
 @version 1
 @date 18/05/2024

 Aquesta és la pàgina web creada a partir de Doxygen i el codi del programa.

 El que es pot trobar en aquesta pàgina web, és l'explicació de les classes (utilitat i el perquè tenen aquests atributs),
 així com les precondicions i postcondicions que han de tenir les accions i funcions del codi principal.
 */

/** @file BeWater.java
 @brief Classe BeWater
 */

/** @class BeWater
 @brief Programa principal de simulació de xarxes de distribució d'aigua
 */

public abstract class BeWater {

    public static void main(String[] args) {
        SimuladorModeText simulador = new SimuladorModeText();
        System.out.println("Be water, my friend");
        System.out.println(args[0]);
        simulador.simular(args[0], args[1]);

    }
}

