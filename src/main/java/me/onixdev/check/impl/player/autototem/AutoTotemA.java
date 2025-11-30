package me.onixdev.check.impl.player.autototem;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import dev.onixac.api.check.CheckStage;
import me.onixdev.check.api.Check;
import me.onixdev.check.api.CheckBuilder;
import me.onixdev.event.api.BaseEvent;
import me.onixdev.event.impl.PlayerCloseInventoryEvent;
import me.onixdev.event.impl.PlayerPacketClickEvent;
import me.onixdev.event.impl.TickEvent;
import me.onixdev.user.OnixUser;

public class AutoTotemA extends Check {
    private boolean clicked = false;
    public AutoTotemA(OnixUser player) {
        super(player, CheckBuilder.create().setCheckName("AutoTotem").setType("A").setCheckStage(CheckStage.BETA).build());
    }

    @Override
    public void onEvent(BaseEvent event) {
        if (event instanceof TickEvent) {
            player.sendMessage("tick: " + clicked);
            clicked = false;
        }
        if (event instanceof PlayerPacketClickEvent) {
            if (((PlayerPacketClickEvent) event).isClient()) {
                ItemStack item = ((PlayerPacketClickEvent) event).getItem();
                if (item == ItemTypes.TOTEM_OF_UNDYING) {
                    clicked = true;
                    player.sendMessage("totem");
                }
            }
        }
        if (event instanceof PlayerCloseInventoryEvent) {
            player.sendMessage("totemc: " + clicked);
            if (clicked) {
                fail("");
            }
        }
    }
}
