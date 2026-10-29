package l2.gameserver.data.xml.parser;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import l2.commons.collections.MultiValueSet;
import l2.commons.data.xml.AbstractDirParser;
import l2.gameserver.Config;
import l2.gameserver.data.xml.holder.EventHolder;
import l2.gameserver.model.entity.events.EventAction;
import l2.gameserver.model.entity.events.EventType;
import l2.gameserver.model.entity.events.GlobalEvent;
import l2.gameserver.model.entity.events.actions.ActiveDeactiveAction;
import l2.gameserver.model.entity.events.actions.AnnounceAction;
import l2.gameserver.model.entity.events.actions.GiveItemAction;
import l2.gameserver.model.entity.events.actions.IfElseAction;
import l2.gameserver.model.entity.events.actions.InitAction;
import l2.gameserver.model.entity.events.actions.NpcSayAction;
import l2.gameserver.model.entity.events.actions.OpenCloseAction;
import l2.gameserver.model.entity.events.actions.PlaySoundAction;
import l2.gameserver.model.entity.events.actions.RefreshAction;
import l2.gameserver.model.entity.events.actions.SayAction;
import l2.gameserver.model.entity.events.actions.SpawnDespawnAction;
import l2.gameserver.model.entity.events.actions.StartStopAction;
import l2.gameserver.model.entity.events.actions.TeleportPlayersAction;
import l2.gameserver.model.entity.events.objects.BoatPoint;
import l2.gameserver.model.entity.events.objects.CTBTeamObject;
import l2.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2.gameserver.model.entity.events.objects.DoorObject;
import l2.gameserver.model.entity.events.objects.SiegeToggleNpcObject;
import l2.gameserver.model.entity.events.objects.SpawnExObject;
import l2.gameserver.model.entity.events.objects.StaticObjectObject;
import l2.gameserver.model.entity.events.objects.ZoneObject;
import l2.gameserver.network.l2.components.ChatType;
import l2.gameserver.network.l2.components.NpcString;
import l2.gameserver.network.l2.components.SysString;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.utils.Location;
import org.dom4j.Element;

public final class EventParser extends AbstractDirParser<EventHolder> {
   private static final EventParser _instance = new EventParser();

   public static EventParser getInstance() {
      return _instance;
   }

   protected EventParser() {
      super(EventHolder.getInstance());
   }

   @Override
   public File getXMLDir() {
      return new File(Config.DATAPACK_ROOT, "data/events/");
   }

   @Override
   public boolean isIgnored(File var1) {
      return false;
   }

   @Override
   public String getDTDFileName() {
      return "events.dtd";
   }

   @Override
   protected void readData(Element var1) throws Exception {
      Iterator var2 = var1.elementIterator("event");

      while (var2.hasNext()) {
         Element var3 = (Element)var2.next();
         int var4 = Integer.parseInt(var3.attributeValue("id"));
         String var5 = var3.attributeValue("name");
         String var6 = var3.attributeValue("impl");
         EventType var7 = EventType.valueOf(var3.attributeValue("type"));
         Class var8 = null;

         try {
            var8 = Class.forName("l2.gameserver.model.entity.events.impl." + var6 + "Event");
         } catch (ClassNotFoundException var17) {
            this.info("Not found impl class: " + var6 + "; File: " + this.getCurrentFileName());
            continue;
         }

         Constructor var9 = var8.getConstructor(MultiValueSet.class);
         MultiValueSet var10 = new MultiValueSet();
         var10.set("id", var4);
         var10.set("name", var5);
         Iterator var11 = var3.elementIterator("parameter");

         while (var11.hasNext()) {
            Element var12 = (Element)var11.next();
            var10.set(var12.attributeValue("name"), var12.attributeValue("value"));
         }

         GlobalEvent var19 = (GlobalEvent)var9.newInstance(var10);
         var19.addOnStartActions(this.parseActions(var3.element("on_start"), Integer.MAX_VALUE));
         var19.addOnStopActions(this.parseActions(var3.element("on_stop"), Integer.MAX_VALUE));
         var19.addOnInitActions(this.parseActions(var3.element("on_init"), Integer.MAX_VALUE));
         Element var20 = var3.element("on_time");
         if (var20 != null) {
            Iterator var13 = var20.elementIterator("on");

            while (var13.hasNext()) {
               Element var14 = (Element)var13.next();
               int var15 = Integer.parseInt(var14.attributeValue("time"));
               List var16 = this.parseActions(var14, var15);
               var19.addOnTimeActions(var15, var16);
            }
         }

         Iterator var21 = var3.elementIterator("objects");

         while (var21.hasNext()) {
            Element var22 = (Element)var21.next();
            String var23 = var22.attributeValue("name");
            List var24 = this.parseObjects(var22);
            var19.addObjects(var23, var24);
         }

         this.getHolder().addEvent(var7, var19);
      }
   }

   private List<Serializable> parseObjects(Element var1) {
      if (var1 == null) {
         return Collections.emptyList();
      }

      ArrayList var2 = new ArrayList(2);
      Iterator var3 = var1.elementIterator();

      while (var3.hasNext()) {
         Element var4 = (Element)var3.next();
         String var5 = var4.getName();
         if (var5.equalsIgnoreCase("boat_point")) {
            var2.add(BoatPoint.parse(var4));
         } else if (var5.equalsIgnoreCase("point")) {
            var2.add(Location.parse(var4));
         } else if (var5.equalsIgnoreCase("spawn_ex")) {
            var2.add(new SpawnExObject(var4.attributeValue("name")));
         } else if (var5.equalsIgnoreCase("door")) {
            var2.add(new DoorObject(Integer.parseInt(var4.attributeValue("id"))));
         } else if (var5.equalsIgnoreCase("static_object")) {
            var2.add(new StaticObjectObject(Integer.parseInt(var4.attributeValue("id"))));
         } else if (!var5.equalsIgnoreCase("siege_toggle_npc")) {
            if (var5.equalsIgnoreCase("castle_zone")) {
               long var15 = Long.parseLong(var4.attributeValue("price"));
               var2.add(new CastleDamageZoneObject(var4.attributeValue("name"), var15));
            } else if (var5.equalsIgnoreCase("zone")) {
               var2.add(new ZoneObject(var4.attributeValue("name")));
            } else if (var5.equalsIgnoreCase("ctb_team")) {
               int var16 = Integer.parseInt(var4.attributeValue("mob_id"));
               int var17 = Integer.parseInt(var4.attributeValue("id"));
               Location var18 = Location.parse(var4);
               var2.add(new CTBTeamObject(var16, var17, var18));
            }
         } else {
            int var6 = Integer.parseInt(var4.attributeValue("id"));
            int var7 = Integer.parseInt(var4.attributeValue("fake_id"));
            int var8 = Integer.parseInt(var4.attributeValue("x"));
            int var9 = Integer.parseInt(var4.attributeValue("y"));
            int var10 = Integer.parseInt(var4.attributeValue("z"));
            int var11 = Integer.parseInt(var4.attributeValue("hp"));
            Set var12 = Collections.emptySet();
            Iterator var13 = var4.elementIterator();

            while (var13.hasNext()) {
               Element var14 = (Element)var13.next();
               if (var12.isEmpty()) {
                  var12 = new HashSet();
               }

               var12.add(var14.attributeValue("name"));
            }

            var2.add(new SiegeToggleNpcObject(var6, var7, new Location(var8, var9, var10), var11, var12));
         }
      }

      return var2;
   }

   private List<EventAction> parseActions(Element var1, int var2) {
      if (var1 == null) {
         return Collections.emptyList();
      }

      IfElseAction var3 = null;
      ArrayList var4 = new ArrayList(0);
      Iterator var5 = var1.elementIterator();

      while (var5.hasNext()) {
         Element var6 = (Element)var5.next();
         if (var6.getName().equalsIgnoreCase("start")) {
            String var29 = var6.attributeValue("name");
            StartStopAction var46 = new StartStopAction(var29, true);
            var4.add(var46);
         } else if (var6.getName().equalsIgnoreCase("stop")) {
            String var28 = var6.attributeValue("name");
            StartStopAction var45 = new StartStopAction(var28, false);
            var4.add(var45);
         } else if (var6.getName().equalsIgnoreCase("spawn")) {
            String var27 = var6.attributeValue("name");
            SpawnDespawnAction var44 = new SpawnDespawnAction(var27, true);
            var4.add(var44);
         } else if (var6.getName().equalsIgnoreCase("despawn")) {
            String var26 = var6.attributeValue("name");
            SpawnDespawnAction var43 = new SpawnDespawnAction(var26, false);
            var4.add(var43);
         } else if (var6.getName().equalsIgnoreCase("open")) {
            String var25 = var6.attributeValue("name");
            OpenCloseAction var42 = new OpenCloseAction(true, var25);
            var4.add(var42);
         } else if (var6.getName().equalsIgnoreCase("close")) {
            String var24 = var6.attributeValue("name");
            OpenCloseAction var41 = new OpenCloseAction(false, var24);
            var4.add(var41);
         } else if (var6.getName().equalsIgnoreCase("active")) {
            String var23 = var6.attributeValue("name");
            ActiveDeactiveAction var40 = new ActiveDeactiveAction(true, var23);
            var4.add(var40);
         } else if (var6.getName().equalsIgnoreCase("deactive")) {
            String var22 = var6.attributeValue("name");
            ActiveDeactiveAction var39 = new ActiveDeactiveAction(false, var22);
            var4.add(var39);
         } else if (var6.getName().equalsIgnoreCase("refresh")) {
            String var21 = var6.attributeValue("name");
            RefreshAction var38 = new RefreshAction(var21);
            var4.add(var38);
         } else if (var6.getName().equalsIgnoreCase("init")) {
            String var20 = var6.attributeValue("name");
            InitAction var37 = new InitAction(var20);
            var4.add(var37);
         } else if (var6.getName().equalsIgnoreCase("npc_say")) {
            int var19 = Integer.parseInt(var6.attributeValue("npc"));
            ChatType var36 = ChatType.valueOf(var6.attributeValue("chat"));
            int var49 = Integer.parseInt(var6.attributeValue("range"));
            NpcString var52 = NpcString.valueOf(var6.attributeValue("text"));
            NpcSayAction var53 = new NpcSayAction(var19, var49, var36, var52);
            var4.add(var53);
         } else if (var6.getName().equalsIgnoreCase("play_sound")) {
            int var18 = Integer.parseInt(var6.attributeValue("range"));
            String var35 = var6.attributeValue("sound");
            PlaySound.Type var48 = PlaySound.Type.valueOf(var6.attributeValue("type"));
            PlaySoundAction var51 = new PlaySoundAction(var18, var35, var48);
            var4.add(var51);
         } else if (var6.getName().equalsIgnoreCase("give_item")) {
            int var17 = Integer.parseInt(var6.attributeValue("id"));
            long var34 = Integer.parseInt(var6.attributeValue("count"));
            GiveItemAction var50 = new GiveItemAction(var17, var34);
            var4.add(var50);
         } else if (var6.getName().equalsIgnoreCase("announce")) {
            String var16 = var6.attributeValue("val");
            if (var16 == null && var2 == Integer.MAX_VALUE) {
               this.info("Can't get announce time." + this.getCurrentFileName());
            } else {
               int var33 = var16 == null ? var2 : Integer.parseInt(var16);
               AnnounceAction var47 = new AnnounceAction(var33);
               var4.add(var47);
            }
         } else if (var6.getName().equalsIgnoreCase("if")) {
            String var7 = var6.attributeValue("name");
            IfElseAction var8 = new IfElseAction(var7, false);
            var8.setIfList(this.parseActions(var6, var2));
            var4.add(var8);
            var3 = var8;
         } else if (var6.getName().equalsIgnoreCase("ifnot")) {
            String var13 = var6.attributeValue("name");
            IfElseAction var30 = new IfElseAction(var13, true);
            var30.setIfList(this.parseActions(var6, var2));
            var4.add(var30);
            var3 = var30;
         } else if (var6.getName().equalsIgnoreCase("else")) {
            if (var3 == null) {
               this.info("Not find <if> for <else> tag");
            } else {
               var3.setElseList(this.parseActions(var6, var2));
            }
         } else if (var6.getName().equalsIgnoreCase("say")) {
            ChatType var14 = ChatType.valueOf(var6.attributeValue("chat"));
            int var31 = Integer.parseInt(var6.attributeValue("range"));
            String var9 = var6.attributeValue("how");
            String var10 = var6.attributeValue("text");
            SysString var11 = SysString.valueOf2(var9);
            Object var12 = null;
            if (var11 != null) {
               var12 = new SayAction(var31, var14, var11, SystemMsg.valueOf(var10));
            } else {
               var12 = new SayAction(var31, var14, var9, NpcString.valueOf(var10));
            }

            var4.add(var12);
         } else if (var6.getName().equalsIgnoreCase("teleport_players")) {
            String var15 = var6.attributeValue("id");
            TeleportPlayersAction var32 = new TeleportPlayersAction(var15);
            var4.add(var32);
         }
      }

      return var4.isEmpty() ? Collections.emptyList() : var4;
   }
}
