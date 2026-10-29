/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.instancemanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import l2.gameserver.Config;
import l2.gameserver.dao.CharacterDAO;
import l2.gameserver.handler.petition.IPetitionHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.NpcHtmlMessage;
import l2.gameserver.network.l2.s2c.Say2;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.GmListTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PetitionManager
implements IPetitionHandler {
    private static final Logger _log = LoggerFactory.getLogger(PetitionManager.class.getName());
    private static final PetitionManager AD = new PetitionManager();
    private AtomicInteger AE = new AtomicInteger();
    private Map<Integer, Petition> AF = new ConcurrentHashMap<Integer, Petition>();
    private Map<Integer, Petition> AG = new ConcurrentHashMap<Integer, Petition>();

    public static final PetitionManager getInstance() {
        return AD;
    }

    private PetitionManager() {
        _log.info("Initializing PetitionManager");
    }

    public int getNextId() {
        return this.AE.incrementAndGet();
    }

    public void clearCompletedPetitions() {
        int n = this.getPendingPetitionCount();
        this.getCompletedPetitions().clear();
        _log.info("PetitionManager: Completed petition data cleared. " + n + " petition(s) removed.");
    }

    public void clearPendingPetitions() {
        int n = this.getPendingPetitionCount();
        this.getPendingPetitions().clear();
        _log.info("PetitionManager: Pending petition queue cleared. " + n + " petition(s) removed.");
    }

    public boolean acceptPetition(Player player, int n) {
        if (!this.w(n)) {
            return false;
        }
        Petition petition = this.getPendingPetitions().get(n);
        if (petition.getResponder() != null) {
            return false;
        }
        petition.setResponder(player);
        petition.setState(PetitionState.In_Process);
        petition.sendPetitionerPacket(new SystemMessage(406));
        petition.sendResponderPacket(new SystemMessage(389).addNumber(petition.getId()));
        petition.sendResponderPacket(new SystemMessage(394).addString(petition.getPetitioner().getName()));
        return true;
    }

    public boolean cancelActivePetition(Player player) {
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition.getPetitioner() != null && petition.getPetitioner().getObjectId() == player.getObjectId()) {
                return petition.endPetitionConsultation(PetitionState.Petitioner_Cancel);
            }
            if (petition.getResponder() == null || petition.getResponder().getObjectId() != player.getObjectId()) continue;
            return petition.endPetitionConsultation(PetitionState.Responder_Cancel);
        }
        return false;
    }

    public void checkPetitionMessages(Player player) {
        if (player != null) {
            for (Petition petition : this.getPendingPetitions().values()) {
                if (petition == null || petition.getPetitioner() == null || petition.getPetitioner().getObjectId() != player.getObjectId()) continue;
                for (Say2 say2 : petition.getLogMessages()) {
                    player.sendPacket((IStaticPacket)say2);
                }
                return;
            }
        }
    }

    public boolean endActivePetition(Player player) {
        if (!player.isGM()) {
            return false;
        }
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null || petition.getResponder() == null || petition.getResponder().getObjectId() != player.getObjectId()) continue;
            return petition.endPetitionConsultation(PetitionState.Completed);
        }
        return false;
    }

    protected Map<Integer, Petition> getCompletedPetitions() {
        return this.AG;
    }

    protected Map<Integer, Petition> getPendingPetitions() {
        return this.AF;
    }

    public int getPendingPetitionCount() {
        return this.getPendingPetitions().size();
    }

    public int getPlayerTotalPetitionCount(Player player) {
        if (player == null) {
            return 0;
        }
        int n = 0;
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null || petition.getPetitioner() == null || petition.getPetitioner().getObjectId() != player.getObjectId()) continue;
            ++n;
        }
        for (Petition petition : this.getCompletedPetitions().values()) {
            if (petition == null || petition.getPetitioner() == null || petition.getPetitioner().getObjectId() != player.getObjectId()) continue;
            ++n;
        }
        return n;
    }

    public boolean isPetitionPending() {
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null || petition.getState() != PetitionState.Pending) continue;
            return true;
        }
        return false;
    }

    public boolean isPetitionInProcess() {
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null || petition.getState() != PetitionState.In_Process) continue;
            return true;
        }
        return false;
    }

    public boolean isPetitionInProcess(int n) {
        if (!this.w(n)) {
            return false;
        }
        Petition petition = this.getPendingPetitions().get(n);
        return petition.getState() == PetitionState.In_Process;
    }

    public boolean isPlayerInConsultation(Player player) {
        if (player != null) {
            for (Petition petition : this.getPendingPetitions().values()) {
                if (petition == null || petition.getState() != PetitionState.In_Process || (petition.getPetitioner() == null || petition.getPetitioner().getObjectId() != player.getObjectId()) && (petition.getResponder() == null || petition.getResponder().getObjectId() != player.getObjectId())) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isPetitioningAllowed() {
        return Config.PETITIONING_ALLOWED;
    }

    public boolean isPlayerPetitionPending(Player player) {
        if (player != null) {
            for (Petition petition : this.getPendingPetitions().values()) {
                if (petition == null || petition.getPetitioner() == null || petition.getPetitioner().getObjectId() != player.getObjectId()) continue;
                return true;
            }
        }
        return false;
    }

    private boolean w(int n) {
        return this.getPendingPetitions().containsKey(n);
    }

    public boolean rejectPetition(Player player, int n) {
        if (!this.w(n)) {
            return false;
        }
        Petition petition = this.getPendingPetitions().get(n);
        if (petition.getResponder() != null) {
            return false;
        }
        petition.setResponder(player);
        return petition.endPetitionConsultation(PetitionState.Responder_Reject);
    }

    public boolean sendActivePetitionMessage(Player player, String string) {
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null) continue;
            if (petition.getPetitioner() != null && petition.getPetitioner().getObjectId() == player.getObjectId()) {
                Say2 say2 = new Say2(player.getObjectId(), ChatType.PETITION_PLAYER, player.getName(), string);
                petition.addLogMessage(say2);
                petition.sendResponderPacket(say2);
                petition.sendPetitionerPacket(say2);
                return true;
            }
            if (petition.getResponder() == null || petition.getResponder().getObjectId() != player.getObjectId()) continue;
            Say2 say2 = new Say2(player.getObjectId(), ChatType.PETITION_GM, player.getName(), string);
            petition.addLogMessage(say2);
            petition.sendResponderPacket(say2);
            petition.sendPetitionerPacket(say2);
            return true;
        }
        return false;
    }

    public void sendPendingPetitionList(Player player) {
        StringBuilder stringBuilder = new StringBuilder(600 + this.getPendingPetitionCount() * 300);
        stringBuilder.append("<html><body><center><table width=270><tr><td width=45><button value=\"Main\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td width=180><center>Petition Menu</center></td><td width=45><button value=\"Back\" action=\"bypass -h admin_admin\" width=45 height=21 back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table><br><table width=\"270\"><tr><td><table width=\"270\"><tr><td><button value=\"Reset\" action=\"bypass -h admin_reset_petitions\" width=\"80\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td align=right><button value=\"Refresh\" action=\"bypass -h admin_view_petitions\" width=\"80\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table><br></td></tr>");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (this.getPendingPetitionCount() == 0) {
            stringBuilder.append("<tr><td>There are no currently pending petitions.</td></tr>");
        } else {
            stringBuilder.append("<tr><td><font color=\"LEVEL\">Current Petitions:</font><br></td></tr>");
        }
        boolean bl = true;
        int n = 0;
        for (Petition petition : this.getPendingPetitions().values()) {
            if (petition == null) continue;
            stringBuilder.append("<tr><td width=\"270\"><table width=\"270\" cellpadding=\"2\" bgcolor=").append(bl ? "131210" : "444444").append("><tr><td width=\"130\">").append(simpleDateFormat.format(new Date(petition.getSubmitTime())));
            stringBuilder.append("</td><td width=\"140\" align=right><font color=\"").append(petition.isPetitionerOnline() ? "00FF00" : "999999").append("\">").append(petition.getPetitionerName()).append("</font></td></tr>");
            stringBuilder.append("<tr><td width=\"130\">");
            if (petition.getState() != PetitionState.In_Process) {
                stringBuilder.append("<table width=\"130\" cellpadding=\"2\"><tr><td><button value=\"View\" action=\"bypass -h admin_view_petition ").append(petition.getId()).append("\" width=\"50\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td><td><button value=\"Reject\" action=\"bypass -h admin_reject_petition ").append(petition.getId()).append("\" width=\"50\" height=\"21\" back=\"sek.cbui94\" fore=\"sek.cbui94\"></td></tr></table>");
            } else {
                stringBuilder.append("<font color=\"").append(petition.getResponder().isOnline() ? "00FF00" : "999999").append("\">").append(petition.getResponder().getName()).append("</font>");
            }
            stringBuilder.append("</td>").append(petition.getTypeAsString()).append("<td width=\"140\" align=right>").append(petition.getTypeAsString()).append("</td></tr></table></td></tr>");
            boolean bl2 = bl = !bl;
            if (++n <= 10) continue;
            stringBuilder.append("<tr><td><font color=\"LEVEL\">There is more pending petition...</font><br></td></tr>");
            break;
        }
        stringBuilder.append("</table></center></body></html>");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
        npcHtmlMessage.setHtml(stringBuilder.toString());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    public int submitPetition(Player player, String string, int n) {
        Petition petition = new Petition(player, string, n);
        int n2 = petition.getId();
        this.getPendingPetitions().put(n2, petition);
        String string2 = player.getName() + " has submitted a new petition.";
        GmListTable.broadcastToGMs(new Say2(player.getObjectId(), ChatType.CRITICAL_ANNOUNCE, "Petition System", string2));
        return n2;
    }

    public void viewPetition(Player player, int n) {
        if (!player.isGM()) {
            return;
        }
        if (!this.w(n)) {
            return;
        }
        Petition petition = this.getPendingPetitions().get(n);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
        npcHtmlMessage.setFile("admin/petition.htm");
        npcHtmlMessage.replace("%petition%", String.valueOf(petition.getId()));
        npcHtmlMessage.replace("%time%", simpleDateFormat.format(new Date(petition.getSubmitTime())));
        npcHtmlMessage.replace("%type%", petition.getTypeAsString());
        npcHtmlMessage.replace("%petitioner%", petition.getPetitionerName());
        npcHtmlMessage.replace("%online%", petition.isPetitionerOnline() ? "00FF00" : "999999");
        npcHtmlMessage.replace("%text%", petition.getContent());
        player.sendPacket((IStaticPacket)npcHtmlMessage);
    }

    @Override
    public void handle(Player player, int n, String string) {
        if (!Config.CAN_PETITION_TO_OFFLINE_GM && GmListTable.getAllGMs().size() == 0) {
            player.sendPacket((IStaticPacket)new SystemMessage(702));
            return;
        }
        if (!PetitionManager.getInstance().isPetitioningAllowed()) {
            player.sendPacket((IStaticPacket)new SystemMessage(381));
            return;
        }
        if (PetitionManager.getInstance().isPlayerPetitionPending(player)) {
            player.sendPacket((IStaticPacket)new SystemMessage(390));
            return;
        }
        if (PetitionManager.getInstance().getPendingPetitionCount() == Config.MAX_PETITIONS_PENDING) {
            player.sendPacket((IStaticPacket)new SystemMessage(602));
            return;
        }
        int n2 = PetitionManager.getInstance().getPlayerTotalPetitionCount(player) + 1;
        if (n2 > Config.MAX_PETITIONS_PER_PLAYER) {
            player.sendPacket((IStaticPacket)new SystemMessage(733));
            return;
        }
        if (string.length() > 255) {
            player.sendPacket((IStaticPacket)new SystemMessage(971));
            return;
        }
        if (n >= PetitionType.values().length) {
            _log.warn("PetitionManager: Invalid petition type : " + n);
            return;
        }
        int n3 = PetitionManager.getInstance().submitPetition(player, string, n);
        player.sendPacket((IStaticPacket)new SystemMessage(389).addNumber(n3));
        player.sendPacket((IStaticPacket)new SystemMessage(730).addNumber(n2).addNumber(Config.MAX_PETITIONS_PER_PLAYER - n2));
        player.sendPacket((IStaticPacket)new SystemMessage(601).addNumber(PetitionManager.getInstance().getPendingPetitionCount()));
    }

    private class Petition {
        private long AH = System.currentTimeMillis();
        private long AI = -1L;
        private int _id;
        private PetitionType AJ;
        private PetitionState AK = PetitionState.Pending;
        private String AL;
        private List<Say2> AM = new ArrayList<Say2>();
        private int AN;
        private int AO;

        public Petition(Player player, String string, int n) {
            this._id = PetitionManager.this.getNextId();
            this.AJ = PetitionType.values()[n - 1];
            this.AL = string;
            this.AN = player.getObjectId();
        }

        protected boolean addLogMessage(Say2 say2) {
            return this.AM.add(say2);
        }

        protected List<Say2> getLogMessages() {
            return this.AM;
        }

        public boolean endPetitionConsultation(PetitionState petitionState) {
            this.setState(petitionState);
            this.AI = System.currentTimeMillis();
            if (this.getResponder() != null && this.getResponder().isOnline()) {
                if (petitionState == PetitionState.Responder_Reject) {
                    this.getPetitioner().sendMessage("Your petition was rejected. Please try again later.");
                } else {
                    this.getResponder().sendPacket((IStaticPacket)new SystemMessage(395).addString(this.getPetitionerName()));
                    if (petitionState == PetitionState.Petitioner_Cancel) {
                        this.getResponder().sendPacket((IStaticPacket)new SystemMessage(391).addNumber(this.getId()));
                    }
                }
            }
            if (this.isPetitionerOnline()) {
                this.getPetitioner().sendPacket((IStaticPacket)new SystemMessage(387));
            }
            PetitionManager.this.getCompletedPetitions().put(this.getId(), this);
            return PetitionManager.this.getPendingPetitions().remove(this.getId()) != null;
        }

        public String getContent() {
            return this.AL;
        }

        public int getId() {
            return this._id;
        }

        public Player getPetitioner() {
            return World.getPlayer(this.AN);
        }

        public boolean isPetitionerOnline() {
            Player player = World.getPlayer(this.AN);
            if (player != null) {
                return player.isOnline();
            }
            return false;
        }

        public String getPetitionerName() {
            Player player = World.getPlayer(this.AN);
            if (player != null) {
                return player.getName();
            }
            return StringUtils.defaultString(CharacterDAO.getInstance().getNameByObjectId(this.AN), "[Unknown]");
        }

        public Player getResponder() {
            return World.getPlayer(this.AO);
        }

        public long getEndTime() {
            return this.AI;
        }

        public long getSubmitTime() {
            return this.AH;
        }

        public PetitionState getState() {
            return this.AK;
        }

        public String getTypeAsString() {
            return this.AJ.toString().replace("_", " ");
        }

        public void sendPetitionerPacket(L2GameServerPacket l2GameServerPacket) {
            Player player = World.getPlayer(this.AN);
            if (player != null) {
                player.sendPacket((IStaticPacket)l2GameServerPacket);
            }
        }

        public void sendResponderPacket(L2GameServerPacket l2GameServerPacket) {
            if (this.getResponder() == null || !this.getResponder().isOnline()) {
                this.endPetitionConsultation(PetitionState.Responder_Missing);
                return;
            }
            this.getResponder().sendPacket((IStaticPacket)l2GameServerPacket);
        }

        public void setState(PetitionState petitionState) {
            this.AK = petitionState;
        }

        public void setResponder(Player player) {
            if (this.getResponder() != null) {
                return;
            }
            this.AO = player.getObjectId();
        }
    }

    public static enum PetitionType {
        Immobility,
        Recovery_Related,
        Bug_Report,
        Quest_Related,
        Bad_User,
        Suggestions,
        Game_Tip,
        Operation_Related,
        Other;

    }

    public static enum PetitionState {
        Pending,
        Responder_Cancel,
        Responder_Missing,
        Responder_Reject,
        Responder_Complete,
        Petitioner_Cancel,
        Petitioner_Missing,
        In_Process,
        Completed;

    }
}

