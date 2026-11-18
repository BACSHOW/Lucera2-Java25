package l2.gameserver.network.l2.s2c;

/**
 * Pacote visual sobre outro objeto que pode ser pulado quando a conexao do cliente esta cheia
 * (NettySlowClientShedding): o proximo pacote do mesmo tipo, ou a ressincronizacao quando a conexao
 * volta ao normal, ja mostra o estado atual. Nunca usar em pacote que apresenta ou remove objetos
 * (NpcInfo, CharInfo, DeleteObject) nem em pacote sobre o proprio jogador.
 */
public interface SlowClientSheddable {
    /** Objeto de quem e o pacote (quem anda, ataca, usa a skill...). */
    int getSubjectObjectId();
}
