package atm.bloodworkxgaming;

import crafttweaker.mc1120.CraftTweaker;
import crafttweaker.mc1120.network.MessageCopyClipboard;
import crafttweaker.mc1120.player.MCPlayer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * @author BloodWorkXGaming
 */
public class ClipboardHelper {

    static final String copyCommandBase = "/ct copy ";

    /**
     * Sends the Player a Message where he can click on and copy the it
     * @param player: Player to send the message to
     * @param holeMessage: String that should be shown in chat
     * @param copyMessage: String that is being copied when the player clicks on it
     */
    public static void sendMessageWithCopy(EntityPlayer player, String holeMessage, String copyMessage) {
        Style style = new Style();
        ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, copyCommandBase + copyMessage);
        style.setClickEvent(click);

        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to copy [§6" + copyMessage + "§r]"));
        style.setHoverEvent(hoverEvent);

        player.sendMessage(new TextComponentString(holeMessage).setStyle(style));
    }

    /**
     * Called by the copy command
     * Copy command is needed to be able to copy something on clicking on a ChatMessage
     * @param sender: sender that copies
     * @param args: strings to copy
     */
    static void copyCommandRun(ICommandSender sender, String[] args) {

        StringBuilder message = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            message.append(args[i]);
            if (i != args.length - 1) message.append(" ");
        }

        if (sender.getCommandSenderEntity() instanceof EntityPlayer){
            MCPlayer player = new MCPlayer((EntityPlayer) sender);
            player.copyToClipboard(message.toString());
        }

        // Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
        // clpbrd.setContents(new StringSelection(message.toString()), null);

        sender.sendMessage(new TextComponentString("Copied [§6" + message.toString() + "§r] to the clipboard"));
    }

    static void copyStringPlayer(EntityPlayer player, String s){
        if(player instanceof EntityPlayerMP) {
            CraftTweaker.NETWORK.sendTo(new MessageCopyClipboard(s), (EntityPlayerMP) player);
        }
    }
}