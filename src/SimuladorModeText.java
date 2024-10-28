/** @file SimuladorModeText.java
 @brief Classe SimuladorModeText
 */

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import java.util.*;
import java.io.*;

/** @class SimuladorModeText
 @brief Simula les operacions de construcció, modificació i consulta d'una xarxa de distribució d'aigua a partir d'un fitxer de text.
 @author Arnau Herrera
 */

public class SimuladorModeText {

    private int comptadorLinies = 0; //comptador de línies
    //Atributs
    private final Xarxa _xarxa = new Xarxa(); //Xarxa on es van implementant les modificacions que indica l'usuari


    /** @brief Processarà el fitxer de text, localitzant els nodes corresponents a la xarxa a partir dels seus identificadors,
                i cridant els mètodes corresponents de la classe Xarxa i GestorXarxes per realitzar les operacions descrites al
                fitxer. Si alguna operació no es pot realitzar, es mostrarà un missatge d'error per la sortida estàndard.
     @pre Fitxer és el nom d'un fitxer de text que conté una seqüència d'operacions a realitzar sobre una xarxa de distribució d'aigua
     @post S'han realitzat les operacions descrites al fitxer sobre la xarxa de distribució d'aigua
     */
    public void simular(String fitxerEntrada, String fitxerSortida)
    {

        try {
            //Lectura i escriptura fitxer
            BufferedReader reader = new BufferedReader(new FileReader(fitxerEntrada));
            PrintStream fileOut = new PrintStream(fitxerSortida);
            System.setOut(fileOut); //Redirigeix la sortida cap el fitxer

            String linea= reader.readLine();
            String nextCase="";
            while (linea != null) {
                comptadorLinies++;
                switch (linea) {
                    case "terminal":
                        lecturaTerminal(reader);
                        break;

                    case "connexio":
                        lecturaConnexio(reader);
                        break;

                    case "origen":
                        lecturaOrigen(reader);
                        break;

                    case "connectar":
                        lecturaConnectar(reader);
                        break;

                    case "abonar":
                        //lectura entrada
                        comptadorLinies++;
                        String idClient = reader.readLine();
                        Terminal terminal = llegirNodeTerminal(reader);
                        //abonar
                        _xarxa.abonar(idClient, terminal);
                        break;

                    case "tancar":
                        NodeAixeta AixetaT = llegirNodeAixeta(reader);
                        _xarxa.tancarAixeta(AixetaT);
                        break;

                    case "obrir":
                        NodeAixeta AixetaO = llegirNodeAixeta(reader);
                        _xarxa.obrirAixeta(AixetaO);
                        break;

                    case "backtrack":
                        comptadorLinies++;
                        int nPassos= Integer.parseInt(reader.readLine());
                        _xarxa.recular(nPassos);
                        break;

                    case "cabal":
                        Origen nodeOrigen = llegirNodeOrigen(reader);
                        comptadorLinies++;
                        Float cabal = Float.parseFloat(reader.readLine());
                        _xarxa.establirCabal(nodeOrigen,cabal);
                        break;

                    case "demanda":
                        lecturaDemanda(reader);
                        break;

                    case "cicles":
                        Origen nodeOrigenCicle = llegirNodeOrigen(reader);
                        //comprobació cicles
                        if(GestorXarxes.teCicles(_xarxa, nodeOrigenCicle)){
                            System.out.println(nodeOrigenCicle.id() + " te cicles");
                        }
                        else{
                            System.out.println(nodeOrigenCicle.id() + " no te cicles");
                        }
                        break;

                    case "arbre":
                        Origen nodeOrigenArbre = llegirNodeOrigen(reader);
                        //comprobació arbre
                        if(GestorXarxes.esArbre(_xarxa, nodeOrigenArbre)){
                            System.out.println(nodeOrigenArbre.id() + " es un arbre");
                        }
                        else{
                            System.out.println(nodeOrigenArbre.id() + " no es un arbre");
                        }
                        break;

                    case "cabal minim":
                        lecturaCabalMinim(reader);
                        break;

                    case "exces cabal":
                        nextCase = lecturaExcesCabal(reader);
                        break;

                    case "situacio":
                        nextCase = lecturaSituacio(reader);
                        break;

                    case "cabal abonat":
                        //lectura i comprobació client
                        comptadorLinies++;
                        String IdClient =  reader.readLine();
                        System.out.println("cabal abonat");
                        if(!(_xarxa.existeixClient(IdClient))){
                            throw new NoSuchElementException("No s'ha trobat el client: " + IdClient);
                        }
                        //Comprovem que no té cicles
                        Origen nodeOrigenClient = buscarOrigen(_xarxa.retornAbonat(IdClient));
                        if(GestorXarxes.teCicles(_xarxa,nodeOrigenClient))
                            throw new IllegalArgumentException("La xarxa té cicles i per tant no es pot calcular el cabal abonat");

                        //calcular cabal
                        Float cabalClient = _xarxa.cabalAbonat(IdClient);
                        System.out.println(cabalClient);
                        break;
                        
                    case "proximitat":
                        nextCase = lecturaProximitat(reader);
                        break;

                    case "dibuix":
                        //llegim el node
                        Origen node = llegirNodeOrigen(reader);
                        //dibuixar
                        _xarxa.dibuixar(node);
                        break;

                    case "max-flow":
                        //llegim el node
                        Origen nodeOrigenMaxFlow = llegirNodeOrigen(reader);
                        //Dibuixar flux màxim
                        GestorXarxes.fluxMaxim(_xarxa,nodeOrigenMaxFlow);
                        break;

                    case "":
                        break;

                    default:
                        System.out.println("Opció no vàlida: " + linea);
                }

                if(linea.equals("proximitat") || linea.equals("exces cabal") || linea.equals("situacio")){
                    linea = nextCase;
                }
                else linea = reader.readLine();
            }

            reader.close();

        }
        catch (IOException e)
        {
            System.out.println("Error!!!: " + e.getMessage());
        }
        catch (NumberFormatException e)
        {
            String error = e.getMessage().split("\"")[1]; //ens quedem amb l'entrada que causa l'error, no amb tot l'error.
            System.out.println("Error format d'entrada a l'opció de la línia " + comptadorLinies + ": s'esperava un real. S'ha trobat \"" + error + "\".");
        }
        catch (IllegalArgumentException e)
        {
            if(e.getMessage().contains("latitud")||e.getMessage().contains("longitud")){
                comptadorLinies++;
                System.out.println("Coordenada errònia a la línia " + comptadorLinies + ": " + e.getMessage());
            }
            else
                System.out.println("Error de configuració a la línia " + comptadorLinies + ": " + e.getMessage());
        }
        catch (NoSuchElementException e) {
            System.out.println("Identificador inexistent: " + e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /** @brief A partir d'un string es classifica la informació i es crea una coordenada de la classe Coordenades
     @pre cord ha de tenir el seguent format: XX:XX:XXN,X:XX:XXE
     @post Retorna la coordenada llegida amb el format demanat per la classe Coordenades
     */
    private Coordenades lecturaCoordenada(String cord)
    {
        String[] partsCord = cord.split(","); //divideix la cadena en latitud i longitud
        String Latitud = partsCord[0];
        String Longitud = partsCord[1];

        String[] partsCordLatitud = Latitud.split("[:]"); //separem cada dada
        String[] partsCordLongitud = Longitud.split("[:]");
        char direccioLat = partsCordLatitud[2].charAt(partsCordLatitud[2].length() - 1); //guardem direcció latitud
        char direccioLon = partsCordLongitud[2].charAt(partsCordLongitud[2].length() - 1); //guardem direcció longitud

        return new Coordenades(
                Integer.parseInt(partsCordLatitud[0]), Integer.parseInt(partsCordLatitud[1]), // id y minuts latitud
                Float.parseFloat(partsCordLatitud[2].substring(0, partsCordLatitud[2].length() - 1)), direccioLat, // demanda y direcció latitud
                Integer.parseInt(partsCordLongitud[0]), Integer.parseInt(partsCordLongitud[1]), // id y minuts longitud
                Float.parseFloat(partsCordLongitud[2].substring(0, partsCordLongitud[2].length() - 1)), direccioLon); // demanda y direcció longitud
    }


    /** @brief Es llegeix l'id del node i si es troba dins la xarxa es retorna el node que concorda amb aquell id. Altrament es llença una excepció
     @pre --
     @post Retorna el node aixeta llegit
     */
    private NodeAixeta llegirNodeAixeta(BufferedReader reader) throws IOException, NoSuchElementException {
        String idNodeAixeta = reader.readLine();
        comptadorLinies++;
        NodeAixeta nodeA = _xarxa.node(idNodeAixeta);
        if(nodeA == null){
            throw new NoSuchElementException("No s'ha trobat el node aixeta " + idNodeAixeta);
        }
        return nodeA;
    }


    /** @brief Es llegeix l'id del node i si es troba dins la xarxa i pertany a la classe Origen, es retorna el node que concorda amb aquell id.
     *     Altrament es llença una excepció
     @pre --
     @post Retorna el node aixeta llegit
     */
    private Origen llegirNodeOrigen(BufferedReader reader) throws IOException, NoSuchElementException {
        String idNodeOrigen = reader.readLine();
        comptadorLinies++;
        NodeAixeta nodeO = _xarxa.node(idNodeOrigen);
        if(nodeO == null){
            throw new NoSuchElementException("No s'ha trobat el node aixeta " + idNodeOrigen);
        }
        if(!(nodeO instanceof Origen)){
            throw new IllegalArgumentException("El node " + idNodeOrigen + " no es un origen");
        }
        return (Origen) nodeO;
    }


    /** @brief Es llegeix l'id del node i si es troba dins la xarxa i pertany a la classe Terminal es retorna el node que concorda amb aquell id.
     * Altrament es llença una excepció
     @pre --
     @post Retorna el node aixeta llegit
     */
    private Terminal llegirNodeTerminal(BufferedReader reader) throws IOException, NoSuchElementException {
        String idNodeTerminal = reader.readLine();
        comptadorLinies++;
        Terminal nodeT = (Terminal) _xarxa.node(idNodeTerminal);
        if(nodeT == null){
            throw new NoSuchElementException("No s'ha trobat el node aixeta " + idNodeTerminal);
        }
        return nodeT;
    }


    /** @brief Crea un node Terminal llegint els seus atributs i s'afegeix a la xarxa
     @pre --
     @post Afegeix el nou node Terminal a la xarxa
     */
    private void lecturaTerminal(BufferedReader reader) throws IOException, IllegalArgumentException{
        String idTerminal = reader.readLine(); //guarda id
        comptadorLinies++;
        Coordenades cordTerminal = lecturaCoordenada(reader.readLine()); //lectura cordenada
        String demandaLectura = reader.readLine();
        float demandaPunta = Float.parseFloat(demandaLectura); //guarda demanda

        _xarxa.afegir(new Terminal(idTerminal, cordTerminal, demandaPunta)); //afageix terminal a la xarxa
        comptadorLinies+=2;
    }


    /** @brief Crea un node Connexió llegint els seus atributs i s'afegeix a la xarxa
     @pre --
     @post Afegeix el nou node connexió a la xarxa
     */
    private void lecturaConnexio(BufferedReader reader) throws IOException, IllegalArgumentException{
        String idConnexio = reader.readLine(); //guarda id
        comptadorLinies++;
        Coordenades cordConnexio = lecturaCoordenada(reader.readLine()); //lectura cordenada

        _xarxa.afegir(new Connexio(idConnexio, cordConnexio)); //afageix connexió a la xarxa
        comptadorLinies++;
    }


    /** @brief Crea un node Origen llegint els seus atributs i s'afegeix a la xarxa
     @pre --
     @post Afegeix el nou node origen a la xarxa
     */
    private void lecturaOrigen(BufferedReader reader) throws IOException, IllegalArgumentException{
        String idOrigen = reader.readLine(); //guarda id
        comptadorLinies++;
        Coordenades cordOrigen = lecturaCoordenada(reader.readLine()); //lectura cordenada

        _xarxa.afegir(new Origen(idOrigen, cordOrigen)); //afageix connexió a la xarxa
        comptadorLinies++;
    }


    /** @brief Llegeix l'id de dos nodes i crida la funció connectar de la classe xarxa per crear una canonada entre aquests dos nodes.
     @pre --
     @post Connecta els dos nodes llegits pel reader
     */
    private void lecturaConnectar(BufferedReader reader) throws IOException, IllegalArgumentException{
        NodeAixeta node1 = llegirNodeAixeta(reader);
        NodeAixeta node2 = llegirNodeAixeta(reader);
        comptadorLinies++;
        _xarxa.connectarAmbCanonada(node1, node2, Float.parseFloat(reader.readLine()));
    }


    /** @brief Llegeix un node Terminal i la seva demanda i ho actualitza a la xarxa cridant la funció establirDemanda.
     @pre --
     @post Estableix una demanda pel node Terminal entrat pel reader
     */
    private void lecturaDemanda(BufferedReader reader) throws IOException, IllegalArgumentException{
        Terminal nodeTerminal = llegirNodeTerminal(reader);
        comptadorLinies++;
        float demandaTerminal = Float.parseFloat(reader.readLine());
        if(nodeTerminal.demandaPunta() < demandaTerminal){
            throw new IllegalArgumentException("La demanda indicada (" + demandaTerminal
                    + ") és superior a la demanda punta del node (" + nodeTerminal.demandaPunta() + ")");
        }
        _xarxa.establirDemanda(nodeTerminal,demandaTerminal);
    }


    /** @brief Llegeix el node Origen entrat pel reader i el percentatge de cabal. A partir d'aquestes dades busca el cabal mínim
     * per satisfer la demanda entrada.
     @pre --
     @post Mostra el cabal mínim que pot haver a la xarxa des del node Origen i el tant per cent entrat.
     */
    private void lecturaCabalMinim(BufferedReader reader) throws IOException, IllegalArgumentException{

        //lectura node
        Origen nodeOrigenCabalMin = llegirNodeOrigen(reader);

        //lectura cabal mínim
        comptadorLinies++;
        String percentatgeDemandaSatisfet = reader.readLine(); //amb %
        float DemandaSatisfet = Float.parseFloat(String.valueOf(percentatgeDemandaSatisfet.replace("%", ""))); //sense %

        if(DemandaSatisfet < 0){
            throw new IllegalArgumentException("La demanda indicada (" + percentatgeDemandaSatisfet
                    + ") ha de ser superior a 0");
        }
        //comprobar cicles
        if(!GestorXarxes.teCicles(_xarxa,nodeOrigenCabalMin)) {
            //impimim valor cabal mínim
            System.out.println("cabal minim\n" +
                    GestorXarxes.cabalMinim(_xarxa, nodeOrigenCabalMin, DemandaSatisfet));
        }
        else throw new IllegalArgumentException("La xarxa actual té cicles i no es pot calcular el cabal mínim");

    }


    /** @brief Va llegint terminals i es van guardant tots els que tenen excés amb la crida de la funció de GestorXarxes
     * fins que es llegeixi un case o s'hagi arribat al final del fitxer que és quan es mostren tots els terminals trobats
     @pre --
     @post Retorna la línia llegida que ja no fa refèrencia a aquesta funció i mostra totes les terminals entrades amb excés de cabal
     */
    private String lecturaExcesCabal(BufferedReader reader) throws IOException, IllegalArgumentException {
        System.out.println("exces cabal");
        Set<Canonada> cjtCanonades = new HashSet<>();
        Set<Canonada> excesCanonades = new HashSet<>();

        //bucle que va llegint les terminals fins trobar un altre case
        String linia= reader.readLine();
        while (!CambiarOpcio(linia) && linia!=null && !linia.trim().isEmpty()) {
            comptadorLinies++;
            String[] canonada = linia.split("-"); //Dividim els dos nodes
            String IdNode1 = canonada[0];  //Guardem node 1
            String IdNode2 = canonada[1];  //Guardem node 2
            //comprobació existència nodes
            NodeAixeta NodeAixeta1 = _xarxa.node(IdNode1);
            NodeAixeta NodeAixeta2 = _xarxa.node(IdNode2);
            if (NodeAixeta1 == null) {
                throw new NoSuchElementException("No existeix un node amb ID: " + IdNode1);
            }
            if (NodeAixeta2 == null) {
                throw new NoSuchElementException("No existeix un node amb ID: " + IdNode2);
            }

            //mirar quin node és origen i buscar excés
            if(NodeAixeta1 instanceof Origen){
                if(!GestorXarxes.teCicles(_xarxa,(Origen) NodeAixeta1)) {//comprovar no cicles
                    //buscar canonada i afegir
                    cjtCanonades.add(trobarCanonada(_xarxa, (Origen) NodeAixeta1, NodeAixeta1, NodeAixeta2));
                    excesCanonades.addAll(GestorXarxes.excesCabal(_xarxa, (Origen) NodeAixeta1, cjtCanonades));
                }
                else throw new IllegalArgumentException("La xarxa actual té cicles i no es pot calcular l'excés de cabal");
            }
            else if(NodeAixeta2 instanceof Origen){
                if(!GestorXarxes.teCicles(_xarxa,(Origen) NodeAixeta2)) {//comprovar no cicles
                    //buscar canonada i afegir
                    cjtCanonades.add(trobarCanonada(_xarxa, (Origen) NodeAixeta2, NodeAixeta2, NodeAixeta1));
                    excesCanonades.addAll(GestorXarxes.excesCabal(_xarxa, (Origen) NodeAixeta2, cjtCanonades));
                }
                else throw new IllegalArgumentException("La xarxa actual té cicles i no es pot calcular l'excés de cabal");
            }
            else {//buscar node Origen
                Origen nodeOrigen = buscarOrigen(NodeAixeta1);
                if(!GestorXarxes.teCicles(_xarxa,nodeOrigen)) {//comprovar no cicles
                    cjtCanonades.add(trobarCanonada(_xarxa, nodeOrigen, NodeAixeta1, NodeAixeta2));
                    excesCanonades.addAll(GestorXarxes.excesCabal(_xarxa, nodeOrigen, cjtCanonades));
                }
                else throw new IllegalArgumentException("La xarxa actual té cicles i no es pot calcular l'excés de cabal");
            }

            linia = reader.readLine(); //llegeix el possible terminal o case
        }
        //mostrar canonades amb excés

        while (!excesCanonades.isEmpty()) {
            Canonada canonada = excesCanonades.iterator().next();
            System.out.println(canonada.node1().id() + "-" + canonada.node2().id());
            excesCanonades.remove(canonada);
        }

        return linia;
    }

    /** @brief Retorna el node Origen el qual li proporciona aigua al node aixeta entrat.
     @pre NodeAixeta pertany a la xarxa
     @post Retorna el node Origen que està connectat amb el nodeAixeta aixeta
     */
    private Origen buscarOrigen(NodeAixeta aixeta){
        Origen nodeOrigen = null;
        boolean trobat = false;
        Iterator<Canonada> ItCanonada = _xarxa.entrades(aixeta);
        while(ItCanonada.hasNext() && !trobat){ //mentre tingui entrades i no s'hagi trobat
            Canonada canonada = ItCanonada.next();
            if(canonada.node1() instanceof Origen){//si és node Origen guardem i parem
                nodeOrigen = (Origen) canonada.node1();
                trobat = true;
            }
            else {
                nodeOrigen = buscarOrigen((NodeAixeta) canonada.node1()); //busquem el node Origen del node de dalt
                if (nodeOrigen != null) {//si trobat parem
                    trobat = true;
                }
            }
        }

        return nodeOrigen;
    }

    /** @brief Busca i retorna l'aresta que uneix els dos nodes (nodeAixeta1 i nodeAixeta2)
     @pre --
     @post Retorna la canonada que es troba entre nodeAixeta 1 i nodeAixeta2
     */
    private Canonada trobarCanonada(Xarxa x,Origen nodeOrigen, NodeAixeta nodeAixeta1, NodeAixeta nodeAixeta2) {
        Graph subGraf = x.crearSubGraf(nodeOrigen);
        for (Node node : subGraf) {
            for(Edge aresta : node) {
                Canonada canonada = (Canonada) aresta.getAttribute("canonada");
                NodeAixeta origen = canonada.node1();
                NodeAixeta destino = canonada.node2();

                // Comprobem si és la canonada que busquem
                if ((origen.equals(nodeAixeta1) && destino.equals(nodeAixeta2)) || (origen.equals(nodeAixeta2) && destino.equals(nodeAixeta1))) {
                    return canonada;
                }
            }
        }
        return null; //No s'ha trobat
    }


    /** @brief Va llegint i guardant terminals fins a trobar un nou case o fi de fitxer. Després crida GestorXarxes
     * per trobar les aixetes que s'han de tancar i les mostra.
     @pre --
     @post Retorna la línia llegida que ja no fa referència a aquesta funció i mostra les terminals entrades que s'han de tancar
     */
    private String lecturaSituacio(BufferedReader reader) throws IOException, NoSuchElementException {
        System.out.println("tancar");
        Map<Terminal, Boolean> aiguaArriba = new HashMap<>();

        //bucle que va llegint les terminals fins trobar un altre case
        String terminalAigua = reader.readLine(); //llegeix terminal
        while (terminalAigua!=null && !terminalAigua.trim().isEmpty() &&!CambiarOpcio(terminalAigua)) {
            comptadorLinies++;

            //Separació dades
            String[] partsTerminalAigua = terminalAigua.split(" "); //Dividim terminal i estat aigua
            String IdTerminal = partsTerminalAigua[0];  //Guradem Terminal
            String EstatAigua = partsTerminalAigua[1]; //Guardem estat aigua

            //Comprovació existència terminal
            Terminal nodeTerminal = (Terminal) _xarxa.node(IdTerminal);
            if(nodeTerminal == null){
                throw new NoSuchElementException("No existeix un node Terminal amb el punt indicat: " + IdTerminal);
            }

            //comprovació arbre
            if(!GestorXarxes.esArbre(_xarxa,buscarOrigen(nodeTerminal)))
                throw new IllegalArgumentException("La xarxa actual no té forma d'arbre i no es pot calcular la situació");

            //Guardar terminal
            if(EstatAigua.equals("NO")) {
                aiguaArriba.put(nodeTerminal, false);
            }
            else{
                aiguaArriba.put(nodeTerminal, true);
            }

            terminalAigua = reader.readLine(); //llegeix el possible terminal o case
        }
        //busquem i mostrem les aixetes que s'han de tancar
        Set<NodeAixeta> tancarAiexetes = GestorXarxes.aixetesTancar(_xarxa,aiguaArriba);
        for(NodeAixeta nodeAixeta : tancarAiexetes) {
            System.out.println(nodeAixeta.id());
        }

        return terminalAigua;
    }


    /** @brief Va llegint i guardant nodes aixetes fins a trobar un nou case o fi de fitxer. Després crida GestorXarxes
     * per ordenar per proximitat els nodes i els mostra ordenats.
     @pre --
     @post Retorna la línia llegida que ja no fa referència a aquesta funció i
     */
    private String lecturaProximitat(BufferedReader reader) throws IOException, NoSuchElementException {
        //llegim coordenades
        Coordenades Cord = lecturaCoordenada(reader.readLine());
        Set<NodeAixeta> llistaAixetes = new HashSet<NodeAixeta>();

        //bucle que va llegint les terminals fins trobar un altre case
        String IdnodeAixeta = reader.readLine();
        while (!CambiarOpcio(IdnodeAixeta) && IdnodeAixeta!=null && !IdnodeAixeta.trim().isEmpty()) {
            comptadorLinies++;
            NodeAixeta nodeAixeta = _xarxa.node(IdnodeAixeta);
            if (nodeAixeta == null) {
                throw new NoSuchElementException("No s'ha trobat el node  " + IdnodeAixeta);
            }
            llistaAixetes.add(nodeAixeta);

            IdnodeAixeta = reader.readLine(); //llegeix el possible terminal o case

        }
        //Ordenem els nodes
        List<NodeAixeta> nodesOrdenats = GestorXarxes.nodesOrdenats(Cord, llistaAixetes);

        //Imprimir aixetes ordenades
        System.out.println("proximitat");
        for (int i = 0; i < nodesOrdenats.size(); i++) {
            System.out.println(nodesOrdenats.get(i).id());
        }
        return IdnodeAixeta;
    }


    /** @brief Funció que serveix per indicar si si la línia indica que hi ha un canvi de case
     @pre --
     @post Retorna true si s'ha detectat una de les opcions del programa. Altrament retorna false.
     */
    private boolean CambiarOpcio(String linia){
        if(linia!=null) {
            String[] opcions = {"terminal", "connexio", "origen", "connectar", "abonar", "tancar", "obrir", "backtrack", "cabal", "demanda", "cicles"
                    , "arbre", "cabal minim", "exces cabal", "situacio", "cabal abonat", "proximitat", "dibuix", "max-flow"};
            if (Arrays.asList(opcions).contains(linia.toLowerCase())) return true;
            else return false;
        }
        else return false;
    }

}
