# Projecte de Programació (GEINF/GEB - UdG)

## Primavera 2024

**Codi font (fitxers .java)**
- [BeWater.java](BeWater.java): Programa principal de simulació de xarxes de distribució d'aigua
- [Canonada.java](Canonada.java): Canonada de la xarxa de distribució d'aigua
- [Connexio.java](Connexio.java): Node de connexió d'una xarxa de distribució d'aigua
- [Coordenades.java](Coordenades.java): Coordenades geogràfiques (latitud, longitud)
- [GestorXarxes.java](GestorXarxes.java): Mòdul funcional amb funcions per a la gestió de xarxes de distribució d'aigua
- [NodeAixeta.java](NodeAixeta.java): Node d'una xarxa de distribució d'aigua
- [Origen.java](Origen.java): Node origen d'una xarxa de distribució d'aigua
- [SimuladorModeText.java](SimuladorModeText.java): Simula les operacions de construcció, modificació i consulta d'una xarxa de distribució d'aigua a partir d'un fitxer de text.
- [Terminal.java](Terminal.java): Node Terminal d'una Xarxa de distribució d'aigua
- [Xarxa.java](Xarxa.java): Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)

**Arxius css (fitxers .css)**
- [estil.css](estil.css): Fitxer on està especificat el disseny dels nodes i arestes

## Repartició de feina
# Arnau
- Coordenades
- SimuladorModeText
- Terminal
- GestorXarxes(menys té cicles i és arbre)
- estil

# Aleix
- Xarxa
- NodeAixeta
- Canonada
- Origen
- Connexio
- GestorXarxes(té cicles i és arbre)
- BeWater
