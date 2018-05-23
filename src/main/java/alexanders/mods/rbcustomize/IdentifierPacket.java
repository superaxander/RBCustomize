package alexanders.mods.rbcustomize;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class IdentifierPacket implements IPacket {
    public static final HashMap<UUID, String> confirmedIdentifiers = new HashMap<>();
    public static final ArrayList<String> ownIdentifiers = new ArrayList<>();
    private static final UUID DUMMY_UUID = new UUID(0, 0);
    public UUID uuid = null;
    public boolean firstTime = false;
    public String identifier = null;

    public IdentifierPacket() {
    }

    public IdentifierPacket(UUID uuid, boolean firstTime, String identifier) {
        this.uuid = uuid;
        this.firstTime = firstTime;
        this.identifier = identifier;
    }

    public IdentifierPacket(boolean firstTime, String identifier) {
        this(DUMMY_UUID, firstTime, identifier);
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
        if (identifier == null) throw new IllegalStateException("Can't send uninitialized packet!");
        buf.writeBoolean(firstTime);
        NetUtil.writeStringToBuffer(identifier, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        uuid = new UUID(buf.readLong(), buf.readLong());
        firstTime = buf.readBoolean();
        identifier = NetUtil.readStringFromBuffer(buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        confirmedIdentifiers.put(uuid, identifier);
        if (firstTime) {
            if (RockBottomAPI.getNet().isClient()) {
                RockBottomAPI.getNet().sendToServer(new IdentifierPacket(RockBottomAPI.getGame().getPlayer().getUniqueId(), false, identifier));
            } else {
                AbstractEntityPlayer player = RockBottomAPI.getGame().getWorld().getPlayer(uuid);
                if (player == null) RBCustomize.logger.severe("Invalid UUID received from client!!!");
                else player.sendPacket(new IdentifierPacket(false, identifier));
            }
        }
    }
}
