package l2.gameserver.model;

/**
 * Gerenciador de mitigação de flood de skills mágicas (Magic Skill Flood Mitigation).
 *
 * Controla duas camadas de proteção:
 * 1. Debounce de pacotes idênticos (mesmo skillId e targetId) recebidos em sequência durante o cast.
 * 2. Histerese de pacotes ActionFailed para evitar envio de centenas de erros redundantes que congelam a UI do cliente.
 */
public class SkillFloodTracker {
    private int _lastPacketSkillId = 0;
    private int _lastPacketTargetObjId = 0;
    private long _lastPacketAt = 0L;

    private int _lastActionFailedSkillId = 0;
    private long _lastActionFailedAt = 0L;

    public synchronized boolean tryMarkCastPacketSeen(int skillId, int targetObjId, long debounceMs, long now) {
        if (skillId == _lastPacketSkillId
                && targetObjId == _lastPacketTargetObjId
                && (now - _lastPacketAt) < debounceMs) {
            return false; // Spam duplicado -> drop silencioso
        }
        _lastPacketSkillId = skillId;
        _lastPacketTargetObjId = targetObjId;
        _lastPacketAt = now;
        return true;
    }

    public boolean tryMarkCastPacketSeen(int skillId, int targetObjId, long debounceMs) {
        return tryMarkCastPacketSeen(skillId, targetObjId, debounceMs, System.currentTimeMillis());
    }

    public synchronized boolean shouldSuppressActionFailed(int skillId, long cooldownMs, long now) {
        return skillId == _lastActionFailedSkillId
                && (now - _lastActionFailedAt) < cooldownMs;
    }

    public boolean shouldSuppressActionFailed(int skillId, long cooldownMs) {
        return shouldSuppressActionFailed(skillId, cooldownMs, System.currentTimeMillis());
    }

    public synchronized void markActionFailed(int skillId, long now) {
        _lastActionFailedSkillId = skillId;
        _lastActionFailedAt = now;
    }

    public void markActionFailed(int skillId) {
        markActionFailed(skillId, System.currentTimeMillis());
    }

    public int getLastPacketSkillId() {
        return _lastPacketSkillId;
    }

    public int getLastPacketTargetObjId() {
        return _lastPacketTargetObjId;
    }

    public long getLastPacketAt() {
        return _lastPacketAt;
    }
}
