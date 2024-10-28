/** @file GestorXarxes.java
 @brief Classe GestorXarxes
 */

import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;


/** @class GestorXarxes
 @brief Mòdul funcional amb funcions per a la gestió de xarxes de distribució d'aigua
 @author Arnau Herrera
 */
public abstract class GestorXarxes {

    /** @brief Retornarà true si el sub graf creat a partir del node origen té cicles. Altrament retornarà false
     @pre nodeOrigen pertany a la xarxa x
     @post Diu si la component connexa de la xarxa x que conté nodeOrigen té cicles
     */
    public static boolean teCicles(Xarxa x, Origen nodeOrigen) {
        // subGraf a mirar si té cicles
        Graph subGraf = x.crearSubGraf(nodeOrigen);
        // conjunt per saber els nodes visitats
        Set<Node> visitat = new HashSet<>();

        // Recorre tots els nodes
        for (Node node : subGraf) {
            // Per cada node netejar la llista
            visitat.clear();

            // Si es troba cicles retornar true
            if (recorregutCicle(node, visitat)) {
                return true;
            }
        }

        return false;
    }


    /** @brief Algoritme cicles
     @pre ---
     @post Retorna true si el graf conté un cicle a partir d'un algoritme que recorre cada node adjacent, false altrament
     */
    private static boolean recorregutCicle(Node node, Set<Node> visitats) {
        // Si el node actual ja s'ha visitat retorna true, hi ha un cicle
        if (visitats.contains(node)) {
            return true;
        }

        // S'afegeix node actual al conjunt de visitats
        visitats.add(node);

        // Es recorre per totes les arestes que surten del node
        Iterator<Edge> iterator = node.leavingEdges().iterator();
        while (iterator.hasNext()) {
            // S'obté el node adjacent a l'actual
            Node adjacent = iterator.next().getTargetNode();

            // Si hi ha cicles retorna true
            if (recorregutCicle(adjacent, visitats)) {
                return true;
            }
        }

        // Es treu el node actual del conjunt de nodes visitats
        visitats.remove(node);
        return false;
    }


    /** @brief Retornarà true si el sub graf creat a partir del node Origen no té cicles i és connex
     @pre nodeOrigen pertany a la xarxa x
     @post Diu si la component connexa de la xarxa x que conté nodeOrigen és un arbre
     */
    public static boolean esArbre(Xarxa x, Origen nodeOrigen)
    {
        return !teCicles(x, nodeOrigen) && esConnex(x, nodeOrigen);
    }


    /** @brief Es Connex
     @pre ---
     @post retorna true si el graf es connex a partir d'un algoritme dfs
     */
    private static boolean esConnex(Xarxa x, Origen nodeOrigen) {
        Graph subGraf = x.crearSubGraf(nodeOrigen);

        Set<Node> visitats = new HashSet<>();
        dfsConnex(subGraf, subGraf.nodes().iterator().next(), visitats);
        return visitats.size() == subGraf.getNodeCount();
    }


    /** @brief Algoritme DFS connex
     @pre ---
     @post actualitza el Set visitats per dir si hi ha una component connexa
     */
    private static void dfsConnex(Graph graf, Node node, Set<Node> visitats)
    {
        visitats.add(node);
        Iterator<Edge> iterator = node.leavingEdges().iterator();
        while (iterator.hasNext()) {
            Node adjacent = iterator.next().getOpposite(node);
            if (!visitats.contains(adjacent)) {
                dfsConnex(graf, adjacent, visitats);
            }
        }
    }


    /** @brief Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa
     *            de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells
     *           on arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet% de la seva demanda
     @pre nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles, i percentatgeDemandaSatisfet > 0
     @post Retorna el cabal mínim del sub graf creat a partir del nodeOrigen
     */
    public static float cabalMinim(Xarxa x, Origen nodeOrigen, float percentatgeDemandaSatisfet)
    {
        float cabalMin = 0;

        Graph subGraf = x.crearSubGraf(nodeOrigen);
        for(Node node : subGraf.nodes().toList()){
            NodeAixeta nodeAixeta = x.node(node.getId());
            //si terminal oberta i li arriba cabal(no hi ha aixetes tancades per sobre)
            if(nodeAixeta.aixetaOberta() && x.cabal(nodeAixeta)>0 && nodeAixeta instanceof Terminal) {
                cabalMin += x.demanda(nodeAixeta) * (percentatgeDemandaSatisfet/100);
            }
        }

        return cabalMin;
    }


    /** @brief Retorna un set amb totes les canonades les quals tenen una demanda més gran que la seva capacitat màxima
     @pre nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles,
      *       i les canonades de cjtCanonades pertanyen a aquesta component
     @post Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes
      *       terminals de la mateixa component, es sobrepassaria la seva capacitat
     */
    public static Set<Canonada> excesCabal(Xarxa x, Origen nodeOrigen, Set<Canonada> cjtCanonades)
    {
        Set<Canonada> cjtCanonadesCabal = new HashSet<>(); //guardar canonades amb excés
        Set<NodeAixeta> nodeAixetaVisitar = new HashSet<>(); //nodes a visitar
        nodeAixetaVisitar.add(nodeOrigen);

        while(!cjtCanonades.isEmpty() && !nodeAixetaVisitar.isEmpty()){//Mentre quedin nodes a visitar i no s'hagin trobat les canonades
            Iterator<Canonada> sortides = x.sortides(nodeAixetaVisitar.stream().findFirst().get());//agafem el primer node
            nodeAixetaVisitar.remove(nodeAixetaVisitar.stream().findFirst().get());

            while(sortides.hasNext() && !cjtCanonades.isEmpty()){//per totes les canonades d'un node
                Canonada canonada = sortides.next();

                if(cjtCanonades.contains(canonada)){//si trobem la canonada
                    cjtCanonades.remove(canonada); //ja no l'hem de buscar més

                    if(canonada.capacitat() < x.demanda(canonada.node2())){//guardem si té excés
                        cjtCanonadesCabal.add(canonada);
                    }
                }
                if(!(canonada.node2() instanceof Terminal)) {//afegim node a visitar si no és terminal
                    nodeAixetaVisitar.add(canonada.node2());
                }
            }
        }
        return cjtCanonadesCabal;
    }


    /** @brief Retorna un set amb tots els nodes aixetes els quals estàn trencats o embossats (no passa aigua)
     @pre Tots els terminals de aiguaArriba pertanyen a la xarxa x, aiguaArriba.get(t) indica si arriba aigua a t i la xarxa x té forma d'arbre
     @post Retorna el conjunt de nodes n de la xarxa x més propers (seguint la topologia) als terminals t de
      *       aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba
     */
    public static Set<NodeAixeta> aixetesTancar(Xarxa x, Map<Terminal, Boolean> aiguaArriba)
    {

        Set<NodeAixeta> tancarAixetes = new HashSet<>();
        //Buscar aixetes trencades
        for (Map.Entry<Terminal, Boolean> ItAiguaArriba : aiguaArriba.entrySet()) {
            Terminal terminal = ItAiguaArriba.getKey();
            Boolean aiguaArribaValor = ItAiguaArriba.getValue();

            if(!aiguaArribaValor && terminal.aixetaOberta()) {//si no arriba aigua
                Iterator<Canonada> entradesT = x.entrades(terminal);
                while (entradesT.hasNext()) { //mirem aixetes superiors
                    tancarAixetes.add(entradesT.next().node1()); //guardem aixeta trencada
                }
            }

        }
        //buscar nodes fills que no estan trencats
        Set<NodeAixeta> tancarAixetesFills = new HashSet<>();//set que serveix per saber els nodes fills
        Iterator<NodeAixeta> iterator = tancarAixetes.iterator();
        while (iterator.hasNext()) {
            NodeAixeta aixeta = iterator.next();
            eliminarFills(tancarAixetes, aixeta, x,tancarAixetesFills);
        }
        //treiem els nodes fills
        tancarAixetes.removeAll(tancarAixetesFills);
        return tancarAixetes;
    }

    /** @brief Guarda a tancarAixetesFills tots els nodes fills trobats des del node aixeta
     @pre --
     @post Omple tancarAixetesFills amb tots els nodes connexió inferiors al nodeAixeta aixeta
     */
    private static void eliminarFills(Set<NodeAixeta> tancarAixetes, NodeAixeta aixeta, Xarxa x, Set<NodeAixeta> tancarAixetesFills) {
        if(aixeta instanceof Connexio) {
            Iterator<Canonada> sortides = x.sortides(aixeta);
            boolean acabar = false;
            while (sortides.hasNext() && !acabar) { //mirem aixetes inferiors
                Canonada canonada = sortides.next();
                NodeAixeta nodoNext = canonada.node2();
                if(nodoNext instanceof Connexio) {
                    if (tancarAixetes.contains(nodoNext)) {
                        tancarAixetesFills.add(nodoNext);
                    }
                    if (!tancarAixetes.isEmpty()) {
                        eliminarFills(tancarAixetes, nodoNext, x,tancarAixetesFills);
                    } else {
                        acabar = true;
                    }
                }
            }
        }
    }

    /** @brief Retorna una llista amb tots els nodes de cjtNodes ordenats
     @pre --
     @post Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat,
      *      en ordre alfabètic dels seus identificadors
     */
    public static List<NodeAixeta> nodesOrdenats(Coordenades c, Set<NodeAixeta> cjtNodes)
    {
        List<NodeAixeta> llistaOrdenats = new LinkedList<NodeAixeta>();
        for (NodeAixeta nodeSet : cjtNodes) { //per cada node del set introduir-lo a la llista creada
            double distancia = c.distancia(nodeSet.coordenades());
            boolean trobat = false;

            // Itera sobre la llista ordenada per trobar posició pel nou node
            ListIterator<NodeAixeta> iterador = llistaOrdenats.listIterator();
            while (iterador.hasNext() && !trobat) {
                NodeAixeta node = iterador.next();
                if ((distancia < c.distancia(node.coordenades())) || (distancia == c.distancia(node.coordenades()) && nodeSet.idPetit(node))) {
                    //Afegir el node
                    iterador.previous();
                    iterador.add(nodeSet);
                    trobat = true; //Sortir del while
                }

            }
            if (!trobat) {
                llistaOrdenats.add(nodeSet);
            }
        }
        return llistaOrdenats;

    }


    /** @brief Dibuixa la xarxa x amb tots els cabals i capacitats de cada canonada i node juntament amb l'estat del node aixeta (obert/tancat)
     @pre nodeOrigen pertany a la xarxa x
     @post Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades
     */
    public static void fluxMaxim(Xarxa x, Origen nodeOrigen) {

        Graph subGraf = x.crearSubGraf(nodeOrigen);
        Graph subGrafMaxFlow = crearMaxFlow(subGraf,nodeOrigen.id());

        float cabalTotal = 0; //cabal maxim al qual es pot arribar en una línia
        float cabalPossible = 0 ;
        float fluxMaxim = 0; //flux màxim
        //Per cada camí del node Origen
        Node node = subGrafMaxFlow.getNode(nodeOrigen.id());
        for(Edge edge : node.edges().toList()){
            Canonada canonada = (Canonada) edge.getAttribute("canonada");

            cabalPossible = canonada.capacitat();
            cabalTotal = buscarCabalMaxim(canonada.node2(), cabalPossible, subGrafMaxFlow);
            fluxMaxim += cabalTotal;
            edge.setAttribute("ui.label",cabalTotal + "/" + canonada.capacitat());
        }
        //dibuixar subgraf
        subGrafMaxFlow.setAttribute("ui.quality");
        subGrafMaxFlow.setAttribute("ui.antialias");
        subGrafMaxFlow.setAttribute("ui.stylesheet", "url('file:src/estil.css')");
        subGrafMaxFlow.display();
        System.out.println("Flux màxim\n" + fluxMaxim);
    }

    /** @brief Crea un nou graf amb un sol node origen i un sol node terminal a partir del graf graph.
     @pre --
     @post Retorna el graf graph, però amb només un origen i un terminal.
     */
    private static Graph crearMaxFlow(Graph graph,String idOrigen) {
        //inicialitzacions
        Graph maxFlow = new SingleGraph("MaxFlow");
        float cabalOrigen = 0;
        Coordenades cordOrigen = null;
        float demandaActualTerminal = 0;
        float demandaPuntaTerminal = 0;
        String idTerminal = "";
        Coordenades cordTerminal = null;

        //Recorregut per juntar nodes Origens i Terminals
        for(Node node : graph.nodes().toList()) {
            NodeAixeta nodeAixeta = (NodeAixeta) graph.getNode(node.getId()).getAttribute("aixeta");

            if(nodeAixeta instanceof Origen origen){
                cabalOrigen += origen.cabal();
                cordOrigen = origen.coordenades();
            }
            else if(nodeAixeta instanceof Terminal terminal){
                demandaActualTerminal += terminal.demandaActual();
                demandaPuntaTerminal += terminal.demandaPunta();
                idTerminal = terminal.id();
                cordTerminal = terminal.coordenades();
            }
            else{
                Node n = maxFlow.addNode(nodeAixeta.id());
                n.setAttribute("aixeta", nodeAixeta);
                n.setAttribute("ui.class", "connexio");
            }
        }

        //Crear nova xarxa MaxFlow
        //Crear Origen general
        Origen unioOrigen= new Origen(idOrigen,cordOrigen);
        unioOrigen.establirCabal(cabalOrigen);
        Node nodeOrigenGeneral = maxFlow.addNode(unioOrigen.id());
        nodeOrigenGeneral.setAttribute("aixeta", unioOrigen);
        nodeOrigenGeneral.setAttribute("ui.class", "origen");

        //Crear Terminal general
        Terminal unioTerminal = new Terminal(idTerminal,cordTerminal,demandaPuntaTerminal);
        unioTerminal.establirDemandaActual(demandaActualTerminal);
        Node nodeTerminalGeneral = maxFlow.addNode(unioTerminal.id());
        nodeTerminalGeneral.setAttribute("aixeta", unioTerminal);
        nodeTerminalGeneral.setAttribute("ui.class", "terminal");

        Set<String> SetIdOrigens = new HashSet<>(); //set de connexions amb nodes origens fets (per no repetir)
        //enllaçar nodes
        for(Node node : maxFlow.nodes().toList()) {
            Node nodeGraph = graph.getNode(node.getId());

            for(Edge edge : nodeGraph.edges().toList()) {
                Canonada canonada = (Canonada) edge.getAttribute("canonada");

                Node node2Graph = graph.getNode(canonada.node2().id());
                Node node1Graph = graph.getNode(canonada.node1().id());
                Node node2Max = maxFlow.getNode(canonada.node2().id());
                Node node1Max = maxFlow.getNode(canonada.node1().id());
                String idCanonada;
                Edge e = null;
                Canonada pipe;

                if (node1Graph.getAttribute("ui.class").toString().contains("origen")) { //si està connectat amb origen
                    if(node2Graph.getAttribute("ui.class").toString().contains("terminal")){ //si hi ha connexió origen i terminal
                        if(!nodeOrigenGeneral.hasEdgeBetween(nodeTerminalGeneral)) {//no s'ha fet la connexió
                            SetIdOrigens.add(canonada.node2().id()); //afegim al set de ja connectats
                            pipe = new Canonada(unioOrigen, unioTerminal, canonada.capacitat());

                            crearPipe(maxFlow,unioOrigen,unioTerminal,pipe);
                        }
                        else if(!(SetIdOrigens.contains(canonada.node2().id()))){
                            SetIdOrigens.add(canonada.node2().id()); //afegim al set de ja connectats

                            Edge edgeEliminar = nodeOrigenGeneral.getEdgeBetween(nodeTerminalGeneral);
                            Canonada canonadaEliminar = (Canonada) edgeEliminar.getAttribute("canonada");
                            pipe = new Canonada(unioOrigen,unioTerminal,canonada.capacitat()+canonadaEliminar.capacitat());
                            maxFlow.removeEdge(edgeEliminar);//eliminem antiga connexió

                            crearPipe(maxFlow,unioOrigen,unioTerminal,pipe);//creem nova connexió general
                        }
                    }
                    else {//no son origen + terminal
                        if (!node2Max.hasEdgeBetween(nodeOrigenGeneral)) { //no s'ha fet connexió
                            SetIdOrigens.add(canonada.node2().id()); //afegim al set de ja connectats

                            pipe = new Canonada(unioOrigen, canonada.node2(), canonada.capacitat());
                            crearPipe(maxFlow,unioOrigen,canonada.node2(),pipe);//creem nova connexió general
                        }
                        else if(!(SetIdOrigens.contains(canonada.node2().id()))){
                            SetIdOrigens.add(canonada.node2().id()); //afegim al set de ja connectats

                            Edge edgeEliminar = node2Max.getEdgeBetween(nodeOrigenGeneral);
                            Canonada canonadaEliminar = (Canonada) edgeEliminar.getAttribute("canonada");
                            pipe = new Canonada(unioOrigen,canonada.node2(),canonada.capacitat()+canonadaEliminar.capacitat());
                            maxFlow.removeEdge(edgeEliminar);//eliminem antiga connexió

                            crearPipe(maxFlow,unioOrigen,canonada.node2(),pipe);//creem nova connexió general
                        }
                    }
                }
                else if(node2Graph.getAttribute("ui.class").toString().contains("terminal")){//si està connectat amb terminal
                    //no cal mirar si node1 és origen perquè ja hagués entrat al primer if
                    if(!node1Max.hasEdgeBetween(nodeTerminalGeneral)) {
                        pipe = new Canonada(canonada.node1(), unioTerminal, canonada.capacitat());
                        crearPipe(maxFlow,canonada.node1(),unioTerminal,pipe);//creem pipe
                    }

                }
                else{//si son dos connexions
                    if(!(node1Max.hasEdgeBetween(node2Max))) {
                        pipe = new Canonada(canonada.node1(), canonada.node2(), canonada.capacitat());
                        crearPipe(maxFlow,canonada.node1(),canonada.node2(),pipe);//creem pipe
                    }
                }
            }

        }
        return maxFlow;
    }

    /** @brief Crea una pipe en el graph maxFlow
     @pre nodesAixeta pertanyen al graph
     @post Crea una pipe amb el nom dels ids dels nodes i es guarda al graph maxFlow
     */
    private static void crearPipe(Graph maxFlow, NodeAixeta node1, NodeAixeta node2, Canonada pipe){
        String idCanonada = node1.id() + "-" + node2.id();
        Edge e = maxFlow.addEdge(idCanonada, node1.id(), node2.id(), true);
        e.setAttribute("canonada", pipe);
    }

    /** @brief Retorna el flux màxim que pot donar el nodeAixeta. Com a màxim serà cabalPossible.
     @pre nodeAixeta pertany al graph
     @post Gràcies a la recursivitat, va baixant per les arestes fins trobar la canonada més petita (capacitat) i acaba
     * tornant la capacitat mínim de la línia del nodeAixeta.
     */
    private static float buscarCabalMaxim(NodeAixeta nodeAixeta,float cabalPossible, Graph graph) {
        float cabalTotal = 0;
        if(nodeAixeta instanceof Terminal terminal){
            cabalTotal = cabalPossible;
        }
        else{
            //per tots els edges sortida del node
            Node nodeIterador = graph.getNode(nodeAixeta.id());
            Iterator<Edge> edgeIterator = nodeIterador.leavingEdges().iterator();
            while (edgeIterator.hasNext()) {
                Edge aresta = edgeIterator.next();
                Canonada canonada = (Canonada) aresta.getAttribute("canonada");
                //comprovar si cabalPossible és més petit i cridar funció recursiva
                float cabalCanonada = 0;
                if (cabalPossible > canonada.capacitat()) {
                    cabalCanonada = buscarCabalMaxim(canonada.node2(), canonada.capacitat(), graph);
                }
                else cabalCanonada = buscarCabalMaxim(canonada.node2(), cabalPossible, graph);

                cabalTotal += cabalCanonada;
                cabalPossible -= cabalCanonada;
                //guardar cabal a dibuix
                aresta.setAttribute("ui.label",cabalCanonada + "/" + canonada.capacitat());

            }
        }
        return cabalTotal;
    }
}
