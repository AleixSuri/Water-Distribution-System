//Aleix Suriñach

// Web utilitzada per mètodes GraphStream --> https://graphstream-project.org/gs-core/org/graphstream/graph/Graph.html
import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.spriteManager.*;

/** @file Xarxa.java
 @brief Classe Xarxa
 */

/** @class Xarxa
 @brief Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)
 @author Aleix Suriñach
 */


public class Xarxa {

    //Atributs
    private final Graph _xarxa;
    private final Map<String, Terminal> _clients;
    private final Stack<String> _modifAixetes;

    /** @brief Crea una Xarxa
     @pre ---
     @post Crea una xarxa de distribució d'aigua buida
     */
    public Xarxa() {
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.debug", "true");

        _xarxa = new SingleGraph("Xarxa Be Water");
        _clients = new HashMap<>();
        _modifAixetes = new Stack<>();
    }

    /** @brief Node xarxa
     @pre ---
     @post Retorna el node de la xarxa amb identificador id
     */
    public NodeAixeta node(String id) {
        Node n = _xarxa.getNode(id);

        return (n != null) ? (NodeAixeta) n.getAttribute("aixeta") : null;
    }

    /** @brief Sortides d'un node
     @pre node pertany a la xarxa
     @post Retorna un iterador que permet recórrer totes les canonades que surten del node
     */
    public Iterator<Canonada> sortides(NodeAixeta node)
    {
        Node nodeIterador = _xarxa.getNode(node.id());
        Iterator<Edge> edgeIterator = nodeIterador.leavingEdges().iterator();

        // Creem una llista per emmagatzemar les canonades
        List<Canonada> canonades = new ArrayList<>();

        // Convertim cada Edge a Canonada i l'afegim a la llista
        while (edgeIterator.hasNext()) {
            Edge aresta = edgeIterator.next();
            Canonada pipe = (Canonada) aresta.getAttribute("canonada");
            canonades.add(pipe);
        }

        return canonades.iterator();
    }

    /** @brief Entrades d'un node
     @pre node pertany a la xarxa
     @post Retorna un iterador que permet recórrer totes les canonades que entre del node
     */
    public Iterator<Canonada> entrades(NodeAixeta node)
    {
        Node nodeIterador = _xarxa.getNode(node.id());
        Iterator<Edge> edgeIterator = nodeIterador.enteringEdges().iterator();

        // Creem una llista per emmagatzemar les canonades
        List<Canonada> canonades = new ArrayList<>();

        // Convertim cada Edge a Canonada i l'afegim a la llista
        while (edgeIterator.hasNext()) {
            Edge aresta = edgeIterator.next();
            Canonada pipe = (Canonada) aresta.getAttribute("canonada");
            canonades.add(pipe);
        }

        return canonades.iterator();
    }

    /** @brief Afegir Origen
     @pre No existeix cap node amb el mateix id que nodeOrigen a la xarxa
     @post S'ha afegit nodeOrigen a la xarxa
     @exception "IllegalArgumentException" si ja existeix un node amb aquest id
     */
    public void afegir(Origen nodeOrigen) {
        String id = nodeOrigen.id();
        if(_xarxa.getNode(id) != null)
        {
            throw new IllegalArgumentException("L'origen amb id " + id + " ja existeix");
        }

        Node n = _xarxa.addNode(nodeOrigen.id());
        n.setAttribute("ui.label", nodeOrigen.id());
        n.setAttribute("aixeta", nodeOrigen);
        n.setAttribute("ui.class", "origen");
    }

    /** @brief Afegir Terminal
     @pre No existeix cap node amb el mateix id que nodeTerminal a la xarxa
     @post S'ha afegit nodeTerminal a la xarxa
     @exception "IllegalArgumentException" si ja existeix un node amb aquest id
     */
    public void afegir(Terminal nodeTerminal) {
        String id = nodeTerminal.id();
        if(_xarxa.getNode(id) != null)
        {
            throw new IllegalArgumentException("La terminal amb id " + id + " ja existeix");
        }

        Node n = _xarxa.addNode(nodeTerminal.id());
        n.setAttribute("ui.label", nodeTerminal.id());
        n.setAttribute("aixeta", nodeTerminal);
        n.setAttribute("ui.class", "terminal");
    }

    /** @brief Afegir Connexio
     @pre No existeix cap node amb el mateix id que nodeConnexio a la xarxa
     @post S'ha afegit nodeConnexio a la xarxa
     @exception "IllegalArgumentException" si ja existeix un node amb aquest id
     */
    public void afegir(Connexio nodeConnexio) {
        String id = nodeConnexio.id();
        if(_xarxa.getNode(id) != null)
        {
            throw new IllegalArgumentException("La connexió amb id " + id + " ja existeix");
        }

        Node n = _xarxa.addNode(nodeConnexio.id());
        n.setAttribute("ui.label", nodeConnexio.id());
        n.setAttribute("aixeta", nodeConnexio);
        n.setAttribute("ui.class", "connexio");
    }

    /** @brief Connectar Canonada
     @pre node1 i node2 pertanyen a la xarxa, i node1 no és un node terminal
     @post S'han connectat els nodes amb una canonada de capacitat c, amb sentit de l'aigua de node1 a node2
     @exception "NoSuchElementException" node1 o node2 no pertanyen a la xarxa
     IllegalArgumentException els nodes ja estan connectats o node1 és un node terminal
     */
    public void connectarAmbCanonada(NodeAixeta node1, NodeAixeta node2, float c) {
        Node n1 = _xarxa.getNode(node1.id());
        Node n2 = _xarxa.getNode(node2.id());

        if(n1 == null || n2 == null) {
            throw new NoSuchElementException("Algun dels nodes no pertany a la xarxa");
        }

        if(node1 instanceof Terminal) {
            throw new IllegalArgumentException("El node " + node1.id() + " és un node terminal");
        }

        //mirar si els dos nodes ja estan connectats
        if (n1.hasEdgeBetween(n2)) {
            throw new IllegalArgumentException("Nodes ja connectats");
        }

        //Capacitat negativa?
        if (c < 0) {
            throw new IllegalArgumentException("La capacitat no pot ser negativa");
        }

        String idCanonada = node1.id() + "-" + node2.id();
        Edge e = _xarxa.addEdge(idCanonada, node1.id(), node2.id(), true);

        Canonada pipe;
        if(node2 instanceof Origen) {
            Connexio conn = new Connexio(node2.id(), node2.coordenades());
            Node n = _xarxa.getNode(conn.id());
            n.removeAttribute("aixeta");
            n.setAttribute("aixeta", conn);

            n.removeAttribute("ui.class");
            n.setAttribute("ui.class", "connexio");

            pipe = new Canonada(node1, conn, c);
        }
        else{
            pipe = new Canonada(node1, node2, c);
        }

        e.setAttribute("canonada", pipe);
    }

    /** @brief Terminal associat
     @pre ---
     @post Retorna el node terminal associat
     */
    public Terminal retornAbonat(String IdClient) {
        return _clients.get(IdClient);
    }

    /** @brief Existeix Client
     @pre ---
     @post True si IdClient està abonat a la xarxa, false altrament
     */
    public boolean existeixClient(String IdClient) {
        return _clients.containsKey(IdClient);
    }

    /** @brief Abonar client
     @pre nodeTerminal pertany a la xarxa
     @post El client identificat amb idClient queda abonat al node terminal, i diu si ja ho estava
     @exception "NoSuchElementException" si nodeTerminal no pertany a la xarxa
     */
    public boolean abonar(String idClient, Terminal nodeTerminal) {
        if(_xarxa.getNode(nodeTerminal.id()) == null)
        {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }

        //Igual a null, no existia client, si diferent retornaria el nodeTerminal
        return _clients.putIfAbsent(idClient, nodeTerminal) == null;
    }

    /** @brief Cabal Abonat
     @pre Existeix un client identificat amb idClient a la xarxa
     @post Retorna el cabal actual al punt d'abastament del client identificat amb idClient
     */
    public float cabalAbonat(String idClient) {
        Terminal abonat = _clients.get(idClient);

        return cabal(abonat);
    }

    /** @brief Obrir aixeta
     @pre node pertany a la xarxa
     @post L'aixeta del node està oberta
     @exception  "NoSuchElementException" si node no pertany a la xarxa
     */
    public void obrirAixeta(NodeAixeta node) {
        if(_xarxa.getNode(node.id()) == null)
        {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }

        String identificadorAixeta;

        // Si l'aixeta canviarà d'estat, s'afegeix "sí" i es canvia
        if (node.aixetaOberta()) {
            identificadorAixeta = node.id() + "-" + "no";
        } else {
            identificadorAixeta = node.id() + "-" + "si";
            node.obrirAixeta();
        }
        _modifAixetes.push(identificadorAixeta);

    }

    /** @brief Tancar aixeta
     @pre node pertany a la xarxa
     @post L'aixeta del node està tancada
     @exception  "NoSuchElementException" si node no pertany a la xarxa
     */
    public void tancarAixeta(NodeAixeta node) {
        if(_xarxa.getNode(node.id()) == null)
        {
            throw new NoSuchElementException("El node no pertany a la xarxa");
        }

        String identificadorAixeta;

        // Si l'aixeta canviarà d'estat, s'afegeix "sí" i es canvia
        if (node.aixetaOberta()) {
            identificadorAixeta = node.id() + "-" + "si";
            node.tancarAixeta();
        } else {
            identificadorAixeta = node.id() + "-" + "no";
        }
        _modifAixetes.push(identificadorAixeta);
    }

    /** @brief Recular passos
     @pre nPassos >= 1
     @post S'ha reculat nPassos passos en la seqüència d'operacions realitzades d'obrir i tancar aixetes
     @exception  "IllegalArgumentException" si nPassos és negatiu o zero
     */
    public void recular(int nPassos) {
        if(nPassos <= 0)
        {
            throw new IllegalArgumentException("El nombre de passos ha de ser 1 o superior");
        }

        int nombrePassos = nPassos;
        // Mentre el nombre de passos > 0 i la pila de modificacions no estigui buida
        while (!_modifAixetes.empty() && nombrePassos > 0) {
            // Desempilar
            String contingut = _modifAixetes.pop();
            String[] parts = contingut.split("-");

            // Si aquella modificació ha creat un canvi d'estat, revertir-lo
            // ex: aixeta oberta i es vol obrir, no hi ha hagut un canvi i, per tant, s'ignora aquest pas
            if (parts[1].equals("si")) {
                Node n = _xarxa.getNode(parts[0]);
                NodeAixeta aixeta = (NodeAixeta) n.getAttribute("aixeta");
                aixeta.canviarEstat();
            }

            nombrePassos--;
        }
    }

    /** @brief Establir cabal
     @pre nodeOrigen pertany a la xarxa i cabal >= 0
     @post El cabal de nodeOrigen és cabal
     @exception "NoSuchElementException" si nodeOrigen no pertany a la xarxa
     IllegalArgumentException si cabal és negatiu
     */
    public void establirCabal(Origen nodeOrigen, float cabal) {
        if(_xarxa.getNode(nodeOrigen.id()) == null)
        {
            throw new NoSuchElementException("NodeOrigen no pertany a la xarxa");
        }

        if(cabal < 0)
        {
            throw new IllegalArgumentException("El cabal no pot ser negatiu");
        }

        nodeOrigen.establirCabal(cabal);
    }

    /** @brief Establir demanda
     @pre nodeTerminal pertany a la xarxa i demanda >= 0
     @post La demanda de nodeTerminal és demanda
     @exception "NoSuchElementException" si nodeTerminal no pertany a la xarxa
     IllegalArgumentException si demanda és negatiu
     */
    public void establirDemanda(Terminal nodeTerminal, float demanda) {
        if(_xarxa.getNode(nodeTerminal.id()) == null)
        {
            throw new NoSuchElementException("NodeOrigen no pertany a la xarxa");
        }

        if(demanda < 0)
        {
            throw new IllegalArgumentException("La demanda no pot ser negativa");
        }

        nodeTerminal.establirDemandaActual(demanda);
    }

    /** @brief Cabal teòric
     @pre node pertany a la xarxa
     @post Retorna el cabal teòric al node segons la configuració actual de la xarxa
     @exception "NoSuchElementException" si node no pertany a la xarxa
     */
    public float cabal(NodeAixeta node) {
        if (_xarxa.getNode(node.id()) == null) {
            throw new NoSuchElementException("Aquest node no pertany a la xarxa");
        }

        if(node instanceof Terminal terminal) {
            if(terminal.demandaActual() == 0) {
                return 0;
            }
        }

        if(node instanceof Origen origen) {
            Node n = _xarxa.getNode(node.id());
            if(n.getAttribute("ui.class").equals("origen")) {
                if (origen.cabal() <= demanda(origen)) {
                    return origen.cabal();
                } else {
                    return demanda(origen);
                }
            }
        }

        float cabalTotal = 0;
        Iterator<Canonada> pipes = entrades(node);
        while (pipes.hasNext()) {
            Canonada pipe = pipes.next();
            cabalTotal += calculCabalCanonada(pipe);
        }

        return cabalTotal;
    }

    /** @brief Cabal canonada
     @pre ---
     @post Retorna el cabal que circula per la canonada
     */
    public float calculCabalCanonada(Canonada pipe){
        float demandaTotalCanonades = 0;
        Iterator<Canonada> pipes = sortides(pipe.node1());
        while(pipes.hasNext())
        {//Suma de les capacitats de les canonades que entren al segon node de pipe
            Canonada c = pipes.next();
            demandaTotalCanonades += calculDemandaCanonada(c);
        }

        float cabalNode = cabal(pipe.node1());
        //Repartició cabal
        float demandaCanonada = calculDemandaCanonada(pipe);
        float cabalCanonada = (demandaCanonada / demandaTotalCanonades) * cabalNode;

        return cabalCanonada;
    }

    /** @brief Demanda teòrica
     @pre node pertany a la xarxa
     @post Retorna la demanda teòrica al node segons la configuració actual de la xarxa
     @exception "NoSuchElementException" si node no pertany a la xarxa
     */
    public float demanda(NodeAixeta node) {
        if (_xarxa.getNode(node.id()) == null)
        {
            throw new NoSuchElementException("Aquest node no pertany a la xarxa");
        }

        if(!node.aixetaOberta())
        {
            return 0;
        }

        if(node instanceof Terminal terminal)
        {
            return terminal.demandaActual();
        }

        float demandaTotal = 0;
        Iterator<Canonada> pipes = sortides(node);
        while(pipes.hasNext())
        {
            Canonada pipe = pipes.next();
            demandaTotal += calculDemandaCanonada(pipe);
        }

        return demandaTotal;
    }

    /** @brief Demanda canonada
     @pre ---
     @post Retorna la demanda que hi ha d'haver per la canonada
     */
    private float calculDemandaCanonada(Canonada pipe) {
        float capacitatCanonadesTotal = 0;
        Iterator<Canonada> pipes = entrades(pipe.node2());
        while(pipes.hasNext())
        {//Suma de les capacitats de les canonades que entren al segon node de pipe
            Canonada c = pipes.next();
            capacitatCanonadesTotal += c.capacitat();
        }

        float demandaNode = demanda(pipe.node2());
        //Repartició demanda
        float demandaCanonada = (pipe.capacitat() / capacitatCanonadesTotal) * demandaNode;

        if(demandaCanonada > pipe.capacitat()) {
            return pipe.capacitat();
        }
        else {
            return demandaCanonada;
        }
    }

    /** @brief Dibuixar
     @pre ---
     @post Dibuixa la xarxa de distribució d'aigua en la qual pertany nodeOrigen
     */
    public void dibuixar(Origen nodeOrigen) {
        Graph subGraf = crearSubGraf(nodeOrigen);
        SpriteManager sman = new SpriteManager(subGraf);

        for(Node node : subGraf.nodes().toList()){
            NodeAixeta nodeAixeta = node(node.getId()); //recuperem info del node
            node.setAttribute("x", Math.toRadians(nodeAixeta.coordenades().getAlongitud()));
            node.setAttribute("y", Math.toRadians(nodeAixeta.coordenades().getAlatitud()));

            enllaçarSprites(sman,node);
            //tanquem node graficament
            if(!nodeAixeta.aixetaOberta()) {
                node.removeAttribute("ui.class");
                node.setAttribute("ui.class", "tancat");
            }

        }

        subGraf.setAttribute("ui.quality");
        subGraf.setAttribute("ui.antialias");
        subGraf.setAttribute("ui.stylesheet", "url('file:src/estil.css')");
        subGraf.display(true);
    }

    /** @brief Enllaç d'sprites
     @pre ---
     @post Crea enllaços entre atributs del nodeAixeta i sprites
     */
    private void enllaçarSprites(SpriteManager sman, Node node) {
        NodeAixeta nodeAixeta = node(node.getId());
        //enllaç coordenades
        Sprite spriteCoordenada = sman.addSprite(nodeAixeta.id() + "Cord");
        spriteCoordenada.setAttribute("ui.label",
                Math.round(nodeAixeta.coordenades().getAlatitud() * 100.0)/100.0 + ":" +
                        Math.round(nodeAixeta.coordenades().getAlongitud() * 100.0)/100.0);
        spriteCoordenada.setPosition(0, 0.03, 0);
        spriteCoordenada.attachToNode(nodeAixeta.id());

        //enllaç demandes terminals
        if(nodeAixeta instanceof Terminal nodeTerminal) {
            Sprite spriteTerminal = sman.addSprite(nodeAixeta.id() + "Terminal");
            spriteTerminal.setAttribute("ui.label",cabal(nodeAixeta) + "/" + nodeTerminal.demandaActual());
            spriteTerminal.setPosition(0, -0.06, 0);
            spriteTerminal.setAttribute("ui.class", "terminal");
            spriteTerminal.attachToNode(nodeAixeta.id());
        }

        //enllaç cabal canonades
        for (Edge edge : node.edges().toList()) {

            Canonada canonada = (Canonada) edge.getAttribute("canonada");
            if(canonada != null) {
                String cabalReal = String.valueOf(Math.round(calculCabalCanonada(canonada)* 100.0)/100.0);
                Sprite spriteCabal = sman.addSprite(edge.getId() + "Canonada");
                spriteCabal.setAttribute("ui.label",cabalReal + "/" + canonada.capacitat());
                spriteCabal.setPosition(0.5);
                spriteCabal.setAttribute("ui.class", "canonada");
                spriteCabal.attachToEdge(edge.getId());
            }
            else throw new IllegalArgumentException("Canonada no trobada per dibuixar");
        }
    }

    /** @brief Crear SubGraf
     @pre nodeXarxa pertany a la xarxa
     @post retorna un subgraf en el qual nodeXarxa pertany
     */
    public Graph crearSubGraf(Origen nodeXarxa) {
        Graph subGraph = new SingleGraph("Subgraf");
        Set<Node> visitats = new HashSet<>();

        Node n = _xarxa.getNode(nodeXarxa.id());
        // Iniciem la cerca en profunditat des del nodeXarxa
        copiaGraf(n, visitats, subGraph);

        return subGraph;
    }

    /** @brief Copiar graf
     @pre ---
     @post Actualitza el subGraph en el qual nomès hi ha les connexions i nodes del graf al qual pertany node
     */
    private void copiaGraf(Node node, Set<Node> visitats, Graph subGraph) {
        visitats.add(node);
        Node n = subGraph.addNode(node.getId());

        Node nodeXarxa = _xarxa.getNode(node.getId());
        NodeAixeta nodeAixeta = (NodeAixeta) nodeXarxa.getAttribute("aixeta");

        if (nodeAixeta instanceof Connexio nodeConnexio) {
            n.setAttribute("aixeta", nodeConnexio);
            n.setAttribute("ui.class", "connexio");

        }
        else if(nodeAixeta instanceof Origen nodeOrigen) {
            n.setAttribute("aixeta", nodeOrigen);
            n.setAttribute("ui.class", "origen");

        } else if (nodeAixeta instanceof Terminal nodeTerminal) {
            n.setAttribute("aixeta", nodeTerminal);
            n.setAttribute("ui.class", "terminal");

        }

        n.setAttribute("ui.label", node.getId());

        Iterator<Edge> iterator = node.edges().iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            Node adjacent = edge.getOpposite(node);

            if (!visitats.contains(adjacent)) {
                copiaGraf(adjacent, visitats, subGraph);
            }

            // Verifiquem si els dos extrems de l'aresta estan al subgraf abans d'afegir l'aresta
            if (subGraph.getNode(node.getId()) != null && subGraph.getNode(adjacent.getId()) != null) {
                // Afegim l'aresta al subgraf
                if (subGraph.getEdge(edge.getId()) == null) {
                    Node nodeInicial = edge.getSourceNode();
                    if (nodeInicial.equals(node)) {
                        // Si el node d'origen es el mateix que el node actual, mantenim la mateixa direccio
                        subGraph.addEdge(edge.getId(), node.getId(), adjacent.getId(), true);
                    } else {
                        subGraph.addEdge(edge.getId(), adjacent.getId(), node.getId(), true);
                    }

                    if (edge.hasAttribute("canonada")) {
                        subGraph.getEdge(edge.getId()).setAttribute("canonada", edge.getAttribute("canonada"));
                    }
                }
            }
        }
    }
}